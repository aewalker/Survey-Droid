package com.peoples.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.peoples.android.R;
import com.peoples.android.model.SurveyIntent;

/**
 * Activity that starts to confirm the submission of a survey
 * @author Henry Liu
 */
public class ConfirmSubmissionSurvey extends Activity {
	
	private static final String TAG = "ConfirmSubmission";
    private static final boolean D = true;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //setting the layout of the activity
        setContentView(R.layout.confirmpage);
        
        
        Intent executedIntent = getIntent();
        
        Integer survid 	= executedIntent.getIntExtra(
				SurveyIntent.SURVEY_ID,
				-1);

        Long time		= executedIntent.getLongExtra(
        		SurveyIntent.SURVEY_TIME,
        		-1);

        Log.d(TAG, "survid: "+survid);
        Log.d(TAG, "time: "+time);
        
            
        final TextView t = (TextView)this.findViewById(R.id.confirm);
    	t.setText("Are you sure you want to submit your responses?");
        
        Button back = (Button) findViewById(R.id.back);
        back.setText("No, Go Back");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent in = new Intent();
            	setResult(0,getIntent());
                finish();
            }
        });
        
        Button confirm = (Button) findViewById(R.id.finish);
        confirm.setText("Submit my responses");
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent in = new Intent();
            	setResult(1,getIntent());
                finish();
            }
        });
    }
}
