/*---------------------------------------------------------------------------*
 * SettingsActivity.java                                                     *
 *                                                                           *
 * Allows the subject to change the application settings: whether or not     *
 * location tracking, call logging, and surveys are enabled.                 *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.peoples.android.R;

/**
 * The Activity that triggers toggling the different settings on and off.
 * 
 * @author Henry Liu
 * @author Austin Walker
 */
public class SettingsActivity extends Activity
{
	//logging tag
    private static final String TAG = "SettingsActivity";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final Context ctxt = this;

        if (Config.D) Log.d(TAG, "Starting settings activity");

        //setting the layout of the activity
        setContentView(R.layout.settings);

        final Config settings = new Config(ctxt);
        
        //checkboxes for each of the three settings
        final ToggleButton locationCheckbox =
        	(ToggleButton) findViewById(R.id.toggleButton1);
        locationCheckbox.setChecked(settings.isLocationEnabled());
        
        final ToggleButton callLogCheckbox =
        	(ToggleButton) findViewById(R.id.toggleButton2);
        callLogCheckbox.setChecked(settings.isCallLogEnabled());
        
        final ToggleButton surveyCheckbox =
        	(ToggleButton) findViewById(R.id.toggleButton3);
        surveyCheckbox.setChecked(settings.isSurveyEnabled());
        
        //save button
        Button save = (Button) findViewById(R.id.save);
        save.setText("Save Changes");
        save.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	StringBuilder info = new StringBuilder();
            	
            	settings.setLocationService(locationCheckbox.isChecked());
            	info.append("Location tracking is " +
            			(locationCheckbox.isChecked() ?
            					"enabled\n" : "disabled\n"));

            	settings.setCallLogService(callLogCheckbox.isChecked());
            	info.append("Call logging is " +
            			(callLogCheckbox.isChecked() ?
            					"enabled\n" : "disabled\n"));

            	settings.setSurveyService(surveyCheckbox.isChecked());
            	info.append("Surveys are " +
            			(surveyCheckbox.isChecked() ? "enabled" : "disabled"));

            	Toast.makeText(getApplicationContext(), info.toString(),
                        Toast.LENGTH_SHORT).show();
            	finish();
            }
        });
        
        //exit button
        Button back = (Button) findViewById(R.id.back);
        back.setText("Return to Main Menu");
        back.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	finish();
            }
        });
    }
}