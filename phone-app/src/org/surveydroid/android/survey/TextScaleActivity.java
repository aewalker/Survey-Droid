/*---------------------------------------------------------------------------*
 * TextScaleActivity.java                                                    *
 *                                                                           *
 * Shows the user a question with a slider that allows them to select a      *
 * value between 1 and 100.  This version shows some text at each end of the *
 * scale.                                                                    *
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

import org.surveydroid.android.R;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Shows the user a question with a slider that allows them to select a value
 * between 1 and 100.  This version shows some text at each end of the scale.
 * 
 * @author Austin Walker
 */
public class TextScaleActivity extends ScaleActivity
{
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		findViewById(R.id.scale_lowImg).setVisibility(View.GONE);
		findViewById(R.id.scale_highImg).setVisibility(View.GONE);
	}
	
	@Override
	protected void onSurveyLoaded()
	{
		super.onSurveyLoaded();
		//set the text on each end of the scale
		TextView lowText = (TextView) findViewById(R.id.scale_lowText);
		TextView highText = (TextView) findViewById(R.id.scale_highText);
		lowText.setText(survey.getLowText());
		highText.setText(survey.getHighText());
	}
}