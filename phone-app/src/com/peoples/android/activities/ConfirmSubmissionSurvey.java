package com.peoples.android.activities;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
        
        String value = "panda";
        
        JSONArray answersTemp = null;
        Bundle extras = getIntent().getExtras(); 
        if(extras !=null) {
            value = extras.getString("confirm");
            String answersJson = extras.getString("answers");
            try {
                answersTemp = new JSONArray(answersJson);
            } catch (JSONException e) {
                Log.e("confirmSubmission", e.getMessage());
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
                boolean success = Push.sendAnswersToServer(answers);
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
		
    }

}
