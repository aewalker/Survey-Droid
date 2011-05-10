package com.peoples.android.server;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.peoples.android.database.PeoplesDB;
//import com.peoples.android.database.SurveyDBHandler;

import static com.peoples.android.database.PeoplesDB.*;

public class Push extends WebClient {

    /**
     * URL to use when pushing user answers
     */
    public static final String PUSH_URL = "http://www.eigendiego.com/cake/app/webroot/answers/push";
    
    
    /**
     * Push all un-uploaded survey answers in the phone database to the server.
     * Once successfully, each pushed answer will be marked as uploaded in the database
     * 
     * @param ctxt - the current Context
     * 
     * @return true if all the answers has been successfully pushed
     */
    public static boolean pushAnswers(Context ctx) {
        Log.d("Push", "Pushing answers to server");
        try {
            PeoplesDB pdb = new PeoplesDB(ctx);
            SQLiteDatabase sdb = pdb.getWritableDatabase();
            
            //set up the query
            String    table    = ANSWER_TABLE_NAME;
            String[]  cols     = {AnswerTable._ID,
                                  AnswerTable.ANS_TEXT,
                                  AnswerTable.CHOICE_ID,
                                  AnswerTable.CREATED,
                                  AnswerTable.QUESTION_ID};
            String    selc     = AnswerTable.UPLOADED + " =  ?";
            String[]  selcArgs = {Integer.toString(0)};
            String    group    = null;
            String    having   = null;
            String    orderBy  = AnswerTable._ID;
            
            //run it and construct json object;
            Cursor a = sdb.query(table, cols, selc, selcArgs, group, having, orderBy);
            JSONArray answers = new JSONArray();
            
            Log.d("Push", "# of answer to push : " + a.getCount());
            
            a.moveToFirst();
            while (!a.isAfterLast()) {
                JSONObject ans = new JSONObject();
                ans.put(AnswerTable._ID, a.getInt(a.getColumnIndexOrThrow(
                        AnswerTable._ID)));
                ans.put(AnswerTable.QUESTION_ID, a.getInt(a.getColumnIndexOrThrow(
                        AnswerTable.QUESTION_ID)));
                ans.put(AnswerTable.ANS_TEXT, a.getString(a.getColumnIndexOrThrow(
                        AnswerTable.ANS_TEXT)));
                ans.put(AnswerTable.CHOICE_ID, a.getInt(a.getColumnIndexOrThrow(
                    AnswerTable.CHOICE_ID)));
                ans.put(AnswerTable.CREATED, a.getLong(a.getColumnIndexOrThrow(
                        AnswerTable.CREATED)));
                answers.put(ans);
                a.moveToNext();
            }
            a.close();
            
            // now send to actual server
            JSONObject data = new JSONObject();
            
            TelephonyManager tManager = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        	String uid = tManager.getDeviceId();
            
            data.put("deviceId", uid);
            data.put("answers", answers);
            Log.d("Push", data.toString());
            boolean success = postJsonToUrl(PUSH_URL, data.toString());
            
            // mark answers as uploaded if appropriate
            if (success) {
                for (int i=0; i<answers.length(); i++) {
                    JSONObject ans = answers.getJSONObject(i);
                    ContentValues values = new ContentValues();
                    values.put(AnswerTable.UPLOADED, 1);
                    
                    sdb.update(ANSWER_TABLE_NAME, values, AnswerTable._ID + " = ?", 
                            new String[] { Integer.toString(ans.getInt(AnswerTable._ID)) } );
                }
            }
            
            pdb.close();
            return success;
        } catch (Exception e) {
            Log.e("Push", e.getMessage());
        }
        return false;
    }
    
    /**
     * Push all un-uploaded GPS Locations in the phone database to the server.
     * Once successfully, each pushed location will be marked as uploaded in the database
     * 
     * @param ctxt - the current Context
     * 
     * @return true if all the locations has been successfully pushed
     */
    public static boolean pushLocations(Context ctx) {
        Log.d("Push", "Pushing locations to server");
        try {
            PeoplesDB pdb = new PeoplesDB(ctx);
            SQLiteDatabase sdb = pdb.getWritableDatabase();
            
            //set up the query
            String    table    = GPS_TABLE_NAME;
            String[]  cols     = {GPSTable._ID,
                                  GPSTable.LONGITUDE,
                                  GPSTable.LATITUDE,
                                  GPSTable.TIME};
            String    selc     = GPSTable.UPLOADED + " =  ?";
            String[]  selcArgs = {Integer.toString(0)};
            String    group    = null;
            String    having   = null;
            String    orderBy  = GPSTable._ID;
            
            //run it and construct json object;
            Cursor l = sdb.query(table, cols, selc, selcArgs, group, having, orderBy);
            JSONArray locations = new JSONArray();
            
            Log.d("Push", "# of locations to push : " + l.getCount());
            
            l.moveToFirst();
            while (!l.isAfterLast()) {
                JSONObject loc = new JSONObject();
                loc.put(GPSTable._ID, l.getInt(l.getColumnIndexOrThrow(
                        GPSTable._ID)));
                loc.put(GPSTable.LONGITUDE, l.getDouble(l.getColumnIndexOrThrow(
                        GPSTable.LONGITUDE)));
                loc.put(GPSTable.LATITUDE, l.getDouble(l.getColumnIndexOrThrow(
                        GPSTable.LATITUDE)));
                loc.put(GPSTable.TIME, l.getLong(l.getColumnIndexOrThrow(
                        GPSTable.TIME)));
                locations.put(loc);
                l.moveToNext();
            }
            l.close();
            
            // now send to actual server
            JSONObject data = new JSONObject();
            
            TelephonyManager tManager = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        	String uid = tManager.getDeviceId();
            data.put("deviceId", uid);
            
            data.put("locations", locations);
            Log.d("Push", data.toString());
            boolean success = postJsonToUrl(PUSH_URL, data.toString());
            
            // mark locations as uploaded if appropriate
            if (success) {
                for (int i=0; i<locations.length(); i++) {
                    JSONObject loc = locations.getJSONObject(i);
                    ContentValues values = new ContentValues();
                    values.put(GPSTable.UPLOADED, 1);
                    
                    sdb.update(GPS_TABLE_NAME, values, GPSTable._ID + " = ?", 
                            new String[] { Integer.toString(loc.getInt(GPSTable._ID)) } );
                }
            }
            
            pdb.close();
            return success;
        } catch (Exception e) {
            Log.e("Push", e.getMessage());
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
     * 
     */
    public static boolean pushCallLog(Context ctx) {
        Log.d("Push", "Pushing calllog to server");
        try {
            PeoplesDB pdb = new PeoplesDB(ctx);
            SQLiteDatabase sdb = pdb.getWritableDatabase();
            
            //set up the query
            String    table    = CALLLOG_TABLE_NAME;
            String[]  cols     = {CallLogTable._ID,
                                  CallLogTable.CALL_TYPE,
                                  CallLogTable.PHONE_NUMBER,
                                  CallLogTable.DURATION,
                                  CallLogTable.TIME};
            String    selc     = CallLogTable.UPLOADED + " =  ?";
            String[]  selcArgs = {Integer.toString(0)};
            String    group    = null;
            String    having   = null;
            String    orderBy  = CallLogTable._ID;
            
            //run it and construct json object;
            Cursor c = sdb.query(table, cols, selc, selcArgs, group, having, orderBy);
            JSONArray callLogs = new JSONArray();
            
            Log.d("Push", "# of call logs to push : " + c.getCount());
            
            c.moveToFirst();
            while (!c.isAfterLast()) {
                JSONObject log = new JSONObject();
                log.put(CallLogTable._ID, c.getInt(c.getColumnIndexOrThrow(
                        CallLogTable._ID)));
                log.put(CallLogTable.CALL_TYPE, c.getString(c.getColumnIndexOrThrow(
                        CallLogTable.CALL_TYPE)));
                log.put(CallLogTable.DURATION, c.getInt(c.getColumnIndexOrThrow(
                        CallLogTable.DURATION)));
                log.put(CallLogTable.TIME, c.getLong(c.getColumnIndexOrThrow(
                        CallLogTable.TIME)));
                log.put("contact_id", hash(c.getString(c.getColumnIndexOrThrow(
                        CallLogTable.PHONE_NUMBER))));
                callLogs.put(log);
                c.moveToNext();
            }
            c.close();
            
            // now send to actual server
            JSONObject data = new JSONObject();
            
            TelephonyManager tManager = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        	String uid = tManager.getDeviceId();
            data.put("deviceId", uid);
            
            data.put("calls", callLogs);
            Log.e("Push", data.toString());
            boolean success = postJsonToUrl(PUSH_URL, data.toString());
            
            // mark answers as uploaded if appropriate
            if (success) {
                for (int i=0; i<callLogs.length(); i++) {
                    JSONObject log = callLogs.getJSONObject(i);
                    ContentValues values = new ContentValues();
                    values.put(CallLogTable.UPLOADED, 1);
                    
                    sdb.update(CALLLOG_TABLE_NAME, values, CallLogTable._ID + " = ?", 
                            new String[] { Integer.toString(log.getInt(CallLogTable._ID)) } );
                }
            }
            pdb.close();
            return success;
        } catch (Exception e) {
            Log.e("Push", e.getMessage());
        }
        return false;
    }
    
    public static boolean pushAll(Context ctx) {
        boolean answers = pushAnswers(ctx);
        boolean locations = pushLocations(ctx);
        boolean calllog = pushCallLog(ctx);
        return answers && locations && calllog;
    }

    private static Integer hash(String s) { 
        if (s == null)
            return null;
        return s.hashCode();
    }
}
