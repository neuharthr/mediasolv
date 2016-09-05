package com.lmm.sched.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import com.lmm.sched.proc.LMMUtils;
import com.lmm.tools.LMMLogger;
import com.lmm.tools.ProcessStarter;
import com.lmm.tools.SendMail;

/**
 * Execs a file as an application
 * 
 */
public class ProcessExecJob implements Job
{
	//used to spawn processes
	private ProcessStarter procStarter = new ProcessStarter();

	public static final String EXEC_FILENAME = "lmm_exe_filename";
	public static final String EXEC_TYPE = "lmm_exe_type";
	
	public static final String TYPE_MAIL = "lmm_mail_type";
	
	public ProcessExecJob()
	{
		super();
	}

    public void execute(JobExecutionContext context)
    {
		JobDataMap map = context.getJobDetail().getJobDataMap();

		String execType = (String)map.get(EXEC_TYPE);
		if( TYPE_MAIL.equalsIgnoreCase(execType) ) {
			execMail();
		}
		else {
			execProc( (String)map.get(EXEC_FILENAME) );
		}


    }

	private void execMail() {

		SendMail sm = new SendMail();
		sm.sendMail(
			"The computer '" + LMMUtils.getComputerName() + "' has been started",
			LMMUtils.getMailSubject() );

		//blat - -to ryan@lastmilemarketing.com -server mail.lastmilemarketing.com 
		//-f bot@lastmilemarketing.com -subject "Kellogg LMM Computer STARTING" 
		//-body "The computer at Kellogg has just started up."		
	}

	private void execProc( String fName ) {
		LMMLogger.info("Executing process name: " + fName );

		//fName should be the absolute path to the app
		Process p = procStarter.startProcess(
			"", null, fName );

		try {
			p.waitFor();
		}
		catch( InterruptedException ie ) {
			LMMLogger.error( "Unable to wait for process", ie );			
		}


		BufferedReader in = new BufferedReader( new  InputStreamReader(p.getInputStream()) );
		BufferedReader errin = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String s = null;
		try { 
			while ( (s = in.readLine()) != null)
				LMMLogger.info( "  " + s );
	
			String errtext = null;
			s = null;
			while((s = in.readLine())!= null)
				errtext += s;
			
			if( errtext != null )
				LMMLogger.info( "  err: " + errtext );

		}
		catch( IOException ioe ) {
			LMMLogger.error( "Not good for a proc", ioe );
		
		}
		
	}
}
