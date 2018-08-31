package cn.ubia.bean;

import java.io.Serializable;

import cn.ubia.base.Constants;
import cn.ubia.util.PreferenceUtil;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class DeviceInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public boolean ChangePassword = false;
	// 设备编号
	public String UID;
	// 数据库ID
	public long DBID;
	public int Mode;
	// 与Camera关联的唯一编号
	public String UUID;
	public String viewAccount;
	public String viewPassword;
	public boolean garageDoor;
	private int channelIndex;
	public int country;
	public boolean isModifyPassword = true;
	public int getChannelIndex() {
		return channelIndex;
	}

	public void setChannelIndex(int channelIndex) {
		this.channelIndex = channelIndex;
		Log.v("","updateChannel setChannelIndex camera_channel------->>>>"+channelIndex);
	}

	public String nickName;
	public String tvCameraStateText;
	public boolean online;
	public boolean offline=false;
	public boolean lineing=false;
	//public transient Bitmap snapshot;
	 public Bitmap snapshot;
	public String location;
	public String Status;
	public boolean isPrivate;
	public boolean isPublic;
	public boolean isShare;

	public String shareUser;
	public String shareMessage;
	public int EventNotification;
	public boolean ShowTipsForFormatSDCard;
	// 是否报警
	public boolean isAlarm;
	// 是否是用户自己的
	public boolean isMy;
	public int connect_count = 0;
	public int n_gcm_count = 0;
	public int device_connect_state=-1;
	public int installmode = -1;//安装方式
	public int hardware_pkg;
	public String firmwareVersion;
	public int firmwareVersionPrefix;
	
	public String pushTimestamp;
	public String pushEvent;

	public DeviceInfo(long var1, String var3, String var4, String var5,
			String var6, String var7, String var8, int var9, int var10,
			Bitmap var11) {
		this.DBID = var1;
		this.UUID = var3;
		this.nickName = var4;
		this.UID = var5;
		this.viewAccount = var6;
		this.viewPassword = var7;
		this.Status = var8;
		this.EventNotification = var9;
		this.channelIndex = var10;
		this.snapshot = var11;
		this.online = false;
		this.ShowTipsForFormatSDCard = true;
	}

	public DeviceInfo(String UID, String NickName, String location,
			boolean isPrivate, boolean isPublic, boolean isShare, boolean online) {
		this.UID = UID;
		this.nickName = NickName;
		this.location = location;
		this.viewAccount = "admin";
		this.viewPassword = "admin123";
		this.channelIndex = 1;
		this.online = false;
		this.isPublic = isPublic;
		this.isPrivate = isPrivate;
		this.isShare = isShare;
		this.online = online;
		this.isAlarm = false;
	}

	// public DeviceInfo(String UID, String UUID,String NickName, String
	// location,String View_Account, String View_Password,
	// String Status,int ChannelIndex,
	// boolean isPrivate, boolean isPublic, boolean isShare, boolean online) {
	// this.UID = UID;
	// this.UUID = UUID;
	// this.nickName = NickName;
	// this.location = location;
	// this.viewAccount = View_Account;
	// this.viewPassword = View_Password;
	// this.channelIndex = ChannelIndex;
	// this.online = false;
	// this.isPublic = isPublic;
	// this.isPrivate = isPrivate;
	// this.isShare = isShare;
	// this.online = online;
	// this.isAlarm = false;
	// this.Status = Status;
	// }

	public DeviceInfo(long DBID, String UUID, String NickName, String UID,
			String location, String View_Account, String View_Password,
			String Status, int EventNotification, int ChannelIndex,
			Bitmap Snapshot, boolean isPrivate, boolean isPublic,
			boolean isShare, boolean isAlarm, boolean my, String shareUser,
			String shareMessage) {
		this.DBID = DBID;
		this.UUID = UUID;
		this.nickName = NickName;
		this.UID = UID;
		this.location = location;
		this.viewAccount = View_Account;
		this.viewPassword = View_Password;
		this.Status = Status;
		this.EventNotification = EventNotification;
		this.channelIndex = ChannelIndex;
		this.snapshot = Snapshot;
		this.online = false;
		this.ShowTipsForFormatSDCard = true;
		this.isPublic = isPublic;
		this.isPrivate = isPrivate;
		this.isShare = isShare;
		this.isAlarm = isAlarm;
		this.isMy = my;
		this.shareUser = shareUser;
		this.shareMessage = shareMessage;
	}

	public DeviceInfo(long DBID, String UUID, String nickName, String UID,
			String location, String viewAccount, String viewPassword,
			int channelIndex, boolean isPrivate, boolean isPublic,
			boolean isShare, boolean isAlarm, boolean my, String shareUser,
			String shareMessage) {
		this.DBID = DBID;
		this.UUID = UUID;
		this.nickName = nickName;
		this.UID = UID;
		this.location = location;
		this.viewAccount = viewAccount;
		this.viewPassword = viewPassword;
		this.channelIndex = channelIndex;
		this.online = false;
		this.isPublic = isPublic;
		this.isPrivate = isPrivate;
		this.isShare = isShare;
		this.isAlarm = isAlarm;
		this.isMy = my;
		this.shareUser = shareUser;
		this.shareMessage = shareMessage;
	}

	public DeviceInfo(String uid, String name, String location,
			String shareUser, String shareMessage, boolean online) {
		this.UID = uid;
		this.location = location;
		this.nickName = name;
		this.shareUser = shareUser;
		this.shareMessage = shareMessage;
		this.isMy = false;
		this.online = online;
		this.viewAccount = "admin";
		this.viewPassword = "admin123456";
	}

	public void setDBID(long DBID){
		this.DBID = DBID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((UID == null) ? 0 : UID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceInfo other = (DeviceInfo) obj;
		if (UID == null) {
			if (other.UID != null)
				return false;
		} else if (!UID.equals(other.UID))
			return false;
		return true;
	}

	public String getLastDeviceSnapPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ Constants.DEVICE_LAST_SNAPSHOT_PATH + UID + ".jpg";
	}

	public void copyFrom(DeviceInfo deviceInfo) {
		this.DBID = deviceInfo.DBID;
		this.UUID = deviceInfo.UUID;
		this.nickName = deviceInfo.nickName;
		this.UID = deviceInfo.UID;
		this.location = deviceInfo.location;
		this.viewAccount = deviceInfo.viewAccount;
		this.viewPassword = deviceInfo.viewPassword;
		this.channelIndex = deviceInfo.channelIndex;
		// this.online = false;
		this.isPublic = deviceInfo.isPublic;
		this.isPrivate = deviceInfo.isPrivate;
		this.isShare = deviceInfo.isShare;
		this.isAlarm = deviceInfo.isAlarm;
	}

	public String getPushTimestamp() {
		return pushTimestamp;
	}

	public void setPushTimestamp(String pushTimestamp) {
		this.pushTimestamp = pushTimestamp;
	}

	public String getPushEvent() {
		return pushEvent;
	}

	public void setPushEvent(String pushEvent) {
		this.pushEvent = pushEvent;
	}
}

/*
 * Location: D:\nwork\app\apk\dex2jar-0.0.9.13\classes_dex2jar.jar Qualified
 * Name: com.tutk.P2PCam264.DeviceInfo JD-Core Version: 0.6.2
 */