package com.peoples.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class CoordinatorService extends IntentService {
	
	private static final String TAG = "CoordinatorService";
    private static final boolean D = true;

	public CoordinatorService() {
		super(CoordinatorService.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		//TODO: Implement logic that will get called regularly:
		//i.e. check on surveys, pull any new ones, push any responses, push GPS or call log data
		//check that everything is running smoothly, and report any errors to the server
		
		if(D) Log.e(TAG, "onHandleIntent");
		
		
	}

}
