package com.ubia.IOTC;

import android.util.Log;


public class AVFrame {

   public static final int AUDIO_CHANNEL_MONO = 0;
   public static final int AUDIO_CHANNEL_STERO = 1;
   public static final int AUDIO_DATABITS_16 = 1;
   public static final int AUDIO_DATABITS_8 = 0;
   public static final int AUDIO_SAMPLE_11K = 1;
   public static final int AUDIO_SAMPLE_12K = 2;
   public static final int AUDIO_SAMPLE_16K = 3;
   public static final int AUDIO_SAMPLE_22K = 4;
   public static final int AUDIO_SAMPLE_24K = 5;
   public static final int AUDIO_SAMPLE_32K = 6;
   public static final int AUDIO_SAMPLE_44K = 7;
   public static final int AUDIO_SAMPLE_48K = 8;
   public static final int AUDIO_SAMPLE_8K = 0;
   public static final int FRAMEINFO_SIZE = 24;
   public static final byte FRM_STATE_COMPLETE = 0;
   public static final byte FRM_STATE_INCOMPLETE = 1;
   public static final byte FRM_STATE_LOSED = 2;
   public static final byte FRM_STATE_UNKOWN = -1;
   public static final int IPC_FRAME_FLAG_IFRAME = 1;
   public static final int IPC_FRAME_FLAG_IO = 3;
   public static final int IPC_FRAME_FLAG_MD = 2;
   public static final int IPC_FRAME_FLAG_PBFRAME = 0;
   public static final int MEDIA_CODEC_AUDIO_ADPCM = 139;
   public static final int MEDIA_CODEC_AUDIO_G726 = 143;
   public static final int MEDIA_CODEC_AUDIO_MP3 = 142;
   public static final int MEDIA_CODEC_AUDIO_PCM = 140;
   public static final int MEDIA_CODEC_AUDIO_SPEEX = 141;
   public static final int MEDIA_CODEC_UNKNOWN = 0;
   public static final int MEDIA_CODEC_VIDEO_H263 = 77;
   public static final int MEDIA_CODEC_VIDEO_H264 = 78;
   public static final int MEDIA_CODEC_VIDEO_MJPEG = 79;
   public static final int MEDIA_CODEC_VIDEO_MPEG4 = 76;
   private short codec_id = 0;
   private byte flags = -1;
   public byte[] frmData = null;
   private long frmNo = -1L;
   private int frmSize = 0;
   private byte frmState = 0;
   private byte onlineNum = 0;
   private byte recordstatus = 0;
   private int timestamp = 0;
   private int videoHeight = 0;
   private int videoWidth = 0;
   private int tempture =0;
   private byte varbit = 0;
   private byte   currentplaySeq=0;
   private int cam_index = 0;
   private int framerate=15;
   private int resolution = -1;
/**
   Audio/Video Frame Header Info  
typedef struct _FRAMEINFO
{
	unsigned short codec_id;	// Media codec type defined in sys_mmdef.h,
								// MEDIA_CODEC_AUDIO_PCMLE16 for audio,
								// MEDIA_CODEC_VIDEO_H264 for video.
	unsigned char flags;		// Combined with IPC_FRAME_xxx.
	unsigned char cam_index;	// 0 - n

	unsigned char onlineNum;	// number of client connected this device
	unsigned char recordstatus; // 0x01:全天（手动）录像, 0x02: 移动侦测录像，0x04: 告警录像，0x08:PIR
    short  temperature;         // 实际值为摄氏温度，如华氏温度需要设备端转换，为1/100摄氏度
    unsigned char validbit;     //bitmap: 0x1 支持温度，0x02:录像状态有效，0x04时区有效 其他预留 0x10 翻转
    char nGMTDiff;	//
    unsigned char reserve2[2];	//
	unsigned int timestamp;     // Timestamp of the frame, in milliseconds

    // unsigned int videoWidth;
    // unsigned int videoHeight;
    
}FRAMEINFO_t;*/
   
   public int getCam_index() {
	return (cam_index&0xff);
}
   public void setCam_index(int cam_index) {
		this.cam_index = cam_index;
	}
   
   
   public AVFrame(long var1, byte var3, byte[] var4, byte[] var5, int var6) {
      this.codec_id = Packet.byteArrayToShort_Little(var4, 0);
      this.frmState = var3;
      this.flags = var4[2];
      this.onlineNum = var4[4];
      this.recordstatus = var4[5];
      this.varbit =var4[8];
      // Log.e("","validbit:"+(int)(varbit&0xff));
      cam_index= var4[3];
      this.  currentplaySeq= var4[9];
      this.timestamp = Packet.byteArrayToInt_Little(var4, 12);
      this.tempture = Packet.byteArrayToShort_Little(var4, 6);
      // Log.e("","validbit:"+(int)(varbit&0xff)+"   tempture："+tempture);
      resolution = var4[10];
      int var7;
      if(var4.length > 16) {
         var7 = Packet.byteArrayToInt_Little(var4, 16);
      } else {
         var7 = 0;
      }
      this.framerate = var4[11];
      this.videoWidth = var7;
      int var8 = var4.length;
      int var9 = 0;
      if(var8 > 16) {
         var9 = Packet.byteArrayToInt_Little(var4, 20);
      }

      this.videoHeight = var9;
      this.frmSize = var6;
      this.frmData = var5;
      this.frmNo = var1;
   }
   public int getFramerate() {
	   if(framerate==0){
		   framerate=15;
	   }
	return framerate;
}
public void setFramerate(int framerate) {
	this.framerate = framerate;
   }
   public int getCurrentplaySeq() {
	return currentplaySeq & 0xff;
}
public void setCurrentplaySeq(byte currentplaySeq) {
	this.currentplaySeq = currentplaySeq;
   }

   public int getTempture() {
	   if(1 == (varbit&0x1))
		   return tempture/100;
	   else if((varbit&0x8)==8 || (varbit&0x80)==0x80)
	    return tempture;//回放毫秒
		   else
		   return -250;
   }
   public int getplaybackTimems() {
	   return tempture ;
   }
   public boolean  getinfoValidBit() {
	  // unsigned char validbit;     //bitmap: 0x1 支持温度，0x02:录像状态有效，0x04时区有效 其他预留
	   if(0 != (varbit&0x2))
		   return true;
	   else
		   return false;
	  
   }

public void setTempture(int tempture) {
	this.tempture = tempture;
}

public byte getVarbit() {
	return varbit;
}
public byte getChange_clip_flag() {
	return (byte) (varbit>>>4&7);
}

public void setVarbit(byte varbit) {
	this.varbit = varbit;
}

public static int getSamplerate(byte var0) {
      switch(var0 >>> 2) {
      case 0:
         return 8000;
      case 1:
         return 11025;
      case 2:
         return 12000;
      case 3:
         return 16000;
      case 4:
         return 22050;
      case 5:
         return 24000;
      case 6:
         return 32000;
      case 7:
         return 44100;
      case 8:
         return 48000;
      default:
         return 8000;
      }

   }

   public short getCodecId() {
      return this.codec_id;
   }

   public byte getFlags() {
      return this.flags;
   }

   public long getFrmNo() {
      return this.frmNo;
   }

   public int getFrmSize() {
      return this.frmSize;
   }

   public byte getFrmState() {
      return this.frmState;
   }

   public byte getOnlineNum() {
      return this.onlineNum;
   }

   public byte getRecordstatus() {
	  return this.recordstatus;
   }
   
   public int getTimeStamp() {
      return this.timestamp;
   }
   public int getTimeStampSec(){
	  
	   if((varbit&0x8)==8)
		   return  this.timestamp;//回放 
	   else 
		   return  this.timestamp/1000;//直播
   }
   public int getVideoHeight() {
      return this.videoHeight;
   }

   public int getVideoWidth() {
      return this.videoWidth;
   }

   public boolean isIFrame() {
      return (1 & this.flags) == 1;
   }
   public int getResolution() {
	return resolution;
   }
}
