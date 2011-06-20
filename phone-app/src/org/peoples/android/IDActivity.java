/*---------------------------------------------------------------------------*
 * IDActivity.java                                                           *
 *                                                                           *
 * Shows the phone's unique id.                                              *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * A very simple screen that shows the phone's id.
 * 
 * @author Austin Walker
 */
public class IDActivity extends Activity
{
	//number of characters per section when displaying the id
	private final static int CHARS_PER_SEC = 4;
	
	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		
		//setting the layout of the activity
		Display display = ((WindowManager)
        		getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		char IDSpacer;
        //check what orientation the phone is in
        //getOrientation() is depreciated as of API 8, but we're targeting
        //API 7, so we have to use it
        if (display.getOrientation() == Configuration.ORIENTATION_LANDSCAPE)
        {
        	setContentView(R.layout.id_activity_horiz);
        	IDSpacer = ' ';
        }
        else
        {
        	setContentView(R.layout.id_activity_vert);
        	IDSpacer = '\n';
        }
		
        //put the phone id into the view
		TelephonyManager tManager =
			(TelephonyManager) getApplicationContext().getSystemService(
					Context.TELEPHONY_SERVICE);
		final TextView uid = (TextView) this.findViewById(R.id.id_id);
		String devID = tManager.getDeviceId();
		
		//this will deal with ANY string, so if we use this on say a tablet
		//where the device id is not the same format, this will still work
		if (devID.length() < CHARS_PER_SEC)
		{
			uid.setText(devID);
		}
		else
		{
			StringBuilder formatedDevID =
				new StringBuilder(devID.substring(0, CHARS_PER_SEC));
			int i = 0;
			for (;i + (CHARS_PER_SEC - 1) < devID.length(); i += CHARS_PER_SEC)
			{
				formatedDevID.append(IDSpacer);
				formatedDevID.append(devID.substring(i, i + CHARS_PER_SEC));
			}
			if (i < devID.length())
			{
				formatedDevID.append(IDSpacer);
				formatedDevID.append(devID.substring(i));
			}
			uid.setText(formatedDevID.toString());
		}
		
		//set up the back button
		final Button back = (Button) this.findViewById(R.id.id_backButton);
        back.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                finish();
            }
        });
	}
}
