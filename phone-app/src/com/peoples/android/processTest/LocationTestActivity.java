package com.peoples.android.processTest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
	       TextView tv = new TextView(this);
	       tv.setText("hi");
	       setContentView(tv);
	       
	       Intent locationIntent = new Intent(this, LocationTestService.class);
	       startService(locationIntent);
	       
	       LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	       
	       //unnecessary
//	       LocationProvider networkLocProvider	= locManager.getProvider(LocationManager.NETWORK_PROVIDER);
//	       LocationProvider gpsLocProvider		= locManager.getProvider(LocationManager.GPS_PROVIDER);
	       
	       Location netLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	       Location gpsLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	       
	       LocationListener locListener = new LocationListener() {
			
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				//doSomethingWithLocation();
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
	       
	       
	       
	       
	       
	       String locString = String.format("net location: %s \n gps location: %s  ", netLocation.toString(), gpsLocation.toString());
	       tv.setText(locString);
	       setContentView(tv);
	   }
}
