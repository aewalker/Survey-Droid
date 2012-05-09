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

import com.commonsware.cwac.wakeful.WakefulIntentService;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

/**
 * Responsible for logging calls.  Catches the system broadcast when a call is
 * going out or coming in and logs it.
 * 
 * @author Austin Walker
 */
public class CallTracker extends WakefulIntentService
{
	/** logging tag */
	private static final String TAG = "CallTracker";
	
	/** Used to tell the tracker to look through the log for new calls */
	public static final String ACTION_SEARCH_LOG =
		"org.surveydroid.android.ACTION_SEARCH_LOG";
	
	/** Tell the tracker to start tracking calls */
	public static final String ACTION_START_TRACKING =
		"org.surveydroid.android.ACTION_START_TRACKING";
	
	/** the most recent CallLog lookup (Config key) */
	private static final String LAST_LOOKUP = "call_tracker_last_lookup";
	
	/** simple constructor */
	public CallTracker()
	{
		super(null);
	}

	@Override
	protected synchronized void doWakefulWork(Intent intent)
	{
		String action = intent.getAction();
		if (action.equals(ACTION_SEARCH_LOG))
		{
			searchLog();
		}
		else if (action.equals(ACTION_START_TRACKING))
		{
			Util.d(null, TAG, "Received tracking start message");
			Config.putSetting(this, LAST_LOOKUP,
				Long.toString(System.currentTimeMillis()));
			CallReceiver.resetCallState(this);
		}
		else
		{
			Util.w(null, TAG, "Unknown intent action: " + action);
		}
	}
	
	/**
	 * Look through the phone log and find new calls, possibly launch a survey.
	 */
	private void searchLog()
	{
		Util.d(null, TAG, "Searching log");
		//go look up the most recent calls in the CallLog
		String[] cols = {CallLog.Calls.TYPE,
						 CallLog.Calls.DATE,
						 CallLog.Calls.NUMBER,
						 CallLog.Calls.DURATION};
		String where = CallLog.Calls.DATE + " > ?";
		String[] whereArgs = {Config.getSetting(this, LAST_LOOKUP, "0")};
		String order = CallLog.Calls.DATE;
		
		Cursor newCalls = getContentResolver().query(
				CallLog.Calls.CONTENT_URI,
				cols, where, whereArgs, order);
		
		Util.d(null, TAG, newCalls.getCount()
				+ " new call(s) found");
		if (newCalls.getCount() != 0)
		{
			//FIXME in the event that multiple calls are detected,
			//this code should only log calls other than the last
			//one if they are from old numbers
			TrackingDBHandler tdbh = new TrackingDBHandler(this);
			tdbh.open();
			newCalls.moveToFirst();
			while (!newCalls.isAfterLast())
			{
				String number = Util.cleanPhoneNumber(newCalls.getString(
						newCalls.getColumnIndexOrThrow(
								CallLog.Calls.NUMBER)));
				int type = newCalls.getInt(
						newCalls.getColumnIndexOrThrow(
								CallLog.Calls.TYPE));
				boolean server = Config.getSetting(this,
						Config.CALL_LOG_SERVER,
						Config.CALL_LOG_SERVER_DEFAULT);
		    	boolean local = Config.getSetting(this,
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
				
				//make sure the number is not null (for some reason...)
				if (number == null)
				{
					Util.e(null, TAG, "Got a null phone number!?!?");
					break;
				}
				
				//make sure the number is not study admin's number
				if (Util.cleanPhoneNumber(Config.getSetting(this,
						Config.ADMIN_PHONE_NUMBER, null)).equals(number))
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
				if (tdbh.isNewNumber(number))
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
						Intent surveyIntent = new Intent(this,
							SurveyService.class);
						surveyIntent.setAction(
							SurveyService.ACTION_SURVEY_READY);
						surveyIntent.putExtra(
							SurveyService.EXTRA_SURVEY_ID,
							surveys.getInt(id_i));
						surveyIntent.putExtra(
							SurveyService.EXTRA_SURVEY_TYPE,
							SurveyService.SURVEY_TYPE_CALL_INIT);
						Uri uri = Uri.parse("call tracker survey");
						Dispatcher.dispatch(this, surveyIntent,
							0, Dispatcher.TYPE_WAKEFUL_MANUAL, uri);
					}
				}
				//this avoids a potential race condition
				long last = newCalls.getLong(
						newCalls.getColumnIndexOrThrow(
						CallLog.Calls.DATE));
				Config.putSetting(this, LAST_LOOKUP, Long.toString(last));
				newCalls.moveToNext();
			}
			tdbh.close();
			
			//tell the coms service to upload the call information
			Intent uploadIntent = new Intent(this,
					ComsService.class);
			uploadIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
			uploadIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
					ComsService.CALL_DATA);
			WakefulIntentService.sendWakefulWork(this, uploadIntent);
		}
		newCalls.close();
	}
}
