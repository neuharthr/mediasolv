package com.lmm.tools;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Date;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.lmm.sched.proc.LMMUtils;


public class SendMail
{
	private String mailHost;
	private String mailFrom;
	private String mailTo;
	private String mailPassword;
	private String execStackTrace = null;
	private String subjectMessage = "";
	private String textMessage = "";
	
	private boolean enabled = LMMUtils.isMailAlertEnabled();

	// construct with passed in values for from and to
	public SendMail(String from, String to)
	{
		init();
		mailFrom = from;
		mailTo = to;		
	}

	// construct using property file values for from, to , host
	public SendMail()
	{
		init();
	}

	public SendMail(Exception e)
	{
		init();
		System.out.println("in exception init");
		e.printStackTrace();
		ByteArrayOutputStream newOut = new ByteArrayOutputStream();
		e.printStackTrace( new PrintStream( newOut ) );
		execStackTrace = newOut.toString();	
	}
	
	private void init() {
		mailHost = LMMUtils.getMailHost();
		mailTo = LMMUtils.getMailTo();
		mailFrom = LMMUtils.getMailFrom();
		mailPassword = LMMUtils.getMailPassword();
		
		//add handlers for main MIME types
		//  work around for mismatching activation.jar & javamail.jar files
//		MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
//		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
//		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
//		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
//		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
//		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
//		CommandMap.setDefaultCommandMap(mc);
	}

	// Sends the email message
	public void sendMail( String sendMessage, String subject)
	{
		if( !enabled )
			return;
		
		try {
			// Set the host
			Properties props = new Properties();
			props.put("mail.host", mailHost);
			props.put("mail.smtp.auth", "true"); 
			Session session = Session.getDefaultInstance(
					props,
					new PWordAuthenticator());

			// Construct the message
			Message msg = new MimeMessage(session);
			msg.setFrom( new InternetAddress(mailFrom) );
			msg.setRecipients( Message.RecipientType.TO, InternetAddress.parse(mailTo, false) );
			msg.setSubject( subject );
			if (execStackTrace != null)
				msg.setText( sendMessage + "\n\n" + execStackTrace );
			else
				msg.setText( sendMessage );
			msg.setHeader( "X-Mailer", "LMM_Mail" );
			msg.setSentDate( new Date() );

			// Send the email message
			Transport.send( msg );
		}
		catch ( Exception e ) {
			LMMLogger.error( "An error has occured in sending mail: " + e.toString(), e );
		}
	}

	class PWordAuthenticator extends Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			  return new PasswordAuthentication(mailFrom, mailPassword);
		  }
	}


	/**
	 * Method setTextMessage.
	 * @param string
	 */
	public void setTextMessage(String string) {
		this.textMessage = string;
	}

	/**
	 * Method setSubjectMessage.
	 * @param string
	 */
	public void setSubjectMessage(String string) {
		this.subjectMessage = string;
	}

	/**
	 * Returns the subjectMessage.
	 * @return String
	 */
	public String getSubjectMessage() {
		return subjectMessage;
	}

	/**
	 * Returns the textMessage.
	 * @return String
	 */
	public String getTextMessage() {
		return textMessage;
	}

    /**
     * @return
     */
    public String getMailTo() {
        return mailTo;
    }

    /**
     * @param string
     */
    public void setMailTo(String string) {
        mailTo = string;
    }

    /**
     * @param b
     */
    public void setEnabled(boolean b) {
    	//just in case we always need to go out
        enabled = b;
    }

}