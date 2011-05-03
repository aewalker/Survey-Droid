package com.peoples.android.activities;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        if(D) Log.d(TAG, "+++ ON CREATE main activity +++");

        setContentView(R.layout.main);

        Log.d(TAG, "Fetching surveys");
        Pull.syncWithWeb(this);

        final TextView q = (TextView) this.findViewById(R.id.msg);
        q.setText("You have a new survey awaiting");
        
        Button sample = (Button) findViewById(R.id.Enter);
        sample.setText("Click here to take the survey now");
        sample.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Peoples.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

        });

        Button postpone = (Button) findViewById(R.id.postpone);
        postpone.setText("Postpone the survey for an hour");
        postpone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Intent myIntent = new Intent(view.getContext(), LocationTestActivity.class);
                //startActivityForResult(myIntent, 0);
            	
            	//##########################//
            	//here's where you would call the service to postpone the survey//
            	//##########################//
            	Toast.makeText(getApplicationContext(), "Implement me in your service yo!",
                        Toast.LENGTH_SHORT).show();
            	finish();
            	
            }
        });
        
        
        //TODO: CANT FIND SYNC VIEW!
        
//        Button sync = (Button) findViewById(R.id.Sync);
//        sync.setText("Sync the surveys manually, to update deploy time or questions");
//        sync.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                Intent myIntent = new Intent(view.getContext(), LocationTestActivity.class);
//                startActivityForResult(myIntent, 0);
//            }
//        });

        //This is just code to test GPS location gathering and persisting to database
        //starts the Activity I"m interested in testing w/o waiting for button handler
        //Context context = this;
        //Intent gpsIntent = new Intent(context, LocationTestActivity.class);
        //gpsIntent = gpsIntent.setClass(context, LocationTestActivity.class);
        //context.startActivity(gpsIntent);

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
