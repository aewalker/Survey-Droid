/*---------------------------------------------------------------------------*
 * TextTracker.java                                                          *
 *                                                                           *
 * Handles incoming and outgoing texts and logs them in the database.        *
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
package org.surveydroid.android;

import org.surveydroid.android.coms.ComsService;
import org.surveydroid.android.database.SurveyDroidDB;
import org.surveydroid.android.database.TrackingDBHandler;
import org.surveydroid.android.survey.SurveyService;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Responsible for logging text messages. Catches the system broadcast when a
 * text is coming in and logs it.
 *
 * @author Tony Xiao
 * @author Austin Walker
 */
public class IncomingSMSTracker extends BroadcastReceiver
{
    /** logging tag */
    private static final String TAG = "IncomingSMS";

    @Override
    public void onReceive(Context ctxt, Intent intent)
    {
    	// make sure the functionality is enabled
    	boolean server = Config.getSetting(ctxt, Config.CALL_LOG_SERVER,
				Config.CALL_LOG_SERVER_DEFAULT);
    	boolean local = Config.getSetting(ctxt, Config.CALL_LOG_LOCAL, true);
    	if (!local || !server)
		{
			Util.d(null, TAG, "Call log local: " + local);
			Util.d(null, TAG, "Call log server: " + server);
			return;				
		}

        // Make sure sms messages are available
//		if (!ACTION_SMS_RECEIVED.equals(intent.getAction()))
//			return;

        Bundle bundle = intent.getExtras();
        Object[] messages = (Object[]) bundle.get("pdus");
        if (messages == null || messages.length == 0)
        {
        	Util.w(null, TAG, "No messages recieved");
            return;
        }

        Util.d(null, TAG, "SMS Received, start tracking");

        // Get those messages
        SmsMessage[] smsMessage = new SmsMessage[messages.length];
        for (int i = 0; i < messages.length; i++)
        {
            smsMessage[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
        }

		Util.d(null, TAG, messages.length + " new sms message(s) detected");

		// write the message record to database
		TrackingDBHandler tdbh = new TrackingDBHandler(ctxt);
		tdbh.open();
		for (int i = 0; i < messages.length; i++)
		{
			String number = Util.cleanPhoneNumber(
				smsMessage[i].getOriginatingAddress());
			tdbh.writeCall(number,
					SurveyDroidDB.CallLogTable.CallType.INCOMING_TEXT,
					-1, //doesn't matter; ignored for this type
					smsMessage[i].getTimestampMillis() / 1000);
			if (i == messages.length - 1)
			{
				Cursor surveys;
				if (tdbh.isNewNumber(number))
				{
					surveys = tdbh.getNewTextSurveys();
				}
				else
				{
					surveys = tdbh.getOldTextSurveys();
				}
				int count = surveys.getCount();
				if (count != 0)
				{
					surveys.moveToFirst();
					int id_i = surveys.getColumnIndexOrThrow(
							SurveyDroidDB.SurveyTable._ID);
					for (int j = 0; j < count; j++)
					{
						Intent surveyIntent = new Intent(ctxt,
							SurveyService.class);
						surveyIntent.setAction(
							SurveyService.ACTION_SURVEY_READY);
						surveyIntent.putExtra(
							SurveyService.EXTRA_SURVEY_ID,
							surveys.getInt(id_i));
						surveyIntent.putExtra(
							SurveyService.EXTRA_SURVEY_TYPE,
							SurveyService.SURVEY_TYPE_CALL_INIT);
						Uri uri = Uri.parse("sms tracker survey");
						Dispatcher.dispatch(ctxt, surveyIntent,
							0, Dispatcher.TYPE_WAKEFUL_MANUAL, uri);
					}
				}
			}
		}
		tdbh.close();
		
		//tell the coms service to upload the call information
		Intent uploadIntent = new Intent(ctxt,
				ComsService.class);
		uploadIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
		uploadIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
				ComsService.CALL_DATA);
		WakefulIntentService.sendWakefulWork(ctxt, uploadIntent);
    }

}
