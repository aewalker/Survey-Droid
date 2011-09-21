/*---------------------------------------------------------------------------*
 * SurveyReceiver.java                                                       *
 *                                                                           *
 * Feeds broadcasts from the alarm service back to the Survey Scheduler.     *
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
package org.surveydroid.android.survey;

import org.surveydroid.android.Util;

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