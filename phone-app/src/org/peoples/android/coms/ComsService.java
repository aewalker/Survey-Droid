/*---------------------------------------------------------------------------*
 * ComsService.java                                                          *
 *                                                                           *
 * Handles communication between the webserver and the phone.                *
 *---------------------------------------------------------------------------*/
package org.peoples.android.coms;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.peoples.android.Config;

/**
 * Communication service that is responsible for communicating with the
 * webserver specified in the configuration.  Runs on an alarm; periodically
 * fetches data from the server and sends up new data generated on the phone.
 * 
 * @author Austin Walker
 */
public class ComsService extends IntentService
{
	//logging tag
	private static final String TAG = "ComsService";
	
	//intent actions
	public static final String ACTION_UPLOAD_DATA =
		"org.peoples.android.coms.ACTION_UPLOAD_DATA";
	public static final String ACTION_DOWNLOAD_DATA =
		"org.peoples.android.coms.ACTION_DOWNLOAD_DATA";
	
	//intent extras
	public static final String EXTRA_DATA_TYPE =
		"org.peoples.android.coms.EXTRA_DATA_TYPE";
	public static final String EXTRA_REPEATING =
		"org.peoples.android.coms.EXTRA_REPEATING";
	public static final String EXTRA_RUNNING_TIME =
		"org.peoples.android.coms.EXTRA_RUNNING_TIME";
	
	//data types for the extra
	public static final int SURVEY_DATA = 0;
	public static final int LOCATION_DATA = 1;
	public static final int CALL_DATA = 2;
	
	/**
	 * Constructor.
	 */
	public ComsService()
	{
		super(null);
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		String action = intent.getAction();
		
		if (action.equals(ACTION_UPLOAD_DATA))
		{
			if (Config.D) Log.d(TAG, "Uploading data");
			switch (intent.getIntExtra(EXTRA_DATA_TYPE, -1))
			{
			case SURVEY_DATA:
				Push.pushAnswers(getApplicationContext());
				break;
			case LOCATION_DATA:
				Push.pushLocations(getApplicationContext());
				break;
			case CALL_DATA:
				Push.pushCallLog(getApplicationContext());
				break;
			default:
				Push.pushAnswers(getApplicationContext());
				Push.pushLocations(getApplicationContext());
				Push.pushCallLog(getApplicationContext());
			}
			
			reschedule(intent);
		}
		else if (action.equals(ACTION_DOWNLOAD_DATA))
		{
			if (Config.D) Log.d(TAG, "Dowloading data");
			Pull.syncWithWeb(getApplicationContext());
			
			reschedule(intent);
		}
		else
		{
			//TODO could just ignore unknown intents...
			throw new RuntimeException("Unknown action: " + action);
		}
	}
	
	//reschedule the service if needed
	private void reschedule(Intent intent)
	{
		if (intent.getBooleanExtra(EXTRA_REPEATING, false))
		{
			long time = intent.getLongExtra(EXTRA_RUNNING_TIME, -1);
			if (time == -1)
				throw new RuntimeException("Must give running time");
			Intent comsIntent = new Intent(getApplicationContext(),
					ComsService.class);
			comsIntent.setAction(intent.getAction());
			comsIntent.putExtra(EXTRA_REPEATING, true);
			PendingIntent pendingComs = PendingIntent.getService(
					getApplicationContext(), 0, comsIntent,
					PendingIntent.FLAG_ONE_SHOT);
			AlarmManager alarm =
				(AlarmManager) getSystemService(Context.ALARM_SERVICE);
			if (intent.getAction().equals(ACTION_UPLOAD_DATA))
			{
				alarm.set(AlarmManager.RTC_WAKEUP,
					time + (Config.PUSH_INTERVAL * 60 * 1000), pendingComs);
			}
			else
			{
				alarm.set(AlarmManager.RTC_WAKEUP,
					time + (Config.PULL_INTERVAL * 60 * 1000), pendingComs);
			}
		}
	}
}
