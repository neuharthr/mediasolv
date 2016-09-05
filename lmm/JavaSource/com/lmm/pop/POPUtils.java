package com.lmm.pop;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;

import com.lmm.sched.proc.LMMUtils;
import com.lmm.tools.LMMLogger;

public class POPUtils {

	/**
	 * The best way known to match a channel with the graphics device
	 * is by the order in which it is returned ge.getScreenDevices()[]
	 * This could be a problem when multi channel systems are deployed.
	 *
	 * gd.getIDstring() does return a unique string for ID purposes,
	 * but, it does not line up with the channel of the graphics card.
	 * 
	 * @param channel
	 * 			an int in the inclusive range of 0 to 3
	 */
	public static void captureVideoScreen( int channel, String fileName ) {
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		final int currDay = new GregorianCalendar().get(GregorianCalendar.DAY_OF_MONTH);

		int pos = 0;
		for (int i = 0; i < gs.length; i++) { 
			GraphicsDevice gd = gs[i];

			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			Rectangle gcBounds = gc.getBounds();

			if( gcBounds.x == 0 && gcBounds.y == 0 )
				continue;	//ignore the computer monitor

			if( pos == channel ) {
				captureScreen(
					gcBounds,
					LMMUtils.getDataDir() +
						currDay + "day_" + fileName + ".jpg");

				break;
			}
			
			pos++;
		}
			
	}

	private static void captureScreen( final Rectangle gcBounds, final String fileName ) {
		
		Rectangle rectArea = new Rectangle(
			gcBounds.x, gcBounds.y,
			(int)gcBounds.getWidth(),
			(int)gcBounds.getHeight() );
		

		Robot robot = null;
		BufferedImage image = null;
		try {
			robot = new Robot();
			robot.setAutoDelay(0);
			robot.setAutoWaitForIdle(false);

			Toolkit.getDefaultToolkit().sync();
			image = robot.createScreenCapture(rectArea);
		}
		catch( AWTException awtExec ) {
			LMMLogger.error("Unable to get a screen shot", awtExec);
			robot = null;
		}

	     // Save as JPEG
		if( robot != null && image != null ) {
		     File file = new File(fileName);
		     try {
		    	 ImageIO.write(image, "jpg", file);	//or png
		    	 LMMLogger.info("Screen capture complete for " + fileName );
		     }
		     catch( IOException ioe ) {
				LMMLogger.error("Unable create screen shot file", ioe);
				
				if( file != null && file.exists() )
					file.delete();			    	 
		     }
		}

	}
}
