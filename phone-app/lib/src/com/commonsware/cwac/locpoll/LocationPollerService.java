/***
  Copyright (c) 2010 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package com.commonsware.cwac.locpoll;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

/**
 * Service providing the guts of the location polling
 * engine. Uses a WakeLock to ensure the CPU stays on while
 * the location lookup is going on. Handles both successful
 * and timeout conditions.
 * 
 * Those wishing to leverage this service should do so via
 * the LocationPoller class.
 */
public class LocationPollerService extends Service {
  private static final String LOCK_NAME_STATIC=
      "com.commonsware.cwac.locpoll.LocationPoller";
  private static final int TIMEOUT=120000; // two minutes
  private static volatile PowerManager.WakeLock lockStatic=null;
  private LocationManager locMgr=null;

  /**
   * Lazy-initializes the WakeLock when we first use it. We
   * use a partial WakeLock since we only need the CPU on,
   * not the screen.
   */
  synchronized private static PowerManager.WakeLock getLock(
                                                            Context context) {
    if (lockStatic==null) {
      PowerManager mgr=
          (PowerManager)context.getApplicationContext()
                               .getSystemService(Context.POWER_SERVICE);

      lockStatic=
          mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                          LOCK_NAME_STATIC);
      lockStatic.setReferenceCounted(true);
    }

    return(lockStatic);
  }

  /**
   * Called by LocationPoller to trigger a poll for the
   * location. Acquires the WakeLock, then starts the
   * service using the supplied Intent (setting the
   * component so routing always goes to the service).
   */
  public static void requestLocation(Context ctxt, Intent i) {
    String provider=i.getStringExtra(LocationPoller.EXTRA_PROVIDER);
    Intent toBroadcast=
        (Intent)i.getExtras().get(LocationPoller.EXTRA_INTENT);

    if (provider==null) {
      Log.e("LocationPoller", "Invalid Intent -- has no provider");
    }
    else if (toBroadcast==null) {
      Log.e("LocationPoller",
            "Invalid Intent -- has no Intent to broadcast");
    }
    else {
      getLock(ctxt).acquire();

      i.setClass(ctxt, LocationPollerService.class);

      ctxt.startService(i);
    }
  }

  /**
   * Obtain the LocationManager on startup
   */
  @Override
  public void onCreate() {
    locMgr=(LocationManager)getSystemService(LOCATION_SERVICE);
  }

  /**
   * No-op implementation as required by superclass
   */
  @Override
  public IBinder onBind(Intent i) {
    return(null);
  }

  /**
   * Validates the required extras (EXTRA_PROVIDER and
   * EXTRA_INTENT). If valid, updates the Intent to be
   * broadcast with the application's own package (required
   * to keep the broadcast within this application, so we do
   * not leak security information). Then, forks a
   * PollerThread to do the actual location lookup.
   * 
   * @return START_REDELIVER_INTENT to ensure we get the
   *         last request again
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    String provider=
        intent.getStringExtra(LocationPoller.EXTRA_PROVIDER);
    Intent toBroadcast=
        (Intent)intent.getExtras().get(LocationPoller.EXTRA_INTENT);

    toBroadcast.setPackage(getPackageName());

    new PollerThread(getLock(this), locMgr, provider, toBroadcast)
                                                                  .start();

    return(START_REDELIVER_INTENT);
  }

  /**
   * A WakefulThread subclass that knows how to look up the
   * current location, plus handle the timeout scenario.
   */
  private class PollerThread extends WakefulThread {
    private LocationManager locMgr=null;
    private String provider=null;
    private Intent intentTemplate=null;
    private Runnable onTimeout=null;
    private LocationListener listener=new LocationListener() {
      /**
       * If we get a fix, get rid of the timeout condition,
       * then attach the location as an extra
       * (EXTRA_LOCATION) on the Intent, broadcast it, then
       * exit the polling loop so the thread terminates.
       */
      public void onLocationChanged(Location location) {
        handler.removeCallbacks(onTimeout);
        Intent toBroadcast=new Intent(intentTemplate);

        toBroadcast.putExtra(LocationPoller.EXTRA_LOCATION, location);
        sendBroadcast(toBroadcast);
        quit();
      }

      public void onProviderDisabled(String provider) {
        // required for interface, not used
      }

      public void onProviderEnabled(String provider) {
        // required for interface, not used
      }

      public void onStatusChanged(String provider, int status,
                                  Bundle extras) {
        // required for interface, not used
      }
    };
    private Handler handler=new Handler();

    /**
     * Constructor.
     * 
     * @param lock
     *          Already-locked WakeLock
     * @param locMgr
     *          LocationManager for doing the location
     *          lookup
     * @param provider
     *          name of the location provider to use
     * @param intentTemplate
     *          Intent to be broadcast when location found
     *          or timeout occurs
     */
    PollerThread(PowerManager.WakeLock lock, LocationManager locMgr,
                 String provider, Intent intentTemplate) {
      super(lock, "LocationPoller-PollerThread");

      this.locMgr=locMgr;
      this.provider=provider;
      this.intentTemplate=intentTemplate;
    }

    /**
     * Called before the Handler loop begins. Registers a
     * timeout, so we do not wait forever for a location.
     * When a timeout occurs, broadcast an Intent containing
     * an error extra, then terminate the thread. Also,
     * requests a location update from the LocationManager.
     */
    @Override
    protected void onPreExecute() {
      onTimeout=new Runnable() {
        public void run() {
          Intent toBroadcast=new Intent(intentTemplate);

          toBroadcast.putExtra(LocationPoller.EXTRA_ERROR, "Timeout!");
          toBroadcast.putExtra(LocationPoller.EXTRA_LASTKNOWN,
                               locMgr.getLastKnownLocation(provider));
          sendBroadcast(toBroadcast);
          quit();
        }
      };

      handler.postDelayed(onTimeout, TIMEOUT);
      locMgr.requestLocationUpdates(provider, 0, 0, listener);
    }

    /**
     * Called when the Handler loop ends. Removes the
     * location listener.
     */
    @Override
    protected void onPostExecute() {
      locMgr.removeUpdates(listener);

      super.onPostExecute();
    }

    /**
     * Called when the WakeLock is completely unlocked.
     * Stops the service, so everything shuts down.
     */
    @Override
    protected void onUnlocked() {
      stopSelf();
    }
  }
}
