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
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import org.peoples.android.R;
import org.peoples.android.coms.ComsService;
import org.peoples.android.database.PeoplesDB;
import org.peoples.android.database.StatusDBHandler;

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
    
    //track whether or not each of the settings has been changed
    //then, when the activity is stopped, notify the database
    private boolean surveyChanged = false;
    private boolean locationChanged = false;
    private boolean calllogChanged = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Util.d(null, TAG, "Starting settings activity");

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
				Config.putSetting(SettingsActivity.this,
						Config.TRACKING_LOCAL, check);
				locationChanged = !locationChanged;
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
				Config.putSetting(SettingsActivity.this,
						Config.CALL_LOG_LOCAL, check);
				calllogChanged = !calllogChanged;
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
				Config.putSetting(SettingsActivity.this,
						Config.SURVEYS_LOCAL, check);
				surveyChanged = !surveyChanged;
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
				Intent idIntent = new Intent(SettingsActivity.this,
						IDActivity.class);
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
            			(Config.getSetting(SettingsActivity.this,
            					Config.TRACKING_LOCAL, true) ?
            					"enabled\n" : "disabled\n"));
            	
            	info.append("Call logging is " +
            			(Config.getSetting(SettingsActivity.this,
            					Config.CALL_LOG_LOCAL, true) ?
            					"enabled\n" : "disabled\n"));
            	
            	info.append("Surveys are " +
            			(Config.getSetting(SettingsActivity.this,
            					Config.SURVEYS_LOCAL, true) ?
            					"enabled" : "disabled"));

            	Toast.makeText(getApplicationContext(), info.toString(),
                        Toast.LENGTH_SHORT).show();
            	
            	finish();
            }
        });
    }
    
    @Override
    protected void onStop()
    {
    	super.onStop();
    	//check to see if any of the settings have been changed
    	//if they have, update the database
    	if (surveyChanged || locationChanged || calllogChanged)
    	{
    		StatusDBHandler sdbh = new StatusDBHandler(this);
            sdbh.openWrite();
            
            if (surveyChanged)
        	{
            	boolean enabled =
            		Config.getSetting(this, Config.SURVEYS_LOCAL, false);
        		sdbh.statusChanged(PeoplesDB.StatusTable.SURVEYS,
        				enabled, System.currentTimeMillis());
        		surveyChanged = false;
        	}
        	
        	if (locationChanged)
        	{
        		boolean enabled =
            		Config.getSetting(this, Config.TRACKING_LOCAL, false);
        		sdbh.statusChanged(PeoplesDB.StatusTable.LOCATION_TRACKING,
        				enabled, System.currentTimeMillis());
        		locationChanged = false;
        	}
        	
        	if (calllogChanged)
        	{
        		boolean enabled =
            		Config.getSetting(this, Config.CALL_LOG_LOCAL, false);
        		sdbh.statusChanged(PeoplesDB.StatusTable.CALL_LOGGING,
        				enabled, System.currentTimeMillis());
        		calllogChanged = false;
        	}
        	
        	sdbh.close();
        	
        	Intent comsIntent = new Intent(this, ComsService.class);
        	comsIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
        	comsIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
        			ComsService.STATUS_DATA);
        	startService(comsIntent);
    	}
    }
}