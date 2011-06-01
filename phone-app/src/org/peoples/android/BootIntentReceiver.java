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
    }
}
