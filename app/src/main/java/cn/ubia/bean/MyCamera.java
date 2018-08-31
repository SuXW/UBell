package cn.ubia.bean;

import android.graphics.Bitmap;
import android.util.Log;

import com.ubia.IOTC.AVFrame;
import com.ubia.IOTC.AVIOCTRLDEFs;
import com.ubia.IOTC.Camera;
import com.ubia.IOTC.IRegisterIOTCListener;
import com.ubia.IOTC.Packet;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MyCamera extends Camera implements IRegisterIOTCListener {

	public int LastAudioMode;
	private boolean bIsIOAlarm;

	private boolean bIsMotionDetected;
	private int cbSize = 0;
	private String mAcc;
	private int mEventCount = 0;
	private String mName;
	private String mPwd;
	private List mStreamDefs = Collections.synchronizedList(new ArrayList());
	private String mUID;
	private UUID mUUID = UUID.randomUUID();
	private int nGMTDiff = 0;
	private int nEnableDST = 0;
	private byte[] szTimeZoneString = new byte[256];
	private int TimeZone; 
	public MyCamera(String var1, String var2, String var3, String var4) {
		this.mName = var1;
		this.mUID = var2;
		this.setmDevUID(mUID); 
		this.mAcc = var3;
		this.mPwd = var4;
		this.isConected = false;
		this.registerIOTCListener(this);
	}

	public String getmUID() {
		return mUID;
	}

	public void setmUID(String mUID) {
		this.mUID = mUID;
	}

	public boolean isbIsIOAlarm() {
		return bIsIOAlarm;
	}

	public void setbIsIOAlarm(boolean bIsIOAlarm) {
		this.bIsIOAlarm = bIsIOAlarm;
	}

	public void connect(String var1) {
		super.connect(var1);
		this.mUID = var1;
	}

	public void connect(String var1, String var2) {
		super.connect(var1, var2);
		this.mUID = var1;
	}

	public void disconnect() {
		super.disconnect();
		this.isConected = false;
		this.mStreamDefs.clear();
	}

	public boolean getAudioInSupported(int var1) {
		return (1L & this.getChannelServiceType(var1)) == 0L;
	}

	public int getAudioOutEncodingFormat(int var1) {
		return (4096L & this.getChannelServiceType(var1)) == 0L ? 141 : 139;
	}

	public boolean getAudioOutSupported(int var1) {
		return (2L & this.getChannelServiceType(var1)) == 0L;
	}

	public boolean getDeviceInfoSupport(int var1) {
		return (16384L & this.getChannelServiceType(var1)) == 0L;
	}

	public boolean getEnvironmentModeSupported(int var1) {
		return (1024L & this.getChannelServiceType(var1)) == 0L;
	}

	public int getEventCount() {
		return this.mEventCount;
	}

	public boolean getEventListSupported(int var1) {
		return (8L & this.getChannelServiceType(var1)) == 0L;
	}

	public boolean getEventSettingSupported(int var1) {
		return (64L & this.getChannelServiceType(var1)) == 0L;
	}

	public int getGMTDiff() {
		return this.nGMTDiff;
	}

	public int getnEnableDST() {
		return this.nEnableDST;
	}

	public boolean getMultiStreamSupported(int var1) {
		return (2048L & this.getChannelServiceType(var1)) == 0L;
	}

	public String getName() {
		return this.mName;
	}

	public boolean getPanTiltSupported(int var1) {
		return (4L & this.getChannelServiceType(var1)) == 0L;
	}

	public String getPassword() {
		return this.mPwd;
	}

	public boolean getPlaybackSupported(int var1) {
		return (16L & this.getChannelServiceType(var1)) == 0L;
	}

	public boolean getRecordSettingSupported(int var1) {
		return (128L & this.getChannelServiceType(var1)) == 0L;
	}

	public boolean getSDCardFormatSupported(int var1) {
		return (256L & this.getChannelServiceType(var1)) == 0L;
	}

	public AVIOCTRLDEFs.SStreamDef[] getSupportedStream() {
		AVIOCTRLDEFs.SStreamDef[] var1 = new AVIOCTRLDEFs.SStreamDef[this.mStreamDefs
				.size()];

		for (int var2 = 0; var2 < var1.length; ++var2) {
			var1[var2] = (AVIOCTRLDEFs.SStreamDef) this.mStreamDefs.get(var2);
		}

		return var1;
	}

	public boolean getTimeZone(int var1) {
		return (65536L & this.getChannelServiceType(var1)) == 0L;
	}

	public byte[] getTimeZoneString() {
		return this.szTimeZoneString;
	}

	public String getUID() {
		return this.mUID;
	}

	public String getUUID() {
		return this.mUUID.toString();
	}

	public boolean getVideoFlipSupported(int var1) {
		return (512L & this.getChannelServiceType(var1)) == 0L;
	}

	public boolean getVideoQualitySettingSupport(int var1) {
		return (8192L & this.getChannelServiceType(var1)) == 0L;
	}

	public boolean getWiFiSettingSupported(int var1) {
		return (32L & this.getChannelServiceType(var1)) == 0L;
	}

	public void receiveChannelInfo(Camera var1, int var2, int var3) {
	}

	public void receiveFrameData(Camera var1, int var2, Bitmap var3) {
	}

	public void receiveFrameInfo(Camera var1, int var2, long var3, int var5,
								 int var6, AVFrame avFrame , int var8) {
	}

	public void receiveIOCtrlData(Camera var1, int var2, int var3, byte[] var4) {
		if (var3 == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_RESP) {
			this.mStreamDefs.clear();
			int var15 = Packet.byteArrayToInt_Little(var4, 0);
			if (var2 == 0 && this.getMultiStreamSupported(0)) {
				for (int var16 = 0; var16 < var15; ++var16) {
					byte[] var17 = new byte[8];
					System.arraycopy(var4, 4 + var16 * 8, var17, 0, 8);
					AVIOCTRLDEFs.SStreamDef var18 = new AVIOCTRLDEFs.SStreamDef(
							var17);
					this.mStreamDefs.add(var18);
					var1.start(var18.channel, this.mAcc, this.mPwd);
				}
			}
		} else if (var3 == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_EVENT_REPORT) {
			int var14 = Packet.byteArrayToInt_Little(var4, 12);
			if (var14 == 1) {
				if (!this.bIsMotionDetected) {
					++this.mEventCount;
				}

				this.bIsMotionDetected = true;
				return;
			}

			if (var14 == 4) {
				this.bIsMotionDetected = false;
				return;
			}

			if (var14 == 3) {
				if (!this.bIsIOAlarm) {
					++this.mEventCount;
				}

				this.bIsIOAlarm = true;
				return;
			}

			if (var14 == 6) {
				this.bIsIOAlarm = false;
				return;
			}
		} else {
			if (var3 == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_RESP) {
				byte[] var8 = new byte[4];
				byte[] var9 = new byte[4];
				byte[] var10 = new byte[4];
				System.arraycopy(var4, 0, var8, 0, 4);
				this.cbSize = Packet.byteArrayToInt_Little(var8);
				System.arraycopy(var4, 4, var9, 0, 4);
				this.nEnableDST = Packet.byteArrayToInt_Little(var9);
				System.arraycopy(var4, 8, var10, 0, 4);
				this.nGMTDiff = Packet.byteArrayToInt_Little(var10);
				System.arraycopy(var4, 12, this.szTimeZoneString, 0, 256);

				try {
					Log.i("szTimeZoneString", new String(this.szTimeZoneString,
							0, this.szTimeZoneString.length, "utf-8"));
					Log.i("szTimeZoneString", new String(this.szTimeZoneString,
							0, this.szTimeZoneString.length, "utf-8"));
					return;
				} catch (UnsupportedEncodingException var19) {
					var19.printStackTrace();
					return;
				}
			}

			if (var3 == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_TIMEZONE_RESP) {
			 
				return;
			}

			if (var3 == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_ADVANCESETTINGS_RESP) {
			
				int data = Packet.byteArrayToInt_Little(var4, 25*4);
				TimeZone = (int)var4[69];
				Log.v("test","TimeZone = "+TimeZone);
				//setFundata(data);
			}
		}

	}

	public void receiveSessionInfo(Camera var1, int var2) {
	}

	public void resetEventCount() {
		this.mEventCount = 0;
	}

	public void setName(String var1) {
		this.mName = var1;
	}

	public void setPassword(String var1) {
		 
		this.mPwd = var1;
	}

	@Override
	public void receiveCameraCtl(Camera var1, int var2, int var3, byte[] var4) {
		// TODO Auto-generated method stub

	}
}