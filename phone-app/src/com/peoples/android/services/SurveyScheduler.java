package com.peoples.android.services;



//import com.peoples.android.Peoples;
import java.util.Calendar;

import com.peoples.android.database.PeoplesDB;
import com.peoples.android.database.ScheduledSurveyDBHandler;
import com.peoples.android.database.SurveyDBHandler;

//import android.app.AlarmManager;
import android.app.IntentService;
//import android.app.PendingIntent;
//import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
//import android.os.SystemClock;
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
	
	//for writing to the log
	private static final String TAG = "SurveyScheduler";
	//want this around for future use
	private static final boolean D = true;
	

	/**
	 * If the next survey is within THRESHHOLD time of the last
	 * skipped survey, then the last survey will be skipped
	 */
	//FIXME remove when used
	private static long THRESHOLD = 3*60*1000;
	
	/**
	 * TODO: should be a survey specific attribute
	 * Surveys will not be administered if they are more than
	 * this much time late
	 */
	//FIXME remove when used
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
		//if a certain threshold time has not been reached then
		//reschedule the first, otherwise simply keep its entry in the prev.
		//scheduled database and only keep the new one.
		
		//TODO: iterate over scheduled surveys table
		//TODO: iterate over surveys
		//note: currently testing iterating over survey tables below
		
		
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
		
		//FIXME remove when used
		SurveyDBHandler		   survHandler = new SurveyDBHandler(this);
		
		
		//open handler
		ssHandler.openRead();
		
		//get previously scheduled surveys
		Cursor scheduledCursor = ssHandler.getScheduledSurveys(today);
		
		//testing
		int id, survid;
		String origTime, time;
		String prevSurv = "";
		
		while(scheduledCursor.moveToNext()){
			Log.d(TAG, "Previously scheduled surveys:");
			
			prevSurv = "";
			
			id 			= scheduledCursor.getInt(0);
			survid 		= scheduledCursor.getInt(1);
			origTime	= scheduledCursor.getString(2);
			time		= scheduledCursor.getString(3);
			
			prevSurv = 	"id: "+id+" survID "+survid+" origTime "+origTime+
						" time "+time;
			
			Log.d(TAG, prevSurv);
		}
		//close the cursor
		scheduledCursor.close();
		
		
		//get surveys that were skipped or have not been scheduled
		Cursor unscheduledCursor = ssHandler.getUnScheduledSurveys(today);
		
		String survDay = "";
		
		while(unscheduledCursor.moveToNext()){
			Log.d(TAG, "unscheduled surveys:");
			survDay = "day: " +	unscheduledCursor.getString(0);
			survDay += "id: " +	unscheduledCursor.getInt(1);
			Log.d(TAG, survDay);
		}
		//close the cursor
		unscheduledCursor.close();
		
		//close handler
		ssHandler.close();
		
		//this is setting a recurring survey (should do within survey scheduler)
		//will need one of these to schedule services
//        AlarmManager alarmManager =
//        	(AlarmManager) getSystemService(Context.ALARM_SERVICE);
//		
//		Intent surveyIntent			= new Intent(this, Peoples.class);
//		
//		PendingIntent pendingSurvey = PendingIntent.getActivity(this, 0, surveyIntent,
//															PendingIntent.FLAG_UPDATE_CURRENT);
//		
//		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//							SystemClock.elapsedRealtime(), 60*1000, pendingSurvey);
//		
		
	}

}
