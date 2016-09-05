package com.lmm.gui;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class UIUtils {

	public static final URL LMM_LOGO_GIF = UIUtils.class.getResource("/logo_single.gif");
	public static final String CRLF = System.getProperties().getProperty("line.separator");
	public static final String FILE_SEP = System.getProperties().getProperty("file.separator");

	/**
	 * Shows a quick popup of the update results
	 */
	public static void showUpdateUI() {
		final JFrame f = new JFrame("Update Applied");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setState( JFrame.ICONIFIED );
		
		JOptionPane.showMessageDialog(
			f,
			"Updates have been applied successfully, you will need to restart the application",
			"MediaSOLV updates have been applied",
			JOptionPane.OK_OPTION,
			new ImageIcon(LMM_LOGO_GIF) );

		f.dispose();
	}


}
