/*---------------------------------------------------------------------------*
 * Condition.java                                                            *
 *                                                                           *
 * Model for a branch condition. Can be evaluated to true/false to help      *
 * determine whether or not to follow a branch.                              *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import java.util.Map;

/**
 * Model for a branch condition.  Based on SQL:
 * 
 * CREATE TABLE conditions (
 *  id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
 *  branch_id INT UNSIGNED NOT NULL,
 *  question_id INT UNSIGNED NOT NULL,
 *  choice_id INT UNSIGNED NOT NULL,
 *  type TINYINT UNSIGNED NOT NULL);
 *  		
 * @author Diego Vargas
 * @author Austin Walker
 */
public class Condition
{
	//enumeration of the comparison types
	public static final int JUST_WAS = 0;
	public static final int EVER_WAS = 1;
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
	 * @param c - Choice to look at
	 * @param t - type of relation required
	 * 
	 * @throws RuntimeException if t is not a valid type
	 * 
	 * @see setQuestion
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
	 * Set the Condition's Question.  Needed to avoid infinite recursion in the
	 * Survey constructor.  Should only be called once.
	 * 
	 * @param q_map - a Map of build Question objects
	 * 
	 * @throws RuntimeException if called multiple times
	 * @throws RuntimeException if the given map doesn't have the need Question
	 */
	public void setQuestion(Map<Integer, Question> qMap)
	{
		if (question != null) throw new RuntimeException(
				"attempt to set condition question multiple times");
		if (!qMap.containsKey(q_id)) throw new RuntimeException(
				"bad question map");
		question = qMap.get(q_id);
	}
	
	/**
	 * Evaluate this Condition.
	 * 
	 * @return true or false
	 * 
	 * @throws RuntimeException if called before setQuestion
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
