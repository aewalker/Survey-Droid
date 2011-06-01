/*---------------------------------------------------------------------------*
 * BootIntentReceiver.java                                                   *
 *                                                                           *
 * Starts/schedules the various services when the phone is started.          *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.peoples.android.coms.ComsService;
import org.peoples.android.survey.SurveyScheduler;

public class BootIntentReceiver extends BroadcastReceiver {
    
	protected static final String TAG = "PEOPLES BootIntentReceiver";
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	Log.i(TAG, "+++Starting PEOPLES+++");
    	
    	//start the survey scheduler
    	Intent schedulerIntent = new Intent(context, SurveyScheduler.class);
    	schedulerIntent.setAction(SurveyScheduler.ACTION_SCHEDULE_SURVEYS);
    	schedulerIntent.putExtra(SurveyScheduler.EXTRA_RUNNING_TIME,
    			Calendar.getInstance().getTimeInMillis());
        context.startService(schedulerIntent);
        
        //start the coms service pulling
        Intent comsPullIntent = new Intent(context, ComsService.class);
        comsPullIntent.setAction(ComsService.ACTION_DOWNLOAD_DATA);
        comsPullIntent.putExtra(ComsService.EXTRA_REPEATING, true);
        context.startService(comsPullIntent);
        
        //start the coms service pushing
        Intent comsPushIntent = new Intent(context, ComsService.class);
        comsPushIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
        comsPushIntent.putExtra(ComsService.EXTRA_REPEATING, true);
        context.startService(comsPushIntent);
    }
}
