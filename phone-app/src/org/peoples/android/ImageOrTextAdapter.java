/*---------------------------------------------------------------------------*
 * ImageOrTextAdapter.java                                                   *
 *                                                                           *
 * A list adapter that shows either images or text.                          *
 *---------------------------------------------------------------------------*/
//FIXME this class should probably be an extention of ArrayAdapter.  Either
//way, it needs to be fixed up as it is quite hackish right now.
package org.peoples.android;

import org.peoples.android.survey.QuestionActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
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
	
	//FIXME remove
	private final QuestionActivity qa;
	
	//multi select or single select
	private final int mode;
	
	//what items start out checked
	private int[] startChecked = new int[0];
	
	//the currently checked view; used for single mode only
	private CheckBox checked = null;
	private int checkedId = -1;
	
	private final View.OnClickListener clickListener =
    	new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			//find the index
			TextView id = (TextView) view.findViewById(R.id.adapter_item_id);
			int pos = Integer.parseInt(id.getText().toString());
			CheckBox cBox =
				(CheckBox) view.findViewById(R.id.adapter_item_checkbox);
			if (cBox.isChecked())
			{
				cBox.setChecked(false);
				qa.remove(pos);
			}
			else
			{
				cBox.setChecked(true);
				qa.add(pos);
				if (mode == ListView.CHOICE_MODE_SINGLE)
				{
					if (checked != null)
					{
						checked.setChecked(false);
						qa.remove(checkedId);
					}
					checked = cBox;
					checkedId = pos;
				}
			}
		}
	};
	
	/**
	 * Create a new adapter that can display either an image or a string.
	 * 
	 * @param context
	 * @param mode - the mode, either multi or single
	 * @param items - the items to put into the list.  Build this array by
	 * creating each sub-array such that the String is at
	 * {@link STRING_POS} and the Bitmap is at {@link IMG_POS}.  Set the
	 * unused element to null.
	 * @parm qa - the QuesionActivity to notify when selections are made
	 */
	//FIXME do this properly, remove the qa
	public ImageOrTextAdapter(Context context,
			int mode, Object[][] items, QuestionActivity qa)
	{
		this.qa = qa;
		ctxt = context;
		this.items = items;
		this.mode = mode;
	}
	
	/**
	 * Set the items that need to be checked by default.
	 * 
	 * @deprecated
	 */
	public void setChecked(int[] checked)
	{
		for (int i : checked)
		{
			if (i > items.length)
				throw new IllegalArgumentException("Bad index");
			qa.add(i);
		}
		startChecked = checked;
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
				TextView t = (TextView)
					convertView.findViewById(R.id.adapter_item_text);
				t.setText(text);
			}
			else if (img != null)
			{
				ImageView i = (ImageView)
					convertView.findViewById(R.id.adapter_item_image);
				i.setImageBitmap(img);
			}
        }
		catch (Exception e)
		{ //if that didn't work, then make a new view
			LayoutInflater li = (LayoutInflater)
				ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = li.inflate(R.layout.adapter_item, null);
			v.setOnClickListener(clickListener);
			TextView textView =
				(TextView) v.findViewById(R.id.adapter_item_text);
			ImageView imgView =
				(ImageView) v.findViewById(R.id.adapter_item_image);
			for (int i : startChecked)
			{
				if (i == position)
				{
					CheckBox cBox = (CheckBox)
						v.findViewById(R.id.adapter_item_checkbox);
					cBox.setChecked(true);
				}
			}
			
			//FIXME remove later
			TextView idView = (TextView) v.findViewById(R.id.adapter_item_id);
			idView.setText(Integer.toString(position));
			idView.setVisibility(View.GONE);
			
			if (text != null)
			{
				imgView.setVisibility(View.GONE);
				textView.setText(text);
			}
			else if (img != null)
			{
				textView.setVisibility(View.GONE);
				imgView.setImageBitmap(img);
			}
			convertView = v;
		}
        return convertView;
	}
}