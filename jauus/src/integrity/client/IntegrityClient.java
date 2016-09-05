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

import java.net.Socket;
import java.io.*;
import integrity.*;
import java.util.*;
import integrity.interfaces.ClientServerCommunication;
import integrity.interfaces.ClientListener;
import java.nio.*;
import integrity.util.*;

public class IntegrityClient extends CheckSumUtility implements ClientServerCommunication, Runnable{
  private String FILE_SEP = System.getProperties().getProperty("file.separator");
  private Thread timer = new Thread();
//  private final String workingDir = System.getProperties().getProperty("user.dir");
  private boolean autoStart = false;
  private String autoStartCommand = null;
  private String swHome = null;
  
  private Socket client;
  private ObjectOutputStream oos;
  private ObjectInputStream ois;
  private TagReader serverReader;
  private DataOutputStream outputStream;
  private ClientListener clientListener;

  public IntegrityClient() {
  	super();
  }

  public IntegrityClient(ClientListener clientList){
    clientListener = clientList;
    clientListener.setPrimaryStatus("Waiting on server response...");
  }

  public void run(){
    try {
    	loadProps();
		System.out.println("[UPD] Connecting to server: "
			+ System.getProperty("jauus_server") + ":" +
			+ Integer.parseInt(System.getProperty("jauus_port"))
			+ " for application: "
			+ System.getProperty("jauus_application"));
		
		client = new Socket(System.getProperty("jauus_server"),
			Integer.parseInt(System.getProperty("jauus_port")) );
		
		oos = new ObjectOutputStream(client.getOutputStream());
		ois = new ObjectInputStream(client.getInputStream());
		oos.writeInt(RUN_UPDATE);
		oos.flush();
		oos.writeObject(System.getProperty("jauus_application"));
		oos.flush();

		clientListener.setLocalStatus("Performing PAK CheckSum");
		ClientServerContainer csc = (ClientServerContainer)ois.readObject();
		serverReader = csc.getTReader();
		autoStart = new Boolean(serverReader.getTagValue("structure","autostart")).booleanValue();
		autoStartCommand = serverReader.getTagValue("structure","autostartargs");
		  
		swHome = serverReader.getTagValue("structure","softwarehome");
		  
		clientListener.setTaskCount(csc.size());
		LocalFileMasterCheckSum lmcs = valideGlobalChecksum(csc);
		if(lmcs.isValid()){
			clientListener.setPrimaryStatus("File structure is in good health (no update needed)");
		}
		else {
			clientListener.setAppStatus("Update is needed");
			clientListener.setPrimaryStatus("Dirty file structure found...performing low level check");
			timer.sleep(DownloadThrottle.SLEEPTIME);
		    resyncFiles(lmcs,csc);
		}

/*
      if(validateIntegrity(csc)){
        clientListener.setPrimaryStatus("Re-Synincing Files");
        clientListener.setLocalStatus("performing low level check");
        resyncFiles(csc);
      }else{
        clientListener.setPrimaryStatus("----- Files are in Sync ------");
        clientListener.setLocalStatus("");
      }
*/
      timer.sleep(DownloadThrottle.SLEEPTIME);
      clientListener.completeApplication(autoStart,autoStartCommand);
    } catch(Exception ex) {
      ex.printStackTrace();
      //System.exit(-1);
    }finally{
      try {
        oos.writeInt(CLOSE_CONNECTION);
        oos.flush();
      } catch(Exception excp) {
        try {ois.close(); } catch(Exception e) {}
        try {oos.close(); } catch(Exception e) {}
        try {client.close(); } catch(Exception e) {}
      }
    }
  }


  private void loadProps() throws Exception {
	//if the settings are in our environment already, use them
	if( System.getProperty("jauus_server") == null ) {
		TagReader tReader =  new TagReader( JAUUSResources.getInputStream("client.properties") );
		System.setProperty( "jauus_server", tReader.getTagValue("client","server") );
		System.setProperty( "jauus_application", tReader.getTagValue("client","application") );
		System.setProperty( "jauus_port", tReader.getTagValue("client","port") );		
	}

  }


  private void resyncFiles(LocalFileMasterCheckSum lfmc, ClientServerContainer csc) throws Exception{
    clientListener.setLocalStatus("performing low level check");
    long serverCheckSum = 0;
    long localCheckSum = 0;
//    Enumeration enm = csc.keys();
//    File file = null;
    FileCheckSum fcs = null;
//validate that we have all the directories
//now that we are about to copy files into them
    TagReader iReader = csc.getFileSet();
    Vector vec = new Vector(iReader.getNodes().keySet());
    Collections.sort(vec);
    Iterator iter = vec.iterator();
    String rootFile = null;
    File dirCheck = null;
    String key = null;
    boolean update = false;
    String appDir = ".";
    while(iter.hasNext()){
      key = (String) iter.next();
//System.out.println("[UPD] KEY: " + key);
      if(key.startsWith("DIR") && key.length() > 4) {
        //appDir = "." + key.substring(3).replaceAll("::"," ").replaceAll("\\:", "\\" + FILE_SEP);
		
		appDir = "." + FILE_SEP + swHome + FILE_SEP +
				key.substring(3).replaceAll("::"," ").replaceAll("\\:", "\\" + FILE_SEP);
		
        dirCheck = new File(appDir);
        if(!dirCheck.exists()){
          dirCheck.mkdir();
        }
      }

      Enumeration enm = iReader.getTags(key).elements();
      while(enm.hasMoreElements()) {
        update = false;
        String compKey = key + ":" + (String) enm.nextElement();
        //String localFileName = "." + compKey.substring(3).replaceAll("::"," ").replaceAll("\\:", "\\" + FILE_SEP);
        
		String localFileName = "." + FILE_SEP + swHome + FILE_SEP +
			compKey.substring(3).replaceAll("::"," ").replaceAll("\\:", "\\" + FILE_SEP);
        
        clientListener.setFileStatus(localFileName);
        clientListener.setLocalChecksum(-1);

          if(lfmc.containsKey(compKey)) {
            clientListener.setServerChecksum(((Long)csc.get(compKey)).longValue());
            clientListener.setLocalChecksum(lfmc.getFileCheckSum(compKey));
            if( ((Long)csc.get(compKey)).longValue() != lfmc.getFileCheckSum(compKey))
              update = true;
          } else {
            update = true;
          }

          if(update){
            try{
//System.out.println("[UPD] compKey: " + localFileName);
              File file = new File(localFileName);
              if(file.exists()){
                clientListener.setLocalStatus("replacing old file...");
                File rename = new File(localFileName + ".nv");
                file.renameTo(rename);
                File oldFile = new File(localFileName + ".nv");
                requestFile(compKey, file);
                oldFile.delete();
                rename.delete();
              }else{
                clientListener.setLocalStatus("downloading missing file...");
                requestFile(compKey, file);
              }
              timer.sleep(DownloadThrottle.SLEEPTIME);

            }catch(Exception ex){
              ex.printStackTrace();
              //System.exit(-1);
            }
          }else{
            clientListener.setLocalStatus("file is in good health");
          }//end if-update
          clientListener.incrementFileCount();
timer.sleep(DownloadThrottle.SLEEPTIME);
      }
    }//end directoryWhile

//    Enumeration enm = lfmc.keys();
//    while(enm.hasMoreElements()) {
//      String compKey = (String)enm.nextElement();
//      System.out.println("[UPD] {" + compKey + "}");
//    }

/*
    FileCheckSum fcs = null;
    Iterator itr = lfmc.iterator();

    while(itr.hasNext()){
      fcs = (FileCheckSum)itr.next();
      System.out.print("\t[" + fcs.getKey() + "] [" + fcs.getChecksum() + "]");
      System.out.println("[UPD]  @ [" + csc.get(fcs.getKey())  + "] [ XXXX ]");
//------------------------
      clientListener.setLocalChecksum(fcs.getChecksum());
+      clientListener.setLocalStatus("performing low level check");
      clientListener.setFileStatus("." + fcs.getFileLocation() + FILE_SEP + fcs.getFileName());
//      clientListener.setfi
//------------------------
      if(fcs.getChecksum() != ((Long)csc.get(fcs.getKey())).longValue() ){
          System.out.println("[UPD] FETCH FROM: ." + fcs.getFileLocation() + " NEW FILE: " + fcs.getFileName());
          clientListener.setLocalStatus("downloading new file...");
          timer.sleep(2000);
      }

    }//end while
*/
//System.out.println("[UPD] ---------------------------------------");
//    Enumeration zz = csc.keys();
//    while(zz.hasMoreElements()){
//      System.out.println("[UPD] \t" + (String)zz.nextElement());
//    }
//System.out.println("[UPD] ---------------------------------------");

//                 #################

//    while(enm.hasMoreElements()) {
//      String fileName = (String) enm.nextElement();
//System.out.println("[UPD] INTEGRITY CHECK: " + fileName);
/*
      clientListener.setFileStatus(fileName);
      serverCheckSum = ( (Long) csc.get(fileName)).longValue();
//System.out.println("[UPD] SCS: " + serverCheckSum + " for File: " + fileName);
      clientListener.setServerChecksum(serverCheckSum);
      try {
        file = new File(root + FILE_SEP + fileName);
        localCheckSum = getChecksumValue(file);
        clientListener.setLocalChecksum(localCheckSum);
        clientListener.setLocalStatus("performing low level check");
        if(serverCheckSum == localCheckSum) {
          clientListener.setLocalStatus("file is in good health");
        } else {
          try{
            clientListener.setLocalStatus("downloading new file...");
            timer.sleep(40);
            File rename = new File(root + FILE_SEP + fileName + ".nv");
            file.renameTo(rename);
            File oldFile = new File(root + FILE_SEP + fileName + ".nv");
            requestFile(fileName, file);
            clientListener.setLocalStatus("removing old file...");
            timer.sleep(40);
            oldFile.delete();
            rename.delete();
          }catch(Exception ex){
            ex.printStackTrace();
          }
        }
      } catch(FileNotFoundException fnfn) {
        requestFile(fileName,file);
      }
      timer.sleep(500);
      clientListener.incrementFileCount();
*/
//    }//end while

  }

/*
  private void resyncFiles(ClientServerContainer csc) throws Exception{
    long serverCheckSum = 0;
    long localCheckSum = 0;
    Enumeration enm = csc.keys();
    File file = null;
    String root = workingDir;
    while(enm.hasMoreElements()) {
      String fileName = (String) enm.nextElement();
      clientListener.setFileStatus(fileName);
      serverCheckSum = Integer.parseInt( (String) csc.get(fileName));
//System.out.println("[UPD] SCS: " + serverCheckSum + " for File: " + fileName);
      clientListener.setServerChecksum(serverCheckSum);
      try {
System.out.println("[UPD] ROOT: " + root);
        file = new File(root + FILE_SEP + fileName);
        localCheckSum = getChecksumValue(file);
        clientListener.setLocalChecksum(localCheckSum);
        clientListener.setLocalStatus("performing low level check");
        if(serverCheckSum == localCheckSum) {
          clientListener.setLocalStatus("file is in good health");
        } else {
          try{
            clientListener.setLocalStatus("downloading new file...");
            timer.sleep(40);
            File rename = new File(root + FILE_SEP + fileName + ".nv");
            file.renameTo(rename);
            File oldFile = new File(root + FILE_SEP + fileName + ".nv");
            requestFile(fileName, file);
            clientListener.setLocalStatus("removing old file...");
            timer.sleep(40);
            oldFile.delete();
            rename.delete();
          }catch(Exception ex){
            ex.printStackTrace();
          }
        }
      } catch(FileNotFoundException fnfn) {
        requestFile(fileName,file);
      }
      timer.sleep(500);
      clientListener.incrementFileCount();
    }//end while
  }
*/
/*
  private boolean validateIntegrity(ClientServerContainer ccs) throws Exception{
      serverReader = ccs.getTReader();
      long serverCheckSumValue = ccs.getServerCheckSum();
      clientListener.setServerChecksum(serverCheckSumValue);
      long localCheckSumValue = getChecksumValue(workingDir,ccs.keys());
      clientListener.setLocalChecksum(localCheckSumValue);
      timer.sleep(500);
      boolean result = true;
      if(serverCheckSumValue == localCheckSumValue){
        result = false;
      }
      return result;
  }
*/
  private LocalFileMasterCheckSum valideGlobalChecksum(ClientServerContainer ccs) throws Exception{
    clientListener.setServerChecksum(ccs.getServerCheckSum());
    clientListener.setPrimaryStatus("Building local checksums ...");
    LocalFileMasterCheckSum lfmc = buildLocalSettings( ccs.getFileSet(), swHome );
    clientListener.setLocalChecksum(lfmc.getMasterCheckSum());
    if(ccs.getServerCheckSum() == lfmc.getMasterCheckSum())
      lfmc.validCheckSum();
    return lfmc;
  }

  private void requestFile(String filename, File saveFile) throws Exception{
//    clientListener.animate();
//    clientListener.setLocalStatus("downloading new file...");
    oos.writeInt(REQUEST_FILE);
    oos.flush();
    oos.writeObject(filename);
    oos.flush();
    int available = ois.readInt();
    clientListener.setFileSize(available);
    byte[] bites = null;

    ByteBuffer array = ByteBuffer.allocate(available);
    int command = ois.readInt();
    while(command == FILE_BYTES){
      bites = (byte[])ois.readObject();
      clientListener.setFileReadLength(bites.length);
      array.put(bites);
      command = ois.readInt();
    }
    try {
      outputStream = new DataOutputStream(new FileOutputStream(saveFile));
      outputStream.write(array.array());
      outputStream.flush();
    } finally {
      try {outputStream.close(); } catch(Exception ex) {}
    }
//    clientListener.animate();
  }


}
