/*---------------------------------------------------------------------------*
 * ComsDBHandler.java                                                        *
 *                                                                           *
 * Allows the communication service to perform needed database functions.    *
 *---------------------------------------------------------------------------*/
package org.peoples.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.peoples.android.Config;

/**
 * Provides the database read/write methods needed by the communication
 * component.
 * 
 * @author Austin Walker
 */
public class ComsDBHandler extends PeoplesDBHandler
{
	//logging tag
	private static final String TAG = "ComsDBHandler";
	
	/**
	 * Constructor.
	 * 
	 * @param context
	 */
	public ComsDBHandler(Context context)
	{
		super(context);
	}
	
	/*-----------------------------------------------------------------------*/
	/*                         Push methods                                  */
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Get all answers that haven't yet been uploaded.  The results are ordered
	 * by creation time so that the oldest data will be sent first (in case of
	 * a weak connection).
	 * 
	 * @return a cursor with the data
	 */
	public Cursor getNewAnswers()
	{
		if (Config.D) Log.d(TAG, "Fetching new answers");
		
		//set up the query

		String    table    = PeoplesDB.ANSWER_TABLE_NAME;
		String[]  cols     = null;
		String    selc     = PeoplesDB.AnswerTable.UPLOADED + " =  ?";
		String[]  selcArgs = {"0"};
		String    group    = null;
		String    having   = null;
		String    orderBy  = PeoplesDB.AnswerTable.CREATED;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/**
	 * Get all the locations.  The results are ordered by time so that older
	 * older data is sent first.
	 * 
	 * @return
	 */
	public Cursor getLocations()
	{
		if (Config.D) Log.d(TAG, "Fetching locations");
		
		//set up the query

		String    table    = PeoplesDB.LOCATION_TABLE_NAME;
		String[]  cols     = null;
		String    selc     = null;
		String[]  selcArgs = null;
		String    group    = null;
		String    having   = null;
		String    orderBy  = PeoplesDB.LocationTable.TIME;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/**
	 * Get all the calls.  The results are ordered by time so that older
	 * older data is sent first.
	 * 
	 * @return
	 */
	public Cursor getCalls()
	{
		if (Config.D) Log.d(TAG, "Fetching calls");
		
		//set up the query

		String    table    = PeoplesDB.CALLLOG_TABLE_NAME;
		String[]  cols     = null;
		String    selc     = null;
		String[]  selcArgs = null;
		String    group    = null;
		String    having   = null;
		String    orderBy  = PeoplesDB.CallLogTable.TIME;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/**
	 * Marks the answer with id as having been uploaded.
	 * 
	 * @param id - id of the answer to mark
	 */
	public void updateAnswer(int id)
	{
		if (Config.D) Log.d(TAG, "Marking answer " + id + " as uploaded");
		
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(PeoplesDB.AnswerTable.UPLOADED, 1);
		String whereClause = PeoplesDB.AnswerTable._ID + " = ?";
		String[] whereArgs = {"" + id};
		
		db.update(PeoplesDB.ANSWER_TABLE_NAME, values, whereClause, whereArgs);
	}
	
	/**
	 * Delete a location from the database.
	 * 
	 * @param id - the id of the location to delete
	 */
	public void delLocation(int id)
	{
		if (Config.D) Log.d(TAG, "Deleting location " + id);
		
		//set up the query
		String whereClause = PeoplesDB.LocationTable._ID + " = ?";
		String[] whereArgs = {"" + id};
		
		db.delete(PeoplesDB.LOCATION_TABLE_NAME, whereClause, whereArgs);
	}
	
	/**
	 * Delete a call from the database.
	 * 
	 * @param id - the id of the call to delete
	 */
	public void delCall(int id)
	{
		if (Config.D) Log.d(TAG, "Deleting call " + id);
		
		//set up the query
		String whereClause = PeoplesDB.CallLogTable._ID + " = ?";
		String[] whereArgs = {"" + id};
		
		db.delete(PeoplesDB.CALLLOG_TABLE_NAME, whereClause, whereArgs);
	}
}
