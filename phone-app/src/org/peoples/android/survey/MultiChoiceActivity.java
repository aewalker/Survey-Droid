/*---------------------------------------------------------------------------*
 * MultiChoiceActivty.java                                                   *
 *                                                                           *
 * Shows the user a question with set options to pick from.  This type of    *
 * question allows the user to pick multiple answers.                        *
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
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Shows the user a question with set options to pick from.  This type of
 * question allows the user to pick multiple answers.
 * 
 * @author Austin Walker
 */
public class MultiChoiceActivity extends QuestionActivity
{
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
	protected void answer()
	{
		SparseBooleanArray checked = listView.getCheckedItemPositions();
		Collection<Choice> answers = new ArrayList<Choice>();
		Choice[] choices = survey.getChoices();
		for (int i = 0; i < choices.length; i++)
		{
			if (checked.get(i, false))
				answers.add(choices[i]);
		}
		survey.answer(answers);
		
		if (Config.D)
		{
			String answer;
			if (answers.size() != 0)
			{
				StringBuilder answerBuilder =
					new StringBuilder("answered with: ");
				for (Choice ans : answers)
				{
					answerBuilder.append(ans.toString() + ", ");
				}
				answer = answerBuilder.toString().substring(
						0, answerBuilder.length() - 2);
			}
			else
			{
				answer = "no answers given";
			}
			Log.d(TAG, answer);
		}
	}

	@Override
	protected boolean isAnswered()
	{
		//getCheckItemIds is @deprecated as of API 8; we should move to
		//getCheckedItemIds if we move up
		if (!Config.getSetting(this, Config.ALLOW_NO_CHOICES,
				Config.ALLOW_NO_CHOICES_DEFAULT)
				&& listView.getCheckItemIds().length == 0)
			return false;
		return true;
	}
	
	@Override
	protected String getInvalidAnswerMsg()
	{
		return "You must select at least one choice";
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
		ImageOrTextAdapter<String> iota = new ImageOrTextAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice,
				strings, pics);
		listView.setAdapter(iota);
		//if (survey.getAnswerChoices() != null)
		//iota.setChecked(survey.getAnswerChoices());
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
}