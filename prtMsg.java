/*
 * ---------------------------------------------------------------------
 * Copyright (c) 2000-2019 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2019/12
 * ---------------------------------------------------------------------
 * Progname   : prtMsg
 * Description: Print message
 * System     : General use
 * Function   : Print message
 * Parameters :
 *     msg      (def) null
 * ---------------------------------------------------------------------
 * Revisions  :
 * 000 2019-12-27 initial version
 * ---------------------------------------------------------------------
*/

public class prtMsg
{
    private        final String THISCLAS = this.getClass().getName();

    public static void main(String[] args)
    {
        if ( args.length == 0 )
            System.out.println("");
        else
        if ( args[0].equals("?") )
            new prtMsg().showUsage();
        else
            System.out.println(String.join(" ",args));
    }

    private void showUsage()
    {
        System.out.println("Usage: "+THISCLAS+" msg");
        System.out.println();
        System.out.println("where:");
        System.out.println("   msg            message");
        System.out.println("                     (def) null");
        System.out.println();
        return;
    }
}
