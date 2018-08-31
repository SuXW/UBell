package cn.ubia.bean;

import java.io.Serializable;

public class AlarmMessage implements Serializable {
	private String alarminfo, uid, alarmTime,event;

	public AlarmMessage(String alarminfo, String uid, String alarmTime) {
		this.alarminfo = alarminfo;
		this.uid = uid;
		this.alarmTime = alarmTime;
	}

	public AlarmMessage(String alarminfo, String uid, String alarmTime,String event) {
		this.alarminfo = alarminfo;
		this.uid = uid;
		this.alarmTime = alarmTime;
		this.event = event;
	}


	public String getAlarminfo() {
		return alarminfo;
	}

	public void setAlarminfo(String alarminfo) {
		this.alarminfo = alarminfo;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}
}