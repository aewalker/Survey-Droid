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
import android.util.Log;

/**
 * Responsible for logging text messages. Catches the system broadcast when a
 * text is
 * coming in and logs it.
 *
 * @author Tony Xiao
 */
public class IncomingSMSTracker extends BroadcastReceiver {
    // logging tag
    private static final String TAG = "IncomingSMS";
	private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context ctxt, Intent intent) {
    	// make sure the functionality is enabled
    	Config cfg = new Config(ctxt);
		if (!cfg.isCallLogEnabled()) return;

        // Make sure sms messages are available
		if (!ACTION_SMS_RECEIVED.equals(intent.getAction()))
			return;

        Bundle bundle = intent.getExtras();
        Object messages[] = (Object[]) bundle.get("pdus");
        if (messages == null || messages.length == 0)
            return;

        if (Config.D) Log.d(TAG, "SMS Received, start tracking");

        // Get those messages
        SmsMessage smsMessage[] = new SmsMessage[messages.length];
        for (int n = 0; n < messages.length; n++)
            smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);

		if (Config.D) Log.d(TAG, messages.length + " new sms(s) detected");

		// write the message record to database
		TrackingDBHandler cdbh = new TrackingDBHandler(ctxt);
		cdbh.openWrite();
		for (int i=0; i<messages.length; i++) {
			cdbh.writeCall(smsMessage[i].getOriginatingAddress(),
					PeoplesDB.CallLogTable.CallType.INCOMING_TEXT,
					0,
					smsMessage[i].getTimestampMillis());
		}
		cdbh.close();
    }

}
