package com.phonetimemanager.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.phonetimemanager.activity.MainActivity;
import com.phonetimemanager.objects.RestAlarm;
import com.phonetimemanager.objects.SleepAlarm;
import com.phonetimemanager.receiver.AlarmReceiver;
import com.phonetimemanager.view.WaringDialog;

import android.R;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TimePicker;

public class ManagerService extends Service {

	private AlarmManager alarmManager;
	private SleepAlarm sleepAlarm;
	private RestAlarm restAlarm;
	private SharedPreferences sharedPreferences;
	
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
	public void onCreate() {
		super.onCreate();
		alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		sleepAlarm = new SleepAlarm();
		restAlarm = new RestAlarm();
		sharedPreferences = getSharedPreferences("alarm", MODE_PRIVATE);
		InitSleepAlarm();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if("rest".equals(intent.getAction())){
			WaringDialog dialog = new WaringDialog(this);
			dialog.setIcon(R.drawable.ic_dialog_info);
			dialog.setTitle("waring");
			dialog.setMessage("时间到");
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);  
			dialog.show();
		}
		if("sleep".equals(intent.getAction())){
			popUpSleepWaring();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void popUpSleepWaring(){
		if(!isContainDayOfWeek()){
			return;
		}
		WaringDialog dialog = new WaringDialog(this);
		dialog.setIcon(R.drawable.ic_dialog_info);
		dialog.setTitle("waring");
		dialog.setMessage("时间到");
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
	
	public void setSleepAlarmTime(int hourOfDay, int minute){
		sleepAlarm.setHour(hourOfDay);
		sleepAlarm.setMinute(minute);
		setSleepAlarmToAlarmManager(sleepAlarm);
		saveSleepAlarm(sleepAlarm);
	}
	
	@SuppressLint("NewApi")
	private void saveSleepAlarm(SleepAlarm sleepAlarm){
		Editor editor = sharedPreferences.edit();
		editor.putInt("sleep_hour", sleepAlarm.getHour());
		editor.putInt("sleep_min", sleepAlarm.getMinute());
		editor.putStringSet("sleep_cycle", sleepAlarm.getCycle());
		editor.commit();
	}

	public void setSleepAlarmCycle(int dayOfWeek){
		sleepAlarm.addDayOfWeek(dayOfWeek);
		setSleepAlarmToAlarmManager(sleepAlarm);
		saveSleepAlarm(sleepAlarm);
	}
	
	@SuppressLint("NewApi")
	private void InitSleepAlarm(){
		if(sharedPreferences.getAll().isEmpty()){
			return;
		}
		sleepAlarm.setHour(sharedPreferences.getInt("sleep_hour", 23));
		sleepAlarm.setMinute(sharedPreferences.getInt("sleep_min", 0));
		sleepAlarm.setCycle((HashSet<String>)sharedPreferences.getStringSet("sleep_cycle", null));
		//setAlarmToAlarmManager(sleepAlarm);
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
	
	public String getSleepTimeScreenShow(){
		String sleepTime = sleepAlarm.getHourString()+":"+sleepAlarm.getMinuteString();
		return sleepTime;
	}
	
	public HashSet<String> getSleepCycleScreenShow(){
		return sleepAlarm.getCycle();
	}
	
	//----------------reset alarm-------------------
	
	public void setRestAlarm(int restMin,int intervalMin){
		restAlarm.setRestMin(restMin);
		restAlarm.setIntervalMin(intervalMin);
		setRestAlarmToAlarmManager(restAlarm);
		saveRestAlarm(restAlarm);
	}
	
	private void setRestAlarmToAlarmManager(RestAlarm restAlarm){
	    Intent intent =new Intent(this, AlarmReceiver.class);
	    intent.setAction("rest");
	    PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
	    int firstGoOff = restAlarm.getIntervalMin()*1000*60;
	    int intervalTime = 60*1000*(restAlarm.getIntervalMin()+restAlarm.getRestMin());
		alarmManager.setRepeating(AlarmManager.RTC, firstGoOff, intervalTime, sender);
	}
	
	private void saveRestAlarm(RestAlarm restAlarm){
		Editor editor = sharedPreferences.edit();
		editor.putInt("rest_time", restAlarm.getRestMin());
		editor.putInt("interval_time", restAlarm.getIntervalMin());
		editor.commit();
	}
}
