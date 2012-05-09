package org.surveydroid.android;

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
			if (Config.getSetting(context, IN_CALL, false))
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
			Config.putSetting(context, IN_CALL, false);
		}
		else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING) ||
				 state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
		{
			if (!Config.getSetting(context, IN_CALL, false))
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
