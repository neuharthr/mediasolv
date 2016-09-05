package com.lmm.client;

import java.io.Serializable;

public interface ClientSender {

	void sendMessage( Serializable content, String receiver );
	
	void sendMessage( Serializable content, String receiver, byte status, byte priority );
}
