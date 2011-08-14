/*---------------------------------------------------------------------------*
 * ChoiceQuestion.java                                                       *
 *                                                                           *
 * Type of question that allows the subject to pick from a list of choices.  *
 * Can be set to allow only a single choice selection or multiple.           *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import java.util.Collection;

import org.peoples.android.database.PeoplesDB;

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
		super(text, id, b, PeoplesDB.QuestionTable.SINGLE_CHOICE, ctxt);
		if (multi)
		{
			type = PeoplesDB.QuestionTable.MULTI_CHOICE;
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
		if (type == PeoplesDB.QuestionTable.SINGLE_CHOICE)
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
		if (type == PeoplesDB.QuestionTable.SINGLE_CHOICE && c.size() > 1)
		{
			throw new IllegalArgumentException(
					"Question only allows a single answer");
		}
		Answer newAns = Choice.answer(this, id, c, ctxt);
		super.answer(newAns);
		return newAns;
	}
}
