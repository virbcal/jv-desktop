/*
 * ---------------------------------------------------------------------
 * Copyright (c) 2000-2014 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2014/03
 * ---------------------------------------------------------------------
 * Progname   : smrgXtrcMpvmErRc5
 * Description: Extract SMRGPSP log records with RC=5.
 *              "MPVM ERROR RC=" statements.
 * System     : Specific case
 * Function   : Read lines from input file,
 *              - search for header,
 *              - when found,
 *                - save group in temp area,
 *                - search for target message,
 *                - when found, write group to output file.
 * Parameters :
 *     infile   (def) SMASAP.LOGHOLD
 *    outfile   (def) <infile>_mpvmerrc5.out
 * ---------------------------------------------------------------------
 * Revisions  :
 * 1.0   2014-03-12 virbcal  initial release;
 * ---------------------------------------------------------------------
 */

import java.io.*;
import utils.uStrip;

public class smrgXtrcMpvmErRc5
{
    private final String THISCLAS = this.getClass().getName();
    private final String CLASDESC = "Extract SMRGPSP log records with RC=5.";
    private final String INFILE  = "SMASAP.LOGHOLD"; //deflt input filename
    private final String OFLEXT  = "_mpvmerrc5.out"; //deflt output filename ext.
    private final String WKFILE  = THISCLAS+"_work.out";//work filename.  

    public static void main(String[] args) throws Exception, IOException
    {
        smrgXtrcMpvmErRc5 instce = new smrgXtrcMpvmErRc5();//create class instance
        String ifil, ofil;
        int al = args.length;

        if ( al > 0 && args[0].charAt(0) == '?' )
            instce.showUsage();
        else
        {
            ifil = al > 0 ? (args[0].charAt(0) == '.'
                          ? instce.INFILE      : args[0]) : instce.INFILE;
            ofil = al > 1 ? (args[1].charAt(0) == '.'
                          ? ifil+instce.OFLEXT : args[1]) : ifil+instce.OFLEXT;
            System.out.println("ifil="+ifil);
            System.out.println("ofil="+ofil);
            if ( !instce.isFileExists(ifil) )
                throw new Exception("Error: Input file does not exist.");
            instce.doProc(ifil, ofil);
        }
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
        System.out.println();
        System.out.println("Note:");
        System.out.println("   . (dot) can be used as a placeholder and "+
                                      "use of default value.");
        System.out.println();
    }


    private void doProc(String ifil, String ofil)
        throws IOException
    {
        //File ifile = new File(ifil);
        BufferedReader br = new BufferedReader(new FileReader(ifil));

        //File ofile = new File(ofil);
        BufferedWriter bw = new BufferedWriter(new FileWriter(ofil));

        String txt, hdr="";
        int ird=0, iwr=0, iwh=0, iwe=0, iwb=0, tl;
        uStrip strp = new uStrip();
        boolean hdrwritten = false, errfound = false;

        try 
        {
            // The while loop calls the readLine method as long as
            // there is a line of characters to be read. 
            while ( (txt = br.readLine()) != null )
            {
                ird++;
                tl = txt.length();
                if ( tl > 27                             &&
                     txt.startsWith("SMRGPSP")           && // hdr rec found
                     txt.indexOf("(Uses CONNECT LIST)",27) > 27 )
                {
                    hdr = txt;
                }
                else
                if ( tl > 48                             &&
                     txt.startsWith("ERROR")             && // err rec found
                     txt.indexOf("MPVM ERROR RC=",48) > 48 )
                {
                    if ( !hdrwritten )
                    {
                        bw.write(strp.uStrip(hdr,'R'));
                        bw.newLine();
                        iwh++;
                        iwr++;
                        hdrwritten = true;
                    }
                    bw.write(strp.uStrip(txt,'R'));
                    bw.newLine();
                    iwe++;
                    iwr++;
                    errfound = true;
                } else
                if ( tl > 7                              &&
                     txt.startsWith("SMRGPDO") )     // end identifier found
                {
                    if ( errfound )
                    {
                        bw.write("");                // insert blank line
                        bw.newLine();
                        iwb++;
                        iwr++;
                        errfound = false;
                    }
                    hdr = "";
                    hdrwritten = false;
                }
            } // end while

            // Always close file/s after processing is done.
            br.close();
            bw.close();

        } // end try

        // Catch and handle an exception if there is
        // a problem reading/writing a file.
        catch (IOException e) {
            System.out.println(e);
        }

        System.out.println("Records read    = "+ird);
        System.out.println("Records written = "+iwr);
        System.out.println("        headers = "+iwh);
        System.out.println("        errors  = "+iwe);
        System.out.println("        blanks  = "+iwb);
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
