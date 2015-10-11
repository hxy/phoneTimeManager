package com.phonetimemanager.receiver;

import com.phonetimemanager.service.ManagerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenStatusReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent popup = new Intent(context,ManagerService.class);
		popup.setAction(intent.getAction());
		context.startService(popup);
	}

}
