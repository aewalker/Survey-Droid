/*---------------------------------------------------------------------------*
 * Choice.java                                                               *
 *                                                                           *
 * Model for a survey choice.  Holds a String or Image as it's choice text.  *
 * Also has functions to look up the history of a Choice.                    *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import java.util.Collection;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.peoples.android.database.SurveyDBHandler;
import org.peoples.android.database.PeoplesDB;

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
	//the text to display as an option if this is a text choice
	private final String choice_text;
	
	//the bitmap to display as an option if this is an image choice
	private final Bitmap choice_img;
	
	//the Choice id
	//unlike some of the other survey classes, we have to keep track of the
	//id here because conditions need to have a way to look up their history
	//in the database
	private final int id;
	
	//is this a text or image choice?
	private final int type;
	
	//context, needed for the same reason as above
	private final Context ctxt;
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Create a new text Choice.
	 * 
	 * @param text - the text the Choice should contain
	 * @param id - choice_id as in the database
	 * @param c - the current context
	 */
	public Choice(String text, int id, Context c)
	{
		choice_text = text;
		type = PeoplesDB.ChoiceTable.TEXT_CHOICE;
		choice_img = null;
		this.id = id;
		ctxt = c;
	}
	
	/**
	 * Create a new image Choice.
	 * 
	 * @param img - the base 64 character array representing the image to use
	 * @param id - the choice_id as in the database
	 * @param c - the current context
	 */
	public Choice(char[] img, Context c, int id) //XD it lets me do this!!!!
	{
		byte[] imgData = Base64Coder.decode(img);
		choice_img = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
		
		choice_text = null;
		type = PeoplesDB.ChoiceTable.IMG_CHOICE;
		this.id = id;
		ctxt = c;
	}
	
	/**
	 * Get this Choice's text.
	 * 
	 * @return a String corresponding to the Choice text (empty string if this
	 * is an image choice)
	 */
	public String getText()
	{
		if (choice_text == null)
			return "";
		return choice_text;
	}
	
	/**
	 * Get this Choice's image.
	 * 
	 * @return this Choice's Bitmap image (or null if this is a text Choice)
	 */
	public Bitmap getImg()
	{
		return choice_img;
	}
	
	/**
	 * Is this Choice an image choice?
	 * 
	 * @return true if it is
	 */
	public boolean isImg()
	{
		if (type == PeoplesDB.ChoiceTable.IMG_CHOICE)
			return true;
		return false;
	}
	
	/**
	 * "Answer" a Question with this Choice.
	 * 
	 * @param q - the Question being answered
	 * @param q_id - the Question being answered's id
	 * 
	 * @return an Answer object corresponding this choice/question pair
	 */
	public static Answer answer(Question q, int q_id, Collection<Choice> c, Context ctxt)
	{
		int[] c_ids = new int[c.size()];
		int i = 0;
		for (Choice choice : c)
		{
			c_ids[i] = choice.id;
			i++;
		}
		return new Answer(q, q_id, c, c_ids, ctxt);
	}
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Checks whether this Choice has ever been used to answer a Question
	 * 
	 * @param id - the Question id to look for
	 * 
	 * @return true or false
	 * 
	 * @throws IllegalArgumentException if any of the required columns do not
	 * exist in the database or can't be found in the results set
	 */
	public boolean hasEverBeen(int id)
	{
		SurveyDBHandler db = new SurveyDBHandler(ctxt);
		db.openRead();
		Cursor c = db.getQuestionHistory(id);;
		c.moveToFirst();
		while (!c.isAfterLast())
		{
			String[] ids = c.getString(c.getColumnIndexOrThrow(
					PeoplesDB.AnswerTable.CHOICE_IDS)).split(",");
			for (String thisID : ids)
			{
				if (Integer.parseInt(thisID) == this.id)
				{
					c.close();
					db.close();
					return true;
				}
			}
		}
		c.close();
		db.close();
		return false;
	}
	
	/**
	 * Get a string representation of this choice.
	 * 
	 * @return the choice text
	 */
	public String toString()
	{
		if (type == PeoplesDB.ChoiceTable.TEXT_CHOICE)
			return choice_text;
		else
			return "image choice";
	}
}
