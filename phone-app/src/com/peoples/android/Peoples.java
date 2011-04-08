package com.peoples.android;

import com.peoples.android.services.CallLogService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        setContentView(R.layout.shortresponseview);
        
        
        final EditText edittext = (EditText) findViewById(R.id.editText1);
        edittext.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                  // Perform action on key press
                  Toast.makeText(Peoples.this, edittext.getText(), Toast.LENGTH_SHORT).show();
                  return true;
                }
                return false;
            }

        });

	//testing ui above        
//        this.startService(new Intent(this, BootService.class));
//        this.startService(new Intent(this, CallLogService.class));



    }
}