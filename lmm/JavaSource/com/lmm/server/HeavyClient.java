package com.lmm.server;

import java.io.IOException;
import java.io.Serializable;

import messageit.message.Message;

import com.lmm.client.ClientSender;
import com.lmm.client.LMMClient;
import com.lmm.msg.CmdMsg;
import com.lmm.msg.LMMMsgListener;
import com.lmm.sched.proc.LMMUtils;

/**
 * A class that is used for haevy traffic by sending & receiving data
 * frequently within the system. Typically, a user application will
 * use this client wrapper.
 */
public class HeavyClient implements ClientSender {
	
	private LMMClient client = null;
	private LMMMsgListener msgList = null;
	private String id = null;
	private String[] topics = new String[]{ LMMClient.TOPIC_GLOBAL, LMMClient.TOPIC_SERVER_UPDATES };

	private HeavyClient( String masterId ) {
		super();
		id = masterId;
	}

	public HeavyClient( String masterId, LMMMsgListener listener ) {
		this( masterId );
		msgList = listener;
	}

	public HeavyClient( String masterId, LMMMsgListener listener, String[] allTopics ) {
		this( masterId );
		msgList = listener;
		topics = allTopics;
	}

	public void sendMessage( Serializable content, String receiver ) {
		client.sendMessage( content, receiver, null,
				Message.STATUS_FROM_MASTER, Message.PRIORITY_NORMAL );
	}

	public void sendMessage( Serializable content, String receiver, String topic ) {
		client.sendMessage( content, receiver, topic,
			Message.STATUS_FROM_MASTER, Message.PRIORITY_NORMAL );
	}

	public void sendMessage( Serializable content, String receiver,
			byte status, byte priority ) {
		client.sendMessage( content, receiver, null, status, priority );
	}


	public void connect() {
		client = new LMMClient(
				LMMUtils.getServerHost(),
				LMMUtils.getServerPort(), id );

		client.setTopics( topics );
		
		client.setMessageListener( msgList );

		client.connect();
		
		//send a request for the current Players
		sendMessage(
			new CmdMsg(CmdMsg.Commands.SERVER_GET_PLAYERS),
			LMMServer.MASTER_SERVER_ID );		
	}
	
	public void disconnect() throws IOException {
		client.disconnect();
	}

	public String getHost() {
		return client.getHost();
	}
	
	public int getPort() {
		return client.getPort();
	}

}
