package com.peoples.android.processTest;

import com.peoples.android.R;
import com.peoples.android.activities.MainActivity;
import com.peoples.android.services.GPSLocationService;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus.Listener;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;
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
    	
    	//TODO: start GPS process
    	//TODO: or sign it up with the alarm service to run it every 15 or w.e. seconds
    	AlarmManager alarmManager	= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent gpsServiceIntent 	= new Intent(getApplicationContext(), GPSLocationService.class);
		
		//TODO: probably not the best way to getApplicaitonContext()
		//TODO: sort out which flag to send
		PendingIntent updateGPSDB = PendingIntent.getService(getApplicationContext(),
																0,
																gpsServiceIntent,
																PendingIntent.FLAG_UPDATE_CURRENT);
		//TODO: first argument determines whether to wake phone up, must consider battery life with final call
		//TODO: 3rd argument is time to fi
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
									SystemClock.elapsedRealtime(),
									30*1000,
									updateGPSDB);
		
    	Button back = (Button) findViewById(R.id.goback);
    	back.setText("Back to selection screen");
    	back.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View view) {
    			Intent myIntent = new Intent(view.getContext(), MainActivity.class);
    			startActivityForResult(myIntent, 0);
    			finish();
    		}

    	});
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
    
    
}
