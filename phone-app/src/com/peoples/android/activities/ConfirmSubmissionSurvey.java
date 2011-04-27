package com.peoples.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.peoples.android.R;
import com.peoples.android.server.Push;

public class ConfirmSubmissionSurvey extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmpage);
            
        final TextView t = (TextView)this.findViewById(R.id.confirm);
    	t.setText("Are you sure you want to submit your responses?");
        
        Button back = (Button) findViewById(R.id.back);
        back.setText("No! Go Back");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent in = new Intent();
            	setResult(0,in);
                finish();
            }
        });
        
        Button confirm = (Button) findViewById(R.id.finish);
        confirm.setText("Submit my responses");
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent in = new Intent();
            	setResult(1,in);
                finish();
            }
        });
        
        
        /*setResult(0,in);//Here I am Setting the Requestcode 1, you can put according to your requirement
        finish();
        
        JSONArray answersTemp = null;
        Bundle extras = getIntent().getExtras(); 
        if(extras !=null) {
            value = extras.getString("confirm");
            String answersJson = extras.getString("answers");
            try {
                answersTemp = new JSONArray(answersJson);
            } catch (JSONException e) {
                Log.d("confirmSubmission", e.getMessage());
            }
        }
        final JSONArray answers = answersTemp;
        
        
        
        //Log.e("PEOPLES", value);
        
        final TextView t = (TextView)this.findViewById(R.id.confirm);
    	t.setText(value);
    	
    	Button sample = (Button) findViewById(R.id.finish);
        sample.setText("Submit Survey");
        sample.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean success = true; //Push.sendAnswersToServer(answers);
                if (success) {
                    t.setText("Successfully submitted the survey");
                    finish();
                } else {
                    t.setText("Ooops, something went wrong");
                }
//                
//                finish();
            }

        });
		*/
    }

}
