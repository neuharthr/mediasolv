package com.lmm.gui;

import messageit.message.Message;

/**
 * Used for signaling a lock on a given resource for a particular client.
 * Primarily used for UI locking.
 * 
 * @author ryan
 */
public class UIMutex {

	private String clientName = null;
	private String resourceName = "";
	private byte status = Message.STATUS_OK;
	
	public UIMutex( String clientName, byte status ) {
		super();
		
		if( clientName == null )
			throw new IllegalArgumentException("A clientName must be given");

		this.clientName = clientName;
		this.status = status;
	}

	public UIMutex( String clientName, String resourceName, byte status ) {
		this( clientName, status );

		if( resourceName == null )
			throw new IllegalArgumentException("A resourceName must be given");

		this.resourceName = resourceName;
	}

	public String getClientName() {
		return clientName;
	}

	public String getResourceName() {
		return resourceName;
	}

	public byte getStatus() {
		return status;
	}

	/**
	 * Only when we are set to a complete status AND the current client name
	 * matches the existing client name AND our resource name is found in
	 * this resource string can the mutext be cleared
	 */
	public boolean isCleared( String receiver, String resMsg ) {		
		return getClientName().equalsIgnoreCase(receiver)
				&& getResourceName().indexOf(resMsg) > 0
				&& getStatus() == Message.STATUS_MSG_COMPLETE;
				
	}
}
