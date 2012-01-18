/*---------------------------------------------------------------------------*
 * SurveyDroidDBHandler.java                                                 *
 *                                                                           *
 * Base class that other DB helpers inherit from.  Just contains generic     *
 * database open/close calls.  Any kind of database task that needs to be    *
 * performed by many different parts of the application can go here, but     *
 * more specific functions should be in their own class that extends this.   *
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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.surveydroid.android.Util;

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
	//logging tag
	private static final String TAG = "SurveyDroidDBHandler";
	
	private SurveyDroidDB pdb;
	
	/**
	 * The {@link Context} this handler was created with; used for database
	 * calls.
	 */
	protected Context contx;
	
	/** The {@link SQLiteDatabase} used to make database calls. */
	protected SQLiteDatabase db;
	
	/**
	 * Simple constructor.
	 * 
	 * @param context - the current {@link Context}
	 */
	public SurveyDroidDBHandler(Context context)
	{
		this.contx = context;
	}
	
	/**
	 * Open a read/write connection to the database.
	 */
	public void openWrite()
	{
		Util.d(null, TAG, "opening read/write database connection");
		pdb = new SurveyDroidDB(contx);
		db  = pdb.getWritableDatabase();
	}
	
	/**
	 * Open a read-only connection to the database.
	 */
	public void openRead()
	{
		Util.d(null, TAG, "opening read-only database connection");
		pdb = new SurveyDroidDB(contx);
		db  = pdb.getReadableDatabase();
	}

	/**
	 * Close the database; should be called after ever getReadable or
	 * getWritable.
	 */
	public void close()
	{
		Util.d(null, TAG, "closing database connection");
		db.close();
		pdb.close();
	}
}
