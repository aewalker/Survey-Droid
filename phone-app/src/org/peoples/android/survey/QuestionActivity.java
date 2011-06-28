/*---------------------------------------------------------------------------*
 * QuestionActivty.java                                                      *
 *                                                                           *
 * Shows the user a question.                                                *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.peoples.android.Config; 
import org.peoples.android.database.PeoplesDB;
import org.peoples.android.survey.SurveyService.SurveyBinder;

/**
 * Abstract question activity that should be extended for each of the various
 * types of questions.
 * 
 * @author Austin Walker
 * @author Henry Liu
 */
public abstract class QuestionActivity extends ListActivity
{	
	//logging tag
	protected static final String TAG = "QuestionActivity";
	
	//the survey being ran
	protected Survey survey;
	
	//has the next question activity been started?
	private boolean isDone = false;
	
	//connection to the SurveyService
	private ServiceConnection connection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			SurveyBinder sBinder = (SurveyBinder) binder;
			survey = sBinder.getSurvey();
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {}
	};
	
	//little hack to get the outer object
	private QuestionActivity getThis()
	{
		return this;
	}
	
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
        		Intent prevIntent = new Intent(getThis(),
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
        	if (getThis().isAnswered())
        	{ //question has been answered properly
        		getThis().answer();
        		survey.nextQuestion();
        		if (!survey.done())
        		{ //still have more questions
        			Intent nextIntent = new Intent(getThis(),
        					getNextQusetionClass(survey.getQuestionType()));
        			startActivity(nextIntent);
        		}
        		else
        		{ //survey is over
        			Intent submitIntent = new Intent(getThis(),
        					ConfirmSubmitActivity.class);
        			startActivity(submitIntent);
        		}
        		isDone = true;
        	}
        	else
        	{ //no answer has been given
        		Toast.makeText(getApplicationContext(),
        				getThis().getInvalidAnswerMsg(),
        				Toast.LENGTH_SHORT).show();
        	}
        }
    };
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		if (Config.D) Log.d(TAG, "Creating QuestionActivity");
		
		//get the survey
		Intent bindIntent = new Intent(this, SurveyService.class);
		bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop()
	{
		/*
		 * When the activity is stopped (that is, when it is no longer
		 * visible), kill it, but only if the next question has been
		 * started.  This makes the app run more smoothly; the user doesn't
		 * see black screens in between questions.
		 */
		super.onStop();
		if (isDone) finish();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
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
