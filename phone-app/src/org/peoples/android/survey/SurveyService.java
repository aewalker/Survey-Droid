/*---------------------------------------------------------------------------*
 * SurveyService.java                                                        *
 *                                                                           *
 * Runs while the user it taking a survey; holds the survey object.  This    *
 * allows the user to rotate the screen without having to rebuild the whole  *
 * survey.                                                                   *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.peoples.android.Config;
import org.peoples.android.R;
import org.peoples.android.database.PeoplesDB;
import org.peoples.android.database.TakenDBHandler;

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
	 * A survey has been approved, show it now
	 */
	public static final String ACTION_SHOW_SURVEY =
		"org.peoples.android.survey.ACTION_SHOW_SURVEY";
	
	/**
	 * Submit all live answers for this survey. Should only be
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
	
	/**
	 * Sent when the user leaves a survey before it's finished.  Used to mark
	 * a survey uncompleted.
	 */
	public static final String ACTION_QUIT_SURVEY =
		"org.peoples.android.survey.ACTION_QUIT_SURVEY";
	
	//refresh the current notification, which vibrates/plays sound again
	private static final String ACTION_REFRESH =
		"org.peoples.android.survey.ACTION_REFRESH";
	
	//removes the current survey notification and marks any surveys
	//that were held in it as being ignored
	private static final String ACTION_REMOVE_SURVEYS =
		"org.peoples.android.survey.ACTION_REMOVE_SURVEYS";
	
	//key values for extras
	/** The id of the survey this service is starting for. */
	public static final String EXTRA_SURVEY_ID =
		"org.peoples.android.survey.EXTRA_SURVEY_ID";
	
	//how many times the survey notification has been refreshed
	private static final String EXTRA_REFRESH_COUNT =
		"org.peoples.android.survey.EXTRA_REFRESH_COUNT";
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Given this id, a dummy survey will be used and the answers will not be
	 * recorded.
	 */
	public static final int DUMMY_SURVEY_ID = 0;
	
	//the survey instance that each instance of this service uses
	private Survey survey;
	
	//the current survey id
	private int currentID;
	
	//is a survey currently running?
	private boolean inSurvey = false;
	
	//is the service currently active?
	private boolean active = false;
	
	//is the current notification going to time out?
	private boolean timeoutOn = false;
	
	//the binder to send to clients
	private final SurveyBinder surveyBinder = new SurveyBinder();
	
	//logging tag
	private static final String TAG = "SurveyService";
	
	//collection of all the survey ids currently in the notification
	//(can be multiple copies of the same one)
	private final ArrayList<Integer> surveys = new ArrayList<Integer>();
	
	//the notification id (only need one)
	private static final int N_ID = 0;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid)
	{
		handleIntent(intent);
		return START_STICKY;
	}
	
	//handle the incoming intents one by one
	//this is basically a cheap way of turning this into an IntentService
	//but keeping the ability to use binding
	private synchronized void handleIntent(Intent intent)
	{
			String action = intent.getAction();
			if (Config.D) Log.d(TAG, "Recieved action: " + action);
			if (action.equals(ACTION_SURVEY_READY))
			{
				int id = intent.getIntExtra(EXTRA_SURVEY_ID, DUMMY_SURVEY_ID);
				if (surveys.isEmpty())
				{
					if (Config.D) Log.v(TAG, "surveys is empty");
					active = true;
				}
				surveys.add(id);
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
			else
			{
				Log.w(TAG, "Unknown intent action: " + action);
				if (Config.D) throw new RuntimeException(
						"Unknown intent action: " + action);
			}
	}
	
	//starts the survey
	private void startSurvey()
	{
		if (inSurvey)
		{
			//FIXME this Toast isn't showing up, but otherwise, the system is
			//behaving properly
			Toast.makeText(getApplicationContext(), "Please finish the current"
					+ " survey before staring another.", Toast.LENGTH_LONG);
			return;
		}
		surveys.remove(currentID);
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
		timeoutOn = false;
		if (survey == null)
		{
			if (Config.D) Log.v(TAG, "creating new survey; current is null");
			currentID  = surveys.get(0);
			if (currentID == DUMMY_SURVEY_ID)
				survey = new Survey(this);
			else
				survey = new Survey(currentID, this);
			if (survey == null) throw new RuntimeException("survey is still null!");
		}
		//things we're going to need for the notification
		int icon = R.drawable.blue_survey_small;
		String tickerText;
		if (surveys.size() == 1)
			tickerText = "New Survey Awaiting";
		else
			tickerText = surveys.size() + " New Surveys Awaiting";
		long when = System.currentTimeMillis();
		Context context = getApplicationContext();
		String contentTitle = "PEOPLES";
		String contentText;
		if (surveys.size() == 1)
			contentText = "You have a new survey awaiting;"
				+ " click here to take it now";
		else
			contentText = "You have " + surveys.size() + " new surveys"
				+ " awaiting; click here to take one now";
		
		//now create the notification
		Intent notificationIntent = new Intent(this, SurveyService.class);
		notificationIntent.setAction(ACTION_SHOW_SURVEY);
		PendingIntent contentIntent =
			PendingIntent.getService(this, 0, notificationIntent, 0);
		Notification notification =
			new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(
				context, contentTitle, contentText, contentIntent);
		
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
	
	//removes the notification and marks all current surveys as being ignored
	//if finish is true, then stop the service
	private void removeNotification(boolean finish)
	{
		active = false;
		
		NotificationManager nm = (NotificationManager)
			getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(N_ID);
		if (!surveys.isEmpty())
		{
			TakenDBHandler tdbh = new TakenDBHandler(this);
			tdbh.openWrite();
			for (int id : surveys)
			{
				if (id != DUMMY_SURVEY_ID) tdbh.writeSurvey(id,
					PeoplesDB.TakenTable.SCHEDULED_IGNORED,
					System.currentTimeMillis());
			}
			tdbh.close();
		}
		
		if (finish) stopSelf();
	}
	
	//submit answers for the current survey and finish up
	private void submit()
	{
		if (!survey.submit())
			Log.e(TAG, "Survey reports error in submission!");
	}
	
	//a user has finished a survey
	private void endSurvey()
	{
		inSurvey = false;
		if (currentID != DUMMY_SURVEY_ID)
		{
			TakenDBHandler tdbh = new TakenDBHandler(this);
			tdbh.openWrite();
			tdbh.writeSurvey(currentID,
					PeoplesDB.TakenTable.SCHEDULED_FINISHED,
					System.currentTimeMillis());
			tdbh.close();
		}
		if (surveys.isEmpty())
			stopSelf();
		else
		{
			currentID  = surveys.get(0);
			if (currentID == DUMMY_SURVEY_ID)
				survey = new Survey(this);
			else
				survey = new Survey(currentID, this);
		}
	}
	
	//a user has exited a survey before it was finished
	private void quitSurvey()
	{
		if (currentID == DUMMY_SURVEY_ID) return;
		TakenDBHandler tdbh = new TakenDBHandler(this);
		tdbh.openWrite();
		tdbh.writeSurvey(currentID, PeoplesDB.TakenTable.SCHEDULED_UNFINISHED,
				System.currentTimeMillis());
		tdbh.close();
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
		if (Config.D) Log.d(TAG, "in onBind");
		return surveyBinder;
	}
}
