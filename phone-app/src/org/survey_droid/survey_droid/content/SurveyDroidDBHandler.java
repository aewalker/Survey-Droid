/*---------------------------------------------------------------------------*
 * SurveyDroidDBHandler.java                                                 *
 *                                                                           *
 * Base class that other DB helpers inherit from.  Just contains generic     *
 * database open/close calls.  Any kind of database task that needs to be    *
 * performed by many different parts of the application can go here, but     *
 * more specific functions should be in their own class that extends this.   *
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
import android.net.Uri;

/**
 * 
 * Interact with our database using this class, {@link SurveyDroidDB} will
 * keep track of the version of the DB, creating the DB, updating
 * the DB, and other versioning manipulations.
 * 
 * @author Diego Vargas
 * @author Austin Walker
 */
public class SurveyDroidDBHandler
{
	/**
	 * The {@link Context} this handler was created with; used for database
	 * calls.
	 */
	protected static Context contx;
	
	/**
	 * Simple constructor.
	 * 
	 * @param context - the current {@link Context}
	 */
	public SurveyDroidDBHandler(Context context)
	{
		contx = context;
	}
	
	/**
	 * @param table a table name from the provider contract
	 * @return the Uri for that table
	 */
	private Uri uriForTable(String table)
	{
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("content");
		builder.authority(ProviderContract.AUTHORITY);
		builder.appendPath(table);
		return builder.build();
	}
	
	/**
	 * Execute a query as in
	 * {@link android.content.ContentResolver#query(Uri, String[], String, String[], String)}
	 * 
	 * @param table
	 * @param cols
	 * @param selc
	 * @param selcArgs
	 * @param orderBy
	 * 
	 * @return the resulting cursor
	 */
	protected Cursor query(String table, String[] cols, String selc, String[] selcArgs, String orderBy)
	{
		return contx.getContentResolver().query(uriForTable(table), cols, selc, selcArgs, orderBy);
	}
	
	/**
	 * Execute an insert as in
	 * {@link android.content.ContentResolver#insert(Uri, ContentValues)}
	 * 
	 * @param table
	 * @param values
	 * 
	 * @return the Uri of the new item
	 */
	protected Uri insert(String table, ContentValues values)
	{
		return contx.getContentResolver().insert(uriForTable(table), values);
	}
	
	/**
	 * Execute an update as in
	 * {@link android.content.ContentResolver#update(Uri, ContentValues, String, String[])}
	 * 
	 * @param table
	 * @param values
	 * @param selc
	 * @param selcArgs
	 * 
	 * @return the number of rows affected
	 */
	protected int update(String table, ContentValues values, String selc, String[] selcArgs)
	{
		return contx.getContentResolver().update(uriForTable(table), values, selc, selcArgs);
	}
	
	/**
	 * Execute a delete as in
	 * {@link android.content.ContentResolver#delete(Uri, String, String[])
	 * 
	 * @param table
	 * @param selc
	 * @param selcArgs
	 * 
	 * @return the number of rows deleted
	 */
	protected int delete(String table, String selc, String[] selcArgs)
	{
		return contx.getContentResolver().delete(uriForTable(table), selc, selcArgs);
	}
}
