package com.lmm.server;


import integrity.client.JAUUS;

import java.io.IOException;
import java.util.Date;

import org.quartz.SimpleTrigger;

import messageit.MsgLogger;
import messageit.dispatcher.Dispatcher;


import com.lmm.db.DBCommon;
import com.lmm.sched.jobs.ClientCheckJob;
import com.lmm.sched.proc.GenericScheduler;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.tools.LMMLogger;

/**
 * Server that listens on the given port number
 * 
 */
public class LMMServer {

	private GenericScheduler scheduler = null;
	private LMMServerHandler serverHandler = null;
	private int portNumber = DEFAULT_PORT;
	private HeavyClient mc = null;
	
	private static long DEAD_CLIENT_CHECK = 510000L; //8.5 minutes
	public static final int DEFAULT_PORT = 7730; //start here
	public static final String MASTER_SERVER_ID = "[Master_Server]";
	
	//server scheduled jobs
	public static final String JOB_CLIENT_CHECK = "_ClientCheck";


    public LMMServer() {
        this( DEFAULT_PORT );
    }

    public LMMServer(int portNum) {
        super();
        setPortNumber(portNum);
    }

    /**
     * Test method to run server
     */
    public static void main(String[] args) {

		LMMLogger.info( "Starting MediaSOLV Server v. " + LMMUtils.VERSION );
		System.setProperty( DBCommon.DB_FILENAME_KEY, "db_server.yap" );
		int servPortNumber = LMMUtils.getServerPort();

        if (args.length != 1) {
            System.err.println();
            System.err.println("Usage:");
            System.err.println("  java LMMServer port");
            System.err.println();
        }
        else
        	servPortNumber = Integer.parseInt(args[0]);

		//start our auto update server if needed
		if( LMMUtils.isAutoUpdate() )
			new Thread( new Runnable() {
				public void run() {
					new JAUUS( new String[]{"-server"} );		
				}
			}, "UpdateServerThread").start();
		


		//start the listening server
		LMMServer server = new LMMServer(servPortNumber);
		server.start();
    }

	private void initScheduler() {
		
		scheduler = new GenericScheduler();
		getServerHandler().setServerScheduler( scheduler );

		//in 20 seconds, start checking clients for bad statuses
		scheduler.addSimple(
			JOB_CLIENT_CHECK,
			new Date(System.currentTimeMillis() + 20000L),
			null,
			SimpleTrigger.REPEAT_INDEFINITELY, DEAD_CLIENT_CHECK,  //check interval
			ClientCheckJob.class,
			ClientCheckJob.SERVER_REF,
			this );		
	}

    /**
     * Starts the listening of the server
     */
    public void start() {

		LMMLogger.setShowTime( true );
		MsgLogger.setShowTime( true );
        LMMLogger.info(
            "Starting LMMServer on port " + getPortNumber() + "...");


		Dispatcher disp = null;
		try {
			//first, create a message dispatcher and start it
			disp = new Dispatcher( portNumber );
			disp.start();
			
			//then create a new Thread to execute it
			Thread t = new Thread(disp, "LMMDispatcher");
			t.start();

			initScheduler();


			//scheduler is all good, lets start our connection
			getMasterClient().connect();
			
		
			//infinite wait
			t.join();
		}
		catch( Exception ioe ) {
			LMMLogger.error( "LMMServer stopped listening", ioe );		
		}
		finally {
			if( disp != null )
				try { 
					disp.shutdown(true);
				}
				catch( IOException ioe ) {
					LMMLogger.error( "Unable to shutdown LMMServer", ioe );		
				}				
		}

    }
    
    public HeavyClient getMasterClient() {
    	if( mc == null ) {
			mc = new HeavyClient(
				LMMServer.MASTER_SERVER_ID, getServerHandler() );
			
			getServerHandler().setMasterClient( mc );
    	}
		
		return mc;
    }
    
    /**
     * @return
     */
    public int getPortNumber() {
        return portNumber;
    }

    /**
     * @param i
     */
    public void setPortNumber(int i) {
        portNumber = i;
    }


    /**
     * @return
     */
    public LMMServerHandler getServerHandler() {    	
    	if( serverHandler == null )
			serverHandler = new LMMServerHandler();

        return serverHandler;
    }

}
