/*---------------------------------------------------------------------------*
 * Util.java                                                                 *
 *                                                                           *
 * Contains some general use functions that don't fit in elsewhere.          *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Contains static, general use functions.
 * 
 * @author Austin Walker
 */
public final class Util
{
	/**
	 * Get the Unix timestamp of the next occurrence of the given day/time
	 * 
	 * @param day - the day of the week as in Config.DAY_FORMAT
	 * @param time - the time of day as in Config.TIME_FORMAT
	 * @return Unix timestamp in miliseconds
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
}