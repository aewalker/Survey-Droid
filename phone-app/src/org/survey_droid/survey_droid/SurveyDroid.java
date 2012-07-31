/*---------------------------------------------------------------------------*
 * SurveyDroid.java                                                          *
 *                                                                           *
 * The main application class.                                               *
 *---------------------------------------------------------------------------*
 * Copyright 2012 Sema Berkiten, Vladimir Costescu, Henry Liu, Diego Vargas, *
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
package org.survey_droid.survey_droid;

import org.acra.*;
import org.acra.annotation.*;
import org.survey_droid.survey_droid.annotation.ConfigKey;

import android.app.Application;

/**
 * The main application class
 * 
 * @author Austin Walker
 */
//TODO move all the singleton things like the DatabaseConnection into this class
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
	/** logging tag */
	private static final String TAG = "SurveyDroid";
	
	/** Should errors be sent to the admin? */
	@ConfigKey(value="false", global=true)
	private static String REPORT_ERRORS = "survey_droid.report_errors";
	
    @Override
    public void onCreate()
    {
        //start ACRA
    	Util.i(null, TAG, "onCreate");
    	if (Config.getBoolean(this, REPORT_ERRORS, false)) ACRA.init(this);
        Config.init(this);
        super.onCreate();
    }
}