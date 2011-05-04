package com.peoples.android.activities;

//import java.util.List;

import android.app.Activity;
//import android.content.Context;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.peoples.android.Peoples;
import com.peoples.android.R;
//import com.peoples.android.model.Survey;
//import com.peoples.android.processTest.LocationTestActivity;
//import com.peoples.android.server.Pull;
//import com.peoples.android.services.BootService;
import com.peoples.android.model.Survey;
import com.peoples.android.services.CoordinatorService;
//import com.peoples.android.services.GPSLocationService;



public class PeoplesSettings extends Activity {

	// Debugging
	// TEST
	//TEST
    private static final String TAG = "Menu";
    private static final boolean D = true;
    //private Survey survey;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context panda = this;

        if(D) Log.e(TAG, "+++ ON CREATE settings activity +++");

        setContentView(R.layout.settings);

        Bundle extras = getIntent().getExtras(); 
        final ToggleButton one = (ToggleButton) findViewById(R.id.toggleButton1);
        one.setChecked(extras.getBoolean("one"));
        final ToggleButton two = (ToggleButton) findViewById(R.id.toggleButton2);
        two.setChecked(extras.getBoolean("two"));
        final ToggleButton three = (ToggleButton) findViewById(R.id.toggleButton3);
        three.setChecked(extras.getBoolean("three"));
        
        Button save = (Button) findViewById(R.id.save);
        save.setText("Save Changes");
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	//for now, toast prints what needs to be done attn:Tony
            	StringBuilder info = new StringBuilder();
            	
            	info.append("GPS is ");
            	if (one.isChecked()) {
            		//enable GPS!
            		info.append("enabled.\n");
            	}
            	else {
            		//disable GPS!
            		info.append("disabled.\n");
            	}
            	
            	info.append("Call Logs are ");
            	if (two.isChecked()) {
            		//enable Call Logs!
            		info.append("enabled.\n");
            	}
            	else {
            		//disable Call Logs!
            		info.append("disabled.\n");
            	}
            	
            	info.append("Surveys are ");
            	if (three.isChecked()) {
            		//enable Surveys
            		info.append("enabled.\n");
            	}
            	else {
            		//disable Surveys
            		info.append("disabled.\n");
            	}
            	
            	Toast.makeText(getApplicationContext(), info.toString(),
                        Toast.LENGTH_SHORT).show();
            	finish();
            }
        });
        
        Button back = (Button) findViewById(R.id.back);
        back.setText("Return to Main Menu");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	//do nothing
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
