/*---------------------------------------------------------------------------*
 * PeoplesDB.java                                                            *
 *                                                                           *
 * Contains information about the set up of the PEOPLES SQLite database and  *
 * methods to create and update that database.                               *
 *---------------------------------------------------------------------------*/
package com.peoples.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Represents the SQLite database to hold information for the app.  Contains
 * functions to create and upgrade the database, as well as public fields that
 * contain the names of the tables and columns. When another class needs to use
 * the database, they should use these constants.  Subclasses represent the
 * various tables.  Each implements BaseColumns by having a unique id field.
 * 
 * @see BaseColumns
 * @see GPSTable
 * @see CallLogTable
 * @see AnswerTable
 * @see BranchTable
 * @see ChoiceTable
 * @see ConditionTable
 * @see QuestionTable
 * @see SurveyTable
 * @see ScheduledSurveys
 * 
 * @author Diego Vargas
 * @author Vladimir Costescu
 */
public class PeoplesDB extends SQLiteOpenHelper {
    private static final String TAG = "PeoplesDB";
    private static final boolean D = true;

    //Change the version number here to force the database to
    //update itself.  This throws out all data.
    private static final String DATABASE_NAME = "peoples.db";
    private static final int DATABASE_VERSION = 5;
    
    //table names
    public static final String GPS_TABLE_NAME = "gps";
    public static final String CALLLOG_TABLE_NAME = "calllog";
    public static final String ANSWER_TABLE_NAME = "answers";
    public static final String BRANCH_TABLE_NAME = "branches";
    public static final String CHOICE_TABLE_NAME = "choices";
    public static final String CONDITION_TABLE_NAME = "conditions";
    public static final String QUESTION_TABLE_NAME = "questions";
    public static final String SURVEY_TABLE_NAME = "surveys";
    public static final String SS_TABLE_NAME	 = "scheduled_surveys";

    //needed for creating the call log
    private Context context;

    /**
     * Location data table.  Contains longitude, latitude, uploaded (to mark
     * whether or not each record has been sent to the web server), and time.
     * 
     * @author Diego Vargas
     * @author Vladimir Costescu
     */
    public static final class GPSTable implements BaseColumns {
        // This class cannot be instantiated
        private GPSTable() {}

        public static final String DEFAULT_SORT_ORDER = "modified DESC";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";
        public static final String TIME = "time";
        public static final String UPLOADED = "uploaded";

        private static String createSql() {
        	return "CREATE TABLE " + GPS_TABLE_NAME + " (" 
        	+ GPSTable._ID			+ " INTEGER PRIMARY KEY AUTOINCREMENT," 
            + GPSTable.LONGITUDE	+ " DOUBLE,"
            + GPSTable.LATITUDE		+ " DOUBLE," 
            + GPSTable.TIME			+ " INTEGER,"
            + GPSTable.UPLOADED 	+ " INT UNSIGNED DEFAULT 0"
            						+ " );";
        }
    }
    
   /**
	* Call log data table.  Contains phone number, duration, uploaded (to mark
	* whether or not each record has been sent to the web server), call type,
	* and time (the time the call was made).
	* 
	* @author Diego Vargas
	* @author Vladimir Costescu
	*/
   public static final class CallLogTable implements BaseColumns {
        // This class cannot be instantiated
        private CallLogTable() {}

        public static final String DEFAULT_SORT_ORDER = "modified DESC";
        public static final String PHONE_NUMBER = "phone_number";
        public static final String CALL_TYPE = "call_type";
        public static final String DURATION = "duration";
        public static final String TIME = "time";
        public static final String UPLOADED = "uploaded";

        // Given an integer call type, return a string representation
        public static String getCallTypeString(int callType) {
            String stringCallType;
            
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
            
            return stringCallType;
        }
        
        private static String createSql() {
        	return "CREATE TABLE " + CALLLOG_TABLE_NAME + " ("
            + CallLogTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CallLogTable.PHONE_NUMBER + " TEXT,"
            + CallLogTable.CALL_TYPE + " TEXT," 
            + CallLogTable.DURATION + " INTEGER," 
            + CallLogTable.TIME + " TEXT,"
            + "uploaded INT UNSIGNED DEFAULT 0)";
        }
    }
   
   /**
    * Survey answers table.  Tracks what the phone user (subject) has answered
    * the administered surveys with.  Contains a question id, a created time,
    * uploaded (to mark whether each answer has been sent to the server), and
    * either a choice id or and answer text depending on the type of question.
    * 
    * @see com.peoples.android.model.Answer
    * 
    * @author Diego Vargas
    * @author Vladimir Costescu
    */
   public static final class AnswerTable implements BaseColumns {
    	public static final String QUESTION_ID = "question_id";
    	public static final String CHOICE_ID = "choice_id";
    	public static final String ANS_TEXT = "ans_text";
    	public static final String CREATED = "created";
    	public static final String UPLOADED = "uploaded";

    	private static String createSql() {
    		return "CREATE TABLE answers (" +
    				_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    				"question_id INT UNSIGNED NOT NULL," +
    				"choice_id INT UNSIGNED," +
    				"ans_text TEXT," +
    				"uploaded INT UNSIGNED DEFAULT 0," +
    				"created DATETIME);";
    	}
    }
   
   /**
    * Survey Branches table.  Contains a question id (the question a
    * branch belongs to), and a next question (the id of the question a
    * branch points to).
    * 
    * @see com.peoples.android.model.Branch
    * 
    * @author Diego Vargas
    * @author Vladimir Costescu
    */
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
    
    /**
     * Survey Choices table.  Contains the choice text and the question id that
     * each Choice belongs to.
     * 
     * @see com.peoples.android.model.Choice
     * 
     * @author Diego Vargas
     * @author Vladimir Costescu
     */
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
    
    /**
     * Survey Conditions table.  Contains the branch id that each Condition
     * belongs to, the question id that each Condition should check, the
     * choice id that that Question should be answered with, and the type of
     * check to do.
     * 
     * @see com.peoples.android.model.Condition
     * 
     * @author Diego Vargas
     * @author Vladimir Costescu
     */
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
    
    /**
     * Survey Questions table.  Contains the survey id each Questionbelongs
     * to and the text of each question.
     * 
     * @see com.peoples.android.model.Question
     * 
     * @author Diego Vargas
     * @author Vladimir Costescu
     */
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
    
    /**
     * Surveys table.  Contains the survey name, its creation date/time, the
     * first Questions id, and 7 fields to hold a list of times as 4 digit,
     * comma separated integers that correspond to the times of the day that
     * each Survey should be given on each day of the week.
     * 
     * @see come.peoples.android.model.Survey
     * 
     * @author Diego Vargas
     * @author Vladimir Costescu
     */
    public static final class SurveyTable implements BaseColumns {
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
    
    /**
     * Table tracking surveys that were scheduled to happen but were rejected
     * by the phone user to be asked at a later time.  Contains the surveys id,
     * the time it was originally supposed to be complete, and how many times
     * it has been skipped.
     * 
     * @author Diego Vargas
     */
    public static final class ScheduledSurveys implements BaseColumns {
    	 
    	public static final String SURVEY_ID	 = "survey_id";
    	public static final String ORIGINAL_TIME = "original_time";
    	public static final String SKIPPED		 = "survey_skipped";
    	
    	private static String createSql() {
    		
    		return "CREATE TABLE " + SS_TABLE_NAME + " (" +
    				_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    				SURVEY_ID + " INT UNSIGNED NOT NULL, " +
    				ORIGINAL_TIME + " LONG UNSIGNED NOT NULL, " +
    				SKIPPED	+ " BOOLEAN NOT NULL);";
    		
    	}
    }
    

    @Override
    public void onCreate(SQLiteDatabase db) {
    	AnswerTable.createSql();
    	Log.d(TAG, "onCreate");

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
            db.execSQL(ScheduledSurveys.createSql());

            buildInitialCallLog(db);

            db.setTransactionSuccessful();
        }

        finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + GPS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CALLLOG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ANSWER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BRANCH_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CHOICE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CONDITION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QUESTION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SURVEY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SS_TABLE_NAME);
        
        onCreate(db);
    }

    //check the phone's call records and copy them into the PEOPLES database
    private void buildInitialCallLog(SQLiteDatabase db) {
        Cursor c = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI,
                                                      null, null, null,
                                                      android.provider.CallLog.Calls.DATE);

        // Retrieve the column-indices of phoneNumber, date and calltype
        int numberColumn = c.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
        int dateColumn = c.getColumnIndex(android.provider.CallLog.Calls.DATE);

        // type can be: Incoming, Outgoing or Missed
        int typeColumn = c.getColumnIndex(android.provider.CallLog.Calls.TYPE);

        // Loop through all entries the cursor provides to us.
        if (c.moveToFirst()) {
            do {
                String callerPhoneNumber = c.getString(numberColumn);
                String callDate = c.getString(dateColumn);
                int callType = c.getInt(typeColumn);
                String stringCallType;

                ContentValues call = new ContentValues();

                stringCallType = CallLogTable.getCallTypeString(callType);

                call.put(CallLogTable.PHONE_NUMBER, callerPhoneNumber);
                call.put(CallLogTable.CALL_TYPE, stringCallType);
                call.put(CallLogTable.TIME, callDate);

                db.insert(CALLLOG_TABLE_NAME, null, call);

            } while (c.moveToNext());
        }

        c.close();
    }


    /**
     * Create the database object.
     *
     * @param context - Android Context; needed to create the call log
     */
    public PeoplesDB(Context context){
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.d(TAG, "in constructor");
    }

    //for debugging; want to be able to print out when database is accessed
    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
    	if(D) Log.d(TAG, "getReadable");
    	return super.getReadableDatabase();
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
    	if(D) Log.d(TAG, "getWriteable");
    	return super.getWritableDatabase();
    }
    
}
