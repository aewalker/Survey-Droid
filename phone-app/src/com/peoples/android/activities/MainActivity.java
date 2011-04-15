package com.peoples.android.activities;

import com.peoples.android.R;
import com.peoples.android.processTest.LocationTestActivity;
import com.peoples.android.services.GPSLocationService;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	// Debugging
	// TEST
	//TEST
    private static final String TAG = "MainActivity";
    private static final boolean D = true;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        
        setContentView(R.layout.main);

        Button survey = (Button) findViewById(R.id.Enter);
        survey.setText("Take a sample survey");
        survey.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), SampleQuestionActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
        
        Button gps = (Button) findViewById(R.id.GPS);
        gps.setText("Tell me my location every 15 seconds!");
        gps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), LocationTestActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
        
        Context context = getApplicationContext();
        
        Intent gpsIntent = new Intent(context, GPSLocationService.class);
        gpsIntent = gpsIntent.setClass(context, GPSLocationService.class);

        context.startService(gpsIntent);
        
    }

}
