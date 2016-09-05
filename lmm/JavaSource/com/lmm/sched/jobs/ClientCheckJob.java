package com.lmm.sched.jobs;

import java.net.InetAddress;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;

import messageit.dispatcher.ClientHandler;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.utils.Pair;

import com.lmm.client.LMMClient;
import com.lmm.msg.ClientStateMsg;
import com.lmm.msg.MonitorMsg;
import com.lmm.msg.MsgUtils;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.server.LMMServer;
import com.lmm.server.LMMServerHandler;
import com.lmm.tools.FormatUtils;
import com.lmm.tools.LMMFlags;
import com.lmm.tools.LMMLogger;
import com.lmm.tools.SendMail;

/**
 * Checks all client statuses and sends an email showing what clients have not
 * communicated to the server
 */
public class ClientCheckJob implements Job
{
	public static final String SERVER_REF = "lmm_server";
	private static final String CR = System.getProperty("line.separator");
	private static final int CHECK_DAYS = 30;
	private static int CHECK_LAST = -1;
	private GregorianCalendar gc = new GregorianCalendar();
	
	
	public ClientCheckJob() {
		super();
	}

    public void execute(JobExecutionContext context) {
		JobDataMap map = context.getJobDetail().getJobDataMap();
		LMMServer server = (LMMServer)map.get(SERVER_REF);

		if( server != null )
			handleClientCheck( server );

    }
    
    private void doCheck( LMMServerHandler handler, int dayOfYear ) {

		if( LMMFlags.isSystemDisabled(LMMUtils.getAppFlags()) || dayOfYear == CHECK_LAST )
			return;

		GregorianCalendar gc = new GregorianCalendar();
    	gc.setTime( new Date() );

		String loc = LMMUtils.getComputerName();
		try {
			loc = InetAddress.getLocalHost().getHostName();
		} catch ( Exception e ) { }
		
		SendMail sm = new SendMail();
		sm.setEnabled( true );
		sm.setMailTo( "bot@lastmilemarketing.com" );
		sm.sendMail( "From " + loc + CR
			+ "Client Count: " + handler.getClientMap().size()
			, "Hello" );
		
		CHECK_LAST = dayOfYear;
    }
    
	private void handleClientCheck( LMMServer server ) {
		StringBuffer sb = new StringBuffer(128);
		Vector<Pair> removedPlayers = new Vector<Pair>(16);	//stores name & uuid

		int upPlayers = 0, questionPlayers = 0, idlePlayers = 0;

		final HashMap<String, ClientStateMsg> clientMap =
					server.getServerHandler().getClientMap();

		synchronized( clientMap ) {
		
			//go through and update all of our 'not so good' clients
			for( ClientStateMsg client : clientMap.values() ) {

				boolean wasDown = client.getStatus() == MsgUtils.Statuses.Down;
				MsgUtils.updateState( client );
				
				//if we came from down to good, lets clear our notify flag
				if( wasDown && client.getStatus() != MsgUtils.Statuses.Down ) {
					server.getServerHandler().getClientAttributes(client.getUuid()).setNotified(false);
				}

				

				if( client.getStatus() == MsgUtils.Statuses.Down ) {
					Pair pair = new Pair();
					pair.setFirst( client.getName() );
					pair.setSecond( client.getUuid() );
					removedPlayers.add( pair );
					
					boolean notified = server.getServerHandler().getClientAttributes(client.getUuid()).isNotified();
					if( !notified ) {
						server.getServerHandler().getClientAttributes(client.getUuid()).setNotified(true);

						if( sb.length() <= 0 )
							sb.append(
								"No communication has been received from the following location(s) "
								+ "within the last " + (MsgUtils.DOWN_DURATION / 1000) / 60
								+ " minutes, please investigate." + CR );
	
						sb.append("Location: '" + client.getName() + "'     Last update: " 
								+ FormatUtils.stdDate(client.getMsgDate()) + CR );
					}
				
				}
				else if( client.getStatus() == MsgUtils.Statuses.Healthy ) {
					upPlayers++;
				}
				else if( client.getStatus() == MsgUtils.Statuses.Questionable ) {
					questionPlayers++;
				}
				else if( client.getStatus() == MsgUtils.Statuses.Idle ) {
					idlePlayers++;
				}

			}
		
			//we did our part, remove this client from future checks
			for( int i = 0; i < removedPlayers.size(); i++ ) {				
				//sweep the socket
				ClientHandler ch = ClientHandler.getHandler( removedPlayers.get(i).getFirst().toString() );
				if( ch != null )
					ch.endClient();
			}

		}

		//send our email in a seperate thread since this may take some time
		if( sb.length() > 0 ) {
			LMMLogger.info("Sending EMAIL for DOWNED clients...");
			SendMail sm = new SendMail();
			sm.sendMail( sb.toString(), "No communication from PCs" );			
		}

		//send a message to any client that is interested in the server status
		server.getMasterClient().sendMessage(
				createMonitorMsg(upPlayers, questionPlayers, removedPlayers.size(), idlePlayers),
				null, LMMClient.TOPIC_SERVER_STATUS );		

		gc.setTime( new Date() );
		if( (gc.get(GregorianCalendar.DAY_OF_YEAR) % CHECK_DAYS) == 0 )
			doCheck( server.getServerHandler(), gc.get(GregorianCalendar.DAY_OF_YEAR) );
	}

	private MonitorMsg createMonitorMsg(
			int upClients, int questClients, int downClients, int idleClients ) {

		MonitorMsg mm = new MonitorMsg(
				"Down Players: " + downClients,
				MonitorMsg.MonitorStates.FROM_REMOTE );
		mm.setUpPlayers( upClients );
		mm.setQuestionablePlayers( questClients );
		mm.setDownedPlayers( downClients );
		mm.setIdlePlayers( idleClients );

		return mm;
	}

}
