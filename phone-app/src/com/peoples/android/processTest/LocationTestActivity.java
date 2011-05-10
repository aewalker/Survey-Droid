package com.peoples.android.processTest;

import java.util.Date;

import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.peoples.android.R;
import com.peoples.android.activities.MainActivity;
import com.peoples.android.database.PeoplesDBHandler;
/**
 * 
 * @author Henry Liu
 *
 */
public class LocationTestActivity extends Activity {
	
	private static final String TAG = "LocTestActi";
    private static final boolean D = true;
    private static final long PERIOD = 30*1000;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.gps);
    	
    	
    	//TODO: sync surveys here
        //TODO: getting some parsing error
        //Pull.syncWithWeb(this);
    	
    	//TODO: run service that takes care of survey scheduling, it should schedule itself(?)
    	//		that's probably BootService?
    	
    	
//		Previous idea was to have a service for GPS stuff. However, we don't need to do that if
//    	and we can rely on the functionality provided by the location providers.
//
//    	start GPS process
//    	or sign it up with the alarm service to run it every 15 or w.e. seconds
//    	AlarmManager alarmManager	= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//		Intent gpsServiceIntent 	= new Intent(getApplicationContext(), GPSLocationService.class);
//		
//		// probably not the best way to getApplicaitonContext()
//		// sort out which flag to send
//		PendingIntent updateGPSDB = PendingIntent.getService(getApplicationContext(),
//																0,
//																gpsServiceIntent,
//																PendingIntent.FLAG_UPDATE_CURRENT);
//		//first argument determines whether to wake phone up, must consider battery life with final call
//		//3rd argument is time to fi
//		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//									SystemClock.elapsedRealtime(),
//									PERIOD,
//									updateGPSDB);
//		
    	
    	//adding functionality to button
    	Button back = (Button) findViewById(R.id.goback);
    	back.setText("Back to selection screen");
    	back.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View view) {
    			Intent myIntent = new Intent(view.getContext(), MainActivity.class);
    			startActivityForResult(myIntent, 0);
    			finish();
    		}
    	});
    	
    	
    	//Signing up for location data
    	LocationManager mlocManager =(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, PERIOD, 0, mlocListener);
		
		///////////////////
		//Writing a mock location to test persistance and doing other DB tests
		//use table handler
		PeoplesDBHandler locHandler = new PeoplesDBHandler(getApplicationContext());
		//open to write
		locHandler.openWrite();
		
		Location loc = new Location(LocationManager.GPS_PROVIDER);
		loc.setLatitude(1);
		loc.setLongitude(2);
		loc.setTime(1234);
		
		//pass location to write
		locHandler.insertLocation(loc);
		//close
		
		if(D) Log.d(TAG, "Here are our databases after:");
		
		String[] dbs = this.databaseList();
		
		dbs = this.databaseList();
		
		if(D){ 			
			for(String s:dbs)
				Log.d(TAG, s);
		}
		
		//getting database table
		Cursor cur = locHandler.getListOfTables();
		//iterate over results if any
		if(cur != null){
			boolean next = cur.moveToFirst();
			//FIXME remove when used
			@SuppressWarnings("unused")
			String[] columnNames = cur.getColumnNames();
			int nameIndex		 = cur.getColumnIndex("name");
			String locString = "LOCATION: \n";
			while(cur.isAfterLast() == false && next){
				
				//locString = columnNames[0] + cur.getString(nameIndex);
				locString = cur.getString(nameIndex);
				
				Log.d(TAG, locString);
				next = cur.moveToNext();
			}
			cur.close();
		}
		
		//getting database schema
		cur = locHandler.getDescription();
		//iterate over results if any
		if(cur != null){
			boolean next = cur.moveToFirst();
			//FIXME remove when used
			@SuppressWarnings("unused")
			String[] columnNames = cur.getColumnNames();
			int nameIndex		 = cur.getColumnIndex("sql");
			String locString = "LOCATION: \n";
			while(cur.isAfterLast() == false && next){
				
				//locString = columnNames[0] + cur.getString(nameIndex);
				locString = cur.getString(nameIndex);
				
				Log.d(TAG, locString);
				next = cur.moveToNext();
			}
			cur.close();
		}
		locHandler.close();
		
		
		if(D) {
			Log.d(TAG, "now signing up with LocationManager");
		
			//get a location manager from the system
			LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			Log.d(TAG, "Best provider:");
			String provider = locManager.getBestProvider(new Criteria(), true);
			
			if(provider != null)
				Log.d(TAG, provider);
			else
				Log.d(TAG, "TURN GPS ON!"); //TODO turn GPS on programatically
			
			
			Log.d(TAG, "Signing up for GpsStatus:");

			locManager.addGpsStatusListener( new GPSStatusListener() );
			
			Log.d(TAG, "Is best provider enabled? " + locManager.isProviderEnabled(provider) );
		}
		////////////////
    }	
    
    /**
     * Called after your activity has been stopped, prior to it being started again.
     */
    @Override
    protected void onRestart() {
    	// TODO Auto-generated method stub
    	super.onRestart();
    }
    
    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    }
    
    /**
     * Called when the activity will start interacting with the user.
     */
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
    
    /**
     * Called when the system is about to start resuming a previous activity. 
     */
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    }
    
    /**
     * Called when the activity is no longer visible to the user, because
     * another activity has been resumed and is covering this one.
     */
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    }
    
    /**
     * The final call you receive before your activity is destroyed.
     */
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	
    	
    	super.onDestroy();
    }
    
	public class MyLocationListener implements LocationListener
	{
		public MyLocationListener() {
			super();
		}
		
		@Override
		public void onLocationChanged(Location loc)
		{
			
			Log.d("LocationListener", "onLocationChanged");
			
			Date date = new Date();
			loc.getLatitude();
			loc.getLongitude();
			String Text = "See log for all locations, here is current: \n" +
			"Latitude = " + loc.getLatitude() +
			"\nLongitude = " + loc.getLongitude() + 
			"\n" + date.toString();
			Toast.makeText( getApplicationContext(),
					Text,
					Toast.LENGTH_SHORT).show();
			
			Log.d(TAG, "+++Here are all the stored locations+++");
			
			//Get the handler
			PeoplesDBHandler dbHandler = new PeoplesDBHandler(getApplicationContext());
			//open to read
			dbHandler.openRead();
			//query
			Cursor cur = dbHandler.getStoredLocations();
			//iterate over results if any
			if(cur != null){
				String[] columnNames = cur.getColumnNames();
				//FIXEME remove when used
				@SuppressWarnings("unused")
				int		 numColumns	 = cur.getColumnCount();
				boolean next = cur.moveToFirst();
				String locString = "";
				while(cur.isAfterLast() == false && next){
					locString  = "Location: \n";		
					locString += columnNames[0]+ "\n" + cur.getInt(0) + "\n";
					locString += columnNames[1]+ "\n" + cur.getDouble(1) + "\n";
					locString += columnNames[2]+ "\n" + cur.getDouble(2) + "\n";
					locString += columnNames[3]+ "\n" + cur.getInt(3) + "\n";

					Log.d(TAG, locString);
					next = cur.moveToNext();
				}
				cur.close();
			}
			//close
			dbHandler.close();
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
			if(D) Log.d(TAG, "+++GPS onStatusChanged+++");
		}

	}/* End of Class MyLocationListener */
	
	private class GPSStatusListener implements GpsStatus.Listener {
		
	public void onGpsStatusChanged(int event) {
			
			LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			GpsStatus status = locManager.getGpsStatus(null);
			
			/**
			 * this is neat, move phone from indoors to somewhere it can get GPS signal
			 * you'll see it increase the number of satellites it can pick up and
			 * eventually fixate call status 3, when a fix is obtained.	
			 */
			Log.d(TAG, "GPS Status: " + event );
			switch(event){
				case 1: Log.d(TAG, "GPS Status: started"); break;
	
				case 2: Log.d(TAG, "GPS Status: stopped"); break;
	
				case 3: Log.d(TAG, "GPS Status: first fix at time:");
						Log.d(TAG, ""+status.getTimeToFirstFix());
						Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						Log.d(TAG, "Location object is:");
						Log.d(TAG, loc.toString() + "\n");
						
						//use table handler
						PeoplesDBHandler locHandler = new PeoplesDBHandler(getApplicationContext());
						//open to write
						locHandler.openWrite();
						//pass location to write
						long result = locHandler.insertLocation(loc);
						//close
						Log.d(TAG, "Result of db insertion: " + result );
						locHandler.close();	break;

				case 4: Log.d(TAG, "GPS Status: max satellites:");
						Log.d(TAG, ""+status.getMaxSatellites());
						Log.d(TAG, "GPS Status: but actual satellites:");
	
						int i = 0;
						for(GpsSatellite s : status.getSatellites() ){
							i++;
							Log.d(TAG, s.toString());
						}
						Log.d(TAG, "There were "+i+" satellites in total"); break;
			}
		}
	}/*End of GpsStatusListener*/

    
}
