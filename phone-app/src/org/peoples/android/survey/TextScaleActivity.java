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
import android.view.View;
import android.widget.TextView;

/**
 * Shows the user a question with a slider that allows them to select a value
 * between 1 and 100.  This version shows some text at each end of the scale.
 * 
 * @author Austin Walker
 */
public class TextScaleActivity extends ScaleActivity
{
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		findViewById(R.id.scale_lowImg).setVisibility(View.GONE);
		findViewById(R.id.scale_highImg).setVisibility(View.GONE);
	}
	
	@Override
	protected void onSurveyLoaded()
	{
		super.onSurveyLoaded();
		//set the text on each end of the scale
		TextView lowText = (TextView) findViewById(R.id.scale_lowText);
		TextView highText = (TextView) findViewById(R.id.scale_highText);
		lowText.setText(survey.getLowText());
		highText.setText(survey.getHighText());
	}
}