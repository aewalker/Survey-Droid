/*---------------------------------------------------------------------------*
 * SurveyService.java                                                        *
 *                                                                           *
 * Runs while the user it taking a survey; holds the survey object.  This    *
 * allows the user to rotate the screen without having to rebuild the whole  *
 * survey.                                                                   *
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

import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import org.surveydroid.android.R;
import org.surveydroid.android.Config;
import org.surveydroid.android.Util;
import org.surveydroid.android.coms.ComsService;
import org.surveydroid.android.database.SurveyDroidDB;
import org.surveydroid.android.database.TakenDBHandler;

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
		"org.surveydroid.android.survey.ACTION_SURVEY_READY";
	
	/**
	 * A survey has been approved, show it now
	 */
	public static final String ACTION_SHOW_SURVEY =
		"org.surveydroid.android.survey.ACTION_SHOW_SURVEY";
	
	/**
	 * Stop the survey service.  Used when the user has finished a survey.
	 */
	public static final String ACTION_END_SURVEY =
		"org.surveydroid.android.survey.ACTION_END_SURVEY";
	
	/**
	 * Sent when the user leaves a survey before it's finished.  Used to mark
	 * a survey uncompleted.
	 */
	public static final String ACTION_QUIT_SURVEY =
		"org.surveydroid.android.survey.ACTION_QUIT_SURVEY";
	
	//action to use in the intent sent when the user clicks the
	//clear all button
	private static final String ACTION_CANCEL_SURVEY =
		"org.surveydroid.android.survey.ACTION_CANCEL_SURVEY";
	
	//key values for extras
	/** The id of the survey this service is starting for. */
	public static final String EXTRA_SURVEY_ID =
		"org.surveydroid.android.survey.EXTRA_SURVEY_ID";
	
	/**
	 * What kind of survey to start; sent with {@link #ACTION_SURVEY_READY}.
	 * Uses {@link #SURVEY_TYPE_TIMED} by default if this extra is not present.
	 */
	public static final String EXTRA_SURVEY_TYPE =
		"org.surveydroid.android.survey.EXTRA_SURVEY_TYPE";
	
	/**
	 * How long (in milis) should the survey be active for?  Use
	 * {@link #SURVEY_TIMEOUT_NEVER} for a survey that should not time out.
	 */
	public static final String EXTRA_SURVEY_TIMEOUT =
		"org.surveydroid.android.survey.EXTRA_SURVEY_TIMEOUT";
	
	//survey types
	/** Used with {@link #EXTRA_SURVEY_TYPE} for time-based surveys */
	public static final int SURVEY_TYPE_TIMED = 0;
	/**
	 * Used with {@link #EXTRA_SURVEY_TYPE} for randomized time-based surveys
	 */
	public static final int SURVEY_TYPE_RANDOM = 1;
	
	/** Used with {@link #EXTRA_SURVEY_TYPE} for user-initiated surveys */
	public static final int SURVEY_TYPE_USER_INIT = 2;
	
	/** Used with {@link #EXTRA_SURVEY_TYPE} for call-initiated surveys */
	public static final int SURVEY_TYPE_CALL_INIT = 3;
	
	/**
	 * Used with {@link #EXTRA_SURVEY_TYPE} for location proximity initiated
	 * surveys.
	 */
	public static final int SURVEY_TYPE_LOC_INIT = 4;
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Given this id, a dummy survey will be used and the answers will not be
	 * recorded.
	 */
	public static final int DUMMY_SURVEY_ID = 0;
	
	//the survey instance that each instance of this service uses
	private Survey survey;
	
	//is a survey currently running?
	private boolean inSurvey = false;
	
	//the current survey's information
	private SurveyInfo currentInfo;
	
	//used for refreshing the notification bar
	private Handler refreshHandler = new Handler();
	private Runnable runRefresh = new Runnable()
	{
		@Override
		public void run()
		{
			refresh();
		}
	};
	private Runnable runRemove = new Runnable()
	{
		@Override
		public void run()
		{
			removeSurveys(false);
		}
	};
	
	//the binder to send to clients
	private final SurveyBinder surveyBinder = new SurveyBinder();
	
	//logging tag
	private static final String TAG = "SurveyService";
	
	//surveys that are ready or running
	private final PriorityBlockingQueue<SurveyInfo> surveys =
		new PriorityBlockingQueue<SurveyInfo>();
	
	//the notification id (only need one)
	private static final int N_ID = 0;
	
	//holds info about a survey instance
	private class SurveyInfo implements Comparable<SurveyInfo>
	{
		int id;             //survey id
		int type;           //survey type
		long startTime;     //when the survey was scheduled for
		long endTime;		//when does this survey timeout
		
		@Override
		public int compareTo(SurveyInfo that)
		{
			return (int) (that.endTime - this.endTime);
		}
		
		public String toString()
		{
			return "SurveyInfo: id = " + id + ", type = " + type +
			", startTime = " + startTime + ", endTime = " + endTime;
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid)
	{
		handleIntent(intent);
		//TODO because this service is so complex, just let it die if it
		//gets killed.  In the future, it would be better to deal with it.
		return START_NOT_STICKY;
	}
	
	//handle the incoming intents one by one
	//this is basically a cheap way of turning this into an IntentService
	//but keeping the ability to use binding
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
		else
		{
			Util.w(this, TAG, "Unknown intent action: " + action);
			if (Config.D) throw new RuntimeException(
					"Unknown intent action: " + action);
		}
	}
	
	//adds a new survey to the list (or not if surveys are off)
	private void addSurvey(Intent intent)
	{
		Util.d(null, TAG, "adding survey");
		SurveyInfo sInfo = new SurveyInfo();
		sInfo.id = intent.getIntExtra(EXTRA_SURVEY_ID, DUMMY_SURVEY_ID);
		sInfo.type = intent.getIntExtra(EXTRA_SURVEY_TYPE,
				SURVEY_TYPE_TIMED);
		sInfo.startTime = intent.getLongExtra(
				SurveyScheduler.EXTRA_RUNNING_TIME,
				System.currentTimeMillis());
		if ((!Config.getSetting(this, Config.SURVEYS_LOCAL, true)
				|| !Config.getSetting(this, Config.SURVEYS_SERVER,
						Config.SURVEYS_SERVER_DEFAULT))
				&& sInfo.type != SURVEY_TYPE_USER_INIT)
		{
			int status;
			switch (sInfo.type)
			{
			case SURVEY_TYPE_TIMED:
				status = SurveyDroidDB.TakenTable.SCHEDULED_IGNORED;
				break;
			case SURVEY_TYPE_RANDOM:
				status = SurveyDroidDB.TakenTable.RANDOM_IGNORED;
				break;
			case SURVEY_TYPE_CALL_INIT:
				status =
					SurveyDroidDB.TakenTable.CALL_INITIATED_IGNORED;
				break;
			case SURVEY_TYPE_LOC_INIT:
				status =
					SurveyDroidDB.TakenTable.LOCATION_BASED_IGNORED;
				break;
			default:
				throw new IllegalArgumentException(
						"Invalid survey type: " + sInfo.type);
			}
			TakenDBHandler tdbh = new TakenDBHandler(this);
			tdbh.openWrite();
			if (tdbh.writeSurvey(sInfo.id, status,
					System.currentTimeMillis() / 1000) == false)
			{
				Util.e(this, TAG,
						"Failed to write completion record!");
			}
			tdbh.close();
			uploadNow();
			return;
		}
		int timeout = Config.getSetting(this,
				Config.SURVEY_TIMEOUT, Config.SURVEY_TIMEOUT_DEFAULT);
		if (timeout == Config.SURVEY_TIMEOUT_NEVER)
		{
			sInfo.endTime = Config.SURVEY_TIMEOUT_NEVER;
		}
		else
		{
			sInfo.endTime = sInfo.startTime + intent.getLongExtra(
					EXTRA_SURVEY_TIMEOUT, timeout * 60 * 1000);
		}
		if ((currentInfo == null || sInfo.compareTo(currentInfo) > 0)
				&& !inSurvey)
		{
			if (currentInfo != null)
			{
				if (surveys.add(currentInfo) == false)
				{
					Util.e(null, TAG, "ERROR: could not add survey to queue!");
				}
			}
			currentInfo = sInfo;
			refresh();
		}
		else
		{
			if (surveys.add(sInfo) == false)
			{
				Util.e(null, TAG, "ERROR: could not add survey to queue!");
			}
		}
	}
	
	//starts the survey
	//removes the survey that will time out soonest from the queue
	private void startSurvey()
	{
		if (!inSurvey)
		{
			refreshHandler.removeCallbacks(runRefresh);
			refreshHandler.removeCallbacks(runRemove);
			if (currentInfo == null) return;
			inSurvey = true;
			if (currentInfo.id == DUMMY_SURVEY_ID)
			{
				survey = new Survey(this);
			}
			else
			{
				survey = new Survey(currentInfo.id, this);
			}
			
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
				PendingIntent.getService(this, 0, notificationIntent, 0);
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
		
		Class<? extends QuestionActivity> c =
			QuestionActivity.getNextQusetionClass(survey.getQuestionType());
		Intent surveyIntent = new Intent(this, c);
		surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(surveyIntent);
	}
	
	//refresh the notification bar
	private void refresh()
	{
		if (inSurvey) return;
		Util.d(null, TAG, "refresh");
		refreshHandler.removeCallbacks(runRefresh);
		refreshHandler.removeCallbacks(runRemove);
		
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
			PendingIntent.getService(this, 0, notificationIntent, 0);
		Notification notification =
			new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(
				this, contentTitle, contentText, contentIntent);
		
		//create the deleteIntent
		Intent deleteIntent = new Intent(this, SurveyService.class);
		deleteIntent.setAction(ACTION_CANCEL_SURVEY);
		PendingIntent pendingDelIntent =
			PendingIntent.getService(this, 1, deleteIntent, 0);
		notification.deleteIntent = pendingDelIntent;
		
		//add some extra things
		//the system policy will determine if either of these will actually
		//happen, so don't need to worry about it
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		
		//send it
		NotificationManager nm = (NotificationManager)
			getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(N_ID, notification);
		
		//now reschedule the refresh if needed
		long refreshInterval = Config.getSetting(this, Config.REFRESH_INTERVAL,
				Config.REFRESH_INTERVAL_DEFAULT) * 60 * 1000;
		Util.v(null, TAG, "time left on current survey: " + (currentInfo.endTime - System.currentTimeMillis()));
		if (currentInfo.endTime >= System.currentTimeMillis() +
				refreshInterval || currentInfo.endTime == Config.SURVEY_TIMEOUT_NEVER)
		{
			Util.v(null, TAG, "go for refresh again");
			refreshHandler.postDelayed(runRefresh, refreshInterval);
		}
		else
		{
			Util.v(null, TAG, "go for remove");
			refreshHandler.postDelayed(runRemove, currentInfo.endTime - System.currentTimeMillis());
		}
		
	}
	
	//marks the current survey as dismissed 
	private void cancel()
	{
		if (inSurvey) return;
		Util.i(null, TAG, "Canceling survey");
		refreshHandler.removeCallbacks(runRefresh);
		refreshHandler.removeCallbacks(runRemove);
		TakenDBHandler tdbh = new TakenDBHandler(this);
		tdbh.openWrite();
		int id = currentInfo.id;
		int type = currentInfo.type;
		if (id != DUMMY_SURVEY_ID && type != SURVEY_TYPE_USER_INIT)
		{
			int status;
			switch (type)
			{
			case SURVEY_TYPE_TIMED:
				status = SurveyDroidDB.TakenTable.SCHEDULED_DISMISSED;
				break;
			case SURVEY_TYPE_RANDOM:
				status = SurveyDroidDB.TakenTable.RANDOM_DISMISSED;
				break;
			case SURVEY_TYPE_CALL_INIT:
				status = SurveyDroidDB.TakenTable.CALL_INITIATED_DISMISSED;
				break;
			case SURVEY_TYPE_LOC_INIT:
				status = SurveyDroidDB.TakenTable.LOCATION_BASED_DISMISSED;
				break;
			default:
				throw new IllegalArgumentException(
						"Invalid survey type: " + type);
			}
			if (tdbh.writeSurvey(id, status,
					System.currentTimeMillis() / 1000) == false)
			{
				Util.e(this, TAG, "Failed to write completion record!");
			}
		}
		tdbh.close();
		
		if (!surveys.isEmpty())
		{
			Util.v(null, TAG, surveys.size() + " more surveys left; go for refresh");
			currentInfo = surveys.poll();
			refresh();
		}
		else
		{
			Util.v(null, TAG, "no more surveys; removing notification");
			currentInfo = null;
			removeNotification();
		}
	}
	
	//removes the notification and stops the service
	private void removeNotification()
	{
		Util.d(null, TAG, "Removing notification and stopping");
		refreshHandler.removeCallbacks(runRefresh);
		refreshHandler.removeCallbacks(runRemove);
		
		NotificationManager nm = (NotificationManager)
			getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(N_ID);
		stopSelf();
	}
	
	//submit answers for the current survey and finish up
	private void submit()
	{
		Util.v(null, TAG, "submitting answers");
		if (!survey.submit())
			Util.e(this, TAG, "Survey reports error in submission!");
		
		//schedule surveys again (just to be safe; it can't hurt)
		Intent scheduleIntent = new Intent(getApplicationContext(), SurveyScheduler.class);
		scheduleIntent.setAction(SurveyScheduler.ACTION_SCHEDULE_SURVEYS);
		startService(scheduleIntent);
	}
	
	//a user has finished a survey
	private void endSurvey()
	{
		if (!inSurvey) throw new
			RuntimeException("Cannot end a survey before starting one");
		Util.v(null, TAG, "ending survey");
		refreshHandler.removeCallbacks(runRefresh);
		refreshHandler.removeCallbacks(runRemove);
		submit();
		inSurvey = false;
		if (currentInfo.id != DUMMY_SURVEY_ID)
		{
			TakenDBHandler tdbh = new TakenDBHandler(this);
			tdbh.openWrite();
			int status;
			switch (currentInfo.type)
			{
			case SURVEY_TYPE_TIMED:
				status = SurveyDroidDB.TakenTable.SCHEDULED_FINISHED;
				break;
			case SURVEY_TYPE_RANDOM:
				status = SurveyDroidDB.TakenTable.RANDOM_FINISHED;
				break;
			case SURVEY_TYPE_USER_INIT:
				status = SurveyDroidDB.TakenTable.USER_INITIATED_FINISHED;
				break;
			case SURVEY_TYPE_CALL_INIT:
				status = SurveyDroidDB.TakenTable.CALL_INITIATED_FINISHED;
				break;
			case SURVEY_TYPE_LOC_INIT:
				status = SurveyDroidDB.TakenTable.LOCATION_BASED_FINISHED;
				break;
			default:
				throw new IllegalArgumentException(
						"Invalid survey type: " + currentInfo.type);
			}
			if (tdbh.writeSurvey(currentInfo.id, status,
					System.currentTimeMillis() / 1000) == false)
			{
				Util.e(this, TAG, "Failed to write completion record!");
			}
			tdbh.close();
			
			//try to upload answers ASAP
			uploadNow();
		}
		else
		{
			Config.putSetting(this, Config.SAMPLE_SURVEY_TAKEN, true);
		}
		if (surveys.isEmpty())
		{
			Util.v(null, TAG, "no more surveys, removing notification");
			removeNotification();
		}
		else
		{
			Util.v(null, TAG, surveys.size() + " more surveys left; go for refresh");
			currentInfo = surveys.poll();
			refresh();
		}
	}
	
	//a user has exited a survey before it was finished
	private void quitSurvey()
	{
		if (!inSurvey) throw new
			RuntimeException("Cannot quit a survey before starting one");
		Util.d(null, TAG, "quiting survey");
		refreshHandler.removeCallbacks(runRefresh);
		refreshHandler.removeCallbacks(runRemove);
		submit();
		inSurvey = false;
		if (currentInfo.id != DUMMY_SURVEY_ID)
		{
			TakenDBHandler tdbh = new TakenDBHandler(this);
			tdbh.openWrite();
			int status;
			switch (currentInfo.type)
			{
			case SURVEY_TYPE_TIMED:
				status = SurveyDroidDB.TakenTable.SCHEDULED_UNFINISHED;
				break;
			case SURVEY_TYPE_RANDOM:
				status = SurveyDroidDB.TakenTable.RANDOM_UNFINISHED;
				break;
			case SURVEY_TYPE_USER_INIT:
				status = SurveyDroidDB.TakenTable.USER_INITIATED_UNFINISHED;
				break;
			case SURVEY_TYPE_CALL_INIT:
				status = SurveyDroidDB.TakenTable.CALL_INITIATED_UNFINISHED;
				break;
			case SURVEY_TYPE_LOC_INIT:
				status = SurveyDroidDB.TakenTable.LOCATION_BASED_UNFINISHED;
				break;
			default:
				throw new IllegalArgumentException(
						"Invalid survey type: " + currentInfo.type);
			}
			if (tdbh.writeSurvey(currentInfo.id, status,
					System.currentTimeMillis() / 1000) == false)
			{
				Util.e(this, TAG, "Failed to write completion record!");
			}
			tdbh.close();
			uploadNow();
		}
		if (!surveys.isEmpty())
		{
			Util.v(null, TAG, surveys.size() + "more surveys left; go for refresh");
			currentInfo = surveys.poll();
			refresh();
		}
		else
		{
			Util.v(null, TAG, "no more surveys, removing notification");
			currentInfo = null;
			removeNotification();
		}
	}
	
	//remove surveys that have expired
	//if all is true, instead removes all surveys
	private void removeSurveys(boolean all)
	{
		Util.d(null, TAG, "Removing " + (all ? "all " : "") + "expired surveys");
		Util.v(null, TAG, "current time: " + System.currentTimeMillis());
		refreshHandler.removeCallbacks(runRefresh);
		refreshHandler.removeCallbacks(runRemove);
		if (currentInfo != null)
			surveys.add(currentInfo);
		TakenDBHandler tdbh = new TakenDBHandler(this);
		tdbh.openWrite();
		Iterator<SurveyInfo> i = surveys.iterator();
		while (i.hasNext())
		{
			SurveyInfo sInfo = i.next();
			Util.v(null, TAG, "Current survey: " + sInfo.id + " at " + sInfo.startTime);
			if ((sInfo.endTime != Config.SURVEY_TIMEOUT_NEVER &&
					sInfo.endTime < System.currentTimeMillis()) || all)
			{
				Util.v(null, TAG, "removing survey");
				i.remove();
				if (sInfo.id != DUMMY_SURVEY_ID &&
						sInfo.type != SURVEY_TYPE_USER_INIT)
				{
					int status;
					switch (sInfo.type)
					{
					case SURVEY_TYPE_TIMED:
						status = SurveyDroidDB.TakenTable.SCHEDULED_IGNORED;
						break;
					case SURVEY_TYPE_RANDOM:
						status = SurveyDroidDB.TakenTable.RANDOM_IGNORED;
						break;
					case SURVEY_TYPE_CALL_INIT:
						status = SurveyDroidDB.TakenTable.CALL_INITIATED_IGNORED;
						break;
					case SURVEY_TYPE_LOC_INIT:
						status = SurveyDroidDB.TakenTable.LOCATION_BASED_IGNORED;
						break;
					default:
						throw new IllegalArgumentException(
								"Invalid survey type: " + sInfo.type);
					}
					if (tdbh.writeSurvey(sInfo.id, status,
							System.currentTimeMillis() / 1000) == false)
					{
						Util.e(this, TAG, "Failed to write completion record!");
					}
				}
			}
		}
		tdbh.close();
		uploadNow();
		if (!surveys.isEmpty())
		{
			Util.v(null, TAG, surveys.size() + " more surveys left; go for refresh");
			currentInfo = surveys.poll();
			refresh();
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
	}

	/**
	 * Simple {@link Binder} extension that provides a survey object.
	 * 
	 * @author Austin Walker
	 */
	public class SurveyBinder extends Binder
	{
		private Handler binderHandler = new Handler();
		private Runnable timeout = new Runnable() {
			@Override
			public void run()
			{
				Util.d(null, TAG, "question timed out; quiting survey");
				quitSurvey();
			}
		};
		
		/**
		 * Called to get the survey currently running.  Also stops the timeout
		 * if it has previously been started with {@link #startTimeout}.
		 * 
		 * @return the {@link Survey}
		 */
		public Survey getSurvey()
		{
			binderHandler.removeCallbacks(timeout);
			return survey;
		}
		
		/**
		 * Tells the service to start the timeout for the current survey.
		 */
		public void startTimeout()
		{
			Util.d(null, TAG, "starting question timeout");
			long delay = Config.getSetting(SurveyService.this,
					Config.QUESTION_TIMEOUT,
					Config.QUESTION_TIMEOUT_DEFAULT) * 60 * 1000;
			binderHandler.postDelayed(timeout, delay);
		}
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		Util.d(null, TAG, "in onBind");
		return surveyBinder;
	}
	
	private void uploadNow()
	{
		Intent comsIntent = new Intent(this, ComsService.class);
		comsIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
		comsIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
				ComsService.SURVEY_DATA);
		startService(comsIntent);
	}
}
