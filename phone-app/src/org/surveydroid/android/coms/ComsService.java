/*---------------------------------------------------------------------------*
 * ComsService.java                                                          *
 *                                                                           *
 * Handles communication between the webserver and the phone.                *
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
package org.surveydroid.android.coms;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;

import org.surveydroid.android.BootIntentReceiver;
import org.surveydroid.android.Config;
import org.surveydroid.android.Dispatcher;
import org.surveydroid.android.Util;
import org.surveydroid.android.coms.WebClient.ApiException;

import com.commonsware.cwac.wakeful.WakefulIntentService;

/**
 * Communication service that is responsible for communicating with the
 * webserver specified in the configuration.  Runs on an alarm; periodically
 * fetches data from the server and sends up new data generated on the phone.
 * 
 * @author Austin Walker
 */
public class ComsService extends WakefulIntentService
{
	/** logging tag */
	private static final String TAG = "ComsService";
	
	//intent actions
	/**
	 * Tells the service to upload any data.  Optionally, the specific type of
	 * data can be specified with {@link #EXTRA_DATA_TYPE}; by default, all data
	 * is uploaded.
	 */
	public static final String ACTION_UPLOAD_DATA =
		"org.surveydroid.android.coms.ACTION_UPLOAD_DATA";
	
	/** Tells the service to download data from the server. */
	public static final String ACTION_DOWNLOAD_DATA =
		"org.surveydroid.android.coms.ACTION_DOWNLOAD_DATA";
	
	/** Tells the service to get the salt string for hashing phone numbers */
	public static final String ACTION_GET_SALT =
		"org.surveydroid.android.coms.ACTION_GET_SALT";
	
	//intent extras
	/**
	 * Can be used with {@link #ACTION_UPLOAD_DATA} to upload only a certain
	 * type of data.  A good example of when this might be useful is when a
	 * survey has just finished: at that point, there is certainly new survey
	 * data.  Can be {@link #SURVEY_DATA}, {@link #LOCATION_DATA},
	 * or {@link #CALL_DATA}.
	 */
	public static final String EXTRA_DATA_TYPE =
		"org.surveydroid.android.coms.EXTRA_DATA_TYPE";
	
	/**
	 * If set to true, the will cause the requested action to be repeated
	 * after the appropriate delay (either {@link Config#PUSH_INTERVAL} or
	 * {@link Config#PUSH_INTERVAL}.  If set, {@link #EXTRA_RUNNING_TIME}
	 * must also be given.
	 */
	public static final String EXTRA_REPEATING =
		"org.surveydroid.android.coms.EXTRA_REPEATING";
	
	/**
	 * Used to tell this service when it was set to run; used with
	 * {@link #EXTRA_REPEATING}.
	 */
	public static final String EXTRA_RUNNING_TIME =
		"org.surveydroid.android.coms.EXTRA_RUNNING_TIME";
	
	//data types for the extra
	/** Survey data; used for {@link #EXTRA_DATA_TYPE}. */
	public static final int SURVEY_DATA = 0;
	
	/** Location data; used for {@link #EXTRA_DATA_TYPE}. */
	public static final int LOCATION_DATA = 1;
	
	/** Call data; used for {@link #EXTRA_DATA_TYPE}. */
	public static final int CALL_DATA = 2;
	
	/** Application status data; used for {@link #EXTRA_DATA_TYPE} */
	public static final int STATUS_DATA = 3;
	
	/** Photos, etc.; used for {@link #EXTRA_DATA_TYPE} */
	public static final int EXTRAS_DATA = 4;
	
	/**
	 * Constructor.
	 */
	public ComsService()
	{
		super(null);
	}

	@Override
	protected void doWakefulWork(Intent intent)
	{
		String action = intent.getAction();
		
		if (action.equals(ACTION_UPLOAD_DATA))
		{
			Util.d(null, TAG, "Uploading data");
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
				break;
			case EXTRAS_DATA:
				Push.pushExtrasData(this);
				break;
			default:
				Push.pushAnswers(this);
				Push.pushCompletionData(this);
				Push.pushLocations(this);
				Push.pushCallLog(this);
				Push.pushStatusData(this);
				Push.pushExtrasData(this);
			}
			
			reschedule(intent);
		}
		else if (action.equals(ACTION_DOWNLOAD_DATA))
		{
			Util.d(null, TAG, "Dowloading data");
			Pull.syncWithWeb(this);
			
			Intent pullDone = new Intent(this, BootIntentReceiver.class);
			pullDone.setAction(BootIntentReceiver.ACTION_PULL_COMPLETE);
			sendBroadcast(pullDone);
			
			reschedule(intent);
		}
		else if (action.equals(ACTION_GET_SALT))
		{
			Util.i(null, TAG, "Geting salt value");
			getSalt();
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
			long time = System.currentTimeMillis();
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
			Uri uri = Uri.parse(TAG + " reschedule");
			Dispatcher.dispatch(this, comsIntent,
				time + (comsIntent.getLongExtra(EXTRA_RUNNING_TIME, -1)),
				Dispatcher.TYPE_WAKEFUL_AUTO, uri);
		}
	}
	
	/**
	 * Fetch the salt value from the server.
	 */
	private void getSalt()
	{
		TelephonyManager tManager =
        	(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		StringBuilder url = new StringBuilder();
		if (Config.getSetting(this, Config.HTTPS, Config.HTTPS_DEFAULT))
			url.append("https://");
		else
			url.append("http://");
		url.append(Config.getSetting(this,
				Config.SERVER, Config.SERVER_DEFAULT));
		url.append("/api/salt/");
		String uid = tManager.getDeviceId();
		if (uid != null)
		{
    		url.append(uid);
    		String finalURL = url.toString();
    		Util.v(null, TAG, "Pull url: " + finalURL);
    		String salt;
    		try
    		{
    			salt = WebClient.getUrlContent(this, finalURL);
    		}
    		catch (Exception e)
    		{
    			Util.e(null, TAG,
    					"Unable to communicate with remote server");
    			try
    			{
    				ApiException apiE = (ApiException) e;
    				Util.e(null, TAG, "Reason: " + apiE.getMessage());
    				Util.e(null, TAG, "Make sure this device is registered"
    						+ " and the server is working");
    			}
    			catch (Exception unknownE)
    			{
    				Util.e(null, TAG, "Unkown Reason: " + Util.fmt(e));
    			}
    			return;
    		}
    		Util.d(null, TAG, "Salt is \"" + salt + "\"");
    		Config.putSetting(this, Config.SALT, salt);
    	}
    	else
    	{
    		Util.w(null, TAG, "Device ID not available, please try again later");
    	}
	}
}
