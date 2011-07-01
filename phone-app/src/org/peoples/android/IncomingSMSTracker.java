/*---------------------------------------------------------------------------*
 * TextTracker.java                                                          *
 *                                                                           *
 * Handles incoming and outgoing texts and logs them in the database.        *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

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

    @Override
    public void onReceive(Context context, Intent intent) {

        // Make sure sms messages are available
        Bundle bundle = intent.getExtras();
        Object messages[] = (Object[]) bundle.get("pdus");
        if (messages == null || messages.length == 0)
            return;
        
        // Get those messages
        SmsMessage smsMessage[] = new SmsMessage[messages.length];
        for (int n = 0; n < messages.length; n++)
            smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
        

        // show first message
        Toast toast = Toast.makeText(context,
                "Received SMS: " + smsMessage[0].getMessageBody() + " from " + smsMessage[0].getOriginatingAddress(), Toast.LENGTH_LONG);
        toast.show();
    }

}
