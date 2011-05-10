package com.peoples.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.peoples.android.R;
import com.peoples.android.model.SurveyIntent;
import com.peoples.android.services.CoordinatorService;

/**
 * The Activity for the administration panel of the PEOPLES application.
 * @author Henry Liu
 */
public class Menu extends Activity {

	// Debugging
    private static final String TAG = "Menu";
    private static final boolean D = true;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context panda = this;

        if(D) Log.e(TAG, "+++ ON CREATE menu activity +++");
        
        //TODO: write a more proper Action, or use provided ones
        //let the coordinator service take care of sync stuff
        Intent coordinatorIntent			= new Intent(this, CoordinatorService.class);
    	coordinatorIntent.setAction(MainActivity.class.getName());
        startService(coordinatorIntent);
        
        //setting the layout of the activity
        setContentView(R.layout.main);

        final TextView q = (TextView) this.findViewById(R.id.title);
        q.setText("Welcome to PEOPLES!");
        
        Button sample = (Button) findViewById(R.id.sample);
        sample.setText("Click here to take a sample survey");
        sample.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SurveyIntent myIntent = new SurveyIntent(view.getContext(), 4, 1104, MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
        
        Button settings = (Button) findViewById(R.id.settings);
        settings.setText("Settings");
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), PeoplesSettings.class);
                startActivityForResult(myIntent, 0);
            	
            }
        });
        
        Button phoneID = (Button) findViewById(R.id.uniqueID);
        phoneID.setText("Find this phone's unique ID");
        phoneID.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	TelephonyManager tManager = (TelephonyManager)panda.getSystemService(Context.TELEPHONY_SERVICE);
            	String uid = tManager.getDeviceId();
            	
            	Toast.makeText(getApplicationContext(), uid,
                        Toast.LENGTH_LONG).show();

            }
        });
        
        Button quit = (Button) findViewById(R.id.quit);
        quit.setText("Exit PEOPLES");
        quit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
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
