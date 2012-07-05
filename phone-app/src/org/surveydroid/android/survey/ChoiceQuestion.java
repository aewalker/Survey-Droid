/*---------------------------------------------------------------------------*
 * ChoiceQuestion.java                                                       *
 *                                                                           *
 * Type of question that allows the subject to pick from a list of choices.  *
 * Can be set to allow only a single choice selection or multiple.           *
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

import org.surveydroid.android.database.SurveyDroidDB;

import android.content.Context;

/**
 * Question that allows the subject to select from a pre-defined set of
 * answers (either just one or multiple).
 * 
 * @author Austin Walker
 */
public class ChoiceQuestion extends Question
{
	//choices offered by this question
	private final Collection<Choice> choices;
	
	/**
	 * Constructor
	 * 
	 * @param text - the question text
	 * @param id - the question id
	 * @param b - a collection of branches from this question
	 * @param c - a collection of choices for this question
	 * @param multi - does this question allow multiple answers
	 * @param ctxt - the current context
	 */
	public ChoiceQuestion(String text, int id, Collection<Branch> b,
			Collection<Choice> c, boolean multi, Context ctxt)
	{
		super(text, id, b, SurveyDroidDB.QuestionTable.SINGLE_CHOICE, ctxt);
		if (multi)
		{
			type = SurveyDroidDB.QuestionTable.MULTI_CHOICE;
		}
		choices = c;
	}
	
	/**
	 * Returns an array of this question's choices.
	 * 
	 * @return an array of the {@link Choice}s
	 */
	public Choice[] getChoices()
	{
		//shortcoming of the generics system; can't just say:
		// return (Choice[]) choices.toArray();
		Choice[] c = new Choice[0];
		return choices.toArray(c);
	}
	
	/**
	 * Does this question allow multiple answers?
	 * 
	 * @return true if it does
	 */
	public boolean isMulti()
	{
		if (type == SurveyDroidDB.QuestionTable.SINGLE_CHOICE)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * Answer this question with the given set of choices.
	 * 
	 * @param c - the {@link Choice}s to answer with
	 * 
	 * @return the newly made {@link Answer}
	 */
	public Answer answer(Collection<Choice> c)
	{
		if (type == SurveyDroidDB.QuestionTable.SINGLE_CHOICE && c.size() > 1)
		{
			throw new IllegalArgumentException(
					"Question only allows a single answer");
		}
		Answer newAns = Choice.answer(this, id, c, ctxt);
		super.answer(newAns);
		return newAns;
	}
}
