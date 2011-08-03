/*---------------------------------------------------------------------------*
 * FreeResponseActivty.java                                                  *
 *                                                                           *
 * Shows the user a free response question where they can type in any        *
 * response.                                                                 *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import org.peoples.android.Config;
import org.peoples.android.R;
import org.peoples.android.Util;

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
