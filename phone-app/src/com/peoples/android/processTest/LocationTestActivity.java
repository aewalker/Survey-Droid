package com.peoples.android.processTest;

import com.peoples.android.R;
import com.peoples.android.activities.MainActivity;

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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;

public class LocationTestActivity extends Activity {
	
	private static final String TAG = "LocTestActi";
    private static final boolean D = true;
	
	
	/** Called when the activity is first created. */
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.gps);
	       
	       Button back = (Button) findViewById(R.id.goback);
	        back.setText("Back to selection screen");
	        back.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
	                startActivityForResult(myIntent, 0);
	                finish();
	            }

	        });
	       
	       doTheRest();
	       
	       
	   }


	private void doTheRest() {
		/*private void doTheRest(final TextView tv) {*/
		
//		TODO: Will make this an activity, and will move GPS gathering to a Service
//		Intent locationIntent = new Intent(this, LocationTestService.class);
//		startService(locationIntent);

		//get a location manager from the system
		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// create a Listener interface that will handle the GPS location update
		LocationListener locListener = new MyLocationListener(); 
		
		//subscribe our Listener to the locManager
		/*locManager.addGpsStatusListener(gpsLocListener);*/
		locManager.requestLocationUpdates("gps", 0, 0, locListener);
		
	}
	
	public class MyLocationListener implements LocationListener
	{
		private int i = 0;

		@Override
		public void onLocationChanged(Location loc)
		{
			i++;
			if (i == 1)
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
				i = 0;
			}
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
