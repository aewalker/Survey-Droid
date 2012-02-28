/*---------------------------------------------------------------------------*
 * CallTracker.java                                                          *
 *                                                                           *
 * Handles incoming and outgoing calls and logs them in the database.        *
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

import java.util.TimeZone;

import org.surveydroid.android.coms.ComsService;
import org.surveydroid.android.database.SurveyDroidDB;
import org.surveydroid.android.database.TrackingDBHandler;
import org.surveydroid.android.survey.SurveyService;

import android.content.Context;
import android.content.Intent;
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
	private long lastLookup;
	
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
		lastLookup = System.currentTimeMillis();
	}
	
	@Override
	public synchronized void onCallStateChanged(
			int state, String incomingNumber)
	{
		super.onCallStateChanged(state, incomingNumber);
		Util.d(null, TAG, "Call state changed: " + state);
		if (state == TelephonyManager.CALL_STATE_RINGING
				|| state == TelephonyManager.CALL_STATE_OFFHOOK)
		{ //call just started or is still going
			if (!inCall) Util.d(ctxt, TAG, "Call starting");
			inCall = true;
		}
		else if (state == TelephonyManager.CALL_STATE_IDLE && inCall == true)
		{ //call just ended
			inCall = false;
			
	    	Runnable r = new Runnable()
	    	{
				@Override
		        public void run()
		        {
					Util.d(null, TAG, "Searching log");
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
						//FIXME in the event that multiple calls are detected,
						//this code should only log calls other than the last
						//one if they are from old numbers
						TrackingDBHandler tdbh = new TrackingDBHandler(ctxt);
						tdbh.openWrite();
						newCalls.moveToFirst();
						while (!newCalls.isAfterLast())
						{
							String number = newCalls.getString(
									newCalls.getColumnIndexOrThrow(
											CallLog.Calls.NUMBER));
							int type = newCalls.getInt(
									newCalls.getColumnIndexOrThrow(
											CallLog.Calls.TYPE));
							boolean server = Config.getSetting(ctxt,
									Config.CALL_LOG_SERVER,
									Config.CALL_LOG_SERVER_DEFAULT);
					    	boolean local = Config.getSetting(ctxt,
					    			Config.CALL_LOG_LOCAL, true);
					    	if (local && server)
					    	{
					    		long time = newCalls.getLong(
												newCalls.getColumnIndexOrThrow(
												CallLog.Calls.DATE));
					    		TimeZone tz = TimeZone.getDefault();
					    		time += tz.getOffset(time);
								tdbh.writeCall(number,type,
									(int) newCalls.getLong(
											newCalls.getColumnIndexOrThrow(
											CallLog.Calls.DURATION)),
									  time / 1000);
					    	}
							/*
							 * In the rare case that multiple calls are found
							 * here, we don't want to confuse people by
							 * starting multiple surveys at once.  Therefore,
							 * only start a survey for the most recent call.
							 */
							if (!newCalls.isLast())
							{
								newCalls.moveToNext();
								continue;
							}
							
							//make sure the number is not study admin's number
							if (Util.cleanPhoneNumber(Config.getSetting(ctxt,
									Config.ADMIN_PHONE_NUMBER, null)).equals(
											Util.cleanPhoneNumber(number)))
							{
								newCalls.moveToNext();
								continue;
							}
							
							if (type != SurveyDroidDB.CallLogTable.CallType.INCOMING)
							{
								newCalls.moveToNext();
								continue;
							}
							
							Cursor surveys;
							if (tdbh.isNewNumber(number, false))
							{
								Util.d(null, TAG, "New number!");
								surveys = tdbh.getNewCallSurveys();
							}
							else
							{
								Util.d(null, TAG, "Old number");
								surveys = tdbh.getOldCallSurveys();
							}
							int count = surveys.getCount();
							if (count != 0)
							{
								surveys.moveToFirst();
								int id_i = surveys.getColumnIndexOrThrow(
										SurveyDroidDB.SurveyTable._ID);
								for (int i = 0; i < count; i++)
								{
									Intent surveyIntent = new Intent(ctxt,
										SurveyService.class);
									surveyIntent.setAction(
										SurveyService.ACTION_SURVEY_READY);
									surveyIntent.putExtra(
										SurveyService.EXTRA_SURVEY_ID,
										surveys.getInt(id_i));
									surveyIntent.putExtra(
										SurveyService.EXTRA_SURVEY_TYPE,
										SurveyService.SURVEY_TYPE_CALL_INIT);
									ctxt.startService(surveyIntent);
								}
							}
							//this avoids a potential race condition
							lastLookup = newCalls.getLong(
									newCalls.getColumnIndexOrThrow(
									CallLog.Calls.DATE));
							newCalls.moveToNext();
						}
						tdbh.close();
						
						//tell the coms service to upload the call information
						Intent uploadIntent = new Intent(ctxt,
								ComsService.class);
						uploadIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
						uploadIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
								ComsService.CALL_DATA);
						ctxt.startService(uploadIntent);
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
			Util.w(null, TAG, "Unknown phone state: " + state);
		}
	}
}
