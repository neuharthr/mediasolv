package com.lmm.systray;

import snoozesoft.systray4j.SysTrayMenuIcon;

/**
 * A place to define constants for the systray project
 *
 */
public interface SystrayDefines {

	//indexes to the icons array that have meaning
	public static final int ICO_DISCON = 0;
	public static final int ICO_CONN = 1;
	public static final int ICO_ANIME_START = 2;

	//this assumes the bat file is in our current directory
	public static final String EXEC_DASHBOARD = "dashboard.bat";
	public static final String EXEC_CMD = "cmd /c start /B /D";


	// create icons
	// the extension can be omitted for icons
	public static final SysTrayMenuIcon[] ALL_ICONS =  {
		//default state
		new SysTrayMenuIcon( 
		        SystrayDefines.class.getResource("/logo_single_ex" + SysTrayMenuIcon.getExtension()) ),
	
		new SysTrayMenuIcon( 
				SystrayDefines.class.getResource("/logo_single" + SysTrayMenuIcon.getExtension()) ),
	
	
		//all animated icons go below here
		new SysTrayMenuIcon( 
				SystrayDefines.class.getResource("/logo_single" + SysTrayMenuIcon.getExtension()) ),
	
		new SysTrayMenuIcon(
				SystrayDefines.class.getResource("/logo_single_rev" + SysTrayMenuIcon.getExtension()) ),
	};

}
