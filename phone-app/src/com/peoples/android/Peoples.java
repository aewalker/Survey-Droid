package com.peoples.android;

import android.app.Activity;
import android.os.Bundle;

public class Peoples extends Activity {	
    // Debugging
    private static final String TAG = "PEOPLES";
    private static final boolean D = true;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}