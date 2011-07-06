/*---------------------------------------------------------------------------*
 * SingleChoiceActivty.java                                                  *
 *                                                                           *
 * Shows the user a question with set options to pick from.  This type of    *
 * question only allows the user to pick a single answer.                    *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import java.util.ArrayList;
import java.util.Collection;

import org.peoples.android.Config;
import org.peoples.android.ImageOrTextAdapter;
import org.peoples.android.R;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Shows the user a question with set options to pick from.  This type of
 * question only allows the user to pick a single answer.
 * 
 * @author Austin Walker
 */
public class SingleChoiceActivity extends QuestionActivity
{
	//the logging tag
	private static final String TAG = "SingleChoiceActivity";
	
	//the main list where choices are shown
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);

		//setting the layout of the activity
        Display display = ((WindowManager)
        		getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        //check what orientation the phone is in
        //getOrientation() is depreciated as of API 8, but we're targeting
        //API 7, so we have to use it
        if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
        {
        	setContentView(R.layout.multiple_choice_horiz);
        }
        else
        {
        	setContentView(R.layout.multiple_choice_vert);
        }
		
		//set the buttons up
		findViewById(R.id.multiple_choice_backButton).setOnClickListener(
				prevListener);
		findViewById(R.id.multiple_choice_nextButton).setOnClickListener(
				nextListener);
	}

	@Override
	protected void onSurveyLoaded()
	{
		//set the question text
		TextView qText = (TextView) findViewById(R.id.multiple_choice_question);
		qText.setText(survey.getText());
		
		Choice[] choices = survey.getChoices();
		Object[][] list = new Object[choices.length][2];
		for (int i = 0; i < choices.length; i++)
		{
			list[i][ImageOrTextAdapter.IMG_POS] = choices[i].getImg();
			list[i][ImageOrTextAdapter.STRING_POS] = choices[i].getText();
		}
		//FIXME still doesn't look like items are selected
		listView = (ListView) findViewById(android.R.id.list);
		listView.setAdapter(new ImageOrTextAdapter(this,
				ListView.CHOICE_MODE_SINGLE, list, this));
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
	
	@Override
	protected void answer()
	{
		Collection<Choice> answer = new ArrayList<Choice>();
		Choice[] choices = survey.getChoices();
		//answer.add(choices[listView.getCheckedItemPosition()]);
		answer.add(choices[(Integer) getSelected().toArray()[0]]);
		survey.answer(answer);
		if (Config.D) Log.d(TAG, "answered with: "
				+ answer.toArray()[0].toString());
	}

	@Override
	protected boolean isAnswered()
	{
		if (Config.D)
			Log.d(TAG, "Answer index: " + listView.getCheckedItemPosition());
		//if (listView.getCheckedItemPosition() == ListView.INVALID_POSITION)
		if (getSelected().size() == 0)
			return false;
		if (getSelected().size() > 1)
			return false;
		return true;
	}
	
	@Override
	protected String getInvalidAnswerMsg()
	{
		if (getSelected().size() == 0)
			return "You must select a choice";
		else return "You can only select one choice";
	}
}
