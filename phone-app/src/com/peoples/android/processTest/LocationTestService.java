/**
 * 
 */
package com.peoples.android.processTest;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.app.Activity;
import android.content.Context;
import android.location.GpsStatus.Listener;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author diego
 * http://developer.android.com/guide/topics/fundamentals/services.html
 * above link explains why IntentService is easier to use. Works fine for demo
 * app
 * 
 * 
 */
public class LocationTestService extends IntentService {

	public LocationTestService(String name) {
		super("LocationTestService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		LocationManager mlocManager =(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
	}
	
	/* Class My Location Listener */
	public class MyLocationListener implements LocationListener
	{

		@Override
		public void onLocationChanged(Location loc)
		{
			loc.getLatitude();
			loc.getLongitude();
			String Text = "My current location is: " +
			"Latitud = " + loc.getLatitude() +
			"Longitud = " + loc.getLongitude();
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
		}

	}/* End of Class MyLocationListener */

	
	
	
	
	


}
