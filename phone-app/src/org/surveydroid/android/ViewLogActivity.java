/*---------------------------------------------------------------------------*
 * ViewLogActivity.java                                                      *
 *                                                                           *
 * Shows the application log.                                                *
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * The Activity for the administration panel of the Survey Droid application.
 * 
 * @author Henry Liu
 * @author Austin Walker
 */
public class ViewLogActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        String log;
        try
        {
		    log = readLogFile();
        }
        catch (Exception e)
        {
        	Util.e(this, "ViewLogActivty", Util.fmt(e));
        	finish();
        	return;
        }
        
        setContentView(R.layout.view_log);
        TextView tv = (TextView) findViewById(R.id.view_log_text);
        tv.setText(log);
    }
    
    public static String readLogFile() throws IOException
    {
    	BufferedReader reader = new BufferedReader(
	    		new FileReader(Util.LOGFILE));
	    String line  = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    String ls = System.getProperty("line.separator");
	    while ((line = reader.readLine()) != null)
	    {
	        stringBuilder.append(line);
	        stringBuilder.append(ls);
	    }
	    return stringBuilder.toString();
    }
}