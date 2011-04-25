package com.peoples.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

/**
 * 
 * Interact with our database using this class, PeoplesDB will
 * keep track of the version of the DB, creating the DB, updating
 * the DB, and other versioning manipulations.
 * 
 * @author diego
 *
 */
public class PeoplesDBHandler {
	
	private static final String TAG = "LocationTableHandler";
	private static final boolean D = true;
	
	private PeoplesDB pdb;
	private Context   contx;
	private SQLiteDatabase db;
	
	public PeoplesDBHandler(Context context){
		this.contx = context;
	}
	
	public void openWrite() {
		if(D) Log.e(TAG, "in openWrite()");
		pdb = new PeoplesDB(contx);
		db  = pdb.getWritableDatabase();
	}
	
	public void openRead(){
		if(D) Log.e(TAG, "in openRead()");
		pdb = new PeoplesDB(contx);
		db  = pdb.getReadableDatabase();	
	}
	
	public long insertLocation(Location loc){
		
		if(D) Log.e(TAG, "insertLocation()");
		
		//There are currently 4 columns GPS table, 3 w/o the auto increment
		//column
		ContentValues values = new ContentValues();
		
		values.put(PeoplesDB.GPSTable.LATITUDE,		 loc.getLatitude() 	);
		values.put(PeoplesDB.GPSTable.LONGITUDE,	 loc.getLongitude() );
		values.put(PeoplesDB.GPSTable.TIME,			 loc.getTime()		);
		
		return db.insert(PeoplesDB.GPS_TABLE_NAME, null, values);
	}
	
	public Cursor getStoredLocations(){
		
		if(D) Log.e(TAG, "in getStoredLocations()");
		
		//Query Arguments
		String table		= PeoplesDB.GPS_TABLE_NAME;
		String[] 	columns			= null; //returns all columns
		String 		selection		= null; //will return all locations
		String[] 	selectionArgs	= null; //not needed, this isn't really a prepared statement
		String		groupBy			= null; //not grouping the rows
		String		having			= null; //SQL having clause, not needed
		String		orderBy			= null; //use the default sort order
		
		Cursor mCursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		
		if(mCursor != null)
			mCursor.moveToFirst();
		
		return mCursor;
	}
	
	/**
	 * 
	 * Queries to find all Tables in this DB
	 * 
	 * @return
	 */
	public Cursor getListOfTables(){
		
		if(D) Log.e(TAG, "in getListOfTables()");
		
		Cursor mCursor = db.rawQuery("SELECT name " +
									 "FROM sqlite_master "+
									 "WHERE type=\"table\"", null);
		return mCursor;
	}
	
	
	/**
	 * 
	 * Self-described 
	 * 
	 * @return
	 */
	public Cursor getDescription(){
		
		
		if(D) Log.e(TAG, "in getListOfTables()");
		if(D) Log.e(TAG, "currently only shows location schema");
		
		Cursor mCursor = db.rawQuery("SELECT sql " +
									 "FROM sqlite_master "+
									 "WHERE name=\"gps\"", null);
		return mCursor;
	}
	

	/**
	 * 
	 */
	public void close() {
		if(D) Log.e(TAG, "in close()");
		pdb.close();
	}
	
	

}
