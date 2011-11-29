/*---------------------------------------------------------------------------*
 * SurveyScheduler.java                                                      *
 *                                                                           *
 * Sets alarms to activate the survey service at the correct times.  This    *
 * service is initially activated by the boot intent receiver.  After that,  *
 * it sets alarms to reactivate itself as specified by the configuration     *
 * downloaded from the server.                                               *
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
package org.surveydroid.android.survey;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import org.surveydroid.android.Config;
import org.surveydroid.android.Util;
import org.surveydroid.android.database.SurveyDroidDB;
import org.surveydroid.android.database.SurveyDBHandler;

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
	 * particular time.  Must be used with {@link #EXTRA_SURVEY_ID} and
	 * {@link #EXTRA_SURVEY_TIME}.
	 */
	public static final String ACTION_ADD_SURVEY =
		"org.surveydroid.android.survey.ACTION_ADD_SURVEY";
	
	/**
	 * Tells the survey scheduler to look for surveys that need to be
	 * scheduled and do so.  Can be used with {@link #EXTRA_RUNNING_TIME}.
	 */
	public static final String ACTION_SCHEDULE_SURVEYS =
		"org.surveydroid.android.survey.ACTION_SCHEDULE_SURVEYS";
	
	//intent extras
	//TODO use the EXTRA_SURVEY_ID from the survey service (?)
	/**
	 * The id of the survey to be added.  Used with {@link #ACTION_ADD_SURVEY}.
	 */
	public static final String EXTRA_SURVEY_ID =
		"org.surveydroid.android.survey.EXTRA_SURVEY_ID";
	
	/**
	 * The time the survey is to be scheduled for.  Used with
	 * {@link #ACTION_ADD_SURVEY}.
	 */
	public static final String EXTRA_SURVEY_TIME =
		"org.surveydroid.android.survey.EXTRA_SURVEY_TIME";
	
	/**
	 * Is this survey randomly timed?  Used with
	 * {@link #ACTION_ADD_SURVEY}.
	 */
	public static final String EXTRA_SURVEY_IS_RANDOM =
		"org.surveydroid.android.survey.EXTRA_SURVEY_IS_RANDOM";
	
	/**
	 * The time this service was set to run.  Used with
	 * {@link #ACTION_SCHEDULE_SURVEYS}.
	 */
	public static final String EXTRA_RUNNING_TIME =
		"org.surveydroid.android.survey.EXTRA_RUNNING_TIME";
	
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
			
			//is this random?  assume not
			boolean random = intent.getBooleanExtra(
					EXTRA_SURVEY_IS_RANDOM, false);
			
			addSurvey(id, time, random);
		}
		else if (action.equals(ACTION_SCHEDULE_SURVEYS))
		{
			long time = intent.getLongExtra(EXTRA_RUNNING_TIME, -1);
			scheduleSurveys(time);
		}
		else
		{
			Util.w(null, TAG, "Unknown action requested: " + action);
			if (Config.D)
				throw new RuntimeException("Unknown action requested: "
						+ action);
		}
	}
	
	//schedule survey id for the given time
	private void addSurvey(int id, long time, boolean random)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		Util.d(this, TAG, "Scheduling survey "
				+ id + " for " + c.getTime().toGMTString());
		
		Intent surveyIntent = new Intent(this, SurveyService.class);
		surveyIntent.setAction(SurveyService.ACTION_SURVEY_READY);
		if (random)
		{
			surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_TYPE,
					SurveyService.SURVEY_TYPE_RANDOM);
		}
		else
		{
			surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_TYPE,
					SurveyService.SURVEY_TYPE_TIMED);
		}
		/*
		 * Extras aren't compared by filterIntent(), so set the data field to
		 * be the survey id.  This way, if two different surveys would go off
		 * at the same time, they will, as opposed to one wiping out the other
		 * if this was not done.
		 */
		Uri uri = Uri.parse(Integer.toString(id));
		surveyIntent.setData(uri);
		surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_ID, id);
		surveyIntent.putExtra(EXTRA_RUNNING_TIME, time);
		/*
		 * We have a tricky situation here.  Each survey "instance" (that is,
		 * a survey at a particular date/time) must be uniquely identified by
		 * request code.  However, that code must be the same each time it is
		 * generated in order to prevent one survey instance from being
		 * scheduled multiple times, which would occur if this service is run
		 * again before that instance is set to be delivered.  Thus, we
		 * essentially need a hash function that takes a time and an id and
		 * produces a unique int in a deterministic way.
		 * 
		 * Since we set the data in the intent above to be the id, we can do
		 * away with having to differentiate between ids; now we only need to
		 * know the time.  Since surveys can only be scheduled at minute
		 * intervals, we can divide the time by 60 * 1000 to get the number of
		 * minutes.  Since the number of minutes that can be put into 32 bits
		 * is much longer than a week, we are good.
		 */
		PendingIntent pendingSurvey = PendingIntent.getService(
				this, (int) (time / 60000l), surveyIntent, 0);
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
		Util.v(null, TAG, "Time difference: "
				+ ((nextRun - runningTime) / 1000) + "s");
		Util.d(this, TAG, "Number of surveys found: " + surveys.getCount());
		while (!surveys.isAfterLast())
		{
			int id = surveys.getInt(surveys.getColumnIndexOrThrow(
					SurveyDroidDB.SurveyTable._ID));
			Util.d(null, TAG, "Doing survey " + id);
			for (int i = 0; i < days.length; i++)
			{
				Util.v(null, TAG, "Doing " + days[i]);
				String timeString = surveys.getString(
						surveys.getColumnIndexOrThrow(
								SurveyDroidDB.SurveyTable.DAYS[i]));
				Util.v(null, TAG, "Time string: " + timeString);
				String[] times = timeString.split(",");
				for (String time : times)
				{
					if (time == "") continue;  //"".split(",") returns { "" }
					long scheduledTime;
					boolean random = false;
					try
					{
						scheduledTime = Util.getUnixTime(days[i], time);
					}
					catch (IllegalArgumentException e1)
					{
						//could be a random survey, so check that
						try
						{
							
							String[] both = time.split("-");
							long start = Util.getUnixTime(days[i], both[0]);
							long end = Util.getUnixTime(days[i],
									both[1], start);

							Date d = new Date(start);
							Util.v(null, TAG, "start "
									+ d.toGMTString());
							d = new Date(end);
							Util.v(null, TAG, "end "
									+ d.toGMTString());
							/*
							 * We want to ensure that, in case the scheduler
							 * runs multiple times between when a random
							 * survey is first scheduled and when it is
							 * started, that we schedule it for the same
							 * time in both cases.
							 */
							long diff = end - start;
							byte[] bytes = Config.getSetting(
									this, Config.SALT, "").getBytes();
							long seed = start;
							seed ^= end;
							seed ^= (long) id;
							for (int j = 0; j < bytes.length; j++)
							{
								seed ^= ((long) bytes[j]) << j; //meh why not
							}
							Random r = new Random(seed);
							//always safe because end - start < max int size
							long offset =
								(long) (r.nextDouble() * diff);
							scheduledTime = start + offset;
							random = true;
						}
						catch (Exception e2)
						{
							Util.e(null, TAG, "Invalid survey time: \""
									+ time + "\"; skipping");
							continue;
						}
					}

					if (Config.D)
					{
						Date d = new Date(scheduledTime);
						Util.v(null, TAG, "Survey would be scheduled for "
								+ d.toGMTString());
						if (!random)
						{
							Util.v(null, TAG, "should be scheduled for "
									+ days[i] + " at " + time);
						}
						else
						{
							Util.v(null, TAG, "should be scheduled for "
									+ days[i] + " between "
									+ time.split("-")[0] + " and "
									+ time.split("-")[1]);
						}
					}
					addSurvey(id, scheduledTime, random);
				}
			}
			surveys.moveToNext();
		}
		surveys.close();
		sdbh.close();
		
		//make sure to run this again later
		if (runningTime == -1) return;
		Util.d(null, TAG, "rescheduling survey scheduler run");
		Intent schedulerIntent = new Intent(this, SurveyScheduler.class);
		schedulerIntent.setAction(ACTION_SCHEDULE_SURVEYS);
		schedulerIntent.putExtra(EXTRA_RUNNING_TIME, nextRun);
		PendingIntent pendingScheduler = PendingIntent.getService(
				this, 0, schedulerIntent, 0);
		AlarmManager alarm =
			(AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC_WAKEUP, nextRun, pendingScheduler);
	}
}
