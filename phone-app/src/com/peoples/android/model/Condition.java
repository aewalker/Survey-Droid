/*---------------------------------------------------------------------------*
 * Condition.java                                                            *
 *                                                                           *
 * Model for a branch condition. Can be evaluated to true/false to help      *
 * determine whether or not to follow a branch.                              *
 *---------------------------------------------------------------------------*/
package com.peoples.android.model;

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
	private Question question;
	
	//Choice that is required for this condition to be true
	private Choice choice;
	
	//"just was", "ever was", or "has never been" as in enum above
	private int type;
	
	//answer set from the Survey
	//required in order to check current answers
	//because of this, Conditions need to be initialized in the Survey
	private Iterable<Answer> answers;
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Create a new Condition.
	 * 
	 * @param q - Question to look at
	 * @param c - Choice to look at
	 * @param t - type of relation required
	 * 
	 * @throws RuntimeException if t is not a valid type
	 */
	public Condition(Question q, Choice c, int t, Iterable<Answer> ans)
	{
		question = q;
		choice = c;
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
	 * Evaluate this Condition.
	 * 
	 * @return true or false
	 */
	public boolean eval()
	{
		switch (type)
		{
			case JUST_WAS: return evalJustWas();
			case EVER_WAS: return question.hasEverBeen(choice);
			case HAS_NEVER_BEEN: return question.hasNeverBeen(choice);
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
				if (a.getChoice().equals(choice))
				{
					return true;
				}
			}
		}
		return false;
	}
}
