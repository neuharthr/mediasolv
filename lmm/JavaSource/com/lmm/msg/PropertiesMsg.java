package com.lmm.msg;

import com.lmm.sched.data.IPUpdater;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.tools.Time;


public class PropertiesMsg extends BaseMsg {
	static final long serialVersionUID = 100003L;

	private String version = null;
	private String ipAddress = null;
	private String totalRuntime;
	
	public PropertiesMsg() {
		super();
	}
	
	public void load() {		
		IPUpdater ipUp = new IPUpdater();
		ipAddress = ipUp.getCurrentIP();				

		totalRuntime =
			Time.getDescription(System.currentTimeMillis() - LMMUtils.getStartTime().getTime());

		version = LMMUtils.VERSION;
		setUuid( LMMUtils.getComputerId() );
	}
	

    /**
     * @return
     */
    public String getIpAddress() {
        return ipAddress;
    }
    
    /**
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return
     */
    public String getTotalRuntime() {
        return totalRuntime;
    }

}
