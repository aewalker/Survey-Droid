/*---------------------------------------------------------------------------*
 * Branch.java                                                               *
 *                                                                           *
 * Model for a survey branch. Can be evaluated to true/false to determine    *
 * what question to display next.  Contains a set of conditions.             *
 *---------------------------------------------------------------------------*/
package com.peoples.android.model;

import java.util.Collection;

/**
 * Model for a survey branch.  Based on the SQL:
 * 
 * CREATE TABLE branches (
 *  id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
 *  prev_q INT UNSIGNED NOT NULL, //foreign keys//
 *  next_q INT UNSIGNED NOT NULL)
 *
 * @author Diego Vargas
 * @author Austin Walker
 **/
public class Branch
{
	//The next question to go to if this branch is true.  Can be null.
	private Question next_q;
	
	//Set of Conditions that belong to this branch; starts empty
	private Collection<Condition> conditions;
	
	/*-----------------------------------------------------------------------*/

	/**
	 * Create a new Branch with no Conditions.
	 * 
	 * @param q - Question this Branch should point to
	 * @param c - Conditions for this Branch
	 */
	public Branch (Question q, Collection<Condition> c)
	{
		next_q = q;
		conditions = c;
	}
	
	/**
	 * Evaluate this Branch.
	 * 
	 * @return true or false
	 */
	public boolean eval()
	{
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
	 * @return Question this Branch points to
	 */
	public Question nextQuestion()
	{
		return next_q;
	}
}
