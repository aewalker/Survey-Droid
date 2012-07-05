/*---------------------------------------------------------------------------*
 * Question.java                                                             *
 *                                                                           *
 * Model for a survey question.  Has Branches to other questions, and some   *
 * basic information and functionality.  This is not a full class, it is     *
 * meant to be extended for specific types of questions.                     *
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
package org.surveydroid.android.survey;

import java.util.Collection;
import java.util.Stack;

import android.content.Context;

/**
 * Model for a survey question.
 * 
 * @author Diego Vargas
 * @author Austin Walker
 * @author Henry Liu
 */
public abstract class Question
{
	//have to keep the Question id to look up history in the DB
	protected final int id;
	
	//question text
	private final String q_text;
	
	//the answers that have been given for this question
	//(starts empty => no answer has been given)
	private final Stack<Answer> answers = new Stack<Answer>();
	
	/* Note: the reason that we have to use a Stack of Answers instead of just
	 * keeping the most recent one is looping.  A Survey could loop back to the
	 * same Question multiple times, so we have to keep track of that.
	 */

	//has the current Question been answered?
	private boolean answered = false;
	
	/**
	 * The type of this Question
	 * 
	 * @see org.surveydroid.android.database.SurveyDroidDB.QuestionTable
	 */
	protected int type;

	//set of branches
	private final Collection<Branch> branches;
	
	/** {@link Context} used to make database calls */
	protected final Context ctxt;
	
	/*-----------------------------------------------------------------------*/

	/**
	 * Create a new Question.  Should only be used by an extending class.
	 * 
	 * @param text - the question text as a String
	 * @param id - the Question's id from the database
	 * @param b - a {@link Collection} of {@link Branch}es for this Question
	 */

	protected Question(String text, int id, Collection<Branch> b,
			int type, Context ctxt)
	{
		q_text = text;
		branches = b;
		this.type = type;
		this.id = id;
		this.ctxt = ctxt;
	}

	/*-----------------------------------------------------------------------*/

	/**
	 * Get this Question's text
	 * 
	 * @return the Question's text as a String
	 */
	public String getText()
	{
		return q_text;
	}
	
	/**
	 * Get the type of this question.
	 * 
	 * @return an int corresponding to one of the types defined in
	 * {@link org.surveydroid.android.database.SurveyDroidDB#QuestionTable}
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * Answer this Question.  Does background work and should be called by
	 * any extending class.
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
		for (Branch b : branches)
		{
			if (b.eval()) return b.nextQuestion();
		}
		return null;
	}

	/*-----------------------------------------------------------------------*/

	/**
	 * Checks whether the question has ever been answered with a particular
	 * Choice.
	 * 
	 * @param c - the {@link Choice} to have been
	 * 
	 * @return true or false
	 */
	public boolean hasEverBeen(Choice c)
	{
		return c.hasEverBeen(id);
	}

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
	 * Set this Question to be unanswered. <strong>WARNING:</strong> only use
	 * this in situations in which you want to reset everything (for example,
	 * when restarting a survey).
	 */
	public void prime()
	{
		answered = false;
	}
}
