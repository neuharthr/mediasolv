/*

    MessageIT - Italian messaging middleware
    Copyright (C) 2005  Luca Cristina <lcristina at sourceforge dot net>

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

 * Created on 20-sep-2005
 * 21-oct-2005: added copyright notice
 */
package messageit.dispatcher;

import java.io.EOFException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.safehaus.uuid.UUIDGenerator;


import messageit.MessageITProtocolConstants;
import messageit.MsgLogger;
import messageit.message.Message;
import messageit.message.MessageSender;

/** This class is responsible for communication with a client connected to the dispatcher. 
 * It implements the server side of the communication protocol and interacts with the client, 
 * notifying actions to the Dispatcher and allows the Dispatcher to forward messages to the client. 
 * @author Luca Cristina
 */
public class ClientHandler implements MessageSender, MessageITProtocolConstants, Runnable {

    private Dispatcher dispatcher;
    private Socket sc;
	private Thread listenerThread;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private MessageQueue queue;
    private String clientID;


	//client -> handler mapping, and queue flushing
	private static Map clientHandlers = new HashMap();


    /** Initializes the ClientHandler with the Dispatcher and the client connection it is associated to. 
     * @param disp the Dispatcher to which client requests must be forwarded
     * @param key the SelectionKey representing the client's connection
     */
    public ClientHandler( Dispatcher disp, Socket socket )
        throws IOException {
        sc = socket;
        dispatcher = disp;
        ois();
        oos();
        queue = new MessageQueue();
    }
    /** Serves the client. This method assumes that some input from client is available; 
     * otherwise it will lock waiting for it. 
     * @throws IOException if a communication error occurs
     */
    public void run() {
        
        boolean healthy = true;
		Object o = null;

        while( healthy ) {
	        try {
	        	o = readObject();

				if (o instanceof String) {
					handleStringMsg( (String)o );
				}
				else if (o instanceof Message) {
					if( clientID != null )
						dispatcher.messageArrived( (Message)o );
				}
				else {
					throw new IllegalArgumentException(
						clientID + ":  Unable to handle object message = '" + o + "'" );
				}

	        }
	        catch( Exception ex ) {
				healthy = false;
				MsgLogger.error( clientID + ": Connection Error, ENDing connection", ex );

				finalShutdown();
	        }
	
        }

    }
    
    /**
     * Let's try everything possible to shut this socket down.
     *
     */
    private void finalShutdown() {
    	
    	Exception exc = null;

    	try {
			//simulate an END message if the client connection messed up
			handleStringMsg( MSGIT_END );
			exc = null;
		} catch( Exception e ) {
			exc = e;
		}
		
		//try to sweep the socket with more aggression
		if( exc != null ) {
			try{
				killClient( getHandler(clientID) );
				exc = null;
			} catch( Exception e ) {
				exc = e;
			}
		}

		//a straigth close is our last hope!
		if( exc != null) {
			try{
				sc.close();
				exc = null;
			} catch( Exception e ) {
				exc = e;
			}
		}

		if( exc != null )
			MsgLogger.error("All attempts at removing client socket FAILED", exc);    	
    }
    
    private void handleStringMsg( String s ) throws IOException {

		MsgLogger.debug("  (handleStringMsg) " + clientID + ": " + s);
		if( s.startsWith(MSGIT_CLIENT) ) {
			clientID = s.substring(MSGIT_CLIENT.length());

			//if already exists, lets remove the old one
			if( getHandler(clientID) != null ) {
				MsgLogger.info( "Removing duplicate client: " + clientID );
				getHandler(clientID).endClient();
			}

			//add our new handler
			addHandler( clientID, this );


			String reply = "";
			ObjectOutputStream os = oos();
			if( os != null ) {
				//doing this everytime could be a perfomance problem in the future!
				reply = UUIDGenerator.getInstance().generateTimeBasedUUID().toString();
				reply += MessageITProtocolConstants.RECIPIENT_DELIM + Dispatcher.version;

				//reply string format is:  AE43209xx;2.2.10
				MsgLogger.debug( "Initial reply to " + clientID + " is: " + reply);
				os.writeObject( reply );
				os.flush();
			}

		}
		else if (s.startsWith(MSGIT_SUBSCRIBE))
			dispatcher.clientSubscribed(
				clientID,
				s.substring(MSGIT_SUBSCRIBE.length()));
		else if (s.equals(MSGIT_UNSUBSCRIBE))
			dispatcher.clientUnsubscribedAll(clientID);
		else if (s.startsWith(MSGIT_UNSUBSCRIBE))
			dispatcher.clientUnsubscribed(
				clientID,
				s.substring(MSGIT_UNSUBSCRIBE.length()));
		else if (s.equals(MSGIT_END)) {
			endClient();
		}

    }
    
    public void endClient() {
		MsgLogger.info( clientID + ": ENDING CLIENT..." );    	
		dispatcher.clientDisconnected( clientID );
		killClient( getHandler(clientID) );    	
		removeHandler( clientID );
    }

    /** Adds a Message to the queue of messages to be sent to the client
     * @param msg the Message to send
     */
    public void enqueueMessage(Message msg) {
        queue.insert(msg);
    }

    /** Sends all messages in the 'outbox' queue to the client
     * @throws IOException if a communication error occurs
     */
    private void flushQueue() throws IOException {
        if (queue.isEmpty())
            return;
        while (!queue.isEmpty()) {
            oos().writeObject(queue.extract());
			oos().flush();
        }
    }
	/** Sends the next message in the 'outbox' queue to the client
	 * @throws IOException if a communication error occurs
	 */
	private void flushQueueMessage() throws IOException {
		if (queue.isEmpty())
			return;

		oos().writeObject( queue.extract() );
		oos().flush();
	}

    /** Read an Object from the client's communication channel
     * @return the read object, or null if it could not be unserialized and instantiated
     * @throws IOException if a communication error occurs
     */
    private Object readObject() throws IOException {
    	if( sc == null || ois == null )
    		return null;

		Object o = null;
        try {
           	o = ois().readObject();
        }
		catch( InvalidClassException ice ) {
			//object version mismatch...bad			
			ObjectOutputStream os = oos();
			if( os != null ) {
				oos().writeObject( ice );
				oos().flush();
			}
		}
		catch (EOFException eof) {
			MsgLogger.error(" Caught EOFException, all objects have been read.", eof);
		}
		catch (OptionalDataException od) {
			MsgLogger.info(" Caught OptionalDataException, the stats: ");
			MsgLogger.info("   length= " + od.length);
			MsgLogger.info("   eof= " + od.eof );
			MsgLogger.info("   msg= " + od.getMessage() );
			throw od;
		}
        catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        
        return o;
    }
    //object I/O streams lazy create
    private ObjectInputStream ois() throws IOException {
        if (sc == null)
            return null;
        if (ois == null)
        	ois = new ObjectInputStream( sc.getInputStream() );
//			ois = new ObjectInputStream(
//				MsgCipher.getInstance().getCipherInputStream(sc.getInputStream()) );

        return ois;
    }
    private ObjectOutputStream oos() throws IOException {
        if (sc == null)
            return null;
        if (oos == null)
			oos = new ObjectOutputStream( sc.getOutputStream() );
//			oos = new ObjectOutputStream(
//				MsgCipher.getInstance().getCipherOutputStream(sc.getOutputStream()) );

        return oos;
    }

	public void startListening( String name ) {

		if( name == null )
			name = hashCode() + "";

		if(listenerThread == null) {
			listenerThread = new Thread( this, name + "_listener" );
			listenerThread.setDaemon( true );
			listenerThread.start();
		}
		else {
			if( listenerThread.isAlive() )
				listenerThread.interrupt();

			listenerThread = null;
			startListening( name );
		}
	}

    /** Sends all messages in the 'outbox' queues of all ClientHandler instances in the VM to the client
     * @throws IOException if a communication error occurs
     */
    public static void flushAllQueues() throws IOException {
        ClientHandler ch;
        
		synchronized( clientHandlers ) {
	        Iterator i = clientHandlers.values().iterator();
	        while (i.hasNext()) {
	            ch = (ClientHandler)i.next();
	            ch.flushQueue();
	        }
		}
    }

	/** Sends the next message in the 'outbox' queues of each ClientHandler instances
	 * in the VM to the client. This ensures every client handler gets a fair chance
	 * to send it's own message. 
	 * @throws IOException if a communication error occurs
	 */
	public static void flushNextMessages() throws IOException {
		ClientHandler ch;

		synchronized( clientHandlers ) {
			if( clientHandlers.size() > 0 ) {
				Iterator i = clientHandlers.values().iterator();
				while( i.hasNext() ) {
					ch = (ClientHandler)i.next();
					ch.flushQueueMessage();
				}
			}
		}
	}

    private static void addHandler(String clientID, ClientHandler ch) {
		synchronized( clientHandlers ) {
	        clientHandlers.put(clientID, ch);
		}
    }
    private static void removeHandler(String clientID) {
		synchronized( clientHandlers ) {
	        clientHandlers.remove(clientID);
		}
    }

	private static void killClient( ClientHandler ch ) {
		
		synchronized( clientHandlers ) {
			//if we are still null, we have issues
			if( ch == null )
				throw new IllegalArgumentException(
					"Unable to killClient() because the ClientHandler is NULL");

			try{
				ch.sc.close();
			} catch( Exception e ) {} //no biggy

			ch.sc = null;
		}
	}

    /**
     * Returns the ClientHandler associated with a given client ID
     * @param clientID the client's ID
     * @return the associated ClientHandler
     */
    public static ClientHandler getHandler(String clientID) {
        return (ClientHandler)clientHandlers.get(clientID);
    }
     

	/**
	 * Returns all the clientIDs for every registerd ClientHandler
	 * @return the associated clientIDs
	 */
	public static String[] getAllClientIDs() {
		String[] names = new String[0];
		synchronized( clientHandlers ) {
			Iterator it = clientHandlers.values().iterator();
			names = new String[ clientHandlers.values().size() ];

			int i = 0;
			while( it.hasNext() ) {	
				names[i++] = ((ClientHandler)it.next()).clientID;			
			}
		}
		
		return names;
	}

}
