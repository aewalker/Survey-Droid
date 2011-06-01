/*---------------------------------------------------------------------------*
 * NotificationActivity.java                                                 *
 *                                                                           *
 * Notifies the subject that they have a new survey to take.  Vibrates the   *
 * phone and shows a message with the option to take the survey or postpone  *
 * it for some time.                                                         *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.peoples.android.Config;
import org.peoples.android.R;

/**
 * Simple activity that shows a message and vibrates the phone when a new
 * survey is ready to be taken.  Offers the subject the options to take the
 * survey now or to pospone it for 15 minutes.
 * 
 * @author Austin Walker
 * @author Henry Liu
 */
public class NotificationActivity extends Activity
{
	//Logging tag
	private static final String TAG = "NotificationActivity";
	
	//time to vibrate to warn user, in milliseconds
    private static final long VIBRATION_TIME = 500;
    
    //time to delay a survey for in milliseconds
    private static final long DELAY = 15 * 60 * 1000;  //15 mins
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		if (Config.D) Log.d(TAG, "Creating NotificationActivity");
		
		//get the survey id
		final int id =
			getIntent().getIntExtra(SurveyService.EXTRA_SURVEY_ID, 0);
		
		//vibrate the phone
		Vibrator vibrator =
			(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(VIBRATION_TIME);
		
		//set up the views
		setContentView(R.layout.remind);
		final TextView msg = (TextView) findViewById(R.id.msg);
		msg.setText("You have a new survey awaiting");
		Button takeNow = (Button) findViewById(R.id.Enter);
		takeNow.setText("Take now");
		takeNow.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent surveyIntent =
					new Intent(getApplicationContext(), SurveyService.class);
				surveyIntent.setAction(SurveyService.ACTION_SHOW_SURVEY);
				surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_ID, id);
				startService(surveyIntent);
				finish();
			}
		});
		Button postpone = (Button) findViewById(R.id.postpone);
		postpone.setText("Pospone");
		postpone.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent postponeIntent =
					new Intent(getApplicationContext(), SurveyScheduler.class);
				postponeIntent.setAction(SurveyScheduler.ACTION_ADD_SURVEY);
				postponeIntent.putExtra(SurveyScheduler.EXTRA_SURVEY_ID, id);
				postponeIntent.putExtra(SurveyScheduler.EXTRA_SURVEY_TIME,
						Calendar.getInstance().getTimeInMillis() + DELAY);
				startService(postponeIntent);
			}
		});
		
	}
}
