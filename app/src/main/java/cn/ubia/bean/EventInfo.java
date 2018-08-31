package cn.ubia.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

import com.ubia.IOTC.AVIOCTRLDEFs;

public class EventInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static byte EVENT_ALL = 0x00, // all event type
			EVENT_MOTIONDECT = 0x01, // motion detect start
			EVENT_VIDEOLOST = 0x02, // video lost alarm
			EVENT_IOALARM = 0x03, // IO alarm start

			EVENT_MOTIONPASS = 0x04, // motion detect end
			EVENT_VIDEORESUME = 0x05, // video resume
			EVENT_IOALARMPASS = 0x06, // IO alarm end

			EVENT_EXPT_REBOOT = 0x10, // system exception reboot
			EVENT_SDFAULT = 0x11; // SDCard record exception

	public static byte STATUS_READED = 1, STATUS_UNREAD = 0;

	public AVIOCTRLDEFs.STimeDay sTimeDay;
	public byte type; // refer to ENUM_EVENTTYPE
	public byte status; // unread = 0, read = 1
	public short length;
	public String uuid = UUID.randomUUID().toString();

	public EventInfo(AVIOCTRLDEFs.STimeDay sTimeDay, byte type, byte status,
			short length) {
		this.sTimeDay = sTimeDay;
		this.type = type;
		this.status = status;
		this.length = length;
	}

	@Override
	public String toString() {
		return "Event [time=" + sTimeDay.getTimeInMillis() + ", type=" + type
				+ ", status=" + status + ", length=" + length + "]";
	}

}
