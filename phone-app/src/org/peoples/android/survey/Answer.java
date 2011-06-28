/*---------------------------------------------------------------------------*
 * Answer.java                                                               *
 *                                                                           *
 * Model for a survey answer.  Contains everything needed to write the       *
 * answer given by a subject into the phone's database.                      *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import java.util.Collection;

import android.content.Context;

import org.peoples.android.database.PeoplesDB;
import org.peoples.android.database.SurveyDBHandler;

/**
 * Model for a Survey answer.  Based on the SQL:
 * 
 * CREATE TABLE answers (
 *  id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
 *  question_id INT UNSIGNED NOT NULL,
 *  subject_id INT UNSIGNED NOT NULL,
 *  choice_id INT UNSIGNED,
 *  ans_text TEXT,
 *  created DATETIME);
 * 
 * @author Diego Vargas
 * @author Austin Walker
 */
public class Answer
{
	//the Question being answered
    private final Question question;
    private final int questionID;
    
    //the type of answer
    private final int type;
    
    //the Choice given for that Question
    private final Collection<Choice> choices;
    private final int[] choiceIDs;

    //the text given for a free-response Question
    private final String text;
    
    //the value for a slider question
    private final int value;

    //time when this Answer was given/created
    private final long created;
    
    //Android Context for database call
    private final Context ctxt;

	
    /*-----------------------------------------------------------------------*/
    
    /**
     * Create a new Answer to a free response question.
     * 
     * @param q - the Question object being answered
     * @param q_id - that Question's id
     * @param t - the answer text for a free response Question
     * @param ctxt - the current context
     */
	public Answer(Question q, int q_id, String t, Context ctxt)
	{
		question = q;
		questionID = q_id;
		text = t;
		type = PeoplesDB.AnswerTable.TEXT;
		this.ctxt = ctxt;
		created = System.currentTimeMillis() / 1000;
		
		//unneeded fields
		value = -1;
		choiceIDs = null;
		choices = null;
	}
	
	/**
	 * Create a new Answer to a slider (value based) question.
	 * 
	 * @param q - the Question object being answered
	 * @param q_id - that Question's id
	 * @param v - the value the question was answered with
	 * @param ctxt - the current context
	 */
	public Answer(Question q, int q_id, int v, Context ctxt)
	{
		question = q;
		questionID = q_id;
		value = v;
		type = PeoplesDB.AnswerTable.VALUE;
		this.ctxt = ctxt;
		created = System.currentTimeMillis() / 1000;
		
		//unneeded fields
		text = null;
		choiceIDs = null;
		choices = null;
	}
	
	/**
	 * Create a new Answer to a multiple choice question.
	 * 
	 * @param q - the Question object being answered
	 * @param q_id - that Question's id
	 * @param choices - the choices that were picked
	 * @param choice_ids - the ids of the choices that were picked
	 * @param ctxt - the current context
	 */
	public Answer(Question q, int q_id, Collection<Choice> choices,
			int[] choice_ids, Context ctxt)
	{
		question = q;
		questionID = q_id;
		type = PeoplesDB.AnswerTable.CHOICE;
		this.choices = choices;
		choiceIDs = choice_ids;
		this.ctxt = ctxt;
		created = System.currentTimeMillis() / 1000;
		
		//unneeded fields
		text = null;
		value = -1;
	}
	
	/**
	 * Get this Answer's Question
	 * 
	 * @return the Question object being answered
	 */
	public Question getQuestion()
	{
		return question;
	}
	
	/**
	 * Get the choices given in this answer
	 * 
	 * @return the Choice array the Question was answered with if the
	 * Question was multiple choice, or null if the Question was free response
	 * or value based.
	 */
	public Collection<Choice> getChoices()
	{
		return choices;
	}
	
	/**
	 * Get this Answer's text
	 * 
	 * @return the text as a String that was given as a response if the
	 * Question was free response, or null otherwise
	 */
    public String getText()
    {
        return text;
    }
    
    /**
     * Get this Answer's value
     * 
     * @return the value (an int), or -1 if this answer was not for a scale
     * question
     */
    public int getValue()
    {
    	return value;
    }
    
    /**
     * Write this Answer to the database.
     * 
     * @return true on success
     */
    public boolean write()
    {
    	//don't write dummy answers
    	if (questionID == 0) return true;
    	
    	SurveyDBHandler db = new SurveyDBHandler(ctxt);
    	db.openWrite();
    	boolean worked = false;
    	switch (type)
    	{
    	case PeoplesDB.AnswerTable.CHOICE:
    		worked = db.writeAnswer(questionID, choiceIDs, created);
    		break;
    	case PeoplesDB.AnswerTable.VALUE:
    		worked = db.writeAnswer(questionID, value, created);
    		break;
    	case PeoplesDB.AnswerTable.TEXT:
    		worked = db.writeAnswer(questionID, text, created);
    		break;
    	default:
    		throw new RuntimeException("Invalid answer type: " + type);
    	}
    	db.close();
    	return worked;
    }
}
