package com.phonetimemanager.receiver;

import com.phonetimemanager.service.ManagerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("aaaa", "boot");
		Intent bootIntent = new Intent(context,ManagerService.class);
		bootIntent.setAction("boot");
		context.startService(bootIntent);
	}

}
