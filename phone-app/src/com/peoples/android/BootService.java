package com.peoples.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class BootService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStart(Intent intent, int startId) {

        super.onStart(intent, startId);

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

    }
}
