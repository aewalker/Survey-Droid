package com.peoples.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootIntentReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, BootService.class));
        context.startService(new Intent(context, CallLogService.class));
    }
}
