package com.lmm.db;

import java.util.List;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.lmm.tools.LMMLogger;

abstract class DBProxy {

	protected ObjectContainer db = null;

	//override this method with the real container getter
	ObjectContainer getDBCont() {
		throw new RuntimeException("I do not work without being overridden!!");		
	}
	
	protected void init() {

		Db4o.configure().allowVersionUpdates( true );
		Db4o.configure().automaticShutDown( false );
		
		//runs the DB clean up routine at shutdown
		Runtime.getRuntime().addShutdownHook( new Thread() {
			public void run() {
				if( db != null && !db.ext().isClosed() ) {
					
					LMMLogger.info("Closing database...");

					//give a timeout of 10 seconds at most to close the DB
					try {
						int wait = 0;
						while( !getDBCont().close() && wait < 10 ) {
							Thread.sleep(1000);
							wait++;
						}
					}
					catch( Exception ex ) {
						LMMLogger.error( "Problem during DB close", ex );
					}
					
				}
			}
		});
		
	}
	
	protected void registerClass( Class clazz ) {
		Db4o.configure().objectClass(clazz).cascadeOnDelete(true);
		Db4o.configure().objectClass(clazz).cascadeOnUpdate(true);
	}


	public List retrieve( Predicate pred ) {
		ObjectSet os = getDBCont().query( pred );
		
		return os;
	}

	public List retrieve( Object template ) {
		ObjectSet result = getDBCont().get( template );
		return result;
	}

	public Object retrieveFirst( Object template ) {
		ObjectSet result = getDBCont().get( template );
		if( result.hasNext() )
			return result.next();
		else
			return null;
	}


	public Object retrieveInstance( Class clazz, String eqField, Object eqValue ) {
		Query query = getDBCont().query();
		query.constrain( clazz );
		query.descend( eqField ).constrain( eqValue );
		
		ObjectSet result = query.execute();
		if( result.hasNext() )
			return result.next();
		else
			return null;
	}

	public void deleteInstance( Object obj ) {		
		getDBCont().delete( obj );
	
		getDBCont().commit();
	}

	public void deleteList( List stored ) {		
		for( Object val : stored ) {
			getDBCont().delete( val );
		}
	
		getDBCont().commit();
	}

	public void delete( Object obj ) {		
		List c = retrieve( obj );
		for( int i = 0; i < c.size(); i++ ) {
			getDBCont().delete( c.get(i) );
		}
	
		getDBCont().commit();
	}

	public void delete( Predicate pred ) {
		ObjectSet res = getDBCont().query( pred );

		//deletes all
		while (res.hasNext()) {
			db.delete(res.next());
		}
	}

	public void store( Object obj ) {
		getDBCont().set( obj );
		getDBCont().commit();
	}





/*
	public static void retrieveCarQuery(ObjectContainer db) {
		Query query = db.query();
		query.constrain(Car.class);
		Query historyquery = query.descend("history");
		historyquery.constrain(SensorReadout.class);
		Query valuequery = historyquery.descend("values");
		valuequery.constrain(new Double(0.3));
		valuequery.constrain(new Double(0.1));
		ObjectSet result = query.execute();
		listResult(result);
	}

	public static void updateCollection(ObjectContainer db) {
		ObjectSet results = db.query(new Predicate() {
			public boolean match(Car candidate) {
				return true;
			}
		});
		Car car = (Car)results.next();
		car.getHistory().remove(0);
		db.set(car.getHistory());
		results = db.query(new Predicate() {
			public boolean match(Car candidate) {
				return true;
			}
		});
		while (results.hasNext()) {
			car = (Car)results.next();
			for (int idx = 0; idx < car.getHistory().size(); idx++) {
				System.out.println(car.getHistory().get(idx));
			}
		}
	}

	public static void deleteAllPart2(ObjectContainer db) {

		//finds all Car objects
		ObjectSet cars = db.query(new Predicate() {
			public boolean match(Car candidate) {
				return true;
			}
		});
        
		//deletes all Car objects
		while (cars.hasNext()) {
			db.delete(cars.next());
		}
		ObjectSet readouts = db.query(new Predicate() {
			public boolean match(SensorReadout candidate) {
				return true;
			}
		});
        
		//deletes all SensorReadouts objects
		while (readouts.hasNext()) {
			db.delete(readouts.next());
		}
	}
*/





/**
 * Debug printing needs below
 */
    protected static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while (result.hasNext()) {
            System.out.println(result.next());
        }
    }

    protected static void listResult(java.util.List result) {
        System.out.println(result.size());
        for (int x = 0; x < result.size(); x++)
            System.out.println(result.get(x));
    }

    protected static void listRefreshedResult(
        ObjectContainer container,
        ObjectSet result,
        int depth) {
        System.out.println(result.size());
        while (result.hasNext()) {
            Object obj = result.next();
            container.ext().refresh(obj, depth);
            System.out.println(obj);
        }
    }

    protected static void retrieveAll(ObjectContainer db) {
        ObjectSet result = db.get(new Object());
        listResult(result);
    }

    protected static void deleteAll(ObjectContainer db) {
        ObjectSet result = db.get(new Object());
        while (result.hasNext()) {
            db.delete(result.next());
        }
    }
}
