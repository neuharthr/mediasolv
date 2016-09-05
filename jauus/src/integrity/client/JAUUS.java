/*************************************************************************
 Copyright (C) 2005  Steve Gee
 ioexcept@cox.net
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *************************************************************************/

package integrity.client;

import javax.swing.UIManager;
import java.awt.*;

public class JAUUS {

  public JAUUS(String args[]) {
    boolean server = false;
    boolean useGUI = true;
    try{
      if(args.length > 0){
        try {
          for( int loop = 0; loop < args.length; loop++){
            if(args[loop].equalsIgnoreCase("-nogui")) {
              useGUI = false;
            }else if(args[loop].equalsIgnoreCase("-server") || args[loop].equalsIgnoreCase("-shutdown")) {
              server = true;
            }else if(args[loop].equalsIgnoreCase("-delay")) {
              loop++;
              integrity.util.DownloadThrottle.setSleepTime(args[loop]);
            }else if(args[loop].equalsIgnoreCase("-v") || args[loop].equalsIgnoreCase("-version")) {
              System.out.println("[UPD] Version 1.062205.0");
              System.exit(0);
            }else if(args[loop].equalsIgnoreCase("-h") || args[loop].equalsIgnoreCase("-help")) {
              showHelp();
            } else {
              showHelp();
            }//end if-else
          }//end for
        } catch(Exception ex) {
          showHelp();
          ex.printStackTrace();
        }
      }
      if(server){
        new integrity.server.IntegrityServer(args);
      }else{
        if(useGUI) {
          JAUUSUI frame = new JAUUSUI(useGUI);
          frame.validate();
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          Dimension frameSize = frame.getSize();
          if(frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
          }
          if(frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
          }
          frame.setLocation( (screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
          frame.setVisible(true);
          frame.runUpdate();
        } else {
          UIInterface iface = new JAUUSUI(useGUI);
          iface.runUpdate();
        }
      }

    }catch(Exception ex){
      ex.printStackTrace();
      //System.exit(-1);
    }
  }

  private void showHelp(){
    System.out.println("[UPD] Command: java -jar jauusClient.jar [-v, -h, -c <file-location> -noguid, -server, -shutdown, -delay <delay>]\n"
        +"\tNo Arguements starts default GUI Application\n"
        +"\t-nogui\t\tRuns the Client in command line mode\n"
        +"\t-v|-version\tCompiled Version\n"
        +"\t-h|-help\tThe Hecd lp message\n"
        +"\t-c <file-location>\tUse an external configuration file for the server\n"
        +"\t-server\tStart in Server mode"
        +"\t-shutdown\tStop the server"
        +"\t-delay\t\tSet the dealy time for the UI screen to allow users"
        +" to see the information on the screen\n"
        +"\tavailable <delay> arguements (default is 20 milliseconds)\n"
        +"\t  none\tNo delay (0)\n"
        +"\t  slow\t\t1/10th second pause (100 ms)\n"
        +"\t  slower\t1/2 a second pause (500 ms)\n"
        +"\t  slowest\t1 second pause (1000 ms)\n"
        +"\t  long\t5 second pause (5000 ms)");
    System.exit(-1);
  }

  //Main method
  public static void main(String[] args) {
    try {
       UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
       new JAUUS(args);
     }catch(Exception e) {
      e.printStackTrace();
     }
  }
}
