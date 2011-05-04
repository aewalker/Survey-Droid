/*---------------------------------------------------------------------------*
 * Question.java                                                             *
 *                                                                           *
 * Model for a survey question.  Has Choices and Branches along with some    *
 * functionality for generating answers and progressing the survey.          *
 *---------------------------------------------------------------------------*/
package com.peoples.android.model;

import java.util.Collection;
import java.util.Stack;

/**
 * Model for a survey Question.  Based on the SQL:
 * 
 * CREATE TABLE questions (
 *  id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
 *  text TEXT);
 * 
 * @author Diego Vargas
 * @author Austin Walker
 */
public class Question
{
	//have to keep the Question id to look up history in the DB
	private int id;

	//question text
	private String q_text;

	//the answers that have been given for this question
	//(starts empty => no answer has been given)
	private Stack<Answer> answers = new Stack<Answer>();

	/* Note: the reason that we have to use a Stack of Answers instead of just
	 * keeping the most recent one is looping.  A Survey could loop back to the
	 * same Question multiple times, so we have to keep track of that.
	 */

	//has the current Question been answered?
	private boolean answered = false;

	//set of branches
	private Collection<Branch> branches;

	//set of choices
	private Collection<Choice> choices;

	/*-----------------------------------------------------------------------*/

	/**
	 * Create a new Question
	 * 
	 * @param text - the question text as a String
	 * @param id - the Question's id from the database
	 * @param b - a Collection of Branches for this Question
	 * @param c - a Collection of Choices for this Question
	 */
	public Question(String text, int id, Collection<Branch> b, Collection<Choice> c)
	{
		q_text = text;
		branches = b;
		choices = c;
		this.id = id;
	}

	/*-----------------------------------------------------------------------*/

	/**
	 * Get all this Question's Choices as an array
	 * 
	 * @return all Choices as an array
	 */
	public Choice[] getChoices()
	{
		//little hack to get the right types
		Choice[] cArray = new Choice[0];
		return choices.toArray(cArray);
	}

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
	 * Answer this Question.  This should (and can) only be used to provide
	 * an answer if this is a free response question (ie has no Choices).
	 * 
	 * @param text - the text to answer with
	 * 
	 * @return the Answer created
	 * 
	 * @throws RuntimeException if called when there are Choices
	 * @throws RuntimeException if the current Question has already been
	 * answered.
	 */
	public Answer answer(String text)
	{
		if (answered) throw new RuntimeException(
				"atempt to answer the same Question multiple times");

		if (choices.size() != 0)
		{
			throw new RuntimeException(
					"call to answer() on a non-free response question");
		}
		else
		{
			Answer newAnswer = new Answer(this, id, null, 0, text, null);
			answers.push(newAnswer);
			answered = true;
			return newAnswer;
		}
	}

	/**
	 * Answer this Question.  This should (and can) only be used to provide
	 * an answer if this is a multiple choice question.
	 * 
	 * @param c - the Choice to answer with
	 * 
	 * @return the Answer created
	 * 
	 * @throws RuntimeException if called when there are no Choices
	 * @throws RuntimeException if given an invalid Choice
	 * @throws RuntimeException if the current Question has already been
	 * answered.
	 */
	public Answer answer(Choice c)
	{
		if (answered) throw new RuntimeException(
				"atempt to answer the same Question multiple times");

		if (choices.size() == 0)
		{
			throw new RuntimeException(
					"call to answer() on a multiple-choice question");
		}
		else
		{
			if (!choices.contains(c))
			{
				throw new RuntimeException("invalid Choice");
			}
			else
			{
				Answer newAnswer = c.answer(this, this.id);
				answers.push(newAnswer);
				answered = true;
				return newAnswer;
			}
		}
	}

	/**
	 * Evaluate Branches to find the next Question.
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
	 * @param c - the Choice to have been
	 * 
	 * @return true or false
	 */
	public boolean hasEverBeen(Choice c)
	{
		return c.hasEverBeen(id);
	}

	/**
	 * Checks that the question has never been answered with a particular
	 * Choice.
	 * 
	 * @param c - the Choice to have been
	 * 
	 * @return true or false
	 */
	public boolean hasNeverBeen(Choice c)
	{
		return c.hasEverBeen(id);
	}

	/**
	 * Remove the most recent Answer from the stack.
	 * 
	 * @return the most recent Answer
	 */
	public Answer popAns()
	{
		return answers.pop();
	}

	public void prime() {
		// TODO Auto-generated method stub
		
	}
}