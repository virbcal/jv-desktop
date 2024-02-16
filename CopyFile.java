/*
 * --------------------------------------------------------------------
 * Copyright (c) 2000-2011 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2011/10
 * --------------------------------------------------------------------
 *
 * Progname   : CopyFile
 * Description: Copy file with options
 * System     : General use
 * Function   : Read lines from input file,
 *              Split lines using the standard split method in the String class,
 *              Write results to output file.
 * Syntax     : CopyFile infile outfile ( options
 *     infile   (def) copyin
 *    outfile   (def) <infile>_copy.out
 *          (   option indicator
 *       FRom   (def) 1
 *    FRLabel         xxxxxxxx
 *        FOR   (def) all
 *    TOLabel         xxxxxxxx
 *    REPlace         boolean
 *    APpend          boolean
 * ---------------------------------------------------------------------
 * Revisions  :
 * @00 2011-10-09 initial version
 * ---------------------------------------------------------------------
 */

import java.io.*;

public class CopyFile
{
    private        final String THISCLAS = this.getClass().getName();
    private static final String INFILE = "copyin";     //default input filename
    private static final String OFLEXT = "_copy.out";  //output filename extension
    private static final String OPTDEL = "(";          //option delimeter
    private static final    int DFROM  = 1;            //default FRom value
    private static final    int DFOR   = 99999999;     //default FOR value
    private static          int vFrom,  vFor;
    private static       String vFrlab, vTolab;
    private static      boolean vRep, vApnd;


    public static void main(String[] args) throws IOException
    {
        CopyFile instce = new CopyFile();              //create the class instance
        String ifil, ofil, deli;
        int al = args.length;

        if ( al > 0 && args[0].charAt(0) == '?' )
            instce.showUsage();
        else
        {
            ifil = al > 0 ? (args[0].charAt(0) == '.' ? instce.INFILE      : args[0]) : instce.INFILE;
            ofil = al > 1 ? (args[1].charAt(0) == '.' ? ifil+instce.OFLEXT : args[1]) : ifil+instce.OFLEXT;
            instce.getOpts(args);
            System.out.println("ifil="+ifil);
            System.out.println("ofil="+ofil);
            System.out.println("from="+vFrom);
            System.out.println(" for="+vFor);
            System.out.println("repl="+vRep);
            System.out.println("apnd="+vApnd);
            instce.doProc(ifil, ofil);
        }
    }

    public void showUsage()
    {
        System.out.println("Usage: "+THISCLAS+" infile outfile ( options");
        System.out.println();
        System.out.println("where:");
        System.out.println("   infile         input filename");
        System.out.println("                     (def) "+INFILE);
        System.out.println("   outfile        output filename");
        System.out.println("                     (def) <infile>"+OFLEXT);
        System.out.println("   (              option indicator");
        System.out.println("   options");
        System.out.println("      FRom        from line/record number");
        System.out.println("                     (def) "+DFROM);
        System.out.println("      FRLabel     from label xxxxxxxx which must  start in column 1");
        System.out.println("      FOR         for number of lines/records");
        System.out.println("                     (def) all");
        System.out.println("      TOLabel     to label xxxxxxxx whihc must  start in column 1");
        System.out.println("      REPlace     replace existing output file");
        System.out.println("      APpend      append to end of output file");
        System.out.println();
        System.out.println("Note:");
        System.out.println("   . can be used as a placeholder and to indicate use of default value");
        System.out.println();
        return;
    }

    public void getOpts(String[] args)
    {
        int al = args.length;
        String[] av = new String[al];
        boolean bopts = false;
        int optstart = 0;

        for (int i=0; i<args.length; i++)
        {
            if ( bopts ) {
                av[i] = args[i];
            } else
            if ( args[i].charAt(0) == '(' ) {
                bopts = true;
                av[i] = args[i].substring(1);
                optstart = i;
            } else {
                av[i] = args[i];
            }
        }

        vFrom = DFROM;
        vFor  = DFOR;
        vRep  = false;
        vApnd = false;

        if ( bopts ) {
            for (int i=optstart; i<args.length; i++)
            {
                if ( av[i].equalsIgnoreCase("FR")  || av[i].equalsIgnoreCase("FRom") )
                    vFrom  = Integer.parseInt(av[i+1]); //Integer.valueOf(av[i+1]).intValue()
                else
                if ( av[i].equalsIgnoreCase("FRL") || av[i].equalsIgnoreCase("FRLabel") )
                    vFrlab = av[i+1];
                else
                if ( av[i].equalsIgnoreCase("FOR") )
                    vFor   = Integer.parseInt(av[i+1]);
                else
                if ( av[i].equalsIgnoreCase("TOL") || av[i].equalsIgnoreCase("TOLabel") )
                    vTolab = av[i+1];
                else
                if ( av[i].equalsIgnoreCase("REP") || av[i].equalsIgnoreCase("REPlace") )
                    vRep   = true;
                else
                if ( av[i].equalsIgnoreCase("AP")  || av[i].equalsIgnoreCase("APpend") )
                    vApnd  = true;
                //else
                //throw new Exception("Unknow parameter -- "+av[i]); 
            }
        }

        return;
    }

    public void doProc(String ifil, String ofil)
        throws IOException
    {
        //File ifile = new File(ifil);
        BufferedReader br = new BufferedReader(new FileReader(ifil));

        //File ofile = new File(ofil);
        BufferedWriter bw = new BufferedWriter(new FileWriter(ofil, vApnd));

        String txt, tmp;
        int ird=0, iwr=0;

        try 
        {
            // The while loop calls the readLine method as long as
            // there is a line of characters to be read. 
            while ( (txt = br.readLine()) != null )
            {
                ird += 1;
                if ( ird >= vFrom ) {
                    if ( iwr < vFor ) {
                        bw.write(txt);
                        bw.newLine();
                        iwr += 1;
                    } else break;
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
