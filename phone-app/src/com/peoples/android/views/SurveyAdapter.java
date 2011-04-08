package com.peoples.android.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;

public class SurveyAdapter extends ArrayAdapter {

	public SurveyAdapter(Context context, int textViewResourceId,
			Object[] objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		return new RadioButton(this.getContext());
	}

}
