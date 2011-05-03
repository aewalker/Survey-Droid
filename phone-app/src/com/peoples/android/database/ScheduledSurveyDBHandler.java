package com.peoples.android.database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;


/**
 * 
 * Handles database interactions with the ScheduledSurveys table.
 * 
 * @author Diego Vargas
 *
 */
public class ScheduledSurveyDBHandler extends PeoplesDBHandler {
	
	//for writing to the log
	private static final String TAG = "ScheduledSurveyDBHandler";
	private static final boolean D = true;

	/**
	 * Create a ScheduledSurveyDBHandler object
	 * 
	 * @param context
	 */
	public ScheduledSurveyDBHandler(Context context) {
		super(context);
	}
	
	
	/**
	 * 
	 * Every survey that is scheduled gets a row in the ScheduledSurveys table.
	 * Once the survey is administered the corresponding entry is removed from 
	 * the database.
	 * 
	 * @return returns a cursor which points to rows of previously scheduled
	 * 			surveys
	 */
	public Cursor getScheduledSurveys() {
		
		if(D) Log.d(TAG, "getting previously scheduled surveys");
		
		String table = PeoplesDB.SS_TABLE_NAME;
		
		String[] columns 	= {PeoplesDB.ScheduledSurveys._ID,
								PeoplesDB.ScheduledSurveys.SURVEY_ID,
								PeoplesDB.ScheduledSurveys.ORIGINAL_TIME};
		
		String selection 	= PeoplesDB.ScheduledSurveys.SKIPPED + " = ?";
		
		String[] selArgs 	= {Boolean.toString(false)};
		
		String group  		= null;
		String having 		= null;
		String orderBy		= null;
		
		return db.query(table, columns, selection, selArgs,
							group, having, orderBy);
		
	}
	

}
