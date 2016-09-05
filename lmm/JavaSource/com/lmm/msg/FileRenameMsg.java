package com.lmm.msg;

public class FileRenameMsg extends BaseMsg {
	static final long serialVersionUID = 100002L;
	
	private String oldFileName = null;
	private String newFileName = null;

	public FileRenameMsg( String oldName, String newName ) {
		super();
		this.oldFileName = oldName;
		this.newFileName = newName;
	}

	public String getNewFileName() {
		return newFileName;
	}

	public String getOldFileName() {
		return oldFileName;
	}


}
