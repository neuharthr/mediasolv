package com.lmm.sched.gui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import messageit.message.Message;

import org.safehaus.uuid.UUIDGenerator;

import swingbeanformbuilder.core.SBFBConfiguration;
import swingbeanformbuilder.core.services.FormBuilder;
import swingbeanformbuilder.gui.ISBFBForm;

import com.lmm.client.FTPHandler;
import com.lmm.client.LMMClient;
import com.lmm.db.DBCommon;
import com.lmm.gui.OkCancelDialog;
import com.lmm.gui.UIMutex;
import com.lmm.gui.UIUtils;
import com.lmm.gui.files.ChooserAccessory;
import com.lmm.gui.files.ChooserResponse;
import com.lmm.msg.CmdMsg;
import com.lmm.msg.FileMsg;
import com.lmm.msg.GlobalConfigMsg;
import com.lmm.msg.MsgUtils;
import com.lmm.msg.PlayerRemoveMsg;
import com.lmm.msg.ReportMsg;
import com.lmm.sched.proc.LMMGlobalConfig;
import com.lmm.sched.proc.LMMScheduler;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.server.LMMServer;
import com.lmm.tools.FileFilters;
import com.lmm.tools.LMMLogger;
import com.lmm.tools.Time;

public class DashBoardActions {

	private LMMJFrame lmmJFrame; 
	
	protected DashBoardActions( LMMJFrame lmmJFrame ) {
		super();
		this.lmmJFrame = lmmJFrame;
	}
	
	public void settings() {
		
		final LMMGlobalConfig storedProps = DBCommon.globalConfig_Retrieve();

		final OkCancelDialog diag = new OkCancelDialog(
			lmmJFrame,
			"MediaSOLV Dashboard Settings", false);
		
		SBFBConfiguration.loadConfiguration( LMMJFrame.BEAN_GUI_CONFIG );
		final ISBFBForm form = FormBuilder.buildForm(
			LMMGlobalConfig.class, diag);
		
		// Load data in the SBFBForm...
		form.loadData(storedProps);
		
		diag.setDisplayPanel((JPanel) form);
		diag.pack();
		diag.setVisible(true);
		
		diag.addOkButtonListener(new ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e) {

				form.saveData(storedProps);
		
				// save the props and reload them
				DBCommon.globalConfig_Update(storedProps);
				LMMUtils.reloadLMMProperties();
				lmmJFrame.restartClientIfChanged();
			}
		});
	}

	public void synch() {
		
		int resp = JOptionPane.showConfirmDialog(
			lmmJFrame,
			"Are you sure you want to update the selected players with your current settings?"
			+ UIUtils.CRLF + "To view these settings click FILE -> SETTINGS..."
			+ UIUtils.CRLF + UIUtils.CRLF
			+ "NOTE: The properties in RED will not be changed on the player",
			"Confirm Update",
			JOptionPane.YES_NO_OPTION);

		if (resp != JOptionPane.OK_OPTION)
			return;
		
		// load all the writtable props from DB AKA the
		// GlobalConfig
		LMMGlobalConfig storedProps = DBCommon.globalConfig_Retrieve();

		if (storedProps != null) {
			int[] sel = lmmJFrame.getJTable().getSelectedRows();
			if (sel.length > 0) {
				lmmJFrame.sendMessage(
					new GlobalConfigMsg(storedProps),
					lmmJFrame.getSelectedRecipients());
			}
		
		} else {
			JOptionPane.showMessageDialog(
				lmmJFrame,
				"Unable to send the CONFIGURATION settings since no settings were found",
				"Config Not Sent",
				JOptionPane.WARNING_MESSAGE);
		}
	}

	public void synchAll() {
		
		int resp = JOptionPane.showConfirmDialog(
				lmmJFrame,
				"Are you sure you want to update ALL PLAYERS IN THE SYSTEM with your current settings?"
				+ UIUtils.CRLF
				+ "To view these settings click FILE -> SETTINGS..."
				+ UIUtils.CRLF + UIUtils.CRLF
				+ "NOTE: The properties in RED will not be changed on the player",
				"Confirm Update",
				JOptionPane.YES_NO_OPTION);

			if (resp != JOptionPane.OK_OPTION)
				return;

			// load all the writtable props from DB AKA the
			// GlobalConfig
			LMMGlobalConfig storedProps = DBCommon.globalConfig_Retrieve();
			if (storedProps != null) {
				GlobalConfigMsg gMsg = new GlobalConfigMsg(
						storedProps);
				gMsg.setBroadcastMsg(true); // global message is
											// called out here

				lmmJFrame.sendMessage(gMsg, null, LMMClient.TOPIC_GLOBAL);
			} else {
				JOptionPane.showMessageDialog(
					lmmJFrame,
					"Unable to send the CONFIGURATION settings since no settings were found",
					"Config Not Sent",
					JOptionPane.WARNING_MESSAGE);
			}
	}
	
	public void restartPlayer() {
		
		int[] sel = lmmJFrame.getJTable().getSelectedRows();
		if (sel.length > 0) {

			int resp = JOptionPane.showConfirmDialog(
				lmmJFrame,
				"Are you sure you want to Restart the video Player(s)?",
				"Confirm Player Restart",
				JOptionPane.YES_NO_OPTION);

			if (resp == JOptionPane.OK_OPTION) {
				lmmJFrame.getTextOutputPanel().addOutput(
						"Player restart(s) commencing...",
						Message.STATUS_FROM_SELF);

				lmmJFrame.sendMessage(
					new CmdMsg(CmdMsg.Commands.RESTART_PLAYER),
					lmmJFrame.getSelectedRecipients() );
			}
		}
	}

	public void removeItem() {
		
		int[] sel = lmmJFrame.getJTable().getSelectedRows();
		if (sel.length > 0) {
			int resp = JOptionPane.showConfirmDialog(
				lmmJFrame,
				"Are you sure you want to remove the "
				+ "selected row(s)? (The row(s) will reappear as updates are received)",
				"Confirm Removal",
				JOptionPane.YES_NO_OPTION);

			if (resp == JOptionPane.OK_OPTION) {
				for (int i = sel.length - 1; i >= 0; i--) {

					PlayerRemoveMsg pMsg = new PlayerRemoveMsg(
						lmmJFrame.getTableDataModel().getRowAt(sel[i]).getUuid());

					lmmJFrame.getTableDataModel().removeRow(sel[i]);
					lmmJFrame.sendMessage(pMsg,
							LMMServer.MASTER_SERVER_ID);
				}

			}
		}
	}

	public void cheatSheet() {
		
		String s = "";
		for (MsgUtils.Statuses status : MsgUtils.Statuses.values())
			s += UIUtils.CRLF + status.getDescription();

		JOptionPane.showMessageDialog(
			lmmJFrame,
			"  ------ Statuses ------"
			+ s + UIUtils.CRLF
			+ UIUtils.CRLF + "  ------ Settings ------" + UIUtils.CRLF
			+ "Refresh Interval:  " + (LMMScheduler.CLIENT_UPDATE / 1000)
			+ " seconds" + UIUtils.CRLF
			+ "Storage Directory:  " + new File(LMMUtils.getDataDir()).getAbsolutePath(),
			"Cheat Sheet",
			JOptionPane.INFORMATION_MESSAGE,
			new ImageIcon(UIUtils.LMM_LOGO_GIF));
	}

	public void about() {		
		JOptionPane.showMessageDialog(
				lmmJFrame,
				"Version: "
				+ LMMUtils.VERSION + UIUtils.CRLF
				+ "Copyright (C) 2005 - " + String.format("%1$tY", new Date())
				+ " Last Mile Marketing, all rights reserved"
				+ UIUtils.CRLF + UIUtils.CRLF
				+ "www.lastmilemarketing.com" + UIUtils.CRLF
				+ "877-753-4021" + UIUtils.CRLF
				+ "support@lastmilemarketing.com",
				"About MediaSOLV Dashboard",
				JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(UIUtils.LMM_LOGO_GIF));
	}

	public void ping() {

		int[] sel = lmmJFrame.getJTable().getSelectedRows();

		if (sel.length > 0) {
			lmmJFrame.getTextOutputPanel().addOutput(
					"Pinging player(s)...",
					Message.STATUS_FROM_SELF);

			lmmJFrame.sendMessage(
				new CmdMsg(CmdMsg.Commands.PING), lmmJFrame.getSelectedRecipients());
		}
	}

	public void getPOPReport(final Date start, final Date end, final String themeName) {

		if( !LMMUtils.isFTPEnabled() ) {
			JOptionPane.showMessageDialog(
					lmmJFrame,
					"Unable to retrieve the report(s) since FTP is not configured",
					"Unable to retrieve report(s)",
					JOptionPane.OK_OPTION);
			return;
		}

		final int[] selRows = lmmJFrame.getJTable().getSelectedRows();
		if( selRows.length > 0 )
			lmmJFrame.getTextOutputPanel().addOutput(
					"Attempting to retrieve the report(s) for the selected player(s)...",
					Message.STATUS_FROM_SELF);

		for( int i = 0; i < selRows.length; i++ ) {									
			ReportMsg rptMsg = new ReportMsg(start, end);
			final String recipient = lmmJFrame.getTableDataModel().getRowAt(selRows[i]).getName();
			rptMsg.setThemeName( themeName );
			rptMsg.setName( recipient );
			
			//were we should put the return report file when retrieved
			rptMsg.setFolderLocation( LMMUtils.getDataDir() );
		
			lmmJFrame.sendMessage(
					rptMsg, lmmJFrame.getSelectedRecipients());
		}

	}

	public void restartPC() {
		
		int[] sel = lmmJFrame.getJTable().getSelectedRows();
		if (sel.length > 0) {
			int resp = JOptionPane.showConfirmDialog(
				lmmJFrame,
				"Are you sure you want to reboot the remote computer(s)?",
				"Confirm Remote Restart",
				JOptionPane.YES_NO_OPTION);

			if (resp == JOptionPane.OK_OPTION)
				lmmJFrame.sendMessage(
					new CmdMsg(CmdMsg.Commands.RESTART_PC),
					lmmJFrame.getSelectedRecipients());
		}
	}

	public void editPlayList() {
		
		int sel = lmmJFrame.getJTable().getSelectedRow();
		if (sel >= 0) {
			FileMsg fMsg = new FileMsg();
			fMsg.addHeader(LMMUtils.PROP_FILE,
							LMMUtils.PLAYER_FILE); // special case
			// fMsg.setHeader( FileMsg.FILE_REF, "data.txt" );
			// //normal case
			fMsg.setRequest(true);
			
			lmmJFrame.getTextOutputPanel().addOutput(
					"Requesting new playlist successfully...",
					Message.STATUS_FROM_SELF);

			sendMessageDialog( lmmJFrame.getTableDataModel().getRowAt(sel).getName(), fMsg );
		}
	}
	
	public void sendPlayList() {
		
		SendPlayListDialog splDialog = new SendPlayListDialog( lmmJFrame );
		splDialog.setTitle("Send Play List");
		splDialog.show();

		if (splDialog.isOk()
				&& splDialog.getSelectedPlayList() != null) {
			FileMsg fMsg = new FileMsg();
			fMsg.setFile(new File(LMMUtils.getDataDir()
					+ File.separator
					+ splDialog.getSelectedPlayList()));

			fMsg.addHeader(LMMUtils.PROP_FILE,
					LMMUtils.PLAYER_FILE);

			LMMLogger.debug("Sending PlayList file: "
					+ fMsg.getFile().getAbsolutePath());
			sendMessageDialog( lmmJFrame.getSelectedRecipients(), fMsg );
		}
	}
	
	public void sendFiles() {
		
		ChooserResponse chooserResp = selectFiles();

		if( chooserResp.isError() ) {
			JOptionPane.showMessageDialog(
					lmmJFrame,
					chooserResp.getMsg(),
					"Unable to retrieve file(s)",
					JOptionPane.OK_OPTION);
			
			return;
		}
		
		int[] sel = lmmJFrame.getJTable().getSelectedRows();
		if (chooserResp.getFiles() != null && sel.length > 0) {

			for (int i = 0; i < chooserResp.getFiles().length; i++) {
				int resp = JOptionPane.OK_OPTION; // default this to OK
				
				if( chooserResp.getFiles()[i].length() >= FileMsg.MAX_FILE_SIZE ) {
					JOptionPane.showMessageDialog(
						lmmJFrame,
						"The selected file ("
						+ chooserResp.getFiles()[i].getName()
						+ ") is too big, maximum size limit is "
						+ (FileMsg.MAX_FILE_SIZE / 1000000) + " MB, try again",
						"File too big",
						JOptionPane.OK_OPTION);

					resp = JOptionPane.CANCEL_OPTION;
				}
				else if( !chooserResp.getFiles()[i].canWrite() ) {
					// do not allow READ-ONLY files to be
					// sent (we may want to delete them in
					// the future)
					JOptionPane.showMessageDialog(
						lmmJFrame,
						"The selected file (" + chooserResp.getFiles()[i].getName()
						+ ") is set to READ-ONLY, "
						+ "plese make the file writtable before sending.",
						"READ-ONLY File",
						JOptionPane.OK_OPTION);

					resp = JOptionPane.CANCEL_OPTION;
				}

				
				if (resp == JOptionPane.OK_OPTION) {
					FileMsg fMsg = new FileMsg();
					fMsg.setFile(chooserResp.getFiles()[i]);
					LMMLogger.debug("Uploading file: "
							+ fMsg.getFile().getAbsolutePath());


					lmmJFrame.getTextOutputPanel().addOutput(
							"Uploading file '" + fMsg.getFile().getName() + "' to the FTP site...",
							Message.STATUS_FROM_SELF);
					
					FTPHandler ftpHand = new FTPHandler(
						fMsg.getFile().getAbsolutePath(),
						lmmJFrame.client,
						lmmJFrame.getSelectedRecipients());

					ftpHand.executePutFile(lmmJFrame);

				}
			}

		} else
			JOptionPane.showMessageDialog(
				lmmJFrame,
				"Invalid file selected or one or more of the files selected could "
						+ "not be found."
						+ UIUtils.CRLF
						+ "Be sure all files selected are in the same folder.",
				"File error",
				JOptionPane.ERROR_MESSAGE);
	}

	
	private ChooserResponse selectFiles() {

		final ChooserResponse retResp = new ChooserResponse();
		
		if( !LMMUtils.isFTPEnabled() ) {
			retResp.setMsg("Unable to retrieve the selected file(s) since FTP is not configured");
			return retResp;
		}

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setCurrentDirectory(new File(LMMUtils.getVideoDir()));
		fc.setDialogTitle("Choose a file");
		fc.setPreferredSize( new Dimension(680, 400) );

		ChooserAccessory chooser = new ChooserAccessory(fc);
		fc.setAccessory(chooser);


		int retval = fc.showDialog(lmmJFrame, "Select");
		if (retval == JFileChooser.APPROVE_OPTION) {
			File[] files = new File[] { fc.getSelectedFile() };

			if (chooser.getThemeFiles() != null
					&& chooser.getThemeFiles().length > 0
					&& chooser.isUploadEntireTheme() ) {

				files = new File[chooser.getThemeFiles().length + 1];
				files[0] = fc.getSelectedFile();

				for (int i = 0; i < chooser.getThemeFiles().length; i++) {
					files[i + 1] = new File(fc.getCurrentDirectory()
							+ UIUtils.FILE_SEP + chooser.getThemeFiles()[i]);

					if (!files[i + 1].exists()) {
						String fName;
						try {
							fName = files[i + 1].getCanonicalPath();
						} catch( IOException ioe ) { fName = files[i + 1].getAbsolutePath(); }

						retResp.setMsg("The file " + fName + " could not be found. " + 
								"Please place the file into the appropriate folder.");
						return retResp; // bail out, one of the files can not be found!
					}
				}

			}

			retResp.setFiles( files );
		} else
			retResp.setFiles( new File[0] );
		
		return retResp;
	}

	
	/**
	 * Forces the GUI to wait for this message to be completely sent. This will typically be used
	 * by small sized file transfers that are not going through FTP.
	 */
	private synchronized void sendMessageDialog(final String receiver, final FileMsg msg) {

		// show the dialog (this blocks our current thread)
		final JDialogWait dialogWait = new JDialogWait( lmmJFrame );

		// send the message in a seperate thread
		new Thread(new Runnable() {
			public void run() {
				try {
					// just in case we are sending to multiple clients, lets
					// watch
					// the end message from the last client (only use this for
					// our lock)
					final String lastClientName = MsgUtils.getEndName(receiver);

					if (msg.isFileMultiPart()) {

						// init our lock in a PROGRESS status
						lmmJFrame.clientLock = new UIMutex(lastClientName,
								msg.getFile().getName(), Message.STATUS_OK);

						dialogWait.setInDeterminate(false);
						dialogWait.setProgress(0, msg.getTotalParts());
						dialogWait.setProgressValue(0);

						int indx = 0;
						FileMsg curr = msg.nextFileMsg();
						while (curr != null) {
							// sending file to the server
							lmmJFrame.client.sendMessage(curr, receiver);
							curr = msg.nextFileMsg();
							dialogWait.setProgressValue(++indx);
						}

						// wait for the recipient clients to respond with each
						// chunk
						// increase the wait when we are sending to more
						// recipients
						final int waitSecs = FileMsg.CHUNK_TIMEOUT_SECS
								* MsgUtils.getRecipientCount(receiver);

						dialogWait.setInDeterminate(true);
						dialogWait.setMsgProgress("Waiting for response...");

						dialogWait
								.setMsgLabel("Each file chunk may take up to "
										+ Time.getDescription(waitSecs * 1000)
										+ " to upload");

						short slpSecs = 0;
						while( !lmmJFrame.clientLock.isCleared(lastClientName, msg.getFile().getName())
								&& slpSecs <= waitSecs) {
							try {
								Thread.sleep(1000);
							} catch (Exception ex) {
							}

							slpSecs++;

							// clear the lock immediately if we received a full
							// chunk
							if (lmmJFrame.clientLock.getStatus() == Message.STATUS_MSG_PROGRESS) {
								slpSecs = 0; // reset our chunk wait time
								lmmJFrame.clientLock = new UIMutex(
										// reset our lock
										lastClientName,
										msg.getFile().getName(),
										Message.STATUS_OK);
							}

						}

						if (slpSecs >= waitSecs)
							lmmJFrame.getTextOutputPanel().addOutput(
								"Time-out exceeded while waiting for file chunk, try sending the file again",
								TextOutputPanel.STATUS_ERROR);

					} else {
						dialogWait.setMsgProgress("Making request...");
						lmmJFrame.client.sendMessage(msg, receiver);
					}

				} catch (IllegalStateException ill) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							LMMLogger.info("Unable to send file because it is too BIG");
							JOptionPane.showMessageDialog(
								lmmJFrame.getJTable(),
								"Maximum size limit of "
										+ (FileMsg.MAX_FILE_SIZE / 1000000)
										+ " MB has been exceeded, try again",
								"Unable to send file",
								JOptionPane.OK_OPTION);
						}
					});
				} catch (IOException ioe) {
					LMMLogger.error("Unable to send file", ioe);
				} finally {
					// close model dialog
					if (msg.isFileMultiPart())
						dialogWait.dispose();

					lmmJFrame.clientLock = new UIMutex(receiver, Message.STATUS_OK);
				}
			}
		}, "LMMFileUpload").start();

		// only show the wait dialog when the file is large
		if (msg.isFileMultiPart() && !dialogWait.isVisible())
			dialogWait.showDialog();
	}
	
	public void getFileListings() {
	
		final int[] selRows = lmmJFrame.getJTable().getSelectedRows();
		if( selRows.length > 0 ) {
			lmmJFrame.getTextOutputPanel().addOutput(
					"Attempting to retrieve file listings for the selected player(s)...",
					Message.STATUS_FROM_SELF);

			lmmJFrame.sendMessage(
				new CmdMsg(CmdMsg.Commands.GET_FILE_LISTING),
				lmmJFrame.getSelectedRecipients() );
		}

	}

	public void getProperties() {
		int sel = lmmJFrame.getJTable().getSelectedRow();
		if (sel >= 0) {
			lmmJFrame.getTextOutputPanel().addOutput(
				"Requesting player properties...",
				Message.STATUS_FROM_SELF);

			lmmJFrame.sendMessage(
				new CmdMsg(CmdMsg.Commands.GET_PROPERTIES),
				lmmJFrame.getTableDataModel().getRowAt(sel).getName());
		}
	}

	public void getLogFiles() {
		
		if( !LMMUtils.isFTPEnabled() ) {
			JOptionPane.showMessageDialog(
					lmmJFrame,
					"Unable to retrieve the selected file(s) since FTP is not configured",
					"Unable to retrieve file(s)",
					JOptionPane.OK_OPTION);
			return;
		}

		final int[] selRows = lmmJFrame.getJTable().getSelectedRows();
		if( selRows.length > 0 )
			lmmJFrame.getTextOutputPanel().addOutput(
					"Attempting to retrieve the selected player(s) log file...",
					Message.STATUS_FROM_SELF);

		for( int i = 0; i < selRows.length; i++ ) {									
			CmdMsg cmd = new CmdMsg(CmdMsg.Commands.GET_LOG_FILE);
			final String recipient = lmmJFrame.getTableDataModel().getRowAt(selRows[i]).getName();

			String fileId = UUIDGenerator.getInstance().generateTimeBasedUUID().toString();
			cmd.addHeader(CmdMsg.HDR_FILE_ID, fileId);

			//we only care about the name of the file now and not the path
			final String fName = recipient + ".log";
			final String fldrName = LMMUtils.getDataDir();
			
			//override the returned file name
			cmd.addHeader(
				CmdMsg.HDR_FILE_NAME, new File(fName).getName() );
			
			//set the folder this file will go into
			cmd.addHeader(
				CmdMsg.HDR_FOLDER_NAME, fldrName );

			lmmJFrame.sendMessage( cmd, recipient );
		}

	}
}