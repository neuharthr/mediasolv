package com.lmm.pop;

import com.lmm.sched.proc.LMMUtils;
import com.lmm.tools.LMMLogger;

public class ScreenCaptureTracker {

	//the next time we need to capture a video screen shot in millis
	private long nextCapture =
		System.currentTimeMillis() + (LMMUtils.getScreenShotIntervalHrs()*60*60*1000);

	//the global ShotInterval value we used to calculate our last time
	//  for a screen shot
	private int lastShotInterval = LMMUtils.getScreenShotIntervalHrs();
	
	//is a screen shot needed
	private boolean isShooting = false;
	
	public static final long NEVER_CAPTURE = Long.MAX_VALUE;;

	
	public ScreenCaptureTracker() {
		super();
	}
	
	public int getLastShotInterval() {
		return lastShotInterval;
	}

	public long getNextCapture() {
		return nextCapture;
	}
	
	private void setNextCapture(long nextCapture, int lastShotInterval) {
		this.nextCapture = nextCapture;
		this.lastShotInterval = lastShotInterval;
	}
	
	public boolean isShooting() {
		return isShooting;
	}
	public void setShooting(boolean isShot) {
		this.isShooting = isShot;
	}
	
	public boolean isCaptureTime() {
		
		boolean ret = false;

		if( LMMUtils.getScreenShotIntervalHrs() <= 0 ) {
			setNextCapture( NEVER_CAPTURE, LMMUtils.getScreenShotIntervalHrs() );
			ret = false;
		}
		else if( System.currentTimeMillis() >= getNextCapture() ) {
			setNextCapture( 
				System.currentTimeMillis() + (LMMUtils.getScreenShotIntervalHrs()*60*60*1000),
				LMMUtils.getScreenShotIntervalHrs() );			

			ret = true;
		}
		

		if( getLastShotInterval() != LMMUtils.getScreenShotIntervalHrs() ) {
			setNextCapture( 
				System.currentTimeMillis() + (LMMUtils.getScreenShotIntervalHrs()*60*60*1000),
				LMMUtils.getScreenShotIntervalHrs() );

			//if the interval changed & the new interval is a > 0, lets do a screen shot now
			if( LMMUtils.getScreenShotIntervalHrs() > 0 )
				ret = true;
		}


		LMMLogger.debug(" ScrShotInterval: " + LMMUtils.getScreenShotIntervalHrs() + "hrs, " +
				"doScreen: " + ret + "  " + 
				"next: " + getNextCapture() + "  (curr time:" + System.currentTimeMillis() + ")");

		return ret;
	}
}
