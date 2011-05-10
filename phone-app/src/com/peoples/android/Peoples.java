package com.peoples.android;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
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
import com.peoples.android.database.ScheduledSurveyDBHandler;
import com.peoples.android.model.Survey;

/**
 * Used to launch actual surveys
 * @author Vlad
 * @author Henry
 */
public class Peoples extends ListActivity {	
    
	// Debugging
	private static final String TAG = "Peoples";
    private static final boolean D = true;
    
    private int requestCode;    
    private Survey survey;
        
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        //setting the layout of the activity
        setContentView(R.layout.survey_list_view);

    	final Context ctxt = this;
    	
    	// Receives survey ID from MainActivity
    	Bundle extras = getIntent().getExtras(); 
    	int survey_id = extras.getInt("SURVEY_ID");
    	
    	if (survey_id == 0)
    		survey = new Survey(ctxt);
    	else 
            survey = new Survey(survey_id, ctxt);
    	
        Log.d(TAG, "Finished creating survey");

        if (survey.done()) throw new RuntimeException("Survey has no questions!");
        
    	final TextView q = (TextView) this.findViewById(R.id.question_textView);
    	setListAdapter(new ArrayAdapter<String>(ctxt, R.layout.simple_list_item_single_choice, survey.getChoiceTexts()));
    	q.setText(survey.getText());
          
    	Button prev = (Button) findViewById(R.id.button1);
        prev.setText("Previous Question");
        /**
         * Handler for "previous" button
         */
        final View.OnClickListener prevListener = new View.OnClickListener() {
            public void onClick(View view) {
          	  
          	  if (!survey.isOnFirst()) {
	            	  survey.prevQuestion(); //go to the previous Question
	            	  ListView lv = getListView();
	            	  
	            	  if (survey.getChoices().length != 0) //if multiple choice
	            	  {
		                  setListAdapter(new ArrayAdapter<String>(ctxt, 
		                		  R.layout.simple_list_item_single_choice, survey.getChoiceTexts()));
		              	  q.setText(survey.getText());
		              	  
		              	  //if answer has been saved before
		            	  if (survey.getAnswerChoice() != -1)
		            		  lv.setItemChecked(survey.getAnswerChoice(), true);
	            	  }
	            	  else //if free response
	            	  {
	            		  String[] test = {""}; //default text is empty String
	            		  if (!survey.getAnswerText().equals(""))
	            		  {
	            			  test[0] = survey.getAnswerText(); //if answer has been saved before
	            		  }
	            		  setListAdapter(new ArrayAdapter<String>(ctxt, 
		                		  R.layout.list_item, test));
		              	  q.setText(survey.getText());
	            	  }
          	  }
          	  else Toast.makeText(getApplicationContext(), "You can't go back on the first question",
                        Toast.LENGTH_SHORT).show();
      	  }
        };
        prev.setOnClickListener(prevListener);
    	
        /**
         * Handler for "next" button
         */
        Button next = (Button) findViewById(R.id.button2);
        next.setText("Next Question");
        final View.OnClickListener nextListener = new View.OnClickListener() {
            public void onClick(View view) {
          	  
          	  ListView lv = getListView();

          	  if ((survey.getChoices().length != 0 && lv.getCheckedItemPosition() != -1)||
          			  (survey.getChoices().length == 0))
          	  {
	            	  //save response 
	            	  if (survey.getChoices().length != 0) //multiple choice
	            	  {
	            		  survey.answer(survey.getChoices()[lv.getCheckedItemPosition()]);

	            	  }
	            	  else //free response
	            	  {
	            		  EditText edit = (EditText)findViewById(R.id.editText1);
	            		  survey.answer(edit.getText().toString());
	            	  }

	            	  survey.nextQuestion(); //go to the next question

	                  if (survey.done()) //if there are no more questions....
	                  {
	                	  Intent myIntent = new Intent(view.getContext(), ConfirmSubmissionSurvey.class);
	                	  startActivityForResult(myIntent, requestCode);
	                  }
	                  else //there are still more questions
	                  {
	                	  if (survey.getChoices().length != 0) //if multiple choice
	                	  {
			                  setListAdapter(new ArrayAdapter<String>(ctxt, 
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
		            		  setListAdapter(new ArrayAdapter<String>(ctxt, 
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
    
    /**
     * Triggers when result is returned from the confirm submission acitivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
        	int remove = ScheduledSurveyDBHandler.removeIntent(
        					getApplicationContext(), data );
        	
            Toast.makeText(this, "Thank you for taking our survey", Toast.LENGTH_LONG).show();
            if(!survey.submit()) Log.e(TAG, "Something went wrong and the survey didn't submit");
            if(remove <= 0) Log.e(TAG, "no surveys matched in the scheduled db");
            finish();
        }
        else{
            //go to the previous question
            survey.prevQuestion();
        }
    }
    
}