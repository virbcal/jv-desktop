/*
 * ---------------------------------------------------------------------
 * Copyright (c) 2000-2010 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2010/06
 * ---------------------------------------------------------------------
 * Progname   : LineSearch
 * Description: Search for a text string
 * System     : General use
 * Function   : Read lines from input file,
 *              Search for a text string,
 *              If found, write the lines to output file.
 * Parameters :
 *     infile   (def) srchin
 *    outfile   (def) <infile>_srch.out
 *    srchstr   (req)
 * ---------------------------------------------------------------------
 * Revisions  :
 * 000 2010-06-20 initial version
 * ---------------------------------------------------------------------
*/

import java.io.*;

public class LineSearch
{
    private        final String THISCLAS = this.getClass().getName();
    private static final String INFILE = "srchin";    //default input filename
    private static final String OFLEXT = "_srch.out"; //output filename extension

    public static void main(String[] args) throws IOException
    {
        String ifil = INFILE;
        String ofil = ifil+OFLEXT;
        String srch;
        int al = args.length;

        if ( (al > 0 && args[0].equals("?")) ||
              al < 3 ) 
            new LineSearch().showUsage();
        else
        {
            ifil = al > 0 ? (args[0].equals(".") ? INFILE      : args[0]) : INFILE;
            ofil = al > 1 ? (args[1].equals(".") ? ifil+OFLEXT : args[1]) : ifil+OFLEXT;
            srch = args[2];
            System.out.println("ifil="+ifil);
            System.out.println("ofil="+ofil);
            System.out.println("srch="+srch);
            LineSearch (ifil, ofil, srch);
        }
    }

    private void showUsage()
    {
        System.out.println("Usage: "+THISCLAS+" infile outfile srchstr");
        System.out.println();
        System.out.println("where:");
        System.out.println("   infile         input filename");
        System.out.println("                     (def) "+INFILE);
        System.out.println("   outfile        output filename");
        System.out.println("                     (def) <infile>"+OFLEXT);
        System.out.println("   srchstr        search string");
        System.out.println("                     (req)");
        System.out.println();
        System.out.println("Notes:");
        System.out.println("   . can be used as placeholder");
        System.out.println();
        return;
    }

    private static void LineSearch(String ifil, String ofil, String srchstr)
        throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(ifil));
        BufferedWriter bw = new BufferedWriter(new FileWriter(ofil));

        String txt;
        int ird=0, iwr=0;

        try 
        {
            // The while loop calls the readLine method as long as
            // there is a line of characters to be read. 
            while ( (txt = br.readLine()) != null )
            {
                ++ird;
                if ( txt.length() > 0  &&
                     txt.indexOf(srchstr) >= 0 )
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
