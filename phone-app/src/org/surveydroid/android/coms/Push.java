/*---------------------------------------------------------------------------*
 * Push.java                                                                 *
 *                                                                           *
 * Contains methods to push data from the phone to the website.              *
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

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;

import org.surveydroid.android.Config;
import org.surveydroid.android.Util;
import org.surveydroid.android.database.ComsDBHandler;
import org.surveydroid.android.database.SurveyDroidDB;

/**
 * Extension of {@link WebClient} that pushes data up to the server
 *
 * @author Tony Xaio
 * @author Austin Walker
 */
public class Push
{
	//logging tag
	private static final String TAG = "Push";

	private static final String PUSH_URL = "/api/push/";

    /**
     * Push all un-uploaded survey answers in the phone database to the server.
     * Once successfully, each pushed answer will be marked as uploaded in the
     * database.
     *
     * @param ctxt - the current {@link Context}
     *
     * @return true if all the answers has been successfully pushed
     */
    public static boolean pushAnswers(Context ctxt)
    {
        Util.i(null, TAG, "Pushing answers to server");
        
        TelephonyManager tManager =
        	(TelephonyManager) ctxt.getSystemService(
        			Context.TELEPHONY_SERVICE);
    	String uid = tManager.getDeviceId();
    	
    	if (uid == null)
    	{
    		Util.w(null, TAG, "Device ID not available");
    		Util.w(null, TAG, "Will reschedule and try again later");
    		return false;
    	}
    	
        try
        {
        	ComsDBHandler cdbh = new ComsDBHandler(ctxt);
            cdbh.open();
            Cursor answers = cdbh.getNewAnswers();

            JSONArray answersJSON = new JSONArray();

            Util.d(null, TAG, "# of answer to push : "
            		+ answers.getCount());
            answers.moveToFirst();

            if (answers.getCount() == 0)
            {
            	answers.close();
                cdbh.close();
            	return true;
            }
            int[] uploadedIDs = new int[answers.getCount()];
            int index = 0;

            while (!answers.isAfterLast())
            {
                JSONObject ans = new JSONObject();
                ans.put(SurveyDroidDB.AnswerTable.ANS_TYPE, answers.getInt(
                		answers.getColumnIndexOrThrow(
                				SurveyDroidDB.AnswerTable.ANS_TYPE)));
                ans.put(SurveyDroidDB.AnswerTable.CREATED, answers.getLong(
                		answers.getColumnIndexOrThrow(
                				SurveyDroidDB.AnswerTable.CREATED)));
                ans.put(SurveyDroidDB.AnswerTable.QUESTION_ID, answers.getInt(
                		answers.getColumnIndexOrThrow(
                				SurveyDroidDB.AnswerTable.QUESTION_ID)));

                //now sort what gets uploaded based on the answer type
                switch (answers.getInt(
                		answers.getColumnIndexOrThrow(
                				SurveyDroidDB.AnswerTable.ANS_TYPE)))
                {
                case SurveyDroidDB.AnswerTable.CHOICE:
                    ans.put(SurveyDroidDB.AnswerTable.CHOICE_IDS,
                    		answers.getString(
                    		answers.getColumnIndexOrThrow(
                    				SurveyDroidDB.AnswerTable.CHOICE_IDS)));
                	break;
                case SurveyDroidDB.AnswerTable.VALUE:
                	ans.put(SurveyDroidDB.AnswerTable.ANS_VALUE, answers.getInt(
                    		answers.getColumnIndexOrThrow(
                    				SurveyDroidDB.AnswerTable.ANS_VALUE)));
                	break;
                case SurveyDroidDB.AnswerTable.TEXT:
                    ans.put(SurveyDroidDB.AnswerTable.ANS_TEXT, answers.getString(
                    		answers.getColumnIndexOrThrow(
                    				SurveyDroidDB.AnswerTable.ANS_TEXT)));
                	break;
                default:
                	throw new RuntimeException("Unknown answer type: "
                			+ answers.getInt(
                    		answers.getColumnIndexOrThrow(
                    				SurveyDroidDB.AnswerTable.ANS_TYPE)));
                }
                answersJSON.put(ans);
                uploadedIDs[index] = answers.getInt(
                		answers.getColumnIndexOrThrow(
                				SurveyDroidDB.AnswerTable._ID));
                index++;
                answers.moveToNext();
            }
            answers.close();
            cdbh.close();

            // now send to actual server
            JSONObject data = new JSONObject();

            data.put("answers", answersJSON);
            Util.d(null, TAG, data.toString());
            boolean success = WebClient.postJsonToUrl(ctxt, getPushURL(ctxt)
            		+ uid, data.toString());

            // mark answers as uploaded if appropriate
            if (success)
            {
            	cdbh.open();
                for (int i = 0; i < uploadedIDs.length; i++)
                {
                    cdbh.updateAnswer(uploadedIDs[i]);
                }
                cdbh.close();
            }
            return success;
        }
        catch (Exception e)
        {
            Util.e(null, TAG, Util.fmt(e));
            if (Config.D)
            	throw new RuntimeException("FATAL ERROR", e);
        }
        return false;
    }
    
    /**
     * Push all data about survey completion in the phone's database to the
     * server.  Once this is done, some of the data may be deleted.  The amount
     * deleted is determined by {@link Config#COMPLETION_SAMPLE}; enough will
     * be kept to fulfill that sample size.
     * 
     * @param ctx - the current {@link Context}
     * @return true on success
     */
    public static boolean pushCompletionData(Context ctx)
    {
    	Util.i(null, TAG, "Pushing survey completion data to server");
    	
    	TelephonyManager tManager =
        	(TelephonyManager) ctx.getSystemService(
        			Context.TELEPHONY_SERVICE);
    	String uid = tManager.getDeviceId();
    	
    	if (uid == null)
    	{
    		Util.w(null, TAG, "Device ID not available");
    		Util.w(null, TAG, "Will reschedule and try again later");
    		return false;
    	}
    	
        try
        {
            ComsDBHandler cdbh = new ComsDBHandler(ctx);
            cdbh.open();
            Cursor compData = cdbh.getNewCompletionData();

            JSONArray recordsJSON = new JSONArray();

            Util.d(null, "Push", "# of results to push : "
            		+ compData.getCount());

            if (compData.getCount() == 0)
            {
            	compData.close();
                cdbh.close();
            	return true;
            }

            compData.moveToFirst();
            int numRecords = 0;
            int[] uploadedIDs = new int[compData.getCount()];
            while (!compData.isAfterLast())
            {
                JSONObject item = new JSONObject();
                item.put(SurveyDroidDB.TakenTable.SURVEY_ID, compData.getDouble(
                		compData.getColumnIndexOrThrow(
                				SurveyDroidDB.TakenTable.SURVEY_ID)));
                item.put(SurveyDroidDB.TakenTable.STATUS, compData.getDouble(
                		compData.getColumnIndexOrThrow(
                				SurveyDroidDB.TakenTable.STATUS)));
                item.put(SurveyDroidDB.TakenTable.CREATED, compData.getDouble(
                		compData.getColumnIndexOrThrow(
                				SurveyDroidDB.TakenTable.CREATED)));
                item.put(SurveyDroidDB.TakenTable.RATE, compData.getDouble(
                		compData.getColumnIndexOrThrow(
                				SurveyDroidDB.TakenTable.RATE)));
                recordsJSON.put(item);
                uploadedIDs[numRecords] = compData.getInt(
                		compData.getColumnIndexOrThrow(
                				SurveyDroidDB.TakenTable._ID));
                compData.moveToNext();
                numRecords++;
            }
            compData.close();
            cdbh.close();

            // now send to actual server
            JSONObject data = new JSONObject();

            data.put("surveysTaken", recordsJSON);
            Util.d(null, TAG, data.toString());
            boolean success = WebClient.postJsonToUrl(ctx, getPushURL(ctx)
            		+ uid, data.toString());

            // delete records if appropriate
            if (success)
            {
            	cdbh.open();
                for (int i = uploadedIDs.length - 1; i >= 0; i--)
                {
                	cdbh.delCompletionRecord(uploadedIDs[i]);
                }
                cdbh.close();
            }
            return success;
        }
        catch (Exception e)
        {
            Util.e(null, TAG, Util.fmt(e));
        }
        return false;
    }

    /**
     * Push all locations in the phone database to the server. Once
     * successfully, each pushed location will be removed from the database.
     *
     * @param ctxt - the current {@link Context}
     *
     * @return true if all the locations has been successfully pushed
     */
    public static boolean pushLocations(Context ctx)
    {
        Util.i(null, TAG, "Pushing locations to server");
        
        TelephonyManager tManager =
        	(TelephonyManager) ctx.getSystemService(
        			Context.TELEPHONY_SERVICE);
    	String uid = tManager.getDeviceId();
    	
    	if (uid == null)
    	{
    		Util.w(null, TAG, "Device ID not available");
    		Util.w(null, TAG, "Will reschedule and try again later");
    		return false;
    	}
    	
        try
        {
            ComsDBHandler cdbh = new ComsDBHandler(ctx);
            cdbh.open();
            Cursor locations = cdbh.getLocations();

            JSONArray locationsJSON = new JSONArray();

            Util.d(null, "Push", "# of locations to push : "
            		+ locations.getCount());

            if (locations.getCount() == 0)
            {
            	locations.close();
                cdbh.close();
            	return true;
            }
            
            int[] uploadedIDs = new int[locations.getCount()];
            int index = 0;

            locations.moveToFirst();
            while (!locations.isAfterLast())
            {
                JSONObject loc = new JSONObject();
                loc.put(SurveyDroidDB.LocationTable.LONGITUDE, locations.getDouble(
                		locations.getColumnIndexOrThrow(
                				SurveyDroidDB.LocationTable.LONGITUDE)));
                loc.put(SurveyDroidDB.LocationTable.LATITUDE, locations.getDouble(
                		locations.getColumnIndexOrThrow(
                				SurveyDroidDB.LocationTable.LATITUDE)));
                loc.put(SurveyDroidDB.LocationTable.ACCURACY, locations.getDouble(
                		locations.getColumnIndexOrThrow(
                				SurveyDroidDB.LocationTable.ACCURACY)));
                loc.put("created", locations.getLong(
                		locations.getColumnIndexOrThrow(
                				SurveyDroidDB.LocationTable.TIME)));
                locationsJSON.put(loc);
                uploadedIDs[index] = locations.getInt(
                		locations.getColumnIndexOrThrow(
                				SurveyDroidDB.LocationTable._ID));
                index++;
                locations.moveToNext();
            }
            locations.close();
            cdbh.close();

            // now send to actual server
            JSONObject data = new JSONObject();

            data.put("locations", locationsJSON);
            Util.d(null, TAG, data.toString());
            boolean success = WebClient.postJsonToUrl(ctx, getPushURL(ctx)
            		+ uid, data.toString());

            // delete locations if appropriate
            if (success)
            {
            	cdbh.open();
                for (int i = 0; i < uploadedIDs.length; i++)
                {
                    cdbh.delLocation(uploadedIDs[i]);
                }
                cdbh.close();
            }
            return success;
        }
        catch (Exception e)
        {
           Util.e(null, TAG, Util.fmt(e));
        }
        return false;
    }

    /**
     * Push all un-uploaded call logs in the phone database to the server
     * Phone number is hashed before sending to the server to preserve privacy
     * Once successful, each call log will be deleted from the database.
     *
     * @param ctxt - the current {@link Context}
     *
     * @return true if push was successful
     */
    public static boolean pushCallLog(Context ctx)
    {
        Util.i(null, TAG, "Pushing calllog to server");
        
        TelephonyManager tManager =
        	(TelephonyManager) ctx.getSystemService(
        			Context.TELEPHONY_SERVICE);
    	String uid = tManager.getDeviceId();
    	
    	if (uid == null)
    	{
    		Util.w(null, TAG, "Device ID not available");
    		Util.w(null, TAG, "Will reschedule and try again later");
    		return false;
    	}
    	
        try
        {
            ComsDBHandler cdbh = new ComsDBHandler(ctx);
            cdbh.open();
            Cursor calls = cdbh.getCalls(true);
            JSONArray callsJSON = new JSONArray();

           Util.d(null, TAG, "# of call logs to push : " + calls.getCount());

            if (calls.getCount() == 0)
            {
            	calls.close();
                cdbh.close();
            	return true;
            }

            calls.moveToFirst();
            while (!calls.isAfterLast())
            {
                JSONObject log = new JSONObject();
                log.put(SurveyDroidDB.CallLogTable.CALL_TYPE, calls.getString(
                		calls.getColumnIndexOrThrow(
                				SurveyDroidDB.CallLogTable.CALL_TYPE)));
                log.put(SurveyDroidDB.CallLogTable.DURATION, calls.getInt(
                		calls.getColumnIndexOrThrow(
                				SurveyDroidDB.CallLogTable.DURATION)));
                log.put("created", calls.getLong(
                		calls.getColumnIndexOrThrow(
                				SurveyDroidDB.CallLogTable.TIME)));
                log.put("contact_id", hash(calls.getString(
                		calls.getColumnIndexOrThrow(
                				SurveyDroidDB.CallLogTable.PHONE_NUMBER)), ctx));
                callsJSON.put(log);
                calls.moveToNext();
            }
            calls.close();
            cdbh.close();

            // now send to actual server
            JSONObject data = new JSONObject();

            data.put("calls", callsJSON);
            Util.d(null, TAG, data.toString());
            boolean success = WebClient.postJsonToUrl(ctx, getPushURL(ctx)
            		+ uid, data.toString());

            // delete calls if appropriate
            if (success)
            {
            	cdbh.open();
                cdbh.delDuplicateCalls();
                cdbh.close();
            }
            return success;
        }
        catch (Exception e)
        {
            Util.e(null, TAG, Util.fmt(e));
        }
        return false;
    }
    
    /**
     * Push all un-uploaded application status change record to the server.
     * Once successful, the pushed records are deleted from the database.
     *
     * @param ctxt - the current {@link Context}
     *
     * @return true if push was successful
     */
    public static boolean pushStatusData(Context ctx)
    {
    	Util.i(null, TAG, "Pushing status data to server");
    	
    	TelephonyManager tManager =
        	(TelephonyManager) ctx.getSystemService(
        			Context.TELEPHONY_SERVICE);
    	String uid = tManager.getDeviceId();
    	
    	if (uid == null)
    	{
    		Util.w(null, TAG, "Device ID not available");
    		Util.w(null, TAG, "Will reschedule and try again later");
    		return false;
    	}
    	
        try
        {
            ComsDBHandler cdbh = new ComsDBHandler(ctx);
            cdbh.open();
            Cursor records = cdbh.getStatusChanges();
            JSONArray recordsJSON = new JSONArray();

            Util.d(null, TAG, "# of status records to push : "
            			+ records.getCount());

            if (records.getCount() == 0)
            {
            	records.close();
                cdbh.close();
            	return true;
            }
            
            int[] uploadedIDs = new int[records.getCount()];
            int index = 0;

            records.moveToFirst();
            while (!records.isAfterLast())
            {
                JSONObject record = new JSONObject();
                record.put("feature", records.getString(
                		records.getColumnIndexOrThrow(
                				SurveyDroidDB.StatusTable.TYPE)));
                record.put(SurveyDroidDB.StatusTable.STATUS, records.getInt(
                		records.getColumnIndexOrThrow(
                				SurveyDroidDB.StatusTable.STATUS)));
                record.put(SurveyDroidDB.StatusTable.CREATED, records.getLong(
                		records.getColumnIndexOrThrow(
                				SurveyDroidDB.StatusTable.CREATED)));
                recordsJSON.put(record);
                uploadedIDs[index] = records.getInt(
                		records.getColumnIndexOrThrow(
                				SurveyDroidDB.StatusTable._ID));
                index++;
                records.moveToNext();
            }
            records.close();
            cdbh.close();

            // now send to actual server
            JSONObject data = new JSONObject();

            data.put("statusChanges", recordsJSON);
            Util.v(null, TAG, data.toString());
            boolean success = WebClient.postJsonToUrl(ctx, getPushURL(ctx)
            		+ uid, data.toString());

            // delete records if appropriate
            if (success)
            {
            	cdbh.open();
                for (int i = 0; i < uploadedIDs.length; i++)
                {
                    cdbh.delStatusChange(uploadedIDs[i]);
                }
                cdbh.close();
            }
            return success;
        }
        catch (Exception e)
        {
            Util.e(null, TAG, Util.fmt(e));
        }
        return false;
    }
    
    /**
     * Push all un-uploaded extra data (photos, etc.) to the server.
     * Once successful, the pushed records are deleted from the database.
     *
     * @param ctxt - the current {@link Context}
     *
     * @return true if push was successful
     */
    public static boolean pushExtrasData(Context ctx)
    {
    	Util.i(null, TAG, "Pushing extras data to server");
    	
    	TelephonyManager tManager =
        	(TelephonyManager) ctx.getSystemService(
        			Context.TELEPHONY_SERVICE);
    	String uid = tManager.getDeviceId();
    	
    	if (uid == null)
    	{
    		Util.w(null, TAG, "Device ID not available");
    		Util.w(null, TAG, "Try again later");
    		return false;
    	}
    	
    	try
        {
    		//we have to be very careful here because extras could
    		//be very large items, and we don't want to run out of memory
            ComsDBHandler cdbh = new ComsDBHandler(ctx);
            cdbh.open();
            Cursor records = cdbh.getNextExtra();
            if (records == null)
            {
            	cdbh.close();
            	return true;
            }
            int i = 1;
            while (records != null)
            {
	            JSONArray recordsJSON = new JSONArray();
	
	            Util.d(null, TAG, "pushing extras, round " + i);
	            
	            int uploadedID;
	
	            records.moveToFirst();
                JSONObject record = new JSONObject();
                record.put("data", records.getString(
                		records.getColumnIndexOrThrow(
                				SurveyDroidDB.ExtrasTable.DATA)));
                record.put("type", records.getInt(
                		records.getColumnIndexOrThrow(
                				SurveyDroidDB.ExtrasTable.TYPE)));
                record.put("created", records.getLong(
                		records.getColumnIndexOrThrow(
                				SurveyDroidDB.ExtrasTable.CREATED)));
                recordsJSON.put(record);
                uploadedID = records.getInt(
                		records.getColumnIndexOrThrow(
                				SurveyDroidDB.ExtrasTable._ID));
	            records.close();
	            cdbh.close();
	
	            // now send to actual server
	            JSONObject data = new JSONObject();
	
	            data.put("extras", recordsJSON);
	            record = null;
	            boolean success = WebClient.postJsonToUrl(ctx, getPushURL(ctx)
	            		+ uid, data.toString());
	            data = null;
	
	            // delete records if appropriate
	            if (!success) return false;
            	cdbh.open();
                cdbh.delExtra(uploadedID);
                records = cdbh.getNextExtra();
                cdbh.close();
                i++;
            }
            return true;
        }
        catch (Exception e)
        {
            Util.e(null, TAG, Util.fmt(e));
        }
        return false;
    }
    
    //get's the full push url
    private static String getPushURL(Context c)
    {
    	StringBuilder url = new StringBuilder();
    	if (Config.getSetting(c, Config.HTTPS, Config.HTTPS_DEFAULT))
    		url.append("https://");
    	else
    		url.append("http://");
    	url.append(Config.getSetting(c, Config.SERVER, Config.SERVER_DEFAULT));
    	url.append(PUSH_URL);
    	return url.toString();
    }

    //salt and hash (for phone numbers)
    private static Integer hash(String s, Context ctxt)
    {
    	if (s == null) return null;
    	String salted = s + Config.getSetting(ctxt, Config.SALT, "");
    	return salted.hashCode();
    }
}
