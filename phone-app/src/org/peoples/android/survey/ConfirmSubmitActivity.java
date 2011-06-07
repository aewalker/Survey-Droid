/*---------------------------------------------------------------------------*
 * ConfirmSubmitActivity.java                                                *
 *                                                                           *
 * Asks the user to either confirm their answers or go back.                 *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.peoples.android.Config;
import org.peoples.android.R;

/**
 * Activity that asks the subject to either confirm their survey answers so
 * they can be written to the database, or go back and change answers.
 * 
 * @author Austin Walker
 * @author Henry Liu
 */
public class ConfirmSubmitActivity extends Activity
{
	//logging tag
	private static final String TAG = "ConfirmSubmitActivity";
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		if (Config.D) Log.d(TAG, "Creating ConfirmSubmitActivity");
		
		//set up views
		setContentView(R.layout.confirmpage);
		final TextView tView = (TextView) findViewById(R.id.confirm);
		tView.setText("Are you sure you want to submit your responses?");
		
		Button back = (Button) findViewById(R.id.back);
		back.setText("No, go back");
		back.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent backIntent = new Intent(
						getApplicationContext(), SurveyService.class);
				backIntent.setAction(SurveyService.ACTION_PREV_QUESTION);
				startService(backIntent);
				finish();
			}
		});
		
		Button finish = (Button) findViewById(R.id.finish);
		finish.setText("Yes, submit my responses");
		finish.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent finishIntent = new Intent(
						getApplicationContext(), SurveyService.class);
				finishIntent.setAction(SurveyService.ACTION_SUBMIT_ANSWERS);
				startService(finishIntent);
				finish();
			}
		});
	}
}
