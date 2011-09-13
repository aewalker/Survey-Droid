/*---------------------------------------------------------------------------*
 * ImgScaleActivty.java                                                      *
 *                                                                           *
 * Shows the user a question with a slider that allows them to select a      *
 * value between 1 and 100.  This version shows an image at each end of the  *
 * scale.                                                                    *
 *---------------------------------------------------------------------------*/
package org.peoples.android.survey;

import org.peoples.android.R;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Shows the user a question with a slider that allows them to select a value
 * between 1 and 100.  This version shows an image at each end of the scale.
 * 
 * @author Austin Walker
 */
public class ImgScaleActivity extends ScaleActivity
{
	@Override
	protected void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		findViewById(R.id.scale_lowText).setVisibility(View.GONE);
		findViewById(R.id.scale_highText).setVisibility(View.GONE);
	}
	
	@Override
	protected void onSurveyLoaded()
	{
		super.onSurveyLoaded();
		//set the scale images
		ImageView lowImg = (ImageView) findViewById(R.id.scale_lowImg);
		ImageView highImg = (ImageView) findViewById(R.id.scale_highImg);
		lowImg.setImageBitmap(survey.getLowImg());
		highImg.setImageBitmap(survey.getHighImg());
	}
}