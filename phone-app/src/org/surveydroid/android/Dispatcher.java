/*---------------------------------------------------------------------------*
 * Dispatcher.java                                                           *
 *                                                                           *
 * Responsible for dispatching alarms to the proper services.                *
 *---------------------------------------------------------------------------*
 * Copyright 2012 Sema Berkiten, Vladimir Costescu, Henry Liu, Diego Vargas, *
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

import com.commonsware.cwac.wakeful.WakefulIntentService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 * Responsible for dispatching alarms to the proper services.
 * 
 * @author Austin Walker
 */
public class Dispatcher extends BroadcastReceiver
{
	/** logging tag */
	private static final String TAG = "Dispatcher";
	
	/** key for the intent within the received intent */
	private static final String EXTRA_INTENT =
		"org.surveydroid.android.EXTRA_INTENT";
	
	/** key for the type of the intent received */
	private static final String EXTRA_LAUNCH_TYPE =
		"org.surveydroid.android.EXTRA_LAUNCH_TYPE";
	
	//type codes
	/** used for a normal {@link Service} */
	public static final int TYPE_SERVICE = 0;
	/** used for a normal {@link Activity} */
	public static final int TYPE_ACTIVITY = 1;
	/** used if the intent is bound for broadcast receivers */
	public static final int TYPE_BROADCAST = 2;
	/** used if the intent is for a {@link WakefulIntentService} */
	public static final int TYPE_WAKEFUL_AUTO = 3;
	/**
	 * Used if the intent is for a {@link Service} for which a {@link WakeLock}
	 * needs to be acquired by this receiver.  In this case, the service should
	 * call 
	 */
	public static final int TYPE_WAKEFUL_MANUAL = 4;
	
	@Override
	public void onReceive(Context ctxt, Intent intent)
	{
		Util.i(null, TAG, "Received dispatch alarm");
		Intent i = (Intent) intent.getParcelableExtra(EXTRA_INTENT);
		int type = intent.getIntExtra(EXTRA_LAUNCH_TYPE, -1);
		switch (type)
		{
		case TYPE_SERVICE:
			Util.i(null, TAG, "starting service");
			ctxt.startService(i);
			break;
		case TYPE_ACTIVITY:
			Util.i(null, TAG, "starting activity");
			ctxt.startActivity(i);
			break;
		case TYPE_BROADCAST:
			Util.i(null, TAG, "sending broadcast");
			ctxt.sendBroadcast(i);
			break;
		case TYPE_WAKEFUL_AUTO:
			Util.i(null, TAG, "starting WakefulIntentService");
			WakefulIntentService.sendWakefulWork(ctxt, i);
			break;
		case TYPE_WAKEFUL_MANUAL:
			Util.i(null, TAG, "aquiring timed lock and starting service");
			PowerManager pm = (PowerManager) ctxt.getSystemService(Context.POWER_SERVICE);
			//FIXME need to have this coordinated with the service
			WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
			wl.acquire(5000); //service has 5 seconds to get its own lock
			ctxt.startService(i);
			break;
		case -1:
			Util.e(null, TAG, "Missing intent type!");
			//fall through
		default:
			Util.e(null, TAG, "Could not send the intent because of a bad type");
		}
	}
	
	/**
	 * Set up an alarm such that an intent is sent at a certain time.
	 * 
	 * @param ctxt
	 * @param i the intent to send
	 * @param time the time the intent is to be delivered, as would be passed
	 * to {@link AlarmManager#set(int, long, PendingIntent)}
	 * @param type determines how the given intent is broadcast, should be one
	 * of {@link Dispatcher#TYPE_ACTIVITY}, {@link Dispatcher#TYPE_SERVICE},
	 * {@link Dispatcher#TYPE_BROADCAST}, {@link Dispatcher#TYPE_WAKEFUL_AUTO},
	 * or {@link Dispatcher#TYPE_WAKEFUL_MANUAL}.
	 * @param data used to differentiate between different intents.  As per the
	 * Android documentation, an alarm that is set for an intent that is the
	 * same as a previous intent as defined by
	 * {@link Intent#filterEquals(Intent)} will override the older alarm.  This
	 * parameter is only used if it is non-null; the data from the intent will be used
	 * if it is.<br /><em>tl;dr</em>: if you call this function twice with the same data, only
	 * one alarm is set.  If data is null, i.getData() is used.
	 */
	public static void dispatch(Context ctxt, Intent i, long time, int type, Uri data)
	{
		Util.d(null, TAG, "dispatch");
		//wrap the given intent inside of a new intent bound for this class
		Intent wrapIntent = new Intent(ctxt, Dispatcher.class);
		wrapIntent.putExtra(EXTRA_INTENT, i);
		Util.d(null, TAG, "Dispatching; type = " + type);
		wrapIntent.putExtra(EXTRA_LAUNCH_TYPE, type);
		wrapIntent.setData(data == null ? i.getData() : data);
		
		//set the alarm
		PendingIntent pi = PendingIntent.getBroadcast(
			ctxt, 0, wrapIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager)
			ctxt.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, time, pi);
	}
	
	/**
	 * Cancel a previously sent dispatch
	 * 
	 * @param ctxt
	 * @param i the intent to cancel
	 * @param data as in
	 * {@link Dispatcher#dispatch(Context, Intent, long, int, Uri)}
	 */
	public static void cancel(Context ctxt, Intent i, Uri data)
	{
		Util.d(null, TAG, "cancel dispatch");
		Intent wrapIntent = new Intent(ctxt, Dispatcher.class);
		wrapIntent.setData(data == null ? i.getData() : data);
		PendingIntent pi = PendingIntent.getBroadcast(ctxt, 0, wrapIntent, 0);
		AlarmManager am = (AlarmManager)
			ctxt.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}
}
