/*---------------------------------------------------------------------------*
 * CallTracker.java                                                          *
 *                                                                           *
 * Handles incoming and outgoing calls and logs them in the database.        *
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

import java.util.TimeZone;

import org.survey_droid.survey_droid.coms.ComsService;
import org.survey_droid.survey_droid.content.ProviderContract.CallLogTable.ContactType;
import org.survey_droid.survey_droid.content.ProviderContract.SurveyTable;
import org.survey_droid.survey_droid.content.TrackingDBHandler;
import org.survey_droid.survey_droid.survey.SurveyService;
import org.survey_droid.survey_droid.survey.SurveyService.SurveyType;
import org.survey_droid.survey_droid.ui.MainActivity;

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
		"org.survey_droid.survey_droid.ACTION_SEARCH_LOG";
	
	/** Tell the tracker to start tracking calls */
	public static final String ACTION_START_TRACKING =
		"org.survey_droid.survey_droid.ACTION_START_TRACKING";
	
	/** the most recent CallLog lookup (Config key) */
	private static final String LAST_LOOKUP = "call_tracker.last_lookup";
	
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
				System.currentTimeMillis());
			CallReceiver.resetCallState(this);
		}
		else
		{
			Util.w(null, TAG, "Unknown intent action: " + action);
		}
	}
	
	/**
	 * Look through the phone log and find new calls, possibly launch surveys.
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
		String[] whereArgs =
			{Long.toString(Config.getLong(this, LAST_LOOKUP, 0l))};
		String order = CallLog.Calls.DATE;
		
		Cursor newCalls = getContentResolver().query(
				CallLog.Calls.CONTENT_URI,
				cols, where, whereArgs, order);
		try
		{
			Util.d(null, TAG, newCalls.getCount()
					+ " new call(s) found");
			if (newCalls.getCount() != 0)
			{
				//FIXME in the event that multiple calls are detected,
				//this code should only log calls other than the last
				//one if they are from old numbers
				TrackingDBHandler tdbh = new TrackingDBHandler(this);
				newCalls.moveToFirst();
				while (!newCalls.isAfterLast())
				{
					String number = Util.cleanPhoneNumber(newCalls.getString(
							newCalls.getColumnIndexOrThrow(
									CallLog.Calls.NUMBER)));
					ContactType type = ContactType.values()[newCalls.getInt(
							newCalls.getColumnIndexOrThrow(
									CallLog.Calls.TYPE))];
					boolean server = Config.getBoolean(this,
							Config.CALL_LOG_STUDY, true, 0); //FIXME
			    	boolean local = Config.getBoolean(this,
			    			Config.CALL_LOG_LOCAL);
			    	if (local && server)
			    	{
			    		long time = newCalls.getLong(
										newCalls.getColumnIndexOrThrow(
										CallLog.Calls.DATE));
			    		TimeZone tz = TimeZone.getDefault();
			    		time += tz.getOffset(time);
						tdbh.writeContact(number, type,
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
					if (Util.cleanPhoneNumber(Config.getString(this, //FIXME
							MainActivity.ADMIN_PHONE_NUMBER, "", 0)).equals(number))
					{
						newCalls.moveToNext();
						continue;
					}
					
					if (!type.equals(ContactType.INCOMING_CALL))
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
								SurveyTable.Fields._ID);
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
								SurveyType.SURVEY_TYPE_CONTACT_INIT.ordinal());
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
				
				//tell the coms service to upload the call information
				Intent uploadIntent = new Intent(this,
						ComsService.class);
				uploadIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
				uploadIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
						ComsService.CALL_DATA);
				WakefulIntentService.sendWakefulWork(this, uploadIntent);
			}
		}
		finally
		{
			newCalls.close();
		}
	}
}
