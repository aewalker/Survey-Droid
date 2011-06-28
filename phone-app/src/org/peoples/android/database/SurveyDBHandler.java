/*---------------------------------------------------------------------------*
 * SurveyDBHandler.java                                                      *
 *                                                                           *
 * Extension of the PeoplesDBHandler for Surveys.  Has calls to write and    *
 * read data as needed by Surveys.                                           *
 *---------------------------------------------------------------------------*/
package org.peoples.android.database;

import org.peoples.android.Config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * Handles Survey related calls to the PEOPLES database.
 * 
 * @author Austin Walker
 */
public class SurveyDBHandler extends PeoplesDBHandler
{
	//for writing to the log
	private static final String TAG = "SurveyDBHandler";
	
	/**
	 * Create a new SurveyDBHandler object.
	 * 
	 * @param context
	 */
	public SurveyDBHandler(Context context)
	{
		super(context);
	}
	
	/*-----------------------------------------------------------------------*/
	/*                        Initialization  Methods                        */
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Get the Survey level data.
	 * 
	 * @param id - the survey_id
	 */
	public Cursor getSurvey(int id)
	{
		if (Config.D) Log.d(TAG, "getting survey " + id);
		
		//set up the query

		String    table    = PeoplesDB.SURVEY_TABLE_NAME;
		String[]  cols     = {PeoplesDB.SurveyTable.NAME,
						      PeoplesDB.SurveyTable.QUESTION_ID};
		String    selc     = PeoplesDB.SurveyTable._ID + " =  ?";
		String[]  selcArgs = {Integer.toString(id)};
		String    group    = null;
		String    having   = null;
		String    orderBy  = null;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/**
	 * Get a Question.
	 * 
	 * @param id - the question_id
	 */
	public Cursor getQuestion(int id)
	{
		if (Config.D) Log.d(TAG, "getting question " + id);
		
		//set up the query
		String    table    = PeoplesDB.QUESTION_TABLE_NAME;
		String[]  cols     = {PeoplesDB.QuestionTable._ID,
						      PeoplesDB.QuestionTable.Q_TEXT,
						      PeoplesDB.QuestionTable.Q_TYPE,
						      PeoplesDB.QuestionTable.Q_SCALE_IMG_LOW,
						      PeoplesDB.QuestionTable.Q_SCALE_IMG_HIGH,
						      PeoplesDB.QuestionTable.Q_SCALE_TEXT_LOW,
						      PeoplesDB.QuestionTable.Q_SCALE_TEXT_HIGH};
		String    selc     = PeoplesDB.QuestionTable._ID + " =  ?";
		String[]  selcArgs = {Integer.toString(id)};
		String    group    = null;
		String    having   = null;
		String    orderBy  = null;

		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/**
	 * Get the Choice level data for a particular Question.
	 * 
	 * @param id - the question_id
	 */
	public Cursor getChoices(int id)
	{
		if (Config.D) Log.d(TAG, "getting choices for question " + id);
		
		//set up the query
		String    table    = PeoplesDB.CHOICE_TABLE_NAME;
		String[]  cols     = {PeoplesDB.ChoiceTable._ID};
		String    selc     = PeoplesDB.ChoiceTable.QUESTION_ID + " =  ?";
		String[]  selcArgs = {Integer.toString(id)};
		String    group    = null;
		String    having   = null;
		String    orderBy  = PeoplesDB.ChoiceTable._ID;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	

	/**
	 * Get a particular Choice
	 * 
	 * @param id - the choice_id
	 */
	public Cursor getChoice(int id)
	{
		if (Config.D) Log.d(TAG, "getting choice " + id);
		
		//set up the query
		String    table    = PeoplesDB.CHOICE_TABLE_NAME;
		String[]  cols     = {PeoplesDB.ChoiceTable.CHOICE_TEXT,
							  PeoplesDB.ChoiceTable.CHOICE_TYPE,
							  PeoplesDB.ChoiceTable.CHOICE_IMG};
		String    selc     = PeoplesDB.ChoiceTable._ID + " =  ?";
		String[]  selcArgs = {Integer.toString(id)};
		String    group    = null;
		String    having   = null;
		String    orderBy  = null;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/**
	 * Get the Branch level data for a particular Question.
	 * 
	 * @param id - the question_id
	 */
	public Cursor getBranches(int id)
	{
		if (Config.D) Log.d(TAG, "getting branches for question " + id);
		
		//set up the query
		String    table    = PeoplesDB.BRANCH_TABLE_NAME;
		String[]  cols     = {PeoplesDB.BranchTable._ID,
						      PeoplesDB.BranchTable.NEXT_Q};
		String    selc     = PeoplesDB.BranchTable.QUESTION_ID + " =  ?";
		String[]  selcArgs = {Integer.toString(id)};
		String    group    = null;
		String    having   = null;
		String    orderBy  = PeoplesDB.BranchTable._ID;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/**
	 * Get the Condition level data for a particular Branch.
	 * 
	 * @param id - the branch_id
	 */
	public Cursor getConditions(int id)
	{
		if (Config.D) Log.d(TAG, "getting conditions for branch " + id);
		

		//set up the query
		String    table    = PeoplesDB.CONDITION_TABLE_NAME;
		String[]  cols     = {PeoplesDB.ConditionTable._ID,
							  PeoplesDB.ConditionTable.TYPE,
						      PeoplesDB.ConditionTable.QUESTION_ID,
						      PeoplesDB.ConditionTable.CHOICE_ID};
		String    selc     = PeoplesDB.ConditionTable.BRANCH_ID + " =  ?";
		String[]  selcArgs = {Integer.toString(id)};
		String    group    = null;
		String    having   = null;
		String    orderBy  = PeoplesDB.ConditionTable._ID;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}

	
	/*-----------------------------------------------------------------------*/
	/*                             Read  Methods                             */
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Get all surveys (id and time fields only); used for scheduling.
	 */
	public Cursor getSurveys()
	{
		if (Config.D) Log.d(TAG, "getting all surveys");
		
		//set up the query

		String    table    = PeoplesDB.SURVEY_TABLE_NAME;
		String[]  cols     = {PeoplesDB.SurveyTable._ID,
						      PeoplesDB.SurveyTable.SU,
						      PeoplesDB.SurveyTable.MO,
						      PeoplesDB.SurveyTable.TU,
						      PeoplesDB.SurveyTable.WE,
						      PeoplesDB.SurveyTable.TH,
						      PeoplesDB.SurveyTable.FR,
						      PeoplesDB.SurveyTable.SA};
		String    selc     = null;
		String[]  selcArgs = null;
		String    group    = null;
		String    having   = null;
		String    orderBy  = null;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/**
	 * Get the Answer history for a particular Question
	 * 
	 * @param id - the question_id
	 */
	public Cursor getQuestionHistory(int id)
	{
		if (Config.D) Log.d(TAG, "getting answers for question " + id);
		
		//set up the query
		String    table    = PeoplesDB.ANSWER_TABLE_NAME;
		String[]  cols     = {PeoplesDB.AnswerTable.CHOICE_IDS};
		String    selc     = PeoplesDB.AnswerTable.QUESTION_ID + " =  ?";
		String[]  selcArgs = {Integer.toString(id)};
		String    group    = null;
		String    having   = null;
		String    orderBy  = PeoplesDB.AnswerTable.CREATED;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/*-----------------------------------------------------------------------*/
	/*                             Write Methods                             */
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Write a subject's answer to a multiple choice question to the database
	 * 
	 * @param q_id - the question_id
	 * @param c_ids - the choice_ids
	 * @param created - the time the answer was given
	 * 
	 * @returns true on success
	 */
	public boolean writeAnswer(int q_id, int[] c_ids, long created)
	{
		//format the ids
		if (c_ids == null || c_ids.length == 0)
		{
			throw new RuntimeException("No choices given in answer");
		}
		StringBuilder ids = new StringBuilder(Integer.toString(c_ids[0]));
		for (int i = 1; i < c_ids.length; i++)
		{
			ids.append(Integer.toString(c_ids[i]));
		}
		
		if (Config.D) Log.d(TAG, "writing answer for question " + q_id
				+ ": choice " + ids.toString());
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(PeoplesDB.AnswerTable.QUESTION_ID, q_id);
		values.put(PeoplesDB.AnswerTable.CHOICE_IDS, ids.toString());
		values.put(PeoplesDB.AnswerTable.CREATED, created);
		
		//run it
		if (db.insert(PeoplesDB.ANSWER_TABLE_NAME, null, values) == -1)
			return false;
		return true;
	}
	
	/**
	 * Write a subject's answer to a free response question to the database
	 * 
	 * @param q_id - the question_id
	 * @param text - the String that is the response text
	 * @param created - the time the answer was given
	 * 
	 * @returns true on success
	 */
	public boolean writeAnswer(int q_id, String text, long created)
	{
		if (Config.D) Log.d(TAG, "writing answer for question " + q_id + ": \""
				+ text + "\"");
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(PeoplesDB.AnswerTable.QUESTION_ID, q_id);
		values.put(PeoplesDB.AnswerTable.ANS_TEXT, text);
		values.put(PeoplesDB.AnswerTable.CREATED, created);
		
		//run it
		if (db.insert(PeoplesDB.ANSWER_TABLE_NAME, null, values) == -1)
			return false;
		return true;
	}
	
	/**
	 * Write a subject's answer to a sliding scale (value based) question to the
	 * database.
	 * 
	 * @param q_id - the question_id
	 * @param value - the value given in the question
	 * @param created - the time the answer was given
	 * 
	 * @returns true on success
	 */
	public boolean writeAnswer(int q_id, int value, long created)
	{
		if (Config.D) Log.d(TAG, "writing answer for question " + q_id + ": "
				+ value);
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(PeoplesDB.AnswerTable.QUESTION_ID, q_id);
		values.put(PeoplesDB.AnswerTable.ANS_VALUE, value);
		values.put(PeoplesDB.AnswerTable.CREATED, created);
		
		//run it
		if (db.insert(PeoplesDB.ANSWER_TABLE_NAME, null, values) == -1)
			return false;
		return true;
	}
}
