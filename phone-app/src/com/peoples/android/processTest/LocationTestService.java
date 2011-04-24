/**
 * 
 */
package com.peoples.android.processTest;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.app.Activity;
import android.content.Context;
import android.location.GpsStatus.Listener;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author diego
 * http://developer.android.com/guide/topics/fundamentals/services.html
 * above link explains why IntentService is easier to use. Works fine for demo
 * app
 * 
 * 
 */
public class LocationTestService extends IntentService {

	public LocationTestService(String name) {
		super("LocationTestService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
	}
	
	
}
