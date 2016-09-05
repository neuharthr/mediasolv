package com.lmm.msg;

import java.io.File;

import com.lmm.sched.proc.LMMUtils;

public class FileListMsg extends BaseMsg {
	static final long serialVersionUID = 100003L;

	private String[] names = new String[0];
	private long[] lengths = new long[0];
	private long[] modified = new long[0];
	private Grouping[] grouping = new Grouping[0];
	private String[] folders = new String[0];

	public enum Grouping {
		Default,
		ProofOfPerformance
	};

	protected FileListMsg() {
		super();
	}

	public FileListMsg( int fileCount ) {
		super();
		names = new String[fileCount];
		lengths = new long[fileCount];
		modified = new long[fileCount];
		grouping = new Grouping[fileCount];
		folders = new String[fileCount];
	}

	public void updateFileAt( final File file, int indx, Grouping grpVal ) {
		if( file == null || indx < 0 || indx >= getNames().length )
			return;

		getNames()[indx] = file.getName();
		getLengths()[indx] = file.length();
		getModified()[indx] = file.lastModified();
		getGrouping()[indx] = grpVal;
		getFolders()[indx] = file.getParent() + LMMUtils.FILE_SEP;
	}
	
    /**
     * @return
     */
    public long[] getModified() {
        return modified;
    }

    /**
     * @return
     */
    public String[] getNames() {
        return names;
    }

    /**
     * @return
     */
    public long[] getLengths() {
        return lengths;
    }

	public Grouping[] getGrouping() {
		return grouping;
	}

	public String[] getFolders() {
		return folders;
	}


}
