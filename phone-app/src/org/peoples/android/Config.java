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
	public static final int SCHEDULER_INTERVAL = 5;
	//public static final int SCHEDULER_INTERVAL = 60 * 24;
	
	/** Format of survey times. */
	public static final String TIME_FORMAT = "HHmm";
	
	/** Format of survey days. */
	public static final String DAY_FORMAT = "EE";
}