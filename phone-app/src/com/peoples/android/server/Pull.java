package com.peoples.android.server;

import java.util.List;

import org.json.JSONObject;

import android.util.Log;

import com.peoples.android.model.Survey;

public class Pull extends WebClient {

    /**
     * URL to use when requesting survey updates
     */
    public static final String PULL_URL = "http://www.eigendiego.com/cake/app/webroot/answers/pull";

    public static List<Survey> getNewSurveys() {
        try {
            JSONObject json = new JSONObject(getUrlContent(PULL_URL));
            Log.d("PULL", json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
