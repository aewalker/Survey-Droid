/*---------------------------------------------------------------------------*
 * SurveyExtrasActivity.java                                                 *
 *                                                                           *
 * Allows the user to submit a photo or voice recording with their survey.   *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.peoples.android.Config;
import org.peoples.android.R;
import org.peoples.android.Util;
import org.peoples.android.survey.SurveyService.SurveyBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class SurveyExtrasActivity extends Activity
{
	//logging tag
	private static final String TAG = "SurveyExtrasActivity";
	
	//the name of the temporary files to create
	private static final String PHOTO_TMP_FILENAME = "PEOPLES_tmp_pic.jpg";
	private static final String VOICE_TMP_FILENAME = "PEOPLES_tmp_voice";
	
	//the request code to use for a photo
	private static final int PHOTO_REQUEST_CODE = 1;
	
	//the current survey
	private Survey survey = null;
	
	//the take photo button
	private Button photo;
	
	//the record voice button
	private Button voice;
	
	//is the phone recording?
	private boolean recording;
	
	//the MediaRecorder we're going to use to record voice
	private final MediaRecorder recorder = new MediaRecorder();
	
	//connection to the SurveyService
	private ServiceConnection connection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder)
		{
			SurveyBinder sBinder = (SurveyBinder) binder;
			survey = sBinder.getSurvey();
			if (survey.hasPhoto()) photo.setClickable(false);
			if (survey.hasVoice()) voice.setClickable(false);
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {}
	};
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		Util.d(null, TAG, "Creating SurveyExtrasActivity");
		
		//get the survey
		Intent bindIntent = new Intent(this, SurveyService.class);
		bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
		
		//setting the layout of the activity
        Display display = ((WindowManager)
        		getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        //check what orientation the phone is in
        //getOrientation() is depreciated as of API 8, but we're targeting
        //API 7, so we have to use it
        if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
        {
        	setContentView(R.layout.survey_extras_activity_horiz);
        }
        else
        {
        	setContentView(R.layout.survey_extras_activity_vert);
        }
        
        photo = (Button) findViewById(R.id.survey_extras_photoButton);
		photo.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent photoIntent =
					new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (Config.getSetting(getThis(), Config.USE_FULL_RES_PHOTOS,
						Config.USE_FULL_RES_PHOTOS_DEFAULT))
				{
					photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				}
				else
				{
					photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(new File(
									Environment.getExternalStorageDirectory(),
									PHOTO_TMP_FILENAME)));
				}
				startActivityForResult(photoIntent, PHOTO_REQUEST_CODE);
			}
		});
		
		//set up the audio recording parameters
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(Config.getSetting(
				this, Config.VOICE_FORMAT, Config.VOICE_FORMAT_DEFAULT));
		recorder.setAudioEncoder(
				MediaRecorder.AudioEncoder.AMR_NB);
		File outputFile = new File(getDir("tmp", 0).getAbsoluteFile(),
				VOICE_TMP_FILENAME);
		if (outputFile.exists())
			outputFile.delete();
		try
		{
			if (!outputFile.createNewFile())
				Util.e(this, TAG, "Cannont create file: "
						+ outputFile.getAbsolutePath());
		}
		catch (IOException e)
		{
			Util.e(this, TAG, Util.fmt(e));
		}
		recorder.setOutputFile(outputFile.getAbsolutePath());
		voice = (Button) findViewById(R.id.survey_extras_voiceButton);
		voice.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!recording)
				{
					try
					{
						recorder.prepare();
						recorder.start();
						recording = true;
						voice.setText("Recording");
					}
					catch (Exception e)
					{
						Util.e(getThis(), TAG, Util.fmt(e));
					}
				}
				else
				{
					try
					{
						recorder.stop();
						recording = false;
						File outputFile = new File(getDir("tmp",
								0).getAbsoluteFile(), VOICE_TMP_FILENAME);
						Uri voiceUri = Uri.fromFile(outputFile);
						if (!survey.addVoice(
								getContentResolver().openInputStream(
										voiceUri)))
							Util.e(getThis(), TAG,
									"Failed to add voice recording!");
						else
						{
							outputFile.deleteOnExit();
							voice.setText("Recording stored");
							voice.setClickable(false);
						}
					}
					catch (Exception e)
					{
						Util.e(getThis(), TAG, Util.fmt(e));
					}
				}
			}
		});
		
		Button finish = (Button) findViewById(R.id.survey_extras_doneButton);
		finish.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent finishIntent =
					new Intent(getThis(), SurveyService.class);
				finishIntent.setAction(SurveyService.ACTION_END_SURVEY);
				startService(finishIntent);
				finish();
			}
		});
	}
	
	//hack to get main object
	private SurveyExtrasActivity getThis()
	{
		return this;
	}
	
	@Override
	protected void onActivityResult(
			int requestCode, int resultCode, Intent intent)
	{
		Util.v(null, TAG, "in onActivityResult");
		boolean worked = false;
		if (resultCode == Activity.RESULT_OK &&
			requestCode == PHOTO_REQUEST_CODE)
		{ //we have a photo
			if (Config.getSetting(getThis(), Config.USE_FULL_RES_PHOTOS,
					Config.USE_FULL_RES_PHOTOS_DEFAULT) && intent == null)
			{
				photo.setClickable(false);
				photo.setText("Full resolution photos not supported");
				Util.w(null, TAG, "Full resolution photos not supported.");
				Util.w(null, TAG, "Please disable in config to allow photo capture.");
				Toast.makeText(this, "Full resolution photos are not "
						+ "supported; please contact the study administrator.",
						Toast.LENGTH_LONG);
				return;
			}
			try
			{
				Util.v(this, TAG, "got a photo");
				if (Config.getSetting(getThis(), Config.USE_FULL_RES_PHOTOS,
						Config.USE_FULL_RES_PHOTOS_DEFAULT))
				{
					worked = survey.addPhoto(
						getContentResolver().openInputStream(
						intent.getData()));
				}
				else
				{
					File picFile = new File(
							Environment.getExternalStorageDirectory(),
							PHOTO_TMP_FILENAME);
					Uri picUri = Uri.parse(
							MediaStore.Images.Media.insertImage(
							getContentResolver(),
							picFile.getAbsolutePath(), null, null));
					worked = survey.addPhoto(
							getContentResolver().openInputStream(picUri));
				}
			}
			catch (FileNotFoundException e)
			{
				Util.e(this, TAG, Util.fmt(e));
			}
		}
		
		if (worked)
		{
			photo.setClickable(false);
			photo.setText("Photo stored");
		}
	}
	
	@Override
	protected void onRestart()
	{
		super.onRestart();
		Intent bindIntent = new Intent(this, SurveyService.class);
		bindService(bindIntent, connection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		unbindService(connection);
	}
}
