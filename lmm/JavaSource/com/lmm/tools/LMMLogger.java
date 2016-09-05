package com.lmm.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lmm.sched.proc.LMMUtils;


public class LMMLogger
{
	private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy HH:mm:ss.SSS");
	private static boolean showTime = false;
	
	public static void info( String s ) {
		if( isShowTime() )
			System.out.println("[" +
				sdf.format(new Date()) + "] " + s);
		else
			System.out.println( s );
	}

	public static void error( String s, Throwable t ) {
		info( s );
		t.printStackTrace(System.out);
	}

	public static void debug( String s ) {
		if( LMMUtils.isDebugLog() ) {
			if( isShowTime() )
				System.out.println("[" +
					sdf.format(new Date()) + "] " +
					"[DBG] " + s);
			else
				System.out.println( "[DBG] " + s );
		}
	}

	public static void debug( String s, Throwable t ) {
		if( LMMUtils.isDebugLog() ) {
			debug( s );
			t.printStackTrace(System.out);
		}
	}

    /**
     * @return
     */
    public static boolean isShowTime() {
        return showTime;
    }

    /**
     * @param b
     */
    public static void setShowTime(boolean b) {
        showTime = b;
    }

}
