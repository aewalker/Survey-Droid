package com.peoples.android.services;

import java.util.Calendar;

import com.peoples.android.database.PeoplesDB;
import com.peoples.android.database.ScheduledSurveyDBHandler;
import com.peoples.android.database.SurveyDBHandler;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;


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
		
		//TODO: iterate over scheduled surveys table
		
		//TODO: seems useful to warrant moving into scheduled survey DB handler 
		String today = null;
		int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		switch (day) {
		case Calendar.SUNDAY:
			today = PeoplesDB.SurveyTable.SU; break;
		case Calendar.MONDAY:
			today = PeoplesDB.SurveyTable.MO; break;
		case Calendar.TUESDAY:
			today = PeoplesDB.SurveyTable.TU; break;
		case Calendar.WEDNESDAY:
			today = PeoplesDB.SurveyTable.WE; break;
		case Calendar.THURSDAY:
			today = PeoplesDB.SurveyTable.TH; break;
		case Calendar.FRIDAY:
			today = PeoplesDB.SurveyTable.FR; break;
		case Calendar.SATURDAY:
			today = PeoplesDB.SurveyTable.SA; break;
		default:
			break;
		}
		
		//TODO: can make better by combining the handlers
		ScheduledSurveyDBHandler ssHandler = new ScheduledSurveyDBHandler(this);
		SurveyDBHandler		   survHandler = new SurveyDBHandler(this);
		
		
		//open
		ssHandler.openRead();
		Cursor scheduledCursor = ssHandler.getScheduledSurveys(today);
		
		if(scheduledCursor != null){
			boolean next = true;
			while(scheduledCursor.isAfterLast() == false && next){
				
				
			
				
			}
			scheduledCursor.close();
		}
		//close
		ssHandler.close();
		
		
		
		
		
		
		//TODO: iterate over surveys 
		
		
		
		
		
		
		
		
		

	}

}
