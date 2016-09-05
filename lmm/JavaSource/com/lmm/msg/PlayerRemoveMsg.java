package com.lmm.msg;


public class PlayerRemoveMsg extends BaseMsg {
	static final long serialVersionUID = 100002L;

	private String playerUuid = null;
	
	public PlayerRemoveMsg( String pUuid ) {
		super();
		playerUuid = pUuid;
	}

	public String getPlayerUuid() {
		return playerUuid;
	}


}
