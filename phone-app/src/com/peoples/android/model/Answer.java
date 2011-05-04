/*---------------------------------------------------------------------------*
 * Answer.java                                                               *
 *                                                                           *
 * Model for a survey answer.  Contains everything needed to write the       *
 * answer given by a subject into the phone's database.                      *
 *---------------------------------------------------------------------------*/
package com.peoples.android.model;

import java.io.Serializable;

import android.content.Context;

import com.peoples.android.database.SurveyDBHandler;

//import org.json.JSONException;
//import org.json.JSONObject;

//import android.util.Log;

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
public class Answer implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	//the Question being answered
    private final Question question;
    private final int questionID;

    //the Choice given for that Question
    private final Choice choice;
    private final int choiceID;

    //the text given for a free-response Question
    private final String text;

    //time when this Answer was given/created
    private final long created;
    
    //Android Context for database call
    private final Context ctxt;

	
    /*-----------------------------------------------------------------------*/
    
    /**
     * Create a new Answer.
     * 
     * @param q - the Question object being answered
     * @param q_id - that Question's id
     * @param c - the Choice being given (if multiple Choice)
     * @param c_id - that Choice's id (ignored if Choice is null)
     * @param t - the answer text for a free response Question
     */
	public Answer(Question q, int q_id, Choice c, int c_id, String t, Context ctxt)
	{
		question = q;
		questionID = q_id;
		if (c != null)
		{
			choice = c;
			choiceID = c_id;
			text = null;
		}
		else
		{
			text = t;
			choice = null;
			choiceID = 0;
		}
		this.ctxt = ctxt;
		created = System.currentTimeMillis() / 1000;
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
	 * Get this Answer's Choice
	 * 
	 * @return the Choice the Question was answered with if the Question was
	 * multiple choice, or null if the Question was free response
	 */
	public Choice getChoice()
	{
		return choice;
	}
	
	/**
	 * Get this Answer's text
	 * 
	 * @return the text as a String that was given as a response if the
	 * Question was free response, or null if it was multiple choice
	 */
    public String getText()
    {
        return text;
    }
    
    /**
     * Write this Answer to the database.
     * 
     * @return true on success
     */
    public boolean write()
    {
    	SurveyDBHandler db = new SurveyDBHandler(ctxt);
    	db.openWrite();
    	boolean worked = false;
    	if (text == null) //multiple choice
    	{
    		worked = db.writeAnswer(questionID, choiceID, created);
    	}
    	else //free response
    	{
    		worked = db.writeAnswer(questionID, text, created);
    	}
    	db.close();
    	return worked;
    }

    //TODO I (Austin) think this should be done elsewhere
    /*public JSONObject getAsJson() {
        JSONObject j = null;
        try {
            j = new JSONObject();
            j.put("question_id", 1); // hack for now
            if (type == 0) {
                j.put("ans_text", text);
            } else {
                j.put("ans_text", text);
            }
            j.put("created", Long.toString(System.currentTimeMillis() / 1000));
        } catch (JSONException e) {
            Log.e("Answer", e.getMessage());
        }
        return j;
    }*/
}
