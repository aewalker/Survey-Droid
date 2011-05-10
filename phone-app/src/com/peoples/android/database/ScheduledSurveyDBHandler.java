package com.peoples.android.database;

import com.peoples.android.model.SurveyIntent;

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
	 * the database. The columns of the cursor, in order, are:
	 * 
	 * ID : in the scheduled surveys table
	 * 
	 * survey ID: in the surveys table
	 * 
	 * original time: the time at which this survey was scheduled and its entry
	 * added to the scheduled survey table.
	 * 
	 * day: the time at which this schedule should run today
	 * 
	 * @param day Day of the week, as given by PeoplesDB.SurveyTable.(day)
	 * 
	 * @return returns a cursor which points to rows of previously scheduled
	 * 			surveys
	 */
	public Cursor getScheduledSurveys(String day) {
		
		if(D) Log.d(TAG, "getting previously scheduled surveys");
		
		String ss = PeoplesDB.SS_TABLE_NAME;
		String su = PeoplesDB.SURVEY_TABLE_NAME;
		
		
		String table = PeoplesDB.SS_TABLE_NAME+", "+PeoplesDB.SURVEY_TABLE_NAME;
		
		String[] columns 	= { ss+"."+PeoplesDB.ScheduledSurveys._ID,
								ss+"."+PeoplesDB.ScheduledSurveys.SURVEY_ID,
								ss+"."+PeoplesDB.ScheduledSurveys.ORIGINAL_TIME,
								su+"."+day};
		
		String selection 	= PeoplesDB.ScheduledSurveys.SKIPPED + " = ? AND " +
							  ss+"."+PeoplesDB.ScheduledSurveys.SURVEY_ID+" = "+
							  su+"."+PeoplesDB.SurveyTable._ID;
		
		String[] selArgs 	= {Boolean.toString(false)};
		
		String group  		= null;
		String having 		= null;
		String orderBy		= null;
		
		return db.query(table, columns, selection, selArgs,
							group, having, orderBy);
		
	}
	
	/**
	 * 
	 * Get surveys that have not been scheduled for today. The cursor has only
	 * one column:
	 * 
	 * day: the time at which this schedule should run today
	 * 
	 * @param day
	 * @return
	 */
	public Cursor getUnScheduledSurveys(String day) {
		
		if(D) Log.d(TAG, "getting previously unscheduled surveys");
		
		String ss = PeoplesDB.SS_TABLE_NAME;
		String su = PeoplesDB.SURVEY_TABLE_NAME;
		
		String query =	" SELECT "	+ su +"."+day+	","+ss+"."+PeoplesDB.ScheduledSurveys.SURVEY_ID+     
						" FROM "	+ su +" , "+ ss		+
						" WHERE "	+
						PeoplesDB.ScheduledSurveys.SKIPPED + " = \'TRUE\' "+" AND "+
						ss+"."+PeoplesDB.ScheduledSurveys.SURVEY_ID+" = "+
						su+"."+PeoplesDB.SurveyTable._ID+
						
						" UNION "	+
						
						" SELECT "	+ su +"."+day+	","+su+"."+PeoplesDB.SurveyTable._ID+     
						" FROM "	+ su +			" "+
						" WHERE "	+				" "+
						" NOT EXISTS ( "	+
							" SELECT * FROM "+su+", "+ss+" "+
							" WHERE "+
							ss+"."+PeoplesDB.ScheduledSurveys.SURVEY_ID+" = "+
						  	su+"."+PeoplesDB.SurveyTable._ID+" )";
		
		return db.rawQuery(query, null);
		
	}
	
	
	public void putIntoScheduledTable(SurveyIntent si){
		
		
	}
	
	

}
