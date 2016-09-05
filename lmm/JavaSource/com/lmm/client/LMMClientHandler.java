package com.lmm.client;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import org.safehaus.uuid.UUIDGenerator;

import com.lmm.db.DBCommon;
import com.lmm.msg.CmdMsg;
import com.lmm.msg.FileDeleteMsg;
import com.lmm.msg.FileListMsg;
import com.lmm.msg.FileMsg;
import com.lmm.msg.FileRenameMsg;
import com.lmm.msg.GlobalConfigMsg;
import com.lmm.msg.LMMMsgListener;
import com.lmm.msg.PropertiesMsg;
import com.lmm.msg.ReportMsg;
import com.lmm.reports.POPDailyMetricReport;
import com.lmm.sched.jobs.AutoUpdateJob;
import com.lmm.sched.proc.LMMScheduler;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.tools.FileFilters;
import com.lmm.tools.FormatUtils;
import com.lmm.tools.LMMLogger;
import com.lmm.tools.ProcessStarter;

import messageit.message.LocalMsg;
import messageit.message.Message;


/**
 * Used for remote clients. Handles messages from the server and does some sort
 * of action and will most of the time send a response message back to the server.
 * 
 */
public class LMMClientHandler implements LMMMsgListener {
	
	private LMMClient client = null;
	private LMMScheduler scheduler = null;
		
	
	public LMMClientHandler() {
		this( null, null );
	}

	public LMMClientHandler( LMMClient lmmClient, LMMScheduler lmmScheduler ) {
		super();
		client = lmmClient;
		scheduler = lmmScheduler;
	}

	public void messageReceived( Message m ) {
		
		try {
			LMMLogger.debug(" " + m.getSender() + " message received: " + m.getContent());
			
			if( m.getContent() instanceof FileMsg ) {

				FileMsg fMsg = (FileMsg)m.getContent();				
				if( fMsg.isRequest() )
					handleFileRequest( fMsg, m.getSender() );
				else
					handleFileReceive( fMsg, m.getSender() );
			}
			else if( m.getContent() instanceof CmdMsg ) {
				
				CmdMsg cmd = (CmdMsg)m.getContent();
				handleCmdMsg( cmd, m.getSender() );
			}
			else if( m.getContent() instanceof GlobalConfigMsg ) {

				GlobalConfigMsg cfgMsg = (GlobalConfigMsg)m.getContent();
				handleGlobalConfig( cfgMsg, m.getSender() );
			}
			else if( m.getContent() instanceof FileDeleteMsg ) {
				FileDeleteMsg fdMsg = (FileDeleteMsg)m.getContent();
				handleFileDeleteMsg( fdMsg, m.getSender() );
			}
			else if( m.getContent() instanceof FileRenameMsg ) {
				FileRenameMsg frMsg = (FileRenameMsg)m.getContent();
				handleFileRenameMsg( frMsg, m.getSender() );
			}

			else if( m.getContent() instanceof ReportMsg ) {
				ReportMsg rptMsg = (ReportMsg)m.getContent();
				handleReportMsg( rptMsg, m.getSender() );
			}

		}
		catch( Exception ex ) {
			LMMLogger.error("Unable to handle the message: " + m, ex);
			client.sendMessage(
					"Player error: " + ex.getMessage(), m.getSender(),
					Message.STATUS_ERROR, Message.PRIORITY_HIGH );
		}

	}
	
	private void handleCmdMsg( CmdMsg cmd, String destination ) {

		//do nothing if we do not have a valid scheduler & client
		if( !hasClient() || !hasScheduler() ) {
			LMMLogger.info("The cmd '" + cmd + "' was not performed since a Scheduler or Client was NULL");
			return;
		}

		if( cmd.getCmd() == CmdMsg.Commands.RESTART_PLAYER ) {			
			LMMUtils.reloadLMMProperties();
			scheduler.restartSchedulerUpdate();
			client.sendMessage(
				"Player restart complete", destination );
		}
		else if( cmd.getCmd() == CmdMsg.Commands.PING ) {			
			client.sendMessage( 
				"Ping successful", destination );
			
			scheduler.runJobNow( LMMScheduler.JOB_HEARTBEAT );
		}
		else if( cmd.getCmd() == CmdMsg.Commands.GET_FILE_LISTING ) {

			File[] vFiles = getFileList( FileFilters.FileFilter, LMMUtils.getVideoDir() );
			File[] dFiles = getFileList( FileFilters.PoPFileFilter, LMMUtils.getDataDir() );

			FileListMsg fListMsg = new FileListMsg( vFiles.length + dFiles.length );
			int indx = 0;
			for( indx = 0; indx < vFiles.length; indx++ )
				fListMsg.updateFileAt( vFiles[indx], indx, FileListMsg.Grouping.Default );

			for( int j = 0; j < dFiles.length; j++ )
				fListMsg.updateFileAt( dFiles[j], indx++, FileListMsg.Grouping.ProofOfPerformance );


			client.sendMessage( fListMsg, destination );
			client.sendMessage(
				"File listing completed", destination );
		}
		else if( cmd.getCmd() == CmdMsg.Commands.GET_PROPERTIES ) {			
			PropertiesMsg pMsg = new PropertiesMsg();
			pMsg.load();
			client.sendMessage( pMsg, destination );
			client.sendMessage(
				"Properties request successful", destination );
		}		
		else if( cmd.getCmd() == CmdMsg.Commands.RESTART_PC ) {			
			
			//shutdown.exe -r -f -t 5
			new ProcessStarter().startProcess(
				ProcessStarter.EXEC_SHUTDOWN, ProcessStarter.EXEC_SHUTDOWN_PARAMS, null );
			
			client.sendMessage(
				"Reboot command commencing", destination );
		}		
		else if( cmd.getCmd() == CmdMsg.Commands.GET_SETTINGS ) {

			GlobalConfigMsg gMsg = new GlobalConfigMsg( DBCommon.globalConfig_Retrieve() );

			client.sendMessage( gMsg, destination );
			client.sendMessage(
				"Settings request successful", destination );
		}		
		else if( cmd.getCmd() == CmdMsg.Commands.FTP_GET_FILE ) {

			FTPHandler ftpHand = new FTPHandler(
					cmd.getHeader(CmdMsg.HDR_FILE_NAME).toString(),
					client,
					destination);

			ftpHand.executeGetFile(
				cmd,
				LMMUtils.getVideoDir() );
		}		
		else if( cmd.getCmd() == CmdMsg.Commands.FTP_PUT_FILE ) {

			String fldrName = (cmd.getHeader(CmdMsg.HDR_FOLDER_NAME) == null
				? LMMUtils.getVideoDir() : cmd.getHeader(CmdMsg.HDR_FOLDER_NAME).toString());

			// lets add our own path to it this request or the one that was given
			FTPHandler ftpHand = new FTPHandler(
					fldrName + cmd.getHeader(CmdMsg.HDR_FILE_NAME).toString(),
					client,
					destination);

			//during FTP operation, a reply is sent to the sender
			ftpHand.executePutFile(this);
		}
		else if( cmd.getCmd() == CmdMsg.Commands.GET_LOG_FILE ) {

			// put the latest log file
			FTPHandler ftpHand = new FTPHandler(
					LMMUtils.getLogsDir() + findCurrentLogFile(),
					client,
					destination);

			//during FTP operation, a reply is sent to the sender
			ftpHand.executePutFile(this, cmd);
		}

	}

	private String findCurrentLogFile() {
		//insert the file list here for the current theme files
		File[] files = getFileList( FileFilters.LogFileFilter, LMMUtils.getLogsDir() );
		String fName = null;
		long greatestTime = 0L;

		//get the latest log file
		for( int i = 0; i < files.length; i++ ) {
			if( files[i].lastModified() > greatestTime) {
				greatestTime = files[i].lastModified();
				fName = files[i].getName();
			}

		}
		
		return fName;
	}

	public void messageLocalReceived(LocalMsg lm) {
		
		if( lm.isVersionMismatch() ) {
			//force an auto update
			if( new AutoUpdateJob().doUpdate(client) > 0 ) {
				LMMLogger.info("Updates were found and have been applied successfully, please restart the application");
			}
		}

	}


	private void handleGlobalConfig( GlobalConfigMsg cfgMsg, String destination ) {
		
		// --- IMPORTANT, we have our own ComputerID; lets keep it that way!!! ------
		cfgMsg.getGlobalConfig().setComputerId( LMMUtils.getComputerId() );

		if( cfgMsg.isBroadcastMsg() ) {
			cfgMsg.getGlobalConfig().setComputerName( LMMUtils.getComputerName() );
	
			client.sendMessage(
				"Applying new settings...", destination,
				Message.STATUS_FROM_MASTER, Message.PRIORITY_NORMAL );

			DBCommon.globalConfig_Update( cfgMsg.getGlobalConfig() );		
			LMMUtils.reloadLMMProperties();
			scheduler.restartSchedulerUpdate();

			client.sendMessage(
				"New settings applied successfully", destination,
				Message.STATUS_FROM_MASTER, Message.PRIORITY_NORMAL );
		}
		else {
			client.sendMessage(
				"Applying new settings...",
				destination );

			DBCommon.globalConfig_Update( cfgMsg.getGlobalConfig() );		
			LMMUtils.reloadLMMProperties();
			scheduler.restartSchedulerUpdate();

			client.sendMessage(
				"New settings applied successfully",
				destination );
		}

	}

	/**
	 * Only deletes files in the 'video' directory.
	 * @param fdMsg
	 */
	private void handleFileDeleteMsg( final FileDeleteMsg fdMsg, String destination ) {

		if( fdMsg == null ) return;

		//set our file to be the name file in our local directory
		int cnt = 0;
		for( String fileName : fdMsg.getFileNames() ) {
			try {
				File locFile = new File( fileName );

				//check for & delete the file
				if( locFile.exists() && locFile.isFile() 
						&& locFile.canWrite() && locFile.delete() ) {
					cnt++;
				}
			}
			catch( Exception ex ) {
				LMMLogger.error("Unable to DELETE file '" + fileName + "'", ex );
			}
		}

		client.sendMessage(
			"File deletion of " + cnt + " file(s) was successful", destination );
	}

	/**
	 * Only renames files in the 'video' directory.
	 * @param fdMsg
	 */
	private void handleFileRenameMsg( final FileRenameMsg frMsg, String destination ) {

		if( frMsg == null ) return;

		String retMsg;
		try {
			File locFile = new File( frMsg.getOldFileName() );
		
			//check for the correct file access
			if( locFile.exists() && locFile.isFile() && locFile.canWrite() ) {
				locFile.renameTo( new File(frMsg.getNewFileName()) );
			}
			
			retMsg = "File rename of " + frMsg.getOldFileName() + " was successful";
		}
		catch( Exception ex ) {
			retMsg = "Unable to RENAME file '" + frMsg.getOldFileName() + "'";
			LMMLogger.error(retMsg, ex );
		}

		client.sendMessage( retMsg, destination );
	}
	
	/**
	 * Handles any & all report request
	 */
	private void handleReportMsg( final ReportMsg rptMsg, String destination ) {

		if( rptMsg == null ) return;
		
		//generate the report file
		POPDailyMetricReport newReport = new POPDailyMetricReport(
				rptMsg.getStartTime(), rptMsg.getEndTime() );

		final Date now = new Date();
		//report file name format:   [player name]-POPReport_[optional theme name]MMDDYYYYHHmmss.pdf
		//  Plaza-05052007123211.pdf
		//  Plaza-Suzukie720x480_05052007123211.pdf
		final String rptFileName = rptMsg.getName() + "-"
			+ (rptMsg.getThemeName() == null ? "" : rptMsg.getThemeName() + "_")
			+ FormatUtils.onlyDate(now) + FormatUtils.onlyTime(now) + ".pdf";

		newReport.savePDF( rptFileName );


		//create the command message that the requestor will use to retrieve the newly
		// uploaded report files
		CmdMsg cmd = new CmdMsg(CmdMsg.Commands.FTP_GET_FILE);
		cmd.addHeader(CmdMsg.HDR_FILE_ID, rptFileName);
		cmd.addHeader( CmdMsg.HDR_FOLDER_NAME, rptMsg.getFolderLocation() );

		//override the returned file name
		cmd.addHeader(
			CmdMsg.HDR_FILE_NAME,
			rptFileName );


		// put the report file onto the ftp server
		FTPHandler ftpHand = new FTPHandler(
				rptFileName,
				client,
				destination);
		
		//during FTP operation, a reply is sent to the sender
		ftpHand.executePutFile(this, cmd);

	}

	private void handleFileRequest( FileMsg fMsg, String destination ) {
		
		FileMsg respFileMsg = new FileMsg();
		//set our file to be the name file in our local directory
		String configFile = (String)fMsg.getHeader(LMMUtils.PROP_FILE);
		if(  configFile != null ) {
			respFileMsg.setFile( new File(LMMUtils.getToolsDir() + configFile) );
			
			//insert the file list here for the current theme files
			File[] files = getFileList( FileFilters.XMLFileFilter, LMMUtils.getVideoDir() );
			String[] fileNames = new String[ files.length ];
			for( int i = 0; i < files.length; i++ )
				fileNames[i] = files[i].getName();
			
			respFileMsg.addHeader( FileMsg.HEADER_THEME_LIST, fileNames );
		}
		else
			respFileMsg.setFile( new File(
					LMMUtils.getVideoDir() + File.separator + fMsg.getHeader(FileMsg.HEADER_FILE_REF) ) );
		
		//only sends at most 1 CHUNK of data, returns a content[] of size 0 if too big
		if( hasClient() ) {
			client.sendMessage( respFileMsg, destination );
		}
		else
			LMMLogger.info("Unable to send response to request for a file from sender '" + destination );
	}

	private void handleFileReceive( FileMsg fMsg, String destination ) {
		
		File file = null;
		RandomAccessFile out = null;
		try {
			String configFile = (String)fMsg.getHeader(LMMUtils.PROP_FILE);
			if( configFile != null )
				file = new File( LMMUtils.getToolsDir() + configFile ); //config files
			else
				file = new File( LMMUtils.getVideoDir() + File.separator + fMsg.getFile().getName() );			

			
			Integer chunk = 
				(fMsg.getHeader(FileMsg.HEADER_CHUNK_KEY) == null ? null
					: (Integer)fMsg.getHeader(FileMsg.HEADER_CHUNK_KEY));
					
			Integer totalChunks =
				(fMsg.getHeader(FileMsg.HEADER_CHUNK_TOTAL_KEY) == null ? null
					: (Integer)fMsg.getHeader(FileMsg.HEADER_CHUNK_TOTAL_KEY));

			//delete any existing file if this is the first message
			if( chunk == null || chunk.intValue() == 1 )
				file.delete();

			if( !file.exists() )
				file.createNewFile();
			
			out = new RandomAccessFile( file, "rw" );

			if( file.length() > 0 )
				out.seek( file.length() );

			out.write( fMsg.getContent() );


			if( fMsg.getHeader(FileMsg.HEADER_CHUNK_KEY) != null )
				LMMLogger.info(" got file chunk (" + fMsg.getHeader(FileMsg.HEADER_CHUNK_KEY) + " of "
					+ totalChunks + ") for file: " + fMsg.getFile().getName());

			//either 1 chunk or many, the last chunk will have the least amount of bytes
			if( hasClient() && 
				(chunk == null || chunk.equals(totalChunks)) ) {
				client.sendMessage( 
					"All file parts received for: " + fMsg.getFile().getName(),
					destination, Message.STATUS_MSG_COMPLETE, Message.PRIORITY_HIGH );
			}
			else if( hasClient() && chunk != null && totalChunks != null ) {
				client.sendMessage("Received file chunk (" + fMsg.getHeader(FileMsg.HEADER_CHUNK_KEY) + " of "
					+ totalChunks + ") for file: " + fMsg.getFile().getName(),
					destination, Message.STATUS_MSG_PROGRESS, Message.PRIORITY_HIGH );
			}


			//if this was a config file, reload our config properties
			if( configFile != null ) {
				LMMUtils.reloadLMMProperties();
				scheduler.restartSchedulerUpdate();
			}

		}
		catch( IOException ioe ) {
			LMMLogger.error( "Unable to write new file, deleting file", ioe );
			if( file != null && file.exists() )
				file.delete();
		}
		finally {
			try {
				if( out != null ) out.close();
			}
			catch( IOException ioe ) {
			}
		}

		
	}
	
	private File[] getFileList( FileFilter filter, String dir ) {		
		File fileDir = new File( dir );
		return fileDir.listFiles( filter );
	}


	private boolean hasClient() {
		return client != null;
	}

	private boolean hasScheduler() {
		return scheduler != null;
	}

}