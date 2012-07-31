/*---------------------------------------------------------------------------*
 * ChoiceAdapter.java                                                        *
 *                                                                           *
 * List adapter to show choices.                                             *
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
package org.survey_droid.survey_droid.survey.questions;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * List adapter to display choices.  Replaces the old ImageOrTextAdapter.
 * 
 * @author Austin Walker
 */
public class ChoiceAdapter extends ArrayAdapter<Choice>
{
	/**
	 * Constructor
	 * 
	 * @param context the context
	 * @param resource the resource id (ie the layout)
	 * @param choices choices to use
	 */
	public ChoiceAdapter(Context context, int resource, List<Choice> choices)
	{
		super(context, resource, choices);
	}
	
	/**
	 * Constructor
	 * 
	 * @param context the context
	 * @param resource the resource id (ie the layout)
	 * @param choices choices to use
	 */
	public ChoiceAdapter(Context context, int resource, Choice[] choices)
	{
		super(context, resource, choices);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = super.getView(position, convertView, parent);
		Choice c = getItem(position);
		if (c.type == Choice.TEXT_CHOICE_BYTE)
			((TextView) v).setText(c.choice_text);
		else if (c.type == Choice.IMAGE_CHOICE_BYTE)
		{
			((TextView) v).setText("");
			Drawable pic = new BitmapDrawable(getContext().getResources(), c.choice_img);
			((TextView) v).setCompoundDrawablesWithIntrinsicBounds(pic, null, null, null);
		}
		else
			throw new RuntimeException("unsupported choice type: " + c.type);
		return v;
	}
}
