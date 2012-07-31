/*---------------------------------------------------------------------------*
 * QuestionActivty.java                                                      *
 *                                                                           *
 * Shows the user a question.                                                *
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
package org.survey_droid.survey_droid.survey;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;

import org.survey_droid.survey_droid.Config;
import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.annotation.Exported;
import org.survey_droid.survey_droid.survey.SurveyInterface;

/**
 * <p>Abstract question activity that should be extended for each of the
 * various types of questions.  Extending classes should make sure to call
 * super methods for all of the activity lifecycle methods if they need to
 * override them (simple question activities will likely not need to override
 * these).</p>
 * 
 *  <p>Subclasses are passed a byte array with whatever data was provided for
 *  that question from {@link SurveyInterface#getQuestionData()}.  Similarly,
 *  subclasses must pass a byte array to {@link #getAnswer()}. Subclasses
 *  are encouraged (but not required) to use {@link ByteArrayInputSteam},
 *  {@link ByteArrayOutputStream}, {@link DataInputStream}, and
 *  {@link DataOutputStream} to aid in parsing byte data.</p>
 * 
 * @author Austin Walker
 * @author Henry Liu
 */
@Exported
public abstract class QuestionActivity extends Activity
{	
	/** Logging tag; feel free to override. */
	protected static final String TAG = "QuestionActivity";
	
	/**
	 * The survey being run.  Note that this <strong>must not be used until
	 * {@link #onSurveyLoaded()}</strong>.
	 */
	protected SurveyInterface survey;
	
	/** Has the next question activity been started? */
	private boolean isDone = false;
	
	/** Should the activity restart? */
	private boolean restart = false;
	
	/** Connection to the SurveyService */
	private ServiceConnection connection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			survey = SurveyInterface.Stub.asInterface(binder);
			if (Config.D)
			{
				Util.d(null, TAG, "service connected");
				if (survey == null) throw new
					RuntimeException("question given null survey connection!");
			}
			QuestionActivity.this.onSurveyLoaded();
			
			//set the title
			try
			{
				setTitle(survey.getSurveyName());
			}
			catch (RemoteException e)
			{
				throw new RuntimeException("Failed to set survey title", e);
			}
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			//remember not to use name; because of the hack in onStop, it could
			//be null
			if (!isDone)
			{
				try
				{
					survey.startTimeout();
				}
				catch (RemoteException e)
				{
					throw new RuntimeException("Failed to start timeout", e);
				}
			}
		}
	};
	
	/**
	 * Handler for a "previous" button.  Extending classes should install
	 * this as the {@link View.OnClickListener} for the back button as well
	 * as activate it whenever the survey should move back to the previous
	 * question.
	 */
    protected final View.OnClickListener prevListener =
    	new View.OnClickListener()
    {
        public void onClick(View view)
        {
        	boolean moved = false;
        	try
			{
				moved = survey.prevQuestion();
			}
			catch (RemoteException e)
			{
				throw new RuntimeException(
					"Failed to move to previous question", e);
			}
        	if (!moved)
        	{ //can't go back from first question
        		Toast.makeText(QuestionActivity.this,
        				"Cannot go back; already on first question",
        				Toast.LENGTH_SHORT).show();
        	}
        	else
        	{
        		isDone = true;
        		//this activity stops itself as the next one is started
        	}
        }
    };
    
    /**
     * Handler for a "next" button.  Extending classes should use this to
     * move the survey to the next question.  This can be accomplished by
     * installing this listener on a button, or by doing something more
     * complicated and then calling the onClick method.
	 */
    protected final View.OnClickListener nextListener =
    	new View.OnClickListener()
    {
        public void onClick(View view)
        {
        	if (isAnswered())
        	{ //question has been answered properly
        		try
        		{
	        		survey.answer(getAnswer());
	        		survey.nextQuestion();
        		}
        		catch (Exception e)
        		{
        			throw new RuntimeException("Failed to answer question", e);
        		}
        		isDone = true;
        	}
        	else
        	{ //no answer has been given
        		Toast.makeText(QuestionActivity.this,
        				QuestionActivity.this.getInvalidAnswerMsg(),
        				Toast.LENGTH_SHORT).show();
        	}
        }
    };
    
    /**
     * By default, this calls {@link #prevListener}.onClick().  This can be
     * overridden by extending classes if they want to do something special,
     * but more than likely it would be good to call this afterwards.
     */
    @Override
	public void onBackPressed()
    {
    	if (survey != null)
    	{
    		prevListener.onClick(null);
    	}
    }
    
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
	protected void onStop()
	{
		/*
		 * Always kill this activity when it's stopped.  However, if it isn't
		 * done (ie the user hasn't answered yet), tell the service about it.
		 */
		super.onStop();
		if (!isDone)
		{
			connection.onServiceDisconnected(null); //TODO this is kind of a hack...
		}
		finish();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (restart)
		{
			Intent restartIntent = new Intent(QuestionActivity.this,
					QuestionActivity.this.getClass());
			startActivity(restartIntent);
		}
		unbindService(connection);
	}
	
	/**
	 * Determines if the current question has been meaningfully answered.
	 * 
	 * @return true if it is
	 */
	protected abstract boolean isAnswered();

	/** 
	 * @return the data this question is answered with.  This is guaranteed to
	 * only be called after after {@link #isAnswered()} returns true.
	 */
	protected abstract byte[] getAnswer();
	
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
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		//TODO implement this to deal with screen rotations
		super.onConfigurationChanged(newConfig);
		restart = true;
		finish();
	}
}
