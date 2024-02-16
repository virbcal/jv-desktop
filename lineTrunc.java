/*
 * ---------------------------------------------------------------------
 * Copyright (c) 2000-2013 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2013/11
 * ---------------------------------------------------------------------
 * Progname   : lineTrunc
 * Description: Truncate line at left or right side
 * System     : General use
 * Function   : Read lines from input file,
 *              Truncate lines according to the side option,
 *              Write results to output file.
 * Parameters :
 *     infile   (def) trncin
 *    outfile   (def) <infile>_trnc.out
 *       side   truncate side
 *              l/L - left side
 *              r/R - right side (def)
 *       tcol   truncate column
 *              (def) 11 for side=L
 *              (def) 72 for side=R
 * ---------------------------------------------------------------------
 * Revisions
 * 01  2013-11-17 virbcal   initial release, copied from TruncLine.
 * ---------------------------------------------------------------------
*/

import java.io.*;
import utils.uStrip;

public class lineTrunc
{
    private final String THISCLAS = this.getClass().getName();
    private final String CLASDESC =
            "Remove whitespaces at left, right or both sides of each line.";
    private final String INFILE  = "trncin";     //default input filename
    private final String OFLEXT  = "_trnc.out";  //output filename extension
    private final String VALSIDE = "LR";         //valid side values
    private final char   DEFSIDE = 'R';          //default side value
    private final int    DEFCOLL = 11;           //default trunc-col left side
    private final int    DEFCOLR = 72;           //default trunc-col right side
    private final String SBLANK  = "";           //string blank
    private final char   CBLANK  = ' ';          //char blank

    public static void main(String[] args) throws Exception, IOException
    {
        lineTrunc instce = new lineTrunc();      //create the class instance
        int al = args.length;

        if ( al == 0) { 
            instce.lineTrunc();
        } else if ( al == 1) {
            instce.lineTrunc(args[0]);
        } else if ( al == 2) {
            instce.lineTrunc(args[0], args[1]);
        } else if ( al == 3) {
            instce.lineTrunc(args[0], args[1], args[2].charAt(0));
        } else if ( al == 4) {
            instce.lineTrunc(args[0], args[1], args[2].charAt(0), args[3]);
        } else {
            throw new Exception("Excessive parameters.");
        }
    }

    public void lineTrunc()
        throws Exception, IOException
    {
        lineTrunc(SBLANK, SBLANK, CBLANK, SBLANK);
    }

    public void lineTrunc(String ifil)
        throws Exception, IOException
    {
        lineTrunc(ifil, SBLANK, CBLANK, SBLANK);
    }

    public void lineTrunc(String ifil, String ofil)
        throws Exception, IOException
    {
        lineTrunc(ifil, ofil, CBLANK, SBLANK);
    }

    public void lineTrunc(String ifil, String ofil, char side)
        throws Exception, IOException
    {
        lineTrunc(ifil, ofil, side, SBLANK);
    }

    public void lineTrunc(String ifil, String ofil, char side, String scol)
        throws Exception, IOException
    {
        int tcol=0;
      //tcol = Integer.parseInt(scol);
      //tcol = Integer.valueOf(scol).intValue();
        side = side > CBLANK            ? (side           == '.' ? DEFSIDE     : side) : DEFSIDE;
        if ( Character.toUpperCase(side) == 'L' ) {
            tcol = scol.trim().length() > 0 ? (scol.charAt(0) == '.' ? DEFCOLL : Integer.valueOf(scol).intValue()) : DEFCOLL;
        } else
        if ( Character.toUpperCase(side) == 'R' ) {
            tcol = scol.trim().length() > 0 ? (scol.charAt(0) == '.' ? DEFCOLR : Integer.valueOf(scol).intValue()) : DEFCOLR;
        }
        lineTrunc(ifil, ofil, side, tcol);
    }

    public void lineTrunc(String ifil, String ofil, char side, int tcol)
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
        System.out.println("tcol="+tcol);
        if ( !isFileExists(ifil) )
            throw new Exception("Error: Input file does not exist.");
      //if ( VALSIDE.indexOf(Character.toUpperCase(side)) < 0 )
      //    throw new Exception("Invalid side value.");
        if ( Character.toUpperCase(side) == 'L' ) {
            doProcL(ifil, ofil, tcol);
        } else
        if ( Character.toUpperCase(side) == 'R' ) {
            doProcR(ifil, ofil, tcol);
        } else
            throw new Exception("Error: Invalid side value.");
    }

    private void showUsage()
    {
        System.out.println("Descptn: "+CLASDESC);
        System.out.println();
        System.out.println("Usage  : "+THISCLAS+" infile outfile side tcol");
        System.out.println();
        System.out.println(" where :");
        System.out.println("   infile         input filename");
        System.out.println("                     (def) "+INFILE);
        System.out.println("   outfile        output filename");
        System.out.println("                     (def) <infile>"+OFLEXT);
        System.out.println("   side           side to truncate - Left or Right");
        System.out.println("                     (def) R");
        System.out.println("   truncol        truncate column");
        System.out.println("                     (def) "+DEFCOLL+" for side=L");
        System.out.println("                     (def) "+DEFCOLR+" for side=R");
        System.out.println();
        System.out.println("Note:");
        System.out.println("   . (dot) can be used as a placeholder and to indicate use of default value.");
        System.out.println();
        return;
    }

    private void doProcL( String ifil, String ofil, int tcol )
        throws FileNotFoundException, IOException
    {
        File ifile = new File(ifil);
        BufferedReader br = new BufferedReader(new FileReader(ifil));

        File ofile = new File(ofil);
        BufferedWriter bw = new BufferedWriter(new FileWriter(ofil));

        String txt;
        int ird=0, iwr=0, tlen0, tlen1;
        uStrip strp = new uStrip();

        try 
        {
            // The while loop calls the readLine method as long as
            // there is a line of characters to be read. 
            while ( (txt=br.readLine()) != null )
            {
                ird += 1;
                if ( txt.length() > 0 )
                {
                    txt = strp.uStrip(txt,'r');
                    if ( txt.length() >= tcol )
                        txt = txt.substring(tcol);
                    else
                        txt = SBLANK;
                }
                try 
                {
                    bw.write(txt);
                    bw.newLine();
                    iwr += 1;
                }
                catch (IOException ioe)
                {
                    System.out.println(ioe);
                }
            } // end while                

            // Always close a file after opening.
            br.close();
            bw.close();

        } // end try read

        // Catch and handle the exception in the
        // event that the named file cannot be found.
        catch (FileNotFoundException fnfe)
        {
            System.out.println(fnfe);
        }

        // Catch and handle an exception if there is
        // a problem reading the file.
        catch (IOException ioe)
        {
            System.out.println(ioe);
        }

        System.out.println("Records read    = "+ird);
        System.out.println("Records written = "+iwr);
        return;
    }

    private void doProcR( String ifil, String ofil, int tcol )
        throws FileNotFoundException, IOException
    {
        File ifile = new File(ifil);
        BufferedReader br = new BufferedReader(new FileReader(ifil));

        File ofile = new File(ofil);
        BufferedWriter bw = new BufferedWriter(new FileWriter(ofil));

        String txt;
        int ird=0, iwr=0, ndx;
        uStrip strp = new uStrip();

        try 
        {
            // The while loop calls the readLine method as long as
            // there is a line of characters to be read. 
            while ( (txt = br.readLine()) != null )
            {
                ird += 1;
                if ( txt.length() > tcol )
                    txt = txt.substring(0,tcol);
                txt = strp.uStrip(txt,'r');
                try 
                {
                    bw.write(txt);
                    bw.newLine();
                    iwr += 1;
                }
                catch (IOException ioe)
                {
                    System.out.println(ioe);
                }
            } // end while                

            // Always close a file after opening.
            br.close();
            bw.close();

        } // end try read

        // Catch and handle the exception in the
        // event that the named file cannot be found.
        catch (FileNotFoundException fnfe)
        {
            System.out.println(fnfe);
        }

        // Catch and handle an exception if there is
        // a problem reading the file.
        catch (IOException ioe)
        {
            System.out.println(ioe);
        }

        System.out.println("Records read    = "+ird);
        System.out.println("Records written = "+iwr);
        return;
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

