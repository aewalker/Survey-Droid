/*---------------------------------------------------------------------------*
 * TakenDBHandler.java                                                       *
 *                                                                           *
 * Contains methods to log when data about surveys that have been taken or   *
 * were scheduled but not taken.  Also contains methods to get the current   *
 * survey completion rate.                                                   *
 *---------------------------------------------------------------------------*/
package org.peoples.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.peoples.android.Config;

/**
 * Provides the database read/write methods needed to track how surveys are
 * being completed.
 *
 * @author Austin Walker
 */
public class TakenDBHandler extends PeoplesDBHandler
{
	//logging tag
	private static final String TAG = "SurveysTakenDBHandler";
	
	/**
	 * Returned from {@link getCompletionRate} if there have not been any
	 * surveys taken
	 */
	public static final int NO_PERCENTAGE = -1;

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public TakenDBHandler(Context context)
	{
		super(context);
	}

	/**
	 * Write an entry for a survey's completion (or not) to the database.
	 * 
	 * @param survey_id - the survey's id
	 * @param code - the code from {@link PeoplesDB.TakenTable} describing
	 * the type of survey and its completion status
	 * @param created - when the surveys was completed/dismissed/etc.
	 * @return true on success
	 */
	public boolean writeSurvey(int survey_id, int code, long created)
	{
		if (Config.D) Log.d(TAG, "Writing survey code: survey "
				+ survey_id + " marked " + code);
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(PeoplesDB.TakenTable.SURVEY_ID, survey_id);
		values.put(PeoplesDB.TakenTable.STATUS, code);
		values.put(PeoplesDB.TakenTable.CREATED, created);
		
		//run it
		if (db.insert(PeoplesDB.TAKEN_TABLE_NAME, null, values) == -1)
			return false;
		return true;
	}
	
	/**
	 * Get the current completion rate for surveys.  Looks at the last few
	 * entries (the exact number is controlled by
	 * {@link Config.COMPLETION_SAMPLE}) to determine this.
	 * 
	 * @return the percentage completed between 0 and 100, or
	 * {@link NO_PERCENTAGE} if there are no suveys to go by 
	 */
	public int getCompletionRate()
	{
		if (Config.D) Log.d(TAG, "Getting survey completion rate");
		
		//set up the query
		String    table    = PeoplesDB.TAKEN_TABLE_NAME;
		String[]  cols     = {PeoplesDB.TakenTable.STATUS};
		String    selc     = null;
		String[]  selcArgs = null;
		String    group    = null;
		String    having   = null;
		String    orderBy  = PeoplesDB.TakenTable.CREATED + " DESC";
		
		//run it
		Cursor result =
			db.query(table, cols, selc, selcArgs, group, having, orderBy);
		if (result.getCount() == 0) return NO_PERCENTAGE;
		result.moveToFirst();
		
		int size = Config.getSetting(contx, Config.COMPLETION_SAMPLE,
				Config.COMPLETION_SAMPLE_DEFAULT);
		int numCompleted = 0;
		int i = 0;
		while (i < size)
		{
			if (result.isAfterLast()) break;
			int code = result.getInt(
					result.getColumnIndexOrThrow(PeoplesDB.TakenTable.STATUS));
			result.moveToNext();
			if (code == PeoplesDB.TakenTable.SURVEYS_DISABLED_SERVER) continue;
				
			if (countsAsCompleted(code))
				numCompleted++;
			i++;
		}
		result.close();
		return numCompleted * (100 / i);
	}
	
	//does the given survey completion status code count as being completed?
	private static boolean countsAsCompleted(int code)
	{
		switch (code)
		{
		case PeoplesDB.TakenTable.SURVEYS_DISABLED_LOCALLY:
    	case PeoplesDB.TakenTable.SURVEYS_DISABLED_SERVER:
    	case PeoplesDB.TakenTable.SCHEDULED_UNFINISHED:
    	case PeoplesDB.TakenTable.SCHEDULED_DISMISSED:
    	case PeoplesDB.TakenTable.SCHEDULED_IGNORED:
    	case PeoplesDB.TakenTable.RANDOM_UNFINISHED:
    	case PeoplesDB.TakenTable.RANDOM_DISMISSED:
    	case PeoplesDB.TakenTable.RANDOM_IGNORED:
    		return false;
    	case PeoplesDB.TakenTable.USER_INITIATED_FINISHED:
    	case PeoplesDB.TakenTable.USER_INITIATED_UNFINISHED:
    	case PeoplesDB.TakenTable.SCHEDULED_FINISHED:
    	case PeoplesDB.TakenTable.RANDOM_FINISHED:
    		return true;
    	default:
    		Log.w(TAG, "Unknown survey completion code: " + code);
    		if (Config.D) throw new
    			RuntimeException("Unknown survey completion code: " + code);
    		return false;
		}
	}
}
