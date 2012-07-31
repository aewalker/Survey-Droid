/*---------------------------------------------------------------------------*
 * CallsDBHandler.java                                                       *
 *                                                                           *
 * Allows the call log to perform needed database functions.                 *
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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.survey_droid.survey_droid.LocationTracker.LocationCode;
import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.content.ProviderContract.CallLogTable.ContactType;
import org.survey_droid.survey_droid.content.ProviderContract.*;

/**
 * Provides the database read/write methods needed by the call logger.
 *
 * @author Austin Walker
 */
public class TrackingDBHandler extends SurveyDroidDBHandler
{
	/** logging tag */
	private static final String TAG = "CallsDBHandler";

	public TrackingDBHandler(Context context)
	{
		super(context);
	}

	/**
	 * Writes a call to the database.  Phone numbers should
	 * <strong>not</strong> be hashed before being given to this function.
	 *
	 * @param number the phone number
	 * @param type the type of contact; from {@link ContactType}
	 * @param duration how long the call was (ignored for missed calls)
	 */
	public void writeContact(String number, ContactType type, int duration, long time)
	{
		Util.d(null, TAG, "Writing contact (type: "
				+ type.toString() + ")");

		ContentValues values = new ContentValues();

		//set up the query
		values.put(CallLogTable.Fields.CALL_TYPE, type.ordinal());
		values.put(CallLogTable.Fields.PHONE_NUMBER, Util.cleanPhoneNumber(number));
		values.put(CallLogTable.Fields.TIME, time);
		if (type.hasDuration())
			values.put(CallLogTable.Fields.DURATION, duration);

		insert(CallLogTable.NAME, values);
	}

	/**
	 * Writes a location to the database.
	 *
	 * @param lat latitude
	 * @param lon longitude
	 * @param accuracy the location accuracy
	 * @param time time at which the subject was at the location
	 */
	public void writeLocation(
			double lat, double lon, double accuracy, long time)
	{
		Util.d(null, TAG, "Writing location: " + lat + ", " + lon);

		ContentValues values = new ContentValues();

		//set up query
		values.put(LocationTable.Fields.LATITUDE, lat);
		values.put(LocationTable.Fields.LONGITUDE, lon);
		values.put(LocationTable.Fields.ACCURACY, accuracy);
		values.put(LocationTable.Fields.TIME, time);

		insert(LocationTable.NAME, values);
	}

	/**
	 * Writes an error location to the database.
	 * 
	 * @param code the error code
	 * @param time time at which the subject was at the location
	 */
	public void writeLocation(LocationCode code, long time)
	{
		writeLocation(0.0, 0.0, code.ordinal(), time);
	}
	
	/**
	 * Has a phone number been seen before?  Only checks incoming calls/texts.
	 * 
	 * @param number the phone number to check
	 * @param texts if true, look at texts; if false, calls
	 */
	public boolean isNewNumber(String number)
	{
		Util.d(null, TAG, "Looking for " + number);
		
		String    table    = CallLogTable.NAME;
		String[]  cols     = {CallLogTable.Fields.PHONE_NUMBER};
		//don't count missed calls...
		String    selc = "(" + CallLogTable.Fields.CALL_TYPE + " = ? OR " +
			CallLogTable.Fields.CALL_TYPE + " = ?) AND (" +
			CallLogTable.Fields.PHONE_NUMBER + " = ?)";
		String[]  selcArgs = new String[3];
			selcArgs[0] = Integer.toString(ContactType.INCOMING_CALL.ordinal());
			selcArgs[1] = Integer.toString(ContactType.INCOMING_TEXT.ordinal());
			selcArgs[2] = Util.cleanPhoneNumber(number);
		String    orderBy  = null;
		
		Cursor result = query(table, cols, selc, selcArgs, orderBy);
		boolean toReturn = true;
		if (result.getCount() > 1) toReturn = false;
		result.close();
		return toReturn;
	}
	
	/**
	 * Gets all surveys that should be started after a call from a new number.
	 * 
	 * @return a {@link Cursor} with the needed surveys in it
	 */
	public Cursor getNewCallSurveys()
	{
		return getSurveys(SurveyTable.Fields.NEW_CALLS);
	}
	
	/**
	 * Gets all surveys that should be started after a call from a previously
	 * seen number.
	 * 
	 * @return a {@link Cursor} with the needed surveys in it
	 */
	public Cursor getOldCallSurveys()
	{
		return getSurveys(SurveyTable.Fields.OLD_CALLS);
	}
	
	/**
	 * Gets all surveys that should be started after a text from a new number.
	 * 
	 * @return a {@link Cursor} with the needed surveys in it
	 */
	public Cursor getNewTextSurveys()
	{
		return getSurveys(SurveyTable.Fields.NEW_TEXTS);
	}
	
	/**
	 * Gets all surveys that should be started after a text from a previously
	 * seen number.
	 * 
	 * @return a {@link Cursor} with the needed surveys in it
	 */
	public Cursor getOldTextSurveys()
	{
		return getSurveys(SurveyTable.Fields.OLD_TEXTS);
	}
	
	/**
	 * Gets all surveys with some value set
	 * 
	 * @param field either new_calls, old_calls, new_texts, or old_texts
	 * @return a cursor with all surveys for which the given field is true,
	 * sorted by study id
	 */
	private Cursor getSurveys(String field)
	{
		Util.d(null, TAG, "getting " + field + " surveys");
		
		String    table    = SurveyTable.NAME;
		String[]  cols     = {SurveyTable.Fields._ID,
							  SurveyTable.Fields.STUDY_ID};
		String    selc     = field + " =  ?";
		String[]  selcArgs = {"" + 1};
		String    orderBy  = SurveyTable.Fields._ID;
		
		//run it
		return query(table, cols, selc, selcArgs, orderBy);
	}
}
