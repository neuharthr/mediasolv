package com.lmm.msg;

/**
 * Used to give a status of the server
 * 
 */
public class MonitorMsg extends BaseMsg {
	static final long serialVersionUID = 100002L;

	private String message = null;
	private int upPlayers = 0;
	private int questionablePlayers = 0;
	private int downedPlayers = 0;
	private int idlePlayers = 0;
	private MonitorStates state = MonitorStates.STARTING;
	//private String[] downedPlayersIDs = null;

	
	public static final MonitorMsg CONNECTED_MONMSG =
		new MonitorMsg("Connected", MonitorMsg.MonitorStates.CONNECTED );

	public static final MonitorMsg DISCONNECTED_MONMSG =
		new MonitorMsg("Not Connected", MonitorMsg.MonitorStates.NOT_CONNECTED );

	public enum MonitorStates {
		STARTING,
		NOT_CONNECTED,
		CONNECTED,
		FROM_REMOTE		//the server sent this msg
	}
	
	private MonitorMsg( String msg ) {
		super();
		setMessage( msg );
	}

	public MonitorMsg( String msg, MonitorStates monitorState ) {
		this(msg);
		setState(monitorState);
	}

    public boolean isLocalMsg() {
    	return getState() != MonitorMsg.MonitorStates.FROM_REMOTE;
    }
	
	public int getDownedPlayers() {
		return downedPlayers;
	}

	public void setDownedPlayers(int downedPlayers) {
		this.downedPlayers = downedPlayers;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public MonitorStates getState() {
		return state;
	}

	public void setState(MonitorStates state) {
		this.state = state;
	}

	public int getQuestionablePlayers() {
		return questionablePlayers;
	}

	public void setQuestionablePlayers(int pendingPlayers) {
		this.questionablePlayers = pendingPlayers;
	}

	public int getUpPlayers() {
		return upPlayers;
	}

	public void setUpPlayers(int upPlayers) {
		this.upPlayers = upPlayers;
	}

	public int getIdlePlayers() {
		return idlePlayers;
	}

	public void setIdlePlayers(int totalPlayers) {
		this.idlePlayers = totalPlayers;
	}


}
