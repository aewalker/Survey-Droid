package com.peoples.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.peoples.android.Peoples;
import com.peoples.android.R;

public class ConfirmSubmissionSurvey extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmpage);
        
        String value = "panda";
        Bundle extras = getIntent().getExtras(); 
        if(extras !=null) {
            value = extras.getString("confirm");
        }
        
        //Log.e("PEOPLES", value);
        
        TextView t = (TextView)this.findViewById(R.id.confirm);
    	t.setText(value);
    	
    	Button sample = (Button) findViewById(R.id.finish);
        sample.setText("Submit Survey");
        sample.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

        });
		
    }

}
