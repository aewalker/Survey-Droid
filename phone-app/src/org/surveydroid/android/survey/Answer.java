/*---------------------------------------------------------------------------*
 * Answer.java                                                               *
 *                                                                           *
 * Model for a survey answer.  Contains everything needed to write the       *
 * answer given by a subject into the phone's database.                      *
 *---------------------------------------------------------------------------*
 * Copyright 2011 Sema Berkiten, Vladimir Costescu, Henry Liu, Diego Vargas, *
 * Austin Walker, and Tony Xiao                                              *
 *                                                                           *
 * This file is part of Survey Droid.                                        *
 *                                                                           *
 * Survey Droid is free software: you can redistribute it and/or modify      *
 * it under the terms of the GNU General Public License as published by      *
 * the Free Software Foundation, either version 3 of the License, or         *
 * (at your option) any later version.                                       *
 *                                                                           *
 * Survey Droid is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of            *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             *
 * GNU General Public License for more details.                              *
 *                                                                           *
 * You should have received a copy of the GNU General Public License         *
 * along with Survey Droid.  If not, see <http://www.gnu.org/licenses/>.     *
 *****************************************************************************/
package org.surveydroid.android.survey;

import java.util.Collection;

import org.surveydroid.android.Util;
import org.surveydroid.android.database.SurveyDroidDB;
import org.surveydroid.android.database.SurveyDBHandler;

/**
 * Holds the answer to a survey question and contains methods to write that
 * answer to the database and extract data from it.
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

	
    /*-----------------------------------------------------------------------*/
    
    /**
     * Create a new Answer to a free response question.
     * 
     * @param q - the {@link Question} being answered
     * @param q_id - that question's id
     * @param t - the answer text
     */
	public Answer(Question q, int q_id, String t)
	{
		question = q;
		questionID = q_id;
		text = t;
		type = SurveyDroidDB.AnswerTable.TEXT;
		created = Util.currentTimeAdjusted() / 1000;
		
		//unneeded fields
		value = -1;
		choiceIDs = null;
		choices = null;
	}
	
	/**
	 * Create a new Answer to a slider (value based) question.
	 * 
	 * @param q - the {@link Question} being answered
	 * @param q_id - that question's id
	 * @param v - the value the question was answered with
	 */
	public Answer(Question q, int q_id, int v)
	{
		question = q;
		questionID = q_id;
		value = v;
		type = SurveyDroidDB.AnswerTable.VALUE;
		created = Util.currentTimeAdjusted() / 1000;
		
		//unneeded fields
		text = null;
		choiceIDs = null;
		choices = null;
	}
	
	/**
	 * Create a new Answer to a multiple choice question.
	 * 
	 * @param q - the {@link Question} being answered
	 * @param q_id - that question's id
	 * @param choices - the {@link Choice}s that were picked
	 * @param choice_ids - the ids of the choices that were picked
	 */
	public Answer(Question q, int q_id, Collection<Choice> choices,
			int[] choice_ids)
	{
		question = q;
		questionID = q_id;
		type = SurveyDroidDB.AnswerTable.CHOICE;
		this.choices = choices;
		choiceIDs = choice_ids;
		created = Util.currentTimeAdjusted() / 1000;
		
		//unneeded fields
		text = null;
		value = -1;
	}
	
	/**
	 * Get this Answer's Question
	 * 
	 * @return the {@link Question} being answered
	 */
	public Question getQuestion()
	{
		return question;
	}
	
	/**
	 * Get the choices given in this answer
	 * 
	 * @return the {@link Choice} array the {@link Question} was answered with
	 * if the Question was multiple choice, or null if the Question was free
	 * response or value based.
	 */
	public Collection<Choice> getChoices()
	{
		return choices;
	}
	
	/**
	 * Get this Answer's text
	 * 
	 * @return the text as a String that was given as a response if the
	 * {@link Question} was free response, or null otherwise
	 */
    public String getText()
    {
        return text;
    }
    
    /**
     * Get this Answer's value
     * 
     * @return the value (an int), or -1 if this answer was not for a scale
     * {@link Question}
     */
    public int getValue()
    {
    	return value;
    }
    
    /**
     * Write this Answer to the database.
     * 
     * @param db an <strong>OPEN</strong> {@link SurveyDBHandler}
     * @return true on success
     */
    public boolean write(SurveyDBHandler db)
    {
    	//don't write dummy answers
    	if (questionID == 0) return true;
    	boolean worked = false;
    	switch (type)
    	{
    	case SurveyDroidDB.AnswerTable.CHOICE:
    		worked = db.writeAnswer(questionID, choiceIDs, created);
    		break;
    	case SurveyDroidDB.AnswerTable.VALUE:
    		worked = db.writeAnswer(questionID, value, created);
    		break;
    	case SurveyDroidDB.AnswerTable.TEXT:
    		worked = db.writeAnswer(questionID, text, created);
    		break;
    	default:
    		db.close();
    		throw new RuntimeException("Invalid answer type: " + type);
    	}
    	return worked;
    }
}
