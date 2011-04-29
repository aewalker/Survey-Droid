package com.peoples.android.services;

import com.peoples.android.Peoples;

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
		
		
		
		
		
		//TODO: download data
		
		
		
				
		
		//TODO: iterate through survey table and schedule surveys
		
		
		//for now, let's try scheduling a survey every minute?
		//TODO: figure out proper flag!!!
		AlarmManager alarmManager 	= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent surveyIntent			= new Intent(this, Peoples.class);
		
		PendingIntent pendingSurvey = PendingIntent.getActivity(this, 0, surveyIntent,
															PendingIntent.FLAG_UPDATE_CURRENT);
		
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
							SystemClock.elapsedRealtime(), 60*1000, pendingSurvey);
		
		
	}

}
