/*---------------------------------------------------------------------------*
 * Choice.java                                                               *
 *                                                                           *
 * Model for a survey choice.  Holds a String as it's choice text.  Also has *
 * functions to look up the history of a Choice.                             *
 *---------------------------------------------------------------------------*/
package com.peoples.android.model;

import com.peoples.android.database.PeoplesDB;

/**
 * Model for a survey choice.  Based on the SQL:
 * 
 * CREATE TABLE choices (
 *  id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
 *  text VARCHAR(255),
 *  question_id INT UNSIGNED);
 * 
 * @author Diego Vargas
 * @author Austin Walker
 */
public class Choice
{
	//the text to display as an option
	private final String choice_text;
	
	//the Choice id
	//unlike some of the other survey classes, we have to keep track of the
	//id here because conditions need to have a way to look up their history
	//in the database
	private final int id;
	
	/*-----------------------------------------------------------------------*/

	/**
	 * Create a new Choice.
	 * 
	 * @param text - the text the Choice should contain
	 * @param id - choice_id as in the database
	 */
	public Choice(String text, int id)
	{
		choice_text = text;
		this.id = id;
	}
	
	/**
	 * Get this Choice's text.
	 * 
	 * @return a String corresponding to the Choice text
	 */
	public String getText()
	{
		return choice_text;
	}
	
	/**
	 * "Answer" a Question with this Choice.
	 * 
	 * @param q - the Question being answered
	 * @param q_id - the Question being answered's id
	 * 
	 * @return an Answer object corresponding this choice/question pair
	 */
	public Answer answer(Question q, int q_id)
	{
		return new Answer(q, q_id, this, id, null);
	}
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Checks whether this Choice has ever been used to answer a Question
	 * 
	 * @param q_id - the Question id to look for
	 * 
	 * @return true or false
	 */
	public boolean hasEverBeen(int id)
	{
		return false;
		//TODO database stuff
	}
	
	/**
	 * Checks that this Choice has never been used to answer a Question
	 * 
	 * @param q_id - the Question id to look for
	 * 
	 * @return true or false
	 */
	public boolean hasNeverBeen(int id)
	{
		return false;
		//TODO database stuff
	}
}
