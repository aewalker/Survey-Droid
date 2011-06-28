/*---------------------------------------------------------------------------*
 * SurveyService.java                                                        *
 *                                                                           *
 * Runs while the user it taking a survey; holds the survey object.  This    *
 * allows the user to rotate the screen without having to rebuild the whole  *
 * survey.                                                                   *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

//import java.util.Calendar;

import java.util.Calendar;

import android.app.Service;
//import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.peoples.android.Config;
//import org.peoples.android.coms.ComsService;

/**
 * Runs while a survey is being administered to the user.  "Spawns" instances
 * of {@link QuestionActivity} to show individual questions.
 * 
 * @author Austin Walker
 */
public class SurveyService extends Service
{
	//intent actions
	/**
	 * A survey is ready but has not been accepted by the user.  Intent must
	 * include a survey id in {@link EXTRA_SURVEY_ID}.
	 */
	public static final String ACTION_SURVEY_READY =
		"org.peoples.android.survey.ACTION_SURVEY_READY";
	
	/**
	 * A survey is ready and has been accepted by the user.  Should only be
	 * sent after {@link ACTION_SURVEY_READY}, otherwise the survey will not
	 * be built and an exception will be thrown.
	 */
	public static final String ACTION_SHOW_SURVEY =
		"org.peoples.android.survey.ACTION_SHOW_SURVEY";
	
	/**
	 * Submit all live answers for this survey and stop. Should only be
	 * sent after {@link ACTION_SURVEY_READY}, otherwise the survey will not
	 * be built and an exception will be thrown.
	 */
	public static final String ACTION_SUBMIT_ANSWERS = 
		"org.peoples.android.survey.ACTION_SUBMIT_ANSWERS";
	
	/**
	 * Stop the survey service.  Used when the user has finished a survey.
	 */
	public static final String ACTION_END_SURVEY =
		"org.peoples.android.survey.ACTION_END_SURVEY";
	
	//key values for extras
	/** The id of the survey this service is starting for. */
	public static final String EXTRA_SURVEY_ID =
		"org.peoples.android.survey.EXTRA_SURVEY_ID";

	/** Name/title of the current survey */
	public static final String EXTRA_SURVEY_NAME =
		"org.peoples.android.survey.EXTRA_SURVEY_NAME";
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Given this id, a dummy survey will be used and the answers will not be
	 * recorded.
	 */
	public static final int DUMMY_SURVEY_ID = 0;
	
	//the survey instance that each instance of this service uses
	private Survey survey;
	
	//is the survey running?
	private Boolean active;
	
	//the binder to send to clients
	private final SurveyBinder surveyBinder = new SurveyBinder();
	
	//logging tag
	private static final String TAG = "SurveyService";
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid)
	{
		//filter the intent action
		String action = intent.getAction();
		if (action.equals(ACTION_SURVEY_READY))
		{
			//check to make sure only one thing is running
			synchronized (active)
			{
				if (active)
				{
					//another survey is already running, so delay the new one
					Intent delayIntent =
						new Intent(this, SurveyScheduler.class);
					delayIntent.setAction(SurveyScheduler.ACTION_ADD_SURVEY);
					delayIntent.putExtra(SurveyScheduler.EXTRA_SURVEY_ID,
							intent.getIntExtra(EXTRA_SURVEY_ID, DUMMY_SURVEY_ID));
					delayIntent.putExtra(SurveyScheduler.EXTRA_SURVEY_TIME,
							Calendar.getInstance().getTimeInMillis()
							+ (Config.SURVEY_DELAY * 60 * 1000));
					startService(delayIntent);
					return START_STICKY;
				}
			
				//check that surveys are enabled
				Config cfg = new Config(this);
				if (!cfg.isSurveyEnabled())
				{
					//if the user is already taking a survey, then we don't want
					//to stop it, even if s/he disables surveys during it
					if (!active)
					{
						stopSelf();
						return START_NOT_STICKY;
					}
					return START_STICKY;
				}
				active = true;
			}
			
			/*
			 * Build the survey here, before anything is shown to the user.
			 * The assumption is that most of the time, the user will take the
			 * survey when first asked to.  Because building a survey can be
			 * time consuming, build it here to provide the user with a snappy
			 * UI.  However, IT IS CRITICAL that future changes ensure that
			 * this occurs after the check to see whether another survey is
			 * running.
			 */
			int surveyID =
				intent.getIntExtra(EXTRA_SURVEY_ID, DUMMY_SURVEY_ID);
			Log.i(TAG, "Starting survey service for survey " + surveyID);
			if (surveyID == DUMMY_SURVEY_ID)
				survey = new Survey(this);
			else survey = new Survey(surveyID, this);
			
			Intent notificationIntent =
				new Intent(this, NotificationActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			notificationIntent.putExtra(EXTRA_SURVEY_NAME, survey.getName());
			startActivity(notificationIntent);
		}
		else if (action.equals(ACTION_END_SURVEY))
		{
			stopSelf();
		}
		else
		{
			if (survey == null)
			{ //if we get to here, then the service has been asked to do
			  //something before it's survey was initialized
				throw new RuntimeException("Survey uninitialized");
			}
			if (action.equals(ACTION_SHOW_SURVEY))
			{
				//survey is just being started
				if (Config.D) Log.d(TAG, "Starting survey");
				
				//spawn the first QuestionActivity
				Intent questionIntent = new Intent(getApplicationContext(),
						QuestionActivity.class);
				questionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(questionIntent);
			}
			else
			{
				//unknown action requested
				
				//When debugging, crash the program in order to make it obvious
				//that something is up.  Otherwise, log a warning, but don't
				//crash anything.
				Log.w(TAG, "Unkown intent action: " + action);
				if (Config.D)
				{
					throw new RuntimeException("Unknown intent action: "
							+ action);
				}
			}
		}
		return START_STICKY;
	}

	/**
	 * Simple Binder extension that provides a survey object.
	 * 
	 * @author Austin Walker
	 */
	public class SurveyBinder extends Binder
	{
		/**
		 * Called to get the survey object currently running.
		 * 
		 * @return the Survey object
		 */
		public Survey getSurvey()
		{
			return survey;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return surveyBinder;
	}
}
