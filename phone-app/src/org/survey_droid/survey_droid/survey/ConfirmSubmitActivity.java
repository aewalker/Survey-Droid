/*---------------------------------------------------------------------------*
 * ConfirmSubmitActivity.java                                                *
 *                                                                           *
 * Asks the user to either confirm their answers or go back.                 *
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
package org.survey_droid.survey_droid.survey;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.survey_droid.survey_droid.Dispatcher;
import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.R;
import org.survey_droid.survey_droid.survey.SurveyInterface;

/**
 * Activity that asks the subject to either confirm their survey answers so
 * they can be written to the database, or go back and change answers.
 * 
 * @author Austin Walker
 * @author Henry Liu
 */
public class ConfirmSubmitActivity extends Activity
{
	/** logging tag */
	private static final String TAG = "ConfirmSubmitActivity";
	
	/** access to the survey information from the survey service */
	private SurveyInterface survey;
	
	/** connection to the SurveyService */
	private ServiceConnection connection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			survey = SurveyInterface.Stub.asInterface(binder);
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {}
	};
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		Util.d(null, TAG, "Creating ConfirmSubmitActivity");
		
		//get the survey
		Intent bindIntent = new Intent(this, SurveyService.class);
		bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
		
		//setting the layout of the activity
        Display display = ((WindowManager)
        		getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        //check what orientation the phone is in
        //getOrientation() is depreciated as of API 8, but we're targeting
        //API 7, so we have to use it
        if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
        {
        	setContentView(R.layout.survey_confirmation_horiz);
        }
        else
        {
        	setContentView(R.layout.survey_confirmation_vert);
        }
		
		Button back =
			(Button) findViewById(R.id.survey_confirmation_backButton);
		back.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				onBackPressed();
			}
		});
		
		Button finish =
			(Button) findViewById(R.id.survey_confirmation_confirmButton);
		finish.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//finish up the backend stuff
				Intent finishIntent =
					new Intent(ConfirmSubmitActivity.this, SurveyService.class);
				finishIntent.setAction(SurveyService.ACTION_END_SURVEY);
				Uri uri = Uri.parse("confirm submit");
				Dispatcher.dispatch(ConfirmSubmitActivity.this, finishIntent,
					0, Dispatcher.TYPE_WAKEFUL_MANUAL, uri);
				
				//tell the user they're done
				Intent doneIntent =
					new Intent(ConfirmSubmitActivity.this,
							SurveyDoneActivity.class);
				startActivity(doneIntent);
				
				finish();
			}
		});
	}
    
    @Override
	public void onBackPressed()
    {
    	if (survey != null)
    	{
    		try
    		{
    			survey.prevQuestion();
    		}
    		catch (Exception e)
    		{
    			throw new RuntimeException("Failed to move to previous question", e);
    		}
			finish();
    	}
    }
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unbindService(connection);
	}
}
