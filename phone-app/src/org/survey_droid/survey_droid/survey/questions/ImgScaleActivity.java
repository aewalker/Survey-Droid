/*---------------------------------------------------------------------------*
 * ImgScaleActivty.java                                                      *
 *                                                                           *
 * Shows the user a question with a slider that allows them to select a      *
 * value between 1 and 100.  This version shows an image at each end of the  *
 * scale.                                                                    *
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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import org.survey_droid.survey_droid.R;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <p>Shows the user a question with a slider that allows them to select a value
 * between 1 and 100.  This version shows an image at each end of the scale.</p>
 * 
 * <p>For the data formats for this question, see {@link ScaleActivity}.</p>
 * 
 * @author Austin Walker
 */
public class ImgScaleActivity extends ScaleActivity
{
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		findViewById(R.id.scale_lowText).setVisibility(View.GONE);
		findViewById(R.id.scale_highText).setVisibility(View.GONE);
	}
	
	@Override
	protected void onSurveyLoaded()
	{
		try
		{
			//set the question text
			TextView qText = (TextView) findViewById(R.id.scale_question);
			DataInputStream dis = new DataInputStream(
				new ByteArrayInputStream(survey.getQuestionData()));
			qText.setText(dis.readUTF());
			super.onSurveyLoaded();
			
			//set the scale images
			ImageView lowImg = (ImageView) findViewById(R.id.scale_lowImg);
			ImageView highImg = (ImageView) findViewById(R.id.scale_highImg);
			lowImg.setImageBitmap(BitmapFactory.decodeStream(dis));
			highImg.setImageBitmap(BitmapFactory.decodeStream(dis));
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to get question data", e);
		}
	}
}