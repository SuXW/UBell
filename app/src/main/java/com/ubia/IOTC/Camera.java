// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Camera.java

package com.ubia.IOTC;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import HeavenTao.Audio.AudioProcessThread;
import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import cn.ubia.UBell.BuildConfig;
import cn.ubia.UbiaApplication;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.ZigbeeInfo;
import cn.ubia.db.DatabaseManager;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.interfaceManager.DeviceStateCallbackInterface_Manager;
import cn.ubia.interfaceManager.DoorStateCallbackInterface_Manager;
import cn.ubia.interfaceManager.IOTCCMDStateCallbackInterface_Manager;
import cn.ubia.interfaceManager.LiveViewTimeStateCallbackInterface_Manager;
import cn.ubia.interfaceManager.ZigbeeInfoCallbackInterface_Manager;

import com.decoder.util.DecADPCM;
import com.decoder.util.DecG726;
import com.decoder.util.DecMp3;
import com.decoder.util.DecSpeex;
import com.encoder.util.EncG726;
import com.hikvh.media.amr.AmrDecoder;
import com.mp4.RecordHelper;
import com.my.IOTC.UBICAPIs;
import com.my.IOTC.UBICAVAPIs;

import com.timeline.listenview.GetTimelineBitMapCallback_Manager;
import com.timeline.listenview.NoteInfoData;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgFileTransPktNak;
import com.ubia.vr.GLView;
import com.ubia.vr.SurfaceDecoder;
import com.ubia.vr.Utils;
import com.ubia.vr.VRConfig;
//import com.mp4.RecordHelper;
// Referenced classes of package com.tutk.IOTC:
//            IOTCAPIs, AVAPIs, AVFrameQueue, IRegisterIOTCListener, 
//            st_LanSearchInfo, AVFrame, Packet, AVIOCTRLDEFs, 
//            St_SInfo

public class Camera
{
    private final int audio_sample_size = 480; //defult 480
    private final int breakJoinTime = 3000;
    public boolean mutePlayout = false;
    public int hardware_pkg = -1;
    private static boolean isInit = false;
    private  int   RECONNECT_COUNT = 2;
    public int 	  currentplaySeq;
    boolean controlCountReady = false;
    public  boolean isConected = false;
    private int tempture;
    public static boolean ADDACCTRACK = true;
    public boolean isControlCountReady() {
        return controlCountReady;
    }
    public void setControlCountReady(boolean controlCountReady) {
        this.controlCountReady = controlCountReady;
    }
    public void setStartRecode(boolean startRecode) {
        StartRecode = startRecode;
    }
    RecordHelper recodeHelper = new RecordHelper();



    public RecordHelper getRecodeHelper() {
        return recodeHelper;
    }

    public void setRecodeHelper(RecordHelper recodeHelper) {
        this.recodeHelper = recodeHelper;
    }

    byte[] pcmBuffer = new byte[2048*10];

    int pcmBufferReadPostion =0;
    int pcmBufferWritePostion =0;

    boolean StartTalk = false;
    boolean StartRecode = false;
    boolean  StartisIFrame = false;
    private int DownLoadfilesize = 0;
    private int DownLoadfileblock = 0;

    Handler handler;
    public boolean IsDownLoading;
    public int IsDownLoadstatus = -1;
    private byte recordstatus = 0;

    public void setRecordstatus(byte recordstatus) {
        this.recordstatus = recordstatus;
    }
    private class AVChannel
    {

        public int getChannel()
        {
            return mChannel;
        }

        public synchronized int getAVIndex()
        {
            return mAVIndex;
        }

        public synchronized void setAVIndex(int idx)
        {
            mAVIndex = idx;
        }

        public synchronized long getServiceType()
        {
            return mServiceType;
        }

        public synchronized int getAudioCodec()
        {
            return mAudioCodec;
        }

        public synchronized void setAudioCodec(int codec)
        {
            mAudioCodec = codec;
        }

        public synchronized void setServiceType(long serviceType)
        {
            mServiceType = serviceType;
            mAudioCodec = (serviceType & 4096L) != 0L ? 139 : 141;
        }

        public String getViewAcc()
        {
            return mViewAcc;
        }

        public String getViewPwd()
        {
            return mViewPwd;
        }

        private volatile int mChannel;
        private volatile int mAVIndex;
        private long mServiceType;
        private String mViewAcc;
        private String mViewPwd;
        private int mAudioCodec;
        public IOCtrlQueue IOCtrlQueue;
        public AVFrameQueue VideoFrameQueue;
        public AVFrameQueue AudioFrameQueue;
        public Bitmap LastFrame;
        public int VideoFPS;
        public int VideoBPS;
        public int AudioBPS;
        public int flowInfoInterval;
        public ThreadStartDev threadStartDev;
        public ThreadRecvIOCtrl threadRecvIOCtrl;
        public ThreadSendIOCtrl threadSendIOCtrl;
        public ThreadRecvVideo2 threadRecvVideo;
        public ThreadRecvAudio threadRecvAudio;
        public ThreadDecodeVideo2 threadDecVideo;
        public ThreadDecodeAudio threadDecAudio;
        public  ThreadWriteFile threadWriteFile = null;


        public AVChannel(int channel, String view_acc, String view_pwd)
        {
            this.mChannel = -1;
            mAVIndex = -1;
            mServiceType = -1L;
            threadStartDev = null;
            threadRecvIOCtrl = null;
            threadSendIOCtrl = null;
            threadRecvVideo = null;
            threadRecvAudio = null;
            threadDecVideo = null;
            threadDecAudio = null;
            mChannel = channel;
            mViewAcc = view_acc;
            mViewPwd = view_pwd;
            mServiceType = -1L;
            VideoFPS = VideoBPS = AudioBPS = flowInfoInterval = 0;
            LastFrame = null;
            IOCtrlQueue = new IOCtrlQueue(null);
            VideoFrameQueue = new AVFrameQueue();
            AudioFrameQueue = new AVFrameQueue();
        }
    }

    private class IOCtrlQueue
    {
        public class IOCtrlSet
        {

            public int IOCtrlType;
            public byte IOCtrlBuf[];


            public IOCtrlSet(int avIndex, int type, byte buf[])
            {
                IOCtrlType = type;
                IOCtrlBuf = buf;
            }

            public IOCtrlSet(int type, byte buf[])
            {
                IOCtrlType = type;
                IOCtrlBuf = buf;
            }
        }


        public synchronized boolean isEmpty()
        {
            return listData.isEmpty();
        }

        public synchronized void Enqueue(int type, byte data[])
        {
            listData.addLast(new IOCtrlSet(type, data));
        }

        public synchronized void Enqueue(int avIndex, int type, byte data[])
        {
            listData.addLast(new IOCtrlSet(avIndex, type, data));
        }

        public synchronized IOCtrlSet Dequeue()
        {
            return listData.isEmpty() ? null : (IOCtrlSet)listData.removeFirst();
        }

        public synchronized void removeAll()
        {
            if(!listData.isEmpty())
                listData.clear();
        }

        LinkedList listData;


        private IOCtrlQueue()
        {
            listData = new LinkedList();
        }

        IOCtrlQueue(IOCtrlQueue ioctrlqueue)
        {
            this();
        }
    }

    private class ThreadCheckDevStatus extends Thread
    {

        public void stopThread()
        {
            m_bIsRunning = false;
            synchronized(m_waitObjForCheckDevStatus)
            {
                m_waitObjForCheckDevStatus.notify();
            }
        }

        public void run()
        {
            super.run();
            m_bIsRunning = true;
            St_SInfo stSInfo = new St_SInfo();
            int ret = 0;
            int reconnect  = 0;
            while(m_bIsRunning && mSID < 0  && !Thread.interrupted())
                try
                {
                    synchronized(mWaitObjectForConnected)
                    {
                        mWaitObjectForConnected.wait(2000L);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            while(m_bIsRunning  && !Thread.interrupted())
            {
                if(mSID >= 0)
                {
                    ret = ubia_UBICAPIs.IOTC_Session_Check(mSID, stSInfo);
                    reconnect++;
                    if(ret >= 0)
                    {
                        isConected = true;
                        if(mSessionMode != stSInfo.Mode)
                            mSessionMode = stSInfo.Mode;
                        reconnect = 0;
                    } else
//                    if(ret == -23 || ret == -13)
//                    {
//                        isConected = false;
//                        Log.i("IOTCamera", (new StringBuilder("IOTC_Session_Check(")).append(mSID).append(") timeout").toString());
////                        for(int i = 0; i < mIOTCListeners.size(); i++)
////                        {
////                            IRegisterIOTCListener listener = (IRegisterIOTCListener)mIOTCListeners.get(i);
////                            listener.receiveSessionInfo(Camera.this, 6);
////                        }
//                        
//                      
//                    } else
                    {
                        isConected = false;
                        Log.i("IOTCamera", (new StringBuilder("IOTC_Session_Check(")).append(mSID).append(") Failed return ").append(ret).toString());
//                        for(int i = 0; i < mIOTCListeners.size(); i++)
//                        {
//                            IRegisterIOTCListener listener = (IRegisterIOTCListener)mIOTCListeners.get(i);
//                            listener.receiveSessionInfo(Camera.this, 8);
//                        }
//                        DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mDevUID, 0, MainCameraFragment.CONNSTATUS_CONNECTION_FAILED);
                        if(reconnect<RECONNECT_COUNT)
                            DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mDevUID, 0, MainCameraFragment.CONNSTATUS_RECONNECTION);
                        else
                            DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mDevUID, 0, MainCameraFragment.CONNSTATUS_CONNECTION_FAILED);
                    }
                }
                synchronized(m_waitObjForCheckDevStatus)
                {
                    try
                    {
                        m_waitObjForCheckDevStatus.wait(2000L);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            Log.i("IOTCamera", "===ThreadCheckDevStatus exit===");
        }

        private boolean m_bIsRunning;
        private Object m_waitObjForCheckDevStatus;


        private ThreadCheckDevStatus()
        {
            m_bIsRunning = false;
            m_waitObjForCheckDevStatus = new Object();
        }

        ThreadCheckDevStatus(ThreadCheckDevStatus threadcheckdevstatus)
        {
            this();
        }
    }
    IOTCAPIs iotcubia = new IOTCAPIs();
    private class ThreadConnectDev extends Thread
    {

        public void stopThread()
        {
            mIsRunning = false;
            if (nGet_SID >= 0)
                ubia_UBICAPIs.IOTC_Connect_Stop_BySID(nGet_SID);
            synchronized(m_waitForStopConnectThread)
            {
                m_waitForStopConnectThread.notify();
            }
        }

        public void run()
        {
            int nRetryForIOTC_Conn = 0;
            mIsRunning = true;
            isConected = false;
            while(mIsRunning && mSID < 0  && !Thread.interrupted())
            {
//                for(int i = 0; i < mIOTCListeners.size(); i++)
//                {
//                    IRegisterIOTCListener listener = (IRegisterIOTCListener)mIOTCListeners.get(i);
//                    listener.receiveSessionInfo(Camera.this, 1);
//                }
                DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mDevUID, 0, MainCameraFragment.CONNSTATUS_CONNECTING);

                int ver = UBICAPIs.UBIC_GetDevLibVer(mDevUID);
                // ubia_UBICAPIs.setP4plibver(ver);
                ubia_UBICAPIs.p4plibver = ver;
                ubia_UBICAVAPIs.p4plibver = ver;
				/*Log.i("getver", "######p4plibver [" + ubia_UBICAPIs.p4plibver
						+ "]");*/
                nGet_SID = ubia_UBICAPIs.IOTC_Get_SessionID();

                if (nGet_SID >= 0) {
                    mSID = ubia_UBICAPIs.IOTC_Connect_ByUID_Parallel2(mDevUID, Camera.this.nGet_SID,isCMBell());

                    Camera.this.nGet_SID = -1;
                }
                if(mSID >= 0)
                {
                    UBICAPIs.UBIC_Session_Check_ByCallBackFn(mSID,
                            iotcubia);
                    isConected = true;
//                    for(int i = 0; i < mIOTCListeners.size(); i++)
//                    {
//                        IRegisterIOTCListener listener = (IRegisterIOTCListener)mIOTCListeners.get(i);
//                        listener.receiveSessionInfo(Camera.this, 2);
//
//                    }
                    DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mDevUID, 0, MainCameraFragment.CONNSTATUS_CONNECTED);
                    synchronized(mWaitObjectForConnected)
                    {
                        mWaitObjectForConnected.notify();
                    }
                    nRetryForIOTC_Conn=0;
                    continue;
                }
                 Log.i("IOTCamera", (new StringBuilder("IOTC_Get_SessionID SID = ")).append(nGet_SID).append(" mSID = ").append(mSID).toString());

                if(mSID == -20 || mSID == -14 ||   mSID == -23 || mSID == -22 || mSID == -13)
                {
                    nRetryForIOTC_Conn++;
                    isConected = false;
                    try
                    {
                        synchronized(m_waitForStopConnectThread)
                        {
                            m_waitForStopConnectThread.wait(2000L);
                        }
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ubia_UBICAPIs.IOTC_Session_Close(mSID);
                    if(  nRetryForIOTC_Conn> RECONNECT_COUNT)
                    {
                        DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mDevUID, 0, MainCameraFragment.CONNSTATUS_CONNECTION_FAILED);
                        break;
                    }
                    continue;
                }
                if(mSID == -15 || mSID == -10 || mSID == -19 )
                {
                    isConected = false;

                    nRetryForIOTC_Conn++;
                    try
                    {
                        long sleepTime = nRetryForIOTC_Conn <= 60 ? nRetryForIOTC_Conn * 1000 : 60000;
                        synchronized(m_waitForStopConnectThread)
                        {
                            m_waitForStopConnectThread.wait(sleepTime);
                        }
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    ubia_UBICAPIs.IOTC_Session_Close(mSID);
                    if(  nRetryForIOTC_Conn>RECONNECT_COUNT)
                    {
                        DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mDevUID, 0, MainCameraFragment.CONNSTATUS_CONNECTION_FAILED);
                        break;
                    }
                    continue;
                }
                if(mSID == -36 || mSID == -37)
                {
                    isConected = false;
//                    for(int i = 0; i < mIOTCListeners.size(); i++)
//                    {
//                        IRegisterIOTCListener listener = (IRegisterIOTCListener)mIOTCListeners.get(i);
//                        listener.receiveSessionInfo(Camera.this, 7);
//                    }
                    DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mDevUID, 0, MainCameraFragment.CONNSTATUS_RECONNECTION);
                } else
                {
                    isConected = false;
                    try
                    {
                        synchronized(m_waitForStopConnectThread)
                        {
                            m_waitForStopConnectThread.wait(2000L);
                        }
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ubia_UBICAPIs.IOTC_Session_Close(mSID);
                    DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mDevUID, 0, MainCameraFragment.CONNSTATUS_CONNECTION_FAILED);
                }
                SystemClock.sleep(40);
            }
            if(mSID<0)
            {
                isConected = false;
                //  if(  nRetryForIOTC_Conn>RECONNECT_COUNT)
                {
                    DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mDevUID, 0, MainCameraFragment.CONNSTATUS_CONNECTION_FAILED);
                }
            }
            Log.i("IOTCamera", "   设备连接  ===ThreadConnectDev exit===");
        }

        private int mConnType;
        private boolean mIsRunning;
        private Object m_waitForStopConnectThread;


        public ThreadConnectDev(int connType)
        {
            mConnType = -1;
            mIsRunning = false;
            m_waitForStopConnectThread = new Object();
            mConnType = connType;
        }
    }

    boolean returnAudio = false;
    private class ThreadDecodeAudio extends Thread
    {

        public void stopThread()
        {
            mStopedDecodeAudio = false;
        }

        public void run()
        {
            final int audioStartCount =40;
            int controlCount = 0;
            byte mp3OutBuf[] = new byte[65535];
            short speexOutBuf[] = new short[160];
            byte adpcmOutBuf[] = new byte[640];
            byte G726OutBuf[] = new byte[2048];
            byte pcmBuf[] = new byte[2048];
            byte G726OutBufZero[] = new byte[2048];
            long G726OutBufLen[] = new long[1];
            boolean bFirst = true;
            boolean bInitAudio = false;
            int nCodecId = -1;
            int nSamplerate = -1;
            int nDatabits = -1;
            int nChannel = -1;
            int nFPS = 0;
            long firstTimeStampFromDevice = 0L;
            long firstTimeStampFromLocal = 0L;
            long sleepTime = 0L;
            mStopedDecodeAudio = true;
            int faacret =  FdkAACCodec.initDecoder();
            this.setName("ThreadDecodeAudio");
            Log.v("main","FdkAACCodec---ThreadDecodeAudio faacret ="+faacret);
            boolean shouldWaitData = true;
            if(mAudioTrack==null)
            {
                mAudioTrack = new AudioTrack( AudioManager.STREAM_MUSIC,
                        16000,
                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        AudioTrack.getMinBufferSize( 16000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT ),
                        AudioTrack.MODE_STREAM );
                mAudioTrack.play();
            }
            while(mStopedDecodeAudio  && !Thread.interrupted())
            {
                if(mAVChannel.AudioFrameQueue.getCount() > 0)
                {
                    if(mAVChannel.AudioFrameQueue.getCount()>audioStartCount){//大于40帧数据，开始播放声音
                        shouldWaitData =false;

                    }
                    if(mAVChannel.AudioFrameQueue.getCount() < 2   ){//小于两个数据，暂停音频
                        shouldWaitData =true;

                    }
                    if(shouldWaitData){
                        try {
                            sleep(30);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        continue;
                    }
                    AVFrame frame = mAVChannel.AudioFrameQueue.removeHead();
                    if(frame==null) {
                        SystemClock.sleep(5);//无音频数据时不瞎忙
                        continue;
                    }

                    if(mutePlayout) {
                        SystemClock.sleep(5);//静音时候不瞎忙
                        continue;
                    }

                    nCodecId = frame.getCodecId();
                    returnAudio = false;
                    long writeStartTime = System.currentTimeMillis();
                    if(bFirst && !mInitAudio && (nCodecId == 142 || nCodecId == 141 || nCodecId == 139 || nCodecId == 140 || nCodecId == 143 || nCodecId == 138 || nCodecId == 137))
                    {
                        bFirst = false;
                        nSamplerate = AVFrame.getSamplerate(frame.getFlags());
                        AudioFrameBit = nSamplerate;

                        nDatabits = frame.getFlags() & 2;
                        nDatabits = nDatabits != 2 ? 0 : 1;
                        nChannel = frame.getFlags() & 1;
                        Log.v("main","ThreadDecodeAudio---");
                        bInitAudio = audioDev_init(nSamplerate, nChannel, nDatabits, nCodecId);
                        if(!bInitAudio)
                            break;
                    }
                    if(nCodecId == 141)
                    {
                        DecSpeex.Decode(frame.frmData, frame.getFrmSize(), speexOutBuf);

                        nFPS = (nSamplerate * (nChannel != 0 ? 2 : 1) * (nDatabits != 0 ? 16 : 8)) / 8 / 160;
                    } else
                    if(nCodecId == 142)
                    {
                        int len = DecMp3.Decode(frame.frmData, frame.getFrmSize(), mp3OutBuf);

                        nFPS = (nSamplerate * (nChannel != 0 ? 2 : 1) * (nDatabits != 0 ? 16 : 8)) / 8 / len;
                    } else
                    if(nCodecId == 139)
                    {
                        DecADPCM.Decode(frame.frmData, frame.getFrmSize(), adpcmOutBuf);

                        nFPS = (nSamplerate * (nChannel != 0 ? 2 : 1) * (nDatabits != 0 ? 16 : 8)) / 8 / 640;
                    } else
                    if(nCodecId == 140)
                    {
                        nFPS = (nSamplerate * (nChannel != 0 ? 2 : 1) * (nDatabits != 0 ? 16 : 8)) / 8 / frame.getFrmSize();
                    } else
                    if(nCodecId == 143)
                    {
                        DecG726.g726_decode(frame.frmData, frame.getFrmSize(), G726OutBuf, G726OutBufLen);
                        //Log.i("IOTCamera", (new StringBuilder("G726 decode size:")).append(G726OutBufLen[0]).toString());
                        nFPS = (nSamplerate * (nChannel != 0 ? 2 : 1) * (nDatabits != 0 ? 16 : 8)) / 8 / 640;
                    } else
                    if(nCodecId == 0x86)//AAC音频数据
                    {
                        //synchronized (currentDisplayTimeStamp)
                        {
                            int framMaxnum =150;
                            int sleeptime = 0;
                            //long times = (((long)frame.getTimeStamp())*1000+frame.getTempture());
                            currentDisplayTimeStamp = (long)( (long)frame.getTimeStamp())*1000+frame.getTempture();

                            Log.v("","视频音频帧数:"+mAVChannel.AudioFrameQueue.getCount()+" 当前视频回放时间  currentDisplayTimeStamp:"+currentDisplayTimeStamp+"  差值："+(currentDisplayTimeStamp-firstTimeStampFromLocal) );
                            firstTimeStampFromLocal =currentDisplayTimeStamp;
                            if (!controlCountReady) {
                                if (mAVChannel.AudioFrameQueue.getCount() > framMaxnum + 100)
                                {

                                    controlCountReady = true;//正方向控制
                                    sendIOCtrl(											0,
                                            AVIOCTRLDEFs.IOTYPE_USER_IPCAM_START,
                                            AVIOCTRLDEFs.UBIA_IO_AVStream
                                                    .playbackparseContent(
                                                            AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_PAUSE,
                                                            0, (byte)currentplaySeq ));
                                    controlCount = 0;
                                }
                            }
                            if (mAVChannel.AudioFrameQueue.getCount() < 100
                                    && controlCountReady) {

                                sendIOCtrl(
                                        0,
                                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_START,
                                        AVIOCTRLDEFs.UBIA_IO_AVStream
                                                .playbackparseContent(
                                                        AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_RESUME,
                                                        0, (byte)  currentplaySeq ));
                                controlCount = 0;
                                controlCountReady = false;//反方向控制
                            }


                            System.arraycopy(G726OutBufZero, 0, G726OutBuf, 0,G726OutBufZero.length);
                            System.arraycopy(G726OutBufZero, 0, pcmBuf, 0,G726OutBufZero.length);
                            Arrays.fill(G726OutBuf, (byte) 0);
                            System.arraycopy( frame.frmData, 0, pcmBuf, 0,frame.getFrmSize() );
                            if(recodeHelper!=null && recodeHelper.isSavingVideo() && ADDACCTRACK && StartisIFrame){
                                recodeHelper.saveAudioFrame(frame );
                            }

                            try
                            {
                                //Log.e("FdkAACCodec","Camera FdkAACCodec decodeFrame frame.getFrmSize():"+frame.getFrmSize());
                                int ret = FdkAACCodec.decodeFrame(G726OutBuf, 2048,pcmBuf, frame.getFrmSize()) ;
                                Log.e("FdkAACCodec","Camera FdkAACCodec decodeFrame ret:"+ret);
                                if(mutePlayout) continue;
                                if(!StartTalk && ret>0 && mAudioTrack!=null){
                                    mAudioTrack.write(G726OutBuf, 0, 2048);
                                }
                            }
                            catch( Exception e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }

                    long usetime = System.currentTimeMillis()- writeStartTime   ;
                    int shouldSleeptime = (int) Math.abs(62-usetime);
                    try
                    {
                        Thread.sleep(shouldSleeptime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    Log.v("","视频音频写音轨时间:  64-usetime: "+shouldSleeptime  +" total used time = "+(System.currentTimeMillis()- writeStartTime) );
                } else
                {
                    try
                    {
                        Thread.sleep(4L);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            if(bInitAudio)
                audioDev_stop(nCodecId);
            Log.e("IOTCamera", "===ThreadDecodeAudio exitDecoder exit=1==");
            FdkAACCodec.exitDecoder();
            mp3OutBuf = null;
            speexOutBuf = null;
            adpcmOutBuf  = null;
            G726OutBuf = null;
            pcmBuf  = null;
            G726OutBufZero  = null;
            System.gc();
            Log.e("IOTCamera", "===ThreadDecodeAudio exitDecoder exit=2==");
        }

        private boolean mStopedDecodeAudio;
        private AVChannel mAVChannel;


        public ThreadDecodeAudio(AVChannel channel)
        {
            mStopedDecodeAudio = false;
            mAVChannel = channel;
        }
    }


    private class ThreadWriteFile extends Thread {
        private AVChannel mAVChannel;
        private boolean m_bIsRunning = false;
        int mDownLoadfileblock;
        public ThreadWriteFile(AVChannel var2) {
            this.mAVChannel = var2;
        }

        public void run() {
            System.gc();
            this.mAVChannel.VideoFPS = 0;
            this.m_bIsRunning = true;
            int sendsleep = 0;
            int getcountf = 0;

            this.setName("ThreadWriteFile "+mDevUID);
            Log.i("Thread",
                    "===Thread name=" + this.getName() + this.getId());
            mDownLoadfileblock=getDownLoadfileblock();
            while (!Thread.interrupted() && m_bIsRunning) {
                if (!m_bIsRunning) {
                    System.gc();
                    Log.i("Thread", "===ThreadWriteFile exit===");
                    return;
                }


                if (map != null)
                    if ( randomFile != null) {
                        try {
                            fileLength = randomFile.length();
                            byte d[] = map.get(getcountf);
                            if (d!=null) {

                                // 将写文件指针移到文件尾。
                                randomFile.seek(fileLength);
                                randomFile.write(d);
                                IsDownLoadstatus = 1;
                                map.remove(getcountf);
                                getcountf++;
                                if (getcountf % 100 == 0)
                                {
                                    Bundle var4 = new Bundle();
                                    var4.putLong("value", fileLength);
                                    Message var5 = handler.obtainMessage();
                                    var5.what = 1001;
                                    var5.setData(var4);
                                    handler.sendMessage(var5);
                                }
                            } else {
                                sendsleep++;
                                if (sendsleep > 50) {
                                    if (getcountf >= mDownLoadfileblock + 1) {
                                        try {
                                            System.gc();
                                            map.clear();
                                            randomFile.close();
                                        } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        Bundle var4 = new Bundle();
                                        var4.putLong("value", fileLength);
                                        Message var5 = handler.obtainMessage();
                                        var5.what = 1001;
                                        var5.setData(var4);
                                        handler.sendMessage(var5);
                                        IsDownLoading = false;
                                        m_bIsRunning = false;
                                        IsDownLoadstatus = 2;

                                        return;
                                    } else {
                                        sendIOCtrl(
                                                mAVChannel.getChannel(),
                                                AVIOCTRLDEFs.IOTYPE_USER_FILE_TRANS_PKT_NAK,
                                                SMsgFileTransPktNak
                                                        .parseContent(
                                                                (byte) mAVChannel
                                                                        .getChannel(),
                                                                getcountf)

                                        );

                                        sendsleep = 0;
                                    }
                                }
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            Thread.sleep(15L);
                        } catch (InterruptedException localInterruptedException) {
                            localInterruptedException.printStackTrace();
                            //Thread.currentThread().interrupt();//重新设置中断标示
                        }
                    }

                try {
                    Thread.sleep(13L);
                } catch (InterruptedException localInterruptedException) {
                    localInterruptedException.printStackTrace();
                    //Thread.currentThread().interrupt();//重新设置中断标示
                }

            }
        }

        public void stopThread() {
            this.m_bIsRunning = false;
            IsDownLoading = false;
        }
    }

    long currentDisplayTimeStamp;
    boolean isPlayback = false;
    SurfaceDecoder SDecoder = null;
    private class ThreadDecodeVideo2 extends Thread
    {
        private int frameno = 0;
        public void stopThread()
        {
            m_bIsRunning = false;

        }
        public int skipSEIFrame(byte[] videoBuffer,int length)
        {
            int nalType = 0;
            int i = 0;
            for (i = 0; i < length; i++) {

                if ((videoBuffer[i] == 0x00) && (videoBuffer[i+1] == 0x00) && (videoBuffer[i+2] == 0x00) && (videoBuffer[i+3] == 0x01))
                {
                    nalType = videoBuffer[i+4] & 0x1F;
                    if (nalType == 0x01 || nalType == 0x05 || nalType == 0x07 ||nalType == 0x08) {
                        break;
                    }else if (nalType == 0x06){
                        Log.i("IOTCamera", "Found SEI Frame");
                    }
                }
            }
            if(i > 0 && i < (length - 4)){
                Log.i("IOTCamera", "Skip SEI Frame Bytes["+i+"] "+ "Valid FrameType:" + videoBuffer[i+4]);
            }
            return i;
        }
        public void run()
        {
            System.gc();
            int avFrameSize = 0;
            AVFrame avFrame = null;
            int videoWidth = 0;
            int videoHeight = 0;

            nDispFrmPreSec = 0;
            long lastUpdateDispFrmPreSec = 0L;
            long lastFrameTimeStamp = 0L;
            long delayTime = 0L;

            ByteBuffer bytBuffer = null;
            Bitmap bitmap = null;
            int out_width[] = new int[1];
            int out_height[] = new int[1];
            int out_size[] = new int[1];
            boolean bInitH264 = false;
            boolean bInitMpeg4 = false;
            mAVChannel.VideoFPS = 0;
            m_bIsRunning = true;

            int[] width = new int[1];
            int[] height = new int[1];
            int[] size = new int[1];
            int[] frameInfo = new int[4];

            int j = 0;
            System.gc();
            long sleepStartTime = 0;
            long sleepEndTime = 0;
            this.setName("ThreadDecodeVideo2");
            boolean bSkipThisRound = false;
            int sleepshort = 4;

            int resolution = -1;

            long detaCameraTime = 0;
            long detalocalTime = 0;

            long firstCameraTime = 0;
            long firstlocalTime = 0;


            while(m_bIsRunning && !Thread.interrupted() ) {
                avFrame = null;

                if(mAVChannel.VideoFrameQueue.getCount() > 0) {
                    avFrame = null;
                    avFrame = mAVChannel.VideoFrameQueue.removeHead();
                    if(avFrame == null) {
                        SystemClock.sleep(5);//no video
                        continue;
                    }
                    avFrameSize = avFrame.getFrmSize();
                } else {
                    try {
                        Thread.sleep(15L);
                    }catch(InterruptedException e)
                    {
                        //e.printStackTrace();
                    }
                    continue;
                }
                sleepStartTime = System.currentTimeMillis();
                if(!avFrame.isIFrame() && delayTime > 2000L) {
                    long skipTime = (long)avFrame.getTimeStamp() - lastFrameTimeStamp;
                    Log.i("IOTCamera", (new StringBuilder("case 1. low decode performance, drop ")).append(avFrame.isIFrame() ? "I" : "P").append(" frame, skip time: ").append((long)avFrame.getTimeStamp() - lastFrameTimeStamp).append(", total skip: ").append(skipTime).toString());
                    lastFrameTimeStamp = avFrame.getTimeStamp();
                    delayTime -= skipTime;
                    bSkipThisRound = true;
                } else if(!avFrame.isIFrame() && bSkipThisRound) {
                    long skipTime = (long)avFrame.getTimeStamp() - lastFrameTimeStamp;
                    Log.i("IOTCamera", (new StringBuilder("case 2. low decode performance, drop ")).append(avFrame.isIFrame() ? "I" : "P").append(" frame, skip time: ").append((long)avFrame.getTimeStamp() - lastFrameTimeStamp).append(", total skip: ").append(skipTime).toString());
                    lastFrameTimeStamp = avFrame.getTimeStamp();
                    delayTime -= skipTime;
                } else{
                    if(avFrameSize > 0) {
                        out_size[0] = 0;
                        out_width[0] = 0;
                        out_height[0] = 0;
                        bSkipThisRound = false;

                        size[0] = 0;
                        width[0] = 0;
                        height[0] = 0;
                        frameInfo[2] = 0;
                        frameInfo[3] = 0;

//                    Utils.LOGD("解码更新    开始取出数据队列>>>>>>>> i帧："+frameInfo[2] );
                        if (!avFrame.isIFrame()) {// 花屏，去除不连贯P帧
                            if (avFrame.getCam_index() != 0) {//队列中帧号连续
                                if (avFrame.getCam_index() - frameno != 1) {
                                    try {
                                        Thread.sleep(10L);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Log.e("test", "解码丢弃 收到直播数据流Call VideoDatmAVChannel.VideoFrameQu lost frameno= "+frameno+"   avFrame.getCam_index()="+avFrame.getCam_index()
                                            +"   avFrame.isIFrame()="+avFrame.isIFrame());

                                    continue;
                                }
                            }
                        }

                        frameno = avFrame.getCam_index();
                        if(resolution!= avFrame.getResolution()&& -1 != avFrame.getResolution() ){
                            StartisIFrame = false;
                            switch (avFrame.getResolution()) {
                                case VRConfig.UBIA_PIC_1280_720:

                                    VRConfig.width = 1280;
                                    VRConfig.height  = 720;
                                    break;
                                case VRConfig.UBIA_PIC_1280_960:
                                    VRConfig.width = 1280;
                                    VRConfig.height  = 960;
                                    break;
                                case VRConfig.UBIA_PIC_1920_1080:
                                    VRConfig.width = 1920;
                                    VRConfig.height  = 1080;
                                    break;
                                case VRConfig.UBIA_PIC_640_360:
                                    VRConfig.width = 640;
                                    VRConfig.height  = 360;
                                    break;
                                case VRConfig.UBIA_PIC_640_480:
                                    VRConfig.width = 640;
                                    VRConfig.height  = 480;
                                    break;
                                case VRConfig.UBIA_PIC_960_960:
                                    VRConfig.width = 960;
                                    VRConfig.height  = 960;
                                    break;
                                case VRConfig.UBIA_PIC_480_480:
                                    VRConfig.width = 480;
                                    VRConfig.height  = 480;
                                    break;
                                default:
                                    break;
                            }
                            resolution = avFrame.getResolution();
                            if(SDecoder!=null) {
                                SDecoder.release();
                            }
                            SDecoder = new SurfaceDecoder();
                            if(SDecoder != null){
                                SDecoder.SoftDecoderPrePare();
                                Log.e("TESTDECODE","camera create SurfaceDecoder");
                            }
                        }
                        if (!StartisIFrame &&  avFrame.isIFrame()) {
                            StartisIFrame = true;
                            firstCameraTime = avFrame.getTimeStamp();
                            firstlocalTime = System.currentTimeMillis();
                        }
//							  Utils.LOGD("解码更新    开始取出数据队列>>>>>>>>22222 i帧："+frameInfo[2] +"  √StartisIFrame:"+StartisIFrame);

                        try {

                            if(avFrame.frmData[4]==0x06) {
                                //Get SEI Frame
                                //int size = recvBuf[6];
                                long deviceTime = Packet.byteArrayToInt_Little(avFrame.frmData,8);
                                LiveViewTimeStateCallbackInterface_Manager.getInstance().TimeUTCStatecallback(deviceTime);
                                //Log.e("Thread","==deviceTime=== getLocalTime(deviceTime):"+deviceTime+"  avFrame.getTimeStamp:"+avFrame.getTimeStampSec());
                            }


                            if (StartisIFrame && SDecoder != null ){
									//  Utils.LOGD("解码更新    开始取出数据队列>>>>>>>>开始解码了   i帧："+frameInfo[2] );
                                bitmap=	SDecoder.doExtract(avFrame.frmData,
                                        avFrameSize, 1920, 1080,
                                        avFrame.isIFrame() ? 1 : 0,
                                        attachedMonitor);

                            }

                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }


                        if (StartisIFrame && recodeHelper!=null && recodeHelper.isSavingVideo()) {
//                   				 LiveViewTimeStateCallbackInterface_Manager.getInstance().TimeUTCSecStatecallback(avFrame.getTimeStampSec());
                            recodeHelper.saveAvFrame(avFrame);
                        }
                        long times = (((long)avFrame.getTimeStamp())*1000+avFrame.getTempture());
                        //Log.v("","视频音频帧数:"+mAVChannel.AudioFrameQueue.getCount()+" 当前视频回放时间  currentDisplayTimeStamp:"+currentDisplayTimeStamp+"  当前音频时间 frame.getTimeStamp():"+times);
                        int sleeptime=0;
                        //Log.v("","视频时间戳小于音频 ，视频差值="+(currentDisplayTimeStamp-times));
                        if( times < currentDisplayTimeStamp-1000 && isPlayback)//小于一秒
                        {
                            sleepshort = 1;//休眠时间小些，快放
                            //Log.v("","视频时间戳小于音频 ，视频要快点放   AAC continue;mAVChannel.AudioFrameQueue.getCount:"+mAVChannel.AudioFrameQueue.getCount()+"   currentDisplayTimeStamp:"+currentDisplayTimeStamp+"   frame.getTimeStamp():"+times);

                        }

                        while(avFrame!=null && times > currentDisplayTimeStamp && isPlayback){
                            //视频时间戳大于音频
                            try {
                                Log.v("","视频时间戳大于音频 ，视频要慢点放   AAC continue;mAVChannel.AudioFrameQueue.getCount:"+mAVChannel.AudioFrameQueue.getCount()+"   currentDisplayTimeStamp:"+currentDisplayTimeStamp+"   frame.getTimeStamp():"+times);
                                sleep(10);
                                sleeptime++;
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            if(!isPlayback||sleeptime>5) break;
                            continue;
                        }

                        while(avFrame!=null && times > currentDisplayTimeStamp+3 && isPlayback){//视频比音频快3秒以上
                            //视频时间戳大于音频
                            try {
                                //Log.v("","视频时间戳大于音频3秒以上 ，视频要一直睡，睡到音频过来为止   AAC continue;mAVChannel.AudioFrameQueue.getCount:"+mAVChannel.AudioFrameQueue.getCount()+"   currentDisplayTimeStamp:"+currentDisplayTimeStamp+"   frame.getTimeStamp():"+times);
                                sleep(10);
                                sleeptime++;
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            if(times < currentDisplayTimeStamp+1 ||sleeptime>20) break;
                            continue;
                        }

//                    		if(bitmap!=null){
//    							this.mAVChannel.VideoFPS = (1 + this.mAVChannel.VideoFPS);
//    							Camera.nFlow_total_FPS_count = 1 + Camera.nFlow_total_FPS_count;
//    							Camera.nFlow_total_FPS_count_noClear = 1 + Camera.nFlow_total_FPS_count_noClear;
//    							Camera localCamera = Camera.this;
//    							localCamera.nDispFrmPreSec = (1 + localCamera.nDispFrmPreSec);
//    							for (int m = 0;; m++) {
//    								if (m >= Camera.this.mIOTCListeners.size()) {
//    									this.mAVChannel.LastFrame = bitmap;
//    									break;
//    								}
//    								((IRegisterIOTCListener) Camera.this.mIOTCListeners
//    										.get(m)).receiveFrameData(Camera.this,
//    										this.mAVChannel.getChannel(), bitmap);
//
//    							}
//    						}else{
//    							Log.e("TESTDECODE","doExtract bitmap is null");
//    						}

                    }

                    detaCameraTime  = avFrame.getTimeStamp()-firstCameraTime;//current frame gap of the first frame
                    detalocalTime = System.currentTimeMillis()-firstlocalTime;//current location  gap of the first frame
                    int ratio = (int) ((detalocalTime-detaCameraTime) /1000);
                    if(ratio<0){
                        ratio = 0;
                    }
                    if(isPlayback){
                        ratio = 0;
                    }
                 //   Log.v("","avFrame.getTimeStamp():"+avFrame.getTimeStamp()+"   detaCameraTime:"+detaCameraTime   + "     detalocalTime"+detalocalTime+ "     gap"+ 	(detalocalTime-detaCameraTime)+"  ratio:"+ratio+"    mAVChannel.VideoFrameQueue.size:"+mAVChannel.VideoFrameQueue.getCount());
                      Log.e("guo..Camera","1.....TimeStamp="+avFrame.getTimeStamp()+",Framerate="+avFrame.getFramerate());
                    try{
                     //   int rate = avFrame.getFramerate() < 0 ? 15 : avFrame.getFramerate();

                        int needSleepTime = 1000/avFrame.getFramerate();
                        sleepEndTime = System.currentTimeMillis();
                        long hasUsedTime = sleepEndTime-sleepStartTime;
                        long acturalSleep = needSleepTime - hasUsedTime;
                        Log.e("guo..Camera","2.....acturalSleep="+acturalSleep+",needSleepTime="+needSleepTime);
                        if (width[0] == 1280) {
                            try {
                                Thread.sleep(5L * sleepshort);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else {
                            if(acturalSleep<30 ){
                                acturalSleep =30;
                            }
                            if( acturalSleep > needSleepTime){
                                acturalSleep = needSleepTime;
                            }
                            int sleeptime = (int) (acturalSleep-(acturalSleep*ratio*0.1));
                            if(sleeptime<=0){
                                sleeptime = 5;
                            }
                            Log.e("guo..Camera","sleeptime="+sleeptime);
                            Thread.sleep(sleeptime);//per sec sleep less 10%
                        }
                        if(avFrame != null){
                            avFrame.frmData = null;
                            avFrame = null;
                        }
                    } catch(Exception e){

                        e.printStackTrace();
                    }

                }

            }
            if (SDecoder!=null) {
                SDecoder.release();
                Log.i("IOTCamera", "===	SDecoder.release  ===");
            }
//        	attachedMonitor = null;
            System.gc();
            Log.i("IOTCamera", "===ThreadDecodeVideo exit===");
        }

        static final int MAX_FRAMEBUF = 0x384000;
        private boolean m_bIsRunning;
        private AVChannel mAVChannel;


        public ThreadDecodeVideo2(AVChannel channel)
        {

            m_bIsRunning = false;
            mAVChannel = channel;
        }
    }

    private class ThreadRecvAudio extends Thread
    {

        public void stopThread()
        {
            bIsRunning = false;
            FdkAACCodec.exitEncoder();
        }

        public void run()
        {
            for(bIsRunning = true; bIsRunning && (mSID < 0 || mAVChannel.getAVIndex() < 0);)
                try
                {
                    synchronized(mWaitObjectForConnected)
                    {
                        mWaitObjectForConnected.wait(100L);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            this.setName("ThreadRecvAudio");
            mAVChannel.AudioBPS = 0;
            FdkAACCodec.initEncoder();
            byte recvBuf[] = new byte[1280];
            byte bytAVFrame[] = new byte[24];
            int pFrmNo[] = new int[1];
            short speexOutBuf[] = new short[160];
            byte adpcmOutBuf[] = new byte[640];
            byte G726OutBuf[] = new byte[2048];
            byte pcm2AACBuffer[] = new byte[2048];
            long G726OutBufLen[] = new long[1];
            boolean bFirst = true;
            boolean bInitAudio = false;
            int nSamplerate = 44100;
            long mSampletime = 0;
            int nDatabits = 1;
            int nChannel = 1;
            int nCodecId = 0;
            int nFPS = 0;
            AmrDecoder.init();
            m_pclAudioReceiveLinkedList = new LinkedList<short[]>();
            short outPCMDATA[] = new short[160];
            if(mSID >= 0 && mAVChannel.getAVIndex() >= 0)
                ubia_UBICAVAPIs.avClientCleanAudioBuf(mAVChannel.getAVIndex());
            mAVChannel.AudioFrameQueue.removeAll();
            if(bIsRunning && mSID >= 0 && mAVChannel.getAVIndex() >= 0)
                mAVChannel.IOCtrlQueue.Enqueue(mAVChannel.getAVIndex(), 768, Packet.intToByteArray_Little(mCamIndex));
            final int bytesPerFrame = CHANNELS * (BITS_PER_SAMPLE / 8);
            byte[] recvBuffer = new byte[bytesPerFrame * (SAMPLE_RATE / BUFFERS_PER_SECOND)];
            short inPCMBufShort[] = new short[audio_sample_size]; //2048
            while(bIsRunning  && !Thread.interrupted())
            {
                if(mSID < 0 || mAVChannel.getAVIndex() < 0)
                {
                    SystemClock.sleep(5);//
                    continue;
                }



                nReadSize = ubia_UBICAVAPIs.avRecvAudioData(mAVChannel.getAVIndex(), recvBuf, recvBuf.length, bytAVFrame, 24, pFrmNo);


                if(nReadSize < 0 && nReadSize != -20012)
                    Log.i("IOTCamera", "avRecvAudioData < 0");
                if(nReadSize > 0)
                {
                    mAVChannel.AudioBPS += nReadSize;
                    byte frameData[] = new byte[nReadSize];
                    System.arraycopy(recvBuf, 0, frameData, 0, nReadSize);
                    AVFrame frame = new AVFrame(pFrmNo[0], (byte)0, bytAVFrame, frameData, nReadSize);
                    nCodecId = frame.getCodecId();



                    if(bFirst && !mInitAudio && (nCodecId == 142 || nCodecId == 141 || nCodecId == 139 || nCodecId == 140 || nCodecId == 143 || nCodecId == 138 || nCodecId == 137))
                    {


                        bFirst = false;
                        nSamplerate = AVFrame.getSamplerate(frame.getFlags());
                        nDatabits = frame.getFlags() & 2;
                        nDatabits = nDatabits != 2 ? 0 : 1;
                        nChannel = frame.getFlags() & 1;
                        if(nCodecId == 141)
                            nFPS = (nSamplerate * (nChannel != 0 ? 2 : 1) * (nDatabits != 0 ? 16 : 8)) / 8 / 160;
                        else
                        if(nCodecId == 139)
                            nFPS = (nSamplerate * (nChannel != 0 ? 2 : 1) * (nDatabits != 0 ? 16 : 8)) / 8 / 640;
                        else
                        if(nCodecId == 140)
                            nFPS = (nSamplerate * (nChannel != 0 ? 2 : 1) * (nDatabits != 0 ? 16 : 8)) / 8 / frame.getFrmSize();
                        nSamplerate = AVFrame.getSamplerate(frame.getFlags());
                        bInitAudio = audioDev_init(nSamplerate, nChannel, nDatabits, nCodecId);
                        if(!bInitAudio)
                            break;
                    }
                    if(nCodecId == 141)
                    {
                        DecSpeex.Decode(recvBuf, nReadSize, speexOutBuf);

                    } else

                    if(nCodecId == 143)
                    {
                        DecG726.g726_decode(recvBuf, nReadSize, G726OutBuf, G726OutBufLen);
                        //Log.i("IOTCamera", (new StringBuilder("G726 decode size:")).append(G726OutBufLen[0]).toString()+"StartTalk ="+StartTalk);

                        if(recodeHelper!=null && recodeHelper.isSavingVideo() && StartisIFrame ){
                            System.arraycopy(G726OutBuf, 0, pcmBuffer, pcmBufferWritePostion, (int) G726OutBufLen[0]);
                            pcmBufferWritePostion += G726OutBufLen[0];

                            if(pcmBufferWritePostion>2048){
                                byte[] pcmTempBuffer = new byte[pcmBufferWritePostion];
                                System.arraycopy(pcmBuffer, 0, pcmTempBuffer, 0,pcmBufferWritePostion);
                                System.arraycopy(pcmTempBuffer, 0, pcm2AACBuffer, 0,2048);
                                Arrays.fill(pcmBuffer, (byte) 0);
                                pcmBufferWritePostion-=2048;
                                if(pcmBuffer!=null )
                                    System.arraycopy(pcmTempBuffer, 2048, pcmBuffer, 0,pcmBufferWritePostion);

                                byte outputArr[] = new byte[1024];
                                int outputSize ;
                                outputSize = FdkAACCodec.encodeFrame(outputArr, outputArr.length, pcm2AACBuffer, 2048);
                                Log.e("FdkAACCodec",  "FdkAACCodec.encodeFrame  pcmBufferWritePostion ="+pcmBufferWritePostion +"   outputSize:"+outputSize);

                                if(outputSize>0){
                                    byte databuffer[] = new byte[outputSize];
                                    System.arraycopy(outputArr, 0, databuffer, 0,outputSize);
                                    AVFrame audioFrame = new AVFrame(0, (byte) 0, outputArr, databuffer, outputSize);
                                    recodeHelper.saveAudioFrame(audioFrame);
                                }
                            }
                        }
                        if(mutePlayout){
                            SystemClock.sleep(5);
                            m_pclAudioReceiveLinkedList.clear();
                            continue;
                        }

//                        short[] buffer;
                        try {


                            short inPCMBuf2Short[] = new short[audio_sample_size];
                            for(int i = 0;i<audio_sample_size;i++){
                                inPCMBuf2Short[i]= Packet.byteArrayToShort_Little(G726OutBuf, i*2) ;//(short) (G726OutBuf[0+2*i] | (((int)G726OutBuf[1+2*i] )<<8));
                            }
                            m_pclAudioReceiveLinkedList.addLast( inPCMBuf2Short );
                            if( !StartTalk ) {
                                if(StartTalk==false && mAudioTrack==null)
                                {
                                    mAudioTrack = new AudioTrack( AudioManager.STREAM_MUSIC,
                                            16000,
                                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                            AudioFormat.ENCODING_PCM_16BIT,
                                            AudioTrack.getMinBufferSize( 16000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT ),
                                            AudioTrack.MODE_STREAM );

                                    mAudioTrack.play();

                                }
                                mAudioTrack.write(inPCMBuf2Short, 0, audio_sample_size);
                            }else{
                                if(mAudioTrack != null)
                                {
                                    mAudioTrack.stop();
                                    mAudioTrack.release();
                                    mAudioTrack = null;
                                    m_pclAudioReceiveLinkedList.clear();
                                }
                            }
                        } catch ( Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    } else
                    if(nCodecId == 144)//AMR 8k 32byte
                    {
                        if(recodeHelper!=null && recodeHelper.isSavingVideo() && nReadSize==32 ){
                            mSampletime = mSampletime+20;
                            //  Log.i("IOTCamera", (new StringBuilder(" AMR 8k 32byte decode size:")).append(nReadSize).toString()+"StartTalk ="+StartTalk);
//                    	    	recodeHelper.saveAudioFrame(recvBuf, 32, mSampletime, 0);
                            AVFrame audioFrame = new AVFrame(0, (byte) 0, recvBuf, recvBuf, 32);
                            recodeHelper.saveAudioFrame(audioFrame);

                        }
                        AmrDecoder.decode(recvBuf, outPCMDATA);
                        if(mutePlayout) continue;
//                             mAudioTrack.write(outPCMDATA, 0, 160);
                    } else
                    if(nCodecId == 0x86)//AAC音频数据
                    {
                        int framMaxnum=50;
                        mAVChannel.AudioFrameQueue.addLast(frame);

                        try
                        {
                            Thread.sleep(20L);
                        }
                        catch(InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }



                } else
                if(nReadSize == -20012)
                    try
                    {
                        Thread.sleep(50L);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                else
                if(nReadSize == -20014)
                {
                    Log.i("IOTCamera", "avRecvAudioData returns AV_ER_LOSED_THIS_FRAME");
                } else
                {
                    try
                    {
                        Thread.sleep(nFPS != 0 ? 1000 / nFPS : 33);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    Log.i("IOTCamera", (new StringBuilder("avRecvAudioData returns ")).append(nReadSize).toString());
                }
            }
            if(bInitAudio)
                audioDev_stop(nCodecId);
            speexOutBuf = null;
            adpcmOutBuf  = null;
            G726OutBuf = null;
            pcm2AACBuffer   = null;
            System.gc();
            if( mAVChannel!= null && mAVChannel.IOCtrlQueue!=null){
                mAVChannel.IOCtrlQueue.Enqueue(mAVChannel.getAVIndex(), 769, Packet.intToByteArray_Little(mCamIndex));
            }

            Log.i("IOTCamera", "===ThreadRecvAudio exit===");
        }

        private final int MAX_BUF_SIZE = 1280;
        private int nReadSize;
        private boolean bIsRunning;
        private AVChannel mAVChannel;


        public ThreadRecvAudio(AVChannel channel)
        {

            nReadSize = 0;
            bIsRunning = false;
            mAVChannel = channel;
        }
    }

    private int lastIoCtrlType;
    private long lasttime = 0;
    private class ThreadRecvIOCtrl extends Thread
    {

        public void stopThread()
        {
            bIsRunning = false;
        }

        public void run()
        {
            for(bIsRunning = true; bIsRunning && (mSID < 0 || mAVChannel.getAVIndex() < 0);)
                try
                {
                    synchronized(mWaitObjectForConnected)
                    {
                        mWaitObjectForConnected.wait(1000L);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            int idx = 0;
            while(bIsRunning  && !Thread.interrupted() )
                if(mSID >= 0 && mAVChannel.getAVIndex() >= 0)
                {
                    int ioCtrlType[] = new int[1];
                    byte ioCtrlBuf[] = new byte[2048];
                    int nRet = ubia_UBICAVAPIs.avRecvIOCtrl(mAVChannel.getAVIndex(), ioCtrlType, ioCtrlBuf, ioCtrlBuf.length, 0);

                     if(ioCtrlType[0] != AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FIRMWARE_UPDATE_CHECK_RSP){ //防止重复收到命令,除了固件更新的进度
                        if(ioCtrlType[0] == lastIoCtrlType){
                            long newTime = System.currentTimeMillis();

                            if(newTime - lasttime < 500){
                                nRet = -1;
                            }
                            lasttime = newTime;
                        }
                    }/*else{
                        Log.e("Camera..guo","更新、、、、");
                    }*/



                    if(nRet >= 0)
                    {
                        lastIoCtrlType = ioCtrlType[0];
                        Log.i("IOTCamera", "nRet="+nRet+"   "+(new StringBuilder("avRecvIOCtrl(")).append(mAVChannel.getAVIndex()).append(", 0x").append(Integer.toHexString(ioCtrlType[0])).append(", ").append(Camera.getHex(ioCtrlBuf, nRet)).append(")").toString());
                        byte data[] = new byte[nRet];
                        System.arraycopy(ioCtrlBuf, 0, data, 0, nRet);
                        if(ioCtrlType[0] == 811)
                        {
                            int channel = Packet.byteArrayToInt_Little(data, 0);
                            int format = Packet.byteArrayToInt_Little(data, 4);
                            for(Iterator iterator = mAVChannels.iterator(); iterator.hasNext();)
                            {
                                AVChannel ch = (AVChannel)iterator.next();
                                if(ch.getChannel() == channel)
                                {
                                    ch.setAudioCodec(format);
                                    break;
                                }
                            }

                        }
                        if(ioCtrlType[0] == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_FLOWINFO_REQ)
                        {
                            int channel = Packet.byteArrayToInt_Little(data, 0);
                            int collect_interval = Packet.byteArrayToInt_Little(data, 4);
                            for(Iterator iterator1 = mAVChannels.iterator(); iterator1.hasNext();)
                            {
                                AVChannel ch = (AVChannel)iterator1.next();
                                if(ch.getChannel() == channel)
                                {
                                    ch.flowInfoInterval = collect_interval;
                                    sendIOCtrl(mAVChannel.mChannel, 913, AVIOCTRLDEFs.SMsgAVIoctrlGetFlowInfoResp.parseContent(channel, ch.flowInfoInterval));
                                    break;
                                }
                            }


                        }

                        if(ioCtrlType[0] == AVIOCTRLDEFs.UBIA_IO_IPCAM_LISTEVENT_RESP)
                        {
                            Log.e("Camera...guo","收到事件列表。。。"+(data.length-12)/8);

                            //前12个字节头
                            byte channel[] = new byte[4];
                            System.arraycopy(data, 0, channel, 0, 4);
                            /* 信息头 */
                            Log.e("Camera...guo","channel："+ Packet.byteArrayToInt_Little(channel));
                            Log.e("Camera...guo","IO："+ String.format("%2x",data[4]&0xff));
                            Log.e("Camera...guo","index："+ String.format("%2x",data[5]&0xff));
                            Log.e("Camera...guo","endflag："+ String.format("%2x",data[6]&0xff));
                          //  Log.e("Camera...guo","count："+  Packet.byteArrayToInt_Little(data, 7));
                            byte total[] = new byte[2];
                            System.arraycopy(data, 8, total, 0, 2);
                            Log.e("Camera...guo","total："+ Packet.byteArrayToInt_Little(total));



                            /* 事件信息 。每8个字节为一个事件 */
                            byte[] bytes = new byte[data.length-12];
                            System.arraycopy(data, 12, bytes, 0, bytes.length);

                            int result = bytes.length/8;

                            for(int i = 0;i < result ;i++){
                                int dataStart = 8 * i;
                                byte[] dataBuf = new byte [8];
                                System.arraycopy(bytes, dataStart, dataBuf, 0, 8);

                                byte stTime[] = new byte[4];
                                System.arraycopy(dataBuf, 0, stTime, 0, 4);
                                Log.e("Camera...guo","stTime："+ Packet.byteArrayToInt_Little(stTime));

                                byte length[] = new byte[2];
                                System.arraycopy(dataBuf, 4, length, 0, 2);
                                Log.e("Camera...guo","length："+  Packet.byteArrayToInt_Little(length));

                                Log.e("Camera...guo","event："+ String.format("%2x",dataBuf[6]&0xff));
                                Log.e("Camera...guo","status："+ String.format("%2x",dataBuf[7]&0xff));
                                Log.e("Camera...guo","============");

                            }

                         }

                        if(ioCtrlType[0] == AVIOCTRLDEFs.UBIA_IO_IPCAM_RECORD_BITMAP_RSP)
                        {
                            int channel = Packet.byteArrayToInt_Little(data, 0);
                            int collect_interval = Packet.byteArrayToInt_Little(data, 4);
                            NoteInfoData mNoteInfoData = new NoteInfoData(data);
                            GetTimelineBitMapCallback_Manager.getInstance().GetPushNoteStatecallback(mNoteInfoData, true);
                        }else if(ioCtrlType[0] == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPASSWORD_RESP ){
                            Log.e("Camera...guo","收到修改密码响应。。。");
                            IOTCCMDStateCallbackInterface_Manager.getInstance().IOTCCMDStatecallback(AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPASSWORD_RESP, data[0]);


                        }
                        else if(ioCtrlType[0] == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_ADVANCESETTINGS_RESP && data.length>74){
                            DeviceInfo mDeviceInfo = MainCameraFragment.getexistDevice(mDevUID);
                            mDeviceInfo.garageDoor = data[74]==1?true:false;
                            DoorStateCallbackInterface_Manager.getInstance().DoorStatecallback(data[74]);

                        } else if(ioCtrlType[0] == AVIOCTRLDEFs.UBIA_IO_EXT_ZIGBEEINFO_RSP && data.length>74){
                            DeviceInfo mDeviceInfo = MainCameraFragment.getexistDevice(mDevUID);
                            ZigbeeInfo mZigbeeInfo = new ZigbeeInfo();
                            int infoSize = Packet.byteArrayToInt_Little(data, 8);
                            byte macaddr[] = new byte[8];
                            byte info[] = new byte[infoSize];
                            System.arraycopy(data, 12, macaddr, 0, 8);
                            System.arraycopy(data, 20, info, 0, infoSize);
                            mZigbeeInfo.macaddr =String.format("%02x%02x%02x%02x%02x%02x",macaddr[0]&0xff,macaddr[1]&0xff,macaddr[2]&0xff,macaddr[3]&0xff,macaddr[4]&0xff,macaddr[5]&0xff )  ;
                            mZigbeeInfo.info = new String(info);
                            ZigbeeInfoCallbackInterface_Manager.getInstance().ZigbeeInfocallback(mZigbeeInfo);

                        }
                        for(int i = 0; i < mIOTCListeners.size(); i++)
                        {
                            IRegisterIOTCListener listener = (IRegisterIOTCListener)mIOTCListeners.get(i);
                            listener.receiveIOCtrlData(Camera.this, mAVChannel.getChannel(), ioCtrlType[0], data);
                        }
                        SystemClock.sleep(5);
                        data= null;
                    } else
                    {
                        try
                        {
                            Thread.sleep(100L);
                        }
                        catch(InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    ioCtrlBuf =null;
                }
            Log.i("IOTCamera", "===ThreadRecvIOCtrl exit===");
        }

        private final int TIME_OUT = 0;
        private boolean bIsRunning;
        private AVChannel mAVChannel;


        public ThreadRecvIOCtrl(AVChannel channel)
        {

            bIsRunning = false;
            mAVChannel = channel;
        }
    }

    private class ThreadRecvVideo2 extends Thread
    {
        public void stopThread()
        {
            bIsRunning = false;
        }

        public void run()
        {
            System.gc();
            for(bIsRunning = true; bIsRunning && (mSID < 0 || mAVChannel.getAVIndex() < 0);)
                try
                {
                    synchronized(mWaitObjectForConnected)
                    {
                        mWaitObjectForConnected.wait(100L);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            this.setName("ThreadRecvVideo2");
            int FRAME_INFO_SIZE=24;
            byte[] frameInfo = new byte[FRAME_INFO_SIZE];
            byte[] videoBuffer = new byte[MAX_BUF_SIZE];
            int[] frameNumber = new int[1];
            AVFrame lastAvFrame = null;
            int i = 0;
            int lostFrameNumber = 0;
            int[] outFrmSize = new int[1];
            int[] pFrmInfoBuf = new int[1];
            int[] outFrmInfoBufSize = new int[1];
            long localFramNo = 0;
            mAVChannel.VideoBPS = 0;
            nRecvFrmPreSec = 0;
            int outBufSize[] = new int[1];
            int nReadSize ;
            if(mSID >= 0 && mAVChannel.getAVIndex() >= 0 && avNoClearBuf)
                AVAPIs.UBIC_avClientCleanVideoBuf(mAVChannel.getAVIndex());
            mAVChannel.VideoFrameQueue.removeAll();
//            if(bIsRunning && mSID >= 0 && mAVChannel.getAVIndex() >= 0)
//                mAVChannel.IOCtrlQueue.Enqueue(mAVChannel.getAVIndex(), AVIOCTRLDEFs.IOTYPE_USER_IPCAM_START, UBIA_IO_AVStream.startLiveView(MainCameraFragment.getexistDevice(mDevUID).channelIndex) );
            while(bIsRunning    && !Thread.interrupted())
                if(mSID >= 0 && mAVChannel.getAVIndex() >= 0)
                {
                    nReadSize = ubia_UBICAVAPIs.avRecvFrameData2(
                            this.mAVChannel.getAVIndex(), videoBuffer,
                            videoBuffer.length, outFrmSize, pFrmInfoBuf,
                            frameInfo, FRAME_INFO_SIZE, outFrmInfoBufSize,
                            frameNumber);


                    if(nReadSize >= 0)
                    {
                        i++;


                        if (IsDownLoading) {
                            byte datas[] = new byte[nReadSize];
                            System.arraycopy(videoBuffer, 0, datas, 0, nReadSize);
                            // Filelength in bytes

                            i = Packet.byteArrayToInt_Little(frameInfo, 4);
                            map.put(i, datas);
                            IsDownLoadstatus = 1;
                            if(i%50==0)
                                Log.i("Thread",
                                        "==  Frame=i=="+i);

                            continue;
                        }
                        if (frameInfo[2] == 0x01) {
                            getFirstIFrame = true;
                        }
                        if(videoBuffer[4]==0x06 &&   0xF0 ==  (videoBuffer[5]&0xff))
                        {
//							//Get SEI Frame
//						    //int size = recvBuf[6];
                            int wifi = Packet.byteArrayToInt_Little(videoBuffer,12);
//			                LiveViewTimeStateCallbackInterface_Manager.getInstance().TimeUTCStatecallback(deviceTime);
                            deviceWifiSignal   = currentWifi(wifi);
                            //Log.i("Thread","==deviceTime=== deviceWifiSignal :"+deviceWifiSignal +"  wifi:"+wifi);
                        }
                        if (getFirstIFrame == true) {
                            AVChannel avchannel1 = mAVChannel;
                            avchannel1.VideoBPS = avchannel1.VideoBPS
                                    + outFrmSize[0];
                            byte data[] = new byte[nReadSize];
                            System.arraycopy(videoBuffer, 0, data, 0, nReadSize);
                            AVFrame avFrame = new AVFrame(frameNumber[0],
                                    (byte) 0, frameInfo, data, nReadSize);
                            short codecId = avFrame.getCodecId();
                            byte onlinrnumber = avFrame.getOnlineNum();
                            if(avFrame.getinfoValidBit()){
                                recordstatus = avFrame.getRecordstatus();
                            }

                            if((avFrame.getCurrentplaySeq()==(currentplaySeq&0xff) && avFrame.getVarbit()==8) ||avFrame.getVarbit()!=8){

                                nRecvFrmPreSec = 1 + nRecvFrmPreSec;
                                localFramNo++;
                                avFrame.setCam_index((int) localFramNo);
//								  Utils.LOGD("解码更新    开始数据进入队列>>>>>>>> i帧："+frameInfo[2] );
                                mAVChannel.VideoFrameQueue.addLast(avFrame);
                            }else{
                                try {
                                    Thread.sleep(32L);
                                    AVAPIs.UBIC_avClientCleanAudioBuf(0);
                                    mAVChannel.AudioFrameQueue.removeAll();
//		    						Log.i("Thread",
//											"avFrame.getCurrentplaySeq():"+avFrame.getCurrentplaySeq()+"=======不相等，等待相同操作号包========="+(currentplaySeq&0xff)+":currentplaySeq");
                                    continue;
                                } catch (InterruptedException e) {
                                    //e.printStackTrace();
                                    // break;
                                }
                            }
//							Log.i("Thread",
//									"mAVChannel.VideoFrameQueue.addLast(avFrame);avFrame.getTimeStamp()="+avFrame.getTimeStamp()+"currentplaySeq="+currentplaySeq+"  currentplaySeq="+avFrame.getCurrentplaySeq()+"  mAVChannel.VideoFrameQueue.getCount():"+mAVChannel.VideoFrameQueue.getCount());
                            isPlayback  =( ((onlinrnumber|avFrame.getVarbit()<<4)&0xf0)>>4)==8?true:false;

                            for (int j = 0; j < mIOTCListeners.size(); j++) {
                                if(  avFrame.getVarbit()==8 ){

                                    ((IRegisterIOTCListener) mIOTCListeners.get(j))
                                            .receiveFrameInfo(
                                                    Camera.this,
                                                    mAVChannel.getChannel(),
                                                    (8 * (mAVChannel.AudioBPS + mAVChannel.VideoBPS)) / 1024,
                                                    mAVChannel.VideoFPS,
                                                    onlinrnumber|avFrame.getVarbit()<<4, avFrame  ,
                                                    avFrame.getTimeStamp());

                                    isPlayback = true;
                                }  else  {
                                    isPlayback = false;

                                    ((IRegisterIOTCListener) mIOTCListeners.get(j))
                                            .receiveFrameInfo(
                                                    Camera.this,
                                                    mAVChannel.getChannel(),
                                                    (8 * (mAVChannel.AudioBPS + mAVChannel.VideoBPS)) / 1024,
                                                    mAVChannel.VideoFPS,
                                                    onlinrnumber|avFrame.getVarbit()<<4, avFrame ,
                                                    avFrame.getTimeStamp());
                                }
                            }
                        } else {
                            setStartisIFrame(false);
                            Log.i("Thread",
                                    "==Drop P-Frame or UnComplete Frame===");
                        }


                    } else if (nReadSize == ubia_UBICAVAPIs.AV_ER_DATA_NOREADY) {
                        try {
                            Thread.sleep(32L);
                            continue;
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                            // break;
                        }
                    } else if (nReadSize == ubia_UBICAVAPIs.AV_ER_LOSED_THIS_FRAME) {
                        // lostFrame = true;
                        // lostCount++;
                        Log.i("IOTCamera", "AV_ER_LOSED_THIS_FRAME");
                        // ubia_UBICAVAPIs.avSendIOCtrl(this.mAVChannel.getAVIndex(),
                        // IOTYPE_USER_IPCAM_I_FRAME_REQ, iFrameRequest, 8);
                        lostFrameNumber++;
                        i++;
                        getFirstIFrame = false;

                        SystemClock.sleep(5);
                        continue;
                    } else if (nReadSize == ubia_UBICAVAPIs.AV_ER_INCOMPLETE_FRAME) {
                        Log.i("IOTCamera", "AV_ER_INCOMPLETE_FRAME");
                        // lostFrame = true;
                        // lostCount++;
                        lostFrameNumber++;
                        i++;
                        getFirstIFrame = false;
                        mAVChannel.VideoBPS += outBufSize[0];
                        SystemClock.sleep(5);
                        continue;
                    } else if (nReadSize == ubia_UBICAVAPIs.AV_ER_SESSION_CLOSE_BY_REMOTE) {
                        System.out.printf("[%s] AV_ER_SESSION_CLOSE_BY_REMOTE\n",
                                Thread.currentThread().getName());
                        Log.i("IOTCamera", "AV_ER_SESSION_CLOSE_BY_REMOTE");
                        getFirstIFrame = false;
                        continue;
                        // closeCamera(mAVChannel, ret);
                        // break;
                    } else if (nReadSize == ubia_UBICAVAPIs.AV_ER_REMOTE_TIMEOUT_DISCONNECT) {
                        System.out.printf("[%s] AV_ER_REMOTE_TIMEOUT_DISCONNECT\n",
                                Thread.currentThread().getName());
                        Log.i("IOTCamera", "AV_ER_REMOTE_TIMEOUT_DISCONNECT");
                        getFirstIFrame = false;
                        SystemClock.sleep(5);
                        continue;
                        // closeCamera(mAVChannel, ret);
                        // break;
                    } else if (nReadSize == ubia_UBICAVAPIs.AV_ER_INVALID_SID) {
                        Log.i("IOTCamera", "AV_ER_INVALID_SID");
                        // closeCamera(mAVChannel, ret);
                        // break;
                        SystemClock.sleep(5);
                        continue;
                    }else if(nReadSize == -20003) {


                    }

//                    Log.i("IOTCamera", "AV_ "+nReadSize);
                    try  {
                        Thread.sleep(40L);
                    }
                    catch(InterruptedException e)
                    {
                        //e.printStackTrace();
                    }
                }

            mAVChannel.VideoFrameQueue.removeAll();
            if(mSID >= 0 && mAVChannel.getAVIndex() >= 0)
                mAVChannel.IOCtrlQueue.Enqueue(mAVChannel.getAVIndex(), 767, Packet.intToByteArray_Little(mCamIndex));
            frameInfo = null ;
            videoBuffer= null ;
            System.gc();
            Log.i("IOTCamera", "===ThreadRecvVideo exit===");
        }

        public boolean isGetFirstIFrame() {
            return getFirstIFrame;
        }

        public void setGetFirstIFrame(boolean getFirstIFrame) {
            this.getFirstIFrame = getFirstIFrame;
        }

        private static final int MAX_BUF_SIZE = 0x2a3000;
        private boolean bIsRunning;
        private AVChannel mAVChannel;
        private boolean avNoClearBuf;
        private boolean getFirstIFrame;


        public ThreadRecvVideo2(AVChannel channel, boolean noClearBuf)
        {

            bIsRunning = false;
            avNoClearBuf = false;
            getFirstIFrame = true;
            mAVChannel = channel;
            avNoClearBuf = noClearBuf;
        }
    }

    private class ThreadSendAudio extends AudioProcessThread
    {
        @Override
        public int UserInit() {
            int trycount =0;
            while(mSID<0){
                SystemClock.sleep(300);
                trycount ++;
                if(trycount>15){
                    break;
                }
            }
            if(mSID <0) {
                Log.e("audio_dbg","ThreadSendAudio exit of mSID"+mSID);
                return -1;
            }


            chIndexForSendAudio = ubia_UBICAPIs.IOTC_Session_Get_Free_Channel(mSID);
            Camera.this.sendIOCtrl(this.mAVChannel.mChannel, 848,
                    AVIOCTRLDEFs.SMsgAVIoctrlAVStream
                            .parseContent(this.chIndexForSendAudio));
            avIndexForSendAudio = ubia_UBICAVAPIs.avServStart(mSID, "admin".getBytes(), "123456".getBytes(), 60L, 0L, chIndexForSendAudio);
            if(avIndexForSendAudio < 0){
                Log.e("audio_dbg","ThreadSendAudio exit of avServStart ret:"+avIndexForSendAudio);
                return -1;
            }
            SetAudioData( 16000, audio_sample_size); //480
            SetUseWebRtcAecm( 4, -1 );
//            SetUseSpeexAec( 500 );

            SetSpeexPreprocessor( 1,
                    1,
                    -200,
                    1,
                    80,
                    65,
                    1,
                    30000,
                    1,
                    -200,
                    -200 );

//            m_pclAudioTalkLinkedList = new LinkedList<short[]>();
            EncG726.g726_enc_state_create((byte)2, (byte)2);
            return 0;
        }
        @Override
        public int UserProcess() {
            // TODO Auto-generated method stub
            return 0;
        }
        @Override
        public void UserDestory() {
            // TODO Auto-generated method stub

        }
        @Override
        public void UserReadAudioInputDataFrame(
                short[] pszi16PcmAudioInputDataFrame,
                short[] pszi16PcmAudioResultDataFrame, int i32VoiceActivityStatus,
                byte[] pszi8SpeexAudioInputDataFrame,
                int i32SpeexAudioInputDataFrameSize,
                int i32SpeexAudioInputDataFrameIsNeedTrans) {
            short p_pszi16PcmAudioInputDataFrame[] = new short[pszi16PcmAudioResultDataFrame.length];
            byte outG726Buf[] = new byte[380];
            long outG726BufLen[] = new long[1];
            byte inPCMBuf[] = new byte[960];
            for( int p_i32Temp = 0; p_i32Temp < pszi16PcmAudioResultDataFrame.length; p_i32Temp++ )
            {
                p_pszi16PcmAudioInputDataFrame[p_i32Temp] = pszi16PcmAudioResultDataFrame[p_i32Temp];
                System.arraycopy( Packet.shortToByteArray_Little(pszi16PcmAudioResultDataFrame[p_i32Temp]), 0, inPCMBuf, p_i32Temp*2, 2);
            }
            byte flag = 2;
            byte frameInfo[] = AVIOCTRLDEFs.SFrameInfo.parseContent((short)143, flag, (byte)0, (byte)0, (int)System.currentTimeMillis());
            int encodeBytes = EncG726.g726_encode(inPCMBuf, audio_sample_size*2, outG726Buf, outG726BufLen);
            ubia_UBICAVAPIs.avSendAudioData(avIndexForSendAudio, outG726Buf, (int)outG726BufLen[0], frameInfo, 16);




        }
        @Override
        public void UserWriteAudioOutputDataFrame(
                short[] pszi16PcmAudioOutputDataFrame,
                byte[] p_pszi8SpeexAudioInputDataFrame,
                int[] p_pszi32SpeexAudioInputDataFrameSize) {
            short p_pszi16PcmAudioInputDataFrame[];
            int p_i32Temp;
            try {


                if( m_pclAudioReceiveLinkedList.size() > 0 )
                {
                    synchronized( m_pclAudioReceiveLinkedList )
                    {
                        p_pszi16PcmAudioInputDataFrame = m_pclAudioReceiveLinkedList.getFirst();
                        m_pclAudioReceiveLinkedList.removeFirst();
                    }

                    for( p_i32Temp = 0; p_i32Temp < p_pszi16PcmAudioInputDataFrame.length; p_i32Temp++ )
                        pszi16PcmAudioOutputDataFrame[p_i32Temp] = p_pszi16PcmAudioInputDataFrame[p_i32Temp];
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        public void stopThread()
        {
            m_i32ExitFlag = 1;
            Log.i("IOTCamera", "=== ThreadSendAudio exit   ===");
            if(mSID >= 0 && chIndexForSendAudio >= 0)
            {
                ubia_UBICAVAPIs.avServExit(mSID, chIndexForSendAudio);
                sendIOCtrl(mAVChannel.mChannel, 849, AVIOCTRLDEFs.SMsgAVIoctrlAVStream.parseContent(chIndexForSendAudio));
            }
            m_bIsRunning = false;
            StartTalk = false;
            m_i32ExitFlag = 1;
        }


        private boolean m_bIsRunning;
        private static final int SAMPLE_RATE_IN_HZ = 8000;
        private int avIndexForSendAudio;
        private int chIndexForSendAudio;
        private AVChannel mAVChannel;


        public ThreadSendAudio(AVChannel ch)
        {

            m_bIsRunning = false;
            avIndexForSendAudio = -1;
            chIndexForSendAudio = -1;
            mAVChannel = null;
            mAVChannel = ch;
        }


    }

    private class ThreadSendIOCtrl extends Thread
    {

        public void stopThread()
        {
            bIsRunning = false;
            if(mAVChannel.getAVIndex() >= 0)
            {
                Log.i("IOTCamera", (new StringBuilder("avSendIOCtrlExit(")).append(mAVChannel.getAVIndex()).append(")").toString());
                ubia_UBICAVAPIs.avSendIOCtrlExit(mAVChannel.getAVIndex());
            }
        }

        public void run()
        {
            for(bIsRunning = true; bIsRunning && (mSID < 0 || mAVChannel.getAVIndex() < 0);)
                try
                {
                    synchronized(mWaitObjectForConnected)
                    {
                        mWaitObjectForConnected.wait(1000L);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            if(bIsRunning && mSID >= 0 && mAVChannel.getAVIndex() >= 0)
            {
                int nDelayTime_ms = 0;
                //  ubia_UBICAVAPIs.avSendIOCtrl(mAVChannel.getAVIndex(), 255, Packet.intToByteArray_Little(nDelayTime_ms), 4);
//                Log.i("IOTCamera", (new StringBuilder("avSendIOCtrl(")).append(mAVChannel.getAVIndex()).append(", 0x").append(Integer.toHexString(255)).append(", ").append(Camera.getHex(Packet.intToByteArray_Little(nDelayTime_ms), 4)).append(")").toString());
            }
            while(bIsRunning  && !Thread.interrupted())
                if(mSID >= 0 &&mAVChannel!=null &&mAVChannel.IOCtrlQueue!=null &&  mAVChannel.getAVIndex() >= 0 && !mAVChannel.IOCtrlQueue.isEmpty())
                {
                    IOCtrlQueue.IOCtrlSet data = mAVChannel.IOCtrlQueue.Dequeue();
                    if(bIsRunning && data != null)
                    {
                        if(data.IOCtrlType==0x1ff && data.IOCtrlBuf[5]==1 ){
                            if(data.IOCtrlBuf[12]==AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START||data.IOCtrlBuf[12]==AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_SEEKTIME)
                            {

                                isPlayback = true;
                                returnAudio = true;//返回音频重新取
                                setControlCountReady(false);

                                if(mAVChannel!=null){
                                    mAVChannel.AudioFrameQueue.removeAll();
                                    mAVChannel.VideoFrameQueue.removeAll();
                                    if(mAVChannel.threadRecvVideo!=null)
                                        mAVChannel.threadRecvVideo. setGetFirstIFrame(false) ;
                                }
                                ClearBuf(0);
                            }
                            if(data.IOCtrlBuf[12]==AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_PAUSE)
                                setControlCountReady(true);
                            if(data.IOCtrlBuf[12]==AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_RESUME)
                                setControlCountReady(false);
                            Log.e("","1FF， 视频控制命令！！1ff  data.IOCtrlBuf[20]:"+data.IOCtrlBuf[20]);
                        }else 	if(data.IOCtrlType==0x1ff && data.IOCtrlBuf[5]== 0 ){

                            isPlayback = false;
                            returnAudio = true;//返回音频重新取
                            if(mAVChannel!=null){
                                mAVChannel.AudioFrameQueue.removeAll();
                                mAVChannel.VideoFrameQueue.removeAll();
                                if(mAVChannel.threadRecvVideo!=null)
                                    mAVChannel.threadRecvVideo. setGetFirstIFrame(false) ;
                            }
                            ClearBuf(0);
                        }
                        int ret = ubia_UBICAVAPIs.avSendIOCtrl(mAVChannel.getAVIndex(), data.IOCtrlType, data.IOCtrlBuf, data.IOCtrlBuf.length);
                        if(ret >= 0)
                            Log.i("IOTCamera", (new StringBuilder("avSendIOCtrl(")).append(mAVChannel.getAVIndex()).append(", 0x").append(Integer.toHexString(data.IOCtrlType)).append(", ").append(Camera.getHex(data.IOCtrlBuf, data.IOCtrlBuf.length)).append(")").toString());
                        else
                            Log.i("IOTCamera", (new StringBuilder("avSendIOCtrl failed : ")).append(ret).toString());
                    }
                    SystemClock.sleep(2);
                } else
                {
                    try
                    {
                        Thread.sleep(50L);
                    }
                    catch(InterruptedException e)
                    {
                        //e.printStackTrace();
                    }
                }
            Log.i("IOTCamera", "===ThreadSendIOCtrl exit===");
        }

        private boolean bIsRunning;
        private AVChannel mAVChannel;


        public ThreadSendIOCtrl(AVChannel channel)
        {

            bIsRunning = false;
            mAVChannel = channel;
        }
    }

    private class ThreadStartDev extends Thread
    {

        public void stopThread()
        {
            mIsRunning = false;
            isConected = false;
            if (mSID >= 0) {
                Log.i("Thread",
                        (new StringBuilder("avClientExit(")).append(mSID)
                                .append(", ").append(mAVChannel.getChannel())
                                .append(")").toString());
                ubia_UBICAVAPIs.avClientExit(mSID, mAVChannel.getChannel());
            }
            synchronized(mWaitObject)
            {
                mWaitObject.notify();
            }
        }

        public void run()
        {
            mIsRunning = true;
            int avIndex = -1;
            while(mIsRunning  && !Thread.interrupted())
            {
                if(mSID < 0)
                {
                    try
                    {
                        synchronized(mWaitObjectForConnected)
                        {
                            mWaitObjectForConnected.wait(100L);
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    continue;
                }
//                for(int i = 0; i < mIOTCListeners.size(); i++)
//                {
//                    IRegisterIOTCListener listener = (IRegisterIOTCListener)mIOTCListeners.get(i);
//                    listener.receiveChannelInfo(Camera.this, mAVChannel.getChannel(), 1);
//                }

                long nServType[] = new long[1];
                long servType = nServType[0];
                long[] pservTypes = new long[1];
                nServType[0] = -1L;
                avIndex = ubia_UBICAVAPIs.avClientStart2(Camera.this.mSID,
                        this.mAVChannel.getViewAcc(), this.mAVChannel.getViewPwd(),
                        30L, pservTypes, this.mAVChannel.getChannel(),
                        Camera.this.bResend);
                Camera.this.tempAvIndex = avIndex;
                tempAvIndex = avIndex;
                Log.i("IOTCamera", (new StringBuilder("avClientStart2(")).append(mAVChannel.getChannel()).append(", ").append(mAVChannel.getViewAcc()).append(", ").append(mAVChannel.getViewPwd()).append(") in Session(").append(mSID).append(") returns ").append(avIndex).append(" bResend = ").append(bResend[0]).toString());
                if(avIndex >= 0)
                {
                    hardware_pkg = (int) ((pservTypes[0] >>> 16) & 0xFFFF);
                    new DatabaseManager(UbiaApplication.getInstance().getApplicationContext()).updateDeviceHardware_pkgByUID( mDevUID,hardware_pkg );

                    Log.i("Thread", "avClientStart2(" + this.mAVChannel.getChannel()
                            + ", " + this.mAVChannel.getViewAcc() + ", "
                            + this.mAVChannel.getViewPwd() + ") in Session("
                            + Camera.this.mSID + ") returns " + avIndex + " bResend = "
                            + Camera.this.bResend[0]+"   hardware_pkg:"+hardware_pkg);

                    mAVChannel.setAVIndex(avIndex);
                    mAVChannel.setServiceType(servType);
//                    for(int i = 0; i < mIOTCListeners.size(); i++)
//                    {
//                        IRegisterIOTCListener listener = (IRegisterIOTCListener)mIOTCListeners.get(i);
//                        listener.receiveChannelInfo(Camera.this, mAVChannel.getChannel(), 2);
//                    }
                    DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mDevUID, 0, MainCameraFragment.CONNSTATUS_STARTDEVICECLIENT);
                    break;
                }
                if(avIndex == -20016 || avIndex == -20011)
                {
//                    for(int i = 0; i < mIOTCListeners.size(); i++)
//                    {
//                        IRegisterIOTCListener listener = (IRegisterIOTCListener)mIOTCListeners.get(i);
//                        listener.receiveChannelInfo(Camera.this, mAVChannel.getChannel(), 6);
//                    }
                    DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mDevUID, 0, MainCameraFragment.CONNSTATUS_RECONNECTION);
                    continue;
                }
                if(avIndex == -20009)
                {
//                    for(int i = 0; i < mIOTCListeners.size(); i++)
//                    {
//                        IRegisterIOTCListener listener = (IRegisterIOTCListener)mIOTCListeners.get(i);
//                        listener.receiveChannelInfo(Camera.this, mAVChannel.getChannel(), 5);
//                    }
                    DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mDevUID, 0, MainCameraFragment.CONNSTATUS_WRONG_PASSWORD);
                    break;
                }
                try
                {
                    synchronized(mWaitObject)
                    {
                        mWaitObject.wait(1000L);
                    }
                    DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mDevUID, 0, MainCameraFragment.CONNSTATUS_DISCONNECTED);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            Log.i("IOTCamera", "===ThreadStartDev exit==="+avIndex);
        }

        private boolean mIsRunning;
        private AVChannel mAVChannel;
        private Object mWaitObject;


        public ThreadStartDev(AVChannel channel)
        {

            mIsRunning = false;
            mWaitObject = new Object();
            mAVChannel = channel;
        }
    }

    private UBICAPIs ubia_UBICAPIs = null;
    private UBICAVAPIs ubia_UBICAVAPIs = null;
    private int AudioFrameBit = 16000;

    /**AEC TEST*/
    private static final int CHANNELS = 1;

    // Default audio data format is PCM 16 bit per sample.
    // Guaranteed to be supported by all devices.
    private static final int BITS_PER_SAMPLE = 16;

    private static final int SAMPLE_RATE = 16000;
    // We ask for a native buffer size of BUFFER_SIZE_FACTOR * (minimum required
    // buffer size). The extra space is allocated to guard against glitches under
    // high load.
    private static final int BUFFER_SIZE_FACTOR = 2;
    private static final int CALLBACK_BUFFER_SIZE_MS = 10;
    // Requested size of each recorded buffer provided to the client.

    // Average number of callbacks per second.
    private static final int BUFFERS_PER_SECOND = 1000 / CALLBACK_BUFFER_SIZE_MS;
    private static final int AEC_BUFFER_SIZE_MS = 10;
    private static final int AEC_LOOP_COUNT = CALLBACK_BUFFER_SIZE_MS / AEC_BUFFER_SIZE_MS;

    private static final int JITTER_STEP_SIZE = 480;
    private final int buffer_count = 15;

    public Camera()
    {
        mThreadConnectDev = null;
        mThreadChkDevStatus = null;
        mThreadSendAudio = null;
        nGet_SID = -1;
        mSID = -1;
        mSessionMode = -1;
        bResend = new int[1];
        tempAvIndex = -1;
        mInitAudio = false;
//        mAudioTrack = null;
        mCamIndex = 0;
        mEnableDither = true;
        mIOTCListeners = Collections.synchronizedList(new Vector());
        mAVChannels = Collections.synchronizedList(new Vector());
        mDevUID = "";
        mDevPwd = "";
        strSDPath = Environment.getExternalStorageDirectory().toString();
        ubia_UBICAPIs = new UBICAPIs();
        ubia_UBICAVAPIs = new UBICAVAPIs();

//	     for(int i = 0 ; i < buffer_count; ++i){
//	            short[] a = new short[JITTER_STEP_SIZE];
//	            try {
//	                _receveQueue.Consumer_Put(a);
//	            }catch (InterruptedException e){}
//	     }

        int ret = -1;
//         try {
//
//             _apm = new Apm( false,false, false,false,false, false,false);
//
//             ret = _apm.HighPassFilter(false);
//
////             if (vm.getAecPC())
//             {
//                 ret = _apm.AECClockDriftCompensation(true);
//                 ret = _apm.AECSetSuppressionLevel(Apm.AEC_SuppressionLevel.values()[_aecPCLevel]);
//                 ret = _apm.AEC(true);
//             }
//         } catch (Exception ex) {
//
//             Log.e("",""+ex.getMessage());
//             return;
//         }

    }

    public int getSessionMode()
    {
        return mSessionMode;
    }

    public int getMSID()
    {
        return mSID;
    }
    public String getmDevUID() {
        return mDevUID;
    }

    public void setmDevUID(String mDevUID) {
        this.mDevUID = mDevUID;
    }
    public int gettempAvIndex()
    {
        return tempAvIndex;
    }

    public int getbResend()
    {
        return bResend[0];
    }

    public int getRecvFrmPreSec()
    {
        return nRecvFrmPreSec;
    }

    public int getDispFrmPreSec()
    {
        return nDispFrmPreSec;
    }

    public long getChannelServiceType(int avChannel)
    {
        long ret = 0L;
        synchronized(mAVChannels)
        {
            for(Iterator iterator = mAVChannels.iterator(); iterator.hasNext();)
            {
                AVChannel ch = (AVChannel)iterator.next();
                if(ch.getChannel() == avChannel)
                {
                    ret = ch.getServiceType();
                    break;
                }
            }

        }
        return ret;
    }

    public boolean registerIOTCListener(IRegisterIOTCListener listener)
    {
        boolean result = false;
        if(!mIOTCListeners.contains(listener))
        {
            Log.i("IOTCamera", "register IOTC listener");
            mIOTCListeners.add(listener);
            result = true;
        }
        return result;
    }

    public boolean unregisterIOTCListener(IRegisterIOTCListener listener)
    {
        boolean result = false;
        if(mIOTCListeners.contains(listener))
        {
            Log.i("IOTCamera", "unregister IOTC listener");
            mIOTCListeners.remove(listener);
            result = true;
        }
        return result;
    }



    public static void setMaxCameraLimit(int limit)
    {
        mDefaultMaxCameraLimit = limit;
    }

    public static synchronized int init()
    {
        if(isInit)
        {
            Log.e("", "  Camera   isInit:"+isInit);
            return 0;
        }

        IOTCAPIs.UBIC_Initialize2(0);
        AVAPIs.UBIC_avInitialize(16 * mDefaultMaxCameraLimit);
        isInit = true;

        return 0;

    }

    public static synchronized int uninit()
    {
        if(!isInit){

            AVAPIs.UBIC_avDeInitialize();
            IOTCAPIs.UBIC_DeInitialize();
            int m = UBICAVAPIs.avDeInitialize();

            int j = UBICAPIs.IOTC_DeInitialize();

            Log.i("Thread", "IOTC_DeInitialize() returns " + j);
            // $FF: Couldn't be decompiled
            isInit = false;

        }
        int nRet = 0;
//        if(mCameraCount > 0)
//        {
//            mCameraCount--;
//            if(mCameraCount == 0)
//            {
//                nRet = AVAPIs.avDeInitialize();
//                Log.i("IOTCamera", (new StringBuilder("avDeInitialize() returns ")).append(nRet).toString());
//                nRet = IOTCAPIs.IOTC_DeInitialize();
//                Log.i("IOTCamera", (new StringBuilder("IOTC_DeInitialize() returns ")).append(nRet).toString());
//            }
//        }
        return nRet;
    }

    public boolean isSessionConnected()
    {
        return mSID >= 0;
    }

    public boolean isChannelConnected(int avChannel)
    {
        boolean result = false;
        synchronized(mAVChannels)
        {
            for(Iterator iterator = mAVChannels.iterator(); iterator.hasNext();)
            {
                AVChannel ch = (AVChannel)iterator.next();
                if(avChannel == ch.getChannel())
                {
                    result = mSID >= 0 && ch.getAVIndex() >= 0;
                    break;
                }
            }

        }
        return result;
    }

    public void sendIOCtrl(int avChannel, int type, byte data[])
    {
        synchronized(mAVChannels)
        {
            try {
                for (Iterator iterator = mAVChannels.iterator(); iterator.hasNext(); ) {
                    AVChannel ch = (AVChannel) iterator.next();
                    if (ch != null && avChannel == ch.getChannel()) {
                        if(ch.IOCtrlQueue!=null){
                            ch.IOCtrlQueue.Enqueue(type, data);
                        }
                     }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void connect(String uid)
    {
        mDevUID = uid;
        if(mThreadConnectDev == null)
        {
            mThreadConnectDev = new ThreadConnectDev(0);
            mThreadConnectDev.start();
        }
        if(mThreadChkDevStatus == null)
        {
            mThreadChkDevStatus = new ThreadCheckDevStatus(null);
            mThreadChkDevStatus.start();
        }
    }

    public void connect(String uid, String pwd)
    {
        mDevUID = uid;
        mDevPwd = pwd;
        if(mThreadConnectDev == null)
        {
            mThreadConnectDev = new ThreadConnectDev(1);
            mThreadConnectDev.start();
        }
        if(mThreadChkDevStatus == null)
        {
            mThreadChkDevStatus = new ThreadCheckDevStatus(null);
            mThreadChkDevStatus.start();
        }
    }

    public void disconnect()
    {
        this.isConected = false;
        if(mAVChannels == null) return;
        synchronized(mAVChannels)
        {
            for(Iterator iterator = mAVChannels.iterator(); iterator.hasNext();)
            {
                AVChannel ch = (AVChannel)iterator.next();
                if(ch == null){
                    return;
                }
                stopSpeaking(ch.getChannel());
                if(ch.threadStartDev != null)
                    ch.threadStartDev.stopThread();
                if(ch.threadDecAudio != null)
                    ch.threadDecAudio.stopThread();
                if(ch.threadDecVideo != null)
                    ch.threadDecVideo.stopThread();
                if(ch.threadRecvAudio != null)
                    ch.threadRecvAudio.stopThread();
                if(ch.threadRecvVideo != null)
                    ch.threadRecvVideo.stopThread();
                if(ch.threadRecvIOCtrl != null)
                    ch.threadRecvIOCtrl.stopThread();
                if(ch.threadSendIOCtrl != null)
                    ch.threadSendIOCtrl.stopThread();
                if(ch.threadRecvVideo != null)
                {
                    try
                    {
                        ch.threadRecvVideo.interrupt();
                        ch.threadRecvVideo.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ch.threadRecvVideo = null;
                }
                if(ch.threadRecvAudio != null)
                {
                    try
                    {
                        ch.threadRecvAudio.interrupt();
                        ch.threadRecvAudio.join(breakJoinTime );
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ch.threadRecvAudio = null;
                }
                if(ch.threadDecAudio != null)
                {
                    try
                    {
                        ch.threadDecAudio.interrupt();
                        ch.threadDecAudio.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ch.threadDecAudio = null;
                }
                if(ch.threadDecVideo != null)
                {
                    try
                    {
                        ch.threadDecVideo.interrupt();
                        ch.threadDecVideo.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ch.threadDecVideo = null;
                }
                if(ch.threadRecvIOCtrl != null)
                {
                    try
                    {
                        ch.threadRecvIOCtrl.interrupt();
                        ch.threadRecvIOCtrl.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ch.threadRecvIOCtrl = null;
                }
                if(ch.threadSendIOCtrl != null)
                {
                    try
                    {
                        ch.threadSendIOCtrl.interrupt();
                        ch.threadSendIOCtrl.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ch.threadSendIOCtrl = null;
                }
                if(ch.threadStartDev != null && ch.threadStartDev.isAlive())
                    try
                    {
                        ch.threadStartDev.interrupt();
                        ch.threadStartDev.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                ch.threadStartDev = null;
                ch.AudioFrameQueue.removeAll();
                ch.AudioFrameQueue = null;
                ch.VideoFrameQueue.removeAll();
                ch.VideoFrameQueue = null;
                ch.IOCtrlQueue.removeAll();
                ch.IOCtrlQueue = null;
                if(ch.getAVIndex() >= 0)
                {
                    ubia_UBICAVAPIs.avClientStop(ch.getAVIndex());
                    Log.i("IOTCamera", (new StringBuilder("avClientStop(avIndex = ")).append(ch.getAVIndex()).append(")").toString());
                }
            }

        }
        mAVChannels.clear();
        synchronized(mWaitObjectForConnected)
        {
            mWaitObjectForConnected.notify();
        }
        if(mThreadChkDevStatus != null)
            mThreadChkDevStatus.stopThread();
        if(mThreadConnectDev != null)
            mThreadConnectDev.stopThread();
        if(mThreadChkDevStatus != null)
        {
            try
            {
                mThreadChkDevStatus.interrupt();
                mThreadChkDevStatus.join(breakJoinTime);
            }
            catch( Exception e)
            {
                e.printStackTrace();
            }
            mThreadChkDevStatus = null;
        }
        if (mThreadConnectDev != null) {
            try {
                if (mThreadConnectDev.isAlive()) {

                        mThreadConnectDev.interrupt();
                        mThreadConnectDev.join(breakJoinTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mThreadConnectDev = null;
        if(mSID >= 0)
        {
            ubia_UBICAPIs.IOTC_Session_Close(mSID);
            Log.i("IOTCamera", (new StringBuilder("IOTC_Session_Close(nSID = ")).append(mSID).append(")").toString());
            mSID = -1;
        }
        mSessionMode = -1;
        Log.i("IOTCamera", (new StringBuilder("disconnect IOTC_Session_Close(nSID = ")).append(mSID).append(")").toString());
    }

    public void start(int avChannel, String viewAccount, String viewPasswd)
    {
        AVChannel session = null;
        synchronized(mAVChannels)
        {
            for(Iterator iterator = mAVChannels.iterator(); iterator.hasNext();)
            {
                AVChannel ch = (AVChannel)iterator.next();
                if(ch.getChannel() == avChannel)
                {
                    session = ch;
                    break;
                }
            }

        }
        if(session == null)
        {
            AVChannel ch = new AVChannel(avChannel, viewAccount, viewPasswd);
            mAVChannels.add(ch);
            ch.threadStartDev = new ThreadStartDev(ch);
            ch.threadStartDev.start();
            ch.threadRecvIOCtrl = new ThreadRecvIOCtrl(ch);
            ch.threadRecvIOCtrl.start();
            ch.threadSendIOCtrl = new ThreadSendIOCtrl(ch);
            ch.threadSendIOCtrl.start();
        } else
        {
            if(session.threadStartDev == null)
            {
                session.threadStartDev = new ThreadStartDev(session);
                session.threadStartDev.start();
            }
            if(session.threadRecvIOCtrl == null)
            {
                session.threadRecvIOCtrl = new ThreadRecvIOCtrl(session);
                session.threadRecvIOCtrl.start();
            }
            if(session.threadSendIOCtrl == null)
            {
                session.threadSendIOCtrl = new ThreadSendIOCtrl(session);
                session.threadSendIOCtrl.start();
            }
        }
    }

    public void stop(int avChannel)
    {
        isConected = false;
        synchronized(mAVChannels)
        {
            int idx = -1;
            for(int i = 0; i < mAVChannels.size(); i++)
            {
                AVChannel ch = (AVChannel)mAVChannels.get(i);
                if(ch.getChannel() != avChannel)
                    continue;
                idx = i;
                stopSpeaking(ch.getChannel());
                if(ch.threadStartDev != null)
                    ch.threadStartDev.stopThread();
                if(ch.threadDecAudio != null)
                    ch.threadDecAudio.stopThread();
                if(ch.threadDecVideo != null)
                    ch.threadDecVideo.stopThread();
                if(ch.threadRecvAudio != null)
                    ch.threadRecvAudio.stopThread();
                if(ch.threadRecvVideo != null)
                    ch.threadRecvVideo.stopThread();
                if(ch.threadRecvIOCtrl != null)
                    ch.threadRecvIOCtrl.stopThread();
                if(ch.threadSendIOCtrl != null)
                    ch.threadSendIOCtrl.stopThread();
                if(ch.threadRecvVideo != null)
                {
                    try
                    {
                        ch.threadRecvVideo.interrupt();
                        ch.threadRecvVideo.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ch.threadRecvVideo = null;
                }
                if(ch.threadRecvAudio != null)
                {
                    try
                    {
                        ch.threadRecvAudio.interrupt();
                        ch.threadRecvAudio.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ch.threadRecvAudio = null;
                }
                if(ch.threadDecAudio != null)
                {
                    try
                    {
                        ch.threadDecAudio.interrupt();
                        ch.threadDecAudio.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ch.threadDecAudio = null;
                }
                if(ch.threadDecVideo != null)
                {
                    try
                    {
                        ch.threadDecVideo.interrupt();
                        ch.threadDecVideo.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ch.threadDecVideo = null;
                }
                if(ch.threadRecvIOCtrl != null)
                {
                    try
                    {
                        ch.threadRecvIOCtrl.interrupt();
                        ch.threadRecvIOCtrl.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ch.threadRecvIOCtrl = null;
                }
                if(ch.threadSendIOCtrl != null)
                {
                    try
                    {
                        ch.threadSendIOCtrl.interrupt();
                        ch.threadSendIOCtrl.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ch.threadSendIOCtrl = null;
                }
                if(ch.threadStartDev != null && ch.threadStartDev.isAlive())
                    try
                    {
                        ch.threadStartDev.interrupt();
                        ch.threadStartDev.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                ch.threadStartDev = null;

                if(ch.AudioFrameQueue!=null){
                    ch.AudioFrameQueue.removeAll();
                    ch.AudioFrameQueue = null;
                }

                if(ch.VideoFrameQueue!=null){
                    ch.VideoFrameQueue.removeAll();
                    ch.VideoFrameQueue = null;
                }

                if(ch.IOCtrlQueue!=null){
                    ch.IOCtrlQueue.removeAll();
                    ch.IOCtrlQueue = null;
                }

                if(ch.getAVIndex() >= 0)
                {
                    ubia_UBICAVAPIs.avClientStop(ch.getAVIndex());
                    Log.i("IOTCamera", (new StringBuilder("avClientStop(avIndex = ")).append(ch.getAVIndex()).append(")").toString());
                }
                break;
            }

            if(idx >= 0)
                mAVChannels.remove(idx);
        }
    }

    public void startShow(int avChannel, boolean avNoClearBuf)
    {
        synchronized(mAVChannels)
        {
            for(int i = 0; i < mAVChannels.size(); i++)
            {
                AVChannel ch = (AVChannel)mAVChannels.get(i);
                if(ch.getChannel() != avChannel)
                    continue;
                StartisIFrame = false;
                ch.VideoFrameQueue.removeAll();
                if(ch.threadDecVideo == null)
                {
                    ch.threadDecVideo = new ThreadDecodeVideo2(ch);
                    ch.threadDecVideo.start();
                }
                if(ch.threadRecvVideo == null)
                {
                    ch.threadRecvVideo = new ThreadRecvVideo2(ch, avNoClearBuf);
                    ch.threadRecvVideo.start();
                }

                break;
            }

        }
    }

    public void stopShow(int avChannel)
    {
        synchronized(mAVChannels)
        {
            for(int i = 0; i < mAVChannels.size(); i++)
            {
                AVChannel ch = (AVChannel)mAVChannels.get(i);

                if(ch == null){
                    continue;
                }

                if(ch.getChannel() != avChannel)
                    continue;
                StartisIFrame = false;
                if(ch.threadRecvVideo != null)
                {
                    ch.threadRecvVideo.stopThread();
                    try
                    {
                        ch.threadRecvVideo.interrupt();
                        ch.threadRecvVideo.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ch.threadRecvVideo = null;
                }
                if(ch.threadDecVideo != null)
                {
                    ch.threadDecVideo.stopThread();
                    try
                    {
                        ch.threadDecVideo.interrupt();
                        ch.threadDecVideo.join(breakJoinTime);
                    }
                    catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    ch.threadDecVideo = null;
                }
                if(ch.VideoFrameQueue != null){
                    ch.VideoFrameQueue.removeAll();
                }

                break;
            }

        }
    }

    public void startSpeaking(int avChannel)
    {
        synchronized(mAVChannels)
        {
            for(int i = 0; i < mAVChannels.size(); i++)
            {
                AVChannel ch = (AVChannel)mAVChannels.get(i);
                if(ch.getChannel() != avChannel)
                    continue;
                ch.AudioFrameQueue.removeAll();
                if(mThreadSendAudio == null)
                {
                    mThreadSendAudio = new ThreadSendAudio(ch);
                    mThreadSendAudio.start();
                    StartTalk = true;
                }
                break;
            }

        }
    }

    public void stopSpeaking(int avChannel)
    {
        StartTalk = false;
        if(mThreadSendAudio != null)
        {
            mThreadSendAudio.stopThread();
            try
            {
                mThreadSendAudio.interrupt();
                mThreadSendAudio.join(breakJoinTime);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            mThreadSendAudio = null;
        }
    }

    public void startListening(int avChannel)
    {
        synchronized(mAVChannels)
        {
            for(int i = 0; i < mAVChannels.size(); i++)
            {
                AVChannel ch = (AVChannel)mAVChannels.get(i);
                if(avChannel != ch.getChannel())
                    continue;
                ch.AudioFrameQueue.removeAll();
                if(ch.threadRecvAudio == null)
                {
                    ch.threadRecvAudio = new ThreadRecvAudio(ch);
                    ch.threadRecvAudio.start();
                }
                if(ch.threadDecAudio == null)
                {
                    ch.threadDecAudio = new ThreadDecodeAudio(ch);
                    ch.threadDecAudio.start();
                }

                break;
            }

        }
    }

    public void stopListening(int avChannel)
    {
        synchronized(mAVChannels) {
            try {
                for (int i = 0; i < mAVChannels.size(); i++) {
                    AVChannel ch = (AVChannel) mAVChannels.get(i);
                    if (ch != null) {
                        if (avChannel != ch.getChannel() || ch != null||  ch.AudioFrameQueue!=null)
                            continue;
                        if (ch.threadRecvAudio != null) {
                            ch.threadRecvAudio.stopThread();
                            try {
                                ch.threadRecvAudio.interrupt();
                                ch.threadRecvAudio.join(breakJoinTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ch.threadRecvAudio = null;
                        }
                        if (ch.threadDecAudio != null) {
                            ch.threadDecAudio.stopThread();
                            try {
                                ch.threadDecAudio.interrupt();
                                ch.threadDecAudio.join(breakJoinTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ch.threadDecAudio = null;
                        }
                        ch.AudioFrameQueue.removeAll();
                        mutePlayout = true;
                        break;
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
     }

    public Bitmap Snapshot(int avChannel)
    {
        Bitmap result = null;
        synchronized(mAVChannels)
        {
            for(int i = 0; i < mAVChannels.size(); i++)
            {
                AVChannel ch = (AVChannel)mAVChannels.get(i);
                if(avChannel != ch.getChannel())
                    continue;
                result = ch.LastFrame;
                break;
            }

        }
        return result;
    }

    private synchronized boolean audioDev_init(int sampleRateInHz, int channel, int dataBit, int codec_id )
    {
        if(!mInitAudio)
        {
            int channelConfig = 2;
            int audioFormat = 2;
            int mMinBufSize = 0;
            channelConfig = channel != 1 ? 2 : 3;
            audioFormat = dataBit != 1 ? 3 : 2;

            mMinBufSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
            if(mMinBufSize == -2 || mMinBufSize == -1)
                return false;
            try
            {
//                mAudioTrack = new AudioTrack(3, sampleRateInHz, channelConfig, audioFormat, mMinBufSize, 1);
                Log.i("IOTCamera", (new StringBuilder("init AudioTrack with SampleRate:")).append(sampleRateInHz).append(" ").append(dataBit != 1 ? String.valueOf(8) : String.valueOf(16)).append("bit ").append(channel != 1 ? "Mono" : "Stereo").toString());
            }
            catch(IllegalArgumentException iae)
            {
                iae.printStackTrace();
                return false;
            }
            if(codec_id == 141)
                DecSpeex.InitDecoder(sampleRateInHz);
            else
            if(codec_id == 142)
            {
                int bit = dataBit != 1 ? 8 : 16;
                DecMp3.InitDecoder(sampleRateInHz, bit);
            } else
            if(codec_id == 139 || codec_id == 140)
                DecADPCM.ResetDecoder();
            else
            if(codec_id == 143)
            {
                Log.v("main","sampleRateInHz ="+sampleRateInHz);

                if(sampleRateInHz==16000)
                {
                    Log.v("main","sampleRateInHz DecG726.g726_dec_state_create((byte)2, (byte)2)2222222" );
                    DecG726.g726_dec_state_create((byte)2, (byte)2);
                    AudioFrameBit = 16000;
                }
                else
                {
                    Log.v("main","sampleRateInHz  DecG726.g726_dec_state_create((byte)0, (byte)2)000000" );
                    DecG726.g726_dec_state_create((byte)0, (byte)2);
                    AudioFrameBit  = 8000;

                }

            }
            else
            if(codec_id == 138)
                DecG726.g726_dec_state_create((byte)0, (byte)2);
            else
            if(codec_id == 137)
                DecG726.g726_dec_state_create((byte)0, (byte)2);
//            mAudioTrack.setStereoVolume(1.0F, 1.0F);
//            mAudioTrack.play();
            mInitAudio = true;
            return true;
        } else
        {
            return false;
        }
    }
    public int isCMBell(){
        Log.e("","isCMBell  hardware_pkg:"+hardware_pkg);
        switch(hardware_pkg){
            case 19:
            case 20:
                return 1;
            default:
                return 0;
        }
    }
    private synchronized void audioDev_stop(int codec_id)
    {
        if(mInitAudio)
        {
            if(mAudioTrack != null)
            {
                mAudioTrack.stop();
                mAudioTrack.release();
                mAudioTrack = null;
            }
            if(codec_id == 141)
                DecSpeex.UninitDecoder();
            else
            if(codec_id == 142)
                DecMp3.UninitDecoder();
            else
            if(codec_id == 143)
                DecG726.g726_dec_state_destroy();
            mInitAudio = false;
        }
    }

    static String getHex(byte raw[], int size)
    {
        if(raw == null)
            return null;
        StringBuilder hex = new StringBuilder(2 * raw.length);
        int len = 0;
        byte abyte0[];
        int j = (abyte0 = raw).length;
        for(int i = 0; i < j; i++)
        {
            byte b = abyte0[i];
            hex.append("0123456789ABCDEF".charAt((b & 0xf0) >> 4)).append("0123456789ABCDEF".charAt(b & 0xf)).append(" ");
            if(++len >= size)
                break;
        }

        return hex.toString();
    }

    public static final String DEFAULT_FILENAME_LOG = "IOTCamera_log.txt";
    public static final String strCLCF = "\r\n";
    protected static String strSDPath = null;
    private static volatile int mCameraCount = 0;
    private static int mDefaultMaxCameraLimit = 4;
    public static final int DEFAULT_AV_CHANNEL = 0;
    public static final int CONNECTION_STATE_NONE = 0;
    public static final int CONNECTION_STATE_CONNECTING = 1;
    public static final int CONNECTION_STATE_CONNECTED = 2;
    public static final int CONNECTION_STATE_DISCONNECTED = 3;
    public static final int CONNECTION_STATE_UNKNOWN_DEVICE = 4;
    public static final int CONNECTION_STATE_WRONG_PASSWORD = 5;
    public static final int CONNECTION_STATE_TIMEOUT = 6;
    public static final int CONNECTION_STATE_UNSUPPORTED = 7;
    public static final int CONNECTION_STATE_CONNECT_FAILED = 8;
    private final Object mWaitObjectForConnected = new Object();
    private ThreadConnectDev mThreadConnectDev;
    private ThreadCheckDevStatus mThreadChkDevStatus;
    private ThreadSendAudio mThreadSendAudio;
    private volatile int nGet_SID;
    private volatile int mSID;
    private volatile int mSessionMode;
    private volatile int bResend[];
    private volatile int nRecvFrmPreSec;
    private volatile int nDispFrmPreSec;
    private volatile int tempAvIndex;
    private boolean mInitAudio;
    private AudioTrack mAudioTrack;
    private int mCamIndex;
    public boolean mEnableDither;
    private String mDevUID;
    private String mDevPwd;
    public int deviceWifiSignal;
    public static int nFlow_total_FPS_count = 0;
    public static int nFlow_total_FPS_count_noClear = 0;
    private List mIOTCListeners;
    protected List mAVChannels;
    private static final String HEXES = "0123456789ABCDEF";
    public    GLView attachedMonitor = null;
    int receivedatasize = 0;
    int countf = 0;
    RandomAccessFile randomFile;
    long fileLength;
    Map<Integer, byte[]> map = new HashMap<Integer, byte[]>();

    public class VideoInfo {

        public int fps;
        public int videoWidth;
        public int videoHeight;

    }


    public boolean isIsDownLoading() {
        return IsDownLoading;
    }

    public void setIsDownLoading(String name, boolean isDownLoading) {
        IsDownLoading = isDownLoading;
        String recordPath = Environment
                .getExternalStorageDirectory().getAbsolutePath()
                + "/DCIM/Camera/"
                ;
        try {

            File div = new File(recordPath);

            if (!div.exists()) {
                div.mkdirs();
            }

            File loadfile = new File(div.getAbsolutePath() +"/"+ BuildConfig.FLAVOR+"_" + name);
            if (loadfile.exists()) {
                loadfile.delete();
            }
            Log.i("Thread", "===setIsDownLoading(String name=" + name);
            randomFile = new RandomAccessFile(recordPath + name, "rw");

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            Log.i("Thread", "===setIsDownLoading abnormal" );
            e1.printStackTrace();
        }


    }

    public void startRecode(int param1, boolean param2,int fps) {
        String recordPath = Environment
                .getExternalStorageDirectory().getAbsolutePath()
                + "/DCIM/Camera/";
        for (int j = 0; j < mAVChannels.size(); j++) {
            AVChannel avchannel = (AVChannel) mAVChannels.get(j);
            if (avchannel.getChannel() == param1) {

                pcmBuffer = new byte[2048*10];

                pcmBufferReadPostion =0;
                pcmBufferWritePostion =0;
                recodeHelper = new RecordHelper();
                if (!StartRecode) {

                    File div = new File(recordPath);

                    if (!div.exists()) {
                        div.mkdirs();
                    }

                    if (!recodeHelper.isSavingVideo()) {
                        String name = getDateTime();
                        VideoInfo mvideoinfo = new VideoInfo();
                        mvideoinfo.fps = fps;

                        DeviceInfo md = MainCameraFragment.getexistDevice(mDevUID);
                        int installmode =md.installmode;
                        if( installmode<0){
                            installmode = 0;
                        }
                        if( hardware_pkg<0){
                            hardware_pkg = 0;
                        }
                        String deviceParm = String.format("_%d_",installmode );
                        String deviceParm_hardpack = String.format("%d_",hardware_pkg);
                        recodeHelper.startRecord(div.getAbsolutePath()+"/"+BuildConfig.FLAVOR+"_" + name +deviceParm+deviceParm_hardpack +".mp4",
                                mvideoinfo);
                        Log.e("Thread",
                                "=== recodeHelper recodeHelper.startRecord==");
                    }
                }


            }
        }

        StartRecode = true;
        StartisIFrame = false;
    }

    public void stopRecode(int param1, boolean param2) {
        StartRecode = false;
        StartisIFrame = false;
        recodeHelper.stopRecord();
        pcmBuffer =null;

        pcmBufferReadPostion =0;
        pcmBufferWritePostion =0;
        FdkAACCodec.exitEncoder();
    }


    private String getDateTime() {
        Date d = new Date();
        //SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        String strDate = f.format(d);
        Log.d("tag", "record strDate:" + strDate);
        // return this.mDevUID + "!" + "LOD_" + strDate;
        return strDate;
    }

    public int getDownLoadfilesize() {
        return DownLoadfilesize;
    }

    public void setDownLoadfilesize(int downLoadfilesize) {

        if(downLoadfilesize<1280)
        {
            DownLoadfileblock = 1;
            return;
        }
        DownLoadfilesize = downLoadfilesize;
        DownLoadfileblock = DownLoadfilesize / 1280;
        if (DownLoadfilesize % 1280 == 0)
            DownLoadfileblock -= 1;

        Log.e("main",
                " -----DownLoadfileblock =" +DownLoadfilesize);
    }

    public int getDownLoadfileblock() {
        return DownLoadfileblock;
    }

    public void setDownLoadfileblock(int downLoadfileblock) {

        DownLoadfileblock = downLoadfileblock;

    }

    public void startDownLoad(int param1, boolean param2) {
        IsDownLoadstatus = -1;
        IsDownLoading = true;
        for (int j = 0; j < mAVChannels.size(); j++) {
            AVChannel avchannel = (AVChannel) mAVChannels.get(j);
            if (avchannel.getChannel() == param1) {
                avchannel.VideoFrameQueue.removeAll();
                if (avchannel.threadRecvVideo == null) {
                    // Log.i("Thread", "ThreadRecvVideo2 is  null!");
                    avchannel.threadRecvVideo = new ThreadRecvVideo2(avchannel,
                            param2);
                    avchannel.threadRecvVideo.start();
                } else {
                    // Log.i("Thread", "ThreadRecvVideo2 is not null!");
                }
                if (avchannel.threadWriteFile == null) {
                    avchannel.threadWriteFile = new ThreadWriteFile(avchannel);
                    avchannel.threadWriteFile.start();
                }

            }
        }


    }

    public void stopDownload(int param1) {
        Log.i("Thread", "stopDownload...............");
        IsDownLoading = false;
        IsDownLoadstatus = -1;
        for (int j = 0; j < mAVChannels.size(); j++) {
            AVChannel avchannel = (AVChannel) mAVChannels.get(j);
            if (avchannel.getChannel() != param1) {
                continue;
            }
            if (avchannel.threadRecvVideo != null) {
                avchannel.threadRecvVideo.stopThread();
                avchannel.threadRecvVideo.interrupt();
                try {
                    avchannel.threadRecvVideo.join(breakJoinTime);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                avchannel.threadRecvVideo = null;
            } else {
                // Log.i("Thread", "threadRecvVideo...............is null");
            }
            if (avchannel.threadWriteFile != null) {
                avchannel.threadWriteFile.stopThread();
                avchannel.threadWriteFile.interrupt();
                try {
                    avchannel.threadWriteFile.join(breakJoinTime);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                avchannel.threadWriteFile = null;
            } else {
                // Log.i("Thread", "threadDecVideo...............is null");
            }
            IsDownLoadstatus = 2;
            avchannel.VideoFrameQueue.removeAll();
        }

        Log.i("Thread", "stop...............");
        int j = -1;
        for (int k = 0; k < mAVChannels.size(); k++) {
            AVChannel avchannel;
            avchannel = (AVChannel) mAVChannels.get(k);
            if (avchannel.getChannel() == param1) {
                j = k;
            }
            if (j >= 0) {
                mAVChannels.remove(j);

                if (avchannel.getAVIndex() >= 0) {
                    ubia_UBICAVAPIs.avClientStop(avchannel.getAVIndex());
                    Log.i("Thread",
                            (new StringBuilder("avClientStop(avIndex = "))
                                    .append(avchannel.getAVIndex()).append(")")
                                    .toString());
                }
            }
        }
    }

    public int isIsDownLoadstatus() {
        return IsDownLoadstatus;
    }
    public byte getRecordstatus(){
        return this.recordstatus;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }


    public void setcurrentplaySeq(int currentplaySeq) {
        this.currentplaySeq = currentplaySeq;
    }

    public void ClearBuf(int param1) {

        for (int j = 0; j < mAVChannels.size(); j++) {
            AVChannel avchannel = (AVChannel) mAVChannels.get(j);
            if (avchannel.getChannel() != param1) {
                continue;
            }
            ubia_UBICAVAPIs.avClientCleanBuf(avchannel.getAVIndex());
            //ubia_UBICAVAPIs.avClientCleanVideoBuf(avchannel.getAVIndex());
            //ubia_UBICAVAPIs.avClientCleanAudioBuf(avchannel.getAVIndex());
            avchannel.AudioFrameQueue.removeAll();
            avchannel.VideoFrameQueue.removeAll();

        }
    }
    //	public int installmode = -1;//安装方式
//	public int getInstallmode() {
//		return installmode;
//	}
//
//	public void setInstallmode(int installmode) {
//		this.installmode = installmode;
//	}
    public String getRecordFilePath() {
        return recodeHelper.getRecordfilePath();
    }
    public void setStartisIFrame(boolean b) {
        //ClearBuf(0);
        StartisIFrame =  b;
        FdkAACCodec.initDecoder();
    }

    //	  LinkedList<short[]> m_pclAudioTalkLinkedList;
    LinkedList<short[]> m_pclAudioReceiveLinkedList;

    private  int currentWifi(int deviceWifiSignal){
        if(deviceWifiSignal< 0) {
            if (deviceWifiSignal > -50) {
                return 3;
            } else if (deviceWifiSignal > -60) {
                return 2;
            } else if (deviceWifiSignal > -70) {
                return 1;
            } else {
                return 0;
            }
        }
        return 4;
    }
}
