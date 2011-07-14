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

	private static final String PUSH_URL = "/answers/push/";

    /**
     * Push all un-uploaded survey answers in the phone database to the server.
     * Once successfully, each pushed answer will be marked as uploaded in the
     * database.
     *
     * @param ctxt - the current Context
     *
     * @return true if all the answers has been successfully pushed
     */
    public static boolean pushAnswers(Context ctxt)
    {
        Log.i(TAG, "Pushing answers to server");
        try
        {
        	ComsDBHandler cdbh = new ComsDBHandler(ctxt);
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
                ans.put(PeoplesDB.AnswerTable.ANS_TYPE, answers.getInt(
                		answers.getColumnIndexOrThrow(
                				PeoplesDB.AnswerTable.ANS_TYPE)));
                ans.put(PeoplesDB.AnswerTable.CREATED, answers.getLong(
                		answers.getColumnIndexOrThrow(
                				PeoplesDB.AnswerTable.CREATED)));
                ans.put(PeoplesDB.AnswerTable.QUESTION_ID, answers.getInt(
                		answers.getColumnIndexOrThrow(
                				PeoplesDB.AnswerTable.QUESTION_ID)));

                //now sort what gets uploaded based on the answer type
                switch (answers.getInt(
                		answers.getColumnIndexOrThrow(
                				PeoplesDB.AnswerTable.ANS_TYPE)))
                {
                case PeoplesDB.AnswerTable.CHOICE:
                    ans.put(PeoplesDB.AnswerTable.CHOICE_IDS, answers.getInt(
                    		answers.getColumnIndexOrThrow(
                    				PeoplesDB.AnswerTable.CHOICE_IDS)));
                	break;
                case PeoplesDB.AnswerTable.VALUE:
                	ans.put(PeoplesDB.AnswerTable.ANS_VALUE, answers.getInt(
                    		answers.getColumnIndexOrThrow(
                    				PeoplesDB.AnswerTable.ANS_VALUE)));
                	break;
                case PeoplesDB.AnswerTable.TEXT:
                    ans.put(PeoplesDB.AnswerTable.ANS_TEXT, answers.getString(
                    		answers.getColumnIndexOrThrow(
                    				PeoplesDB.AnswerTable.ANS_TEXT)));
                	break;
                default:
                	throw new RuntimeException("Unknown answer type: "
                			+ answers.getInt(
                    		answers.getColumnIndexOrThrow(
                    				PeoplesDB.AnswerTable.ANS_TYPE)));
                }
                answersJSON.put(ans);
                answers.moveToNext();
            }
            answers.close();
            cdbh.close();

            // now send to actual server
            JSONObject data = new JSONObject();

            TelephonyManager tManager =
            	(TelephonyManager) ctxt.getSystemService(
            			Context.TELEPHONY_SERVICE);
        	String uid = tManager.getDeviceId();

            //data.put("deviceId", uid); //moved to url
            data.put("answers", answersJSON);
            if (Config.D) Log.d(TAG, data.toString());
            boolean success = postJsonToUrl(ctxt, getPushURL(ctxt)
            		+ uid, data.toString());

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
                loc.put(PeoplesDB.LocationTable.LONGITUDE, locations.getDouble(
                		locations.getColumnIndexOrThrow(
                				PeoplesDB.LocationTable.LONGITUDE)));
                loc.put(PeoplesDB.LocationTable.LATITUDE, locations.getDouble(
                		locations.getColumnIndexOrThrow(
                				PeoplesDB.LocationTable.LATITUDE)));
                loc.put(PeoplesDB.LocationTable.ACCURACY, locations.getDouble(
                		locations.getColumnIndexOrThrow(
                				PeoplesDB.LocationTable.ACCURACY)));
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

            data.put("locations", locationsJSON);
            if (Config.D) Log.d(TAG, data.toString());
            boolean success = postJsonToUrl(ctx, getPushURL(ctx)
            		+ uid, data.toString());

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
     * Once successful, each call log will be deleted from the database.
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
                				PeoplesDB.CallLogTable.PHONE_NUMBER)), ctx));
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

            data.put("calls", callsJSON);
            if (Config.D) Log.d(TAG, data.toString());
            boolean success = postJsonToUrl(ctx, getPushURL(ctx)
            		+ uid, data.toString());

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
    
    /**
     * Push all un-uploaded application status change record to the server.
     * Once successful, the pushed records are deleted from the database.
     *
     * @param ctxt - the current Context
     *
     * @return true if push was successful
     */
    public static boolean pushStatusData(Context ctx)
    {
    	Log.i(TAG, "Pushing status data to server");
        try
        {
            ComsDBHandler cdbh = new ComsDBHandler(ctx);
            cdbh.openRead();
            Cursor records = cdbh.getStatusChanges();
            JSONArray recordsJSON = new JSONArray();

            if (Config.D)
            	Log.d(TAG, "# of status records to push : "
            			+ records.getCount());

            if (records.getCount() == 0)
            {
            	records.close();
                cdbh.close();
            	return true;
            }

            records.moveToFirst();
            while (!records.isAfterLast())
            {
                JSONObject record = new JSONObject();
                record.put(PeoplesDB.StatusTable.TYPE, records.getString(
                		records.getColumnIndexOrThrow(
                				PeoplesDB.StatusTable.TYPE)));
                record.put(PeoplesDB.StatusTable.STATUS, records.getInt(
                		records.getColumnIndexOrThrow(
                				PeoplesDB.StatusTable.STATUS)));
                record.put(PeoplesDB.StatusTable.CREATED, records.getLong(
                		records.getColumnIndexOrThrow(
                				PeoplesDB.StatusTable.CREATED)));
                recordsJSON.put(record);
                records.moveToNext();
            }
            records.close();
            cdbh.close();

            // now send to actual server
            JSONObject data = new JSONObject();

            TelephonyManager tManager =
            	(TelephonyManager) ctx.getSystemService(
            			Context.TELEPHONY_SERVICE);
        	String uid = tManager.getDeviceId();

            data.put("statusChanges", recordsJSON);
            if (Config.D) Log.v(TAG, data.toString());
            boolean success = postJsonToUrl(ctx, getPushURL(ctx)
            		+ uid, data.toString());

            // delete records if appropriate
            if (success)
            {
            	cdbh.openWrite();
                for (int i = 0; i < recordsJSON.length(); i++)
                {
                    JSONObject log = recordsJSON.getJSONObject(i);
                    cdbh.delStatusChange(
                    		log.getInt(PeoplesDB.StatusTable._ID));
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
    	String salted = s + Config.getSetting(ctxt, Config.SALT, 0);
    	return salted.hashCode();
    }
}
