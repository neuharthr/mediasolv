package com.lmm.sched.proc;

import java.io.Serializable;

/**
 * Global system configuration settings that are shared among many components. Try
 * to have defaults values for every attribute in this class.
 */
public class LMMGlobalConfig implements Serializable {

	static final long serialVersionUID = 100001L;

	//ALL attributes must have useful default values
	private int msgQuestionableMins = 5, msgDownMins = 15,
		msgFileChunkSizeMB = 2, msgFileTimeoutSecs = 80,
		appFlags = 0, serverPort = 7730,
		screenShotIntervalHrs = 0;

	private boolean mailAlertEnabled = false, autoUpdate = true,
			debugLog = false, popEnabled = false;
	
	private String
		computerName,	//gets auto generated if it is NULL
		serverHost = "127.0.0.1",
		ftpUrl, ftpUsername, ftpPword,
		ftpUploadDir = ".",
		
		killCmd = "pskill.exe OnTheAir",
		killCronExpr = "0 3 3 * * ?",	//once a day

		defaultTheme = "default.xml",
		mailHost = "mail.lastmilemarketing.com",
		mailTo = "ryan@lastmilemarketing.com",
		mailFrom = "bot@lastmilemarketing.com",
		mailSubject = "An LMM computer has just started",
		mailPassword = "unknown",
		ipDiscoveryUrl = "http://www.lastmilemarketing.com/ip.php",
		ipCheckCronExpr = "33 33 0,6,12,18 * * ?"; //every 3 hours
	

	//non settabble attributes that are NOT set in the config file
	private String computerId = null;
	

    public LMMGlobalConfig() {
        super();
    }


    /**
     * @return
     */
    public String getComputerId() {
        return computerId;
    }

    /**
     * @return
     */
    public int getMsgDownMins() {
        return msgDownMins;
    }

    /**
     * @return
     */
    public int getMsgFileChunkSizeMB() {
        return msgFileChunkSizeMB;
    }

    /**
     * @return
     */
    public int getMsgFileTimeoutSecs() {
        return msgFileTimeoutSecs;
    }

    /**
     * @return
     */
    public int getMsgQuestionableMins() {
        return msgQuestionableMins;
    }

    /**
     * @param string
     */
    public void setComputerId(String string) {
        computerId = string;
    }

    /**
     * @param i
     */
    public void setMsgDownMins(int i) {
        msgDownMins = i;
    }

    /**
     * @param i
     */
    public void setMsgFileChunkSizeMB(int i) {
        msgFileChunkSizeMB = i;
    }

    /**
     * @param i
     */
    public void setMsgFileTimeoutSecs(int i) {
        msgFileTimeoutSecs = i;
    }

    /**
     * @param i
     */
    public void setMsgQuestionableMins(int i) {
        msgQuestionableMins = i;
    }

    /**
     * @return
     */
    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    /**
     * @return
     */
    public boolean isMailAlertEnabled() {
        return mailAlertEnabled;
    }

    /**
     * @param b
     */
    public void setAutoUpdate(boolean b) {
        autoUpdate = b;
    }

    /**
     * @param b
     */
    public void setMailAlertEnabled(boolean b) {
        mailAlertEnabled = b;
    }

    /**
     * @return
     */
    public int getAppFlags() {
        return appFlags;
    }

    /**
     * @param i
     */
    public void setAppFlags(int i) {
        appFlags = i;
    }

    /**
     * @return
     */
    public boolean isDebugLog() {
        return debugLog;
    }

    /**
     * @param b
     */
    public void setDebugLog(boolean b) {
        debugLog = b;
    }

    /**
     * @return
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * @param i
     */
    public void setServerPort(int i) {
        serverPort = i;
    }

    /**
     * @return
     */
    public String getDefaultTheme() {
        return defaultTheme;
    }

    /**
     * @param string
     */
    public void setDefaultTheme(String string) {
        defaultTheme = string;
    }


	public String getMailFrom() {
		return mailFrom;
	}

	
	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}


	public String getMailHost() {
		return mailHost;
	}


	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}


	public String getMailSubject() {
		return mailSubject;
	}


	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}


	public String getMailTo() {
		return mailTo;
	}


	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}


	public String getFtpPword() {
		return ftpPword;
	}


	public void setFtpPword(String ftpPword) {
		this.ftpPword = ftpPword;
	}


	public String getFtpUploadDir() {
		return ftpUploadDir;
	}


	public void setFtpUploadDir(String ftpUploadDir) {
		this.ftpUploadDir = ftpUploadDir;
	}


	public String getFtpUrl() {
		return ftpUrl;
	}


	public void setFtpUrl(String ftpUrl) {
		this.ftpUrl = ftpUrl;
	}


	public String getFtpUsername() {
		return ftpUsername;
	}


	public void setFtpUsername(String ftpUsername) {
		this.ftpUsername = ftpUsername;
	}


	public String getIpCheckCronExpr() {
		return ipCheckCronExpr;
	}


	public void setIpCheckCronExpr(String ipCheckCronExpr) {
		this.ipCheckCronExpr = ipCheckCronExpr;
	}


	public String getIpDiscoveryUrl() {
		return ipDiscoveryUrl;
	}


	public void setIpDiscoveryUrl(String ipDiscoveryUrl) {
		this.ipDiscoveryUrl = ipDiscoveryUrl;
	}


	public String getServerHost() {
		return serverHost;
	}


	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}


	public String getKillCmd() {
		return killCmd;
	}


	public void setKillCmd(String killCmd) {
		this.killCmd = killCmd;
	}


	public String getKillCronExpr() {
		return killCronExpr;
	}


	public void setKillCronExpr(String killCronExpr) {
		this.killCronExpr = killCronExpr;
	}


	public String getComputerName() {
		return computerName;
	}


	public void setComputerName(String computerName) {
		this.computerName = computerName;
	}


	public int getScreenShotIntervalHrs() {
		return screenShotIntervalHrs;
	}


	public void setScreenShotIntervalHrs(int screenShotInterval) {
		this.screenShotIntervalHrs = screenShotInterval;
	}


	public boolean isPopEnabled() {
		return popEnabled;
	}


	public void setPopEnabled(boolean popEnabled) {
		this.popEnabled = popEnabled;
	}


	public String getMailPassword() {
		return mailPassword;
	}


	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}

}