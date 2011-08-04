/*---------------------------------------------------------------------------*
 * ViewLogActivity.java                                                      *
 *                                                                           *
 * Shows the application log.                                                *
 *---------------------------------------------------------------------------*/
package org.peoples.android;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * The Activity for the administration panel of the PEOPLES application.
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