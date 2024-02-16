/*
 * --------------------------------------------------------------------
 * Copyright (c) 2000-2010 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2010/06
 * --------------------------------------------------------------------
 *
 * Progname   : LineCounter
 * Description: Count number of lines
 * System     : General use
 * Function   : Read lines from input file,
 *              Count number of lines,
 *              Report results.
 * Input File :  (def) countin
 * Output File: -
 * Input Parm : -
 * --------------------------------------------------------------------
*/

import java.io.*;

public class LineCounter
{
    private static final String INFILE = "countin";     //default input filename

    public static void main(String[] args) throws IOException
    {
        String ifil = INFILE;
        int al = args.length;

        if (al > 0 && args[0].equals("?")) {
            System.out.println("Usage: LineCounter infile");
            System.out.println();
            System.out.println("where:");
            System.out.println("   infile         input filename");
            System.out.println("                     default: countin");
            System.out.println();
        } else {
            if (al > 0) {
                ifil = args[0];
            }
            System.out.println("ifil="+ifil);
            new LineCounter (ifil);
        }
    }

    public LineCounter(String ifil)
        throws IOException
    {
        //File ifile = new File(ifil);
        BufferedReader br = new BufferedReader(new FileReader(ifil));

        String txt;
        int ird=0;

        try 
        {
            // The while loop calls the readLine method as long as
            // there is a line of characters to be read. 
            while ( (txt = br.readLine()) != null )
            {
                ird += 1;
            } // end while                

            // Always close file/s after processing is done.
            br.close();

        } // end try

        // Catch and handle an exception if there is
        // a problem reading/writing a file.
        catch (IOException e) {
            System.out.println(e);
        }

        System.out.println("Records read = "+ird);
        return;
    }
}
