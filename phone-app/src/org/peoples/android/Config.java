/*---------------------------------------------------------------------------*
 * Config.java                                                               *
 *                                                                           *
 * Holds configuration things (such as whether or not debugging is enabled.  *
 *---------------------------------------------------------------------------*/

package org.peoples.android;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Holds static information about the current configuration, such as whether or
 * not debugging is enabled, etc.
 * 
 * @author Austin Walker
 * @author Tony Xaio
 * @author Henry Liu
 */
public class Config
{
	//application context
	private final Context ctxt;
	
	//manual settings; can't be changed programatically
	/** Is debugging enabled? */
	public static final boolean D = true;
	
	/** Format of survey times. */
	public static final String TIME_FORMAT = "HHmm";
	
	/** Format of survey days. */
	public static final String DAY_FORMAT = "EE";
	
	
	//TODO convert these to use shared preferences
	/** Frequency with which to run the survey scheduler (in minutes) */
	public static final long SCHEDULER_INTERVAL = 5;
	//public static final int SCHEDULER_INTERVAL = 60 * 24;
	
	/** Frequency with which to push data (in minutes) */
	public static final long PUSH_INTERVAL = 5;
	//public static final int PUSH_INTERVAL = 60 * 24;
	
	/** Frequency with which to pull data (in minutes) */
	public static final long PULL_INTERVAL = 60 * 24;
	
	/** Server to connect to */
	public static final String SERVER =
		"ec2-50-19-132-198.compute-1.amazonaws.com";
	
	/** Salting value for hashing phone number */
	public static final String SALT =
		"oi234509gweamFastbkewp09tj2g3o2igfh90";

	/** Approximate time between location updates, in minutes */
	public static int LOCATION_INTERVAL = 15;
	
	/**
	 * Initialize the setting object.  Settings are available application-wide.
	 * 
	 * @param ctxt - the application context
	 */
	public Config(Context ctxt)
	{
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
    
    //Private helper to persist key-value pairs 
    private void putBoolean(String key, boolean value) {
        SharedPreferences settings = ctxt.getSharedPreferences(
        		"peoples.conf", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    
    //Private helper to retrieve key-value pairs
    private boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences settings = ctxt.getSharedPreferences(
        		"peoples.conf", 0);
        return settings.getBoolean(key, defaultValue);
    }
}