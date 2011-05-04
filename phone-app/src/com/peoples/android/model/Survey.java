/*---------------------------------------------------------------------------*
 * Survey.java                                                               *
 *                                                                           *
 * Model for a PEOPLES survey.  Self-contained class that can be used to     *
 * initialize surveys from the database and run through them.                *
 *---------------------------------------------------------------------------*/
package com.peoples.android.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.peoples.android.database.PeoplesDB;
import com.peoples.android.database.SurveyDBHandler;
import com.peoples.android.server.Push;

/**
 * The highest-level survey-related class.  A survey object contains everything
 * needed to administer a given survey to subjects.  For a summary of how to
 * use this class, see the model-design document.
 * 
 * @author Diego Vargas
 * @author Tony Xiao
 * @author Austin Walker
 */
public class Survey implements Serializable
{
	//the database helper instance and Context
	private final SurveyDBHandler db;
	private final Context ctxt;
	
	//Android log stuff
	private final boolean D = true;
	private final String TAG = "SURVEY";
	
	//the survey name
	private final String name;
	
	//the first question in the survey
	private final Question firstQ;
	
	//the current Question object
	private Question currentQ;
	
	//the current Question's previously given Answer;
	private Answer currentAns;
	
	//a registry of live Answers for Conditions to look at
	private final Stack<Answer> registry = new Stack<Answer>();
	
	//the Question history via a stack
	private final Stack<Question> history = new Stack<Question>();
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Create a new survey by fetching the needed information from the
	 * database and initializing the child objects.
	 * 
	 * @param id - the survey_id from the database to base this Survey on
	 * @param ctxt - the current Context
	 * 
	 * @throws IllegalArgumentException if the survey with id does not exist
	 * @throws IllegalArgumentException if any of the required columns do not
	 * exist in the database or can't be found in the results set
	 * 
	 * @see SurveyDBHandler
	 * @see PeoplesDB
	 */
	public Survey(int id, Context ctxt)
	{
		/*
		 * Just a friendly warning: this constructor and it's helpers are
		 * just really ugly.  I (Austin) don't think there's much that can
		 * be done about it; it is just a really complicated data structure to
		 * initialize.  The rationale behind the design is the need for the UI
		 * to be snappy.  By doing all the work in the constructor, a Survey
		 * can be built once, and then the subject can be notified that it is
		 * time to take the survey.  At that point, all operations are constant
		 * time, so the subject gets a very responsive UI.
		 */
		
		//open up a database helper
		this.ctxt = ctxt;
		db = new SurveyDBHandler(ctxt);
		db.openRead();
		
		//start out by getting the survey level stuff done
		Cursor s = db.getSurvey(id);
		if (!s.moveToFirst())
			throw new IllegalArgumentException("no such survey");
		name = s.getString(
				s.getColumnIndexOrThrow(PeoplesDB.SurveyTable.NAME));
		int firstQID = s.getInt(
				s.getColumnIndexOrThrow(PeoplesDB.SurveyTable.QUESTION_ID));
		s.close();
		
		//set up a bunch of data structures to help out
		Map<Integer, Question> qMap = new HashMap<Integer, Question>();
		Map<Integer, Choice> cMap = new HashMap<Integer, Choice>();
		Map<Integer, Boolean> seen = new HashMap<Integer, Boolean>();
		Queue<Integer> toDo = new LinkedList<Integer>();
		Collection<Branch> bList = new LinkedList<Branch>();
		Collection<Condition> cList = new LinkedList<Condition>();
		
		//set up the first question, then iterate until done
		firstQ = setUpQuestion(firstQID, qMap, cMap, seen, bList, cList, toDo);
		Log.d("Survey", "First question Setup");
		currentQ = firstQ;
		while (!toDo.isEmpty())
		{
			if (D) Log.d(TAG, "next question");
			setUpQuestion(toDo.remove(),
					qMap, cMap, seen, bList, cList, toDo);
		}
		db.close();
		
		//now that we have a complete Question mapping, go back and set
		//all the Branches and Conditions
		for (Branch branch : bList)
		{
			branch.setQuestion(qMap);
		}
		Log.d("Survey", "branch Setup");
		for (Condition condition : cList)
		{
			condition.setQuestion(qMap);
		}
		Log.d("Survey", "condition Setup");
		
	}
	
	//set up the Question object with id
	private Question setUpQuestion(
			int id,
			Map<Integer, Question> qMap,
			Map<Integer, Choice> cMap,
			Map<Integer, Boolean> seen,
			Collection<Branch> bList,
			Collection<Condition> cList,
			Queue<Integer> toDo)
	{
		Cursor q = db.getQuestion(id);
		q.moveToFirst();
		String text = q.getString(
				q.getColumnIndexOrThrow(PeoplesDB.QuestionTable.Q_TEXT));
		q.close();
		
		//set up Branches
		Cursor b = db.getBranches(id);
		b.moveToFirst();
		Log.d("Survey", "I have this many branches " +  b.getCount());
		LinkedList<Branch> branches = new LinkedList<Branch>();
		while (!b.isAfterLast())
		{
			int b_id = b.getInt(
					b.getColumnIndexOrThrow(PeoplesDB.BranchTable._ID));
			int q_id = b.getInt(
					b.getColumnIndexOrThrow(PeoplesDB.BranchTable.NEXT_Q));
			if (!seen.containsKey(q_id))
            {
                seen.put(q_id, true);
                toDo.add(q_id);
            }
			branches.add(new Branch(q_id,
					getConditions(b_id, cMap, seen, toDo, cList)));
			b.moveToNext();
		}
		for (Branch branch : branches)
		{
			bList.add(branch);
		}
		b.close();
		
		//set up Choices
		Cursor ch = db.getChoices(id);
		ch.moveToFirst();
		Log.d("Survey", "I have this many choices " +  ch.getCount());
		LinkedList<Choice> choices = new LinkedList<Choice>();
		while (!ch.isAfterLast())
		{
			choices.add(getChoice(ch.getInt(ch.getColumnIndexOrThrow(
							PeoplesDB.ChoiceTable._ID)),
							cMap));
			ch.moveToNext();
		}
		ch.close();
		Log.d("Survey", "Building new question");
		//finally, create the new Question
		Question newQ = new Question(text, id, branches, choices, ctxt);
		qMap.put(id, newQ);
		return newQ;
	}
	
	//gets a Branch's Conditions by branch_id
	private Collection<Condition> getConditions(
			int id,
			Map<Integer, Choice> cMap,
			Map<Integer, Boolean> seen,
			Queue<Integer> toDo,
			Collection<Condition> cList)
	{
		Collection<Condition> conditions = new LinkedList<Condition>();
		Cursor c = db.getConditions(id);
		c.moveToFirst();
		while (!c.isAfterLast())
		{
			int q_id = c.getInt(c.getColumnIndexOrThrow(
					PeoplesDB.ConditionTable. QUESTION_ID));
			int t = c.getInt(c.getColumnIndexOrThrow(
					PeoplesDB.ConditionTable.TYPE));
			int c_id = c.getInt(c.getColumnIndexOrThrow(
					PeoplesDB.ConditionTable.CHOICE_ID));
			Condition newC = new Condition(q_id, getChoice(c_id, cMap), t, registry);
			conditions.add(newC);
			cList.add(newC);
			c.moveToNext();
		}
		c.close();
		return conditions;
	}
	
	//get the Choice corresponding to id
	private Choice getChoice(int id, Map<Integer, Choice> cMap)
	{
		if (cMap.containsKey(id))
		{
			return cMap.get(id);
		}
		Cursor c = db.getChoice(id);
		c.moveToFirst();
		Choice newC = new Choice(c.getString(
				c.getColumnIndexOrThrow(PeoplesDB.ChoiceTable.CHOICE_TEXT)),
				id, ctxt);
		cMap.put(id, newC);
		c.close();
		return newC;
	}
	
	/**
	 * A simple constructor to put together a sample survey.
	 * 
	 * @deprecated
	 */
	public Survey(Context ctxt)
	{
		this.ctxt = ctxt;
		db = null;
		
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
				choicesList.add(new Choice(choice, 0, ctxt));
			}
			LinkedList<Branch> branches = new LinkedList<Branch>();
			if (prevQ != null)
			{
				Branch b = new Branch(i, new LinkedList<Condition>());
				Map<Integer, Question> qMap = new HashMap<Integer, Question>();
				qMap.put(i, prevQ);
				b.setQuestion(qMap);
				branches.add(b);
			}
			prevQ = new Question(questionTexts[i], i, branches, choicesList, ctxt);
		}
		firstQ = prevQ;
		currentQ = prevQ;
		name = "Test Survey";
		if (firstQ == null) throw new RuntimeException("null question");
		if (firstQ.getChoices().length == 0) throw new RuntimeException("no choices");
	}
	
	/*-----------------------------------------------------------------------*/
	
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
		if (currentQ != null)
		{
			return currentQ.getChoices();
		}
		return new Choice[0];
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
		if (!currentAns.getQuestion().equals(currentQ))
			throw new RuntimeException("questions aren't equal");
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
		currentAns = null;
		if (currentQ != null)
		{
			currentQ.prime();
		}
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
		currentAns = registry.pop();
		currentQ.popAns();
	}
	
	/**
	 * Is the Survey on the first Question?  Useful when choosing to whether or
	 * not to display a back button.
	 * 
	 * @return true or false
	 */
	public boolean isOnFirst()
	{
		if (currentQ == null) return false;
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
	public boolean submit()
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
		
		//TODO: after fix2, is this still current? (Diego)

		//TODO this should be done by the master service
		//try to upload answers to the server
		//write record of original scheduled time,
		//vs actual time of completion

		if (!Push.pushAnswers(ctxt))
		    worked = false;
		
		//wipe the Question history
		while (!history.empty())
		{
			history.pop().popAns();
		}
		
		return worked;
	}
	
	/**
	   * Always treat de-serialization as a full-blown constructor, by
	   * validating the final state of the de-serialized object.
	   */
	   private void readObject(
	     ObjectInputStream aInputStream
	   ) throws ClassNotFoundException, IOException {
	     //always perform the default de-serialization first
	     aInputStream.defaultReadObject();

	     //make defensive copy of the mutable Date field
	     //fDateOpened = new Date( fDateOpened.getTime() );

	     //ensure that object state has not been corrupted or tampered with maliciously
	     //validateState();
	  }

	    /**
	    * This is the default implementation of writeObject.
	    * Customise if necessary.
	    */
	    private void writeObject(
	      ObjectOutputStream aOutputStream
	    ) throws IOException {
	      //perform the default serialization for all non-transient, non-static fields
	      aOutputStream.defaultWriteObject();
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
