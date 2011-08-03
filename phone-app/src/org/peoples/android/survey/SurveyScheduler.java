/*---------------------------------------------------------------------------*
 * SurveyScheduler.java                                                      *
 *                                                                           *
 * Sets alarms to activate the survey service at the correct times.  This    *
 * service is initially activated by the boot intent receiver.  After that,  *
 * it sets alarms to reactivate itself as specified by the configuration     *
 * downloaded from the server.                                               *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import org.peoples.android.Config;
import org.peoples.android.Util;
import org.peoples.android.database.PeoplesDB;
import org.peoples.android.database.SurveyDBHandler;

/**
 * Schedules surveys based on the database information about them.  Reschedules
 * itself as specified by the configuration downloaded from the website (for
 * example, for one day later).
 * 
 * @author Austin Walker
 * @author Diego Vargas
 */
public class SurveyScheduler extends IntentService
{
	//logging tag
	private static final String TAG = "SurveyScheduler";
	
	//intent actions
	/**
	 * Use this action to tell the scheduler to schedule a survey for a
	 * particular time.  Must be used with {@link EXTRA_SURVEY_ID} and
	 * {@link EXTRA_SURVEY_TIME}.
	 */
	public static final String ACTION_ADD_SURVEY =
		"org.peoples.android.survey.ACTION_ADD_SURVEY";
	
	/**
	 * Tells the survey scheduler to look for surveys that need to be
	 * scheduled and do so.  Must be used with {@link EXTRA_RUNNING_TIME}.
	 */
	public static final String ACTION_SCHEDULE_SURVEYS =
		"org.peoples.android.survey.ACTION_SCHEDULE_SURVEYS";
	
	//intent extras
	//TODO use the EXTRA_SURVEY_ID from the survey service
	/**
	 * The id of the survey to be added.  Used with {@link ACTION_ADD_SURVEY}.
	 */
	public static final String EXTRA_SURVEY_ID =
		"org.peoples.android.survey.EXTRA_SURVEY_ID";
	
	//TODO add code to make use of this
	/**
	 * The number of times a survey has been skipped.  Used (optionally)
	 * with {@link ACTION_ADD_SURVEY}.
	 */
	public static final String EXTRA_SKIPPED_COUNT =
		"org.peoples.android.survey.EXTRA_SKIPPED_COUNT";
	
	/**
	 * The time the survey is to be scheduled for.  Used with
	 * {@link ACTION_ADD_SURVEY}.
	 */
	public static final String EXTRA_SURVEY_TIME =
		"org.peoples.android.survey.EXTRA_SURVEY_TIME";
	
	/**
	 * The time this service was set to run.  Used with
	 * {@link ACTION_SCHEDULE_SURVEYS}.
	 */
	public static final String EXTRA_RUNNING_TIME =
		"org.peoples.android.survey.EXTRA_RUNNING_TIME";
	
	/**
	 * Constructor.
	 * 
	 * @param name
	 */
	public SurveyScheduler()
	{
		super(null);
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		String action = intent.getAction();
		if (action.equals(ACTION_ADD_SURVEY))
		{
			//get and validate the time of day
			long time = intent.getLongExtra(EXTRA_SURVEY_TIME, -1);
			if (time == -1) throw new RuntimeException("No time given");
			
			//get the survey id
			int id = intent.getIntExtra(EXTRA_SURVEY_ID,
					SurveyService.DUMMY_SURVEY_ID);
			
			addSurvey(id, time);
		}
		else if (action.equals(ACTION_SCHEDULE_SURVEYS))
		{
			long time = intent.getLongExtra(EXTRA_RUNNING_TIME, -1);
			if (time == -1) throw new RuntimeException("No running time");
			scheduleSurveys(time);
		}
		else
		{
			//throw an error; unknown action requested
			//TODO could just ignore it instead...
			throw new RuntimeException("Unknown action requested");
		}
	}
	
	//schedule survey id for the given time
	private void addSurvey(int id, long time)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		Util.d(this, TAG, "Scheduling survey "
				+ id + " for " + c.getTime().toString());
		
		Intent surveyIntent = new Intent(getApplicationContext(),
				SurveyService.class);
		surveyIntent.setAction(SurveyService.ACTION_SURVEY_READY);
		surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_ID, id);
		surveyIntent.putExtra(EXTRA_RUNNING_TIME, time);
		PendingIntent pendingSurvey = PendingIntent.getService(
				getApplicationContext(), 0, surveyIntent,
				PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarm =
			(AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC_WAKEUP, time, pendingSurvey);
	}
	
	//look for surveys that need to be scheduled and do so
	private void scheduleSurveys(long runningTime)
	{
		Util.i(null, TAG, "Scheduling surveys");
		
		SurveyDBHandler sdbh = new SurveyDBHandler(this);
		sdbh.openRead();
		Cursor surveys = sdbh.getSurveys();
		
		surveys.moveToFirst();
		String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
		long nextRun = runningTime + (Config.getSetting(this,
				Config.SCHEDULER_INTERVAL, Config.SCHEDULER_INTERVAL_DEFAULT)
				* 60 * 1000);
		Util.v(null, TAG, "Current run time: "
			+ runningTime + ", next run time: " + nextRun);
		Util.d(this, TAG, "Number of surveys found: " + surveys.getCount());
		while (!surveys.isAfterLast())
		{
			int id = surveys.getInt(surveys.getColumnIndexOrThrow(
					PeoplesDB.SurveyTable._ID));
			Util.d(null, TAG, "Doing survey " + id);
			for (int i = 0; i < days.length; i++)
			{
				Util.v(null, TAG, "Doing " + days[i]);
				String timeString = surveys.getString(
						surveys.getColumnIndexOrThrow(
								PeoplesDB.SurveyTable.DAYS[i]));
				Util.v(null, TAG, "Time string: " + timeString);
				String[] times = timeString.split(",");
				for (String time : times)
				{
					if (time == "") continue;  //"".split(",") returns { "" }
					long scheduledTime = Util.getUnixTime(days[i], time);

					if (Config.D)
					{
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(scheduledTime);
						Util.v(null, TAG, "Survey would be scheduled for "
								+ c.getTime().toString());
						Util.v(null, TAG, "should be scheduled for "
								+ days[i] + " at " + time);
					}
					//FIXME should check the time better
					if (/*(scheduledTime < (nextRun + (60 * 1000)))
							&& (*/scheduledTime >= runningTime/*)*/)
					{
						addSurvey(id, scheduledTime);
					}
				}
			}
			surveys.moveToNext();
		}
		surveys.close();
		sdbh.close();
		
		//make sure to run this again later
		Intent schedulerIntent = new Intent(getApplicationContext(),
				SurveyScheduler.class);
		schedulerIntent.setAction(ACTION_SCHEDULE_SURVEYS);
		schedulerIntent.putExtra(EXTRA_RUNNING_TIME, nextRun);
		PendingIntent pendingScheduler = PendingIntent.getService(
				getApplicationContext(), 0, schedulerIntent,
				PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarm =
			(AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC_WAKEUP, nextRun, pendingScheduler);
	}
}
