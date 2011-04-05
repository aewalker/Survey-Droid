package com.peoples.android.processTest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus.Listener;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.TextView;

public class LocationTestActivity extends Activity {
	
	private static final String TAG = "LocTestActi";
    private static final boolean D = true;
	
	
	/** Called when the activity is first created. */
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       final TextView tv = new TextView(this);
	       tv.setText("hi");
	       setContentView(tv);
	       
	       
	       try{
	    	   doTheRest(tv);
	       } catch (Exception e){
	    	   e.printStackTrace();
	    	   tv.setText(tv.getText() + "\n" + e.fillInStackTrace().toString());
	    	   setContentView(tv);
	       }
	       
	       tv.setText(tv.getText() + "\n" + "bye");
	       
	   }


	private void doTheRest(final TextView tv) {
		// TODO Auto-generated method stub
		
//		TODO: Will make this an activity, and will move GPS gathering to a Service
//		Intent locationIntent = new Intent(this, LocationTestService.class);
//		startService(locationIntent);
		
		

		//get a location manager from the system
		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// create a Listener interface that will handle the GPS location update
		
		LocationListener locListener = new LocationListener() {
			
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				
				Location gpsLocation = locManager.getLastKnownLocation("gps");
				
				String locString = String.format("gps location: %s  ", gpsLocation.toString() );
				tv.setText(locString);
				setContentView(tv);
			}
			
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				
			}
		};
		
		GpsStatus.Listener gpsLocListener = new Listener() {
			
			public void onGpsStatusChanged(int event) {
				// TODO Auto-generated method stub
				
				LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				
				Location gpsLocation = locManager.getLastKnownLocation("gps");
				
				String locString = String.format("gps location: %s  ", gpsLocation.toString() );
				tv.setText(locString);
				setContentView(tv);
			}
		};
		
		//subsccribe our Listener to the locManager
		locManager.addGpsStatusListener(gpsLocListener);
		locManager.requestLocationUpdates("gps", 0, 0, locListener);
		
		
		
		
		
	}
}
