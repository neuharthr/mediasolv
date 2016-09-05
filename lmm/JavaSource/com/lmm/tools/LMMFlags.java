package com.lmm.tools;


public class LMMFlags {

	//the player does not output video 
	public static final int SUPRESS_PLAYER = 0x00000001;
	
	//prohibit any external processes from executing
	public static final int SUPRESS_EXT_PROC_PLAYER = 0x00000002;
	
	//view only of the dashboard, no operations allowed
	public static final int DASH_VIEW_ONLY = 0x00000004;


	public static final int SYSTEM_DISABLED = 0x80000000;



	public static boolean isPlayerSuppressed( int flag ) {
		return (flag & SUPRESS_PLAYER) != 0;
	}

	public static boolean isExtPlayerProcSuppressed( int flag ) {
		return (flag & SUPRESS_EXT_PROC_PLAYER) != 0;
	}

	public static boolean isDashViewOnly( int flag ) {
		return (flag & DASH_VIEW_ONLY) != 0;
	}

	public static boolean isSystemDisabled( int flag ) {
		return (flag & SYSTEM_DISABLED) != 0;
	}

}