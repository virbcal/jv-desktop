/*
 * ---------------------------------------------------------------------
 * Copyright (c) 2013 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2013/09
 * ---------------------------------------------------------------------
 * Progname   : menu
 * Description: List executable entries
 * System     : General use
 * Function   : List java "class" files
 * Parameters :
 *       path   (def) .
 * ---------------------------------------------------------------------
 * Revisions  :
 * 00  2013-09-23 virbcal  initial release;
 * 00a 2013-09-24 virbcal  add option for showUsage;
 * 00b 2015-04-16 virbcal  add variable file extension;
 * ---------------------------------------------------------------------
*/

public class menu
{
    private final String THISCLAS = this.getClass().getName();
    private final String CLASDESC = "List files with the specified file extension";
    private final String DEFPATH = ".";          //default path
    private final String DEFFEXT = "class";      //default file extension

    public static void main(String[] args)
    {
        menu instce = new menu();                //create the class instance
        String path = args.length > 0 ? args[0] : instce.DEFPATH;
        String fext = args.length > 1 ? args[1] : instce.DEFFEXT;

        if ( path.charAt(0) == '?' )
            instce.showUsage();
        else
            new ListDir().doProc(path,fext);
    }

    public void showUsage()
    {
        System.out.println("Descptn: "+CLASDESC);
        System.out.println();
        System.out.println("Usage: "+THISCLAS+" path fext");
        System.out.println();
        System.out.println("where:");
        System.out.println("   path           search path");
        System.out.println("                     default: '.'");
        System.out.println("   fext           file extension");
        System.out.println("                     default: 'class'");
        System.out.println();
        return;
    }
}