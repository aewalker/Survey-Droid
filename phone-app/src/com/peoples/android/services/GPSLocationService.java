package com.peoples.android.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class GPSLocationService extends Service {
	
	
	private static final String TAG = "GPSLocationService";
    private static final boolean D = true;

	/**
	 * 
	 * We'll sign up with the AlarmManager
	 * 
	 * 
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		if(D) Log.e(TAG, "+++GPSLocationService.onCreate()+++");

		
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		Intent gpsServiceIntent = new Intent(getApplicationContext(), GPSLocationService.class);
		
		//TODO: probably not the best way to getApplicaitonContext()
		//TODO: sort out which flag to send
		PendingIntent pendingGPS = PendingIntent.getService(getApplicationContext(),
																0,
																gpsServiceIntent,
																PendingIntent.FLAG_UPDATE_CURRENT);
		
		//TODO: first argument determines whether to wake phone up, must consider battery life with final call
		//TODO: 3rd argument is time to fi
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
									SystemClock.elapsedRealtime(),
									30*1000,
									pendingGPS);
		
	}
	
	/**
	 * 
	 * this will be called by AlarmManager
	 * 
	 * 
	 */
	@Override
	public ComponentName startService(Intent service) {
		
		if(D) Log.e(TAG, "+++GPSLocationService.startService()+++");
		return super.startService(service);
		
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(D) Log.e(TAG, "+++GPSLocationService.onStartCommand()+++");
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
