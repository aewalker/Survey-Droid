/*---------------------------------------------------------------------------*
 * JoinStudiesActivity.java                                                  *
 *                                                                           *
 * Lets users join studies that have been found on a server.                 *
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
package org.survey_droid.survey_droid.ui;

import org.survey_droid.survey_droid.Study;
import org.survey_droid.survey_droid.Util;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Allows users to get information about and join studies that are available
 * from a given server.
 * 
 * @author Austin Walker
 */
public class JoinStudiesActivity extends ListActivity
{	
	/** logging tag */
	private static final String TAG = "JoinStudiesActivity";
	
	/** Extra key for the studies that this activity should display */
	public static final String EXTRA_STUDIES =
			"org.survey_droid.survey_droid.ui.EXTRA_STUDIES";
		
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);

        Util.d(null, TAG, "starting join studies activity");
        
        Intent i = this.getIntent();
        if (!i.hasExtra(EXTRA_STUDIES))
        {
        	Util.e(null, TAG, "JoinStudiesActivity started without any studies!");
        	finish();
        	return;
        }
        Study[] studies = null;
        try
        {
        	studies = (Study[]) i.getSerializableExtra(EXTRA_STUDIES);
        }
        catch (Exception e)
        {
        	Util.e(null, TAG, EXTRA_STUDIES + " given an incorrecto object type!");
        	finish();
        	return;
        }
        StudyAdapter sa = new StudyAdapter(this, studies);
        setListAdapter(sa);
	}
}
