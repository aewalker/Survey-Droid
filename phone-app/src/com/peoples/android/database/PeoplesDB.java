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
    private static final int DATABASE_VERSION = 1;
    public static final String GPS_TABLE_NAME = "gps";
    public static final String CALLLOG_TABLE_NAME = "calllog";
    public static final String ANSWER_TABLE_NAME = "answers";
    public static final String BRANCH_TABLE_NAME = "branches";
    public static final String CHOICE_TABLE_NAME = "choices";
    public static final String CONDITION_TABLE_NAME = "conditions";
    public static final String QUESTION_TABLE_NAME = "questions";
    public static final String SURVEY_TABLE_NAME = "surveys";

    private Context context;

    public static final class GPSTable implements BaseColumns {
        // This class cannot be instantiated
        private GPSTable() {}

        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        public static final String LONGITUDE = "longitude";

        public static final String LATITUDE = "latitude";

        public static final String TIME = "time";

        private static String createSql() {
        	return "CREATE TABLE " + GPS_TABLE_NAME + " (" + GPSTable._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + GPSTable.LONGITUDE + " DOUBLE,"
            + GPSTable.LATITUDE + " DOUBLE," + GPSTable.TIME + " INTEGER"
            + " );";
        }
    }
    public static final class CallLogTable implements BaseColumns {
        // This class cannot be instantiated
        private CallLogTable() {}

        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        public static final String PHONE_NUMBER = "phone_number";

        public static final String CALL_TYPE = "call_type";

        public static final String TIME = "time";

        private static String createSql() {
        	return "CREATE TABLE " + CALLLOG_TABLE_NAME + " ("
            + CallLogTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CallLogTable.PHONE_NUMBER + " TEXT,"
            + CallLogTable.CALL_TYPE + " TEXT," + CallLogTable.TIME
            + " INTEGER" + ");";
        }
    }
    public static final class AnswerTable implements BaseColumns {
    	public static final String QUESTION_ID = "question_id";
    	public static final String CHOICE_ID = "choice_id";
    	public static final String ANS_TEXT = "ans_text";
    	public static final String CREATED = "created";

    	private static String createSql() {
    		return "CREATE TABLE answers (" +
    				_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    				"question_id INT UNSIGNED NOT NULL," +
    				"subject_id INT UNSIGNED NOT NULL,choice_id INT UNSIGNED," +
    				"ans_text TEXT,created DATETIME);";
    	}
    }
    public static final class BranchTable implements BaseColumns {
    	public static final String QUESTION_ID = "question_id";
    	public static final String NEXT_Q = "next_q";

    	private static String createSql() {
    		return "CREATE TABLE branches (" +
    				_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    				"question_id INT UNSIGNED NOT NULL, " +
    				"next_q INT UNSIGNED NOT NULL);";
    	}

    }
    public static final class ChoiceTable implements BaseColumns {
    	public static final String CHOICE_TEXT = "choice_text";
    	public static final String QUESTION_ID = "question_id";

    	private static String createSql() {
    		return "CREATE TABLE choices (" +
    				_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    				"choice_text VARCHAR(255)," +
    				"question_id INT UNSIGNED);";
    	}

    }
    public static final class ConditionTable implements BaseColumns {
    	public static final String BRANCH_ID = "branch_id";
    	public static final String QUESTION_ID = "question_id";
    	public static final String CHOICE_ID = "choice_id";
    	public static final String TYPE = "type";

    	private static String createSql() {
    		return "CREATE TABLE conditions (" +
    				_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    				"branch_id INT UNSIGNED NOT NULL, " +
    				"question_id INT UNSIGNED NOT NULL, " +
    				"choice_id INT UNSIGNED NOT NULL, " +
    				"type TINYINT UNSIGNED NOT NULL);";
    	}

    }
    public static final class QuestionTable implements BaseColumns {
    	public static final String SURVEY_ID = "survey_id";
    	public static final String Q_TEXT = "q_text";

    	private static String createSql() {
    		return "CREATE TABLE questions (" +
    				_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    				"survey_id INT UNSIGNED NOT NULL," +
    				"q_text TEXT);";
    	}

    }
    public static final class SurveyTable implements BaseColumns {
    	public static final String ID = "id";
    	public static final String NAME = "name";
    	public static final String CREATED = "created";
    	public static final String QUESTION_ID = "question_id";

    	public static final String MO = "mo";
    	public static final String TU = "tu";
    	public static final String WE = "we";
    	public static final String TH = "th";
    	public static final String FR = "fr";
    	public static final String SA = "sa";
    	public static final String SU = "su";

    	private static String createSql() {
    		return "CREATE TABLE surveys (" +
    				_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    				"name VARCHAR(255),created DATETIME," +
    				"question_id INT UNSIGNED NOT NULL," +
    				"mo VARCHAR(255)," +
    				"tu VARCHAR(255)," +
    				"we VARCHAR(255)," +
    				"th VARCHAR(255)," +
    				"fr VARCHAR(255)," +
    				"sa VARCHAR(255)," +
    				"su VARCHAR(255));";
    	}
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	AnswerTable.createSql();
    	Log.e(TAG, "onCreate");

        db.beginTransaction();

        try {
            db.execSQL(GPSTable.createSql());
            db.execSQL(CallLogTable.createSql());
            db.execSQL(AnswerTable.createSql());
            db.execSQL(BranchTable.createSql());
            db.execSQL(ChoiceTable.createSql());
            db.execSQL(ConditionTable.createSql());
            db.execSQL(QuestionTable.createSql());
            db.execSQL(SurveyTable.createSql());

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
