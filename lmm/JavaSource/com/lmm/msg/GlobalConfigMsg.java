package com.lmm.msg;

import com.lmm.sched.proc.LMMGlobalConfig;


public class GlobalConfigMsg extends BaseMsg {
	static final long serialVersionUID = 100002L;

	private LMMGlobalConfig globalConfig = null;
	
	//flag saying if this message is sent to everyone
	private boolean isBroadcastMsg = false;
	
	public GlobalConfigMsg() {
		super();
	}

	public GlobalConfigMsg( LMMGlobalConfig gConfig ) {
		this();
		globalConfig = gConfig;
	}
	
    /**
     * @return
     */
    public LMMGlobalConfig getGlobalConfig() {
        return globalConfig;
    }

	public boolean isBroadcastMsg() {
		return isBroadcastMsg;
	}

	public void setBroadcastMsg(boolean isGlobalMsg) {
		this.isBroadcastMsg = isGlobalMsg;
	}


}
