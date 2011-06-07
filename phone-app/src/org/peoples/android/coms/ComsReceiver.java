/*---------------------------------------------------------------------------*
 * ComsReceiver.java                                                         *
 *                                                                           *
 * Feeds intents back to the ComsService from alarm broadcasts.              *
 *---------------------------------------------------------------------------*/
package org.peoples.android.coms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Helper class to get broadcast intents and send them back to
 * the ComsService.
 * 
 * @author Austin Walker
 */
class ComsReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		context.startService(intent);
	}
}