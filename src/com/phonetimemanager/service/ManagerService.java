package com.phonetimemanager.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.phonetimemanager.activity.MainActivity;
import com.phonetimemanager.objects.RestAlarm;
import com.phonetimemanager.objects.SleepAlarm;
import com.phonetimemanager.receiver.AlarmReceiver;
import com.phonetimemanager.receiver.ScreenStatusReceiver;
import com.phonetimemanager.view.CountdownDialog;
import com.phonetimemanager.view.CountdownDialog.DialogStatusInterface;
import com.phonetimemanager.view.WaringDialog;

import android.R;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class ManagerService extends Service implements DialogStatusInterface{

	private AlarmManager alarmManager;
	private SleepAlarm sleepAlarm;
	private RestAlarm restAlarm;
	private SharedPreferences sharedPreferences;
	private boolean isRestDialogShow = false;
	private ScreenStatusReceiver screenStatusReceiver;
	private boolean isScreenOn = true;
	private TelephonyManager telManager;
	private boolean delayPopup = false;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new MyBinder();
	}
	
	public class MyBinder extends Binder{
		public ManagerService getMyService(){
			return ManagerService.this;
		}
	}

	@Override
	public void setCountDownDialogStatus(boolean status) {
		isRestDialogShow = status;
	}

	@Override
	public boolean isScreenOn() {
		return isScreenOn;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("aaaa", "service oncreate");
		this.startForeground(0, new Notification());
		screenStatusReceiver = new ScreenStatusReceiver();
		telManager = (TelephonyManager)getSystemService(Service.TELEPHONY_SERVICE);
		alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		sleepAlarm = new SleepAlarm();
		restAlarm = new RestAlarm();
		sharedPreferences = getSharedPreferences("alarm", MODE_PRIVATE);
		initSleepAlarm();
		initRestAlarm();
		
		registerScreenActionReceiver();
	}
	CountdownDialog dialog;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("aaaa", "service start:"+intent.getAction());
		if("rest".equals(intent.getAction())){
			dialog = new CountdownDialog(this,intent.getIntExtra("restMinutes", 0),intent.getIntExtra("intervalMinutes", 0),this);
			if(telManager.getCallState() == TelephonyManager.CALL_STATE_RINGING || telManager.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK){
				delayPopup = true;
			}else{
				dialog.show();
			}
		}else if("sleep".equals(intent.getAction())){
			popUpSleepWaring();
		}else if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())){
			isScreenOn = true;
			if(!isRestDialogShow){
				if(restAlarm.getStatus()){
				   setRestAlarmToAlarmManager(restAlarm);
				}
			}
		}else if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
			isScreenOn = false;
			if(!isRestDialogShow){
				cancleRestAlarmWithOutSave();
			}
		}else if("call".equals(intent.getAction())){
			if(isRestDialogShow){
				dialog.hide();
			}
		}else if ("hangup".equals(intent.getAction())) {
			if(isRestDialogShow){
				dialog.resumeShow();
			}
			if(delayPopup){
				dialog.show();
				delayPopup = false;
			}
		}

		return START_REDELIVER_INTENT;
	}

	
	
	@Override
	public void onDestroy() {
		unregisterReceiver(screenStatusReceiver);
		
		super.onDestroy();
	}
//---------------sleep alarm-----------------
	public void popUpSleepWaring(){
		if(!isContainDayOfWeek()){
			return;
		}
		WaringDialog dialog = new WaringDialog(this);
		dialog.setIcon(R.drawable.ic_dialog_info);
		dialog.setTitle("睡觉时间到");
		dialog.setMessage("少年，该睡觉了");
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);  
		dialog.show();
	}
	
	private boolean isContainDayOfWeek(){
		Date date=new Date();
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if(!sleepAlarm.isContainDayOfWeek(dayOfWeek+"")){
        	return false;
        }
        return true;
	}
	
	public void setSleepAlarmTime(int hourOfDay, int minute,HashSet<String> sleepCycleSet){
		sleepAlarm.setStatus(true);
		sleepAlarm.setHour(hourOfDay);
		sleepAlarm.setMinute(minute);
		sleepAlarm.setCycle(sleepCycleSet);
		setSleepAlarmToAlarmManager(sleepAlarm);
		saveSleepAlarm(sleepAlarm);
	}
	
	@SuppressLint("NewApi")
	private void saveSleepAlarm(SleepAlarm sleepAlarm){
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("sleep_status", sleepAlarm.getStatus());
		editor.putInt("sleep_hour", sleepAlarm.getHour());
		editor.putInt("sleep_min", sleepAlarm.getMinute());
		editor.putStringSet("sleep_cycle", sleepAlarm.getCycle());
		editor.commit();
	}
	
	@SuppressLint("NewApi")
	private void initSleepAlarm(){
		sleepAlarm.setStatus(sharedPreferences.getBoolean("sleep_status", false));
		Log.d("aaaa", "initSleepAlarm--sleepAlarm.getStatus:"+sleepAlarm.getStatus());
		sleepAlarm.setHour(sharedPreferences.getInt("sleep_hour", 23));
		sleepAlarm.setMinute(sharedPreferences.getInt("sleep_min", 0));
		sleepAlarm.setCycle((HashSet<String>)sharedPreferences.getStringSet("sleep_cycle", null));
		if(sleepAlarm.getStatus()){
			setSleepAlarmToAlarmManager(sleepAlarm);
		}
	}
	
	private void setSleepAlarmToAlarmManager(SleepAlarm sleepAlarm){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, sleepAlarm.getHour());
		calendar.set(Calendar.MINUTE, sleepAlarm.getMinute());
		calendar.set(Calendar.SECOND, 0);
	    Intent intent =new Intent(this, AlarmReceiver.class);
	    intent.setAction("sleep");
	    PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
	}
	
	public SleepAlarm getSleepAlarm(){
		return sleepAlarm;
	}
	
	public void cancleSleepAlarm(){
	    Intent intent =new Intent(this, AlarmReceiver.class);
	    intent.setAction("sleep");
	    PendingIntent sender=PendingIntent.getBroadcast(this, 0, intent, 0);
	    alarmManager.cancel(sender);
	    
	    sleepAlarm.setStatus(false);
	    saveSleepAlarm(sleepAlarm);
	}

	//----------------reset alarm-------------------
	
	public void setRestAlarm(int restMin,int intervalMin){
		restAlarm.setStatus(true);
		restAlarm.setRestMin(restMin);
		restAlarm.setIntervalMin(intervalMin);
		setRestAlarmToAlarmManager(restAlarm);
		saveRestAlarm(restAlarm);
	}

	private void setRestAlarmToAlarmManager(RestAlarm restAlarm){
	    Intent intent =new Intent(this, AlarmReceiver.class);
	    intent.setAction("rest");
	    intent.putExtra("restMinutes", restAlarm.getRestMin());
	    intent.putExtra("intervalMinutes", restAlarm.getIntervalMin());
	    PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	    long firstGoOff = System.currentTimeMillis()+restAlarm.getIntervalMin()*1000*60;
	    alarmManager.set(AlarmManager.RTC, firstGoOff, sender);
	}
	
	private void saveRestAlarm(RestAlarm restAlarm){
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("rest_status",restAlarm.getStatus());
		editor.putInt("rest_time", restAlarm.getRestMin());
		editor.putInt("interval_time", restAlarm.getIntervalMin());
		editor.commit();
	}
	
	private void initRestAlarm(){
//		if(sharedPreferences.getAll().isEmpty()){
//			return;
//		}
		restAlarm.setStatus(sharedPreferences.getBoolean("rest_status", false));
		restAlarm.setRestMin(sharedPreferences.getInt("rest_time", 1));
		restAlarm.setIntervalMin(sharedPreferences.getInt("interval_time", 2));
		if(restAlarm.getStatus()){
			setRestAlarmToAlarmManager(restAlarm);
		}
	}
	
	public RestAlarm getSavedRestAlarm(){
		return restAlarm;
	}
	
	public void cancleRestAlarm(){
	    Intent intent =new Intent(this, AlarmReceiver.class);
	    intent.setAction("rest");
	    PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	    alarmManager.cancel(sender);

	    restAlarm.setStatus(false);
	    saveRestAlarm(restAlarm);
	}
	
	private void cancleRestAlarmWithOutSave(){
	    Intent intent =new Intent(this, AlarmReceiver.class);
	    intent.setAction("rest");
	    PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	    alarmManager.cancel(sender);
	}
	
	//——----------------------------------
	


    private void registerScreenActionReceiver(){  
        final IntentFilter filter = new IntentFilter();  
        filter.addAction(Intent.ACTION_SCREEN_OFF);  
        filter.addAction(Intent.ACTION_SCREEN_ON);  
        registerReceiver(screenStatusReceiver, filter);  
    }

}
