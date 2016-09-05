package messageit.message;

import java.util.Date;


public class LocalMsg {

	private Date msgDate = new Date();
	private boolean connected = false;
	private boolean isVersionMismatch = false;

	public LocalMsg( boolean isConnected ) {
		super();
		this.connected = isConnected;
	}

    /**
     * @return
     */
    public Date getMsgDate() {
        return msgDate;
    }

    /**
     * @param date
     */
    public void setMsgDate(Date date) {
        msgDate = date;
    }

    /**
     * @return
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * @param b
     */
    public void setConnected(boolean b) {
        connected = b;
    }

	public boolean isVersionMismatch() {
		return isVersionMismatch;
	}

	public void setVersionMismatch(boolean isVersionMismatch) {
		this.isVersionMismatch = isVersionMismatch;
	}

}
