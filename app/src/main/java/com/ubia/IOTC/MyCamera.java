package com.ubia.IOTC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import android.graphics.Bitmap;
import android.util.Log;

import com.ubia.IOTC.AVIOCTRLDEFs;
import com.ubia.IOTC.Camera;
import com.ubia.IOTC.IRegisterIOTCListener;
import com.ubia.IOTC.Packet;

public class MyCamera extends Camera  {
	public int LastAudioMode;
	private boolean bIsIOAlarm;
	private boolean bIsMotionDetected;
	private int cbSize = 0;
	private String mAcc;
	private int mEventCount = 0;
	private String mName;
	private String mPwd;
	private List<AVIOCTRLDEFs.SStreamDef> mStreamDefs = Collections
			.synchronizedList(new ArrayList());
	private String mUID;
	private UUID mUUID = UUID.randomUUID();
	private int nGMTDiff = 0;
	private int nIsSupportTimeZone = 0;
	private byte[] szTimeZoneString = new byte[256];
	private int mSelectChannel;
	public static final String TAG = "MyCamera";
	public MyCamera(String var1, String var2, String var3, String var4) {
		this.mName = var1;
		this.mUID = var2;
		this.mAcc = var3;
		this.mPwd = var4;
	}
	public MyCamera(String mName, String mUID, String mAcc, String mPwd,
			int selectChannel) {
		this.mName = mName;
		this.mUID = mUID;
		this.mAcc = mAcc;
		this.mPwd = mPwd;
		this.mSelectChannel = selectChannel;
	}

	public void connect(String uid) {
		super.connect(uid);
		this.mUID = uid;
	}

	public void disconnect() {
		super.disconnect();
		this.mStreamDefs.clear();
	}

	public boolean getAudioInSupported(int paramInt) {
		return (1L & getChannelServiceType(paramInt)) == 0L;
	}

	public int getAudioOutEncodingFormat(int paramInt) {
		if ((0x1000 & getChannelServiceType(paramInt)) == 0L)
			return 141;
		return 139;
	}

	public boolean getAudioOutSupported(int paramInt) {
		return (0x2 & getChannelServiceType(paramInt)) == 0L;
	}

	public boolean getDeviceInfoSupport(int paramInt) {
		return (0x4000 & getChannelServiceType(paramInt)) == 0L;
	}

	public boolean getEnvironmentModeSupported(int paramInt) {
		return (0x400 & getChannelServiceType(paramInt)) == 0L;
	}

	public int getEventCount() {
		return this.mEventCount;
	}

	public boolean getEventListSupported(int paramInt) {
		return (0x8 & getChannelServiceType(paramInt)) == 0L;
	}

	public boolean getEventSettingSupported(int paramInt) {
		return (0x40 & getChannelServiceType(paramInt)) == 0L;
	}

	public int getGMTDiff() {
		return this.nGMTDiff;
	}

	public int getIsSupportTimeZone() {
		return this.nIsSupportTimeZone;
	}

	public boolean getMultiStreamSupported(int paramInt) {
		return (0x800 & getChannelServiceType(paramInt)) == 0L;
	}

	public String getName() {
		return this.mName;
	}

	public boolean getPanTiltSupported(int paramInt) {
		return (0x4 & getChannelServiceType(paramInt)) == 0L;
	}

	public String getAccount() {
		return this.mAcc;
	}

	public String getPassword() {
		return this.mPwd;
	}

	public boolean getPlaybackSupported(int paramInt) {
		return (0x10 & getChannelServiceType(paramInt)) == 0L;
	}

	public boolean getRecordSettingSupported(int paramInt) {
		return (0x80 & getChannelServiceType(paramInt)) == 0L;
	}

	public boolean getSDCardFormatSupported(int paramInt) {
		return (0x100 & getChannelServiceType(paramInt)) == 0L;
	}

	public AVIOCTRLDEFs.SStreamDef[] getSupportedStream() {
		Log.i("bbb", "this.mStreamDefs.size()" + this.mStreamDefs.size());
		AVIOCTRLDEFs.SStreamDef[] arrayOfSStreamDef = new AVIOCTRLDEFs.SStreamDef[this.mStreamDefs
				.size()];
		for (int i = 0; i < arrayOfSStreamDef.length; i++) {
			// if (i >= arrayOfSStreamDef.length)
			// return arrayOfSStreamDef;
			arrayOfSStreamDef[i] = ((AVIOCTRLDEFs.SStreamDef) this.mStreamDefs
					.get(i));
		}
		return arrayOfSStreamDef;
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

	public boolean getVideoFlipSupported(int paramInt) {
		return (0x200 & getChannelServiceType(paramInt)) == 0L;
	}

	public boolean getVideoQualitySettingSupport(int paramInt) {
		return (0x2000 & getChannelServiceType(paramInt)) == 0L;
	}

	public boolean getWiFiSettingSupported(int paramInt) {
		return (0x20 & getChannelServiceType(paramInt)) == 0L;
	}

//	public void receiveChannelInfo(Camera paramCamera, int paramInt1,
//			int paramInt2) {
//	}
//
//	public void receiveFrameData(Camera paramCamera, int paramInt,
//			Bitmap paramBitmap, long time) {
//	}
//
//	public void receiveFrameInfo(Camera paramCamera, int paramInt1,
//			long paramLong, int paramInt2, int paramInt3, int paramInt4,
//			int paramInt5) {
//	}

//	public void receiveIOCtrlData(Camera paramCamera, int paramInt1,
//			int paramInt2, byte[] paramArrayOfByte) {
//
//		int k;
//		if (paramInt2 == 809)// 支持流类型 02 00 00 00, 00 00 00 00 00 00 00 00, 01
//								// 00 01 00 00 00 00 00
//		{
//			Log.i(TAG, "支持流类型...................");
//			this.mStreamDefs.clear();
//			int j = Packet.byteArrayToInt_Little(paramArrayOfByte, 0);
//			if ((paramInt1 == 0) && (getMultiStreamSupported(0))) {
//				k = 0;
//				do {
//					if (k < j) {
//						byte[] arrayOfByte7 = new byte[8];
//						System.arraycopy(paramArrayOfByte, 4 + k * 8,
//								arrayOfByte7, 0, 8);
//						AVIOCTRLDEFs.SStreamDef localSStreamDef = new AVIOCTRLDEFs.SStreamDef(
//								arrayOfByte7);
//						this.mStreamDefs.add(localSStreamDef);
//					}
//					k++;
//				} while (k < j);
//
//			}
//		}

		/*
		 * int k; if (paramInt2 == 809)//支持流类型 { this.mStreamDefs.clear(); int j
		 * = Packet.byteArrayToInt_Little(paramArrayOfByte, 0); if ((paramInt1
		 * == 0) && (getMultiStreamSupported(0))) { k = 0; if (k < j) break
		 * label47; } } label47: label215: do { int i; do { return; byte[]
		 * arrayOfByte7 = new byte[8]; System.arraycopy(paramArrayOfByte, 4 + k
		 * * 8, arrayOfByte7, 0, 8); AVIOCTRLDEFs.SStreamDef localSStreamDef =
		 * new AVIOCTRLDEFs.SStreamDef(arrayOfByte7);
		 * this.mStreamDefs.add(localSStreamDef);
		 * paramCamera.start(localSStreamDef.channel, this.mAcc, this.mPwd);
		 * k++; break; if (paramInt2 != 8191)//device event report msg break
		 * label215; i = Packet.byteArrayToInt_Little(paramArrayOfByte, 12); if
		 * (i == 1) { if (!this.bIsMotionDetected) this.mEventCount = (1 +
		 * this.mEventCount); this.bIsMotionDetected = true; return; } if (i ==
		 * 4) { this.bIsMotionDetected = false; return; } if (i == 3) { if
		 * (!this.bIsIOAlarm) this.mEventCount = (1 + this.mEventCount);
		 * this.bIsIOAlarm = true; return; } } while (i != 6); this.bIsIOAlarm =
		 * false; return; if (paramInt2 == 929) { byte[] arrayOfByte4 = new
		 * byte[4]; byte[] arrayOfByte5 = new byte[4]; byte[] arrayOfByte6 = new
		 * byte[4]; System.arraycopy(paramArrayOfByte, 0, arrayOfByte4, 0, 4);
		 * this.cbSize = Packet.byteArrayToInt_Little(arrayOfByte4);
		 * System.arraycopy(paramArrayOfByte, 4, arrayOfByte5, 0, 4);
		 * this.nIsSupportTimeZone = Packet.byteArrayToInt_Little(arrayOfByte5);
		 * System.arraycopy(paramArrayOfByte, 8, arrayOfByte6, 0, 4);
		 * this.nGMTDiff = Packet.byteArrayToInt_Little(arrayOfByte6);
		 * System.arraycopy(paramArrayOfByte, 12, this.szTimeZoneString, 0,
		 * 256); try { Log.i("szTimeZoneString", new
		 * String(this.szTimeZoneString, 0, this.szTimeZoneString.length,
		 * "utf-8")); Log.i("szTimeZoneString", new
		 * String(this.szTimeZoneString, 0, this.szTimeZoneString.length,
		 * "utf-8")); return; } catch (UnsupportedEncodingException
		 * localUnsupportedEncodingException) {
		 * localUnsupportedEncodingException.printStackTrace(); return; } } }
		 * while (paramInt2 != 945); byte[] arrayOfByte1 = new byte[4]; byte[]
		 * arrayOfByte2 = new byte[4]; byte[] arrayOfByte3 = new byte[4];
		 * System.arraycopy(paramArrayOfByte, 0, arrayOfByte1, 0, 4);
		 * this.cbSize = Packet.byteArrayToInt_Little(arrayOfByte1);
		 * System.arraycopy(paramArrayOfByte, 4, arrayOfByte2, 0, 4);
		 * this.nIsSupportTimeZone = Packet.byteArrayToInt_Little(arrayOfByte2);
		 * System.arraycopy(paramArrayOfByte, 8, arrayOfByte3, 0, 4);
		 * this.nGMTDiff = Packet.byteArrayToInt_Little(arrayOfByte3);
		 * System.arraycopy(paramArrayOfByte, 12, this.szTimeZoneString, 0,
		 * 256);
		 */
//	}
//
//	public void receiveSessionInfo(Camera paramCamera, int paramInt) {
//	}

	public void resetEventCount() {
		this.mEventCount = 0;
	}

	public void setName(String paramString) {
		this.mName = paramString;
	}

	public void setPassword(String paramString) {
		this.mPwd = paramString;
	}

	public int getSelectChannel() {
		return mSelectChannel;
	}

}
