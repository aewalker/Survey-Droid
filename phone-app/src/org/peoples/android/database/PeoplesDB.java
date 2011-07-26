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
 * @see ExtrasTable
 * @see StatusTable
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
    private static final int DATABASE_VERSION = 6;

    //table names
    public static final String TAKEN_TABLE_NAME = "surveysTaken";
    public static final String STATUS_TABLE_NAME = "statusChanges";
    public static final String LOCATION_TABLE_NAME = "locations";
    public static final String CALLLOG_TABLE_NAME = "calls";
    public static final String ANSWER_TABLE_NAME = "answers";
    public static final String BRANCH_TABLE_NAME = "branches";
    public static final String CHOICE_TABLE_NAME = "choices";
    public static final String CONDITION_TABLE_NAME = "conditions";
    public static final String QUESTION_TABLE_NAME = "questions";
    public static final String SURVEY_TABLE_NAME = "surveys";
    public static final String EXTRAS_TABLE_NAME = "extras";
    
    /** Contains the names of all the tables declared in PeoplesDB. */
    public static final String[] TABLE_NAMES = {LOCATION_TABLE_NAME,
    	CALLLOG_TABLE_NAME, ANSWER_TABLE_NAME, BRANCH_TABLE_NAME,
    	CHOICE_TABLE_NAME, CONDITION_TABLE_NAME, QUESTION_TABLE_NAME,
    	SURVEY_TABLE_NAME, EXTRAS_TABLE_NAME, STATUS_TABLE_NAME,
    	TAKEN_TABLE_NAME};
    
    /**
     * Class from which all the other tables in this class should inherit
     * from.  Makes iterating over all the tables possible.
     * 
     * This should be treated as though it were abstract, though it cannot be
     * because you can't mix static and abstract for some reason...
     */
    private static class PEOPLESTable implements BaseColumns {
    	//class cannot be instantiated
    	private PEOPLESTable() {}
    	
    	/**
    	 * Get SQL that should be used to create this table.  Note that this
    	 * method (and it's counterparts in all of the other classes) have
    	 * SuppressWarnings("unused") because this method is never called
    	 * directly (only through Method.invoke()).
    	 * 
    	 * @return SQL as a string that should be executed
    	 */
    	@SuppressWarnings("unused")
		private static String createSql() {
			return null;
		}
    }
    
    /**
     * Table holding information about each instance where a survey was taken
     * by or displayed to a user.
     * 
     * @author Austin Walker
     */
    public static final class TakenTable extends PEOPLESTable {
    	public static final String CREATED = "created";
    	public static final String SURVEY_ID = "survey_id";
    	public static final String STATUS = "status";
    	public static final String UPLOADED = "uploaded";
    	
    	//status types
    	public static final int SURVEYS_DISABLED_LOCALLY = 0;
    	public static final int SURVEYS_DISABLED_SERVER = 1;
    	public static final int USER_INITIATED_FINISHED = 2;
    	public static final int USER_INITIATED_UNFINISHED = 3;
    	public static final int SCHEDULED_FINISHED = 4;
    	public static final int SCHEDULED_UNFINISHED = 5;
    	public static final int SCHEDULED_DISMISSED = 6;
    	public static final int SCHEDULED_IGNORED = 7;
    	public static final int RANDOM_FINISHED = 8;
    	public static final int RANDOM_UNFINISHED = 9;
    	public static final int RANDOM_DISMISSED = 10;
    	public static final int RANDOM_IGNORED = 11;
    	
		private static String createSql() {
    		return "CREATE TABLE " + TAKEN_TABLE_NAME + " ("
    		+ TakenTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
    		+ TakenTable.SURVEY_ID + " INTEGER,"
    		+ TakenTable.STATUS + " INTEGER,"
    		+ TakenTable.UPLOADED + " INTEGER DEFAULT 0,"
    		+ TakenTable.CREATED + " INTEGER);";
    	}
    }
    
    /**
     * Table to hold application status changes that need to be sent up the
     * server.  Tracks on/off status of surveys, location tracking, and call
     * logging.
     * 
     * @author Austin Walker
     */
    public static final class StatusTable extends PEOPLESTable {
    	public static final String CREATED = "created";
    	public static final String STATUS = "status";
    	public static final String TYPE = "type";
    	
    	//status types:
    	public static final int STATUS_ON = 1;
    	public static final int STATUS_OFF = 0;
    	
    	//feature types (for TYPE):
    	public static final int LOCATION_TRACKING = 0;
    	public static final int CALL_LOGGING = 1;
    	public static final int TEXT_LOGGING = 2;
    	public static final int SURVEYS = 3;
    	
		private static String createSql() {
    		return "CREATE TABLE " + STATUS_TABLE_NAME + " ("
    		+ StatusTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
    		+ StatusTable.TYPE + " INTEGER,"
    		+ StatusTable.STATUS + " INTEGER,"
    		+ StatusTable.CREATED + " INTEGER);";
    	}
    }

    /**
     * Location data table.  Contains longitude, latitude, uploaded (to mark
     * whether or not each record has been sent to the web server), and time.
     *
     * @author Diego Vargas
     * @author Vladimir Costescu
     */
    public static final class LocationTable extends PEOPLESTable {
        // This class cannot be instantiated
        private LocationTable() {}

        public static final String DEFAULT_SORT_ORDER = "modified DESC";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";
        public static final String ACCURACY = "accuracy";
        public static final String TIME = "time";

		private static String createSql() {
        	return "CREATE TABLE " + LOCATION_TABLE_NAME + " ("
        	+ LocationTable._ID			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + LocationTable.LONGITUDE	+ " DOUBLE,"
            + LocationTable.LATITUDE		+ " DOUBLE,"
            + LocationTable.ACCURACY		+ " DOUBLE,"
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
   public static final class CallLogTable extends PEOPLESTable {
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
    * @see com.peoples.android.survey.Answer
    * 
    * @author Diego Vargas
    * @author Vladimir Costescu
    */
   public static final class AnswerTable extends PEOPLESTable {
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
    				QUESTION_ID + " INT UNSIGNED NOT NULL," +
    				ANS_TYPE + " INT," +
    				CHOICE_IDS + " TEXT," +
    				ANS_TEXT + " TEXT," +
    				ANS_VALUE + " INT," +
    				UPLOADED + " INT UNSIGNED DEFAULT 0," +
    				CREATED + " DATETIME);";
    	}
    }

   /**
    * Contains survey extras: photos and voice recordings that have been
    * submitted as part of a survey.  Contains the survey id of each item,
    * as well as the base 64 photo and/or voice recording.  Also includes the
    * time of creation and whether or not it has been uploaded to the server.
    * 
    * @author Austin Walker
    */
   public static final class ExtrasTable extends PEOPLESTable {
	   public static final String SURVEY_ID ="survey_id";
	   public static final String PHOTO = "photo";
	   public static final String VOICE = "voice";
	   public static final String CREATED = "created";
	   public static final String UPLOADED = "uploaded";
	   
	   private static String createSql() {
		   	return "CREATE TABLE " + EXTRAS_TABLE_NAME + " (" +
		   		_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		   		SURVEY_ID + " INT UNSIGNED NOT NULL, " +
		   		PHOTO + " TEXT, " + VOICE + " TEXT, " +
		   		CREATED + " DATETIME, " +
		   		UPLOADED + "INT UNSIGNED DEFAULT 0);";
	   }
   }
   
   /**
    * Survey Branches table.  Contains a question id (the question a
    * branch belongs to), and a next question (the id of the question a
    * branch points to).
    * 
    * @see com.peoples.android.survey.Branch
    * 
    * @author Diego Vargas
    * @author Vladimir Costescu
    */
    public static final class BranchTable extends PEOPLESTable {
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
     * @see com.peoples.android.survey.Choice
     * 
     * @author Diego Vargas
     * @author Vladimir Costescu
     */
    public static final class ChoiceTable extends PEOPLESTable {
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
     * @see com.peoples.android.survey.Condition
     * 
     * @author Diego Vargas
     * @author Vladimir Costescu
     */
    public static final class ConditionTable extends PEOPLESTable {
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
     * @see com.peoples.android.survey.Question
     * 
     * @author Diego Vargas
     * @author Vladimir Costescu
     */
    public static final class QuestionTable extends PEOPLESTable {
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
     * @see come.peoples.android.survey.Survey
     * 
     * @author Diego Vargas
     * @author Vladimir Costescu
     */
    public static final class SurveyTable extends PEOPLESTable {
    	public static final String NAME = "name";
    	public static final String CREATED = "created";
    	public static final String QUESTION_ID = "question_id";
    	public static final String SUBJECT_INIT = "subject_init";

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
    				"subject_init INT UNSIGNED NOT NULL," +
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
        	db.execSQL(ExtrasTable.createSql());
        	db.execSQL(StatusTable.createSql());
        	db.execSQL(TakenTable.createSql());

            db.setTransactionSuccessful();
        } catch (Exception e) {
        	Log.e(TAG, e.toString());
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.i(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        for (String table : TABLE_NAMES)
        {
        	db.execSQL("DROP TABLE IF EXISTS " + table);
        }
        onCreate(db);
    }

    /**
     * Create the database object.
     *
     * @param context - Android Context; needed to create the call log
     */
    public PeoplesDB(Context context) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
