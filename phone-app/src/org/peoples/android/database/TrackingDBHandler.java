/*---------------------------------------------------------------------------*
 * CallsDBHandler.java                                                       *
 *                                                                           *
 * Allows the call log to perform needed database functions.                 *
 *---------------------------------------------------------------------------*/
package org.peoples.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.provider.CallLog;
import android.util.Log;

import org.peoples.android.Config;
import static org.peoples.android.database.PeoplesDB.CallLogTable;
import static org.peoples.android.database.PeoplesDB.LocationTable;

/**
 * Provides the database read/write methods needed by the call logger.
 *
 * @author Austin Walker
 */
public class TrackingDBHandler extends PeoplesDBHandler
{
	//logging tag
	private static final String TAG = "CallsDBHandler";

	public TrackingDBHandler(Context context)
	{
		super(context);
	}

	/**
	 * Writes a call to the database.
	 *
	 * @param number - the phone number
	 * @param type - the type of call; one of
	 * {@link CallLog.Calls.INCOMING_TYPE},
	 * {@link CallLog.Calls.MISSED_TYPE}, or
	 * {@link CallLog.Calls.OUTGOING_TYPE}
	 * @param duration - how long the call was (ignored for missed calls)
	 */
	public void writeCall(String number, int type, int duration, long time)
	{
		if (Config.D) Log.d(TAG, "Writing call (type: "
				+ CallLogTable.getCallTypeString(type) + ")");

		ContentValues values = new ContentValues();

		//set up the query
		values.put(CallLogTable.CALL_TYPE,
				CallLogTable.getCallTypeString(type));
		values.put(CallLogTable.PHONE_NUMBER, number);
		values.put(CallLogTable.TIME, time);
		if (CallLogTable.hasDuration(type))
			values.put(CallLogTable.DURATION, duration);

		db.insert(PeoplesDB.CALLLOG_TABLE_NAME, null, values);
	}

	/**
	 * Writes a location to the database.
	 *
	 * @param lat - latitude
	 * @param lon - longitude
	 * @param time - time at which the subject was at the location
	 */
	public void writeLocation(double lat, double lon, double accuracy, long time)
	{
		if (Config.D) Log.d(TAG, "Writing location: " + lat + ", " + lon);

		ContentValues values = new ContentValues();

		//set up query
		values.put(LocationTable.LATITUDE, lat);
		values.put(LocationTable.LONGITUDE, lon);
		values.put(LocationTable.ACCURACY, accuracy);
		values.put(LocationTable.TIME, time);

		db.insert(PeoplesDB.LOCATION_TABLE_NAME, null, values);
	}
}
