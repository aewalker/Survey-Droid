/*---------------------------------------------------------------------------*
 * TextTracker.java                                                          *
 *                                                                           *
 * Handles incoming and outgoing texts and logs them in the database.        *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import org.peoples.android.database.PeoplesDB;
import org.peoples.android.database.TrackingDBHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Responsible for logging text messages. Catches the system broadcast when a
 * text is coming in and logs it.
 *
 * @author Tony Xiao
 */
public class IncomingSMSTracker extends BroadcastReceiver
{
    // logging tag
    private static final String TAG = "IncomingSMS";
//	private static final String ACTION_SMS_RECEIVED =
//		"android.provider.Telephony.SMS_RECEIVED";

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

		Util.d(ctxt, TAG, messages.length + " new sms message(s) detected");

		// write the message record to database
		TrackingDBHandler cdbh = new TrackingDBHandler(ctxt);
		cdbh.openWrite();
		for (int i = 0; i < messages.length; i++)
		{
			cdbh.writeCall(smsMessage[i].getOriginatingAddress(),
					PeoplesDB.CallLogTable.CallType.INCOMING_TEXT,
					-1, //doesn't matter; ignored for this type
					smsMessage[i].getTimestampMillis());
		}
		cdbh.close();
    }

}
