package com.phonetimemanager.objects;

public class RestAlarm {

	private int restMin;
	private int intervalMin;
	private boolean status = false;
	
	public int getRestMin() {
		return restMin;
	}
	public void setRestMin(int restMin) {
		this.restMin = restMin;
	}
	public int getIntervalMin() {
		return intervalMin;
	}
	public void setIntervalMin(int intervalMin) {
		this.intervalMin = intervalMin;
	}
	public boolean getStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
}
