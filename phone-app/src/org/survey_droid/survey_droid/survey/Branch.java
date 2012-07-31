/*---------------------------------------------------------------------------*
 * Branch.java                                                               *
 *                                                                           *
 * Model for a survey branch. Can be evaluated to true/false to determine    *
 * what question to display next.  Contains a set of conditions.             *
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

import java.util.List;
import java.util.Map;

/**
 * Model for a survey branch.
 *
 * @author Diego Vargas
 * @author Austin Walker
 **/
public class Branch
{
	//The next question to go to if this branch is true
	private final long q_id;
	private Question next_q;
	
	//Set of Conditions that belong to this branch; starts empty
	private final List<Condition> conditions;
	
	/*-----------------------------------------------------------------------*/

	/**
	 * Create a new Branch.
	 * 
	 * @param q_id - the id of the next question to go to
	 * @param c - {@link Condition}s for this Branch
	 * 
	 * @see setQuestion
	 */
	public Branch(long q_id, List<Condition> c)
	{
		next_q = null;
		this.q_id = q_id;
		conditions = c;
	}
	
	/**
	 * Set the Branch's {@link Question}.  Needed to avoid infinite recursion
	 * in {@link Survey#Survey(int, android.content.Context)}. Should only be
	 * called once.
	 * 
	 * @param qMap - a mapping of Question objects to their ids
	 * 
	 * @throws RuntimeException if called multiple times
	 * @throws SurveyConstructionException if the needed question doesn't exist
	 */
	public void setQuestion(Map<Long, Question> qMap)
		throws SurveyConstructionException
	{
		if (next_q != null) throw new RuntimeException(
					"attempt to set condition question multiple times");
		if (!qMap.containsKey(q_id))
		{
			SurveyConstructionException e = new SurveyConstructionException();
			e.setRefQuestion(q_id);
			throw e;
		}
		next_q = qMap.get(q_id);
	}
	
	/**
	 * Evaluate this Branch.
	 * 
	 * @return true or false
	 * 
	 * @throws RuntimeException if called before {@link #setQuestion}
	 */
	public boolean eval()
	{
		if (next_q == null) throw new RuntimeException(
				"must set question before evaluating");
			
		boolean status = true;
		for (Condition c : conditions)
		{
			if (c.eval() == false)
				status = false;
		}
		return status;
	}
	
	/**
	 * Get the Question this Branch points to
	 * 
	 * @return {@link Question} this Branch points to
	 */
	public Question nextQuestion()
	{
		return next_q;
	}
}
