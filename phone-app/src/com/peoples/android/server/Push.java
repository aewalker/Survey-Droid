package com.peoples.android.server;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.peoples.android.database.PeoplesDB;
import static com.peoples.android.database.PeoplesDB.*;

public class Push extends WebClient {

    /**
     * URL to use when pushing user answers
     */
    public static final String PUSH_URL = "http://www.eigendiego.com/cake/app/webroot/answers/push";
    
    
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
            data.put("deviceId", "testingId");
            data.put("answers", answers);
            Log.e("Push", data.toString());
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
    
    public static boolean pushLocations(Context ctx) {
        return false;
    }
    
    public static boolean pushCallLog(Context ctx) {
        return false;
    }
    
    public static boolean pushAll(Context ctx) {
        boolean answers = pushAnswers(ctx);
        boolean locations = pushLocations(ctx);
        boolean calllog = pushCallLog(ctx);
        return answers && locations && calllog;
    }
    
    
    public static boolean sendAnswersToServer(JSONArray answers) {
        try {
        JSONObject data = new JSONObject();
        data.put("answers", answers);
        data.put("deviceId", "testingId");
        boolean success = postJsonToUrl(PUSH_URL, data.toString());
        return success;
        } catch (Exception e) {
            Log.e("Push", e.getMessage());
        }
        return false;
    }

    public static boolean sendLocations(JSONArray locations) {
    	return false;
    }

    public static boolean sendCalllog(JSONArray calllog) {
    	return false;
    }

    public static boolean sendAll() {
    	return false;
    }
}
