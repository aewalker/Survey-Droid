/*---------------------------------------------------------------------------*
 * SlidingScaleTextQuestion.java                                             *
 *                                                                           *
 * Type of question that allows the subject pick a value between 1 and 100   *
 * by moving a slider with text on each end.                                 *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import java.util.Collection;

import org.peoples.android.database.PeoplesDB;

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
	 * @param b - the Branches from this question
	 * @param textLow - text for the low end of the scale
	 * @param textHigh - text for the high end of the scale
	 * @param ctxt - the context (used for database calls)
	 */
	public SlidingScaleTextQuestion(String text, int id, Collection<Branch> b,
			String textLow, String textHigh, Context ctxt)
	{
		super(text, id, b, PeoplesDB.QuestionTable.SCALE_TEXT, ctxt);
		
		this.textLow = Survey.processText(ctxt, textLow);
		this.textHigh = Survey.processText(ctxt, textHigh);
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
