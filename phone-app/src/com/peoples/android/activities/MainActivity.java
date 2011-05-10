package com.peoples.android.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.peoples.android.Peoples;
import com.peoples.android.R;

import com.peoples.android.model.SurveyIntent;


/**
 * Triggers the pop-up informing user they have a new survey awaiting.
 * @author Henry Liu
 *
 */
public class MainActivity extends Activity {

	// Debugging
    private static final String TAG = "MainActivity";
    private static final boolean D = true;

    //time to vibrate to warn user, in milliseconds
    private static final long VIBRATION_TIME = 500;
    
    //Delay in milliseconds of your life passing
    private static final long DELAY = 20*1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(D){
        	Log.d(TAG, "+++ ON CREATE main activity +++");
        	Bundle extras = getIntent().getExtras();
            Log.d(TAG, "Deploying survey_id: "+extras.getInt("SURVEY_ID", -1));
            Log.d(TAG, "Original time: "+ extras.getLong("SURVEY_TIME"));
            Log.d(TAG, "Current  time: "+ System.currentTimeMillis());
        }
        
        // Vibrates the phone when this activity is created
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VIBRATION_TIME);
        
        //setting the layout of the activity
        setContentView(R.layout.remind);

        final TextView q = (TextView) this.findViewById(R.id.msg);
        q.setText("You have a new survey awaiting");
        
        Button sample = (Button) findViewById(R.id.Enter);
        sample.setText("Click here to take the survey now");
        sample.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Bundle extras = getIntent().getExtras(); 
                SurveyIntent myIntent = new SurveyIntent(view.getContext(), extras.getInt("SURVEY_ID"),
                		extras.getLong("SURVEY_TIME"), Peoples.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

        });

        Button postpone = (Button) findViewById(R.id.postpone);
        postpone.setText("Postpone the survey for an hour");
        postpone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Intent myIntent = new Intent(view.getContext(), LocationTestActivity.class);
                //startActivityForResult(myIntent, 0);

            	//will need one of these to schedule services
                AlarmManager alarmManager =
                	(AlarmManager) getSystemService(Context.ALARM_SERVICE);
                
				PendingIntent pendingSurvey =
					PendingIntent.getActivity(getApplicationContext(), 0,
							getIntent(),
							PendingIntent.FLAG_UPDATE_CURRENT);

				alarmManager.set(AlarmManager.RTC_WAKEUP,
									System.currentTimeMillis()+DELAY,
									pendingSurvey);
            	
            	Toast.makeText(getApplicationContext(), "The survey has been postponed for one hour",
                        Toast.LENGTH_SHORT).show();
            	finish();
            }
        });
    }
    
    
    /**
     * Called after your activity has been stopped, prior to it being started again.
     */
    @Override
    protected void onRestart() {
    	// TODO Auto-generated method stub
    	super.onRestart();
    }
    
    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    }
    
    /**
     * Called when the activity will start interacting with the user.
     */
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
    
    /**
     * Called when the system is about to start resuming a previous activity. 
     */
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    }
    
    /**
     * Called when the activity is no longer visible to the user, because
     * another activity has been resumed and is covering this one.
     */
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    }
    
    /**
     * The final call you receive before your activity is destroyed.
     */
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }

}
