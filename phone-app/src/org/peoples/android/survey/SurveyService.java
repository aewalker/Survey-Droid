/*---------------------------------------------------------------------------*
 * SurveyService.java                                                        *
 *                                                                           *
 * Runs while the user it taking a survey; holds the survey object.  This    *
 * allows the user to rotate the screen without having to rebuild the whole  *
 * survey.                                                                   *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
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
	//some intent actions to be passed to this service
	public static final String ACTION_SURVEY_READY =
		"org.peoples.android.survey.ACTION_SURVEY_READY";
	public static final String ACTION_SHOW_SURVEY =
		"org.peoples.android.survey.ACTION_SHOW_SURVEY";
	public static final String ACTION_NEXT_QUESTION =
		"org.peoples.android.survey.ACTION_NEXT_QUESTION";
	public static final String ACTION_PREV_QUESTION =
		"org.peoples.android.survey.ACTION_PREV_QUESTION";
	public static final String ACTION_SUBMIT_ANSWERS = 
		"org.peoples.android.survey.ACTION_SUBMIT_ANSWERS";
	
	//key values for extras
	public static final String EXTRA_SURVEY_ID =
		"org.peoples.android.survey.EXTRA_SURVEY_ID";
	public static final String EXTRA_ANS_TEXT =
		"org.peoples.android.survey.EXTRA_ANS_TEXT";
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
	public int onStartCommand(Intent intent, int flags, int startid)
	{
		//filter the intent action
		String action = intent.getAction();
		if (action.equals(ACTION_SURVEY_READY))
		{
			//wake up the phone if it's asleep
			PowerManager pm =
				(PowerManager) getSystemService(Context.POWER_SERVICE);
			WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
					| PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.ON_AFTER_RELEASE, TAG);
			wl.acquire();
			
			Intent notificationIntent =
				new Intent(getApplicationContext(), NotificationActivity.class);
			notificationIntent.putExtra(EXTRA_SURVEY_ID,
					intent.getIntExtra(EXTRA_SURVEY_ID, DUMMY_SURVEY_ID));
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(notificationIntent);
			wl.release();
		}
		else if (action.equals(ACTION_SHOW_SURVEY))
		{ //survey is just being started
			Log.i(TAG, "Starting survey service");
			int surveyID =
				intent.getIntExtra(EXTRA_SURVEY_ID, DUMMY_SURVEY_ID);
			if (Config.D) Log.d(TAG, "Starting survey number " + surveyID);
			if (surveyID == DUMMY_SURVEY_ID)
				survey = new Survey(getApplicationContext());
			else survey = new Survey(surveyID, getApplicationContext());
			
			//spawn the first QuestionActivity
			startQuestionActivity();
		}
		else
		{
			if (survey == null)
			{ //if we get to here, then the service has been asked to do
			  //something before it's survey was initialized
				throw new RuntimeException("Survey uninitialized");
			}
			if (action.equals(ACTION_NEXT_QUESTION))
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
			{ //unknown action requested
				Log.e(TAG, action);
				//TODO should other intents just be ignored, or should they
				//generate an exception?
				throw new RuntimeException("Unknown intent action");
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
