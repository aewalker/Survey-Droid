package com.peoples.android.services;

//import com.peoples.android.Peoples;
import com.peoples.android.PeoplesConfig;
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
    
    
    private static long CALL_LOG_PERIOD = 60*60*1000;
    
    private static long GPS_PERIOD		= 15*60*1000;
    
    

	public CoordinatorService() {
		super(CoordinatorService.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		//i.e. check on surveys, pull any new ones, push any responses, push GPS or call log data
		//check that everything is running smoothly, and report any errors to the server
		
		if(D) Log.e(TAG, "onHandleIntent");
		
        PeoplesConfig config = new PeoplesConfig(getApplicationContext());
        
        //TODO: begin GPS collection
        if(config.isLocationEnabled())
        	launchGPS();
        
        //TODO: begin call log collection
        if(config.isLocationEnabled())
        	launchCallLog();
        
        
        AlarmManager alarmManager =
        	(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        //schedule SurveyScheduler to run periodically
        //survey scheduler pushes, pulls, and schedules
        Intent surveySchedulerIntent = new Intent(this, SurveyScheduler.class);
        PendingIntent pendingScheduler = PendingIntent.getService(this, 0,
        		surveySchedulerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(
        					AlarmManager.ELAPSED_REALTIME_WAKEUP,
        					SystemClock.elapsedRealtime(),
        					SCHEDULER_PERIOD, pendingScheduler);
	}
	
	private void launchCallLog() {
		AlarmManager alarmManager =
        	(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        //schedule call logger to run periodically
        Intent callLogIntent = new Intent(this, CallLogService.class);
        PendingIntent pendingLogIntent = PendingIntent.getService(this, 0,
        		callLogIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(
        					AlarmManager.ELAPSED_REALTIME_WAKEUP,
        					SystemClock.elapsedRealtime(),
        					CALL_LOG_PERIOD, pendingLogIntent);
	}

	private void launchGPS() {
		AlarmManager alarmManager =
        	(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        //schedule location logger to run periodically
        Intent gpsIntent = new Intent(this, GPSLocationService.class);
        PendingIntent pendingGPSIntent = PendingIntent.getService(this, 0,
        		gpsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(
        					AlarmManager.ELAPSED_REALTIME_WAKEUP,
        					SystemClock.elapsedRealtime(),
        					GPS_PERIOD, pendingGPSIntent);
	}


}
