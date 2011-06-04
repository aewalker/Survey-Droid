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
				+ PeoplesDB.CallLogTable.getCallTypeString(type) + ")");
		
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(PeoplesDB.CallLogTable.CALL_TYPE,
				PeoplesDB.CallLogTable.getCallTypeString(type));
		values.put(PeoplesDB.CallLogTable.PHONE_NUMBER, number);
		values.put(PeoplesDB.CallLogTable.TIME, time);
		if (type != CallLog.Calls.MISSED_TYPE)
			values.put(PeoplesDB.CallLogTable.DURATION, duration);
		
		db.insert(PeoplesDB.CALLLOG_TABLE_NAME, null, values);
	}
	
	/**
	 * Writes a location to the database.
	 * 
	 * @param lat - latitude
	 * @param lon - longitude
	 * @param time - time at which the subject was at the location
	 */
	public void writeLocation(double lat, double lon, long time)
	{
		if (Config.D) Log.d(TAG, "Writing location: " + lat + ", " + lon);
		
		ContentValues values = new ContentValues();
		
		//set up query
		values.put(PeoplesDB.LocationTable.LATITUDE, lat);
		values.put(PeoplesDB.LocationTable.LONGITUDE, lon);
		values.put(PeoplesDB.LocationTable.TIME, time);
		
		db.insert(PeoplesDB.LOCATION_TABLE_NAME, null, values);
	}
}
