/*---------------------------------------------------------------------------*
 * Pull.java                                                                 *
 *                                                                           *
 * Contains methods to pull data from the website.                           *
 *---------------------------------------------------------------------------*
 * Copyright 2011 Sema Berkiten, Vladimir Costescu, Henry Liu, Diego Vargas, *
 * Austin Walker, and Tony Xiao                                              *
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
package org.surveydroid.android.coms;

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
import android.media.MediaRecorder;
import android.telephony.TelephonyManager;

import static org.surveydroid.android.database.SurveyDroidDB.*;

import org.surveydroid.android.Config;
import org.surveydroid.android.LocationTrackerService;
import org.surveydroid.android.Util;
import org.surveydroid.android.coms.WebClient;
import org.surveydroid.android.coms.WebClient.ApiException;
import org.surveydroid.android.database.SurveyDroidDB;

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
	//logging tag
	private static final String TAG = "Pull";

	//pull address to be appended to the server
    private static final String PULL_URL = "/api/pull/";
    
    private static Context c;
    
    //data types - see syncTable
    private static final int INT = 0;
    private static final int LONG = 1;
    private static final int DOUBLE = 2;
    private static final int STRING = 3;

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
    		if (Config.getSetting(ctxt, Config.HTTPS, Config.HTTPS_DEFAULT))
    			url.append("https://");
    		else
    			url.append("http://");
    		url.append(Config.getSetting(ctxt,
    				Config.SERVER, Config.SERVER_DEFAULT));
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
	    			Util.e(ctxt, TAG,
	    					"Unable to communicate with remote server");
	    			try
	    			{
	    				ApiException apiE = (ApiException) e;
	    				Util.e(ctxt, TAG, "Reason: " + apiE.getMessage());
	    				Util.e(null, TAG, "Make sure this device is registered"
	    						+ " and the server is working");
	    			}
	    			catch (Exception unknownE)
	    			{
	    				Util.e(ctxt, TAG, "Unkown Reason: " + Util.fmt(e));
	    			}
	    			return;
	    		}
	            SurveyDroidDB pdb = new SurveyDroidDB(ctxt);
	            SQLiteDatabase sdb = pdb.getWritableDatabase();
	            //it's important that config be the first thing
	            syncConfig(sdb, json.getJSONObject("config"), ctxt);
	            syncSurveys(sdb, json.getJSONArray("surveys"));
	            syncQuestions(sdb, json.getJSONArray("questions"));
	            syncChocies(sdb, json.getJSONArray("choices"));
	            syncBranches(sdb, json.getJSONArray("branches"));
	            syncConditions(sdb, json.getJSONArray("conditions"));
	            sdb.close();
	            pdb.close();
        	}
        	else
        	{
        		Util.w(ctxt, TAG, "Device ID not available");
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
        	c = null;
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
    		Map<String, String> fields, Map<String, Integer> types,
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
    		}
    		if (sql.executeInsert() == -1)
    			Util.e(null, TAG, "Insertion failed!");
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
	private static void syncSurveys(SQLiteDatabase db, JSONArray surveys)
    {
    	Util.i(null, TAG, "Syncing surveys table");
    	try
    	{
    		Util.d(c, TAG, "Fetched " + surveys.length() + " surveys");
	    	for (int i = 0 ; i < surveys.length(); i++)
	    	{
	    		JSONObject survey = surveys.getJSONObject(i);
	    		ContentValues values = new ContentValues();
	    		int id = survey.getInt("id");
	    		values.put(SurveyTable._ID, id);
	    		values.put(SurveyTable.NAME,
	    				survey.getString(SurveyTable.NAME));
	    		values.put(SurveyTable.CREATED,
	    				survey.getLong(SurveyTable.CREATED));
	    		values.put(SurveyTable.QUESTION_ID,
	    				survey.getInt(SurveyTable.QUESTION_ID));
	    		String[] opt_fields = {SurveyTable.SUBJECT_INIT,
	    							   SurveyTable.NEW_CALLS,
	    							   SurveyTable.OLD_CALLS,
	    							   SurveyTable.NEW_TEXTS,
	    							   SurveyTable.OLD_TEXTS,
	    							   SurveyTable.MO, SurveyTable.TU,
	    							   SurveyTable.WE, SurveyTable.TH,
	    							   SurveyTable.FR, SurveyTable.SA,
	    							   SurveyTable.SU};
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
			Util.e(c, TAG, Util.fmt(e));
		}
    }
	
    private static void syncQuestions(SQLiteDatabase db, JSONArray questions)
    {
    	String table = SurveyDroidDB.QUESTION_TABLE_NAME;
    	Map<String, String> names = new HashMap<String, String>();
    	Map<String, Integer> types = new HashMap<String, Integer>();

    	//fields
    	names.put("id", QuestionTable._ID);
    	types.put("id", INT);
    	names.put("survey_id", QuestionTable.SURVEY_ID);
    	types.put("survey_id", INT);
    	names.put("q_text", QuestionTable.Q_TEXT);
    	types.put("q_text", STRING);
    	names.put("q_type", QuestionTable.Q_TYPE);
    	types.put("q_type", INT);
    	names.put("q_img_low", QuestionTable.Q_SCALE_IMG_LOW);
    	types.put("q_img_low", STRING);
    	names.put("q_img_high", QuestionTable.Q_SCALE_IMG_HIGH);
    	types.put("q_img_high", STRING);
    	names.put("q_text_low", QuestionTable.Q_SCALE_TEXT_LOW);
    	types.put("q_text_low", STRING);
    	names.put("q_text_high", QuestionTable.Q_SCALE_TEXT_HIGH);
    	types.put("q_text_high", STRING);
    	
    	syncTable(db, table, names, types, questions);
    }
    
    private static void syncConditions(SQLiteDatabase db, JSONArray conditions)
    {
    	String table = SurveyDroidDB.CONDITION_TABLE_NAME;
    	Map<String, String> names = new HashMap<String, String>();
    	Map<String, Integer> types = new HashMap<String, Integer>();

    	//fields
    	names.put("id", ConditionTable._ID);
    	types.put("id", INT);
    	names.put("branch_id", ConditionTable.BRANCH_ID);
    	types.put("branch_id", INT);
    	names.put("question_id", ConditionTable.QUESTION_ID);
    	types.put("question_id", INT);
    	names.put("choice_id", ConditionTable.CHOICE_ID);
    	types.put("choice_id", INT);
    	names.put("type", ConditionTable.TYPE);
    	types.put("type", INT);
    	
    	syncTable(db, table, names, types, conditions);
    }
    
    private static void syncBranches(SQLiteDatabase db, JSONArray branches)
    {
    	String table = SurveyDroidDB.BRANCH_TABLE_NAME;
    	Map<String, String> names = new HashMap<String, String>();
    	Map<String, Integer> types = new HashMap<String, Integer>();

    	//fields
    	names.put("id", BranchTable._ID);
    	types.put("id", INT);
    	names.put("question_id", BranchTable.QUESTION_ID);
    	types.put("question_id", INT);
    	names.put("next_q", BranchTable.NEXT_Q);
    	types.put("next_q", INT);
    	
    	syncTable(db, table, names, types, branches);
    }
    
    private static void syncChocies(SQLiteDatabase db, JSONArray choices)
    {
    	String table = SurveyDroidDB.CHOICE_TABLE_NAME;
    	Map<String, String> names = new HashMap<String, String>();
    	Map<String, Integer> types = new HashMap<String, Integer>();

    	//fields
    	names.put("id", ChoiceTable._ID);
    	types.put("id", INT);
    	names.put("question_id", ChoiceTable.QUESTION_ID);
    	types.put("question_id", INT);
    	names.put("choice_type", ChoiceTable.CHOICE_TYPE);
    	types.put("choice_type", INT);
    	names.put("choice_text", ChoiceTable.CHOICE_TEXT);
    	types.put("choice_text", STRING);
    	names.put("choice_img", ChoiceTable.CHOICE_IMG);
    	types.put("choice_img", STRING);
    	
    	syncTable(db, table, names, types, choices);
    }
    
    //this doesn't use the syncTable method because all it's fields are so strange
    private static void syncConfig(SQLiteDatabase db, JSONObject config, Context ctxt)
    {
    	Util.i(ctxt, TAG, "Updating configuration values");
    	try
    	{
    		//do the special keys first
    		
    		try
    		{
	    		//application features
	    		JSONObject features = config.getJSONObject("features_enabled");
	    		if (features.optString("survey", "off").equals("on"))
	    			Config.putSetting(ctxt, Config.SURVEYS_SERVER, true);
	    		else
	    			Config.putSetting(ctxt, Config.SURVEYS_SERVER, false);
	    		if (features.optString("callog", "off").equals("on"))
	    			Config.putSetting(ctxt, Config.CALL_LOG_SERVER, true);
	    		else
	    			Config.putSetting(ctxt, Config.CALL_LOG_SERVER, false);
	    		if (features.optString("location", "off").equals("on"))
	    			Config.putSetting(ctxt, Config.TRACKING_SERVER, true);
	    		else
	    			Config.putSetting(ctxt, Config.TRACKING_SERVER, false);
	    		config.remove("features_enabled");
			}
			catch (JSONException e)
			{
				Util.w(ctxt, TAG, "No features_enabled");
			}
    		
			try
			{
	    		//location tracked
	    		JSONArray locations = config.getJSONArray("location_tracked");
	    		Config.putSetting(ctxt, Config.NUM_LOCATIONS_TRACKED,
	    				locations.length());
	    		for (int i = 0; i < locations.length(); i++)
	    		{
	    			JSONObject location = locations.getJSONObject(i);
	    			Config.putSetting(ctxt, Config.TRACKED_LONG + i,
	    					(float) location.optDouble("long", 0.0));
	    			Config.putSetting(ctxt, Config.TRACKED_LAT + i,
	    					(float) location.optDouble("lat", 0.0));
	    			Config.putSetting(ctxt, Config.TRACKED_RADIUS + i,
	    					(float) location.optDouble("radius", 0.0));
	    		}
	    		config.remove("location_tracked");
			}
			catch (JSONException e)
			{
				Util.w(ctxt, TAG, "No location_tracked");
				Config.putSetting(ctxt, Config.NUM_LOCATIONS_TRACKED, 0);
			}
    		
			try
			{
	    		//times tracked
	    		JSONArray times = config.getJSONArray("time_tracked");
	    		Config.putSetting(ctxt, LocationTrackerService.TIMES_COALESCED,
	    				false);
	    		Config.putSetting(ctxt, Config.NUM_TIMES_TRACKED,
	    				times.length());
	    		for (int i = 0; i < times.length(); i++)
	    		{
	    			JSONObject time = times.getJSONObject(i);
	    			Config.putSetting(ctxt, Config.TRACKED_START + i,
	    					"" + time.optInt("start", 0));
	    			Config.putSetting(ctxt, Config.TRACKED_END + i,
	    					"" + time.optInt("end", 0));
	    		}
	    		config.remove("time_tracked");
	    		//tell the location tracking service to recalculate
	    		Intent trackingIntent = new Intent(ctxt,
	    				LocationTrackerService.class);
	    		trackingIntent.setAction(
	    				LocationTrackerService.ACTION_START_TRACKING);
	    		ctxt.startService(trackingIntent);
			}
			catch (JSONException e)
			{
				Util.w(ctxt, TAG, "No times_tracked");
				Config.putSetting(ctxt, Config.NUM_TIMES_TRACKED, 0);
			}
    		
    		//voice_format (because it needs to be converted
    		String format = config.optString("voice_format");
    		if (format.equals("mpeg4"))
    			Config.putSetting(ctxt, Config.VOICE_FORMAT,
    					MediaRecorder.OutputFormat.MPEG_4);
    		else if (format.equals("3gp"))
    			Config.putSetting(ctxt, Config.VOICE_FORMAT,
    					MediaRecorder.OutputFormat.THREE_GPP);
    		config.remove("voice_format");
    		
    		//now do the standard ones
    		//note that this requires the keys in the incoming JSON
    		//to be the same as those defined in Config.java
    		JSONArray cNames = config.names();
    		if (cNames == null) return;
    		for (int i = 0; i < cNames.length(); i++)
    		{
    			boolean done = false;
    			String key = cNames.getString(i);
    			Util.v(null, TAG, "Current key: " + key);
    			
    			//check for a string value
    			for (String k : Config.STRINGS)
    			{
    				if (key.equals(k))
    				{
    					Config.putSetting(ctxt, key, config.getString(key));
    					done = true;
    					break;
    				}
    			}
    			if (done) continue;
    			
    			//check for an integer value
    			for (String k : Config.INTS)
    			{
    				if (key.equals(k))
    				{
    					Config.putSetting(ctxt, key, config.getInt(key));
    					done = true;
    					break;
    				}
    			}
    			if (done) continue;
    			
    			//check for a boolean value
    			for (String k : Config.BOOLEANS)
    			{
    				if (key.equals(k))
    				{
						String val = config.getString(key);
						if (val.equals("1") || val.equals("on")
								|| val.equals("enabled"))
	    					Config.putSetting(ctxt, key, true);
						else if (val.equals("0") || val.equals("off")
								|| val.equals("disabled") || val.equals(""))
							Config.putSetting(ctxt, key, false);
						else
							Util.w(null, TAG, "Can't interpret \"" + val
									+ "\" as a boolean, keeping old value");
    					done = true;
    					break;
    				}
    			}
    			if (done) continue;
    			
    			//check for a float value
    			for (String k : Config.FLOATS)
    			{
    				if (key.equals(k))
    				{
    					Config.putSetting(ctxt, key,
    							(float) config.getDouble(key));
    					break;
    				}
    			}
    		}
    	}
    	catch (JSONException e)
    	{
    		Util.e(ctxt, TAG, Util.fmt(e));
    	}
    }
}
