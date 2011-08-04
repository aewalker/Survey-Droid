/*---------------------------------------------------------------------------*
 * Util.java                                                                 *
 *                                                                           *
 * Contains some general use functions that don't fit in elsewhere.          *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
	//tag name for the whole app
	private static final String TAG = "PEOPLES";
	
	/** Location of the application log */
	public static final String LOGFILE =
		"/data/data/org.peoples.android/peoples_log";
	
	/**
	 * Get the Unix timestamp of the next occurrence of the given day/time
	 * 
	 * @param day - the day of the week as in Config.DAY_FORMAT
	 * @param time - the time of day as in Config.TIME_FORMAT
	 * @return Unix timestamp in milliseconds
	 */
	public static long getUnixTime(String day, String time)
	{
		SimpleDateFormat timeSDF = new SimpleDateFormat(Config.TIME_FORMAT);
		timeSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
		try
		{
			timeSDF.parse(time);
		}
		catch (ParseException e)
		{
			throw new RuntimeException("Invalid time: " + time);
		}
		
		SimpleDateFormat daySDF = new SimpleDateFormat(Config.DAY_FORMAT);
		daySDF.setTimeZone(TimeZone.getTimeZone("UTC"));
		try
		{
			daySDF.parse(day);
		}
		catch (ParseException e)
		{
			throw new RuntimeException("Invalid day: " + day);
		}
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK,
				daySDF.getCalendar().get(Calendar.DAY_OF_WEEK));
		cal.set(Calendar.HOUR_OF_DAY,
				timeSDF.getCalendar().get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, timeSDF.getCalendar().get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTimeInMillis();
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
			Log.e(tag, msg);
			log(msg);
		}
		else
		{
			Log.e(TAG, msg);
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
			Log.w(tag, msg);
			log(msg);
		}
		else
		{
			Log.w(TAG, msg);
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
			Log.i(tag, msg);
			log(msg);
		}
		else
		{
			Log.i(TAG, msg);
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
		SimpleDateFormat sdf = new SimpleDateFormat("MM.dd HH:mm:ss z");
		String time = sdf.format(Calendar.getInstance().getTime()); 
		try
		{
			FileOutputStream fos = new FileOutputStream(LOGFILE, true);
			fos.write(time.getBytes());
			fos.write(" | ".getBytes());
			fos.write(msg.getBytes());
			fos.write("\n".getBytes());
			fos.close();
		}
		catch (FileNotFoundException e)
		{
			Log.w(TAG, "Can't write to log; file not found");
			return;
		}
		catch (IOException e)
		{
			Log.w(TAG, "IO exception when trying to write to log");
			return;
		}
	}
	
	//tries to show a toast message
	private static void toast(Context c, final String msg)
	{
		//this doesn't always work, and there's no way to fix it -Austin
		Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
	}
}