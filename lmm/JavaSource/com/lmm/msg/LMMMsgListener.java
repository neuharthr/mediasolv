package com.lmm.msg;

import messageit.message.LocalMsg;
import messageit.message.Message;

/**
 * Only used as a marking interface
 */ 
public interface LMMMsgListener {
	
	public void messageReceived( Message msg );
	
	public void messageLocalReceived( LocalMsg local );
}