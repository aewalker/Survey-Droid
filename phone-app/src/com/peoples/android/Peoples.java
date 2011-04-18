package com.peoples.android;

import src.com.peoples.model.Question;
import src.com.peoples.model.Survey;

import com.peoples.android.activities.ConfirmSubmissionSurvey;
import com.peoples.android.activities.SampleQuestionActivity2;
import com.peoples.android.processTest.LocationTestActivity;
import com.peoples.android.services.CallLogService;
import com.peoples.android.views.SurveyAdapter;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
    private static final String TAG = "PEOPLES";
    private static final boolean D = true;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
        setContentView(R.layout.survey_list_view);
		//Creating a bogus Survey!
        final Survey survey = new Survey();
        
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
        question5.setNextQuestionID(1104);
        
        survey.addQuestion(1, question1);
        survey.addQuestion(2, question2);
        survey.addQuestion(3, question3);
        survey.addQuestion(4, question4);
        survey.addQuestion(5, question5);
    
        survey.updateCurrentQuestionID(1);
        
        final Question question = question1;
    	final Context panda = this;
    	final TextView q = (TextView)this.findViewById(R.id.question_textView);
    	setListAdapter(new ArrayAdapter<String>(panda, R.layout.simple_list_item_single_choice, question.getChoices()));
    	q.setText(question.getQuestionText());
          
        Button next = (Button) findViewById(R.id.button1);
        next.setText("Next Question");
        next.setOnClickListener(new View.OnClickListener() {
              public void onClick(View view) {
            	  /*save response gogo?*/
            	  
            	  ListView lv = getListView();

            	  //this stuff will be created dynamically based on the choice!
            	  Toast.makeText(getApplicationContext(), 
            			  survey.getQuestion(survey.getCurrentQuestionID()).getChoices()[lv.getCheckedItemPosition()],
                          Toast.LENGTH_SHORT).show();
            	  
            	  int nextQuestionID = survey.getQuestion(survey.getCurrentQuestionID()).getNextQuestionID();
                  if (nextQuestionID == 1104)
                  {
                	  //display submission page?
                	  Log.e(TAG, "im a cool kid");
                	  Toast.makeText(getApplicationContext(), "Display submission page yo!",
                              Toast.LENGTH_SHORT).show();
                	  Intent myIntent = new Intent(view.getContext(), ConfirmSubmissionSurvey.class);
                      startActivityForResult(myIntent, 0);
                	  finish();
                  }
                  else 
                  {
                  setListAdapter(new ArrayAdapter<String>(panda, 
                		  R.layout.simple_list_item_single_choice, survey.getQuestion(nextQuestionID).getChoices()));
              	  q.setText(survey.getQuestion(nextQuestionID).getQuestionText());
                  survey.updateCurrentQuestionID(nextQuestionID);
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