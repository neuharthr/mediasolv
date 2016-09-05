package com.lmm.sched.proc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.lmm.msg.ClientStateMsg;
import com.lmm.sched.data.LMMEntry;


public class PlayOrder
{	
	//map of:  key<Integer>, value<LMMEntry>
	private HashMap<Integer, LMMEntry> playMap = null;
	
	//keeps track of the state our video playing is in
	private ClientStateMsg currentState = new ClientStateMsg();


    /**
     * @return
     */
    protected HashMap<Integer, LMMEntry> getPlayMap()  {

    	if( playMap == null )
			playMap = new HashMap<Integer, LMMEntry>(32);

        return playMap;
    }

	/**
	 * Returns all items in the list and orders them
	 * 
	 */
	private List<LMMEntry> getAllOrderedList()  {

		Object[] keys = getPlayMap().keySet().toArray();
		Arrays.sort( keys );
		ArrayList<LMMEntry> retList = new ArrayList<LMMEntry>( keys.length );
		
		for( int i = 0; i < keys.length; i++ ) {			
			retList.add( getPlayMap().get(keys[i]) );
		}

		return retList;
	}

	/**
	 * Returns all items in the list and orders them.
	 * Only adds entries that are playable now.
	 * 
	 */
	public synchronized List<LMMEntry> getCurrOrderedList()  {

		Object[] keys = getPlayMap().keySet().toArray();
		Arrays.sort( keys );
		ArrayList<LMMEntry> retList = new ArrayList<LMMEntry>( keys.length );
		Date currDate = new Date();
		
		for( int i = 0; i < keys.length; i++ ) {
			
			LMMEntry entry = getPlayMap().get(keys[i]);

			if( !entry.isDateInWindows(currDate) )
				continue;

			retList.add( getPlayMap().get(keys[i]) );
		}

		return retList;
	}

	/**
	 * Only considers Entries that are playable now
	 * 
	 */
	public synchronized long getCurrTotalDuation() {
		
		long total = 0L; 
		Iterator itr = getPlayMap().values().iterator();
		Date currDate = new Date();

		while( itr.hasNext() ) {

			LMMEntry entry = (LMMEntry)itr.next();
			if( !entry.isDateInWindows(currDate) )
				continue;

			total += entry.getPlayDuration().intValue();
		}
		
		//we must have a reasonable duration if none is present (zero would be bad!!)
		return total < 5000 ? 5000 : total;	//a minimum of 5 seconds per reel is allowed
	}

    /**
     * @return
     */
    public ClientStateMsg getCurrentState() {
        return currentState;
    }

}