package com.peoples.android.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.peoples.android.database.PeoplesDBHandler;

public class GPSLocationService extends IntentService {

	private static final String TAG = "GPSLocationService";
    private static final boolean D = true;
    
    private static final int GPS_PERIOD = 30*60*1000;

    public GPSLocationService() {
		super(null);
	}
    
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * 
	 * We'll sign up with the AlarmManager
	 * 
	 * 
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		if(D) Log.d(TAG, "+++GPSLocationService.onCreate()+++");
		
	}
	
	/**
	 * 
	 * this will be called by AlarmManager
	 * 
	 * 
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		
		
		
		
	}
	
	
	
		
	
	private void testLocationPersistence() {
		Log.d(TAG, "+++Here are all the stored locations+++");
		
		//Get the handler
		PeoplesDBHandler dbHandler = new PeoplesDBHandler(getApplicationContext());
		//open to read
		dbHandler.openRead();
		//query
		Cursor cur = dbHandler.getStoredLocations();
		//iterate over results if any
		if(cur != null){
			boolean next = true;
			while(cur.isAfterLast() == false && next){
			
				String[] columnNames = cur.getColumnNames();
				//FIXME remove when used
				@SuppressWarnings("unused")
				int		 numColumns	 = cur.getColumnCount();
				
				String locString = "LOCATION: \n";
				
				while( cur.isAfterLast() == false ){
					locString += columnNames[0] + cur.getInt(0) + "\n";
					locString += columnNames[0] + cur.getDouble(0) + "\n";
					locString += columnNames[0] + cur.getDouble(0) + "\n";
					locString += columnNames[0] + cur.getInt(0) + "\n";
				}
				
				Log.d(TAG, locString);
				next = cur.moveToNext();
			}
			cur.close();
		}
		//close
		dbHandler.close();
	}

	/** @deprecated */
	@SuppressWarnings("unused") //see below comments
	private void initializeGPS(LocationManager locManager, PendingIntent pendingGPS) {
		/*private void doTheRest(final TextView tv) {*/
		
//		TODO: Will make this an activity, and will move GPS gathering to a Service
//		Intent locationIntent = new Intent(this, LocationTestService.class);
//		startService(locationIntent);

		
		// create a Listener interface that will handle the GPS location update
		//LocationListener locListener = new MyLocationListener(); 
		
		//subscribe our Listener to the locManager
		/*locManager.addGpsStatusListener(gpsLocListener);*/
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 7*1000, 0, pendingGPS);
		
		String location = "null";
		if(locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null)
			location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).toString();
		
		if(D) Log.d(TAG, location);
		
	}



	
	
	
}
