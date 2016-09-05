package com.lmm.systray;

import java.util.Date;

import javax.swing.SwingUtilities;

import messageit.message.LocalMsg;
import messageit.message.Message;

import com.lmm.client.LMMClient;
import com.lmm.gui.UIUtils;
import com.lmm.msg.CmdMsg;
import com.lmm.msg.LMMMsgListener;
import com.lmm.msg.MonitorMsg;
import com.lmm.sched.jobs.AutoUpdateJob;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.server.HeavyClient;
import com.lmm.server.LMMServer;
import com.lmm.tools.LMMLogger;

public class SystrayHandler implements LMMMsgListener {
	public static final String MASTER_SYSTRY_ID = "[Systry]";

	private HeavyClient client = null;
	private MonitorListener monitorListener = null;

	public SystrayHandler(MonitorListener ml) {
		super();
		monitorListener = ml;
	}

	public void messageReceived( final Message m ) {
		
		try {
			LMMLogger.debug(" " + m.getSender() + " message received: " + m.getContent());
			
			if( m.getContent() instanceof MonitorMsg ) {
				final MonitorMsg mMsg = (MonitorMsg)m.getContent();

				SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						try {
							monitorListener.setMonitorMsg( mMsg );
						}
						catch( Exception ex ) {
							LMMLogger.error("Unable to set MonitorMsg", ex);
						}
					}
				});
			}
			
		}
		catch( Exception ex ) {
			LMMLogger.error("Problem with handling message from server", ex);
		}
	}
	
	public void exceptionRaised(Exception ex) {}

	public void messageLocalReceived(final LocalMsg lm) {
		//Conn State Change
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				try {
					monitorListener.setMonitorMsg(
						lm.isConnected() 
							? MonitorMsg.CONNECTED_MONMSG
							: MonitorMsg.DISCONNECTED_MONMSG );
					
		            //we just connected, lets ask the server to send us an update
					if( lm.isConnected() && client != null )
						client.sendMessage(
							new CmdMsg(CmdMsg.Commands.SERVER_FORCE_CHECK),
							LMMServer.MASTER_SERVER_ID );
					
					
					if( lm.isVersionMismatch() ) {
						//force an auto update
						if( new AutoUpdateJob().doUpdate() > 0 ) {
							LMMLogger.info("Updates were found and have been applied successfully, please restart the application");
							UIUtils.showUpdateUI();
						}			
					}
					
				}
				catch( Exception ex ) {
					LMMLogger.error("Unable to set MonitorMsg", ex);
				}
			}
		});
		
	}

	public void startMonitoring() {
		//make the client a unique name all the time
		client = new HeavyClient(
					MASTER_SYSTRY_ID + "_" + LMMUtils.getComputerName() +
					":" + new Date().getTime(),
					this,
					new String[]{ LMMClient.TOPIC_SERVER_STATUS } );

		client.connect();	
	}

}