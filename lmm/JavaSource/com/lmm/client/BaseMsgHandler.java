package com.lmm.client;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Vector;

import com.lmm.msg.BaseMsg;
import com.lmm.msg.LMMMsgListener;

import messageit.message.LocalMsg;
import messageit.message.Message;
import messageit.message.MessageListener;


/**
 * Base class for all message handling classes
 * 
 */
class BaseMsgHandler implements MessageListener {

	//this does support many listeners, but, MessageIT only supports 1 for now
	private Vector<LMMMsgListener> msgListeners = new Vector<LMMMsgListener>(8);
		
	public BaseMsgHandler() {
		super();
	}

	public void messageReceived( Message m ) {		
		synchronized( msgListeners ) {
			for( int i = msgListeners.size()-1; i >= 0; i-- )
				msgListeners.get(i).messageReceived( m );
		}
		
	}
	
	/**
	 * Adjust time to our current time zone.
	 * 
	 * @param msg
	 */
	private void adjustTime( BaseMsg msg ) throws IOException {		

//		final DateFormat df = new SimpleDateFormat();		
//		df.setTimeZone( TimeZone.getDefault() );
//		final String local = df.format( msg.getMsgDate() );
//
//		//df.setTimeZone(vueTimeZone);
//		try {
//			msg.setMsgDate( df.parse(local) );
//		} catch (ParseException ex) {
//			throw new RuntimeException("Unexpected parse failure: \"" + local + "\".");
//		}
	}

	public void addListener( LMMMsgListener msgList ) {
		synchronized( msgListeners ) {
			msgListeners.add( msgList );
		}		
	}

	public void removeListener( LMMMsgListener msgList ) {
		synchronized( msgListeners ) {
			msgListeners.remove( msgList );
		}		
	}

	/**
	 * Send to all liseners
	 */
	public void messageLocalReceived(LocalMsg ex) {
		synchronized( msgListeners ) {
			for( int i = msgListeners.size()-1; i >= 0; i-- )
				msgListeners.get(i).messageLocalReceived( ex );
		}
	}

	//no-op, swallow this
	public void exceptionRaised( Exception ex ) {}
}