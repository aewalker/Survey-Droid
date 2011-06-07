/*---------------------------------------------------------------------------*
 * MainActivity.java                                                         *
 *                                                                           *
 * User control panel with buttons to adjust settings, show a sample survey, *
 * get the phone's id, and exit.                                             *
 *---------------------------------------------------------------------------*/
//TODO add a button to call Naomi
//TODO add an indication of what percentage of surveys have been completed
package org.peoples.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.peoples.android.survey.SurveyService;

/**
 * The Activity for the administration panel of the PEOPLES application.
 * 
 * @author Henry Liu
 * @author Austin Walker
 */
public class MainActivity extends Activity
{
	// Debugging
    private static final String TAG = "MainActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Config.D) Log.d(TAG, "starting mainActivity");
        
        //FIXME TESTING ONLY!
        //BootIntentReceiver bir = new BootIntentReceiver();
        //bir.startup(getApplicationContext());
        
        //setting the layout of the activity
        setContentView(R.layout.main);

        final TextView q = (TextView) this.findViewById(R.id.title);
        q.setText("Welcome to PEOPLES!");
        
        //sample survey button
        Button sample = (Button) findViewById(R.id.sample);
        sample.setText("Click here to take a sample survey");
        sample.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent surveyIntent = new Intent(view.getContext(),
                		SurveyService.class);
                surveyIntent.setAction(SurveyService.ACTION_SURVEY_READY);
                surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_ID,
                		SurveyService.DUMMY_SURVEY_ID);
                startService(surveyIntent);
            }
        });
        
        //go to settings button
        Button settings = (Button) findViewById(R.id.settings);
        settings.setText("Settings");
        settings.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent settingsIntent = new Intent(view.getContext(),
                		SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
        
        //show id button
        Button phoneID = (Button) findViewById(R.id.uniqueID);
        phoneID.setText("Find this phone's unique ID");
        phoneID.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	Intent showIDIntent = new Intent(view.getContext(),
            			IDActivity.class);
            	startActivity(showIDIntent);
            }
        });
        
        //exit button
        Button quit = (Button) findViewById(R.id.quit);
        quit.setText("Exit PEOPLES");
        quit.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	finish();
            }
        }); 
    }
}