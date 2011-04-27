package com.peoples.android;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        Log.d(TAG, "Fetching surveys");
        Pull.syncWithWeb(this);
      
        setContentView(R.layout.survey_list_view);
    	final Context panda = this;
    	
    	//code you need to put into your file to run my GUI using your survey id
    	//Bundle bundle = new Bundle();
 	    //bundle.putInt("survey_id", YOUR_FAVORITE_INTEGER(the survey id));
 		//Intent myIntent = new Intent(this, Peoples.class);
        //myIntent.putExtras(bundle);
    	//startActivityForResult(myIntent, 0);
    	
    	
    	
    	
    	//here's where i get the survey id from the bundle
    	//Bundle extras = getIntent().getExtras(); 
    	//int survey_id = extras.getInt("survey_id");
    	//survey = new Survey(survey_id, panda);
    	
        survey = new Survey(3, panda);
        
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
	            		  String[] test = {"Enter your response here"};
	            		  setListAdapter(new ArrayAdapter<String>(panda, 
		                		  R.layout.list_item, test));
		              	  q.setText(survey.getText());
		              	  
		              	  if (!survey.getAnswerText().equals(""))
		              	  {
		              		  EditText e = (EditText) findViewById(R.id.editText1);
		              		  e.setText(survey.getAnswerText());
		              	  }
	            	  }
          	  }
          	  else Toast.makeText(getApplicationContext(), "You can't go back it's the first question yo!",
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
	            		  Toast.makeText(getApplicationContext(), 
	                			  survey.getChoices()[lv.getCheckedItemPosition()].getText(),
	                              Toast.LENGTH_SHORT).show();
	            	  }
	            	  else //free response
	            	  {
	            		  EditText edit = (EditText)findViewById(R.id.editText1);
	            		  //Log.e(TAG, edit.getText().toString());

	            		  survey.answer(edit.getText().toString());
	            		  Toast.makeText(getApplicationContext(), 
	            				  edit.getText().toString(),
	                              Toast.LENGTH_SHORT).show();
	            	  }


	            	  survey.nextQuestion(); //go to the next Question

	                  if (survey.done()) //if there are no more Questions....
	                  {
	                	  Intent myIntent = new Intent(view.getContext(), ConfirmSubmissionSurvey.class);
	                	  startActivityForResult(myIntent, requestCode);
	                	  
	                	  
	                	  
	                	  /*//survey.submit();
	                	  Log.e(TAG, "it reaches the finish state");

	                	  String[] test = {"Why is this hereeee?"};
              		  setListAdapter(new ArrayAdapter<String>(panda, R.layout.list_item, test));
	                	  q.setText("Are you sure you want to submit your responses?");
	                	  //final TextView t = (TextView) findViewById(R.id.confirm);
	                	  
	                	  //setContentView(R.layout.confirmpage);
	                	  
	                	  Button back = (Button) findViewById(R.id.button1);
	                      back.setText("Go Back");
	                      back.setOnClickListener(new View.OnClickListener() {
	                            public void onClick(View view) {
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
	              	            		  String[] test = {"Enter your response here"};
	              	            		  setListAdapter(new ArrayAdapter<String>(panda, 
	              		                		  R.layout.list_item, test));
	              		              	  q.setText(survey.getText());
	              		              	  
	              		              	  if (!survey.getAnswerText().equals(""))
	              		              	  {
	              		              		  EditText e = (EditText) findViewById(R.id.editText1);
	              		              		  //e.setText(survey.getAnswerText());
	              		              	  }
	              	            	  }
	              	            	Button prev = (Button) findViewById(R.id.button1);
	              	                prev.setText("Previous Question");
	              	                prev.setOnClickListener(prevListener);
	              	                Button next = (Button) findViewById(R.id.button2);
	              	                next.setText("Next Question");
	              	                //next.setOnClickListener(nextListener);
	              	            	  
	                      	  }
	                        });
	                	  
	                	  
		                  Button submit = (Button) findViewById(R.id.button2);
		                  submit.setText("Submit Survey");
		                  submit.setOnClickListener(new View.OnClickListener() {
		                        public void onClick(View view) {
		                            boolean success = survey.submit(); //Push.sendAnswersToServer(answers);
		                            if (success) {
		                                //t.setText("Successfully submitted the survey");
		                            	Log.e(TAG, "success!");
		                                finish();
		                            } else {
		                            	Log.e(TAG, "failure");
		                            	finish();
		                                //t.setText("Oops, something went wrong");
		                            }
		                        }
	
		                  });*/
	                	  //if (survey.submit())
	                		  //Log.e(TAG, "YAY");
	                  }
	                  else //there are still more Questions
	                  {
	                	  if (survey.getChoices().length != 0) //if multiple choice
	                	  {
			                  setListAdapter(new ArrayAdapter<String>(panda, 
			                		  R.layout.simple_list_item_single_choice, survey.getChoiceTexts()));
			              	  q.setText(survey.getText());
			              	  
			              	  Log.d(TAG, "badpanda");
			              	  ListView hi = getListView();
			            	  if (survey.getAnswerChoice() != -1)
			            	  {
			            		  hi.setItemChecked(survey.getAnswerChoice(), true);
			            		  Log.d(TAG, "panda");
			            	  }
	                	  }
	                	  else //if free response
	                	  {
	                		  String[] test = {"Enter your response here"};
	                		  setListAdapter(new ArrayAdapter<String>(panda, R.layout.list_item, test));
			              	  q.setText(survey.getText());
			              	  
			              	  if (!survey.getAnswerText().equals(""))
			              	  {
			              		  EditText e = (EditText) findViewById(R.id.editText1);
			              		  e.setText(survey.getAnswerText());
			              	  }
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
            Toast.makeText(this, "Pass", Toast.LENGTH_LONG).show();
            if(survey.submit()) Log.e(TAG, "it works!");
            finish();
        }
        else{
            Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show();
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