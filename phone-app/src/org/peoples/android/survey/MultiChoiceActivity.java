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
@SuppressWarnings("unused")
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
		//SparseBooleanArray checked = listView.getCheckedItemPositions();
		Collection<Integer> checked = getSelected();
		Collection<Choice> answers = new ArrayList<Choice>();
		Choice[] choices = survey.getChoices();
//		for (int i = 0; i < choices.length; i++)
//		{
//			if (checked.get(i, false))
//				answers.add(choices[i]);
//		}
		for (int i : checked)
		{
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
		
		//ignore the dead code warning for now
		//if (!Config.ALLOW_NO_CHOICES && listView.getCheckItemIds().length == 0)
		if (!Config.ALLOW_NO_CHOICES && getSelected().size() == 0)
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
		TextView qText =
			(TextView) findViewById(R.id.multiple_choice_question);
		qText.setText(survey.getText());
		
		Choice[] choices = survey.getChoices();
		Object[][] list = new Object[choices.length][2];
		for (int i = 0; i < choices.length; i++)
		{
			list[i][ImageOrTextAdapter.IMG_POS] = choices[i].getImg();
			list[i][ImageOrTextAdapter.STRING_POS] = choices[i].getText();
		}
		listView = (ListView) findViewById(android.R.id.list);
		ImageOrTextAdapter iotr = new ImageOrTextAdapter(this,
				ListView.CHOICE_MODE_MULTIPLE, list, this);
		listView.setAdapter(iotr);
		if (survey.getAnswerChoices() != null)
			iotr.setChecked(survey.getAnswerChoices());
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
}