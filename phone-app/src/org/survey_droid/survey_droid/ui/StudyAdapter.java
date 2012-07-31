/*---------------------------------------------------------------------------*
 * StudyAdapter.java                                                         *
 *                                                                           *
 * ListAdapter to allow users to join/leave/lear about studies.              *
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
package org.survey_droid.survey_droid.ui;

import org.survey_droid.survey_droid.Study;
import org.survey_droid.survey_droid.Util;
import org.survey_droid.survey_droid.Study.StudyListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * List adapter that lets users learn about, join, and leave studies.
 * 
 * @author Austin Walker
 */
public class StudyAdapter extends BaseAdapter
{
	/** view that displays a survey */
	private class StudyView extends LinearLayout
	{
		final Study s;
		
		final TextView name;
		final CheckBox joined;
		final ProgressBar spinner;
		
		boolean working;
		
		StudyView(Study s)
		{
			super(c);
			this.s = s;
			working = false;
			
			this.setOrientation(HORIZONTAL);
			name = new TextView(c);
			name.setText(s.name);
			joined = new CheckBox(c);
			joined.setChecked(s.isJoined());
			spinner = new ProgressBar(c);
			spinner.setIndeterminate(true);
			spinner.setVisibility(View.INVISIBLE);
			addView(spinner, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			addView(joined, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			addView(name, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			
			final StudyListener sl = new StudyListener()
			{
				@Override
				public void onJoin(boolean worked)
				{
					working = false;
					spinner.setVisibility(View.INVISIBLE);
					if (!worked)
					{
						Util.e(c, TAG, "Failed to join study!");
						return;
					}
					joined.setChecked(true);
				}

				@Override
				public void onLeave(boolean worked)
				{
					working = false;
					spinner.setVisibility(View.INVISIBLE);
					if (!worked)
					{
						Util.e(c, TAG, "Failed to leave study!");
						return;
					}
					joined.setChecked(false);
				}
			};
			
			setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (working) return;
					StringBuilder msg = new StringBuilder(StudyView.this.s.name);
					if (StudyView.this.s.oneOff) msg.append("(one time only)");
					msg.append('\n');
					msg.append(StudyView.this.s.description);
					msg.append('\n');
					msg.append("Tracks: ");
					int numItems = 0;
					String[] tracked = new String[3];
					if (StudyView.this.s.tracksCalls)
					{
						tracked[numItems] = "your phone calls";
						numItems++;
					}
					if (StudyView.this.s.tracksTexts)
					{
						tracked[numItems] = "your incoming texts";
						numItems++;
					}
					if (StudyView.this.s.tracksCalls)
					{
						tracked[numItems] = "your location (every " + StudyView.this.s.locationInterval + " minutes)";
					}
					switch (numItems)
					{
					case 0:
						msg.append("nothing");
						break;
					case 1:
						msg.append(tracked[0]);
						break;
					case 2:
						msg.append(tracked[0]);
						msg.append(" and ");
						msg.append(tracked[1]);
						break;
					case 3:
						msg.append(tracked[0]);
						msg.append(", ");
						msg.append(tracked[1]);
						msg.append(", and ");
						msg.append(tracked[2]);
						break;
					}
					AlertDialog.Builder builder = new AlertDialog.Builder(c);
					builder.setMessage(msg.toString());
					builder.setCancelable(true);
					final Context c = v.getContext();
					final DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							if (which != DialogInterface.BUTTON_POSITIVE) return;
							working = true;
							spinner.setVisibility(View.VISIBLE);
							if (!joined.isChecked())
							{
								StudyView.this.s.join(sl, c);
							}
							else
							{
								StudyView.this.s.leave(sl, c);
							}
						}
					};
					if (!joined.isChecked())
						builder.setPositiveButton("Join", ocl);
					else
						builder.setPositiveButton("Leave", ocl);
					builder.setPositiveButton("Cancel", ocl);
					builder.create().show();
				}	
			});
		}
	}
	
	/** logging tag */
	private static final String TAG = "StudyAdapter";
	
	private final Study[] studies;
	
	private final Context c;
	
	/**
	 * Create a new adapter.
	 * 
	 * @param studies the studies to display
	 */
	public StudyAdapter(Context c, Study[] studies)
	{
		this.c = c;
		this.studies = studies;
	}
	
	@Override
	public int getCount()
	{
		return studies.length;
	}

	@Override
	public Object getItem(int position)
	{
		if (position < 0 || position >= getCount()) return null;
		return studies[position];
	}

	@Override
	public long getItemId(int position)
	{
		if (position < 0 || position >= getCount()) return -1;
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (position < 0 || position >= getCount()) return null;
		StudyView sv;
		if (convertView == null)
		{
			sv = new StudyView(studies[position]);
		}
		else
		{
			sv = (StudyView) convertView;
		}
		return sv;
	}
	
}
