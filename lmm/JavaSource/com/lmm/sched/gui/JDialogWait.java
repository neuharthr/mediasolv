package com.lmm.sched.gui;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;

import com.lmm.tools.LMMLogger;

/**
 * Shows a modal dialog asking the user to wait
 *
 */
public class JDialogWait extends JDialog
{
	private JLabel jLabelMessage = null;
	private JProgressBar jProgressBar = null;


	/**
	 * Constructor for JDialogWait.
	 */
	public JDialogWait( Frame owner_ )
	{
		super( owner_ );
		initialize();
	}


	private void initialize()
	{
		setSize( new Dimension(400, 120) );
		setModal( true );
		setResizable( false );
		setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
		//setUndecorated(true);
		setTitle("Progress");
		getRootPane().setWindowDecorationStyle( JRootPane.INFORMATION_DIALOG );

		getContentPane().setLayout(null);
		getContentPane().add( getJProgressBar() );//, BorderLayout.CENTER );		
		getContentPane().add( getJLabelMessage() );//, BorderLayout.CENTER );		
	}
	
	private JProgressBar getJProgressBar()
	{
		if( jProgressBar == null )
		{
			jProgressBar = new JProgressBar();
			jProgressBar.setString("Performing operation...");
			jProgressBar.setIndeterminate( true );
			jProgressBar.setStringPainted( true );
			
			jProgressBar.setLocation( 20, 20 );

			jProgressBar.setSize( new Dimension(350,25) );
		}
		
		return jProgressBar;
	}

	private JLabel getJLabelMessage()
	{
		if( jLabelMessage == null ) {
			jLabelMessage = new JLabel();
			jLabelMessage.setText("This operation may take a while, please be patient...");
			
			jLabelMessage.setLocation( 20, 40 );
			jLabelMessage.setSize( new Dimension(350,25) );
		}
		
		return jLabelMessage;
	}

	public void showDialog() {		
		setLocation(
			getOwner().getX() + (getOwner().getWidth() - getWidth()) / 2, 
			getOwner().getY() + (getOwner().getHeight() - getHeight()) / 2 );

		//always show this dialog in the middle of the parent
		//super.show();
		try {
			setVisible( true );
		}
		catch( NullPointerException ex ) {
			//TODO: not sure why this is happening, seems like a Sun bug
			//ignoring for now...
			LMMLogger.info("  warn: swing component(" + this.getClass().getName()
					+ ") having an issue : " + ex.getMessage() );
		}

	}

	public void setInDeterminate( boolean val ) {
		getJProgressBar().setIndeterminate( val );
		if( val )
			getJProgressBar().setString( "Performing operation..." );
		else
			getJProgressBar().setString( null );
	}
	
	public void setProgress( int min, int max ) {
		getJProgressBar().setMinimum( min );		
		getJProgressBar().setMaximum( max );
	}

	public void setProgressValue( int val ) {		
		getJProgressBar().setValue( val );	
	}

	public void setMsgProgress( String msg ) {
		getJProgressBar().setString( msg );
	}

	public void setMsgLabel( String msg ) {
		getJLabelMessage().setText( msg );
	}
	
}
