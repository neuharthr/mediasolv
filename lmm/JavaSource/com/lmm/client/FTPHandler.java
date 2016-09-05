package com.lmm.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.safehaus.uuid.UUIDGenerator;

import messageit.message.Message;

import com.lmm.msg.CmdMsg;
import com.lmm.msg.LMMMsgListener;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.tools.ChecksumCRC32;
import com.lmm.tools.LMMLogger;

/**
 * Multi threaded operational class that will execute ftp functions in
 * the background. Any results, error or success, are sent back to the original sender.
 */
public class FTPHandler {

	private String fileName;	//name of the file after it is downloaded
	private boolean isNameUnique = true;
	private ClientSender client;
	private String replyDest;	//who wants to know results of this
	

	private final FTPClient ftpClient = new FTPClient();
	
	public FTPHandler( String fileName ) {
		this( fileName, null, null, false );	//maintain the name given on the server
	}

	public FTPHandler( String fileName, ClientSender client, String replyDest ) {
		this( fileName, client, replyDest, true );
	}

	private FTPHandler( String fileName, ClientSender client, String replyDest, boolean isNameUnique ) {
		super();
		this.replyDest = replyDest;

		if( fileName == null )
			throw new IllegalArgumentException("A null FileName is not allowed");

		this.fileName = fileName;
		this.client = client;
		this.isNameUnique = isNameUnique;
	}

	public void executeGetFile( final CmdMsg cmd, final String fileFolder ) {
		new Thread( new Runnable() {
			public void run() {
				runGetFile( null, cmd, fileFolder );
			}
		}, "FTPWorker").start();
	}

	public void executeGetFile( final LMMMsgListener callBack,
			final CmdMsg cmd, final String fileFolder ) {
		new Thread( new Runnable() {
			public void run() {
				runGetFile(
					callBack,
					cmd,
					fileFolder );
			}
		}, "FTPWorker").start();
	}

	public void executePutFile( final LMMMsgListener callBack, final CmdMsg cmd ) {
		new Thread( new Runnable() {
			public void run() {
				runPutFile( callBack, cmd );
			}
		}, "FTPWorker").start();
	}

	public void executePutFile( final LMMMsgListener callBack ) {
		executePutFile( callBack, null );
	}

	public void executePutFile() {
		executePutFile( null );
	}
	
	private void checkFTPReply( String errorMsg ) throws IOException {
        if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
        	LMMLogger.info( errorMsg );
            throw new IOException(errorMsg + ", " + ftpClient.getReplyString());
        }	
	}

	private void doFTPLogin() throws IOException, UnknownHostException {
	   
		// connect to the server
		ftpClient.connect( LMMUtils.getFtpUrl() );
		checkFTPReply( "FTP server refused connection" );

	    ftpClient.login(
	    		LMMUtils.getFtpUsername(),
	    		LMMUtils.getFtpPword() );
		checkFTPReply( "Unable to login to the FTP server" );

		ftpClient.enterLocalPassiveMode();
	    checkFTPReply( "Unable to enter FTP PASSIVE mode" );

		    
	    ftpClient.changeWorkingDirectory( LMMUtils.getFtpUploadDir() );
		checkFTPReply( "Unable to CWD on the FTP server" );
	    
	    ftpClient.setFileType( FTP.BINARY_FILE_TYPE );
		checkFTPReply( "Unable to set the transfer type to BINARY on the FTP server" );

	}
	
	private void doFTPClose() {

		// disconnect from the server
		try {
			ftpClient.disconnect();

		} catch( Exception ex ) {
			LMMLogger.error("Unable to close a FTP connection", ex);
		}
	}

	/**
	 * Attempts to retrieve a file via means of a FTP request. Executes a CRC 32
	 * checksum to ensure file integrity.
	 * 
	 * @param callBack
	 * 			Typically a UI component that needs to be notified on
	 * 			completion of this task. This can be NULL.
	 * @param cmd
	 * 			The command message that is used for a response to this
	 * 			FTP operation.
	 * @param fileFlder
	 * 			The local file locatin the file is pulled from.
	 */
	private void runGetFile( final LMMMsgListener callBack,
			final CmdMsg cmd, final String fileFolder ) {
		
		if( !LMMUtils.isFTPEnabled() || cmd == null )
			return;

		final String fileId = cmd.getHeader(CmdMsg.HDR_FILE_ID).toString();
		final String localFileName = fileFolder + fileName;		
		String retMsg = "";
		byte msgStatus = Message.STATUS_OK;

		BufferedOutputStream bo = null;
		try {
			doFTPLogin();

			bo = new BufferedOutputStream( new FileOutputStream(localFileName) );
			ftpClient.retrieveFile( fileId, bo );
		
			//only reply back with the file name & not the absolute path if we do not
			// have a local callBack (i.e. UI app) defined, else we reply with the
			// full path name
			retMsg = "Download of '" +
				(callBack == null ? fileName : localFileName) + "' was successful";

		}
		catch( Exception ex ) {
			retMsg = "FTP download of '" + fileName + "' was NOT successful";
			msgStatus = Message.STATUS_ERROR;
			LMMLogger.error(retMsg, ex);
		}
		finally {
			try{ 
				if( bo != null) bo.flush();
			} catch( Exception ex ) {}

			try{
				if( bo != null ) bo.close();
			} catch( Exception ex ) {}

			//delete the file after we received it
			try{ ftpClient.deleteFile(fileId);  } catch( Exception ex ) {
				LMMLogger.error("Unable to delete FTP temp file on server", ex);
			}
			
			doFTPClose();
		}

		
		//check CRC value of our new file and the file msg that was sent to us
		final String checkSum = ChecksumCRC32.doChecksum(localFileName);
		if( !checkSum.equals(cmd.getHeader(CmdMsg.HDR_FILE_CHECKSUM)) ) {
			retMsg = "The file '" + fileName + "' was CORRUPTED during transfer, please retry the transfer";
			msgStatus = Message.STATUS_ERROR;
			LMMLogger.debug("Checksum was CORRUPT, expecting " + cmd.getHeader(CmdMsg.HDR_FILE_CHECKSUM) +
					" but received " + checkSum);
			
			//delete the corrupted local file
			new File(localFileName).delete();
		}

		if( client != null && replyDest != null )
			client.sendMessage( retMsg, replyDest, msgStatus, Message.PRIORITY_NORMAL );

		//log the msg to the console
		LMMLogger.info( retMsg );

		processCallBack(callBack, msgStatus, retMsg);		
	}
	
	private void runPutFile( final LMMMsgListener callBack, final CmdMsg recvCmd ) {

		if( !LMMUtils.isFTPEnabled() )
			return;

		String retMsg = "";
		byte msgStatus = Message.STATUS_OK;
		String fileId = null;
		BufferedInputStream bi = null;
		CmdMsg respCmd = null;

		try {
			doFTPLogin();

			fileId = (isNameUnique()
				? UUIDGenerator.getInstance().generateTimeBasedUUID().toString()
				: fileName);

			bi = new BufferedInputStream( new FileInputStream(fileName) );
			ftpClient.storeFile( fileId, bi );

			//may need to do this if when storing a file the FTP server sends a RESTart command
			//OutputStream fo = ftpClient.storeFileStream( fileId );
			//Util.copyStream( fi, fo );

			
			respCmd = createResponseCmd( fileId, recvCmd );
			
			retMsg = "FTP upload of '" + new File(fileName).getName() + "' was successful, attempting notification of new file...";
		}
		catch( Exception ex ) {
			respCmd = null;
			retMsg = "FTP upload of '" + new File(fileName).getName() + "' was NOT successful";
			msgStatus = Message.STATUS_ERROR;
			LMMLogger.error(retMsg, ex);
			
			//delete the file (or part of file) if it exists on the FTP server
			try{ ftpClient.deleteFile(fileId);  } catch( Exception dex ) {
				LMMLogger.error("Unable to delete corrupt FTP temp file on server", dex);
			}

		}
		finally {			
			try{
				if( bi != null ) bi.close();
			} catch( Exception ex ) {}

			doFTPClose();
		}

		if( client != null && replyDest != null ) {
			//on an error or success, send a text response
			client.sendMessage( retMsg, replyDest, msgStatus, Message.PRIORITY_NORMAL );

			//send the follow up respnose cmd if we have one
			if( respCmd != null )
				client.sendMessage( respCmd, replyDest );
		}
		

		//log the msg to the console
		LMMLogger.info( retMsg );


		processCallBack(callBack, msgStatus, retMsg);
	}
	
	/**
	 * Creates a response cmd that will tell the caller that it will need to do an FTP get
	 * since the original FTP upload is completed.
	 * 
	 * @param fileId
	 * @param recvCmd
	 * @return The cmdMsg
	 */
	private CmdMsg createResponseCmd( final String fileId, final CmdMsg recvCmd ) {
		
		final CmdMsg respCmd = new CmdMsg(CmdMsg.Commands.FTP_GET_FILE);
		respCmd.addHeader(CmdMsg.HDR_FILE_ID, fileId);
		
		//we only care about the name of the file now and not the path
		respCmd.addHeader(CmdMsg.HDR_FILE_CHECKSUM, ChecksumCRC32.doChecksum(fileName));

		//check to see if the givem CMD object overrides any file properties
		if( recvCmd != null && recvCmd.getHeader(CmdMsg.HDR_FILE_NAME) != null )
			respCmd.addHeader(CmdMsg.HDR_FILE_NAME, recvCmd.getHeader(CmdMsg.HDR_FILE_NAME));
		else
			respCmd.addHeader(CmdMsg.HDR_FILE_NAME, new File(fileName).getName());

		if( recvCmd != null && recvCmd.getHeader(CmdMsg.HDR_FOLDER_NAME) != null )
			respCmd.addHeader(CmdMsg.HDR_FOLDER_NAME, recvCmd.getHeader(CmdMsg.HDR_FOLDER_NAME));
		
		return respCmd;
	}
	
	private void processCallBack( final LMMMsgListener callBack, final byte msgStatus,
				final String retMsg ) {

		if( callBack != null ) {
			try {
				Message msg = new Message();
				//a message used for internal messaging only
				msg.setStatus(
					(msgStatus != Message.STATUS_ERROR ? Message.STATUS_FROM_SELF : Message.STATUS_ERROR) );
				
				msg.setContent( retMsg );
				callBack.messageReceived( msg );
			}
			catch( IOException ioe ) {
				LMMLogger.error("Unable to create a call-back status for a FTP msg", ioe);				
			}
		}
	}

	public boolean isNameUnique() {
		return isNameUnique;
	}

}