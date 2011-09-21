/*---------------------------------------------------------------------------*
 * SlidingScaleTextQuestion.java                                             *
 *                                                                           *
 * Type of question that allows the subject pick a value between 1 and 100   *
 * by moving a slider with text on each end.                                 *
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
public class SlidingScaleTextQuestion extends Question
{
	//text that goes on the top and bottom of the scale
	private final String textHigh;
	private final String textLow;
	
	/**
	 * Constructor
	 * 
	 * @param text - the question text
	 * @param id - the question id
	 * @param b - the {@link Branch}es from this question
	 * @param textLow - text for the low end of the scale
	 * @param textHigh - text for the high end of the scale
	 * @param ctxt - the {@link Context} (used for database calls)
	 */
	public SlidingScaleTextQuestion(String text, int id, Collection<Branch> b,
			String textLow, String textHigh, Context ctxt)
	{
		super(text, id, b, SurveyDroidDB.QuestionTable.SCALE_TEXT, ctxt);
		
		this.textLow = textLow;
		this.textHigh = textHigh;
	}
	
	/**
	 * Returns the String that should be used for the low end of the scale.
	 * 
	 * @return the low text
	 */
	public String getLowText()
	{
		return textLow;
	}
	
	/**
	 * Returns the String that should be used for the high end of the scale.
	 * 
	 * @return the high text
	 */
	public String getHighText()
	{
		return textHigh;
	}
	
	public Answer answer(int val)
	{
		if (val < 0 || val > 100)
			throw new IllegalArgumentException(
					"Scale value " + val + " is not between 0 and 100");
		Answer newAnswer = new Answer(this, id, val, ctxt);
		super.answer(newAnswer);
		return newAnswer;
	}
}
