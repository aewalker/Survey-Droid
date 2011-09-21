/*---------------------------------------------------------------------------*
 * Choice.java                                                               *
 *                                                                           *
 * Model for a survey choice.  Holds a String or Image as it's choice text.  *
 * Also has functions to look up the history of a Choice.                    *
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

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.surveydroid.android.Base64Coder;
import org.surveydroid.android.database.SurveyDroidDB;
import org.surveydroid.android.database.SurveyDBHandler;

/**
 * Model for a survey choice.
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
	 * @param c - the current {@link Context}
	 */
	public Choice(String text, int id, Context c)
	{
		choice_text = text;
		type = SurveyDroidDB.ChoiceTable.TEXT_CHOICE;
		choice_img = null;
		this.id = id;
		ctxt = c;
	}
	
	/**
	 * Create a new image Choice.
	 * 
	 * @param img - the base 64 character array representing the image to use
	 * @param id - the choice_id as in the database
	 * @param c - the current {@link Context}
	 */
	public Choice(char[] img, Context c, int id) //XD it lets me do this!!!!
	{
		byte[] imgData = Base64Coder.decode(img);
		choice_img = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
		
		choice_text = null;
		type = SurveyDroidDB.ChoiceTable.IMG_CHOICE;
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
	 * @return this Choice's {@link Bitmap} image (or null if this is a text
	 * Choice)
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
		if (type == SurveyDroidDB.ChoiceTable.IMG_CHOICE)
			return true;
		return false;
	}
	
	/**
	 * "Answer" a Question with this Choice.
	 * 
	 * @param q - the {@link Question} being answered
	 * @param q_id - the Question being answered's id
	 * 
	 * @return an {@link Answer} corresponding this choice/question pair
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
	 * Checks whether this Choice has ever been used to answer a question
	 * 
	 * @param id - the question id to look for
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
					SurveyDroidDB.AnswerTable.CHOICE_IDS)).split(",");
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
	 * @return the Choice's text
	 */
	public String toString()
	{
		if (type == SurveyDroidDB.ChoiceTable.TEXT_CHOICE)
			return getText();
		else
			return "image choice";
	}
}
