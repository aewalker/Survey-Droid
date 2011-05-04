package com.peoples.android;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    
    Context ctx;
    
    public Settings(Context ctx) {
        this.ctx = ctx;
    }

    public void setLocationService(boolean enabled) {
        putBoolean("locationOn", enabled);
    }
    
    public void setCallLocService(boolean enabled) {
        putBoolean("callLogOn", enabled);
    }
    
    public boolean isLocationEnabled() {
        return getBoolean("locationOn", true);
    }
    
    public boolean isCallLogEnabled() {
        return getBoolean("callLogOn", true);
    }
    
    private void putBoolean(String key, boolean value) {
        SharedPreferences settings = ctx.getSharedPreferences("com.peoples.settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    
    private boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences settings = ctx.getSharedPreferences("com.peoples.settings", 0);
        return settings.getBoolean(key, defaultValue);
    }
}
