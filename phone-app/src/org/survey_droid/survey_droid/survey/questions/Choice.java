/*---------------------------------------------------------------------------*
 * Choice.java                                                               *
 *                                                                           *
 * Model for a survey choice.  Holds a String or Image as it's choice text.  *
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
package org.survey_droid.survey_droid.survey.questions;

import android.graphics.Bitmap;

/**
 * Model for a survey choice.
 * 
 * @author Diego Vargas
 * @author Austin Walker
 */
public class Choice
{
	/** the text to display as an option if this is a text choice */
	public final String choice_text;
	
	/** the bitmap to display as an option if this is an image choice */
	public final Bitmap choice_img;
	
	/** is this a text or image choice? */
	public final byte type;
	
	public static final byte IMAGE_CHOICE_BYTE = 0x00;
	public static final byte TEXT_CHOICE_BYTE = 0x01;
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Create a new text Choice.
	 * 
	 * @param text the text the Choice should contain
	 */
	public Choice(String text)
	{
		choice_text = text;
		type = TEXT_CHOICE_BYTE;
		choice_img = null;
	}
	
	/**
	 * Create a new image Choice.
	 * 
	 * @param b the bitmap image this choice will display
	 */
	public Choice(Bitmap b)
	{
		choice_img = b;
		choice_text = null;
		type = IMAGE_CHOICE_BYTE;
	}
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Get a string representation of this choice.
	 * 
	 * @return the Choice's text
	 */
	public String toString()
	{
		if (type == TEXT_CHOICE_BYTE)
			return choice_text;
		else
			return "image choice";
	}
}
