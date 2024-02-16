import utils.*;

public class DispSysProps
{
   public static void main(String[] args)
   {
      SysProps sp = new SysProps();
      sp.ListSysProps();
      System.out.println();
      System.out.println("OS: "+sp.getenvValue("OS"));
      System.out.println("os.name: "+sp.getpropValue("os.name"));
      System.out.println("os.version: "+sp.getpropValue("os.version"));
      System.out.println("unknown: "+sp.getpropValue("unknown"));
   }
}

