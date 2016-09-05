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

import java.awt.*;
import javax.swing.*;
import java.util.*;
import integrity.interfaces.ClientListener;
import java.text.*;

public class JAUUSUI extends JWindow implements UIInterface{
  private boolean isAnimating = false;
  private int maxTask = 100;
  private int completedTask = 0;
  private ClientListenerInstance clInstance;
//  private CheckSumUtility updater;
  private boolean useGUI = false;
//  private Thread timer = new Thread();
  private NumberFormat formatter = NumberFormat.getPercentInstance(Locale.US);
//-------------------------------------------------
  private String comicSans = "Comic Sans MS";
  private ImageIcon staticImage;
  private ImageIcon animation;
  private ImageIcon splashScreen;
  private JPanel contentPane;
  private BorderLayout borderLayout1;
  private JProgressBar totalProgress;
  private JProgressBar fileProgress;
  private JPanel jPanel1;
  private JLabel jLabel1;
  private JLabel jLabel2;
  private JLabel jlLocal;
  private JLabel jlServer;
  private JLabel jlResult;
  private JLabel jlNewTask;
  private JLabel jLabel3;
  private JLabel jlCheckFile;
  private JLabel jLabel5;
  private JLabel jlFileCount;
  private JLabel imageLabel;
  private JLabel jLabel4 = new JLabel();
//  private JProgressBar fileProgess = new JProgressBar();
  private int fileSize = 0;
  private int fileReadSize = 0;
  private double doubleFileSize = 0d;
  private double doubleFileReadSize = 0d;
  private JLabel filePercentage = new JLabel();


//-------------------------------------------------

  public JAUUSUI() {
    try {
      jbInit();
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  //Construct the frame
  public JAUUSUI(boolean _useGUI) {
    useGUI = _useGUI;
    clInstance = new ClientListenerInstance();
    try {
      if(_useGUI) {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        staticImage = new ImageIcon();
        animation = new ImageIcon();
        borderLayout1 = new BorderLayout();
        jPanel1 = new JPanel();
        totalProgress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 10);
        fileProgress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 10);
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jlLocal = new JLabel();
        jlServer = new JLabel();
        jlResult = new JLabel();
        jlNewTask = new JLabel();
        jLabel3 = new JLabel();
        jlCheckFile = new JLabel();
        jLabel5 = new JLabel();
        jlFileCount = new JLabel();
        imageLabel = new JLabel();
        splashScreen = new ImageIcon();
//        throbber = new JLabel();
        jbInit();
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  //Component initialization
  private void jbInit() throws Exception {
    contentPane = (JPanel)this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(400, 225));
    jPanel1.setLayout(null);
    jLabel1.setFont(new java.awt.Font(comicSans, 0, 12));
    jLabel1.setForeground(Color.blue);
    jLabel1.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel1.setText("Local File Checksum: ");
    jLabel1.setBounds(new Rectangle(9, 84, 186, 14));
    jLabel2.setFont(new java.awt.Font(comicSans, 0, 12));
    jLabel2.setForeground(Color.blue);
    jLabel2.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel2.setText("Server File Checksum: ");
    jLabel2.setBounds(new Rectangle(9, 63, 186, 14));
    jlLocal.setFont(new java.awt.Font(comicSans, 0, 12));
    jlLocal.setForeground(Color.blue);
    jlLocal.setBounds(new Rectangle(200, 84, 131, 14));
    jlServer.setFont(new java.awt.Font(comicSans, 0, 12));
    jlServer.setForeground(Color.blue);
    jlServer.setBounds(new Rectangle(200, 63, 131, 14));
    jlResult.setFont(new java.awt.Font("Dialog", 0, 14));
    jlResult.setForeground(Color.blue);
    jlResult.setHorizontalAlignment(SwingConstants.CENTER);
    jlResult.setText("checking...");
    jlResult.setBounds(new Rectangle(71, 129, 261, 17));
    jlNewTask.setFont(new java.awt.Font(comicSans, 0, 14));
    jlNewTask.setForeground(Color.red);
    jlNewTask.setHorizontalAlignment(SwingConstants.CENTER);
    jlNewTask.setBounds(new Rectangle(71, 111, 261, 17));
    jLabel3.setFont(new java.awt.Font(comicSans, Font.PLAIN, 14));
    jLabel3.setForeground(Color.black); jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel3.setText("Java Application Update Utility Software");
    jLabel3.setBounds(new Rectangle(6, 7, 388, 17));
    jlCheckFile.setFont(new java.awt.Font(comicSans, Font.ITALIC, 12));
    jlCheckFile.setForeground(Color.blue);
    jlCheckFile.setHorizontalAlignment(SwingConstants.CENTER);
    jlCheckFile.setText("Checking File: ");
    jlCheckFile.setBounds(new Rectangle(8, 33, 385, 17));
    totalProgress.setBounds(new Rectangle(37, 200, 327, 16));
    jLabel5.setFont(new java.awt.Font(comicSans, 0, 11));
    jLabel5.setForeground(Color.gray);
    jLabel5.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel5.setText("Total Progress");
    jLabel5.setBounds(new Rectangle( -1, 183, 94, 14));
    jlFileCount.setFont(new java.awt.Font(comicSans, Font.ITALIC, 12));
    jlFileCount.setForeground(Color.gray); jlFileCount.setText("(0/0)");
    jlFileCount.setBounds(new Rectangle(101, 183, 188, 14));
    imageLabel.setMaximumSize(new Dimension(390, 215));
    imageLabel.setMinimumSize(new Dimension(390, 215));
    jLabel4.setFont(new java.awt.Font(comicSans, 0, 11));
    jLabel4.setForeground(Color.gray);
    jLabel4.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel4.setText("File Progress");
    jLabel4.setBounds(new Rectangle( -1, 147, 94, 14));
    fileProgress.setBounds(new Rectangle(38, 164, 327, 16));
    filePercentage.setFont(new java.awt.Font(comicSans, 0, 11));
    filePercentage.setForeground(Color.gray);
    filePercentage.setText("0%");
    filePercentage.setBounds(new Rectangle(101, 147, 54, 14));
    contentPane.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jLabel3, null);

//    splashScreen = new ImageIcon(new ResourceLocator().getClass().getResource("jauus.gif"));
//    imageLabel.setIcon(splashScreen);
//    imageLabel.setBounds(new Rectangle(5, 5, 390, 215));
//    jPanel1.add(imageLabel, null);
    jPanel1.add(jlCheckFile);
    jPanel1.add(jlNewTask, null);
    jPanel1.add(jlResult, null);
    jPanel1.add(jLabel4, null);
    jPanel1.add(fileProgress, null);
    jPanel1.add(jLabel5);
    jPanel1.add(totalProgress);
    jPanel1.add(jlFileCount);
    jPanel1.add(filePercentage, null);
    jPanel1.add(jlLocal, null);
    jPanel1.add(jLabel2, null);
    jPanel1.add(jLabel1, null);
    jPanel1.add(jlServer, null);

    //animation = new ImageIcon(new ResourceLocator().getClass().getResource("throbber.gif"));
//    staticImage = new ImageIcon(new ResourceLocator().getClass().getResource("staticgear.gif"));
  }

  public void runUpdate() {
    Thread updateThread = new Thread(new IntegrityClient(clInstance));
    updateThread.start();
  }

//  public void doAnimation() {
//    if(isAnimating) {
//      throbber.setIcon(staticImage);
//      isAnimating = false;
//    } else {
//      throbber.setIcon(animation);
//      isAnimating = true;
//    }
//  }

  private void startApplication(boolean restart, String args) {
    this.dispose();
    if(restart) {
      try {
        Runtime.getRuntime().exec(args);
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    } //end if
    System.exit(0);
  }

  public void updateMaxTaskCount(int count) {
    maxTask = count;
    if(useGUI) {
      totalProgress.setMaximum(count);
      totalProgress.setValue(completedTask);
      jlFileCount.setText("( " + completedTask + " / " + maxTask + " )");
    } else {
      System.out.println("[UPD] Task Progress: " + "( " + completedTask + " / " + maxTask + " )");
    }
  }

  public void updatePrimaryStatus(String msg) {
    if(useGUI) {
      jlNewTask.setText(msg);
    } else {
      System.out.println(msg);
    }
  }

  public void updateLocalStatus(String msg) {
    if(useGUI) {
      jlResult.setText(msg);
    } else {
      System.out.println(msg);
    }
  }

  public void updateTaskCount() {
    completedTask++;
    if(useGUI) {
      totalProgress.setValue(completedTask);
      jlFileCount.setText("( " + completedTask + " / " + maxTask + " )");
    } else {
      System.out.println(completedTask + " of " + maxTask);
    }
  }

  public void updateFileStatus(String msg) {
    if(useGUI)
      jlCheckFile.setText("Checking File: " + msg);
    else
      System.out.println("[UPD] Checking File: " + msg);
  }

  public void updateServerChecksum(long cs) {
    if(useGUI)
      jlServer.setText("" + cs);
    else
      System.out.println("[UPD] Server CheckSum: " + cs);
  }

  public void updateLocalChecksum(long cs) {
    if(useGUI)
      jlLocal.setText("" + cs);
    else
      System.out.println("[UPD] Local CheckSum: " + cs);
  }

  public void setFileProgessSize(int fsize){
    fileSize = fsize;
    doubleFileSize = new Double(fsize).doubleValue();
    fileReadSize = 0;
    if(useGUI){
      fileProgress.setValue(0);
      fileProgress.setMaximum(fsize);
      filePercentage.setText("0%");
    }else
      System.out.println("[UPD] FILE SIZE: " + fsize);
  }

   public void setFileProgessRead(int read){
     fileReadSize += read;
     doubleFileReadSize = new Double(fileReadSize).doubleValue();
     if(useGUI) {
       fileProgress.setValue(fileReadSize);
       filePercentage.setText(formatter.format((doubleFileReadSize/doubleFileSize)));
     }else{
       String readPercent = formatter.format((doubleFileReadSize/doubleFileSize));
       if(readPercent.indexOf("0%") > -1)
         System.out.println(readPercent + " :: " + fileReadSize + "/" + fileSize);
     }
   }




//#################################################################################
  public class ClientListenerInstance implements ClientListener {
    public void setTaskCount(int count) {
      updateMaxTaskCount(count);
    }
    public void setPrimaryStatus(String msg) {
      if(useGUI)
        updatePrimaryStatus(msg);
      else
        System.out.println("[UPD] Primary Status: " + msg);
    }
    
    public void setAppStatus(String msg) {}

    public void setLocalStatus(String msg) {
      if(useGUI)
        updateLocalStatus(msg);
      else
        System.out.println("[UPD] Local Status: " + msg);
    }
    public void incrementFileCount() {
      updateTaskCount();
    }
    public void setFileStatus(String msg) {
      if(useGUI)
        updateFileStatus(msg);
      else
        System.out.println("[UPD] File Status: " + msg);
    }
    public void setServerChecksum(long cs) {
      if(useGUI)
        updateServerChecksum(cs);
      else
        System.out.println("[UPD] Server CheckSum: " + cs);
    }
    public void setLocalChecksum(long cs) {
      if(useGUI)
        updateLocalChecksum(cs);
      else
        System.out.println("[UPD] Local CheckSum: " + cs);
    }
    public void completeApplication(boolean restart, String args) {
      startApplication(restart, args);
    }
    public void setFileSize(int fsize){
      setFileProgessSize(fsize);
    }
    public void setFileReadLength(int read){
      setFileProgessRead(read);
    }
  }

}