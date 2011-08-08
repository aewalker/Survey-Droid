/*---------------------------------------------------------------------------*
 * SurveyDoneActivity.java                                                   *
 *                                                                           *
 * Tells the user that the survey has been successfully submitted.  Note     *
 * that this screen's being displayed to a user does not say anything about  *
 * the survey being sent to the server; it only means that they have         *
 * finished taking it.                                                       *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.peoples.android.R;
import org.peoples.android.Util;

/**
 * Activity that tells the subject that they are done with the survey.
 * 
 * @author Austin Walker
 */
public class SurveyDoneActivity extends Activity
{
	//logging tag
	private static final String TAG = "SurveyDoneActivity";
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		Util.d(null, TAG, "Creating SurveyDoneActivity");
		
		//setting the layout of the activity
        setContentView(R.layout.survey_done);
		
		Button back =
			(Button) findViewById(R.id.survey_done_exitButton);
		back.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				finish();
			}
		});
	}
}
