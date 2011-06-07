/*---------------------------------------------------------------------------*
 * QuestionActivty.java                                                      *
 *                                                                           *
 * Shows the user a question and then reports the answer.                    *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import android.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.peoples.android.Config; 

/**
 * Activity that shows the user a single question and reports the answer.
 * 
 * @author Austin Walker
 * @author Henry Liu
 */
public class QuestionActivity extends ListActivity
{
	//intent extras
	public static final String QUESTION_TEXT = 
		"org.peoples.android.survey.QUESTION_TEXT";
	public static final String QUESTION_CHOICES =
		"org.peoples.android.survey.QUESTION_CHOICES";
	public static final String IS_FIRST_QUESTION =
		"org.peoples.android.survey.IS_FIRST_QUESTION";
	
	//logging tag
	private static final String TAG = "QuestionActivity";
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		if (Config.D) Log.d(TAG, "Creating QuestionActivity");
		
		//extract information from the intent
		Intent intent = getIntent();
		final String qText = intent.getStringExtra(QUESTION_TEXT);
		if (qText == null) throw new RuntimeException("No question text");
		final String[] choices = intent.getStringArrayExtra(QUESTION_CHOICES);
		if (choices == null) throw new RuntimeException("No question choices");
		final boolean isOnFirst =
			intent.getBooleanExtra(IS_FIRST_QUESTION, true);
		final int prevAnsIndex =
			intent.getIntExtra(SurveyService.EXTRA_ANS_INDEX, -1);
		String prevAnsText = "";
		if (prevAnsIndex == -1)
			prevAnsText = intent.getStringExtra(SurveyService.EXTRA_ANS_TEXT);
		
		//set the information to be used in the views
		setContentView(org.peoples.android.R.layout.survey_list_view);
		final TextView qTextView = (TextView) this.findViewById(
				org.peoples.android.R.id.question_textView);
		if (choices.length == 0)
		{
			setListAdapter(new ArrayAdapter<String>(this,
				org.peoples.android.R.layout.list_item, new String[] {prevAnsText}));
		}
		else
		{
			//TODO change this to be ArrayAdapter<Choice> and implement
			//toString() in Choice, then remove the getChoiceTexts() method
			//from Survey
			setListAdapter(new ArrayAdapter<String>(this,
					R.layout.simple_list_item_single_choice, choices));
			if (prevAnsIndex != -1)
				getListView().setItemChecked(prevAnsIndex, true);
		}
		qTextView.setText(qText);
		
		//set up the buttons
		Button prev = (Button) findViewById(org.peoples.android.R.id.button1);
		prev.setText("Previous Question");
		Button next = (Button) findViewById(org.peoples.android.R.id.button2);
		next.setText("Next Question");
		
		//Handler for "previous" button
        final View.OnClickListener prevListener = new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	if (isOnFirst)
            	{ //can't go back from first question
            		Toast.makeText(getApplicationContext(),
            				"Cannot go back; already on first question",
            				Toast.LENGTH_SHORT).show();
            	}
            	else
            	{ //request the service start the next activity
            		Intent prevIntent = new Intent(getApplicationContext(),
            				SurveyService.class);
            		prevIntent.setAction(SurveyService.ACTION_PREV_QUESTION);
            		startService(prevIntent);
            		finish();
            	}
            }
        };
        prev.setOnClickListener(prevListener);
        
        //Handler for the "next" button
        final View.OnClickListener nextListener = new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	int choice = getListView().getCheckedItemPosition();
            	boolean isMC = (choices.length != 0); //isMultipleChoice
            	
            	if ((isMC && choice != -1) || !isMC)
            	{ //question has been answered properly
            		Intent nextIntent = new Intent(getApplicationContext(),
            				SurveyService.class);
            		nextIntent.setAction(SurveyService.ACTION_NEXT_QUESTION);
            		if (isMC)
            		{
            			nextIntent.putExtra(
            					SurveyService.EXTRA_ANS_INDEX, choice);
            		}
            		else
            		{
            			EditText eText = (EditText) findViewById(
            					org.peoples.android.R.id.editText1);
            			nextIntent.putExtra(SurveyService.EXTRA_ANS_TEXT,
            					eText.getText().toString());
            		}
            		startService(nextIntent);
            		finish();
            	}
            	else
            	{ //no answer has been given
            		Toast.makeText(getApplicationContext(),
            				"Please select a choice",
            				Toast.LENGTH_SHORT).show();
            	}
            }
        };
        next.setOnClickListener(nextListener);
	}
}
