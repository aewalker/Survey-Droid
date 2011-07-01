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
 * text is going out and logs it.
 * 
 * @author Tony Xiao
 */
public class OutgoingSMSTracker extends BroadcastReceiver {
    // logging tag
    private static final String TAG = "OutgoingSMS";

    @Override
    public void onReceive(Context context, Intent intent) {
    }

}
