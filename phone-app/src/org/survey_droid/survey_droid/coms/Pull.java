/*---------------------------------------------------------------------------*
 * Pull.java                                                                 *
 *                                                                           *
 * Contains methods to pull data from the website.                           *
 *---------------------------------------------------------------------------*
 * Copyright (C) 2011-2012 Survey Droid Contributors                         *
 *                                                                           *
 * This file is part of Survey Droid.                                        *
 *                                                                           *
 * Survey Droid is free software: you can redistribute it and/or modify      *
 * it under the terms of the GNU General Public License as published by      *
 * the Free Software Foundation, either version 3 of the License, or         *
 * (at your option) any later version.                                       *
 *                                                                           *
 * Survey Droid is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of            *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             *
 * GNU General Public License for more details.                              *
 *                                                                           *
 * You should have received a copy of the GNU General Public License         *
 * along with Survey Droid.  If not, see <http://www.gnu.org/licenses/>.     *
 *****************************************************************************/
package org.survey_droid.survey_droid.coms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.telephony.TelephonyManager;

import org.survey_droid.survey_droid.Config;
import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.annotation.ConfigKey;
import org.survey_droid.survey_droid.coms.WebClient;
import org.survey_droid.survey_droid.coms.WebClient.ApiException;
import org.survey_droid.survey_droid.content.SurveyDroidDB;
import org.survey_droid.survey_droid.content.ProviderContract.*;
import org.survey_droid.survey_droid.survey.SurveyScheduler;

import com.commonsware.cwac.wakeful.WakefulIntentService;

/* Note that this class doesn't use the ComsDBHandler like it seems that is
 * should.  The reason for this is that the database operations that are
 * executed here are mass insert/update, which require the SQL to be prepared
 * as we iterate over the data.  To have to bundle up that data and ship it
 * to another class would waste a lot of resources. 
 */

/**
 * Provides the ability to snyc the database with the website's.
 *
 * @author Tony Xaio
 * @author Austin Walker
 */
public class Pull
{
	/** logging tag */
	private static final String TAG = "Pull";
	
	/** Frequency with which to pull data (in minutes) */
	@ConfigKey("" + 60 * 24)
	public static final String PULL_INTERVAL = "pull_interval";

	/** pull address to be appended to the server */
    private static final String PULL_URL = "/api/pull/";
    
    /** context for various calls */
    private static Context c;
    
    //data types - see syncTable
    private enum DataType
    {
    	INT,
    	LONG,
    	DOUBLE,
    	STRING,
    	BLOB;
    }

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
    		c = ctxt;
    		TelephonyManager tManager =
            	(TelephonyManager) ctxt.getSystemService(
            			Context.TELEPHONY_SERVICE);
    		StringBuilder url = new StringBuilder();
    		if (Config.getBoolean(ctxt, ComsService.HTTPS))
    			url.append("https://");
    		else
    			url.append("http://");
    		url.append(Config.getString(ctxt, ComsService.SERVER));
    		url.append(PULL_URL);
    		String uid = tManager.getDeviceId();
    		if (uid != null)
    		{
	    		url.append(uid);
	    		Util.v(null, TAG, "Pull url: " + url.toString());
	    		JSONObject json;
	    		try
	    		{
	    			json = new JSONObject(WebClient.getUrlContent(ctxt, url.toString()));
	    		}
	    		catch (Exception e)
	    		{
	    			Util.e(null, TAG,
	    					"Unable to communicate with remote server");
	    			try
	    			{
	    				ApiException apiE = (ApiException) e;
	    				Util.e(null, TAG, "Reason: " + apiE.getMessage());
	    				Util.e(null, TAG, "Make sure this device is registered"
	    						+ " and the server is working");
	    			}
	    			catch (Exception unknownE)
	    			{
	    				Util.e(null, TAG, "Unkown Reason: " + Util.fmt(e));
	    			}
	    			return;
	    		}
	    		try
	    		{
		            SurveyDroidDB pdb = new SurveyDroidDB(ctxt);
		            SQLiteDatabase sdb = pdb.getWritableDatabase();
		            //it's important that config be the first thing
		            syncConfig(sdb, json.getJSONObject("config"), ctxt);
		            syncSurveys(sdb, json.getJSONArray("surveys"));
		            syncQuestions(sdb, json.getJSONArray("questions"));
		            syncBranches(sdb, json.getJSONArray("branches"));
		            syncConditions(sdb, json.getJSONArray("conditions"));
		            sdb.close();
		            pdb.close();
	    		}
	    		catch (Exception e)
	    		{
	    			Util.e(null, TAG, "database error during pull:");
	    			Util.d(ctxt, TAG, "Database error: " + Util.fmt(e, Integer.MAX_VALUE));
	    			Util.e(null, TAG, Util.fmt(e));
	    		}
	            
	            //run the scheduler
		    	Util.d(null, TAG, "Starting survey scheduler");
		    	Intent schedulerIntent = new Intent(ctxt.getApplicationContext(),
		    			SurveyScheduler.class);
		    	schedulerIntent.setAction(
		    			SurveyScheduler.ACTION_SCHEDULE_SURVEYS);
		    	schedulerIntent.putExtra(SurveyScheduler.EXTRA_RUNNING_TIME,
		    			System.currentTimeMillis());
		    	WakefulIntentService.sendWakefulWork(ctxt, schedulerIntent);
        	}
        	else
        	{
        		Util.w(null, TAG, "Device ID not available");
        		Util.w(null, TAG, "Will reschedule and try again later");
        	}
        }
    	catch (Exception e)
    	{
            Util.e(ctxt, TAG, Util.fmt(e));
            throw new RuntimeException("FATAL ERROR", e);
    	}
    	finally
    	{
        	c = null; //avoids a memory leak
    	}
    }
    
    /**
     * Syncs a table with data gathered from the web.
     * 
     * @param db - the database object to insert into
     * @param table - name of the table to insert into
     * @param fields - map:JSON field name -> SQL field name
     * @param types - map:JSON field name -> type of data
     * @param data - the JSON data
     */
    //This method is based on the technique described here:
    //sagistech.blogspot.com/2010/07/notes-on-android-sqlite-bukl-insert.html
    private static void syncTable(SQLiteDatabase db, String table,
    		Map<String, String> fields, Map<String, DataType> types,
    		JSONArray data)
    {
    	Util.i(null, TAG, "Synching " + table + " table");
    	Util.d(null, TAG, "Fetched " + data.length() + " records");
    	
    	//do some error checking first
    	if (Config.D)
    	{
    		String[] JSONnames = fields.keySet().toArray(new String[0]);
    		for (String name : JSONnames)
    		{
    			if (!types.containsKey(name))
    				throw new IllegalArgumentException(
    					"No type information about \"" + name + "\"");
    		}
    	}
    	
    	db.beginTransaction();
    	try
    	{
    		//first, we need to remove all the old data
    		db.execSQL("DELETE FROM " + table);
    		
    		//now put in the new data
    		StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
    		sqlBuilder.append(table);
    		sqlBuilder.append(" (");
    		String[] JSONnames = fields.keySet().toArray(new String[0]);
    		for (int i = 0; i < JSONnames.length - 1; i++)
    		{
    			sqlBuilder.append(fields.get(JSONnames[i]));
    			sqlBuilder.append(", ");
    		}
    		sqlBuilder.append(fields.get(JSONnames[JSONnames.length - 1]));
    		sqlBuilder.append(") VALUES (");
    		for (int i = 0; i < JSONnames.length - 1; i++)
    		{
    			sqlBuilder.append("?, ");
    		}
    		sqlBuilder.append("?)");
    		SQLiteStatement sql = db.compileStatement(sqlBuilder.toString());
    		for (int i = 0; i < data.length(); i++)
    		{
    			JSONObject item = data.getJSONObject(i);
    			for (int j = 0; j < JSONnames.length; j++)
    			{
    				String name = JSONnames[j];
    				try
    				{
	    				switch (types.get(name))
	    				{
	    				case INT:
	    					sql.bindLong(j + 1, item.getInt(name));
	    					break;
	    				case LONG:
	    					sql.bindLong(j + 1, item.getLong(name));
	    					break;
	    				case DOUBLE:
	    					sql.bindDouble(j + 1, item.getDouble(name));
	    					break;
	    				case STRING:
	    					sql.bindString(j + 1, item.getString(name));
	    					break;
	    				default:
	    					throw new IllegalArgumentException(
	    							"Illegal JSON data type");
	    				}
    				}
    				catch(JSONException je)
    				{
    					sql.bindNull(j);
    				}
    			}
        		if (sql.executeInsert() == -1)
        			Util.e(null, TAG, "Insertion failed!");
    		}
    		db.setTransactionSuccessful();
    	}
    	catch (Exception e)
    	{
    		Util.e(null, TAG, "Error during database sync: " + Util.fmt(e));
    	}
    	finally
    	{
    		db.endTransaction();
    	}
    }

	@SuppressWarnings("unchecked")
	//this doesn't use syncTable because it has strange fields and will
	//only have a few rows
	//additionally, we need to track the number of surveys per week
	private static void syncSurveys(SQLiteDatabase db, JSONArray surveys)
    {
    	Util.i(null, TAG, "Syncing surveys table");
    	try
    	{
    		Util.d(null, TAG, "Fetched " + surveys.length() + " surveys");
    		int numSurveys = 0;
	    	for (int i = 0 ; i < surveys.length(); i++)
	    	{
	    		JSONObject survey = surveys.getJSONObject(i);
	    		ContentValues values = new ContentValues();
	    		long id = survey.getLong("id");
	    		values.put(SurveyTable.Fields.SURVEY_ID, id);
	    		values.put(SurveyTable.NAME,
	    				survey.getString(SurveyTable.NAME));
	    		values.put(SurveyTable.Fields.QUESTION_ID,
	    				survey.getLong(SurveyTable.Fields.QUESTION_ID));
	    		String[] opt_fields = {SurveyTable.Fields.SUBJECT_INIT,
	    							   SurveyTable.Fields.NEW_CALLS,
	    							   SurveyTable.Fields.OLD_CALLS,
	    							   SurveyTable.Fields.NEW_TEXTS,
	    							   SurveyTable.Fields.OLD_TEXTS};
	    		for (String field : opt_fields)
	    		{
	    			try
	    			{
	    				values.put(field, survey.getString(field));
	    			}
	    			catch (Exception e)
	    			{
	    				Util.v(null, TAG,
	    						"survey " + i + ": no \"" + field + "\"");
	    			}
	    		}
	    		
	    		//count up how many surveys per week should go off
	    		for (String day : SurveyTable.DAYS)
	    		{
	    			try
	    			{
	    				String times = survey.getString(day);
	    				values.put(day, times);
	    				for (String time : times.split(","))
	    				{
	    					if (time.matches("\\s") || time.equals(""))
	    						continue;
	    					numSurveys++;
	    				}
	    				
	    			}
	    			catch (Exception e)
	    			{
	    				Util.v(null, TAG,
	    						"survey " + i + ": no \"" + day + "\"");
	    			}
	    		}

	    		//subject variables is special (it's an object)
	    		JSONObject vars = null;
	    		try
	    		{
	    			vars = survey.getJSONObject("subject_variables");	
	    		}
	    		catch (JSONException e)
	    		{
	    			Util.v(null, TAG, "no or mal-formed subject variables");
	    		}
    		    
	    		if (vars != null)
	    		{
	    			Util.v(null, TAG, "Grabing subject variables for " + id);
	    			Iterator<String> keys = vars.keys();
		    		while (keys.hasNext())
		    		{
		    			String key = keys.next();
						Config.putSetting(c, Config.USER_DATA + "#" + id +
								"#" + key, vars.getString(key));
						Util.v(null, TAG, "key: \"" + Config.USER_DATA + "#" + id +
								"#" + key + "\", value: \"" + vars.getString(key) + "\"");
		    		}
	    		}

	    		db.beginTransaction();
	    		db.delete(SurveyTable.NAME, SurveyTable.Fields._ID + " = ?",
	    				new String[] {Long.toString(survey.getLong("id"))});
				if (db.insert(SurveyTable.NAME, null, values) == -1 )
				{
					db.endTransaction();
					throw new RuntimeException("Database insert error");
				}
				db.setTransactionSuccessful();
				db.endTransaction();
	    	}
    		Config.putSetting(c, Config.SURVEYS_PER_WEEK, numSurveys);
    	}
    	catch (JSONException e)
    	{
			Util.e(null, TAG, Util.fmt(e));
		}
    }
	
    private static void syncQuestions(SQLiteDatabase db, JSONArray questions)
    {
    	String table = QuestionTable.NAME;
    	Map<String, String> names = new HashMap<String, String>();
    	Map<String, DataType> types = new HashMap<String, DataType>();

    	//fields
    	names.put("id", QuestionTable.Fields.QUESTION_ID);
    	types.put("id", DataType.LONG);
    	names.put("survey_id", QuestionTable.Fields.SURVEY_ID);
    	types.put("survey_id", DataType.LONG);
    	names.put("data", QuestionTable.Fields.DATA);
    	types.put("data", DataType.BLOB);
    	names.put("package", QuestionTable.Fields.PACKAGE);
    	types.put("package", DataType.STRING);
    	names.put("class", QuestionTable.Fields.CLASS);
    	types.put("class", DataType.STRING);
    	
    	syncTable(db, table, names, types, questions);
    }
    
    private static void syncConditions(SQLiteDatabase db, JSONArray conditions)
    {
    	String table = ConditionTable.NAME;
    	Map<String, String> names = new HashMap<String, String>();
    	Map<String, DataType> types = new HashMap<String, DataType>();

    	//fields
    	names.put("id", ConditionTable.Fields.CONDITION_ID);
    	types.put("id", DataType.LONG);
    	names.put("branch_id", ConditionTable.Fields.BRANCH_ID);
    	types.put("branch_id", DataType.LONG);
    	names.put("question_id", ConditionTable.Fields.QUESTION_ID);
    	types.put("question_id", DataType.LONG);
    	names.put("type", ConditionTable.Fields.TYPE);
    	types.put("type", DataType.INT);
    	names.put("scope", ConditionTable.Fields.SCOPE);
    	types.put("scope", DataType.INT);
    	names.put("data_type", ConditionTable.Fields.DATA_TYPE);
    	types.put("data_type", DataType.INT);
    	
    	syncTable(db, table, names, types, conditions);
    }
    
    private static void syncBranches(SQLiteDatabase db, JSONArray branches)
    {
    	String table = BranchTable.NAME;
    	Map<String, String> names = new HashMap<String, String>();
    	Map<String, DataType> types = new HashMap<String, DataType>();

    	//fields
    	names.put("id", BranchTable.Fields.BRANCH_ID);
    	types.put("id", DataType.LONG);
    	names.put("question_id", BranchTable.Fields.QUESTION_ID);
    	types.put("question_id", DataType.LONG);
    	names.put("next_q", BranchTable.Fields.NEXT_Q);
    	types.put("next_q", DataType.LONG);
    	
    	syncTable(db, table, names, types, branches);
    }
    
    //this doesn't use the syncTable method because all it's fields are so strange
    private static void syncConfig(SQLiteDatabase db, JSONObject config, Context ctxt)
    {
    	Util.i(null, TAG, "Updating configuration values");
    	try
    	{ 		
    		//note that this requires the keys in the incoming JSON
    		//to be the same as those defined in the code on this side
    		JSONArray cNames = config.names();
    		if (cNames == null) return;
    		for (int i = 0; i < cNames.length(); i++)
    		{
    			String key = cNames.getString(i);
    			Util.v(null, TAG, "Current key: " + key);
    			Config.putSetting(ctxt, key, config.getString(key));
    		}
    	}
    	catch (JSONException e)
    	{
    		Util.e(null, TAG, Util.fmt(e));
    	}
    }
}
