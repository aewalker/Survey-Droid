/*---------------------------------------------------------------------------*
 * FreeResponseActivty.java                                                  *
 *                                                                           *
 * Shows the user a free response question where they can type in any        *
 * response.                                                                 *
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

import org.surveydroid.android.R;
import org.surveydroid.android.Config;
import org.surveydroid.android.Util;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Shows the user a free response question where they can type in any response.
 * 
 * @author Austin Walker
 */
public class FreeResponseActivity extends QuestionActivity
{
	//the field where the user types his/her answer
	private EditText input;
	
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
	protected void answer()
	{
		survey.answer(input.getText().toString());
		Util.d(this, TAG, "answered with \""
				+ input.getText().toString() + "\"");
	}

	@Override
	protected boolean isAnswered()
	{ //ignore the dead code warning here for now
		if (!Config.getSetting(this, Config.ALLOW_BLANK_FREE_RESPONSE,
				Config.ALLOW_BLANK_FREE_RESPONSE_DEFAULT) &&
			input.getText().toString().equals(""))
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
		//set the question text
		TextView qText = (TextView) findViewById(R.id.free_response_question);
		qText.setText(survey.getText());
		EditText tEdit = (EditText) findViewById(R.id.free_response_textEntry);
		tEdit.setText(survey.getAnswerText());
	}
}
