package com.peoples.android;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Used to adjust privacy settings on phone
 * @author Tony
 * @author Henry Liu
 */
public class PeoplesConfig {
    
    Context ctxt;
    
    /**
     * Initialize the setting object. Settings are available application-wide
     * 
     * @param ctx - the application context 
     */
    public PeoplesConfig(Context ctxt) {
        this.ctxt = ctxt;
    }

    /**
     * Enable / disable location service
     * 
     * @param enabled - true to enable location service
     */
    public void setLocationService(boolean enabled) {
        putBoolean("locationOn", enabled);
    }
    
    /**
     * Enable / disable call log service
     * 
     * @param enabled - true to enable call log service
     */
    public void setCallLogService(boolean enabled) {
        putBoolean("callLogOn", enabled);
    }
    
    /**
     * Enable / disable survey service
     * 
     * @param enabled - true to enable survey service
     */
    public void setSurveyService(boolean enabled) {
        putBoolean("surveyOn", enabled);
    }
    
    /**
     * Check this method to make sure if location service should run
     * 
     * @return true if location is enabled
     */
    public boolean isLocationEnabled() {
        return getBoolean("locationOn", true);
    }
    
    /**
     * Check this method to make sure if call log service should run
     * 
     * @return true if call log is enabled
     */
    public boolean isCallLogEnabled() {
        return getBoolean("callLogOn", true);
    }
    
    /**
     * Check this method to make sure if survey service should run
     * 
     * @return true if survey is enabled
     */
    public boolean isSurveyEnabled() {
        return getBoolean("surveyOn", true);
    }
    
    /**
     * Private helper to persist key-value pairs 
     * 
     * @param key - key has to be string
     * @param value - this method only accepts boolean value
     */
    private void putBoolean(String key, boolean value) {
        SharedPreferences settings = ctxt.getSharedPreferences("com.peoples.settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    
    /**
     * Private helper to retrieve key-value pairs
     * 
     * @param key - key has to be string
     * @param defaultValue - value to return if no property identified by key is availabe
     * 
     * @return value is it is set, else the defaultValue
     */
    private boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences settings = ctxt.getSharedPreferences("com.peoples.settings", 0);
        return settings.getBoolean(key, defaultValue);
    }
}
