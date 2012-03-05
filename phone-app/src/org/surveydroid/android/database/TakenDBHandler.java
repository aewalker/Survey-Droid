/*---------------------------------------------------------------------------*
 * TakenDBHandler.java                                                       *
 *                                                                           *
 * Contains methods to log when data about surveys that have been taken or   *
 * were scheduled but not taken.  Also contains methods to get the current   *
 * survey completion rate.                                                   *
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
package org.surveydroid.android.database;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;

import org.surveydroid.android.Config;
import org.surveydroid.android.Util;

/**
 * Provides the database read/write methods needed to track how surveys are
 * being completed.
 *
 * @author Austin Walker
 */
public class TakenDBHandler extends SurveyDroidDBHandler
{
	//logging tag
	private static final String TAG = "SurveysTakenDBHandler";
	
	/** The current number of surveys completed */
	private static final String NUM_COMPLETED = "num_completed";
	
	/**
	 * Whether or not the number of completed surveys has been reset this week.
	 */
	private static final String RESET = "completion_reset";
	
	/**
	 * Returned from {@link #getCompletionRate} if there have not been any
	 * surveys taken
	 */
	public static final int NO_PERCENTAGE = -1;

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public TakenDBHandler(Context context)
	{
		super(context);
	}

	/**
	 * Write an entry for a survey's completion (or not) to the database.
	 * 
	 * @param survey_id - the survey's id
	 * @param code - the code from {@link SurveyDroidDB#TakenTable} describing
	 * the type of survey and its completion status
	 * @param created - when the surveys was completed/dismissed/etc.
	 * @return true on success
	 */
	public boolean writeSurvey(int survey_id, int code, long created)
	{
		Util.d(null, TAG, "Writing survey code: survey "
				+ survey_id + " marked " + code);
		
		//first update the completion percentage if needed
		boolean counts = countsAsCompleted(code);
		if (counts)
		{
			Config.putSetting(contx, NUM_COMPLETED,
					Config.getSetting(contx, NUM_COMPLETED, 0) + 1);
		}
		
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(SurveyDroidDB.TakenTable.SURVEY_ID, survey_id);
		values.put(SurveyDroidDB.TakenTable.STATUS, code);
		values.put(SurveyDroidDB.TakenTable.CREATED, created);
		
		//run it
		if (db.insert(SurveyDroidDB.TAKEN_TABLE_NAME, null, values) == -1)
			return false;
		return true;
	}
	
	/**
	 * Get the current completion rate for surveys.
	 * 
	 * @param c the context to use.  This is to avoid having to open a database
	 * connection in order to use this function.
	 * @return the percentage completed between 0 and 100, or
	 * {@link #NO_PERCENTAGE} if there are no surveys to go by 
	 */
	public static int getCompletionRate(Context c)
	{
		Util.d(null, TAG, "Getting survey completion rate");
		
		//try to update if we are on one of the correct days
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_WEEK);
		if (day == Calendar.SUNDAY)
		{
			if (Config.getSetting(c, RESET, false) == false)
			{
				Util.i(null, TAG, "Reseting completion count");
				Config.putSetting(c, NUM_COMPLETED, 0);
				Config.putSetting(c, RESET, true);
			}
		}
		else
		{
			/*
			 * There is one possible problem here: if the user doesn't turn the
			 * phone on at all between one Sunday and the next, this doesn't
			 * work.  Because this is an extremely unlikely event and would be
			 * annoying to detect, we apply the ostrich algorithm.
			 */
			Config.putSetting(c, RESET, false);
		}
		
		//get the rate
		float done = Config.getSetting(c, NUM_COMPLETED, 0);
		float target = Config.getSetting(c, Config.SURVEYS_PER_WEEK, 1);
		Util.d(null, TAG, "Completed " + done + " out of " + target);
		return (int) Math.round(done * 100 / target);
	}
	
	/**
	 * Determine how to count a certain type of survey.
	 * 
	 * @param code - the survey completion code, as in
	 * {@link SurveyDroidDB#TakenTable}
	 * @return true if a survey should be counted, false if not
	 */
	private static boolean countsAsCompleted(int code)
	{
		switch (code)
		{
    	case SurveyDroidDB.TakenTable.SCHEDULED_UNFINISHED:
    	case SurveyDroidDB.TakenTable.SCHEDULED_DISMISSED:
    	case SurveyDroidDB.TakenTable.SCHEDULED_IGNORED:
    	case SurveyDroidDB.TakenTable.RANDOM_UNFINISHED:
    	case SurveyDroidDB.TakenTable.RANDOM_DISMISSED:
    	case SurveyDroidDB.TakenTable.RANDOM_IGNORED:
    	case SurveyDroidDB.TakenTable.USER_INITIATED_UNFINISHED:
    	case SurveyDroidDB.TakenTable.SURVEYS_DISABLED_SERVER:
    	case SurveyDroidDB.TakenTable.LOCATION_BASED_DISMISSED:
    	case SurveyDroidDB.TakenTable.LOCATION_BASED_FINISHED:
    	case SurveyDroidDB.TakenTable.LOCATION_BASED_IGNORED:
    	case SurveyDroidDB.TakenTable.LOCATION_BASED_UNFINISHED:
    	case SurveyDroidDB.TakenTable.CALL_INITIATED_UNFINISHED:
    	case SurveyDroidDB.TakenTable.CALL_INITIATED_DISMISSED:
    	case SurveyDroidDB.TakenTable.CALL_INITIATED_IGNORED:
    	case SurveyDroidDB.TakenTable.CALL_INITIATED_FINISHED:
		case SurveyDroidDB.TakenTable.SURVEYS_DISABLED_LOCALLY:
    	case SurveyDroidDB.TakenTable.USER_INITIATED_FINISHED:
			return false;

    	case SurveyDroidDB.TakenTable.SCHEDULED_FINISHED:
    	case SurveyDroidDB.TakenTable.RANDOM_FINISHED:
			return true;
			
    	default:
    		Util.w(null, TAG, "Unknown survey completion code: " + code);
    		return false;
		}
	}
}
