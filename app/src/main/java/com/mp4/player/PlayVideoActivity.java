package com.mp4.player;

import java.util.ArrayList;

import cn.ubia.PhotoGridActivity;
import cn.ubia.UBell.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class PlayVideoActivity extends Activity implements OnClickListener,
		OnTouchListener, OnSeekBarChangeListener {
	private int mCurrentVolume, mMaxVolume;
	private AudioManager mAudioManager = null;
	RelativeLayout control_bottom;
	private boolean isControlShow = true;
	private boolean mIsCloseVoice = false;
	ImageView stopVoice, previous, pause, next, close_palyback;
	Context mContext;
	private SeekBar seekbar;
	boolean isPause = false;
	boolean isRegFilter = false;
	TextView nowTime, totalTime;

	boolean isScroll = false;
	boolean isReject = false;
	private VideoView pView;
	private ImageView back;
	private TextView title;
	private ImageView title_img;
	private  RelativeLayout title_father;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getActionBar().hide();
		setContentView(R.layout.mp4player);
		mContext = this;
		initComponent();

		startWatcher();
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		}
		mCurrentVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		mMaxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		// 初始化静音
		mIsCloseVoice = false;
		stopVoice.setImageResource(R.drawable.btn_call_sound_out);
		if (mCurrentVolume == 0) {
			mCurrentVolume = 1;
		}
		if (mAudioManager != null) {
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
					mCurrentVolume, 0);
		}
		back = (ImageView) this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		title = (TextView) this.findViewById(R.id.title);
		title.setText(nickName);
		title_img = (ImageView) findViewById(R.id.title_img);
		title_img.setImageResource(R.drawable.record_tap_off);
		title_father = (RelativeLayout)findViewById(R.id.de_tit);
		this.findViewById(R.id.left_ll).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						PlayVideoActivity.this.finish();
					}
				});
	}

	private final static int PROGRESS_CHANGED = 0;
	private final static int HIDE_CONTROLER = 1;
	private boolean seekbarouttime = false;
	private int counti = 0;
	private int LastPosition;
	Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {

			case PROGRESS_CHANGED:
				int iDuration = pView.getDuration();

				totalTime.setText(convertTime(iDuration));
				int CurrentPosition = pView.getCurrentPosition();

				nowTime.setText(convertTime(CurrentPosition));
				if (CurrentPosition == LastPosition) {
					counti++;
				} else {
					counti = 0;
					LastPosition = CurrentPosition;
				}
				if (iDuration != CurrentPosition && counti < 10 && !isPause) {
					seekbar.setProgress(CurrentPosition);
					seekbar.setMax(iDuration);
					sendEmptyMessageDelayed(PROGRESS_CHANGED, 500);
				} else if (counti >= 10) {
					isPause = true;
					pView.pause();
					pause.setImageResource(R.drawable.playing_start);
				}
				break;

			case HIDE_CONTROLER:
				changeControl();
				break;
			}

			super.handleMessage(msg);
		}
	};

	private void startWatcher() {
		// mPhoneWatcher = new PhoneWatcher(mContext);
		// mPhoneWatcher.setOnCommingCallListener(new
		// PhoneWatcher.OnCommingCallListener(){
		//
		// @Override
		// public void onCommingCall() {
		// // TODO Auto-generated method stub
		// reject();
		// }
		//
		// });
		// mPhoneWatcher.startWatcher();
	}
 
	private void initComponent() {
		Display display = this.getWindowManager().getDefaultDisplay();
		int width = display.getWidth();

		pView = (VideoView) findViewById(R.id.videoView);

		LayoutParams laParams = (LayoutParams) pView.getLayoutParams();
		laParams.height = (int) ((width) * 0.56);
		pView.setLayoutParams(laParams);

		control_bottom = (RelativeLayout) findViewById(R.id.control_bottom);
		title_father = (RelativeLayout)findViewById(R.id.de_tit);
		pView.setControl_bottom(control_bottom,title_father);
		stopVoice = (ImageView) findViewById(R.id.close_voice);
		previous = (ImageView) findViewById(R.id.previous);
		close_palyback = (ImageView) findViewById(R.id.close_palyback);
		pause = (ImageView) findViewById(R.id.pause);
		next = (ImageView) findViewById(R.id.next);
		seekbar = (SeekBar) findViewById(R.id.seek_bar);
		nowTime = (TextView) findViewById(R.id.nowTime);
		totalTime = (TextView) findViewById(R.id.totalTime);

		stopVoice.setOnClickListener(this);
		// control_bottom.setOnTouchListener(this);
		previous.setOnClickListener(this);
		pause.setOnClickListener(this);
		next.setOnClickListener(this);
		seekbar.setOnSeekBarChangeListener(this);
		close_palyback.setOnClickListener(this);
		this.initP2PView();
	}

	private int position;
	String itemhead;
	ArrayList list2;// = bundle.getParcelableArrayList("list");
	String nickName;
	private void initP2PView() {
		// TODO Auto-generated method stub
		// ((TextView)
		// findViewById(R.id.tv_text)).setText(getIntent().getStringExtra("item"));
		Bundle bundle = this.getIntent().getExtras();
		list2 = bundle.getParcelableArrayList("list");
		position = bundle.getInt("position");
		nickName= getIntent().getStringExtra("nickName");
		itemhead = getIntent().getStringExtra("itemhead");
		pView.setVideoPath(getIntent().getStringExtra("item"));

		pView.start();
		int i = pView.getDuration();
		seekbar.setMax(i);
		totalTime.setText(convertTime(i));
		nowTime.setText(convertTime(0));
		myHandler.sendEmptyMessage(PROGRESS_CHANGED);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
			mCurrentVolume++;
			if (mCurrentVolume > mMaxVolume) {
				mCurrentVolume = mMaxVolume;
			}

			if (mCurrentVolume != 0) {
				mIsCloseVoice = false;
				stopVoice.setImageResource(R.drawable.btn_call_sound_out);
			}
			return false;
		} else if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
			mCurrentVolume--;
			if (mCurrentVolume < 0) {
				mCurrentVolume = 0;
			}

			if (mCurrentVolume == 0) {
				mIsCloseVoice = true;
				stopVoice.setImageResource(R.drawable.btn_call_sound_out);
			}

			return false;
		}

		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.control_bottom:
			changeControl();
			return true;
		}
		return false;
	}

	@Override
	public void onDestroy() {
		Log.e("myyy", "onDestroy");

		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.close_voice:
			if (mIsCloseVoice) {
				mIsCloseVoice = false;
				stopVoice.setImageResource(R.drawable.btn_call_sound_out);
				if (mCurrentVolume == 0) {
					mCurrentVolume = 1;
				}
				if (mAudioManager != null) {
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							mCurrentVolume, 0);
				}
			} else {
				mIsCloseVoice = true;
				stopVoice.setImageResource(R.drawable.btn_call_sound_out);
				if (mAudioManager != null) {
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
							0);
				}
			}
			break;
		case R.id.pause:
			if (isPause) {
				pView.start();
				pause.setImageResource(R.drawable.playing_pause);
				myHandler.sendEmptyMessage(PROGRESS_CHANGED);

			} else {
				pView.pause();
				pause.setImageResource(R.drawable.playing_start);
				myHandler.sendEmptyMessage(PROGRESS_CHANGED);

			}
			isPause = !isPause;
			break;
		case R.id.close_palyback:
			pView.stopPlayback();
			this.finish();
			break;
		case R.id.videoView:
			Log.e("PP", "R.id.videoView: on click");
			changeControl();
			break;
		case R.id.previous:

		{
			 
			int startposition = position;
			position--;
			if(position<0) position = list2.size()-1;
			String nextpath = "";
			for (int i = position; i > 0;) {
				if (startposition == i) {
					nextpath = list2.get(i).toString().substring(5) + "";
					position = i;
					break;
				}
				nextpath = list2.get(i).toString().substring(5) + "";

				if (nextpath.toUpperCase().contains(".MP4")) {
					position = i;
					break;
				} 
				i--;
				if (i<0) {
					i =list2.size();
				}

			}
			
			pView.stopPlayback(); 
			if (pView.isPlaying())
				pView.stopPlayback(); 
			pView.setVideoPath(nextpath);
			pView.start();
			int i = pView.getDuration();
			seekbar.setMax(i);
			totalTime.setText(convertTime(i));
			nowTime.setText(convertTime(0));
			pause.setImageResource(R.drawable.playing_pause);
			myHandler.sendEmptyMessage(PROGRESS_CHANGED);
			Log.e("PP", "R.id.videoView: on click previous position="
					+ position + "    " + nextpath);
		}
			isPause = false;

			break;
		case R.id.next:
			int startposition = position;
			position++;
			if(position >= list2.size()) position = 0;
			String nextpath = "";
			for (int i = position; i < list2.size();) {
				if (startposition == i) {
					nextpath = list2.get(i).toString().substring(5) + "";
					position = i;
					break;
				}
				nextpath = list2.get(i).toString().substring(5) + "";

				if (nextpath.toUpperCase().contains(".MP4")) {
					position = i;
					break;
				}

				i++;
				if (list2.size() == i) {
					i = 0;
				}

			}
			pView.stopPlayback(); 
			if (pView.isPlaying())
				pView.stopPlayback();

			pView.setVideoPath(nextpath);

			pView.start();
			int i = pView.getDuration();
			seekbar.setMax(i);
			totalTime.setText(convertTime(i));
			nowTime.setText(convertTime(0));
			myHandler.sendEmptyMessage(PROGRESS_CHANGED);
			pause.setImageResource(R.drawable.playing_pause);
			isPause = false;
			Log.e("PP", "R.id.videoView: on click next position=" + nextpath);
			break;
		}
	}

	private boolean next() {
		// TODO Auto-generated method stub
		if (position < 0)
			return true;
		else
			return false;
	}

	private boolean previous() {
		// TODO Auto-generated method stub

		if (position < list2.size() - 1)
			return true;
		else
			return false;
	}

	private void pausePlayBack() {
		// TODO Auto-generated method stub

	}

	private void startPlayBack() {
		// TODO Auto-generated method stub

	}

	public void changeControl() {
		if (isControlShow) {
			isControlShow = false;
			// Animation anim2 = AnimationUtils.loadAnimation(this,
			// R.anim.slide_out_top);
			// anim2.setDuration(300);
			// control_bottom.startAnimation(anim2);
			control_bottom.setVisibility(RelativeLayout.GONE);
			title_father.setVisibility(RelativeLayout.GONE);

		} else {
			isControlShow = true;
			control_bottom.setVisibility(RelativeLayout.VISIBLE);
			// Animation anim2 = AnimationUtils.loadAnimation(this,
			// R.anim.slide_in_bottom);
			// anim2.setDuration(300);
			// control_bottom.startAnimation(anim2);
			title_father.setVisibility(RelativeLayout.VISIBLE);
		}
	}
	
	public void onConfigurationChanged(Configuration var1) {
		super.onConfigurationChanged(var1);
		Configuration var2 = this.getResources().getConfiguration();
		if (var2.orientation == 2) {
			isControlShow = false;
			 
			control_bottom.setVisibility(RelativeLayout.GONE);
			title_father.setVisibility(RelativeLayout.GONE);
			 
			LayoutParams laParams = (LayoutParams) pView.getLayoutParams();;
		
			WindowManager wm = (WindowManager) this
                    .getSystemService(Context.WINDOW_SERVICE);
 
			int width = wm.getDefaultDisplay().getWidth();
			laParams.height = (int) ((width) * 0.56);
			pView.setLayoutParams(laParams);

		} else if (var2.orientation == 1) {
			isControlShow = true;
			control_bottom.setVisibility(RelativeLayout.VISIBLE); 
			title_father.setVisibility(RelativeLayout.VISIBLE);
			LayoutParams laParams = (LayoutParams) pView.getLayoutParams();;
			
			WindowManager wm = (WindowManager) this
                    .getSystemService(Context.WINDOW_SERVICE);
 
			int width = wm.getDefaultDisplay().getWidth();
			laParams.height = (int) ((width) * 0.56);
			pView.setLayoutParams(laParams);
			//竖屏
			return;
		}

	}
	
	// @Override
	// public boolean onTouch(View arg0, MotionEvent event) {
	// // TODO Auto-generated method stub
	// switch(arg0.getId()){
	// case R.id.control_bottom:
	// return true;
	// }
	// return false;
	// }

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean fromUser) {
		// TODO Auto-generated method stub

		Log.e("playback", "onProgressChanged arg1:" + arg1 + " fromUser:"
				+ fromUser);
		nowTime.setText(convertTime(arg1));

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		isScroll = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		this.jump(arg0.getProgress());
		isScroll = false;
	}

	private void jump(int progress) {
		// TODO Auto-generated method stub

		pView.seekTo(progress);
	}

	public String convertTime(int i) {

		i /= 1000;
		int minute = i / 60;
		int hour = minute / 60;
		int second = i % 60;
		minute %= 60;
		return String.format("%02d:%02d:%02d", hour, minute, second);

	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), R.string.page29_exit_app_warn,
						Toast.LENGTH_SHORT).show();
				// T.showShort(mContext,R.string.Press_again_exit);
				exitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
