/*---------------------------------------------------------------------------*
 * SlidingScaleImgQuestion.java                                              *
 *                                                                           *
 * Type of question that allows the subject pick a value between 1 and 100   *
 * by moving a slider with an image on each end.                             *
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

import org.surveydroid.android.Base64Coder;
import org.surveydroid.android.database.SurveyDroidDB;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Question that allows the subject to enter their own text as a response.
 * 
 * @author Austin Walker
 */
public class SlidingScaleImgQuestion extends Question
{
	//images that go on the top and bottom of the scale
	private final Bitmap imgHigh;
	private final Bitmap imgLow;
	
	/**
	 * Constructor
	 * 
	 * @param text - the question text
	 * @param id - the question id
	 * @param b - the {@link Branch}es from this question
	 * @param imgLow - base 64 encoded image for the low end of the scale
	 * @param imgHigh - base 64 encoded image for the high end of the scale
	 * @param ctxt - the {@link Context} (used for database calls)
	 */
	public SlidingScaleImgQuestion(String text, int id, Collection<Branch> b,
			char[] imgLow, char[] imgHigh, Context ctxt)
	{
		super(text, id, b, SurveyDroidDB.QuestionTable.SCALE_IMG, ctxt);
		
		//TODO if we upgrade to API 8, use the built in Android base 64 class
		byte[] lowImgData = Base64Coder.decode(imgLow);
		this.imgLow = BitmapFactory.decodeByteArray(
				lowImgData, 0, lowImgData.length);
		
		byte[] highImgData = Base64Coder.decode(imgHigh);
		this.imgHigh = BitmapFactory.decodeByteArray(
				highImgData, 0, highImgData.length);
	}
	
	/**
	 * Returns the Bitmap created from the base 64 string given to the
	 * constructor as the low image.
	 * 
	 * @return the low {@link Bitmap}
	 */
	public Bitmap getLowImg()
	{
		return imgLow;
	}
	
	/**
	 * Returns the Bitmap created from the base 64 string given to the
	 * constructor as the high image.
	 * 
	 * @return the high {@link Bitmap}
	 */
	public Bitmap getHighImg()
	{
		return imgHigh;
	}
	
	/**
	 * Answer this question.
	 * 
	 * @param val - the value on the scale between 0 and 100
	 * 
	 * @return the new {@link Answer} created
	 */
	public Answer answer(int val)
	{
		if (val < 0 || val > 100)
			throw new IllegalArgumentException(
					"Scale value " + val + " is not between 0 and 100");
		Answer newAnswer = new Answer(this, id, val, ctxt);
		super.answer(newAnswer);
		return newAnswer;
	}
}
