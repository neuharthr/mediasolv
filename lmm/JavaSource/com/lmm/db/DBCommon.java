package com.lmm.db;

import com.db4o.*;
import com.db4o.query.Predicate;
import com.lmm.pop.POPDailyMetric;
import com.lmm.sched.proc.LMMGlobalConfig;
import com.lmm.sched.proc.LMMUtils;
import com.lmm.server.lite.LiteGroupPlayer;
import com.lmm.server.lite.LitePlayer;
import com.lmm.server.lite.OwnableData;
import com.lmm.tools.LMMLogger;

import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.HashMap;

/**
 * In db4O, the default activation depth for the object graph is 5. This means objects referenced
 * from an object at 5 levels down or more will have all of its members set to NULL.
 * 		foo.member1.member2.member3.member4.member5 will be a valid object
 *		foo, member1, member2, member3 and member4 will be activated
 * 		member5 will be deactivated, all of it's members will be null
 * 		member5 can be activated at any time by calling ObjectContainer#activate(member5, depth). 
 */
public class DBCommon extends DBProxy {
	
	public static final String DB_FILENAME_KEY = "LMM_DBFileName";

	private static DBCommon dbAccess = null;
	private DBMaintenance dbMaint = null;

	private DBCommon() {
		super();
		
		//all classes that are in ths DB must go here
		registerClass( LMMGlobalConfig.class );
		registerClass( HashMap.class );

		registerClass( LiteGroupPlayer.class );
		registerClass( LitePlayer.class );
		registerClass( POPDailyMetric.class );
		
		registerClass( OwnableData.class );
	}

    /**
     * singleton
     */
    public static DBCommon getDB() {
    	if( dbAccess == null )
			dbAccess = new DBCommon();

    	return dbAccess;
    }
    
    public DBMaintenance getMaintenance() {
    	if( dbMaint == null )
    		dbMaint = new DBMaintenance();
    	
    	return dbMaint;
    }
    
    ObjectContainer getDBCont() {
    	if( db == null || db.ext().isClosed() ) {
    		
			init();  //do anything else that may be needed in the super
			    		
			//not in standard db4o jar, found in com.db40.tools
			//new Defragment().run( DBUtil.YAPFILENAME, true );

			Db4o.configure().objectClass(LitePlayer.class).objectField("uuid").indexed(true);
			Db4o.configure().objectClass(POPDailyMetric.class).objectField("date").indexed(true);
			
			//blow them out of the water if this is not set!!!
			if( System.getProperty(DB_FILENAME_KEY) == null ) {
				IllegalStateException i = new IllegalStateException("The system environment variable '" +
					DB_FILENAME_KEY + "' must be set inside the application. Use System.setProperty(key, value) to do this.");
				
				LMMLogger.error( "Unable to start application", i );
				System.exit(-1);
			}

			try {
				db = Db4o.openFile( LMMUtils.getDataDir() + System.getProperty(DB_FILENAME_KEY) ); //db_common.yap
			}
			catch( Exception ex ) {
				LMMLogger.error( "Unable to open/create the database '" +
					(LMMUtils.getDataDir() + System.getProperty(DB_FILENAME_KEY)) + "'", ex );
			}

    	}
    	
    	return db;
    }



	/**
	 * Start specific object methods
	 * 
	 */

	public static void globalConfig_Update( LMMGlobalConfig globalProps )  {		
		DBCommon.getDB().delete( LMMGlobalConfig.class );		
		DBCommon.getDB().store( globalProps );
	}

	public static LMMGlobalConfig globalConfig_Retrieve()  {
		return (LMMGlobalConfig)DBCommon.getDB().retrieveFirst( LMMGlobalConfig.class );
	}
	
	public static void groupPlayer_Update( LiteGroupPlayer groupPlayer )  {		
		groupPlayer_Delete( groupPlayer );
		DBCommon.getDB().store( groupPlayer );
	}

	public static void groupPlayer_Delete( LiteGroupPlayer groupPlayer )  {
		LiteGroupPlayer storedGrp = (LiteGroupPlayer)DBCommon.getDB().retrieveInstance(
			LiteGroupPlayer.class, "uuid", groupPlayer.getUuid() );

		if( storedGrp != null )
			DBCommon.getDB().deleteInstance( storedGrp );
	}
	
	@SuppressWarnings("unchecked")
	public static Vector<LiteGroupPlayer> groupPlayer_RetrieveAll()  {
		List<LiteGroupPlayer> storage = DBCommon.getDB().retrieve( LiteGroupPlayer.class );
		if( storage == null )
			return new Vector<LiteGroupPlayer>();
		else
			return new Vector<LiteGroupPlayer>( storage );
	}

	
	
	
	public void litePlayer_Update( LitePlayer litePlayer )  {
		List<LitePlayer> lPlayers = litePlayer_Retrieve( litePlayer.getUuid() );
		if( lPlayers.size() > 0 )
			litePlayer = lPlayers.get(0);

		DBCommon.getDB().store( litePlayer );
	}

	public void litePlayer_Delete( LitePlayer litePlayer )  {
		List<LitePlayer> lPlayers = litePlayer_Retrieve( litePlayer.getUuid() );

		if( lPlayers.size() > 0 )
			DBCommon.getDB().deleteInstance( lPlayers.get(0) );
	}
	
	@SuppressWarnings("unchecked")
	public List<LitePlayer> litePlayer_RetrieveAll() {
		return litePlayer_Retrieve( null );
	}

	@SuppressWarnings("unchecked")
	public List<LitePlayer> litePlayer_Retrieve( final String uuid ) {
	
		Predicate<LitePlayer> pred = DBPredicates.getLitePlayerPred(uuid);
		
		return DBCommon.getDB().retrieve( pred );
	}
	
	public void popDailyMetric_Update( POPDailyMetric popDaily )  {
		DBCommon.getDB().store( popDaily );
	}

	public void popDailyMetric_Delete( POPDailyMetric popDaily )  {
		List<POPDailyMetric> stored  = popDailyMetric_Retrieve(
				popDaily.getDate(), popDaily.getThemeName() );

		if( stored.size() > 0 )
			DBCommon.getDB().deleteList( stored );
	}


	@SuppressWarnings("unchecked")
	public List<POPDailyMetric> popDailyMetric_Retrieve( 
			final Date date, final String themeName )  {

		Predicate<POPDailyMetric> pred = DBPredicates.getPOPPredicate(date, null, themeName);
		 
		return DBCommon.getDB().retrieve( pred );
	}

	@SuppressWarnings("unchecked")
	public List<POPDailyMetric> popDailyMetric_Retrieve( 
			final Date start, final Date end, final String themeName )  {

		Predicate<POPDailyMetric> pred = DBPredicates.getPOPPredicate(start, end, themeName);
		 
		return DBCommon.getDB().retrieve( pred );
	}

	public List<POPDailyMetric> popDailyMetric_Retrieve( final Date date )  {
		return popDailyMetric_Retrieve( date, null );
	}

	
}