package com.mp4;

import java.io.File;
import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import cn.ubia.interfaceManager.LiveViewTimeStateCallbackInterface_Manager;

import com.ubia.IOTC.AVFrame;
import com.ubia.IOTC.Camera.VideoInfo;

public class RecordHelper {
	private Mp4Writer mMp4Writer = null;
	private H264HeaderParser mH264Parser = null;
	private int mStreamWidth = 0;
	private int mStreamHeight = 0;
	private int mStreamFrameRate = 0;
	private String recordfilePath;
	private long lastVideoTimestamp = 0;
	private long writeAudioTime = 0;//已经写入的视频时间
    public RecordAudioQueue audioFrameQueue;
	public String getRecordfilePath() {
		return recordfilePath;
	}
	public void setRecordfilePath(String recordfilePath) {
		this.recordfilePath = recordfilePath;
	}
	private LinkedList<AVFrame> mMp4Buffer;
	private String filePath;
	
	
	private boolean startSameBitValue = false;
	private int valbit = 0;
	
	
	private boolean startIsIframe = false;
	public boolean isSavingVideo() {
		if (null == mMp4Writer)
			return false;
		boolean isOpen = mMp4Writer.IsOpen();
		return isOpen;
	}

	public void startRecord(String filePath, VideoInfo videoInfo) {
		this.filePath = filePath;
		if (null == mMp4Writer)
			mMp4Writer = new Mp4Writer();

		if (null == mH264Parser)
			mH264Parser = new H264HeaderParser();

		if(audioFrameQueue==null){
			audioFrameQueue =   new RecordAudioQueue();
		}
		audioFrameQueue.removeAll();
		mH264Parser.Clear();

		recordfilePath = filePath;

		int suc = mMp4Writer.Create(recordfilePath);
		mStreamFrameRate = videoInfo.fps;
		mStreamWidth = videoInfo.videoWidth;
		mStreamHeight = videoInfo.videoHeight;
		lastVideoTimestamp = 0;
		if (suc == Mp4ErrorCode.MP4_S_OK) {
			int duration = 1000 / 10;
			if (0 != mStreamFrameRate)
				duration = 1000 / mStreamFrameRate;
			if (0 == mStreamWidth || 0 == mStreamHeight) {
				mStreamWidth = 640;// 352;
				mStreamHeight = 360;// 288;
			}
			mMp4Writer.AddVideoTrack(1000, duration, (short) mStreamWidth,
					(short) mStreamHeight, (byte) 0);
// 			mMp4Writer.AddAmrTrack(1000, 50, (byte) 1, false);
			mMp4Writer.AddAudioTrack(1000, 16000, 64, (byte) 0);
			mMp4Writer.BeginWrite();

			if (mMp4Buffer != null) {
				mMp4Buffer.clear();
				int size = mMp4Buffer.size();
				for (int i = 0; i < size; i++) {
					AVFrame frame = mMp4Buffer.get(i);
					saveVideoData(frame);
				}

			}
		}
	}

	public void updateMp4Buffer(AVFrame frame) {
		if (null == mMp4Buffer)
			mMp4Buffer = new LinkedList<AVFrame>();

		if (frame.isIFrame()) {
			mMp4Buffer.clear();
		}

		mMp4Buffer.add(frame);
	}

	public void saveAvFrame(final AVFrame frame) {
		updateMp4Buffer(frame);
		if (isSavingVideo()) {
			if(frame.isIFrame() && frame.frmData!=null && !startIsIframe){
				saveVideoData(frame);//开始多写个i帧
				writeAudioTime = ((long)frame.getTimeStampSec())*1000; //视频开始记录的时间

			}
			if(startIsIframe){
				saveVideoData(frame);
				AVFrame audioframe =audioFrameQueue.removeHead();
				if(audioframe==null) return;
			 
				//Log.e("","frame.getTimeStamp():"+frame.getTimeStamp()+"  writeAudioTime:"+writeAudioTime+"    audioframe.frmData"+audioframe.frmData.length+"   audioframe.getFrmSize():"+audioframe.getFrmSize());
			 
				while(writeAudioTime<=((long)frame.getTimeStampSec())*1000)//写入小于当前时间的所有音频数据
				{
					writeAudioTime +=64;
					mMp4Writer.WriteAudioSampe(audioframe.frmData, audioframe.frmData.length,64,  1000);
					audioframe =audioFrameQueue.removeHead();
					if(audioframe==null) return;	
				}
		
			}
		}
	}
	public void saveAudioFrame(AVFrame avFrame) {
		//Log.e("","saveAudioFrame"+avFrame.getTimeStamp());
		if(audioFrameQueue!=null	&& startIsIframe){
			audioFrameQueue.addLast(avFrame);
		}
	}
//	public void saveAudioFrame(byte[] frameBuf,int frameLen,long timeStamp,long time) {
//		//updateMp4Buffer(frame);
////		if (isSavingVideo() && startIsIframe) 
//		{
//			//saveVideoData(frame);
//		 	Log.e("WriteAudioSampe", "   WriteAudioSampe frameLen= " + frameLen+"   timeStamp= " + timeStamp +"   time= " + time);
//			mMp4Writer.WriteAudioSampe(frameBuf, frameLen,
//					timeStamp,  1000);
//		}
//	}
	public void stopRecord() {
		startIsIframe = false;
		startSameBitValue = false;
		valbit = 0;
		writeAudioTime = 0;
		if (null != mMp4Writer) {
			mMp4Writer.FinishWrite();
			mMp4Writer.Close();
			mMp4Writer=null;
		}
		if (null != mH264Parser)
			{
				mH264Parser.Clear();
				mH264Parser=null;
			}
		if (null != mMp4Buffer)
			{
				mMp4Buffer.clear();
				mMp4Buffer=null;
			}
		if (filePath != null && mContext != null) {
			Log.i("video", "Scan video = " + filePath);
			Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			scanIntent.setData(Uri.fromFile(new File(filePath)));
			LocalBroadcastManager.getInstance(mContext).sendBroadcast(scanIntent);
		}
	}
	 
	private Context mContext;
	public void setContext(Context mContext){
		this.mContext = mContext;
	}

	private void saveVideoData(AVFrame frame) {
		int beginPosition = 0;
		int frameLen = 0;
		if(frame.frmData==null){
			startIsIframe = false;
			return;
		}
		byte[] frameBuf = frame.frmData;
		int length = frameBuf.length;
		boolean isIFrame = frame.isIFrame();
		//LogHelper.d("frame.frmData="+frame.frmData +  "frame = "+ frame );

		if( isIFrame)
			startIsIframe = isIFrame;
 
		if(!startIsIframe){
			return;
		}
		// long long timeStamp = frame.m_lFrameTimeStamp/1000;
		long timeduration = 0;
		if(lastVideoTimestamp!=0 && startSameBitValue){
			timeduration = frame.getTimeStamp()-lastVideoTimestamp; 
		} 
	
		
		if(timeduration<=0 ){
			if(mStreamFrameRate==0){
				mStreamFrameRate =15;
			}
			timeduration = (long) (1000.0f/mStreamFrameRate);
		}
		if(timeduration<(long) (1000.0f/mStreamFrameRate)){
			timeduration =(long) (1000.0f/mStreamFrameRate);
		}
 	 
		lastVideoTimestamp =frame.getTimeStamp();  
		if(valbit == 0 && !startSameBitValue )//playback first frame
		{
			startSameBitValue = true; 
			timeduration = (long) (1000.0f/mStreamFrameRate);
			lastVideoTimestamp =frame.getTimeStamp();  
		}
		
		 if(valbit!=frame.getChange_clip_flag() &&  startSameBitValue )
		{
			startSameBitValue = true;
			valbit = frame.getChange_clip_flag();
			timeduration = (long) (1000.0f/mStreamFrameRate);//timesnap set default value
			lastVideoTimestamp =frame.getTimeStamp(); 
		
		} 
	 
		 LiveViewTimeStateCallbackInterface_Manager.getInstance().saveTimeMsSeccallback(timeduration);//save rec time

			Log.e("","beginPosition:"+  "   lastVideoTimestamp:"+lastVideoTimestamp+"  timeStamp:"+frame.getTimeStamp()+"   frameBuf.timeduration:"+timeduration);
		 
		for (int i = 4; i < length - 4 - 4; i++) {
			boolean isNalBeginShort = false;
			boolean isNalBegin = false;
			isNalBeginShort = ((frameBuf[i] == 0x00)
					&& (frameBuf[i + 1] == 0x00) && (frameBuf[i + 2] == 0x01));
			isNalBegin = ((frameBuf[i] == 0x00) && (frameBuf[i + 1] == 0x00)
					&& (frameBuf[i + 2] == 0x00) && (frameBuf[i + 3] == 0x01));
			if (isNalBeginShort || isNalBegin)
			// if ((frameBuf[i] == 0x00) && (frameBuf[i+1] == 0x00) &&
			// (frameBuf[i+2] == 0x00) && (frameBuf[i+3] == 0x01))
			{
				// this is a start of h264 frame
				frameLen = i - beginPosition;
			 
				
				mMp4Writer.WriteVideoSample(frameBuf, beginPosition, frameLen,
						timeduration, isIFrame, false, 1000);
				beginPosition = i;
				i += isNalBeginShort ? 3 : 4;
			}
		}

		if (beginPosition < length) {
			 

			mMp4Writer.WriteVideoSample(frameBuf, beginPosition, length
					- beginPosition, timeduration, isIFrame, true, 1000);
		}
	}
	class RecordAudioQueue {
		public volatile LinkedList<AVFrame> listData = new LinkedList<AVFrame>();
		private volatile int mSize = 0;
 
		public synchronized void addLast(AVFrame avFrame) {

			boolean isFull = false;
			if (this.mSize > 1500) {
				isFull = true;
			}
			//Log.i("cv", listData.size()+"==========="+mSize);
			while (isFull) {
				AVFrame localAVFrame = (AVFrame) this.listData.get(0);
				if (localAVFrame.isIFrame()) {
					System.out.println("drop I frame");
					this.listData.removeFirst();
					this.mSize = (-1 + this.mSize);
				} else {
					System.out.println("drop p frame");
					this.listData.removeFirst();
					this.mSize = (-1 + this.mSize);
					isFull = false;
				}
				if (this.mSize == 0) {
					break;
				}
			}

			this.listData.addLast(avFrame);
			this.mSize = (1 + this.mSize);

		}

		public synchronized void addFirst(AVFrame avFrame) {

			 

			this.listData.addFirst(avFrame);
			this.mSize = (1 + this.mSize);

		}
		
		public int getCount() {
			try {
				int i = this.mSize;
				return i;
			} finally {
				// localObject = finally;
				// throw localObject;
			}
		}

		public boolean isFirstIFrame() {
			try {
				boolean bool1 = false;
				if ((this.listData != null) && (!this.listData.isEmpty())) {
					boolean bool2 = ((AVFrame) this.listData.get(0)).isIFrame();
					if (bool2) {
						bool1 = true;
						return bool1;
					}
				}

			} finally {
			}
			return false;
		}

		public void removeAll() {
			try {
				if (!this.listData.isEmpty())
					this.listData.clear();
				this.mSize = 0;
				return;
			} finally {
			}
		}
	//poll
		public synchronized AVFrame removeHead() {
			AVFrame localAVFrame;
			try {
				int i = this.mSize;

				if (i == 0)
					localAVFrame = null;
				else {
					if (true) {
						localAVFrame = (AVFrame) this.listData.removeFirst();
						this.mSize = (-1 + this.mSize);
					}
				}
			}catch(Exception e){
				return null;
			} finally {
			}
			return localAVFrame;
		}
	}

}
