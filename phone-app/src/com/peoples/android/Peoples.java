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
import com.peoples.android.model.Choice;
import com.peoples.android.model.Question;
import com.peoples.android.model.Survey;

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
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        setContentView(R.layout.survey_list_view);
		//Creating a bogus Survey!
        
//        String[] question1choices = {"Keira Knightley",
//        		"Natalie Portman",
//        		"Emmanuelle Chiriqui"};
//        final Question question1 = new Question(1, "Who is your favorite actress?",
//        		question1choices, null);
//        String[] question2choices = {"Red",
//        		"Blue",
//        		"Green",
//        		"Purple"};
//        final Question question2 = new Question(2, "What is your favorite color", 
//        		null, null);	
//        String[] question3choices = {"Panda",
//        		"Tiger",
//        		"Penguin"};
//        final Question question3 = new Question(3, "What is your favorite animal?", 
//        		question3choices, null);
//        String[] question4choices = {"10",
//        		"24",
//        		"33"};
//        final Question question4 = new Question(4, "How old are you?", 
//        		question4choices, null);	
//        final Question question5 = new Question(5, "What country are you from?", 
//        		CHOICES, null);
//        
//        question1.setNextQuestionID(2);
//        question2.setNextQuestionID(3);
//        question3.setNextQuestionID(4);
//        question4.setNextQuestionID(5);
//        question5.setNextQuestionID(1104);
//        
//        survey.addQuestion(question1);
//        survey.addQuestion(question2);
//        survey.addQuestion(question3);
//        survey.addQuestion(question4);
//        survey.addQuestion(question5);
//    
//        survey.updateCurrentQuestionID(1);
        
//        final Question question = question1;
    	final Context panda = this;
        final Survey survey = new Survey(panda);
    	final TextView q = (TextView) this.findViewById(R.id.question_textView);
    	setListAdapter(new ArrayAdapter<String>(panda, R.layout.simple_list_item_single_choice, survey.getChoiceTexts()));
    	q.setText(survey.getText());
          
        Button next = (Button) findViewById(R.id.button1);
        next.setText("Next Question");
        next.setOnClickListener(new View.OnClickListener() {
              public void onClick(View view) {
            	  
            	  ListView lv = getListView();
            	  if ((survey.getChoices().length != 0 && lv.getCheckedItemPosition() != -1)||
            			  (survey.getChoices().length == 0))
            	  {
	            	  /*save response gogo?*/
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
	            		  survey.answer(edit.getText().toString());
	            		  Toast.makeText(getApplicationContext(), 
	            				  edit.getText().toString(),
	                              Toast.LENGTH_SHORT).show();
	            	  }
	            	  
	            	  //this stuff will be created dynamically based on the choice!
	            	  /*Toast.makeText(getApplicationContext(), 
	            			  survey.getQuestion(survey.getCurrentQuestionID()).getChoices()[lv.getCheckedItemPosition()],
	                          Toast.LENGTH_SHORT).show();*/
	            	  
	            	  survey.nextQuestion(); //go to the next Question
	            	  
	                  if (survey.done()) //if there are no more Questions....
	                  {
	                	  //display submission page?
//	                	  StringBuilder s = new StringBuilder();
//	                	  s.append("Your choices are: \n");
//	                	  for (int i = 1; i < 6; i++)
//	                	  {
//	                		  if (survey.getQuestion(i).getAnswer() != null)
//	                		  s.append("Question " + i + ": " + survey.getQuestion(i).getAnswer() + "\n");
//	                	  }
	                	  
//	                	  Bundle bundle = new Bundle();
//	                	  bundle.putString("confirm", s.toString());
//	                	  bundle.putString("answers", survey.getAnswersAsJson().toString());
	                	  
	                	  /*Toast.makeText(getApplicationContext(), s.toString(),
	                              Toast.LENGTH_SHORT).show();*/
	                	  survey.sumbit();
//	                	  Intent myIntent = new Intent(view.getContext(), ConfirmSubmissionSurvey.class);
//	                      myIntent.putExtras(bundle);
//	                	  startActivityForResult(myIntent, 0);
//	                	  finish();
	                  }
	                  else //there are still more Questions
	                  {
	                	  if (survey.getChoices().length != 0) //if multiple choice
	                	  {
			                  setListAdapter(new ArrayAdapter<String>(panda, 
			                		  R.layout.simple_list_item_single_choice, survey.getChoiceTexts()));
			              	  q.setText(survey.getText());
	                	  }
	                	  else //if free response
	                	  {
	                		  String[] test = {"Enter your response here"};
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
          });
    }
    
    static final String[] CHOICES = new String[] {
        "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
        "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
        "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
        "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
        "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
        "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory",
        "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi",
        "Cote d'Ivoire", "Cambodia", "Cameroon", "Canada", "Cape Verde",
        "Cayman Islands", "Central African Republic", "Chad", "Chile", "China",
        "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo",
        "Cook Islands", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech Republic",
        "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica", "Dominican Republic",
        "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea",
        "Estonia", "Ethiopia", "Faeroe Islands", "Falkland Islands", "Fiji", "Finland",
        "Former Yugoslav Republic of Macedonia", "France", "French Guiana", "French Polynesia",
        "French Southern Territories", "Gabon", "Georgia", "Germany", "Ghana", "Gibraltar",
        "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau",
        "Guyana", "Haiti", "Heard Island and McDonald Islands", "Honduras", "Hong Kong", "Hungary",
        "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica",
        "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos",
        "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg",
        "Macau", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands",
        "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia", "Moldova",
        "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia",
        "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand",
        "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea", "Northern Marianas",
        "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru",
        "Philippines", "Pitcairn Islands", "Poland", "Portugal", "Puerto Rico", "Qatar",
        "Reunion", "Romania", "Russia", "Rwanda", "Sqo Tome and Principe", "Saint Helena",
        "Saint Kitts and Nevis", "Saint Lucia", "Saint Pierre and Miquelon",
        "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Saudi Arabia", "Senegal",
        "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands",
        "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "South Korea",
        "Spain", "Sri Lanka", "Sudan", "Suriname", "Svalbard and Jan Mayen", "Swaziland", "Sweden",
        "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "The Bahamas",
        "The Gambia", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey",
        "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Virgin Islands", "Uganda",
        "Ukraine", "United Arab Emirates", "United Kingdom",
        "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan",
        "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Wallis and Futuna", "Western Sahara",
        "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"
      };
}