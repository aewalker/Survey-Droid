/*---------------------------------------------------------------------------*
 * MockSurveyDroidProvider.java                                              *
 *                                                                           *
 * Fake provider used for testing.                                           *
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
package org.survey_droid.survey_droid.test;

import org.survey_droid.survey_droid.content.SurveyDroidContentProvider;
import org.survey_droid.survey_droid.content.SurveyDroidDB;
import org.survey_droid.survey_droid.content.SurveyDroidDBHandler;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Provider that is made for testing.  Contains some dummy data.
 */
//TODO if we move up to API 8, use the actual mock provider
public class MockSurveyDroidProvider extends SurveyDroidContentProvider
{
	private ProviderListener listener;
	
	public static abstract class ProviderListener
	{
		public abstract void onQuery(String method, Uri uri, String[] cols,
				String selection, String[] selectionArgs, ContentValues values,
				String sortOrder);
	}
	
	public void setListener(ProviderListener listener)
	{
		this.listener = listener;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		if (listener != null)
			listener.onQuery("delete", uri, null, selection, selectionArgs, null, null);
		return super.delete(uri, selection, selectionArgs);
	}

	@Override
	public String getType(Uri uri)
	{
		return SurveyDroidContentProvider.getTypeStatic(uri);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		if (listener != null)
			listener.onQuery("insert", uri, null, null, null, values, null);
		return super.insert(uri, values);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder)
	{
		if (listener != null)
			listener.onQuery("query", uri, projection, selection, selectionArgs, null, sortOrder);
		return super.query(uri, projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs)
	{
		if (listener != null)
			listener.onQuery("update", uri, null, selection, selectionArgs, values, null);
		return super.update(uri, values, selection, selectionArgs);
	}
	
	/**
	 * Add data to the provider with triggering the listener.
	 * 
	 * @param table
	 * @param values
	 */
	public void inject(String table, ContentValues values)
	{
		Uri uri = SurveyDroidDBHandler.uriForTable(table);
		super.insert(uri, values);
	}
	
	/**
	 * Clear all data.
	 */
	public void clear()
	{
		for (String table : SurveyDroidDB.tables)
		{
			Uri uri = SurveyDroidDBHandler.uriForTable(table);
			super.delete(uri, null, null);
		}
	}
}
