/*---------------------------------------------------------------------------*
 * SurveyService.java                                                        *
 *                                                                           *
 * Runs while the user it taking a survey; holds the survey object.  This    *
 * allows the user to rotate the screen without having to rebuild the whole  *
 * survey.                                                                   *
 *---------------------------------------------------------------------------*/
//TODO Another way to set up this whole service is as bound service that is
//initiated by the first question activity and called upon to provide the
//survey object's methods when needed.  This would avoid having to start a
//separate activity for each question.
package org.peoples.android.survey;

import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.peoples.android.Config;
import org.peoples.android.coms.ComsService;

/**
 * Runs while a survey is being administered to the user.  "Spawns" instances
 * of {@link QuestionActivity} to show individual questions.
 * 
 * @author Austin Walker
 */
public class SurveyService extends Service
{
	//intent actions
	/**
	 * A survey is ready but has not been accepted by the user.  Intent must
	 * include a survey id in {@link EXTRA_SURVEY_ID}.
	 */
	public static final String ACTION_SURVEY_READY =
		"org.peoples.android.survey.ACTION_SURVEY_READY";
	
	/**
	 * A survey is ready and has been accepted by the user.  Should only be
	 * sent after {@link ACTION_SURVEY_READY}, otherwise the survey will not
	 * be built and an exception will be thrown.
	 */
	public static final String ACTION_SHOW_SURVEY =
		"org.peoples.android.survey.ACTION_SHOW_SURVEY";
	
	/**
	 * Move the survey to the next question and display it.  Should only be
	 * sent after {@link ACTION_SURVEY_READY}, otherwise the survey will not
	 * be built and an exception will be thrown
	 */
	public static final String ACTION_NEXT_QUESTION =
		"org.peoples.android.survey.ACTION_NEXT_QUESTION";
	
	/**
	 * Move the survey to the prvious question and display it.  Should only be
	 * sent after {@link ACTION_SURVEY_READY}, otherwise the survey will not
	 * be built and an exception will be thrown.
	 */
	public static final String ACTION_PREV_QUESTION =
		"org.peoples.android.survey.ACTION_PREV_QUESTION";
	
	/**
	 * Submit all live answers for this survey and stop. Should only be
	 * sent after {@link ACTION_SURVEY_READY}, otherwise the survey will not
	 * be built and an exception will be thrown.
	 */
	public static final String ACTION_SUBMIT_ANSWERS = 
		"org.peoples.android.survey.ACTION_SUBMIT_ANSWERS";
	
	/**
	 * Stop the survey service.  Used when the user has declined to take a
	 * survey.
	 */
	public static final String ACTION_STOP_SURVEY =
		"org.peoples.android.survey.ACTION_STOP_SURVEY";
	
	//key values for extras
	/** The id of the survey this service is starting for. */
	public static final String EXTRA_SURVEY_ID =
		"org.peoples.android.survey.EXTRA_SURVEY_ID";
	
	/**
	 * Contains the text of the current answer if the current question is free
	 * response. Should not be set if the question is multiple choice.
	 */
	public static final String EXTRA_ANS_TEXT =
		"org.peoples.android.survey.EXTRA_ANS_TEXT";
	
	/**
	 * Contains the index of the current answer if the current qustion is
	 * multiple choice.  Should not be set if the question is free response.
	 */
	public static final String EXTRA_ANS_INDEX =
		"org.peoples.android.survey.EXTRA_ANS_INDEX";
	
	/**
	 * Given this id, a dummy survey will be used and the answers will not be
	 * recorded.
	 */
	public static final int DUMMY_SURVEY_ID = 0;
	
	//the survey instance that each instance of this service uses
	private Survey survey;
	
	//logging tag
	private static final String TAG = "SurveyService";
	
	@Override
	public synchronized int onStartCommand(Intent intent, int flags, int startid)
	{
		/*
		 * Why synchronized?  Well there's a big issue that I (Austin) found
		 * with this service.  If the time is changed on the phone (which, at
		 * least on the phone I'm using, happens every time it turns on), it
		 * can cause more than one survey to be started at one time because
		 * alarms that are set for a time in the past are delivered at once.
		 * The combination of making this synchronized and doing the check to
		 * see if survey is not null when receiving an ACTION_SURVEY_READY
		 * should ensure that only one survey runs at a time.
		 */
		
		//filter the intent action
		String action = intent.getAction();
		if (action.equals(ACTION_SURVEY_READY))
		{
			if (survey != null)
			{
				//another survey is already running, so delay the new one
				Intent delayIntent =
					new Intent(getApplicationContext(), SurveyScheduler.class);
				delayIntent.setAction(SurveyScheduler.ACTION_ADD_SURVEY);
				delayIntent.putExtra(SurveyScheduler.EXTRA_SURVEY_ID,
						intent.getIntExtra(EXTRA_SURVEY_ID, DUMMY_SURVEY_ID));
				delayIntent.putExtra(SurveyScheduler.EXTRA_SURVEY_TIME,
						Calendar.getInstance().getTimeInMillis()
						+ (Config.SURVEY_DELAY * 60 * 1000));
				startService(delayIntent);
				return START_STICKY;
			}
			
			//check that surveys are enabled
			Config cfg = new Config(this);
			if (!cfg.isSurveyEnabled())
			{
				//if the user is already taking a survey, then we don't want
				//to stop it, even if s/he disables surveys during it
				if (survey == null)
				{
					stopSelf();
					return START_NOT_STICKY;
				}
				return START_STICKY;
			}
			
			/*
			 * Build the survey here, before anything is shown to the user.
			 * The assumption is that most of the time, the user will take the
			 * survey when first asked to.  Because building a survey can be
			 * time consuming, build it here to provide the user with a snappy
			 * UI.
			 */
			int surveyID =
				intent.getIntExtra(EXTRA_SURVEY_ID, DUMMY_SURVEY_ID);
			Log.i(TAG, "Starting survey service for survey " + surveyID);
			if (surveyID == DUMMY_SURVEY_ID)
				survey = new Survey(getApplicationContext());
			else survey = new Survey(surveyID, getApplicationContext());
			
			Intent notificationIntent =
				new Intent(getApplicationContext(), NotificationActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(notificationIntent);
		}
		else if (action.equals(ACTION_STOP_SURVEY))
		{
			stopSelf();
		}
		else
		{
			if (survey == null)
			{ //if we get to here, then the service has been asked to do
			  //something before it's survey was initialized
				throw new RuntimeException("Survey uninitialized");
			}
			if (action.equals(ACTION_SHOW_SURVEY))
			{
				//survey is just being started
				if (Config.D) Log.d(TAG, "Starting survey");
				
				//spawn the first QuestionActivity
				startQuestionActivity();
			}
			else if (action.equals(ACTION_NEXT_QUESTION))
			{ //subject has entered a response and is moving on
				int ansIndex = intent.getIntExtra(EXTRA_ANS_INDEX, -1);
				if (ansIndex != -1)
				{ //multiple choice answer given
					survey.answer(survey.getChoices()[ansIndex]);
					if (Config.D)
						Log.d(TAG, "Question answered with " + ansIndex);
					survey.nextQuestion();
				}
				else
				{ //free response answer given
					String ansText = intent.getStringExtra(EXTRA_ANS_TEXT);
					if (ansText == null)
					{ //the intent did not contain answer information!
						throw new RuntimeException("No answer provided");
					}
					else
					{
						survey.answer(ansText);
						if (Config.D) Log.d(TAG,
								"Question answered with \"" + ansText + "\"");
						survey.nextQuestion();
					}
				}
				
				if (!survey.done())
				{ //spawn the next QuestionActivity
					startQuestionActivity();
				}
				else
				{ //ask the subject to confirm submission of answers
					Intent confirmIntent = new Intent(getApplicationContext(),
							ConfirmSubmitActivity.class);
					confirmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(confirmIntent);
				}
			}
			else if (action.equals(ACTION_PREV_QUESTION))
			{ //subject has requested to move back a question
				survey.prevQuestion();
				startQuestionActivity();
			}
			else if (action.endsWith(ACTION_SUBMIT_ANSWERS))
			{ //subject has approved submission of answers
				if (!survey.submit()) Log.e(TAG, "Survey submission error");
				else if (Config.D) Log.d(TAG, "Answers submitted");
				
				//try to upload the new answers ASAP
				Intent comsIntent =
					new Intent(getApplicationContext(), ComsService.class);
				comsIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
				comsIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
						ComsService.SURVEY_DATA);
				startService(comsIntent);
				
				stopSelf();
			}
			else
			{
				//unknown action requested
				
				//When debugging, crash the program in order to make it obvious
				//that something is up.  Otherwise, log a warning, but don't
				//crash anything.
				Log.w(TAG, "Unkown intent action: " + action);
				if (Config.D)
				{
					throw new RuntimeException("Unknown intent action: "
							+ action);
				}
			}
		}
		return START_STICKY;
	}
	
	//start a QuestionActivity for the current question in the survey
	private void startQuestionActivity()
	{
		Intent questionIntent =
			new Intent(getApplicationContext(), QuestionActivity.class);
		questionIntent.putExtra(
				QuestionActivity.QUESTION_TEXT, survey.getText());
		questionIntent.putExtra(QuestionActivity.QUESTION_CHOICES,
				survey.getChoiceTexts());
		questionIntent.putExtra(QuestionActivity.IS_FIRST_QUESTION,
				survey.isOnFirst());
		int ans = survey.getAnswerChoice();
		if (ans == -1)
		{
			questionIntent.putExtra(EXTRA_ANS_TEXT, survey.getAnswerText());
		}
		else
		{
			questionIntent.putExtra(EXTRA_ANS_INDEX, ans);
		}
		questionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(questionIntent);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
}
