/*---------------------------------------------------------------------------*
 * Survey.java                                                               *
 *                                                                           *
 * Model for a Survey Droid survey.  Self-contained class that can be used   *
 * to initialize surveys from the database and run through them.             *
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
package org.survey_droid.survey_droid.survey;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.HashMap;

import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

import org.survey_droid.survey_droid.Config;
import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.content.ProviderContract.*;
import org.survey_droid.survey_droid.content.SurveyDBHandler;
import org.survey_droid.survey_droid.content.SurveyDroidDB;
import org.survey_droid.survey_droid.survey.Condition.ConditionScope;
import org.survey_droid.survey_droid.survey.Condition.ConditionType;
import org.survey_droid.survey_droid.survey.Condition.DataType;
import org.survey_droid.survey_droid.survey.questions.Choice;
import org.survey_droid.survey_droid.survey.questions.ChoiceActivity;
import org.survey_droid.survey_droid.survey.questions.FreeResponseActivity;
import org.survey_droid.survey_droid.survey.questions.ImgScaleActivity;
import org.survey_droid.survey_droid.R;

/**
 * The highest-level survey-related class.  A survey object contains everything
 * needed to administer a given survey to subjects.
 * 
 * @author Diego Vargas
 * @author Tony Xiao
 * @author Austin Walker
 */
public class Survey
{
	/** ID for the dummy (example) survey */
	public static final long DUMMY_SURVEY_ID = 0;
	
	/** The database helper instance */
	private final SurveyDBHandler db;
	/** The context for various calls */
	private final Context ctxt;
	
	/** this survey's id */
	private final long id;
	
	/** this survey's id in its study */
	private final long study_survey_id;
	
	/** the id of the study this survey is from */
	private final long study_id;

	/** Logging tag */
	private final String TAG = "Survey";

	/** The survey name; it has already been processed */
	private final String name;

	/** The first question in the survey */
	private final Question firstQ;

	/** The current Question object */
	private Question currentQ;

	/** The current Question's previously given Answer */
	private Answer currentAns;

	/** A registry of live Answers for Conditions to look at */
	private final Stack<Answer> registry = new Stack<Answer>();

	/** The Question history via a stack */
	private final Stack<Question> history = new Stack<Question>();

	/*-----------------------------------------------------------------------*/
	/**
	 * Create a new survey by fetching the needed information from the
	 * database and initializing the child objects.
	 * 
	 * @param id - the survey_id from the database to base this Survey on
	 * @param ctxt - the current {@link Context}
	 * 
	 * @throws IllegalArgumentException if the survey with id does not exist
	 * @throws IllegalArgumentException if any of the required columns do not
	 * exist in the database or can't be found in the results set
	 * 
	 * @see SurveyDBHandler
	 * @see SurveyDroidDB
	 */
	public Survey(long id, Context ctxt) throws SurveyConstructionException
	{
		/*
		 * Just a friendly warning: this constructor and it's helpers are
		 * just really ugly.  I (Austin) don't think there's much that can
		 * be done about it; it is just a really complicated data structure to
		 * initialize.  The rationale behind the design is the need for the UI
		 * to be snappy.  By doing all the work in the constructor, a Survey
		 * can be built once, and then the subject can be notified that it is
		 * time to take the survey.  At that point, all operations are constant
		 * time, so the subject gets a very responsive UI.
		 */
		//TODO convert all database access to mass queries OR convert this
		//class to use a lazy data access policy instead of an eager one
		
		this.id = id;

		//open up a database helper
		this.ctxt = ctxt;
		long[] tmp_id = new long[1];
		db = SurveyDBHandler.getStudyId(id, ctxt, tmp_id);
		if (db == null)
		{
			throw new SurveyConstructionException(id,
				new IllegalArgumentException("no such survey: " + id));
		}
		study_survey_id = tmp_id[0];
		study_id = db.study_id;

		//start out by getting the survey level stuff done
		Cursor s = db.getSurvey(id);
		long firstQID;
		try
		{
			name = processText(s.getString(
					s.getColumnIndexOrThrow(SurveyTable.Fields.NAME)));
			firstQID = s.getLong(
					s.getColumnIndexOrThrow(SurveyTable.Fields.QUESTION_ID));
		}
		finally
		{
			s.close();
		}

		//set up a bunch of data structures to help out
		Map<Long, Question> qMap = new HashMap<Long, Question>();
		Map<Long, Boolean> seen = new HashMap<Long, Boolean>();
		Queue<Long> toDo = new LinkedList<Long>();
		Collection<Branch> bList = new LinkedList<Branch>();
		Collection<Condition> cList = new LinkedList<Condition>();

		//set up the first question, then iterate until done
		Util.d(null, TAG, "First question Setup");
		try
		{
			firstQ = setUpQuestion(firstQID, qMap, seen, bList, cList, toDo);
		}
		catch (SurveyConstructionException e)
		{
			e.setBuildQustion(firstQID);
			throw e;
		}
		currentQ = firstQ;
		while (!toDo.isEmpty())
		{
			Util.v(null, TAG, "next question");
			long qid = toDo.remove();
			try
			{
				setUpQuestion(qid, qMap, seen, bList, cList, toDo);
			}
			catch (SurveyConstructionException e)
			{
				e.setBuildQustion(qid);
				throw e;
			}
		}

		//now that we have a complete Question mapping, go back and set
		//all the Branches and Conditions
		for (Branch branch : bList)
		{
			try
			{
				branch.setQuestion(qMap);
			}
			catch (SurveyConstructionException e)
			{
				e.setSurvey(id);
				throw e;
			}
		}
		Util.v(null, TAG, "branch setup complete");
		for (Condition condition : cList)
		{
			try
			{
				condition.setQuestion(qMap);
			}
			catch (SurveyConstructionException e)
			{
				e.setSurvey(id);
				throw e;
			}
		}
		Util.v(null, TAG, "condition setup complete");
	}

	//set up the Question object with id
	private Question setUpQuestion(
			long id,
			Map<Long, Question> qMap,
			Map<Long, Boolean> seen,
			Collection<Branch> bList,
			Collection<Condition> cList,
			Queue<Long> toDo) throws SurveyConstructionException
	{
		//set up Branches
		Cursor b = db.getBranches(id);
		List<Branch> branches = new ArrayList<Branch>();
		try
		{
			b.moveToFirst();
			Util.v(null, TAG, "I have this many branches " +  b.getCount());
			while (!b.isAfterLast())
			{
				long b_id = b.getLong(
						b.getColumnIndexOrThrow(BranchTable.Fields.BRANCH_ID));
				long q_id = b.getLong(
						b.getColumnIndexOrThrow(BranchTable.Fields.NEXT_Q));
				if (!seen.containsKey(q_id))
	            {
	                seen.put(q_id, true);
	                toDo.add(q_id);
	            }
				Branch newBranch;
				try
				{
					newBranch = new Branch(q_id,
						getConditions(b_id, seen, toDo, cList));
				}
				catch (SurveyConstructionException e)
				{
					e.setBuildBranch(b_id);
					b.close();
					throw e;
				}
				branches.add(newBranch);
				bList.add(newBranch);
				b.moveToNext();
			}
		}
		finally
		{
			b.close();
		}

		Util.v(null, TAG, "Building new question");
		//finally, create the new Question
		Cursor q = db.getQuestion(id);
		Question newQ;
		try
		{
			if (!q.moveToFirst())
			{
				SurveyConstructionException e = new SurveyConstructionException(this.id);
				e.setRefQuestion(id);
				throw e;
			}
			byte[] data = q.getBlob(
					q.getColumnIndexOrThrow(QuestionTable.Fields.DATA));
			String pkgName = q.getString(q.getColumnIndexOrThrow(QuestionTable.Fields.PACKAGE));
			String clsName = q.getString(q.getColumnIndexOrThrow(QuestionTable.Fields.CLASS));
			newQ = new Question(id, branches, data, pkgName, clsName);
		}
		finally
		{
			q.close();
		}
		qMap.put(id, newQ);
		return newQ;
	}

	//gets a Branch's Conditions by branch_id
	private List<Condition> getConditions(
			long id,
			Map<Long, Boolean> seen,
			Queue<Long> toDo,
			Collection<Condition> cList) throws SurveyConstructionException
	{
		List<Condition> conditions = new LinkedList<Condition>();
		Cursor c = db.getConditions(id); //FIXME fails here sometimes
		try
		{
			c.moveToFirst();
			while (!c.isAfterLast())
			{
				long q_id = c.getLong(c.getColumnIndexOrThrow(
						ConditionTable.Fields.QUESTION_ID));
				ConditionType t = ConditionType.values()[c.getInt(c.getColumnIndexOrThrow(
						ConditionTable.Fields.TYPE))];
				ConditionScope s = ConditionScope.values()[c.getInt(c.getColumnIndexOrThrow(
					ConditionTable.Fields.SCOPE))];
				DataType dt = DataType.values()[c.getInt(c.getColumnIndexOrThrow(
					ConditionTable.Fields.DATA_TYPE))];
				byte[] data = c.getBlob(c.getColumnIndexOrThrow(ConditionTable.Fields.DATA));
				int location = c.getInt(c.getColumnIndexOrThrow(ConditionTable.Fields.OFFSET));
				Condition newC;
				try
				{
					newC = new Condition(q_id, t, s, dt, data, location, registry);
				}
				catch (IllegalArgumentException e)
				{
					SurveyConstructionException sce = new SurveyConstructionException(id, e);
					sce.setBuildCondition(c.getInt(c.getColumnIndexOrThrow(
							ConditionTable.Fields.CONDITION_ID)));
					c.close();
					throw sce;
				}
				conditions.add(newC);
				cList.add(newC);
				c.moveToNext();
			}
		}
		finally
		{
			c.close();
		}
		return conditions;
	}

	/**
	 * A simple constructor to put together a sample survey.
	 * 
	 * @param ctxt - the current {@link Context}
	 * @throws SurveyConstructionException should never happen, declared to
	 * maintain compatibility with the other constructor
	 */
	public Survey(Context ctxt) throws SurveyConstructionException
	{
		this.ctxt = ctxt;
		db = new SurveyDBHandler(ctxt, 0);
		id = DUMMY_SURVEY_ID;
		study_id = DUMMY_SURVEY_ID;
		study_survey_id = DUMMY_SURVEY_ID;

		Question prevQ = null;
		for (int i = 3; i >= 0; i--)
		{
			String pkgName = ctxt.getPackageName();
			LinkedList<Branch> branches = new LinkedList<Branch>();
			if (prevQ != null)
			{
				Branch b = new Branch(i, new LinkedList<Condition>());
				Map<Long, Question> qMap = new HashMap<Long, Question>();
				qMap.put((long) i, prevQ);
				b.setQuestion(qMap);
				branches.add(b);
			}
			ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(outBytes);
			String clazz = null;
			switch (i)
			{
			case 0:
				//A single (text) choice question
				clazz = ChoiceActivity.class.getName();
				try
				{	
					//prompt
					out.writeUTF("What is your favorite season?");
					
					//single choice
					out.writeBoolean(false);
					
					//don't allow no choices (though it doesn't matter)
					out.writeBoolean(false);
					
					//the choices
					String[] choices = { "Winter", "Spring", "Summer", "Fall" };
					out.writeInt(choices.length);
					for (int j = 0; j < choices.length; j++)
					{
						out.writeByte(Choice.TEXT_CHOICE_BYTE);
						out.writeUTF(choices[j]);
					}
				}
				catch (Exception e)
				{
					Util.e(null, TAG, "Unable to build byte array");
					Util.e(null, TAG, Util.fmt(e));
					throw new SurveyConstructionException();
				}
				break;
			case 1:
				//A multiple (image) choice question
				clazz = ChoiceActivity.class.getName();
				try
				{
					//prompt
					out.writeUTF("What are your favorite colors?");
					
					//multiple choice
					out.writeBoolean(true);
					
					//don't allow no choices
					out.writeBoolean(false);
					
					//number of choices
					out.writeInt(6);
					
					//the choices
					//purple 1
					out.writeByte(Choice.IMAGE_CHOICE_BYTE);
					Bitmap purpleImg = BitmapFactory.decodeResource(
							ctxt.getResources(), R.drawable.purple);
					purpleImg.compress(Bitmap.CompressFormat.PNG, 100, out);
					
					//blue 2
					out.writeByte(Choice.IMAGE_CHOICE_BYTE);
					Bitmap blueImg = BitmapFactory.decodeResource(
							ctxt.getResources(), R.drawable.blue);
					blueImg.compress(Bitmap.CompressFormat.PNG, 100, out);
					
					//green 3
					out.writeByte(Choice.IMAGE_CHOICE_BYTE);
					Bitmap greenImg = BitmapFactory.decodeResource(
							ctxt.getResources(), R.drawable.green);
					greenImg.compress(Bitmap.CompressFormat.PNG, 100, out);
					
					//yellow 4
					out.writeByte(Choice.IMAGE_CHOICE_BYTE);
					Bitmap yellowImg = BitmapFactory.decodeResource(
							ctxt.getResources(), R.drawable.yellow);
					yellowImg.compress(Bitmap.CompressFormat.PNG, 100, out);
					
					//orange 5
					out.writeByte(Choice.IMAGE_CHOICE_BYTE);
					Bitmap orangeImg = BitmapFactory.decodeResource(
							ctxt.getResources(), R.drawable.orange);
					orangeImg.compress(Bitmap.CompressFormat.PNG, 100, out);
					
					//red 6
					out.writeByte(Choice.IMAGE_CHOICE_BYTE);
					Bitmap redImg = BitmapFactory.decodeResource(
							ctxt.getResources(), R.drawable.red);
					redImg.compress(Bitmap.CompressFormat.PNG, 100, out);
				}
				catch (Exception e)
				{
					Util.e(null, TAG, "Unable to build byte array");
					Util.e(null, TAG, Util.fmt(e));
					throw new SurveyConstructionException();
				}
				break;
			case 2:
				//A image scale question
				clazz = ImgScaleActivity.class.getName();

				try
				{
					//the prompt
					out.writeUTF("How are you today?");
					
					//the choices
					Bitmap lowImg = BitmapFactory.decodeResource(
							ctxt.getResources(), R.drawable.sad);
					lowImg.compress(CompressFormat.PNG, 100, out);
					Bitmap highImg = BitmapFactory.decodeResource(
							ctxt.getResources(), R.drawable.happy);
					highImg.compress(CompressFormat.PNG, 100, out);
				}
				catch (Exception e)
				{
					Util.e(null, TAG, "Unable to build byte array");
					Util.e(null, TAG, Util.fmt(e));
					throw new SurveyConstructionException();
				}
				break;
			case 3:
				//A free response question
				clazz = FreeResponseActivity.class.getName();
				try
				{
					out.writeBoolean(false); //blank response flag
					out.writeUTF("What is your favorite food?");
				}
				catch (Exception e)
				{
					Util.e(null, TAG, "Unable to build byte array");
					Util.e(null, TAG, Util.fmt(e));
					throw new SurveyConstructionException();
				}
				break;
			}
			prevQ = new Question(i, branches, outBytes.toByteArray(),
				pkgName, clazz);
		}
		firstQ = prevQ;
		currentQ = prevQ;
		name = "Test Survey";
	}

	/*-----------------------------------------------------------------------*/

	/**
	 * Is the Survey over?
	 * 
	 * @return true if the Survey has no more questions to ask.
	 */
	public boolean done()
	{
		if (currentQ == null)
			return true;
		return false;
	}

	/**
	 * Get this Survey's name/title
	 * 
	 * @return the name/title as a String
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Get the current qusetion's current answer's value
	 * 
	 * @return a byte array containing data; it is up to individual questions
	 * to determine how to parse this data.  Returns null if there is no
	 * current answer
	 */
	public byte[] getAnswerValue()
	{
		if (currentAns == null)
		{
			return null;
		}
		return currentAns.getValue();
	}
	
	/**
	 * @return the current question
	 */
	public Question getQuestion()
	{
		return currentQ;
	}

	/**
	 * Move the survey to the next question.
	 */
	public void nextQuestion()
	{
		history.push(currentQ);
		currentQ = currentQ.nextQuestion();
		currentAns = null;
		if (currentQ != null)
		{
			currentQ.prime();
		}
	}

	/**
	 * Move the survey back one question.
	 * 
	 * @throws RuntimeException the current question is the first question
	 */
	public void prevQuestion()
	{
		if (isOnFirst()) throw new RuntimeException("no previous Question");
		currentQ = history.pop();
		currentAns = registry.pop();
		currentQ.popAns();
	}

	/**
	 * Is the survey on the first question?  Useful when choosing to whether or
	 * not to display a back button.
	 * 
	 * @return true or false
	 */
	public boolean isOnFirst()
	{
		if (currentQ == null) return false;
		return (currentQ.equals(firstQ));
	}
	
	/**
	 * Get the type of the current question.
	 * 
	 * @return the class (extending QuestionActivity) that the current
	 * question corresponds to as a ComponentName
	 */
	public ComponentName getQuestionType()
	{
		if (currentQ == null) throw new
			RuntimeException("Call to getQuestionType after end of survey");
		return currentQ.type;
	}
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Answer a question.
	 * 
	 * @param data the data with which the question is answered as raw bytes.
	 * The actual meaning of this data varies based on the question type.
	 */
	public void answer(byte[] data)
	{
		currentQ.answer(new Answer(currentQ, data));
	}

	/**
	 * Finalize the answers to this survey and enter them in the database. Also
	 * deletes all given answers after they are written to the database, leaving
	 * this Survey in it's original form.
	 * 
	 * @return true on success
	 */
	public boolean submit()
	{
		boolean worked = true;

		//save all the live Answers
		db.startWrite();
		try
		{
			while (!registry.empty())
			{
				if (registry.pop().write(db) == false)
				{
					worked = false;
				}
			}
		}
		catch (Exception e)
		{
			worked = false;
			Util.e(null, TAG, "Failed to write answers");
			Util.e(null, TAG, Util.fmt(e));
		}
		finally
		{
			db.endWrite();
		}

		//wipe the Question history
		while (!history.empty())
		{
			history.pop().popAns();
		}

		return worked;
	}
	
	/**
	 * <p>Looks through a string and replaces any occurrences of control
	 * sequences with the correct text.  If the data needed cannot be found,
	 * then the name of that data is used instead.</p>
	 * 
	 * <p>The full set of rules for this function is as follows (by
	 * example):</p>
	 * <ol>
	 * <li>"Hello, #place" becomes "Hello, World" ("#place" => "World")</li>
	 * <li>"Hello, #place!" becomes "Hello, World" ("#place!" => "World")</li>
	 * <li>"Hello, #place\!" becomes "Hello, World!" ("#place" => "World")</li>
	 * <li>"Hello, \#place" becomes "Hello, #place" ("\" removed)</li>
	 * <li>"#greeting World" becomes "Hello, World" ("#greeting" => "Hello,")
	 * </li>
	 * <li>"#greeting#place" becomes "Hello, World" ("#greeting" => "Hello, "
	 * and "#place" => "World")</li>
	 * <li>"Hello, #somenonexistantkey" becomes "Hello, somenonexistantkey"
	 * ("#" removed)</li>
	 * </ol>
	 * 
	 * <p>The search and replace is case sensitive.  Because of this (and
	 * because of the last rule above), it is recommended that administrators
	 * use descriptive keys so that, in the event that the data meant to
	 * replace that key is not found, the resulting text looks acceptable.</p>
	 * 
	 * @param text - the string to look through
	 * @return a new string with the replaced values
	 */
	public String processText(String text)
	{
		Util.v(null, TAG, "processing text: \"" + text + "\"");
		//it is just a coincidence that these are the same
		char ctl = '#'; //escape character
		char fSep = '#'; //field separator for config calls
		char esc = '\\';
		StringBuilder newString = new StringBuilder();
		char[] chars = text.toCharArray();
		boolean escape = false;
		for (int i = 0; i < text.length(); i++)
		{
			char c = chars[i];
			if (c == ctl && !escape)
			{
				StringBuilder key = new StringBuilder();
				i++;
				if (i < text.length()) c = chars[i];
				while (c != ' ' && c != esc && i < text.length())
				{
					if (c == ctl)
					{
						String replacement = Config.getString(ctxt,
								Config.USER_DATA + fSep + study_survey_id + fSep + key,
								key.toString(), study_id);
						Util.v(null, TAG, "for \"" + Config.USER_DATA + fSep
								+ study_survey_id + fSep + key + "\", found " + replacement);
						newString.append(replacement);
						newString.append(processText(text.substring(i)));
						return newString.toString();
					}
					key.append(c);
					i++;
					if (i < text.length()) c = chars[i];
				}
				String replacement = Config.getString(ctxt,
						Config.USER_DATA + fSep + study_survey_id + fSep + key,
						key.toString(), study_id);
				Util.v(null, TAG, "for \"" + Config.USER_DATA + fSep
						+ study_survey_id + fSep + key + "\", found " + replacement);
				newString.append(replacement);
				i--;
				continue;
			}
			if (c == esc)
			{
				escape = true;
				continue;
			}
			newString.append(c);
			escape = false;
		}
		return newString.toString();
	}
}
