package com.lmm.tools;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.GregorianCalendar;

import com.lmm.sched.proc.LMMUtils;

/**
 * Class to start a seperate process
 * 
 */
public class ProcessStarter {
	public static final String EXEC_SHUTDOWN = "shutdown.exe";
	public static final String[] EXEC_SHUTDOWN_PARAMS = { "-r", "-f", "-t", "5" };
	public static final String EXEC_EXPLORER = "explorer.exe /select,";
	public static final String EXEC_FTP = "ftp.exe";

	public static Integer xCoord = 0;
	public static Integer yCoord = 0;
	
	private static final String[] PLAYER_PARAMS = {
		"--intf", "dummy",
		"--one-instance",
		"--loop",
//		"--playlist-enqueue",
//		"--video-x", xCoord.toString(),
//		"--video-y", yCoord.toString(),
		"--fullscreen"
	};
//	NOTE:  reel transition is flashy.....fix



	static {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		for (int i = 0; i < gs.length; i++) { 
			GraphicsDevice gd = gs[i];

			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			Rectangle gcBounds = gc.getBounds();

			if( gcBounds.x == 0 && gcBounds.y == 0 )
				continue;	//ignore the computer monitor
			
			//for now, assume the screen we are playing on is the second monitor
			xCoord = new Integer(gcBounds.x);
			yCoord = new Integer(gcBounds.y);
			break;
		}
	}
	
	/**
	 * Starts the Enseo player application.
	 * 
	 * @param fileName
	 * @param channel
	 * @deprecated Enseo player is no longer used, use the startPlayer method
	 */
	public void startPlayer(String fileName) {
		// c:\progs\OnTheAir.exe -play0 c:\lmm\lmm_video.xml
		if (!LMMFlags.isExtPlayerProcSuppressed(LMMUtils.getAppFlags()))
			startProcess(LMMUtils.getOnTheAir(), new String[] { "-play"
					+ 0 }, fileName);
	}

	/**
	 * Starts the current Player application.
	 * 
	 * @param fileName
	 */
	public void startNewPlayer(String fileName) {
		if (!LMMFlags.isExtPlayerProcSuppressed(LMMUtils.getAppFlags()))
			startProcess(LMMUtils.getPlayer(), PLAYER_PARAMS, fileName);
	}

	/**
	 * Starts an exteranl process
	 * 
	 * @param exePath
	 * @param exeParams
	 * @param fileName
	 * 
	 * @return Returns a Process instance if the process was started
	 *         successfully, else null is returned.
	 */
	public Process startProcess(String exePath, String[] exeParams,
			String fileName) {
		try {
			return Runtime.getRuntime().exec(
					exePath + getStrArray(exeParams)
							+ (fileName == null ? "" : fileName));
		}
		catch (IOException ioe) {
			LMMLogger.error("Unable to start the process: " + exePath, ioe);
		}
		return null;
	}

	private String getStrArray(String[] val) {
		String ret = " ";
		if (val != null) {
			for (int i = 0; i < val.length; i++)
				ret += val[i] + " ";
		}

		return ret;
	}
}