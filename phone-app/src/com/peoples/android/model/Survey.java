/*---------------------------------------------------------------------------*
 * Survey.java                                                               *
 *                                                                           *
 * Model for a PEOPLES survey.  Self-contained class that can be used to     *
 * initialize surveys from the database and run through them.                *
 *---------------------------------------------------------------------------*/
package com.peoples.android.model;

import java.util.Stack;

//import org.json.JSONArray;
//import org.json.JSONObject;

/**
 * The highest-level survey-related class.  A survey object contains everything
 * needed to administer a given survey to subjects.
 * 
 * @author Diego Vargas
 * @author Tony Xaio
 * @author Austin Walker
 */
public class Survey
{
	//the survey name
	private String name;

	//The survey's creation time
	//TODO probably don't need this
	//private String created;
	
	//the first question in the survey
	private Question firstQ;
	
	//TODO I think this should be handled somewhere else
	//field for each day; holds times in 24 hour format separated by commas
	//private String mo, tu, we, th, fr, sa, su;
	
	//the current Question object
	private Question currentQ;
	
	//the current Question's previously given Answer;
	private Answer currentAns;
	
	//a registry of live Answers for Conditions to look at
	private Stack<Answer> registry = new Stack<Answer>();
	
	//the Question history via a stack
	private Stack<Question> history = new Stack<Question>();
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Create a new survey by fetching the needed information from the
	 * database and initializing the child objects.
	 * 
	 * @param id - the survey_id from the database to base this Survey on
	 */
	public Survey(int id)
	{
		//TODO database stuff
	}
	
	/**
	 * Is the Survey over?
	 * 
	 * @return true if the Survey has no more Questions to ask.
	 */
	public boolean done()
	{
		if (currentQ == null)
			return true;
		return false;
	}
	
	/**
	 * Get the current Question's Choices.
	 * 
	 * @return the current Question's Choices as an array
	 */
	public Choice[] getChoices()
	{
		return currentQ.getChoices();
	}
	
	/**
	 * Get the current Question's current Answer
	 * 
	 * @return null if the Question has never been answered, Answer given
	 * if it has been
	 */
	public Answer getAnswer()
	{
		return currentAns;
	}
	
	/**
	 * Move the Survey to the next Question.
	 */
	public void nextQuestion()
	{
		history.push(currentQ);
		currentQ = currentQ.nextQusetion();
	}
	
	/**
	 * Move the Survey back one Question.
	 * 
	 * @throws RuntimeException the current Question is the first Question
	 */
	public void prevQuestion()
	{
		if (isOnFirst()) throw new RuntimeException("no previous Question");
		currentQ = history.pop();
		currentAns = currentQ.popAns();
		registry.pop();
	}
	
	/**
	 * Is the Survey on the first Question?  Useful when chosing to whether or
	 * not to display a back button.
	 * 
	 * @return true or false
	 */
	public boolean isOnFirst()
	{
		return (currentQ.equals(firstQ))
	}
	
	/**
	 * Answer a Question.
	 * 
	 * @param q - the Question to answer
	 * @param c - the Choice to answer with
	 * @param text - the text to answer with
	 * 
	 * @throws RuntimeException if either c and text are null or both are
	 * not null.  For a free response Question, c should be null, and for a
	 * multiple-choice Question, text should be null.
	 */
	public void answer(Question q, Choice c, String text)
	{
		//free response
		if (c == null)
		{
			if (text == null) throw new RuntimeException(
					"must give either a Choice or answer text");
			registry.push(currentQ.answer(text));
		}
		//multiple choice
		else
		{
			if (text != null) throw new RuntimeException(
					"cannot give both a Choice and answer text");
			registry.push(currentQ.answer(c));
		}
	}
	
	/**
	 * Finalize the Answers to this Survey and enter them in the database.
	 */
	public void sumbit()
	{
		//TODO database stuff
	}
	
	
	//TODO I think this should be handled in the communication manager
	/*
	public JSONArray getAnswersAsJson()
	{
	    JSONArray answers = new JSONArray();
	    for (Question q : getQuestions())
	    {
	        answers.put(q.answer.getAsJson());
	    }
	    return answers;
	}*/
}
