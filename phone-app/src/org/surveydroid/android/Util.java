/*---------------------------------------------------------------------------*
 * Util.java                                                                 *
 *                                                                           *
 * Contains some general use functions that don't fit in elsewhere.          *
 *---------------------------------------------------------------------------*
 * Copyright 2011 Sema Berkiten, Vladimir Costescu, Henry Liu, Diego Vargas, *
 * Austin Walker, and Tony Xiao                                              *
 *                                                                           *
 * This file is part of Survey Droid.                                        *
 *                                                                           *
 * Survey Droid is free software: you can redistribute it and/or modify      *
 * it under the terms of the GNU General Public License as published by      *
 * the Free Software Foundation, either version 3 of the License, or         *
 * (at your option) any later version.                                       *
 *                                                                           *
 * Survey Droid is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of            *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             *
 * GNU General Public License for more details.                              *
 *                                                                           *
 * You should have received a copy of the GNU General Public License         *
 * along with Survey Droid.  If not, see <http://www.gnu.org/licenses/>.     *
 *****************************************************************************/
package org.surveydroid.android;


import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Contains static, general use functions.
 * 
 * @author Austin Walker
 */
public final class Util
{
	/** tag name for the whole app */
	private static final String APP_TAG = "SurveyDroid";
	
	/** tag name for this class */
	private static final String TAG = "Util";
	
	/** if true, logs all messages using the application tag */
	private static final boolean USE_APP_TAG = true;
	
	/** Prevent all subclassing and instantiation */
	private Util()
	{
		throw new AssertionError("Tried to instantiate Util");
	}
	
	/**
	 * Get the day of the week from {@link Calendar} that corresponds to the
	 * day given as a string.
	 * 
	 * @param day - the day as a string from the set {Sun, Mon, Tue,
	 * Wed, Thu, Fri, Sat}
	 * @return the number for that day, as in {@link Calendar#get(int)}
	 */
	public static int getDay(String day)
	{
		if (day.equals("Sun")) return Calendar.SUNDAY;
		else if (day.equals("Mon")) return Calendar.MONDAY;
		else if (day.equals("Tue")) return Calendar.TUESDAY;
		else if (day.equals("Wed")) return Calendar.WEDNESDAY;
		else if (day.equals("Thu")) return Calendar.THURSDAY;
		else if (day.equals("Fri")) return Calendar.FRIDAY;
		else if (day.equals("Sat")) return Calendar.SATURDAY;
		else throw new IllegalArgumentException("Bad day: " + day);
	}
	
	/**
	 * Get the Unix timestamp of the next occurrence of the given day/time
	 * 
	 * @param day - the day of the week as in {@link Config#DAY_FORMAT}
	 * @param time - the time of day as in {@link Config#TIME_FORMAT}
	 * @return Unix timestamp in milliseconds
	 */
	public static long getUnixTime(String day, String time)
	{
		return getUnixTime(day, time, System.currentTimeMillis());
	}
	
	/**
	 * Get the Unix timestamp of the next occurrence of the given day/time
	 * 
	 * @param day - the day of the week as in {@link Config#DAY_FORMAT}
	 * @param time - the time of day as in {@link Config#TIME_FORMAT}
	 * @param base - the base time; all returned times must be after this time
	 * @return Unix timestamp in milliseconds
	 */
	public static long getUnixTime(String day, String time, long base)
	{
		//first, get the day right
		Calendar now = Calendar.getInstance(TimeZone.getDefault(), Locale.US);
		int targetDay = getDay(day);
		while (now.get(Calendar.DAY_OF_WEEK) != targetDay)
		{
			now.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		//do some parsing now to get the time
		int hours;
		int mins;
		if (time.length() == 3) time = "0" + time;
		try
		{
			if (time.length() != 4) throw new RuntimeException();
			hours = Integer.parseInt(time.substring(0, 2));
			mins = Integer.parseInt(time.substring(2, 4));
			now.set(Calendar.HOUR_OF_DAY, hours);
			now.set(Calendar.MINUTE, mins);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Invalid time string: " + time);
		}
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		long returnTime = now.getTimeInMillis();
		
		//make sure we're not behind the base time
		if (returnTime < base)
		{
			now.roll(Calendar.DAY_OF_YEAR, 7);
			returnTime = now.getTimeInMillis();
		}
		Util.d(null, TAG, "Time difference: " + 
				(returnTime - System.currentTimeMillis()));
		return returnTime;
	}
	
	/**
	 * Cleans up a phone number so that it contains no non-numerical chars.
	 * 
	 * @param number - the number to clean up
	 * @return number without non-numerical characters
	 */
	public static String cleanPhoneNumber(String number)
	{
		StringBuilder sb = new StringBuilder();
		for (char c : number.toCharArray())
		{
			switch(c)
			{
			case '0': case '1': case '2': case '3': case '4':
			case '5': case '6': case '7': case '8': case '9':
				sb.append(c);
			default:
			}
		}
		if (sb.length() == 10) sb.append(Config.COUNTRY_CODE);
		return sb.toString();
	}
	
	/*-----------------------------------------------------------------------*/
	
	/*
	 * Use the logging methods below so that when debugging is off, we don't
	 * pollute the log with a bunch of different tags.  In addition, when
	 * debugging is on, print more information and show toasts to allow
	 * monitoring the application without using logcat.
	 */
	
	/**
	 * Wrapper of {@link Log#e(String, String)}.
	 * 
	 * @param c - pass null to not show a toast
	 * @param tag
	 * @param msg
	 */
	public static void e(Context c, String tag, String msg)
	{
		if (Config.D)
		{
			if (c != null) toast(c, tag + " :: " + msg);
			if (USE_APP_TAG)
				Log.e(APP_TAG, tag + " :: " + msg);
			else
				Log.e(tag, msg);
			log(msg);
		}
		else
		{
			Log.e(APP_TAG, msg);
		}
	}
	
	/**
	 * Wrapper of {@link Log#w(String, String)}.
	 * 
	 * @param c - pass null to not show a toast
	 * @param tag
	 * @param msg
	 */
	public static void w(Context c, String tag, String msg)
	{
		if (Config.D)
		{
			if (c != null) toast(c, tag + " :: " + msg);
			if (USE_APP_TAG)
				Log.w(APP_TAG, tag + " :: " + msg);
			else
				Log.w(tag, msg);
			log(msg);
		}
		else
		{
			Log.w(APP_TAG, msg);
		}
	}
	
	/**
	 * Wrapper of {@link Log#i(String, String)}.
	 * 
	 * @param c - pass null to not show a toast
	 * @param tag
	 * @param msg
	 */
	public static void i(Context c, String tag, String msg)
	{
		if (Config.D)
		{
			if (c != null) toast(c, tag + " :: " + msg);
			if (USE_APP_TAG)
				Log.w(APP_TAG, tag + " :: " + msg);
			else
				Log.w(tag, msg);
			log(msg);
		}
		else
		{
			Log.w(APP_TAG, msg);
		}
	}
	
	/**
	 * Wrapper of {@link Log#d(String, String)}.
	 * 
	 * @param c - pass null to not show a toast
	 * @param tag
	 * @param msg
	 */
	public static void d(Context c, String tag, String msg)
	{
		if (Config.D)
		{
			if (c != null) toast(c, tag + " :: " + msg);
			if (USE_APP_TAG)
				Log.w(APP_TAG, tag + " :: " + msg);
			else
				Log.w(tag, msg);
			log(msg);
		}
	}
	
	/**
	 * Wrapper of {@link Log#v(String, String)}.
	 * 
	 * @param c - pass null to not show a toast
	 * @param tag
	 * @param msg
	 */
	public static void v(Context c, String tag, String msg)
	{
		if (Config.D)
		{
			if (c != null) toast(c, tag + " :: " + msg);
			if (USE_APP_TAG)
				Log.w(APP_TAG, tag + " :: " + msg);
			else
				Log.w(tag, msg);
			log(msg);
		}
	}
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Extract data from an exception so it can be easily printed.  Prints the
	 * first three lines of the stack trace.  For better control, see
	 * {@link #fmt(Exception, int)}.  If in debugging mode, prints out a full
	 * stack trace to the log.
	 * 
	 * @param e
	 */
	public static String fmt(Exception e)
	{
		return fmt(e, 3);
	}
	
	/**
	 * Extract data from an exception so it can be easily printed.  Prints the
	 * the given number of lines of the stack trace.  If in debugging mode,
	 * prints out a full stack trace to the log.
	 * 
	 * @param e
	 * @param numLines - the number of stack trace lines to print
	 */
	public static String fmt(Exception e, int numLines)
	{
		if (Config.D)
		{
			log(e.toString());
			StackTraceElement[] items = e.getStackTrace();
			for (int i = 0; i < items.length; i++)
			{
				log("\tat " + items[i].toString());
			}
			Throwable c = e.getCause();
			if (c != null)
			{
				log("Caused by " + c.toString());
				StackTraceElement[] cItems = c.getStackTrace();
				for (int i = 0; i < cItems.length; i++)
				{
					log("\tat " + cItems[i].toString());
				}
			}
		}
		StackTraceElement[] items = e.getStackTrace();
		String trace = "";
		for (int i = 0; i < numLines && i < items.length; i++)
			trace = trace + " :: " + items[i].toString();
		Throwable c = e.getCause();
		if (c != null)
		{
			trace = trace + " CAUSED BY " + c.toString();
			StackTraceElement[] cItems = c.getStackTrace();
			for (int i = 0; i < numLines && i < cItems.length; i++)
			{
				trace = trace + " :: " + cItems[i].toString();
			}
		}
		return e.toString() + trace;
	}
	
	/**
	 * Logs the given message
	 * 
	 * @param msg
	 */
	private static void log(String msg)
	{
		/*
		 * TODO
		 * This method is part of an old scheme to write our own log.  Although
		 * that effort turned out badly, it might be useful to have a function
		 * through which all log messages are sent, so we'll keep this around. 
		 */
	}
	
	/**
	 * Tries to show a toast message
	 * 
	 * @param c
	 * @param msg
	 */
	private static void toast(Context c, final String msg)
	{
		Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * @return the current time in millis adjusted for the current timezone
	 */
	public static long currentTimeAdjusted()
	{
		long time = System.currentTimeMillis();
		TimeZone tz = TimeZone.getDefault();
		return time + tz.getOffset(time);
	}
}