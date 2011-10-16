/*---------------------------------------------------------------------------*
 * SurveyDoneActivity.java                                                   *
 *                                                                           *
 * Tells the user that the survey has been successfully submitted.  Note     *
 * that this screen's being displayed to a user does not say anything about  *
 * the survey being sent to the server; it only means that they have         *
 * finished taking it.                                                       *
 *---------------------------------------------------------------------------*
 * Copyright 2011 Sema Berkiten, Vladimir Costescu, Henry Liu, Diego Vargas, *
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.surveydroid.android.R;
import org.surveydroid.android.Util;

/**
 * Activity that tells the subject that they are done with the survey.
 * 
 * @author Austin Walker
 */
public class SurveyDoneActivity extends Activity
{
	//logging tag
	private static final String TAG = "SurveyDoneActivity";
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		Util.d(null, TAG, "Creating SurveyDoneActivity");
		
		//setting the layout of the activity
        setContentView(R.layout.survey_done);
		
		Button back =
			(Button) findViewById(R.id.survey_done_exitButton);
		back.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				//finish up the backend stuff
				Intent finishIntent =
					new Intent(SurveyDoneActivity.this, SurveyService.class);
				finishIntent.setAction(SurveyService.ACTION_END_SURVEY);
				startService(finishIntent);
				
				finish();
			}
		});
	}
}
