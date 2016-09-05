package com.lmm.server.lite;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * 
 * @author ryan
 */
public class LiteGroupPlayer extends LiteBase {

	private String description = null;
	private SortedSet<String> playerIDs = new TreeSet<String>();


	public LiteGroupPlayer( String uid ) {
		super( uid );
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SortedSet<String> getPlayerIDs() {
		return playerIDs;
	}
	
}
