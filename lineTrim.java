/*
 * ---------------------------------------------------------------------
 * Copyright (c) 2000-2013 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2013/11
 * ---------------------------------------------------------------------
 * Progname   : lineTrim
 * Description: Remove whitespaces at left, right or both sides of each line
 * System     : General use
 * Function   : Read lines from input file,
 *              Trim lines according to the side option,
 *              Write results to output file.
 * Parameters :
 *     infile   (def) trimin
 *    outfile   (def) <infile>_trim.out
 *       side   l/L - left side
 *              r/R - right side (def)
 *              b/B - both sides
 * ---------------------------------------------------------------------
 * Revisions
 * 01  2013-11-17 virbcal   initial release, copied from LineTrimmer.
 * ---------------------------------------------------------------------
*/

import java.io.*;
import utils.uStrip;

public class lineTrim
{
    private final String THISCLAS = this.getClass().getName();
    private final String CLASDESC =
            "Remove whitespaces at left, right or both sides of each line.";
    private final String INFILE  = "trimin";     //default input filename
    private final String OFLEXT  = "_trim.out";  //output filename extension
    private final String VALSIDE = "LRB";        //valid side values
    private final char   DEFSIDE = 'R';          //default side value
    private final String SBLANK  = "";           //string blank
    private final char   CBLANK  = ' ';          //char blank

    public static void main(String[] args) throws Exception, IOException
    {
        lineTrim instce = new lineTrim();        //create the class instance
        int al = args.length;

        if ( al == 0) { 
            instce.lineTrim();
        } else if ( al == 1) {
            instce.lineTrim(args[0]);
        } else if ( al == 2) {
            instce.lineTrim(args[0], args[1]);
        } else if ( al == 3) {
            instce.lineTrim(args[0], args[1], args[2].charAt(0));
        } else {
            throw new Exception("Excessive parameters.");
        }
    }

    public void lineTrim()
        throws Exception, IOException
    {
        lineTrim(SBLANK, SBLANK, CBLANK);
    }

    public void lineTrim(String ifil)
        throws Exception, IOException
    {
        lineTrim(ifil, SBLANK, CBLANK);
    }

    public void lineTrim(String ifil, String ofil)
        throws Exception, IOException
    {
        lineTrim(ifil, ofil, CBLANK);
    }

    public void lineTrim(String ifil, String ofil, char side)
        throws Exception, IOException
    {
        if ( ifil.length() > 0 && ifil.charAt(0) == '?' ) {
            showUsage();
            return;
        }

        ifil = ifil.trim().length() > 0 ? (ifil.charAt(0) == '.' ? INFILE      : ifil) : INFILE;
        ofil = ofil.trim().length() > 0 ? (ofil.charAt(0) == '.' ? ifil+OFLEXT : ofil) : ifil+OFLEXT;
        side = side > CBLANK            ? (side           == '.' ? DEFSIDE     : side) : DEFSIDE;
        System.out.println("ifil="+ifil);
        System.out.println("ofil="+ofil);
        System.out.println("side="+side);
        if ( !isFileExists(ifil) )
            throw new Exception("Error: Input file does not exist.");
        if ( VALSIDE.indexOf(Character.toUpperCase(side)) < 0 )
            throw new Exception("Error: Invalid side value.");
        doProc(ifil, ofil, side);
    }

    private void showUsage()
    {
        System.out.println("Descptn: "+CLASDESC);
        System.out.println();
        System.out.println("Usage  : "+THISCLAS+" infile outfile side");
        System.out.println();
        System.out.println(" where :");
        System.out.println("   infile         input filename");
        System.out.println("                     (def) "+INFILE);
        System.out.println("   outfile        output filename");
        System.out.println("                     (def) <infile>"+OFLEXT);
        System.out.println("   side           side to trim - Left, Right or Both");
        System.out.println("                     (def) R");
        System.out.println();
        System.out.println("Note:");
        System.out.println("   . (dot) can be used as a placeholder and to indicate use of default value.");
        System.out.println();
    }

    private void doProc(String ifil, String ofil, char side)
        throws IOException
    {
        //File ifile = new File(ifil);
        BufferedReader br = new BufferedReader(new FileReader(ifil));

        //File ofile = new File(ofil);
        BufferedWriter bw = new BufferedWriter(new FileWriter(ofil));

        String txt;
        int ird=0, iwr=0;
        uStrip strp = new uStrip();

        try 
        {
            // The while loop calls the readLine method as long as
            // there is a line of characters to be read. 
            while ( (txt = br.readLine()) != null )
            {
                ird += 1;
                if ( txt.length() > 0 )
                    bw.write(strp.uStrip(txt,side));
                else
                    bw.write(txt);
                bw.newLine();
                iwr += 1;
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
