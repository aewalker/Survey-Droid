/*---------------------------------------------------------------------------*
 * StatusDBHandler.java                                                      *
 *                                                                           *
 * Extension of the PeoplesDBHandler for application status changes.         *
 *---------------------------------------------------------------------------*/
package org.peoples.android.database;

import org.peoples.android.Util;

import android.content.ContentValues;
import android.content.Context;

/**
 * Handles application status related calls to the PEOPLES database.
 * 
 * @author Austin Walker
 */
public class StatusDBHandler extends PeoplesDBHandler
{
	//logging tag
	private static final String TAG = "StatusDBHandler";
	
	/**
	 * Create a new StatusDBHandler object.
	 * 
	 * @param context
	 */
	public StatusDBHandler(Context context)
	{
		super(context);
	}
	
	/**
	 * Write to the database that a feature has been enabled or disabled.
	 * 
	 * @param feature - the feature affected (see {@link PeoplesDB#StatusTable}
	 * for the feature codes)
	 * @param enabled - true if the feature was enabled, false if it was
	 * disabled
	 * @param created - when the setting was changed
	 * @return true on success
	 */
	public boolean statusChanged(int feature, boolean enabled, long created)
	{
		Util.d(contx, TAG, "Writing status change: "
				+ feature + " is " + enabled);
		ContentValues values = new ContentValues();
		
		//set up the query
		values.put(PeoplesDB.StatusTable.TYPE, feature);
		if (enabled) values.put(PeoplesDB.StatusTable.STATUS,
				PeoplesDB.StatusTable.STATUS_ON);
		else values.put(PeoplesDB.StatusTable.STATUS,
				PeoplesDB.StatusTable.STATUS_OFF);
		values.put(PeoplesDB.StatusTable.CREATED, created);
		
		//run it
		if (db.insert(PeoplesDB.STATUS_TABLE_NAME, null, values) == -1)
			return false;
		return true;
	}
}