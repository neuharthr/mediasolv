package com.lmm.sched.data;

import java.io.FileWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import com.lmm.client.FTPHandler;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.tools.Curl;
import com.lmm.tools.LMMLogger;
import com.lmm.tools.Time;

public class IPUpdater
{
	private String[] internalsIPs = null;
	private static final String CR = System.getProperty("line.separator");	
	

	public IPUpdater() {
		super();
	}

	public void execute()
	{
		String currIP = getCurrentIP();
		try {

			writeNewIP( currIP );

			FTPHandler ftpHand = new FTPHandler( LMMUtils.getFtpFilename() );
			ftpHand.executePutFile();

			//lastIP = currIP;
		}
		catch(Exception ex) {
			LMMLogger.error( "Exception caught during IP Update process", ex);
		}
	}
	
	private String[] getInternalIPs()
	{		
		if( internalsIPs == null ) {

			try {
				Enumeration e = NetworkInterface.getNetworkInterfaces();		
				Vector retVals = new Vector(8);
				while( e.hasMoreElements() )
				{
					NetworkInterface n = (NetworkInterface)e.nextElement();
					Enumeration e2 = n.getInetAddresses();
					while( e2.hasMoreElements() ) {
						InetAddress ip = (InetAddress)e2.nextElement();
						retVals.add( ip.toString() );
					}
				}

				internalsIPs = (String[])retVals.toArray( new String[retVals.size()] );
			}
			catch( Exception ex ) {
				LMMLogger.error( "Unable to get Internal IPs", ex );
				internalsIPs = new String[0];
			}
		}
		
		return internalsIPs;
	}

	private void writeNewIP(String newIP) throws Exception
	{
		FileWriter fw = new FileWriter(LMMUtils.getFtpFilename(), false);
		fw.write("Time of Update:  " + new Date() + CR);
		fw.write("Total runtime: " + 
			Time.getDescription(System.currentTimeMillis() - LMMUtils.getStartTime().getTime()) + CR);

		fw.write(" External IP: " + newIP + CR);
		
		for( int i = 0; i < getInternalIPs().length; i++ )
			fw.write( " Internal IP: " + getInternalIPs()[i] + CR );


		fw.close();
	}

	public String getCurrentIP()
	{
		String str = "(Unknown IP)";
		if( LMMUtils.getIpDiscoveryUrl() == null )
			return str;

		try {
			Curl curl = new Curl(LMMUtils.getIpDiscoveryUrl());
			str = curl.executeGET();
			
			try {
				//force a parsing of the IP address to make sure it is valid
				InetAddress.getByName( str );
			} catch( Exception ex ) {
				LMMLogger.info( "Received a non-IPAddress string, using local: " + str );
				str = InetAddress.getLocalHost().getHostAddress() + "  (internal)";
			}

		}
		catch(Exception e) {
			LMMLogger.error("Unable to get IP address", e);
		}

		return str;
	}


}

