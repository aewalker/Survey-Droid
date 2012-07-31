/*---------------------------------------------------------------------------*
 * FreeResponseActivty.java                                                  *
 *                                                                           *
 * Shows the user a free response question where they can type in any        *
 * response.                                                                 *
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

import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.R;
import org.survey_droid.survey_droid.survey.QuestionActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

/**
 * <p>Shows the user a free response question where they can type in any response.</p>
 * 
 * <p>Data format for this question activity is:</p>
 * <table><tr>
 * <td>Allow Black Response Flag</td><td>Question Prompt</td>
 * </tr><tr>
 * <td>boolean</td><td>String</td>
 * </tr></table>
 * 
 * <p>Data format for this question's answers is:</p>
 * <table><tr>
 * <td>Answer Text</td>
 * </tr><tr>
 * <td>String</td>
 * </tr></table>
 * 
 * @author Austin Walker
 */
public class FreeResponseActivity extends QuestionActivity
{
	/** the field where the user types his/her answer */
	private EditText input;
	
	/** does the question allow a blank response? */
	private boolean allowBlank = false;
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		setContentView(R.layout.free_response);
		
		//add the button listeners
		findViewById(R.id.free_response_backButton).setOnClickListener(
				prevListener);
		findViewById(R.id.free_response_nextButton).setOnClickListener(
				nextListener);
		
		//grab the input area for future use
		input = (EditText) findViewById(R.id.free_response_textEntry);
	}
	
	@Override
	protected byte[] getAnswer()
	{
		String ans = input.getText().toString();
		Util.d(null, TAG, "answered with \""
				+ input.getText().toString() + "\"");
		return ans.getBytes();
	}

	@Override
	protected boolean isAnswered()
	{
		if (!allowBlank && input.getText().toString().equals(""))
			return false;
		return true;
	}
	
	@Override
	protected String getInvalidAnswerMsg()
	{
		return "You must enter a response";
	}

	@Override
	protected void onSurveyLoaded()
	{
		try
		{
			//set the question text
			TextView qText = (TextView) findViewById(R.id.free_response_question);
			DataInputStream dis = new DataInputStream(
				new ByteArrayInputStream(survey.getQuestionData()));
			allowBlank = dis.readBoolean();
			qText.setText(dis.readUTF());
			EditText tEdit = (EditText) findViewById(R.id.free_response_textEntry);
			byte[] prevAns = survey.getAnswer();
			if (prevAns != null)
				tEdit.setText(new String(prevAns));
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to load question", e);
		}
	}
}
