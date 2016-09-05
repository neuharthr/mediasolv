package com.lmm.sched.jobs;

import java.util.HashMap;

import messageit.message.Message;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import com.lmm.client.LMMClient;
import com.lmm.msg.ClientStateMsg;
import com.lmm.msg.MsgUtils;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.sched.proc.PlayOrder;

/**
 * Sends a heartbeat status message
 * 
 */
public class ClientMsgJob implements Job
{
	//props from the scheduler
	public static final String CLIENT_MAP = "client_map";
	public static final String LMM_CLIENT = "lmm_client";
	
	public ClientMsgJob()
	{
		super();
	}

    public void execute(JobExecutionContext context)
    {
		JobDataMap map = context.getJobDetail().getJobDataMap();		
		HashMap clientMap = (HashMap)map.get(CLIENT_MAP);

		//let us execute the file
		final LMMClient client = (LMMClient)clientMap.get(LMM_CLIENT);		
		final PlayOrder pOrder = (PlayOrder)clientMap.get(VideoJob.PLAYORDER_MAP);
		
		ClientStateMsg csMsg = new ClientStateMsg();
		csMsg.setCurrentPlay( pOrder.getCurrentState().getCurrentPlay() );
		csMsg.setStatus( MsgUtils.Statuses.Healthy );
		csMsg.setCurrentPlaySlot( pOrder.getCurrentState().getCurrentPlaySlot() );
		csMsg.setUptime( System.currentTimeMillis() - LMMUtils.getStartTime().getTime() );
		csMsg.setName( client.getName() );
		csMsg.setUuid( LMMUtils.getComputerId() );

		client.sendMessage(
			csMsg, null, LMMClient.TOPIC_SERVER_UPDATES, Message.PRIORITY_HIGH );		
    }

}
