/*
 * --------------------------------------------------------------------
 * Copyright (c) 2000-2020 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2010/06
 * --------------------------------------------------------------------
 *
 * Progname   : lineXtrcSentence
 * Description: Extract the sentences from a long text string.
 * System     : General use
 * Procedure  : Read lines from input file,
 *              Split lines delimeted by the standard ". ",
 *              Write results to output file.
 * Parameters :
 *     infile   (def) splitin
 *    outfile   (def) <infile>_split.out
 * delimeters   (def) ". " [period+whitespace]
 *  appenddlm   (def) noappend
 *      debug   (def) nodebug
 * ---------------------------------------------------------------------
 * Revisions  :
 * 000 2010-06-08 initial version
 * 01a 2010-06-25 separate showUsage function;
 *                allow callability from separate main;
 * 01b 2015-04-24 rename to lineSplitter (lower case Line);
 *                insert 3rd parm - retain delimeter option;
 * 020 2020-01-17 version update;
 *                replace String.split() with Regex Pattern.split();
 * ---------------------------------------------------------------------
*/

import java.io.*;
import java.util.regex.Pattern; 

public class lineSplitter
{
    private        final String THISCLAS = this.getClass().getName();
    private static final String INFILE   = "splitin";    //default input filename
    private static final String OFLEXT   = "_split.out"; //output filename extension
    private static final String DEFDLM   = ". ";         //default delimeter
    private static final String NO       = "n";          //negative value
    private              boolean debug;

    public static void main(String[] args) throws IOException
    {
        lineSplitter instce = new lineSplitter();      //create the class instance
        int al = args.length;
        String ifil, ofil, delm, apnd, dbug;
        boolean bapnd;

        if ( al > 0 && args[0].equals("?") )
            instce.showUsage();
        else
        {
            ifil = al > 0 ? (args[0].equals(".") ? INFILE      : args[0]) : INFILE;
            ofil = al > 1 ? (args[1].equals(".") ? ifil+OFLEXT : args[1]) : ifil+OFLEXT;
            delm = al > 2 ? (args[2].equals(".") ? DEFDLM      : args[2]) : DEFDLM;
            apnd = al > 3 ? (args[3].equals(".") ? NO          : args[3]) : NO;
            dbug = al > 4 ?  args[4]             : NO;
            System.out.println("ifil="+ifil);
            System.out.println("ofil="+ofil);
            System.out.println("delm="+delm);
            System.out.println("apnd="+apnd);
            System.out.println("dbug="+dbug);
            bapnd = ( apnd.toLowerCase().charAt(0) == 'r' |
                      apnd.toLowerCase().charAt(0) == 'y' ) ? true : false;
            instce.debug = ( dbug.toLowerCase().charAt(0) == 'd' |
                             dbug.toLowerCase().charAt(0) == 'y' ) ? true : false;
            instce.doProc(ifil, ofil, delm, bapnd);
        }
    }

    public void showUsage()
    {
        System.out.println("Usage: "+THISCLAS+" infile outfile delimeter appenddlm debug");
        System.out.println();
        System.out.println("where:");
        System.out.println("   infile         input filename");
        System.out.println("                     (def) "+INFILE);
        System.out.println("   outfile        output filename");
        System.out.println("                     (def) <infile>"+OFLEXT);
        System.out.println("   delimeter      delimeter string");
        System.out.println("                     (def) <dot+whitespace>");
        System.out.println("   appenddlm      append delimeter switch [Y/N]");
        System.out.println("                     (def) <noappend>");
        System.out.println("   debug          debug switch [Y/N]");
        System.out.println("                     (def) <nodebug>");
        System.out.println();
        System.out.println("Note:");
        System.out.println("   . can be used as a placeholder and to indicate use of default value");
        System.out.println("   except as the delimeter value where it will be taken as is.");
        System.out.println();
        return;
    }

    public void doProc(String ifil, String ofil, String delm, boolean bapnd)
        throws IOException
    {
        //File ifile = new File(ifil);
        BufferedReader br = new BufferedReader(new FileReader(ifil));

        //File ofile = new File(ofil);
        BufferedWriter bw = new BufferedWriter(new FileWriter(ofil));

        // Convert delimeter characters to regex format (i.e. ". " -> "\\.\\s")
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<delm.length(); i++) {
            sb = sb.append("\\");
            if ( delm.charAt(i) == ' ' )
                sb = sb.append("s");
            else
                sb = sb.append(delm.charAt(i));
        }
        String delims = sb.toString();
        Pattern pattern = Pattern.compile(delims, Pattern.CASE_INSENSITIVE); 

        String text;
        int ird=0, iwr=0;
        String[] tokens;

        try 
        {
            // The while loop calls the readLine method as long as
            // there is a line of characters to be read. 
            while ( (text = br.readLine()) != null )
            {
                ird++;
                if ( debug )
                    System.out.println("line# " + ird + ", length=" + text.length());
                if ( text.length() > 0 )
                {
                    tokens = pattern.split(text); 
                    if (tokens.length > 0)
                    {
                        for (String token : tokens)
                        {
                            if ( bapnd ) bw.write(token+delm);
                            else         bw.write(token);
                            bw.newLine();
                            iwr++;
                        }
                    } else {
                        bw.write(text);
                        bw.newLine();
                        iwr++;
                    }
                } else {
                    bw.write(text);
                    bw.newLine();
                    iwr++;
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
