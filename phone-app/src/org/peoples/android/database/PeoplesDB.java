/*---------------------------------------------------------------------------*
 * PeoplesDB.java                                                            *
 *                                                                           *
 * Contains information about the set up of the PEOPLES SQLite database and  *
 * methods to create and update that database.                               *
 *---------------------------------------------------------------------------*/
package org.peoples.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import org.peoples.android.Config;

/**
 * Represents the SQLite database to hold information for the app.  Contains
 * functions to create and upgrade the database, as well as public fields that
 * contain the names of the tables and columns. When another class needs to use
 * the database, they should use these constants.  Subclasses represent the
 * various tables.  Each implements {@link BaseColumns} by having a unique id
 * field.
 *
 * @see BaseColumns
 * @see LocationTable
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
 * @author Tony Xiao
 */
public class PeoplesDB extends SQLiteOpenHelper
{
    private static final String TAG = "PeoplesDB";

    //Change the version number here to force the database to
    //update itself.  This throws out all data.
    private static final String DATABASE_NAME = "peoples.db";
    private static final int DATABASE_VERSION = 2;

    //table names
    public static final String LOCATION_TABLE_NAME = "locations";
    public static final String CALLLOG_TABLE_NAME = "calls";
    public static final String ANSWER_TABLE_NAME = "answers";
    public static final String BRANCH_TABLE_NAME = "branches";
    public static final String CHOICE_TABLE_NAME = "choices";
    public static final String CONDITION_TABLE_NAME = "conditions";
    public static final String QUESTION_TABLE_NAME = "questions";
    public static final String SURVEY_TABLE_NAME = "surveys";

    //for convenience
    public static final String[] TABLES = {LOCATION_TABLE_NAME,
    	CALLLOG_TABLE_NAME, ANSWER_TABLE_NAME, BRANCH_TABLE_NAME,
    	CHOICE_TABLE_NAME, CONDITION_TABLE_NAME, QUESTION_TABLE_NAME,
    	SURVEY_TABLE_NAME};

    //needed for creating the call log
    //private Context context;

    /**
     * Location data table.  Contains longitude, latitude, uploaded (to mark
     * whether or not each record has been sent to the web server), and time.
     *
     * @author Diego Vargas
     * @author Vladimir Costescu
     */
    public static final class LocationTable implements BaseColumns {
        // This class cannot be instantiated
        private LocationTable() {}

        public static final String DEFAULT_SORT_ORDER = "modified DESC";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";
        public static final String TIME = "time";

        private static String createSql() {
        	return "CREATE TABLE " + LOCATION_TABLE_NAME + " ("
        	+ LocationTable._ID			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + LocationTable.LONGITUDE	+ " DOUBLE,"
            + LocationTable.LATITUDE		+ " DOUBLE,"
            + LocationTable.TIME			+ " INTEGER"
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

        public static final class CallType {
            public static final int INCOMING = android.provider.CallLog.Calls.INCOMING_TYPE;
            public static final int OUTGOING = android.provider.CallLog.Calls.OUTGOING_TYPE;
            public static final int MISSED = android.provider.CallLog.Calls.MISSED_TYPE;
            public static final int INCOMING_TEXT = 4;
            public static final int OUTGOING_TEXT = 5;
        }

        // Given an integer call type, return a string representation
        public static String getCallTypeString(int callType) {
            String stringCallType;

            switch (callType) {
                case CallType.INCOMING:
                    stringCallType = "Incoming";
                    break;
                case CallType.MISSED:
                    stringCallType = "Missed";
                    break;
                case CallType.OUTGOING:
                    stringCallType = "Outgoing";
                    break;
                case CallType.INCOMING_TEXT:
                    stringCallType = "Incoming Text";
                    break;
                case CallType.OUTGOING_TEXT:
                    stringCallType = "Outgoing Text";
                    break;
                default:
                    stringCallType = "";
                    break;
            }

            return stringCallType;
        }

        public static boolean hasDuration(int callType) {
        	switch (callType) {
        		case CallType.INCOMING:
        			return true;
        		case CallType.OUTGOING:
        			return true;
        		default:
        			return false;
        	}
        }

        private static String createSql() {
        	return "CREATE TABLE " + CALLLOG_TABLE_NAME + " ("
            + CallLogTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CallLogTable.PHONE_NUMBER + " TEXT,"
            + CallLogTable.CALL_TYPE + " TEXT,"
            + CallLogTable.DURATION + " INTEGER,"
            + CallLogTable.TIME + " TEXT)";
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
    	public static final String ANS_TYPE = "ans_type";
    	public static final String CHOICE_IDS = "choice_ids";
    	public static final String ANS_VALUE = "ans_value";
    	public static final String ANS_TEXT = "ans_text";
    	public static final String CREATED = "created";
    	public static final String UPLOADED = "uploaded";

    	/** Answer types */
    	public static final int CHOICE = 0;
    	public static final int VALUE = 1;
    	public static final int TEXT = 2;

    	private static String createSql() {
    		return "CREATE TABLE answers (" +
    				_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    				"question_id INT UNSIGNED NOT NULL," +
    				"choice_ids TEXT," +
    				"ans_text TEXT," +
    				"ans_value INT," +
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
    	public static final String CHOICE_TYPE = "choice_type";
    	public static final String CHOICE_TEXT = "choice_text";
    	public static final String CHOICE_IMG = "choice_img";
    	public static final String QUESTION_ID = "question_id";

    	/** Choice types */
    	public static final int TEXT_CHOICE = 0;
    	public static final int IMG_CHOICE = 1;

    	private static String createSql() {
    		return "CREATE TABLE choices (" +
    				_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    				"choice_type INT UNSIGNED NOT NULL," +
    				"choice_text VARCHAR(255)," +
    				"choice_img BLOB," +
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
     * Survey Questions table.  Contains the survey id each Question belongs
     * to, the type of question, and the text of each question.
     *
     * @see com.peoples.android.model.Question
     *
     * @author Diego Vargas
     * @author Vladimir Costescu
     */
    public static final class QuestionTable implements BaseColumns {
    	public static final String SURVEY_ID = "survey_id";
    	public static final String Q_TEXT = "q_text";
    	public static final String Q_TYPE = "q_type";
    	public static final String Q_SCALE_IMG_LOW = "q_img_low";
    	public static final String Q_SCALE_IMG_HIGH = "q_img_high";
    	public static final String Q_SCALE_TEXT_LOW = "q_text_low";
    	public static final String Q_SCALE_TEXT_HIGH = "q_text_high";

    	/** Question types */
    	public static final int SINGLE_CHOICE = 0;
    	public static final int MULTI_CHOICE = 1;
    	public static final int SCALE_TEXT = 2;
    	public static final int SCALE_IMG = 3;
    	public static final int FREE_RESPONSE = 4;

    	private static String createSql() {
    		return "CREATE TABLE questions (" +
    				_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
    				"survey_id INT UNSIGNED NOT NULL," +
    				"q_text TEXT," +
    				"q_img_low TEXT," +
    				"q_img_high TEXT," +
    				"q_text_low TEXT," +
    				"q_text_high TEXT," +
    				"q_type INT NOT NULL);";
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

    	//for convenience
    	public static final String[] DAYS = {SU, MO, TU, WE, TH, FR, SA, SA};

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
    	Log.d(TAG, "onCreate");

        db.beginTransaction();

        try {
            db.execSQL(LocationTable.createSql());
            db.execSQL(CallLogTable.createSql());
            db.execSQL(AnswerTable.createSql());
            db.execSQL(BranchTable.createSql());
            db.execSQL(ChoiceTable.createSql());
            db.execSQL(ConditionTable.createSql());
            db.execSQL(QuestionTable.createSql());
            db.execSQL(SurveyTable.createSql());

            //buildInitialCallLog(db);

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
        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CALLLOG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ANSWER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BRANCH_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CHOICE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CONDITION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QUESTION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SURVEY_TABLE_NAME);

        onCreate(db);
    }

    //check the phone's call records and copy them into the PEOPLES database
    //TODO evaluate the usefulness of this
//    private void buildInitialCallLog(SQLiteDatabase db) {
//        Cursor c = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI,
//                                                      null, null, null,
//                                                      android.provider.CallLog.Calls.DATE);
//
//        // Retrieve the column-indices of phoneNumber, date and calltype
//        int numberColumn = c.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
//        int dateColumn = c.getColumnIndex(android.provider.CallLog.Calls.DATE);
//
//        // type can be: Incoming, Outgoing or Missed
//        int typeColumn = c.getColumnIndex(android.provider.CallLog.Calls.TYPE);
//
//        // Loop through all entries the cursor provides to us.
//        if (c.moveToFirst()) {
//            do {
//                String callerPhoneNumber = c.getString(numberColumn);
//                String callDate = c.getString(dateColumn);
//                int callType = c.getInt(typeColumn);
//                String stringCallType;
//
//                ContentValues call = new ContentValues();
//
//                stringCallType = CallLogTable.getCallTypeString(callType);
//
//                call.put(CallLogTable.PHONE_NUMBER, callerPhoneNumber);
//                call.put(CallLogTable.CALL_TYPE, stringCallType);
//                call.put(CallLogTable.TIME, callDate);
//
//                db.insert(CALLLOG_TABLE_NAME, null, call);
//
//            } while (c.moveToNext());
//        }
//
//        c.close();
//    }


    /**
     * Create the database object.
     *
     * @param context - Android Context; needed to create the call log
     */
    public PeoplesDB(Context context){
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //this.context = context;
        Log.d(TAG, "in constructor");
    }

    //for debugging; want to be able to print out when database is accessed
    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
    	if(Config.D) Log.d(TAG, "getReadable");
    	return super.getReadableDatabase();
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
    	if(Config.D) Log.d(TAG, "getWriteable");
    	return super.getWritableDatabase();
    }

}
