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
import android.location.LocationManager;
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
    		throw new RuntimeException("Wrong action: " + intent.getAction());
    	startup(context);
    }
    
    //FIXME FOR TESTING ONLY - move back to onReceive when done
    public void startup(Context context)
    {
    	Log.i(TAG, "+++Starting PEOPLES+++");
        
        //start the coms service pulling
        if (Config.D) Log.d(TAG, "Starting pull service");
        Intent comsPullIntent = new Intent(context, ComsService.class);
        comsPullIntent.setAction(ComsService.ACTION_DOWNLOAD_DATA);
        comsPullIntent.putExtra(ComsService.EXTRA_RUNNING_TIME,
        		Calendar.getInstance().getTimeInMillis());
        comsPullIntent.putExtra(ComsService.EXTRA_REPEATING, true);
        context.startService(comsPullIntent);
        
        //start the coms service pushing
        if (Config.D) Log.d(TAG, "Starting push service");
        Intent comsPushIntent = new Intent(context, ComsService.class);
        comsPushIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
        comsPullIntent.putExtra(ComsService.EXTRA_RUNNING_TIME,
        		Calendar.getInstance().getTimeInMillis());
        Log.d(TAG, "TIME: " + Calendar.getInstance().getTimeInMillis());
        comsPushIntent.putExtra(ComsService.EXTRA_REPEATING, true);
        context.startService(comsPushIntent);
        
        //start call monitoring
        if (Config.D) Log.d(TAG, "Starting call monitoring");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(
        		Context.TELEPHONY_SERVICE);
        tm.listen(new CallTracker(context),
        		PhoneStateListener.LISTEN_CALL_STATE);
        
        //start location tracking
        if (Config.D) Log.d(TAG, "Starting location tracking");
        LocationManager lm = (LocationManager) context.getSystemService(
        		Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
        		Config.LOCATION_INTERVAL * 60 * 1000, 0,
        		new LocationTracker(context));
    	
    	//start the survey scheduler
    	if (Config.D) Log.d(TAG, "Starting survey scheduler");
    	Intent schedulerIntent = new Intent(context, SurveyScheduler.class);
    	schedulerIntent.setAction(SurveyScheduler.ACTION_SCHEDULE_SURVEYS);
    	schedulerIntent.putExtra(SurveyScheduler.EXTRA_RUNNING_TIME,
    			Calendar.getInstance().getTimeInMillis());
        context.startService(schedulerIntent);
    }
}
