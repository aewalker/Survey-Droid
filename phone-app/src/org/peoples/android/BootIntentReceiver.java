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
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.peoples.android.coms.ComsService;
import org.peoples.android.survey.SurveyScheduler;

/**
 * Starts the background PEOPLES services after at boot time.
 * 
 * @author Austin Walker
 */
public class BootIntentReceiver extends BroadcastReceiver
{
	protected static final String TAG = "PEOPLES BootIntentReceiver";
	
    @Override
    public void onReceive(Context context, Intent intent)
    {
    	if (!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
    	{
    		if (Config.D)
    		{
    			throw new RuntimeException("Wrong action: "
    				+ intent.getAction());
    		}
    		else
    		{
    			Log.w(TAG, "Wrong action: " + intent.getAction());
    			return;
    		}
    	}
    	startup(context);
    }
    
    //FIXME FOR TESTING ONLY - move back to onReceive when done
    public static void startup(Context context)
    {
    	Util.i(null, TAG, "+++Starting PEOPLES+++");
        
        //start the coms service pulling
        Util.d(null, TAG, "Starting pull service");
        Intent comsPullIntent = new Intent(context, ComsService.class);
        comsPullIntent.setAction(ComsService.ACTION_DOWNLOAD_DATA);
        comsPullIntent.putExtra(ComsService.EXTRA_RUNNING_TIME,
        		Calendar.getInstance().getTimeInMillis());
        comsPullIntent.putExtra(ComsService.EXTRA_REPEATING, true);
        context.startService(comsPullIntent);
        
        //start the coms service pushing
        Util.d(null, TAG, "Starting push service");
        Intent comsPushIntent = new Intent(context, ComsService.class);
        comsPushIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
        comsPullIntent.putExtra(ComsService.EXTRA_RUNNING_TIME,
        		Calendar.getInstance().getTimeInMillis());
        comsPushIntent.putExtra(ComsService.EXTRA_REPEATING, true);
        context.startService(comsPushIntent);
        
        //start call monitoring
        Util.d(null, TAG, "Starting call monitoring");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(
        		Context.TELEPHONY_SERVICE);
        tm.listen(new CallTracker(context),
        		PhoneStateListener.LISTEN_CALL_STATE);
        
        //start location tracking
        Util.d(null, TAG, "Starting location tracking");
        Intent trackingIntent =
        	new Intent(context, LocationTrackerService.class);
        trackingIntent.setAction(LocationTrackerService.ACTION_START_TRACKING);
        context.startService(trackingIntent);
    	
    	//start the survey scheduler
    	Util.d(null, TAG, "Starting survey scheduler");
    	Intent schedulerIntent = new Intent(context, SurveyScheduler.class);
    	schedulerIntent.setAction(SurveyScheduler.ACTION_SCHEDULE_SURVEYS);
    	schedulerIntent.putExtra(SurveyScheduler.EXTRA_RUNNING_TIME,
    			Calendar.getInstance().getTimeInMillis());
        context.startService(schedulerIntent);
    }
}
