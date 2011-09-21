/*---------------------------------------------------------------------------*
 * MultiChoiceActivty.java                                                   *
 *                                                                           *
 * Shows the user a question with set options to pick from.  This type of    *
 * question allows the user to pick multiple answers.                        *
 *---------------------------------------------------------------------------*
 * Copyright 2011 Sema Berkiten, Vladimir Costescu, Henry Liu, Diego Vargas, *
 * Austin Walker, and Tony Xiao                                              *
 *                                                                           *
 * This file is part of Survey Droid.                                        *
 *                                                                           *
 * Survey Droid is free software: you can redistribute it and/or modify      *
 * it under the terms of the GNU General Public License as published by      *
 * the Free Software Foundation, either version 3 of the License, or         *
 * (at your option) any later version.                                       *
 *                                                                           *
 * Survey Droid is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of            *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             *
 * GNU General Public License for more details.                              *
 *                                                                           *
 * You should have received a copy of the GNU General Public License         *
 * along with Survey Droid.  If not, see <http://www.gnu.org/licenses/>.     *
 *****************************************************************************/
package org.surveydroid.android.survey;

import java.util.ArrayList;
import java.util.Collection;

import org.surveydroid.android.R;
import org.surveydroid.android.Config;
import org.surveydroid.android.ImageOrTextAdapter;
import org.surveydroid.android.Util;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
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
			Util.d(this, TAG, answer);
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
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		ImageOrTextAdapter<String> iota = new ImageOrTextAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice,
				strings, pics);
		listView.setAdapter(iota);
		int[] ansChoices = survey.getAnswerChoices();
		if (ansChoices != null)
		{
			for (int item : ansChoices)
			{
				listView.setItemChecked(item, true);
			}
		}
	}
}