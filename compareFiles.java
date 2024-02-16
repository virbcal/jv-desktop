/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2000-2018 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2018/05
 *.......^.........^.........^.........^.........^.........^.........^.........8
 * Progname   : compareFiles
 * Description: Compare files utility
 * System     : General use
 * Function   : Read records from two or more input files,
 *              Compare one or more fields or the full record,
 *              Write a report showing which fields/records:
 *              - have a match in all input files;
 *              - have a match with at least one of the other input file/s
 *                (not generated if there are only 2 input files)
 *              - does not have a match with any of the other input file/s
 * Parameters :
 *             *Positional parameters
 *   infile +   input file name                                      (1)(2)
 *              (def) compareF1, compareF2
 *             *Non-positional parameters using switches
 *  /I          input file switch
 *   infile +   input file name                                      (1)(2)
 *              (def) compareF1, compareF2
 *  /O          output file switch
 *   outfile    output report file
 *              (def) <infile1>_compareF.out
 *  /FCL        compare field column-length switch
 *   stacol     start column                                         (3)(4)
 *   fldlen     field length                                         (3)(4)
 *   +
 *              ---
 *       NOTES: (1) min-of-2 and max-of-6 infiles may be specified
 *              (2) will use the default for each missing required entry
 *              (3) min-of-1 and max-of-6 stacol-fldlen pairs may be specified
 *              (4) will use the whole record if none is specified
 * -----------------------------------------------------------------------------
 * Revisions
 * 1.0  2018-05-12 virbcal   initial release.
 * 2.0  2018-05-15 virbcal   accept upto 6 infiles.
 * 2.0a 2018-06-15 virbcal   include recs read in report hdr;
 * -----------------------------------------------------------------------------
 */

import java.util.*;
import java.util.Arrays;
import java.io.*;
import utils.wkArrLst;

public class compareFiles
{
    private final String THISCLAS = this.getClass().getName();
    private final String CLASDESC =
            "Compare files utility";
    private final String CLASRELS = "v2r0a";
    private final String ER000001 =
            "     + Processing terminated for "+THISCLAS+".";
    private final String ER000011 =
            "Error: Invalid switch found.";
    private final String ER000012 =
            "Error: Repetition of switch not allowed.";
    private final String ER000013 =
            "Error: Repetition of input file specification not allowed.";
    private final String ER000014 =
            "Error: Repetition of output file specification not allowed.";
    private final String ER000015 =
            "Error: Input file does not exist.";
    private final String ER000016 =
            "Error: Unsupported/incomplete key datatype specification - ";
    private final String ER000017 =
            "Error: Max number of input file parameters exceeded.";
    private final String ER000018 =
            "Error: Max number of /i parameters exceeded.";
    private final String ER000021 =
            "Error: Max number of /fcl pair parameters exceeded.";
    private final String ER000022 =
            "Error: Missing /fcl required parameter/s.";
    private final String ER000023 =
            "Error: Invalid /fcl parameter; Numeric required.";
    private final String ER000031 =
            "Error: Max number of /kpd parameters exceeded.";
    private final String ER000032 =
            "Error: Missing /kpd required parameter/s.";
    private final String ER000033 =
            "Error: Invalid /kpd parameter; Numeric required.";
    private final String INFILE1  = "compareF1"; //default input filename1
    private final String INFILE2  = "compareF2"; //default input filename2
    private final String OFLEXT   = "_compareF.out";//output filename extension
    private final String SBLANK   = "";           //string blank
    private final char   CBLANK   = ' ';          //char blank
    private final char   TABCHAR  = '\t';         //char blank
    private final int    MAXIFLS  = 6;            //max infiles
    private final int    MAXFLDS  = 6;            //max flds/fld-pairs
    private       String   swi    = SBLANK;
    private       String   swo    = SBLANK;
    private       String   swfcl  = SBLANK;
    private       String[] flds = new String[MAXFLDS];
    private       String   flds1, flds2, recno;
    private       int[]    icol = new int[MAXFLDS];
    private       int[]    ilen = new int[MAXFLDS];
    private ArrayList<String>   recArrLis1;
    private ArrayList<String>   recArrLis2;
    private ArrayList<String>   recArrLis3;
    private ArrayList<String>   recArrLis4;
    private ArrayList<String>   recArrLis5;
    private ArrayList<String>   recArrLis6;
    private ArrayList<String>   fndArrList;
    private ArrayList<wkArrLst> fldArrLis1;
    private ArrayList<wkArrLst> fldArrLis2;
    private ArrayList<wkArrLst> fldArrLis3;
    private ArrayList<wkArrLst> fldArrLis4;
    private ArrayList<wkArrLst> fldArrLis5;
    private ArrayList<wkArrLst> fldArrLis6;
    private   StringBuffer sb;
  //private BufferedReader br;
    private BufferedWriter bw;
    private       String   argsin, argstr, prms, delim, txt, str,
                           ifl1, ifl2, ifl3, ifl4, ifl5, ifl6, ofil;
    private       int      argsln, ndx, iln, ird, iwr;

    public static void main(String[] args) throws Exception, IOException
    {
        //create the class instance
        compareFiles instce = new compareFiles();
        if ( args.length > 0 &&
            (args[0].equals("?") || args[0].equals("/?")) ) {
            instce.showUsage();
            return;
        }
        instce.doProc(args);
    }

    private void showUsage()
    {
        System.out.println(THISCLAS+" ("+CLASRELS+")"+
"\n"+CLASDESC+
"\n"+THISCLAS+" [infile +] [/I infile +] [/O outfile] [/FCL stacol fldlen +]     "+
"\n   infile +   input file name                                      (1)(2)     "+
"\n              (def) compareF1, compareF2                                      "+
"\n  /I          input file switch                                               "+
"\n   infile +   input file name                                      (1)(2)     "+
"\n              (def) compareF1, compareF2                                      "+
"\n  /O          output file switch                                              "+
"\n   outfile    output report file                                              "+
"\n              (def) <infile1>_compareF.out                                    "+
"\n  /FCL        compare field column-length switch                              "+
"\n   stacol     start column                                         (1),(2)    "+
"\n   fldlen     field length                                         (1),(2)    "+
"\n   +                                                                          "+
"\n              ---                                                             "+
"\n       NOTES: (1) min-of-2 and max-of-6 infiles may be specified              "+
"\n              (2) will use the default for each missing required entry        "+
"\n              (3) min-of-1 and max-of-6 stacol-fldlen pairs may be specified  "+
"\n              (4) will use the whole record if none is specified              ");
    }

    private void doProc(String[] args) throws Exception
    {
        valParms(args);
        if ( swfcl.length() > 0 ) fclProc();
        else                      nofldProc();
        logrep(SBLANK);
        logrep("Infile1 records read         = "+recArrLis1.size());
        logrep("Infile2 records read         = "+recArrLis2.size());

        if ( !ifl3.equals(SBLANK) )
         logrep("Infile3 records read         = "+recArrLis3.size());
        if ( !ifl4.equals(SBLANK) )
         logrep("Infile4 records read         = "+recArrLis4.size());
        if ( !ifl5.equals(SBLANK) )
         logrep("Infile5 records read         = "+recArrLis5.size());
        if ( !ifl6.equals(SBLANK) )
         logrep("Infile6 records read         = "+recArrLis6.size());

        logrep("Records with matching fields = "+fndArrList.size());
        logrep("Infile1 unmatched records    = "+fldArrLis1.size());
        logrep("Infile2 unmatched records    = "+fldArrLis2.size());

        if ( !ifl3.equals(SBLANK) )
         logrep("Infile3 unmatched records    = "+fldArrLis3.size());
        if ( !ifl4.equals(SBLANK) )
         logrep("Infile4 unmatched records    = "+fldArrLis4.size());
        if ( !ifl5.equals(SBLANK) )
         logrep("Infile5 unmatched records    = "+fldArrLis5.size());
        if ( !ifl6.equals(SBLANK) )
         logrep("Infile6 unmatched records    = "+fldArrLis6.size());

        try {
            bw.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    private void valParms(String[] args) throws Exception
    {
        argsln = args.length;
        int i, j, ndx1, ndx2;
        ifl1=SBLANK; ifl2=SBLANK; ifl3=SBLANK;
        ifl4=SBLANK; ifl5=SBLANK; ifl6=SBLANK;
        ofil=SBLANK;

        sb = new StringBuffer();
        for (i=0; i<argsln; i++)
            sb = sb.append(args[i]).append(' ');
        argsin = "===>"+sb.toString().trim()+"<";
        System.out.println(argsin);//args

        // Extract infiles
        j=0;
        if ( argsln > 0 && args[0].trim().charAt(0) != '/' ) {
            ifl1 = args[0].trim(); j++;                 //ifl1
        } 
        if ( argsln > 1 && args[1].trim().charAt(0) != '/' &&
                           args[0].trim().charAt(0) != '/' ) {
            ifl2 = args[1].trim(); j++;                 //ifl2
        }
        if ( argsln > 2 && args[2].trim().charAt(0) != '/' &&
                           args[1].trim().charAt(0) != '/' &&
                           args[0].trim().charAt(0) != '/' ) {
            ifl3 = args[2].trim(); j++;                 //ifl3
        }
        if ( argsln > 3 && args[3].trim().charAt(0) != '/' &&
                           args[2].trim().charAt(0) != '/' &&
                           args[1].trim().charAt(0) != '/' &&
                           args[0].trim().charAt(0) != '/' ) {
            ifl4 = args[3].trim(); j++;                 //ifl4
        } 
        if ( argsln > 4 && args[4].trim().charAt(0) != '/' &&
                           args[3].trim().charAt(0) != '/' &&
                           args[2].trim().charAt(0) != '/' &&
                           args[1].trim().charAt(0) != '/' &&
                           args[0].trim().charAt(0) != '/' ) {
            ifl5 = args[4].trim(); j++;                 //ifl5
        }
        if ( argsln > 5 && args[5].trim().charAt(0) != '/' &&
                           args[4].trim().charAt(0) != '/' &&
                           args[3].trim().charAt(0) != '/' &&
                           args[2].trim().charAt(0) != '/' &&
                           args[1].trim().charAt(0) != '/' &&
                           args[0].trim().charAt(0) != '/' ) {
            ifl6 = args[5].trim(); j++;                 //ifl6
        }
        if ( argsln > 6 && args[6].trim().charAt(0) != '/' &&
                           args[5].trim().charAt(0) != '/' &&
                           args[4].trim().charAt(0) != '/' &&
                           args[3].trim().charAt(0) != '/' &&
                           args[2].trim().charAt(0) != '/' &&
                           args[1].trim().charAt(0) != '/' &&
                           args[0].trim().charAt(0) != '/' )
            throw new Exception("\n"+ER000017+"\n"+ER000001);

        sb = new StringBuffer();
        for (i=j; i<argsln; i++)
            sb = sb.append(args[i]).append(' ');
        argstr = sb.toString().trim();
        argsln = argstr.length();
        System.out.println(" ==>"+argstr+"<");          //switches

      //Extract valid switch groups and validate parms
        String swg = SBLANK;
        ndx1 = argstr.indexOf('/');
        ndx2 = 0;
        while ( ndx1 >= 0 && ndx2 >= 0 ) {
            if ( (ndx2=argstr.substring(ndx1+1).indexOf('/')) >= 0 ) {
                ndx2 = ndx1 + ndx2;
                swg = argstr.substring(ndx1,ndx2);      //sw group
            }
            else
                swg = argstr.substring(ndx1);           //sw group

            if ( swg.indexOf("/i" ) == 0 ) {
                if ( swi.length() > 0 )                 //dup sw
                    throw new Exception("\n"+ER000012+"\n"+ER000001);
                if ( ifl1.length() > 0 )                //dup spec
                    throw new Exception("\n"+ER000013+"\n"+ER000001);
                valInfiles(swg);
                swi = swg; ndx1 = ndx2+1;
            }
            else
            if ( swg.indexOf("/o"  ) == 0 ) {
                if ( swo.length() > 0 )                 //dup sw
                    throw new Exception("\n"+ER000012+"\n"+ER000001);
                if ( ofil.length() > 0 )                //dup spec
                    throw new Exception("\n"+ER000014+"\n"+ER000001);
                ndx  = swg.indexOf(' ');                //find first white space
                ofil = swg.substring(ndx+1);            //remove leading switch keyword
                swo  = swg; ndx1 = ndx2+1;
            }
            else
            if ( swg.indexOf("/fcl") == 0 ) {
                if ( swfcl.length() > 0 )               //dup sw
                    throw new Exception("\n"+ER000012+"\n"+ER000001);
                valFcl(swg);
                swfcl = swg; ndx1 = ndx2+1;
            }
            else
                throw new Exception("\n"+ER000011+"\n"+ER000001); //invalid
        } //end while

        if ( ifl1.equals(SBLANK) ) ifl1 = INFILE1;
        if ( ifl2.equals(SBLANK) ) ifl2 = INFILE2;
        if ( ofil.equals(SBLANK) ) ofil = ifl1+OFLEXT;

        logrep("\n"+THISCLAS+" ("+CLASRELS+")");
        logrep(argsin);

        logrep("ifl1="+ifl1);
        if ( !isFileExists(ifl1) ) logexcp("\n"+ER000015+"\n"+ER000001);

        logrep("ifl2="+ifl2);
        if ( !isFileExists(ifl2) ) logexcp("\n"+ER000015+"\n"+ER000001);

        if ( !ifl3.equals(SBLANK) ) {
            logrep("ifl3="+ifl3);
            if ( !isFileExists(ifl3) ) logexcp("\n"+ER000015+"\n"+ER000001);
        }
        if ( !ifl4.equals(SBLANK) ) {
            logrep("ifl4="+ifl4);
            if ( !isFileExists(ifl4) ) logexcp("\n"+ER000015+"\n"+ER000001);
        }
        if ( !ifl5.equals(SBLANK) ) {
            logrep("ifl5="+ifl5);
            if ( !isFileExists(ifl5) ) logexcp("\n"+ER000015+"\n"+ER000001);
        }
        if ( !ifl6.equals(SBLANK) ) {
            logrep("ifl6="+ifl6);
            if ( !isFileExists(ifl6) ) logexcp("\n"+ER000015+"\n"+ER000001);
        }

        logrep("ofil="+ofil);
    }

    private void valInfiles(String swg) throws Exception
    {
        ndx  = swg.indexOf(' ');                //find first white space
        prms = swg.substring(ndx+1);            //remove leading switch keyword
        String[] prm = prms.split(" ");
        int prmln = prm.length;

        if ( prmln > MAXIFLS )                  //max infiles
            throw new Exception("\n"+ER000018+"\n"+ER000001);
        if ( prmln > 0 ) ifl1 = prm[0];
        if ( prmln > 1 ) ifl2 = prm[1];
        if ( prmln > 2 ) ifl3 = prm[2];
        if ( prmln > 3 ) ifl4 = prm[3];
        if ( prmln > 4 ) ifl5 = prm[4];
        if ( prmln > 5 ) ifl6 = prm[5];
    }

    private void valFcl(String swg) throws Exception
    {
        for (int i=0; i<MAXFLDS; i++) {
            icol[i] = 0;
            ilen[i] = 0;
        }
        ndx  = swg.indexOf(' ');                //find first white space
        prms = swg.substring(ndx+1);            //remove leading switch keyword
        String[] prm = prms.split(" ");
        int prmln = prm.length;

        if ( prmln > MAXFLDS*2 )                //max flds
            throw new Exception("\n"+ER000021+"\n"+ER000001);
        if ( prmln % 2 != 0 )                   //missing
            throw new Exception("\n"+ER000022+"\n"+ER000001);
        for ( int i=0, j=0; j<prmln; i++, j+=2 )//extract the parms
        {
            if ( !prm[j].matches("[0-9]+") ||
                 !prm[j+1].matches("[0-9]+") )
                throw new Exception("\n"+ER000023+"\n"+ER000001);//not num
            else {
                icol[i] = Integer.parseInt(prm[j]);
                ilen[i] = Integer.parseInt(prm[j+1]);
//System.out.println("icol"+i+"="+icol[i]+",ilen"+i+"="+ilen[i]);
            }
        }
    }

    private void openFiles() throws IOException, Exception
    {
        recArrLis1 = new ArrayList<String>();
        recArrLis2 = new ArrayList<String>();
        recArrLis3 = new ArrayList<String>();
        recArrLis4 = new ArrayList<String>();
        recArrLis5 = new ArrayList<String>();
        recArrLis6 = new ArrayList<String>();

        fndArrList = new ArrayList<String>();

        fldArrLis1 = new ArrayList<wkArrLst>();
        fldArrLis2 = new ArrayList<wkArrLst>();
        fldArrLis3 = new ArrayList<wkArrLst>();
        fldArrLis4 = new ArrayList<wkArrLst>();
        fldArrLis5 = new ArrayList<wkArrLst>();
        fldArrLis6 = new ArrayList<wkArrLst>();

        loadFile(ifl1,recArrLis1);
        loadFile(ifl2,recArrLis2);

        if ( ifl3.equals(SBLANK) ) return;
        loadFile(ifl3,recArrLis3);

        if ( ifl4.equals(SBLANK) ) return;
        loadFile(ifl4,recArrLis4);

        if ( ifl5.equals(SBLANK) ) return;
        loadFile(ifl5,recArrLis5);

        if ( ifl6.equals(SBLANK) ) return;
        loadFile(ifl6,recArrLis6);
    }

    private void loadFile(String file,
                          ArrayList<String> recarr) throws IOException, Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String txt;

        try {
            while ( (txt=br.readLine()) != null ) {
                recarr.add(txt);
            }
            br.close();
        } // end try

        // Catch a problem reading/writing a file
        catch (IOException e) {
            logexcp(e.toString());
        }
    }

    private void logrep(String txt) throws IOException
    {
        if ( bw == null ) bw = new BufferedWriter(new FileWriter(ofil));

        try {
            System.out.println(txt);
            bw.write(txt);
            bw.newLine();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    private void logexcp(String txt) throws IOException, Exception
    {
        logrep(txt);
        throw new Exception(txt);
    }

    private void fclProc() throws IOException, Exception
    {
        openFiles();

        loadFlds(recArrLis1,fldArrLis1);
        loadFlds(recArrLis2,fldArrLis2);

        if ( !ifl3.equals(SBLANK) ) loadFlds(recArrLis3,fldArrLis3);
        if ( !ifl4.equals(SBLANK) ) loadFlds(recArrLis4,fldArrLis4);
        if ( !ifl5.equals(SBLANK) ) loadFlds(recArrLis5,fldArrLis5);
        if ( !ifl6.equals(SBLANK) ) loadFlds(recArrLis6,fldArrLis6);

        cmprFlds();
        genReport();
    }

    private void loadFlds(ArrayList<String> recarr,
                          ArrayList<wkArrLst> fldarr) throws IOException
    {
        String txt;
        int iln;

        // Extract fields and prepare to compare
        for (int i=0; i<recarr.size(); i++) {
            txt = recarr.get(i);
            iln = txt.length();
            for (int j=0; j<MAXFLDS; j++) {
                if ( icol[j]-1 < 0 || icol[j]-1 >= iln )
                    flds[j] = SBLANK;
                else
                if ( icol[j] <= iln && icol[j]-1+ilen[j] > iln )
                    flds[j] = txt.substring(icol[j]-1);
                else
                    flds[j] = txt.substring(icol[j]-1, icol[j]-1+ilen[j]);
            }
            fldarr.add(new wkArrLst(Integer.toString(i),flds[0],flds[1],flds[2],
                                                        flds[3],flds[4],flds[5]));
        }
    }

    private void cmprFlds()
    {
        int[] irec = new int[MAXIFLS];
        String flds1, str;

        for (int i=0; i<fldArrLis1.size(); i++) {
            irec[0] = i;
            str   = fldArrLis1.get(i).toString();
            flds1 = str.substring(str.indexOf(',')+1). //remove recno
                    replaceAll("[,]+$", "");           //remove trailing commas
                  //replaceAll("^[,]+", "");           //remove leading commas

            if ( (irec[1]=cmprToFlds(flds1, fldArrLis2)) < 0 ) continue; //nomatch

            if ( ifl3.equals(SBLANK) ) {
                matchedFlds(irec); i--; continue;                        //match
            }
            if ( (irec[2]=cmprToFlds(flds1, fldArrLis3)) < 0 ) continue; //nomatch

            if ( ifl4.equals(SBLANK) ) { 
                matchedFlds(irec); i--; continue;                        //match
            }
            if ( (irec[3]=cmprToFlds(flds1, fldArrLis4)) < 0 ) continue; //nomatch

            if ( ifl5.equals(SBLANK) ) { 
                matchedFlds(irec); i--; continue;                        //match
            }
            if ( (irec[4]=cmprToFlds(flds1, fldArrLis5)) < 0 ) continue; //nomatch

            if ( ifl6.equals(SBLANK) ) { 
                matchedFlds(irec); i--; continue;                        //match
            }
            if ( (irec[5]=cmprToFlds(flds1, fldArrLis6)) < 0 ) continue; //nomatch
                matchedFlds(irec); i--; continue;                        //match
        }
    }

    private int cmprToFlds(String flds1, ArrayList<wkArrLst> fldarr)
    {
        String flds2, str;
        int irec2 = -1;
        for (int i=0; i<fldarr.size(); i++) {
            str   = fldarr.get(i).toString();
            flds2 = str.substring(str.indexOf(',')+1). //remove recno
                    replaceAll("[,]+$", "");           //remove trailing commas
            if ( flds1.equals(flds2) ) {
                irec2 = i; break;
            }
        }
        return irec2;
    }

    private void matchedFlds(int[] irec)
    {
        fndArrList.add(fldArrLis1.get(irec[0]).recno());
        fldArrLis1.remove(irec[0]);
        fldArrLis2.remove(irec[1]);
        if ( !ifl3.equals(SBLANK) ) { fldArrLis3.remove(irec[2]); return; }
        if ( !ifl4.equals(SBLANK) ) { fldArrLis4.remove(irec[3]); return; }
        if ( !ifl5.equals(SBLANK) ) { fldArrLis5.remove(irec[4]); return; }
        if ( !ifl6.equals(SBLANK) ) { fldArrLis6.remove(irec[5]); return; }
    }

    private void genReport() throws IOException, Exception
    {
        int nrec = fndArrList.size();
        try 
        {
            bw.newLine();
            bw.write("* Records found in all input files - "+nrec);
            bw.newLine(); bw.newLine();
            iwr+=2;
            for (int i=0; i<nrec; i++) {
                bw.write(recArrLis1.get(Integer.parseInt(fndArrList.get(i))));
                bw.newLine();
                iwr++;
            }
          //bw.close();
        } // end try
        catch (IOException e) {
            logexcp(e.toString());
        }

        genRep("* Records found in Infile1 only - ", recArrLis1, fldArrLis1);
        genRep("* Records found in Infile2 only - ", recArrLis2, fldArrLis2);
        if ( ifl3.equals(SBLANK) ) return;
        genRep("* Records found in Infile3 only - ", recArrLis3, fldArrLis3);
        if ( ifl4.equals(SBLANK) ) return;
        genRep("* Records found in Infile4 only - ", recArrLis4, fldArrLis4);
        if ( ifl5.equals(SBLANK) ) return;
        genRep("* Records found in Infile5 only - ", recArrLis5, fldArrLis5);
        if ( ifl6.equals(SBLANK) ) return;
        genRep("* Records found in Infile6 only - ", recArrLis6, fldArrLis6);
    }

    private void genRep(String hdr, ArrayList<String> recarr,
                                    ArrayList<wkArrLst> fldarr)
                        throws IOException, Exception
    {
        int nrec = fldarr.size();
        try
        {
            bw.newLine();
            bw.write(hdr+nrec);
            bw.newLine(); bw.newLine();
            iwr+=3;
            for (int i=0; i<nrec; i++) {
                bw.write(recarr.get(Integer.parseInt(fldarr.get(i).recno())));
                bw.newLine();
                iwr++;
            }
          //bw.close();
        } // end try
        catch (IOException e) {
            logexcp(e.toString());
        }
    }

    private void nofldProc() throws IOException, Exception
    {
        openFiles();

        // Extract fields and prepare to compare
        for (int i=0; i<recArrLis1.size(); i++)
            fldArrLis1.add(new wkArrLst(Integer.toString(i),recArrLis1.get(i)));

        // Extract fields and prepare to compare
        for (int i=0; i<recArrLis2.size(); i++)
            fldArrLis2.add(new wkArrLst(Integer.toString(i),recArrLis2.get(i)));

        if ( ifl3.equals(SBLANK) ) return;
        for (int i=0; i<recArrLis3.size(); i++)
            fldArrLis3.add(new wkArrLst(Integer.toString(i),recArrLis3.get(i)));

        if ( ifl4.equals(SBLANK) ) return;
        for (int i=0; i<recArrLis4.size(); i++)
            fldArrLis4.add(new wkArrLst(Integer.toString(i),recArrLis4.get(i)));

        if ( ifl5.equals(SBLANK) ) return;
        for (int i=0; i<recArrLis5.size(); i++)
            fldArrLis5.add(new wkArrLst(Integer.toString(i),recArrLis5.get(i)));

        if ( ifl6.equals(SBLANK) ) return;
        for (int i=0; i<recArrLis6.size(); i++)
            fldArrLis6.add(new wkArrLst(Integer.toString(i),recArrLis6.get(i)));

        cmprFlds();
        genReport();
    }

    private boolean isFileExists( String fnam )
    {
        File ifile = new File(fnam);
        if (ifile.exists() && ifile.isFile())
            return true;
        else
            return false;
    }
}