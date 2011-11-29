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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

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
	
	//switch tracking on or off, depending on its current state
	private static final String ACTION_TOGGLE_TRACKING =
		"org.surveydroid.android.ACTION_TOGGLE_TRACKING";
	
	/** Config key: have the location tracking times been coalesced? */
	public static final String TIMES_COALESCED = "times_coalesced";
	
	//logging tag
	private static final String TAG = "LocationTrackerService";
	
	//is tracking currently running?
	private boolean isTracking = false;
	
	//has this service already gone through its first run?
	private boolean alreadyStarted = false;
	
	private LocationTracker lt = new LocationTracker();
	
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

	//handles each intent
	private synchronized void handleIntent(Intent intent)
	{
		String action = intent.getAction();
		if (action.equals(ACTION_TOGGLE_TRACKING))
		{
			if (isTracking) lt.stop();
			else lt.start();
			isTracking = !isTracking;
			schedule();
		}
		else if (action.equals(ACTION_START_TRACKING))
		{
			schedule();
		}
		else
		{
			Util.w(this, TAG, "Unknown intent action: " + action);
			if (Config.D) throw new
				RuntimeException("Unknown intent action: " + action);
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
	}
	
//	//returns day as string by converting using the Calendar constants
//	private static String getDay(int day)
//	{
//		switch (day)
//		{
//		case Calendar.SUNDAY:
//			return "Sun";
//		case Calendar.MONDAY:
//			return "Mon";
//		case Calendar.TUESDAY:
//			return "Tue";
//		case Calendar.WEDNESDAY:
//			return "Wed";
//		case Calendar.THURSDAY:
//			return "Thu";
//		case Calendar.FRIDAY:
//			return "Fri";
//		case Calendar.SATURDAY:
//			return "Sat";
//		default:
//			throw new IllegalArgumentException("Invalid day: " + day);
//		}
//	}
	
	//fix the times so that they don't overlap
	private void coalesceTimes()
	{
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
		}
		Config.putSetting(this, TIMES_COALESCED, true);
	}
	
	//schedules alarms to turn tracking on and off
	private void schedule()
	{
		//some setup stuff
		AlarmManager as = (AlarmManager)
			this.getSystemService(ALARM_SERVICE);
		
		if (!Config.getSetting(this, TIMES_COALESCED, false))
		{
			Util.d(null, TAG, "Reseting service and coalescing times");
			
			//reset the whole service
			if (isTracking)
			{
				lt.stop();
				isTracking = false;
			}
			alreadyStarted = false;
			coalesceTimes();
		}
		else
		{
			Util.d(null, TAG, "Times already coalesced");
		}
		
		int numTimes = Config.getSetting(this, Config.NUM_TIMES_TRACKED, 0);
		//TODO fix this; remove all the old code
		if (true)
//		if (numTimes == 0)
		{
			Util.d(null, TAG, "No times tracked; tracking all the time");
			if (!isTracking)
				lt.start();
			isTracking = true;
			alreadyStarted = true;
		}
		else
		{
			Util.d(null, TAG, numTimes + " times tracked");
			//fetch all the times
			int[] times = new int[numTimes * 2];
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
					throw new RuntimeException(
							"No time tracked for time " + i);
				}
				times[i * 2] = start;
				times[(i * 2) + 1] = end;
			}
			
			//now we have the final collection of times that don't overlap
			//figure out what the current time is and act accordingly
			Calendar now = Calendar.getInstance(
					TimeZone.getDefault(), Locale.US);
			now.setTimeInMillis(System.currentTimeMillis());
			int hour = now.get(Calendar.HOUR_OF_DAY);
			int mins = now.get(Calendar.MINUTE);
			
			int currentTime = (hour * 100) + mins;
			
			boolean onNow = false; //should tracking be on now?
			int nextTime = 0; //the next time after now
			while (nextTime < times.length &&
					times[nextTime] < currentTime)
			{
				onNow = !onNow;
				nextTime++;
			}
			
			if (onNow && !alreadyStarted)
			{
				Util.d(null, TAG, "should be tracking now; turning on");
				if (isTracking)
				{
					throw new RuntimeException("Already tracking!");
				}
				lt.start();
				isTracking = true;
				alreadyStarted = true;
			}
			
			//now schedule the alarm for the next time to toggle
			long nextRun = System.currentTimeMillis();
			int time;
			if (nextTime >= times.length)
			{
				//roll over to the next day
				time = times[0];
				nextRun += (24 - (hour + 1)) * 60 * 60 * 1000;
				nextRun += (60 - mins) * 60 * 1000;
				hour = 0;
				mins = 0;
			}
			else
			{
				time = times[nextTime];
			}
			int thenHour = time / 100;
			int thenMins = time - (thenHour * 100);
			nextRun += (thenHour - (hour + 1)) * 60 * 60 * 1000;
			nextRun += (60 - mins) * 60 * 1000;
			nextRun += thenMins * 60 * 1000;
			
			Calendar c = Calendar.getInstance(
					TimeZone.getDefault(), Locale.US);
			c.setTimeInMillis(nextRun);
			Util.d(null, TAG, "Next toggle at: " +
					c.getTime().toLocaleString());
			
			Intent toggleIntent =
				new Intent(this, LocationTrackerService.class);
			toggleIntent.setAction(ACTION_TOGGLE_TRACKING);
			PendingIntent pendingToggle = PendingIntent.getService(
					this, 0, toggleIntent, 0);
			as.set(AlarmManager.RTC_WAKEUP, nextRun, pendingToggle);
		}
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	private class LocationTracker implements LocationListener
	{
		//has the location tracker been started?
		private boolean started = false;
	
		//logging tag
		private static final String TAG = "LocationTracker";
		
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
			
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				Config.getSetting(LocationTrackerService.this,
						Config.LOCATION_INTERVAL,
				Config.LOCATION_INTERVAL_DEFAULT) * 60 * 1000, 0, this);
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					Config.getSetting(LocationTrackerService.this,
							Config.LOCATION_INTERVAL,
					Config.LOCATION_INTERVAL_DEFAULT) * 60 * 1000, 0, this);
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
			//XXX fix this quickly
			Util.i(null, TAG, "Got a new location");
			if (Config.getSetting(LocationTrackerService.this,
						Config.TRACKING_LOCAL, true) /*&&
				Config.getSetting(LocationTrackerService.this,
						Config.TRACKING_SERVER,
							Config.TRACKING_SERVER_DEFAULT)*/)
			{
				Util.d(LocationTrackerService.this, TAG, "tracking is enabled; logging");
				double lat = loc.getLatitude();
				double lon = loc.getLongitude();
				Util.v(null, TAG, "Lat: " + lat + ", long: " + lon);
				int numLocs = Config.getSetting(LocationTrackerService.this,
						Config.NUM_LOCATIONS_TRACKED, 0);
				boolean log = false;
				//TODO fix up the location sensitivity code
				if (false)
//				if (numLocs >= 1)
				{
					for (int i = 0; i < numLocs; i++)
					{
						//for each location tracked, get the information
						//for that location and validate it
						double thisLon = (double) Config.getSetting(
								LocationTrackerService.this,
								Config.TRACKED_LONG + i, (float) -1.0);
						double thisLat = (double) Config.getSetting(
								LocationTrackerService.this,
								Config.TRACKED_LAT + i, (float) -1.0);
						double thisRad = (double) Config.getSetting(
								LocationTrackerService.this,
								Config.TRACKED_RADIUS + i, (float) -1.0);
						if (thisLon == -1.0 ||
							thisLat == -1.0 ||
							thisRad == -1.0)
						{
							throw new RuntimeException("cannot find location "
									+ "tracking value for location index" + i);
						}
						
						//now see if the incoming location
						//is within a valid location
						float[] results = new float[1];
						Location.distanceBetween(lat, lon,
								thisLat, thisLon, results);
						Util.v(null, TAG, "Distance: "
								+ (results[0] / 1000) + "km");
						if (results[0] < (thisRad * 1000))
						{
							log = true;
							break;
						}
					}
				}
				else
				{ //assume that if there are no locations, then everything
				  //should be tracked (to track nowhere just turn off tracking)
					log = true;
				}
				
				//TODO generalize this
				//right now it's just a crutch to get by
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(loc.getTime());
				int hour = c.get(Calendar.HOUR_OF_DAY);
				if (hour < 8 || hour >= 1900) log = false;
				
				if (log)
				{
					Util.d(null, TAG, "Storing location");
					TrackingDBHandler tdbh =
						new TrackingDBHandler(LocationTrackerService.this);
					tdbh.openWrite();
					tdbh.writeLocation(lat, lon, loc.getAccuracy(),
							loc.getTime() / 1000);
					tdbh.close();
					
					//tell the coms service to upload this data
					Intent uploadIntent = new Intent(LocationTrackerService.this,
							ComsService.class);
					uploadIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
					uploadIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
							ComsService.LOCATION_DATA);
					startService(uploadIntent);
				}
				else
				{
					Util.d(null, TAG, "Not storing: loction out of range");
				}
				
			}
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