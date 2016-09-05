package com.lmm.sched.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import com.lmm.db.DBCommon;

/**
 * Runs a maintenance task for DB specific needs.
 * 
 */
public class DBMaintenanceJob implements Job {


	public DBMaintenanceJob() {
		super();
	}

    public void execute(JobExecutionContext context) {
 
    	//removes old data from the DB
    	pruneAll();

    }

    private void pruneAll() {
    	DBCommon.getDB().getMaintenance().prune();
    }

}
