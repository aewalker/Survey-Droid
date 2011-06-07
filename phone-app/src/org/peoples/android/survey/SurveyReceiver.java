/*---------------------------------------------------------------------------*
 * SurveyReceiver.java                                                       *
 *                                                                           *
 * Feeds broadcasts from the alarm service back to the Survey Scheduler.     *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Helper class to get broadcasts from the alarm service for the
 * SurveyScheduler
 * 
 * @author Austin Walker
 */
public class SurveyReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context ctxt, Intent intent)
	{
		if (intent.getAction().equals(SurveyScheduler.ACTION_SCHEDULE_SURVEYS))
		{
			ctxt.startService(intent);
		}
		else throw new RuntimeException("Unknown action: "
				+ intent.getAction());
	}		
}