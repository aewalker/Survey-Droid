package com.peoples.android.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Survey {
	
	
	/**id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,**/
	private int id;
	
	/**name VARCHAR(255)),**/
	private String name;
	
	/**created DATETIME,**/
	private String DATETIME;
	
	/**first_q INT UNSIGNED NOT NULL**/
	private Question first_q;
	
	/**field for each day; holds times in 24 hour format separated by commas
	mo VARCHAR(255),
	tu VARCHAR(255),
	we VARCHAR(255),
	th VARCHAR(255),
	fr VARCHAR(255),
	sa VARCHAR(255),
	su VARCHAR(255));**/
	private String mo, tu, we, th, fr, sa, su;
	
	private int currentQuestionID;
	private HashMap<Integer, Question> survey;
	
	public Survey() {
		survey = new HashMap<Integer, Question>();
	}
	
	public void addQuestion(Question question)
	{
		survey.put(question.getQuestionKey(), question);
	}
	
	public Question getQuestion(int id)
	{
		return survey.get(id);
	}
	
	public List<Question> getQuestions() {
	    List<Question> qs = new ArrayList<Question>();
	    for (int id : survey.keySet()) 
	        qs.add(survey.get(id));
	    return qs;
	}
	
	public JSONArray getAnswersAsJson() {
	    JSONArray answers = new JSONArray();
	    for (Question q : getQuestions()) {
	        answers.put(q.answer.getAsJson());
	    }
	    return answers;
	}
	
	public int getCurrentQuestionID() 
	{
		return currentQuestionID;
	}
	
	public void updateCurrentQuestionID(int id)
	{
		currentQuestionID = id;
	}
}
