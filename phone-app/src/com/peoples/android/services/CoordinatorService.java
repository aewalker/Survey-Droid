package com.peoples.android.services;

//import com.peoples.android.Peoples;
import com.peoples.android.PeoplesConfig;
import com.peoples.android.database.PeoplesDBHandler;
import com.peoples.android.server.Pull;
import com.peoples.android.server.Push;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
    
    private static LocationListener LOCATION_LISTENER;
    
	public CoordinatorService() {
		super(CoordinatorService.class.getName());
		LOCATION_LISTENER = new GPSListener();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		//i.e. check on surveys, pull any new ones, push any responses, push GPS or call log data
		//check that everything is running smoothly, and report any errors to the server
		
		if(D) Log.e(TAG, "onHandleIntent");
		
        PeoplesConfig config = new PeoplesConfig(getApplicationContext());
        
        //begin GPS collection
        if(config.isLocationEnabled())
        	launchGPS();
        else
        	killGPS();
        
        //begin call log collection
        if(config.isLocationEnabled())
        	launchCallLog();
        else
        	killCallLog();
        
        
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
	
	private void killCallLog() {
		// TODO Auto-generated method stub
		
	}

	private void launchGPS() {
		
		if(D) Log.d(TAG, "+++launchGPS+++");
		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String provider = locManager.getBestProvider(new Criteria(), true);
		locManager.requestLocationUpdates(provider, GPS_PERIOD, 0, LOCATION_LISTENER);
	}
	
	private void killGPS() {
		
		if(D) Log.d(TAG, "+++killGPS+++");
		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.removeUpdates(LOCATION_LISTENER);
	}
	
	private class GPSListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			
			PeoplesDBHandler locHandler = new PeoplesDBHandler(getApplicationContext());
			locHandler.openWrite();
			locHandler.insertLocation(location);
			locHandler.close();
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d(TAG, "Provider disabled: "+provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.d(TAG, "Provider enabled: "+provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d(TAG, "onStatusChanged. Provider: "+provider+" status" + status);
		}
		
	}



}
