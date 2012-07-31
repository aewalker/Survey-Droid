/*---------------------------------------------------------------------------*
 * TakenDBHandler.java                                                       *
 *                                                                           *
 * Contains methods to log when data about surveys that have been taken or   *
 * were scheduled but not taken.  Also contains methods to get the current   *
 * survey completion rate.                                                   *
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
package org.survey_droid.survey_droid.content;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.survey_droid.survey_droid.Config;
import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.annotation.ConfigKey;
import org.survey_droid.survey_droid.content.ProviderContract.SurveyTable;
import org.survey_droid.survey_droid.content.ProviderContract.SurveysTakenTable;
import org.survey_droid.survey_droid.content.ProviderContract.SurveysTakenTable.SurveyCompletionCode;

/**
 * Provides the database read/write methods needed to track how surveys are
 * being completed.
 *
 * @author Austin Walker
 */
public class TakenDBHandler extends SurveyDroidDBHandler
{
	/** logging tag */
	private static final String TAG = "SurveysTakenDBHandler";
	
	/** The current number of surveys completed */
	@ConfigKey("0")
	private static final String NUM_COMPLETED = "taken_db_handler.num_completed";
	
	/** Number of additional surveys taken this week beyond the planned number */
	@ConfigKey("0")
	private static final String TMP_ADDED_SURVEYS = "taken_db_handler.tmp_added_surveys";
	
	/**
	 * Whether or not the number of completed surveys has been reset this week.
	 */
	@ConfigKey("false")
	private static final String RESET = "taken_db_handler.completion_reset";
	
	/**
	 * Returned from {@link #getCompletionRate} if there have not been any
	 * surveys taken
	 */
	public static final int NO_PERCENTAGE = -1;
	
	/** The different ways a survey can be counted */
	private enum SurveyStatus
	{
		/** adds to the numerator only */
		COUNTS_NUMERATOR,
		/** adds to the denominator only */
		COUNTS_DENOMINATOR,
		/** adds to both the numerator and the denominator */
		COUNTS_BOTH,
		/** doesn't count at all */
		COUNTS_NETHER;
	}

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
	 * @param id the survey's id
	 * @param code see {@link SurveyCompletionCode}
	 * @param created when the surveys was completed/dismissed/etc.
	 * @return true on success
	 */
	public boolean writeSurvey(long id, SurveyCompletionCode code, long created)
	{
		Util.d(null, TAG, "Writing survey code: survey "
				+ id + " marked " + code.name());
		
		//collect needed info (study id and study's internal survey id)
		String    table    = SurveyTable.NAME;
		String[]  cols     = {SurveyTable.Fields.SURVEY_ID,
							  SurveyTable.Fields.STUDY_ID};
		String    selc     = SurveyTable.Fields._ID + " = ?";
		String[]  selcArgs = {Long.toString(id)};
		String    orderBy  = null;
		
		Cursor result = query(table, cols, selc, selcArgs, orderBy);
		result.moveToFirst();
		long study_id = result.getLong(result.getColumnIndexOrThrow(SurveyTable.Fields.STUDY_ID));
		long survey_id = result.getLong(result.getColumnIndexOrThrow(SurveyTable.Fields.SURVEY_ID));
		result.close();
		
		//first update the completion percentage if needed
		getCompletionRate(contx, study_id); //make sure the rate is properly updated
		switch (countsAsCompleted(code))
		{
		case COUNTS_BOTH:
			Config.putSetting(contx, NUM_COMPLETED,
				Config.getInt(contx, NUM_COMPLETED, 0, study_id) + 1, study_id);
			//fall through
		case COUNTS_DENOMINATOR:
			Config.putSetting(contx, TMP_ADDED_SURVEYS,
				Config.getInt(contx, TMP_ADDED_SURVEYS, 0, study_id) + 1, study_id);
			break;
		case COUNTS_NUMERATOR:
			Config.putSetting(contx, NUM_COMPLETED,
				Config.getInt(contx, NUM_COMPLETED, 0, study_id) + 1, study_id);
			break;
		default:
			//do nothing
			break;
		}
		
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(SurveysTakenTable.Fields.SURVEY_ID, survey_id);
		values.put(SurveysTakenTable.Fields.STATUS, code.ordinal());
		values.put(SurveysTakenTable.Fields.RATE, getCompletionRate(contx, study_id));
		values.put(SurveysTakenTable.Fields.STUDY_ID, study_id);
		values.put(SurveysTakenTable.Fields.CREATED, created);
		
		//run it
		insert(SurveysTakenTable.NAME, values);
		return true;
	}
	
	/**
	 * Get the current completion rate for surveys in a particular study.
	 * 
	 * @param c the context to use.  This is to avoid having to open a database
	 * connection in order to use this function.
	 * @return the percentage completed between 0 and 100, or
	 * {@link #NO_PERCENTAGE} if there are no surveys to go by 
	 */
	public static int getCompletionRate(Context c, long study_id)
	{
		Util.d(null, TAG, "Getting survey completion rate");
		
		//try to update if we are on one of the correct days
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_WEEK);
		if (day == Calendar.SUNDAY)
		{
			if (Config.getBoolean(c, RESET, false, study_id) == false)
			{
				Util.i(null, TAG, "Reseting completion count");
				Config.putSetting(c, NUM_COMPLETED, 0, study_id);
				Config.putSetting(c, TMP_ADDED_SURVEYS, 0, study_id);
				Config.putSetting(c, RESET, true, study_id);
			}
		}
		else
		{
			/*
			 * There is one possible problem here: if the user doesn't turn the
			 * phone on at all between one Sunday and the next, this doesn't
			 * work.  Because this is an extremely unlikely event and would be
			 * annoying to detect, we employ the ostrich algorithm.
			 */
			//FIXME the fix is to record the date
			Config.putSetting(c, RESET, false, study_id);
		}
		
		//get the rate
		float done = Config.getInt(c, NUM_COMPLETED, 0, study_id);
		float target = Config.getInt(c, Config.SURVEYS_PER_WEEK, 1, study_id);
		target += Config.getInt(c, TMP_ADDED_SURVEYS, 0, study_id);
		Util.d(null, TAG, "Completed " + done + " out of " + target);
		return (int) Math.round(done * 100 / target);
	}
	
	/**
	 * Determine how to count a certain type of survey.
	 * 
	 * @param code the survey completion code, as in
	 * {@link SurveyCompletionCode}
	 * @return 
	 */
	private static SurveyStatus countsAsCompleted(SurveyCompletionCode code)
	{
		switch (code)
		{
    	case SCHEDULED_UNFINISHED:
    	case SCHEDULED_DISMISSED:
    	case SCHEDULED_IGNORED:
    	case RANDOM_UNFINISHED:
    	case RANDOM_DISMISSED:
    	case RANDOM_IGNORED:
    	case USER_INITIATED_UNFINISHED:
    	case SURVEYS_DISABLED_SERVER:
    	case LOCATION_BASED_DISMISSED:
    	case LOCATION_BASED_FINISHED:
    	case LOCATION_BASED_IGNORED:
    	case LOCATION_BASED_UNFINISHED:
		case SURVEYS_DISABLED_LOCALLY:
    	case USER_INITIATED_FINISHED:
			return SurveyStatus.COUNTS_NETHER;

    	case CALL_INITIATED_UNFINISHED:
    	case CALL_INITIATED_DISMISSED:
    	case CALL_INITIATED_IGNORED:
    		return SurveyStatus.COUNTS_DENOMINATOR;
    		
    	case CALL_INITIATED_FINISHED:
    		return SurveyStatus.COUNTS_BOTH;

    	case SCHEDULED_FINISHED:
    	case RANDOM_FINISHED:
			return SurveyStatus.COUNTS_NUMERATOR;
			
    	default:
    		Util.w(null, TAG, "Unknown survey completion code: " + code);
    		return null;
		}
	}
}
