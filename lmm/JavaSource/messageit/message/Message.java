/*

    MessageIT - Italian messaging middleware
    Copyright (C) 2005  Luca Cristina <lcristina at sourceforge dot net>

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

 * Created on 20-sep-2005
 * 21-oct-2005: added copyright notice
 */
package messageit.message;

import java.io.*;

import messageit.MessageITProtocolConstants;

/** A class representing a message. 
 * A message is composed of a set of fields: sender, recipient, topic, header and data. 
 * The sender, recipient and topic are Strings. At least one between recipient and topic must be set 
 * for the message to be sent. The header can be used to add arbitrary key/value pairs describing the message, 
 * according to the application's needs. The data can be an array of bytes or an arbitrary Java object 
 * implementing the java.io.Serializable interface. If this is the case, the class of the object must be known
 * to the sender and recipients, or exceptions will occur. 
 * Important note: it is strongly recommended to use Message instances only once, and instantiating a new one. 
 * This is because serialization of the same object multiple times o the same stream results in a reference
 * to the first, regardless of changes. As a good practice, always use new instances. 
 * @author Luca Cristina
 */
public class Message implements Serializable, MessageITProtocolConstants {
    private String sender, topic;
	private String recipient; //delimited field with 1 to many recipients
    private byte[] content = new byte[0];
    private byte status = STATUS_UNKNOWN; //2^8 max states
	private byte priority = PRIORITY_NORMAL; //2^8 max priorities
    
	static final long serialVersionUID = 100000L;

    /**Sets up the message*/
    public Message() {
        this( STATUS_UNKNOWN ) ;//clear();
    }

	public Message( byte aStatus ) {
		super() ;
		setStatus( aStatus );
	}

    /**Clears all the message's fields*/
    public void clear() {
        sender = null;
        recipient = null;
        topic = null;
        content = null;
    }
    /** Sets the message's sender
     * @param sender the new sender (can be null)
     */
    public void setSender(String sender) {
        this.sender = sender;
    }
    /** Returns the message's sender
     * @return the sender
     */
    public String getSender() {
        return sender;
    }
    /** Sets the message's recipient
     * @param recipient the new recipient (can be null)
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    /** Returns the message's recipient
     * @return the sender
     */
    public String getRecipient() {
        return recipient;
    }
	/** Returns all recipients
	 * @return all recipients
	 */
	public String[] getAllRecipients() {
		if( getRecipient() == null )
			return new String[0];
		else
			return getRecipient().split( Message.RECIPIENT_DELIM );
	}
    /** Sets the message's topic
     * @param topic the new topic (can be null)
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }
    /** Returns the message's topic
     * @return the sender
     */
    public String getTopic() {
        return topic;
    }
    
    /** Sets the message's content to the specified object, which must implement java.io.Serializable. 
     * @param content the object
     * @throws java.io.IOException if an I/O error occurs while serializing the object, or if it is not serializable
     */
    public void setContent(Object content) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(content);
		oos.flush();
        setContentAsBytes(baos.toByteArray());
    }
    /** Sets the message's content to the specified byte array. 
     * @param content the byte array
     */
    public void setContentAsBytes(byte[] content) {
        this.content = content;
    }
    /** Returns the message's content as an Object. 
     * @return the message's content
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the object is an instance of a class not known to the JVM
     */
    public Object getContent() throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais =
            new ByteArrayInputStream(getContentAsBytes());
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }
    /** Returns the message's content as a byte array. 
     * @return the message's content
     */
    public byte[] getContentAsBytes() {
        return content;
    }


    /**
     * @return
     */
    public byte getStatus() {
        return status;
    }

    /**
     * @param b
     */
    public void setStatus(byte b) {
        status = b;
    }


	public void readObject( ObjectInputStream oi  ) throws IOException, ClassNotFoundException {
		oi.defaultReadObject();
	}

	public void writeObject( ObjectOutputStream oo  ) throws IOException {
		oo.defaultWriteObject();
	}

    /**
     * @return
     */
    public byte getPriority() {
        return priority;
    }

    /**
     * @param b
     */
    public void setPriority(byte b) {
        priority = b;
    }

}
