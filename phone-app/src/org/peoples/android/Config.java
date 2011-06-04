/*---------------------------------------------------------------------------*
 * Config.java                                                               *
 *                                                                           *
 * Holds configuration things (such as whether or not debugging is enabled.  *
 *---------------------------------------------------------------------------*/
//TODO I believe there is some specific way to do this with Java. -Austin

package org.peoples.android;

/**
 * Holds static information about the current configuration, such as whether or
 * not debugging is enabled, etc.
 * 
 * @author Austin Walker
 */
public class Config
{
	/** Is debugging enabled? */
	public static final boolean D = true;
	
	/** Frequency with which to run the survey scheduler (in minutes) */
	public static final long SCHEDULER_INTERVAL = 5;
	//public static final int SCHEDULER_INTERVAL = 60 * 24;
	
	/** Frequency with which to push data (in minutes) */
	public static final long PUSH_INTERVAL = 5;
	//public static final int PUSH_INTERVAL = 60 * 24;
	
	/** Frequency with which to pull data (in minutes) */
	public static final long PULL_INTERVAL = 60 * 24;
	
	/** Format of survey times. */
	public static final String TIME_FORMAT = "HHmm";
	
	/** Format of survey days. */
	public static final String DAY_FORMAT = "EE";
	
	/** Server to connect to */
	public static final String SERVER =
		"ec2-50-19-132-198.compute-1.amazonaws.com";
	
	/** Salting value for hashing phone number */
	public static final String SALT =
		"oi234509gweamFastbkewp09tj2g3o2igfh90";

	/** Approximate time between location updates, in minutes */
	public static int LOCATION_INTERVAL = 15;
}