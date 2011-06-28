/*---------------------------------------------------------------------------*
 * ImageOrTextAdapter.java                                                   *
 *                                                                           *
 * A list adapter that shows either images or text.                          *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Shows either an image or text.  Based on the IconSimpleAdapter, which
 * appears in many places on the net.
 * 
 * @author Austin Walker
 */
public class ImageOrTextAdapter extends BaseAdapter
{
	/** Index for stings */
	public static final int STRING_POS = 0;
	
	/** Index for images */
	public static final int IMG_POS = 1;
	
	//the items to go into the list
	private final Object[][] items;
	
	//the context this list is being used in
	private final Context ctxt;
	
	/**
	 * Create a new adapter that can display either an image or a string.
	 * 
	 * @param context
	 * @param items - the items to put into the list.  Build this array by
	 * creating each sub-array such that the String is at
	 * {@link STRING_POS} and the Bitmap is at {@link IMG_POS}.  Set the
	 * unused element to null.
	 */
	public ImageOrTextAdapter(Context context, Object[][] items)
	{
		ctxt = context;
		this.items = items;
	}
	
	@Override
	public int getCount()
	{
		return items.length;
	}
	
	@Override
	public Object getItem(int position)
	{
		return items[position];
	}
	
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Bitmap img = (Bitmap) items[position][IMG_POS];
		String text = (String) items[position][STRING_POS];
		try
		{ //first try to reuse convertView
			if (text != null)
			{
				TextView t = (TextView) convertView;
				t.setText(text);
			}
			else if (img != null)
			{
				ImageView i = (ImageView) convertView;
				i.setImageBitmap(img);
			}
        }
		catch (Exception e)
		{ //if that didn't work, then make a new view
			if (text != null)
			{
				TextView textView = new TextView(ctxt);
				textView.setText(text);
				convertView = textView;
			}
			else if (img != null)
			{
				ImageView imgView = new ImageView(ctxt);
				imgView.setImageBitmap(img);
				convertView = imgView;
			}
		}
        return convertView;
	}
}