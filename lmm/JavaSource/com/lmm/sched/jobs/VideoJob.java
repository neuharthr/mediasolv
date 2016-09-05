package com.lmm.sched.jobs;

import java.util.List;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.SimpleTrigger;

import com.lmm.msg.MsgUtils;
import com.lmm.pop.POPDailyMetric;
import com.lmm.pop.POPMetricManager;
import com.lmm.pop.POPUtils;
import com.lmm.pop.ScreenCaptureTracker;
import com.lmm.sched.data.LMMEntry;
import com.lmm.sched.data.VideoEntry;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.sched.proc.PlayOrder;
import com.lmm.tools.HTTPChecker;
import com.lmm.tools.LMMLogger;
import com.lmm.tools.ProcessStarter;

/**
 * Cron help:
 * 
 *    http://www.opensymphony.com/quartz/tutorial.html#cronTriggers
 */
public class VideoJob implements Job
{
	//used to spawn processes
	private ProcessStarter procStarter = new ProcessStarter();
	
	//screen shot tracking
	final static ScreenCaptureTracker SCRN_TRAKR = new ScreenCaptureTracker();


	public static final String LMM_ENTRY = "lmm_entry";
	public static final String PLAYORDER_MAP = "lmm_playorder_map";
	
	public VideoJob() {
		super();
	}

    public void execute(JobExecutionContext context)
    {
		JobDataMap map = context.getJobDetail().getJobDataMap();		

		if( map.get(PLAYORDER_MAP) != null ) {

			SimpleTrigger trigger = (SimpleTrigger)context.getTrigger();
			LMMLogger.info("  (start)Current repeat interval for SIMPLE: " + trigger.getRepeatInterval() );
			
			PlayOrder pOrder = (PlayOrder)map.get(PLAYORDER_MAP);
			processPlayOrder( pOrder );			
		}
		else    	
			processVideo( (LMMEntry)map.get(LMM_ENTRY) );
    }

	/**
	 * Processes our linear play list
	 * 
	 */
	private void processPlayOrder( PlayOrder pOrder ) {
		
		pOrder.getCurrentState().setStatus( MsgUtils.Statuses.Healthy );
		
		final List entryList = pOrder.getCurrOrderedList();
		SCRN_TRAKR.setShooting( false );

		for( int i = 0; i < entryList.size(); i++ ) {
			
			final LMMEntry entry = (LMMEntry)entryList.get(i);
			
			processVideo( entry );
			
			pOrder.getCurrentState().setCurrentPlay( entry.getFileNameNoExt() );
			pOrder.getCurrentState().setCurrentPlaySlot( i+1 );
			
			//force us to start screen shots only when we are
			// at the beginning of a reel
			if( i == 0 && SCRN_TRAKR.isCaptureTime() ) {
				SCRN_TRAKR.setShooting( true );
				processScreenCapture( entry );
			}
			else if( SCRN_TRAKR.isShooting() ) {
				//if our first screen was shot, do all remaining screens
				processScreenCapture(entry);
			}


			try {
				Thread.sleep( entry.getPlayDuration().intValue() );			
			} catch( InterruptedException ie ) {
				LMMLogger.error( "Error waiting for PlayOrder entry to finish", ie );
			}
		}

	}
	
	private void processScreenCapture( final LMMEntry entry ) {

		//run the screen shot
		new Thread( new Runnable() {
			public void run() {
				try {
					//try to 'best guess' a good time to snapshot the image
					Thread.sleep(
						(long)(entry.getPlayDuration().intValue() / 2) );

				} catch( Exception e ) {
					LMMLogger.error("Unable to sleep for our screen shot", e);
				}

				POPUtils.captureVideoScreen(
						entry.getChannel(),
						entry.getFileNameNoExt() );
			}
		}, "LMMScreenShot").start();

	}

	/**
	 * Plays the actual video themes and checks any needed web
	 * URLs before running any web themes.
	 * 
	 */	
	private void processVideo( LMMEntry entry ) {

		if( entry instanceof VideoEntry  )
		{
			VideoEntry videoEntry = (VideoEntry)entry;

			if( videoEntry.getUrl() == null ) {
				procStarter.startPlayer( 
					LMMUtils.getVideoDir() + videoEntry.getFileName() );

				LMMLogger.info("Starting: " +
					LMMUtils.getVideoDir() + videoEntry.getFileName());
			}
			else if( HTTPChecker.isGoodHTTP(videoEntry.getUrl()) ) {
				procStarter.startPlayer( 
					LMMUtils.getVideoDir() + videoEntry.getFileName() );

				LMMLogger.info("Starting Web: " +
					LMMUtils.getVideoDir() + videoEntry.getFileName());
			}
			else {
				procStarter.startPlayer(
					LMMUtils.getVideoDir() + LMMUtils.getDefaultTheme() );

				LMMLogger.info("Unable to load web page, attempting to play default theme: " +
					LMMUtils.getVideoDir() + LMMUtils.getDefaultTheme());
			}


			updatePOPMetrics( videoEntry );
		}
	}

	private void updatePOPMetrics( final VideoEntry ve ) {
		if( !LMMUtils.isPOPEnabled() )
			return;

		POPDailyMetric popDaily =
			POPMetricManager.getInstance().getTodaysMetric( ve.getFileNameNoExt() );
	
		synchronized( popDaily ) {
			//update all metrics on our POP instance for today
			popDaily.incrementTotalPlays();
			popDaily.addPlayTime( System.currentTimeMillis() );
			popDaily.addSeconds( ve.getPlayDuration() / 1000 );
		}

	}
}
