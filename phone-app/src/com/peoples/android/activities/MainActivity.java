package com.peoples.android.activities;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.peoples.android.Peoples;
import com.peoples.android.R;
import com.peoples.android.model.Survey;
import com.peoples.android.processTest.LocationTestActivity;
import com.peoples.android.server.Pull;
import com.peoples.android.services.GPSLocationService;



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

        Log.d(TAG, "Fetching surveys");

        Pull.syncWithWeb(this);

        Button sample = (Button) findViewById(R.id.Enter);
        sample.setText("Take a sample survey");
        sample.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Peoples.class);
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

        //This is just code to test GPS location gathering and persisting to database
        Context context = getApplicationContext();
        Intent gpsIntent = new Intent(context, GPSLocationService.class);
        gpsIntent = gpsIntent.setClass(context, GPSLocationService.class);
        context.startService(gpsIntent);

    }

}
