/*---------------------------------------------------------------------------*
 * SurveyConstructionException.java                                          *
 *                                                                           *
 * Exception type for dealing with invalid survey setups, such as references *
 * to questions that don't exist, etc.                                       *
 *---------------------------------------------------------------------------*
 * Copyright 2012 Sema Berkiten, Vladimir Costescu, Henry Liu, Diego Vargas, *
 * Austin Walker, and Tony Xiao                                              *
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
package org.surveydroid.android.survey;

/**
 * Checked exception class holds information about errors that can occur when
 * a survey is being constructed.
 * 
 * @author Austin Walker
 */
public class SurveyConstructionException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	//id(s) of item(s) currently being built
	private int survey = 0;
	private int buildQuestion = 0;
	private int buildBranch = 0;
	private int buildCondition = 0;
	private int buildChoice = 0;
	
	//id(s) of item(s) that were referenced
	private int refQuestion = 0;
	private int refChoice = 0;
	private int refBranch = 0;
	private int refCondition = 0;
	
	/**
	 * Default constructor for base exceptions
	 */
	public SurveyConstructionException()
	{
		super();
	}
	
	/**
	 * Fill in the survey id on creation
	 * 
	 * @param s_id the survey id
	 */
	public SurveyConstructionException(int s_id)
	{
		super();
		survey = s_id;
	}
	
	/**
	 * Builds up this exception from another
	 * 
	 * @param s_id the survey id
	 * @param e can be any {@link Throwable}
	 */
	public SurveyConstructionException(int s_id, Throwable e)
	{
		super(e);
		survey = s_id;
	}
	
	@Override
	public String getMessage()
	{
		return "Error while building survey";
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(getMessage());
		sb.append(" " + survey + ": ");
		
		//say where the error is
		if (buildQuestion != 0)
		{
			sb.append("at question " + buildQuestion + " ");
			if (buildBranch != 0)
			{
				sb.append("at branch " + buildBranch + " ");
				if (buildCondition != 0)
				{
					sb.append("at condition " + buildCondition + " ");
				}
			}
			else if (buildChoice != 0)
			{
				sb.append("at choice " + buildChoice + " ");
			}
		}
		else
		{
			sb.append("at an unknown location ");
		}
		
		//say what the error is
		if (refQuestion != 0)
		{
			sb.append("question " + refQuestion + " was referenced in error, ");
		}
		if (refBranch != 0)
		{
			sb.append("branch " + refBranch + " was referenced in error, ");
		}
		if (refCondition != 0)
		{
			sb.append("condition " + refCondition + " was referenced in error, ");
		}
		if (refChoice != 0)
		{
			sb.append("choice " + refChoice + " was referenced in error, ");
		}
		
		//add the final message
		sb.append("please check your survey configuration for these error.");
		
		return sb.toString();
	}

	/**
	 * @param buildQuestion the question being built when the error occurred
	 */
	public void setBuildQustion(int buildQuestion)
	{
		this.buildQuestion = buildQuestion;
	}

	/**
	 * @return the question being built when the error occurred, or 0 if none
	 * exists
	 */
	public int getBuildQustion()
	{
		return buildQuestion;
	}

	/**
	 * @param buildBranch the branch being built when the error occurred
	 */
	public void setBuildBranch(int buildBranch)
	{
		this.buildBranch = buildBranch;
	}

	/**
	 * @return the branch being built when the error occurred, or 0 if none
	 * exists
	 */
	public int getBuildBranch()
	{
		return buildBranch;
	}

	/**
	 * @param buildCondition the condition being built when the error occurred
	 */
	public void setBuildCondition(int buildCondition)
	{
		this.buildCondition = buildCondition;
	}

	/**
	 * @return the condition being built when the error occurred, or 0 if none
	 * exists
	 */
	public int getBuildCondition()
	{
		return buildCondition;
	}

	/**
	 * @param buildChoice the choice being built when the error occurred
	 */
	public void setBuildChoice(int buildChoice)
	{
		this.buildChoice = buildChoice;
	}

	/**
	 * @return the choice being built when the error occurred, or 0 if none
	 * exists
	 */
	public int getBuildChoice()
	{
		return buildChoice;
	}

	/**
	 * @param refQuestion the faulty question that was referenced
	 */
	public void setRefQuestion(int refQuestion)
	{
		this.refQuestion = refQuestion;
	}

	/**
	 * @return the faulty question that was referenced, or 0 if none exists
	 */
	public int getRefQuestion()
	{
		return refQuestion;
	}

	/**
	 * @param refChoice the faulty choice that was referenced
	 */
	public void setRefChoice(int refChoice)
	{
		this.refChoice = refChoice;
	}

	/**
	 * @return the faulty choice that was referenced, or 0 if none exists
	 */
	public int getRefChoice()
	{
		return refChoice;
	}

	/**
	 * @param refBranch the faulty branch that was referenced
	 */
	public void setRefBranch(int refBranch)
	{
		this.refBranch = refBranch;
	}

	/**
	 * @return the faulty branch that was referenced, or 0 if none exists
	 */
	public int getRefBranch()
	{
		return refBranch;
	}

	/**
	 * @param refCondition the faulty condition that was referenced
	 */
	public void setRefCondition(int refCondition)
	{
		this.refCondition = refCondition;
	}

	/**
	 * @return the faulty condition that was referenced, or 0 if none exists
	 */
	public int getRefCondition()
	{
		return refCondition;
	}

	/**
	 * @return the survey
	 */
	public int getSurvey()
	{
		return survey;
	}
	
	/**
	 * @param survey the survey being built
	 */
	public void setSurvey(int survey)
	{
		this.survey = survey;
	}
}
