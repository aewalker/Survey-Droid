/*---------------------------------------------------------------------------*
 * DatabaseConnection.java                                                   *
 *                                                                           *
 * Manages the singleton database connection.                                *
 *---------------------------------------------------------------------------*
 * Copyright 2012 Sema Berkiten, Vladimir Costescu, Henry Liu, Diego Vargas, *
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

import org.surveydroid.android.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Manages the single database connection in order to prevent threading
 * problems.
 * 
 * @author Austin Walker
 */
public class DatabaseConnection
{
	private static final String TAG = "DatabaseConnection";
	
	private Context c;
	
	private static DatabaseConnection conn;
	
	private SurveyDroidDB sddb;
	
	private SQLiteDatabase db;
	
	private int openCount = 0;
	
	/**
	 * Make sure there can only be one of this class
	 */
	private DatabaseConnection(Context c)
	{
		this.c = c;
	}
	
	public static DatabaseConnection getConnection(Context c)
	{
		if (conn == null) conn =
			new DatabaseConnection(c.getApplicationContext());
		return conn;
	}
	
	/**
	 * Open a database connection
	 */
	public synchronized SQLiteDatabase open()
	{
		Util.d(null, TAG, "opening read/write database connection");
		if (sddb == null)
			sddb = new SurveyDroidDB(c);
		db = sddb.getWritableDatabase();
		openCount++;
		return db;
	}

	/**
	 * Close the database.
	 */
	public synchronized void close()
	{
		Util.d(null, TAG, "closing database connection");
		openCount--;
		if (openCount == 0)
		{
			db.close();
			sddb.close();
			db = null;
		}
		if (openCount < 0)
			throw new IllegalStateException("database over-closed");
	}
}
