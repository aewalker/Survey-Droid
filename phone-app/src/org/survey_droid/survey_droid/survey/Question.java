/*---------------------------------------------------------------------------*
 * Question.java                                                             *
 *                                                                           *
 * Model for a survey question.  Has Branches to other questions, and some   *
 * basic information and functionality.  This is not a full class, it is     *
 * meant to be extended for specific types of questions.                     *
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

import java.util.Collection;
import java.util.List;
import java.util.Stack;

import android.content.ComponentName;

/**
 * Model for a survey question. Questions are simply a way of taking some kind
 * of data and prompting the user to give some other data; this is
 * intentionally vague because it is impossible to try an anticipate the ways
 * that this could be done. Anything from a simple multiple choice question
 * (one of the included types) to a minigame testing reaction speed or taking
 * an audio recording could be a question.
 * <br />
 * Unlike previous versions, Question is no longer abstract and should not be
 * inherited from.  It now functions as a basic data container; it is up to the
 * Question Activity to do all question-type specific logic.
 * 
 * @author Diego Vargas
 * @author Austin Walker
 * @author Henry Liu
 * 
 * @see QuestionActivity
 * @see ByteDataParser
 */
public class Question
{
	/** This question's id */
	public final long id;
	
	/** the answers given for this question in order */
	private final Stack<Answer> answers = new Stack<Answer>();
	
	/* Note: the reason that we have to use a Stack of Answers instead of just
	 * keeping the most recent one is looping.  A Survey could loop back to the
	 * same Question multiple times, so we have to keep track of that.
	 */

	/** has the current Question been answered? */
	private boolean answered = false;

	/** list of branches leading from this question in check order */
	private final List<Branch> branches;
	
	/** the question's data for sending to the activity */
	public final byte[] data;
	
	/**
	 * the type of this question (as the activity that should be started to
	 * handle it
	 */
	public final ComponentName type;
	
	/*-----------------------------------------------------------------------*/

	/**
	 * Create a new Question. <strong>Must be called by extending
	 * classes!</strong>
	 * 
	 * @param id the Question's id from the database
	 * @param b a {@link Collection} of {@link Branch}es for this Question
	 * @param data the question's input data
	 * @param pkg the question activity's package
	 * @param clazz the question activity's class
	 */
	public Question(long id, List<Branch> b, byte[] data,
		String pkg, String clazz)
	{
		branches = b;
		this.id = id;
		this.data = data;
		this.type = new ComponentName(pkg, clazz); //TODO not sure if this will work
	}

	/*-----------------------------------------------------------------------*/
	
	/**
	 * Answer this Question. 
	 * 
	 * @param ans - the {@link Answer} to be recorded
	 * 
	 * @throws RuntimeException if the current Question has already been
	 * answered.
	 */
	protected void answer(Answer ans)
	{
		if (answered) throw new RuntimeException(
			"Atempt to answer the same Question multiple times");
		
		answered = true;
		answers.push(ans);
	}

	/**
	 * Evaluate branches to find the next Question.
	 * 
	 * @return the next Question
	 * 
	 * @throws RuntimeException if the current Question hasn't been answered
	 */
	public Question nextQuestion()
	{
		if (answered == false) throw new RuntimeException(
				"must answer current Question before calling nextQuestion");
		for (Branch b : branches) //since branches is a List, this is in order
		{
			if (b.eval()) return b.nextQuestion();
		}
		return null;
	}

	/*-----------------------------------------------------------------------*/

	/**
	 * Remove the most recent Answer from the stack.
	 * 
	 * @return the most recent {@link Answer}
	 */
	public Answer popAns()
	{
		answered = false;
		if (!answers.isEmpty())
		{
			return answers.pop();
		}
		return null;
	}
	
	/**
	 * @return a copy of all the current Answers
	 */
	@SuppressWarnings("unchecked")
	public Iterable<Answer> getAnswers()
	{
		return (Iterable<Answer>) answers.clone();
	}

	/**
	 * Set this Question to be unanswered. <strong>WARNING:</strong> only use
	 * this in situations in which you want to reset everything (for example,
	 * when restarting a survey).
	 */
	public void prime()
	{
		answered = false;
	}
}
