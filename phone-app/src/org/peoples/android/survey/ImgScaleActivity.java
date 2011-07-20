/*---------------------------------------------------------------------------*
 * ImgScaleActivty.java                                                      *
 *                                                                           *
 * Shows the user a question with a slider that allows them to select a      *
 * value between 1 and 100.  This version shows an image at each end of the  *
 * scale.                                                                    *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import org.peoples.android.Config;
import org.peoples.android.R;
import org.peoples.android.VerticalSeekBar;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Shows the user a question with a slider that allows them to select a value
 * between 1 and 100.  This version shows an image at each end of the scale.
 * 
 * @author Austin Walker
 */
public class ImgScaleActivity extends QuestionActivity
{
	//the display; keep it since it's used a lot
	private Display display;
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		
		//setting the layout of the activity
       display =((WindowManager)
    		   getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        //check what orientation the phone is in
        //getOrientation() is depreciated as of API 8, but we're targeting
        //API 7, so we have to use it
        if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
        {
        	setContentView(R.layout.img_scale_horiz);
        }
        else
        {
        	setContentView(R.layout.img_scale_vert);
        }
		
		//set the buttons up
		findViewById(
				R.id.img_scale_backButton).setOnClickListener(prevListener);
		findViewById(
				R.id.img_scale_nextButton).setOnClickListener(nextListener);
	}
	
	@Override
	protected void answer()
	{
		int ans;
		int max;
		if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
		{
			SeekBar slider = (SeekBar) findViewById(R.id.img_scale_slider);
			ans = slider.getProgress();
			max = slider.getMax();
		}
		else
		{
			VerticalSeekBar slider =
				(VerticalSeekBar) findViewById(R.id.img_scale_slider);
			ans = slider.getProgress();
			max = slider.getMax();
		}
		ans++; //because SeekBar starts at 0
		ans *= (100.0 / (max + 1)); //have to scale the answer
		
		survey.answer(ans);
		if (Config.D) Log.d(TAG, "answering with " + ans);
	}

	@Override
	protected boolean isAnswered()
	{ //any position on the bar is fine, so this is just true
		return true;
	}
	
	@Override
	protected String getInvalidAnswerMsg()
	{ //this should never get called because of the way isAnswered is written
		return null;
	}

	@Override
	protected void onSurveyLoaded()
	{
		//set the question text
		TextView qText = (TextView) findViewById(R.id.img_scale_question);
		qText.setText(survey.getText());
		
		//set the scale images
		ImageView lowImg = (ImageView) findViewById(R.id.img_scale_lowImg);
		ImageView highImg = (ImageView) findViewById(R.id.img_scale_highImg);
		lowImg.setImageBitmap(survey.getLowImg());
		highImg.setImageBitmap(survey.getHighImg());
		if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
		{
			SeekBar slider = (SeekBar) findViewById(R.id.img_scale_slider);
			if (survey.getAnswerValue() == -1)
				slider.setProgress(slider.getMax() / 2);
			else slider.setProgress((int) ((survey.getAnswerValue() - 1)
					* (100.0 / (slider.getMax() + 1))));
		}
		else
		{
			VerticalSeekBar slider =
				(VerticalSeekBar) findViewById(R.id.img_scale_slider);
			if (survey.getAnswerValue() == -1)
				slider.setProgress(slider.getMax() / 2);
			else slider.setProgress((int) ((survey.getAnswerValue() - 1)
					* (100.0 / (slider.getMax() + 1))));
		}
	}
}