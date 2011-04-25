/*---------------------------------------------------------------------------*
 * SurveyDBHandler.java                                                      *
 *                                                                           *
 * Extension of the PeoplesDBHandler for Surveys.  Has calls to write and    *
 * read data as needed by Surveys.                                           *
 *---------------------------------------------------------------------------*/
package com.peoples.android.database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * Handles Survey related calls to the PEOPLES database.
 * 
 * @author Austin Walker
 */
public class SurveyDBHandler extends PeoplesDBHandler
{
	//for writing to the log
	private static final String TAG = "SurveyDBHandler";
	private static final boolean D = true;
	
	/**
	 * Create a new SurveyDBHandler object.
	 * 
	 * @param context
	 */
	public SurveyDBHandler(Context context)
	{
		super(context);
	}
	
	/**
	 * Get the Survey level data.
	 * 
	 * @param id - the survey_id
	 */
	public Cursor getSurvey(int id)
	{
		if (D) Log.e(TAG, "getting survey");
		
		//set up the query

		String    table    = PeoplesDB.SURVEY_TABLE_NAME;
		String[]  cols     = {PeoplesDB.SurveyTable.NAME,
						      PeoplesDB.SurveyTable.QUESTION_ID};
		String    selc     = PeoplesDB.SurveyTable.ID + " =  ?";
		String[]  selcArgs = {Integer.toString(id)};
		String    group    = null;
		String    having   = null;
		String    orderBy  = null;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/**
	 * Get the Question level data.
	 * 
	 * @param id - the survey_id
	 */
	public Cursor getQuestions(int id)
	{
		if (D) Log.e(TAG, "getting questions");
		
		//set up the query
		String    table    = PeoplesDB.QUESTION_TABLE_NAME;
		String[]  cols     = {PeoplesDB.QuestionTable.Q_TEXT,
						      PeoplesDB.SurveyTable.QUESTION_ID};
		String    selc     = PeoplesDB.SurveyTable.ID + " =  ?";
		String[]  selcArgs = {Integer.toString(id)};
		String    group    = null;
		String    having   = null;
		String    orderBy  = null;

		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	
/*public Cursor getStoredLocations(){
		
		if(D) Log.e(TAG, "in getStoredLocations()");
		
		//Query Arguments
		String table		= PeoplesDB.GPS_TABLE_NAME;
		String[] 	columns			= null; //returns all columns
		String 		selection		= null; //will return all locations
		String[] 	selectionArgs	= null; //not needed, this isn't really a prepared statement
		String		groupBy			= null; //not grouping the rows
		String		having			= null; //SQL having clause, not needed
		String		orderBy			= null; //use the default sort order
		
		Cursor mCursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		
		if(mCursor != null)
			mCursor.moveToFirst();
		
		return mCursor;
	}*/
	
	//public long insertSurveyAsNext(Survey survey){
		
		//TODO: Austin implement
		
		// see 
		//http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html
		
		//Use static constants found in PeoplesDB, for example:
		//String surveyTableName = PeoplesDB.SURVEY_TABLE_NAME;
		
		//then you can do raw or prepared queries on the DB 
		
		//return 0L;
	//}
}
