package com.lmm.msg;

import java.util.Date;

public class ReportMsg extends BaseMsg {
	static final long serialVersionUID = 100002L;

	//default startTime to 2 weeks ago
	private Date startTime = new Date(System.currentTimeMillis() - 1209600000L);
	private Date endTime = new Date();
	private String themeName;	//the name of the theme that this report is on (optional)
	private String folderLocation;	//requestor storage location for the report file
	
	/**
	 * Default constructor gets a reporting range that leads up to now
	 */
	public ReportMsg() {
		super();
	}

	public ReportMsg( Date startTime, Date endTime) {
		super();
		if( startTime == null || endTime == null )
			throw new IllegalArgumentException("Both the given startTime & endTime for a report must not be null");

		if( startTime.after(endTime) )
			throw new IllegalArgumentException("A reports startTime can not be after the endTime");
		
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public String getFolderLocation() {
		return folderLocation;
	}

	public void setFolderLocation(String folderLocation) {
		this.folderLocation = folderLocation;
	}

}
