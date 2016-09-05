package com.lmm.systray;

import snoozesoft.systray4j.SysTrayMenu;

/**
 * Flashes or cycles through a set of images
 * 
 */
public class SystrayFlasher implements Runnable {
    private SysTrayMenu sysTrayMenu = null;

    /**
     * Builds the flasher action
     */
    public SystrayFlasher(SysTrayMenu sysTray) {
        super();

        sysTrayMenu = sysTray;
    }

    public void run() {
        try {
	        while (true) {
	            for( int i = SystrayDefines.ICO_ANIME_START; i < SystrayDefines.ALL_ICONS.length; i++) {
	                sysTrayMenu.setIcon(SystrayDefines.ALL_ICONS[i]);
	
	                Thread.sleep(1200);
	            }

            }
        }
        catch (Exception e) {
        }
        finally {
        	//set this back to normal only if we are not disconnected
        	if( sysTrayMenu.getIcon() != SystrayDefines.ALL_ICONS[SystrayDefines.ICO_DISCON] )
	            sysTrayMenu.setIcon(
	                SystrayDefines.ALL_ICONS[SystrayDefines.ICO_CONN]);
        }

    }

}
