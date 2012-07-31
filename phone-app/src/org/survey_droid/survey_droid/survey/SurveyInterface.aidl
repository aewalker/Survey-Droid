/*---------------------------------------------------------------------------*
 * SurveyInterface.aidl                                                      *
 *                                                                           *
 * IPC interface for question plugins.                                       *
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
package org.survey_droid.survey_droid.survey;

/**
 * Interface that allows Questions to communicate with the main survey system.
 */
interface SurveyInterface
{
	/**
	 * Start the timeout countdown.  Call this when the user leaves the
	 * question activity (probably in onPuase or onStop).
	 */
	void startTimeout();
	
	/**
	 * Stop the timeout countdown.  Call this when the user comes back to
	 * the question activity (maybe in onResume).  If this is called before
	 * {@link #startTimeout()}, it does nothing.
	 */
	void stopTimeout();
	
	/**
	 * Attempt to move the survey to the next question.  This will only work if
	 * the current question has been adequately answered.  If this is the last
	 * question in the survey, this will cause the confirmation screen to be
	 * shown.
	 * 
	 * @return true on success, in which case the next question is loaded, or
	 * false on failure, in which case some kind of error message should be
	 * show to explain what happened (eg no answer has been given).
	 */
	boolean nextQuestion();
	
	/**
	 * Attempt to move the survey to the previous question.  Fails if the
	 * current question is the first one.
	 *
	 * @return true on success, false on failure
	 */
	boolean prevQuestion();
	
	/**
	 * Inform the survey system of the user's answer to a question (whatever
	 * that data may be).
	 *
	 * @param data the answer
	 * @return true on success, false on failure (usually indicating an
	 * internal error)
	 */
	boolean answer(in byte[] data);
	
	/**
	 * Get the raw data that was provided for this question.
	 *
	 * @return the data as bytes.  It is up to the individual question to then
	 * parse this data and display it to the user. {@link ByteDataParser} could
	 * be useful when handling this data.
	 */
	byte[] getQuestionData();
	
	/**
	 * @return the survey's name (exactly as it is to be displayed).
	 */
	String getSurveyName();
	
	/**
	 * @return the previous answer for this question, or null if there is none
	 */
	byte[] getAnswer();
	
	/**
	 * If your question supports custom user data (as it should!), use this to
	 * process any text you want to display.
	 *
	 * @param text the original text
	 * @return text with any user variables replaced with their proper value 
	 */
	String processText(in String text);
}