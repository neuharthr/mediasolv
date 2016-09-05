package com.lmm.tools;

import java.io.*;
import java.net.*;

public class Curl
{

	private static String defaultServer = "http://www.google.com";
	private String server;

	public Curl(String _server)
	{
		server = defaultServer;
		server = _server;
		if(server.length() >= 1)
		{
			server = server.startsWith("http") ? server : "http://" + server;
		}
	}

	public static void main(String args[])
	{
		if(args.length <= 0)
		{
			System.out.println("Usage: curl [url]");
			return;
		}
		String server = defaultServer;
		Curl curl = new Curl(server);
		try
		{
			curl.executeGET();
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}

	public String executeGET() throws MalformedURLException, IOException, ProtocolException
	{
		URL u = new URL(server);
		java.net.URLConnection uc = u.openConnection();

		HttpURLConnection conn = (HttpURLConnection)uc;
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("GET");

		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		char buff[] = new char[1024];
		StringBuffer strFull = new StringBuffer();
		do
		{
			int bytes = reader.read(buff, 0, 1024);
			if(bytes != -1)
			{
				String str = new String(buff, 0, bytes);
				strFull.append(str);
			}
			else
			{
//				System.out.println(strFull.toString());
//				System.out.println("----------------------------");
//				System.out.println("CURL = " + server);
//				System.out.println("CODE = " + conn.getResponseCode());
				reader.close();
				conn.disconnect();
				return strFull.toString();
			}
		} while(true);
	}

}
