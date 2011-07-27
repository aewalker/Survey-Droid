/*---------------------------------------------------------------------------*
 * Location Tracker.java                                                     *
 *                                                                           *
 * Logs the subject's locations.                                             *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import org.peoples.android.database.TrackingDBHandler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

//TODO
/*
 * It's possible that this class could benefit by using proximity alarms,
 * which are a function of the LocationManager.  Battery life could be extended
 * quite a bit if they are somehow more efficient that the current system.
 * This is something that can be tested extensively later on.
 */

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
		"org.peoples.android.ACTION_START_TRACKING";
	
	//switch tracking on or off, depending on its current state
	private static final String ACTION_TOGGLE_TRACKING =
		"org.peoples.android.ACTION_TOGGLE_TRACKING";
	
	//logging tag
	private static final String TAG = "LocationTrackerService";
	
	//is tracking currently running?
	private boolean isTracking = false;
	
	private LocationTracker lt = new LocationTracker();
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid)
	{
		handleIntent(intent);
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
		}
		else if (action.equals(ACTION_START_TRACKING))
		{
			schedule();
		}
		else
		{
			Log.w(TAG, "Unknown intent action: " + action);
			if (Config.D) throw new
				RuntimeException("Unknown intent action: " + action);
		}
	}
	
	private class TimePeriod implements Comparable<TimePeriod>
	{
		long start;
		long end;
		
		public TimePeriod(long s, long e)
		{
			start = s;
			end = e;
		}
		
		//does this time period contain a given time?
		public boolean contains(long time)
		{
			if (time >= start && time <= end) return true;
			return false;
		}

		@Override
		public int compareTo(TimePeriod that)
		{
			if (this.start < that.start) return 1;
			if (this.start > that.start) return -1;
			return 0;
		}
	}
	
	//schedules alarms to turn tracking on and off
	private void schedule()
	{
		//some setup stuff
		AlarmManager as = (AlarmManager)
			this.getSystemService(ALARM_SERVICE);
		//Yes, there supposed to be 8 days.  See the rescheduling code
		//later on.
		String[] days =
			{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
		Calendar cal = Calendar.getInstance();
		
		//get the number of times we're looking at
		int numTimes = Config.getSetting(this,
				Config.NUM_TIMES_TRACKED, 0);
		if (numTimes == 0)
		{
			lt.start();
			isTracking = true;
		}
		else
		{
			String day = days[cal.get(Calendar.DAY_OF_WEEK) - 1];
			
			TimePeriod[] times = new TimePeriod[numTimes];
			for (int i = 0; i < numTimes; i++)
			{
				String start =
					Config.getSetting(this, Config.TRACKED_START + i, null);
				String end =
					Config.getSetting(this, Config.TRACKED_END + i, null);
				if (start == null || end == null) throw new RuntimeException(
						"start or end time is null for time " + i);
				long startTime = Util.getUnixTime(day, start);
				long endTime = Util.getUnixTime(day, end);
				times[i] = new TimePeriod(startTime, endTime);
			}
			Arrays.sort(times);
			
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
			
			//now we have the final collection of times that don't overlap
			Intent toggleIntent =
				new Intent(this, LocationTrackerService.class);
			toggleIntent.setAction(ACTION_TOGGLE_TRACKING);
			PendingIntent pendingToggle = PendingIntent.getService(
					getApplicationContext(), 0, toggleIntent, 0);
			for (TimePeriod time : finalTimes)
			{
				//TODO One thing to improve here is to filter out times that
				//are in the past.  Since there will likely be very few time
				//periods, it's not a big deal right now.
				as.set(AlarmManager.RTC_WAKEUP, time.start, pendingToggle);
				as.set(AlarmManager.RTC_WAKEUP, time.end, pendingToggle);
			}
		}
		
		//finally, reschedule this for the next day
		Intent rescheduleIntent =
			new Intent(this, LocationTrackerService.class);
		rescheduleIntent.setAction(ACTION_START_TRACKING);
		PendingIntent pendingReschedule = PendingIntent.getService(
				getApplicationContext(), 0, rescheduleIntent, 0);
		String nextDay = days[cal.get(Calendar.DAY_OF_WEEK)];
		//this might not work very well
		as.set(AlarmManager.RTC_WAKEUP, Util.getUnixTime(nextDay, "0000"),
				pendingReschedule);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	//hack to get parent object
	private LocationTrackerService getThis() { return this; }

	private class LocationTracker implements LocationListener
	{
		//is the app using network location updates?
		private boolean networkMode = false;
		
		//has the location tracker been started?
		private boolean started = false;
	
		//logging tag
		private static final String TAG = "LocationTracker";
		
		/** Starts the location tracker. */
		public void start()
		{
			if (started) throw new RuntimeException("already started!");
			started = true;
			LocationManager lm = (LocationManager)
				getThis().getSystemService(Context.LOCATION_SERVICE);
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				Config.getSetting(getThis(), Config.LOCATION_INTERVAL,
				Config.LOCATION_INTERVAL_DEFAULT) * 60 * 1000, 0, this);
		}
		
		/** Stops the location tracker. */
		public void stop()
		{
			if (!started)
				throw new RuntimeException("can't stop; not started!");
			started = false;
			LocationManager lm = (LocationManager)
				getThis().getSystemService(Context.LOCATION_SERVICE);
			lm.removeUpdates(this);
		}
	
		@Override
		public void onLocationChanged(Location loc)
		{
			if (Config.getSetting(getThis(), Config.TRACKING_LOCAL, true) &&
				Config.getSetting(getThis(), Config.TRACKING_SERVER,
							Config.TRACKING_SERVER_DEFAULT))
			{
				double lat = loc.getLatitude();
				double lon = loc.getLongitude();
				int numLocs = Config.getSetting(getThis(),
						Config.NUM_LOCATIONS_TRACKED, 0);
				boolean log = false;
				if (numLocs >= 1)
				{
					for (int i = 0; i < numLocs; i++)
					{
						//for each location tracked, get the information
						//for that location and validate it
						double thisLon = (double) Config.getSetting(getThis(),
								Config.TRACKED_LONG + i, (float) -1.0);
						double thisLat = (double) Config.getSetting(getThis(),
								Config.TRACKED_LAT + i, (float) -1.0);
						double thisRad = (double) Config.getSetting(getThis(),
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
				if (log)
				{
					TrackingDBHandler tdbh = new TrackingDBHandler(getThis());
					tdbh.openWrite();
					tdbh.writeLocation(lat, lon, loc.getAccuracy(),
							loc.getTime());
					tdbh.close();
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
			if (provider.equals(LocationManager.GPS_PROVIDER))
			{
				Log.i(TAG, "GPS disabled, falling back to network");
				networkMode = true;
				
				LocationManager lm = (LocationManager)
					getThis().getSystemService(Context.LOCATION_SERVICE);
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					Config.getSetting(getThis(), Config.LOCATION_INTERVAL,
        			Config.LOCATION_INTERVAL_DEFAULT) * 60 * 1000, 0, this);
			}
		}
	
		@Override
		public void onProviderEnabled(String provider)
		{
			//switch back to GPS if it comes back online
			if (provider.equals(LocationManager.GPS_PROVIDER))
			{
				Log.i(TAG, "GPS re-enabled, "
						+ "switching back from network updates");
				networkMode = false;
				
				stop();
				start();
			}
		}
	
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			//basically, if the gps goes out of service (or comes back in),
			//treat it like it has been disabled (or re-enabled)
			if (provider.equals(LocationManager.GPS_PROVIDER))
			{
				if (status == LocationProvider.OUT_OF_SERVICE
						&& networkMode == false)
				{
					onProviderDisabled(provider);
				}
				
				if (status == LocationProvider.AVAILABLE
						&& networkMode == true)
				{
					onProviderEnabled(provider);
				}
			}
		}
	}
}