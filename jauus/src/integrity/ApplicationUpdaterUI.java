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

package integrity;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import integrity.util.*;

public class ApplicationUpdaterUI extends JWindow {
  private String monospace = "Monospaced";
  private String fileSeperator = System.getProperties().getProperty("file.separator");
  private String serverFiles[];
  private String localFiles[];
  private ArrayList fileList;
  private File localDir = null;
  private File serverDir = null;
  private boolean autoStart = false;
  private String commandArg = null;
  private Thread timer = new Thread();
  private JPanel contentPane;
  private BorderLayout borderLayout1 = new BorderLayout();
  private CheckSumUtility updater;
  private JPanel jPanel1 = new JPanel();
  private JProgressBar fileProgress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 70);
  private JLabel jLabel1 = new JLabel();
  private JLabel jLabel2 = new JLabel();
  private JLabel jlLocal = new JLabel();
  private JLabel jlServer = new JLabel();
  private JLabel jlResult = new JLabel();
  private JLabel jlNewTask = new JLabel();
  private JLabel jLabel3 = new JLabel();
  private TagReader tReader = null;
  private JLabel jlFile = new JLabel();
  private JProgressBar totalProgress;
  private JLabel jLabel4 = new JLabel();
  private JLabel jLabel5 = new JLabel();
  private JLabel jlFileCount = new JLabel();
  //Construct the frame
  public ApplicationUpdaterUI() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      tReader = new TagReader( JAUUSResources.getInputStream("config.properties") );
      intiFiles();
      totalProgress = new JProgressBar(JProgressBar.HORIZONTAL, 0, fileList.size());
      jbInit();
      updater = new CheckSumUtility();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  //Component initialization
  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(400, 278));
    jPanel1.setLayout(null);
    fileProgress.setBounds(new Rectangle(37, 197, 327, 16));
    jLabel1.setFont(new java.awt.Font(monospace, 0, 12));
    jLabel1.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel1.setText("Local File Checksum: ");
    jLabel1.setBounds(new Rectangle(29, 81, 186, 14));
    jLabel2.setFont(new java.awt.Font(monospace, 0, 12));
    jLabel2.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel2.setText("Server File Checksum: ");
    jLabel2.setBounds(new Rectangle(29, 102, 186, 14));
    jlLocal.setFont(new java.awt.Font(monospace, 0, 12));
    jlLocal.setBounds(new Rectangle(213, 81, 131, 14));
    jlServer.setFont(new java.awt.Font(monospace, 0, 12));
    jlServer.setBounds(new Rectangle(213, 102, 131, 14));
    jlResult.setFont(new java.awt.Font("Dialog", 0, 14));
    jlResult.setForeground(Color.blue);
    jlResult.setHorizontalAlignment(SwingConstants.CENTER);
    jlResult.setText("checking...");
    jlResult.setBounds(new Rectangle(70, 154, 261, 17));
    jlNewTask.setFont(new java.awt.Font(monospace, 0, 14));
    jlNewTask.setForeground(Color.red);
    jlNewTask.setHorizontalAlignment(SwingConstants.CENTER);
    jlNewTask.setBounds(new Rectangle(70, 129, 261, 17));
    jLabel3.setFont(new java.awt.Font(monospace, 0, 14));
    jLabel3.setForeground(Color.black);
    jLabel3.setText("Java File Update Utility");
    jLabel3.setBounds(new Rectangle(5, 7, 304, 17)); jlFile.setFont(new java.awt.Font(monospace, Font.ITALIC, 12)); jlFile.setForeground(Color.gray); jlFile.setHorizontalAlignment(SwingConstants.CENTER); jlFile.setText("Checking File: "); jlFile.setBounds(new Rectangle(8, 37, 385, 17)); totalProgress.setBounds(new Rectangle(37, 243, 327, 16)); jLabel4.setFont(new java.awt.Font(monospace, Font.PLAIN, 11)); jLabel4.setForeground(Color.gray); jLabel4.setText("File Progress"); jLabel4.setBounds(new Rectangle(37, 179, 186, 14)); jLabel5.setFont(new java.awt.Font(monospace, Font.PLAIN, 11));
        jLabel5.setForeground(Color.gray);
    jLabel5.setText("Total Progress"); jLabel5.setBounds(new Rectangle(37, 227, 119, 14)); jlFileCount.setFont(new java.awt.Font(monospace, Font.ITALIC, 12)); jlFileCount.setForeground(Color.gray); jlFileCount.setText("(0/0)"); jlFileCount.setBounds(new Rectangle(161, 227, 58, 14));
    contentPane.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jLabel3, null);
    jPanel1.add(jLabel2, null); jPanel1.add(jlResult, null); jPanel1.add(jlNewTask, null); jPanel1.add(jlServer, null); jPanel1.add(jLabel1, null); jPanel1.add(jlLocal, null); jPanel1.add(jlFile); jPanel1.add(fileProgress, null); jPanel1.add(totalProgress); jPanel1.add(jLabel4); jPanel1.add(jLabel5); jPanel1.add(jlFileCount); }

  private void intiFiles() throws Exception{
//System.out.println("[UPD] Server: " + tReader.getTagValue("structure","server"));
//System.out.println("[UPD] Local: " + tReader.getTagValue("structure","local"));
    serverDir = new File(tReader.getTagValue("structure","server"));
//System.out.println("[UPD] serverDir: " + serverDir.getAbsolutePath());
    localDir = new File(tReader.getTagValue("structure","local"));
    autoStart = new Boolean(tReader.getTagValue("structure","autostart")).booleanValue();
    commandArg = tReader.getTagValue("structure","main-class");
    serverFiles = serverDir.list();
    localFiles = localDir.list();

    Hashtable extensions = tReader.getTags("extentions");
    Hashtable exclusions = tReader.getTags("exclude");

    int index = 0;
    fileList = new ArrayList();
    for(int i = 0;i < serverFiles.length;i++){
      String fileName = serverFiles[i];
      index = fileName.lastIndexOf(".");

      if(index > 0){
        if(extensions.containsKey(fileName.substring(index + 1))
            && !exclusions.contains(fileName)){
          fileList.add(fileName);
        }//extension
      }//end index
    }//end for
  }

  public void runUpdate() throws Exception{

    Iterator iter = fileList.iterator();
    int fileCounter = 0;
    while(iter.hasNext()){
      String workingFile = (String)iter.next();
      jlFile.setText(workingFile);
      timer.sleep(500);
      fileCounter++;
      totalProgress.setValue(fileCounter);
      jlFileCount.setText("(" + fileCounter + "/" + fileList.size() + ")");
      validateIntegrity(new File(serverDir.getAbsolutePath() + fileSeperator + workingFile),
          new File(localDir.getAbsolutePath() + fileSeperator + workingFile));
    }
    if(autoStart){
      startApplication(commandArg);
    }else{
      System.exit(0);
    }
  }

  public void validateIntegrity(File serverCopy, File localCopy) {
    try {
      jlResult.setText("...");
      jlServer.setText("...");
      fileProgress.setValue(0);
      jlResult.setText("generating local checksum...");
      timer.sleep(500);
      fileProgress.setValue(10);
      timer.sleep(500);
      fileProgress.setValue(20);
      long localCheckSum = updater.getChecksumValue(localCopy);
      jlLocal.setText(new Long(localCheckSum).toString());
      timer.sleep(500);
      fileProgress.setValue(30);
      jlResult.setText("generating server checksum...");
      timer.sleep(500);
      fileProgress.setValue(40);
      timer.sleep(500);
      fileProgress.setValue(50);
      long serverCheckSum = updater.getChecksumValue(serverCopy);
      jlServer.setText(new Long(serverCheckSum).toString());
      timer.sleep(500);
      fileProgress.setValue(60);

      jlResult.setText("comparing checksum values...");
      timer.sleep(500);
      fileProgress.setValue(70);
      if(serverCheckSum == localCheckSum) {
        jlResult.setText("Local Copy is Up to Date");
      } else {
        jlNewTask.setText("Newer Version Found....Updating");
        copyFile(localCopy, serverCopy,false);
      }
    } catch(FileNotFoundException fnfe) {
      try {
        jlNewTask.setText("Local Copy Missing....Updating");
        try {timer.sleep(1000); } catch(Exception ex) {}
        copyFile(localCopy, serverCopy,true);
      } catch(Exception ex) {
        ex.printStackTrace();
        System.exit(-1);
      }
    } catch(Exception ex) {
      ex.printStackTrace();
      System.exit(-1);
    }

  }

  private void copyFile(File localCopy, File serverCopy, boolean missing) throws Exception {
    Thread timer = new Thread();
    timer.sleep(1000);
    fileProgress.setValue(0);
    timer.sleep(1000);
    if(missing)
      jlResult.setText("Preparing to copy...");
    else
      jlResult.setText("Deleting current copy...");
    try {
      localCopy.delete();
    } catch(Exception ex) {
//      System.out.println("[UPD] Local Copy Missing...attempting to copy the new version");
    }
    timer.sleep(1000);
    fileProgress.setValue(33);
    FileInputStream fileInputStream = null;
    byte bites[];
    try {
      fileInputStream = new FileInputStream(serverCopy);
      int availble = fileInputStream.available();
      bites = new byte[availble];
      fileInputStream.read(bites);
    } finally {
      try {
        fileInputStream.close(); } catch(Exception ex) {}
    }
    DataOutputStream outputStream = null;
    try {
      outputStream = new DataOutputStream(new FileOutputStream(localCopy));
      outputStream.write(bites);
      outputStream.flush();
    } finally {
      try {
        outputStream.close(); } catch(Exception ex) {}
    }
     jlResult.setText("Copying New Version...");
    timer.sleep(1000);
    fileProgress.setValue(66);

    timer.sleep(2000);
    jlNewTask.setText("Performing New Checksum");
    validateIntegrity(localCopy, serverCopy);
  }

  private void startApplication(String args){
    try{
      this.dispose();
      Runtime.getRuntime().exec(args);
    }catch(Exception ex){
    ex.printStackTrace();
    }
    System.exit(0);
  }

}
