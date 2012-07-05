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

import com.commonsware.cwac.locpoll.LocationPoller;
import com.commonsware.cwac.wakeful.WakefulIntentService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

/*
 * TODO
 * It's possible that this class could benefit by using proximity alarms,
 * which are a function of the LocationManager.  Battery life could be extended
 * quite a bit if they are somehow more efficient that the current system.
 * This is something that can be tested extensively later on.
 * 
 * This will be great for location-based surveys actually.
 */

/**
 * Tracks the subject's location and writes location data points to the
 * database.
 *
 * @author Austin Walker
 */
public class LocationTracker extends BroadcastReceiver
{	
	/** Config key: have the location tracking times been coalesced? */
	public static final String TIMES_COALESCED = "times_coalesced";
	
	/** logging tag */
	private static final String TAG = "LocationTracker";
	
	//prefs keys
	private static final String LATEST = TAG + "_latest_";
	private static final String LAT = "lat";
	private static final String LONG = "long";
	private static final String ACC = "accuracy";
	public static final String TIMES_SENT = TAG +"_times_sent";
	
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
	public void onReceive(Context ctxt, Intent intent)
	{
		//code adapted from https://github.com/commonsguy/cwac-locpoll
		Location loc = (Location) intent.getExtras().get(LocationPoller.EXTRA_LOCATION);
        String msg;

        if (loc == null)
        {
            msg = intent.getStringExtra(LocationPoller.EXTRA_ERROR);
        }
        else
        {
            msg = loc.toString();
            Config.putSetting(ctxt, TIMES_SENT, 0);
            Config.putSetting(ctxt, LATEST + LAT, (float) loc.getLatitude());
            Config.putSetting(ctxt, LATEST + LONG, (float) loc.getLongitude());
            Config.putSetting(ctxt, LATEST + ACC, (float) loc.getAccuracy());
        }

        if (msg == null)
        {
            Util.e(null, TAG, "Invalid broadcast received!");
            return;
        }
        
        Util.i(null, TAG, msg);
        if (!Config.getSetting(ctxt, TIMES_COALESCED, false)) coalesceTimes(ctxt);
        sendLocation(ctxt);
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
	private void uploadNow(Context c)
	{
		Intent comsIntent = new Intent(c, ComsService.class);
		comsIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
		comsIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
				ComsService.LOCATION_DATA);
		WakefulIntentService.sendWakefulWork(c, comsIntent);
	}
	
	/**
	 * Log the latest location information in the database.
	 */
	private void sendLocation(Context c)
	{
		//check that tracking is on
		if (!Config.getSetting(c, Config.TRACKING_LOCAL, true) ||
			!Config.getSetting(c, Config.TRACKING_SERVER,
						Config.TRACKING_SERVER_DEFAULT))
		{
			Util.d(null, TAG, "Tracking is disabled; sending null location");
			TrackingDBHandler tdbh = new TrackingDBHandler(c);
			tdbh.open();
			tdbh.writeLocation(0, 0, TRACKING_OFF, Util.currentTimeAdjusted() / 1000);
			tdbh.close();
			uploadNow(c);
			return;
		}
		
		//check to ensure that we're in a valid time
		boolean log = false;
		int numTimes = Config.getSetting(c, Config.NUM_TIMES_TRACKED, 0);
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
							c, Config.TRACKED_START + i, null));
					end = Integer.parseInt(Config.getSetting(
							c, Config.TRACKED_END + i, null));
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
			TrackingDBHandler tdbh = new TrackingDBHandler(c);
			tdbh.open();
			tdbh.writeLocation(0, 0, BAD_TIME, Util.currentTimeAdjusted() / 1000);
			tdbh.close();
			uploadNow(c);
			return;
		}
		
		//get the times sent
		int timesSent = Config.getSetting(c, TIMES_SENT, Integer.MAX_VALUE);
		
		//make sure we have a valid time
		if (timesSent > 1)
		{
			Util.d(null, TAG, "No valid location to send; sending null location");
			TrackingDBHandler tdbh = new TrackingDBHandler(c);
			tdbh.open();
			tdbh.writeLocation(0, 0, NO_LOCATION, Util.currentTimeAdjusted() / 1000);
			tdbh.close();
			uploadNow(c);
			return;
		}
		
		//get the actual location
		double latestLat = (double) Config.getSetting(c, LATEST + LAT, (float) NO_LOCATION);
        double latestLong = (double) Config.getSetting(c, LATEST + LONG, (float) NO_LOCATION);
        double latestAcc = (double) Config.getSetting(c, LATEST + ACC, (float) NO_LOCATION);
		
		//make sure we are in range
		int numLocs = Config.getSetting(c, Config.NUM_LOCATIONS_TRACKED, 0);
		if (numLocs != 0)
		{
			log = false;
			for (int i = 0; i < numLocs; i++)
			{
				//get the location information
				double thisLon = (double) Config.getSetting(
						c, Config.TRACKED_LONG + i, (float) -1.0);
				double thisLat = (double) Config.getSetting(
						c, Config.TRACKED_LAT + i, (float) -1.0);
				double thisRad = (double) Config.getSetting(
						c, Config.TRACKED_RADIUS + i, (float) -1.0);
				if (thisLon == -1.0 || thisLat == -1.0 || thisRad == -1.0)
				{
					throw new RuntimeException(
							"cannot find location tracking value "
							+ "for location index" + i);
				}
				
				//now see if the incoming location
				//is within a valid location
				float[] results = new float[1];
				Location.distanceBetween(latestLat, latestLong,
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
				TrackingDBHandler tdbh = new TrackingDBHandler(c);
				tdbh.open();
				tdbh.writeLocation(0, 0, OUT_OF_RANGE, Util.currentTimeAdjusted() / 1000);
				tdbh.close();
				uploadNow(c);
				return;
			}
		}
		
		Util.d(null, TAG, "Tracking is enabled, time is valid, and we have a location that is in range; logging");
		Util.v(null, TAG, "Lat: " + latestLat + ", long: " + latestLong);
		TrackingDBHandler tdbh = new TrackingDBHandler(c);
		tdbh.open();
		tdbh.writeLocation(latestLat, latestLong, latestAcc, Util.currentTimeAdjusted() / 1000);
		tdbh.close();
		uploadNow(c);
		timesSent++;
	}
	
	/**
	 * Fix the times so that they don't overlap
	 */
	private void coalesceTimes(Context c)
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
		Config.putSetting(c, TIMES_COALESCED, true);
	}
}