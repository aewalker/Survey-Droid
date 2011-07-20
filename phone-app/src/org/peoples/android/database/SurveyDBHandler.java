/*---------------------------------------------------------------------------*
 * SurveyDBHandler.java                                                      *
 *                                                                           *
 * Extension of the PeoplesDBHandler for Surveys.  Has calls to write and    *
 * read data as needed by Surveys.                                           *
 *---------------------------------------------------------------------------*/
package org.peoples.android.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.peoples.android.Config;
import org.peoples.android.survey.Base64Coder;

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
	 * Returned by methods that require and int return type when an error
	 * has occurred.
	 */
	public static final int WRITE_ERROR = -1;
	
	/**
	 * Used with {@link writeExtra} to write a photo taken as part of a
	 * survey to the database.
	 */
	public static final int PHOTO = 0;
	
	/**
	 * Used with {@link writeExtra} to write a voice recording made as part of
	 * a survey to the database.
	 */
	public static final int VOICE = 1;
	
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
	 * 
	 * @return a cursor with the results
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
	 * Get all surveys (id and name fields) that subjects can initiate
	 * themselves.  Results are in alphabetical order by survey name.
	 * 
	 * @return a cursor with the results
	 */
	public Cursor getSubjectInitSurveys()
	{
		if (Config.D) Log.d(TAG, "getting subject-init surveys");
		
		//set up the query
		String    table    = PeoplesDB.SURVEY_TABLE_NAME;
		String[]  cols     = {PeoplesDB.SurveyTable._ID,
						      PeoplesDB.SurveyTable.NAME};
		String    selc     = PeoplesDB.SurveyTable.SUBJECT_INIT + " = ?";
		String[]  selcArgs = {"1"};
		String    group    = null;
		String    having   = null;
		String    orderBy  = PeoplesDB.SurveyTable.NAME;
		
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
		StringBuilder ids;
		if (c_ids == null || c_ids.length == 0)
		{
			if (!Config.getSetting(contx, Config.ALLOW_NO_CHOICES,
					Config.ALLOW_NO_CHOICES_DEFAULT))
				throw new RuntimeException("No choices given in answer");
			else
			{
				ids = new StringBuilder();
			}
		}
		else
		{
			ids = new StringBuilder(Integer.toString(c_ids[0]));
			for (int i = 1; i < c_ids.length; i++)
			{
				ids.append(Integer.toString(c_ids[i]));
			}
		}
		
		if (Config.D) Log.d(TAG, "writing answer for question " + q_id
				+ ": " + ids.toString());
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
	
	/**
	 * Write a photo or voice recording to the database.
	 * 
	 * @param survey_id - the survey id that this photo was taken for
	 * @param type - either {@link PHOTO} or {@link VOICE}
	 * @param stream - the stream containing the photo or recording's data
	 * @param created - timestamp of when the photo or recording was made
	 * @param row_id - the row to put the photo or recording into, or 0 if a
	 * new row is to be made
	 * 
	 * @return the row in the extras table where the photo or recording was put,
	 * or {@link WRITE_ERROR} on error
	 */
	public int writeExtra(int survey_id, int type,
			InputStream stream, long created, int row_id)
	{
		if (Config.D) Log.d(TAG, "writing extra for survey "
				+ survey_id + " to database");
		if (Config.D && row_id != 0)
		{
			//check that row_id is a valid row; if debugging is off,
			//assume that it is
			
			//set up the query
			String    table    = PeoplesDB.EXTRAS_TABLE_NAME;
			String[]  cols     = {PeoplesDB.ExtrasTable._ID};
			String    selc     = PeoplesDB.ExtrasTable._ID + " =  ?";
			String[]  selcArgs = {Integer.toString(row_id)};
			String    group    = null;
			String    having   = null;
			String    orderBy  = null;
			
			//run it
			Cursor result =
				db.query(table, cols, selc, selcArgs, group, having, orderBy);
			if (result.getCount() == 0) throw new IllegalArgumentException(
					"row_id " + row_id + " does not exist");
			if (result.getCount() > 1) throw new IllegalStateException(
					"SEVERE ERROR: table contains multiple rows with same id: "
					+ row_id);
		}
		
		//now that we know that row_id is valid, let's actually do something
		if (row_id == 0)
		{ //set up a new row
			String extra64;
			try
			{
				extra64 = new String(
						Base64Coder.encode(readStream(stream)));
			}
			catch (Exception e)
			{
				Log.e(TAG, e.getMessage());
				return WRITE_ERROR;
			}
			
			ContentValues values = new ContentValues();
			
			//set up the query
			values.put(PeoplesDB.ExtrasTable.SURVEY_ID, survey_id);
			values.put(PeoplesDB.ExtrasTable.CREATED, created);
			if (type == PHOTO)
				values.put(PeoplesDB.ExtrasTable.PHOTO, extra64);
			else if (type == VOICE)
				values.put(PeoplesDB.ExtrasTable.VOICE, extra64);
			else throw new RuntimeException("Unknown extra type: " + type);
			
			//run it
			return (int) db.insert(PeoplesDB.EXTRAS_TABLE_NAME, null, values);
		}
		else
		{ //amend the existing row
			String extra64;
			try
			{
				extra64 = new String(
						Base64Coder.encode(readStream(stream)));
			}
			catch (Exception e)
			{
				Log.e(TAG, e.getMessage());
				return WRITE_ERROR;
			}
			
			//set up the query
			ContentValues values = new ContentValues();
			if (type == PHOTO)
				values.put(PeoplesDB.ExtrasTable.PHOTO, extra64);
			else if (type == VOICE)
				values.put(PeoplesDB.ExtrasTable.VOICE, extra64);
			else throw new RuntimeException("Unknown extra type: " + type);
			String whereClause = PeoplesDB.ExtrasTable._ID + " = ?";
			String[] whereArgs = {Integer.toString(row_id)};
			
			//run it
			if (db.update(PeoplesDB.EXTRAS_TABLE_NAME,
					values, whereClause, whereArgs) == 1)
				return row_id;
			return WRITE_ERROR;
		}
	}
	
	//reads all of the data out of an input stream into an array
	private byte[] readStream(InputStream is) throws IOException
	{
		ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		
		while ((len = is.read(buffer)) != -1)
		{
			byteBuff.write(buffer, 0, len);
		}
		
		return byteBuff.toByteArray();
	}
}
