/*---------------------------------------------------------------------------*
 * CallsDBHandler.java                                                       *
 *                                                                           *
 * Allows the call log to perform needed database functions.                 *
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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import org.surveydroid.android.Util;
import org.surveydroid.android.database.SurveyDroidDB.CallLogTable.CallType;

import static org.surveydroid.android.database.SurveyDroidDB.CallLogTable;
import static org.surveydroid.android.database.SurveyDroidDB.LocationTable;

/**
 * Provides the database read/write methods needed by the call logger.
 *
 * @author Austin Walker
 */
public class TrackingDBHandler extends SurveyDroidDBHandler
{
	//logging tag
	private static final String TAG = "CallsDBHandler";

	public TrackingDBHandler(Context context)
	{
		super(context);
	}

	/**
	 * Writes a call to the database.  Phone numbers should
	 * <strong>not</strong> be hashed before being given to this function.
	 *
	 * @param number - the phone number
	 * @param type - the type of call; from {@link CallType}
	 * @param duration - how long the call was (ignored for missed calls)
	 */
	public void writeCall(String number, int type, int duration, long time)
	{
		Util.d(contx, TAG, "Writing call (type: "
				+ CallLogTable.getCallTypeString(type) + ")");

		ContentValues values = new ContentValues();

		//set up the query
		values.put(CallLogTable.CALL_TYPE, type);
		values.put(CallLogTable.PHONE_NUMBER, number);
		values.put(CallLogTable.TIME, time);
		if (CallLogTable.hasDuration(type))
			values.put(CallLogTable.DURATION, duration);

		if (db.insert(SurveyDroidDB.CALLLOG_TABLE_NAME, null, values) == -1)
		{
			Util.e(contx, TAG, "insert error!");
		}
	}

	/**
	 * Writes a location to the database.
	 *
	 * @param lat - latitude
	 * @param lon - longitude
	 * @param time - time at which the subject was at the location
	 */
	public void writeLocation(
			double lat, double lon, double accuracy, long time)
	{
		Util.d(contx, TAG, "Writing location: " + lat + ", " + lon);

		ContentValues values = new ContentValues();

		//set up query
		values.put(LocationTable.LATITUDE, lat);
		values.put(LocationTable.LONGITUDE, lon);
		values.put(LocationTable.ACCURACY, accuracy);
		values.put(LocationTable.TIME, time);

		db.insert(SurveyDroidDB.LOCATION_TABLE_NAME, null, values);
	}
	
	/**
	 * Has a phone number been seen before?
	 * 
	 * @param number - the phone number to check
	 * @param texts - if true, look at texts; if false, calls
	 * 
	 * @return true if the number has not been seen before
	 */
	public boolean isNewNumber(String number, boolean texts)
	{
		Util.d(contx, TAG, "Looking for " + number);
		
		String    table    = SurveyDroidDB.CALLLOG_TABLE_NAME;
		String[]  cols     = {SurveyDroidDB.CallLogTable.PHONE_NUMBER};
		//don't count missed calls...
		String    selc = SurveyDroidDB.CallLogTable.CALL_TYPE + " = ? or " +
			SurveyDroidDB.CallLogTable.CALL_TYPE + " = ?";
		String[]  selcArgs = new String[2];
		if (texts)
		{
			selcArgs[0] = Integer.toString(
					SurveyDroidDB.CallLogTable.CallType.OUTGOING);
			selcArgs[1] = Integer.toString(
					SurveyDroidDB.CallLogTable.CallType.INCOMING);
		}
		else
		{
			selcArgs[0] = Integer.toString(
					SurveyDroidDB.CallLogTable.CallType.OUTGOING_TEXT);
			selcArgs[1] = Integer.toString(
					SurveyDroidDB.CallLogTable.CallType.INCOMING_TEXT);
		}
		String    group    = null;
		String    having   = null;
		String    orderBy  = null;
		
		Cursor result =
			db.query(table, cols, selc, selcArgs, group, having, orderBy);
		boolean toReturn = true;
		if (result.getCount() > 0) toReturn = false;
		result.close();
		return toReturn;
	}
}
