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

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.survey_droid.survey_droid.SurveyDroid;
import org.survey_droid.survey_droid.content.ProviderContract;
import org.survey_droid.survey_droid.content.ProviderContract.SurveysTakenTable;
import org.survey_droid.survey_droid.content.SurveyDroidContentProvider;
import org.survey_droid.survey_droid.survey.QuestionActivity;
import org.survey_droid.survey_droid.survey.Survey;
import org.survey_droid.survey_droid.survey.SurveyService;
import org.survey_droid.survey_droid.survey.SurveyService.SurveyType;
import org.survey_droid.survey_droid.test.MockSurveyDroidProvider.ProviderListener;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.test.IsolatedContext;
import android.test.ServiceTestCase;
import android.test.mock.MockContentResolver;
import android.test.mock.MockContext;
import android.util.Log;

//FIXME find a way to remove the dependency on Survey working correctly
@Tests({SurveyService.class})
public class SurveyServiceTest extends ServiceTestCase<SurveyService>
{
	private final String TAG = "SurveyServiceTest";
	private final AtomicBoolean started = new AtomicBoolean(false);
	private Class<? extends Activity> intendedActivity;
	
	private MockSurveyDroidProvider provider = new MockSurveyDroidProvider();
	private final MockContentResolver resolver = new MockContentResolver();
	
	private Queue<Long> surveys = new LinkedList<Long>();
	
	private ProviderListener failListener = new ProviderListener()
	{
		@Override
		public void onQuery(String method, Uri uri,
				String[] cols, String selection,
				String[] selectionArgs, ContentValues values,
				String sortOrder)
		{
			fail("unexpected query to " + method + " - " + uri.toString());
		}
		
	};
	
	public SurveyServiceTest()
	{
		super(SurveyService.class);
		resolver.addProvider(ProviderContract.AUTHORITY, provider);
	}
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		provider.setListener(failListener);
		(new SurveyDroid()).onCreate(); //needed to force config setup
		
		setContext(new ContextWrapper(new IsolatedContext(resolver, getContext())) //to determine when activities would be started
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
	
	/* because this is annoying to type */
	private void wait(Object o)
	{
		try
		{
			o.wait(2000);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/* ----- Some helper methods to simulate user input ----- */
	
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
		surveys.add(Survey.DUMMY_SURVEY_ID);
		startService(surveyIntent);
	}
	
	private void addDummySurvey()
	{
		Log.i(TAG, "adding dummy survey");
		Intent surveyIntent =
				new Intent(getContext(), SurveyService.class);
		surveyIntent.setAction(SurveyService.ACTION_SURVEY_READY);
		surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_TYPE,
				SurveyType.SURVEY_TYPE_USER_INIT.ordinal());
		surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_ID,
				Survey.DUMMY_SURVEY_ID);
		surveys.add(Survey.DUMMY_SURVEY_ID);
		getService().onStartCommand(surveyIntent, 0, 0);
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
			wait(started);
		}
		assertTrue("Activity should be started", started.get());
	}
	
	private void endSurvey()
	{
		Log.i(TAG, "ending survey");
		Intent endIntent = new Intent(getContext(), SurveyService.class);
		endIntent.setAction(SurveyService.ACTION_END_SURVEY);
		final AtomicBoolean bool = new AtomicBoolean(false);
		provider.setListener(new ProviderListener()
		{
			@Override
			public void onQuery(String method, Uri uri, String[] cols,
					String selection, String[] selectionArgs,
					ContentValues values, String sortOrder)
			{
				if (surveys.peek() == Survey.DUMMY_SURVEY_ID)
				{
					fail("Dummy survey being recorded");
				}
				else
				{
					//TODO
					synchronized (bool)
					{
						bool.set(true);
						bool.notify();
					}
				}
			}
		});
		synchronized (bool)
		{
			getService().onStartCommand(endIntent, 0, 0);
			wait(bool);
		}
		if (surveys.peek() == Survey.DUMMY_SURVEY_ID)
			assertFalse(bool.get());
		else assertTrue(bool.get());
		surveys.poll();
	}
	
	private void quitSurvey()
	{
		Log.i(TAG, "quiting survey");
		Intent quitIntent = new Intent(getContext(), SurveyService.class);
		quitIntent.setAction(SurveyService.ACTION_QUIT_SURVEY);
		final AtomicBoolean bool = new AtomicBoolean(false);
		provider.setListener(new ProviderListener()
		{
			@Override
			public void onQuery(String method, Uri uri, String[] cols,
					String selection, String[] selectionArgs,
					ContentValues values, String sortOrder)
			{
				if (surveys.peek() == Survey.DUMMY_SURVEY_ID)
				{
					fail("Dummy survey being recorded");
				}
				else if (method.equals("insert") && SurveyDroidContentProvider.getTable(
						uri, null).equals(ProviderContract.SurveysTakenTable.NAME))
				{
					assertEquals(values.getAsLong(SurveysTakenTable.Fields.SURVEY_ID), surveys.poll());
					//TODO test code type and other stuff
					provider.setListener(failListener);
					synchronized (bool)
					{
						bool.set(true);
						bool.notify();
					}
				}
			}
		});
		synchronized (bool)
		{
			getService().onStartCommand(quitIntent, 0, 0);
			wait(bool);
		}

		if (surveys.peek() == Survey.DUMMY_SURVEY_ID)
		{
			assertFalse(bool.get());
			surveys.poll();
		}
		else assertTrue(bool.get());
	}
	
	private void cancelSurvey()
	{
		Log.i(TAG, "canceling survey");
		Intent cancelIntent = new Intent(getContext(), SurveyService.class);
		try
		{
			Field cancelAction = SurveyService.class.getDeclaredField("ACTION_CANCEL_SURVEY");
			cancelAction.setAccessible(true);
			cancelIntent.setAction((String) cancelAction.get(null));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		final AtomicBoolean bool = new AtomicBoolean(false);
		provider.setListener(new ProviderListener()
		{
			@Override
			public void onQuery(String method, Uri uri, String[] cols,
					String selection, String[] selectionArgs,
					ContentValues values, String sortOrder)
			{
				if (surveys.peek() == Survey.DUMMY_SURVEY_ID)
				{
					fail("Dummy survey being recorded");
				}
				else if (method.equals("insert") && SurveyDroidContentProvider.getTable(
						uri, null).equals(ProviderContract.SurveysTakenTable.NAME))
				{
					assertEquals(values.getAsLong(SurveysTakenTable.Fields.SURVEY_ID), surveys.poll());
					//TODO test code type and other stuff
					provider.setListener(failListener);
					synchronized (bool)
					{
						bool.set(true);
						bool.notify();
					}
				}
			}
		});
		synchronized (bool)
		{
			getService().onStartCommand(cancelIntent, 0, 0);
			wait(bool);
		}
		if (surveys.peek() == Survey.DUMMY_SURVEY_ID)
		{
			assertFalse(bool.get());
			surveys.poll();
		}
		else assertTrue(bool.get());
	}
	
	private void refresh()
	{
		Log.i(TAG, "refreshing");
		Intent refreshIntent = new Intent(getContext(), SurveyService.class);
		try
		{
			Field refreshAction = SurveyService.class.getDeclaredField("ACTION_REFRESH");
			refreshAction.setAccessible(true);
			refreshIntent.setAction((String) refreshAction.get(null));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		getService().onStartCommand(refreshIntent, 0, 0);
	}
	
	private void removeSurveys()
	{
		Log.i(TAG, "removing surveys");
		Intent removeIntent = new Intent(getContext(), SurveyService.class);
		try
		{
			Field removeAction = SurveyService.class.getDeclaredField("ACTION_REMOVE_SURVEYS");
			removeAction.setAccessible(true);
			removeIntent.setAction((String) removeAction.get(null));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		getService().onStartCommand(removeIntent, 0, 0);
	}
	
	/* ----- Actual tests ----- */
	
	/** Simplest case: start a survey and then finish it. */
	public void testStartEnd()
	{
		startWithDummySurvey();
		startSurvey();
		endSurvey();
	}
	
	/** Start a survey then have it time out. */
	public void testStartQuit()
	{
		startWithDummySurvey();
		startSurvey();
		quitSurvey();
	}
	
	/**
	 * Start a survey, have another get added while the first is running, then
	 * finish the first, then finish the second.
	 */
	public void testStart2()
	{
		startWithDummySurvey();
		intendedActivity = QuestionActivity.class;
		started.set(false);
		synchronized (started)
		{
			startSurvey();
			wait(started);
		}
		assertTrue("Activity should be started", started.get());
		addDummySurvey();
		started.set(false);
		endSurvey();
		synchronized (started)
		{
			startSurvey();
			wait(started);
		}
		assertTrue("Activity should be started", started.get());
		endSurvey();
	}
	
	/**
	 * Start a survey, have another get added while the first is running, then
	 * quit the first, then finish the second.
	 */
	public void testStart2WithQuit()
	{
		startWithDummySurvey();
		intendedActivity = QuestionActivity.class;
		started.set(false);
		synchronized (started)
		{
			startSurvey();
			wait(started);
		}
		assertTrue("Activity should be started", started.get());
		addDummySurvey();
		quitSurvey();
		started.set(false);
		synchronized (started)
		{
			startSurvey();
			wait(started);
		}
		assertTrue("Activity should be started", started.get());
		endSurvey();
	}
	
	/** Start a survey and then kill the service. */
	public void testStartAndKill()
	{
		startWithDummySurvey();
		startSurvey();
		//tearDown kills the service
	}
	
	/** Cancel a survey */
	public void testCancel()
	{
		startWithDummySurvey();
		cancelSurvey();
	}
	
	/** Cancel multiple times */
	public void testCancelMultiple()
	{
		startWithDummySurvey();
		addDummySurvey();
		cancelSurvey();
		cancelSurvey();
	}
}
