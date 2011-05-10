package com.peoples.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.peoples.android.R;

/**
 * Activity that starts to confirm the submission of a survey
 * @author Henry
 *
 */
public class ConfirmSubmissionSurvey extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmpage);
            
        final TextView t = (TextView)this.findViewById(R.id.confirm);
    	t.setText("Are you sure you want to submit your responses?");
        
        Button back = (Button) findViewById(R.id.back);
        back.setText("No, Go Back");
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
    }
}
