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
     /*   int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float radio = (9.0f/16.0f);
        double surface_height = screenWidth * (radio);
        RelativeLayout.LayoutParams paramsrl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, (int) ((int) screenWidth * radio));
        if( play_monitor!=null  )
            play_monitor.setLayoutParams(paramsrl);*/
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
           /* if(mediaPlayer.getVideoWidth()!=0 && mediaPlayer.getVideoHeight()!=0) {
                float radio = 9.0f/16.0f;//(((float)(mediaPlayer.getVideoHeight())) / ((float)(mediaPlayer.getVideoWidth())));
                double surface_height = screenWidth * (radio);
                RelativeLayout.LayoutParams paramsrl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, (int) ((int) screenWidth * radio));
                if (play_monitor != null)
                    play_monitor.setLayoutParams(paramsrl);
                Log.e("","mediaPlayer.getVideoHeight():"+mediaPlayer.getVideoHeight() +"   (mediaPlayer.getVideoWidth():"+(mediaPlayer.getVideoWidth()));
            }*/
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
