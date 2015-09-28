package com.phonetimemanager.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class WaringDialog extends AlertDialog {

	public WaringDialog(Context context) {
		super(context);
	}

	@Override
	public void cancel() {
		Log.d("aaaa", "cancel");
		//super.cancel();
	}

	@Override
	public void dismiss() {
		Log.d("aaaa", "dismiss");
		//super.dismiss();
	}
int count = 0;
	@Override
	public void onBackPressed() {
		Log.d("aaaa", "onBackPressed");
		if(++count >= 10){
			super.dismiss();
		}
		//super.onBackPressed();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("aaaa", "onTouchEvent");
		return super.onTouchEvent(event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	
	
}
