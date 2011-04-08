package com.peoples.android.services;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

public class BootService extends IntentService {

    public BootService() {
        super(BootService.class.getName());
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        //Toast.makeText(this, "BootService Started", Toast.LENGTH_LONG).show();
        
        long endTime = System.currentTimeMillis() + 30*1000;
        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    wait(endTime - System.currentTimeMillis());
                } catch (Exception e) {
                }
            }
        }
    }
    
}
