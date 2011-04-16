package com.peoples.android.services;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus.Listener;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class GPSLocationService extends Service {
	
	
	private static final String TAG = "GPSLocationService";
    private static final boolean D = true;
    
    private static Location location;

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
		
		if(D) Log.e(TAG, "added alarm item, now signing up with LocationManager");
		
		//get a location manager from the system
		final LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		
		if(D) Log.e(TAG, "Best provider:");
			String provider = locManager.getBestProvider(new Criteria(), true);
			Log.e(TAG, provider);
			
			
		/**
		 * 
		 * this is neat, move phone from indoors to somewhere it can get GPS signal
		 * you'll see it increase the number of satellites it can pick up and
		 * eventually fixate call status 3, when a fix is obtained.
		 * 	
		 */
		if(D) Log.e(TAG, "Signing up for GpsStatus:");
			locManager.addGpsStatusListener( new GpsStatus.Listener() {
					
					public void onGpsStatusChanged(int event) {
					
						GpsStatus status = locManager.getGpsStatus(null);
						
						switch(event){
							case 1: Log.e(TAG, "GPS Status: started"); break;
							case 2: Log.e(TAG, "GPS Status: stopped"); break;
							case 3: Log.e(TAG, "GPS Status: first fix at time:");
									Log.e(TAG, ""+status.getTimeToFirstFix());
									Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
									Log.e(TAG, "Location object is:");
									Log.e(TAG, loc.toString() + "\n");
									break;
							case 4: Log.e(TAG, "GPS Status: max satellites:");
									Log.e(TAG, ""+status.getMaxSatellites());
									Log.e(TAG, "GPS Status: but actual satellites:");
									
									int i = 0;
									for(GpsSatellite s : status.getSatellites() ){
										i++;
										Log.e(TAG, s.toString());
									}
									Log.e(TAG, "There were "+i+" satellites in total");
									break;
						}
						Log.e(TAG, "GPS Status: " + event );					
				}
			});
	}
	
	
	
	
	/**
	 * 
	 * this will be called by AlarmManager
	 * 
	 * 
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(D) Log.e(TAG, "+++GPSLocationService.onStartCommand()+++");
		
		Context context = getApplicationContext();
		CharSequence text = "onStartCommand GPSLocation Service";
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
				
		return super.onStartCommand(intent, flags, startId);
	}
	
	public class MyLocationListener implements LocationListener
	{
		public MyLocationListener() {
			super();
		}
		
		@Override
		public void onLocationChanged(Location loc)
		{
			
			Date date = new Date();
			loc.getLatitude();
			loc.getLongitude();
			String Text = "My current location is: \n" +
			"Latitude = " + loc.getLatitude() +
			"\nLongitude = " + loc.getLongitude() + 
			"\n" + date.toString();
			Toast.makeText( getApplicationContext(),
					Text,
					Toast.LENGTH_SHORT).show();

		}
	
	
		@Override
		public void onProviderDisabled(String provider)
		{
			Toast.makeText( getApplicationContext(),
			"Gps Disabled",
			Toast.LENGTH_SHORT ).show();
		}
	
	
		@Override
		public void onProviderEnabled(String provider)
		{
			Toast.makeText( getApplicationContext(),
			"Gps Enabled",
			Toast.LENGTH_SHORT).show();
		}
	
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			if(D) Log.e(TAG, "+++GPS onStatusChanged+++");
		}

	}/* End of Class MyLocationListener */
	
	
	private void initializeGPS(LocationManager locManager, PendingIntent pendingGPS) {
		/*private void doTheRest(final TextView tv) {*/
		
//		TODO: Will make this an activity, and will move GPS gathering to a Service
//		Intent locationIntent = new Intent(this, LocationTestService.class);
//		startService(locationIntent);

		
		// create a Listener interface that will handle the GPS location update
		LocationListener locListener = new MyLocationListener(); 
		
		//subscribe our Listener to the locManager
		/*locManager.addGpsStatusListener(gpsLocListener);*/
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 7*1000, 0, pendingGPS);
		
		String location = "null";
		if(locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null)
			location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).toString();
		
		if(D) Log.e(TAG, location);
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
}
