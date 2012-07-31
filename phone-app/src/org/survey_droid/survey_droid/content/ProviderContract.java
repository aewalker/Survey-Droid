/*---------------------------------------------------------------------------*
 * ProviderContract.java                                                     *
 *                                                                           *
 * Class for constants that define the "contract" that the Survey Droid      *
 * content provider provides.                                                *
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
package org.survey_droid.survey_droid.content;

import org.survey_droid.survey_droid.annotation.Options;

import android.provider.BaseColumns;

import static org.survey_droid.survey_droid.annotation.Options.ID;
import static org.survey_droid.survey_droid.annotation.Options.ENUM;
import static org.survey_droid.survey_droid.annotation.Options.BOOL;

/**
 * Class of constants that together define the "contract" that can be used with
 * the {@link SurveyDroidContentProvider} to access and manipulate Survey Droid
 * data.
 * 
 * @author Austin Walker
 */
public final class ProviderContract
{
	/** The authority used by the provider */
	public static final String AUTHORITY = "org.survey_droid.survey_droid.provider";
	
	//These marker interfaces are basically used to make it easy to deal with
	//all of these tables in a loop via reflection.
	/**
	 * Marker interface that indicates that a class defines the way a data
	 * table is laid out.  Each class implementing this interface is
	 * guaranteed to have
	 * <ul>
	 * <li>A <code>NAME</code> field that holds the name of the table as it
	 * is used for both database calls and URIs passed to the
	 * {@link SurveyDroidContentProvider}</li>
	 * <li>An internal <code>Fields</code> class that implements the
	 * {@link BaseColumns} interface.</li>
	 * </ul>
	 */
	public interface DataTableContract { }
	
	/**
	 * Defines the way that records of surveys that have been taken (or missed,
	 * quit, etc.) are laid out.
	 */
	public static final class SurveysTakenTable implements DataTableContract
	{
		public static final String NAME = "surveysTaken";
		
		public static final class Fields implements BaseColumns
		{
			/** The date/time this record was created */
			@Options("DATETIME")
	    	public static final String CREATED = "created";
	    	/** The id of the survey that this record refers to */
			@Options(ID)
	    	public static final String SURVEY_ID = "survey_id";
			/** the id of the study this answer belongs to */
			@Options(ID)
			public static final String STUDY_ID = "study_id";
	    	/** see {@link SurveyCompletionCode} */
			@Options(ENUM)
	    	public static final String STATUS = "status";
	    	/**
	    	 * The percentage of weekly surveys that have been completed as of
	    	 * the completion of this survey record
	    	 */
			@Options("INTEGER")
	    	public static final String RATE = "rate";
	    	/** Marks whether a record has been uploaded to the server */
			@Options(BOOL)
	    	public static final String UPLOADED = "uploaded";
		}
    	
    	/**
    	 * Enumeration of all the different possible statuses which a survey
    	 * instance can be marked as.
    	 */
		//note that the order these are declared in is backwards compatible
    	public enum SurveyCompletionCode
    	{
	    	SURVEYS_DISABLED_LOCALLY,
	    	SURVEYS_DISABLED_SERVER,
	    	USER_INITIATED_FINISHED,
	    	USER_INITIATED_UNFINISHED,
	    	SCHEDULED_FINISHED,
	    	SCHEDULED_UNFINISHED,
	    	SCHEDULED_DISMISSED,
	    	SCHEDULED_IGNORED,
	    	RANDOM_FINISHED,
	    	RANDOM_UNFINISHED,
	    	RANDOM_DISMISSED,
	    	RANDOM_IGNORED,
	    	CALL_INITIATED_FINISHED,
	    	CALL_INITIATED_UNFINISHED,
	    	CALL_INITIATED_DISMISSED,
	    	CALL_INITIATED_IGNORED,
	    	LOCATION_BASED_FINISHED,
	    	LOCATION_BASED_UNFINISHED,
	    	LOCATION_BASED_DISMISSED,
	    	LOCATION_BASED_IGNORED;
    	}
    }
    
    /**
     * Defines how the enabling/disabling of application features is recorded.
     */
    public static final class StatusTable implements DataTableContract
    {
		public static final String NAME = "statusChanges";
		
		public static final class Fields implements BaseColumns
		{
			/** The date/time this record was created */
			@Options("DATETIME")
	    	public static final String CREATED = "created";
	    	/**
	    	 * Was the feature turned on or off?
	    	 * 
	    	 * @see StatusTable#STATUS_ON
	    	 * @see StatusTable#STATUS_OFF
	    	 */
			@Options(ENUM)
	    	public static final String STATUS = "status";
	    	/**
	    	 * The feature that was turned on or off.
	    	 * 
	    	 * @see StatusTable#Feature
	    	 */
			@Options(ENUM)
	    	public static final String FEATURE = "type";
		}
    	
		/**
		 * Application feature types used for {@link Fields#FEATURE}
		 */
		//backwards compatible
		public enum Feature
		{
			LOCATION_TRACKING,
			CALL_LOGGING,
			TEXT_LOGGING,
			SURVEYS;
		}
		
    	//status types:
		/**
		 * Indicates that a feature was turned on; for use with
		 * {@link Fields#STATUS}
		 */
    	public static final int STATUS_ON = 1;

		/**
		 * Indicates that a feature was turned off; for use with
		 * {@link Fields#STATUS}
		 */
    	public static final int STATUS_OFF = 0;
    }

    /**
     * Defines how location information is recorded.
     */
    public static final class LocationTable implements DataTableContract
    {
    	public static final String NAME = "locations";
    	
    	public static final class Fields implements BaseColumns
    	{
    		/** Longitude in decimal degrees */
    		@Options("REAL")
	        public static final String LONGITUDE = "longitude";
	        /** Latitude in decimal degrees */
    		@Options("REAL")
	        public static final String LATITUDE = "latitude";
	        /** Maximum error of this location in meters */
    		@Options("REAL")
	        public static final String ACCURACY = "accuracy";
	        /** Date/time that this location was captured */
    		@Options("DATETIME")
	        public static final String TIME = "time";
    	}
    }

   /**
	* Defines how call and text logs are recorded.
	*/
   public static final class CallLogTable implements DataTableContract
   {
	   public static final String NAME = "calls";
	   
	   public static final class Fields implements BaseColumns
	   {
		   /** The phone number this call/text was from/to */
		   @Options("TEXT")
		   public static final String PHONE_NUMBER = "phone_number";
		   /** see {@link ContactType} */
		   @Options(ENUM)
		   public static final String CALL_TYPE = "type";
		   /** How long the call lasted (unused for texts and missed calls) */
		   @Options("INTEGER")
		   public static final String DURATION = "duration";
		   /** When the contact started */
		   @Options("DATETIME")
		   public static final String TIME = "time";
		   /**
		    * Contains a list of all the study ids for which this contact has
		    * yet to be uploaded to (comma separated).
		    */
		   @Options("TEXT")
		   //TODO this might not be such a good idea
		   public static final String UPLOAD_TO = "upload_to";
	   }
	   
	   /** Defines different types of contacts */
	   /*
	    * WARNING!
	    * 
	    * This is NOT backwards compatible.  The reason for this is twofold:
	    * 1. The previous version used android.provider.CallLog.Calls'
	    *    INCOMING_TYPE, OUTGOING_TYPE, and MISSED_TYPE.  These started at
	    *    1 instead of 0, making them unsuitable for an enum.
	    * 2. These fields might change, so it is best to force application
	    *    components to translate.
	    */
	   //outgoing text is reserved for future use
	   public enum ContactType
	   {
		   INCOMING_CALL("incoming call"),
		   OUTGOING_CALL("outgoing call"),
		   MISSED_CALL("missed call"),
		   INCOMING_TEXT("incoming text"),
		   OUTGOING_TEXT("outgoing text");
		   
		   private String s;
		   
		   ContactType(String s) { this.s = s; }
		   
		   @Override
		   public String toString() { return s; }
		   
		   /** @return true if this contact type has a duration */
		   public boolean hasDuration()
		   {
			   if (this.equals(INCOMING_CALL) || this.equals(OUTGOING_CALL))
				   return true;
			   return false;
		   }
	   }
    }

   /**
    * Survey answers table.  Tracks what the phone user (subject) has answered
    * the administered surveys with.  Contains a question id, a created time,
    * uploaded (to mark whether each answer has been sent to the server), and
    * either a choice id or and answer text depending on the type of question.
    */
	public static final class AnswerTable implements DataTableContract
	{
		public static final String NAME = "answers";
		
		public static final class Fields implements BaseColumns
		{
			/** the id of the study this question belongs to */
			@Options(ID)
			public static final String STUDY_ID = "study_id";
			/** the id of the question answered */
			@Options(ID)
			public static final String QUESTION_ID = "question_id";
			/** the data that was given as an answer */
			@Options("BLOB")
	    	public static final String ANS_VALUE = "ans_value";
	    	/** when the answer was given */
			@Options("DATETIME")
	    	public static final String CREATED = "created";
	    	/** marks an answer as having been uploaded to the study server */
			@Options(BOOL)
	    	public static final String UPLOADED = "uploaded";
		}
    }
	
   /**
    * Survey Branches table.  Contains a question id (the question a
    * branch belongs to), and a next question (the id of the question a
    * branch points to).
    */
	public static final class BranchTable implements DataTableContract
	{
		public static final String NAME = "branches";
		
		public static final class Fields implements BaseColumns
		{
			/** this branch's id as assigned by the server */
			@Options(ID)
			public static final String BRANCH_ID = "branch_id";
			/** the study this question belongs to */
			@Options(ID)
			public static final String STUDY_ID = "study_id";
			/** this question's id */
			@Options(ID)
			public static final String QUESTION_ID = "question_id";
			/** the id of the next question along this branch */
			@Options(ID)
	    	public static final String NEXT_Q = "next_q";
		}
    }

    /**
     * Survey Conditions table.  Contains the branch id that each Condition
     * belongs to, the question id that each Condition should check, the
     * choice id that that Question should be answered with, and the type of
     * check to do.
     */
    public static final class ConditionTable implements DataTableContract
    {
    	public static final String NAME = "conditions";
    	
    	public static final class Fields implements BaseColumns
    	{
    		/** id of this condition as assigned by the server */
    		@Options(ID)
    		public static final String CONDITION_ID = "condition_id";
    		/** id of the study this condition belongs to */
    		@Options(ID)
    		public static final String STUDY_ID = "study_id";
    		/** id of the branch this study belongs to */
    		@Options(ID)
    		public static final String BRANCH_ID = "branch_id";
    		/** id of the question this condition refers to */
    		@Options(ID)
        	public static final String QUESTION_ID = "question_id";
        	/** type of condition to check; see {@link ConditionType} */
    		@Options(ENUM)
        	public static final String TYPE = "type";
        	/** scope of this condition; see {@link ConditionScope} */
    		@Options(ENUM)
        	public static final String SCOPE = "scope";
        	/** type of data comparison to do; see {@link DataType} */
    		@Options(ENUM)
        	public static final String DATA_TYPE = "data_type";
    		/** The data to use in comparison */
    		@Options("BLOB")
    		public static final String DATA = "data";
    		/** The offset into the data array to use */
    		@Options("INTEGER NOT NULL DEFAULT 0")
    		public static final String OFFSET = "offset";
    	}
    }

    /**
     * Survey Questions table.  Contains the survey id each Question belongs
     * to, the type of question, and the text of each question.
     */
    public static final class QuestionTable implements DataTableContract
    {
    	public static final String NAME = "questions";
    	
    	public static final class Fields implements BaseColumns
    	{
    		/** the id given to this question by the server */
    		@Options(ID)
    		public static final String QUESTION_ID = "question_id";
    		/** the study this question belongs to */
    		@Options(ID)
    		public static final String STUDY_ID = "study_id";
    		/** the survey this question belongs to */
    		@Options(ID)
    		public static final String SURVEY_ID = "survey_id";
    		/** the data provided for this question */
    		@Options("BLOB")
    		public static final String DATA = "data";
    		/**
    		 * the name of the package that contains the activity that can
    		 * display this question
    		 */
    		@Options("TEXT NOT NULL")
    		public static final String PACKAGE = "package";
    		/** class name of the activity that can display this question */
    		@Options("TEXT NOT NULL")
    		public static final String CLASS = "class";
    	}
    }
    
    /**
     * Surveys table.  Contains the survey name, its creation date/time, the
     * first Questions id, and 7 fields to hold a list of times as 4 digit,
     * comma separated integers that correspond to the times of the day that
     * each Survey should be given on each day of the week.
     */
    public static final class SurveyTable implements DataTableContract
    {
    	public static final String NAME = "surveys";
    	
    	public static final class Fields implements BaseColumns
    	{
    		/** the survey's name */
    		@Options("TEXT")
    		public static final String NAME = "name";
    		/** the id of the study that this survey is part of */
    		@Options(ID)
    		public static final String STUDY_ID = "study_id";
    		/** the id of the first question in the survey */
    		@Options(ID)
        	public static final String QUESTION_ID = "question_id";
    		/** the id of the survey within the study */
    		@Options(ID)
    		public static final String SURVEY_ID = "survey_id";
    		/** whether or not users can initiate this survey manually */
    		@Options(BOOL)
        	public static final String SUBJECT_INIT = "subject_init";
    		/**
    		 * whether or not calls from previously seen numbers trigger this
    		 * survey
    		 */
    		@Options(BOOL)
        	public static final String OLD_CALLS = "old_calls";
    		/**
    		 * whether or not calls from numbers that have not previously been
    		 * seen trigger this survey
    		 */
    		@Options(BOOL)
        	public static final String NEW_CALLS = "new_calls";
    		/**
    		 * whether or not texts from previously seen numbers trigger this
    		 * survey
    		 */
    		@Options(BOOL)
        	public static final String OLD_TEXTS = "old_texts";
    		/**
    		 * whether or not texts from numbers that have not previously been
    		 * seen trigger this survey
    		 */
    		@Options(BOOL)
        	public static final String NEW_TEXTS = "new_texts";

    		@Options("TEXT")
        	public static final String MO = "mo";
    		@Options("TEXT")
        	public static final String TU = "tu";
    		@Options("TEXT")
        	public static final String WE = "we";
    		@Options("TEXT")
        	public static final String TH = "th";
    		@Options("TEXT")
        	public static final String FR = "fr";
    		@Options("TEXT")
        	public static final String SA = "sa";
    		@Options("TEXT")
        	public static final String SU = "su";
    	}

    	/** for convenience, lists all the days from fields */
    	public static final String[] DAYS =
    	{
    		Fields.SU, Fields.MO, Fields.TU,
    		Fields.WE, Fields.TH, Fields.FR,
    		Fields.SA, Fields.SA
    	};
    }
    
    /**
     * Contains information about the different studies the user has entered.
     */
    public static final class StudyTable implements DataTableContract
    {
    	public static final String NAME = "studies";
    	
    	public static final class Fields implements BaseColumns
    	{
    		/** The study id (as reported by the server, not the local id) */
    		@Options(ID)
    		public static final String STUDY_ID = "study_id";
    		/** The name of this study */
    		@Options("TEXT")
    		public static final String NAME = "name";
    		/** The description of this study */
    		@Options("TEXT")
    		public static final String DESCRIPTION = "description";
    		/** The server this study is from */
    		@Options("TEXT NOT NULL")
    		public static final String SERVER = "server";
    		/**
    		 * marks whether or not this is a one-off study (consists of just
    		 * a single, one-time survey).
    		 */
    		@Options(BOOL)
    		public static final String ONE_OFF = "one_off";
    		/** whether or not this study tracks the user's calls */
    		@Options(BOOL)
    		public static final String TRACKS_CALLS = "tracks_calls";
    		/** whether or not this study tracks the user's texts */
    		@Options(BOOL)
    		public static final String TRACKS_TEXT = "tracks_texts";
    		/** whether or not this study tracks the user's location */
    		@Options(BOOL)
    		public static final String TRACKS_LOCATION = "tracks_location";
    		/**
    		 * If {@link #TRACKS_LOCATION} is true, this holds the granularity
    		 * (in minutes) at which this study will collect locations
    		 */
    		@Options("INTEGER")
    		public static final String LOCATION_INTERVAL = "location_interval";
    		
    		//TODO decide how to split fields between database and config 
    	}
    }
}
