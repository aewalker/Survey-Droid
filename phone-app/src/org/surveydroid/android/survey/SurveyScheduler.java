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

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import org.surveydroid.android.Config;
import org.surveydroid.android.Dispatcher;
import org.surveydroid.android.Util;
import org.surveydroid.android.database.SurveyDroidDB;
import org.surveydroid.android.database.SurveyDBHandler;

import com.commonsware.cwac.wakeful.WakefulIntentService;

/**
 * Schedules surveys based on the database information about them.  Reschedules
 * itself as specified by the configuration downloaded from the website (for
 * example, for one day later).
 * 
 * @author Austin Walker
 * @author Diego Vargas
 */
public class SurveyScheduler extends WakefulIntentService
{
	/** logging tag */
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
	/**
	 * The id of the survey to be added.  Used with {@link #ACTION_ADD_SURVEY}.
	 */
	public static final String EXTRA_SURVEY_ID = SurveyService.EXTRA_SURVEY_ID;
	
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
	 * Tells the service to reschedule itself.
	 */
	public static final String EXTRA_RUN_AGAIN =
		"org.surveydroid.android.survey.EXTRA_RUN_AGAIN";
	
	public SurveyScheduler()
	{
		super(null);
	}

	@Override
	public void doWakefulWork(Intent intent)
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
			scheduleSurveys(intent.getBooleanExtra(EXTRA_RUN_AGAIN, false));
		}
		else
		{
			Util.w(null, TAG, "Unknown action requested: " + action);
			if (Config.D)
				throw new RuntimeException("Unknown action requested: "
						+ action);
		}
	}
	
	/**
	 * Schedule survey id for the given time.
	 * 
	 * @param context
	 * @param id
	 * @param time
	 * @param random set to true if the survey is a random one
	 */
	private void addSurvey(int id, long time, boolean random)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		Util.d(null, TAG, "Scheduling survey "
				+ id + " for " + c.getTime().toLocaleString());
		
		Intent surveyIntent = new Intent(getApplicationContext(), SurveyService.class);
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
		 * 
		 * 01/18/2012 - I have verified that this will cause filterEquals to
		 * return false by examining the source code. - Austin
		 */
		Uri uri = Uri.parse("survey:" + Integer.toString(id) + "@" + Long.toString(time));
		surveyIntent.setData(uri);
		surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_ID, id);
		surveyIntent.putExtra(EXTRA_RUNNING_TIME, time);
		Dispatcher.dispatch(this.getApplicationContext(),
			surveyIntent, time, Dispatcher.TYPE_WAKEFUL_MANUAL, null);
	}
	
	/**
	 * Use a hash function to determine when a random survey should be 
	 * scheduled for based on the end times.
	 * 
	 * @param id the survey id
	 * @param start the starting time
	 * @param end the ending time
	 * @return the time the survey should be scheduled for
	 */
	private long getRandomSurveyTime(int id, long start, long end)
	{
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
			(long) (r.nextDouble() * (end - start));
		return start + offset;
	}
	
	/**
	 * Look for surveys that need to be scheduled and does so.
	 * 
	 * @param context
	 * @param reschedule if true, set up an alarm to reschedule
	 */
	private void scheduleSurveys(boolean reschedule)
	{
		Util.i(null, TAG, "Scheduling surveys");
		
		SurveyDBHandler sdbh = new SurveyDBHandler(this);
		sdbh.open();
		Cursor surveys = sdbh.getSurveys();
		
		surveys.moveToFirst();
		String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
		long nextRun = System.currentTimeMillis() + (Config.getSetting(this,
				Config.SCHEDULER_INTERVAL, Config.SCHEDULER_INTERVAL_DEFAULT)
				* 60 * 1000);
		Util.d(null, TAG, "Number of surveys found: " + surveys.getCount());
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
						String first;
						String second;
						try
						{
							
							String[] both = time.split("-");
							first = both[0];
							second = both[1];

						}
						catch (Exception e2)
						{
							Util.e(null, TAG, "Invalid survey time: \""
									+ time + "\"; skipping");
							continue;
						}
						long start = Util.getUnixTime(days[i], first);
						long end = Util.getUnixTime(days[i],
							second, start);
						long diff = end - start;
						
						//don't miss surveys if the scheduler runs inside the
						//survey window
						end = Util.getUnixTime(days[i], second);
						//an (over) estimate of the maximum amount of "leap"
						//time that can occur in a single week
						long maxTimeDiff = 2 * 60 * 60 * 1000; //2 hours
						start = Util.getUnixTime(days[i], first,
							end - diff - maxTimeDiff);
						
						scheduledTime = getRandomSurveyTime(id, start, end);
						if (scheduledTime < System.currentTimeMillis())
						{
							start = Util.getUnixTime(days[i], first);
							end = Util.getUnixTime(days[i], second, start);
							scheduledTime = getRandomSurveyTime(id, start, end);
						}
						
						random = true;
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
		if (!reschedule) return;
		Util.d(null, TAG, "rescheduling survey scheduler run");
		Intent schedulerIntent = new Intent(this, SurveyScheduler.class);
		schedulerIntent.setAction(ACTION_SCHEDULE_SURVEYS);
		schedulerIntent.putExtra(EXTRA_RUNNING_TIME, nextRun);
		Uri uri = Uri.parse("survey scheduler reschedule");
		Dispatcher.dispatch(this, schedulerIntent,
			nextRun, Dispatcher.TYPE_WAKEFUL_AUTO, uri);
	}
}
