/*---------------------------------------------------------------------------*
 * Location Tracker.java                                                     *
 *                                                                           *
 * Logs the subject's locations.                                             *
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
package org.surveydroid.android;

import java.util.Calendar;

import org.surveydroid.android.coms.ComsService;
import org.surveydroid.android.database.TrackingDBHandler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

//TODO
/*
 * It's possible that this class could benefit by using proximity alarms,
 * which are a function of the LocationManager.  Battery life could be extended
 * quite a bit if they are somehow more efficient that the current system.
 * This is something that can be tested extensively later on.
 * 
 * This will be great for location-based surveys actually.
 */

//TODO this class CANNOT deal with time periods that stretch through midnight

/**
 * Tracks the subject's location and writes location data points to the
 * database.
 *
 * @author Austin Walker
 */
public class LocationTrackerService extends Service
{
	/** Start location tracking */
	public static final String ACTION_START_TRACKING =
		"org.surveydroid.android.ACTION_START_TRACKING";
	
	/** Send information to the server */
	private static final String ACTION_SEND_LOCATION =
		"org.surveydroid.android.ACTION_SEND_LOCATION";
	
	/** Config key: have the location tracking times been coalesced? */
	public static final String TIMES_COALESCED = "times_coalesced";
	
	//logging tag
	private static final String TAG = "LocationTrackerService";
	
	private LocationTracker lt = new LocationTracker();
	
	private Location latest = null;
	
	private int timesSent = 0;
	
	//TODO move these to the database class?
	
	/** Put this as the accuracy to indicate that times aren't being tracked now */
	private static final int BAD_TIME = -1;
	
	/** Put this as the accuracy to indicate that tracking is off */
	private static final int TRACKING_OFF = -2;
	
	/** Put this as the accuracy to indicate that we don't have a valid time now */
	private static final int NO_LOCATION = -3;
	
	/** Put this as the accuracy to indicate that the location is out of range */
	private static final int OUT_OF_RANGE = -4;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid)
	{
		if (intent == null)
		{
			Intent restartIntent =
				new Intent(this, LocationTrackerService.class);
			restartIntent.setAction(ACTION_START_TRACKING);
			handleIntent(restartIntent);
		}
		else
		{
			handleIntent(intent);
		}
		return START_STICKY;
	}

	/**
	 * Handles each intent
	 * 
	 * @param intent the intent that started this service
	 */
	private synchronized void handleIntent(Intent intent)
	{
		String action = intent.getAction();
		if (action.equals(ACTION_START_TRACKING))
		{
			schedule();
		}
		else if (action.equals(ACTION_SEND_LOCATION))
		{
			sendLocation();
		}
		else
		{
			Util.w(null, TAG, "Unknown intent action: " + action);
		}
	}
	
	private class TimePeriod implements Comparable<TimePeriod>
	{
		int start;
		int end;
		
		public TimePeriod(int s, int e)
		{
			start = s;
			end = e;
		}
		
		//does this time period contain a given time?
		public boolean contains(int time)
		{
			if (time >= start && time <= end) return true;
			return false;
		}

		@Override
		public int compareTo(TimePeriod that)
		{
			//rationale for this being ok:
			//1. this is an internal class so all uses are known
			//2. no two numbers are going to differ by more than the number of
			//seconds in a day, which is less than 2^32 - 1
			return (this.start - that.start);
		}
		
		@Override
		public String toString()
		{
			return "(" + start + ", " + end + ")";
		}
	}

	/**
	 * Provides a quick way to upload data.
	 */
	private void uploadNow()
	{
		Intent comsIntent = new Intent(this, ComsService.class);
		comsIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
		comsIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
				ComsService.LOCATION_DATA);
		startService(comsIntent);
	}
	
	/**
	 * Reschedule the service to send a location again later.
	 */
	private void reschedule()
	{
		AlarmManager as = (AlarmManager)
		this.getSystemService(ALARM_SERVICE);
		
		Intent sendIntent = new Intent(this, LocationTrackerService.class);
		sendIntent.setAction(ACTION_SEND_LOCATION);
		PendingIntent pendingSend =
			PendingIntent.getService(this, 0, sendIntent, 0);
		
		long offset = Config.getSetting(this, Config.LOCATION_INTERVAL,
				Config.LOCATION_INTERVAL_DEFAULT) * 60 * 1000;
		
		as.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + offset,
				pendingSend);
	}
	
	/**
	 * Log the latest location information in the database.
	 */
	private void sendLocation()
	{
		//check that tracking is on
		if (!Config.getSetting(this, Config.TRACKING_LOCAL, true) ||
			!Config.getSetting(this, Config.TRACKING_SERVER,
						Config.TRACKING_SERVER_DEFAULT))
		{
			Util.d(null, TAG, "Tracking is disabled; sending null location");
			TrackingDBHandler tdbh = new TrackingDBHandler(this);
			tdbh.open();
			tdbh.writeLocation(0, 0, TRACKING_OFF, Util.currentTimeAdjusted() / 1000);
			tdbh.close();
			reschedule();
			uploadNow();
			return;
		}
		
		//check to ensure that we're in a valid time
		boolean log = false;
		int numTimes = Config.getSetting(this, Config.NUM_TIMES_TRACKED, 0);
		if (numTimes == 0)
		{
			Util.d(null, TAG, "Tracking always on; sending location");
			log = true;
		}
		else
		{
			Util.v(null, TAG, numTimes + " times tracked");
			//fetch all the times
			TimePeriod[] times = new TimePeriod[numTimes];
			for (int i = 0; i < numTimes; i ++)
			{
				int start;
				int end;
				try
				{
					start = Integer.parseInt(Config.getSetting(
							this, Config.TRACKED_START + i, null));
					end = Integer.parseInt(Config.getSetting(
							this, Config.TRACKED_END + i, null));
				}
				catch (NumberFormatException e)
				{
					throw new RuntimeException("No time tracked for time " + i);
				}
				times[i] = new TimePeriod(start, end);
			}
			
			//now we have the final collection of times that don't overlap
			//figure out what the current time is and act accordingly
			Calendar now = Calendar.getInstance();
			int hour = now.get(Calendar.HOUR_OF_DAY);
			int mins = now.get(Calendar.MINUTE);
			
			int currentTime = (hour * 100) + mins;
			
			Util.v(null, TAG, "Current time: " + currentTime);
			for (TimePeriod t : times)
			{
				Util.v(null, TAG, "Next time period: " + t);
				if (t.contains(currentTime))
				{
					log = true;
					Util.d(null, TAG, "Inside a valid time range; sending location");
					break;
				}
			}
		}
		if (log == false)
		{
			Util.d(null, TAG, "Not in valid time; sending null location");
			TrackingDBHandler tdbh = new TrackingDBHandler(this);
			tdbh.open();
			tdbh.writeLocation(0, 0, BAD_TIME, Util.currentTimeAdjusted() / 1000);
			tdbh.close();
			reschedule();
			uploadNow();
			return;
		}
		
		//make sure we have a valid time
		if (timesSent > 1 || latest == null)
		{
			Util.d(null, TAG, "No valid location to send; sending null location");
			TrackingDBHandler tdbh = new TrackingDBHandler(this);
			tdbh.open();
			tdbh.writeLocation(0, 0, NO_LOCATION, Util.currentTimeAdjusted() / 1000);
			tdbh.close();
			reschedule();
			uploadNow();
			return;
		}
		
		//make sure we are in range
		int numLocs = Config.getSetting(this, Config.NUM_LOCATIONS_TRACKED, 0);
		if (numLocs != 0)
		{
			log = false;
			for (int i = 0; i < numLocs; i++)
			{
				//get the location information
				double thisLon = (double) Config.getSetting(
						LocationTrackerService.this,
						Config.TRACKED_LONG + i, (float) -1.0);
				double thisLat = (double) Config.getSetting(
						LocationTrackerService.this,
						Config.TRACKED_LAT + i, (float) -1.0);
				double thisRad = (double) Config.getSetting(
						LocationTrackerService.this,
						Config.TRACKED_RADIUS + i, (float) -1.0);
				if (thisLon == -1.0 || thisLat == -1.0 || thisRad == -1.0)
				{
					throw new RuntimeException(
							"cannot find location tracking value "
							+ "for location index" + i);
				}
				
				//now see if the incoming location
				//is within a valid location
				float[] results = new float[1];
				Location.distanceBetween(latest.getLatitude(), latest.getLongitude(),
						thisLat, thisLon, results);
				Util.v(null, TAG, "Distance: "
						+ (results[0] / 1000) + "km");
				if (results[0] < (thisRad * 1000))
				{
					log = true;
					break;
				}
			}
			if (log == false)
			{
				Util.d(null, TAG, "Location is out of range");
				TrackingDBHandler tdbh = new TrackingDBHandler(this);
				tdbh.open();
				tdbh.writeLocation(0, 0, OUT_OF_RANGE, Util.currentTimeAdjusted() / 1000);
				tdbh.close();
				reschedule();
				uploadNow();
				return;
			}
		}
		
		Util.d(null, TAG, "Tracking is enabled, time is valid, and we have a location that is in range; logging");
		double lat = latest.getLatitude();
		double lon = latest.getLongitude();
		Util.v(null, TAG, "Lat: " + lat + ", long: " + lon);
		TrackingDBHandler tdbh = new TrackingDBHandler(this);
		tdbh.open();
		tdbh.writeLocation(lat, lon, latest.getAccuracy(), Util.currentTimeAdjusted() / 1000);
		tdbh.close();
		reschedule();
		uploadNow();
		timesSent++;
	}
	
	/**
	 * Fix the times so that they don't overlap
	 */
	private void coalesceTimes()
	{
		//FIXME
		/*
		//get the number of times we're looking at
		int numTimes = Config.getSetting(this,
				Config.NUM_TIMES_TRACKED, 0);
		if (numTimes == 0)
		{
			Config.putSetting(this, TIMES_COALESCED, true);
			return;
		}
		
		TimePeriod[] times = new TimePeriod[numTimes];
		for (int i = 0; i < numTimes; i++)
		{
			String start =
				Config.getSetting(this, Config.TRACKED_START + i, null);
			String end =
				Config.getSetting(this, Config.TRACKED_END + i, null);
			if (start == null || end == null) throw new RuntimeException(
					"start or end time is null for time " + i);
			times[i] = new TimePeriod(Integer.parseInt(start),
					Integer.parseInt(end));
		}
		Arrays.sort(times);
		
		Util.v(null, TAG, "Uncoalessed times:");
		for (TimePeriod time : times)
		{
			Util.v(null, TAG, "Time period: " + time.start
					+ " to " + time.end);
		}
		
		//at this point, we have an array of times sorted by the start time
		ArrayList<TimePeriod> finalTimes = new ArrayList<TimePeriod>();
		int lastTime = 0;
		for (int i = 1; i < times.length; i++)
		{
			if (times[lastTime].contains(times[i].start))
			{
				if (!times[lastTime].contains(times[i].end))
					times[lastTime].end = times[i].end;
			}
			else
			{
				finalTimes.add(times[lastTime]);
				lastTime = i;
			}
		}
		finalTimes.add(times[lastTime]);
		
		Config.putSetting(this, Config.NUM_TIMES_TRACKED, finalTimes.size());
		
		Util.v(null, TAG, "Coalessed times:");
		int i = 0;
		for (TimePeriod time : finalTimes)
		{
			Config.putSetting(this, Config.TRACKED_START + i, "" + time.start);
			Config.putSetting(this, Config.TRACKED_END + i, "" + time.end);
			Util.v(null, TAG, "Time period: " + time.start
					+ " to " + time.end);
			i++;
		}*/
		Config.putSetting(this, TIMES_COALESCED, true);
	}
	
	/**
	 * Schedules alarms to turn tracking on and off
	 */
	private void schedule()
	{
		//some setup stuff
		AlarmManager as = (AlarmManager)
			this.getSystemService(ALARM_SERVICE);
		
		if (!Config.getSetting(this, TIMES_COALESCED, false))
		{
			Util.d(null, TAG, "Coalescing times");
			coalesceTimes();
		}
		else
		{
			Util.d(null, TAG, "Times already coalesced");
		}
		if (!lt.started) lt.start();
		
		Intent sendIntent = new Intent(this, LocationTrackerService.class);
		sendIntent.setAction(ACTION_SEND_LOCATION);
		PendingIntent pendingSend =
			PendingIntent.getService(this, 0, sendIntent, 0);
		
		//cancel any previous instances (in case this service was restarted)
		as.cancel(pendingSend);
		
		//give the tracker a moment to get the first location
		as.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingSend);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public void onDestroy()
	{
		Util.d(null, TAG, "Location service killed!");
		lt.stop();
	}
	
	/* ------------------------------------------------ */
	/* -- The actual location recording goes on here -- */
	/* ------------------------------------------------ */

	private class LocationTracker implements LocationListener
	{
		//has the location tracker been started?
		private boolean started = false;
	
		//logging tag
		private static final String TAG = "LocationTracker";
		
		private static final int INTERNAL_LOGGING_RATE = 5; //minutes
		
		/** Starts the location tracker. */
		public void start()
		{
			if (started) 
			{
				throw new RuntimeException("already started!");
			}
			started = true;
			LocationManager lm = (LocationManager)
				LocationTrackerService.this.getSystemService(
						Context.LOCATION_SERVICE);
			int numStarted = 0;
			try
			{
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					INTERNAL_LOGGING_RATE * 60 * 1000, 0, this);
				numStarted++;
			}
			catch (IllegalArgumentException e)
			{
				Util.w(null, TAG, "Could not request updates from the GPS provider");
			}
			try
			{
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					INTERNAL_LOGGING_RATE * 60 * 1000, 0, this);
				numStarted++;
			}
			catch (IllegalArgumentException e)
			{
				Util.w(null, TAG, "Could not request updates from the network provider");
			}
			if (numStarted == 0)
			{
				Util.e(null, TAG, "Could not start tracking location because no providers were accepted");
				started = false;
				throw new RuntimeException("Could not start tracking");
			}
		}
		
		/** Stops the location tracker. */
		public void stop()
		{
			if (!started)
				throw new RuntimeException("can't stop; not started!");
			started = false;
			LocationManager lm = (LocationManager)
				LocationTrackerService.this.getSystemService(
						Context.LOCATION_SERVICE);
			lm.removeUpdates(this);
		}
	
		@Override
		public void onLocationChanged(Location loc)
		{
			Util.i(null, TAG, "Got a new location");
			latest = loc;
			timesSent = 0;
		}
	
		@Override
		public void onProviderDisabled(String provider)
		{
			/*
			 * Turns out, it's actually not possible to turn on the GPS
			 * programatically without exploiting some pretty serious security
			 * flaws in the Android system.  Since it's probably not good
			 * to write stuff that relies on bugs that are likely to get fixed,
			 * all we can do here is to fall back to a different provider.
			 */
			Util.i(null, TAG, "Location provider " + provider +
					" disabled!");
		}
	
		@Override
		public void onProviderEnabled(String provider)
		{
			Util.i(null, TAG, "Location provider " + provider +
			" enabled!");
		}
	
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			//nothing to do
		}
	}
}