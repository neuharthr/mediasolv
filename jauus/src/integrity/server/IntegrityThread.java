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
import java.io.*;
import integrity.TagReader;
import integrity.interfaces.ClientServerCommunication;
import integrity.ClientServerContainer;
import java.util.*;
import integrity.util.*;

public class IntegrityThread extends Thread implements ClientServerCommunication{
  private String FILE_SEP = System.getProperties().getProperty("file.separator");
  private String instanceConfig;
  private Socket openSocket;
  private boolean online = true;
  private ObjectInputStream ois;
  private ObjectOutputStream oos;
  private TagReader instanceReader;
  private CheckSumUtility checkSumUtil;
  private ClientServerContainer csc = new ClientServerContainer();
  private String applicationDir;

  public IntegrityThread(Socket skt, String icon, String appdir) throws Exception{
    openSocket = skt;
    ois = new ObjectInputStream(openSocket.getInputStream());
    oos = new ObjectOutputStream(openSocket.getOutputStream());
    instanceConfig = icon;
    applicationDir = appdir;
    checkSumUtil = new CheckSumUtility();
  }

  public void run() {
    startListener();
  }

  private void startListener() {
    try {
      int inputValue = -1;
      while(online) {
        inputValue = ois.readInt();
       switch (inputValue) {
         case SHUTDOWN:
           System.out.println("[UPD] Shutdown command recieved...(not implemented)");
           //System.exit(-1942);
           break;
         case CLOSE_CONNECTION:
           disconnect();
           break;
         case RUN_UPDATE:
           String application = (String)ois.readObject();
           instanceReader = new TagReader(instanceConfig + application + ".xml");
           csc.setTReader(instanceReader);
           generateFileContainer();
//           csc.setServerCheckSum( checkSumUtil.getChecksumValue( applicationDir + instanceReader.getTagValue("structure","softwarehome"),csc.keys()) ) ;
//           checkSumUtil.getChecksumValueX(csc,
//               applicationDir + instanceReader.getTagValue("structure","softwarehome"));
           csc = checkSumUtil.getChecksumValue(csc,
               applicationDir + instanceReader.getTagValue("structure","softwarehome"));
           oos.writeObject(csc);
           oos.flush();
           break;
         case REQUEST_FILE:
           String fileName = (String)ois.readObject();
           if(csc.containsKey(fileName)){
             String fileToPush = fileName.substring(3).replaceAll("::"," ").replaceAll("\\:", "\\" + FILE_SEP);
             pushFile(new File(applicationDir + instanceReader.getTagValue("structure","softwarehome") + CheckSumUtility.FILE_SEP + fileToPush));
           }else{
             System.err.println("Requesting invalid file: " + fileName);
             pushDeadFile();
           }//end if-else
           break;
         default:
           disconnect();
         break;
       }//end switch
      } //end while
    }catch(SocketException se){
    } catch(Exception e) {
      System.out.println("[UPD] ErrorStartingItegrityThread::IntegrityThread::startListener ");
      e.printStackTrace();
      disconnect();
    } //end try-catch
  } //end startListener


  private void generateFileContainer() throws Exception{
//####################################################################//
//System.out.println("[UPD] LOADING THE FILE LISTING HERE");
    Hashtable extensions = instanceReader.getTags("extensions");
    Hashtable exclusions = instanceReader.getTags("exclude");
    Hashtable exclusionExtensions = instanceReader.getTags("excludeExtensions");
    int index = 0;
//####################################################################//
System.out.println("[UPD] SCAN: " + applicationDir + instanceReader.getTagValue("structure","softwarehome"));
      RecursiveDirectory recFile = new RecursiveDirectory();
      recFile.recurse(applicationDir + FILE_SEP + instanceReader.getTagValue("structure","softwarehome"),"",extensions,exclusions,exclusionExtensions);
      Vector fileVec = new Vector();
      fileVec.addAll(recFile);
      StringBuffer directoryStructure = new StringBuffer();
      directoryStructure.append("<jauus>" + (char)13 + (char)10);
      directoryStructure.append(recFile.getFileStructure());
      directoryStructure.append("</jauus>");
System.out.println("[UPD] struct: " + directoryStructure.toString().replaceAll(" ","::"));
      TagReader iReader = new TagReader();
      iReader.parseFile(directoryStructure.toString().replaceAll(" ","::"));
      csc.setFileSet(iReader);
//System.out.println("[UPD] --< END LOADING THE FILE LISTING HERE >--");
//####################################################################//
  }//end generateFileContainer

  private void pushFile(File fileName) throws Exception{
    BufferedInputStream buffInputStream = null;
    try{
      buffInputStream = new BufferedInputStream(new FileInputStream(fileName));
      int available = buffInputStream.available();
      System.out.println("[UPD] " + new java.util.Date() + " :: From: " + openSocket.getInetAddress());
	  System.out.println("[UPD] Filename: " + fileName + "  fsize: " + available);

        oos.writeInt(available);
        oos.flush();

        long start = new java.util.Date().getTime();
      byte bites[] = new byte[1024];
      int READ_SIZE = 1024;

      while( available > 0){
        if(available <= 1024) {
          bites = new byte[available];
          READ_SIZE = available;
        } else {
          bites = new byte[1024];
        }
        buffInputStream.read(bites,0,READ_SIZE);
        available = buffInputStream.available();
        oos.writeInt(FILE_BYTES);
        oos.flush();
        oos.writeObject(bites);
        oos.flush();

      }//end while
        oos.writeInt(FILE_COMPLETE);
        oos.flush();
        
        System.out.println("[UPD] \tTotal Time: " + (new java.util.Date().getTime() - start) + " :: " + fileName);
    }finally{
      try{ buffInputStream.close();}catch(Exception ex){}
    }

  }
/*
   This is the method for slamming the whole file at once.
 The method above it will now send 1024 byte packets
 so the progress bar can track the file download
  private void pushFile(File fileName) throws Exception{
    FileInputStream fins = new FileInputStream(fileName);
    int fsize = (int)fileName.length();
System.out.println("[UPD] Filename: " + fileName + "  fsize: " + fsize);
    long start = new java.util.Date().getTime();
    byte[] bite = new byte[fsize];
    fins.read(bite);
System.out.println("[UPD] Read Time: " + (new java.util.Date().getTime() - start));
    start = new java.util.Date().getTime();
    oos.writeObject(bite);
    oos.flush();
System.out.println("[UPD] Network Write Time: " + (new java.util.Date().getTime() - start));

  }
*/
  private void pushDeadFile() throws Exception{
    byte[] bite = new byte[0];
    oos.writeObject(bite);
    oos.flush();
  }

  public void disconnect() {
    try {
      openSocket.close();
      online = false;
    } catch(Exception e) {
      System.out.println("[UPD] ErrorDisconnecting::IntegrityThread::disconnect:: ");
      e.printStackTrace();
    }
  }
}
