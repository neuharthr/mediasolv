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

 * Created on 27-sep-2005
 * 21-oct-2005: added copyright notice
 */
package messageit.dispatcher;

import java.io.IOException;
import java.util.*;

import messageit.MsgLogger;
import messageit.message.Message;

/** This class is responsible for managing messages. Messages are accepted in an incoming queue, 
 * dispatched to the outgoing queues of the proper recipient clients, and then sent. 
 * The manager also manages topics and subscriptions to topics. 
 * A topic has an associated set of subscribers, initially empty. Later clients can subscribe 
 * and unsubscribe it. 
 * @author Luca Cristina
 */
public class MessageManager {
    private Map subscriptions;
    private MessageQueue incoming;
    private MessageManagerThread mmt;

    /** 
     * Initializes the MessageManager and the associated dispatching thread
     */
    public MessageManager() {
        subscriptions = new HashMap();
        incoming = new MessageQueue();
        mmt = new MessageManagerThread();
        new Thread(mmt, "Message_Mgr").start();
    }
    /** Shuts down the manager and thread
     */
    public void shutdown() {
        mmt.active = false;
    }
    /** Method used to notify the manager that a new Message has arrived. 
     * The message is queued to be dispatched later. 
     */
    public void messageArrived(Message msg) {
        incoming.insert(msg);
    }
    /** Adds a topic to the manager. 
     * @param topic the topic to add
     */
    public void addTopic(String topic) {
        if (topicExists(topic))
            return;
        
        subscriptions.put(topic, new Vector());
    }
    /** Removes a topic from the manager. 
     * @param topic the topic to add
     */
    public void removeTopic(String topic) {
        if (!topicExists(topic))
            return;

        subscriptions.remove(topic);
    }
    /** Checks if a topic exists in the manager. 
     * @param topic the topic to look for
     * @return true if the topic exists
     */
    public boolean topicExists(String topic) {
        return subscriptions.containsKey(topic);
    }
    /** Adds a subscription to a given topic for a given client
     * @param clientID the subscriber's ID
     * @param topic the topic's name
     */
    public void addSubscription(String clientID, String topic) {
        addTopic(topic);

        Vector subscribers = (Vector)subscriptions.get(topic);
        
        synchronized( subscribers ) {
	        subscribers.add(clientID);
        }

    }
    /** Removes a subscription to a given topic for a given client
     * @param clientID the subscriber's ID
     * @param topic the topic's name
     */
    public void removeSubscription(String clientID, String topic) {

        Vector subscribers = (Vector)subscriptions.get(topic);
        if (subscribers == null)
            return; //unsubscribed an unknown topic

		synchronized( subscribers ) {
			subscribers.remove(clientID);
		}
		
        if (subscribers.isEmpty())
            removeTopic(topic);
    }
    /** Removes all subscriptions for a given client
     * @param clientID the subscriber's ID
     */
    public void removeAllSubscriptions(String clientID) {

        Iterator i = subscriptions.keySet().iterator();
        while (i.hasNext())
            removeSubscription(clientID, (String)i.next());

        //doesn't remove empty topics, not a problem for now
        //TODO check
    }
    /** This Runnable performs the actual job of dispatching messages. 
     */
    private class MessageManagerThread implements Runnable {
        boolean active = true;
        /**
         * Main dispatching loop. While it runs, it extracts messages from the incoming queue 
         * and adds them to the appropriate clients' queues through the respective 
         * ClientHandler instances. When no message is coming, it instructs
         * all ClientHandlers to send all the messages they have in queue. 
         * This method is to be called by the Thread running this Runnable. Do not call. 
         */
        public void run() {
            while (active) {
                //while( !incoming.isEmpty() )
				if( !incoming.isEmpty() )
                    dispatchIncoming();

                try {
                    //ClientHandler.flushAllQueues();
					ClientHandler.flushNextMessages();
                }
                catch (Exception ex) {
                    MsgLogger.error( "Unable to flushNextMessages()", ex );
                }
                try {
                	//how often our meessages should be sent out to
                	//  every client (can be very expensive!)
                    Thread.sleep(500);
                }
                catch (InterruptedException ie) {}
            }
        }

        private void dispatchIncoming() {
            Message m;
            m = incoming.extract();
            
            //recipient list handler
            if (m.getRecipient() != null) {
            	String[] receivers = m.getAllRecipients();
            	for( int i = 0; i < receivers.length; i++ )
                	enqueue( m, receivers[i] );
            }
            else { //must be a msg sent to a broad topic
                if (m.getTopic() == null)
                    throw new RuntimeException("No recipient and no topic for message");

                Vector subscribers =
                    (Vector)subscriptions.get(m.getTopic());
                if (subscribers == null) {
                    //TODO - no such topic - no action for now - check
                    return;
                }
	                
				synchronized( subscribers ) {
	                Iterator i = subscribers.iterator();
	                try {
		                while (i.hasNext())
		                    enqueue(m, (String)i.next());
	                }
	                catch( Exception ex ) {
	                	MsgLogger.info("Exception during Iterator walking in dispatchIncoming()");
						MsgLogger.info("   " + ex.getMessage() );
	                }

				}

            }
        }

        private void enqueue(Message m, String clientID) {        	
            if (ClientHandler.getHandler(clientID) != null)
                ClientHandler.getHandler(clientID).enqueueMessage(m);
            else {
                //no such recipient, tell the sender that
				// this message is going nowhere
				Message respMsg = new Message( Message.STATUS_ERROR );
				respMsg.setRecipient( m.getSender() );
				respMsg.setSender( Dispatcher.DEFAULT_SERVER_ID );
				respMsg.setPriority( Message.PRIORITY_HIGH );
				
				try {
					respMsg.setContent(
						"The message was unable to be delivered to '"
						+ clientID + "'" );
					
					//maybe a subscriber who is not with us anymore... wipe them
					removeAllSubscriptions( clientID );
				}
				catch ( IOException  ioe ) {
					MsgLogger.error( "Unable to form response for invalid client", ioe );
				}
				
				ClientHandler.getHandler(m.getSender()).enqueueMessage( respMsg );
            }
        }
        
    }
}
