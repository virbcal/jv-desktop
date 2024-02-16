/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2000-2018 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2018/05
 *.......^.........^.........^.........^.........^.........^.........^.........8
 * Progname   : fmtToHelpScreen
 * Description: Format file to Help screen
 * System     : General use
 * Function   : Read records from input file,
 *              Format records to Help screen,
 *              Write formatted records to output file.
 * Parameters :
 *             *Positional parameters
 *   infile     input filename                                       (1)(2)
 *              (def) helpscrin
 *   outfile    output filename                                      (1)(2)
 *              (def) <infile>_helpscr.out
 *              ---
 *       NOTES: (1) 1 filename may be specified
 *              (2) will use the default if not specified
 * -----------------------------------------------------------------------------
 * Revisions
 * 1.0 2018-05-16 virbcal   initial release.
 * -----------------------------------------------------------------------------
 */

import java.io.*;

public class fmtToHelpScreen
{
    private final String THISCLAS = this.getClass().getName();
    private final String CLASDESC =
            "Format records to Help screen";
    private final String CLASRELS = "v1r0";
    private final String ER000001 =
            "     + Processing terminated for "+THISCLAS+".";
    private final String ER000011 =
            "Error: Input file does not exist.";
    private final String ER000017 =
            "Error: Max number of file parameters exceeded.";
    public  static final String INFILE = "helpscrin";  //default input filename
    public  static final String OFLEXT = "_helpscr.out"; //output file extension
    private final String SBLANK   = "";           //string blank
    private final char   CBLANK   = ' ';          //char blank
    private final int    MAXFILES = 2;            //max files
    private final int    MAXWIDTH = 79;           //max width
    private       String   ifil, ofil;
    private       int      ird, iwr;
  //private BufferedReader br;
  //private BufferedWriter bw;

    public static void main(String[] args) throws Exception, IOException
    {
        //create the class instance
        fmtToHelpScreen instce = new fmtToHelpScreen();
        if ( args.length > 0 &&
            (args[0].equals("?") || args[0].equals("/?")) ) {
            instce.showUsage();
            return;
        }
        instce.doProc(args);
    }

    private void showUsage()
    {
        System.out.println(THISCLAS+" ("+CLASRELS+")"+
"\n"+CLASDESC+
"\n"+THISCLAS+" [infile] [outfile]                                               "+
"\n   infile     input filename                                       (1)(2)     "+
"\n              (def) helpscrin                                                 "+
"\n   outfile    output filename                                      (1)(2)     "+
"\n              (def) <infile>_helpscr.out                                      "+
"\n              ---                                                             "+
"\n       NOTES: (1) 1 filename may be specified                                 "+
"\n              (2) will use the default if not specified                       ");
    }

    public void doProc(String[] args) throws IOException, Exception
    {
        valParms(args);
        doFormat();

        System.out.println("Records read    = "+ird);
        System.out.println("Records written = "+iwr);
    }

    public void valParms (String[] args) throws Exception
    {
        ifil = SBLANK; ofil = SBLANK;

        for (int i=0; i<args.length; i++) {
            if ( i == 0 ) { ifil = args[i].trim(); continue; } //ifil
            if ( i == 1 ) { ofil = args[i].trim(); continue; } //ofil
            if ( i >= MAXFILES )                               //max
                throw new Exception("\n"+ER000017+"\n"+ER000001);
        }

        if ( ifil.equals(SBLANK) ) ifil = INFILE;
        if ( ofil.equals(SBLANK) ) ofil = ifil+OFLEXT;

        System.out.println("ifil="+ifil);
        if ( !isFileExists(ifil) )
            throw new Exception("\n"+ER000011+"\n"+ER000001);
        System.out.println("ofil="+ofil);
    }

    public void doFormat () throws IOException
    {
        //File ifile = new File(ifil);
        BufferedReader br = new BufferedReader(new FileReader(ifil));

        //File ofile = new File(ofil);
        BufferedWriter bw = new BufferedWriter(new FileWriter(ofil));

        String txt, str;
        ird=0; iwr=0;

        try 
        {
            // The while loop calls the readLine method as long as
            // there is a line of characters to be read. 
            while ( (txt=br.readLine()) != null )
            {
                ird++;
                str = txt.substring(2);
                StringBuffer sb = new StringBuffer().append(str);
                for (int i=str.length(); i<MAXWIDTH; i++)
                     sb = sb.append(" ");
                bw.write("*sta*"+sb.toString()+"*end*");
                bw.newLine();
                iwr++;
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
