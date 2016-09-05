package com.lmm.msg;

import java.util.Vector;

public class FileDeleteMsg extends BaseMsg {
	static final long serialVersionUID = 10002L;
	
	private Vector<String> fileNames = new Vector<String>(8);

	public FileDeleteMsg() {
		super();
	}

	public Vector<String> getFileNames() {
		return fileNames;
	}

}
