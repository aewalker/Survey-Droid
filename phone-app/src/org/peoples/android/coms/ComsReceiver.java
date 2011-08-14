/*---------------------------------------------------------------------------*
 * ComsReceiver.java                                                         *
 *                                                                           *
 * Feeds intents back to the ComsService from alarm broadcasts.              *
 *---------------------------------------------------------------------------*/
package org.peoples.android.coms;

import org.peoples.android.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//TODO remove this class
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
		Util.e(null, "ComsReceiver", "THIS GOT USED!");
		throw new RuntimeException("Coms receiver got used");
		//context.startService(intent);
	}
}