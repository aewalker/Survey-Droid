/*---------------------------------------------------------------------------*
 * VerticalProgressBar.java                                                  *
 *                                                                           *
 * Extension of the ProgressBar class to allow it to be vertical.            *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.ProgressBar;

public class VerticalProgressBar extends ProgressBar
{
	/**
	 * Constructor.
	 * 
	 * @param context - the activity context
	 */
	public VerticalProgressBar(Context context)
	{
		super(context);
	}
	
	@Override
	protected synchronized void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.rotate(90);
	}
}