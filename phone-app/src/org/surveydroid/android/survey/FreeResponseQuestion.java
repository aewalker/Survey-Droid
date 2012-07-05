/*---------------------------------------------------------------------------*
 * FreeResponseQuestion.java                                                 *
 *                                                                           *
 * Type of question that allows the subject to enter their own text.         *
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
 * Question that allows the subject to enter their own text as a response.
 * 
 * @author Austin Walker
 */
public class FreeResponseQuestion extends Question
{
	/**
	 * Constructor
	 * 
	 * @param text - the question text
	 * @param id - the question id
	 * @param b - the {@link Branch}es from this question
	 * @param ctxt - the {@link Context} (used for database calls)
	 */
	public FreeResponseQuestion(String text, int id, Collection<Branch> b,
			Context ctxt)
	{
		super(text, id, b, SurveyDroidDB.QuestionTable.FREE_RESPONSE, ctxt);
	}
	
	public Answer answer(String text)
	{
		Answer newAnswer = new Answer(this, id, text);
		super.answer(newAnswer);
		return newAnswer;
	}
}
