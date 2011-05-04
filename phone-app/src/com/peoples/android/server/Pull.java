package com.peoples.android.server;

//import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.peoples.android.database.PeoplesDB;
import static com.peoples.android.database.PeoplesDB.*;
import com.peoples.android.server.WebClient;

public class Pull extends WebClient {

    /**
     * URL to use when requesting survey updates
     */
    public static final String PULL_URL = "http://www.eigendiego.com/cake/app/webroot/answers/pull";

    public static void syncWithWeb(Context ctx) {
    	try {
            JSONObject json = new JSONObject(getUrlContent(PULL_URL));
            PeoplesDB pdb = new PeoplesDB(ctx);
            SQLiteDatabase sdb = pdb.getWritableDatabase();
            syncSurveys(sdb, json.getJSONArray("surveys"));
            syncQuestions(sdb, json.getJSONArray("questions"));
            syncChocies(sdb, json.getJSONArray("choices"));
            syncBranches(sdb, json.getJSONArray("branches"));
            syncConditions(sdb, json.getJSONArray("conditions"));
            pdb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void syncSurveys(SQLiteDatabase db, JSONArray surveys) {
    	Log.d("Pull", "Syncing surveys table");
    	try {
	    	for (int i=0; i<surveys.length(); i++) {
	    		JSONObject survey = surveys.getJSONObject(i);
	    		ContentValues values = new ContentValues();
	    		values.put(SurveyTable._ID, survey.getInt("id"));
	    		values.put(SurveyTable.NAME, survey.getString("name"));
	    		values.put(SurveyTable.CREATED, survey.getLong("created"));
	    		values.put(SurveyTable.QUESTION_ID, survey.getInt("question_id"));
	    		values.put(SurveyTable.MO, survey.getString("mo"));
	    		values.put(SurveyTable.TU, survey.getString("tu"));
	    		values.put(SurveyTable.WE, survey.getString("we"));
	    		values.put(SurveyTable.TH, survey.getString("th"));
	    		values.put(SurveyTable.FR, survey.getString("fr"));
	    		values.put(SurveyTable.SA, survey.getString("sa"));
	    		values.put(SurveyTable.SU, survey.getString("su"));
	    		//FIXME should use l to check if it worked
	    		@SuppressWarnings("unused")
				long l = db.replace(SURVEY_TABLE_NAME, null, values);
	    	}
    	} catch (JSONException e) {
			Log.e("PUll", e.getMessage());
		}
    }
    private static void syncQuestions(SQLiteDatabase db, JSONArray questions) {
    	Log.d("Pull", "Syncing questions table");
    	try {
	    	for (int i=0; i<questions.length(); i++) {
	    		JSONObject survey = questions.getJSONObject(i);
	    		ContentValues values = new ContentValues();
	    		values.put(QuestionTable._ID, survey.getInt("id"));
	    		values.put(QuestionTable.SURVEY_ID, survey.getInt("survey_id"));
	    		values.put(QuestionTable.Q_TEXT, survey.getString("q_text"));
	    		//FIXME should use l to check if it worked
	    		@SuppressWarnings("unused")
	    		long l = db.replace(QUESTION_TABLE_NAME, null, values);
	    	}
    	} catch (JSONException e) {
			Log.e("PUll", e.getMessage());
		}
    }
    private static void syncConditions(SQLiteDatabase db, JSONArray conditions) {
    	Log.d("Pull", "Syncing conditions table");
    	try {
	    	for (int i=0; i<conditions.length(); i++) {
	    		JSONObject survey = conditions.getJSONObject(i);
	    		ContentValues values = new ContentValues();
	    		values.put(ConditionTable._ID, survey.getInt("id"));
	    		values.put(ConditionTable.BRANCH_ID, survey.getInt("branch_id"));
	    		values.put(ConditionTable.QUESTION_ID, survey.getInt("question_id"));
	    		values.put(ConditionTable.CHOICE_ID, survey.getInt("choice_id"));
	    		values.put(ConditionTable.TYPE, survey.getInt("type"));
	    		//FIXME should use l to check if it worked
	    		@SuppressWarnings("unused")
	    		long l = db.replace(CONDITION_TABLE_NAME, null, values);
	    	}
    	} catch (JSONException e) {
			Log.e("PUll", e.getMessage());
		}
    }
    private static void syncBranches(SQLiteDatabase db, JSONArray branches) {
    	Log.d("Pull", "Syncing branches table");
    	try {
	    	for (int i=0; i<branches.length(); i++) {
	    		JSONObject survey = branches.getJSONObject(i);
	    		ContentValues values = new ContentValues();
	    		values.put(BranchTable._ID, survey.getInt("id"));
	    		values.put(BranchTable.QUESTION_ID, survey.getInt("question_id"));
	    		values.put(BranchTable.NEXT_Q, survey.getInt("next_q"));
	    		//FIXME should use l to check if it worked
	    		@SuppressWarnings("unused")
	    		long l = db.replace(BRANCH_TABLE_NAME, null, values);
	    	}
    	} catch (JSONException e) {
			Log.e("PUll", e.getMessage());
		}
    }
    private static void syncChocies(SQLiteDatabase db, JSONArray choices) {
    	Log.d("Pull", "Syncing choices table");
    	try {
	    	for (int i=0; i<choices.length(); i++) {
	    		JSONObject survey = choices.getJSONObject(i);
	    		ContentValues values = new ContentValues();
	    		values.put(ChoiceTable._ID, survey.getInt("id"));
	    		values.put(ChoiceTable.QUESTION_ID, survey.getInt("question_id"));
	    		values.put(ChoiceTable.CHOICE_TEXT, survey.getString("choice_text"));
	    		//FIXME should use l to check if it worked
	    		@SuppressWarnings("unused")
	    		long l = db.replace(CHOICE_TABLE_NAME, null, values);
	    	}
    	} catch (JSONException e) {
			Log.e("PUll", e.getMessage());
		}
    }
}
