package com.peoples.android;

import com.peoples.android.services.CallLogService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * 
 * Used to launch processes during development and testing
 * 
 * @author Vlad
 *
 */
public class Peoples extends Activity {	
    // Debugging
	// TEST
	//TEST
    private static final String TAG = "PEOPLES";
    private static final boolean D = true;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(D) Log.e(TAG, "+++ ON CREATE +++");
        
        // Set up the window layout
        setContentView(R.layout.main);
        
        //this.startService(new Intent(this, BootService.class));
        this.startService(new Intent(this, CallLogService.class));

    }
}