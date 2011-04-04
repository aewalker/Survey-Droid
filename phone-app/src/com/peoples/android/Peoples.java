package com.peoples.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

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
        TextView tv = new TextView(this);
        tv.setText("hi");
        setContentView(tv);
    }
}