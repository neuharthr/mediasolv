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

package integrity.server;

import java.net.*;
import integrity.interfaces.*;
import java.io.*;

public class ShutdownServer implements ClientServerCommunication{
  public ShutdownServer() {
    ObjectOutputStream oos = null;
    Socket socket = null;
    try{
      Thread timer = new Thread();
      System.out.println("[UPD] Shutting down Server...");
      socket = new Socket("localhost",1025);
      oos = new ObjectOutputStream(socket.getOutputStream());
      oos.writeInt(SHUTDOWN);
      oos.flush();
      timer.sleep(1500);
    }catch(ConnectException ce){
    //-- this server is now shutodown
    }catch(SocketException se){
    //-- this server is now shutodown
    }catch(Exception ex){
      ex.printStackTrace();
    }finally{
      try{ oos.close();}catch(Exception ex){}
      try{ socket.close();}catch(Exception ex){}
    }
  }
  public static void main(String[] args) {
    new ShutdownServer();
  }

}