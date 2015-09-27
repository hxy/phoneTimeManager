package com.phonetimemanager.objects;

import java.util.HashSet;

public class SleepAlarm {

	private int hour;
	private int minute;
	private HashSet<String> cycle = new HashSet<String>();
	
	
	public void addDayOfWeek(int dayOfWeek){
		cycle.add(dayOfWeek+"");
	}
	public HashSet<String> getCycle(){
		return cycle;
	}
	public void setCycle(HashSet<String>cycle){
		if(cycle!=null){
			this.cycle = cycle;
		}
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}

	public boolean isContainDayOfWeek(String dayOfWeek){
		return cycle.contains(dayOfWeek);
	}
	
	public String getHourString(){
		if(hour<10){
			return "0"+hour;
		}
		return hour+"";
	}
	
	public String getMinuteString(){
		if(minute<10){
			return "0"+minute;
		}
		return minute+"";
	}
}
