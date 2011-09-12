/*---------------------------------------------------------------------------*
 * Util.java                                                                 *
 *                                                                           *
 * Contains some general use functions that don't fit in elsewhere.          *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.util.Log;
//import android.widget.Toast;

/**
 * Contains static, general use functions.
 * 
 * @author Austin Walker
 */
public final class Util
{
	//tag name for the whole app
	private static final String APP_TAG = "PEOPLES";
	
	//tag name for this class
	private static final String TAG = "Util";
	
	/** Location of the application log */
	public static final String LOGFILE =
		"/data/data/org.peoples.android/peoples_log";
	
	private static final boolean USE_APP_TAG = true;
	
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
		//first, get the day right
		Calendar now = Calendar.getInstance(TimeZone.getDefault(), Locale.US);
		now.setTimeInMillis(System.currentTimeMillis());
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
			hours = Integer.parseInt(time.substring(0, 2));
			mins = Integer.parseInt(time.substring(2, 4));
		}
		catch (Exception e)
		{
			throw new RuntimeException("Invalid time string: " + time);
		}
		now.set(Calendar.HOUR_OF_DAY, hours);
		now.set(Calendar.MINUTE, mins);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		long returnTime = now.getTimeInMillis();
		
		//account for the situation where the day desired is the same as the
		//current day, but the time is in the past
		if (returnTime < System.currentTimeMillis())
		{
			now.add(Calendar.DAY_OF_YEAR, 7);
			returnTime = now.getTimeInMillis();
		}
		Util.d(null, TAG, "Time difference: " + 
				(returnTime - System.currentTimeMillis()));
		return returnTime;
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
				Log.i(APP_TAG, tag + " :: " + msg);
			else
				Log.i(tag, msg);
			log(msg);
		}
		else
		{
			Log.i(APP_TAG, msg);
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
				Log.d(APP_TAG, tag + " :: " + msg);
			else
				Log.d(tag, msg);
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
				Log.v(APP_TAG, tag + " :: " + msg);
			else
				Log.v(tag, msg);
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
		for (int i = 0; i < numLines; i++)
			trace = trace + " :: " + items[i].toString();
		Throwable c = e.getCause();
		if (c != null)
		{
			trace = trace + " CAUSED BY " + c.toString();
			StackTraceElement[] cItems = c.getStackTrace();
			for (int i = 0; i < numLines; i++)
			{
				trace = trace + " :: " + cItems[i].toString();
			}
		}
		return e.toString() + trace;
	}
	
	//tries to log the message to the internal log
	private static void log(final String msg)
	{
//		SimpleDateFormat sdf = new SimpleDateFormat("MM.dd HH:mm:ss z");
//		String time = sdf.format(Calendar.getInstance().getTime()); 
//		try
//		{
//			FileOutputStream fos = new FileOutputStream(LOGFILE, true);
//			fos.write(time.getBytes());
//			fos.write(" | ".getBytes());
//			fos.write(msg.getBytes());
//			fos.write("\n".getBytes());
//			fos.close();
//		}
//		catch (FileNotFoundException e)
//		{
//			Log.w(TAG, "Can't write to log; file not found");
//			return;
//		}
//		catch (IOException e)
//		{
//			Log.w(TAG, "IO exception when trying to write to log");
//			return;
//		}
	}
	
	//tries to show a toast message
	private static void toast(Context c, final String msg)
	{
		//this doesn't always work, and there's no way to fix it -Austin
		//Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
	}
}