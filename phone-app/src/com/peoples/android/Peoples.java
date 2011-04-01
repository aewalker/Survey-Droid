package com.peoples.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class Peoples extends Activity {
    // Debugging
    private static final String TAG = "PEOPLES";
    private static final boolean D = true;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        
        // Set up the window layout
        setContentView(R.layout.main);
    }
}