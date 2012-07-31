/*---------------------------------------------------------------------------*
 * ChoiceActivty.java                                                        *
 *                                                                           *
 * Shows the user a question with set options to pick from.                  *
 *---------------------------------------------------------------------------*
 * Copyright (C) 2011-2012 Survey Droid Contributors                         *
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
package org.survey_droid.survey_droid.survey.questions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.R;
import org.survey_droid.survey_droid.survey.QuestionActivity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

/**
 * <p>Shows the user a question with set options to pick from.  Can be configured
 * to allow either a single choice or multiple choices to be picked.</p>
 * <p>Data format for this question is:</p>
 * <table><tr>
 * <td>Question Prompt</td>
 * <td>Multi-Choice Flag</td>
 * <td>No-Choices Flag</td>
 * <td>Number of Choices</td>
 * <td>Type of Choice 1</td>
 * <td>Text/Image Choice 1</td>
 * <td>Type of Choice 2</td>
 * <td>Text/Image Choice 2</td>
 * <td>...</td>
 * </tr><tr>
 * <td>String</td>
 * <td>boolean</td>
 * <td>boolean</td>
 * <td>int</td>
 * <td>byte</td>
 * <td>String/Bitmap</td>
 * <td>byte</td>
 * <td>String/Bitmap</td>
 * <td>...</td>
 * </tr></table>
 * 
 * <p>Data format for this question's answers</p>
 * <table><tr>
 * <td>Number of answers</td>
 * <td>Index of 1<sup>st</sup> Answer</td>
 * <td>Index of 2<sup>nd</sup> Answer</td>
 * <td>...</td>
 * </tr><tr>
 * <td>int</td>
 * <td>int</td>
 * <td>int</td>
 * <td>...</td>
 * </tr></table>
 * 
 * @author Austin Walker
 */
public class ChoiceActivity extends QuestionActivity
{
	/** the logging tag */
	private static final String TAG = "SingleChoiceActivity";
	
	/** the main list where choices are shown */
	private ListView listView;
	
	/** Contains the choices for this question in order */
	private List<Choice> choices = new ArrayList<Choice>();
	
	/** If true, allow multiple choices to be selected */
	private boolean multipleChoice = false;
	
	/**
	 * If true and this is a multiple choice question, allow no choices as a
	 * valid answer
	 */
	private boolean allowNoChoices = false;
	
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
		try
		{
			ByteArrayInputStream bytesIn = new ByteArrayInputStream(
				survey.getQuestionData());
			DataInputStream dis = new DataInputStream(bytesIn);
			
			//set the question text
			TextView qText = (TextView) findViewById(
				R.id.multiple_choice_question);
			String title = dis.readUTF();
			Util.v(null, TAG, "Question prompt: \"" + title + "\"");
			qText.setText(title);
			
			//determine question options
			if (dis.readBoolean())
			{
				multipleChoice = true;
				Util.v(null, TAG, "multiple choice flag set");
			}
			if (dis.readBoolean())
			{
				allowNoChoices = true;
				Util.v(null, TAG, "allow no choices flag set");
			}
			
			//de-serialize all the choices
			int numChoices = dis.readInt();
			Util.v(null, TAG, "Looking for " + numChoices + " choices");
			for (int i = 0; i < numChoices; i++)
			{
				byte type = dis.readByte();
				Choice c = null;
				switch (type)
				{
				case Choice.TEXT_CHOICE_BYTE:
					c = new Choice(dis.readUTF());
					break;
				case Choice.IMAGE_CHOICE_BYTE:
					c = new Choice(BitmapFactory.decodeStream(dis));
					break;
				default:
					//wtf how did this happen?  stupid server
					throw new RuntimeException("Bad choice type: " + type);
				}
				choices.add(c);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to load question", e);
		}
		
		String[] strings = new String[choices.size()];
		Bitmap[] pics = new Bitmap[choices.size()];
		for (int i = 0; i < choices.size(); i++)
		{
			pics[i] = choices.get(i).choice_img;
			strings[i] = choices.get(i).choice_text;
		}
		listView = (ListView) findViewById(android.R.id.list);
		ChoiceAdapter choiceAdapter;
		if (multipleChoice)
		{
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			choiceAdapter = new ChoiceAdapter(this,
					android.R.layout.simple_list_item_multiple_choice,
					choices);
		}
		else
		{
			listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			choiceAdapter = new ChoiceAdapter(this,
					android.R.layout.simple_list_item_single_choice,
					choices);
		}
		listView.setAdapter(choiceAdapter);
		
		//look for previous answers
		//answers for choice questions are stored as a series of ints that
		//are indices into the choices list
		int[] ansChoices = null;
		try
		{
			byte[] ans = survey.getAnswer();
			if (ans != null)
			{
				DataInputStream dis = new DataInputStream(
					new ByteArrayInputStream(ans));
				ansChoices = new int[dis.readInt()];
				for (int i = 0; i < ansChoices.length; i++)
				{
					ansChoices[i] = dis.readInt();
				}
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to get previous answer", e);
		}
		if (ansChoices != null)
		{
			StringBuilder msg = new StringBuilder("Previous answer: ");
			for (int i = 0; i < ansChoices.length; i++)
			{
				msg.append(choices.get(ansChoices[i]));
				msg.append(" (");
				msg.append(ansChoices[0]);
				msg.append(") ");
			}
			Util.d(null, TAG, msg.toString());
			for (int i = 0; i < ansChoices.length; i++)
			{
				listView.setItemChecked(ansChoices[0], true);
			}
		}
	}

	@Override
	protected boolean isAnswered()
	{
		if (multipleChoice && !allowNoChoices) return true;
		if (listView.getCheckedItemPosition() == ListView.INVALID_POSITION)
			return false;
		return true;
	}
	
	@Override
	protected String getInvalidAnswerMsg()
	{
		return "You must select a choice";
	}

	@Override
	protected byte[] getAnswer()
	{
		SparseBooleanArray checked = listView.getCheckedItemPositions();
		//first pass: get the number of answers
		int numAns = 0;
		for (int i = 0; i < choices.size(); i++)
		{
			if (checked.get(i, false))
			{
				numAns++;
			}
		}
		//second pass: write to the array (and debug string)
		ByteArrayOutputStream out = new ByteArrayOutputStream(4 + 4 * numAns);
		DataOutputStream dataOut = new DataOutputStream(out);
		try
		{
			dataOut.writeInt(numAns);
		}
		catch (IOException e)
		{
			throw new RuntimeException(
				"Failed to create answer array", e);
		}
		StringBuilder msg = new StringBuilder("Answered with:");
		for (int i = 0; i < choices.size(); i++)
		{
			if (checked.get(i, false))
			{
				msg.append(' ');
				msg.append(choices.get(i).toString());
				msg.append('(');
				msg.append(i);
				msg.append(')');
				try
				{
					dataOut.writeInt(i);
				}
				catch (IOException e)
				{
					throw new RuntimeException(
						"Failed to create answer array", e);
				}
			}
		}

		Util.d(null, TAG, msg.toString());
		return out.toByteArray();
	}
}
