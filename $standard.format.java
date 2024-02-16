/*
 * --------------------------------------------------------------------
 * Copyright (c) 2000-2010 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2010/06
 * --------------------------------------------------------------------
 *
 * Progname   : LineSplitter
 * Description: Split lines indicated by delimiter/s
 * System     : General use
 * Function   : Read lines from input file,
 *              Split lines using the standard split method in the String class,
 *              Write results to output file.
 * Parameters :
 *     infile   (def) splitin
 *    outfile   (def) <infile>_split.out
 *  delimiter   (def) whitespace
 * ---------------------------------------------------------------------
 * Revisions  :
 * 00= 2010-06-08 virbcal   initial release;
 * 01= 2010-06-25 virbcal   separate showUsage function;
 *                          allow callability from separate main;
 * 01a 2013-01-21 virbcal   replace .equals() with .charAt();
 * .+....1....+....2....+....3....+....4....+....5....+....6....+....7....+....8
 */

import java.io.*;

public class LineSplitter
{
    private        final String THISCLAS = this.getClass().getName();
    public  static final String INFILE = "splitin";    //default input filename
    public  static final String OFLEXT = "_split.out"; //output filename extnsn
    public  static final String WHTSPC = " ";          //default delimiter
    //      static final String DELIM = "[ ]+";        //deflt delim (whitespc)

    public static void main(String[] args) throws IOException
    {
        LineSplitter instce = new LineSplitter();      //create class instance
        String ifil, ofil, deli;
        int al = args.length;

        if ( al > 0 && args[0].charAt(0) == '?' )
            instce.showUsage();
        else
        {
            ifil = al > 0 ? (args[0].charAt(0) == '.'
                          ? instce.INFILE      : args[0]) : instce.INFILE;
            ofil = al > 1 ? (args[1].charAt(0) == '.'
                          ? ifil+instce.OFLEXT : args[1]) : ifil+instce.OFLEXT;
            deli = al > 2 ? args[2] : instce.WHTSPC;
            System.out.println("ifil="+ifil);
            System.out.println("ofil="+ofil);
            System.out.println("deli="+deli);
            instce.doProc(ifil, ofil, deli);
        }
    }

    public void showUsage()
    {
        System.out.println("Usage: "+THISCLAS+" infile outfile delimiter");
        System.out.println();
        System.out.println("where:");
        System.out.println("   infile         input filename");
        System.out.println("                     (def) "+INFILE);
        System.out.println("   outfile        output filename");
        System.out.println("                     (def) <infile>"+OFLEXT);
        System.out.println("   delimiter      character delimiter/s");
        System.out.println("                     default: <whitespace>");
        System.out.println();
        System.out.println("Notes:");
        System.out.println("   . can be used as a placeholder and to indicate
                               use of default value");
        System.out.println("   except as the delimiter value where it will be
                               taken as is");
        System.out.println();
        return;
    }

    public void doProc(String ifil, String ofil, String deli)
        throws IOException
    {
        //File ifile = new File(ifil);
        BufferedReader br = new BufferedReader(new FileReader(ifil));

        //File ofile = new File(ofil);
        BufferedWriter bw = new BufferedWriter(new FileWriter(ofil));

        StringBuilder sb = new StringBuilder();
        String delims = sb.append('[').append(deli).append("]+").toString();
                                                                    //"[ ;.]+"

        String txt;
        int ird=0, iwr=0;
        String[] tokens;

        try 
        {
            // The while loop calls the readLine method as long as
            // there is a line of characters to be read. 
            while ( (txt = br.readLine()) != null )
            {
                ird += 1;
                if ( txt.length() > 0 )
                {
                    tokens = txt.split(delims);
                    if (tokens.length > 0)
                    {
                        for (int i = 0; i < tokens.length; i++)
                        {
                            bw.write(tokens[i]);
                            bw.newLine();
                            iwr += 1;
                        }
                    } else {
                        bw.write(txt);
                        bw.newLine();
                        iwr += 1;
                    }
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
