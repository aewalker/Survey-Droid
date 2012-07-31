/*---------------------------------------------------------------------------*
 * MainActivity.java                                                         *
 *                                                                           *
 * User control panel with buttons to adjust settings, show a sample survey, *
 * get the phone's id, and exit.                                             *
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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.survey_droid.survey_droid.BootIntentReceiver;
import org.survey_droid.survey_droid.Config;
import org.survey_droid.survey_droid.R;
import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.annotation.ConfigKey;
import org.survey_droid.survey_droid.content.TakenDBHandler;

/**
 * The Activity for the administration panel of the Survey Droid application.
 * 
 * @author Henry Liu
 * @author Austin Walker
 */
public class MainActivity extends Activity
{
	/** logging tag */
    private static final String TAG = "MainActivity";

    /** Phone number of the study administrator. */
    @ConfigKey("7652996509") //FIXME currently Austin's phone number
    public static final String ADMIN_PHONE_NUMBER = "admin_phone_number";

	/** Name of the study administrator. */
    @ConfigKey("Admin")
	public static final String ADMIN_NAME = "admin_name";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Util.d(null, TAG, "starting mainActivity");
        if (Config.getInt(this, BootIntentReceiver.STARTED_KEY) == 0)
        {
        	Util.w(null, TAG, "Background services not started; starting now");
        	BootIntentReceiver.startup(this);
        }
        
        //setting the layout of the activity
        Display display = ((WindowManager)
        		getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        //check what orientation the phone is in
        //getOrientation() is depreciated as of API 8, but we're targeting
        //API 7, so we have to use it
        if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
        { //yeah this makes no sense, but it works...
        	setContentView(R.layout.main_activity_horiz);
        }
        else
        {
        	setContentView(R.layout.main_activity_vert);
        }
        
        //go to settings button
        Button settings = (Button) findViewById(R.id.main_settingsButton);
        settings.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent settingsIntent = new Intent(MainActivity.this,
                		SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
        
        //user surveys button
        Button surveys = (Button) findViewById(R.id.main_sampleButton);
        surveys.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent surveyIntent = new Intent(MainActivity.this,
                		UserSurveysActivity.class);
                startActivity(surveyIntent);
            }
        });
        
        //call survey admin button
        //TODO move this to a per-study based thing
        Button call = (Button) findViewById(R.id.main_callButton);
        call.setText(call.getText() + Config.getString(this,
        		ADMIN_NAME, ""));
        call.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	Intent callIntent = new Intent(Intent.ACTION_CALL);
            	callIntent.setData(Uri.parse("tel:"
            			+ Config.getString(MainActivity.this,
            					ADMIN_PHONE_NUMBER, "")));
            	try
            	{
            		startActivity(callIntent);
            	}
            	catch (ActivityNotFoundException e)
            	{
            		Toast.makeText(MainActivity.this,
            				"Call failed!", Toast.LENGTH_SHORT);
            	}
            }
        });
        
        //exit button
        Button quit = (Button) findViewById(R.id.main_exitButton);
        quit.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	finish();
            }
        });
    }
    
    @Override
    protected void onStart()
    {
    	super.onStart();
        //add the survey progress bar
    	//do this here so the bar updates after a survey is
    	//finished without having to restart the activity
    	//TODO move to per-study thing
        int p = 50; //TakenDBHandler.getCompletionRate(this);
        VerticalProgressBar progress = (VerticalProgressBar)
        	findViewById(R.id.main_progressBar);
        progress.setMax(100);
        int goal = 75; //Config.getSetting(this, Config.COMPLETION_GOAL,
        		//Config.COMPLETION_GOAL_DEFAULT);
        progress.setSecondaryProgress(goal);
        if (p == TakenDBHandler.NO_PERCENTAGE)
        	//TODO find a way to make the bar indicate this better
        	progress.setProgress(0);
        else
        	progress.setProgress(p);
    }
}