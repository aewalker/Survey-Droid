/*---------------------------------------------------------------------------*
 * StatusDBHandler.java                                                      *
 *                                                                           *
 * Extension of the SurveyDroidDBHandler for application status changes.     *
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

import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.content.ProviderContract.StatusTable;
import org.survey_droid.survey_droid.content.ProviderContract.StatusTable.Feature;

import android.content.ContentValues;
import android.content.Context;

/**
 * Handles application status related calls to the Survey Droid database.
 * 
 * @author Austin Walker
 */
public class StatusDBHandler extends SurveyDroidDBHandler
{
	/** logging tag */
	private static final String TAG = "StatusDBHandler";
	
	/**
	 * Create a new StatusDBHandler object.
	 * 
	 * @param context
	 */
	public StatusDBHandler(Context context)
	{
		super(context);
	}
	
	/**
	 * Write to the database that a feature has been enabled or disabled.
	 * 
	 * @param feature the feature affected (see {@link SurveyDroidDB#StatusTable}
	 * for the feature codes)
	 * @param enabled true if the feature was enabled, false if it was
	 * disabled
	 * @param created when the setting was changed
	 * @return true on success
	 */
	public boolean statusChanged(Feature feature, boolean enabled, long created)
	{
		Util.d(null, TAG, "Writing status change: "
				+ feature + " is " + enabled);
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(StatusTable.Fields.FEATURE, feature.ordinal());
		if (enabled) values.put(StatusTable.Fields.STATUS,
			StatusTable.STATUS_ON);
		else values.put(StatusTable.Fields.STATUS,
			StatusTable.STATUS_OFF);
		values.put(StatusTable.Fields.CREATED, created);
		
		//run it
		insert(StatusTable.NAME, values);
		return true;
	}
}