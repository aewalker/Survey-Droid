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
import android.os.Bundle;
import android.util.Log;

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
	
	//logging tag
	private static final String TAG = "LocationTracker";

	LocationTracker(Context ctxt)
	{
		this.ctxt = ctxt;
	}

	@Override
	public void onLocationChanged(Location loc)
	{
		TrackingDBHandler tdbh = new TrackingDBHandler(ctxt);
		tdbh.openWrite();
		tdbh.writeLocation(
				loc.getLatitude(), loc.getLongitude(), loc.getTime());
		tdbh.close();
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		if (Config.D) Log.d(TAG, "GPS disabled!");
		/* Turns out, it's actually not possible to turn on the GPS
		 * programatically without exploiting some pretty serious security
		 * flaws in the Android system.  Since it's probably not a good idea
		 * to write stuff that relies on bugs that are likely to get fixed,
		 * all we can do here is ask the user to turn it back on.
		 */
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		//nothing to do here =)
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		
	}
}
