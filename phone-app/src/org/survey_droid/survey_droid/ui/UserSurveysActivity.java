/*---------------------------------------------------------------------------*
 * UserSurveysActivity.java                                                  *
 *                                                                           *
 * Allows users to take surveys that they select from a list.                *
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

import org.survey_droid.survey_droid.Config;
import org.survey_droid.survey_droid.Dispatcher;
import org.survey_droid.survey_droid.R;
import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.annotation.ConfigKey;
import org.survey_droid.survey_droid.content.ProviderContract.SurveyTable;
import org.survey_droid.survey_droid.content.SurveyDBHandler;
import org.survey_droid.survey_droid.survey.SurveyService;
import org.survey_droid.survey_droid.survey.SurveyService.SurveyType;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * Shows the user a list of surveys that can be started on demand and lets the
 * user do so.
 * 
 * @author Austin Walker
 */
public class UserSurveysActivity extends ListActivity
{
	/** logging tag */
	private static final String TAG = "UserSurveysActivity";
	
	/**  key to denote whether or not a the sample survey has been taken */
	@ConfigKey(value="false", global=true)
	public static final String SAMPLE_SURVEY_TAKEN =
		"user_surveys_activity.sample_survey_taken";
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);

        Util.d(null, TAG, "starting user surveys activity");
        
        //setting the layout of the activity
        Display display = ((WindowManager)
        		getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        //check what orientation the phone is in
        //getOrientation() is depreciated as of API 8, but we're targeting
        //API 7, so we have to use it
        if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
        {
        	setContentView(R.layout.user_surveys_horiz);
        }
        else
        {
        	setContentView(R.layout.user_surveys_vert);
        }
        
        //set the list data
        SurveyDBHandler sdbh = new SurveyDBHandler(this, 0);
        boolean sampleTaken =
        	Config.getBoolean(this, SAMPLE_SURVEY_TAKEN);
        final long[] ids;
        String[] names;
        Cursor surveys = sdbh.getSubjectInitSurveys();
        try
        {
	        //remember to add one so we can insert the sample survey
	        int count = surveys.getCount();
	        if (!sampleTaken) count++;
	        ids = new long[count];
	        names = new String[count];
	        surveys.moveToFirst();
	        int i = 0;
	        if (!sampleTaken) i++;
	        for (; !surveys.isAfterLast(); i++)
	        {
	        	ids[i] = surveys.getInt(surveys.getColumnIndexOrThrow(
	        			SurveyTable.Fields._ID));
	        	names[i] = surveys.getString(surveys.getColumnIndexOrThrow(
	        			SurveyTable.Fields.NAME));
	        	surveys.moveToNext();
	        }
        }
        finally
        {
        	surveys.close();
        }
        if (!sampleTaken)
        {
	        ids[0] = 0;
	        names[0] = "Sample Survey";
        }
        
        final ListView lv = getListView();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1, names);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id)
			{
				Util.d(null, TAG, "Starting survey " + ids[pos]);
				
				Intent surveyIntent =
					new Intent(UserSurveysActivity.this, SurveyService.class);
				surveyIntent.setAction(SurveyService.ACTION_SURVEY_READY);
				surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_TYPE,
						SurveyType.SURVEY_TYPE_USER_INIT.ordinal());
				surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_ID,
						ids[pos]);
				Uri uri = Uri.parse("user surveys survey");
				Dispatcher.dispatch(UserSurveysActivity.this, surveyIntent,
					0, Dispatcher.TYPE_WAKEFUL_MANUAL, uri);
				
				finish();
			}
		});
        
        //back button
        Button back = (Button) findViewById(R.id.user_surveys_backButton);
        back.setOnClickListener(new View.OnClickListener()
        {
        	@Override
            public void onClick(View view)
            {
            	finish();
            }
        });
	}
}
