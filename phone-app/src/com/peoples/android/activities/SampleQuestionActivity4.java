package com.peoples.android.activities;

import com.peoples.android.R;
import com.peoples.android.processTest.LocationTestActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class SampleQuestionActivity4 extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplechoiceview);
        
        TextView q1 = (TextView)this.findViewById(R.id.question_textView);
        q1.setText("Are we getting an A for this project??");
        RadioButton q1r1 = (RadioButton)this.findViewById(R.id.radio1);
        q1r1.setText("Yes");
        RadioButton q1r2 = (RadioButton)this.findViewById(R.id.radio2);
        q1r2.setText("Yes");
        
        Button next = (Button) findViewById(R.id.button1);
        next.setText("Finish Survey");
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

    }

}
