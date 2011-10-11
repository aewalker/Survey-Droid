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

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

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
	 * Submit all live answers for this survey. Should only be
	 * sent after {@link #ACTION_SURVEY_READY}, otherwise the survey will not
	 * be built and an exception will be thrown.
	 */
	public static final String ACTION_SUBMIT_ANSWERS = 
		"org.surveydroid.android.survey.ACTION_SUBMIT_ANSWERS";
	
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
	
	//refresh the current notification, which vibrates/plays sound again
	private static final String ACTION_REFRESH =
		"org.surveydroid.android.survey.ACTION_REFRESH";
	
	//removes the current survey notification and marks any surveys
	//that were held in it as being ignored
	private static final String ACTION_REMOVE_SURVEYS =
		"org.surveydroid.android.survey.ACTION_REMOVE_SURVEYS";
	
	//action to use in the intent sent when the user clicks the
	//clear all button
	private static final String ACTION_CANCEL_SURVEYS =
		"org.surveydroid.android.survey.ACTION_CANCEL_SURVEYS";
	
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
	
	//how many times the survey notification has been refreshed
	private static final String EXTRA_REFRESH_COUNT =
		"org.surveydroid.android.survey.EXTRA_REFRESH_COUNT";
	
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
	 * Used with {@link #EXTRA_SURVEY_TYPE} for location poximity initiated
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
	
	//the id of the survey that is currently running
	private int currentID;
	
	//is the service currently active?
	private boolean active = false;
	
	//is the current notification going to time out?
	private boolean timeoutOn = false;
	
	//for testing, holds the time that the most recent survey was scheduled for
	private long currentTime;
	
	//the binder to send to clients
	private final SurveyBinder surveyBinder = new SurveyBinder();
	
	//logging tag
	private static final String TAG = "SurveyService";
	
	//collection of all the survey ids currently in the notification
	//(can be multiple copies of the same one)
	//also keep the types
	private final Queue<Integer> surveys = new LinkedList<Integer>();
	private final Queue<Integer> surveyTypes = new LinkedList<Integer>();
	
	//the notification id (only need one)
	private static final int N_ID = 0;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid)
	{
		handleIntent(intent);
		//TODO because this service is so complex, just let it die if it
		//gets killed.  In the future, it would be better to do with it.
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
				int id = intent.getIntExtra(EXTRA_SURVEY_ID, DUMMY_SURVEY_ID);
				int type = intent.getIntExtra(EXTRA_SURVEY_TYPE,
						SURVEY_TYPE_TIMED);
				currentTime = intent.getLongExtra(
						SurveyScheduler.EXTRA_RUNNING_TIME, 0);
				if (surveys.isEmpty())
				{
					Util.v(null, TAG, "surveys is empty");
					active = true;
				}
				surveys.add(id);
				surveyTypes.add(type);
				refresh(0, true);
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
			else if (action.equals(ACTION_REFRESH))
			{
				if (!active)
				{
					stopSelf();
					return;
				}
				int count = intent.getIntExtra(EXTRA_REFRESH_COUNT, 0);
				refresh(count, true);
			}
			else if (action.equals(ACTION_REMOVE_SURVEYS))
			{
				if (timeoutOn) removeNotification(true);
			}
			else if (action.equals(ACTION_SUBMIT_ANSWERS))
			{
				submit();
			}
			else if (action.equals(ACTION_CANCEL_SURVEYS))
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
	
	//starts the survey
	private void startSurvey()
	{
		if (inSurvey)
		{
			Toast.makeText(this, "Please finish the current survey "
					+ "before starting another.", Toast.LENGTH_SHORT).show();
			return;
		}
		currentID = surveys.poll();
		inSurvey = true;
		if (!surveys.isEmpty()) refresh(0, false);
		else removeNotification(false);
		
		Class<? extends QuestionActivity> c =
			QuestionActivity.getNextQusetionClass(survey.getQuestionType());
		Intent surveyIntent = new Intent(this, c);
		surveyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(surveyIntent);
	}
	
	//refresh the notification bar; it has been refreshed count times
	//if alarm is true, then some kind of alarm should go off (vibration/sound)
	private void refresh(int count, boolean alarm)
	{
		Util.d(null, TAG, "refresh: count = " + count + ", alarm = " + alarm);
		timeoutOn = false;
		if (survey == null)
		{
			Util.v(null, TAG, "creating new survey; current is null");
			int id  = surveys.peek();
			if (id == DUMMY_SURVEY_ID)
				survey = new Survey(this);
			else
				survey = new Survey(id, this);
		}
		//things we're going to need for the notification
		int icon = R.drawable.survey_small;
		String tickerText;
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(currentTime);
		if (surveys.size() == 1)
			tickerText = "New Survey Awaiting";
		else
			tickerText = surveys.size() + " New Surveys Awaiting";
		long when = System.currentTimeMillis();
		Context context = getApplicationContext();
		String contentTitle = getString(R.string.app_name);
		String contentText;
		if (Config.D)
			contentText = "(" + surveys.size() + ")" +
				c.getTime().toGMTString();
		else
		{
			if (surveys.size() == 1)
				contentText = "You have a new survey awaiting;"
					+ " click here to take it now";
			else
				contentText = "You have " + surveys.size() + " new surveys"
					+ " awaiting; click here to take one now";
		}
		
		//now create the notification
		Intent notificationIntent = new Intent(this, SurveyService.class);
		notificationIntent.setAction(ACTION_SHOW_SURVEY);
		PendingIntent contentIntent =
			PendingIntent.getService(this, 0, notificationIntent, 0);
		Notification notification =
			new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(
				context, contentTitle, contentText, contentIntent);
		
		//create the deleteIntent
		Intent deleteIntent = new Intent(this, SurveyService.class);
		deleteIntent.setAction(ACTION_CANCEL_SURVEYS);
		PendingIntent pendingDelIntent =
			PendingIntent.getService(this, 1, deleteIntent, 0);
		notification.deleteIntent = pendingDelIntent;
		
		//add some extra things
		//the system policy will determine if either of these will actually
		//happen, so don't need to worry about it
		if (alarm)
		{
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		if (surveys.size() > 1)
		{
			notification.number = surveys.size();
		}
		notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
		
		//send it
		NotificationManager nm = (NotificationManager)
			getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(N_ID, notification);
		
		//now reschedule the refresh if needed
		int maxCount = Config.getSetting(this, Config.REFRESH_COUNT,
				Config.REFRESH_COUNT_DEFAULT);
		int refreshTime = Config.getSetting(this, Config.REFRESH_INTERVAL,
				Config.REFRESH_INTERVAL_DEFAULT);
		AlarmManager am = (AlarmManager)
			getSystemService(Context.ALARM_SERVICE);
		
		//make the alarm intent, etc.
		if (count < maxCount)
		{
			Intent surveyIntent = new Intent(this, SurveyService.class);
			surveyIntent.setAction(SurveyService.ACTION_REFRESH);
			surveyIntent.putExtra(SurveyService.EXTRA_REFRESH_COUNT,
					count + 1);
			PendingIntent pendingSurvey = PendingIntent.getService(
					this, 0, surveyIntent, PendingIntent.FLAG_ONE_SHOT);
			am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
					+ (refreshTime * 60 * 1000), pendingSurvey);
		}
		else
		{
			int timeout = Config.getSetting(this,
					Config.SURVEY_TIMEOUT, Config.SURVEY_TIMEOUT_DEFAULT);
			if (timeout != Config.SURVEY_TIMEOUT_NEVER)
			{
				timeoutOn = true;
				Intent timeoutIntent = new Intent(this, SurveyService.class);
				timeoutIntent.setAction(ACTION_REMOVE_SURVEYS);
				PendingIntent pendingTimeout = PendingIntent.getService(
						this, 0, timeoutIntent, PendingIntent.FLAG_ONE_SHOT);
				am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
						+ (timeout * 60 * 1000), pendingTimeout);
			}
		}
		
	}
	
	//marks all current surveys as dismissed 
	private void cancel()
	{
		Util.i(null, TAG, "Canceling all surveys");
		TakenDBHandler tdbh = new TakenDBHandler(this);
		tdbh.openWrite();
		int currentType = 0;
		if (inSurvey) currentType = surveyTypes.poll();
		while (!surveys.isEmpty())
		{
			int id = surveys.poll();
			int type = surveyTypes.poll();
			if (id != DUMMY_SURVEY_ID)
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
				case SURVEY_TYPE_USER_INIT:
					//it seems really unlikely that this case will ever
					//be used, so I don't think it makes sense to create
					//a case for USER_INITIATED_DISMISSED...
					//TODO maybe just don't even log this?
					status =
						SurveyDroidDB.TakenTable.USER_INITIATED_UNFINISHED;
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
						System.currentTimeMillis()) == false)
				{
					Util.e(this, TAG, "Failed to write completion record!");
				}
			}
		}
		tdbh.close();
		
		//just let the removeNotification function do the cleanup
		if (inSurvey)
		{
			surveyTypes.add(currentType);
			removeNotification(false);
		}
		else removeNotification(true);
	}
	
	//removes the notification and marks all current surveys as being ignored
	//if finish is true, then stop the service
	private void removeNotification(boolean finish)
	{
		Util.d(null, TAG, "Removing notification (finish = " + finish + ")");
		active = false;
		
		NotificationManager nm = (NotificationManager)
			getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(N_ID);
		int currentType = 0;
		if (!finish) currentType = surveyTypes.poll();
		if (!surveys.isEmpty())
		{
			TakenDBHandler tdbh = new TakenDBHandler(this);
			tdbh.openWrite();
			while (!surveys.isEmpty())
			{
				int id = surveys.poll();
				int type = surveyTypes.poll();
				if (id != DUMMY_SURVEY_ID)
				{
					int status;
					switch (type)
					{
					case SURVEY_TYPE_TIMED:
						status = SurveyDroidDB.TakenTable.SCHEDULED_IGNORED;
						break;
					case SURVEY_TYPE_RANDOM:
						status = SurveyDroidDB.TakenTable.RANDOM_IGNORED;
						break;
					case SURVEY_TYPE_USER_INIT:
						//it seems really unlikely that this case will ever
						//be used, so I don't think it makes sense to create
						//a case for USER_INITIATED_IGNORED...
						//TODO again, we could just not do anything here
						status =
							SurveyDroidDB.TakenTable.USER_INITIATED_UNFINISHED;
						break;
					case SURVEY_TYPE_CALL_INIT:
						status = SurveyDroidDB.TakenTable.CALL_INITIATED_IGNORED;
						break;
					case SURVEY_TYPE_LOC_INIT:
						status = SurveyDroidDB.TakenTable.LOCATION_BASED_IGNORED;
						break;
					default:
						throw new IllegalArgumentException(
								"Invalid survey type: " + type);
					}
					if (tdbh.writeSurvey(id, status,
							System.currentTimeMillis()) == false)
					{
						Util.e(this, TAG, "Failed to write completion record!");
					}
				}
			}
			tdbh.close();
		}
		
		if (finish) stopSelf();
		else surveyTypes.add(currentType);
	}
	
	//submit answers for the current survey and finish up
	private void submit()
	{
		if (!survey.submit())
			Util.e(this, TAG, "Survey reports error in submission!");
	}
	
	//a user has finished a survey
	private void endSurvey()
	{
		if (!inSurvey) throw new
			RuntimeException("Cannot end a survey before starting one");
		inSurvey = false;
		if (currentID != DUMMY_SURVEY_ID)
		{
			TakenDBHandler tdbh = new TakenDBHandler(this);
			tdbh.openWrite();
			int type = surveyTypes.poll();
			int status;
			switch (type)
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
						"Invalid survey type: " + type);
			}
			if (tdbh.writeSurvey(currentID, status,
					System.currentTimeMillis()) == false)
			{
				Util.e(this, TAG, "Failed to write completion record!");
			}
			tdbh.close();
			
			//try to upload answers ASAP
			Intent comsIntent = new Intent(this, ComsService.class);
			comsIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
			comsIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
					ComsService.SURVEY_DATA);
			startService(comsIntent);
		}
		if (surveys.isEmpty())
			stopSelf();
		else
		{
			int id  = surveys.peek();
			if (id == DUMMY_SURVEY_ID)
				survey = new Survey(this);
			else
				survey = new Survey(id, this);
		}
	}
	
	//a user has exited a survey before it was finished
	private void quitSurvey()
	{
		if (!inSurvey) throw new
			RuntimeException("Cannot quit a survey before starting one");
		inSurvey = false;
		int type = surveyTypes.poll();
		if (currentID == DUMMY_SURVEY_ID) return;
		TakenDBHandler tdbh = new TakenDBHandler(this);
		tdbh.openWrite();
		int status;
		switch (type)
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
					"Invalid survey type: " + type);
		}
		if (tdbh.writeSurvey(currentID, status,
				System.currentTimeMillis()) == false)
		{
			Util.e(this, TAG, "Failed to write completion record!");
		}
		tdbh.close();
	}

	/**
	 * Simple {@link Binder} extension that provides a survey object.
	 * 
	 * @author Austin Walker
	 */
	public class SurveyBinder extends Binder
	{
		/**
		 * Called to get the survey currently running.
		 * 
		 * @return the {@link Survey}
		 */
		public Survey getSurvey()
		{
			return survey;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		Util.d(null, TAG, "in onBind");
		return surveyBinder;
	}
}
