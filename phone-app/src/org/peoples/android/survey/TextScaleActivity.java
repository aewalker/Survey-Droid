/*---------------------------------------------------------------------------*
 * TextScaleActivity.java                                                    *
 *                                                                           *
 * Shows the user a question with a slider that allows them to select a      *
 * value between 1 and 100.  This version shows some text at each end of the *
 * scale.                                                                    *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import org.peoples.android.R;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Shows the user a question with a slider that allows them to select a value
 * between 1 and 100.  This version shows some text at each end of the scale.
 * 
 * @author Austin Walker
 */
public class TextScaleActivity extends QuestionActivity
{
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		//FIXME set based on orientation when better layouts are written
		setContentView(R.layout.text_scale);
		
		//set the buttons up
		findViewById(
				R.id.text_scale_backButton).setOnClickListener(prevListener);
		findViewById(
				R.id.text_scale_nextButton).setOnClickListener(nextListener);
	}
	
	@Override
	protected void answer()
	{
		SeekBar input = (SeekBar) findViewById(R.id.text_scale_slider);
		int ans = input.getProgress();
		ans++; //because SeekBar starts at 0
		ans *= (100 / input.getMax()); //have to scale the answer
		
		survey.answer(ans);
	}

	@Override
	protected boolean isAnswered()
	{ //any position on the bar is fine, so this is just true
		return true;
	}
	
	@Override
	protected String getInvalidAnswerMsg()
	{ //this should never get called because of the way isAnswered is written
		return null;
	}

	@Override
	protected void onSurveyLoaded()
	{
		//set the question text
		TextView qText = (TextView) findViewById(R.id.text_scale_question);
		qText.setText(survey.getText());
		
		//set the text on each end of the scale
		TextView lowText = (TextView) findViewById(R.id.text_scale_lowText);
		TextView highText = (TextView) findViewById(R.id.text_scale_highText);
		lowText.setText(survey.getLowText());
		highText.setText(survey.getHighText());
	}
}