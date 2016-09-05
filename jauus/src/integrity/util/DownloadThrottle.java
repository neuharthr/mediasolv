package integrity.util;

public class DownloadThrottle {
  public static long SLEEPTIME = 20;
  public static void setSleepTime(String newSleepTime){
    if(newSleepTime.equalsIgnoreCase("unpossible") || newSleepTime.equalsIgnoreCase("none")){
      SLEEPTIME = 0;
    }else if(newSleepTime.equalsIgnoreCase("SLOW")){
      SLEEPTIME = 100;
    }else if(newSleepTime.equalsIgnoreCase("SLOWER")){
      SLEEPTIME = 500;
    }else if(newSleepTime.equalsIgnoreCase("SLOWEST")){
      SLEEPTIME = 1000;
    }else if(newSleepTime.equalsIgnoreCase("LONG")){
      SLEEPTIME = 5000;
    }else{
      System.out.println("[UPD] Invalid Delay setting, using default 20 milliseconds");
    }
  }
}
