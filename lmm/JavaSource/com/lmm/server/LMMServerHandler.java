package com.lmm.server;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import com.lmm.db.DBCommon;
import com.lmm.msg.ClientStateMsg;
import com.lmm.msg.CmdMsg;
import com.lmm.msg.LMMMsgListener;
import com.lmm.msg.PlayerRemoveMsg;
import com.lmm.sched.proc.GenericScheduler;
import com.lmm.server.lite.LitePlayer;
import com.lmm.tools.LMMLogger;

import messageit.message.LocalMsg;
import messageit.message.Message;


public class LMMServerHandler implements LMMMsgListener {
	
	private GenericScheduler serverScheduler = null;
	private HashMap<String, ClientStateMsg> clientMap;
	private HeavyClient masterClient = null;

	//place holder for needed state
	private HashMap<String, ServerAttributes> clientAttributesMap = new HashMap<String, ServerAttributes>();
	
	public class ServerAttributes {
		private BitSet bs = new BitSet();
		public ServerAttributes() {}

		public void setNotified(boolean val) { bs.set(1, val); };
		public boolean isNotified() { return bs.get(1); };
		
	}


	public LMMServerHandler() {
		super();
	}

	/**
	 * Do allow NULL to be returned
	 */
	public ServerAttributes getClientAttributes( String uuid ) {
		ServerAttributes sa = clientAttributesMap.get( uuid );
		if( sa == null ) {
			sa = new ServerAttributes();
			clientAttributesMap.put( uuid, new ServerAttributes() );
		}

		return (sa == null ? new ServerAttributes() : sa);
	}


	public void messageReceived( Message m ) {
		
		try {
			LMMLogger.debug(" " + m.getSender() + " message received: " + m.getContent());
			
			if( m.getContent() instanceof ClientStateMsg ) {
				handleClientStates( (ClientStateMsg)m.getContent() );
			}
			else if( m.getContent() instanceof CmdMsg ) {
				handleCmdMsg( (CmdMsg)m.getContent(), m.getSender() );
			}
			else if( m.getContent() instanceof PlayerRemoveMsg ) {
				handlePlayerRemove( (PlayerRemoveMsg)m.getContent() );
			}
			
		}
		catch( Exception ex ) {
			LMMLogger.error("An exception occured while handling a server side message", ex);
		}

	}
	
	public void exceptionRaised(Exception ex) {}
	public void messageLocalReceived(LocalMsg ex) {}

	private void handleCmdMsg( CmdMsg cmd, String requestor ) {
		//do nothing if we do not have a valid scheduler object
		if( serverScheduler == null ) {
			LMMLogger.info("The cmd '" + cmd + "' was not performed since the Scheduler is NULL");
			return;
		}

		if( cmd.getCmd() == CmdMsg.Commands.SERVER_FORCE_CHECK ) {
			serverScheduler.runJobNow( LMMServer.JOB_CLIENT_CHECK );
		}
		else if( cmd.getCmd() == CmdMsg.Commands.SERVER_GET_PLAYERS ) {
			serverScheduler.runJobNow( LMMServer.JOB_CLIENT_CHECK );

			//respond to the client that requestor this data
			if( getMasterClient() != null ) {
				getMasterClient().sendMessage(
					(ClientStateMsg[])getClientMap().values().toArray( new ClientStateMsg[getClientMap().values().size()] ),
					requestor );
	
				getMasterClient().sendMessage(
						"Player listing completed", requestor );
			}

		}
		

	}

	private HeavyClient getMasterClient() {
		return masterClient;
	}

	private void handleClientStates( ClientStateMsg cMsg ) {		
		
		synchronized( getClientMap() ) {
			if( !getClientMap().containsKey(cMsg.getUuid()) ) {
				LitePlayer lite = new LitePlayer( cMsg.getUuid() );
				lite.setName( cMsg.getName() );
				DBCommon.getDB().litePlayer_Update( lite );
			}

			getClientMap().put( cMsg.getUuid(), cMsg );
		}
	}

	private void handlePlayerRemove( PlayerRemoveMsg pRemoveMsg ) {		

		synchronized( getClientMap() ) {
			LitePlayer lite = new LitePlayer( pRemoveMsg.getPlayerUuid() );
			getClientMap().remove( lite.getUuid() );
			DBCommon.getDB().litePlayer_Delete( lite );
			
		}
	}

    /**
     * @return
     */
    public HashMap<String, ClientStateMsg> getClientMap() {
    	if( clientMap == null ) {
			clientMap = new HashMap<String, ClientStateMsg>( 128 );

			List<LitePlayer> players = DBCommon.getDB().litePlayer_RetrieveAll();
			for( LitePlayer p : players ) {
				ClientStateMsg cMsg = new ClientStateMsg();
				cMsg.setUuid( p.getUuid() );
				cMsg.setName( p.getName() );
				cMsg.setMsgDate( null );	//not a real msg
				
				getClientMap().put( cMsg.getUuid(), cMsg );
			}
    	}

        return clientMap;
    }

	public void setServerScheduler(GenericScheduler serverScheduler) {
		this.serverScheduler = serverScheduler;
	}

	public void setMasterClient(HeavyClient masterClient) {
		this.masterClient = masterClient;
	}

}
