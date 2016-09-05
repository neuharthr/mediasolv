package com.lmm.sched.proc;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import messageit.dispatcher.Dispatcher;

import com.lmm.db.DBCommon;
import com.lmm.sched.data.VideoEntry;
import com.lmm.tools.LMMLogger;

public class LMMUtils
{
	//update when any code changes
	public static final String VERSION = "2.4.004";
	
	/* *
	 * Only update MSG_VERSION when messaging classes change that could corrupt
	 * the stream, thus leaving the client unupdatable. Things like JAUUS project
	 * changes or changes to the MessageIt project. BaseMsg or CmdMsg changes
	 * will neeed to be ensured checked on as well.
	 * 
	 * !!! NOTICE !!! Once this changes, every client will update as soon
	 * as they connect or reconnect to the server! This could be a huge bottlneck in the
	 * system.
	 **/
	public static final String MSG_VERSION = "1";

	
	public static final String PROP_FILE = "lmm.properties";
	public static String PLAYER_FILE = "player.properties";
	
	public static final String FILE_SEP = System.getProperties().getProperty("file.separator");

	public static Properties lmmProps = null;
	private static final Date startTime = new Date();
	private static VideoEntry[] videoEntries = new VideoEntry[0];

	//when -1 is set for any property, NULL is returned internally
	public static final String INVALID_STRING_VALUE = "-1";
	public static final Integer INVALID_INT_VALUE = new Integer(INVALID_STRING_VALUE);


	//all properties that are specific to a single install
	private static String onTheAir, wrkDir;
	
	
	//all read/write properties
	private static LMMGlobalConfig globalProps = new LMMGlobalConfig();


	static {
		Dispatcher.version = LMMUtils.MSG_VERSION;
		loadLMMProperties( true );
		
		//env properties
		System.setProperty( "jauus_server", getServerHost() );
		System.setProperty( "jauus_port", new Integer(getServerPort() + 1).toString() );  //default 7731
		System.setProperty( "jauus_application", "lmm" ); 
		System.setProperty( "jauus_config-dir", getDataDir() );
		System.setProperty( "jauus_application-dir", "." );
	}

	private static String createDefaultName() {

		//make a unique default name
		String cName = "" + new Date().getTime();
		try {
			cName = InetAddress.getLocalHost().getHostName();
		}
		catch ( Exception e ) {
			LMMLogger.info( "Cannot get local computer name, using a default computerName of " + cName );
		}

		globalProps.setComputerName( cName );
		return globalProps.getComputerName();
	}
	
	private static synchronized InputStream getLMMInputStream( String propFile ) throws IllegalStateException
	{
		try {
			InputStream is = new FileInputStream( propFile );
			//Object.class.getResourceAsStream( propFile );
	
			//if( is == null ) //not in CLASSPATH
			//	throw new IllegalStateException("Unable to find " + propFile + " in classpath");

			return is;
		}
		catch( FileNotFoundException fnf ) {
			System.out.println("ClassPath= " + System.getProperty("java.class.path") );
			throw new IllegalStateException("Unable to find " + propFile + " in classpath");
		}
		
	}

	private static synchronized String[] toStringProps() {
		
		Properties props = new Properties();
		String[] retVals = new String[0];

		try {
			props.load( getLMMInputStream(PROP_FILE) );		
			
			Properties tProps = new Properties();
			tProps.load( getLMMInputStream(PLAYER_FILE) );

			props.putAll( tProps );			
			retVals = new String[ props.size() ];

			Enumeration allKeys = props.keys();
			int i = 0;
			while( allKeys.hasMoreElements() ) {
				String key = allKeys.nextElement().toString();
				String val = props.getProperty( key );
				
				retVals[i++] = key + ": " + val;
			}
			
			Arrays.sort( retVals );
		}
		catch( IOException ie ) {
			LMMLogger.error("Unable to parse the properties into strings", ie );
		}
			
		return retVals;
	}
	
	/**
	 * Sets the all important work directory. On first time executing, it will try to
	 * best guess what it should be.
	 */
	private static void createWorkDir() throws IOException {
		if( lmmProps.get("wrkdir") != null ) {
			wrkDir = lmmProps.get("wrkdir").toString();
		}
		else {
			File currDir = new File( System.getProperty("user.dir") );
			
			//First, try to go up 2 directories
			File upTwo = currDir.getParentFile().getParentFile();			
			List<String> tempFiles = Arrays.asList( upTwo.list() );
			for( String fName : tempFiles) {
				if( fName.equalsIgnoreCase("tools") || fName.equalsIgnoreCase("video") ) {
					wrkDir = upTwo.getCanonicalPath();
					return;
				}				
			}
			
			//Second, search for the LMM directory going up the path
			File par = currDir.getParentFile();
			while( par != null ) {
				if( par.getName().equalsIgnoreCase("LMM") ) {
					wrkDir = par.getCanonicalPath();
					return;
				}
				
				par = par.getParentFile();
			}



			//Lastly, use a command line agument (mainly for development)
			wrkDir = System.getProperty("lmm.wrkdir");			
		}

	}

	private static synchronized void loadValues( boolean writeConfig )
	{
		//load all read-only properties
		if( lmmProps.get("ontheair") != null )
			onTheAir = lmmProps.get("ontheair").toString();		

		try {
			createWorkDir();
		}
		catch( IOException ioe ) {
			LMMLogger.error("Unable to set a valid work directory", ioe);
		}
		

		//properties on every play list entry
		String[] playOrder = getMultiProp("play_order");
		Integer[] playDuration = getMultiPropInteger("play_duration");
		String[] videoStrs = getMultiProp("xml");
		String[] webURLs = getMultiProp("web_url");
		String[] timeWindow = getMultiProp("time_window");
		String[] dayWindow = getMultiProp("day_window");
		String[] channel = getMultiProp("channel");

		videoEntries = new VideoEntry[videoStrs.length];
		for( int i = 0; i < videoEntries.length; i++ ) {

			videoEntries[i] = new VideoEntry(
				i + "",
				videoStrs[i],
				parseProp(webURLs[i]),
				parseIntegers(playOrder[i]),
				(parseProp(playDuration[i]) != null 
					? new Integer(playDuration[i].intValue()*1000) : null),
				parseProp(timeWindow[i]),
				parseProp(dayWindow[i]),
				new Integer(parseProp(channel[i]))
			);

		}



		//load all the writtable props from DB AKA the GlobalConfig
		LMMGlobalConfig storedProps = DBCommon.globalConfig_Retrieve();
		if( storedProps != null )
			globalProps = storedProps;
		

		//configurations here (overrides values in the DB with values in our prop file)
		if( propExists("computer_name") ) {
			globalProps.setComputerName( lmmProps.get("computer_name").toString() );
			globalProps.setComputerName(
					globalProps.getComputerName().replaceFirst( "%millis%", "_" + new Date().getTime() ) );
		}

		//if we still do not have a computerName, create a unique one
		if( globalProps.getComputerName() == null )
			createDefaultName();
		
		if( propExists("server_host") )
			globalProps.setServerHost( lmmProps.get("server_host").toString() );

		if( propExists("kill_cmd") )
			globalProps.setKillCmd( lmmProps.get("kill_cmd").toString() );

		if( propExists("kill_cron_expr") )
			globalProps.setKillCronExpr( lmmProps.get("kill_cron_expr").toString() );

		if( propExists("ftp_url") )
			globalProps.setFtpUrl( lmmProps.get("ftp_url").toString() );
		
		if( propExists("ftp_username") )
			globalProps.setFtpUsername( lmmProps.get("ftp_username").toString() );

		if( propExists("ftp_pword") )
			globalProps.setFtpPword( lmmProps.get("ftp_pword").toString() );
		
		if( propExists("ftp_upload_dir") )
			globalProps.setFtpUploadDir( lmmProps.get("ftp_upload_dir").toString() );

		if( propExists("ip_discover_url") )
			globalProps.setIpDiscoveryUrl( lmmProps.get("ip_discover_url").toString() );

		if( propExists("ip_check_cron_expr") )
			globalProps.setIpCheckCronExpr( lmmProps.get("ip_check_cron_expr").toString() );

		
		if( propExists("mail_host") )
			globalProps.setMailHost( lmmProps.get("mail_host").toString() );

		if( propExists("mail_to") )
			globalProps.setMailTo( lmmProps.get("mail_to").toString() );
		
		if( propExists("mail_from") )
			globalProps.setMailFrom( lmmProps.get("mail_from").toString() );
		
		if( propExists("mail_subject") )
			globalProps.setMailSubject( lmmProps.get("mail_subject").toString() );
		
		if( propExists("mail_password") )
			globalProps.setMailPassword( lmmProps.get("mail_password").toString() );


		if( propExists("default_theme") )
			globalProps.setDefaultTheme( lmmProps.get("default_theme").toString() );

		if( propExists("server_port") )
			globalProps.setServerPort( Integer.parseInt( lmmProps.get("server_port").toString() ) );

		if( propExists("mail_alert_enabled") )
			globalProps.setMailAlertEnabled( Boolean.valueOf(lmmProps.get("mail_alert_enabled").toString() ).booleanValue() );

		if( propExists("app_flags") )
			globalProps.setAppFlags( Integer.decode( lmmProps.get("app_flags").toString() ).intValue() );

		if( propExists("auto_update") )
			globalProps.setAutoUpdate( Boolean.valueOf(lmmProps.get("auto_update").toString()).booleanValue() );		
		
		if( propExists("msg_questionable_minutes") )
			globalProps.setMsgQuestionableMins( Integer.parseInt(lmmProps.get("msg_questionable_minutes").toString()) );

		if( propExists("msg_down_minutes") )
			globalProps.setMsgDownMins( Integer.parseInt(lmmProps.get("msg_down_minutes").toString()) );

		if( propExists("msg_file_chunk_size_mb") )
			globalProps.setMsgFileChunkSizeMB( Integer.parseInt(lmmProps.get("msg_file_chunk_size_mb").toString()) );

		if( propExists("msg_file_timeout_secs") )
			globalProps.setMsgFileTimeoutSecs( Integer.parseInt(lmmProps.get("msg_file_timeout_secs").toString()) );

		if( propExists("log_debug") )
			globalProps.setDebugLog( Boolean.valueOf(lmmProps.get("log_debug").toString()).booleanValue() );

		if( propExists("screen_shot_interval_hrs") )
			globalProps.setScreenShotIntervalHrs( Integer.parseInt(lmmProps.get("screen_shot_interval_hrs").toString()) );

		if( propExists("pop_enabled") )
			globalProps.setPopEnabled( Boolean.valueOf(lmmProps.get("pop_enabled").toString()).booleanValue() );

		

		//just in case our configuration values have changed
		if( writeConfig ) 
			persistGlobalConfig();
	}
	
	public static String parseProp( String propVal ) {
		
		if( INVALID_STRING_VALUE.equalsIgnoreCase(propVal) )
			return null;
		else
			return propVal;
	}

	public static String parseNullProp( String propVal ) {
		
		if( propVal == null || propVal.length() <= 0 )
			return INVALID_STRING_VALUE;
		else
			return propVal;
	}

	public static Integer parseProp( Integer propVal ) {
		
		if( INVALID_INT_VALUE.equals(propVal) )
			return null;
		else
			return propVal;
	}
	
	private static synchronized void loadLMMProperties( boolean isInitialLoad ) 
	{
		String propFile = PROP_FILE;
		try {
			InputStream is = getLMMInputStream( propFile );
			lmmProps = new Properties();
			lmmProps.load(is);
			is.close();

			propFile = PLAYER_FILE;
			InputStream isv = getLMMInputStream( propFile );
			Properties tempProps = new Properties();
			tempProps.load(isv);
			isv.close();
			
			lmmProps.putAll( tempProps );
			loadValues( isInitialLoad );
			
		}
		catch( Exception ie ) {
			LMMLogger.error("Unable to load " + propFile, ie );
		}
		       
	}

	public static synchronized void reloadLMMProperties() {
		loadLMMProperties( false );
	}
	
	/**
	 * Discovers the existance of a property
	 */
	public static synchronized boolean propExists( String propId, Properties props )
	{
		boolean retVal = false;
		if( propId != null
			&& props != null 
			&& props.containsKey(propId) )
		{
			String val = (String)props.get(propId);
			
			retVal = val.trim().length() > 0;
		}

		return retVal;
	}

	/**
	 * Discovers the existance of a property
	 */
	public static synchronized boolean propExists( String propId )
	{
		return propExists( propId, lmmProps );
	}

	public static synchronized String[] getMultiProp( String keyPreString )
	{
		ArrayList tmpList = new ArrayList(16);
		
		String[] keys = new String[ lmmProps.keySet().size() ];
		keys = (String[])lmmProps.keySet().toArray( keys );
		Arrays.sort( keys );
		for( int i = 0; i < keys.length; i++ ) {
			if( keys[i].startsWith(keyPreString) )
				tmpList.add( lmmProps.get(keys[i]) );
		}

		keys = new String[ tmpList.size() ];
		return (String[])tmpList.toArray(keys);
	}

	public static synchronized Integer[] getMultiPropInteger( String keyPreString )
	{
		ArrayList<Integer> tmpList = new ArrayList<Integer>(16);
		
		String[] keys = new String[ lmmProps.keySet().size() ];
		keys = (String[])lmmProps.keySet().toArray( keys );
		Arrays.sort( keys );
		for( int i = 0; i < keys.length; i++ ) {
			if( keys[i].startsWith(keyPreString) )
				tmpList.add( new Integer(lmmProps.get(keys[i]).toString()) );
		}

		Integer[] vals = new Integer[ tmpList.size() ];
		return (Integer[])tmpList.toArray(vals);
	}

	public static synchronized Integer[] parseIntegers( String strInts )
	{
		ArrayList<Integer> tmpList = new ArrayList<Integer>(16);		
		String[] strs = strInts.split(",");
		
		for( int i = 0; i < strs.length; i++ ) {
			tmpList.add( new Integer(strs[i]) );
		}

		Integer[] vals = new Integer[ tmpList.size() ];
		return (Integer[])tmpList.toArray(vals);
	}

	public static synchronized String arrayAsString( Object[] objs, String del )
	{
		StringBuffer sb = new StringBuffer(32);
		if( objs != null ) {
			for( int i = 0; i < objs.length; i++ )
				sb.append( (objs[i] != null ? objs[i].toString() : "") + del );

			if( sb.length() > 0 )
				sb.deleteCharAt( sb.length()-1 );
		}
		
		return sb.toString();
	}

	/**
	 * @return
	 */
	public static VideoEntry getFirstSimple()
	{
		if( LMMUtils.getVideoEntries().length > 0 )
			return LMMUtils.getVideoEntries()[0];

		return null;
	}

    /**
     * @return
     */
    private static String getWrkDir() {
        return wrkDir;
    }

	public static String getVideoDir()
	{
		return LMMUtils.getWrkDir() + FILE_SEP + "video" + FILE_SEP;
	}

	public static String getToolsDir()
	{
		return LMMUtils.getWrkDir() + FILE_SEP + "tools" + FILE_SEP;
	}

	public static String getPlayer()
	{
		//return LMMUtils.getWrkDir() + FILE_SEP + "vlc" + FILE_SEP;
		return "C:/vlc-0.8.6d/vlc.exe";
	}

    /**
     * @return
     */
    public static String getOnTheAir()
    {
        return onTheAir;
    }

    /**
     * @return
     */
    public static VideoEntry[] getVideoEntries()
    {
        return videoEntries;
    }

    /**
     * @return
     */
    public static Date getStartTime()
    {
        return startTime;
    }

    /**
     * @return
     */
    public static String getKillCmd() {
        return globalProps.getKillCmd();
    }

    /**
     * @return
     */
    public static String getKillCronExpr() {
        return globalProps.getKillCronExpr();
    }

    /**
     * @return
     */
    public static String getServerHost() {
        return globalProps.getServerHost();
    }

    /**
     * @return
     */
    public static int getServerPort() {
        return globalProps.getServerPort();
    }

	/**
	 * @return
	 */
	public static String getDefaultTheme()
	{
		return globalProps.getDefaultTheme();
	}
	
    /**
     * @return
     */
    public static boolean isMailAlertEnabled() {
        return globalProps.isMailAlertEnabled();
    }

    /**
     * @return
     */
    public static boolean isDebugLog() {
        return globalProps.isDebugLog();
    }

    /**
     * @return
     */
    public static String getDataDir() {
		return getToolsDir() + "data" + FILE_SEP;
    }

    public static String getAppsDir() {
		return getToolsDir() + "apps" + FILE_SEP;
    }

    public static String getLogsDir() {
		return getToolsDir() + "logs" + FILE_SEP;
    }
    
    /**
     * @return
     */
    public static int getMsgDownMins() {
        return globalProps.getMsgDownMins();
    }

    /**
     * @return
     */
    public static int getMsgQuestionableMins() {
        return globalProps.getMsgQuestionableMins();
    }

    /**
     * @return
     */
    public static int getMsgFileChunkSizeMB() {
        return globalProps.getMsgFileChunkSizeMB();
    }

    /**
     * @return
     */
    public static int getMsgFileTimeoutSecs() {
        return globalProps.getMsgFileTimeoutSecs();
    }

    /**
     * @return
     */
    public static boolean isAutoUpdate() {
        return globalProps.isAutoUpdate();
    }

    /**
     * @return
     */
    public static boolean isPOPEnabled() {
        return globalProps.isPopEnabled();
    }

    /**
     * @return
     */
    public static String getComputerName() {
        return globalProps.getComputerName();
    }

    /**
     * @param string
     */
    public static void setComputerId(String string) {
		globalProps.setComputerId( string );
		persistGlobalConfig();
    }

	public static String getComputerId() {
		return globalProps.getComputerId();
	}

	/**
	 * Writes the current Global Properties to the database
	 */
	public static void persistGlobalConfig() {
		LMMLogger.debug("Persisting Global Configuration locally...");
		DBCommon.globalConfig_Update( globalProps );
	}


    /**
     * @return
     */
    public static int getAppFlags() {
        return globalProps.getAppFlags();
    }

	public static String getMailFrom() {
		return globalProps.getMailFrom();
	}

	public static String getMailPassword() {
		return globalProps.getMailPassword();
	}
	
	public static String getMailHost() {
		return globalProps.getMailHost();
	}

	public static String getMailSubject() {
		return globalProps.getMailSubject();
	}

	public static String getMailTo() {
		return globalProps.getMailTo();
	}

	public static String getFtpFilename() {
		return getComputerName() + ".txt";
	}

	public static String getFtpPword() {
		return globalProps.getFtpPword();
	}

	public static String getFtpUploadDir() {
		return globalProps.getFtpUploadDir();
	}

	public static String getFtpUrl() {
		return globalProps.getFtpUrl();
	}
	
	public static boolean isFTPEnabled() {
		return getFtpUrl() != null;
	}

	public static String getFtpUsername() {
		return globalProps.getFtpUsername();
	}

	public static String getIpCheckCronExpr() {
		return globalProps.getIpCheckCronExpr();
	}

	public static String getIpDiscoveryUrl() {
		return globalProps.getIpDiscoveryUrl();
	}

	public static int getScreenShotIntervalHrs() {
		return globalProps.getScreenShotIntervalHrs();
	}

}