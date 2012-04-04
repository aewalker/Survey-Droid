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
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import org.surveydroid.android.coms.ComsService;
import org.surveydroid.android.survey.SurveyScheduler;

import com.commonsware.cwac.locpoll.LocationPoller;
import com.commonsware.cwac.wakeful.WakefulIntentService;

/*
 * TODO
 * This whole things should be rebuilt.  In particular, the use of handlers
 * needs to be removed and replaced with a series of callbacks from the
 * relevant services via broadcasts.
 */

/**
 * Starts the background Survey Droid services after at boot time.
 * 
 * @author Austin Walker
 */
public class BootIntentReceiver extends BroadcastReceiver
{
	protected static final String TAG = "BootIntentReceiver";
	
	/** Config key; if true, the app is started */
	public static final String STARTED_KEY = "started";
	
    @Override
    public void onReceive(Context context, Intent intent)
    {
    	if (!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
    	{
			Util.w(null, TAG, "Wrong action: " + intent.getAction());
			return;
    	}
    	/*
    	 * Here, we delay the startup of the application for a while.  The
    	 * main reason for this is to allow the device that the app is running
    	 * on to update it's clock.  It also avoids problems where required
    	 * system services have not been started up in time.
    	 */
    	final Context c = context;
		Config.putSetting(context, STARTED_KEY, false);
    	Runnable level1 = new Runnable()
    	{
			@Override
			public void run()
			{
				startup(c);
			}
    	};
    	Handler h = new Handler();
    	h.postDelayed(level1, 10 * 1000); //delay 10 seconds
    }
    
    /**
     * Starts up the basic Survey Droid services.
     * 
     * @param context - the {@link Context} given to the receiver
     */
    public static synchronized void startup(final Context context)
    {
		
    	if (Config.getSetting(context, STARTED_KEY, false))
    	{
    		Util.w(null, TAG, "Already started; aborting");
    		return;
    	}
    	Util.i(null, TAG, "+++Starting Survey Droid+++");
    	Config.putSetting(context, STARTED_KEY, true);
    	
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
        
        /*
         * We only need the configuration values to be loaded in order to
         * start the tracking services.  Since those values are the first thing
         * loaded by the pull, we can start these rather quickly.
         */
        Runnable level2 = new Runnable()
    	{
			@Override
	        public void run()
	        {
				//start call monitoring
		        Util.d(null, TAG, "Starting call monitoring");
		        TelephonyManager tm = (TelephonyManager)
		        	context.getSystemService(Context.TELEPHONY_SERVICE);
		        tm.listen(new CallTracker(context),
		        		PhoneStateListener.LISTEN_CALL_STATE);
		        
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
	        }
    	};
        
        /*
         * Delay push and survey scheduler until everything has been pulled.
         */
        Runnable level3 = new Runnable()
    	{
			@Override
	        public void run()
	        {
		        //start the coms service pushing
		        Util.d(null, TAG, "Starting push service");
		        Intent comsPushIntent = new Intent(context, ComsService.class);
		        comsPushIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
		        comsPullIntent.putExtra(ComsService.EXTRA_RUNNING_TIME,
		        		System.currentTimeMillis());
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
	        }
    	};
    	Handler h = new Handler();
    	h.postDelayed(level2, 30 * 1000);
    	h.postDelayed(level3, 2 * 60 * 1000);
    }
}
