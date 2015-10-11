package com.phonetimemanager.receiver;

import com.phonetimemanager.service.ManagerService;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PhoneStatusReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		TelephonyManager telManager = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
		Intent serviceIntent = new Intent(context,ManagerService.class);
        switch (telManager.getCallState()) {
        case TelephonyManager.CALL_STATE_RINGING:
        	serviceIntent.setAction("call");
    		context.startService(serviceIntent);
            break;
        case TelephonyManager.CALL_STATE_IDLE:
        	serviceIntent.setAction("hangup");
    		context.startService(serviceIntent);
        	break;
        default:
            break;
        }
	}

}
