/*---------------------------------------------------------------------------*
 * MainActivity.java                                                         *
 *                                                                           *
 * User control panel with buttons to adjust settings, show a sample survey, *
 * get the phone's id, and exit.                                             *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.peoples.android.database.TakenDBHandler;

/**
 * The Activity for the administration panel of the PEOPLES application.
 * 
 * @author Henry Liu
 * @author Austin Walker
 */
public class MainActivity extends Activity
{
	//logging tag
    private static final String TAG = "MainActivity";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Util.d(null, TAG, "starting mainActivity");
        
        //setting the layout of the activity
        Display display = ((WindowManager)
        		getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        //check what orientation the phone is in
        //getOrientation() is depreciated as of API 8, but we're targeting
        //API 7, so we have to use it
        if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
        { //yeah this makes no sense, but it works...
        	setContentView(R.layout.main_activity_horiz);
        }
        else
        {
        	setContentView(R.layout.main_activity_vert);
        }
        
        //add the survey progress bar
        TakenDBHandler tdbh = new TakenDBHandler(this);
        tdbh.openRead();
        int p = tdbh.getCompletionRate();
        tdbh.close();
        VerticalProgressBar progress = (VerticalProgressBar)
        	findViewById(R.id.main_progressBar);
        progress.setMax(100);
        int goal = Config.getSetting(this, Config.COMPLETION_GOAL,
        		Config.COMPLETION_GOAL_DEFAULT);
        progress.setSecondaryProgress(goal);
        if (p == TakenDBHandler.NO_PERCENTAGE)
        	//TODO find a way to make the bar indicate this better
        	progress.setProgress(0);
        else
        	progress.setProgress(p);
        
        //go to settings button
        Button settings = (Button) findViewById(R.id.main_settingsButton);
        settings.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent settingsIntent = new Intent(view.getContext(),
                		SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
        
        //user surveys button
        Button sample = (Button) findViewById(R.id.main_sampleButton);
        sample.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent surveyIntent = new Intent(view.getContext(),
                		UserSurveysActivity.class);
                startActivity(surveyIntent);
            }
        });
        
        //call survey admin button
        Button call = (Button) findViewById(R.id.main_callButton);
        call.setText(call.getText() + Config.getSetting(this,
        		Config.ADMIN_NAME, Config.ADMIN_NAME_DEFAULT));
        call.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	Intent callIntent = new Intent(Intent.ACTION_CALL);
            	callIntent.setData(Uri.parse("tel:"
            			+ Config.getSetting(MainActivity.this,
            					Config.ADMIN_PHONE_NUMBER,
            					Config.ADMIN_PHONE_NUMBER_DEFAULT)));
            	try
            	{
            		startActivity(callIntent);
            	}
            	catch (ActivityNotFoundException e)
            	{
            		Toast.makeText(MainActivity.this,
            				"Call failed!", Toast.LENGTH_SHORT);
            	}
            }
        });
        
        //exit button
        Button quit = (Button) findViewById(R.id.main_exitButton);
        quit.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	finish();
            }
        });
    }
}