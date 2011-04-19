package com.peoples.android.server;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class Push extends WebClient {

    /**
     * URL to use when pushing user answers
     */
    public static final String PUSH_URL = "http://www.eigendiego.com/cake/app/webroot/answers/push";

    public static boolean sendAnswersToServer(JSONArray answers) {
        try {
        JSONObject data = new JSONObject();
        data.put("answers", answers);
        data.put("deviceId", "testingId");
        boolean success = postToUrl(PUSH_URL, "data", data.toString());
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
