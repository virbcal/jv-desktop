/*
 * --------------------------------------------------------------------
 * Copyright (c) 2000-2012 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2012/01
 * --------------------------------------------------------------------
 *
 * Progname   : trimSTxcript
 * Description: Trim Sametime transcript
 * System     : General use
 * Function   : Read lines from input file,
 *              Trim, replace name with nickname,
 *              Write results to output file.
 * Parameters :
 *     infile   (def) trimst
 *    outfile   (def) <infile>_trimst.out
 *       name   (def) Virgilio...
 *       nick   (def) Vir
 * ---------------------------------------------------------------------
 * Revisions  :
 * 000 2012-01-27 initial version
 * ---------------------------------------------------------------------
*/

import java.io.*;

public class trimSTxcript
{
    private        final String THISCLAS = this.getClass().getName();
    public  static final String INFILE = "trimst";      //default input filename
    public  static final String OFLEXT = "_trimst.out"; //output filename extension
    public  static final String DNAME  = "Virgilio B Calimlim: "; //default ST name
    public  static final String DNICK  = "Vir: ";       //default ST nickname

    public static void main(String[] args) throws IOException
    {
        trimSTxcript instce = new trimSTxcript();      //create the class instance
        String ifil, ofil, name, nick;
        int al = args.length;

        if ( al > 0 && args[0].equals("?") )
            instce.showUsage();
        else
        {
            ifil = al > 0 ? (args[0].equals(".") ? instce.INFILE      : args[0]) : instce.INFILE;
            ofil = al > 1 ? (args[1].equals(".") ? ifil+instce.OFLEXT : args[1]) : ifil+instce.OFLEXT;
            name = al > 2 ? (args[2].equals(".") ? instce.DNAME       : args[2]) : instce.DNAME;
            nick = al > 3 ? (args[3].equals(".") ? instce.DNICK       : args[3]) : instce.DNICK;
            System.out.println("ifil="+ifil);
            System.out.println("ofil="+ofil);
            System.out.println("name="+name);
            System.out.println("nick="+nick);
            instce.doProc(ifil, ofil, name, nick);
        }
    }

    public void showUsage()
    {
        System.out.println("Usage: "+THISCLAS+" infile outfile delimeter");
        System.out.println();
        System.out.println("where:");
        System.out.println("   infile         input filename");
        System.out.println("                     (def) "+INFILE);
        System.out.println("   outfile        output filename");
        System.out.println("                     (def) <infile>"+OFLEXT);
        System.out.println("   name           name of ST participant");
        System.out.println("                     default: "+DNAME);
        System.out.println("   nick           nickname of ST participant");
        System.out.println("                     default: "+DNICK);
        System.out.println();
        System.out.println("Notes:");
        System.out.println("   . can be used as a placeholder and to indicate use of default value");
        System.out.println("   except as the delimeter value where it will be taken as is");
        System.out.println();
        return;
    }

    public void doProc(String ifil, String ofil, String name, String nick)
        throws IOException
    {
        //File ifile = new File(ifil);
        BufferedReader br = new BufferedReader(new FileReader(ifil));

        //File ofile = new File(ofil);
        BufferedWriter bw = new BufferedWriter(new FileWriter(ofil));

        StringBuilder sb;
        String txt, text;
        int ird=0, iwr=0, ndx;
        int namel=name.length();

        try 
        {
            // The while loop calls the readLine method as long as
            // there is a line of characters to be read. 
            while ( (txt = br.readLine()) != null )
            {
                ird++;
                if ( txt.length() > 0 )
                {
                    if ( (ndx=txt.indexOf(name)) > 0 )
                    {
                        sb = new StringBuilder();
                        text = sb.append(nick).append(txt.substring(ndx+namel)).toString();
                        bw.write(text);
                    } else {
                        bw.write(txt);
                    }
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
