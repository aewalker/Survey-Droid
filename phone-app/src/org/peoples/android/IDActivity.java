/*---------------------------------------------------------------------------*
 * IDActivity.java                                                           *
 *                                                                           *
 * Shows the phone's unique id.                                              *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class IDActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		
		//setting the layout of the activity
        setContentView(R.layout.id_view);
		
        //put the phone id into the view
		TelephonyManager tManager =
			(TelephonyManager) getApplicationContext().getSystemService(
					Context.TELEPHONY_SERVICE);
		final TextView uid = (TextView) this.findViewById(R.id.phone_id);
		uid.setText(tManager.getDeviceId());
		
		//set up the back button
		final Button back = (Button) this.findViewById(R.id.id_back);
        back.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                finish();
            }
        });
	}
}
