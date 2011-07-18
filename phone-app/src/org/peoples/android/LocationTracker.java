/*---------------------------------------------------------------------------*
 * Location Tracker.java                                                     *
 *                                                                           *
 * Logs the subject's locations.                                             *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import org.peoples.android.database.TrackingDBHandler;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
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
public class LocationTracker implements LocationListener
{
	//application context
	private final Context ctxt;
	
	//is the app using network location updates
	private boolean networkMode = false;

	//logging tag
	private static final String TAG = "LocationTracker";

	/**
	 * Constructor.  Typically only one instance of this class should be
	 * created; it can be given to the location manager.
	 *
	 * @param ctxt - the application context
	 */
	public LocationTracker(Context ctxt)
	{
		this.ctxt = ctxt;
	}

	@Override
	public void onLocationChanged(Location loc)
	{
		if (Config.getSetting(ctxt, Config.TRACKING_LOCAL, true) &&
			Config.getSetting(ctxt, Config.TRACKING_SERVER,
						Config.TRACKING_SERVER_DEFAULT))
		{
			double lat = loc.getLatitude();
			double lon = loc.getLongitude();
			int numLocs = Config.getSetting(ctxt,
					Config.NUM_LOCATIONS_TRACKED, 0);
			boolean log = false;
			if (numLocs >= 1)
			{
				for (int i = 0; i < numLocs; i++)
				{
					//for each location tracked, get the information
					//for that location and validate it
					double thisLon = (double) Config.getSetting(ctxt,
							Config.TRACKED_LONG + i, (float) -1.0);
					double thisLat = (double) Config.getSetting(ctxt,
							Config.TRACKED_LAT + i, (float) -1.0);
					double thisRad = (double) Config.getSetting(ctxt,
							Config.TRACKED_RADIUS + i, (float) -1.0);
					if (thisLon == -1.0 || thisLat == -1.0 || thisRad == -1.0)
					{
						throw new RuntimeException("cannot find location "
								+ "tracking value for location index" + i);
					}
					
					//now see if the incoming location is with a valid location
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
			{ //assume that if there are no locations, then everything should
			  //be tracked (to track nowhere, just turn off tracking)
				log = true;
			}
			if (log)
			{
				TrackingDBHandler tdbh = new TrackingDBHandler(ctxt);
				tdbh.openWrite();
				tdbh.writeLocation(lat, lon, loc.getAccuracy(), loc.getTime());
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
		 * flaws in the Android system.  Since it's probably not a good idea
		 * to write stuff that relies on bugs that are likely to get fixed,
		 * all we can do here is to fall back to a different provider.
		 */
		if (provider.equals(LocationManager.GPS_PROVIDER))
		{
			Log.i(TAG, "GPS disabled, falling back to network");
			networkMode = true;
			
			LocationManager lm = (LocationManager)
				ctxt.getSystemService(Context.LOCATION_SERVICE);
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					Config.getSetting(ctxt, Config.LOCATION_INTERVAL,
        			Config.LOCATION_INTERVAL_DEFAULT) * 60 * 1000, 0, this);
		}
		
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		//switch back to GPS if it comes back online
		if (provider.equals(LocationManager.GPS_PROVIDER))
		{
			Log.i(TAG, "GPS re-enabled, switching back from network updates");
			networkMode = false;
			
			LocationManager lm = (LocationManager)
			ctxt.getSystemService(Context.LOCATION_SERVICE);
			lm.removeUpdates(this);
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				Config.getSetting(ctxt, Config.LOCATION_INTERVAL,
    			Config.LOCATION_INTERVAL_DEFAULT) * 60 * 1000, 0, this);
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
