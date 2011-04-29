package com.peoples.android;

import com.peoples.android.services.BootService;
import com.peoples.android.services.CallLogService;
import com.peoples.android.services.GPSLocationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;




public class BootIntentReceiver extends BroadcastReceiver {
    
	protected static final String TAG = "BootIntentReceiver";
	protected static final boolean D = true;
	
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	if(D) Log.e(TAG, "oh hi, in BootIntentReceiver");
    	
//    	before (doesn't work):
//    	new Intent(context, BootService.class)
    	
//    	after (works):
    	Intent bootIntent = new Intent();
    	bootIntent.setAction("com.peoples.android.BootService");
    	bootIntent.setClass(context, BootService.class);
        context.startService(bootIntent);
        
        
        context.startService(new Intent(context, CallLogService.class));
        context.startService(new Intent(context, GPSLocationService.class));
    }
}
