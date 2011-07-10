/*---------------------------------------------------------------------------*
 * Config.java                                                               *
 *                                                                           *
 * Holds configuration things (such as whether or not debugging is enabled.  *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;

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
	
	/** Is https enabled? */
	public static final boolean HTTPS = true;
	
	/** Frequency with which to run the survey scheduler (in minutes) */
	public static final long SCHEDULER_INTERVAL = 20;
	//public static final int SCHEDULER_INTERVAL = 60 * 24;

	/** Frequency with which to push data (in minutes) */
	public static final long PUSH_INTERVAL = 20;
	//public static final int PUSH_INTERVAL = 60 * 24;

	/** Frequency with which to pull data (in minutes) */
	public static final long PULL_INTERVAL = 60 * 24;

	/** Server to connect to */
	public static final String SERVER = "50.19.254.168";

	//TODO each phone should generate a unique salt string
	/** Salting value for hashing phone number */
	public static final String SALT =
		"oi234509gweamFastbkewp09tj2g3o2igfh90";

	/** Approximate time between location updates, in minutes */
	public static int LOCATION_INTERVAL = 15;

	/**
	 * Time to vibrate to warn user that a survey is ready in milliseconds.
	 * 
	 * @deprecated Use the built in constants.
	 */
    public static final long VIBRATION_TIME = 2000;

    /** Time to delay a survey for in minutes if the user chooses to. */
    public static final long SURVEY_DELAY = 15;

    /** Phone number of the study administrator. */
	public static final String ADMIN_PHONE_NUMBER = "7652996509"; //Austin cell

	/** Name of the study administrator. */
	public static final String ADMIN_NAME = "Austin";

	/** Should we allow the entry of blank free response answers? */
	public static final boolean ALLOW_BLANK_FREE_RESPONSE = true;

	/** Should we allow answering multiple choice with no answers? */
	public static final boolean ALLOW_NO_CHOICES = false;

	/** Should the name/title of a survey be shown? */
	public static final boolean SHOW_SURVEY_NAME = true;
	
	/** What format should voice be recorded int */
	public static final int VOICE_FORMAT =
		MediaRecorder.OutputFormat.MPEG_4;
		//MediaRecorder.OutputFormat.THREE_GPP;
	
	/**
	 * Should full resolution photos (that are possibly/likely quite large) be
	 * sent to the server when the user takes a photo with their survey? 
	 * 
	 * Note that due to an old bug on a small number of Android devices, it is
	 * possible that that photos will not be able to be sent if this is turned
	 * on.  Make sure to test on the device in use.
	 */
	public static final boolean USE_FULL_RES_PHOTOS = false;
	
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

    /**
     * Call this method to get the default communications protocol
     *
     * @return uri protocol to be used
     */
    public static String getComProtocol() {
    	if (HTTPS)
    		return "https";
    	return "http";
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
