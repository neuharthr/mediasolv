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

import java.io.*;
import java.net.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import messageit.MessageITProtocolConstants;
import messageit.MsgLogger;
import messageit.message.Message;

/** This class represents a message dispatcher, which is a server listening on a predefined port 
 * and serving clients. A dispatcher can be active or inactive. 
 * When it is created it is inactive, and must be started with the start() method. To be executed it must be
 * wrapped in a Thread object which must be started as well. 
 * The dispatcher can be stopped with the stop(boolean) method, and then started over again. 
 * Instead, shutdown is irreversible. After shutdown a dispatcher is unusable. 
 * This class contains only connection handling logic; message management and dispatching is delegated
 * to a MessageManager instance, and service to clients to ClientHandler instances. 
 * @author Luca Cristina
 */
public class Dispatcher implements Runnable {
    public static final int DEFAULT_CONNECTION_BACKLOG = 10;
	public static final String DEFAULT_SERVER_ID = "Server";
 	public static String version = "1";	//used to detect message versioning mismatches 

    private ServerSocket serverSocket = null;
    private boolean accept, serve, shutdown;
    private MessageManager mm;
 	private int port = 7730;

	/**Initializes the Dispatcher*/
	public Dispatcher( int aPort ) {
		accept = false;
		serve = false;
		shutdown = false;
		port = aPort;
		mm = new MessageManager();
	}
	
    /**Starts the dispatcher. The dispatcher will begin accepting connections and serving clients. */
    public void start() throws IOException {
        if (accept)
            return;

		if( MessageITProtocolConstants.IS_SSL ) {
			SSLServerSocketFactory 
					sslSrvFact = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault(); 
                
			serverSocket = sslSrvFact.createServerSocket( port ); 
                        
			//use an anonymous cipher suite so that a KeyManager or TrustManager is not needed 
			//NOTE:  this assumes that the cipher suite is known.  A check -should- be done first. 
			((SSLServerSocket)serverSocket).setEnabledCipherSuites( MessageITProtocolConstants.CIPHER_SUITES ); 
                        
			((SSLServerSocket)serverSocket).setNeedClientAuth(false);                     

		}
		else
			serverSocket = new ServerSocket( port, DEFAULT_CONNECTION_BACKLOG );  //non encrypted

        serve = true;
        accept = true;
    }
    /**Stops the dispatcher. If kickClients is true, will disconnect from all clients, 
     * otherwise will wait for all of them to disconnect normally. 
     * In the meantime the dispatcher can be started again 
     * and no interruption of service will occur for connected clients
     * @param kickClients true to disconnect from all clients, false to wait for them to disconnect
     */
    public void stop(boolean kickClients) throws IOException {
        accept = false;

		if( !serverSocket.isClosed() ) {
			serverSocket.close();
		}

        if (kickClients) {
            serve = false;

            //kick all clients one by one
			String[] clientIDs = ClientHandler.getAllClientIDs();
			for( int i = 0; i < clientIDs.length; i++ ) {
				ClientHandler.getHandler(clientIDs[i]).endClient();
			}
        }
        
		serverSocket = null;
    }

    /**Stops and shuts down the dispatcher. See stop()
     * @param kickClients true to disconnect from all clients, false to wait for them to disconnect
     */
    public void shutdown(boolean kickClients) throws IOException {
        shutdown = true;
        stop(kickClients);
        mm.shutdown();
    }
    /**This method performs the actual job of the Dispatcher. It accepts new connections
     * and delegates service of clients' requests to the appropriate ClientManager. 
     * When inactive, it tries not to waste CPU through Thread.sleep(). 
     */
    public void run() {
        while (!shutdown) {
            while( accept ) {
                try {
					addClient( serverSocket.accept() );

					//check for new connections once a second
					Thread.sleep(1000L);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

			//while the server is not accepting connections let it sleep
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException _ex) {}
        }

    }

//   Can not do ObjectInput(OutPut)Stream with NIO because of this:
//    
//	[06/23/06 06:18:50.397] -LMMScheduler-1151036559818:  Connection Error, ENDing connection
//	java.io.IOException: An existing connection was forcibly closed by the remote host
//		at sun.nio.ch.SocketDispatcher.read0(Native Method)
//		at sun.nio.ch.SocketDispatcher.read(SocketDispatcher.java:25)
//		at sun.nio.ch.IOUtil.readIntoNativeBuffer(IOUtil.java:233)
//		at sun.nio.ch.IOUtil.read(IOUtil.java:206)
//		at sun.nio.ch.SocketChannelImpl.read(SocketChannelImpl.java:207)
//		at messageit.dispatcher.BlockingInputStream.read(BlockingInputStream.java:49)
//		at java.io.ObjectInputStream$PeekInputStream.peek(ObjectInputStream.java:2133)
//		at java.io.ObjectInputStream$BlockDataInputStream.peek(ObjectInputStream.java:2423)
//		at java.io.ObjectInputStream$BlockDataInputStream.peekByte(ObjectInputStream.java:2433)
//		at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1245)
//		at java.io.ObjectInputStream.readObject(ObjectInputStream.java:324)
//		at messageit.dispatcher.ClientHandler.readObject(ClientHandler.java:154)
//		at messageit.dispatcher.ClientHandler.doService(ClientHandler.java:84)
//		at messageit.dispatcher.Dispatcher.service(Dispatcher.java:152)
//		at messageit.dispatcher.Dispatcher.run(Dispatcher.java:122)
//		at java.lang.Thread.run(Thread.java:534)


    /**Adds a new connection to the connections managed by the dispatcher
     * @param sc the SocketChannel representing the connection
     */
    private void addClient(Socket sc) throws IOException {
    	if( sc == null )
    		return;

		new ClientHandler( this, sc ).startListening( sc.getInetAddress().getHostAddress() );
        MsgLogger.info(
            "Connection from " + sc.getInetAddress() + " established");
    }
    /**Notifies the dispatcher that a message has arrived. Treatment is delegated to the MessageManager. 
     * @param m the message
     */
    public void messageArrived(Message m) {
    	if( serve )
        	mm.messageArrived(m);
    }
    /**Notifies the dispatcher that a client has subscribed for a topic. Treatment is delegated to the MessageManager. 
     * @param clientID the subscriber's ID
     * @param topic the topic
     */
    public void clientSubscribed(String clientID, String topic) {
        mm.addSubscription(clientID, topic);
    }
    /**Notifies the dispatcher that a client has unsubscribed from a topic. Treatment is delegated to the MessageManager. 
     * @param clientID the subscriber's ID
     * @param topic the topic
     */
    public void clientUnsubscribed(String clientID, String topic) {
        mm.removeSubscription(clientID, topic);
    }
    /**Notifies the dispatcher that a client has unsubscribed from all topics. Treatment is delegated to the MessageManager. 
     * @param clientID the subscriber's ID
     */
    public void clientUnsubscribedAll(String clientID) {
        mm.removeAllSubscriptions(clientID);
    }
    /**Notifies the dispatcher that a client has left the dispatcher. No action taken for now. 
     * @param clientID the client's ID
     */
    public void clientDisconnected(String clientID) {
		clientUnsubscribedAll( clientID );
    }

}