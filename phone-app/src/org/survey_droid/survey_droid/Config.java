/*---------------------------------------------------------------------------*
 * Config.java                                                               *
 *                                                                           *
 * Holds configuration things (such as whether or not debugging is enabled.  *
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

import java.lang.reflect.Field;

//FIXME
import org.survey_droid.survey_droid.annotation.*;
import org.survey_droid.survey_droid.coms.*;
import org.survey_droid.survey_droid.content.*;
import org.survey_droid.survey_droid.survey.*;
import org.survey_droid.survey_droid.survey.questions.*;
import org.survey_droid.survey_droid.ui.*;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Holds static information about the current configuration, such as whether or
 * not debugging is enabled, etc.
 *
 * @author Austin Walker
 * @author Tony Xaio
 * @author Henry Liu
 */
public final class Config
{
	/** logging tag */
	private static final String TAG = "Config";
	
	/*
	 * This whole thing here is SOOOO bad, but Reflections won't compile on
	 * Android because javassist is being dumb.
	 */
	
	/** recursively find all ConfigKeys */
	private static void register(Class<?> clazz, Context c)
	{
		for (Class<?> subClass : clazz.getDeclaredClasses())
		{
			register(subClass, c);
		}
		Field[] fields = clazz.getDeclaredFields();
		SharedPreferences settings = c.getSharedPreferences(
    		CONFIG_FILE, Context.MODE_PRIVATE);
		for (Field field : fields)
		{
			ConfigKey ck = field.getAnnotation(ConfigKey.class);
			if (ck != null)
			{
				field.setAccessible(true);
				String key;
				try
				{
					key = (String) field.get(null);
				}
				catch (Exception e)
				{
					Util.e(null, TAG, "Failed to configure ConfigKey from class " + clazz.getName());
					Util.e(null, TAG, Util.fmt(e));
					continue;
				}
				String value = ck.value();
				boolean global = ck.global();
				SharedPreferences.Editor editor = settings.edit();
				if (!settings.contains(key) && global)
				{
					Util.v(null, TAG, "Writing starting key pair: \"" + key + "\":\"" + value + "\"");
					editor.putString(key, value);
				}
				if (!editor.commit())
					throw new RuntimeException("Unable to write to config file");
			}
		}
	}
	
	/**
	 * @param ctxt
	 */
	public static void init(Context ctxt)
	{
		//FIXME I really hope this isn't necessary. Sooooo painful...
		Util.i(null, TAG, "Config init");
		Class<?>[] classes = {
			//main package
			Base64Coder.class, BootIntentReceiver.class, CallReceiver.class, CallTracker.class,
			Config.class, Dispatcher.class, IncomingSMSTracker.class, LocationTracker.class,
			Study.class, SurveyDroid.class, Util.class,
			//annotation
			ConfigKey.class, Exported.class, Options.class,
			//coms
			ComsService.class, Pull.class, Push.class, WebClient.class,
			//content
			ComsDBHandler.class, ProviderContract.class, StatusDBHandler.class, SurveyDBHandler.class,
			SurveyDroidContentProvider.class, SurveyDroidDB.class, SurveyDroidDBHandler.class,
			TakenDBHandler.class, TrackingDBHandler.class,
			//survey
			Answer.class, Branch.class, Condition.class, ConfirmSubmitActivity.class,
			Question.class, QuestionActivity.class, Survey.class, SurveyConstructionException.class,
			SurveyDoneActivity.class, SurveyScheduler.class, SurveyService.class,
			//survey.questions
			Choice.class, ChoiceActivity.class, FreeResponseActivity.class, ImgScaleActivity.class,
			ScaleActivity.class, TextScaleActivity.class,
			//ui
			AbsVerticalSeekBar.class, IDActivity.class, ChoiceAdapter.class, JoinStudiesActivity.class,
			MainActivity.class, SettingsActivity.class, StudyAdapter.class, UserSurveysActivity.class,
			VerticalProgressBar.class, VerticalSeekBar.class
		};
		for (Class<?> clazz : classes)
		{
			register(clazz, ctxt);
		}
	}
	
	/* ----------------------- Internal Config fields ---------------------- */
	
	/** name of the file to write the config to */
	private static final String CONFIG_FILE = "sd.conf";
	
	/* ------------------------ Public access fields ----------------------- */
	
	/** Is debugging enabled? */
	public static boolean D = false;
	
	/* ------------------------ Hard Coded Settings ------------------------ */
	
	/** Format of survey times. */
	public static final String TIME_FORMAT = "HHmm";

	/** Format of survey days. */
	public static final String DAY_FORMAT = "EE";
	
	/* --------------------- Global Configuration Keys --------------------- */
	
	/** Does the user have surveys enabled? */
	@ConfigKey(value="true", global=true)
	public static final String SURVEYS_LOCAL = "surveys_local";
	
	/** Does the user have location tracking enabled? */
	@ConfigKey(value="true", global=true)
	public static final String TRACKING_LOCAL = "tracking_local";
	
	/** Does the user have call logging enabled? */
	@ConfigKey(value="true", global=true)
	public static final String CALL_LOG_LOCAL = "call_log_local";

	/** Does the user have text logging enabled? */
	@ConfigKey(value="true", global=true)
	public static final String TEXT_LOG_LOCAL = "text_log_local";

	/** Salting value for hashing phone number */
	@ConfigKey(value="", global=true) //TODO something better?  maybe drop the annotation?
	public static final String SALT = "salt";
	
	/* -------------------- Per Study Configuration Keys ------------------- */
	
	/** Does the server have surveys enabled? */
	@ConfigKey("true")
	public static final String SURVEYS_STUDY = "surveys_enabled";
	
	/** Does the server have location tracking enabled? */
	@ConfigKey("false")
	public static final String TRACKING_STUDY = "tracking_enabled";
	
	/** Does the server have call/text logging enabled? */
	@ConfigKey("false")
	public static final String CALL_LOG_STUDY = "call_log_enabled";
	
	/** Does the server have call/text logging enabled? */
	@ConfigKey("false")
	public static final String TEXT_LOG_STUDY = "text_log_enabled";
	
	
	


	


	//TODO this should be per-study
	/** Approximate time between location updates, in minutes */
	//public static String LOCATION_INTERVAL = "location_interval";
	//public static int LOCATION_INTERVAL_DEFAULT = 15;

	/** Should we allow the entry of blank free response answers? */
	public static final String ALLOW_BLANK_FREE_RESPONSE =
		"allow_blank_free_response";
	public static final boolean ALLOW_BLANK_FREE_RESPONSE_DEFAULT = true;

	/** Should we allow answering multiple choice with no answers? */
	public static final String ALLOW_NO_CHOICES = "allow_no_choices";
	public static final boolean ALLOW_NO_CHOICES_DEFAULT = false;

	/** Should the name/title of a survey be shown? */
	public static final String SHOW_SURVEY_NAME = "show_survey_name";
	public static final boolean SHOW_SURVEY_NAME_DEFAULT = true;
	
	/** What is the target percentage of surveys to have completed? */
	public static final String COMPLETION_GOAL = "completion_goal";
	public static final int COMPLETION_GOAL_DEFAULT = 75;
	
	
	/**
	 * How long should the system wait for a user to answer a question
	 * (in minutes)?
	 */
	public static final String QUESTION_TIMEOUT = "question_timeout";
	public static final int QUESTION_TIMEOUT_DEFAULT = 30;
	
	/**
	 * The number of different locations in which locations should be logged.
	 */
	public static final String NUM_LOCATIONS_TRACKED = "num_locations";
	
	/**
	 * Longitude for location tracked.  Since multiple locations can be set,
	 * make sure to append a number to the end of this. You can get the number
	 * of locations being tracked using {@link #NUM_LOCATIONS_TRACKED}.
	 */
	public static final String TRACKED_LONG = "lt_long";
	
	/**
	 * Latitude for location tracked.  Since multiple locations can be set,
	 * make sure to append a number to the end of this. You can get the number
	 * of locations being tracked using {@link #NUM_LOCATIONS_TRACKED}.
	 */
	public static final String TRACKED_LAT = "lt_lat";
	
	/**
	 * Radius for location tracked.  Since multiple locations can be set,
	 * make sure to append a number to the end of this. You can get the number
	 * of locations being tracked using {@link #NUM_LOCATIONS_TRACKED}.
	 */
	public static final String TRACKED_RADIUS = "lt_rad";
	
	/**
	 * The number of different times during which locations should be logged.
	 */
	public static final String NUM_TIMES_TRACKED = "times_tracked";
	
	/**
	 * Start of time tracked.  Since multiple times can be set,
	 * make sure to append a number to the end of this. You can get the number
	 * of times being tracked using {@link #NUM_TIMES_TRACKED}.
	 */
	public static final String TRACKED_START = "tt_start";
	
	/**
	 * End of time tracked.  Since multiple times can be set,
	 * make sure to append a number to the end of this. You can get the number
	 * of times being tracked using {@link #NUM_TIMES_TRACKED}.
	 */
	public static final String TRACKED_END = "tt_end";
	
	/**
	 * Use this to get custom data set by the study admin.  To get a specific
	 * key, append "#key_name" to this value.
	 */
	public static final String USER_DATA = "user_data";
	
	/** the number of surveys that should be sent per week */
	public static final String SURVEYS_PER_WEEK = "surveys_per_week";
	
//	//settings by type
//	/** List of settings which are strings */
//	public static final String[] STRINGS =
//		{SERVER, SALT, ADMIN_PHONE_NUMBER, ADMIN_NAME, TRACKED_START,
//		TRACKED_END};
//	
//	/** List of settings which are booleans */
//	public static final String[] BOOLEANS =
//		{HTTPS, ALLOW_BLANK_FREE_RESPONSE, ALLOW_NO_CHOICES, SHOW_SURVEY_NAME,
//		USE_FULL_RES_PHOTOS, SURVEYS_LOCAL, TRACKING_LOCAL, CALL_LOG_LOCAL,
//		SURVEYS_SERVER, TRACKING_SERVER, CALL_LOG_SERVER};
//	
//	/** List of settings which are ints */
//	public static final String[] INTS =
//		{SCHEDULER_INTERVAL, PUSH_INTERVAL, PULL_INTERVAL, LOCATION_INTERVAL,
//		SURVEY_DELAY, VOICE_FORMAT, NUM_LOCATIONS_TRACKED, NUM_TIMES_TRACKED,
//		COMPLETION_GOAL, QUESTION_TIMEOUT, SURVEYS_PER_WEEK};
//	
//	/** List of settings which are floats */
//	public static final String[] FLOATS =
//		{TRACKED_LONG, TRACKED_LAT, TRACKED_RADIUS};
	
	/** Prevent instantiation */
	private Config()
	{
		throw new AssertionError("Tried to instantiate Config");
	}
	
	/** get the name of a config file for a given study */
	private static String configFor(long studyID)
	{
		return "study" + studyID + ".conf";
	}
	
	/* -------------------------- boolean methods -------------------------- */
	
	/**
	 * Get the setting associated with a certain key.  If you want the value
	 * of a field annotated with {@link ConfigKey}, you should use
	 * {@link #getBoolean(Context, String)} instead.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * @param ifNotFound the value to return if the settings isn't found
	 * @return true or false if the setting was found, or the value of
	 * ifNotFound if it wasn't
	 */
	public static boolean getBoolean(Context ctxt, String key, boolean ifNotFound)
	{
        return Boolean.parseBoolean(getString(ctxt, key, "" + ifNotFound));
	}
	
	/**
	 * Get the setting associated with a certain key for a certain study.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * @param ifNotFound the value to return if the settings isn't found
	 * @param studyID the id of the study
	 * @return true or false if the setting was found, or the value of
	 * ifNotFound if it wasn't
	 */
	public static boolean getBoolean(Context ctxt, String key, boolean ifNotFound, long studyID)
	{
        return Boolean.parseBoolean(getString(ctxt, key, "" + ifNotFound, studyID));
	}
	
	/**
	 * Get the setting associated with a certain key, or throw an exception if
	 * it does not exist.  This should probably only be used with keys that
	 * are fields marked with {@link ConfigKey}.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * 
	 * @return the value associated with key
	 * 
	 * @throws RuntimeException if key cannot be found
	 */
	public static boolean getBoolean(Context ctxt, String key)
	{
        return Boolean.parseBoolean(getString(ctxt, key));
	}
	
	/**
	 * Set the setting of key to val.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to set
	 * @param val the boolean to set it to
	 * @return true on success
	 */
	public static boolean putSetting(Context ctxt, String key, boolean val)
	{
		return putSetting(ctxt, key, "" + val);
	}
	
	/**
	 * Set the setting of key to val for a certain study.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to set
	 * @param val the boolean to set it to
	 * @param studyID the id of the study to get the setting for
	 * @return true on success
	 */
	public static boolean putSetting(Context ctxt, String key, boolean val, long studyID)
	{
		return putSetting(ctxt, key, "" + val, studyID);
	}
	
	/* --------------------------- float methods --------------------------- */
	
	/**
	 * Get the setting associated with a certain key.  If you want the value
	 * of a field annotated with {@link ConfigKey}, you should use
	 * {@link #getFloat(Context, String)} instead.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * @param ifNotFound the value to return if the settings isn't found
	 * @return true or false if the setting was found, or the value of
	 * ifNotFound if it wasn't
	 */
	public static float getFloat(Context ctxt, String key, float ifNotFound)
	{
        return Float.parseFloat(getString(ctxt, key, "" + ifNotFound));
	}
	
	/**
	 * Get the setting associated with a certain key for a certain study.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * @param ifNotFound the value to return if the settings isn't found
	 * @param studyID the id of the study
	 * @return true or false if the setting was found, or the value of
	 * ifNotFound if it wasn't
	 */
	public static float getFloat(Context ctxt, String key, float ifNotFound, long studyID)
	{
        return Float.parseFloat(getString(ctxt, key, "" + ifNotFound, studyID));
	}
	
	/**
	 * Get the setting associated with a certain key, or throw an exception if
	 * it does not exist.  This should probably only be used with keys that
	 * are fields marked with {@link ConfigKey}.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * 
	 * @return the value associated with key
	 * 
	 * @throws RuntimeException if key cannot be found
	 */
	public static float getFloat(Context ctxt, String key)
	{
        return Float.parseFloat(getString(ctxt, key));
	}
	
	/**
	 * Set the setting of key to val.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to set
	 * @param val the boolean to set it to
	 * @return true on success
	 */
	public static boolean putSetting(Context ctxt, String key, float val)
	{
		return putSetting(ctxt, key, "" + val);
	}
	
	/**
	 * Set the setting of key to val for a certain study.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to set
	 * @param val the boolean to set it to
	 * @param studyID the id of the study to get the setting for
	 * @return true on success
	 */
	public static boolean putSetting(Context ctxt, String key, float val, long studyID)
	{
		return putSetting(ctxt, key, "" + val, studyID);
	}
	
	/* ---------------------------- int methods ---------------------------- */
	
	/**
	 * Get the setting associated with a certain key.  If you want the value
	 * of a field annotated with {@link ConfigKey}, you should use
	 * {@link #getFloat(Context, String)} instead.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * @param ifNotFound the value to return if the settings isn't found
	 * @return true or false if the setting was found, or the value of
	 * ifNotFound if it wasn't
	 */
	public static int getInt(Context ctxt, String key, int ifNotFound)
	{
        return Integer.parseInt(getString(ctxt, key, "" + ifNotFound));
	}
	
	/**
	 * Get the setting associated with a certain key for a certain study.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * @param ifNotFound the value to return if the settings isn't found
	 * @param studyID the id of the study
	 * @return true or false if the setting was found, or the value of
	 * ifNotFound if it wasn't
	 */
	public static int getInt(Context ctxt, String key, int ifNotFound, long studyID)
	{
        return Integer.parseInt(getString(ctxt, key, "" + ifNotFound, studyID));
	}
	
	/**
	 * Get the setting associated with a certain key, or throw an exception if
	 * it does not exist.  This should probably only be used with keys that
	 * are fields marked with {@link ConfigKey}.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * 
	 * @return the value associated with key
	 * 
	 * @throws RuntimeException if key cannot be found
	 */
	public static int getInt(Context ctxt, String key)
	{
        return Integer.parseInt(getString(ctxt, key));
	}
	
	/**
	 * Set the setting of key to val.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to set
	 * @param val the boolean to set it to
	 * @return true on success
	 */
	public static boolean putSetting(Context ctxt, String key, int val)
	{
		return putSetting(ctxt, key, "" + val);
	}
	
	/**
	 * Set the setting of key to val for a certain study.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to set
	 * @param val the boolean to set it to
	 * @param studyID the id of the study to get the setting for
	 * @return true on success
	 */
	public static boolean putSetting(Context ctxt, String key, int val, long studyID)
	{
		return putSetting(ctxt, key, "" + val, studyID);
	}
	
	/* ---------------------------- long methods --------------------------- */
	
	/**
	 * Get the setting associated with a certain key.  If you want the value
	 * of a field annotated with {@link ConfigKey}, you should use
	 * {@link #getFloat(Context, String)} instead.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * @param ifNotFound the value to return if the settings isn't found
	 * @return true or false if the setting was found, or the value of
	 * ifNotFound if it wasn't
	 */
	public static long getLong(Context ctxt, String key, long ifNotFound)
	{
        return Long.parseLong(getString(ctxt, key, "" + ifNotFound));
	}
	
	/**
	 * Get the setting associated with a certain key for a certain study.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * @param ifNotFound the value to return if the settings isn't found
	 * @param studyID the id of the study
	 * @return true or false if the setting was found, or the value of
	 * ifNotFound if it wasn't
	 */
	public static long getLong(Context ctxt, String key, long ifNotFound, long studyID)
	{
        return Long.parseLong(getString(ctxt, key, "" + ifNotFound, studyID));
	}
	
	/**
	 * Get the setting associated with a certain key, or throw an exception if
	 * it does not exist.  This should probably only be used with keys that
	 * are fields marked with {@link ConfigKey}.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * 
	 * @return the value associated with key
	 * 
	 * @throws RuntimeException if key cannot be found
	 */
	public static long getLong(Context ctxt, String key)
	{
        return Long.parseLong(getString(ctxt, key));
	}
	
	/**
	 * Set the setting of key to val.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to set
	 * @param val the boolean to set it to
	 * @return true on success
	 */
	public static boolean putSetting(Context ctxt, String key, long val)
	{
		return putSetting(ctxt, key, "" + val);
	}
	

	
	/**
	 * Set the setting of key to val for a certain study.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to set
	 * @param val the boolean to set it to
	 * @param studyID the id of the study to get the setting for
	 * @return true on success
	 */
	public static boolean putSetting(Context ctxt, String key, long val, long studyID)
	{
		return putSetting(ctxt, key, "" + val, studyID);
	}
	
	/* --------------------------- String methods -------------------------- */
	
	/**
	 * Get the setting associated with a certain key.  If you want the value
	 * of a field annotated with {@link ConfigKey}, you should use
	 * {@link #getFloat(Context, String)} instead.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * @param ifNotFound the value to return if the settings isn't found
	 * @return true or false if the setting was found, or the value of
	 * ifNotFound if it wasn't
	 */
	public static String getString(Context ctxt, String key, String ifNotFound)
	{
		SharedPreferences settings = ctxt.getSharedPreferences(
        		CONFIG_FILE, Context.MODE_PRIVATE);
        return settings.getString(key, ifNotFound);
	}
	
	/**
	 * Get the setting associated with a certain key for a certain study.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * @param ifNotFound the value to return if the settings isn't found
	 * @param studyID the id of the study to get the setting for
	 * @return true or false if the setting was found, or the value of
	 * ifNotFound if it wasn't
	 */
	public static String getString(Context ctxt, String key, String ifNotFound, long studyID)
	{
		SharedPreferences settings = ctxt.getSharedPreferences(
        		configFor(studyID), Context.MODE_PRIVATE);
        return settings.getString(key, ifNotFound);
	}
	
	/**
	 * Get the setting associated with a certain key for a certain study
	 * 
	 * @param ctxt the current context
	 * @param key the setting to get
	 * 
	 * @return the value associated with key
	 * 
	 * @throws RuntimeException if key cannot be found
	 */
	public static String getString(Context ctxt, String key)
	{
		SharedPreferences settings = ctxt.getSharedPreferences(
        		CONFIG_FILE, Context.MODE_PRIVATE);
		if (!settings.contains(key)) throw new RuntimeException(
			"Config key \"" + key + "\" was not found");
        return settings.getString(key, null);
	}
	
	/**
	 * Set the setting of key to val.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to set
	 * @param val the boolean to set it to
	 * @return true on success
	 */
	public static boolean putSetting(Context ctxt, String key, String val)
	{
		SharedPreferences settings = ctxt.getSharedPreferences(
        		CONFIG_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, val);
        return editor.commit();
	}
	
	/**
	 * Set the setting of key to val for a certain study.
	 * 
	 * @param ctxt the current context
	 * @param key the setting to set
	 * @param val the boolean to set it to
	 * @param studyID the id of the study to get the setting for
	 * @return true on success
	 */
	public static boolean putSetting(Context ctxt, String key, String val, long studyID)
	{
		SharedPreferences settings = ctxt.getSharedPreferences(
        		configFor(studyID), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, val);
        return editor.commit();
	}
}
