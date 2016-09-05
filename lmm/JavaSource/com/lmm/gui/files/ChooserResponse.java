package com.lmm.gui.files;

import java.io.File;


public class ChooserResponse {

	private File[] files = new File[0];
	private String msg;


	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public boolean isError() {
		return getMsg() != null;
	}
	
	public File[] getFiles() {
		return files;
	}

	public void setFiles(File[] files) {
		this.files = files;
	}

}
