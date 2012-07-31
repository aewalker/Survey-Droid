/*---------------------------------------------------------------------------*
 * ComsDBHandler.java                                                        *
 *                                                                           *
 * Allows the communication service to perform needed database functions.    *
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

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.content.ProviderContract.CallLogTable.ContactType;
import org.survey_droid.survey_droid.content.ProviderContract.*;

/**
 * Provides the database read/write methods needed by the communication
 * component.
 * 
 * @author Austin Walker
 */
public class ComsDBHandler extends SurveyDroidDBHandler
{
	/** logging tag */
	private static final String TAG = "ComsDBHandler";
	
	/** the study id we are working with */
	private final long study_id;
	
	/**
	 * Constructor.
	 * 
	 * @param context
	 * @param study_id
	 */
	public ComsDBHandler(Context context, long study_id)
	{
		super(context);
		this.study_id = study_id;
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
		String    table    = AnswerTable.NAME;
		String[]  cols     = null; //get everything
		String    selc     = AnswerTable.Fields.UPLOADED + " =  ? AND " + AnswerTable.Fields.STUDY_ID + " = ?";
		String[]  selcArgs = {"0", Long.toString(study_id)};
		String    orderBy  = AnswerTable.Fields.CREATED;
		
		//run it
		return query(table, cols, selc, selcArgs, orderBy);
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
		String    table    = SurveysTakenTable.NAME;
		String[]  cols     = null; //get everything
		String    selc     = SurveysTakenTable.Fields.UPLOADED + " =  ? AND " + SurveysTakenTable.Fields.STUDY_ID + " = ?";
		String[]  selcArgs = {"0", Long.toString(study_id)};
		String    orderBy  = SurveysTakenTable.Fields.CREATED;
		
		//run it
		return query(table, cols, selc, selcArgs, orderBy);
	}
	
	/**
	 * Get all the locations.  The results are ordered by time so that older
	 * older data is sent first.  The caller must sort out whether or not each
	 * member of the returned cursor is to be uploaded for a given study.
	 * 
	 * @return a {@link Cursor} with the data
	 */
	public Cursor getLocations()
	{
		Util.d(null, TAG, "Fetching locations");
		
		//set up the query

		String    table    = LocationTable.NAME;
		String[]  cols     = null; //get everything
		String    selc     = null;
		String[]  selcArgs = null;
		String    orderBy  = LocationTable.Fields.TIME;
		
		//run it
		return query(table, cols, selc, selcArgs, orderBy);
	}
	
	/**
	 * Get all the contacts.  The results are ordered by time so that older
	 * older data is sent first.  Phone numbers returned by this function
	 * are <strong>not</strong> yet hashed, so remember to do so in order
	 * to protect privacy.
	 * 
	 * @param notUploaded if true, only return calls that have not been
	 * uploaded.  This function does not implement logic to determine if
	 * a given contact should be uploaded to a given study.
	 * 
	 * @return a {@link Cursor} with the data
	 */
	public Cursor getContacts(boolean notUploaded)
	{
		Util.d(null, TAG, "Fetching calls");
		
		//set up the query

		String    table    = CallLogTable.NAME;
		String[]  cols     = null; //get everything
		String	  selc	   = CallLogTable.Fields.UPLOAD_TO + " != ?";
		String[]  selcArgs = {"0"};
		if (!notUploaded)
		{
			selc = null;
			selcArgs = null;
		}
		String    orderBy  = CallLogTable.Fields.TIME;
		
		//run it
		return query(table, cols, selc, selcArgs, orderBy);
	}
	
	/**
	 * Get all of the status changes currently in the database.  Used to fetch
	 * data for conversion to JSON so it can be sent to the server.  Caller
	 * should be careful not to send data about (for example) call logging to
	 * studies that don't log calls.
	 * 
	 * @return a {@link Cursor} with the status changes in it
	 */
	public Cursor getStatusChanges()
	{
		Util.d(null, TAG, "Getting all status changes");
		
		//set up the query
		String    table    = StatusTable.NAME;
		String[]  cols     = null; //get everything
		String    selc     = null;
		String[]  selcArgs = null;
		String    orderBy  = StatusTable.Fields.CREATED;
		
		//run it
		return query(table, cols, selc, selcArgs, orderBy);
	}
	
	/**
	 * Marks the answer with id as having been uploaded.
	 * 
	 * @param id of the answer to mark
	 * @param newUploadStatus the new value to use for
	 * {@link AnswerTable.Fields#UPLOADED}
	 */
	public void updateAnswer(long id, String newUploadStatus)
	{
		Util.d(null, TAG, "Marking answer " + id + " as uploaded");
		
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(AnswerTable.Fields.UPLOADED, newUploadStatus);
		String whereClause = AnswerTable.Fields._ID + " = ?";
		String[] whereArgs = {Long.toString(id)};
		
		update(AnswerTable.NAME, values, whereClause, whereArgs);
	}
	
	/**
	 * Marks the survey completion record with id as having been uploaded.
	 * 
	 * @param id id of the answer to mark
	 */
	public void updateCompletionRecord(long id)
	{
		Util.d(null, TAG, "Marking record " + id + " as uploaded");
		
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(SurveysTakenTable.Fields.UPLOADED, 1);
		String whereClause = SurveysTakenTable.Fields._ID + " = ?";
		String[] whereArgs = {Long.toString(id)};
		
		update(SurveysTakenTable.NAME, values, whereClause, whereArgs);
	}
	
	/**
	 * Delete a survey completion record from the database.
	 * 
	 * @param id the id of the record to delete
	 */
	public void delCompletionRecord(long id)
	{
		Util.d(null, TAG, "Deleting record " + id);
		
		//set up the query
		String whereClause = SurveysTakenTable.Fields._ID + " = ?";
		String[] whereArgs = {Long.toString(id)};
		
		delete(SurveysTakenTable.NAME, whereClause, whereArgs);
	}
	
	/**
	 * Delete a location from the database.
	 * 
	 * @param id the id of the location to delete
	 */
	public void delLocation(long id)
	{
		Util.d(null, TAG, "Deleting location " + id);
		
		//set up the query
		String whereClause = LocationTable.Fields._ID + " = ?";
		String[] whereArgs = {Long.toString(id)};
		
		delete(LocationTable.NAME, whereClause, whereArgs);
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
		
		//highest id encountered
		long max_id = 0;
		
		Cursor contacts = getContacts(false);
		
		if (contacts.getCount() == 0)
		{
			//database is empty!
			contacts.close();
			return;
		}
		
		contacts.moveToFirst();
		int num_i = contacts.getColumnIndexOrThrow(
				CallLogTable.Fields.PHONE_NUMBER);
		int id_i = contacts.getColumnIndexOrThrow(
				CallLogTable.Fields._ID);
		int type_i = contacts.getColumnIndexOrThrow(
				CallLogTable.Fields.CALL_TYPE);
		int uploaded_i = contacts.getColumnIndexOrThrow(
			CallLogTable.Fields.UPLOAD_TO);
		while (!contacts.isAfterLast())
		{
			long id = contacts.getLong(id_i);
			if (id > max_id) max_id = id;
			String num = contacts.getString(num_i);
			ContactType type = ContactType.values()[contacts.getInt(type_i)];
			if (!unique_nums.containsValue(num) &&
					(type.equals(ContactType.INCOMING_CALL) ||
					 type.equals(ContactType.INCOMING_TEXT)))
			{
				//this is a new number, so mark it
				unique_nums.put(num, true);
			}
			else if (contacts.getString(uploaded_i).equals(""))
			{
				//this is an old number (or a missed call), so delete it
				Util.v(null, TAG, "Deleting call " + id);
				String whereClause = CallLogTable.Fields._ID + " = ?";
				String[] whereArgs = {Long.toString(id)};
				
				//TODO use mass delete
				delete(CallLogTable.NAME, whereClause, whereArgs);
			}
			contacts.moveToNext();
		}
		contacts.close();

		/* XXX move this to a method like updateContact or something
		//now mark everything left as being uploaded
		ContentValues values = new ContentValues();
		values.put(SurveyDroidDB.CallLogTable.UPLOADED, 1);
		String whereClause = SurveyDroidDB.CallLogTable._ID + " < ?";
		String[] whereArgs = {Integer.toString(max_id + 1)};
		
		//avoid a race condition: only update calls that have been seen
		//in case one is added during this method
		
		db.update(SurveyDroidDB.CALLLOG_TABLE_NAME,
				values, whereClause, whereArgs);
				*/
	}
	
	/**
	 * Delete a record of an application status change from the database.
	 * 
	 * @param id the id of the record to delete
	 */
	public void delStatusChange(long id)
	{
		Util.d(null, TAG, "Deleting status change " + id);
		
		//set up the query
		String whereClause = StatusTable.Fields._ID + " = ?";
		String[] whereArgs = {Long.toString(id)};
		
		delete(StatusTable.NAME, whereClause, whereArgs);
	}
}
