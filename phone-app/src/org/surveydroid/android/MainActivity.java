/*---------------------------------------------------------------------------*
 * MainActivity.java                                                         *
 *                                                                           *
 * User control panel with buttons to adjust settings, show a sample survey, *
 * get the phone's id, and exit.                                             *
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

import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.surveydroid.android.coms.ComsService;
import org.surveydroid.android.database.ExtrasDBHandler;
import org.surveydroid.android.database.TakenDBHandler;

/**
 * The Activity for the administration panel of the Survey Droid application.
 * 
 * @author Henry Liu
 * @author Austin Walker
 */
public class MainActivity extends Activity
{
	//logging tag
    private static final String TAG = "MainActivity";
    
    //some photo taking related stuff
    private static final String TMP_DIR = ".tmp";
	private static final String PHOTO_TMP_FILENAME = "SD_tmp_pic.jpg";
	private static final int PHOTO_REQUEST_CODE = 1;
	private Uri photoUri;
	private boolean highRes = Config.USE_FULL_RES_PHOTOS_DEFAULT;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Util.d(null, TAG, "starting mainActivity");
        
        //setting the layout of the activity
        Display display = ((WindowManager)
        		getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        //check what orientation the phone is in
        //getOrientation() is depreciated as of API 8, but we're targeting
        //API 7, so we have to use it
        if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
        { //yeah this makes no sense, but it works...
        	setContentView(R.layout.main_activity_horiz);
        }
        else
        {
        	setContentView(R.layout.main_activity_vert);
        }
        
        //go to settings button
        Button settings = (Button) findViewById(R.id.main_settingsButton);
        settings.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent settingsIntent = new Intent(MainActivity.this,
                		SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
        
        //user surveys button
        Button surveys = (Button) findViewById(R.id.main_sampleButton);
        surveys.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent surveyIntent = new Intent(MainActivity.this,
                		UserSurveysActivity.class);
                startActivity(surveyIntent);
            }
        });
        
        //take photo button
        Button photo = (Button) findViewById(R.id.main_photoButton);
        photo.setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View view)
			{
				Intent photoIntent =
					new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				highRes = false;
//				if (Config.getSetting(MainActivity.this,
//						Config.USE_FULL_RES_PHOTOS,
//						Config.USE_FULL_RES_PHOTOS_DEFAULT))
				if (true)
				{
					photoUri = getPhotoUri();
					if (photoUri == null)
					{
						Util.w(null, TAG, "Unable to write full resolution "
								+ "photo; maybe the SD card is missing?");
						Util.w(null, TAG, "Atempting to get low "
								+ "resolution photo anyway");
					}
					else
					{
						photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
								photoUri);
						highRes = true;
					}
				}
				Util.d(null, TAG, "Using hi-res: " + highRes);
				Toast.makeText(MainActivity.this, "highRes: " + highRes, Toast.LENGTH_SHORT).show();
				startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
			}
		});
        
        //call survey admin button
        Button call = (Button) findViewById(R.id.main_callButton);
        call.setText(call.getText() + Config.getSetting(this,
        		Config.ADMIN_NAME, Config.ADMIN_NAME_DEFAULT));
        call.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	Intent callIntent = new Intent(Intent.ACTION_CALL);
            	callIntent.setData(Uri.parse("tel:"
            			+ Config.getSetting(MainActivity.this,
            					Config.ADMIN_PHONE_NUMBER,
            					Config.ADMIN_PHONE_NUMBER_DEFAULT)));
            	try
            	{
            		startActivity(callIntent);
            	}
            	catch (ActivityNotFoundException e)
            	{
            		Toast.makeText(MainActivity.this,
            				"Call failed!", Toast.LENGTH_SHORT);
            	}
            }
        });
        
        //exit button
        Button quit = (Button) findViewById(R.id.main_exitButton);
        quit.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	finish();
            }
        });
    }
    
    @Override
    protected void onStart()
    {
    	super.onStart();
        //add the survey progress bar
    	//do this here so the bar updates after a survey is
    	//finished without having to restart the activity
        TakenDBHandler tdbh = new TakenDBHandler(this);
        tdbh.openRead();
        int p = tdbh.getCompletionRate();
        tdbh.close();
        VerticalProgressBar progress = (VerticalProgressBar)
        	findViewById(R.id.main_progressBar);
        progress.setMax(100);
        int goal = Config.getSetting(this, Config.COMPLETION_GOAL,
        		Config.COMPLETION_GOAL_DEFAULT);
        progress.setSecondaryProgress(goal);
        if (p == TakenDBHandler.NO_PERCENTAGE)
        	//TODO find a way to make the bar indicate this better
        	progress.setProgress(0);
        else
        	progress.setProgress(p);
    }
    
    //now time for some wizardry that we need to deal with the camera
	//thanks to barmaley on StackOverflow
	
	//getPhotoUri gets a Uri where a photo can be stored
	//if one can't be found, then it returns null
    private Uri getPhotoUri()
    {
    	File tempDir = Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath()
        		+ "/" + TMP_DIR + "/");
        Util.d(null, TAG, "Temp dir: " + tempDir.getAbsolutePath());
        if(!tempDir.exists())
        {
        	if (!tempDir.mkdir())
        	{
        		Util.e(null, TAG, "Failed to create temp directory");
        		return null;
        	}
        }
        
        try
        {
        	File test = File.createTempFile("test", null, tempDir);
        	test.delete();
        	Util.i(null, TAG, "Temp directory is writable");
        	return Uri.fromFile(new File(tempDir, PHOTO_TMP_FILENAME));
        }
        catch (Exception e)
        {
        	Util.e(null, TAG, "Cannot write to temp directory");
        	return null;
        }

    }
    
    private void errorToast(String action)
    {
    	Toast.makeText(this, "Failed to " + action + " photo; "
        		+ "please tell the study administrator",
        		Toast.LENGTH_SHORT).show();
    }

    //for taking photos
    @Override
    public void onActivityResult(int requestCode,
    		int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == PHOTO_REQUEST_CODE)
        {
        	if (resultCode == RESULT_CANCELED) return;
        	if (resultCode != RESULT_OK)
        	{
        		Util.e(null, TAG, "Photo request failed with code: "
        				+ resultCode);
        		errorToast("take");
        		return;
        	}
            Bitmap bitmap;
            if (highRes)
            {
                try
                { //high res photo
                    ContentResolver cr = this.getContentResolver();
                    cr.notifyChange(photoUri, null);
                    bitmap = MediaStore.Images.Media.getBitmap(cr, photoUri);
                }
                catch (Exception e)
                {
                    Util.e(null, TAG, "Failed to load camera image; "
                    		+ "try disabling full res photos:");
                    Util.e(null, TAG, Util.fmt(e));
                    errorToast("load");
                    return;
                }
            }
            else
            {
            	try
            	{ //low res photo
                    bitmap = (Bitmap) intent.getExtras().get("data");
            	}
            	catch (Exception e)
            	{
            		Util.e(null, TAG, "Failed to load camera image!");
            		errorToast("load");
            		return;
            	}
            }
            
            //we have a picture at this point!
            ExtrasDBHandler edbh = new ExtrasDBHandler(this);
            edbh.openWrite();
            if (!edbh.writePhoto(bitmap,
            		System.currentTimeMillis() / 1000, highRes))
            {
            	Util.e(null, TAG, "Error writing photo to database");
            	errorToast("save");
            }
            else
            {
            	Toast.makeText(this, "Your photo has been submitted",
            			Toast.LENGTH_SHORT).show();
            	
            	Intent pushIntent = new Intent(this, ComsService.class);
            	pushIntent.setAction(ComsService.ACTION_UPLOAD_DATA);
            	pushIntent.putExtra(ComsService.EXTRA_DATA_TYPE,
            			ComsService.EXTRAS_DATA);
            	startService(pushIntent);
            }
            edbh.close();
            File tmp = new File(photoUri.getPath());
            tmp.delete();
        }
    }
}