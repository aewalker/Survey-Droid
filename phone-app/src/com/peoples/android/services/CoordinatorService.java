package com.peoples.android.services;

//import com.peoples.android.Peoples;
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
    
    
    /**
     * Run scheduler this often
     */
    private static long SCHEDULER_PERIOD = 30*1000;
    
    

	public CoordinatorService() {
		super(CoordinatorService.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		//TODO: Implement logic that will get called regularly:
		//i.e. check on surveys, pull any new ones, push any responses, push GPS or call log data
		//check that everything is running smoothly, and report any errors to the server
		
		if(D) Log.e(TAG, "onHandleIntent");
		
        //will need one of these to schedule services
        AlarmManager alarmManager =
        	(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
		//for now, let's try scheduling a survey every minute?
		//TODO: figure out proper flag!!!
        	
        //TODO: have alarmManager run SurveyScheduler at specified interval
        
        //schedule SurveyScheduler to run periodically
        Intent surveySchedulerIntent = new Intent(this, SurveyScheduler.class);
        PendingIntent pendingScheduler = PendingIntent.getService(this, 0,
        		surveySchedulerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(
        					AlarmManager.ELAPSED_REALTIME_WAKEUP,
        					SystemClock.elapsedRealtime(),
        					SCHEDULER_PERIOD, pendingScheduler);
	}


}
