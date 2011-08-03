/*---------------------------------------------------------------------------*
 * ComsService.java                                                          *
 *                                                                           *
 * Handles communication between the webserver and the phone.                *
 *---------------------------------------------------------------------------*/
package org.peoples.android.coms;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.peoples.android.Config;
import org.peoples.android.Util;

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
	/**
	 * Tells the service to upload any data.  Optionally, the specific type of
	 * data can be specified with {@link EXTRA_DATA_TYPE}; by default, all data
	 * is uploaded.
	 */
	public static final String ACTION_UPLOAD_DATA =
		"org.peoples.android.coms.ACTION_UPLOAD_DATA";
	
	/** Tells the service to download data from the server. */
	public static final String ACTION_DOWNLOAD_DATA =
		"org.peoples.android.coms.ACTION_DOWNLOAD_DATA";
	
	//intent extras
	/**
	 * Can be used with {@link ACTION_UPLOAD_DATA} to upload only a certain
	 * type of data.  A good example of when this might be useful is when a
	 * survey has just finished: at that point, there is certainly new survey
	 * data.  Can be {@link SURVEY_DATA}, {@link LOCATION_DATA},
	 * or {@link CALL_DATA}.
	 */
	public static final String EXTRA_DATA_TYPE =
		"org.peoples.android.coms.EXTRA_DATA_TYPE";
	
	/**
	 * If set to true, the will cause the requested action to be repeated
	 * after the appropriate delay (either {@link Config.PUSH_INTERVAL} or
	 * {@link Config.PUSH_INTERVAL}.  If set, {@link EXTRA_RUNNING_TIME}
	 * must also be given.
	 */
	public static final String EXTRA_REPEATING =
		"org.peoples.android.coms.EXTRA_REPEATING";
	
	/**
	 * Used to tell this service when it was set to run; used with
	 * {@link EXTRA_REPEATING}.
	 */
	public static final String EXTRA_RUNNING_TIME =
		"org.peoples.android.coms.EXTRA_RUNNING_TIME";
	
	//data types for the extra
	/** Survey data; used for {@link EXTRA_DATA_TYPE}. */
	public static final int SURVEY_DATA = 0;
	
	/** Location data; used for {@link EXTRA_DATA_TYPE}. */
	public static final int LOCATION_DATA = 1;
	
	/** Call data; used for {@link EXTRA_DATA_TYPE}. */
	public static final int CALL_DATA = 2;
	
	/** Application status data; used for {@link EXTRA_DATA_TYPE} */
	public static final int STATUS_DATA = 3;
	
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
			Util.d(this, TAG, "Uploading data");
			switch (intent.getIntExtra(EXTRA_DATA_TYPE, -1))
			{
			case SURVEY_DATA:
				Push.pushAnswers(this);
				Push.pushCompletionData(this);
				break;
			case LOCATION_DATA:
				Push.pushLocations(this);
				break;
			case CALL_DATA:
				Push.pushCallLog(this);
				break;
			case STATUS_DATA:
				Push.pushStatusData(this);
			default:
				Push.pushAnswers(this);
				Push.pushCompletionData(this);
				Push.pushLocations(this);
				Push.pushCallLog(this);
				Push.pushStatusData(this);
			}
			
			reschedule(intent);
		}
		else if (action.equals(ACTION_DOWNLOAD_DATA))
		{
			Util.d(this, TAG, "Dowloading data");
			Pull.syncWithWeb(this);
			
			reschedule(intent);
		}
		else
		{
			Util.w(null, TAG, "Unknown action: " + action);
			if (Config.D)
				throw new RuntimeException("Unknown action: " + action);
		}
	}
	
	//reschedule the service if needed
	private void reschedule(Intent intent)
	{
		if (intent.getBooleanExtra(EXTRA_REPEATING, false))
		{
			//FIXME still need to figure out what's going on here
//			long time = intent.getLongExtra(EXTRA_RUNNING_TIME, -1);
//			if (time == -1)
//				throw new RuntimeException("Must give running time");
			long time = Calendar.getInstance().getTimeInMillis();
			Intent comsIntent = new Intent(this, ComsService.class);
			comsIntent.setAction(intent.getAction());
			comsIntent.putExtra(EXTRA_REPEATING, true);
			if (intent.getAction().equals(ACTION_UPLOAD_DATA))
			{
				comsIntent.putExtra(EXTRA_RUNNING_TIME,
						time + (Config.getSetting(this, Config.PUSH_INTERVAL,
								Config.PUSH_INTERVAL_DEFAULT) * 60 * 1000));
			}
			else
			{
				comsIntent.putExtra(EXTRA_RUNNING_TIME,
						time + (Config.getSetting(this, Config.PULL_INTERVAL,
								Config.PULL_INTERVAL_DEFAULT) * 60 * 1000));
			}
			PendingIntent pendingComs = PendingIntent.getService(
					this, 0, comsIntent, PendingIntent.FLAG_ONE_SHOT);
			AlarmManager alarm =
				(AlarmManager) getSystemService(Context.ALARM_SERVICE);
			alarm.set(AlarmManager.RTC_WAKEUP,
				time + (comsIntent.getLongExtra(EXTRA_RUNNING_TIME, -1)), pendingComs);
		}
	}
}
