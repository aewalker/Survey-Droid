/*---------------------------------------------------------------------------*
 * CallTracker.java                                                          *
 *                                                                           *
 * Handles incoming and outgoing calls and logs them in the database.        *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import java.util.Calendar;

import org.peoples.android.database.TrackingDBHandler;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Responsible for logging calls.  Catches the system broadcast when a call is
 * going out or coming in and logs it.
 * 
 * @author Austin Walker
 */
public class CallTracker extends PhoneStateListener
{
	//logging tag
	private static final String TAG = "CallTracker";
	
	//is the phone in a call?
	private boolean inCall = false;
	
	//application context
	private final Context ctxt;
	
	//the most recent CallLog lookup
	private long lastLookup = Calendar.getInstance().getTimeInMillis();
	
	public CallTracker(Context ctxt)
	{
		super();
		this.ctxt = ctxt;
	}
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber)
	{
		super.onCallStateChanged(state, incomingNumber);
		if (state == TelephonyManager.CALL_STATE_RINGING
				|| state == TelephonyManager.CALL_STATE_OFFHOOK)
		{ //call just started or is still going
			if (!inCall && Config.D) Log.d(TAG, "Call starting");
			inCall = true;
		}
		else if (state == TelephonyManager.CALL_STATE_IDLE && inCall == true)
		{ //call just ended
			inCall = false;
			if (Config.D) Log.d(TAG, "Call ended; call log lookup starting");
			
			//go look up the most recent calls in the CallLog
			String[] cols = {CallLog.Calls.TYPE,
							 CallLog.Calls.DATE,
							 CallLog.Calls.NUMBER,
							 CallLog.Calls.DURATION};
			String where = CallLog.Calls.DATE + " > ?";
			String[] whereArgs = {Long.toString(lastLookup)};
			lastLookup = Calendar.getInstance().getTimeInMillis();
			
			Cursor newCalls = ctxt.getContentResolver().query(
					CallLog.CONTENT_URI, cols, where, whereArgs, null);
			
			if (Config.D) Log.d(TAG, newCalls.getCount()
					+ " new call(s) found");
			if (newCalls.getCount() != 0)
			{
				TrackingDBHandler cdbh = new TrackingDBHandler(ctxt);
				cdbh.openWrite();
				while (!newCalls.isAfterLast())
				{
					cdbh.writeCall(
						newCalls.getString(newCalls.getColumnIndexOrThrow(
								CallLog.Calls.NUMBER)),
						newCalls.getInt(newCalls.getColumnIndexOrThrow(
								CallLog.Calls.TYPE)),
						newCalls.getInt(newCalls.getColumnIndexOrThrow(
								CallLog.Calls.DURATION)),
						newCalls.getLong(newCalls.getColumnIndexOrThrow(
								CallLog.Calls.DATE)));
				}
				cdbh.close();
			}
		}
		else throw new RuntimeException("Unkown phone state: " + state);
	}
}
