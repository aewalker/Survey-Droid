/*---------------------------------------------------------------------------*
 * PeoplesDBHandler.java                                                     *
 *                                                                           *
 * Base class that other DB helpers inherit from.  Just contains generic     *
 * database open/close calls.  Any kind of database task that needs to be    *
 * performed by many different parts of the application can go here, but     *
 * more specific functions should be in their own class that extends this.   *
 *---------------------------------------------------------------------------*/
package org.peoples.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.peoples.android.Util;

/**
 * 
 * Interact with our database using this class, PeoplesDB will
 * keep track of the version of the DB, creating the DB, updating
 * the DB, and other versioning manipulations.
 * 
 * @author Diego Vargas
 * @author Austin Walker
 */
public class PeoplesDBHandler
{
	//logging tag
	protected static final String TAG = "LocationTableHandler";
	
	protected PeoplesDB pdb;
	protected Context contx;
	protected SQLiteDatabase db;
	
	/**
	 * Simple constructor.
	 * 
	 * @param context - the current Context
	 */
	public PeoplesDBHandler(Context context)
	{
		this.contx = context;
	}
	
	/**
	 * Open a write-only connection to the database.
	 */
	public void openWrite()
	{
		Util.d(null, TAG, "opening write-only database connection");
		pdb = new PeoplesDB(contx);
		db  = pdb.getWritableDatabase();
	}
	
	/**
	 * Open a read-only connection to the database.
	 */
	public void openRead()
	{
		Util.d(null, TAG, "opening read-only database connection");
		pdb = new PeoplesDB(contx);
		db  = pdb.getReadableDatabase();
	}

	/**
	 * Close the database; should be called after ever getReadable or
	 * getWritable.
	 */
	public void close()
	{
		Util.d(null, TAG, "closing database connection");
		db.close();
		pdb.close();
	}
}
