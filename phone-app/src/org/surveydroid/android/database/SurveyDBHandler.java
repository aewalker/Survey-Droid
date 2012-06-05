/*---------------------------------------------------------------------------*
 * SurveyDBHandler.java                                                      *
 *                                                                           *
 * Extension of the SurveyDroidDBHandler for Surveys.  Has calls to write    *
 * and read data as needed by Surveys.                                       *
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
package org.surveydroid.android.database;

import org.surveydroid.android.Config;
import org.surveydroid.android.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Handles Survey related calls to the Survey Droid database.
 * 
 * @author Austin Walker
 */
public class SurveyDBHandler extends SurveyDroidDBHandler
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
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getSurvey(int id)
	{
		Util.d(null, TAG, "getting survey " + id);
		
		//set up the query

		String    table    = SurveyDroidDB.SURVEY_TABLE_NAME;
		String[]  cols     = {SurveyDroidDB.SurveyTable.NAME,
						      SurveyDroidDB.SurveyTable.QUESTION_ID};
		String    selc     = SurveyDroidDB.SurveyTable._ID + " =  ?";
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
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getQuestion(int id)
	{
		Util.d(null, TAG, "getting question " + id);
		
		//set up the query
		String    table    = SurveyDroidDB.QUESTION_TABLE_NAME;
		String[]  cols     = {SurveyDroidDB.QuestionTable._ID,
						      SurveyDroidDB.QuestionTable.Q_TEXT,
						      SurveyDroidDB.QuestionTable.Q_TYPE,
						      SurveyDroidDB.QuestionTable.Q_SCALE_IMG_LOW,
						      SurveyDroidDB.QuestionTable.Q_SCALE_IMG_HIGH,
						      SurveyDroidDB.QuestionTable.Q_SCALE_TEXT_LOW,
						      SurveyDroidDB.QuestionTable.Q_SCALE_TEXT_HIGH};
		String    selc     = SurveyDroidDB.QuestionTable._ID + " =  ?";
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
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getChoices(int id)
	{
		Util.d(null, TAG, "getting choices for question " + id);
		
		//set up the query
		String    table    = SurveyDroidDB.CHOICE_TABLE_NAME;
		String[]  cols     = {SurveyDroidDB.ChoiceTable._ID};
		String    selc     = SurveyDroidDB.ChoiceTable.QUESTION_ID + " =  ?";
		String[]  selcArgs = {Integer.toString(id)};
		String    group    = null;
		String    having   = null;
		String    orderBy  = SurveyDroidDB.ChoiceTable._ID;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	

	/**
	 * Get a particular Choice
	 * 
	 * @param id - the choice_id
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getChoice(int id)
	{
		Util.d(null, TAG, "getting choice " + id);
		
		//set up the query
		String    table    = SurveyDroidDB.CHOICE_TABLE_NAME;
		String[]  cols     = {SurveyDroidDB.ChoiceTable.CHOICE_TEXT,
							  SurveyDroidDB.ChoiceTable.CHOICE_TYPE,
							  SurveyDroidDB.ChoiceTable.CHOICE_IMG};
		String    selc     = SurveyDroidDB.ChoiceTable._ID + " =  ?";
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
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getBranches(int id)
	{
		Util.d(null, TAG, "getting branches for question " + id);
		
		//set up the query
		String    table    = SurveyDroidDB.BRANCH_TABLE_NAME;
		String[]  cols     = {SurveyDroidDB.BranchTable._ID,
						      SurveyDroidDB.BranchTable.NEXT_Q};
		String    selc     = SurveyDroidDB.BranchTable.QUESTION_ID + " =  ?";
		String[]  selcArgs = {Integer.toString(id)};
		String    group    = null;
		String    having   = null;
		String    orderBy  = SurveyDroidDB.BranchTable._ID;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/**
	 * Get the Condition level data for a particular Branch.
	 * 
	 * @param id - the branch_id
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getConditions(int id)
	{
		Util.d(null, TAG, "getting conditions for branch " + id);
		

		//set up the query
		String    table    = SurveyDroidDB.CONDITION_TABLE_NAME;
		String[]  cols     = {SurveyDroidDB.ConditionTable._ID,
							  SurveyDroidDB.ConditionTable.TYPE,
						      SurveyDroidDB.ConditionTable.QUESTION_ID,
						      SurveyDroidDB.ConditionTable.CHOICE_ID};
		String    selc     = SurveyDroidDB.ConditionTable.BRANCH_ID + " =  ?";
		String[]  selcArgs = {Integer.toString(id)};
		String    group    = null;
		String    having   = null;
		String    orderBy  = SurveyDroidDB.ConditionTable._ID;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}

	
	/*-----------------------------------------------------------------------*/
	/*                             Read  Methods                             */
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Get all surveys (id and time fields only); used for scheduling.
	 * 
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getSurveys()
	{
		Util.d(null, TAG, "getting all surveys");
		
		//set up the query

		String    table    = SurveyDroidDB.SURVEY_TABLE_NAME;
		String[]  cols     = {SurveyDroidDB.SurveyTable._ID,
						      SurveyDroidDB.SurveyTable.SU,
						      SurveyDroidDB.SurveyTable.MO,
						      SurveyDroidDB.SurveyTable.TU,
						      SurveyDroidDB.SurveyTable.WE,
						      SurveyDroidDB.SurveyTable.TH,
						      SurveyDroidDB.SurveyTable.FR,
						      SurveyDroidDB.SurveyTable.SA};
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
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getSubjectInitSurveys()
	{
		Util.d(null, TAG, "getting subject-init surveys");
		
		//set up the query
		String    table    = SurveyDroidDB.SURVEY_TABLE_NAME;
		String[]  cols     = {SurveyDroidDB.SurveyTable._ID,
						      SurveyDroidDB.SurveyTable.NAME};
		String    selc     = SurveyDroidDB.SurveyTable.SUBJECT_INIT + " = ?";
		String[]  selcArgs = {"1"};
		String    group    = null;
		String    having   = null;
		String    orderBy  = SurveyDroidDB.SurveyTable.NAME;
		
		//run it
		return db.query(table, cols, selc, selcArgs, group, having, orderBy);
	}
	
	/**
	 * Get the Answer history for a particular Question
	 * 
	 * @param id - the question_id
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getQuestionHistory(int id)
	{
		Util.d(null, TAG, "getting answers for question " + id);
		
		//set up the query
		String    table    = SurveyDroidDB.ANSWER_TABLE_NAME;
		String[]  cols     = {SurveyDroidDB.AnswerTable.CHOICE_IDS};
		String    selc     = SurveyDroidDB.AnswerTable.QUESTION_ID + " =  ?";
		String[]  selcArgs = {Integer.toString(id)};
		String    group    = null;
		String    having   = null;
		String    orderBy  = SurveyDroidDB.AnswerTable.CREATED;
		
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
			{
				if (Config.D)
					throw new RuntimeException("No choices given in answer");
				else
					Util.e(null, TAG, "No choices given in answer when ALLOW_NO_CHOICES is false");
			}
			ids = new StringBuilder();
		}
		else
		{
			ids = new StringBuilder(Integer.toString(c_ids[0]));
			for (int i = 1; i < c_ids.length; i++)
			{
				ids.append(',');
				ids.append(Integer.toString(c_ids[i]));
			}
		}
		
		Util.d(null, TAG, "writing answer for question " + q_id
				+ ": " + ids.toString());
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(SurveyDroidDB.AnswerTable.QUESTION_ID, q_id);
		values.put(SurveyDroidDB.AnswerTable.CHOICE_IDS, ids.toString());
		values.put(SurveyDroidDB.AnswerTable.ANS_TYPE,
				SurveyDroidDB.AnswerTable.CHOICE);
		values.put(SurveyDroidDB.AnswerTable.CREATED, created);
		
		//run it
		if (db.insert(SurveyDroidDB.ANSWER_TABLE_NAME, null, values) == -1)
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
		Util.d(null, TAG, "writing answer for question " + q_id + ": \""
				+ text + "\"");
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(SurveyDroidDB.AnswerTable.QUESTION_ID, q_id);
		values.put(SurveyDroidDB.AnswerTable.ANS_TEXT, text);
		values.put(SurveyDroidDB.AnswerTable.ANS_TYPE,
				SurveyDroidDB.AnswerTable.TEXT);
		values.put(SurveyDroidDB.AnswerTable.CREATED, created);
		
		//run it
		if (db.insert(SurveyDroidDB.ANSWER_TABLE_NAME, null, values) == -1)
			return false;
		return true;
	}
	
	/**
	 * Write a subject's answer to a sliding scale (value based) question to
	 * the database.
	 * 
	 * @param q_id - the question_id
	 * @param value - the value given in the question
	 * @param created - the time the answer was given
	 * 
	 * @returns true on success
	 */
	public boolean writeAnswer(int q_id, int value, long created)
	{
		Util.d(null, TAG, "writing answer for question " + q_id + ": "
				+ value);
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(SurveyDroidDB.AnswerTable.QUESTION_ID, q_id);
		values.put(SurveyDroidDB.AnswerTable.ANS_VALUE, value);
		values.put(SurveyDroidDB.AnswerTable.ANS_TYPE,
				SurveyDroidDB.AnswerTable.VALUE);
		values.put(SurveyDroidDB.AnswerTable.CREATED, created);
		
		//run it
		if (db.insert(SurveyDroidDB.ANSWER_TABLE_NAME, null, values) == -1)
			return false;
		return true;
	}
}
