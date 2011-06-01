/*---------------------------------------------------------------------------*
 * MainActivity.java                                                         *
 *                                                                           *
 * User control panel for the application; lets subjects enable/disable      *
 * parts of the app, contact Naomi, see their response percentage, and take  *
 * a sample survey.                                                          *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.peoples.android.Config;
import org.peoples.android.survey.SurveyService;

/**
 * The main activity for PEOPLES subjects; it acts as a control panel to
 * enable/disable parts of the app and more.
 * 
 * @author Austin Walker
 */
public class MainActivity extends Activity
{
	private static final String TAG = "MainActivity";
	
	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		if (Config.D) Log.d(TAG, "Starting main activity");
		
		//TODO for testing; just show the sample survey
    	Intent surveyIntent =
    		new Intent(getApplicationContext(),SurveyService.class);
    	surveyIntent.setAction(SurveyService.ACTION_SHOW_SURVEY);
        startService(surveyIntent);
        finish();
	}
}
