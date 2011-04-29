package com.peoples.android.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
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
	
	private static long COORDINATOR_PERIOD = 10*1000;
	
    public BootService() {
        super(BootService.class.getName());
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    	
    	if(D) Log.e(TAG, "onHandleIntent");
    	
        // TODO Auto-generated method stub
        Toast.makeText(this, "BootService Started", Toast.LENGTH_LONG).show();
        
        
        //TODO: schedule master service
        //TODO: begin GPS collection
        //TODO: begin call log collection
        
        
        //implementing master service scheduling
        //TODO: set COORDINATOR_PERIOD programatically
    	AlarmManager alarmManager			= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    	Intent coordinatorIntent			= new Intent(this, CoordinatorService.class);
        //TODO: write a more proper Action, or use provided ones
    	coordinatorIntent.setAction(BootService.class.getName());
    	PendingIntent pendingCoordinator	= PendingIntent.getService(getApplicationContext(), 0,
    											coordinatorIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	
    	// probably not the best way to getApplicaitonContext()
		// sort out which flag to send
    	
    	//first argument determines whether to wake phone up, must consider battery life with final call
		//3rd argument is time to fi
    	alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime(),
				COORDINATOR_PERIOD,
				pendingCoordinator);        
   
    }
    
}
