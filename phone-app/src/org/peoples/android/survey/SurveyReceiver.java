/*---------------------------------------------------------------------------*
 * SurveyReceiver.java                                                       *
 *                                                                           *
 * Feeds broadcasts from the alarm service back to the Survey Scheduler.     *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import org.peoples.android.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//TODO remove this class
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
			Util.e(null, "SurveyReceiver", "THIS GOT USED!");
			throw new RuntimeException("Survey receiver got used");
			//ctxt.startService(intent);
		}
		else throw new RuntimeException("Unknown action: "
				+ intent.getAction());
	}		
}