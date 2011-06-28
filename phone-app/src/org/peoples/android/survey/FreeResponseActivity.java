/*---------------------------------------------------------------------------*
 * FreeResponseActivty.java                                                  *
 *                                                                           *
 * Shows the user a free response question where they can type in any        *
 * response.                                                                 *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import org.peoples.android.Config;
import org.peoples.android.R;

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
		
		//set the question text
		TextView qText = (TextView) findViewById(R.id.free_response_question);
		qText.setText(survey.getText());
		
		//grab the input area for future use
		input = (EditText) findViewById(R.id.free_response_textEntry);
	}
	
	@Override
	protected void answer()
	{
		survey.answer(input.getText().toString());
	}

	@Override
	protected boolean isAnswered()
	{ //ignore the dead code warning here for now
		if (!Config.ALLOW_BLANK_FREE_RESPONSE &&
			input.getText().toString().equals(""))
			return false;
		return true;
	}
	
	@Override
	protected String getInvalidAnswerMsg()
	{
		return "You must enter a response";
	}
}
