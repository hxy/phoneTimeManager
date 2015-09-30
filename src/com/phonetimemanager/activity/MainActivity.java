package com.phonetimemanager.activity;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashSet;

import com.phonetimemanager.R;
import com.phonetimemanager.objects.RestAlarm;
import com.phonetimemanager.objects.SleepAlarm;
import com.phonetimemanager.receiver.AlarmReceiver;
import com.phonetimemanager.service.ManagerService;
import com.phonetimemanager.service.ManagerService.MyBinder;
import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

import android.R.integer;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ManagerService mService;
	private ToggleButton sleeptoggleBtn;
	private ToggleButton resttoggleBtn;
	private TextView sleep_time;
	private TextView monday;
	private TextView tuesday;
	private TextView wednesday;
	private TextView thursday;
	private TextView friday;
	private TextView saturday;
	private TextView sunday;
	private TimePickerDialog tpd;
	private ServiceConnection connection;
	private Button restSetBtn;
	private EditText rest_interval_edit;
	private EditText rest_time_edit;
	private Button sleepSetBtn;
	private View sleep_cover;
	private View rest_cover;
	
	private int sleepAlarm_hour = 23;
	private int sleepAlarm_min = 0;
	private HashSet<String> sleepCycleSet = new HashSet<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sleep_time = (TextView) findViewById(R.id.sleep_time);
		monday = (TextView) findViewById(R.id.mon);
		tuesday = (TextView) findViewById(R.id.tue);
		wednesday = (TextView) findViewById(R.id.wed);
		thursday = (TextView) findViewById(R.id.thu);
		friday = (TextView) findViewById(R.id.fri);
		saturday = (TextView) findViewById(R.id.sat);
		sunday = (TextView) findViewById(R.id.sun);
		sleeptoggleBtn = (ToggleButton) findViewById(R.id.sleep_togglebutton);
		restSetBtn = (Button)findViewById(R.id.restAlarm_setBtn);
		rest_interval_edit = (EditText)findViewById(R.id.rest_interval_edit);
		rest_time_edit = (EditText)findViewById(R.id.rest_time_edit);
		sleepSetBtn = (Button)findViewById(R.id.sleepAlarm_setBtn);
		resttoggleBtn = (ToggleButton)findViewById(R.id.rest_togglebutton);
		sleep_cover = (View)findViewById(R.id.sleep_cover);
		rest_cover = (View)findViewById(R.id.rest_cover);
		
		initClickEvent();
		
		bindService();
	}

	private void bindService() {
		connection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mService = ((MyBinder) service).getMyService();
				initSleepTimeShow();
				initRestAlarmShow();
			}
		};

		bindService(new Intent(this, ManagerService.class), connection,
				BIND_AUTO_CREATE);
	}

	private void initClickEvent() {
		sleeptoggleBtn.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				if (on) {
					turnSleepModelOn();
				} else {
					turnSleepModelOff();
				}
			}

		});
		
		sleep_time.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tpd == null) {
					initSleepTimePickerDialog();
				}
				tpd.updateTime(sleepAlarm_hour, sleepAlarm_min);
				tpd.show();
			}
		});
		
		sunday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addOrRemoveWeekDay(v,"1");
			}
		});
		
		monday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addOrRemoveWeekDay(v,"2");
			}
		});
		tuesday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addOrRemoveWeekDay(v,"3");
			}
		});
		wednesday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addOrRemoveWeekDay(v,"4");
			}
		});
		thursday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addOrRemoveWeekDay(v,"5");
			}
		});
		friday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addOrRemoveWeekDay(v,"6");
			}
		});
		saturday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addOrRemoveWeekDay(v,"7");
			}
		});
		
		resttoggleBtn.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				if (on) {
					turnRestModelOn();
				} else {
					turnRestModelOff();
				}
			}

		});
		
		restSetBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 setRestAlarm();
			}
		});
		
		sleepSetBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setSleepAlarm(sleepAlarm_hour,sleepAlarm_min,sleepCycleSet);
			}
		});
		
//		rest_interval_edit.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Log.d("aaaa", "onclick");
//				rest_interval_edit.selectAll();
//			}
//		});

		rest_interval_edit.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.d("aaaa", "interval_hasFocus:"+hasFocus);
				if(hasFocus)
				rest_interval_edit.selectAll();
			}
		});
		rest_time_edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("aaaa", "onclick");
				rest_time_edit.selectAll();
			}
		});
	}

	private void addOrRemoveWeekDay(View v,String day){
		if(sleepCycleSet.contains(day)){
			v.setBackgroundColor(Color.TRANSPARENT);
			sleepCycleSet.remove(day);
		}else {
			v.setBackgroundColor(Color.GREEN);
			sleepCycleSet.add(day);
		}
	}
	
	private void turnSleepModelOn() {
		sleep_cover.setVisibility(View.GONE);
		sleepSetBtn.performClick();
	}

	private void turnSleepModelOff() {
		sleep_cover.setVisibility(View.VISIBLE);
		mService.cancleSleepAlarm();
	}
	
	private void turnRestModelOn() {
		rest_cover.setVisibility(View.GONE);
		restSetBtn.performClick();
	}

	private void turnRestModelOff() {
		rest_cover.setVisibility(View.VISIBLE);
		mService.cancleRestAlarm();
	}

	private void initSleepTimePickerDialog() {
		tpd = new TimePickerDialog(this, new OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				sleepAlarm_hour = hourOfDay;
				sleepAlarm_min = minute;
			}
		}, 23, 0, true);
	}

	private void setSleepAlarm(int hourOfDay, int minute,HashSet<String> sleepCycleSet) {
		if(sleepCycleSet.isEmpty()){
			Toast.makeText(this,R.string.sleepset_noweek_note, Toast.LENGTH_SHORT).show();
			return;
		}
		String hour = hourOfDay + "";
		String min = minute + "";
		if (hourOfDay < 10) {
			hour = "0" + hourOfDay;
		}
		if (minute < 10) {
			min = "0" + minute;
		}
		sleep_time.setText(hour + ":" + min);
		mService.setSleepAlarmTime(hourOfDay, minute,sleepCycleSet);
		Toast.makeText(this,R.string.sleep_set_successful, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
	}
	
	private void initSleepTimeShow(){
		SleepAlarm sleepAlarm = mService.getSleepAlarm();
		sleepAlarm_hour = sleepAlarm.getHour();
		sleepAlarm_min = sleepAlarm.getMinute();
		sleepCycleSet = sleepAlarm.getCycle();
		
		sleep_time.setText(sleepAlarm.getHourString()+":"+sleepAlarm.getMinuteString());
		HashSet<String> cycleSet = sleepAlarm.getCycle();
		if(cycleSet.contains("1")){
			sunday.setBackgroundColor(Color.GREEN);
		}
		if(cycleSet.contains("2")){
			monday.setBackgroundColor(Color.GREEN);
		}
		if(cycleSet.contains("3")){
			tuesday.setBackgroundColor(Color.GREEN);
		}
		if(cycleSet.contains("4")){
			wednesday.setBackgroundColor(Color.GREEN);
		}
		if(cycleSet.contains("5")){
			thursday.setBackgroundColor(Color.GREEN);
		}
		if(cycleSet.contains("6")){
			friday.setBackgroundColor(Color.GREEN);
		}
		if(cycleSet.contains("7")){
			saturday.setBackgroundColor(Color.GREEN);
		}
		
		if(sleepAlarm.getStatus()){
			sleeptoggleBtn.setToggleOn();
			sleep_cover.setVisibility(View.GONE);
		}
	}
	
	//-------------------------------------------
	
	private void setRestAlarm(){
		String intervalTime = rest_interval_edit.getText().toString();
		String restTime = rest_time_edit.getText().toString();
		if(intervalTime == null || restTime == null || "".equals(intervalTime) || "".equals(restTime)){
			Toast.makeText(this, R.string.restset_null_note, Toast.LENGTH_SHORT).show();
			return;
		}
		if("0".equals(intervalTime) || "0".equals(restTime)){
			Toast.makeText(this, R.string.restset_zero_note, Toast.LENGTH_SHORT).show();
			return;
		}
		mService.setRestAlarm(Integer.parseInt(restTime), Integer.parseInt(intervalTime));
		Toast.makeText(this, "设置每隔"+intervalTime+"分钟"+"休息"+restTime+"分钟", Toast.LENGTH_SHORT).show();
	}
	
	private void initRestAlarmShow(){
		RestAlarm restAlarm = mService.getSavedRestAlarm();
		rest_interval_edit.setText(restAlarm.getIntervalMin()+"");
		rest_time_edit.setText(restAlarm.getRestMin()+"");
		
		if(restAlarm.getStatus()){
			resttoggleBtn.setToggleOn();
			rest_cover.setVisibility(View.GONE);
		}
	}
}
