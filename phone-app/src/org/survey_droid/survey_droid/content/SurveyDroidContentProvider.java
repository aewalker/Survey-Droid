/*---------------------------------------------------------------------------*
 * SurveyDroidContentProvider.java                                           *
 *                                                                           *
 * Interface to all data used by the system as well as any extensions.       *
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

import java.util.ArrayList;
import java.util.List;

import org.survey_droid.survey_droid.Util;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Interface to all data used by the system as well as any extensions.
 * Content Uris should have a table name from {@link ProviderContract} as
 * the first path element.  Query, delete, and update are allowed to have
 * a row id appended as the second element.  Any other Uri patterns will cause
 * an IllegalArgumentException to be thrown.
 * 
 * @author Austin Walker
 */
public class SurveyDroidContentProvider extends ContentProvider
{
	/** Database helper used to make calls */
    private SurveyDroidDB sddb;

    /** logging tag */
    private static final String TAG = "SurveyDroidContentProvider";

    /** The actual database object */
    private SQLiteDatabase db;
    
    /**
     * Extract and validate the table name from a uri.
     * 
     * @param uri the request made
     * @param rowid if there is a row id appended, then the first item in this
     * array will be set to that id
     * @return the table name the request is performed on
     * @throws IllegalArgumentException if the path has more than one
     * segment, no segments, or refers to an invalid table or row
     */
    public static String getTable(Uri uri, long[] rowid)
    {
    	List<String> segments = uri.getPathSegments();
		if (segments.size() > 2)
			throw new IllegalArgumentException("Path has more than two segments");
		if (segments.size() == 0)
			throw new IllegalArgumentException("Path has no segments (no table selected)");
		String requestedTable = segments.get(0);
		if (segments.size() == 2)
		{
			rowid[0] = Long.parseLong(segments.get(1));
			if (rowid[0] < 0) throw new IllegalArgumentException("Invalid row id: " + rowid[0]);
		}
    	for (String table : SurveyDroidDB.tables)
    	{
    		if (table.equals(requestedTable)) return table;
    	}
    	throw new IllegalArgumentException("Invalid table name: " + requestedTable);
    }
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		long[] id = { -1 };
		String table = getTable(uri, id);
		if (id[0] != -1)
		{
			//if an id was given in the uri, override the query
			selection = BaseColumns._ID + " = ?";
			selectionArgs = new String[1];
			selectionArgs[0] = Long.toString(id[0]);
		}
		db = sddb.getWritableDatabase();
		return db.delete(table, selection, selectionArgs);
	}
	
	@Override
	public String getType(Uri uri)
	{
		return getTypeStatic(uri);
	}
	
	/**
	 * Hack to allow getType to be used outside this provider
	 * 
	 * @param uri
	 * @return see {@link #getType(Uri)}
	 */
	public static String getTypeStatic(Uri uri)
	{
		long[] id = { -1 };
		String table = getTable(uri, id);
		if (id[0] != -1)
		{
			return "vnd.android.cursor.item/vnd.org.survey_droid.provider." + table;
		}
		return "vnd.android.cursor.dir/vnd.org.survey_droid.provider." + table;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		long[] id = { -1 };
		String table = getTable(uri, id);
		if (id[0] != -1) throw new IllegalArgumentException("Cannot pass a row id to insert");
		db = sddb.getWritableDatabase();
		long resultID = db.insert(table, null, values);
		if (resultID == -1)
		{
			throw new IllegalArgumentException("Failed to execute insert with given uri");
		}
		return uri.buildUpon().appendPath(Long.toString(resultID)).build();
	}
	
	@Override
	public boolean onCreate()
	{
		Util.i(null, TAG, "onCreate()");
		
		//this method has to return quickly, so don't actually open the
		//database here
        sddb = new SurveyDroidDB(getContext());
        return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
		String[] selectionArgs, String sortOrder)
	{
		long[] id = { -1 };
		String table = getTable(uri, id);
		if (id[0] != -1)
		{
			//if an id was given in the uri, override the query
			projection = new String[1];
			projection[0] = BaseColumns._ID;
			selection = BaseColumns._ID + " = ?";
			selectionArgs = new String[1];
			selectionArgs[0] = Long.toString(id[0]);
		}
		db = sddb.getWritableDatabase();
		return db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
		String[] selectionArgs)
	{
		long[] id = { -1 };
		String table = getTable(uri, id);
		if (id[0] != -1)
		{
			//if an id was given in the uri, override the query
			selection = BaseColumns._ID + " = ?";
			selectionArgs = new String[1];
			selectionArgs[0] = Long.toString(id[0]);
		}
		db = sddb.getWritableDatabase();
		return db.update(table, values, selection, selectionArgs);
	}
	
	@Override
	public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
	{
		db = sddb.getWritableDatabase();
		db.beginTransaction();
		try
		{
			ContentProviderResult[] result = super.applyBatch(operations);
			db.setTransactionSuccessful();
			db.endTransaction();
			return result;
		}
		catch (OperationApplicationException e)
		{
			db.endTransaction();
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values)
	{
		long[] id = { -1 };
		String table = getTable(uri, id);
		if (id[0] != -1) throw new IllegalArgumentException("Cannot pass a row id to bulkInsert");
		db = sddb.getWritableDatabase();
		db.beginTransaction();
		boolean errorOccured = false;
		SQLException error = null;
		int numInserted = 0;
		try
		{
			for (int i = 0; i < values.length; i++)
			{
				long result = db.insert(table, null, values[i]);
				if (result == -1)
				{
					numInserted = 0;
					throw new SQLException("Failed to execute insert number " + i);
				}
				numInserted += result;
			}
			db.setTransactionSuccessful();
		}
		catch (SQLException e)
		{
			error = e;
			errorOccured = true;
		}
		db.endTransaction();
		if (errorOccured)
		{
			throw new IllegalArgumentException("Failed to execute insert with given uri", error);
		}
		return numInserted;
	}
}
