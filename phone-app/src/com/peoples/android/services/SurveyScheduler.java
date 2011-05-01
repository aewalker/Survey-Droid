package com.peoples.android.services;

import android.app.IntentService;
import android.content.Intent;


/**
 * Schedules surveys to be administered by the activity specified
 * in SurveyIntent. This is expected to run periodically by the
 * AlarmManager
 * 
 * 
 * @author Diego
 *
 */
public class SurveyScheduler extends IntentService {

	/**
	 * If the next survey is within THRESHHOLD time of the last
	 * skipped survey, then the last survey will be skipped
	 */
	private static long THRESHOLD = 3*60*1000;
	
	/**
	 * TODO: should be a survey specific attribute
	 * Surveys will not be administered if they are more than
	 * this much time late
	 */
	private static long EXPIRES = 3*60*1000;
	
	public SurveyScheduler() {
		super(SurveyScheduler.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		
		
		//TODO: Surveys that need scheduling come from two places:
		//1. previously scheduled surveys table
		//2. surveys table
		//
		//When surveys belong to both 1 and 2, then one will be deleted.
		//if a certain threshhold time has not been reached then
		//reschedule the first, otherwise simply keep its entry in the prev.
		//scheduled database and only keep the new one.
		
		
		
		//TODO: iterate over database tables, schedule surveys
		
		

	}

}
