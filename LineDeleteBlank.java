/*
 * --------------------------------------------------------------------
 * Copyright (c) 2000-2010 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2010/06
 * --------------------------------------------------------------------
 *
 * Progname   : LineDeleteBlank
 * Description: Delete blank lines
 * System     : General use
 * Function   : Read lines from input file,
 *              Delete blank lines,
 *              Write results to output file.
 * Input File :  (def) delblkin
 * Output File:  (def) <infile>_delblk.out
 * Input Parm : -
 * --------------------------------------------------------------------
*/

import java.io.*;

public class LineDeleteBlank
{
    private static final String INFILE = "delblkin";    //default input filename
    private static final String OFLEXT = "_delblk.out"; //output filename extension

    public static void main(String[] args) throws IOException
    {
        String ifil = INFILE;
        String ofil = ifil+OFLEXT;
        int al = args.length;

        if (al > 0 && args[0].equals("?")) {
            System.out.println("Usage: LineDeleteBlank infile outfile delimeter");
            System.out.println();
            System.out.println("where:");
            System.out.println("   infile         input filename");
            System.out.println("                     default: delblkin");
            System.out.println("   outfile        output filename");
            System.out.println("                     default: <infile>_delblk.out");
            System.out.println();
            System.out.println("Notes:");
            System.out.println("   . can be used as placeholder");
            System.out.println();
        } else {
            if (al > 0) {
                if (args[0].equals(".")) {}
                else
                    ifil = args[0];
            }
            if (al > 1) {
                if (args[1].equals("."))
                    ofil = ifil+OFLEXT;
                else
                    ofil = args[1];
            }
            System.out.println("ifil="+ifil);
            System.out.println("ofil="+ofil);
            new LineDeleteBlank (ifil, ofil);
        }
    }

    public LineDeleteBlank(String ifil, String ofil)
        throws IOException
    {
        //File ifile = new File(ifil);
        BufferedReader br = new BufferedReader(new FileReader(ifil));

        //File ofile = new File(ofil);
        BufferedWriter bw = new BufferedWriter(new FileWriter(ofil));

        String txt, tmp;
        int ird=0, iwr=0;

        try 
        {
            // The while loop calls the readLine method as long as
            // there is a line of characters to be read. 
            while ( (txt = br.readLine()) != null )
            {
                ird += 1;
                tmp = txt.trim();
                if ( tmp.length() > 0 )
                {
                    bw.write(txt);
                    bw.newLine();
                    iwr += 1;
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
        return;
    }
}
