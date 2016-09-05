package com.lmm.msg;

public class CmdMsg extends BaseMsg {
	static final long serialVersionUID = 100002L;

	public enum Commands {
		RESTART_PLAYER,	//restarts the client scheduler
		PING,
		GET_FILE_LISTING,	//request a list of files on the client
		GET_PROPERTIES,	//get a list of properties for the given client

		RESTART_PC,	//restarts the client PC
		SERVER_FORCE_CHECK,	//force the client to check for update
		GET_SETTINGS,	//requests for te settings at the client
		SERVER_GET_PLAYERS,	//requests a client list from the server
		
		GET_LOG_FILE, //retrieves the current log file via FTP

		FTP_PUT_FILE,	//force the client to upload a file to the FTP server
		FTP_GET_FILE;	//force the client to download a file from the FTP server
	}

	public static final String HDR_FILE_NAME = "cmdFileName"; //a plain file name
	public static final String HDR_FOLDER_NAME = "cmdFolderName"; //a folder name where a file will go
	public static final String HDR_FILE_ID = "cmdFileId"; //unique ID of a file
	public static final String HDR_FILE_CHECKSUM = "cmdFileCheckSum"; //the CRC checksum of a file (used for integrity of transfers)

	private Commands cmd = null;

	public CmdMsg( Commands cmd ) {
		super();
		this.cmd = cmd;
	}

	public Commands getCmd() {
		return cmd;
	}




}
