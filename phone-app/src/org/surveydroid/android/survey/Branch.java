/*---------------------------------------------------------------------------*
 * Branch.java                                                               *
 *                                                                           *
 * Model for a survey branch. Can be evaluated to true/false to determine    *
 * what question to display next.  Contains a set of conditions.             *
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
	private final int q_id;
	private Question next_q;
	
	//Set of Conditions that belong to this branch; starts empty
	private final Collection<Condition> conditions;
	
	/*-----------------------------------------------------------------------*/

	/**
	 * Create a new Branch.
	 * 
	 * @param q_id - the id of the next question to go to
	 * @param c - {@link Condition}s for this Branch
	 * 
	 * @see setQuestion
	 */
	public Branch(int q_id, Collection<Condition> c)
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
	 * @throws RuntimeException if the qMap doesn't have the needed Question
	 */
	public void setQuestion(Map<Integer, Question> qMap)
	{
		if (next_q != null) throw new RuntimeException(
					"attempt to set condition question multiple times");
		if (!qMap.containsKey(q_id)) throw new RuntimeException(
				"bad question map");
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
