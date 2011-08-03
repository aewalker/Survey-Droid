/*---------------------------------------------------------------------------*
 * Survey.java                                                               *
 *                                                                           *
 * Model for a PEOPLES survey.  Self-contained class that can be used to     *
 * initialize surveys from the database and run through them.                *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.peoples.android.database.PeoplesDB;
import org.peoples.android.database.SurveyDBHandler;
import org.peoples.android.Config;
import org.peoples.android.R;
import org.peoples.android.Util;

/**
 * The highest-level survey-related class.  A survey object contains everything
 * needed to administer a given survey to subjects.  For a summary of how to
 * use this class, see the model-design document.
 * 
 * @author Diego Vargas
 * @author Tony Xiao
 * @author Austin Walker
 */
public class Survey
{
	//the database helper instance and Context
	private final SurveyDBHandler db;
	private final Context ctxt;
	
	//this survey's id
	private final int id;

	//Android log stuff
	private final String TAG = "Survey";

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
	
	//the id of the row in the extras table for the current run of this survey
	private int row = 0;
	
	//does this survey have a photo with it?
	private boolean hasPhoto = false;
	
	//does this survey have a voice recording with it?
	private boolean hasVoice = false;

	/*-----------------------------------------------------------------------*/
	/**
	 * Create a new survey by fetching the needed information from the
	 * database and initializing the child objects.
	 * 
	 * @param id - the survey_id from the database to base this Survey on
	 * @param ctxt - the current {@link Context}
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
		
		this.id = id;

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
		Util.d(null, TAG, "First question Setup");
		currentQ = firstQ;
		while (!toDo.isEmpty())
		{
			Util.v(null, TAG, "next question");
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
		Util.v(null, TAG, "branch Setup");
		for (Condition condition : cList)
		{
			condition.setQuestion(qMap);
		}
		Util.v(null, TAG, "condition Setup");

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
		//set up Branches
		Cursor b = db.getBranches(id);
		b.moveToFirst();
		Util.v(null, TAG, "I have this many branches " +  b.getCount());
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
		Util.v(null, TAG, "I have this many choices " +  ch.getCount());
		LinkedList<Choice> choices = new LinkedList<Choice>();
		while (!ch.isAfterLast())
		{
			choices.add(getChoice(ch.getInt(ch.getColumnIndexOrThrow(
							PeoplesDB.ChoiceTable._ID)),
							cMap));
			ch.moveToNext();
		}
		ch.close();
		Util.v(null, TAG, "Building new question");
		//finally, create the new Question
		Cursor q = db.getQuestion(id);
		q.moveToFirst();
		String text = q.getString(
				q.getColumnIndexOrThrow(PeoplesDB.QuestionTable.Q_TEXT));
		int q_type = q.getInt(
				q.getColumnIndexOrThrow(PeoplesDB.QuestionTable.Q_TYPE));
		Question newQ;
		switch (q_type)
		{
		case PeoplesDB.QuestionTable.FREE_RESPONSE:
			newQ = new FreeResponseQuestion(text, id, branches, ctxt);
			break;
		case PeoplesDB.QuestionTable.MULTI_CHOICE:
			newQ = new ChoiceQuestion(text, id, branches, choices, true, ctxt);
			break;
		case PeoplesDB.QuestionTable.SINGLE_CHOICE:
			newQ = new ChoiceQuestion(text, id, branches, choices, false, ctxt);
			break;
		case PeoplesDB.QuestionTable.SCALE_TEXT:
			newQ = new SlidingScaleTextQuestion(text, id, branches,
					q.getString(q.getColumnIndexOrThrow(
							PeoplesDB.QuestionTable.Q_SCALE_TEXT_LOW)),
					q.getString(q.getColumnIndexOrThrow(
							PeoplesDB.QuestionTable.Q_SCALE_TEXT_HIGH)),
					ctxt);
			break;
		case PeoplesDB.QuestionTable.SCALE_IMG:
			newQ = new SlidingScaleImgQuestion(text, id, branches,
				q.getString(q.getColumnIndexOrThrow(
					PeoplesDB.QuestionTable.Q_SCALE_IMG_LOW)).toCharArray(),
				q.getString(q.getColumnIndexOrThrow(
					PeoplesDB.QuestionTable.Q_SCALE_IMG_HIGH)).toCharArray(),
				ctxt);
			break;
		default:
			throw new IllegalStateException("Invlaid question type: " + q_type);
		}
		q.close();
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
		Choice newC;
		switch (c.getInt(c.getColumnIndexOrThrow(
				PeoplesDB.ChoiceTable.CHOICE_TYPE)))
		{
		case PeoplesDB.ChoiceTable.TEXT_CHOICE:
			newC = new Choice(c.getString(c.getColumnIndexOrThrow(
					PeoplesDB.ChoiceTable.CHOICE_TEXT)), id, ctxt);
			break;
		case PeoplesDB.ChoiceTable.IMG_CHOICE:
			//be careful about which constructor this is!
			newC = new Choice(c.getString(c.getColumnIndexOrThrow(
				PeoplesDB.ChoiceTable.CHOICE_IMG)).toCharArray(), ctxt, id);
			break;
		default:
			throw new IllegalStateException("Invlaid choice type");
		}
		cMap.put(id, newC);
		c.close();
		return newC;
	}

	/**
	 * A simple constructor to put together a sample survey.
	 * 
	 * @param ctxt - the current Context
	 */
	public Survey(Context ctxt)
	{
		this.ctxt = ctxt;
		db = null;
		id = 0;

		Question prevQ = null;
		for (int i = 4; i >= 0; i--)
		{
			LinkedList<Branch> branches = new LinkedList<Branch>();
			if (prevQ != null)
			{
				Branch b = new Branch(i, new LinkedList<Condition>());
				Map<Integer, Question> qMap = new HashMap<Integer, Question>();
				qMap.put(i, prevQ);
				b.setQuestion(qMap);
				branches.add(b);
			}
			switch (i)
			{
			case 0:
				LinkedList<Choice> choicesList0 = new LinkedList<Choice>();
				choicesList0.add(new Choice("Keira Knightley", 0, ctxt));
				choicesList0.add(new Choice("Natalie Portman", 0, ctxt));
				choicesList0.add(new Choice("Emmanuelle Chiriqui", 0, ctxt));
				prevQ = new ChoiceQuestion("Who is your favorite actress?", i,
						branches, choicesList0, false, ctxt);
				break;
			case 1:
				//this looks rather dumb; it's just a test to make sure the
				//encoding works properly
				LinkedList<Choice> choicesList1 = new LinkedList<Choice>();
				//blue
				Bitmap blueImg = BitmapFactory.decodeResource(
						ctxt.getResources(), R.drawable.blue_back_medium);
				ByteArrayOutputStream baosBlue = new ByteArrayOutputStream();
				blueImg.compress(Bitmap.CompressFormat.PNG, 100, baosBlue);
				choicesList1.add(new Choice(Base64Coder.encode(
						baosBlue.toByteArray()), ctxt, 0));
				
				//red
				Bitmap redImg = BitmapFactory.decodeResource(
						ctxt.getResources(), R.drawable.red_back_medium);
				ByteArrayOutputStream baosRed = new ByteArrayOutputStream();
				redImg.compress(Bitmap.CompressFormat.PNG, 100, baosRed);
				choicesList1.add(new Choice(Base64Coder.encode(
						baosRed.toByteArray()), ctxt, 0));
				
				//green
				Bitmap greenImg = BitmapFactory.decodeResource(
						ctxt.getResources(), R.drawable.green_back_medium);
				ByteArrayOutputStream baosGreen = new ByteArrayOutputStream();
				greenImg.compress(Bitmap.CompressFormat.PNG, 100, baosGreen);
				choicesList1.add(new Choice(Base64Coder.encode(
						baosGreen.toByteArray()), ctxt, 0));
				
				prevQ = new ChoiceQuestion("What are your favorite colors", i,
						branches, choicesList1, true, ctxt);
				break;
			case 2:
				Bitmap lowImg = BitmapFactory.decodeResource(
						ctxt.getResources(), R.drawable.sad);
				Bitmap highImg = BitmapFactory.decodeResource(
						ctxt.getResources(), R.drawable.happy);
				ByteArrayOutputStream baosLow = new ByteArrayOutputStream();
				ByteArrayOutputStream baosHigh = new ByteArrayOutputStream();
				lowImg.compress(Bitmap.CompressFormat.PNG, 100, baosLow);
				highImg.compress(Bitmap.CompressFormat.PNG, 100, baosHigh);
				prevQ = new SlidingScaleImgQuestion("How are you today?", i,
						branches, Base64Coder.encode(baosLow.toByteArray()),
						Base64Coder.encode(baosHigh.toByteArray()), ctxt);
				break;
			case 3:
				prevQ = new SlidingScaleTextQuestion("How old are you?", i,
						branches, "1", "100", ctxt);
				break;
			case 4:
				prevQ = new FreeResponseQuestion("What country are you from?",
						i, branches, ctxt);
				break;
			}
		}
		firstQ = prevQ;
		currentQ = prevQ;
		name = "Test Survey";
	}

	/*-----------------------------------------------------------------------*/

	/**
	 * Is the Survey over?
	 * 
	 * @return true if the Survey has no more questions to ask.
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
	 * Get the current question's choices.
	 * 
	 * @return the current question's choices as an array.  If the current
	 * question does not exist or is not of the correct type, then an empty
	 * array will be returned
	 */
	public Choice[] getChoices()
	{
		if (currentQ != null &&
				(currentQ.getType() == PeoplesDB.QuestionTable.MULTI_CHOICE ||
				 currentQ.getType() == PeoplesDB.QuestionTable.SINGLE_CHOICE))
		{
			ChoiceQuestion q = (ChoiceQuestion) currentQ;
			return q.getChoices();
		}
		return new Choice[0];
	}
	
	/**
	 * Get the current question's low-end text.
	 * 
	 * @return the current question's low-end text.  If the current
	 * question does not exist or is not a text-based scale question, then
	 * null will be returned.
	 */
	public String getLowText()
	{
		return getScaleText("low");
	}
	
	/**
	 * Get the current question's high-end text.
	 * 
	 * @return the current question's high-end text.  If the current
	 * question does not exist or is not a text-based scale question, then
	 * null will be returned.
	 */
	public String getHighText()
	{
		return getScaleText("high");
	}
	
	//gets the scale text for a question ("low" or "high")
	private String getScaleText(String where)
	{
		if (currentQ != null &&
				currentQ.getType() == PeoplesDB.QuestionTable.SCALE_TEXT)
		{
			SlidingScaleTextQuestion q = (SlidingScaleTextQuestion) currentQ;
			if (where.equals("low"))
			{
				return q.getLowText();
			}
			else if (where.equals("high"))
			{
				return q.getHighText();
			}
			else throw new IllegalArgumentException(
					"\"" + where + "\" is not a valid location");
		}
		return null;
	}
	
	/**
	 * Get the current question's low-end image.
	 * 
	 * @return the current question's low-end image as a Bitmap. If the current
	 * question does not exist or is not an image-based scale question, then
	 * null will be returned.
	 */
	public Bitmap getLowImg()
	{
		return getScaleImg("low");
	}
	
	/**
	 * Get the current question's high-end image.
	 * 
	 * @return the current question's high-end image as a Bitmap. If the current
	 * question does not exist or is not an image-based scale question, then
	 * null will be returned.
	 */
	public Bitmap getHighImg()
	{
		return getScaleImg("high");
	}
	
	//gets the scale image for a question ("low" or "high")
	private Bitmap getScaleImg(String where)
	{
		if (currentQ != null &&
				currentQ.getType() == PeoplesDB.QuestionTable.SCALE_IMG)
		{
			SlidingScaleImgQuestion q = (SlidingScaleImgQuestion) currentQ;
			if (where.equals("low"))
			{
				return q.getLowImg();
			}
			else if (where.equals("high"))
			{
				return q.getHighImg();
			}
			else throw new IllegalArgumentException(
					"\"" + where + "\" is not a valid location");
		}
		return null;
	}

	/**
	 * Get the current question's text
	 * 
	 * @return the current question's text as a String
	 */
	public String getText()
	{
		return currentQ.getText();
	}

	/**
	 * Get the current question's current answer's choice or choices' index or
	 * indices.
	 * 
	 * @return an array of ints corresponding to the indices, or null if this
	 * was a free response or scale question or if no choice has yet been
	 * selected.
	 * 
	 * @throws RuntimeException on internal error
	 */
	public int[] getAnswerChoices()
	{
		if (currentAns == null)
		{
			return null;
		}
		if (Config.D)
		{
			if (!currentAns.getQuestion().equals(currentQ))
				throw new RuntimeException("questions aren't equal");
		}
		Collection<Choice> choices = currentAns.getChoices();
		if (choices != null) //multiple choice
		{
			int[] toReturn = new int[choices.size()];
			int returnInd = 0; //position in return array
			ChoiceQuestion q = (ChoiceQuestion) currentQ;
			for (Choice c1 : choices)
			{
				int i = 0;
				for (Choice c2 : q.getChoices())
				{
					if (c1.equals(c2))
					{
						toReturn[returnInd] = i;
						returnInd++;
						break;
					}
					i++;
				}
			}
			return toReturn;
		}
		else return null;
	}

	/**
	 * Get the current question's current answer's text
	 * 
	 * @return the text as a String, or the empty string if no answer has
	 * been given yet, or null if this was not a free response question
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
	 * Get the current qusetion's current answer's value
	 * 
	 * @return the value (an int), or -1 if no answer has been given yet
	 * or this was not a scale question
	 */
	public int getAnswerValue()
	{
		if (currentAns == null)
		{
			return -1;
		}
		return currentAns.getValue();
	}

	/**
	 * Move the survey to the next question.
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
	 * Move the survey back one question.
	 * 
	 * @throws RuntimeException the current question is the first question
	 */
	public void prevQuestion()
	{
		if (isOnFirst()) throw new RuntimeException("no previous Question");
		currentQ = history.pop();
		currentAns = registry.pop();
		currentQ.popAns();
	}

	/**
	 * Is the survey on the first question?  Useful when choosing to whether or
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
	 * Get the type of the current question.
	 * 
	 * @return the type, as in {@link PeoplesDB.QuestionTable}
	 */
	public int getQuestionType()
	{
		if (currentQ == null) throw new
			RuntimeException("Call to getQuestionType after end of survey");
		return currentQ.getType();
	}
	
	/**
	 * Has a photo been submitted with this survey?
	 * 
	 * @return true if one has
	 */
	public boolean hasPhoto()
	{
		return hasPhoto;
	}
	
	/**
	 * Has a voice recording been submitted with this survey?
	 * 
	 * @return true if one has
	 */
	public boolean hasVoice()
	{
		return hasVoice;
	}
	
	/*-----------------------------------------------------------------------*/

	/**
	 * Answer a multiple choice question.
	 * 
	 * @param c - the Collection of {@link Choice}s to answer with
	 * 
	 * @throws RuntimeException if the question being answered is not a
	 * choice question
	 */
	public void answer(Collection<Choice> c)
	{
		if (currentQ.getType() == PeoplesDB.QuestionTable.SINGLE_CHOICE ||
			currentQ.getType() == PeoplesDB.QuestionTable.MULTI_CHOICE)
		{
			ChoiceQuestion q = (ChoiceQuestion) currentQ;
			registry.push(q.answer(c));
		}
		else throw new RuntimeException("Wrong question type");
	}

	/**
	 * Answer a free response question.
	 * 
	 * @param text - the String to answer with
	 * 
	 * @throws RuntimeException if the question being answered is not a free
	 * response question
	 */
	public void answer(String text)
	{
		if (currentQ.getType() == PeoplesDB.QuestionTable.FREE_RESPONSE)
		{
			FreeResponseQuestion q = (FreeResponseQuestion) currentQ;
			registry.push(q.answer(text));
		}
		else throw new RuntimeException("Wrong question type");
	}
	
	/**
	 * Answer a scale question.
	 * 
	 * @param val - the scale value
	 * 
	 * @throws RuntimeException if the question being answered is not a scale
	 * question
	 */
	public void answer(int val)
	{
		if (currentQ.getType() == PeoplesDB.QuestionTable.SCALE_IMG)
		{
			SlidingScaleImgQuestion q = (SlidingScaleImgQuestion) currentQ;
			registry.push(q.answer(val));
		}
		else if (currentQ.getType() == PeoplesDB.QuestionTable.SCALE_TEXT)
		{
			SlidingScaleTextQuestion q = (SlidingScaleTextQuestion) currentQ;
			registry.push(q.answer(val));
		}
		else throw new RuntimeException("Wrong question type");
	}
	
	/**
	 * Adds a photo to this survey.  Writes it into the database.
	 * 
	 * @param photo - the photo's data in an {@link InputStream}
	 * 
	 * @return true on success
	 */
	public boolean addPhoto(InputStream photo)
	{
		Util.v(null, TAG, "adding photo");
		if (id == 0)
		{
			hasPhoto = true;
			return true;
		}
			
		SurveyDBHandler sdbh = new SurveyDBHandler(ctxt);
		sdbh.openWrite();
		int returned = sdbh.writeExtra(id, SurveyDBHandler.PHOTO,
				photo, System.currentTimeMillis() / 1000, row);
		sdbh.close();
		if (returned == SurveyDBHandler.WRITE_ERROR)
		{
			return false;
		}
		row = returned;
		hasPhoto = true;
		return true;
	}
	
	/**
	 * Adds a voice recording to this survey.  Writes it into the database.
	 * 
	 * @param voice - the recording's data in an {@link InputStream}
	 * 
	 * @return true on success
	 */
	public boolean addVoice(InputStream voice)
	{
		if (id == 0)
		{
			hasVoice = true;
			return true;
		}
		
		SurveyDBHandler sdbh = new SurveyDBHandler(ctxt);
		sdbh.openWrite();
		int returned = sdbh.writeExtra(id, SurveyDBHandler.VOICE,
				voice, System.currentTimeMillis() / 1000, row);
		sdbh.close();
		if (returned == SurveyDBHandler.WRITE_ERROR)
		{
			return false;
		}
		row = returned;
		hasVoice = true;
		return true;
	}

	/**
	 * Finalize the answers to this survey and enter them in the database. Also
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

		//wipe the Question history
		while (!history.empty())
		{
			history.pop().popAns();
		}

		return worked;
	}
}
