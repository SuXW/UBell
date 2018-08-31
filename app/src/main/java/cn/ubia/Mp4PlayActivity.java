package cn.ubia;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.decoder.xiaomi.H264Decoder;
import com.mp4.Mp4Reader;
import com.ubia.IOTC.FdkAACCodec;
import com.ubia.IOTC.Monitor;
import com.ubia.IOTC.Packet;
import com.ubia.vr.SurfaceDecoder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.interfaceManager.LiveViewTimeStateCallbackInterface_Manager;
import cn.ubia.manager.CameraManagerment;

/**
 * Created by Steven.lin on 2018/4/23.
 */
public class Mp4PlayActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

    private ImageView back;

    private String mDevUid;
    private DeviceInfo mDevice;
    private String mp4Path = "";
    private Date recordTime;
    private Date newRecordTime;
    int screenWidth;
    private ImageView play_btn;

    private MediaPlayer mediaPlayer;
    private SeekBar time_seek;
    private Monitor play_monitor;
    private boolean isPlaying;

    private TextView current_time_txt, total_time_txt, record_time_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.play_mp4_activity);
        mDevUid = getIntent().getStringExtra("dev_uid");
        this.mDevice = MainCameraFragment.getexistDevice(mDevUid);
        mp4Path = getIntent().getStringExtra("path");
        recordTime = (Date) getIntent().getSerializableExtra("fileDate");
        initView();
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    process(mp4Path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/

    }


    public void onResume(){
        super.onResume();
        play(0);
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setBackgroundResource(R.drawable.ic_action_left);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        ((TextView) findViewById(R.id.title)).setText(mDevice.nickName);
        play_btn = (ImageView) findViewById(R.id.play_btn);
        play_btn.setOnClickListener(this);
        play_monitor = (Monitor) findViewById(R.id.play_monitor);
        /*play_monitor.attachCamera(CameraManagerment.getInstance().getexistCamera(mDevUid), 0, mDevice.installmode, mDevice, mDevice.snapshot, true);
        play_monitor.setCameraPutModel(mDevice.installmode);
        play_monitor.setCameraHardware_pkg(mDevice.hardware_pkg);
        play_monitor.setHorizontal(false);*/
         screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float radio = (9.0f/16.0f);
        double surface_height = screenWidth * (radio);
        LinearLayout.LayoutParams paramsrl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int) ((int) screenWidth * radio));
        if( play_monitor!=null  )
            play_monitor.setLayoutParams(paramsrl);
        play_monitor.setrectCanvas(mDevice.snapshot);
        time_seek = (SeekBar) findViewById(R.id.time_seek);
        time_seek.setOnTouchListener(this);
        total_time_txt = (TextView) findViewById(R.id.total_time_txt);
        current_time_txt = (TextView) findViewById(R.id.current_time_txt);
        record_time_txt = (TextView) findViewById(R.id.record_time_txt);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.play_btn:
                if ((currentTime / 1000) >= ((totalTime / 1000) - 1))
                    play(0);
                else
                    pause();
                break;
        }
    }

    private int totalTime = 0;
    private int currentTime = 0;

    protected void play(final int msec) {
        currentTime = msec;
        File file = new File(mp4Path);
        if (!file.exists()) {
            return;
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(file.getAbsolutePath());
            if(mediaPlayer.getVideoWidth()!=0 && mediaPlayer.getVideoHeight()!=0) {
                float radio = 9.0f/16.0f;//(((float)(mediaPlayer.getVideoHeight())) / ((float)(mediaPlayer.getVideoWidth())));
                double surface_height = screenWidth * (radio);
                LinearLayout.LayoutParams paramsrl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int) ((int) screenWidth * radio));
                if (play_monitor != null)
                    play_monitor.setLayoutParams(paramsrl);
                Log.e("","mediaPlayer.getVideoHeight():"+mediaPlayer.getVideoHeight() +"   (mediaPlayer.getVideoWidth():"+(mediaPlayer.getVideoWidth()));
            }
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.setDisplay(play_monitor.getHolder());
                    mediaPlayer.start();
                    mediaPlayer.seekTo(currentTime);
                    totalTime = mediaPlayer.getDuration();
                    time_seek.setMax(totalTime);
                    mHandler.sendEmptyMessage(1001);
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                isPlaying = true;
                                int sleepSum = 1;
                                newRecordTime = recordTime;
                                mHandler.sendEmptyMessage(1004);
                                while (isPlaying) {
                                    currentTime = mediaPlayer
                                            .getCurrentPosition();
                                    mHandler.sendEmptyMessage(1002);
                                    time_seek.setProgress(currentTime);
                                    sleep(500);
                                    if (sleepSum == 2) {
                                        addOneSecond(newRecordTime);
                                        sleepSum = 1;
                                    } else {
                                        sleepSum++;
                                    }
                                }
                             } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    time_seek.setProgress(0);
                    current_time_txt.setText(timeData(0));
                    mediaPlayer.release();
                    mHandler.sendEmptyMessage(1003);
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    isPlaying = false;
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isIframe = false;
    private boolean bInitH264;
    private boolean isOneShow = true;
    int[] width = new int[1];

    int[] height = new int[1];

    int j = 0;
    private int selfFram;
    private int size = 0;

    private boolean isPlayMp4;
    Mp4Reader p_mp4Read;

    protected boolean process(String Uil) throws IOException {
        //Uil = "/storage/emulated/0/DCIM/Camera/UBell_2018-04-20_172235_2_19_.mp4";
        isPlaying = true;
        AudioTrack mAudioTrack = null;
        Log.e("", "read frame Uil :" + Uil);
        long TimeStamp[] = new long[1];
        long lastTimeStamp = 0;
        long TimeDuration[] = new long[1];
        long audioTimeStamp[] = new long[1];

        boolean iRet;
        int ReadLen[] = new int[1];
//		Mp4Reader  p_mp4Read = new Mp4Reader ();
        p_mp4Read = new Mp4Reader();
        int ret = p_mp4Read.Open(Uil);
        Log.e("", "p_mp4Read  :" + ret);
        byte[] fVideoBuffer = new byte[1024 * 256];
        SurfaceDecoder SDecoder = null;
        int sameCount = 0;
        if (!bInitH264) {
            if (SDecoder != null) {
                SDecoder.release();
            }

            SDecoder = new SurfaceDecoder();
            if (SDecoder != null) {
                SDecoder.SoftDecoderPrePare();//初始化软解
                Log.e("TESTDECODE", "LiveviewGLW create SurfaceDecoder for playback");
            }
            bInitH264 = true;
        }
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        info.presentationTimeUs = 0;
        isPlayMp4 = true;
        FdkAACCodec.exitDecoder();
        FdkAACCodec.initDecoder();
        Bitmap mp4bitmap = null;
        //play_monitor.refreshData();
        CameraManagerment.getInstance().userIPCMuteControl(mDevUid, true); //mp4关闭底层声音
        while (isPlayMp4) {
            if (!isPlaying) {
                try {
                    Thread.sleep(66);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            {
                byte G726OutBuf[] = new byte[2048];
                byte pcmBuf[] = new byte[2048];
                int readAudiolen[] = {1024};
                byte[] fAudioBuffer = new byte[1024];

                long audioTimeDuration[] = new long[1];
                iRet = p_mp4Read.GetNextAudioSample(fAudioBuffer, readAudiolen, audioTimeStamp, audioTimeDuration);
                Log.e("", "read frame error  audioTimeDuration:" + audioTimeDuration[0] + "    +iRet  " + readAudiolen[0]);

                if (readAudiolen[0] > 100) {
                    byte realData[] = new byte[2048];
                    System.arraycopy(fAudioBuffer, 0, realData, 7, readAudiolen[0]);
                    addADTStoPacket(realData, readAudiolen[0]);
                    int ret2 = FdkAACCodec.decodeFrame(G726OutBuf, 2048, realData, readAudiolen[0] + 7);
                    try {
                        if (mAudioTrack == null) {
                            mAudioTrack = new AudioTrack(3, 16000, 2, 2, 2048, 1);
                            mAudioTrack.setStereoVolume(1.0F, 1.0F);
                            mAudioTrack.play();
                        }

                        if (mAudioTrack != null)
                            mAudioTrack.write(G726OutBuf, 0, 2048);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }

            long cutime = System.currentTimeMillis();
//        	if(		TimeStamp[0]< 	audioTimeStamp[0] ) //视频跟着音频走
            iRet = p_mp4Read.GetNextVideoSample(fVideoBuffer, ReadLen, TimeStamp, TimeDuration);
            //Log.e("", "read frame error/TimeStamp[%ld]  close file iRet VideoTimeStamp:"+TimeStamp[0]+"    VideoTimeDuration:"+TimeDuration[0]+"     ,ReadLen[%d]  "+ReadLen[0]  +iRet +"   CheckIsSyncSample:"+p_mp4Read.CheckIsSyncSample());

            if (lastTimeStamp != 0 && TimeStamp[0] == lastTimeStamp) {
                sameCount++;
            }
            if (TimeStamp[0] != lastTimeStamp) {
                sameCount = 0;
            }
            lastTimeStamp = TimeStamp[0];
            if (ReadLen[0] < 4) {
                try {
                    Thread.sleep(66);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            byte[] h264 = new byte[ReadLen[0]];
            System.arraycopy(fVideoBuffer, 0, h264, 0, ReadLen[0]);
            if (ReadLen[0] > 0 && h264[4] == 0x06 && ReadLen[0] > 16) {
                long deviceTime = Packet.byteArrayToInt_Little(h264, 8);
                LiveViewTimeStateCallbackInterface_Manager.getInstance().TimeUTCStatecallback(deviceTime);
            }
            if (SDecoder != null) {
                //SDecoder.doExtract(h264, ReadLen[0], 1920, 1080, 0, play_monitor);
            }
            if (p_mp4Read.CheckIsSyncSample())
                continue;


            if (isPlayMp4) {
                Bundle var4 = new Bundle();
                //    		Log.v("","mMediaExtractor :"+mMediaExtractor);
//	        	Log.e("", "read frame error/TimeStamp[%ld]  close file iRet:"+TimeStamp[0]+"    TimeDuration:"+TimeDuration[0]+"     ,ReadLen[%d]  "+ReadLen[0]  +iRet +"   CheckIsSyncSample:"+p_mp4Read.CheckIsSyncSample());
                int refreshTime = (int) TimeStamp[0];
                if (refreshTime > (int) TimeDuration[0]) {
                    refreshTime = (int) TimeDuration[0];
                }
                var4.putInt("SampleTime", refreshTime);
                var4.putInt("duration", (int) TimeDuration[0]);
                Message var5 = this.mHandler.obtainMessage();
                var5.what = 1111;
                var5.setData(var4);
                mHandler.sendMessage(var5);
            }
            try {//帧率控制 15
                long lasttime = System.currentTimeMillis();
                int sleeptime = (int) Math.abs(60 - (lasttime - cutime));
                if (sleeptime > 60) {
                    sleeptime = 60;
                }
                Thread.sleep(sleeptime);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (TimeStamp[0] / 1000 >= TimeDuration[0] / 1000 || sameCount > 10)//文件读完
            {
                Log.e("", "read frame  文件读完 :" + ReadLen[0] + "    TimeDuration:" + TimeDuration[0]);
                break;
            }
//    		if(isPlayMp4)
//    		mMediaExtractor.advance();
        }//文件结束
//    	if(monitor!=null)
//			monitor.restartPlay();
        {
            if (mAudioTrack != null) {
                mAudioTrack.stop();
                mAudioTrack = null;
            }
            FdkAACCodec.exitDecoder();
            Log.e("FdkAACCodec", "LiveView FdkAACCodec exitDecoder");
            p_mp4Read.Close();
            p_mp4Read = null;

            H264Decoder.release();
            if (SDecoder != null)
                SDecoder.release();
            SurfaceDecoder.delloc();
            bInitH264 = false;

//          currentplaySeq++;
//        if(  MainCameraFragment.getRunningActivityName(this.getClass().getSimpleName())){//不返回直播
//          mCameraManagerment.setcurrentplaySeq(mDevUID,currentplaySeq);
//            mCameraManagerment.userIPCStart(mDevUID, mDevice.getChannelIndex(),(byte)currentplaySeq );
//            mCameraManagerment.userIPCstartAllStream(mDevUID ,true,false);
//            if(monitor!=null){
//            	monitor.setCameraPutModel(mDevice.installmode);
//            	MyCamera mCamera = mCameraManagerment.getexistCamera(mDevUID);
//            	monitor.setCameraHardware_pkg(mCamera.hardware_pkg);
//            }
//          }
//		 if(myHorizontalScrollView!=null){
//				myHorizontalScrollView.scorllToCurrentTimeAndNoRecall();
//		 }
            isPlayMp4 = false;
            fVideoBuffer = null;
            System.gc();
            {//恢复显示为0

                Bundle var4 = new Bundle();
                //    		Log.v("","mMediaExtractor :"+mMediaExtractor);
//	        	Log.e("", "read frame error/TimeStamp[%ld]  close file iRet:"+TimeStamp[0]+"    TimeDuration:"+TimeDuration[0]+"     ,ReadLen[%d]  "+ReadLen[0]  +iRet +"   CheckIsSyncSample:"+p_mp4Read.CheckIsSyncSample());
                int refreshTime = (int) 0;
                var4.putInt("SampleTime", refreshTime);
                var4.putInt("duration", (int) TimeDuration[0]);
                Message var5 = this.mHandler.obtainMessage();
                var5.what = 1118;
                var5.setData(var4);
                mHandler.sendMessage(var5);

            }
            mHandler.sendEmptyMessage(1114);


        }
        Log.e("", "read frame error  return true");

        return true;
    }

    public void onPause(){
        super.onPause();

        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    this.pause();
                }

                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    void addADTStoPacket(byte[] packet, int payloadLen) {
        int profile = 2; //AAC LC
        int freqIdx = 8; //0=96K, 1=88.2K, 2=64K  3=48K, 4=44.1KHz
        int chanCfg = 1; //CPE
        int packetLen = payloadLen + 7;
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF1; //0xF9
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >>> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >>> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >>> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
        //	Log.e("","  packetLen:"+String.format("%d",packetLen&0xff)+"  packet[0]:"+String.format("%X",packet[0]&0xff)+"  packet[1]:"+String.format("%X",packet[1]&0xff)+"  packet[2]:"+String.format("%X",packet[2]&0xff)+"  packet[3]:"+String.format("%X",packet[3]&0xff)+"  packet[4]:"+String.format("%X",packet[4]&0xff)+"    packet[5]:"+  String.format("%X", packet[5]&0xff)+"   packet[6]:"+String.format("%X",packet[6]&0xff));
    }

    protected void pause() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            play_btn.setImageDrawable(getResources().getDrawable(R.drawable.playing_pause));
            mediaPlayer.start();
            return;
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            play_btn.setImageDrawable(getResources().getDrawable(R.drawable.playing_start));
            mediaPlayer.pause();
        }

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    total_time_txt.setText(timeData(totalTime));
                    play_btn.setImageDrawable(getResources().getDrawable(R.drawable.playing_pause));
                    break;
                case 1002:
                    current_time_txt.setText(timeData(currentTime));
                    break;
                case 1003:
                    play_btn.setImageDrawable(getResources().getDrawable(R.drawable.playing_start));
                    break;
                case 1004:
                    if (newRecordTime!=null) {
                        record_time_txt.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(newRecordTime));
                    }

                    break;
            }
        }
    };

    private String timeData(int seconds) {
        seconds /= 1000;
        String tm;
        int min = seconds / 60;
        if (min < 10)
            tm = "0" + min + ":";
        else
            tm = min + ":";
        int sec = seconds % 60;
        if (sec < 10)
            tm += "0" + sec;
        else
            tm += sec;
        return tm;
    }

    public void addOneSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, 1);
        newRecordTime = calendar.getTime();
        mHandler.sendEmptyMessage(1004);
    }
}
