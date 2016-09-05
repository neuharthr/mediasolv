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


/** The MessageListener interface must be implemented by objects willing
 * to receive messages asynchronously and be notified of serious dispatcher exceptions. 
 * Objects implementing this interface must register at a messageit.client.Client object
 * though its setMessageListener method to receive messages. 
 * @author Luca Cristina
 */
public interface MessageListener {
	/** This method is called by the Client object(s) to which this listener is associated
	 * whenever a message arrives. 
	 * @param message the received Message
	 */
	public void messageReceived(Message message);
	public void messageLocalReceived( LocalMsg message);
	
	public void exceptionRaised(Exception ex);
}
