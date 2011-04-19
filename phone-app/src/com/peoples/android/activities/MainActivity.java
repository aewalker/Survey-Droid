package com.peoples.android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.peoples.android.Peoples;
import com.peoples.android.R;
import com.peoples.android.model.Question;
import com.peoples.android.model.Survey;
import com.peoples.android.processTest.LocationTestActivity;
import com.peoples.android.services.GPSLocationService;



public class MainActivity extends Activity {
	
	// Debugging
	// TEST
	//TEST
    private static final String TAG = "MainActivity";
    private static final boolean D = true;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        
        setContentView(R.layout.main);

        //Creating a bogus Survey!
        /*Survey survey = new Survey();
        
        final Question question1 = new Question("Who is your favorite actress?", 
        		"Keira Knightley",
        		"Natalie Portman",
        		"Emmanuelle Chiriqui");
        final Question question2 = new Question("What is your favorite color", 
        		"Red",
        		"Blue",
        		"Green");	
        final Question question3 = new Question("What is your favorite animal?", 
        		"Panda",
        		"Tiger",
        		"Penguin");	
        final Question question4 = new Question("How old are you?", 
        		"10",
        		"24",
        		"33");	
        final Question question5 = new Question("I can't think of anymore lame questions", 
        		"ag;oagrf",
        		"qgwljdbsn;f",
        		"afilue4atg");
        
        question1.setNextQuestionID(2);
        question2.setNextQuestionID(3);
        question3.setNextQuestionID(4);
        question4.setNextQuestionID(5);
        question5.setNextQuestionID(1);
        
        survey.addQuestion(1, question1);
        survey.addQuestion(2, question2);
        survey.addQuestion(3, question3);
        survey.addQuestion(4, question4);
        survey.addQuestion(5, question5);*/
        
        
        
        
        
        
        Button sample = (Button) findViewById(R.id.Enter);
        sample.setText("Take a sample survey");
        sample.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Peoples.class);
                startActivityForResult(myIntent, 0);
            }

        });
        
        Button gps = (Button) findViewById(R.id.GPS);
        gps.setText("Tell me my location every 15 seconds!");
        gps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), LocationTestActivity.class);
                startActivityForResult(myIntent, 0);
            }

        });
        
        //This is just code to test GPS location gathering and persisting to database
        Context context = getApplicationContext();
        Intent gpsIntent = new Intent(context, GPSLocationService.class);
        gpsIntent = gpsIntent.setClass(context, GPSLocationService.class);
        context.startService(gpsIntent);
        
    }

}
