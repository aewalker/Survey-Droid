/*---------------------------------------------------------------------------*
 * Pull.java                                                                 *
 *                                                                           *
 * Contains methods to pull data from the website.                           *
 *---------------------------------------------------------------------------*/
package org.peoples.android.coms;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.peoples.android.Config;
import org.peoples.android.database.PeoplesDB;
import static org.peoples.android.database.PeoplesDB.*;
import org.peoples.android.coms.WebClient;

//TODO move all the database stuff into the ComsDBHelper.  Problem is, that
//would basically render this class a wrapper.  Have to think about it...

/**
 * Provides the ability to snyc the database with the website's.
 * 
 * @author Tony Xaio
 * @author Austin Walker
 */
public class Pull extends WebClient
{
	//logging tag
	private static final String TAG = "Pull";
	
	//pull address
    private static final String PULL_URL =
    	"http://" + Config.SERVER + "/answers/pull/";

    /**
     * Syncs surveys, questions, choices, branches, and conditions with the
     * webserver's database.
     * 
     * @param ctx - the Context
     */
    public static void syncWithWeb(Context ctxt)
    {
    	try
    	{
            JSONObject json = new JSONObject(getUrlContent(PULL_URL));
            PeoplesDB pdb = new PeoplesDB(ctxt);
            SQLiteDatabase sdb = pdb.getWritableDatabase();
            syncSurveys(sdb, json.getJSONArray("surveys"));
            syncQuestions(sdb, json.getJSONArray("questions"));
            syncChocies(sdb, json.getJSONArray("choices"));
            syncBranches(sdb, json.getJSONArray("branches"));
            syncConditions(sdb, json.getJSONArray("conditions"));
            sdb.close();
            pdb.close();
        }
    	catch (Exception e)
    	{
            Log.e(TAG, e.getMessage());
        }
    }

    private static void syncSurveys(SQLiteDatabase db, JSONArray surveys)
    {
    	Log.i(TAG, "Syncing surveys table");
    	try
    	{
    		if (Config.D) Log.d(TAG, "Fetched " + surveys.length() + " surveys");
	    	for (int i = 0 ; i < surveys.length(); i++)
	    	{
	    		JSONObject survey = surveys.getJSONObject(i);
	    		ContentValues values = new ContentValues();
	    		values.put(SurveyTable._ID, survey.getInt("id"));
	    		values.put(SurveyTable.NAME, survey.getString("name"));
	    		values.put(SurveyTable.CREATED, survey.getLong("created"));
	    		values.put(SurveyTable.QUESTION_ID,
	    				survey.getInt("question_id"));
	    		values.put(SurveyTable.MO, survey.getString("mo"));
	    		values.put(SurveyTable.TU, survey.getString("tu"));
	    		values.put(SurveyTable.WE, survey.getString("we"));
	    		values.put(SurveyTable.TH, survey.getString("th"));
	    		values.put(SurveyTable.FR, survey.getString("fr"));
	    		values.put(SurveyTable.SA, survey.getString("sa"));
	    		values.put(SurveyTable.SU, survey.getString("su"));
	    		
	    		//TODO change this so that it uses replace()?
	    		db.beginTransaction();
	    		db.delete(SURVEY_TABLE_NAME, SurveyTable._ID + " = ?",
	    				new String[] {Integer.toString(survey.getInt("id"))});
				if (db.insert(SURVEY_TABLE_NAME, null, values) == -1 )
				{
					db.endTransaction();
					throw new RuntimeException("Database insert error");
				}
				db.setTransactionSuccessful();
				db.endTransaction();
	    	}
    	}
    	catch (JSONException e)
    	{
			Log.e(TAG, e.getMessage());
		}
    }
    private static void syncQuestions(SQLiteDatabase db, JSONArray questions)
    {
    	Log.i(TAG, "Syncing questions table");
    	try
    	{
	    	for (int i = 0; i < questions.length(); i++)
	    	{
	    		JSONObject survey = questions.getJSONObject(i);
	    		ContentValues values = new ContentValues();
	    		values.put(QuestionTable._ID, survey.getInt("id"));
	    		values.put(QuestionTable.SURVEY_ID,
	    				survey.getInt("survey_id"));
	    		values.put(QuestionTable.Q_TEXT, survey.getString("q_text"));
	    		if (db.replace(QUESTION_TABLE_NAME, null, values) == -1 )
				{
					throw new RuntimeException("Database replace error");
				}
	    	}
    	}
    	catch (JSONException e)
    	{
			Log.e(TAG, e.getMessage());
		}
    }
    private static void syncConditions(SQLiteDatabase db, JSONArray conditions)
    {
    	Log.i(TAG, "Syncing conditions table");
    	try
    	{
	    	for (int i = 0; i < conditions.length(); i++)
	    	{
	    		JSONObject survey = conditions.getJSONObject(i);
	    		ContentValues values = new ContentValues();
	    		values.put(ConditionTable._ID, survey.getInt("id"));
	    		values.put(ConditionTable.BRANCH_ID,
	    				survey.getInt("branch_id"));
	    		values.put(ConditionTable.QUESTION_ID,
	    				survey.getInt("question_id"));
	    		values.put(ConditionTable.CHOICE_ID,
	    				survey.getInt("choice_id"));
	    		values.put(ConditionTable.TYPE, survey.getInt("type"));
	    		if (db.replace(CONDITION_TABLE_NAME, null, values) == -1 )
				{
					throw new RuntimeException("Database replace error");
				}
	    	}
    	}
    	catch (JSONException e)
    	{
			Log.e(TAG, e.getMessage());
		}
    }
    private static void syncBranches(SQLiteDatabase db, JSONArray branches)
    {
    	Log.i(TAG, "Syncing branches table");
    	try
    	{
	    	for (int i=0; i<branches.length(); i++)
	    	{
	    		JSONObject survey = branches.getJSONObject(i);
	    		ContentValues values = new ContentValues();
	    		values.put(BranchTable._ID, survey.getInt("id"));
	    		values.put(BranchTable.QUESTION_ID,
	    				survey.getInt("question_id"));
	    		values.put(BranchTable.NEXT_Q, survey.getInt("next_q"));
	    		if (db.replace(BRANCH_TABLE_NAME, null, values) == -1 )
				{
					throw new RuntimeException("Database replace error");
				}
	    	}
    	}
    	catch (JSONException e)
    	{
			Log.e(TAG, e.getMessage());
		}
    }
    private static void syncChocies(SQLiteDatabase db, JSONArray choices)
    {
    	Log.i(TAG, "Syncing choices table");
    	try
    	{
	    	for (int i = 0; i < choices.length(); i++)
	    	{
	    		JSONObject survey = choices.getJSONObject(i);
	    		ContentValues values = new ContentValues();
	    		values.put(ChoiceTable._ID, survey.getInt("id"));
	    		values.put(ChoiceTable.QUESTION_ID,
	    				survey.getInt("question_id"));
	    		values.put(ChoiceTable.CHOICE_TEXT,
	    				survey.getString("choice_text"));
	    		if (db.replace(CHOICE_TABLE_NAME, null, values) == -1 )
				{
					throw new RuntimeException("Database replace error");
				}
	    	}
    	}
    	catch (JSONException e)
    	{
			Log.e(TAG, e.getMessage());
		}
    }
}
