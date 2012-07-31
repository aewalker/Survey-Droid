/*---------------------------------------------------------------------------*
 * Study.java                                                                *
 *                                                                           *
 * Holds information about a study.                                          *
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

import java.io.Serializable;

import org.survey_droid.survey_droid.coms.ComsService;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Holds information about a study.
 * 
 * @author Austin Walker
 */
public class Study implements Serializable
{
	/**
	 * Interface to allow things to receive notice when this study has been
	 * joined or left.
	 */
	public interface StudyListener
	{
		/**
		 * Callback for when this study has been joined.
		 * 
		 * @param worked if true, then the join was successful
		 */
		public void onJoin(boolean worked);
		
		/**
		 * Callback for when this study has been left
		 * 
		 * @param worked if true, then the leave was successful
		 */
		public void onLeave(boolean worked);
	}
	
	/** Logging tag */
	private static final String TAG = "Study";
	
	private static final long serialVersionUID = 1L;
	
	//FIXME move to coms somewhere
	/** indicates that a requested network operation has completed */
	public static final String ACTION_NETWORK_OPERATION_COMPLETE =
			"org.survey_droid.survey_droid.coms.ACTION_NETWORK_OPERATION_COMPLETE";
	
	/** action to join a study */
	public static final String ACTION_JOIN_STUDY =
			"org.survey_droid.survey_droid.ACTION_JOIN_STUDY";
	
	/** action to leave a study */
	public static final String ACTION_LEAVE_STUDY =
			"org.survey_droid.survey_droid.ACTION_LEAVE_STUDY";
	
	/** category type that indicates a study operation was completed */
	public static final String CATEGORY_STUDY_OPERATION =
			"org.survey_droid.survey_droid.CATETORY_STUDY_OPERATION";
	
	/** intent extra key; if true, then the operation succeeded */
	public static final String EXTRA_SUCCESS =
			"org.survey_droid.survey_droid.EXTRA_SUCCESS";
	
	/** Intent extra that defines the server the study is from */
	public static final String EXTRA_STUDY_SERVER =
			"org.survey_droid.survey_droid.EXTRA_STUDY_SERVER";
	
	/** Intent extra that defines the server study id. */
	public static final String EXTRA_STUDY_ID =
			"org.survey_droid.survey_droid.EXTRA_STUDY_ID";
	
	/** (optional) contains an explanation of why the operation did not work */
	public static final String EXTRA_ERROR_MESSAGE =
			"org.survey_droid.survey_droid.EXTRA_ERROR_MESSAGE";
	
	/** The study id (as reported by the server, not the local id) */
	private final int id;
	
	/** The name of this study */
	public final String name;
	
	/** The description of this study */
	public final String description;
	
	/** The server this study is from */
	private final String server;
	
	/** Whether or not the user has joined this study */
	private boolean joined;
	
	/**
	 * If true, this is a oneOff study (consists of just a single, one-time
	 * survey).
	 */
	public final boolean oneOff;
	
	/** If true, this study tracks the user's calls */
	public final boolean tracksCalls;
	
	/** If true, this study tracks the user's texts */
	public final boolean tracksTexts;
	
	/** If true, this study tracks the user's location */
	public final boolean tracksLocation;
	
	/**
	 * If {@link #tracksLocation} is true, this holds the granularity (in
	 * minutes) at which this study will collect locations
	 */ 
	public final int locationInterval;
	
	public Study(int id, String name, String description, String server,
		boolean joined, boolean oneOff,
		boolean tracksCalls, boolean tracksTexts,
		boolean tracksLocation,	int locationInterval)
	{
		this.id = id;
		this.name = name;
		this.description = description;
		this.server = server;
		this.joined = joined;
		this.oneOff = oneOff;
		this.tracksCalls = tracksCalls;
		this.tracksTexts = tracksTexts;
		this.tracksLocation = tracksLocation;
		this.locationInterval = locationInterval;
	}
	
	/**
	 * @return true if this study has been joined
	 */
	public boolean isJoined()
	{
		return joined;
	}
	
	/**
	 * Attempt to join this study
	 * 
	 * @param l will be called when this works or times out
	 * @param c the current context
	 * 
	 * @throws RuntimeException if this study has already been joined
	 */
	public void join(final StudyListener l, Context c)
	{
		Util.d(null, TAG, "Atempting to joing study " + name + " from " + server);
		if (joined) throw new RuntimeException("Study already joined!");
		
		toggle(l, c, true);
	}
	
	/**
	 * Attempt to leave a study
	 * 
	 * @param l will be called when this works or times out
	 * @param c the current context
	 * 
	 * @throws RuntimeException if this study has not been joined
	 * @throws RuntimeException if this study is a one-off
	 */
	public void leave(final StudyListener l, Context c)
	{
		Util.d(null, TAG, "Atempting to leave study " + name + " from " + server);
		if (!joined) throw new RuntimeException(
			"Can't leave study; hasn't been joined!");
		if (oneOff) throw new RuntimeException(
			"Can't leave one-off studies");
		
		toggle(l, c, false);
	}
	
	/**
	 * Like join/leave, but lets you specify which to do.
	 * 
	 * @param l
	 * @param c
	 * @param join if true, try to join; else, try to leave
	 */
	private void toggle(final StudyListener l, Context c, final boolean join)
	{
		//send an intent to the coms service to do the join
		Intent joinIntent = new Intent(c, ComsService.class);
		if (join) joinIntent.setAction(ACTION_JOIN_STUDY);
		else joinIntent.setAction(ACTION_LEAVE_STUDY);
		joinIntent.putExtra(EXTRA_STUDY_SERVER, server);
		joinIntent.putExtra(EXTRA_STUDY_ID, id);
		
		//create a receiver to get the result
		BroadcastReceiver br = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				if (!intent.getAction().equals(
					ACTION_NETWORK_OPERATION_COMPLETE)) return;
				if (!intent.hasCategory(CATEGORY_STUDY_OPERATION)) return;
				if (intent.getIntExtra(EXTRA_STUDY_ID, -1) != id) return;
				if (!intent.getStringExtra(EXTRA_STUDY_SERVER).equals(server)) return;
				context.unregisterReceiver(this);
				if (!intent.hasExtra(EXTRA_SUCCESS))
					throw new RuntimeException("Operation success not defined!");
				boolean worked = intent.getBooleanExtra(EXTRA_SUCCESS, false);
				if (!worked)
				{
					String msg = intent.getStringExtra(EXTRA_ERROR_MESSAGE);
					if (msg == null) msg = "An unknown error occured.";
					Util.e(context, TAG, msg);
				}
				if (join) l.onJoin(worked);
				else l.onLeave(worked);
			}
		};
		IntentFilter intFilter = new IntentFilter(ACTION_NETWORK_OPERATION_COMPLETE);
		intFilter.addCategory(CATEGORY_STUDY_OPERATION);
		
		c.registerReceiver(br, intFilter);
		WakefulIntentService.sendWakefulWork(c, joinIntent);
	}
}
