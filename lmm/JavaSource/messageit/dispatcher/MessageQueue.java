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

 * Created on 21-sep-2005
 * 21-oct-2005: added copyright notice
 */
package messageit.dispatcher;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BufferUtils;
import org.apache.commons.collections.UnboundedFifoBuffer;

import messageit.message.Message;

/** A class representing a queue of messages, accessible with a FIFO (first in fist out) logic. 
 * @author Luca Cristina
 */
public class MessageQueue {
	//these queues stay fairly small all the time
	private Buffer qNorm;
	private Buffer qHigh;

	/** Creates an empty MessageQueue */
	public MessageQueue(){
		qNorm = BufferUtils.synchronizedBuffer(
				new UnboundedFifoBuffer(32) );

		qHigh = BufferUtils.synchronizedBuffer(
				new UnboundedFifoBuffer(16) );
	}

	/** Returns the next message in the queue, 
	 * @return the next message, or null if queue is empty
	 */
	public Message extract(){
		if( !qHigh.isEmpty() )
			return (Message)qHigh.remove();
		else
			return (Message)qNorm.remove();
	}
	/** Inserts a new message in the queue
	 * @param m the message to be added
	 */
	public void insert(Message m){
		if( m.getPriority() == Message.PRIORITY_HIGH )
			qHigh.add(m);
		else
			qNorm.add(m);
	}
	/** Checks whether the queue is empty
	 * @return true if and only if the queue is empty
	 */
	public boolean isEmpty(){
		return qHigh.isEmpty() && qNorm.isEmpty();
	}

}
