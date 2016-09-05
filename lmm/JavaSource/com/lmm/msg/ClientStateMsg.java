package com.lmm.msg;


public class ClientStateMsg extends BaseMsg {
	static final long serialVersionUID = 100002L;
	
	private MsgUtils.Statuses status = MsgUtils.Statuses.Idle;
	private String currentPlay = null;
	private int currentPlaySlot = 0;
	private long uptime = 0L;
	

	public ClientStateMsg() {
		super();
	}

    /**
     * @return
     */
    public String getCurrentPlay() {
        return currentPlay;
    }

    /**
     * @param string
     */
    public void setCurrentPlay(String string) {
        currentPlay = string;
    }

    /**
     * @return
     */
    public int getCurrentPlaySlot() {
        return currentPlaySlot;
    }

    /**
     * @param i
     */
    public void setCurrentPlaySlot(int i) {
        currentPlaySlot = i;
    }


    /**
     * @return
     */
    public long getUptime() {
        return uptime;
    }

    /**
     * @param l
     */
    public void setUptime(long l) {
        uptime = l;
    }

    /**
     * @return
     */
    public MsgUtils.Statuses getStatus() {
        return status;
    }

    /**
     * @param s
     */
    public void setStatus(MsgUtils.Statuses s) {
        status = s;
    }

    public void setIdle() {
    	setMsgDate( null ); 
    	setStatus( MsgUtils.Statuses.Idle );
    }

    public boolean isIdle() {
    	return getMsgDate() == null;
    }

}
