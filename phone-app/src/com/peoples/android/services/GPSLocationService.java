package com.peoples.android.services;

import java.util.Date;

import com.peoples.android.database.LocationTableHandler;
import com.peoples.android.database.PeoplesDB;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus.Listener;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class GPSLocationService extends IntentService {

	private static final String TAG = "GPSLocationService";
    private static final boolean D = true;

    public GPSLocationService() {
		super(null);
		// TODO Auto-generated constructor stub
	}
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
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

		if(D) Log.e(TAG, "+++GPSLocationService.onCreate()+++");
		
		if(D) Log.e(TAG, "Here are our databases before:");
		
		String[] dbs = this.databaseList();
		
		if(D){ 			
			for(String s:dbs)
				Log.e(TAG, s);
		}
	}
	
	
	
	
	/**
	 * 
	 * this will be called by AlarmManager
	 * 
	 * 
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		
		if(D) Log.e(TAG, "+++GPSLocationService.onHandleIntent+++");
		
		if(D){
			
			Log.e(TAG, "+++Here are all the stored locations+++");
			
			//Get the handler
			LocationTableHandler dbHandler = new LocationTableHandler(getApplicationContext());
			//open to read
			dbHandler.openRead();
			//query
			Cursor cur = dbHandler.getStoredLocations();
			//iterate over results if any
			if(cur != null){
				boolean next = true;
				while(cur.isAfterLast() == false && next){
				
					String[] columnNames = cur.getColumnNames();
					int		 numColumns	 = cur.getColumnCount();
					
					String locString = "LOCATION: \n";
					
					while( cur.isAfterLast() == false ){
						locString += columnNames[0] + cur.getInt(0) + "\n";
						locString += columnNames[0] + cur.getDouble(0) + "\n";
						locString += columnNames[0] + cur.getDouble(0) + "\n";
						locString += columnNames[0] + cur.getInt(0) + "\n";
					}
					
					Log.e(TAG, locString);
					next = cur.moveToNext();
				}
				cur.close();
			}
			//close
			dbHandler.close();
		}
		
//		Context context = getApplicationContext();
//		CharSequence text = "onStartCommand GPSLocation Service";
//		int duration = Toast.LENGTH_LONG;
//		Toast toast = Toast.makeText(context, text, duration);
//		toast.show();
		
		//use table handler
		LocationTableHandler locHandler = new LocationTableHandler(getApplicationContext());
		//open to write
		locHandler.openWrite();
		
		Location loc = new Location(LocationManager.GPS_PROVIDER);
		loc.setLatitude(1);
		loc.setLongitude(2);
		loc.setTime(1234);
		
		//pass location to write
		locHandler.insertLocation(loc);
		//close
		locHandler.close();
		
		
		if(D) Log.e(TAG, "Here are our databases after:");
		
		String[] dbs = this.databaseList();
		
		dbs = this.databaseList();
		
		if(D){ 			
			for(String s:dbs)
				Log.e(TAG, s);
		}
		
		if(D) {
			Log.e(TAG, "now signing up with LocationManager");
		
			//get a location manager from the system
			LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			Log.e(TAG, "Best provider:");
			String provider = locManager.getBestProvider(new Criteria(), true);
			
			if(provider != null)
				Log.e(TAG, provider);
			else
				Log.e(TAG, "TURN GPS ON!"); //TODO turn GPS on programatically
			
			/**
			 * this is neat, move phone from indoors to somewhere it can get GPS signal
			 * you'll see it increase the number of satellites it can pick up and
			 * eventually fixate call status 3, when a fix is obtained.	
			 */
			Log.e(TAG, "Signing up for GpsStatus:");

			locManager.addGpsStatusListener( new GpsStatus.Listener() {

				public void onGpsStatusChanged(int event) {
					
					LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

					GpsStatus status = locManager.getGpsStatus(null);

					Log.e(TAG, "GPS Status: " + event );
					switch(event){
					case 1: Log.e(TAG, "GPS Status: started"); break;

					case 2: Log.e(TAG, "GPS Status: stopped"); break;

					case 3: Log.e(TAG, "GPS Status: first fix at time:");
					Log.e(TAG, ""+status.getTimeToFirstFix());
					Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					Log.e(TAG, "Location object is:");
					Log.e(TAG, loc.toString() + "\n");
					
					//use table handler
					LocationTableHandler locHandler = new LocationTableHandler(getApplicationContext());
					//open to write
					locHandler.openWrite();
					//pass location to write
					locHandler.insertLocation(loc);
					//close
					locHandler.close();

					
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
										
				}
			});
			
			Log.e(TAG, "Is best provider enabled? " + locManager.isProviderEnabled(provider) );
			
			//locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 7*1000, 0, updateGPSDB);
		}
	}
	
	
	
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
		
		if(D) Log.e(TAG, location);
		
	}



	
	
	
}
