package com.phonetimemanager.receiver;

import com.phonetimemanager.service.ManagerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Log.d("aaaa", "receive");
		Intent popup = new Intent(arg0,ManagerService.class);
		popup.setAction(arg1.getAction());
		popup.putExtra("restMinutes", arg1.getIntExtra("restMinutes", 0));
		popup.putExtra("intervalMinutes", arg1.getIntExtra("intervalMinutes", 0));
		arg0.startService(popup);
	}

}
