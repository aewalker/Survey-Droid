/*---------------------------------------------------------------------------*
 * FreeResponseQuestion.java                                                 *
 *                                                                           *
 * Type of question that allows the subject to enter their own text.         *
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
		super(text, id, b, PeoplesDB.QuestionTable.FREE_RESPONSE, ctxt);
	}
	
	public Answer answer(String text)
	{
		Answer newAnswer = new Answer(this, id, text, ctxt);
		super.answer(newAnswer);
		return newAnswer;
	}
}
