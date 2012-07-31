/*---------------------------------------------------------------------------*
 * Condition.java                                                            *
 *                                                                           *
 * Model for a branch condition. Can be evaluated to true/false to help      *
 * determine whether or not to follow a branch.                              *
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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Arrays;
import java.util.Map;

import org.survey_droid.survey_droid.annotation.Exported;

/**
 * Model for a branch condition.
 *  		
 * @author Diego Vargas
 * @author Austin Walker
 */
@Exported
public class Condition
{
	/**
	 * Enumeration of the different ways that a condition can compare answers.
	 */
	public enum ConditionType
	{
		EQUALS,
		NOT_EQUALS,
		LESS_THAN,
		LESS_THAN_EQUAL_TO,
		GREATER_THAN,
		GREATER_THAN_EQUAL_TO,
		CONTAINS,
		DOES_NOT_CONTAIN,
		CONTAINS_AFTER,
		DOES_NOT_CONTAIN_AFTER,
		CONTAINS_BEFORE,
		DOES_NOT_CONTAIN_BEFORE;
	}
	
	/**
	 * Enumeration of the different scopes within which a condition will be
	 * evaluated.
	 */
	public enum ConditionScope
	{
		/**
		 * Condition scope that checks if the question has been answered with the
		 * given choice during the current instance of the survey.
		 */
		JUST_WAS,
		/**
		 * Condition scope that checks if the question has ever been answered with
		 * the given choice in <strong>any</strong> instance of the survey.
		 */
		EVER_WAS;
	}
	
	/**
	 * Enumeration of the different data types that a condition can check.
	 */
	//TODO add byte[] to allow comparison of basically anything
	public enum DataType
	{
		BYTE("Byte"),
		CHAR("Char"),
		SHORT("Short"),
		INT("Int"),
		LONG("Long"),
		FLOAT("Float"),
		DOUBLE("Double"),
		STRING("UTF");
		
		DataType(String name)
		{
			this.name = name;
		}
		
		String name;
	}
	
	/** ID of the question to look at when evaluating this condition */
	private final long q_id;
	/** Question to look at when evaluating this condition */
	private Question question;
	
	/** see {@link ConditionType} */
	private final ConditionType type;
	/** see {@link ConditionScope} */
	private final ConditionScope scope;
	/** see {@link DataType} */
	private final DataType dataType;
	
	/** Data to compare against */
	private final byte[] checkData;
	/** Index in the question answer to check at */
	private final int location;
	
	/**
	 * Answer set from the Survey (the previous answers to the question);
	 * required in order to check current answers.
	 */
	private final Iterable<Answer> answers;
	
	/*-----------------------------------------------------------------------*/
	
	/**
	 * Create a new Condition.  Depending on the exact variety of condition you
	 * want to create, the <code>location</code> parameter bellow may not be
	 * applicable and will be ignored; the same is true for
	 * <code>dataType</code> if <code>type</code> is anything except one of
	 * <ul>
	 * <li>{@link ConditionType#LESS_THAN}</li>
	 * <li>{@link ConditionType#LESS_THAN_EQUAL_TO}</li>
	 * <li>{@link ConditionType#GREATER_THAN}</li>
	 * <li>{@link ConditionType#GREATER_THAN_EQUAL_TO}</li>
	 * </ul>
	 * In the event that <code>type</code> is one the above,
	 * <code>dataType</code> must be a numeric type.
	 * 
	 * @param q_id the id of the question this condition checks
	 * @param type see {@link ConditionType}
	 * @param scope see {@link ConditionScope}
	 * @param dataType see {@link DataType}
	 * @param data the data to match against
	 * @param location the index into the answer data to use
	 * @param answers the list of answers to the question from previous surveys
	 * 
	 * @throws IllegalArgumentException if type and dataType do not match
	 * @throws IllegalArgumentException if location is needed and is negative
	 * 
	 * @see Condition#setQuestion(Map)
	 */
	public Condition(long q_id, ConditionType type, ConditionScope scope,
		DataType dataType, byte[] data, int location, Iterable<Answer> answers)
	{
		this.q_id = q_id;
		question = null;
		this.type = type;
		this.scope = scope;
		this.dataType = dataType;
		checkData = data;
		this.location = location;
		this.answers = answers;
		
		//check for invalid combinations of type/dataType
		if (dataType == DataType.STRING || dataType == DataType.CHAR)
		{
			switch (type)
			{
			case LESS_THAN:
			case LESS_THAN_EQUAL_TO:
			case GREATER_THAN:
			case GREATER_THAN_EQUAL_TO:
				throw new IllegalArgumentException(
					"Cannot do numeric comparisons with strings/chars");
			default:
				//do nothing
			}
		}
		
		//check for invalid location
		if (location < 0)
		{
			switch (type)
			{
			case CONTAINS_AFTER:
			case DOES_NOT_CONTAIN_AFTER:
			case CONTAINS_BEFORE:
			case DOES_NOT_CONTAIN_BEFORE:
				throw new IllegalArgumentException(
					"Invalid location: " + location);
			default:
				//do nothing
			}
		}
	}
	
	/**
	 * Set the Condition's {@link Question}.  Needed to avoid infinite
	 * recursion in {@link Survey#Survey(int, android.content.Context)}.
	 * Should only be called once.
	 * 
	 * @param q_map a {@link Map} of built Questions
	 * @throws RuntimeException if called multiple times
	 * @throws SurveyConstructionException if the given map doesn't
	 * have the need Question
	 */
	public void setQuestion(Map<Long, Question> qMap)
		throws SurveyConstructionException
	{
		if (question != null) throw new RuntimeException(
				"attempt to set condition question multiple times");
		if (!qMap.containsKey(q_id))
		{
			SurveyConstructionException e = new SurveyConstructionException();
			e.setRefQuestion(q_id);
			throw e;
		}
		question = qMap.get(q_id);
	}
	
	/**
	 * Evaluate this Condition.
	 * 
	 * @return true or false
	 * 
	 * @throws RuntimeException if called before {@link #setQuestion(Map)}
	 */
	//TODO perhaps there is a nicer way to organize this.
	public boolean eval()
	{
		if (question == null)
		{
			throw new RuntimeException(
					"must set question before calling eval");
		}
		
		Iterable<Answer> toCheck;
		switch (scope)
		{
		case JUST_WAS:
			toCheck = question.getAnswers();
			break;
		case EVER_WAS:
			toCheck = answers;
			break;
		default:
			throw new IllegalArgumentException("Invalid scope: " + scope);
		}
		
		boolean result = false;
		switch (type)
		{
		case NOT_EQUALS:
			result = true;
			//fall through
		case EQUALS:
			for (Answer a : toCheck)
			{
				if (Arrays.equals(a.getValue(), checkData))
				{
					result = !result;
					break;
				}
			}
			break;
			
		
		case LESS_THAN_EQUAL_TO:
		case LESS_THAN:
		case GREATER_THAN_EQUAL_TO:
		case GREATER_THAN:
			Number checkedValue;
			try
			{
				DataInputStream in = new DataInputStream(
					new ByteArrayInputStream(checkData));
				checkedValue = (Number) in.getClass().getMethod(
					"read" + dataType.name).invoke(in);
			}
			catch (Exception e)
			{
				throw new RuntimeException(
					"Failed to extract data from stream", e);
			}
			for (Answer a : toCheck)
			{
				int compResult = 0;
				try
				{
					DataInputStream in = new DataInputStream(
						new ByteArrayInputStream(a.getValue()));
					in.skipBytes(location);

					/*
					 * My entry for most unreadable code.  This just does:
					 * compResult = ((Byte) checkedValue).compareTo(in.readByte());
					 * but in a more generic way that will work for all the
					 * primitive types.
					 * 
					 * -Austin
					 */
					Object value = in.getClass().getMethod(
						"read" + dataType.name).invoke(in);
					Class<?> valueClass = checkedValue.getClass();
					compResult = (Integer) valueClass.getMethod(
						"compareTo", valueClass).invoke(checkedValue, value);
				}
				catch (Exception e)
				{
					throw new RuntimeException(
						"Failed to extract data from stream", e);
				}
				switch (type) //yo dawg
				{
				case LESS_THAN_EQUAL_TO:
					if (compResult >= 0) result = true;
					break;
				case LESS_THAN:
					if (compResult > 0) result = true;
					break;
				case GREATER_THAN_EQUAL_TO:
					if (compResult <= 0) result = true;
					break;
				case GREATER_THAN:
					if (compResult < 0) result = true;
					break;
				default:
					throw new RuntimeException("Cosmic rays");
				}
			}
			break;
		

		
		case DOES_NOT_CONTAIN_AFTER:
			result = true;
			//fall through
		case CONTAINS_AFTER:
			for (Answer a : toCheck)
			{
				if (rabinKarp(checkData, a.getValue(), location, Integer.MAX_VALUE))
				{
					result = !result;
					break;
				}
			}
			break;
			
			
		case DOES_NOT_CONTAIN_BEFORE:
			result = true;
			//fall through
		case CONTAINS_BEFORE:
			for (Answer a : toCheck)
			{
				if (rabinKarp(checkData, a.getValue(), -1, location))
				{
					result = !result;
					break;
				}
			}
			break;
			
			
		case DOES_NOT_CONTAIN:
			result = true;
			//fall through
		case CONTAINS:
			for (Answer a : toCheck)
			{
				if (rabinKarp(checkData, a.getValue(), -1, Integer.MAX_VALUE))
				{
					result = !result;
					break;
				}
			}
			break;
			
			
		default:
			throw new IllegalArgumentException("Invalid type: " + type);
		}
		return result;
	}
	
	/**
	 * Performs Rabin-Karp hash-based substring search on byte arrays.
	 * 
	 * @param needle
	 * @param haystack
	 * @param start
	 * @param end
	 * @return true if needle is found in haystack between start and end (exclusive)
	 */
	private static boolean rabinKarp(byte[] needle, byte[] haystack, int start, int end)
	{
		if (needle.length > haystack.length) return false;
		int nHash = 0;
		for (int i = 0; i < needle.length; i++)
		{
			nHash = rollingHash(nHash, needle[i], (byte) 0x00);
		}
		int hHash = 0;
		for (int i = 0; i < needle.length; i++)
		{
			hHash = rollingHash(hHash, haystack[i], (byte) 0x00);
		}
		for (int i = 0; i < haystack.length - needle.length - 1; i++)
		{
			if (i + needle.length >= end) break;
			if (nHash == hHash && i > start)
			{
				boolean match = true;
				for (int j = 0; j < needle.length; j++)
				{
					if (needle[j] != haystack[i + j])
					{
						match = false;
						break;
					}
				}
				if (match) return true;
			}
			hHash = rollingHash(hHash, haystack[needle.length + i], haystack[i]);
		}
		return false;
	}
	
	/** The mod base to use for {@link #rollingHash(int, byte, byte)} */
	//a big prime; note that Integer.MAX_VALUE < BASE + Byte.MAX_VALUE
	private static final int BASE = 479001599;
	
	/**
	 * Rolling hash for {@link #rabinKarp(byte[], byte[])}.
	 * 
	 * @param base the previous hash
	 * @param add the byte to add to the hash
	 * @param sub the byte to subtract from the hash
	 * @return the new hash
	 */
	private static int rollingHash(int base, byte add, byte sub)
	{
		return (base + add - sub) % BASE;
	}
}
