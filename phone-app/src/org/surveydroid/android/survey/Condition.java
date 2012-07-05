/*---------------------------------------------------------------------------*
 * Condition.java                                                            *
 *                                                                           *
 * Model for a branch condition. Can be evaluated to true/false to help      *
 * determine whether or not to follow a branch.                              *
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

import java.util.Map;

/**
 * Model for a branch condition.
 *  		
 * @author Diego Vargas
 * @author Austin Walker
 */
public class Condition
{
	//enumeration of the comparison types
	/**
	 * Condition type that checks if the question has been answered with the
	 * given choice during the current instance of the survey.
	 */
	public static final int JUST_WAS = 0;
	/**
	 * Condition type that checks if the question has ever been answered with
	 * the given choice in <strong>any</strong> instance of the survey.
	 */
	public static final int EVER_WAS = 1;
	/**
	 * Condition type that checks to ensure that the question has <strong>never
	 * </strong> been answered with the given choice in <strong>any</strong>
	 * instance of the survey.
	 */
	public static final int HAS_NEVER_BEEN = 2;
	
	//Question to look at when evaluating this condition
	private final int q_id;
	private Question question;
	
	//Choice that is required for this condition to be true
	private final Choice choice;
	
	//"just was", "ever was", or "has never been" as in enum above
	private final int type;
	
	//answer set from the Survey
	//required in order to check current answers
	//because of this, Conditions need to be initialized in the Survey
	private final Iterable<Answer> answers;
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Create a new Condition.
	 * 
	 * @param q_id the id of the question this condition checks
	 * @param c - {@link Choice} to look at
	 * @param t - type of relation required
	 * @param ans - {@link Answer}s from the survey (required to  check current
	 * answers)
	 * 
	 * @throws RuntimeException if t is not a valid type from the set
	 * {{@link #JUST_WAS}, {@link #EVER_WAS}, {@link #HAS_NEVER_BEEN}}.
	 * 
	 * @see Condition#setQuestion(Map)
	 */
	public Condition(int q_id, Choice c, int t, Iterable<Answer> ans)
	{
		choice = c;
		this.q_id = q_id;
		question = null;
		switch (t)
		{
			case JUST_WAS: type = JUST_WAS; break;
			case EVER_WAS: type = EVER_WAS; break;
			case HAS_NEVER_BEEN: type = HAS_NEVER_BEEN; break;
			default: throw new RuntimeException("Invalid condition type: " + t);
		}
		answers = ans;
	}
	
	/**
	 * Set the Condition's {@link Question}.  Needed to avoid infinite
	 * recursion in {@link Survey#Survey(int, android.content.Context)}.
	 * Should only be called once.
	 * 
	 * @param q_map - a {@link Map} of built Questions
	 * @throws RuntimeException if called multiple times
	 * @throws SurveyConstructionException if the given map doesn't
	 * have the need Question
	 */
	public void setQuestion(Map<Integer, Question> qMap)
		throws SurveyConstructionException
	{
		if (question != null) throw new RuntimeException(
				"attempt to set condition question multiple times");
		if (!qMap.containsKey(q_id))
		{
			SurveyConstructionException e = new SurveyConstructionException();
			e.setRefQuestion(q_id);
			throw e;
		}
		question = qMap.get(q_id);
	}
	
	/**
	 * Evaluate this Condition.
	 * 
	 * @return true or false
	 * 
	 * @throws RuntimeException if called before {@link #setQuestion(Map)}
	 */
	public boolean eval()
	{
		if (question == null)
		{
			throw new RuntimeException(
					"must set question before calling eval");
		}
		switch (type)
		{
			case JUST_WAS: return evalJustWas();
			case EVER_WAS: return question.hasEverBeen(choice);
			case HAS_NEVER_BEEN: return !question.hasEverBeen(choice);
			default: throw new RuntimeException(
					"Something is very wrong; invalid condition type: "
					+ type);
		}
	}
	
	//function to check the list of Answers to evaluate this condition
	private boolean evalJustWas()
	{
		for (Answer a : answers)
		{
			if (a.getQuestion().equals(question))
			{
				for (Choice c : a.getChoices())
				{
					if (c.equals(choice))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}
