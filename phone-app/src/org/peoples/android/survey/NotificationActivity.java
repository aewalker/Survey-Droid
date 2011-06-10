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
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.PowerManager.WakeLock;
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
	
    //has the subject started the survey (ie pressed take now)?
    private boolean started = false;
    
    //the id of the survey that is ready.
    private int id;
	
	//wakelock to keep the phone on while a survey is being started
	private WakeLock wl;
    
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		if (Config.D) Log.d(TAG, "Creating NotificationActivity");
		
		//get the survey id
		id = getIntent().getIntExtra(SurveyService.EXTRA_SURVEY_ID, 0);
		
		//wake up the phone if it's asleep
		PowerManager pm =
			(PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, TAG);
		wl.acquire();
		
		//vibrate the phone
		Vibrator vibrator =
			(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(Config.VIBRATION_TIME);
		
		wl.release();
		
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
				started = true;
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
				finish();
			}
		});
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		
		//Don't let this activity sit in the background; kill it if for some
		//reaon the subject tries to hide it without responding.  This way,
		//onDestroy() is called, which will cause this to pop back up later.
		finish();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		if (!started && isFinishing() && id != SurveyService.DUMMY_SURVEY_ID)
		{
			//set an alarm for this survey to reapear
			Intent postponeIntent =
				new Intent(getApplicationContext(), SurveyScheduler.class);
			postponeIntent.setAction(SurveyScheduler.ACTION_ADD_SURVEY);
			postponeIntent.putExtra(SurveyScheduler.EXTRA_SURVEY_ID, id);
			postponeIntent.putExtra(SurveyScheduler.EXTRA_SURVEY_TIME,
					Calendar.getInstance().getTimeInMillis()
					+ (Config.SURVEY_DELAY * 60 * 1000));
			startService(postponeIntent);
			
			//tell the survey service to shut down
			Intent stopServiceIntent =
				new Intent(getApplicationContext(), SurveyService.class);
			stopServiceIntent.setAction(SurveyService.ACTION_STOP_SURVEY);
			startService(stopServiceIntent);
		}
	}
}
