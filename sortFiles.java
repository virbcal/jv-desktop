/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2000-2018 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2018/04
 *.......^.........^.........^.........^.........^.........^.........^.........8
 * Progname   : sortFiles
 * Description: Sort files utility
 * System     : General use
 * Function   : Read records from one or more input files,
 *              Sort records according to one or more fields or the full record,
 *              Write sorted records to output file.
 * Parameters :
 *             *Positional parameters
 *   infile +   input filename                                       (1)(2)
 *              (def) sortin
 *             *Non-positional parameters using switches
 *  /I          input file switch
 *   infile +   input filename                                       (1)(2)
 *              (def) sortin
 *  /O          output file switch
 *   outfile    output sort file                                     (2)
 *              (def) <infile1>_sort.out
 *  /L          output logfile switch
 *   logfile    output logfile                                       (2)
 *              (def) <infile1>_sort.log
 *  /FCL        sort field column-length switch                      (3)(6)
 *   stacol     start column                                         (4)
 *   fldlen     field length                                         (4)
 *   datyp      datatype                                             (4)(8)(9)
 *   +
 *  /FPD        sort field position-delimited format switch          (3)(6)
 *   delim      delimiter character                                  (7)
 *   posnum     position number                                      (5)
 *   datyp      datatype                                             (5)(8)(9)
 *   +
 *  /R          reverse lexicographical order switch
 *              ---
 *       NOTES: (1) max-of-6 infiles may be specified
 *              (2) will use the default for the missing required entry
 *              (3) /FCL & /FPD are mutually exclusive
 *              (4) max-of-6 stacol-fldlen-datyp pairs may be specified
 *              (5) max-of-6 posnum-datyp pairs may be specified
 *              (6) whole record field is taken if both /FCL & /FPD are missing
 *              (7) use s for whitespace delimeter
 *              (8) valid values: str (String), int (integer), dte (date format)
 *              (9) date format : dte(MM/dd/yyyy) or other format variations
 * -----------------------------------------------------------------------------
 * Revisions
 * 1.0 2018-04-15 virbcal   initial release.
 * 1.1 2018-04-30 virbcal   more thorough validation of switches.
 * 2.0 2018-05-08 virbcal   sortkey parms to include datatype.
 * 2.1 2018-05-11 virbcal   reverse option added.
 * 3.0 2018-05-19 virbcal   accept upto 6 infiles.
 * -----------------------------------------------------------------------------
 */

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import utils.wkArrLst;

public class sortFiles
{
    private final String THISCLAS = this.getClass().getName();
    private final String CLASDESC =
            "Sort files utility";
    private final String CLASRELS = "v3r0";
    private final String ER000001 =
            "     + Processing terminated for "+THISCLAS+".";
    private final String ER000011 =
            "Error: Invalid switch found.";
    private final String ER000012 =
            "Error: Repetition of switch not allowed.";
    private final String ER000013 =
            "Error: Repetition of input file specification not allowed.";
    private final String ER000014 =
            "Error: /FCL and /FPD switches are mutually exclusive.";
    private final String ER000015 =
            "Error: Input file does not exist.";
    private final String ER000016 =
            "Error: Unsupported/incomplete field datatype specification - ";
    private final String ER000017 =
            "Error: Max number of input file parameters exceeded.";
    private final String ER000018 =
            "Error: Max number of /I parameters exceeded.";
    private final String ER000021 =
            "Error: Max number of /FCL pair parameters exceeded.";
    private final String ER000022 =
            "Error: Missing /FCL required parameter/s.";
    private final String ER000023 =
            "Error: Invalid /FCL parameter; Numeric required.";
    private final String ER000031 =
            "Error: Max number of /FPD pair parameters exceeded.";
    private final String ER000032 =
            "Error: Missing /FPD required parameter/s.";
    private final String ER000033 =
            "Error: Invalid /FPD parameter; Numeric required.";
    private final  String   INFILE   = "sortin";     //deflt input filename
    private final  String   OFLEXT   = "_sort.out";  //deflt output file ext
    private final  String   LOGEXT   = "_sort.log";  //deflt output logf ext
    private final  String   SBLANK   = "";           //string blank
    private final  char     CBLANK   = ' ';          //char blank
    private final  char     TABCHAR  = '\t';         //tab char
    private static final int MAXIFLS = 6;            //max infiles
    private static final int MAXFLDS = 6;            //max flds/fld-pairs
    private final  String   DTYPDFT  = new String(".");
    private final  String   DTYPSTR  = new String("str");
    private final  String   DTYPINT  = new String("int");
    private final  String   DTYPDTE  = new String("dte");
    private final  String[] VALDTYP  = new String[] {
                                       DTYPDFT,DTYPSTR,DTYPINT,DTYPDTE};
    private final  int      VTYPLEN  = VALDTYP.length;
    private static String   swi, swo, swl, swfcl, swfpd, swr, delim,
                            ifl1, ifl2, ifl3, ifl4, ifl5, ifl6, ofil, logf;
    private static boolean  reverse  = false;
    private static String[] flds = new String[MAXFLDS];
    private static String[] dtyp = new String[MAXFLDS];
    private static int[]    icol = new int[MAXFLDS];
    private static int[]    ilen = new int[MAXFLDS];
    private static int[]    ipos = new int[MAXFLDS];
    private static int[]    irec = new int[MAXIFLS+1];
    private static List<String>        recList;
    private static ArrayList<wkArrLst> fldArrList;
  //private static BufferedReader br;
    private static BufferedWriter bw;
    private static BufferedWriter bwlog;
    private static int      iwr;

    public static void main(String[] args) throws IOException, Exception
    {
        //create the class instance
        sortFiles instce = new sortFiles();
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
"\n"+THISCLAS+" [infile +] [/I infile +] [/O outfile] [/FCL stacol fldlen +]      "+     
"\n             [/FPD delim posnum datyp +]                                       "+     
"\n              *Positional parameters                                           "+     
"\n   infile +   input filename                                       (1)(2)      "+     
"\n              (def) sortin                                                     "+     
"\n             *Non-positional parameters using switches                         "+     
"\n  /I          input file switch                                                "+     
"\n   infile +   input filename                                       (1)(2)      "+     
"\n              (def) sortin                                                     "+     
"\n  /O          output file switch                                               "+     
"\n   outfile    output sort file                                     (2)         "+     
"\n              (def) <infile1>_sort.out                                         "+     
"\n  /L          output logfile switch                                            "+     
"\n   logfile    output logfile                                       (2)         "+     
"\n              (def) <infile1>_sort.log                                         "+     
"\n  /FCL        sort field column-length switch                      (3)(6)      "+     
"\n   stacol     start column                                         (4)         "+     
"\n   fldlen     field length                                         (4)         "+     
"\n   datyp      datatype                                             (4)(8)(9)   "+     
"\n   +                                                                           "+     
"\n  /FPD        sort field position-delimited format switch          (3)(6)      "+     
"\n   delim      delimiter character                                  (7)         "+     
"\n   posnum     position number                                      (5)         "+     
"\n   datyp      datatype                                             (5)(8)(9)   "+     
"\n   +                                                                           "+     
"\n  /R          reverse lexicographical order switch                             "+     
"\n              ---                                                              "+     
"\n       NOTES: (1) max-of-6 infiles may be specified                            "+     
"\n              (2) will use the default for the missing required entry          "+     
"\n              (3) /FCL & /FPD are mutually exclusive                           "+     
"\n              (4) max-of-6 stacol-fldlen-datyp pairs may be specified          "+     
"\n              (5) max-of-6 posnum-datyp pairs may be specified                 "+     
"\n              (6) whole record field is taken if both /FCL & /FPD are missing  "+     
"\n              (7) use s for whitespace delimeter                               "+     
"\n              (8) valid values: str (String), int (integer), dte (date format) "+     
"\n              (9) date format : dte(MM/dd/yyyy) or other format variations     ");
    }

    private void doProc(String[] args) throws IOException, Exception
    {
        valParms(args);
        if ( swfcl.length() > 0 ) fclProc();
        else
        if ( swfpd.length() > 0 ) fpdProc();
        else                      nofldProc();

        logrep(SBLANK);
         logrep("Infile1 records read    = "+irec[1]);
        if ( !ifl2.equals(SBLANK) )
         logrep("Infile2 records read    = "+irec[2]);
        if ( !ifl3.equals(SBLANK) )
         logrep("Infile3 records read    = "+irec[3]);
        if ( !ifl4.equals(SBLANK) )
         logrep("Infile4 records read    = "+irec[4]);
        if ( !ifl5.equals(SBLANK) )
         logrep("Infile5 records read    = "+irec[5]);
        if ( !ifl6.equals(SBLANK) )
         logrep("Infile6 records read    = "+irec[6]);
         logrep("Total   records read    = "+irec[0]);
         logrep("Outfile records written = "+iwr);

        try {
            bwlog.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    private void valParms(String[] args) throws Exception
    {
        int argsln = args.length;
        int i, j, k, ndx, ndx1, ndx2;
        String argsin, argstr;
        StringBuffer sb;

        ifl1 = SBLANK; ifl2 = SBLANK; ifl3 = SBLANK;
        ifl4 = SBLANK; ifl5 = SBLANK; ifl6 = SBLANK;
        ofil = SBLANK; logf = SBLANK;

        swi   = SBLANK; swo   = SBLANK; swl   = SBLANK;
        swfcl = SBLANK; swfpd = SBLANK; swr   = SBLANK;

        sb = new StringBuffer();
        for (i=0; i<argsln; i++)
            sb = sb.append(args[i]).append(' ');
        argsin = "===>"+sb.toString().trim()+"<";
        System.out.println(argsin);//args

        // Extract infiles entered before any switch keyword
        j=0;
        for (k=0; k<argsln; k++) {
            if ( args[k].trim().charAt(0) == '/' ) break;
            if ( k == 0 ) { ifl1 = args[k].trim(); j++; continue; } //ifl1
            if ( k == 1 ) { ifl2 = args[k].trim(); j++; continue; } //ifl2
            if ( k == 2 ) { ifl3 = args[k].trim(); j++; continue; } //ifl3
            if ( k == 3 ) { ifl4 = args[k].trim(); j++; continue; } //ifl4
            if ( k == 4 ) { ifl5 = args[k].trim(); j++; continue; } //ifl5
            if ( k == 5 ) { ifl6 = args[k].trim(); j++; continue; } //ifl6
            if ( k >= MAXIFLS )                                     //max
                throw new Exception("\n"+ER000017+"\n"+ER000001);
        }

        sb = new StringBuffer();
        for (i=j; i<argsln; i++)
            sb = sb.append(args[i]).append(' ');
        argstr = sb.toString().trim();
        argsln = argstr.length();
        System.out.println(" ==>"+argstr+"<");          //switches

        //Extract valid switch groups and validate parms
        String swgrp = SBLANK;
        ndx1 = argstr.indexOf('/');
        ndx2 = 0;
        while ( ndx1 >= 0 && ndx2 >= 0 && ndx1 < argsln) {
            if ( (ndx2=argstr.substring(ndx1+1).indexOf('/')) >= 0 ) {
                if ( argstr.substring(ndx1).indexOf('(') < ndx2 )
                    ndx2 = argstr.substring(ndx1).indexOf(" /");
            }
            if ( ndx2 >= 0 )
                swgrp = argstr.substring(ndx1,ndx2);    //sw group
            else
                swgrp = argstr.substring(ndx1);         //sw group
//System.out.println("valParms: swgrp="+swgrp+"<");

           if ( swgrp.indexOf("/i "  ) == 0 ) {         //infile sw
                if ( swi.length() > 0 )                 //dup sw
                    throw new Exception("\n"+ER000012+"\n"+ER000001);
                if ( ifl1.length() > 0 )                //dup spec
                    throw new Exception("\n"+ER000013+"\n"+ER000001);
                valInfiles(swgrp);
                swi = swgrp; ndx1 = ndx2+1;
            }
            else
            if ( swgrp.indexOf("/o "  ) == 0 ) {        //outfile sw
                if ( swo.length() > 0 )                 //dup sw
                    throw new Exception("\n"+ER000012+"\n"+ER000001);
                ndx  = swgrp.indexOf(' ');              //find first white space
                ofil = swgrp.substring(ndx+1);          //remove leading sw kw
                swo  = swgrp; ndx1 = ndx2+1;
            }
            else
            if ( swgrp.indexOf("/l "  ) == 0 ) {        //logfile sw
                if ( swl.length() > 0 )                 //dup sw
                    throw new Exception("\n"+ER000012+"\n"+ER000001);
                ndx  = swgrp.indexOf(' ');              //find first white space
                logf = swgrp.substring(ndx+1);          //remove leading sw kw
                swl  = swgrp; ndx1 = ndx2+1;
            }
            else
            if ( swgrp.indexOf("/fcl ") == 0 ) {        //fcl sw
                if ( swfcl.length() > 0 )               //dup sw
                    throw new Exception("\n"+ER000012+"\n"+ER000001);
                if ( swfpd.length() > 0 )               //mutexc
                    throw new Exception("\n"+ER000014+"\n"+ER000001);
                valFcl(swgrp);
                swfcl = swgrp; ndx1 = ndx2+1;
            }
            else
            if ( swgrp.indexOf("/fpd ") == 0 ) {        //fpd sw
                if ( swfpd.length() > 0 )               //dup sw
                    throw new Exception("\n"+ER000012+"\n"+ER000001);
                if ( swfcl.length() > 0 )               //mutexc
                    throw new Exception("\n"+ER000014+"\n"+ER000001);
                valFpd(swgrp);
                swfpd = swgrp; ndx1 = ndx2+1;
            }
            else 
            if ( swgrp.indexOf("/r"   ) == 0 ) {        //rev sw
                if ( swr.length() > 0 )                 //dup sw
                    throw new Exception("\n"+ER000012+"\n"+ER000001);
                reverse = true;
                swr   = swgrp; ndx1 = ndx2+1;
            }
            else
                throw new Exception("\n"+ER000011+"\n"+ER000001); //invalid
        } //end while

        if ( ifl1.equals(SBLANK) ) ifl1 = INFILE;
        if ( ofil.equals(SBLANK) ) ofil = ifl1+OFLEXT;
        if ( logf.equals(SBLANK) ) logf = ifl1+LOGEXT;

        bwlog = new BufferedWriter(new FileWriter(logf));

        logrep("\n"+THISCLAS+" ("+CLASRELS+")");
        logrep(argsin);
        logrep(SBLANK);

        logrep("ifl1="+ifl1);
        if ( !isFileExists(ifl1) ) logexcp("\n"+ER000015+"\n"+ER000001);

        if ( !ifl2.equals(SBLANK) ) {
            logrep("ifl2="+ifl2);
            if ( !isFileExists(ifl2) ) logexcp("\n"+ER000015+"\n"+ER000001);
        }
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
        logrep("logf="+logf);
    }

    private void valInfiles(String swg) throws Exception
    {
        int ndx, prmln;
        String prms;

        ndx  = swg.indexOf(' ');                //find first white space
        prms = swg.substring(ndx+1);            //remove leading switch keyword
        String[] prm = prms.split(" ");
        prmln = prm.length;

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
//System.out.println("valFcl: swg="+swg+"<");
        int ndx;
        String prms;
        String[] prm;

        for (int i=0; i<MAXFLDS; i++) {
            icol[i] = 0;
            ilen[i] = 0;
            dtyp[i] = SBLANK;
        }
        ndx  = swg.indexOf(' ');                //find first white space
        prms = swg.substring(ndx+1);            //remove leading switch keyword

        for (int i=0; i<MAXFLDS; i++) {         //extract the parms
            prms = prms.trim();
//System.out.println(" -->prms="+prms+"<");
            if ( prms.length() <= 0 ) break;

            prm = prms.split(" ");
//System.out.println("  ->prm0="+prm[0]+" prm1="+prm[1]);
            if ( !prm[0].matches("[0-9]+") ||
                 !prm[1].matches("[0-9]+") )
                throw new Exception("\n"+ER000023+"\n"+ER000001);//not num

            icol[i] = Integer.parseInt(prm[0]);
            ilen[i] = Integer.parseInt(prm[1]);

            prms = valDtyp(prms, prm, i, 2);
//System.out.println("  ->col="+icol[i]+" len="+ilen[i]+" typ="+dtyp[i]);
        } // end for
    }

    private void valFpd(String swg) throws Exception
    {
//System.out.println("valFpd: swg="+swg+"<");
        int ndx, ndx2;
        String prms;
        String[] prm;

        for (int i=0; i<MAXFLDS; i++) {
            ipos[i] = 0;
            dtyp[i] = SBLANK;
        }
        ndx  = swg.indexOf(' ');                //find first white space  (sw kw)
        ndx2 = swg.indexOf(' ',ndx+1);          //find second white space (delim)
        delim= swg.substring(ndx+1,ndx2);       //delimeter character/s
        prms = swg.substring(ndx2+1);           //remove leading switch kw & delim

        for (int i=0; i<MAXFLDS; i++) {         //extract the parms
            prms = prms.trim();
//System.out.println(" -->prms="+prms+"<");
            if ( prms.length() <= 0 ) break;

            prm = prms.split(" ");
//System.out.println("  ->prm0="+prm[0]);
            if ( !prm[0].matches("[0-9]+") )
                throw new Exception("\n"+ER000033+"\n"+ER000001);//not num

            ipos[i] = Integer.parseInt(prm[0]);

            prms = valDtyp(prms, prm, i, 1);
//System.out.println("  ->delim="+delim+" pos="+ipos[i]+" typ="+dtyp[i]);
        } // end for
    }

    private String valDtyp(String prms, String[] prm, int i, int idtyp)
                           throws Exception
    {
        int ndx, ndx2;

        for (int j=0; j<VTYPLEN; j++) {
            if ( prm[idtyp].indexOf(VALDTYP[j]) == 0 ) {
                dtyp[i] = VALDTYP[j];
                break;
            }
        }

        if ( dtyp[i].equals(DTYPDTE) )
            if ( (ndx =prms.indexOf("(")) > 0 &&
                 (ndx2=prms.indexOf(")")) > 0 )
                dtyp[i] = dtyp[i] + prms.substring(ndx,ndx2+1);
            else
             throw new Exception("\n"+ER000016+prm[idtyp]+"\n"+ER000001);//incomp

        if ( dtyp[i].equals(SBLANK) )
             throw new Exception("\n"+ER000016+prm[idtyp]+"\n"+ER000001);//incomp

        ndx  = prms.indexOf(dtyp[i]);
        prms = prms.substring(ndx+dtyp[i].length());

        return prms;
    }

    private void openFiles() throws IOException, Exception
    {
        recList    = new ArrayList<String>();
        fldArrList = new ArrayList<wkArrLst>();

                                    irec[1] = loadFile(ifl1, recList);
        if ( !ifl2.equals(SBLANK) ) irec[2] = loadFile(ifl2, recList);
        if ( !ifl3.equals(SBLANK) ) irec[3] = loadFile(ifl3, recList);
        if ( !ifl4.equals(SBLANK) ) irec[4] = loadFile(ifl4, recList);
        if ( !ifl5.equals(SBLANK) ) irec[5] = loadFile(ifl5, recList);
        if ( !ifl6.equals(SBLANK) ) irec[6] = loadFile(ifl6, recList);
                                    irec[0] = recList.size();
    }

    private int loadFile(String ifile, List<String> recarr)
                         throws IOException, Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(ifile));
        String txt;
        int i=0;

        try {
            while ( (txt=br.readLine()) != null ) {
                recarr.add(txt);
                i++;
            }
            br.close();
        } // end try
        // Catch a problem reading/writing a file
        catch (IOException e) {
            logexcp(e.toString());
        }
        return i;
    }

    private void logrep(String txt) throws IOException
    {
        try {
            System.out.println(txt);
            bwlog.write(txt.replaceAll("\n",""));
            bwlog.newLine();
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

        loadFclFlds(recList, fldArrList);

        sortFile();
    }

    private void loadFclFlds(List<String> recarr,
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

    private void fpdProc() throws IOException, Exception
    {
        openFiles();

        loadFpdFlds(recList, fldArrList);

        sortFile();
    }

    private void loadFpdFlds(List<String> recarr,
                             ArrayList<wkArrLst> fldarr) throws Exception
    {
        String rec;
        int flen;
        String[] fld = new String[0];

        if ( delim.equals("s") ) delim = " ";   //whitespace

        // Extract fields and prepare to compare
        for (int i=0; i<recarr.size(); i++) {
            rec = recarr.get(i);
            Arrays.fill(fld,null);
            fld = rec.split(delim);
            flen= fld.length;
            for (int j=0; j<MAXFLDS; j++) {
                if ( ipos[j]-1 < 0 || ipos[j]-1 >= flen )
                    flds[j] = SBLANK;
                else
                    flds[j] = fld[ipos[j]-1];
            }
            fldarr.add(new wkArrLst(Integer.toString(i),flds[0],flds[1],flds[2],
                                                        flds[3],flds[4],flds[5]));
        }
    }

    private void nofldProc() throws IOException, Exception
    {
        openFiles();

        dtyp[0] = DTYPDFT;
        for (int i=1; i<MAXFLDS; i++)
            dtyp[i] = SBLANK;

        // Extract fields and prepare to compare
        for (int i=0; i<recList.size(); i++)
            fldArrList.add(new wkArrLst(Integer.toString(i),recList.get(i)));

        sortFile();
    }

    private void sortFile() throws IOException
    {
      //Collections.sort(fldArrList);
        Collections.sort(fldArrList, new Comparator<wkArrLst>()
        {
            @Override
            public int compare(wkArrLst n, wkArrLst o)
            {
//System.out.println("compare: nfld1="+n.fld1()+",ofld1="+o.fld1()+",
//                             typ="+dtyp[0]+",orec="+o.recno());
                int lastCmp = comadre(n.fld1(), o.fld1(), dtyp[0], o.recno());

                if ( dtyp[1].equals(SBLANK) ) return lastCmp;
                if ( lastCmp == 0 )
                    lastCmp = comadre(n.fld2(), o.fld2(), dtyp[1], o.recno());

                if ( dtyp[2].equals(SBLANK) ) return lastCmp;
                if ( lastCmp == 0 )
                    lastCmp = comadre(n.fld3(), o.fld3(), dtyp[2], o.recno());

                if ( dtyp[3].equals(SBLANK) ) return lastCmp;
                if ( lastCmp == 0 )
                    lastCmp = comadre(n.fld4(), o.fld4(), dtyp[3], o.recno());

                if ( dtyp[4].equals(SBLANK) ) return lastCmp;
                if ( lastCmp == 0 )
                    lastCmp = comadre(n.fld5(), o.fld5(), dtyp[4], o.recno());

                if ( dtyp[5].equals(SBLANK) ) return lastCmp;
                if ( lastCmp == 0 )
                    lastCmp = comadre(n.fld6(), o.fld6(), dtyp[5], o.recno());

                return lastCmp;
            }

            public int comadre(String ns, String os, String typ, String recno)
            {
                int iln = typ.length();
//System.out.println("comadre: ns="+ns+",os="+os+",typ="+typ+",recno="+recno);
                if ( typ.equals(DTYPDFT) || typ.equals(DTYPSTR) )
                    return ns.compareTo(os);
                else
                if ( typ.equals(DTYPINT) )
                    return Integer.parseInt(ns) - Integer.parseInt(os);
                else
                if ( typ.indexOf(DTYPDTE) == 0 ) {
//System.out.println("         val="+typ));
                    DateFormat fmt = new SimpleDateFormat(typ.substring(
                                                          DTYPDTE.length()+1,iln-1));
                    try {
                        return fmt.parse(ns).compareTo(fmt.parse(os));
                    } catch (ParseException e) {
                        System.out.println(e);
                    }
                }
                else {
                System.out.println("comadre: ns="+ns+",os="+os+",typ="+typ+",recno="+recno);
                System.out.println("comadre:\n"+ER000016+typ+"\n"+ER000001);  //unsup
                  //throw new Exception("\n"+ER000016+typ+"\n"+ER000001); //unsup
                }
                return 0;
            }
        });

        if ( reverse ) Collections.reverse(fldArrList);

        bw = new BufferedWriter(new FileWriter(ofil));
        iwr = 0;

        try 
        {
            for (int i=0; i<fldArrList.size(); i++) {
                bw.write(recList.get(Integer.parseInt(fldArrList.get(i).recno())));
                bw.newLine();
                iwr++;
            }
            // Always close file/s after processing is done.
            bw.close();
        } // end try

        // Catch and handle an exception if there is
        // a problem reading/writing a file.
        catch (IOException e) {
            logrep(e.toString());
        }
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