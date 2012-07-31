/*---------------------------------------------------------------------------*
 * SurveyServiceTest.java                                                    *
 *                                                                           *
 * Tests how well the survey service runs.                                   *
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
package org.survey_droid.survey_droid.test;

import java.util.concurrent.atomic.AtomicBoolean;

import org.survey_droid.survey_droid.SurveyDroid;
import org.survey_droid.survey_droid.survey.QuestionActivity;
import org.survey_droid.survey_droid.survey.Survey;
import org.survey_droid.survey_droid.survey.SurveyService;
import org.survey_droid.survey_droid.survey.SurveyService.SurveyType;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.test.ServiceTestCase;
import android.util.Log;

//FIXME find a way to remove the dependency on Survey working correctly
@Tests({SurveyService.class})
public class SurveyServiceTest extends ServiceTestCase<SurveyService>
{
	private final String TAG = "SurveyServiceTest";
	private final AtomicBoolean started = new AtomicBoolean(false);
	private Class<? extends Activity> intendedActivity;
	
	public SurveyServiceTest()
	{
		super(SurveyService.class);
	}
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		setApplication(new SurveyDroid()); //needed for config stuff
		
		setContext(new ContextWrapper(getContext()) //to determine when activities would be started
		{
			@Override
			public void startActivity(Intent activity)
			{
				Log.i(TAG, "Starting Activity");
				try
				{
					if (!intendedActivity.isAssignableFrom(Class.forName(activity.getComponent().getClassName())))
					{
						fail("Wrong activity started!");
					}
					synchronized (started)
					{
						started.set(true);
						started.notify();
					}
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		});
	}
	
	private void startWithDummySurvey()
	{
		Log.i(TAG, "staring service with dummy survey");
		Intent surveyIntent =
				new Intent(getContext(), SurveyService.class);
		surveyIntent.setAction(SurveyService.ACTION_SURVEY_READY);
		surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_TYPE,
				SurveyType.SURVEY_TYPE_USER_INIT.ordinal());
		surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_ID,
				Survey.DUMMY_SURVEY_ID);
		startService(surveyIntent);
	}
	
	private void startSurvey()
	{
		Log.i(TAG, "starting survey");
		intendedActivity = QuestionActivity.class;
		Intent startIntent = new Intent(getContext(), SurveyService.class);
		startIntent.setAction(SurveyService.ACTION_SHOW_SURVEY);
		synchronized (started)
		{
			getService().onStartCommand(startIntent, 0, 0);
			try
			{
				started.wait(10000);
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}
		assertTrue("Activity should be started", started.get());
	}
	
	private void endSurvey()
	{
		Log.i(TAG, "ending survey");
		Intent endIntent = new Intent(getContext(), SurveyService.class);
		endIntent.setAction(SurveyService.ACTION_END_SURVEY);
		getService().onStartCommand(endIntent, 0, 0);
	}
	
	private void quitSurvey()
	{
		Log.i(TAG, "quiting survey");
		Intent quitIntent = new Intent(getContext(), SurveyService.class);
		quitIntent.setAction(SurveyService.ACTION_QUIT_SURVEY);
		getService().onStartCommand(quitIntent, 0, 0);
	}
	
	public void startEnd() throws InterruptedException
	{
		startWithDummySurvey();
		startSurvey();
		endSurvey();
	}
	
	public void startQuit()
	{
		startWithDummySurvey();
		startSurvey();
		quitSurvey();
	}
}
