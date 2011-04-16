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
	       
	       
	       
	       
	   }


	
	
	
	
	
}
