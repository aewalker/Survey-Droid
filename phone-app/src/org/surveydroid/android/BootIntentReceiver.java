/*---------------------------------------------------------------------------*
 * BootIntentReceiver.java                                                   *
 *                                                                           *
 * Starts/schedules the various services when the phone is started.          *
 *---------------------------------------------------------------------------*
 * Copyright 2011 Sema Berkiten, Vladimir Costescu, Henry Liu, Diego Vargas, *
 * Austin Walker, and Tony Xiao                                              *
 *                                                                           *
 * This file is part of Survey Droid.                                        *
 *                                                                           *
 * Survey Droid is free software: you can redistribute it and/or modify      *
 * it under the terms of the GNU General Public License as published by      *
 * the Free Software Foundation, either version 3 of the License, or         *
 * (at your option) any later version.                                       *
 *                                                                           *
 * Survey Droid is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of            *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             *
 * GNU General Public License for more details.                              *
 *                                                                           *
 * You should have received a copy of the GNU General Public License         *
 * along with Survey Droid.  If not, see <http://www.gnu.org/licenses/>.     *
 *****************************************************************************/
package org.surveydroid.android;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.os.SystemClock;

import org.surveydroid.android.coms.ComsService;
import org.surveydroid.android.survey.SurveyScheduler;

import com.commonsware.cwac.locpoll.LocationPoller;
import com.commonsware.cwac.wakeful.WakefulIntentService;

/**
 * Starts the background Survey Droid services after at boot time.
 * 
 * @author Austin Walker
 */
public class BootIntentReceiver extends BroadcastReceiver
{
	protected static final String TAG = "BootIntentReceiver";
	
	/** Config key; holds the run level that we are at */
	public static final String STARTED_KEY = "started";
	
	/* intent actions */
	/**
	 * Used by the pull service to tell the startup process that the initial
	 * pull is complete
	 */
	public static final String ACTION_PULL_COMPLETE =
		"org.surveydroid.android.ACTION_PULL_COMPLETE";
	
	
	
    @Override
    public void onReceive(Context context, Intent intent)
    {
    	String action = intent.getAction();
    	if (action.equals(Intent.ACTION_BOOT_COMPLETED))
    	{
    		Config.putSetting(context, STARTED_KEY, 0);
    		startup(context);
    	}
    	else if (action.equals(ACTION_PULL_COMPLETE))
    	{
    		if (Config.getSetting(context, STARTED_KEY, 0) == 1)
    			runLevel2(context);
    	}
    	else
    	{
			Util.w(null, TAG, "Wrong action: " + intent.getAction());
			return;
    	}
    }
    
    /**
     * Starts up the basic Survey Droid services.
     * 
     * @param context - the {@link Context} given to the receiver
     */
    public static synchronized void startup(Context context)
    {
		
    	if (Config.getSetting(context, STARTED_KEY, 0) != 0)
    	{
    		Util.w(null, TAG, "Already started or starting; aborting");
    		return;
    	}
    	Util.i(null, TAG, "+++Starting Survey Droid+++");
    	
    	//set debugging based on the package information
    	PackageManager pm = context.getPackageManager();
		ApplicationInfo ai = new ApplicationInfo();
		try
		{
			ai = pm.getApplicationInfo(context.getPackageName(), 0);
			if ((ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) ==
			        ApplicationInfo.FLAG_DEBUGGABLE )
			{
				Config.D = true;
			}
			else
			{
				Config.D = false;
			}
			Util.i(null, "Config", "Application " + (Config.D ? "is" : "isn't") + " debuggable");
		}
		catch (NameNotFoundException e)
		{
			Util.e(null, TAG, "Invalid package name?!?");
		}
		runLevel1(context);
    }
    
    /**
     * The first run level of startup activities.
     * 
     * @param context
     */
    private static void runLevel1(Context context)
    {
    	//make sure we have the salt
		if (Config.getSetting(context, Config.SALT, null) == null)
		{
			Util.d(null, TAG, "Getting salt");
			Intent saltIntent = new Intent(context, ComsService.class);
			saltIntent.setAction(ComsService.ACTION_GET_SALT);
			WakefulIntentService.sendWakefulWork(context, saltIntent);
		}
        
        //start the coms service pulling
        Util.d(null, TAG, "Starting pull service");
        final Intent comsPullIntent = new Intent(context, ComsService.class);
        comsPullIntent.setAction(ComsService.ACTION_DOWNLOAD_DATA);
        comsPullIntent.putExtra(ComsService.EXTRA_RUNNING_TIME,
        		System.currentTimeMillis());
        comsPullIntent.putExtra(ComsService.EXTRA_REPEATING, true);
        WakefulIntentService.sendWakefulWork(context, comsPullIntent);
        
		//start call logging
		Util.d(null, TAG, "Starting call monitoring");
		Intent callLogIntent = new Intent(context, CallTracker.class);
		callLogIntent.setAction(CallTracker.ACTION_START_TRACKING);
		WakefulIntentService.sendWakefulWork(context, callLogIntent);
        
        Config.putSetting(context, STARTED_KEY, 1);
    }
    
    /**
     * The second level of startup activities
     * 
     * @param context
     */
	private static void runLevel2(Context context)
    {
        //start location tracking
        Util.d(null, TAG, "Starting location tracking");
        //make sure to invalidate any old location entry
        Config.putSetting(context, LocationTracker.TIMES_SENT, Integer.MAX_VALUE);
        //code adapted from https://github.com/commonsguy/cwac-locpoll
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(context, LocationPoller.class);
        i.putExtra(LocationPoller.EXTRA_INTENT,
                             new Intent(context, LocationTracker.class));
        i.putExtra(LocationPoller.EXTRA_PROVIDER,
                             LocationManager.GPS_PROVIDER);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        long period = Config.getSetting(context, Config.LOCATION_INTERVAL,
        		Config.LOCATION_INTERVAL_DEFAULT) * 60 * 1000;
        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
        		SystemClock.elapsedRealtime(), period, pi);
        
        //start the coms service pushing
        Util.d(null, TAG, "Starting push service");
        Intent comsPushIntent = new Intent(context, ComsService.class);
        comsPushIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
        comsPushIntent.putExtra(ComsService.EXTRA_REPEATING, true);
        WakefulIntentService.sendWakefulWork(context, comsPushIntent);
        
    	//start the survey scheduler
    	Util.d(null, TAG, "Starting survey scheduler");
    	Intent schedulerIntent = new Intent(context,
    			SurveyScheduler.class);
    	schedulerIntent.setAction(
    			SurveyScheduler.ACTION_SCHEDULE_SURVEYS);
    	schedulerIntent.putExtra(SurveyScheduler.EXTRA_RUN_AGAIN, true);
    	WakefulIntentService.sendWakefulWork(context, schedulerIntent);
    	
    	Config.putSetting(context, STARTED_KEY, 2);
    	Util.i(null, TAG, "+++Startup Complete+++");
    }
}
