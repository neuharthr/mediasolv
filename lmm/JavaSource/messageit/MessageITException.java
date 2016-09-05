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
package messageit;

/** An Exception regarding MessageIT errors
 * @author Luca Cristina
 */
public class MessageITException extends Exception {
	/**
	 * @see java.lang.Exception
	 */
	public MessageITException() {
		super();
	}
	/**
	 * @see java.lang.Exception
	 * @param message the exception's message
	 */
	public MessageITException(String message) {
		super(message);
	}
	/**
	 * @see java.lang.Exception
	 * @param cause the Throwable which caused the exception
	 */
	public MessageITException(Throwable cause) {
		super(cause);
	}
	/**
	 * @see java.lang.Exception
	 * @param message the exception's message
	 * @param cause the Throwable which caused the exception
	 */
	public MessageITException(String message, Throwable cause) {
		super(message, cause);
	}
}
