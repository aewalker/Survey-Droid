/*---------------------------------------------------------------------------*
 * Push.java                                                                 *
 *                                                                           *
 * Contains methods to push data from the phone to the website.              *
 *---------------------------------------------------------------------------*/
package org.peoples.android.coms;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.peoples.android.database.PeoplesDB;
import org.peoples.android.database.ComsDBHandler;
import org.peoples.android.Config;

/**
 * Extension of {@link WebClient} that pushes data up to the server
 * 
 * @author Tony Xaio
 * @author Austin Walker
 */
public class Push extends WebClient
{
	//logging tag
	private static final String TAG = "Push";
	
	private static final String PUSH_URL =
		"http://" + Config.SERVER + "/answers/push/";
	
    /**
     * Push all un-uploaded survey answers in the phone database to the server.
     * Once successfully, each pushed answer will be marked as uploaded in the
     * database.
     * 
     * @param ctxt - the current Context
     * 
     * @return true if all the answers has been successfully pushed
     */
    public static boolean pushAnswers(Context ctx)
    {
        Log.i(TAG, "Pushing answers to server");
        try
        {
        	ComsDBHandler cdbh = new ComsDBHandler(ctx);
            cdbh.openRead();
            Cursor answers = cdbh.getNewAnswers();
            
            JSONArray answersJSON = new JSONArray();
            
            if (Config.D) Log.d(TAG, "# of answer to push : "
            		+ answers.getCount());
            answers.moveToFirst();
            
            if (answers.getCount() == 0)
            {
            	answers.close();
                cdbh.close();
            	return true;
            }
            
            while (!answers.isAfterLast())
            {
                JSONObject ans = new JSONObject();
                ans.put(PeoplesDB.AnswerTable._ID, answers.getInt(
                		answers.getColumnIndexOrThrow(
                				PeoplesDB.AnswerTable._ID)));
                ans.put(PeoplesDB.AnswerTable.QUESTION_ID, answers.getInt(
                		answers.getColumnIndexOrThrow(
                				PeoplesDB.AnswerTable.QUESTION_ID)));
                ans.put(PeoplesDB.AnswerTable.ANS_TEXT, answers.getString(
                		answers.getColumnIndexOrThrow(
                				PeoplesDB.AnswerTable.ANS_TEXT)));
                ans.put(PeoplesDB.AnswerTable.CHOICE_ID, answers.getInt(
                		answers.getColumnIndexOrThrow(
                				PeoplesDB.AnswerTable.CHOICE_ID)));
                ans.put(PeoplesDB.AnswerTable.CREATED, answers.getLong(
                		answers.getColumnIndexOrThrow(
                				PeoplesDB.AnswerTable.CREATED)));
                answersJSON.put(ans);
                answers.moveToNext();
            }
            answers.close();
            cdbh.close();
            
            // now send to actual server
            JSONObject data = new JSONObject();
            
            TelephonyManager tManager =
            	(TelephonyManager) ctx.getSystemService(
            			Context.TELEPHONY_SERVICE);
        	String uid = tManager.getDeviceId();
            
            data.put("deviceId", uid);
            data.put("answers", answersJSON);
            if (Config.D) Log.d(TAG, data.toString());
            boolean success = postJsonToUrl(PUSH_URL, data.toString());
            
            // mark answers as uploaded if appropriate
            if (success)
            {
            	cdbh.openWrite();
                for (int i = 0; i < answersJSON.length(); i++)
                {
                    JSONObject ans = answersJSON.getJSONObject(i);
                    cdbh.updateAnswer(ans.getInt(PeoplesDB.AnswerTable._ID));
                }
                cdbh.close();
            }
            return success;
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }
    
    /**
     * Push all GPS Locations in the phone database to the server. Once
     * successfully, each pushed location will be removed from the database.
     * 
     * @param ctxt - the current Context
     * 
     * @return true if all the locations has been successfully pushed
     */
    public static boolean pushLocations(Context ctx)
    {
        Log.i(TAG, "Pushing locations to server");
        try
        {
            ComsDBHandler cdbh = new ComsDBHandler(ctx);
            cdbh.openRead();
            Cursor locations = cdbh.getLocations();
            
            JSONArray locationsJSON = new JSONArray();
            
            if (Config.D) Log.d("Push", "# of locations to push : "
            		+ locations.getCount());
            
            if (locations.getCount() == 0)
            {
            	locations.close();
                cdbh.close();
            	return true;
            }
            
            locations.moveToFirst();
            while (!locations.isAfterLast())
            {
                JSONObject loc = new JSONObject();
                loc.put(PeoplesDB.LocationTable._ID, locations.getInt(
                		locations.getColumnIndexOrThrow(
                				PeoplesDB.LocationTable._ID)));
                loc.put(PeoplesDB.LocationTable.LONGITUDE, locations.getDouble(
                		locations.getColumnIndexOrThrow(
                				PeoplesDB.LocationTable.LONGITUDE)));
                loc.put(PeoplesDB.LocationTable.LATITUDE, locations.getDouble(
                		locations.getColumnIndexOrThrow(
                				PeoplesDB.LocationTable.LATITUDE)));
                loc.put(PeoplesDB.LocationTable.TIME, locations.getLong(
                		locations.getColumnIndexOrThrow(
                				PeoplesDB.LocationTable.TIME)));
                locationsJSON.put(loc);
                locations.moveToNext();
            }
            locations.close();
            cdbh.close();
            
            // now send to actual server
            JSONObject data = new JSONObject();
            
            TelephonyManager tManager =
            	(TelephonyManager) ctx.getSystemService(
            			Context.TELEPHONY_SERVICE);
        	String uid = tManager.getDeviceId();
            data.put("deviceId", uid);
            
            data.put("locations", locationsJSON);
            if (Config.D) Log.d(TAG, data.toString());
            boolean success = postJsonToUrl(PUSH_URL, data.toString());
            
            // delete locations if appropriate
            if (success)
            {
            	cdbh.openWrite();
                for (int i = 0; i < locationsJSON.length(); i++)
                {
                    JSONObject loc = locationsJSON.getJSONObject(i);
                    cdbh.delLocation(loc.getInt(PeoplesDB.LocationTable._ID));
                }
                cdbh.close();
            }
            return success;
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }
    
    /**
     * Push all un-uploaded call logs in the phone database to the server
     * Phone number is hashed before sending to the server to preserve privacy
     * Once successful, each call log will be marked as uploaded in the database
     * 
     * @param ctxt - the current Context
     * 
     * @return true if push was successful
     */
    public static boolean pushCallLog(Context ctx)
    {
        Log.i(TAG, "Pushing calllog to server");
        try
        {
            ComsDBHandler cdbh = new ComsDBHandler(ctx);
            cdbh.openRead();
            Cursor calls = cdbh.getCalls();
            JSONArray callsJSON = new JSONArray();
            
            if (Config.D)
            	Log.d(TAG, "# of call logs to push : " + calls.getCount());
            
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
                log.put(PeoplesDB.CallLogTable._ID, calls.getInt(
                		calls.getColumnIndexOrThrow(
                				PeoplesDB.CallLogTable._ID)));
                log.put(PeoplesDB.CallLogTable.CALL_TYPE, calls.getString(
                		calls.getColumnIndexOrThrow(
                				PeoplesDB.CallLogTable.CALL_TYPE)));
                log.put(PeoplesDB.CallLogTable.DURATION, calls.getInt(
                		calls.getColumnIndexOrThrow(
                				PeoplesDB.CallLogTable.DURATION)));
                log.put(PeoplesDB.CallLogTable.TIME, calls.getLong(
                		calls.getColumnIndexOrThrow(
                				PeoplesDB.CallLogTable.TIME)));
                log.put("contact_id", hash(calls.getString(
                		calls.getColumnIndexOrThrow(
                				PeoplesDB.CallLogTable.PHONE_NUMBER))));
                callsJSON.put(log);
                calls.moveToNext();
            }
            calls.close();
            cdbh.close();
            
            // now send to actual server
            JSONObject data = new JSONObject();
            
            TelephonyManager tManager =
            	(TelephonyManager) ctx.getSystemService(
            			Context.TELEPHONY_SERVICE);
        	String uid = tManager.getDeviceId();
            data.put("deviceId", uid);
            
            data.put("calls", callsJSON);
            if (Config.D) Log.d(TAG, data.toString());
            boolean success = postJsonToUrl(PUSH_URL, data.toString());
            
            // delete calls if appropriate
            if (success)
            {
            	cdbh.openWrite();
                for (int i = 0; i < callsJSON.length(); i++)
                {
                    JSONObject log = callsJSON.getJSONObject(i);
                    cdbh.delCall(log.getInt(PeoplesDB.CallLogTable._ID));
                }
                cdbh.close();
            }
            return success;
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }
    
    //salt and hash (for phone numbers)
    private static Integer hash(String s)
    {
    	if (s == null) return null;
    	String salted = s + Config.SALT;
    	return salted.hashCode();
    }
}
