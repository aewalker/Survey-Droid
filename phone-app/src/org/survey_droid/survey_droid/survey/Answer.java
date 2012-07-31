/*---------------------------------------------------------------------------*
 * Answer.java                                                               *
 *                                                                           *
 * Model for a survey answer.  Contains everything needed to write the       *
 * answer given by a subject into the phone's database.                      *
 *---------------------------------------------------------------------------*
 * Copyright (C) 2011-2012 Survey Droid Contributors                         *
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
package org.survey_droid.survey_droid.survey;

import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.content.SurveyDBHandler;

/**
 * Holds the answer to a survey question and contains methods to write that
 * answer to the database and extract data from it.
 * 
 * @author Diego Vargas
 * @author Austin Walker
 */
public class Answer
{
	/** the Question being answered */
    private final Question question;
    
    /** The actual data that was given as the answer */
    private final byte[] data;

    /** time when this Answer was given/created */
    private final long created;

	
    /*-----------------------------------------------------------------------*/
    
    /**
     * Create a new Answer to some question.
     * 
     * @param q the {@link Question} being answered
     * @param data the data given in response to the question
     */
	public Answer(Question q, byte[] data)
	{
		question = q;
		this.data = data;
		created = Util.currentTimeAdjusted() / 1000;
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
     * Get this Answer's value
     * 
     * @return the data as raw bytes used to answer the question
     */
    public byte[] getValue()
    {
    	return data;
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
    	if (question.id == Survey.DUMMY_SURVEY_ID) return true;
    	db.writeAnswer(question.id, data, created);
    	return true;
    }
}
