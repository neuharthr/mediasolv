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

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
/** A subclass of OutputStream to write in blocking mode to a WritableByteChannel, configured in blocking or non-blocking mode. 
 * This is essentially a wrapper to avoid IllegalBlockingModeExceptions
 * @author Luca Cristina
 */
public class BlockingOutputStream extends OutputStream{
	private WritableByteChannel out;
	private static final int BUFSIZE=10000;
	private ByteBuffer buf;
	/** Creates the stream and prepares it to write to channel in
	 * @param out the output channel
	 */
	public BlockingOutputStream(WritableByteChannel out){
		this.out=out;
		buf=ByteBuffer.allocate(BUFSIZE);
	}
	public void write(int i) throws IOException{
		int ct=0;
		ByteBuffer b2=ByteBuffer.allocate(1);
		b2.clear();
		b2.put((byte)i);
		b2.flip();
		while(ct<1)ct+=out.write(b2);
	}
	public void write(byte[] b)throws IOException{
		write(b,0,b.length);
	}
	public void write(byte[] b, int off, int len) throws IOException{
		int ct=0;
		buf.clear();
		buf.limit(len);
		buf.put(b,off,len);
		buf.flip();
		while(ct<len)ct+=out.write(buf);
	}
}
