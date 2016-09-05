package com.lmm.msg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import javax.swing.ImageIcon;
import com.lmm.sched.proc.LMMUtils;

import messageit.message.Message;

public final class MsgUtils {
	
	private static final ImageIcon IMG_NONE = new ImageIcon(MsgUtils.class.getResource("/status_none.gif"));
	private static final ImageIcon IMG_OK = new ImageIcon(MsgUtils.class.getResource("/status_ok.gif"));
	private static final ImageIcon IMG_ALERT = new ImageIcon(MsgUtils.class.getResource("/status_alert.gif"));
	private static final ImageIcon IMG_DOWN = new ImageIcon(MsgUtils.class.getResource("/status_critical.gif"));
	private static final ImageIcon IMG_UPGRADING = new ImageIcon(MsgUtils.class.getResource("/status_upgrade.gif"));
	

	public enum Statuses {
		Idle ("Any player that has not made contact, including dorment players.", IMG_NONE),
		Upgrading ("Player is currently downloading the latest version of software from the server.", IMG_UPGRADING),
		Healthy ("Received a message within the last "
				+ (NONE_UPDATED_DURATION/60/1000) + " minutes.", IMG_OK),
		Questionable ("Received a message between "
				+ (NONE_UPDATED_DURATION/60/1000) + " and " + (DOWN_DURATION/60/1000) + " minutes ago.", IMG_ALERT),
		Down ("No message received within the last "
				+ (DOWN_DURATION/60/1000) + " minutes or more.", IMG_DOWN);
		
		final String desc;
		final ImageIcon imgIcon;
		private Statuses( String desc, ImageIcon imgIcon ) {
			this.desc = this.name() + ": " + desc;
			this.imgIcon = imgIcon;
		}
		
		public String getDescription() {
			return desc;
		}
		
		public ImageIcon getImageIcon() {
			return imgIcon;
		}
	}

	public static final long NONE_UPDATED_DURATION = LMMUtils.getMsgQuestionableMins()*60*1000;
	public static final long DOWN_DURATION = LMMUtils.getMsgDownMins()*60*1000;
	
	public static final long START_MILLIS = System.currentTimeMillis();
	public static boolean isStarting = true;

	/**
	 * Sets the status of the given message based on the current time
	 * @param msg
	 */
	public static void updateState( ClientStateMsg msg ) {
		
		if( msg == null ) return;

		if( msg.isIdle() ) {
			msg.setStatus( MsgUtils.Statuses.Idle );
		}
		else {
			long diff = System.currentTimeMillis() - msg.getMsgDate().getTime();

			if( isStartingUp() && diff >= MsgUtils.NONE_UPDATED_DURATION )
				msg.setStatus( MsgUtils.Statuses.Idle ); //just started & no update msg
			else if( diff >= MsgUtils.DOWN_DURATION )
				msg.setStatus( MsgUtils.Statuses.Down );
			else if( diff >= MsgUtils.NONE_UPDATED_DURATION )
				msg.setStatus( MsgUtils.Statuses.Questionable );
		}

	}

	private static boolean isStartingUp() {
		//do the below calculation every time until it is false
		if( isStarting )
			isStarting = System.currentTimeMillis() <= (START_MILLIS + NONE_UPDATED_DURATION);
		
		return isStarting;
	}

	/**
	 * Returns the total number of recipients
	 * @return recipients count
	 */
	public static int getRecipientCount( String recipients ) {
		if( recipients == null )
			return 0;
		else
			return recipients.split(Message.RECIPIENT_DELIM).length;
	}

	/**
	 * Returns a DELIMITED string of all the given names
	 * @param names
	 * @return
	 */
	public static String getClientNames( String[] names ) {

		if( names == null || names.length <= 0 )
			return null;

		StringBuffer clientIDs = new StringBuffer(32);
		for( int i = 0; i < names.length; i++ )
			clientIDs.append( names[i] + Message.RECIPIENT_DELIM );
		
		return clientIDs.toString();
	}
	
	/**
	 * Returns the last name in this list. If there is only 1 name, that
	 * name is returned.
	 * @return
	 */
	public static String getEndName( String names ) {
		if( names == null )
			return null;

		if( names.indexOf(Message.RECIPIENT_DELIM) > 0  ) {			
			String[] vals = names.split(Message.RECIPIENT_DELIM);
			return vals[vals.length - 1];
		}
		else
			return names;

	}



	/**
	 * Test to see how big messages are once serialized.
	 */
	public static void main( String[] args ){

		Message msg = new Message();

		ClientStateMsg m = new ClientStateMsg();
		//FileMsg msg = new FileMsg();
		//FileListMsg msg = new FileListMsg();

		
		try {		
			msg.setContent( m );

			FileOutputStream fileOut = new FileOutputStream( new File("c:/msg.txt") );
			ObjectOutputStream oos = new ObjectOutputStream( fileOut );
			
			oos.writeObject( msg );
			oos.flush();
			
			oos.close();
		}
		catch( Exception e ){}
	}

}