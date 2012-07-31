/*---------------------------------------------------------------------------*
 * SurveyDBHandler.java                                                      *
 *                                                                           *
 * Extension of the SurveyDroidDBHandler for Surveys.  Has calls to write    *
 * and read data as needed by Surveys.                                       *
 *---------------------------------------------------------------------------*
 * Copyright (C) 2011-2012 Survey Droid Contributors                         *
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
package org.survey_droid.survey_droid.content;

import java.util.LinkedList;
import java.util.List;

import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.content.ProviderContract.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Handles Survey related calls to the Survey Droid database.
 * 
 * @author Austin Walker
 */
public class SurveyDBHandler extends SurveyDroidDBHandler
{
	/** logging tag */
	private static final String TAG = "SurveyDBHandler";
	
	/** study id */
	public final long study_id;
	
	/**
	 * Create a new SurveyDBHandler object.
	 * 
	 * @param context
	 */
	public SurveyDBHandler(Context context, long study_id)
	{
		super(context);
		this.study_id = study_id;
	}
	
	/*-----------------------------------------------------------------------*/
	/*                        Initialization  Methods                        */
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Create a handler from a survey id
	 * 
	 * @param id survey id
	 * @param c
	 * @param internal_id the first member of this will be set to the internal
	 * survey id if this function does not return null
	 * 
	 * @returns a new handler using the proper study id for the given survey
	 * id, or null if the survey cannot be found
	 */
	public static SurveyDBHandler getStudyId(long id, Context c, long[] internal_id)
	{
		Util.d(null, TAG, "getting study for survey " + id);
		
		//set up the query

		String    table    = SurveyTable.NAME;
		String[]  cols     = {SurveyTable.Fields.STUDY_ID, SurveyTable.Fields.SURVEY_ID};
		String    selc     = SurveyTable.Fields._ID + " = ?";
		String[]  selcArgs = {Long.toString(id)};
		String    orderBy  = null;
		
		Cursor result = (new SurveyDroidDBHandler(c)).query(table, cols, selc, selcArgs, orderBy);
		if (result.getCount() != 1) return null;
		result.moveToFirst();
		long study_id = result.getLong(result.getColumnIndexOrThrow(SurveyTable.Fields.STUDY_ID));
		internal_id[0] = result.getLong(result.getColumnIndexOrThrow(SurveyTable.Fields.SURVEY_ID));
		return new SurveyDBHandler(c, study_id);
	}
	
	/**
	 * Get the Survey level data.
	 * 
	 * @param id the survey_id
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getSurvey(long id)
	{
		Util.d(null, TAG, "getting survey " + id);
		
		//set up the query

		String    table    = SurveyTable.NAME;
		String[]  cols     = {SurveyTable.Fields._ID,
							  SurveyTable.Fields.NAME,
						      SurveyTable.Fields.QUESTION_ID};
		String    selc     = SurveyTable.Fields.SURVEY_ID + " = ? AND " + SurveyTable.Fields.STUDY_ID + " =  ?";
		String[]  selcArgs = {Long.toString(id), Long.toString(study_id)};
		String    orderBy  = null;
		
		return query(table, cols, selc, selcArgs, orderBy);
	}
	
	/**
	 * Get a Question.
	 * 
	 * @param id the question_id
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getQuestion(long id)
	{
		Util.d(null, TAG, "getting question " + id);
		
		//set up the query
		String    table    = QuestionTable.NAME;
		String[]  cols     = {QuestionTable.Fields.QUESTION_ID,
						      QuestionTable.Fields.DATA,
						      QuestionTable.Fields.CLASS,
						      QuestionTable.Fields.PACKAGE};
		String    selc     = QuestionTable.Fields.QUESTION_ID + " =  ? AND " + QuestionTable.Fields.STUDY_ID + " = ?";
		String[]  selcArgs = {Long.toString(id), Long.toString(study_id)};
		String    orderBy  = null;

		
		//run it
		return query(table, cols, selc, selcArgs, orderBy);
	}
	
	/**
	 * Get the Branch level data for a particular Question.
	 * 
	 * @param id the question_id
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getBranches(long id)
	{
		Util.d(null, TAG, "getting branches for question " + id);
		
		//set up the query
		String    table    = BranchTable.NAME;
		String[]  cols     = {BranchTable.Fields.BRANCH_ID,
						      BranchTable.Fields.NEXT_Q};
		String    selc     = BranchTable.Fields.QUESTION_ID + " = ? AND " + BranchTable.Fields.STUDY_ID + " =  ?";
		String[]  selcArgs = {Long.toString(id), Long.toString(study_id)};
		String    orderBy  = BranchTable.Fields.BRANCH_ID;
		
		//run it
		return query(table, cols, selc, selcArgs, orderBy);
	}
	
	/**
	 * Get the Condition level data for a particular Branch.
	 * 
	 * @param id the branch_id
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getConditions(long id)
	{
		Util.d(null, TAG, "getting conditions for branch " + id);
		

		//set up the query
		String    table    = ConditionTable.NAME;
		String[]  cols     = {ConditionTable.Fields.CONDITION_ID,
							  ConditionTable.Fields.TYPE,
							  ConditionTable.Fields.SCOPE,
							  ConditionTable.Fields.DATA_TYPE,
						      ConditionTable.Fields.QUESTION_ID};
		String    selc     = ConditionTable.Fields.BRANCH_ID + " = ? AND " + ConditionTable.Fields.STUDY_ID + " =  ?";
		String[]  selcArgs = {Long.toString(id), Long.toString(study_id)};
		String    orderBy  = ConditionTable.Fields.CONDITION_ID;
		
		//run it
		return query(table, cols, selc, selcArgs, orderBy);
	}

	
	/*-----------------------------------------------------------------------*/
	/*                             Read  Methods                             */
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Get all surveys (id and time fields only); used for scheduling.  This
	 * will ignore the study_id given to the constructor; it will find all
	 * surveys.  Results will be ordered by study_id.
	 * 
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getSurveys()
	{
		Util.d(null, TAG, "getting all surveys");
		
		//set up the query

		String    table    = SurveyTable.NAME;
		String[]  cols     = {SurveyTable.Fields._ID,
							  SurveyTable.Fields.STUDY_ID,
						      SurveyTable.Fields.SU,
						      SurveyTable.Fields.MO,
						      SurveyTable.Fields.TU,
						      SurveyTable.Fields.WE,
						      SurveyTable.Fields.TH,
						      SurveyTable.Fields.FR,
						      SurveyTable.Fields.SA};
		String    selc     = null;
		String[]  selcArgs = null;
		String    orderBy  = SurveyTable.Fields.STUDY_ID;
		
		//run it
		return query(table, cols, selc, selcArgs, orderBy);
	}
	
	/**
	 * Get all surveys (id and name fields) that subjects can initiate
	 * themselves.  Results are in alphabetical order by survey name.  This
	 * will ignore the study_id given to the constructor; it will find all
	 * subject-init surveys.
	 * 
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getSubjectInitSurveys()
	{
		Util.d(null, TAG, "getting subject-init surveys");
		
		//set up the query
		String    table    = SurveyTable.NAME;
		String[]  cols     = {SurveyTable.Fields._ID,
						      SurveyTable.Fields.NAME};
		String    selc     = SurveyTable.Fields.SUBJECT_INIT + " = ?";
		String[]  selcArgs = {"1"};
		String    orderBy  = SurveyTable.Fields.NAME;
		
		//run it
		return query(table, cols, selc, selcArgs, orderBy);
	}
	
	/**
	 * Get the Answer history for a particular Question in the order they were
	 * created.
	 * 
	 * @param id the question_id
	 * @return a {@link Cursor} with the results
	 */
	public Cursor getQuestionHistory(long id)
	{
		Util.d(null, TAG, "getting answers for question " + id);
		
		//set up the query
		String    table    = AnswerTable.NAME;
		String[]  cols     = {AnswerTable.Fields.ANS_VALUE};
		String    selc     = AnswerTable.Fields.QUESTION_ID + " = ? AND " + AnswerTable.Fields.STUDY_ID + " =  ?";
		String[]  selcArgs = {Long.toString(id), Long.toString(study_id)};
		String    orderBy  = AnswerTable.Fields.CREATED;
		
		//run it
		return query(table, cols, selc, selcArgs, orderBy);
	}
	
	/*-----------------------------------------------------------------------*/
	/*                             Write Methods                             */
	/*-----------------------------------------------------------------------*/
	
	/** if true, then startWrite has been called and endWrite has not */
	private boolean massInsertStarted = false;
	
	/** list of values being compiled for the mass insert */
	private List<ContentValues> valuesList;
	
	/**
	 * Start writing answers to the database.
	 */
	public void startWrite()
	{
		if (massInsertStarted) throw new RuntimeException("already started write");
		massInsertStarted = true;
		valuesList = new LinkedList<ContentValues>();
	}
	
	/**
	 * Write a subject's answer to the database.  Make sure to call
	 * {@link #startWrite()} before calling this, and then call
	 * {@link #endWrite()} after all writes have been completed.
	 * 
	 * @param q_id the question_id
	 * @param data the given answer
	 * @param created the time the answer was given
	 */
	public void writeAnswer(long q_id, byte[] data, long created)
	{
		if (!massInsertStarted) throw new RuntimeException("write not yet started");
		Util.d(null, TAG, "writing answer for question " + q_id);
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(ProviderContract.AnswerTable.Fields.QUESTION_ID, q_id);
		values.put(ProviderContract.AnswerTable.Fields.ANS_VALUE, data);
		values.put(ProviderContract.AnswerTable.Fields.CREATED, created);
		values.put(ProviderContract.AnswerTable.Fields.STUDY_ID, study_id);
		
		valuesList.add(values);
		
		
	}
	
	/**
	 * Execute all the writes
	 * 
	 * @return true on success
	 */
	public boolean endWrite()
	{
		if (!massInsertStarted) throw new RuntimeException("write not yet started");
		//run it
		try
		{
			Uri.Builder builder = new Uri.Builder();
			builder.scheme("content");
			builder.authority(ProviderContract.AUTHORITY);
			builder.appendPath(ProviderContract.AnswerTable.NAME);
			contx.getContentResolver().bulkInsert(
				builder.build(), valuesList.toArray(new ContentValues[0]));
			return true;
		}
		catch (Exception e)
		{
			Util.e(null, TAG, "Failed to insert data");
			Util.e(null, TAG, Util.fmt(e));
			return false;
		}
	}
}
