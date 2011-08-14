/*---------------------------------------------------------------------------*
 * QuestionActivty.java                                                      *
 *                                                                           *
 * Shows the user a question.                                                *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

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

import org.peoples.android.Config; 
import org.peoples.android.Util;
import org.peoples.android.database.PeoplesDB;
import org.peoples.android.survey.SurveyService.SurveyBinder;

/**
 * Abstract question activity that should be extended for each of the various
 * types of questions.
 * 
 * @author Austin Walker
 * @author Henry Liu
 */
public abstract class QuestionActivity extends Activity
{	
	//logging tag
	protected static final String TAG = "QuestionActivity";
	
	/**
	 * The survey being ran.  Note that this should not be used until
	 * onSurveyLoaded().
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
	 * this as the onClickListener for the back button.
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
     * this as the onClickListener for the next button.
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
	 * Answer the current question with the appropriate answer() call to
	 * survey.  Note that you should NOT call survey.nextQuestion().
	 */
	protected abstract void answer();
	
	/**
	 * Should return a string that is to be displayed to the user as an
	 * explanation of why they cannot move to the next question.  Called if
	 * the next button is pressed and isAnswered() returns false.
	 * 
	 * @return the message to show to the user
	 */
	protected abstract String getInvalidAnswerMsg();
	
	/**
	 * Called once the survey has been loaded (usually after onCreate).
	 */
	protected abstract void onSurveyLoaded();
	
	/**
	 * Gets the proper class for a question of a given type.  Useful when
	 * creating intents to start question activities.
	 * 
	 * @param type - the type as seen in {@link PeoplesDB.QuestionTable}
	 * 
	 * @return the class type corresponding to that type
	 */
	public static Class<? extends QuestionActivity>
		getNextQusetionClass(int type)
	{
		switch (type)
		{
		case PeoplesDB.QuestionTable.SINGLE_CHOICE:
			return SingleChoiceActivity.class;
		case PeoplesDB.QuestionTable.MULTI_CHOICE:
			return MultiChoiceActivity.class;
		case PeoplesDB.QuestionTable.SCALE_TEXT:
			return TextScaleActivity.class;
		case PeoplesDB.QuestionTable.SCALE_IMG:
			return ImgScaleActivity.class;
		case PeoplesDB.QuestionTable.FREE_RESPONSE:
			return FreeResponseActivity.class;
		default:
			throw new RuntimeException("Unknown question type: " + type);
		}
	}
}
