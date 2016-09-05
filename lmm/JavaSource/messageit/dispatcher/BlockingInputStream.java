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

 * Created on 26-sep-2005
 * 21-oct-2005: added copyright notice
 */
package messageit.dispatcher;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/** A subclass of InputStream to read in blocking mode from a ReadableByteChannel, configured in blocking or non-blocking mode. 
 * This is essentially a wrapper to avoid IllegalBlockingModeExceptions
 * @author Luca Cristina
 */
public class BlockingInputStream extends InputStream {
	private ReadableByteChannel in;
	private static final int BUFSIZE=10000;
	private ByteBuffer buf;
	/** Creates the stream and prepares it to read from channel in
	 * @param in the source channel
	 */
	public BlockingInputStream(ReadableByteChannel in) {
		this.in=in;
		buf=ByteBuffer.allocate(BUFSIZE);
	}
	public int read() throws IOException{
		int ct;
		ByteBuffer b2=ByteBuffer.allocate(1);
		b2.clear();
		ct=in.read(b2);
		if(ct==0)return -1;
		b2.flip();
		return b2.get();
	}
	public int read(byte[] b)throws IOException{return read(b,0,b.length);}
	public int read(byte[] b, int off, int len) throws IOException{
		int ct=0;
		buf.clear();
		buf.limit(len);
		ct=in.read(buf);
		buf.flip();
		buf.get(b,off,ct);
		return ct;
	}
	
/*
	public boolean read() {

		int numBytes = 0;
		for (int i = 0; ; i++)
		{
			if (endpoint.directWritesEnabled())
			{
				numBytes = channel.read (inBuffer);
			}
			else
			{
				synchronized (channel)
				{
					numBytes = channel.read (inBuffer);
				}
			}

			if (numBytes < 0)
			{
				endpoint.close (EIOReasonCode.DISCONNECTION);
				return (false);
			}
			else if (!inBuffer.hasRemaining())
			{
				return (true);
			}
			if (!endpoint.isBlocking() && i >= 2)
			{
				break;
			}
		}
		return (!inBuffer.hasRemaining());
		}		
	}
*/
}