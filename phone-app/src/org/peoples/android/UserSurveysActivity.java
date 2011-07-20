/*---------------------------------------------------------------------------*
 * UserSurveysActivity.java                                                  *
 *                                                                           *
 * Allows users to take surveys that they select from a list.               *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import org.peoples.android.database.PeoplesDB;
import org.peoples.android.database.SurveyDBHandler;
import org.peoples.android.survey.SurveyService;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class UserSurveysActivity extends ListActivity
{
	//logging tag
	private static final String TAG = "UserSurveysActivity";
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);

        if (Config.D) Log.d(TAG, "starting user surveys activity");
        
        //setting the layout of the activity
        Display display = ((WindowManager)
        		getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        //check what orientation the phone is in
        //getOrientation() is depreciated as of API 8, but we're targeting
        //API 7, so we have to use it
        if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
        {
        	setContentView(R.layout.user_surveys_horiz);
        }
        else
        {
        	setContentView(R.layout.user_surveys_vert);
        }
        
        //set the list data
        SurveyDBHandler sdbh = new SurveyDBHandler(this);
        sdbh.openRead();
        Cursor surveys = sdbh.getSubjectInitSurveys();
        //remember to add one so we can insert the sample survey
        final int[] ids = new int[surveys.getCount() + 1];
        String[] names = new String[surveys.getCount() + 1];
        surveys.moveToFirst();
        for (int i = 1; !surveys.isAfterLast(); i++)
        {
        	ids[i] = surveys.getInt(surveys.getColumnIndexOrThrow(
        			PeoplesDB.SurveyTable._ID));
        	names[i] = surveys.getString(surveys.getColumnIndexOrThrow(
        			PeoplesDB.SurveyTable._ID));
        	surveys.moveToNext();
        }
        surveys.close();
        sdbh.close();
        ids[0] = 0;
        names[0] = "Sample Survey";
        
        final ListView lv = getListView();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1, names);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id)
			{
				if (Config.D) Log.d(TAG, "Starting survey " + ids[pos]);
				
				Intent surveyIntent =
					new Intent(getThis(), SurveyService.class);
				surveyIntent.setAction(SurveyService.ACTION_SURVEY_READY);
				surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_TYPE,
						SurveyService.SURVEY_TYPE_USER_INIT);
				surveyIntent.putExtra(SurveyService.EXTRA_SURVEY_ID,
						ids[pos]);
				startService(surveyIntent);
				
				finish();
			}
		});
        
        //back button
        Button back = (Button) findViewById(R.id.user_surveys_backButton);
        back.setOnClickListener(new View.OnClickListener()
        {
        	@Override
            public void onClick(View view)
            {
            	finish();
            }
        });
	}
	
	//hack to get parent object
	private UserSurveysActivity getThis() { return this; }
}
