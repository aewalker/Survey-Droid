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

public class SampleQuestionActivity3 extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shortresponseview);
        
        TextView q1 = (TextView)this.findViewById(R.id.textView1);
        q1.setText("You can type something here if you'd like :)");
        
        Button next = (Button) findViewById(R.id.button1);
        next.setText("Next Question");
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent myIntent = new Intent(view.getContext(), SampleQuestionActivity4.class);
                startActivityForResult(myIntent, 0);
            }
        });

    }

}
