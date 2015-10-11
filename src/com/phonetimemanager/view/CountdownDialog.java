package com.phonetimemanager.view;

import java.util.Timer;
import java.util.TimerTask;

import com.phonetimemanager.R;
import com.phonetimemanager.receiver.AlarmReceiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

public class CountdownDialog extends AlertDialog {

	private int restMinutes = 0;
	private int intervalMinutes = 0;
	
	private int countDownMinutes = 0;
	private int countDownSeconds = 60;
	private Handler handler;
	private final int TICK = 100;
	private TimerTask task;
	private String message = null;
	private Context context;
	
	private DialogStatusInterface listener;
	
	
	public CountdownDialog(Context context) {
		super(context);
		this.context = context;
	}
	
	public CountdownDialog(Context context,int restMinutes,int intervalMinutes,DialogStatusInterface listener){
		this(context);
		this.listener = listener;
		this.restMinutes = restMinutes;
		this.intervalMinutes = intervalMinutes;
		
		this.countDownMinutes = restMinutes - 1;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//setContentView(R.layout.countdown_dialog);
		this.setIcon(android.R.drawable.ic_dialog_info);
		this.setTitle("休息时间到");
		this.setMessage("hou cai ke yi wan shou ji o");
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case TICK: updateView();break;
				}
			}
		};
		task = new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(TICK);
			}
		};
		super.onCreate(savedInstanceState);
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
		if(++count >= 50){
			super.dismiss();
		}
		//super.onBackPressed();
	}

	@Override
	public void show() {
		super.show();
		listener.setCountDownDialogStatus(true);
//		Timer timer = new Timer(true);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, 1000);
	}

	public void resumeShow(){
		super.show();
	}
	
	private void updateView(){
		if(countDownMinutes == 0 && countDownSeconds == 1){
			super.dismiss();
			task.cancel();
			listener.setCountDownDialogStatus(false);
			if(listener.isScreenOn()){
				setRestAlarm(context,intervalMinutes,restMinutes);
			}
		}else if(countDownSeconds == 0){
			countDownMinutes--;
			countDownSeconds = 59;
		}else {
			countDownSeconds--;
		}
		message = "少年，休息一会儿吧 "+countDownMinutes+":"+countDownSeconds+" 后才可以玩手机哦！";
		this.setMessage(message);
	}

	private void setRestAlarm(Context context,int intervalMinutes,int restMinutes){
	    Intent intent =new Intent(context, AlarmReceiver.class);
	    intent.setAction("rest");
	    intent.putExtra("restMinutes", restMinutes);
	    intent.putExtra("intervalMinutes", intervalMinutes);
	    PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	    AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	    long goOff = System.currentTimeMillis()+intervalMinutes*1000*60;
	    alarmManager.set(AlarmManager.RTC, goOff, sender);
	}
	
	public interface DialogStatusInterface{
		public void setCountDownDialogStatus(boolean status);
		public boolean isScreenOn();
	}
}
