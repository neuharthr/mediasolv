package com.lmm.tools;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.lmm.tools.*;


public class HTTPChecker
{

	public static boolean isGoodHTTP( String urlStr )
	{
		int code = 0;
		try {
			code = makeRequest( urlStr );
		}
		catch (IOException e) { LMMLogger.error("Unable to get URL: " + urlStr, e ); }

		return code == HttpURLConnection.HTTP_OK; 
	}

	private static int makeRequest( String urlStr ) throws IOException
	{
		URL u = new URL(urlStr);
		HttpURLConnection huc = (HttpURLConnection)u.openConnection();
		huc.setRequestMethod("GET");
		huc.connect();
		int code = huc.getResponseCode();
		huc.disconnect();

		return code;
	}
	
}
