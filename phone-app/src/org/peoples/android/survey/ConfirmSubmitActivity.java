/*---------------------------------------------------------------------------*
 * ConfirmSubmitActivity.java                                                *
 *                                                                           *
 * Asks the user to either confirm their answers or go back.                 *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.peoples.android.R;
import org.peoples.android.Util;
import org.peoples.android.survey.SurveyService.SurveyBinder;

/**
 * Activity that asks the subject to either confirm their survey answers so
 * they can be written to the database, or go back and change answers.
 * 
 * @author Austin Walker
 * @author Henry Liu
 */
public class ConfirmSubmitActivity extends Activity
{
	//logging tag
	private static final String TAG = "ConfirmSubmitActivity";
	
	//the current survey
	private Survey survey;
	
	//connection to the SurveyService
	private ServiceConnection connection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			SurveyBinder sBinder = (SurveyBinder) binder;
			survey = sBinder.getSurvey();
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
				survey.prevQuestion();
				Intent backIntent = new Intent(getThis(),
						QuestionActivity.getNextQusetionClass(
								survey.getQuestionType()));
    			startActivity(backIntent);
				finish();
			}
		});
		
		Button finish =
			(Button) findViewById(R.id.survey_confirmation_confirmButton);
		finish.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//show the extras page
				Intent extrasIntent = new Intent(
						getThis(), SurveyExtrasActivity.class);
				startActivity(extrasIntent);
				
				//submit the answers
				Intent finishIntent = new Intent(
						getThis(), SurveyService.class);
				finishIntent.setAction(SurveyService.ACTION_SUBMIT_ANSWERS);
				startService(finishIntent);
				
				finish();
			}
		});
	}
	
	//little hack to get the outer object
	private ConfirmSubmitActivity getThis()
	{
		return this;
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unbindService(connection);
	}
}
