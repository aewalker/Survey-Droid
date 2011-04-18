package src.com.peoples.model;


import java.util.HashMap;
import java.util.Iterator;

public class Survey {
	
	
	/**id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,**/
	private int SURVEY_KEY;
	
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
	
	private HashMap<Integer, Question> panda;
	
	public Survey() {
		panda = new HashMap<Integer, Question>();
	}
	
	public void addQuestion(int id, Question question)
	{
		panda.put(id, question);
	}
	
	public Question getQuestion(int id)
	{
		return panda.get(id);
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
