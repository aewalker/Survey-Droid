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
import android.graphics.Bitmap;
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
		String[] strings = new String[choices.length];
		Bitmap[] pics = new Bitmap[choices.length];
		for (int i = 0; i < choices.length; i++)
		{
			pics[i] = choices[i].getImg();
			strings[i] = choices[i].getText();
		}
		listView = (ListView) findViewById(android.R.id.list);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		ImageOrTextAdapter<String> iota = new ImageOrTextAdapter<String>(this,
				android.R.layout.simple_list_item_single_choice,
				strings, pics);
		listView.setAdapter(iota);
		int[] ansChoices = survey.getAnswerChoices();
		if (ansChoices != null)
		{
			if (Config.D) Log.d(TAG, "Previous answer: " + ansChoices[0]);
			listView.setItemChecked(ansChoices[0], true);
		}
	}
	
	@Override
	protected void answer()
	{
		Collection<Choice> answer = new ArrayList<Choice>();
		Choice[] choices = survey.getChoices();
		answer.add(choices[listView.getCheckedItemPosition()]);
		survey.answer(answer);
		if (Config.D) Log.d(TAG, "answered with: "
				+ answer.toArray()[0].toString());
	}

	@Override
	protected boolean isAnswered()
	{
		if (listView.getCheckedItemPosition() == ListView.INVALID_POSITION)
			return false;
		return true;
	}
	
	@Override
	protected String getInvalidAnswerMsg()
	{
		return "You must select a choice";
	}
}
