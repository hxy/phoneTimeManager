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
		//super.cancel();
	}

	@Override
	public void dismiss() {
		//super.dismiss();
	}
	int count = 0;
	@Override
	public void onBackPressed() {
		if(++count >= 10){
			super.dismiss();
		}
		//super.onBackPressed();
	}

	
	
}
