/*---------------------------------------------------------------------------*
 * CallTracker.java                                                          *
 *                                                                           *
 * Handles incoming and outgoing calls and logs them in the database.        *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import org.peoples.android.database.TrackingDBHandler;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

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
	private long lastLookup = System.currentTimeMillis();
	
	/**
	 * Constructor.  Typically only one instance of this class should be
	 * created; it is given to the telephony manager to receive updates.
	 * 
	 * @param ctxt - the application {@link Context}
	 */
	public CallTracker(Context ctxt)
	{
		super();
		this.ctxt = ctxt;
	}
	
	@Override
	public synchronized void onCallStateChanged(
			int state, String incomingNumber)
	{
		super.onCallStateChanged(state, incomingNumber);
		Util.d(ctxt, TAG, "Call state changed: " + state);
		if (state == TelephonyManager.CALL_STATE_RINGING
				|| state == TelephonyManager.CALL_STATE_OFFHOOK)
		{ //call just started or is still going
			if (!inCall) Util.d(ctxt, TAG, "Call starting");
			inCall = true;
		}
		else if (state == TelephonyManager.CALL_STATE_IDLE && inCall == true)
		{ //call just ended
			inCall = false;
			boolean server = Config.getSetting(ctxt, Config.CALL_LOG_SERVER,
					Config.CALL_LOG_SERVER_DEFAULT);
	    	boolean local = Config.getSetting(ctxt, Config.CALL_LOG_LOCAL, true);
	    	if (!local || !server)
			{
				Util.d(null, TAG, "Call log local: " + local);
				Util.d(null, TAG, "Call log server: " + server);
				lastLookup = System.currentTimeMillis();
				return;				
			}
			
	    	Runnable r = new Runnable()
	    	{
				@Override
		        public void run()
		        {
					Util.d(ctxt, TAG, "Searching log");
					//go look up the most recent calls in the CallLog
					String[] cols = {CallLog.Calls.TYPE,
									 CallLog.Calls.DATE,
									 CallLog.Calls.NUMBER,
									 CallLog.Calls.DURATION};
					String where = CallLog.Calls.DATE + " > ?";
					String[] whereArgs = {Long.toString(lastLookup)};
					String order = CallLog.Calls.DATE;
					
					Cursor newCalls = ctxt.getContentResolver().query(
							CallLog.Calls.CONTENT_URI,
							cols, where, whereArgs, order);
					
					Util.d(ctxt, TAG, newCalls.getCount()
							+ " new call(s) found");
					if (newCalls.getCount() != 0)
					{
						TrackingDBHandler cdbh = new TrackingDBHandler(ctxt);
						cdbh.openWrite();
						newCalls.moveToFirst();
						while (!newCalls.isAfterLast())
						{
							cdbh.writeCall(
								newCalls.getString(
										newCalls.getColumnIndexOrThrow(
										CallLog.Calls.NUMBER)),
								newCalls.getInt(
										newCalls.getColumnIndexOrThrow(
										CallLog.Calls.TYPE)),
								(int) newCalls.getLong(
										newCalls.getColumnIndexOrThrow(
										CallLog.Calls.DURATION)),
								newCalls.getLong(
										newCalls.getColumnIndexOrThrow(
										CallLog.Calls.DATE)));
							if (newCalls.isLast())
							{
								//this avoids a potential race condition
								lastLookup = newCalls.getLong(
										newCalls.getColumnIndexOrThrow(
										CallLog.Calls.DATE));
							}
							newCalls.moveToNext();
						}
						cdbh.close();
					}
					newCalls.close();
		        }
	    	};
	    	//delay the call log lookup by 3 seconds to make sure the call
	    	//actually gets put into the log
	    	
	    	//even if it doesn't, it will still get found on the next run
	    	Handler h = new Handler();
	    	h.postDelayed(r, 3 * 1000);
		}
		else if (state != TelephonyManager.CALL_STATE_IDLE)
		{
			if (Config.D)
				throw new RuntimeException("Unkown phone state: " + state);
			else Util.w(ctxt, TAG, "Unknown phone state: " + state);
		}
	}
}
