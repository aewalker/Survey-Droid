/*---------------------------------------------------------------------------*
 * ImageOrTextAdapter.java                                                   *
 *                                                                           *
 * A list adapter that shows either images or text.                          *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Modified array adapter that shows both text and images.
 * 
 * @author Austin Walker
 */
public class ImageOrTextAdapter<T> extends ArrayAdapter<T>
{
	//bitmap pictures to use
	private final Bitmap[] pics;
	
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
		if (pics[position] != null)
		{
			v.setBackgroundDrawable(new BitmapDrawable(
					v.getContext().getResources(), pics[position]));
		}
		return v;
	}
}