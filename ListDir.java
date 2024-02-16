/*
 * ---------------------------------------------------------------------
 * Copyright (c) 2013 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2013/09
 * ---------------------------------------------------------------------
 * Progname   : ListDir
 * Description: List directory entries
 * System     : General use
 * Function   : List directory entries
 * Parameters :
 *       path   (def) .
 *       extn   (def) *
 * ---------------------------------------------------------------------
 * Revisions  :
 * 00  2013-09-23 virbcal  initial release;
 * ---------------------------------------------------------------------
*/

import java.io.*;

public class ListDir
{
    private final String THISCLAS = this.getClass().getName();
    private final String CLASDESC = "List directory entries in path.";
    private final String DEFPATH = ".";          //default path
    private final String DEFEXTN = "*";          //default extension

    public static void main(String[] args) throws Exception, IOException
    {
        ListDir instce = new ListDir();          //create the class instance
        String path, extn;
        int al = args.length;

        if ( al > 0 && args[0].charAt(0) == '?' )
            instce.showUsage();
        else
        {
            path = al > 0 ? (args[0].charAt(0) == '.' ? instce.DEFPATH : args[0]) : instce.DEFPATH;
            extn = al > 1 ? (args[1].charAt(0) == '.' ? args[1]        : args[1]) : instce.DEFEXTN;

            System.out.println("path="+path);
            System.out.println("extn="+extn);
            System.out.println();

            if ( extn.length()  == 0 ||
                 extn.charAt(0) == '*' )
                instce.doProc (path);
            else
                instce.doProc (path, extn);
        }
    }

    public void showUsage()
    {
        System.out.println("Descptn: "+CLASDESC);
        System.out.println();
        System.out.println("Usage: "+THISCLAS+" path extension");
        System.out.println();
        System.out.println("where:");
        System.out.println("   path           path");
        System.out.println("                     default: '.'");
        System.out.println("   extension      file extension");
        System.out.println("                     default: '*' all entries");
        System.out.println();
        System.out.println("Note:");
        System.out.println("   . can be used as placeholder");
        System.out.println();
        return;
    }

    public void doProc(String path)
    {
        File f = null;
        String[] flist;
            
        try {      
            // create new file
            f = new File(path);
                                 
            // array of files and directory
            flist = f.list();
            
            // for each name in the path array
            for (String entry:flist)
            {
                // prints filename and directory name
                System.out.println(entry);
            }
        } catch (Exception e) {
            // if any error occurs
            e.printStackTrace();
        }

    }

    public void doProc(String path, String extn)
    {
        // File extension
        extn = extn.toLowerCase();
        if ( extn.charAt(0) != '.' )
           extn = "."+extn;

        String files;
        try {
            File folder = new File(path);
            File[] listOfFiles = folder.listFiles(); 
 
            for (int i=0; i<listOfFiles.length; i++) 
            {
                if (listOfFiles[i].isFile()) 
                {
                    files = listOfFiles[i].getName();
                    if (files.toLowerCase().endsWith(extn))
                    {
                        System.out.println(files);
                    }
                }
            }
        } catch (Exception e) {
            // if any error occurs
            e.printStackTrace();
        }
    }
}