/*---------------------------------------------------------------------------*
 * UserSurveysActivity.java                                                  *
 *                                                                           *
 * Allows users to take surveys that they select from a list.                *
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
package org.surveydroid.android;

import org.surveydroid.android.database.SurveyDroidDB;
import org.surveydroid.android.database.SurveyDBHandler;
import org.surveydroid.android.survey.SurveyService;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
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
	//logging tag
	private static final String TAG = "UserSurveysActivity";
	
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
        SurveyDBHandler sdbh = new SurveyDBHandler(this);
        sdbh.open();
        Cursor surveys = sdbh.getSubjectInitSurveys();
        //remember to add one so we can insert the sample survey
        int count = surveys.getCount();
        boolean sampleTaken =
        	Config.getSetting(this, Config.SAMPLE_SURVEY_TAKEN, false);
        if (!sampleTaken) count++;
        final int[] ids = new int[count];
        String[] names = new String[count];
        surveys.moveToFirst();
        int i = 0;
        if (!sampleTaken) i++;
        for (; !surveys.isAfterLast(); i++)
        {
        	ids[i] = surveys.getInt(surveys.getColumnIndexOrThrow(
        			SurveyDroidDB.SurveyTable._ID));
        	names[i] = surveys.getString(surveys.getColumnIndexOrThrow(
        			SurveyDroidDB.SurveyTable.NAME));
        	surveys.moveToNext();
        }
        surveys.close();
        sdbh.close();
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
						SurveyService.SURVEY_TYPE_USER_INIT);
				surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_ID,
						ids[pos]);
				startService(surveyIntent);
				
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
