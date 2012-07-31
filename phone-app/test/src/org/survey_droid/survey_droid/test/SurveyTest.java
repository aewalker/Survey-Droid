/*---------------------------------------------------------------------------*
 * SurveyTest.java                                                           *
 *                                                                           *
 * Test suite for the core survey functionality.  Tests survey logic (but    *
 * not display, scheduling, etc.).                                           *
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
package org.survey_droid.survey_droid.test;

import org.survey_droid.survey_droid.survey.Survey;
import org.survey_droid.survey_droid.survey.SurveyConstructionException;

import android.test.AndroidTestCase;

/**
 * Test suite for core survey logic.
 * 
 * @author Austin Walker
 */
@Tests({Survey.class})
public class SurveyTest extends AndroidTestCase
{
	public void testDummySurveyConstrution() throws SurveyConstructionException
	{
		//build a copy of the test survey
		Survey s = new Survey(getContext());
		
		//test some simple things about the survey's state
		assertFalse(s.done());
		assertTrue(s.isOnFirst());
	}
}
