/*---------------------------------------------------------------------------*
 * ComsDBHandler.java                                                        *
 *                                                                           *
 * Allows the communication service to perform needed database functions.    *
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

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.surveydroid.android.Util;

/**
 * Provides the database read/write methods needed by the communication
 * component.
 * 
 * @author Austin Walker
 */
public class ComsDBHandler extends SurveyDroidDBHandler
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

		String    table    = SurveyDroidDB.ANSWER_TABLE_NAME;
		String[]  cols     = null;
		String    selc     = SurveyDroidDB.AnswerTable.UPLOADED + " =  ?";
		String[]  selcArgs = {"0"};
		String    group    = null;
		String    having   = null;
		String    orderBy  = SurveyDroidDB.AnswerTable.CREATED;
		
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
		String    table    = SurveyDroidDB.TAKEN_TABLE_NAME;
		String[]  cols     = null;
		String    selc     = SurveyDroidDB.TakenTable.UPLOADED + " =  ?";
		String[]  selcArgs = {"0"};
		String    group    = null;
		String    having   = null;
		String    orderBy  = SurveyDroidDB.TakenTable.CREATED;
		
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

		String    table    = SurveyDroidDB.LOCATION_TABLE_NAME;
		String[]  cols     = null;
		String    selc     = null;
		String[]  selcArgs = null;
		String    group    = null;
		String    having   = null;
		String    orderBy  = SurveyDroidDB.LocationTable.TIME;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/**
	 * Get all the calls.  The results are ordered by time so that older
	 * older data is sent first.  Phone numbers returned by this function
	 * are <strong>not</strong> yet hashed, so remember to do so in order
	 * to protect privacy.
	 * 
	 * @param notUploaded - if true, only return calls that have not been
	 * uploaded
	 * 
	 * @return a {@link Cursor} with the data
	 */
	public Cursor getCalls(boolean notUploaded)
	{
		Util.d(null, TAG, "Fetching calls");
		
		//set up the query

		String    table    = SurveyDroidDB.CALLLOG_TABLE_NAME;
		String[]  cols     = null;
		String	  selc	   = null;
		if (notUploaded)
			selc = SurveyDroidDB.CallLogTable.UPLOADED + " = 0";
		String[]  selcArgs = null;
		String    group    = null;
		String    having   = null;
		String    orderBy  = SurveyDroidDB.CallLogTable.TIME;
		
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
		String    table    = SurveyDroidDB.STATUS_TABLE_NAME;
		String[]  cols     = null;
		String    selc     = null;
		String[]  selcArgs = null;
		String    group    = null;
		String    having   = null;
		String    orderBy  = SurveyDroidDB.StatusTable.CREATED;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/**
	 * Get another extra from the database if one exists.
	 * 
	 * @return a (@link Cursor} with another extra in it if one exists, or null
	 * if there are no more
	 */
	public Cursor getNextExtra()
	{
		Util.d(null, TAG, "Atempting to get another extra item");
		
		//do a preliminary query to find the first item
		String    table    = SurveyDroidDB.EXTRAS_TABLE_NAME;
		String[]  cols     = {SurveyDroidDB.ExtrasTable.CREATED};
		String    selc     = null;
		String[]  selcArgs = null;
		String    group    = null;
		String    having   = null;
		String    orderBy  = SurveyDroidDB.ExtrasTable.CREATED;
		
		//run it
		Cursor items =
			db.query(table, cols, selc, selcArgs, group, having, orderBy);
		
		if (items.getCount() == 0) return null;
		items.moveToFirst();
		String firstTime = items.getString(items.getColumnIndexOrThrow(
				SurveyDroidDB.ExtrasTable.CREATED));
		
		//now do the real query
				  table    = SurveyDroidDB.EXTRAS_TABLE_NAME;
		String[]  cols2    = {SurveyDroidDB.ExtrasTable._ID,
							  SurveyDroidDB.ExtrasTable.CREATED,
					          SurveyDroidDB.ExtrasTable.DATA,
					          SurveyDroidDB.ExtrasTable.TYPE};
				  selc     = SurveyDroidDB.ExtrasTable.CREATED + " = ?";
		String[]  selcArgs2 = {firstTime};
				  group    = null;
				  having   = null;
				  orderBy  = SurveyDroidDB.ExtrasTable.CREATED;
		
		//run it
		return db.query(table, cols2, selc, selcArgs2, group, having, orderBy);
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
		values.put(SurveyDroidDB.AnswerTable.UPLOADED, 1);
		String whereClause = SurveyDroidDB.AnswerTable._ID + " = ?";
		String[] whereArgs = {"" + id};
		
		db.update(SurveyDroidDB.ANSWER_TABLE_NAME, values, whereClause, whereArgs);
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
		values.put(SurveyDroidDB.TakenTable.UPLOADED, 1);
		String whereClause = SurveyDroidDB.TakenTable._ID + " = ?";
		String[] whereArgs = {"" + id};
		
		db.update(SurveyDroidDB.ANSWER_TABLE_NAME, values, whereClause, whereArgs);
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
		String whereClause = SurveyDroidDB.TakenTable._ID + " = ?";
		String[] whereArgs = {"" + id};
		
		db.delete(SurveyDroidDB.TAKEN_TABLE_NAME, whereClause, whereArgs);
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
		String whereClause = SurveyDroidDB.LocationTable._ID + " = ?";
		String[] whereArgs = {"" + id};
		
		db.delete(SurveyDroidDB.LOCATION_TABLE_NAME, whereClause, whereArgs);
	}
	
	/**
	 * Delete calls from the database such that only one call from each unique
	 * number is left.  Marks all the calls that are left as uploaded.
	 */
	public void delDuplicateCalls()
	{
		Util.d(null, TAG, "Deleting calls");
		
		//set of unique phone numbers
		Map<String, Boolean> unique_nums = new HashMap<String, Boolean>();
		
		Cursor calls = getCalls(false);
		
		if (calls.getCount() == 0)
		{
			//database is empty!
			calls.close();
			return;
		}
		
		calls.moveToFirst();
		int num_i = calls.getColumnIndexOrThrow(
				SurveyDroidDB.CallLogTable.PHONE_NUMBER);
		int id_i = calls.getColumnIndexOrThrow(
				SurveyDroidDB.CallLogTable.PHONE_NUMBER);
		int type_i = calls.getColumnIndexOrThrow(
				SurveyDroidDB.CallLogTable.CALL_TYPE);
		while (!calls.isAfterLast())
		{
			String num = calls.getString(num_i);
			if (!unique_nums.containsValue(num) && calls.getInt(type_i) !=
				SurveyDroidDB.CallLogTable.CallType.MISSED)
			{
				//this is a new number, so mark it
				unique_nums.put(num, true);
			}
			else
			{
				int id = calls.getInt(id_i);
				//this is an old number (or a missed call), so delete it
				Util.v(null, TAG, "Deleting call " + id);
				String whereClause = SurveyDroidDB.CallLogTable._ID + " = ?";
				String[] whereArgs = {"" + id};
				
				db.delete(SurveyDroidDB.CALLLOG_TABLE_NAME,
						whereClause, whereArgs);
			}
			calls.moveToNext();
		}
		calls.close();

		//now mark everything left as being uploaded
		ContentValues values = new ContentValues();
		values.put(SurveyDroidDB.CallLogTable.UPLOADED, 1);
		String whereClause = null;
		String[] whereArgs = null;
		
		db.update(SurveyDroidDB.CALLLOG_TABLE_NAME,
				values, whereClause, whereArgs);
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
		String whereClause = SurveyDroidDB.StatusTable._ID + " = ?";
		String[] whereArgs = {"" + id};
		
		db.delete(SurveyDroidDB.STATUS_TABLE_NAME, whereClause, whereArgs);
	}
	
	/**
	 * Delete an extra item from the database.
	 * 
	 * @param id - the id of the extra to delete
	 */
	public void delExtra(int id)
	{
		Util.d(null, TAG, "Deleting extra " + id);
		
		//set up the query
		String whereClause = SurveyDroidDB.ExtrasTable._ID + " = ?";
		String[] whereArgs = {"" + id};
		
		db.delete(SurveyDroidDB.EXTRAS_TABLE_NAME, whereClause, whereArgs);
	}
}
