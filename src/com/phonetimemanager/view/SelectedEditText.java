package com.phonetimemanager.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class SelectedEditText extends EditText {

	public SelectedEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
	}

	public SelectedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public SelectedEditText(Context context) {
		super(context);
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		this.selectAll();
		return true;
	}
}
