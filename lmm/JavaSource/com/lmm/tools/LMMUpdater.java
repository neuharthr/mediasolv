package com.lmm.tools;

import java.text.NumberFormat;
import java.util.Locale;

import messageit.message.Message;

import org.tanukisoftware.wrapper.WrapperManager;

import com.lmm.client.LMMClient;
import com.lmm.msg.ClientStateMsg;
import com.lmm.msg.MsgUtils;
import com.lmm.sched.proc.LMMUtils;

import integrity.client.IntegrityClient;
import integrity.interfaces.ClientListener;

public class LMMUpdater implements ClientListener {
    private int taskCount = 0;
    private int completedTask = 0;
    private int fileSize = 0;
    private int fileReadSize = 0;
    private double doubleFileSize = 0d;
    private double doubleFileReadSize = 0d;
    private NumberFormat formatter = NumberFormat.getPercentInstance(Locale.US);
    private LMMClient lmmClient;

    /**
     * Runs the actual update task.
     * @return
     * 		The number of updates done.
     */
    public int executeUpdates() {
		IntegrityClient ic = new IntegrityClient(this);
		ic.run();
		return getCompletedTask();    	
    }

    public void setLMMClient( LMMClient client ) {
    	lmmClient = client;
    }

    public void setTaskCount(int count) {
		taskCount = count;
    }
    public void setPrimaryStatus(String msg) {
        LMMLogger.info("[UPD] Primary Status: " + msg);
    }
    public void setLocalStatus(String msg) {
        LMMLogger.info("[UPD] Local Status: " + msg);
    }
    
    /**
     * Used to replay information about the application status changins. Sends
     * out a notification when this happens.
     */
    public void setAppStatus(String msg) {
        LMMLogger.info("[UPD] Application Status: " + msg);
        
        //send a message to say this application is in the Upgrading state
        if( lmmClient != null ) {
    		ClientStateMsg csMsg = new ClientStateMsg();
    		csMsg.setCurrentPlay( "" );
    		csMsg.setStatus( MsgUtils.Statuses.Upgrading );
    		csMsg.setCurrentPlaySlot( 0 );
    		csMsg.setUptime( System.currentTimeMillis() - LMMUtils.getStartTime().getTime() );
    		csMsg.setName( lmmClient.getName() );
    		csMsg.setUuid( LMMUtils.getComputerId() );
    		
    		lmmClient.sendMessage(
    				csMsg, null, LMMClient.TOPIC_SERVER_UPDATES, Message.PRIORITY_HIGH );		
        }

    }

    public void incrementFileCount() {
        completedTask++;
    }
    public void setFileStatus(String msg) {
        LMMLogger.info("[UPD] File Status: " + msg);
    }
    public void setServerChecksum(long cs) {
        //LMMLogger.info("[UPD] Server CheckSum: " + cs);
    }
    public void setLocalChecksum(long cs) {
        //LMMLogger.info("[UPD] Local CheckSum: " + cs);
    }

    public void completeApplication(boolean restart, String args) {
		if(restart && completedTask > 0 ) {
		  try { 
			if( "SERVICE".equals(args) && WrapperManager.isLaunchedAsService() ) {
				WrapperManager.restart();  //call the resart since this app is a Windows Service

				//just in case, lets wait until we are dead from the restart
				Thread.sleep( 20000L );
				
				//we should never get here since the service above restarts us
				LMMLogger.info( "AUTO-UPDATE warning: the service restart call was not successful, exiting..." );
				System.exit(0);  //bail out now
			}

		  } catch(Exception ex) {
			  LMMLogger.error( "An error occured when handling the AUTO-UPDATE completion routine", ex );
		  }

		}
    }

    public void setFileSize(int fsize) {
        //setFileProgessSize(fsize);
    }
    public void setFileReadLength(int read) {
        //setFileProgessRead(read);
    }

    public void setFileProgessSize(int fsize) {
        fileSize = fsize;
        LMMLogger.info("[UPD] FILE SIZE: " + fsize);
    }

    public void setFileProgessRead(int read) {
        fileReadSize += read;
        doubleFileReadSize = new Double(fileReadSize).doubleValue();
        String readPercent =
            formatter.format((doubleFileReadSize / doubleFileSize));
        if (readPercent.indexOf("0%") > -1)
			LMMLogger.info("[UPD] " +
                readPercent + " :: " + fileReadSize + "/" + fileSize);
    }
    /**
     * @return
     */
    public int getCompletedTask() {
        return completedTask;
    }

    /**
     * @return
     */
    public int getTaskCount() {
        return taskCount;
    }

}
