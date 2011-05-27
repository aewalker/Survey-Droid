/*---------------------------------------------------------------------------*
 * LocationDBHandler.java                                                    *
 *                                                                           *
 * Contains location-specific database functionality.                        *
 *---------------------------------------------------------------------------*/
package org.peoples.android.database;

import org.peoples.android.Config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.util.Log;

/**
 * Contains location-specific functions to deal with the database.
 * 
 * @author Diego Vargas
 * @author Austin Walker
 */
public class LocationDBHandler extends PeoplesDBHandler
{
	/**
	 * Simple constructor.
	 * 
	 * @param context - the current {@link Context}
	 */
	public LocationDBHandler(Context context)
	{
		super(context);
	}

	//for writing to the log
	private static final String TAG = "LocationDBHandler";
	
	/**
	 * Writes a location object to the database.
	 * 
	 * @param loc - the {@link Location} to write to the database
	 * 
	 * @return the id of the new row, or -1 on error
	 */
	public long insertLocation(Location loc)
	{
		if(Config.D) Log.d(TAG, "inserting new location");
		
		//There are currently 4 columns GPS table, 3 w/o the auto increment
		//column
		ContentValues values = new ContentValues();
		
		values.put(PeoplesDB.LocationTable.LATITUDE, loc.getLatitude());
		values.put(PeoplesDB.LocationTable.LONGITUDE, loc.getLongitude());
		values.put(PeoplesDB.LocationTable.TIME, loc.getTime());
		
		return db.insert(PeoplesDB.LOCATION_TABLE_NAME, null, values);
	}
	
	/**
	 * Gets all stored locations from the database.
	 * 
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getStoredLocations()
	{
		//TODO this should just return the things that need to be uploaded
		if(Config.D) Log.d(TAG, "getting locations");
		
		//Query Arguments
		String table		= PeoplesDB.LOCATION_TABLE_NAME;
		String[] 	columns			= null; //returns all columns
		String 		selection		= null; //will return all locations
		String[] 	selectionArgs	= null; //not needed
		String		groupBy			= null; //not grouping the rows
		String		having			= null; //SQL having clause, not needed
		String		orderBy			= null; //use the default sort order
		
		Cursor mCursor = db.query(table, columns, selection,
				selectionArgs, groupBy, having, orderBy);
		
		if(mCursor != null)
			mCursor.moveToFirst();
		
		return mCursor;
	}
}
