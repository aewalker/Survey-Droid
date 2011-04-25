/*---------------------------------------------------------------------------*
 * Survey.java                                                               *
 *                                                                           *
 * Model for a PEOPLES survey.  Self-contained class that can be used to     *
 * initialize surveys from the database and run through them.                *
 *---------------------------------------------------------------------------*/
package com.peoples.android.model;

import java.util.LinkedList;
import java.util.Stack;
import com.peoples.android.database.SurveyDBHandler;

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
	
	//fake constructor for testing only
	public Survey()
	{
		String[] questionTexts =
		{
			"Who is your favorite actress?",
			"What is your favorite color",
			"What is your favorite animal?",
			"How old are you?",
			"What country are you from?"
		};
		
		String[][] choices =
		{
			{"Keira Knightley", "Natalie Portman", "Emmanuelle Chiriqui"},
			{"Red", "Blue", "Green", "Purple"},
			{"Panda", "Tiger", "Penguin"},
			{"10", "24", "33"},
			{"United States", "Canada", "Turkey"}
		};
		
		//new Question(String text, int id, Collection<Branch> b, Collection<Choice> c)
		//new Branch(Question q, Collection<Condition> c)
		//new Choice(String text, int id)
		
		Question prevQ = null;
		for (int i = questionTexts.length - 1; i >= 0; i--)
		{
			LinkedList<Choice> choicesList = new LinkedList<Choice>();
			for (String choice : choices[i])
			{
				choicesList.add(new Choice(choice, 0));
			}
			LinkedList<Branch> branches = new LinkedList<Branch>();
			if (prevQ != null)
			{
				branches.add(new Branch(prevQ, new LinkedList<Condition>()));
			}
			prevQ = new Question(questionTexts[i], 0, branches, choicesList);
		}
		firstQ = prevQ;
		currentQ = prevQ;
		name = "Test Survey";
		if (firstQ == null) throw new RuntimeException("null question");
		if (firstQ.getChoices().length == 0) throw new RuntimeException("no choices");
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
	 * Get this Survey's name/title
	 * 
	 * @return the name/title as a String
	 */
	public String getName()
	{
		return name;
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
	 * Get an array of Strings corresponding to the text of the Choices for the
	 * current Question.
	 * 
	 * @return array of Choice texts as Strings
	 */
	public String[] getChoiceTexts()
	{
		Choice[] choices = currentQ.getChoices();
		String[] choiceTexts = new String[choices.length];
		for (int i = 0; i < choices.length; i++)
		{
			choiceTexts[i] = choices[i].getText();
		}
		return choiceTexts;
	}
	
	/**
	 * Get the current Question's text
	 * 
	 * @return the current Question's text as a String
	 */
	public String getText()
	{
		return currentQ.getText();
	}
	
	/**
	 * Get the current Question's current Answer's Choice's index
	 * 
	 * @return the index as an int, or -1 if this was a free response Question
	 * or no Choice has yet been selected.
	 * 
	 * @throws RuntimeException on internal error
	 */
	public int getAnswerChoice()
	{
		if (currentAns == null)
		{
			return -1;
		}
		Choice choice = currentAns.getChoice();
		if (choice != null) //multiple choice
		{
			int i = 0;
			for (Choice c : currentQ.getChoices())
			{
				if (c.equals(choice))
				{
					return i;
				}
				i++;
			}
			throw new RuntimeException(
					"something is very wrong: choice not found");
		}
		else return -1;
	}
	
	/**
	 * Get the current Question's current Answer's text
	 * 
	 * @return the text as a String, or the empty string if no answer has
	 * been given yet, or null if this was a multiple choice question
	 */
	public String getAnswerText()
	{
		if (currentAns == null)
		{
			return "";
		}
		return currentAns.getText();
	}
	
	/**
	 * Move the Survey to the next Question.
	 */
	public void nextQuestion()
	{
		history.push(currentQ);
		currentQ = currentQ.nextQuestion();
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
	 * Is the Survey on the first Question?  Useful when choosing to whether or
	 * not to display a back button.
	 * 
	 * @return true or false
	 */
	public boolean isOnFirst()
	{
		return (currentQ.equals(firstQ));
	}
	
	/**
	 * Answer a  multiple choice Question.
	 * 
	 * @param c - the Choice to answer with
	 */
	public void answer(Choice c)
	{
		registry.push(currentQ.answer(c));
	}
	
	/**
	 * Answer a free response Question.
	 * 
	 * @param text - the String to answer with
	 */
	public void answer(String text)
	{
		registry.push(currentQ.answer(text));
	}
	
	/**
	 * Finalize the Answers to this Survey and enter them in the database. Also
	 * deletes all given answers after they are written to the database, leaving
	 * this Survey in it's original form.
	 * 
	 * @return true on success
	 */
	public boolean sumbit()
	{
		boolean worked = true;
		
		//save all the live Answers
		while (!registry.empty())
		{
			if (registry.pop().write() == false)
			{
				worked = false;
			}
		}
		
		//wipe the Question history
		while (!history.empty())
		{
			history.pop().popAns();
		}
		
		return worked;
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
