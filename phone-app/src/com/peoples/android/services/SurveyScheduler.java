package com.peoples.android.services;



//import com.peoples.android.Peoples;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.peoples.android.activities.MainActivity;
import com.peoples.android.database.PeoplesDB;
import com.peoples.android.database.ScheduledSurveyDBHandler;
import com.peoples.android.database.SurveyDBHandler;
import com.peoples.android.model.SurveyIntent;
import com.peoples.android.server.Pull;
import com.peoples.android.server.Push;

//import android.app.AlarmManager;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
//import android.app.PendingIntent;
//import android.content.Context;
import android.content.Context;
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
		
		//upload data
		Log.d(TAG, "Pushing all data");
		Push.pushAll(this);
		
		//download data
		Log.d(TAG, "Fetching surveys");
        Pull.syncWithWeb(this);
		
		//Surveys that need scheduling come from two places:
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
		Integer id, survid;
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
		
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date survDate;
		
		Calendar calendar;
		
		
		//will need one of these to schedule services
        AlarmManager alarmManager =
        	(AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		while(unscheduledCursor.moveToNext()){
			Log.d(TAG, "unscheduled surveys:");
			
			survDay	= unscheduledCursor.getString(0);
			survid  = unscheduledCursor.getInt(1);
			
			Log.d(TAG, survDay+" "+survid);
			
			//gotta make sure we're not retrieving null day
			if( survDay == null ||
					survDay.equals("null") ||
					survDay.length() == 0 )
				continue;
				
			String[] dates = survDay.split(",");
			
			for(String survTime : dates){

				//TODO: do real error handling
				try {
					//make calendar for current time
					Log.d(TAG, "-------------------");
					
					//Parse the time found in the database
					sdf.parse(survTime);
					
					//get a calendar with the current day/time, and 
					//change only the time to match what was parsed from DB
					Calendar surveyTime = Calendar.getInstance();
					surveyTime.set(Calendar.HOUR_OF_DAY, sdf.getCalendar().get(Calendar.HOUR_OF_DAY));
					surveyTime.set(Calendar.MINUTE, sdf.getCalendar().get(Calendar.MINUTE));

					//*****************************************
					//TODO: NEEDS TO BE TUNED
					//*****************************************
					// Currently does not schedule if original time is in past
					if( System.currentTimeMillis() > surveyTime.getTimeInMillis() )
						continue;
					

					//Debug reporting
					if(D){
						Log.d(TAG, "Scheduling survey with id: "+ survid);
						Log.d(TAG, "Current time: "+ System.currentTimeMillis());
						Log.d(TAG, "Scheduling for: "+ surveyTime.getTimeInMillis());
					}
					
					
					//survey intent has valuable intel
					SurveyIntent surveyIntent =
						new SurveyIntent(getApplicationContext(),
								survid,
								surveyTime.getTimeInMillis(),
								MainActivity.class);
					
					PendingIntent pendingSurvey =
						PendingIntent.getActivity(getApplicationContext(), 0,
								surveyIntent,
								PendingIntent.FLAG_UPDATE_CURRENT);

					alarmManager.set(AlarmManager.RTC_WAKEUP,
							surveyTime.getTimeInMillis(), pendingSurvey);

					//TODO: write scheduled surveys to scheduled database

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						Log.e(TAG, "DATE PARSE ERROR", e);
					}
				}
			
			
			
			
		}
		//close the cursor
		unscheduledCursor.close();
		
		//close handler
		ssHandler.close();
		
		
		
		
	}

}
