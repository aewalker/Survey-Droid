/*---------------------------------------------------------------------------*
 * TextTracker.java                                                          *
 *                                                                           *
 * Handles incoming and outgoing texts and logs them in the database.        *
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
package org.survey_droid.survey_droid;

import org.survey_droid.survey_droid.coms.ComsService;
import org.survey_droid.survey_droid.content.ProviderContract.CallLogTable.ContactType;
import org.survey_droid.survey_droid.content.TrackingDBHandler;
import org.survey_droid.survey_droid.content.ProviderContract.SurveyTable;
import org.survey_droid.survey_droid.survey.SurveyService;
import org.survey_droid.survey_droid.survey.SurveyService.SurveyType;

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
    	boolean local = Config.getBoolean(ctxt, Config.TEXT_LOG_LOCAL);
    	if (!local)
		{
			Util.d(null, TAG, "Ignoring text; user has disabled text logging");
			return;
		}

        Bundle bundle = intent.getExtras();
        Object[] messages = (Object[]) bundle.get("pdus");
        if (messages == null || messages.length == 0)
        {
        	Util.w(null, TAG, "No messages recieved!?");
            return;
        }

        Util.d(null, TAG, "SMS Received, start logging");

        // Get those messages
        SmsMessage[] smsMessage = new SmsMessage[messages.length];
        for (int i = 0; i < messages.length; i++)
        {
        	/*
        	 * TODO the dev page says this "will soon be deprecated", but
        	 * there is no replacement yet.  Make sure to look for it.
        	 */
            smsMessage[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
        }

		Util.d(null, TAG, messages.length + " new sms message(s) detected");

		// write the message record to database
		boolean newInfo = false;
		TrackingDBHandler tdbh = new TrackingDBHandler(ctxt);
		for (int i = 0; i < messages.length; i++)
		{
			String number = Util.cleanPhoneNumber(
				smsMessage[i].getOriginatingAddress());
			//FIXME
			//for each study:
			//	if text_logging is enabled:
			//		newInfo = true;
			tdbh.writeContact(number,
					ContactType.INCOMING_TEXT,
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
				try
				{
					int count = surveys.getCount();
					if (count != 0)
					{
						surveys.moveToFirst();
						int id_i = surveys.getColumnIndexOrThrow(
								SurveyTable.Fields._ID);
						int study_i = surveys.getColumnIndexOrThrow(
							SurveyTable.Fields.STUDY_ID);
						for (int j = 0; j < count; j++)
						{
							Intent surveyIntent = new Intent(ctxt,
								SurveyService.class);
							surveyIntent.setAction(
								SurveyService.ACTION_SURVEY_READY);
							surveyIntent.putExtra(
								SurveyService.EXTRA_SURVEY_ID,
								surveys.getLong(id_i));
							surveyIntent.putExtra(
								SurveyService.EXTRA_STUDY_ID,
								surveys.getLong(study_i));
							surveyIntent.putExtra(
								SurveyService.EXTRA_SURVEY_TYPE,
								SurveyType.SURVEY_TYPE_CONTACT_INIT.ordinal());
							Uri uri = Uri.parse("sms tracker survey");
							Dispatcher.dispatch(ctxt, surveyIntent,
								0, Dispatcher.TYPE_WAKEFUL_MANUAL, uri);
						}
					}
				}
				finally
				{
					surveys.close();
				}
			}
		}
		
		//tell the coms service to upload the call information
		if (newInfo)
		{
			Intent uploadIntent = new Intent(ctxt,
					ComsService.class);
			uploadIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
			uploadIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
					ComsService.CALL_DATA);
			WakefulIntentService.sendWakefulWork(ctxt, uploadIntent);
		}
    }

}
