package com.peoples.android;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
//import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.peoples.android.activities.ConfirmSubmissionSurvey;
import com.peoples.android.model.Survey;
import com.peoples.android.server.Pull;

/**
 * 
 * Used to launch processes during development and testing
 * 
 * @author Vlad
 *
 */
public class Peoples extends ListActivity {	
    // Debugging
	// TEST
	//TEST
    private static final String TAG = "Peoples";
    private static final boolean D = true;
    private int requestCode;
    
    private Survey survey;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        
        //Why is this here? (Diego asks...)
//        Log.d(TAG, "Fetching surveys");
//        Pull.syncWithWeb(this);

        setContentView(R.layout.survey_list_view);

    	final Context panda = this;
    	
    	//code you need to put into your file to run my GUI using your survey id
    	//Bundle bundle = new Bundle();
 	    //bundle.putInt("survey_id", YOUR_FAVORITE_INTEGER(the survey id));
 		//Intent myIntent = new Intent(this, Peoples.class);
        //myIntent.putExtras(bundle);
    	//startActivityForResult(myIntent, 0);

    	//here's where i get the survey id from the bundle
    	Bundle extras = getIntent().getExtras(); 
    	int survey_id = extras.getInt("SURVEY_ID");
    	
    	if (survey_id == 0)
    	{
    		survey = new Survey(panda);
    	}
    	else 
    	{
            survey = new Survey(survey_id, panda);
    	}
    	//survey = new Survey(survey_id, panda);

    	//Bundle extras = getIntent().getExtras(); 
    	//survey = (Survey) extras.getSerializable("survey");
    	
        Log.d(TAG, "Finished creating survey");

        if (survey.done()) throw new RuntimeException("Survey has no questions!");
    	final TextView q = (TextView) this.findViewById(R.id.question_textView);
    	setListAdapter(new ArrayAdapter<String>(panda, R.layout.simple_list_item_single_choice, survey.getChoiceTexts()));
    	q.setText(survey.getText());
          
    	Button prev = (Button) findViewById(R.id.button1);
        prev.setText("Previous Question");
        final View.OnClickListener prevListener = new View.OnClickListener() {
            public void onClick(View view) {
          	  
          	  if (!survey.isOnFirst()) {
	            	  survey.prevQuestion(); //go to the previous Question
	            	  ListView lv = getListView();
	            	  
	            	  if (survey.getChoices().length != 0) //if multiple choice
	            	  {
		                  setListAdapter(new ArrayAdapter<String>(panda, 
		                		  R.layout.simple_list_item_single_choice, survey.getChoiceTexts()));
		              	  q.setText(survey.getText());
		              	  
		            	  if (survey.getAnswerChoice() != -1)
		            		  lv.setItemChecked(survey.getAnswerChoice(), true);
	            	  }
	            	  else //if free response
	            	  {
	            		  String[] test = {""};
	            		  if (!survey.getAnswerText().equals(""))
	            		  {
	            			  test[0] = survey.getAnswerText();
	            		  }
	            		  setListAdapter(new ArrayAdapter<String>(panda, 
		                		  R.layout.list_item, test));
		              	  q.setText(survey.getText());

	            	  }
          	  }
          	  else Toast.makeText(getApplicationContext(), "You can't go back on the first question",
                        Toast.LENGTH_SHORT).show();
      	  }
        };
        prev.setOnClickListener(prevListener);
    	
        Button next = (Button) findViewById(R.id.button2);
        next.setText("Next Question");
        final View.OnClickListener nextListener = new View.OnClickListener() {
            public void onClick(View view) {
          	  
          	  ListView lv = getListView();

          	  if ((survey.getChoices().length != 0 && lv.getCheckedItemPosition() != -1)||
          			  (survey.getChoices().length == 0))
          	  {
	            	  //save response gogo?
	            	  if (survey.getChoices().length != 0) //multiple choice
	            	  {
	            		  survey.answer(survey.getChoices()[lv.getCheckedItemPosition()]);
	            		  /*Toast.makeText(getApplicationContext(), 
	                			  survey.getChoices()[lv.getCheckedItemPosition()].getText(),
	                              Toast.LENGTH_SHORT).show();*/
	            	  }
	            	  else //free response
	            	  {
	            		  EditText edit = (EditText)findViewById(R.id.editText1);

	            		  //Log.e(TAG, edit.getText().toString());

	            		  survey.answer(edit.getText().toString());
	            		  /*Toast.makeText(getApplicationContext(), 
	            				  edit.getText().toString(),
	                              Toast.LENGTH_SHORT).show();*/
	            	  }


	            	  survey.nextQuestion(); //go to the next Question

	                  if (survey.done()) //if there are no more Questions....
	                  {
	                	  Intent myIntent = new Intent(view.getContext(), ConfirmSubmissionSurvey.class);
	                	  startActivityForResult(myIntent, requestCode);
	                  }
	                  else //there are still more Questions
	                  {
	                	  if (survey.getChoices().length != 0) //if multiple choice
	                	  {
			                  setListAdapter(new ArrayAdapter<String>(panda, 
			                		  R.layout.simple_list_item_single_choice, survey.getChoiceTexts()));
			              	  q.setText(survey.getText());
			              	  
			              	  ListView hi = getListView();
			            	  if (survey.getAnswerChoice() != -1)
			            	  {
			            		  hi.setItemChecked(survey.getAnswerChoice(), true);
			            	  }

	                	  }
	                	  else //if free response
	                	  {
		            		  String[] test = {""};
		            		  if (!survey.getAnswerText().equals(""))
		            		  {
		            			  test[0] = survey.getAnswerText();
		            		  }
		            		  setListAdapter(new ArrayAdapter<String>(panda, 
			                		  R.layout.list_item, test));
			              	  q.setText(survey.getText());

	                	  }
	                  }
          	  }
          	  else 
          	  {
          		  Toast.makeText(getApplicationContext(), "Please select a choice", Toast.LENGTH_SHORT).show();
          	  }
            }
        };
        next.setOnClickListener(nextListener);
        
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
            Toast.makeText(this, "Thank you for taking our survey", Toast.LENGTH_LONG).show();
            if(!survey.submit()) Log.e(TAG, "Something went wrong and the survey didn't submit");
            finish();
        }
        else{
            //Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show();
            //go to the previous question and start over?
            survey.prevQuestion();
        }
    }
    
/*    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.survey_list_view);
    	Log.e(TAG, "panddaaaa");
    }*/
}