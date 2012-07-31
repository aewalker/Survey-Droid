/*---------------------------------------------------------------------------*
 * CallReceiver.java                                                         *
 *                                                                           *
 * Dispatches calls to the call tracker.                                     *
 *---------------------------------------------------------------------------*
 * Copyright (C) 2011-2012 Survey Droid Contributors                         *
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
package org.survey_droid.survey_droid;

import org.survey_droid.survey_droid.annotation.ConfigKey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;

/**
 * Receives system broadcasts that the phone state has changed and triggers the
 * {@link CallTracker}.
 * 
 * @author Austin Walker
 */
public class CallReceiver extends BroadcastReceiver
{
	/** Config key; true if the phone is in a call */
	@ConfigKey(value="false", global=true)
	private static final String IN_CALL = "phone_in_call";
	
	/** delay before searching the log */
	private static final long delay = 2 * 1000;
	
	/** logging tag */
	private static final String TAG = "CallReceiver";
	
	/**
	 * Resets the call state; call when the phone starts up
	 * 
	 * @param c
	 */
	public static void resetCallState(Context c)
	{
		Config.putSetting(c, IN_CALL, false);
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (!intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
		{
			Util.w(null, TAG, "Wrong intent action: " + intent.getAction());
			return;
		}
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		if (state == null)
		{
			Util.e(null, TAG, "Call state information missing!");
			return;
		}
		Util.d(null, TAG, "Call state changed: " + state);
		if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
		{
			try
			{
				if (Config.getBoolean(context, IN_CALL))
				{
					//schedule the alarm to run the service
					Util.d(null, TAG, "call over; scheduling lookup");
					Intent searchIntent = new Intent(context, CallTracker.class);
					searchIntent.setAction(CallTracker.ACTION_SEARCH_LOG);
					Uri data = Uri.parse("call tracker search intent");
					long time = System.currentTimeMillis() + delay;
					Dispatcher.dispatch(context, searchIntent, time,
						Dispatcher.TYPE_WAKEFUL_AUTO, data);
				}
			}
			finally
			{
				Config.putSetting(context, IN_CALL, false);
			}
		}
		else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING) ||
				 state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
		{
			if (!Config.getBoolean(context, IN_CALL))
			{
				Util.d(null, TAG, "call started");
				Config.putSetting(context, IN_CALL, true);
			}
		}
		else
		{
			Util.e(null, TAG, "Bad phone state: " + state);
			return;
		}
	}
}
