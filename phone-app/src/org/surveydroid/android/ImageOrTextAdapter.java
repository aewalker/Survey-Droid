/*---------------------------------------------------------------------------*
 * ImageOrTextAdapter.java                                                   *
 *                                                                           *
 * A list adapter that shows either images or text.                          *
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
package org.surveydroid.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Modified array adapter that shows both text and images.
 * 
 * @author Austin Walker
 */
public class ImageOrTextAdapter<T> extends ArrayAdapter<T>
{
	//logging tag
	private static final String TAG = "ImageOrTextAdapter";
	
	//bitmap pictures to use
	private final Bitmap[] pics;
	
	//does the current type of view support unscaledBackground images
	private boolean unscaledBackground = true;
	
	//what text size to use for the choices
	private float size = 0;
	
	//how much to scale the size of the choices by
	private static final float SCALE_FACTOR = 0.6f;
	
	/**
	 * Constructor
	 * 
	 * @param context - the context
	 * @param resource - the resource id (ie the layout)
	 * @param objects - the objects to use; their toString method must not
	 * return null
	 * @param pics - the bitmaps to use
	 */
	public ImageOrTextAdapter(Context context, int resource, T[] objects,
			Bitmap[] pics)
	{
		super(context, resource, objects);
		this.pics = pics;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = super.getView(position, convertView, parent);
		try
		{
			//TODO create a theme for the whole app so we don't have to do this
			TextView tv = (TextView) v;
			tv.setTypeface(tv.getTypeface(), Typeface.ITALIC);
			if (size == 0.0f) size = tv.getTextSize() * SCALE_FACTOR;
			tv.setTextSize(size);
			v = tv;
		}
		catch (Exception e)
		{
			Util.e(null, TAG, "Can't set italic type: " + e);
		}
		if (pics[position] != null)
		{
			BitmapDrawable pic = new BitmapDrawable(
					v.getContext().getResources(), pics[position]);
			if (!unscaledBackground)
			{
				v.setBackgroundDrawable(pic);
			}
			else
			{
				TextView tv;
				try
				{
					tv = (TextView) v;
				}
				catch (Exception e)
				{
					Util.w(null, TAG, "Returned view does not "
							+ "support unscaled background images");
					unscaledBackground = true;
					v.setBackgroundDrawable(pic);
					return v;
				}
				tv.setCompoundDrawablesWithIntrinsicBounds(
						pic, null, null, null);
				return tv;
			}
		}
		return v;
	}
}