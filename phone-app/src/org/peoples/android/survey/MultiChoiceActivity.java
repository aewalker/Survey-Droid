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
import org.peoples.android.R;

import android.os.Bundle;
import android.util.SparseBooleanArray;
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
		//FIXME set to proper views once we get horizontal/vertical ones made
		setContentView(R.layout.multiple_choice);
		
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
	}

	@Override
	protected boolean isAnswered()
	{
		//getCheckItemIds is @deprecated as of API 8; we should move to
		//getCheckedItemIds if we move up
		
		//ignore the dead code warning for now
		if (!Config.ALLOW_NO_CHOICES && listView.getCheckItemIds().length == 0)
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
		listView.setAdapter(new ImageOrTextAdapter(this, list));
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
}