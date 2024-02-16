/*
 * --------------------------------------------------------------------
 * Copyright (c) 2010 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2010/11
 * --------------------------------------------------------------------
 *
 * Progname   : getJulian
 * Description: Get the Julian date (non-graphic)
 * System     : General use
 * Function   : Get input parameters,
 *              show parameters
 *              print the Julian date in the terminal console.
 * Parameters :
 *       yyyy   (def) current year
 *         mm   (def) current month
 *         dd   (def) current day
 * ---------------------------------------------------------------------
 * Revisions  :
 * 000 2010-11-27 initial version
 * ---------------------------------------------------------------------
 */

import java.util.*;

public class getJulian
{
   private        final String THISCLAS = this.getClass().getName();

   public static void main(String[] args) throws Exception
   {
      getJulian instce = new getJulian(); //create the class instance
      int al = args.length;

      if ( al > 0 && args[0].equals("?") )
         instce.showUsage();
      else
      {
         Calendar cal = Calendar.getInstance();
         Date dt = new Date ();
         cal.setTime (dt);

         int yyyy = al > 0 ? (args[0].equals(".") ? cal.get(Calendar.YEAR)         : Integer.valueOf(args[0]).intValue()) : cal.get(Calendar.YEAR);
         int   mm = al > 0 ? (args[1].equals(".") ? 1 + cal.get(Calendar.MONTH)    : Integer.valueOf(args[1]).intValue()) : 1 + cal.get(Calendar.MONTH);
         int   dd = al > 0 ? (args[2].equals(".") ? cal.get(Calendar.DAY_OF_MONTH) : Integer.valueOf(args[2]).intValue()) : cal.get(Calendar.DAY_OF_MONTH);

         System.out.println("yyyy="+yyyy+", mm="+mm+", dd="+dd);
         System.out.println("julian="+instce.getDayOfYear( yyyy, mm, dd ));
      }
   }

    private void showUsage()
    {
        System.out.println("Usage: "+THISCLAS+" yyyy mm dd");
        System.out.println();
        System.out.println("where:");
        System.out.println("   yyyy           year of date");
        System.out.println("                     (def) current year");
        System.out.println("   mm             month of date");
        System.out.println("                     (def) current month");
        System.out.println("   dd             day of date");
        System.out.println("                     (def) current day");
        System.out.println();
        System.out.println("Notes:");
        System.out.println("   . (dot) can be used as a placeholder and to indicate use of default value");
        System.out.println();
        return;
    }

   private int getDayOfYear( int year, int month, int day ) throws Exception
   {
      int[] daysInMonth = {31,28,31,30,31,30,31,31,30,31,30,31};

      if (month < 1 || month > 12)
         throw new Exception("Month value '"+month+"' out of range."); 

      if ( month > 1 ) // past January
         if ( ( (year%4 == 0)&&(year%100 != 0) ) || (year%400 == 0) ) // leap year
            daysInMonth[1] = 29;

      if (day < 1 || day > daysInMonth[month-1])
         throw new Exception("Day value '"+day+"' out of range."); 

      int julian = day;

      for(int iMo=0; iMo<month-1; iMo++)
         julian = julian + daysInMonth[iMo];

      return julian;
   }

}
