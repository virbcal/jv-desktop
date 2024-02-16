/*
 * ---------------------------------------------------------------------
 * Copyright (c) 2000-2011 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2010/06
 * ---------------------------------------------------------------------
 * Progname   : LineSeqno
 * Description: Insert line sequence number
 * System     : General use
 * Function   : Read lines from input file,
 *              Insert sequence number,
 *              Write results to output file.
 * Parameters :
 *     infile   (def) seqnoin
 *    outfile   (def) <infile>_seqno.out
 *   startnum   (def) 1
 *  increment   (def) 1
 *      width   (def) 3
 *       side   l/L - left side  (def)
 *              r/R - right side
 * ---------------------------------------------------------------------
 * Revisions  :
 * 00  2010-06-16 virbcal  initial version;
 * 01  2011-05-19 virbcal
 *                use of THISCLAS;
 *                use of showUsage and doProc routines;
 *                allow callability from separate main;
 * 01a 2013-08-17 virbcal
 *                correct infil shown at report;
 *                replace if-then-else parm validation with ternary operator ?:;
 *                use of CLASDESC;
 *                validation of side variable;
 * 01b 2013-08-20 virbcal
 *                add option to add/remove trailing 0s from sequence numbers;
 * ---------------------------------------------------------------------
*/

import java.io.*;
import utils.ufill;

public class LineSeqno
{
    private final String THISCLAS = this.getClass().getName();
    private final String CLASDESC =
            "Insert sequence number at left or right side of each line.";
    private final String INFILE  = "seqnoin";    //default input filename
    private final String OFLEXT  = "_seqno.out"; //default output filename ext.
    private final String VALSIDE = "LR";         //valid side values
    private final String VALYN   = "YN";         //valid yes/no values
    private final int    DEFSTAR = 1;            //default starting number
    private final int    DEFINCR = 1;            //default increment
    private final int    DEFWIDE = 3;            //default width of seq number
    private final char   DEFSIDE = 'L';          //default side value
    private final char   DEFTRL0 = 'N';          //default trail0s value

    public static void main(String[] args) throws Exception, IOException
    {
        LineSeqno instce = new LineSeqno();      //create the class instance
        String ifil, ofil;
        int ista, incr, iwid;
        char side, trl0;
        int al = args.length;

        if ( al > 0 && args[0].charAt(0) == '?' )
            instce.showUsage();
        else
        {
            ifil = al > 0 ? (args[0].charAt(0) == '.' ? instce.INFILE      : args[0]) : instce.INFILE;
            ofil = al > 1 ? (args[1].charAt(0) == '.' ? ifil+instce.OFLEXT : args[1]) : ifil+instce.OFLEXT;
            ista = al > 2 ? (args[2].charAt(0) == '.' ? instce.DEFSTAR     : Integer.valueOf(args[2]).intValue()) : instce.DEFSTAR;
            incr = al > 3 ? (args[3].charAt(0) == '.' ? instce.DEFINCR     : Integer.valueOf(args[3]).intValue()) : instce.DEFINCR;
            iwid = al > 4 ? (args[4].charAt(0) == '.' ? instce.DEFWIDE     : Integer.valueOf(args[4]).intValue()) : instce.DEFWIDE;
            side = al > 5 ? (args[5].charAt(0) == '.' ? instce.DEFSIDE     : args[5].charAt(0)) : instce.DEFSIDE;
            trl0 = al > 6 ? (args[6].charAt(0) == '.' ? instce.DEFTRL0     : args[6].charAt(0)) : instce.DEFTRL0;

            System.out.println("ifil="+ifil);
            System.out.println("ofil="+ofil);
            System.out.println("strt="+ista);
            System.out.println("incr="+incr);
            System.out.println("wdth="+iwid);
            System.out.println("side="+side);
            System.out.println("trl0="+trl0);

            side = Character.toUpperCase(side);
            trl0 = Character.toUpperCase(trl0);
            if ( instce.VALSIDE.indexOf(side) < 0 )
               throw new Exception("Invalid side value.");
            else if ( instce.VALYN.indexOf(trl0) < 0 )
               throw new Exception("Invalid trl0 value.");
            else
               instce.doProc (ifil, ofil, ista, incr, iwid, side, trl0);
        }
    }

    public void showUsage()
    {
        System.out.println("Descptn: "+CLASDESC);
        System.out.println();
        System.out.println("Usage: "+THISCLAS+" infile outfile startnum increment width side trail0");
        System.out.println();
        System.out.println("where:");
        System.out.println("   infile         input filename");
        System.out.println("                     default: trimin");
        System.out.println("   outfile        output filename");
        System.out.println("                     default: <infile>_trim.out");
        System.out.println("   startnum       starting number of sequence");
        System.out.println("                     default: 1");
        System.out.println("   increment      increment of sequence numbers");
        System.out.println("                     default: 1");
        System.out.println("   width          width (no. of digits) of sequence numbers");
        System.out.println("                     default: 3");
        System.out.println("   side           side to insert - Left/Right");
        System.out.println("                     default: L");
        System.out.println("   trail0         trailing 0s - Yes/No");
        System.out.println("                     default: N");
        System.out.println();
        System.out.println("Note:");
        System.out.println("   . can be used as placeholder");
        System.out.println();
        return;
    }

    public void doProc(String ifil, String ofil, int istart, int increm, int iwidth, char side, char trl0)
        throws IOException
    {
        //File ifile = new File(ifil);
        BufferedReader br = new BufferedReader(new FileReader(ifil));

        //File ofile = new File(ofil);
        BufferedWriter bw = new BufferedWriter(new FileWriter(ofil));

        String txt;
        int ird=0, iwr=0, iseq=istart-increm, maxlen=0, len;
        StringBuilder sb;
        ufill uf = new ufill();
        char pad=' ';
        if ( trl0 == 'Y' ) pad='0';

        try 
        {
            // The while loop calls the readLine method as long as
            // there is a line of characters to be read. 
            while ( (txt = br.readLine()) != null )
            {
                if ( txt.length() > maxlen ) maxlen = txt.length();
                ++ird;
            }
            if ( iwidth < (len=Integer.valueOf(ird).toString().length()) )
                iwidth = len;
            ird=0;

            br.close();
            br = new BufferedReader(new FileReader(ifil));
            while ( (txt = br.readLine()) != null )
            {
                ++ird;
                iseq += increm;
                sb = new StringBuilder();
                if ( side == 'R' )
                {
                    bw.write(sb.append(uf.fillr(txt,' ',maxlen))
                               .append(' ')
                               .append(uf.filll(iseq,pad,iwidth))
                               .toString()
                            );
                }
                else
                {
                    bw.write(sb.append(uf.filll(iseq,pad,iwidth))
                               .append(' ')
                               .append(txt)
                               .toString()
                            );
                }
                bw.newLine();
                ++iwr;
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
