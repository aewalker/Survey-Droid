/*---------------------------------------------------------------------------*
 * SettingsActivity.java                                                     *
 *                                                                           *
 * Allows the subject to change the application settings: whether or not     *
 * location tracking, call logging, and surveys are enabled.                 *
 *---------------------------------------------------------------------------*
 * Copyright 2011 Sema Berkiten, Vladimir Costescu, Henry Liu, Diego Vargas, *
 * Austin Walker, and Tony Xiao                                              *
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
package org.surveydroid.android;

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

import org.surveydroid.android.coms.ComsService;
import org.surveydroid.android.database.SurveyDroidDB;
import org.surveydroid.android.database.StatusDBHandler;

import com.commonsware.cwac.wakeful.WakefulIntentService;

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
        
        // sync now
        if (Config.D)
        {
	        Button syncButton = (Button) findViewById(R.id.settings_syncButton);
	        syncButton.setOnClickListener(new View.OnClickListener()
	        {
				@Override
				public void onClick(View v)
				{
					Intent syncIntent = new Intent(SettingsActivity.this, ComsService.class);
					syncIntent.setAction(ComsService.ACTION_DOWNLOAD_DATA);
					WakefulIntentService.sendWakefulWork(SettingsActivity.this, syncIntent);
				}
			});
        }
        else
        {
        	findViewById(R.id.settings_syncButton).setVisibility(View.GONE);
        }
        
        //exit button
        Button back = (Button) findViewById(R.id.settings_backButton);
        back.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	onBackPressed();
            }
        });
    }
    
    
    @Override
    public void onBackPressed()
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
    
    @Override
    protected void onStop()
    {
    	super.onStop();
    	
    	//check to see if any of the settings have been changed
    	//if they have, update the database
    	if (surveyChanged || locationChanged || calllogChanged)
    	{
    		StatusDBHandler sdbh = new StatusDBHandler(this);
            sdbh.open();
            
            if (surveyChanged)
        	{
            	boolean enabled =
            		Config.getSetting(this, Config.SURVEYS_LOCAL, true);
        		sdbh.statusChanged(SurveyDroidDB.StatusTable.SURVEYS,
        				enabled, Util.currentTimeAdjusted() / 1000);
        		surveyChanged = false;
        	}
        	
        	if (locationChanged)
        	{
        		boolean enabled =
            		Config.getSetting(this, Config.TRACKING_LOCAL, true);
        		sdbh.statusChanged(SurveyDroidDB.StatusTable.LOCATION_TRACKING,
        				enabled, Util.currentTimeAdjusted() / 1000);
        		locationChanged = false;
        	}
        	
        	if (calllogChanged)
        	{
        		boolean enabled =
            		Config.getSetting(this, Config.CALL_LOG_LOCAL, true);
        		sdbh.statusChanged(SurveyDroidDB.StatusTable.CALL_LOGGING,
        				enabled, Util.currentTimeAdjusted() / 1000);
        		calllogChanged = false;
        	}
        	
        	sdbh.close();
        	
        	Intent comsIntent = new Intent(this, ComsService.class);
        	comsIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
        	comsIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
        			ComsService.STATUS_DATA);
        	WakefulIntentService.sendWakefulWork(this, comsIntent);
    	}
    }
}