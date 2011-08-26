/*---------------------------------------------------------------------------*
 * ComsDBHandler.java                                                        *
 *                                                                           *
 * Allows the communication service to perform needed database functions.    *
 *---------------------------------------------------------------------------*/
package org.peoples.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.peoples.android.Util;

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
	 * @return a {@link Cursor} with the data
	 */
	public Cursor getNewAnswers()
	{
		Util.d(null, TAG, "Fetching new answers");
		
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
	 * Get all survey completion data that haven't yet been uploaded.
	 * 
	 * @return a {@link Cursor} with the data
	 */
	public Cursor getNewCompletionData()
	{
		Util.d(null, TAG, "Fetching new survey completion data");
		
		//set up the query
		String    table    = PeoplesDB.TAKEN_TABLE_NAME;
		String[]  cols     = null;
		String    selc     = PeoplesDB.TakenTable.UPLOADED + " =  ?";
		String[]  selcArgs = {"0"};
		String    group    = null;
		String    having   = null;
		String    orderBy  = PeoplesDB.TakenTable.CREATED;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/**
	 * Get all the locations.  The results are ordered by time so that older
	 * older data is sent first.
	 * 
	 * @return a {@link Cursor} with the data
	 */
	public Cursor getLocations()
	{
		Util.d(null, TAG, "Fetching locations");
		
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
	 * older data is sent first.  Phone numbers returned by this function
	 * are <strong>not</strong> yet hashed, so remember to do so in order
	 * to protect privacy.
	 * 
	 * @return a {@link Cursor} with the data
	 */
	public Cursor getCalls()
	{
		Util.d(null, TAG, "Fetching calls");
		
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
	 * Get all of the status changes currently in the database.  Used to fetch
	 * data for conversion to JSON so it can be sent to the server.
	 * 
	 * @return a {@link Cursor} with the status changes in it
	 */
	public Cursor getStatusChanges()
	{
		Util.d(null, TAG, "Getting all status changes");
		
		//set up the query
		String    table    = PeoplesDB.STATUS_TABLE_NAME;
		String[]  cols     = {PeoplesDB.StatusTable.TYPE,
							  PeoplesDB.StatusTable.STATUS,
							  PeoplesDB.StatusTable.CREATED};
		String    selc     = null;
		String[]  selcArgs = null;
		String    group    = null;
		String    having   = null;
		String    orderBy  = PeoplesDB.StatusTable.CREATED;
		
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
		Util.d(null, TAG, "Marking answer " + id + " as uploaded");
		
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(PeoplesDB.AnswerTable.UPLOADED, 1);
		String whereClause = PeoplesDB.AnswerTable._ID + " = ?";
		String[] whereArgs = {"" + id};
		
		db.update(PeoplesDB.ANSWER_TABLE_NAME, values, whereClause, whereArgs);
	}
	
	/**
	 * Marks the survey completion record with id as having been uploaded.
	 * 
	 * @param id - id of the answer to mark
	 */
	public void updateCompletionRecord(int id)
	{
		Util.d(null, TAG, "Marking record " + id + " as uploaded");
		
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(PeoplesDB.TakenTable.UPLOADED, 1);
		String whereClause = PeoplesDB.TakenTable._ID + " = ?";
		String[] whereArgs = {"" + id};
		
		db.update(PeoplesDB.ANSWER_TABLE_NAME, values, whereClause, whereArgs);
	}
	
	/**
	 * Delete a survey completion record from the database.
	 * 
	 * @param id - the id of the record to delete
	 */
	public void delCompletionRecord(int id)
	{
		Util.d(null, TAG, "Deleting record " + id);
		
		//set up the query
		String whereClause = PeoplesDB.TakenTable._ID + " = ?";
		String[] whereArgs = {"" + id};
		
		db.delete(PeoplesDB.TAKEN_TABLE_NAME, whereClause, whereArgs);
	}
	
	/**
	 * Delete a location from the database.
	 * 
	 * @param id - the id of the location to delete
	 */
	public void delLocation(int id)
	{
		Util.d(null, TAG, "Deleting location " + id);
		
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
		Util.d(null, TAG, "Deleting call " + id);
		
		//set up the query
		String whereClause = PeoplesDB.CallLogTable._ID + " = ?";
		String[] whereArgs = {"" + id};
		
		db.delete(PeoplesDB.CALLLOG_TABLE_NAME, whereClause, whereArgs);
	}
	
	/**
	 * Delete a record of an application status change from the database.
	 * 
	 * @param id - the id of the record to delete
	 */
	public void delStatusChange(int id)
	{
		Util.d(null, TAG, "Deleting status change " + id);
		
		//set up the query
		String whereClause = PeoplesDB.StatusTable._ID + " = ?";
		String[] whereArgs = {"" + id};
		
		db.delete(PeoplesDB.STATUS_TABLE_NAME, whereClause, whereArgs);
	}
}
