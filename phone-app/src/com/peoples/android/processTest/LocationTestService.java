/**
 * 
 */
package com.peoples.android.processTest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * @author diego
 *
 */
public class LocationTestService extends Service {

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	/**
	 * Called when process is started. Returns START_STICKY to keep it going
	 */
	public int onStartCommand(Intent intent, int flags, int startId) {	
		return START_STICKY;
	}
	
	@Override
	/**
	 * Called when service is FIRST created, before onStartCommand
	 */
	public void onCreate() {
		
		return;
	}
	
	public void onDestroyed() {
		return;
	}
	
	
	
	
	


}
