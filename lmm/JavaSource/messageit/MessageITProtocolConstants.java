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

 * Created on 27-sep-2005
 * 21-oct-2005: added copyright notice
 */
package messageit;

/** Constants used in the communication protocol between the dispatcher and clients
 * @author Luca Cristina
 */
public interface MessageITProtocolConstants {
	public static String MSGIT_CLIENT = "CLIENT_";
	public static String MSGIT_SUBSCRIBE = "SUBSCRIBE_";
	public static String MSGIT_UNSUBSCRIBE = "UNSUBSCRIBE_";
	public static String MSGIT_END = "END_";
	
	public static String RECIPIENT_DELIM = ";";

	public static short CONNSTATUS_DISCONNECTED = 0;
	public static short CONNSTATUS_CONNECTED = 1;
	public static short CONNSTATUS_RECONNECTING = 2;
	
	
	// Message statuses
	public static byte STATUS_ERROR = -20;
	public static byte STATUS_WARNING = -10;
	public static byte STATUS_UNKNOWN = 0;
	public static byte STATUS_OK = 10;
	public static byte STATUS_FROM_MASTER = 11;
	public static byte STATUS_FROM_SELF = 12;
	public static byte STATUS_MSG_PROGRESS = 20;
	public static byte STATUS_MSG_COMPLETE = 30;

	// Message priorities
	public static byte PRIORITY_LOW = -10;
	public static byte PRIORITY_NORMAL = 0;
	public static byte PRIORITY_HIGH = 10;
	public static byte PRIORITY_ALERT = 20;



	//socket encryption settings (i.e. SSL)
	public static boolean IS_SSL = true;
	public static String[] CIPHER_SUITES = {
		"SSL_DH_anon_WITH_RC4_128_MD5", "TLS_DH_anon_WITH_AES_128_CBC_SHA" };
	
}