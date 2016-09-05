package com.lmm.client;

import java.io.IOException;
import java.io.Serializable;

import messageit.client.Client;
import messageit.message.Message;

import com.lmm.msg.LMMMsgListener;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.tools.LMMLogger;


public class LMMClient implements ClientSender {

	public static final String TOPIC_SERVER_UPDATES = "ServerUpdate";
	public static final String TOPIC_GLOBAL = "GlobalChannel";
	public static final String TOPIC_SERVER_STATUS = "ServerStatus";

	private Client client = null;
	private int port = 7730;
	private String host = "";
	private String name = "<unknown>";
	private String[] topics = new String[]{ LMMClient.TOPIC_GLOBAL }; //default to global channel only

	public LMMClient( String aHost, int aPort, String aName ) {
		super();
		host = aHost;
		port = aPort;
		name = aName;
	}

	public void setMessageListener( LMMMsgListener msgList ) {
		BaseMsgHandler bMsg = new BaseMsgHandler();
		bMsg.addListener( msgList );

		getClient().setMessageListener( bMsg );
	}

	public boolean connect() {

		try {
			if( getTopics() != null ) {
				for( int i = 0; i < getTopics().length; i++ )
					getClient().addTopic( getTopics()[i] );
			}

			getClient().connect();
			
			if( LMMUtils.getComputerId() == null ) {
				LMMUtils.setComputerId( getClient().getUUID() );
			}

			return true;
		}
		catch( Exception ex ) {
			LMMLogger.info("Unable to connect with client @ " + getHost() + ":" + getPort());
			LMMLogger.info("   (exception): " + ex.getMessage());
			return false;
		}
	}
	
	public void disconnect() throws IOException {
		getClient().disconnect();
	}

	public void setReconnect( boolean val ) {
		getClient().setReconnect( val );
	}

	
	public void sendMessage( Serializable content, String aReceiver,
			String aTopic, byte status, byte priority ) {

		if( !getClient().isOnline() )
			return;

		try {
			Message msg = new Message();
			msg.setStatus( status );
			msg.setSender( getName() );
			msg.setRecipient( aReceiver );
			msg.setTopic( aTopic );
			msg.setContent( content );
			msg.setPriority( priority );

			LMMLogger.debug( " Sending message to (recv:" +
						aReceiver + ") (topic:" + aTopic + ")...");

			getClient().sendMessage( msg );
		}
		catch (IOException e) {
			LMMLogger.error("Unable to send message", e);
			try {
				disconnect();
			} catch( IOException ioe ) {}

		}
	}


	public void sendMessage( Serializable content, String receiver ) {
		sendMessage( content, receiver, null, Message.STATUS_OK, Message.PRIORITY_NORMAL );
	}

	public void sendMessage( Serializable content, String receiver,
				byte status, byte priority ) {
		sendMessage( content, receiver, null, status, priority );
	}

	public void sendMessage( Serializable content, String receiver, String topic ) {
		sendMessage( content, receiver, topic, Message.STATUS_OK, Message.PRIORITY_NORMAL );
	}

	public void sendMessage( Serializable content, String receiver,
				String topic, byte priority ) {
		sendMessage( content, receiver, topic, Message.STATUS_OK, priority );
	}

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public String[] getTopics() {
        return topics;
    }

    /**
     * @param string
     */
    public void setTopics(String[] string) {
        topics = string;
    }

    /**
     * @return
     */
    private Client getClient() {    	

		//"67.102.38.194", 7730
    	if( client == null ) {
			client = new Client( getHost(), getPort(), getName() );
			client.setUUID( LMMUtils.getComputerId() );
    	}
        
        return client;
    }

    /**
     * @return
     */
    public String getHost() {
        return host;
    }

    /**
     * @return
     */
    public int getPort() {
        return port;
    }
	
}
