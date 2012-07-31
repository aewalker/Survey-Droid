/*---------------------------------------------------------------------------*
 * SurveyService.java                                                        *
 *                                                                           *
 * Runs while the user it taking a survey; holds the survey object.  This    *
 * allows the user to rotate the screen without having to rebuild the whole  *
 * survey.                                                                   *
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

import java.util.concurrent.PriorityBlockingQueue;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;

import org.survey_droid.survey_droid.Config;
import org.survey_droid.survey_droid.Dispatcher;
import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.annotation.ConfigKey;
import org.survey_droid.survey_droid.coms.ComsService;
import org.survey_droid.survey_droid.content.ProviderContract.SurveysTakenTable.SurveyCompletionCode;
import org.survey_droid.survey_droid.content.TakenDBHandler;
import org.survey_droid.survey_droid.R;
import org.survey_droid.survey_droid.survey.SurveyInterface;
import org.survey_droid.survey_droid.ui.UserSurveysActivity;

import com.commonsware.cwac.wakeful.WakefulIntentService;

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
	 * include a survey id in {@link #EXTRA_SURVEY_ID}.
	 */
	public static final String ACTION_SURVEY_READY =
		"org.survey_droid.survey_droid.survey.ACTION_SURVEY_READY";
	
	/**
	 * A survey has been approved, show it now
	 */
	public static final String ACTION_SHOW_SURVEY =
		"org.survey_droid.survey_droid.survey.ACTION_SHOW_SURVEY";
	
	/**
	 * Stop the survey service.  Used when the user has finished a survey.
	 */
	public static final String ACTION_END_SURVEY =
		"org.survey_droid.survey_droid.survey.ACTION_END_SURVEY";
	
	/**
	 * Sent when the user leaves a survey before it's finished.  Used to mark
	 * a survey uncompleted.
	 */
	public static final String ACTION_QUIT_SURVEY =
		"org.survey_droid.survey_droid.survey.ACTION_QUIT_SURVEY";
	
	//action to use in the intent sent when the user clicks the
	//clear all button
	private static final String ACTION_CANCEL_SURVEY =
		"org.survey_droid.survey_droid.survey.ACTION_CANCEL_SURVEY";
	
	/** Tells the service to run refresh */
	private static final String ACTION_REFRESH =
		"org.survey_droid.survey_droid.survey.ACTION_REFRESH";
	
	/** Tells the service to run remove */
	private static final String ACTION_REMOVE_SURVEYS =
		"org.survey_droid.survey_droid.survey.ACTION_REMOVE_SURVEYS";
	
	//key values for extras
	/** The id of the survey this service is starting for. */
	public static final String EXTRA_SURVEY_ID =
		"org.survey_droid.survey_droid.survey.EXTRA_SURVEY_ID";
	
	/** The id of the study this service is starting for. */
	public static final String EXTRA_STUDY_ID =
		"org.survey_droid.survey_droid.survey.EXTRA_STUDY_ID";
	
	/**
	 * What kind of survey to start; sent with {@link #ACTION_SURVEY_READY}.
	 * Uses {@link #SURVEY_TYPE_TIMED} by default if this extra is not present.
	 */
	public static final String EXTRA_SURVEY_TYPE =
		"org.survey_droid.survey_droid.survey.EXTRA_SURVEY_TYPE";
	
	/**
	 * How long (in milis) should the survey be active for?  Use
	 * {@link #SURVEY_TIMEOUT_NEVER} for a survey that should not time out.
	 */
	public static final String EXTRA_SURVEY_TIMEOUT =
		"org.survey_droid.survey_droid.survey.EXTRA_SURVEY_TIMEOUT";
	
	//config keys, etc
	/**
	 * How long (in minutes) to wait between re-notifying the user that a
	 * survey or surveys is awaiting.
	 */
	@ConfigKey("" + 10)
	private static final String REFRESH_INTERVAL = "refresh_interval";
	
	/**
	 * How long should the system wait for the user to take a survey before it
	 * considers that survey ignored and removes it.  It works best if this is
	 * a multiple of {@link #REFRESH_INTERVAL}.
	 */
	@ConfigKey("" + 60)
	private static final String SURVEY_TIMEOUT = "survey_timeout";
	
	/**
	 * This value, if used for {@link #SURVEY_TIMEOUT} will result in
	 * surveys being kept until the phone is turned off.  <strong>Use of this
	 * is not recommended if tracking what percentage of surveys a user answers
	 * is important.</strong>
	 */
	private static final int SURVEY_TIMEOUT_NEVER = -1;
	
	/**
	 * If true, then the full name of a survey should be shown to the user.  If
	 * false, then just show "Survey Droid Survey".
	 */
	@ConfigKey("true")
	private static final String SHOW_SURVEY_NAME = "survey_service.show_survet_name";
	
	
	/** survey types */
	//this is backwards compatible
	public enum SurveyType
	{
		/** Used with {@link #EXTRA_SURVEY_TYPE} for time-based surveys */
		SURVEY_TYPE_TIMED,
		/**
		 * Used with {@link #EXTRA_SURVEY_TYPE} for randomized time-based
		 * surveys
		 */
		SURVEY_TYPE_RANDOM,
		/** Used with {@link #EXTRA_SURVEY_TYPE} for user-initiated surveys */
		SURVEY_TYPE_USER_INIT,
		/** Used with {@link #EXTRA_SURVEY_TYPE} for call-initiated surveys */
		SURVEY_TYPE_CONTACT_INIT,
		/**
		 * Used with {@link #EXTRA_SURVEY_TYPE} for location proximity
		 * initiated surveys.
		 */
		SURVEY_TYPE_LOCATION_INIT;
	}
	
	/*-----------------------------------------------------------------------*/
	
	/** the survey instance that each instance of this service uses */
	private Survey survey;
	
	/** set to true in order to tell the refresh function to build the survey */
	private boolean build = false;
	
	/** is a survey currently running? */
	private boolean inSurvey = false;
	
	/** the current survey's information */
	private SurveyInfo currentInfo;
	
	/** logging tag */
	private static final String TAG = "SurveyService";
	
	/** Surveys that are ready but not running */
	private final PriorityBlockingQueue<SurveyInfo> surveys =
		new PriorityBlockingQueue<SurveyInfo>();
	
	/** The notification id to use */
	private static final int N_ID = 0;
	
	private Intent runRefresh;
	private Intent runRemove;
	private Intent runTimeout;
	
	private WakeLock wl;
	
	/**
	 * Data class that holds info about a survey instance.
	 */
	private class SurveyInfo implements Comparable<SurveyInfo>
	{
		long id;            //survey id
		long study_id;		//study id
		SurveyType type;    //survey type
		long startTime;     //when the survey was scheduled for
		long endTime;		//when does this survey timeout
		
		@Override
		public int compareTo(SurveyInfo that)
		{
			if (this.endTime == SURVEY_TIMEOUT_NEVER)
			{
				if (that.endTime == SURVEY_TIMEOUT_NEVER)
				{
					return (int) (this.startTime - that.startTime);
				}
				return 1;
			}
			return (int) (this.endTime - that.endTime);
		}
		
		@Override
		public boolean equals(Object that)
		{
			SurveyInfo s;
			try
			{
				s = (SurveyInfo) that;
				if (s.id == id && s.type == type &&
						s.startTime == startTime && s.endTime == endTime)
					return true;
			}
			catch (Exception e) {}
			return false;
		}
		
		@Override
		public String toString()
		{
			return "SurveyInfo: id = " + study_id + ":" + id + ", type = " + type +
			", startTime = " + startTime + ", endTime = " + endTime;
		}
	}
	
	/*
	 * This is only in here due to a bug in the testing framework.
	 * ServiceTestCase doesn't call onStartCommand, it calls onStart.  Thus,
	 * we have to put this in here in order to get things working properly.
	 * 
	 * see http://code.google.com/p/android/issues/detail?id=12117
	 */
	@Override
	public void onStart(Intent intent, int startid)
	{
		Util.w(null, TAG, "deprecated onStart called");
		onStartCommand(intent, 0, startid);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid)
	{
		Util.d(null, TAG, "onStartCommand");
		if (wl == null)
		{
			PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		}
		if (!wl.isHeld()) wl.acquire();
		if (runRefresh == null)
		{
			runRefresh = new Intent(this, SurveyService.class);
			runRefresh.setAction(ACTION_REFRESH);
			runRefresh.setData(Uri.parse(TAG + " runRefresh"));
		}
		if (runRemove == null)
		{
			runRemove = new Intent(this, SurveyService.class);
			runRemove.setAction(ACTION_REMOVE_SURVEYS);
			runRemove.setData(Uri.parse(TAG + " runRemove"));
		}
		if (runTimeout == null)
		{
			runTimeout = new Intent(this, SurveyService.class);
			runTimeout.setAction(ACTION_QUIT_SURVEY);
			runTimeout.setData(Uri.parse(TAG + " runTimeout"));
		}
		handleIntent(intent);
		//TODO because this service is so complex, just let it die if it
		//gets killed.  In the future, it would be better to deal with it.
		if (wl.isHeld()) wl.release();
		return START_NOT_STICKY;
	}
	
	/**
	 * Handle the incoming intents one by one; this is basically a cheap way of
	 * turning this into an {@link IntentService}, but keeping the ability to
	 * use binding.
	 * 
	 * @param intent the received intent
	 */
	private synchronized void handleIntent(Intent intent)
	{
		String action = intent.getAction();
		Util.d(null, TAG, "Recieved action: " + action);
		if (action.equals(ACTION_SURVEY_READY))
		{
			addSurvey(intent);
		}
		else if (action.equals(ACTION_SHOW_SURVEY))
		{
			startSurvey();
		}
		else if (action.equals(ACTION_END_SURVEY))
		{
			endSurvey();
		}
		else if (action.equals(ACTION_QUIT_SURVEY))
		{
			quitSurvey();
		}
		else if (action.equals(ACTION_CANCEL_SURVEY))
		{
			cancel();
		}
		else if (action.equals(ACTION_REFRESH))
		{
			refresh();
		}
		else if (action.equals(ACTION_REMOVE_SURVEYS))
		{
			removeSurveys(false);
		}
		else
		{
			Util.w(null, TAG, "Unknown intent action: " + action);
		}
	}
	
	/**
	 * Adds a new survey to the list (or not if surveys are off)
	 * 
	 * @param intent the intent holding the survey info
	 */
	private void addSurvey(Intent intent)
	{
		Util.d(null, TAG, "adding survey");
		SurveyInfo sInfo = new SurveyInfo();
		sInfo.id = intent.getLongExtra(EXTRA_SURVEY_ID, Survey.DUMMY_SURVEY_ID);
		sInfo.study_id = intent.getLongExtra(EXTRA_STUDY_ID, 0);
		sInfo.type = SurveyType.values()[intent.getIntExtra(EXTRA_SURVEY_TYPE,
				SurveyType.SURVEY_TYPE_TIMED.ordinal())];
		sInfo.startTime = System.currentTimeMillis();
		if ((!Config.getBoolean(this, Config.SURVEYS_LOCAL)
				|| !Config.getBoolean(this, Config.SURVEYS_STUDY,
						true, sInfo.study_id))
				&& sInfo.type != SurveyType.SURVEY_TYPE_USER_INIT)
		{
			SurveyCompletionCode status;
			switch (sInfo.type)
			{
			case SURVEY_TYPE_TIMED:
				status = SurveyCompletionCode.SCHEDULED_IGNORED;
				break;
			case SURVEY_TYPE_RANDOM:
				status = SurveyCompletionCode.RANDOM_IGNORED;
				break;
			case SURVEY_TYPE_CONTACT_INIT:
				status =
						SurveyCompletionCode.CALL_INITIATED_IGNORED;
				break;
			case SURVEY_TYPE_LOCATION_INIT:
				status =
						SurveyCompletionCode.LOCATION_BASED_IGNORED;
				break;
			default:
				Util.w(this, TAG, "Invalid survey type: " + sInfo.type);
				if (currentInfo == null)
				{
					stopSelf();
				}
				return;
			}
			TakenDBHandler tdbh = new TakenDBHandler(this);
			if (tdbh.writeSurvey(sInfo.id, status,
					Util.currentTimeAdjusted() / 1000) == false)
			{
				Util.e(null, TAG,
						"Failed to write completion record!");
			}
			uploadNow();
			if (currentInfo == null)
			{
				stopSelf();
			}
			return;
		}
		long timeout = Config.getLong(this,
				SURVEY_TIMEOUT, 60, sInfo.study_id);
		if (timeout == SURVEY_TIMEOUT_NEVER)
		{
			sInfo.endTime = SURVEY_TIMEOUT_NEVER;
		}
		else
		{
			//TODO This isn't working now.  In the future, if we want to add
			//the ability to set timeouts on a per-survey basis, this can be
			//fixed.
//			sInfo.endTime = sInfo.startTime + intent.getLongExtra(
//					EXTRA_SURVEY_TIMEOUT, timeout * 60 * 1000);
			sInfo.endTime = sInfo.startTime + timeout * 60 * 1000;
			/////
		}
		Util.v(null, TAG, "current time is " + System.currentTimeMillis());
		Util.v(null, TAG, "new survey: " + sInfo);
		if (inSurvey)
		{
			surveys.add(sInfo);
			return;
		}
		if (currentInfo == null)
		{
			currentInfo = sInfo;
			build = true;
			refresh();
//			Dispatcher.dispatch(this, runRefresh,
//				0, Dispatcher.TYPE_WAKEFUL_MANUAL, null);
		}
		else
		{
			surveys.add(currentInfo);
			surveys.add(sInfo);
			sInfo = surveys.poll();
			if (!sInfo.equals(currentInfo))
			{
				currentInfo = sInfo;
				build = true;
				refresh();
//				Dispatcher.dispatch(this, runRefresh,
//					0, Dispatcher.TYPE_WAKEFUL_MANUAL, null);
			}
		}
	}
	
	/**
	 * Builds the survey from the current info
	 * 
	 * @return true if it worked
	 */
	private boolean buildSurvey()
	{
		if (currentInfo == null)
		{
			throw new RuntimeException("attempted to start null survey");
		}
		Dispatcher.cancel(this, runRefresh, null);
		Dispatcher.cancel(this, runRemove, null);
		try
		{
			if (currentInfo.id == Survey.DUMMY_SURVEY_ID)
			{
				survey = new Survey(this);
			}
			else
			{
				survey = new Survey(currentInfo.id, this);
			}
		}
		catch (SurveyConstructionException e)
		{
			final String message = e.toString();
			Util.e(null, TAG, "Survey construction error:");
			Util.e(null, TAG, message);
			
			//FIXME this is such a hack
			//we need to get information about the crash somehow, so do this
			if (Config.D)
			{
				//this is supposed to crash immediately
				final SurveyConstructionException e2 = e;
				new Thread(new Runnable()
				{
					public void run()
					{
						throw new RuntimeException(message, e2);
					}
				}).run();
			}
			
			Util.e(this, TAG, "Error starting survey; please" +
					" tell the study administrator.");
			return false;
		}
		return true;
	}
	
	/**
	 * Starts the current survey.
	 */
	private void startSurvey()
	{
		if (currentInfo == null)
			throw new RuntimeException("Tried to run startSurvey() with null survey");
		Util.d(null, TAG, "starting survey");
		Dispatcher.cancel(this, runRefresh, null);
		Dispatcher.cancel(this, runRemove, null);
		if (!inSurvey)
		{
			inSurvey = true;
			
			//update the notification
			int icon = R.drawable.survey_small;
			String tickerText;
			tickerText = getString(R.string.in_survey_ticker);
			long when = System.currentTimeMillis();
			String contentTitle = getString(R.string.app_name);
			String contentText;
			contentText = getString(R.string.in_survey_content);
			
			//now create the notification
			Intent notificationIntent = new Intent(this, SurveyService.class);
			notificationIntent.setAction(ACTION_SHOW_SURVEY);
			PendingIntent contentIntent =
				PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			Notification notification =
				new Notification(icon, tickerText, when);
			notification.setLatestEventInfo(
					this, contentTitle, contentText, contentIntent);
			notification.flags |= Notification.FLAG_NO_CLEAR;
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			
			//send it
			NotificationManager nm = (NotificationManager)
				getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(N_ID, notification);
		}
		
		Intent surveyIntent = new Intent();
		surveyIntent.setComponent(survey.getQuestionType());
		surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(surveyIntent);
	}
	
	/**
	 * Refresh the notification for the current survey
	 */
	private void refresh()
	{
		if (inSurvey)
		{
			if (Config.D)
				throw new RuntimeException("Ran refresh() while in a survey");
			else
				Util.w(null, TAG, "Ran refresh() while in a survey");
			return;
		}
		if (currentInfo == null)
		{
			if (Config.D)
				throw new RuntimeException("Tried to run refresh() with null survey");
			else
				Util.w(null, TAG, "Tried to run refresh() with null survey");
			return;
		}
		Util.d(null, TAG, "refresh");
		
		if (build)
		{
			build = false;
			while (!buildSurvey())
			{
				currentInfo = surveys.poll();
				if (currentInfo == null)
				{
					stopSelf();
					return;
				}
			}
		}
		
		//things we're going to need for the notification
		int icon = R.drawable.survey_small;
		String tickerText;
		tickerText = getString(R.string.survey_waiting_ticker);
		long when = System.currentTimeMillis();
		String contentTitle = getString(R.string.app_name);
		String contentText;
		contentText = getString(R.string.survey_waiting_content);
		
		//now create the notification
		Intent notificationIntent = new Intent(this, SurveyService.class);
		notificationIntent.setAction(ACTION_SHOW_SURVEY);
		PendingIntent contentIntent =
			PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		Notification notification =
			new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(
				this, contentTitle, contentText, contentIntent);
		
		//create the deleteIntent
		Intent deleteIntent = new Intent(this, SurveyService.class);
		deleteIntent.setAction(ACTION_CANCEL_SURVEY);
		PendingIntent pendingDelIntent =
			PendingIntent.getService(this, 1, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		notification.deleteIntent = pendingDelIntent;
		
		//add sound and vibration
		//the system policy will determine if either of these will actually
		//happen, so don't need to worry about it
		//FIXME
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		
		//send it - make sure the screen turns on properly
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		WakeLock wakeup = pm.newWakeLock(
			  PowerManager.SCREEN_DIM_WAKE_LOCK
			| PowerManager.ACQUIRE_CAUSES_WAKEUP
			| PowerManager.ON_AFTER_RELEASE, TAG);
		wakeup.acquire();
		NotificationManager nm = (NotificationManager)
			getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(N_ID, notification);
		wakeup.release();
		
		//now reschedule the refresh if needed
		long refreshInterval = Config.getLong(this, REFRESH_INTERVAL,
				60, currentInfo.study_id) * 60 * 1000;
		Util.v(null, TAG, "time left on current survey: " + (currentInfo.endTime - when));
		if (currentInfo.endTime > when + refreshInterval ||
				currentInfo.endTime == SURVEY_TIMEOUT_NEVER)
		{
			Util.v(null, TAG, "go for refresh again");
			Dispatcher.dispatch(this, runRefresh,
				System.currentTimeMillis() + refreshInterval,
				Dispatcher.TYPE_WAKEFUL_MANUAL, null);
		}
		else
		{
			Util.v(null, TAG, "go for remove");
			Dispatcher.dispatch(this, runRemove, currentInfo.endTime,
				Dispatcher.TYPE_WAKEFUL_MANUAL, null);
		}
	}
	
	/**
	 * Marks the current survey as dismissed. 
	 */
	private void cancel()
	{
		if (inSurvey)
			throw new RuntimeException("Tried to run cancel() while in survey");
		if (currentInfo == null)
			throw new RuntimeException("Tried to run cancel() with null survey");
		Util.i(null, TAG, "Canceling survey");
		TakenDBHandler tdbh = new TakenDBHandler(this);
		long id = currentInfo.id;
		SurveyType type = currentInfo.type;
		if (id != Survey.DUMMY_SURVEY_ID && type != SurveyType.SURVEY_TYPE_USER_INIT)
		{
			SurveyCompletionCode status;
			switch (type)
			{
			case SURVEY_TYPE_TIMED:
				status = SurveyCompletionCode.SCHEDULED_DISMISSED;
				break;
			case SURVEY_TYPE_RANDOM:
				status = SurveyCompletionCode.RANDOM_DISMISSED;
				break;
			case SURVEY_TYPE_CONTACT_INIT:
				status = SurveyCompletionCode.CALL_INITIATED_DISMISSED;
				break;
			case SURVEY_TYPE_LOCATION_INIT:
				status = SurveyCompletionCode.LOCATION_BASED_DISMISSED;
				break;
			default:
				//wtf how?
				throw new RuntimeException("Invalid survey code?!");
			}
			if (tdbh.writeSurvey(id, status,
						Util.currentTimeAdjusted() / 1000) == false)
			{
				Util.e(null, TAG, "Failed to write completion record!");
			}
		}
		
		if (!surveys.isEmpty())
		{
			Util.v(null, TAG, surveys.size() + " more surveys left; go for refresh");
			currentInfo = surveys.poll();
			build = true;
			refresh();
//			Dispatcher.dispatch(this, runRefresh,
//				0, Dispatcher.TYPE_WAKEFUL_MANUAL, null);
		}
		else
		{
			Util.v(null, TAG, "no more surveys; removing notification");
			currentInfo = null;
			removeNotification();
		}
	}
	
	/**
	 * Removes any notification and stops the service.
	 */
	private void removeNotification()
	{
		Util.d(null, TAG, "Removing notification and stopping");
		
		if (currentInfo != null || surveys.size() != 0)
			throw new RuntimeException("Attempted to stop survey "
					+ "service whith surveys left to run");
		
		NotificationManager nm = (NotificationManager)
			getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(N_ID);
		Dispatcher.cancel(this, runRefresh, null);
		Dispatcher.cancel(this, runRemove, null);
		Dispatcher.cancel(this, runTimeout, null);
		survey = null; //probably not needed, but just to make sure
		stopSelf();
	}
	
	//submit answers for the current survey
	private void submit()
	{
		Util.v(null, TAG, "submitting answers");
		if (!survey.submit())
			Util.e(null, TAG, "Survey reports error in submission!");
		
		//schedule surveys again (just to be safe; it can't hurt)
		Intent scheduleIntent = new Intent(getApplicationContext(), SurveyScheduler.class);
		scheduleIntent.setAction(SurveyScheduler.ACTION_SCHEDULE_SURVEYS);
		WakefulIntentService.sendWakefulWork(this, scheduleIntent);
	}
	
	/**
	 * Called when a user has finished a survey normally
	 */
	private void endSurvey()
	{
		if (!inSurvey)
			throw new RuntimeException("Cannot end a survey before starting one");
		if (currentInfo == null)
			throw new RuntimeException("Tried to run endSurvey() with null survey");
		Util.v(null, TAG, "ending survey");
		submit();
		inSurvey = false;
		if (currentInfo.id != Survey.DUMMY_SURVEY_ID)
		{
			TakenDBHandler tdbh = new TakenDBHandler(this);
			SurveyCompletionCode status;
			switch (currentInfo.type)
			{
			case SURVEY_TYPE_TIMED:
				status = SurveyCompletionCode.SCHEDULED_FINISHED;
				break;
			case SURVEY_TYPE_RANDOM:
				status = SurveyCompletionCode.RANDOM_FINISHED;
				break;
			case SURVEY_TYPE_USER_INIT:
				status = SurveyCompletionCode.USER_INITIATED_FINISHED;
				break;
			case SURVEY_TYPE_CONTACT_INIT:
				status = SurveyCompletionCode.CALL_INITIATED_FINISHED;
				break;
			case SURVEY_TYPE_LOCATION_INIT:
				status = SurveyCompletionCode.LOCATION_BASED_FINISHED;
				break;
			default:
				//wtf how?
				throw new RuntimeException("Invalid survey code?!");
			}
			if (tdbh.writeSurvey(currentInfo.id, status,
						Util.currentTimeAdjusted() / 1000) == false)
			{
				Util.e(null, TAG, "Failed to write completion record!");
			}
			
			//try to upload answers ASAP
			uploadNow();
		}
		else
		{
			Config.putSetting(this, UserSurveysActivity.SAMPLE_SURVEY_TAKEN, true);
		}
		if (surveys.isEmpty())
		{
			Util.v(null, TAG, "no more surveys, removing notification");
			currentInfo = null;
			removeNotification();
		}
		else
		{
			Util.v(null, TAG, surveys.size() + " more surveys left; go for refresh");
			currentInfo = surveys.poll();
			build = true;
			refresh();
//			Dispatcher.dispatch(this, runRefresh,
//				0, Dispatcher.TYPE_WAKEFUL_MANUAL, null);
		}
	}
	
	/**
	 * Called when a user has exited a survey before it was finished
	 */
	private void quitSurvey()
	{
		if (!inSurvey)
			throw new RuntimeException("Cannot quit a survey before starting one");
		if (currentInfo == null)
			throw new RuntimeException("Tried to run quitSurvey() with null survey");
		Util.d(null, TAG, "quiting survey");
		submit();
		inSurvey = false;
		if (currentInfo.id != Survey.DUMMY_SURVEY_ID)
		{
			TakenDBHandler tdbh = new TakenDBHandler(this);
			SurveyCompletionCode status;
			switch (currentInfo.type)
			{
			case SURVEY_TYPE_TIMED:
				status = SurveyCompletionCode.SCHEDULED_UNFINISHED;
				break;
			case SURVEY_TYPE_RANDOM:
				status = SurveyCompletionCode.RANDOM_UNFINISHED;
				break;
			case SURVEY_TYPE_USER_INIT:
				status = SurveyCompletionCode.USER_INITIATED_UNFINISHED;
				break;
			case SURVEY_TYPE_CONTACT_INIT:
				status = SurveyCompletionCode.CALL_INITIATED_UNFINISHED;
				break;
			case SURVEY_TYPE_LOCATION_INIT:
				status = SurveyCompletionCode.LOCATION_BASED_UNFINISHED;
				break;
			default:
				//wtf how?
				throw new RuntimeException("Invalid survey code?!");
			}
			if (tdbh.writeSurvey(currentInfo.id, status,
					Util.currentTimeAdjusted() / 1000) == false)
			{
				Util.e(null, TAG, "Failed to write completion record!");
			}
			uploadNow();
		}
		if (!surveys.isEmpty())
		{
			Util.v(null, TAG, surveys.size() + "more surveys left; go for refresh");
			currentInfo = surveys.poll();
			build = true;
			refresh();
//			Dispatcher.dispatch(this, runRefresh,
//				0, Dispatcher.TYPE_WAKEFUL_MANUAL, null);
		}
		else
		{
			Util.v(null, TAG, "no more surveys, removing notification");
			currentInfo = null;
			removeNotification();
		}
	}
	
	/**
	 * Shows the user a notification that they have missed a survey
	 */
	private void showMissedSurveyNotification()
	{
		//things we're going to need for the notification
		int icon = R.drawable.missed_survey;
		String tickerText = getString(R.string.app_name);
		long when = System.currentTimeMillis();
		String contentTitle = getString(R.string.survey_missed_ticker);
		String contentText = getString(R.string.survey_missed_content);
		
		//now create the notification
		Intent notificationIntent = new Intent();
		PendingIntent contentIntent =
			PendingIntent.getService(this, 0, notificationIntent, 0);
		Notification notification =
			new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(
				this, contentTitle, contentText, contentIntent);
		
		//add sound and vibration
		//the system policy will determine if either of these will actually
		//happen, so don't need to worry about it
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		//auto cancel
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		//don't use the power manager here; we don't need to alert the user
		//immediately since they are already not paying attention
		NotificationManager nm = (NotificationManager)
			getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(N_ID + 1, notification);
	}
	
	/**
	 * Remove surveys that have expired
	 * 
	 * @param all if true, removes all surveys (even if they are not expired)
	 */
	private void removeSurveys(boolean all)
	{
		Util.d(null, TAG, "Removing " + (all ? "all" : "expired") + " surveys");
		Util.v(null, TAG, "current time: " + System.currentTimeMillis());
		if (currentInfo != null)
		{
			surveys.add(currentInfo);
			currentInfo = null;
		}
		TakenDBHandler tdbh = new TakenDBHandler(this);
		while (true)
		{
			SurveyInfo sInfo = surveys.poll();
			if (sInfo == null) break;
			Util.v(null, TAG, "Current survey: " + sInfo.id + " at " + sInfo.startTime);
			if ((sInfo.endTime != SURVEY_TIMEOUT_NEVER &&
					sInfo.endTime <= System.currentTimeMillis()) || all)
			{
				Util.v(null, TAG, "removing survey");
				if (sInfo.id != Survey.DUMMY_SURVEY_ID &&
						sInfo.type != SurveyType.SURVEY_TYPE_USER_INIT)
				{
					SurveyCompletionCode status;
					switch (sInfo.type)
					{
					case SURVEY_TYPE_TIMED:
						status = SurveyCompletionCode.SCHEDULED_IGNORED;
						break;
					case SURVEY_TYPE_RANDOM:
						status = SurveyCompletionCode.RANDOM_IGNORED;
						break;
					case SURVEY_TYPE_CONTACT_INIT:
						status = SurveyCompletionCode.CALL_INITIATED_IGNORED;
						break;
					case SURVEY_TYPE_LOCATION_INIT:
						status = SurveyCompletionCode.LOCATION_BASED_IGNORED;
						break;
					default:
						//wtf how?
						throw new RuntimeException("Invalid survey code?!");
					}
					if (tdbh.writeSurvey(sInfo.id, status,
							Util.currentTimeAdjusted() / 1000) == false)
					{
						Util.e(null, TAG, "Failed to write completion record!");
					}
					showMissedSurveyNotification();
				}
			}
			else
			{
				//survey is not being removed
				//since the surveys queue is sorted, we can stop now
				surveys.add(sInfo);
				break;
			}
		}
		uploadNow();
		if (!surveys.isEmpty())
		{ 
			Util.v(null, TAG, surveys.size() + " more surveys left; go for refresh");
			currentInfo = surveys.poll();
			build = true;
			refresh();
//			Dispatcher.dispatch(this, runRefresh,
//				0, Dispatcher.TYPE_WAKEFUL_MANUAL, null);
		}
		else
		{
			Util.v(null, TAG, "no more surveys, removing notification");
			removeNotification();
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		//make sure to mark the remaining surveys as unfinished or ignored
		//in the case that this service is killed (likely because the phone
		//is being shut down)
		if (inSurvey) quitSurvey();
		removeSurveys(true);
		if (wl.isHeld()) wl.release();
	}

	/**
	 * Implements the {@link SurveyInterface} for client Question Activities.
	 */
	private final SurveyInterface.Stub surveyBinder = new SurveyInterface.Stub()
	{
		@Override
		public void startTimeout() throws RemoteException
		{
			Util.d(null, TAG, "starting question timeout");
			long delay = Config.getLong(SurveyService.this,
					Config.QUESTION_TIMEOUT, 0, currentInfo.study_id) * 60 * 1000;
			Dispatcher.dispatch(SurveyService.this, runTimeout,
				System.currentTimeMillis() + delay,
				Dispatcher.TYPE_WAKEFUL_MANUAL, null);
		}

		@Override
		public void stopTimeout() throws RemoteException
		{
			if (runTimeout == null)
			{
				Util.w(null, TAG, "runTimeout was null; service must have been restarted...");
				runTimeout = new Intent(SurveyService.this, SurveyService.class);
				runTimeout.setAction(ACTION_QUIT_SURVEY);
				runTimeout.setData(Uri.parse(TAG + " runTimeout"));
			}
			Dispatcher.cancel(SurveyService.this, runTimeout, null);
		}

		@Override
		public boolean nextQuestion() throws RemoteException
		{
			try
			{
				survey.nextQuestion();
			}
			catch (Exception e)
			{
				return false;
			}
    		if (!survey.done())
    		{ //still have more questions
    			Intent nextIntent = new Intent();
    			nextIntent.setComponent(survey.getQuestionType());
    			//TODO see FLAG_ACTIVITY_NO_HISTORY
    			nextIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    			startActivity(nextIntent);
    		}
    		else
    		{ //survey is over
    			Intent submitIntent = new Intent(SurveyService.this,
    					ConfirmSubmitActivity.class);
    			submitIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    			startActivity(submitIntent);
    		}
			return true;
		}

		@Override
		public boolean prevQuestion() throws RemoteException
		{
			if (survey.isOnFirst()) return false;
			survey.prevQuestion();
    		Intent prevIntent = new Intent();
    		prevIntent.setComponent(survey.getQuestionType());
    		startActivity(prevIntent);
			return true;
		}

		@Override
		public boolean answer(byte[] data) throws RemoteException
		{
			survey.answer(data);
			return true;
		}

		@Override
		public byte[] getQuestionData() throws RemoteException
		{
			return survey.getQuestion().data;
		}

		@Override
		public String getSurveyName() throws RemoteException
		{
			if (Config.getBoolean(SurveyService.this,
					SHOW_SURVEY_NAME, true, currentInfo.study_id))
			{
				return survey.getName();
			}
			return "Survey Droid Survey";
		}

		@Override
		public byte[] getAnswer() throws RemoteException
		{
			return survey.getAnswerValue();
		}

		@Override
		public String processText(String text) throws RemoteException
		{
			return survey.processText(text);
		}
	};
	
	@Override
	public IBinder onBind(Intent intent)
	{
		Util.d(null, TAG, "in onBind");
		return surveyBinder;
	}
	
	/**
	 * Provides a quick way to upload survey data.
	 */
	private void uploadNow()
	{
		Intent comsIntent = new Intent(this, ComsService.class);
		comsIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
		comsIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
				ComsService.SURVEY_DATA);
		WakefulIntentService.sendWakefulWork(this, comsIntent);
	}
}
