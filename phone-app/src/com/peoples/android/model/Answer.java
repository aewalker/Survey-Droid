/*---------------------------------------------------------------------------*
 * Answer.java                                                               *
 *                                                                           *
 * Model for a survey answer.  Contains everything needed to write the       *
 * answer given by a subject into the phone's database.                      *
 *---------------------------------------------------------------------------*/
package com.peoples.android.model;

import com.peoples.android.database.PeoplesDB;

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
public class Answer
{
    //the Question being answered
    private Question question;
    private int questionID;

    //the Choice given for that Question
    private Choice choice;
    private int choiceID;

    //the text given for a free-response Question
    private String text;

    //time when this Answer was given/created
    private long created;
	
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
	public Answer(Question q, int q_id, Choice c, int c_id, String t)
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
    	//TODO database stuff
    	return true;
    }

    //TODO I think this should be done elsewhere
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
