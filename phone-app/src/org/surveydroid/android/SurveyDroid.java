package org.surveydroid.android;

import org.acra.*;
import org.acra.annotation.*;

import android.app.Application;

@ReportsCrashes(formKey = "",
        formUri = "https://survey-droid.org/api/error",
        mode = ReportingInteractionMode.NOTIFICATION,
        resNotifTickerText = R.string.crash_notif_ticker_text,
        resNotifTitle = R.string.crash_notif_title,
        resNotifText = R.string.crash_notif_text,
        resDialogText = R.string.crash_dialog_text,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast)
public class SurveyDroid extends Application
{
	private static final String TAG = "SurveyDroid";
	
    @Override
    public void onCreate()
    {
        // The following line triggers the initialization of ACRA
    	Util.i(null, TAG, "onCreate");
        ACRA.init(this);
        super.onCreate();
    }
}