/*---------------------------------------------------------------------------*
 * ScaleActivity.java                                                        *
 *                                                                           *
 * Shows the user a question with a slider that allows them to select a      *
 * value between 1 and 100.                                                  *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import org.peoples.android.R;
import org.peoples.android.Util;
import org.peoples.android.VerticalSeekBar;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

public abstract class ScaleActivity extends QuestionActivity
{
	//the display; keep it since it's used a lot
	private Display display;
	
	//key to save the value in when saving/retrieving state
	private static final String VALUE_KEY = "value_key";
	
	//key to save whether or not the slider has been moved
	private static final String MOVED_KEY = "moved_key";
	
	//the current value
	private int value = 50;
	
	//is this activity being restored after being stopped?
	private boolean isRestoring = false;
	
	//has the slider been moved at all?
	private boolean sliderMoved = false;
	
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		
		//setting the layout of the activity
        display = ((WindowManager)
        		getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		//check what orientation the phone is in
        //getOrientation() is depreciated as of API 8, but we're targeting
        //API 7, so we have to use it
        if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
        {
        	setContentView(R.layout.scale_horiz);
        }
        else
        {
        	setContentView(R.layout.scale_vert);
        }
		
		//set the buttons up
		findViewById(
				R.id.scale_backButton).setOnClickListener(prevListener);
		findViewById(
				R.id.scale_nextButton).setOnClickListener(nextListener);
		
		if (savedState != null && savedState.containsKey(VALUE_KEY))
		{
			sliderMoved = savedState.getBoolean(MOVED_KEY);
			value = savedState.getInt(VALUE_KEY);
			isRestoring = true;
		}
	}
	
	@Override
	protected void answer()
	{
		int ans;
		int max;
		SeekBar sliderHoriz =
			(SeekBar) findViewById(R.id.scale_sliderHoriz);
		VerticalSeekBar sliderVert =
			(VerticalSeekBar) findViewById(R.id.scale_sliderVert);
		if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
		{
			ans = sliderHoriz.getProgress();
			max = sliderHoriz.getMax();
		}
		else
		{
			ans = sliderVert.getProgress();
			max = sliderVert.getMax();
		}
		ans++; //because SeekBar starts at 0
		ans *= (100.0 / (max + 1)); //have to scale the answer
		
		survey.answer(ans);
		Util.d(this, TAG, "answering with " + ans);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle state)
	{
		super.onSaveInstanceState(state);
		state.putInt(VALUE_KEY, value);
		state.putBoolean(MOVED_KEY, sliderMoved);
	}

	@Override
	protected String getInvalidAnswerMsg()
	{
		return "Please move the slider to select an answer";
	}

	@Override
	protected boolean isAnswered()
	{ //make sure the slider has been adjusted at least a bit
		return sliderMoved;
	}

	@Override
	protected void onSurveyLoaded()
	{
		//set the question text
		TextView qText = (TextView) findViewById(R.id.scale_question);
		qText.setText(survey.getText());
		
		SeekBar sliderHoriz =
			(SeekBar) findViewById(R.id.scale_sliderHoriz);
		VerticalSeekBar sliderVert =
			(VerticalSeekBar) findViewById(R.id.scale_sliderVert);
		if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT)
		{
			sliderVert.setVisibility(View.GONE);
			sliderHoriz.setOnSeekBarChangeListener(
					new SeekBar.OnSeekBarChangeListener()
			{
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar)
				{
					sliderMoved = true;
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser)
				{
					value = progress;
					sliderMoved = true;
				}
			});
			if (isRestoring)
				sliderHoriz.setProgress(value);
			else if (survey.getAnswerValue() == -1)
				sliderHoriz.setProgress(sliderHoriz.getMax() / 2);
			else sliderHoriz.setProgress((int) ((survey.getAnswerValue() - 1)
					* (100.0 / (sliderHoriz.getMax() + 1))));
		}
		else
		{
			sliderHoriz.setVisibility(View.GONE);
			sliderVert.setOnSeekBarChangeListener(
					new VerticalSeekBar.OnSeekBarChangeListener()
			{
				@Override
				public void onStopTrackingTouch(VerticalSeekBar seekBar) {}
				
				@Override
				public void onStartTrackingTouch(VerticalSeekBar seekBar)
				{
					sliderMoved = true;
				}
				
				@Override
				public void onProgressChanged(VerticalSeekBar seekBar, int progress,
						boolean fromUser)
				{
					value = progress;
				}
			});
			if (isRestoring)
				sliderVert.setProgress(value);
			else if (survey.getAnswerValue() == -1)
				sliderVert.setProgress(sliderVert.getMax() / 2);
			else sliderVert.setProgress((int) ((survey.getAnswerValue() - 1)
					* (100.0 / (sliderVert.getMax() + 1))));
		}
	}
}
