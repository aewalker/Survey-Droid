package com.peoples.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


/**
 * 
 * TODO:
 * 
 * Will run on phone boot and:
 * 
 * 1. needs to schedule master service
 * 2. 
 * 
 * 
 * 
 * @author diego
 *
 */
public class BootService extends IntentService {
	
	protected static final String TAG = "BootService";
	protected static final boolean D = true;

    public BootService() {
        super(BootService.class.getName());
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    	
    	if(D) Log.e(TAG, "oh hi in bootService");
    	
        // TODO Auto-generated method stub
        Toast.makeText(this, "BootService Started", Toast.LENGTH_LONG).show();
        
        long endTime = System.currentTimeMillis() + 30*1000;
        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    wait(endTime - System.currentTimeMillis());
                } catch (Exception e) {
                }
            }
        }
    }
    
}
