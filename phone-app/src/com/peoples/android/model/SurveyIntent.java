package com.peoples.android.model;

import com.peoples.android.Peoples;
import android.content.Context;
import android.content.Intent;


/**
 * 
 * Represents a survey that will be displayed at a given time.
 * 
 * 
 * @author Diego
 *
 */
public class SurveyIntent extends Intent {
	/**
	 * 
	 * Specifies the default activity that displays a survey to
	 * the user.
	 * 
	 * May be set using setSurveyActivity 
	 * 
	 */
	private static Class<Peoples> SURVEY_ACTIVITY = com.peoples.android.Peoples.class;
	
	public static String SURVEY_ID = "SURVEY_ID";
	public static String SURVEY_TIME= "SURVEY_TIME";
	
	
	/**
	 * 
	 * @param context 
	 * @param surveyID Survey id of survey to be triggered
	 * @param surveyTime time at which to display the survey
	 */
	public SurveyIntent(Context context, int surveyID, long surveyTime) {
		// TODO Auto-generated constructor stub
		super(context, SURVEY_ACTIVITY);
		
		//store the survey id that this survey refers to
		putExtra(SURVEY_ID, surveyID);

		//set and store field that tracks original scheduled time
		putExtra(SURVEY_TIME, surveyTime);

	}
	
	/**
	 * Extends the default implementation which doesn't take into
	 * account the extras of the intent
	 */
	@Override
	public boolean filterEquals(Intent other) {
		
		//default behavior first
		if(! super.filterEquals(other) )
			return false;
		
		//compare survey id's
		int myId = this.getIntExtra(SURVEY_ID, -1);
		if(myId == -1 ||
				myId != other.getIntExtra(SURVEY_ID, -1))
			return false;
		
		//compare times
		long myTime = this.getIntExtra(SURVEY_TIME, -1);
		if(myTime == -1 ||
				myTime != other.getIntExtra(SURVEY_TIME, -1))
			return false;
		
		//return true since time and id are the same
		return true;
	}
	

}
