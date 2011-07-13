/*---------------------------------------------------------------------------*
 * SettingsActivity.java                                                     *
 *                                                                           *
 * Allows the subject to change the application settings: whether or not     *
 * location tracking, call logging, and surveys are enabled.                 *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

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
    
    //little hack here to get this object (see the id button listener)
    private Activity getThis()
    {
    	return this;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Config.D) Log.d(TAG, "Starting settings activity");

        //setting the layout of the activity
        Display display = ((WindowManager)
        		getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        //check what orientation the phone is in
        //getOrientation() is depreciated as of API 8, but we're targeting
        //API 7, so we have to use it
        if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
        {
        	setContentView(R.layout.settings_activity_horiz);
        }
        else
        {
        	setContentView(R.layout.settings_activity_vert);
        }
        
        //checkboxes for each of the three settings
        final ToggleButton locationCheckbox =
        	(ToggleButton) findViewById(R.id.settings_locationSettingToggle);
        locationCheckbox.setChecked(
        		Config.getSetting(this, Config.TRACKING_LOCAL, true));
        locationCheckbox.setOnCheckedChangeListener(
        		new OnCheckedChangeListener()
        {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean check)
			{
				Config.putSetting(getThis(), Config.TRACKING_LOCAL, check);
			}
        });
        
        final ToggleButton callLogCheckbox =
        	(ToggleButton) findViewById(R.id.settings_loggingSettingsToggle);
        callLogCheckbox.setChecked(
        		Config.getSetting(this, Config.CALL_LOG_LOCAL, true));
        callLogCheckbox.setOnCheckedChangeListener(
        		new OnCheckedChangeListener()
        {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean check)
			{
				Config.putSetting(getThis(), Config.CALL_LOG_LOCAL, check);
			}
        });
        
        final ToggleButton surveyCheckbox =
        	(ToggleButton) findViewById(R.id.settings_surveysSettingToggle);
        surveyCheckbox.setChecked(
        		Config.getSetting(this, Config.SURVEYS_LOCAL, true));
        surveyCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean check)
			{
				Config.putSetting(getThis(), Config.SURVEYS_LOCAL, check);
			}
        });
        
        //id button
        Button id = (Button) findViewById(R.id.settings_idButton);
        id.setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View view)
			{
				//show the id activity
				Intent idIntent = new Intent(getThis(), IDActivity.class);
				startActivity(idIntent);
			}
		});
        
        //exit button
        Button back = (Button) findViewById(R.id.settings_backButton);
        back.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	//show the user a summary of the current settings
            	StringBuilder info = new StringBuilder();
            	
            	info.append("Location tracking is " +
            			(Config.getSetting(getThis(),
            					Config.TRACKING_LOCAL, true) ?
            					"enabled\n" : "disabled\n"));
            	
            	info.append("Call logging is " +
            			(Config.getSetting(getThis(),
            					Config.CALL_LOG_LOCAL, true) ?
            					"enabled\n" : "disabled\n"));
            	
            	info.append("Surveys are " +
            			(Config.getSetting(getThis(),
            					Config.SURVEYS_LOCAL, true) ?
            					"enabled" : "disabled"));

            	Toast.makeText(getApplicationContext(), info.toString(),
                        Toast.LENGTH_SHORT).show();
            	
            	finish();
            }
        });
    }
}