package com.peoples.android.services;

import com.peoples.android.Peoples;
import com.peoples.android.server.Pull;
import com.peoples.android.server.Push;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class CoordinatorService extends IntentService {
	
	private static final String TAG = "CoordinatorService";
    private static final boolean D = true;
    
    

	public CoordinatorService() {
		super(CoordinatorService.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		//TODO: Implement logic that will get called regularly:
		//i.e. check on surveys, pull any new ones, push any responses, push GPS or call log data
		//check that everything is running smoothly, and report any errors to the server
		
		if(D) Log.e(TAG, "onHandleIntent");
		
		//TODO: upload data
		Log.d(TAG, "Pushing all data");
		Push.pushAll(this);
		
		//TODO: download data
		Log.d(TAG, "Fetching surveys");
        Pull.syncWithWeb(this);
		
        //will need one of these to schedule services
        AlarmManager alarmManager =
        	(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        
		//TODO: iterate through survey table and schedule surveys
        //need to somehow find row of unanswered survey
        
		
		//for now, let's try scheduling a survey every minute?
		//TODO: figure out proper flag!!!
        
        //TODO: have alarmManager run SurveyScheduler at specified interval
        Intent surveySchedulerIntent = new Intent(this, SurveyScheduler.class);
        PendingIntent pendingScheduler = PendingIntent.getService(this, 0,
        		surveySchedulerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(
        					AlarmManager.ELAPSED_REALTIME_WAKEUP,
        					SystemClock.elapsedRealtime(),
        					60*1000, pendingScheduler);
        
        //this is setting a recurring survey
		
		Intent surveyIntent			= new Intent(this, Peoples.class);
		
		PendingIntent pendingSurvey = PendingIntent.getActivity(this, 0, surveyIntent,
															PendingIntent.FLAG_UPDATE_CURRENT);
		
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
							SystemClock.elapsedRealtime(), 60*1000, pendingSurvey);
		
		
	}


}
