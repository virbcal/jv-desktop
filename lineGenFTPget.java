/*
 * ---------------------------------------------------------------------
 * Copyright (c) 2000-2011 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2013/11
 * ---------------------------------------------------------------------
 * Progname   : lineGenFTPget
 * Description: Generate FTP get statements
 * System     : General use
 * Function   : Read lines from input file,
 *              Convert to FTP get statements,
 *              Write results to output file.
 * Parameters :
 *     infile   (def) flist
 *    outfile   (def) <infile>_ftpget.out
 *      delim   (def) whitespace
 * ---------------------------------------------------------------------
 * Revisions  :
 * 1.0   2013-11-19 virbcal  initial version;
 * 1.1   2013-12-15 virbcal  include optional infile control record to
 *                           specify prepend/append and start-column;
 *                           sample:
 *                           *cntl: PREpend=xxx APPend=yyy STArt=col
 * 1.1.a 2013-12-16 virbcal  use uGetKeywordValue;
 * 1.1.b 2014-04-08 virbcal  set default start parm to 23;
 * 1.1.c 2015-04-24 virbcal  accept *ctl: keyword
 * ---------------------------------------------------------------------
*/

import java.io.*;
import utils.ufill;
import utils.uGetKeywordValue;

public class lineGenFTPget
{
    private final String THISCLAS = this.getClass().getName();
    private final String CLASDESC = "Generate FTP get statements.";
    private final String INFILE  = "flist";      //default input filename
    private final String OFLEXT  = "_ftpget.out";//default output filename ext.
    private final String DEFDELM = " ";          //default delimeter (whitespace)
    private final String SBLANK  = "";           //string blank

    public static void main(String[] args) throws Exception, IOException
    {
        lineGenFTPget instce = new lineGenFTPget();   //create the class instance
        int al = args.length;

        if ( al == 0) { 
            instce.lineGenFTPget();
        } else if ( al == 1) {
            instce.lineGenFTPget(args[0]);
        } else if ( al == 2) {
            instce.lineGenFTPget(args[0], args[1]);
        } else if ( al == 3) {
            instce.lineGenFTPget(args[0], args[1], args[2]);
        } else {
            throw new Exception("Error: Excessive parameters.");
        }
    }

    public void lineGenFTPget()
        throws Exception, IOException
    {
        lineGenFTPget(SBLANK, SBLANK, SBLANK);
    }

    public void lineGenFTPget(String ifil)
        throws Exception, IOException
    {
        lineGenFTPget(ifil, SBLANK, SBLANK);
    }

    public void lineGenFTPget(String ifil, String ofil)
        throws Exception, IOException
    {
        lineGenFTPget(ifil, ofil, SBLANK);
    }

    public void lineGenFTPget(String ifil, String ofil, String delm)
        throws Exception, IOException
    {
        if ( ifil.length() > 0 && ifil.charAt(0) == '?' ) {
            showUsage();
            return;
        }

        ifil = ifil.trim().length() > 0 ? (ifil.charAt(0) == '.' ? INFILE      : ifil) : INFILE;
        ofil = ofil.trim().length() > 0 ? (ofil.charAt(0) == '.' ? ifil+OFLEXT : ofil) : ifil+OFLEXT;
        delm = delm.length() > 0        ? (delm.charAt(0) == '.' ? DEFDELM     : delm) : DEFDELM;
        System.out.println("ifil="+ifil);
        System.out.println("ofil="+ofil);
        System.out.println("delm="+delm);
        if ( !isFileExists(ifil) )
            throw new Exception("Error: Input file does not exist.");
        doProc(ifil, ofil, delm);
    }

    private void showUsage()
    {
        System.out.println("Descptn: "+CLASDESC);
        System.out.println();
        System.out.println("Usage  : "+THISCLAS+" infile outfile");
        System.out.println();
        System.out.println(" where :");
        System.out.println("   infile         input filename");
        System.out.println("                     (def) "+INFILE);
        System.out.println("   outfile        output filename");
        System.out.println("                     (def) <infile>"+OFLEXT);
        System.out.println("   delim          line delimeter");
        System.out.println("                     (def) whitespace");
        System.out.println();
        System.out.println("Note:");
        System.out.println("   . (dot) can be used as a placeholder and to indicate use of default value.");
        System.out.println();
    }


    private void doProc(String ifil, String ofil, String delm)
        throws IOException
    {
        //File ifile = new File(ifil);
        BufferedReader br = new BufferedReader(new FileReader(ifil));

        //File ofile = new File(ofil);
        BufferedWriter bw = new BufferedWriter(new FileWriter(ofil));

        String txt, text;
        int ird=0, iwr=0, ictl=0, icmt=0, ista=0, ltok, i;
        String[] tokens;
        String pre="", app="", sta="", str1, str2;
        StringBuilder fid, fld1, fld2;
        String delims = new StringBuilder()
                        .append('[')
                        .append(delm)
                        .append("]+")
                        .toString();
        uGetKeywordValue ug = new uGetKeywordValue();
        ufill uf = new ufill();

        try 
        {
            // The while loop calls the readLine method as long as
            // there is a line of characters to be read. 
            while ( (txt = br.readLine()) != null )
            {
                ird++;
                text = txt.trim();
                if ( text.length() >= 3 )     // min.len: w1 spc w2
                {
                    tokens = text.split(delims);
                    ltok   = tokens.length;
                    if ( ltok > 1 )
                    {
                        if ( tokens[0].equalsIgnoreCase("*cntl:") ||
                             tokens[0].equalsIgnoreCase("*ctl:")  ) // cntl record found
                        {
                            ictl++;
                            pre=""; app=""; sta="";
                            for (i=1; i<ltok; i++)
                            {
                                if ( pre.isEmpty() ) {
                                    if ( !(pre=ug.getvalue(tokens[i],'=',"prepend",3,true)).isEmpty() )
                                        continue;
                                }
                                if ( app.isEmpty() ) {
                                    if ( !(app=ug.getvalue(tokens[i],'=',"append",3,true)).isEmpty() )
                                        continue;
                                }
                                if ( sta.isEmpty() ) sta=ug.getvalue(tokens[i],'=',"start",3,true);
                            }
                            ista = sta.isEmpty() ? 23 : Integer.valueOf(sta).intValue();
                            System.out.println("CNTL:----");
                            System.out.println("prepend = "+pre);
                            System.out.println("append  = "+app);
                            System.out.println("start   = "+ista);
                            continue;
                        }
                        if ( tokens[0].startsWith("*") ) {          // skip comment record
                            icmt++;
                            continue;
                        }
                        fid = new StringBuilder()
                                  .append(tokens[0])
                                  .append('.')
                                  .append(tokens[1]);
                        fld1= new StringBuilder()
                                  .append("get ")
                                  .append(fid);
                        fld2= new StringBuilder()
                                  .append(pre)
                                  .append(fid)
                                  .append(app);
                        if ( ista > 0 ) str1 = uf.fillr(fld1.toString(),' ',ista-1);
                        else            str1 = fld1.append(' ').toString();
                        if ( !pre.isEmpty() || !app.isEmpty() ) str2 = fld2.toString();
                        else                                    str2 = "";
                        bw.write(new StringBuilder()
                                 .append(str1)
                                 .append(str2)
                                 .toString()
                                );
                        bw.newLine();
                        iwr++;
                    } // end tokens.len
                } // end text.len
            } // end while                

            // Always close a file after opening.
            br.close();
            bw.close();

        } // end try read

        // Catch and handle an exception if there is
        // a problem reading the file.
        catch (IOException ioe)
        {
            System.out.println(ioe);
        }

        System.out.println("Records read    = "+ird);
        System.out.println("Records written = "+iwr);
        System.out.println("Records cntl    = "+ictl);
        System.out.println("Records comment = "+icmt);
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
