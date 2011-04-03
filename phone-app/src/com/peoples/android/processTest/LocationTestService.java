/**
 * 
 */
package com.peoples.android.processTest;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

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
		// TODO Auto-generated method stub
		
		//ok find out how to get location here 
		
		//find out how to send location here
		
		//then close anything that needs to be closed.
		
	}

	
	
	
	
	


}
