package com.peoples.android.processTest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class LocationTestActivity extends Activity {
	
	
	/** Called when the activity is first created. */
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       
	       Intent locationIntent = new Intent(this, LocationTestService.class);
	       startService(locationIntent);
	       
	       LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	       
	       LocationListener locListener = new LocationListener() {
			
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				doSomethingWithLocation();
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
	       
	       
	       
	       
	       
	       
	       
	       
	       TextView tv = new TextView(this);
	       tv.setText("Hello, Android");
	       setContentView(tv);
	   }
}
