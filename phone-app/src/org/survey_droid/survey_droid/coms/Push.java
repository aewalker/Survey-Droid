/*---------------------------------------------------------------------------*
 * Push.java                                                                 *
 *                                                                           *
 * Contains methods to push data from the phone to the website.              *
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

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;

import org.survey_droid.survey_droid.Base64Coder;
import org.survey_droid.survey_droid.Config;
import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.annotation.ConfigKey;
import org.survey_droid.survey_droid.content.ComsDBHandler;
import org.survey_droid.survey_droid.content.ProviderContract.*;

/**
 * Extension of {@link WebClient} that pushes data up to the server
 *
 * @author Tony Xaio
 * @author Austin Walker
 */
public class Push
{
	/** logging tag */
	private static final String TAG = "Push";

	/** Frequency with which to push data (in minutes) */
	@ConfigKey("" + 60 * 24)
	public static final String PUSH_INTERVAL = "push_interval";

	/** url suffix for the push operation */
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
        	ComsDBHandler cdbh = new ComsDBHandler(ctxt, 0);
            Cursor answers = cdbh.getNewAnswers();
            JSONArray answersJSON = new JSONArray();

            Util.d(null, TAG, "# of answer to push : "
            		+ answers.getCount());
            answers.moveToFirst();

            if (answers.getCount() == 0)
            {
            	answers.close();
            	return true;
            }
            int[] uploadedIDs = new int[answers.getCount()];
            int index = 0;

            while (!answers.isAfterLast())
            {
                JSONObject ans = new JSONObject();
                ans.put(AnswerTable.Fields.CREATED, answers.getLong(
                		answers.getColumnIndexOrThrow(
                				AnswerTable.Fields.CREATED)));
                ans.put(AnswerTable.Fields.QUESTION_ID, answers.getLong(
                		answers.getColumnIndexOrThrow(
                				AnswerTable.Fields.QUESTION_ID)));

                //now sort what gets uploaded based on the answer type
                //FIXME need to make a String I think
                ans.put(AnswerTable.Fields.ANS_VALUE, Base64Coder.encode(
                	answers.getBlob(answers.getColumnIndexOrThrow(
                		AnswerTable.Fields.ANS_VALUE))));
                answersJSON.put(ans);
                uploadedIDs[index] = answers.getInt(
                		answers.getColumnIndexOrThrow(
                				AnswerTable.Fields._ID));
                index++;
                answers.moveToNext();
            }
            answers.close();

            // now send to actual server
            JSONObject data = new JSONObject();

            data.put("answers", answersJSON);
            Util.d(null, TAG, data.toString());
            boolean success = WebClient.postJsonToUrl(ctxt, getPushURL(ctxt)
            		+ uid, data.toString());

            // mark answers as uploaded if appropriate
            if (success)
            {
                for (int i = 0; i < uploadedIDs.length; i++)
                {
                    //cdbh.updateAnswer(uploadedIDs[i]); //FIXME
                }
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
            ComsDBHandler cdbh = new ComsDBHandler(ctx, 0);
            Cursor compData = cdbh.getNewCompletionData();

            JSONArray recordsJSON = new JSONArray();

            Util.d(null, "Push", "# of results to push : "
            		+ compData.getCount());

            if (compData.getCount() == 0)
            {
            	compData.close();
            	return true;
            }

            compData.moveToFirst();
            int numRecords = 0;
            long[] uploadedIDs = new long[compData.getCount()];
            while (!compData.isAfterLast())
            {
                JSONObject item = new JSONObject();
                item.put(SurveysTakenTable.Fields.SURVEY_ID, compData.getLong(
                		compData.getColumnIndexOrThrow(
                				SurveysTakenTable.Fields.SURVEY_ID)));
                item.put(SurveysTakenTable.Fields.STATUS, compData.getInt(
                		compData.getColumnIndexOrThrow(
                				SurveysTakenTable.Fields.STATUS)));
                item.put(SurveysTakenTable.Fields.CREATED, compData.getLong(
                		compData.getColumnIndexOrThrow(
                				SurveysTakenTable.Fields.CREATED)));
                item.put(SurveysTakenTable.Fields.RATE, compData.getInt(
                		compData.getColumnIndexOrThrow(
                				SurveysTakenTable.Fields.RATE)));
                recordsJSON.put(item);
                uploadedIDs[numRecords] = compData.getLong(
                		compData.getColumnIndexOrThrow(
                				SurveysTakenTable.Fields._ID));
                compData.moveToNext();
                numRecords++;
            }
            compData.close();

            // now send to actual server
            JSONObject data = new JSONObject();

            data.put("surveysTaken", recordsJSON);
            Util.d(null, TAG, data.toString());
            boolean success = WebClient.postJsonToUrl(ctx, getPushURL(ctx)
            		+ uid, data.toString());

            // delete records if appropriate
            if (success)
            {
                for (int i = uploadedIDs.length - 1; i >= 0; i--)
                {
                	cdbh.delCompletionRecord(uploadedIDs[i]);
                }
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
            ComsDBHandler cdbh = new ComsDBHandler(ctx, 0);
            Cursor locations = cdbh.getLocations();

            JSONArray locationsJSON = new JSONArray();

            Util.d(null, "Push", "# of locations to push : "
            		+ locations.getCount());

            if (locations.getCount() == 0)
            {
            	locations.close();
            	return true;
            }
            
            int[] uploadedIDs = new int[locations.getCount()];
            int index = 0;

            locations.moveToFirst();
            while (!locations.isAfterLast())
            {
                JSONObject loc = new JSONObject();
                loc.put(LocationTable.Fields.LONGITUDE, locations.getDouble(
                		locations.getColumnIndexOrThrow(
                				LocationTable.Fields.LONGITUDE)));
                loc.put(LocationTable.Fields.LATITUDE, locations.getDouble(
                		locations.getColumnIndexOrThrow(
                				LocationTable.Fields.LATITUDE)));
                loc.put(LocationTable.Fields.ACCURACY, locations.getDouble(
                		locations.getColumnIndexOrThrow(
                				LocationTable.Fields.ACCURACY)));
                loc.put("created", locations.getLong(
                		locations.getColumnIndexOrThrow(
                				LocationTable.Fields.TIME)));
                locationsJSON.put(loc);
                uploadedIDs[index] = locations.getInt(
                		locations.getColumnIndexOrThrow(
                				LocationTable.Fields._ID));
                index++;
                locations.moveToNext();
            }
            locations.close();

            // now send to actual server
            JSONObject data = new JSONObject();

            data.put("locations", locationsJSON);
            Util.d(null, TAG, data.toString());
            boolean success = WebClient.postJsonToUrl(ctx, getPushURL(ctx)
            		+ uid, data.toString());

            // delete locations if appropriate
            if (success)
            {
                for (int i = 0; i < uploadedIDs.length; i++)
                {
                    cdbh.delLocation(uploadedIDs[i]);
                }
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
            ComsDBHandler cdbh = new ComsDBHandler(ctx, 0);
            Cursor calls = cdbh.getContacts(true);
            JSONArray callsJSON = new JSONArray();

           Util.d(null, TAG, "# of call logs to push : " + calls.getCount());

            if (calls.getCount() == 0)
            {
            	calls.close();
            	return true;
            }

            calls.moveToFirst();
            while (!calls.isAfterLast())
            {
                JSONObject log = new JSONObject();
                log.put(CallLogTable.Fields.CALL_TYPE, calls.getString(
                		calls.getColumnIndexOrThrow(
                				CallLogTable.Fields.CALL_TYPE)));
                log.put(CallLogTable.Fields.DURATION, calls.getInt(
                		calls.getColumnIndexOrThrow(
                				CallLogTable.Fields.DURATION)));
                log.put("created", calls.getLong(
                		calls.getColumnIndexOrThrow(
                				CallLogTable.Fields.TIME)));
                log.put("contact_id", hash(calls.getString(
                		calls.getColumnIndexOrThrow(
                				CallLogTable.Fields.PHONE_NUMBER)), ctx));
                callsJSON.put(log);
                calls.moveToNext();
            }
            calls.close();

            // now send to actual server
            JSONObject data = new JSONObject();

            data.put("calls", callsJSON);
            Util.d(null, TAG, data.toString());
            boolean success = WebClient.postJsonToUrl(ctx, getPushURL(ctx)
            		+ uid, data.toString());

            // delete calls if appropriate
            if (success)
            {
                cdbh.delDuplicateCalls();
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
            ComsDBHandler cdbh = new ComsDBHandler(ctx, 0);
            Cursor records = cdbh.getStatusChanges();
            JSONArray recordsJSON = new JSONArray();

            Util.d(null, TAG, "# of status records to push : "
            			+ records.getCount());

            if (records.getCount() == 0)
            {
            	records.close();
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
                				StatusTable.Fields.FEATURE)));
                record.put(StatusTable.Fields.STATUS, records.getInt(
                		records.getColumnIndexOrThrow(
                				StatusTable.Fields.STATUS)));
                record.put(StatusTable.Fields.CREATED, records.getLong(
                		records.getColumnIndexOrThrow(
                				StatusTable.Fields.CREATED)));
                recordsJSON.put(record);
                uploadedIDs[index] = records.getInt(
                		records.getColumnIndexOrThrow(
                				StatusTable.Fields._ID));
                index++;
                records.moveToNext();
            }
            records.close();

            // now send to actual server
            JSONObject data = new JSONObject();

            data.put("statusChanges", recordsJSON);
            Util.v(null, TAG, data.toString());
            boolean success = WebClient.postJsonToUrl(ctx, getPushURL(ctx)
            		+ uid, data.toString());

            // delete records if appropriate
            if (success)
            {
                for (int i = 0; i < uploadedIDs.length; i++)
                {
                    cdbh.delStatusChange(uploadedIDs[i]);
                }
            }
            return success;
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
    	if (Config.getBoolean(c, ComsService.HTTPS))
    		url.append("https://");
    	else
    		url.append("http://");
    	url.append(Config.getString(c, ComsService.SERVER));
    	url.append(PUSH_URL);
    	return url.toString();
    }

    //salt and hash (for phone numbers)
    private static Integer hash(String s, Context ctxt)
    {
    	if (s == null) return null;
    	String salted = s + Config.getString(ctxt, Config.SALT);
    	return salted.hashCode();
    }
}
