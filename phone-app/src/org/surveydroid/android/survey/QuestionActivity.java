/*---------------------------------------------------------------------------*
 * QuestionActivty.java                                                      *
 *                                                                           *
 * Shows the user a question.                                                *
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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import org.surveydroid.android.Config;
import org.surveydroid.android.Util;
import org.surveydroid.android.database.SurveyDroidDB;
import org.surveydroid.android.survey.SurveyService.SurveyBinder;

/**
 * Abstract question activity that should be extended for each of the various
 * types of questions.
 * 
 * @author Austin Walker
 * @author Henry Liu
 */
public abstract class QuestionActivity extends Activity
{	
	/** Logging tag */
	protected static final String TAG = "QuestionActivity";
	
	/**
	 * The survey being ran.  Note that this should not be used until
	 * {@link #onSurveyLoaded()}.
	 */
	protected Survey survey;
	
	//has the next question activity been started?
	private boolean isDone = false;
	
	//handle to deal with timeouts
	private final Handler timeoutHandler = new Handler();
	
	//runnable that times out the survey
	private final Runnable timeout = new Runnable()
	{
		@Override
		public void run()
		{
			Util.i(QuestionActivity.this, TAG, "Survey timed out");
			Intent timeoutIntent = new Intent(QuestionActivity.this,
					SurveyService.class);
			timeoutIntent.setAction(SurveyService.ACTION_QUIT_SURVEY);
			startService(timeoutIntent);
			finish();
		}
	};
	
	//connection to the SurveyService
	private ServiceConnection connection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			SurveyBinder sBinder = (SurveyBinder) binder;
			survey = sBinder.getSurvey();
			if (Config.D)
			{
				Util.d(null, TAG, "service connected");
				if (survey == null) throw new
					RuntimeException("question given null survey");
			}
			QuestionActivity.this.onSurveyLoaded();
			
			//set the title if desired
			if (Config.getSetting(QuestionActivity.this,
					Config.SHOW_SURVEY_NAME,
					Config.SHOW_SURVEY_NAME_DEFAULT))
				setTitle(survey.getName());
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {}
	};
	
	/**
	 * Handler for the "previous" button.  Extending classes should install
	 * this as the {@link View.OnClickListener} for the back button.
	 */
    protected final View.OnClickListener prevListener =
    	new View.OnClickListener()
    {
        public void onClick(View view)
        {
        	if (survey.isOnFirst())
        	{ //can't go back from first question
        		Toast.makeText(getApplicationContext(),
        				"Cannot go back; already on first question",
        				Toast.LENGTH_SHORT).show();
        	}
        	else
        	{ //start the next activity
        		survey.prevQuestion();
        		Intent prevIntent = new Intent(QuestionActivity.this,
        				getNextQusetionClass(survey.getQuestionType()));
        		startActivity(prevIntent);
        		isDone = true;
        	}
        }
    };
    
    /**
     * Handler for the "next" button.  Extending classes should install
     * this as the {@link View.OnClickListener} for the next button.
	 */
    protected final View.OnClickListener nextListener =
    	new View.OnClickListener()
    {
        public void onClick(View view)
        {
        	if (isAnswered())
        	{ //question has been answered properly
        		answer();
        		survey.nextQuestion();
        		if (!survey.done())
        		{ //still have more questions
        			Intent nextIntent = new Intent(QuestionActivity.this,
        					getNextQusetionClass(survey.getQuestionType()));
        			startActivity(nextIntent);
        		}
        		else
        		{ //survey is over
        			Intent submitIntent = new Intent(QuestionActivity.this,
        					ConfirmSubmitActivity.class);
        			startActivity(submitIntent);
        		}
        		isDone = true;
        	}
        	else
        	{ //no answer has been given
        		Toast.makeText(getApplicationContext(),
        				QuestionActivity.this.getInvalidAnswerMsg(),
        				Toast.LENGTH_SHORT).show();
        	}
        }
    };
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		Util.d(null, TAG, "Creating QuestionActivity");
		
		//get the survey
		Intent bindIntent = new Intent(this, SurveyService.class);
		bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		Util.d(null, TAG, "in onStart()");
		
		//remove the timeout if it exists
		timeoutHandler.removeCallbacks(timeout);
	}
	
	@Override
	protected void onStop()
	{
		/*
		 * When the activity is stopped (that is, when it is no longer
		 * visible), kill it, but only if the next question has been
		 * started.  This makes the app run more smoothly; the user doesn't
		 * see black screens in between questions.
		 * 
		 * On the other hand, if the next question hasn't been started, start
		 * the timeout that will kill the survey if the user doesn't come back
		 * to this after a certain amount of time.
		 */
		super.onStop();
		if (isDone) finish();
		else
		{
			int delay = Config.getSetting(this, Config.QUESTION_TIMEOUT,
					Config.QUESTION_TIMEOUT_DEFAULT);
			delay *= 60 * 1000;
			timeoutHandler.postDelayed(timeout, delay);
		}
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		timeoutHandler.removeCallbacks(timeout);
		unbindService(connection);
	}
	
	/**
	 * Determines if the current question has been meaningfully answered.
	 * 
	 * @return true if it is
	 */
	protected abstract boolean isAnswered();
	
	/**
	 * Answer the current question with the appropriate answer call to the
	 * survey.  Note that you should <strong>NOT</strong> call
	 * {@link Survey#nextQuestion()}.
	 */
	protected abstract void answer();
	
	/**
	 * Should return a string that is to be displayed to the user as an
	 * explanation of why they cannot move to the next question.  Called if
	 * the next button is pressed and {@link #isAnswered()} returns false.
	 * 
	 * @return the message to show to the user
	 */
	protected abstract String getInvalidAnswerMsg();
	
	/**
	 * Called once the survey has been loaded (usually after
	 * {@link QuestionActivity#onCreate(Bundle)}).
	 */
	protected abstract void onSurveyLoaded();
	
	/**
	 * Gets the proper class for a question of a given type.  Useful when
	 * creating {@link Intent}s to start question activities.
	 * 
	 * @param type - the type as seen in {@link SurveyDroidDB#QuestionTable}
	 * 
	 * @return the class type corresponding to that type
	 */
	public static Class<? extends QuestionActivity>
		getNextQusetionClass(int type)
	{
		switch (type)
		{
		case SurveyDroidDB.QuestionTable.SINGLE_CHOICE:
			return SingleChoiceActivity.class;
		case SurveyDroidDB.QuestionTable.MULTI_CHOICE:
			return MultiChoiceActivity.class;
		case SurveyDroidDB.QuestionTable.SCALE_TEXT:
			return TextScaleActivity.class;
		case SurveyDroidDB.QuestionTable.SCALE_IMG:
			return ImgScaleActivity.class;
		case SurveyDroidDB.QuestionTable.FREE_RESPONSE:
			return FreeResponseActivity.class;
		default:
			throw new RuntimeException("Unknown question type: " + type);
		}
	}
}
