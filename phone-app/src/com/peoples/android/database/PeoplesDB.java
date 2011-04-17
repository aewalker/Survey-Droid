package com.peoples.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class PeoplesDB extends SQLiteOpenHelper {
    private static final String TAG = "PeoplesDB";
    private static final boolean D = true;

    private static final String DATABASE_NAME = "peoples.db";
    private static final int DATABASE_VERSION = 2;
    public static final String GPS_TABLE_NAME = "gps";
    public static final String CALLLOG_TABLE_NAME = "calllog";
    
    private Context context; 

    public static final class GPSTable implements BaseColumns {
        // This class cannot be instantiated
        private GPSTable() {}

        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        public static final String LONGITUDE = "longitude";

        public static final String LATITUDE = "latitude";

        public static final String TIME = "time";
    }

    public static final class CallLogTable implements BaseColumns {
        // This class cannot be instantiated
        private CallLogTable() {}

        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        public static final String PHONE_NUMBER = "phone_number";

        public static final String CALL_TYPE = "call_type";

        public static final String TIME = "time";
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
    	
    	Log.e(TAG, "onCreate");
    	
        db.beginTransaction();
        
        try {
            db.execSQL("CREATE TABLE " + GPS_TABLE_NAME + " (" + GPSTable._ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + GPSTable.LONGITUDE + " DOUBLE,"
                    + GPSTable.LATITUDE + " DOUBLE," + GPSTable.TIME + "INTEGER"
                    + ");");
    
            db.execSQL("CREATE TABLE " + CALLLOG_TABLE_NAME + " ("
                    + CallLogTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + CallLogTable.PHONE_NUMBER + " TEXT,"
                    + CallLogTable.CALL_TYPE + " TEXT," + CallLogTable.TIME
                    + " INTEGER" + ");");
            
            buildInitialCallLog(db);
            
            db.setTransactionSuccessful();
        }
        
        finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
    	//I think this is being called every time I create a nre PeoplesDB
    	
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + GPS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CALLLOG_TABLE_NAME);
        onCreate(db);
    }

    private void buildInitialCallLog(SQLiteDatabase db) {
        Cursor c = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI,
                                                      null, null, null,
                                                      android.provider.CallLog.Calls.DATE + " DESC");

        // Retrieve the column-indixes of phoneNumber, date and calltype
        int numberColumn = c.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
        int dateColumn = c.getColumnIndex(android.provider.CallLog.Calls.DATE);

        // type can be: Incoming, Outgoing or Missed
        int typeColumn = c.getColumnIndex(android.provider.CallLog.Calls.TYPE);

        // Loop through all entries the cursor provides to us.
        if (c.moveToFirst()) {
            do {
                String callerPhoneNumber = c.getString(numberColumn);
                int callDate = c.getInt(dateColumn);
                int callType = c.getInt(typeColumn);
                String stringCallType;
                
                ContentValues call = new ContentValues();

                switch (callType) {

                case android.provider.CallLog.Calls.INCOMING_TYPE:
                    stringCallType = "Incoming";
                    break;
                    
                case android.provider.CallLog.Calls.MISSED_TYPE:
                    stringCallType = "Missed";
                    break;

                case android.provider.CallLog.Calls.OUTGOING_TYPE:
                    stringCallType = "Outgoing";
                    break;
                default:
                    stringCallType = "";
                    break;
                    
                }
                
                call.put(CallLogTable.PHONE_NUMBER, callerPhoneNumber);
                call.put(CallLogTable.CALL_TYPE, stringCallType);
                call.put(CallLogTable.TIME, callDate);
                
                db.insert(CALLLOG_TABLE_NAME, null, call);
                
            } while (c.moveToNext());
        }
        
        c.close();
    }
    
    
    /**
     * Not sure if we need to keep the context around
     * 
     * @param context
     */
    public PeoplesDB(Context context){
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.e(TAG, "in constructor");
    }
    
    //testings
    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
    	if(D) Log.e(TAG, "getReadable");
    	return super.getReadableDatabase();
    }
    
    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
    	if(D) Log.e(TAG, "getWriteable");
    	return super.getWritableDatabase();
    }
    
    
    
    
}
