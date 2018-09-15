package cn.ubia;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioTrack;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import cn.ubia.UBell.BuildConfig;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.base.Constants;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.MyCamera;
import cn.ubia.bean.ZigbeeInfo;
import cn.ubia.db.DatabaseManager;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.interfaceManager.DeviceStateCallbackInterface;
import cn.ubia.interfaceManager.DeviceStateCallbackInterface_Manager;
import cn.ubia.interfaceManager.DoorCallBackInterface;
import cn.ubia.interfaceManager.DoorStateCallbackInterface_Manager;
import cn.ubia.interfaceManager.LiveViewTimeCallBackInterface;
import cn.ubia.interfaceManager.LiveViewTimeStateCallbackInterface_Manager;
import cn.ubia.interfaceManager.TimeLineTouchCallBackInterface;
import cn.ubia.interfaceManager.TimeLineTouchCallbackInterface_Manager;
import cn.ubia.interfaceManager.ZigbeeInfoCallBackInterface;
import cn.ubia.interfaceManager.ZigbeeInfoCallbackInterface_Manager;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.util.FileUtils;
import cn.ubia.util.PreferenceUtil;
import cn.ubia.util.StringUtils;
import cn.ubia.util.UbiaUtil;
import cn.ubia.widget.BatteryView;
import cn.ubia.widget.DialogUtil;
import cn.ubia.widget.DialogUtil.DialogChooseItemStringcallback;
import cn.ubia.widget.DialogUtil.Dialogcallback;
import cn.ubia.widget.MyPagerAdapter;

import com.decoder.xiaomi.H264Decoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mp4.Mp4Reader;
import com.timeline.listenview.GetTimelineBitMapCallback_Manager;
import com.timeline.listenview.GetTimelineBitMapbackInterface;
import com.timeline.listenview.NoteInfoData;
import com.timeline.listenview.RollBackToCurrentTimeCallbackInterface;
import com.timeline.listenview.TimeLinePlayCallBackInterface;
import com.ubia.IOTC.AVFrame;
import com.ubia.IOTC.AVIOCTRLDEFs;
import com.ubia.IOTC.AVIOCTRLDEFs.STimeDay;
import com.ubia.IOTC.Camera;
import com.ubia.IOTC.FdkAACCodec;
import com.ubia.IOTC.HARDWAEW_INFO;
import com.ubia.IOTC.HARDWAEW_PKG;
import com.ubia.IOTC.IRegisterIOTCListener;
import com.ubia.IOTC.Packet;
import com.ubia.IOTC.St_SInfo;
import com.ubia.vr.GLView;
import com.ubia.vr.SurfaceDecoder;
import com.ubia.vr.VRConfig;

import static cn.ubia.fragment.MainCameraFragment.CONNSTATUS_WRONG_PASSWORD;


@SuppressLint("NewApi")
public class LiveViewGLviewActivity extends BaseActivity implements ViewFactory,OnTouchListener,
		IRegisterIOTCListener, OnGestureListener,TimeLinePlayCallBackInterface , RollBackToCurrentTimeCallbackInterface ,ViewPager.OnPageChangeListener  {
	private RelativeLayout title_father;

	public static final int PHOTOGRID_REQUESTCODE = 0x321;
	private static final int BUILD_VERSION_CODES_ICE_CREAM_SANDWICH = 14;
//	private MediaExtractor mMediaExtractor;
	// private static final int OPT_MENU_ITEM_ALBUM = 1;

	// private static final int OPT_MENU_ITEM_AUDIO_IN = 4;
	// private static final int OPT_MENU_ITEM_AUDIO_OUT = 5;
	private static final int OPT_MENU_ITEM_SNAPSHOT = 3;
	private static final int OPT_MENU_ITEM_RECORD = 1;
	private static final int OPT_MENU_ITEM_AUDIOCTRL = 2;

	// private static final int OPT_MENU_ITEM_EVENTS = 5;
	private static final int OPT_MENU_ITEM_SETTING = 4;
	private static final int OPT_MENU_ITEM_RECORD_tap = 999;
	private static final int OPT_MENU_ITEM_LOCALVIDEO = 8;
	private static final int OPT_MENU_ITEM_DEVICEVIDEO = 9;
	private static final int REQUEST_CODE_ALBUM = 99;
	private static final int STS_CHANGE_CHANNEL_STREAMINFO = 99;
	private static final int STS_SNAPSHOT_SCANED = 98;
	private static final int STS_SNAPSHOT_SAVED = 198;
	private static final int DELAYSETMONITOR = 111198;
	private BitmapDrawable bg;
	private BitmapDrawable bgSplit;
	private ImageButton button_say;
	private ImageButton button_showctl;
	public int micvalue = 0;
	public int spvalue = 0;
	private static final int msgKey1 = 1;
	private TextView mTime;
	private boolean timeRunning = true;
	private Animation animationButtonSay;
	private boolean getvolume = false;
	private boolean getmic = false;
	private boolean isclick = false;
	private ImageView back;
	private TextView title,recode_time_txt,right2_tv;
	private ImageView title_img;
	private ImageView right_image5;
	private ImageView right_image4;
	private ImageView right_image3;
	private ImageView right_image2;
	private ImageView right_image;

	private static boolean isModifyPassword = false;

	ImageButton bt_top, bt_bottom, bt_left, bt_right, BtnPTZ_Auto;
	private int BatteryViewChargeRec[] = {
			R.drawable.charge01,R.drawable.charge02,R.drawable.charge03,R.drawable.charge04
	};
	private int BatteryViewRec[] = {
			R.drawable.you01,R.drawable.you01,R.drawable.you02,R.drawable.you03,R.drawable.you04,
	};
	private int WiFiViewRec[] = {
			R.drawable.wifi_00,R.drawable.wifi_01,R.drawable.wifi_02,R.drawable.wifi_03,R.drawable.full_transparent
	};
	private ImageView img_photo;
	private ImageView img_video;
	private ImageView img_lock;
	private ImageView img_mic;
	private ImageView img_record;
	private ImageView img_setting;
	private TextView txt_time,rockbacktoLive_photo,txtOnlineNumberlive,txtLowPower;
	private	int seccount = 0;
	private	long recordStartsec = 0;
	private	long recordStartseccount = 0;
	private int popwinoffset = 0;
	private boolean isTenmin;
	public  boolean isBackgroundRunning = true;
	private boolean showGridViewBitmap = false;

	private RelativeLayout seek_bar_rl;//mp4播放进度父控件
	private SeekBar mp4seekbar;
	private ImageView mp4pause ;
	private TextView nowTime_tv,totalTime_tv;
	private long seekMp4time= 0;
	private boolean onSeekbar = false;

	int isdoolbeel;
	private boolean recZigbee= false;
	private ZigbeeInfo mZigbeeInfo;

	private CameraManagerment  mCameraManagerment=CameraManagerment.getInstance();
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {

				switch (msg.what) {
					case msgKey1:

						break;
					case 2:
						if (mProgressBar != null) {
							mProgressBar.setVisibility(View.GONE);

						}
						break;
					case 98:
						if(title_father.getVisibility()==View.VISIBLE){
							title_father.setVisibility(View.GONE);
							control_bottom_new.setVisibility(View.GONE);

							right_image5.setVisibility(View.GONE);
							right_image4.setVisibility(View.GONE);
							right_image3.setVisibility(View.GONE);
							right_image2.setVisibility(View.GONE);
							right_image.setVisibility(View.GONE);
						}	else{
							title_father.setVisibility(View.VISIBLE);
							control_bottom_new.setVisibility(View.VISIBLE);

							right_image5.setVisibility(View.VISIBLE);
							right_image4.setVisibility(View.VISIBLE);
							right_image3.setVisibility(View.VISIBLE);
							right_image2.setVisibility(View.GONE);
							right_image.setVisibility(View.GONE);
						}

						break;

					default:
						break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};
	private int ConnectCount=0;
	Runnable Reconnectrun = new Runnable() {
		@Override
		public void run() {

			{
				Log.v("main", "设备重连！！ConnectCount =" + ConnectCount+"----->>"+ Thread.currentThread());
				ConnectCount++;
				if (ConnectCount > 5) {
//						Toast.makeText(LiveViewGLviewActivity.this, LiveViewGLviewActivity.this
//										.getText(R.string.page26_page8_MyCameraFragment_connstus_connection_failed), 0).show();
//						quit(-1);
//						LiveViewGLviewActivity.this.finish();
//						return;
				}
				else
					handler.postDelayed(this, 5000);
			}
			Log.v("main", "设备重连！！StopPPPP =" + ConnectCount+"----->>"+ Thread.currentThread());
			StopAudio();
			mCameraManagerment.userIPCStopAllPPP(mDevUID);
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			Log.v("main", "设备重连！！StartPPPP =" + ConnectCount+"----->>"+ Thread.currentThread());
			{
				mCameraManagerment.StartPPPP(mDevUID, mDevice.viewAccount, mDevice.viewPassword);
			}

		}
	};

	boolean ispostingconnect= false;

	private Handler handler = new Handler() {

		@SuppressLint("NewApi")
		public void handleMessage(Message var1) {
			try {
				if(isBackgroundRunning) return; //后台不处理状态变化
				byte[] databyte = var1.getData().getByteArray("data");
				int var28;
				int var2 = var1.getData().getInt("avChannel");
				St_SInfo var3 = new St_SInfo();

				switch (var1.what) {
					case DELAYSETMONITOR:
					{
						setUpMonitor(isLandorientation);
					}
					break;
					case MainCameraFragment.CONNSTATUS_CONNECTING:
					{
						LiveViewGLviewActivity.this.mConnStatus = LiveViewGLviewActivity.this
								.getText(R.string.page26_page8_connstus_connecting)
								.toString();

					}
					break;
					case MainCameraFragment.CONNSTATUS_CONNECTED:
					{
						LiveViewGLviewActivity.this.mConnStatus = LiveViewGLviewActivity.this
								.getText(R.string.page8_connstus_connected)
								.toString();

						if(ConnectCount>0){
							monitor.restartPlay();
							mCameraManagerment.userIPCstartShow(mDevUID);
							mCameraManagerment.userIPCDeviceInfo(mDevUID);
							mCameraManagerment.userIPCGetAudioOutFormat(mDevUID);
							ispostingconnect = false;
							ConnectCount = 0;
//							if(UbiaApplication.BUILD_CHECK_PLAYVOICE)
							StartAudio();
							if (mIsSpeaking) {
								mCameraManagerment.userIPCstartSpeak (mDevUID);
							}
							handler.removeCallbacks(Reconnectrun);
						}
						LiveViewGLviewActivity.this.invalidateOptionsMenu();
					}
					break;

					case MainCameraFragment.CONNSTATUS_DISCONNECTED:
						LiveViewGLviewActivity.this.mConnStatus = LiveViewGLviewActivity.this
								.getText(R.string.page8_connstus_disconnect).toString();


						LiveViewGLviewActivity.this.invalidateOptionsMenu();
						if ( !ispostingconnect)
						{
							ispostingconnect = true;

							handler.post(Reconnectrun);
						}

						break;
					case MainCameraFragment.CONNSTATUS_UNKOWN_DEVICE:
						LiveViewGLviewActivity.this.mConnStatus = LiveViewGLviewActivity.this
								.getText(R.string.page8_connstus_unknown_device)
								.toString();

						LiveViewGLviewActivity.this.invalidateOptionsMenu();
						break;
					case CONNSTATUS_WRONG_PASSWORD:
						LiveViewGLviewActivity.this.mConnStatus = LiveViewGLviewActivity.this
								.getText(R.string.page8_connstus_wrong_password)
								.toString();

						runOnUiThread(new   Runnable() {
							public void run() {
								DialogUtil.getInstance().showpwdErrorDialo(LiveViewGLviewActivity.this,new  DialogChooseItemStringcallback(){

									@Override
									public void chooseItemString(String newPassword) {

										DeviceInfo var16 =  mDevice;
										String var17;
										var17 = newPassword;

										var16.viewPassword = var17;
										// //////////////////////////////////////////////////////////////////////////
										(new DatabaseManager(LiveViewGLviewActivity.this)).updateDeviceInfoByDBID(
												mDevice.DBID, mDevice.UID, mDevice.nickName,
												"", "", "admin", mDevice.viewPassword,
												mDevice.EventNotification, mDevice.getChannelIndex(),
												mDevice.isPublic);
										if ( !ispostingconnect)
										{
											ispostingconnect = true;
											StopAudio();


											handler.post(Reconnectrun);
										}
									}

								},LiveViewGLviewActivity.this)	;
							}
						});
						break;
					case MainCameraFragment.CONNSTATUS_RECONNECTION:
						if(mProgressBar!=null)
						{
							mProgressBar.setVisibility(View.VISIBLE);
							mProgressBar.bringToFront();
						}
						else{
							mProgressBar =  (ProgressBar) LiveViewGLviewActivity.this.findViewById(R.id.MyprogressBar);
							mProgressBar.setVisibility(View.VISIBLE);
							mProgressBar.bringToFront();
						}

						if ( !ispostingconnect)
						{
							ispostingconnect = true;
							handler.post(Reconnectrun);
						}

						break;
					case MainCameraFragment.CONNSTATUS_CONNECTION_FAILED:
						LiveViewGLviewActivity.this.mConnStatus = LiveViewGLviewActivity.this
								.getText(R.string.page8_connstus_connection_failed) .toString();

						if ( !ispostingconnect)
						{
							ispostingconnect = true;
							StopAudio();


							handler.post(Reconnectrun);
						}
						LiveViewGLviewActivity.this.invalidateOptionsMenu();
						break;

					case STS_SNAPSHOT_SAVED:
						Toast.makeText(
								LiveViewGLviewActivity.this,
								LiveViewGLviewActivity.this
										.getText(R.string.page8_tips_snapshot_ok), 0)
								.show();
						break;
					case 99:
						if(mProgressBar!=null)
							mProgressBar.setVisibility(View.GONE);




						mlastVideoWidth = mVideoWidth;
						//Log.v("", "mVideoWidth:"+mVideoWidth);
//				if(isLandorientation){
//					if (right2_tv != null ) {
//						right2_tv.setVisibility(View.GONE);
//					}
//				}else
					{
						if(mVideoWidth==1920 || mVideoWidth == 1280 || mVideoWidth ==960 )
						{
							Resources res = getResources();
							String text = res.getString(R.string.AVIOCTRLDEFs_page8_hd);

							if (right2_tv == null) {
								right2_tv   = (TextView) findViewById(R.id.right2_tv);
								right2_tv.setTextColor(getResources().getColor(R.color.color_skin_color));
							}


						}else{
							Resources res = getResources();
							String text = res.getString(R.string.AVIOCTRLDEFs_page8_vga);

							if (right2_tv == null) {
								right2_tv   = (TextView) findViewById(R.id.right2_tv);
								right2_tv.setTextColor(getResources().getColor(R.color.color_skin_color));
							}

						}
					}
					if (LiveViewGLviewActivity.this.txtOnlineNumberlive != null) {
						LiveViewGLviewActivity.this.txtOnlineNumberlive.setText(getString(R.string.page8_txtOnlineNumber) + String
								.valueOf(LiveViewGLviewActivity.this.mOnlineNm));
					} else {
						Log.v("test", "onlinenumber is null");
					}
					break;
					case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETVOLUME_RESP:
						if(mProgressBar!=null)
							mProgressBar.setVisibility(View.GONE);
						var28 = Packet.byteArrayToInt_Little(databyte, 4);
						if(button_showctl!=null)
							button_showctl.setEnabled(true);
						Log.i("deviceinfo",
								"IOTYPE_USER_IPCAM_GETVOLUME_RESP..............="
										+ var28);
						if (var28 > 255 || var28 < 0)
							break;
						spvalue = var28;
						getvolume = true;

						if (getmic && getvolume && isclick) {

							SeekBar var4 = (SeekBar) VolumeView
									.findViewById(R.id.seekBarspeak);
							var4.setMax(255);
							var4.setProgress(spvalue);

							SeekBar var5 = (SeekBar) VolumeView
									.findViewById(R.id.seekBarmic);
							var5.setMax(255);

							var5.setProgress(micvalue);
							AlertDialogvar2.show();
							getvolume = false;
							getmic = false;
							isclick = false;
						}


						break;
					case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL_RESP:
					{
						int var4 = Packet.byteArrayToInt_Little(databyte, 0);
						int var5 = Packet.byteArrayToInt_Little(databyte, 4);
						switch (var4) {
							case AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_PAUSE:
								Log.v("deviceinfo", "AVIOCTRL_RECORD_PLAY_PAUSE");
								System.out.println("AVIOCTRL_RECORD_PLAY_PAUSE");
								break;
							case AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_STOP:
								System.out.println("AVIOCTRL_RECORD_PLAY_STOP");
								break;
							case AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_BACKWARD:
								break;
							default:
								break;
							case AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_END:
								System.out.println("AVIOCTRL_RECORD_PLAY_END");
								if(recording){
									saveVideo();
								}
								Toast.makeText(
										LiveViewGLviewActivity.this,
										LiveViewGLviewActivity.this
												.getText(R.string.page8_tips_play_record_end),
										0).show();
								if(LiveViewGLviewActivity.this.onActivityRuning)
									rockBackToLive();
//								 myHorizontalScrollView.scorllToCurrentTime();
								break;
							case AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START:
								System.out.println("AVIOCTRL_RECORD_PLAY_START");
								break;
						}
					}
					break;
					case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETMICVOLUME_RESP:
						if(mProgressBar!=null)
							mProgressBar.setVisibility(View.GONE);
						var28 = Packet.byteArrayToInt_Little(databyte, 4);
						Log.i("deviceinfo",
								"IOTYPE_USER_IPCAM_GETVOLUME_RESP..............="
										+ var28);
						if (var28 > 255 || var28 < 0)
							break;
						micvalue = var28;
						if(button_showctl!=null)
							button_showctl.setEnabled(true);
						getmic = true;
						if (getmic && getvolume && isclick) {
							if (AlertDialogvar2 != null) {

								final SeekBar var4 = (SeekBar) VolumeView
										.findViewById(R.id.seekBarspeak);
								var4.setMax(255);
								var4.setProgress(spvalue);

								final SeekBar var5 = (SeekBar) VolumeView
										.findViewById(R.id.seekBarmic);
								var5.setMax(255);

								var5.setProgress(micvalue);
								AlertDialogvar2.show();
								getvolume = false;
								getmic = false;
								isclick = false;
							}
						}

						break;

					case 809:
						LiveViewGLviewActivity.this.invalidateOptionsMenu();
						break;

					case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPASSWORD_RESP:

						new DatabaseManager(LiveViewGLviewActivity.this).updateDeviceInfoByDBID(
								mDevice.DBID, mDevice.UID, mDevice.nickName,
								"", "", "admin", mDevice.viewPassword,
								mDevice.EventNotification, mDevice.getChannelIndex(),
								mDevice.isPublic);

						break;
					case 1109://相册返回直播
					{
						if(rockbacktoLive_photo.getVisibility()==View.GONE)
							rockbacktoLive_photo.setVisibility(View.VISIBLE);
						if (LiveViewGLviewActivity.this.txtOnlineNumberlive != null && txtOnlineNumberlive.getVisibility()==View.VISIBLE) {
							txtOnlineNumberlive.setVisibility(View.GONE);
						}
						if (LiveViewGLviewActivity.this.txt_time != null&& txt_time.getVisibility()==View.VISIBLE) {
							txt_time.setVisibility(View.GONE);
						}
						myHorizontalScrollView.setVisibility(View.VISIBLE);
						seek_bar_rl.setVisibility(View.GONE);
						if(mViewPager!=null)
							mViewPager.setVisibility(View.GONE);
						LinearLayout points_ll = (LinearLayout)	findViewById(R.id.points_ll);
						if(points_ll!=null){
							points_ll.setVisibility(View.GONE);
						}
					}
					break;
					case 1110://直播初始化UI
					{
						if(rockbacktoLive_photo.getVisibility()==View.VISIBLE)
							rockbacktoLive_photo.setVisibility(View.GONE);
						if (LiveViewGLviewActivity.this.txtOnlineNumberlive != null && txtOnlineNumberlive.getVisibility()==View.GONE) {
							txtOnlineNumberlive.setVisibility(View.VISIBLE);
						}
						if (LiveViewGLviewActivity.this.txt_time != null&& txt_time.getVisibility()==View.VISIBLE) {
							txt_time.setVisibility(View.GONE);
						}
						monitor.refreshData( );
						myHorizontalScrollView.setVisibility(View.VISIBLE);
						seek_bar_rl.setVisibility(View.GONE);
					}
					break;
					case 1111:
					{
						int duration = var1.getData().getInt("duration");
						int mSampleTime = var1.getData().getInt("SampleTime");
						mp4seekbar.setMax((int) (duration ));
						Log.e("Thread", "=== ===duration:"+duration+"    mSampleTime:"+mSampleTime+"   getMax:" +mp4seekbar.getMax());
						if(Math.abs(seekMp4time-mSampleTime)<2000)//滑动点与更新点相差2000毫秒
						{
							seekMp4time = mSampleTime;
							if(!onSeekbar)
								mp4seekbar.setProgress(mSampleTime);
						}
						totalTime_tv.setText(""+convertTime(duration));
						nowTime_tv.setText(""+convertTime(mSampleTime));
						if(rockbacktoLive_photo.getVisibility()==View.GONE)
							rockbacktoLive_photo.setVisibility(View.VISIBLE);
						myHorizontalScrollView.setVisibility(View.GONE);
						if (!isPause) {
							mp4pause.setImageResource(R.drawable.playing_pause);
						} else {
							mp4pause.setImageResource(R.drawable.playing_start);
						}
						if(isPlayMp4){
							if(right2_tv!=null)
								right2_tv.setVisibility(View.GONE);
							if(mViewPager!=null)
								mViewPager.setVisibility(View.GONE);
							LinearLayout points_ll = (LinearLayout)	findViewById(R.id.points_ll);
							if(points_ll!=null){
								points_ll.setVisibility(View.GONE);
							}
						}
						if (LiveViewGLviewActivity.this.txtOnlineNumberlive != null && txtOnlineNumberlive.getVisibility()==View.VISIBLE) {
							txtOnlineNumberlive.setVisibility(View.GONE);
						}


					}
					break;
					case 1113:
						Toast.makeText(LiveViewGLviewActivity. this,
								getText(R.string.page21_tips_play_record_failed), 0)
								.show();
						break;
					case 1114:
						if (isPause) {
							mp4pause.setImageResource(R.drawable.playing_pause);
						} else {
							mp4pause.setImageResource(R.drawable.playing_start);
						}
						break;
					case 1112:
					{
						if(rockbacktoLive_photo.getVisibility()==View.VISIBLE)
							rockbacktoLive_photo.setVisibility(View.GONE);
						myHorizontalScrollView.setVisibility(View.VISIBLE);
						seek_bar_rl.setVisibility(View.GONE);

						if (LiveViewGLviewActivity.this.txtOnlineNumberlive != null && txtOnlineNumberlive.getVisibility()==View.GONE) {
							txtOnlineNumberlive.setVisibility(View.VISIBLE);
						}

						if (LiveViewGLviewActivity.this.txt_time != null&& txt_time.getVisibility()==View.GONE) {
							txt_time.setVisibility(View.VISIBLE);
						}

						final MyCamera mCamera=  CameraManagerment.getInstance().getexistCamera(mDevUID);
						monitor.setCameraHardware_pkg(mCamera.hardware_pkg);
						monitor.attachCamera(mCameraManagerment.getexistCamera(mDevUID), 0,mDevice.installmode,mDevice,mDevice.snapshot,true);
						monitor.setCameraPutModel(mDevice.installmode);
						checkDoorbellSound();
					}
					break;

					case 1118:
					{
						int duration = var1.getData().getInt("duration");
						int mSampleTime = 0;
						mp4seekbar.setMax((int) (duration ));
						mp4seekbar.setProgress(0);

						totalTime_tv.setText(""+convertTime(duration));
						nowTime_tv.setText(""+convertTime(mSampleTime));

					}
					break;

					case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_ADVANCESETTINGS_RESP:
						char adv_valid = (char) databyte[48] ;
						if(adv_valid==0x7E){
							int isCloud = (int) (databyte[77]&0xff);
							int light = (int) databyte[81]&0xff;
							SharedPreferences.Editor editor = LiveViewGLviewActivity.this.getSharedPreferences("isCloudSave", Context.MODE_PRIVATE).edit();
							editor.putBoolean(mDevUID, isCloud==1);
							editor.commit();
							Boolean isCloudSave = (isCloud==1);
							Log.e("guo..live","hardware_pkg="+mDevice.hardware_pkg);

							if(mDevice.hardware_pkg == HARDWAEW_PKG.SREE_VR_LINUX_PIR_LED) {
								lockStatus =  light == 1;
								Log.e("guo..live","lockStatus="+lockStatus);

								if (lockStatus) {
									img_lock.setImageResource(R.drawable.kandeng);
								} else {
									img_lock.setImageResource(R.drawable.guandeng);
								}
							}



						}
						break;
				}

				super.handleMessage(var1);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};

	private String mConnStatus = "";
	private String mDevUID;
	private DeviceInfo mDevice = null;

	private int temperature;
	private int battery;
	private int avFrameTimeStamp;
	public boolean mIsListening = false;
	public boolean mIsSpeaking = false;
	private int mOnlineNm;
	//	private int mSelectedChannel=0;
	private long mVideoBPS;
	private int mVideoFPS;
	private int mVideoHeight;
	private int mVideoWidth;
	private int mlastVideoWidth=0;
	private GLView monitor = null;


	//	private TimeThread timeThread;
	private ProgressBar mProgressBar;
	/*** 中心控制点图标 */
	private ImageButton img_control_dot ;
	private ImageButton pzv_control_dot ;
	private ImageButton voiceMute ;
	private ImageButton img_control_vrmode,img_control_vrvideo,img_control_runrefresh,img_xy_setting,img_qr_setting ,img_ptz_setting;
	private void addImageGallery(File var1) {
		ContentValues var2 = new ContentValues();
		var2.put("_data", var1.getAbsolutePath());
		var2.put("mime_type", "image/jpeg");
		this.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, var2);
	}

	private Bitmap compressImage(Bitmap var1) {
		try {
			ByteArrayOutputStream var2 = new ByteArrayOutputStream();
			var1.compress(CompressFormat.JPEG, 100, var2);
			Bitmap var5 = BitmapFactory.decodeStream(new ByteArrayInputStream(
					var2.toByteArray()), (Rect) null, (Options) null);
			return var5;
		} catch (Exception var6) {
			var6.printStackTrace();
			return var1;
		}
	}

	private static String getFileNameWithTime(Bitmap var23, int installmode, int hardware_pkg) {
		Calendar var0 = Calendar.getInstance();
		int var1 = var0.get(1);
		int var2 = 1 + var0.get(2);
		int var3 = var0.get(5);
		int var4 = var0.get(11);
		int var5 = var0.get(12);
		int var6 = var0.get(13);
		var0.get(14);
		StringBuffer var8 = new StringBuffer();
		var8.append("IMG_");
		var8.append(var1);
		if (var2 < 10) {
			var8.append('0');
		}

		var8.append(var2);
		if (var3 < 10) {
			var8.append('0');
		}

		var8.append(var3);
		var8.append('_');
		if (var4 < 10) {
			var8.append('0');
		}

		var8.append(var4);
		if (var5 < 10) {
			var8.append('0');
		}

		var8.append(var5);
		if (var6 < 10) {
			var8.append('0');
		}

		var8.append(var6);

		var8.append(var6);
		int installmodeStr = installmode;
		if(installmode<0){
			installmodeStr = 0;
		}
		if( hardware_pkg < 0){
			hardware_pkg = 0;
		}
		String deviceParm = String.format("_%d_",installmodeStr );
		var8.append(deviceParm);
		String deviceParmhardware_pkg = String.format("%d_",hardware_pkg );
		var8.append(deviceParmhardware_pkg);
		var8.append(".jpg");
		return var8.toString();
	}

	private String getPerformance(int var1) {
		return var1 < 30 ? this.getText(R.string.page8_txtBad).toString()
				: (var1 < 60 ? this.getText(R.string.page8_txtNormal).toString()
				: this.getText(R.string.page8_txtGood).toString());
	}

	private String getSessionMode(int var1) {
		return var1 == 0 ? this.getText(R.string.page8_connmode_p2p).toString()
				: (var1 == 1 ? this.getText(R.string.page8_connmode_relay).toString()
				: (var1 == 2 ? this.getText(R.string.page8_connmode_lan)
				.toString() : this.getText(
				R.string.page8_connmode_none).toString()));
	}

	private static boolean isSDCardValid() {
		return Environment.getExternalStorageState().equals("mounted");
	}

	private void quit(int result) {
		isruningRefresh = false;
		if(mProgressBar!=null)
			mProgressBar.setVisibility(View.VISIBLE);
		mCameraManagerment.unregisterIOTCListener(mDevUID,LiveViewGLviewActivity.this);
		mCameraManagerment.userIPCStopAlStream(mDevUID);
		lastsnap();
		new Thread(
				new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						Log.i("IOTCamera", "quit  Thread:"+Thread.currentThread());

						if ( mIsListening) {
							mCameraManagerment.setCameraLastAudioMode(mDevUID,1);
						} else if ( mIsSpeaking) {
							mCameraManagerment.setCameraLastAudioMode(mDevUID,2);
						} else {
							mCameraManagerment.setCameraLastAudioMode(mDevUID,0);
						}


						StopAudio();
						System.gc();

					}
				}
		).start();

		Bundle var3 = new Bundle();
		var3.putString("dev_uid", this.mDevUID);
		var3.putString("view_password", mDevice.viewPassword);
		var3.putByteArray("snapshot", null);
		var3.putInt("camera_channel", 0);
		Intent var4 = new Intent();

		var4.putExtras(var3);
		this.setResult(result, var4);
		this.finish();
		System.gc();
	}

	private boolean saveImage(String fileName, Bitmap mBitmap) {
		Log.i("IOTCamera", "fileName:" + fileName);
		if (mBitmap != null) {
			Log.i("IOTCamera", "mBitmap!=null");
		}
		if (mBitmap.isRecycled()) {
			Log.i("IOTCamera", "mBitmap isRecycled");
			return false;
		}
		File f = new File(fileName);
		try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (!mBitmap.isRecycled()) {

			Bitmap newB=FileUtils.ratio(mBitmap, mBitmap.getWidth() , mBitmap.getHeight() );
			newB.compress(CompressFormat.JPEG, 99, fOut); //大图压缩后再存储，显示时提高转换效率

			try {
				fOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			long mImageTime = System.currentTimeMillis();//取当前系统时间
			// media provider uses seconds for DATE_MODIFIED and DATE_ADDED, but milliseconds
			// for DATE_TAKEN
			long dateSeconds = mImageTime / 1000;
			String mImageFileName = fileName ; //以保存时间命名
			String mImageFilePath = fileName; //注意这里的mImageFilePath是： 目录名称+文件名
			int mImageWidth = newB.getWidth();
			int mImageHeight = newB.getHeight();

			// Save the screenshot to the MediaStore
			ContentValues values = new ContentValues();
			ContentResolver resolver = this.getContentResolver();
			values.put(MediaStore.Images.ImageColumns.DATA, mImageFilePath);
			values.put(MediaStore.Images.ImageColumns.TITLE, mImageFileName);
			values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, mImageFileName);
			values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, mImageTime);
			values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds);
			values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds);
			values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg");
			values.put(MediaStore.Images.ImageColumns.WIDTH, mImageWidth);
			values.put(MediaStore.Images.ImageColumns.HEIGHT, mImageHeight);
			Uri uri = resolver.insert(Media.EXTERNAL_CONTENT_URI, values);

			// update file size in the database
			values.clear();
			values.put(MediaStore.Images.ImageColumns.SIZE, new File(mImageFilePath).length());
			if(uri==null){
				return false;
			}else{
				Log.i("IOTCamera", "mBitmap uri:"+uri.toString());
				resolver.update(uri, values, null, null);

			}

			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}



		}
		return true;
	}

	private   void syncAlbum(String imageUrl,File path) {
		if (VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			final Uri contentUri = Uri.fromFile(path);
			scanIntent.setData(contentUri);
			LocalBroadcastManager.getInstance(this).sendBroadcast(scanIntent);
		} else {
			//4.4开始不允许发送"Intent.ACTION_MEDIA_MOUNTED"广播, 否则会出现: Permission Denial: not allowed to send broadcast android.intent.action.MEDIA_MOUNTED from pid=15410, uid=10135
			final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
	}

	public DisplayMetrics getDM(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		return outMetrics;
	}
	private long lasttime = 0;
	private long nowtime = 0;
	private AlertDialog AlertDialogvar2;
	private View VolumeView;
	private int currenTime;
	private RelativeLayout control_bottom_new;

	private void setupViewInLandscapeLayout() {
		this.getWindow().setFlags(128, 128);
		this.getWindow().addFlags(1024);
		this.getWindow().clearFlags(2048);
		isLandorientation = true;
		this.setContentView(R.layout.live_view_glview_landscape);
		control_bottom_new=(RelativeLayout)findViewById(R.id.control_bottom_new);
		if (VERSION.SDK_INT < 14) {
			this.bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			this.bgSplit.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		}
		if(handler.hasMessages(DELAYSETMONITOR)){
			handler.removeMessages(DELAYSETMONITOR);
			handler.sendEmptyMessageDelayed(DELAYSETMONITOR,500);
		}else{
			handler.sendEmptyMessageDelayed(DELAYSETMONITOR,0);
		}


		mTime = (TextView) findViewById(R.id.mytime);


		initView();
		initAddPopupWindow();
		if(title!=null && mDevice !=  null && mDevice.nickName !=null)
			title.setText(mDevice.nickName);
		right_image5 = (ImageView) findViewById(R.id.right_image5);
		right_image4 = (ImageView) findViewById(R.id.right_image4);
		right_image3 = (ImageView) findViewById(R.id.right_image3);
		right_image2 = (ImageView) findViewById(R.id.right_image2);
		right_image = (ImageView) findViewById(R.id.right_image);

		right_image5.setImageResource(R.drawable.photo_off);
		right_image4.setImageResource(R.drawable.record_off);
		right_image3.setImageResource(R.drawable.mic_off);
		//right_image2.setImageResource(R.drawable.record_client);
		right_image2.setImageResource(R.drawable.record_tap_off);
		right_image.setImageResource(R.drawable.setting_off);

		right_image5.setVisibility(View.VISIBLE);
		right_image4.setVisibility(View.VISIBLE);
		right_image3.setVisibility(View.VISIBLE);
		right_image2.setVisibility(View.GONE);
		right_image.setVisibility(View.GONE);

		right_image5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				savePhoto();
			}
		});
		right_image4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveVideo();
			}
		});
		right_image3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(recording || isPlaybackData || isPlayMp4){
					return;
				}
				changestatue_voice();
			}
		});
		right_image2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				mPopupWindowAdd.showAsDropDown(right_image2, 10, 15);
				lastsnap();
				Bundle var21 = new Bundle();
				Intent var3 = new Intent();
				var21.putString("dev_uid", LiveViewGLviewActivity.this.mDevice.UID);
				var21.putString("dev_nickName",
						LiveViewGLviewActivity.this.mDevice.nickName);
				var21.putString("view_acc",
						LiveViewGLviewActivity.this.mDevice.viewAccount);
				var21.putString("view_pwd",
						LiveViewGLviewActivity.this.mDevice.viewPassword);
				var21.putInt("camera_channel",
						LiveViewGLviewActivity.this.mDevice.getChannelIndex());

				var3.putExtras(var21);
				var3.setClass(LiveViewGLviewActivity.this, PhotoGridActivity.class);
				startActivity(var3);
//				mPopupWindowAdd.dismiss();
			}
		});
		right_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				Intent intent = new Intent();
				bundle.putString("dev_uid", LiveViewGLviewActivity.this.mDevice.UID);
				bundle.putString("dev_nickName",
						LiveViewGLviewActivity.this.mDevice.nickName);
				bundle.putString("view_acc",
						LiveViewGLviewActivity.this.mDevice.viewAccount);
				bundle.putString("view_pwd",
						LiveViewGLviewActivity.this.mDevice.viewPassword);
				bundle.putInt("camera_channel",
						LiveViewGLviewActivity.this.mDevice.getChannelIndex());
				bundle.putInt("battery",battery);
				intent.putExtras(bundle);
				intent.setClass(LiveViewGLviewActivity.this, SettingActivity.class);
				startActivityForResult(intent, -11);//(intent);
			}
		});
//		if(UbiaApplication.BUILD_CHECK_PLAYVOICE)
		StartAudio();//open anytime

		SetUpView();
		getActionBar().hide();
		setRecodestatue();
		setstatue_voice_icon();
		myHorizontalScrollView =(com.view.timeline.MyHorizontalScrollView)this.findViewById(R.id.myHorizontalScrollView);
		myHorizontalScrollView .setTimeLinePlayCallBackInterface(this);
		myHorizontalScrollView.setRollBackToCurrentTimeCallbackInterface(this);
		myHorizontalScrollView.scorllToTheTime(currenTime);
		myHorizontalScrollView.setEventColor(R.color.color_less_blue_timeline);

		// TODO Auto-generated method stub

		if(this.mNoteInfoData!=null){
			myHorizontalScrollView.setNoteInfoData(mNoteInfoData);
		}


//			getTimeLineBitmap();
		myHorizontalScrollView.view.setTimeUnit(timeUnit);
		myHorizontalScrollView.invalidate();
		rockbacktoLive_photo= (TextView) this
				.findViewById(R.id.rockbacktoLive_photo);
		if(rockbacktoLive_photo!=null)
			rockbacktoLive_photo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					rockBackToLive();
					return;

				}
			});
	}

	private long clickBackToLivetime;
	protected boolean isPause;
	private ViewPager mViewPager;
	private MyPagerAdapter myAdapter;
	View ll_dot ,ll_dot2;
	private ImageView[] indicationPoint;//指示点控件
	private int[] points = {R.id.point1,R.id.point2 };
	boolean lockStatus;
	private List<View> viewList = new ArrayList<View>();
	private void setupViewInPortraitLayout() {
		this.getWindow().setFlags(128, 128);
		this.getWindow().addFlags(1024);
		this.getWindow().clearFlags(2048);
		this.setContentView(R.layout.live_view_glview_portrait);
		if (VERSION.SDK_INT < 14) {
			this.bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			this.bgSplit.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		}
		isLandorientation = false;

		LayoutInflater inflater = LayoutInflater.from(this);
		ll_dot = inflater.inflate(R.layout.ll_dot,null);

		viewList.clear();
		viewList.add(ll_dot);

		if(mDevice.hardware_pkg==HARDWAEW_PKG.SREE_VR_LINUX_PIR_LED||mDevice.hardware_pkg==HARDWAEW_PKG.BELL_LOCK){
			ll_dot2 = inflater.inflate(R.layout.ll_dot2,null);
			img_lock= (ImageView) ll_dot2.findViewById(R.id.img_lock);

			viewList.add(ll_dot2);
			LinearLayout points_ll = (LinearLayout)	findViewById(R.id.points_ll);
			points_ll.setVisibility(View.VISIBLE);
			img_lock.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					lockStatus = !lockStatus;
					mCameraManagerment.userIPCsetLock(mDevUID,lockStatus?0:1);
				}
			});

			if(mDevice.hardware_pkg == HARDWAEW_PKG.SREE_VR_LINUX_PIR_LED){

				img_lock.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						lockStatus = !lockStatus;
						if(lockStatus){
							img_lock.setImageResource(R.drawable.kandeng);
						}else{
							img_lock.setImageResource(R.drawable.guandeng);
						}

						mCameraManagerment.userIPCsetLock(mDevUID,lockStatus?1:0);
					}
				});
			}


		}

		myAdapter = new MyPagerAdapter(viewList);
		{
			mViewPager = (ViewPager) this.findViewById(R.id.mViewPager);


			mViewPager.setAdapter(myAdapter);
			mViewPager.setOffscreenPageLimit(viewList.size());// 加载缓存的页面个数
			mViewPager.setOnPageChangeListener(this);
		}
		indicationPoint = new ImageView[viewList.size()];
		for (int i=0; i<viewList.size(); i++) {
			indicationPoint[i] = (ImageView) findViewById(points[i]);
		}
		if(handler.hasMessages(DELAYSETMONITOR)){
			handler.removeMessages(DELAYSETMONITOR);
			handler.sendEmptyMessageDelayed(DELAYSETMONITOR,500);
		}else{
			handler.sendEmptyMessageDelayed(DELAYSETMONITOR,0);
		}

		initView();

		if(title!=null && mDevice !=  null && mDevice.nickName !=null)
			title.setText(mDevice.nickName);


		this.showMessage();
		mTime = (TextView) findViewById(R.id.mytime);
		initAddPopupWindow();
//		if(UbiaApplication.BUILD_CHECK_PLAYVOICE)
		StartAudio();

		SetUpView();

		img_photo = (ImageView)ll_dot. findViewById(R.id.img_photo);
		img_video = (ImageView) ll_dot.findViewById(R.id.img_video);
		img_mic = (ImageView) ll_dot.findViewById(R.id.img_mic);



		img_record = (ImageView) findViewById(R.id.img_record);
		img_setting = (ImageView) findViewById(R.id.img_setting);
		img_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				savePhoto();
			}
		});

		img_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveVideo();
			}
		});
		img_mic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(recording || isPlaybackData || isPlayMp4){
					return;
				}
				changestatue_voice();
			}
		});
		final LinearLayout ll_plan = (LinearLayout) findViewById(R.id.ll_plan);
		//mPopupWindowAdd.showAsDropDown(img_mic, 0, 0);
		img_record.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				goPhotoGridActivity();
//				if(mPopViewAdd.getHeight() > 0){
//					popwinoffset = mPopViewAdd.getHeight() + 3;
//				}
//				mPopupWindowAdd.showAsDropDown(img_record, 0, -ll_plan.getHeight()-popwinoffset);
				//else mPopupWindowAdd.showAsDropDown(img_record, 0, -ll_plan.getHeight()-mPopViewAdd.getHeight()+3);//(0 - 170 - ll_plan.getHeight())
			}
		});
		img_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				Intent intent = new Intent();
				bundle.putString("dev_uid", LiveViewGLviewActivity.this.mDevice.UID);
				bundle.putString("dev_nickName",
						LiveViewGLviewActivity.this.mDevice.nickName);
				bundle.putString("view_acc",
						LiveViewGLviewActivity.this.mDevice.viewAccount);
				bundle.putString("view_pwd",
						LiveViewGLviewActivity.this.mDevice.viewPassword);
				bundle.putInt("camera_channel",
						LiveViewGLviewActivity.this.mDevice.getChannelIndex());
				bundle.putInt("battery",battery);
				intent.putExtras(bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.setClass(LiveViewGLviewActivity.this, SettingActivity.class);
				startActivityForResult(intent, -11);//(intent);
				//		getTimeLineBitmap();
			}
		});

		getActionBar().hide();
		setRecodestatue();
		setstatue_voice_icon();
		myHorizontalScrollView =(com.view.timeline.MyHorizontalScrollView)this.findViewById(R.id.myHorizontalScrollView);
//		 myHorizontalScrollView.scorllToCurrentTime();
		myHorizontalScrollView .setTimeLinePlayCallBackInterface(this);
		myHorizontalScrollView.setRollBackToCurrentTimeCallbackInterface(this);
		myHorizontalScrollView.setEventColor(R.color.color_less_blue_timeline);
		// TODO Auto-generated method stub


		myHorizontalScrollView.scorllToTheTime(currenTime);
		myHorizontalScrollView.view.setTimeUnit(timeUnit);
		myHorizontalScrollView.invalidate();
		if(mNoteInfoData!=null){
			myHorizontalScrollView.setNoteInfoData(mNoteInfoData);
		}
		rockbacktoLive_photo= (TextView) this
				.findViewById(R.id.rockbacktoLive_photo);
		if(rockbacktoLive_photo!=null)
			rockbacktoLive_photo.setOnClickListener(new OnClickListener() {//竖屏

				@Override
				public void onClick(View arg0) {
					rockBackToLive();
				}
			});
		//mCameraManagerment.userIPCGetAdvanceSetting(mDevUID);
		img_control_dot =  (ImageButton)ll_dot.findViewById(R.id.img_control_dot);
		img_control_dot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				garageDoor = mDevice.garageDoor;
				garageDoor = !garageDoor;
				img_control_dot.setSelected(garageDoor);
				mCameraManagerment.userIPCControlDoor(mDevUID,0,garageDoor);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//mCameraManagerment.userIPCGetAdvanceSetting(mDevUID);
			}
		});
		img_control_vrmode =  (ImageButton)findViewById(R.id.img_control_vrmode);
		img_control_vrmode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//					monitor.setVisibility(View.GONE);
				if(mProgressBar!=null && mProgressBar.getVisibility()==View.VISIBLE)return;
//					monitorLayout_iv.setVisibility(View.VISIBLE);
//					monitorLayout_iv.setImageBitmap(monitor.getLastBitmap());
//					mTilesFrameLayout.onResume();
//					mTilesFrameLayout.startAnimation();
				if (getSharedPreferences("isCloudSave",Context.MODE_PRIVATE).getBoolean(mDevice.UID, false)){
					Bundle bundle = new Bundle();
					Intent intent = new Intent();
					bundle.putString("dev_uid", LiveViewGLviewActivity.this.mDevice.UID);
					bundle.putString("dev_nickName",
							LiveViewGLviewActivity.this.mDevice.nickName);
					bundle.putString("view_acc",
							LiveViewGLviewActivity.this.mDevice.viewAccount);
					bundle.putString("view_pwd",
							LiveViewGLviewActivity.this.mDevice.viewPassword);
					bundle.putInt("camera_channel",
							LiveViewGLviewActivity.this.mDevice.getChannelIndex());
					intent.putExtras(bundle);
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					intent.setClass(LiveViewGLviewActivity.this, CloudSaveVideoListActivity.class);
					startActivityForResult(intent, -12);//(intent);
				} else
					monitor.changeSurfaceMode();
			}
		});


		img_xy_setting =  (ImageButton)ll_dot.findViewById(R.id.img_xy_setting);
		img_xy_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showXYSettingsDialo();
			}
		});
		img_control_vrvideo =  (ImageButton)findViewById(R.id.img_control_vrvideo);
		img_control_vrvideo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				goPhotoGridActivity();
			}
		});

		img_qr_setting =  (ImageButton)ll_dot.findViewById(R.id.img_qr_setting);
		img_qr_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mCameraManagerment.userIPCGetZigbee(mDevUID);
			}
		});
		img_ptz_setting =  (ImageButton)ll_dot.findViewById(R.id.img_ptz_setting);
		img_ptz_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				RelativeLayout layout_steering_wheel = (RelativeLayout) findViewById(R.id.layout_steering_wheel);
				if(layout_steering_wheel.getVisibility()==View.GONE)
				{
					layout_steering_wheel.setVisibility(View.VISIBLE);
				}else{
					layout_steering_wheel.setVisibility(View.GONE);
				}
			}
		});

		if(MainCameraFragment.build_for_factory_tool ){
			img_qr_setting.setVisibility(View.VISIBLE);
			img_ptz_setting.setVisibility(View.VISIBLE);
		}else{
			img_qr_setting.setVisibility(View.GONE);
			img_ptz_setting.setVisibility(View.GONE);
		}


		if(mDevUID.length()>18){
			//String uidPrefix = LiveViewActivity.this.mCamera.getmDevUID().substring(0,18);
			//if(MainCameraFragment.build_for_factory_tool && uidPrefix.equals("FFFFFFFFFFFFFFFFFF"))
			{
				pzv_control_dot =  (ImageButton)ll_dot.findViewById(R.id.pzv_control_dot);
				//pzv_control_dot.setVisibility(View.VISIBLE);
//			pzv_control_dot.setOnClickListener(new OnClickListener() {
			}
			pzv_control_dot.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					if(arg1.getAction()==MotionEvent.ACTION_UP){
						pzv_control_dot.setSelected(false);
						pzv_control_dot.setPressed(false);
						return true;
					}
					else if(arg1.getAction()==MotionEvent.ACTION_DOWN){
						pzv_control_dot.setSelected(true);
						pzv_control_dot.setPressed(true);
						mCameraManagerment.userIPCPTZCruise(mDevUID,0,0);
						return true;
					}
					return false;
				}
			});
		}

		img_control_runrefresh =  (ImageButton)findViewById(R.id.img_control_runrefresh);
		img_control_runrefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				isruningRefresh = !isruningRefresh;
				if(isruningRefresh){
					img_control_runrefresh.setImageResource(R.drawable.tab_cruise_pre);
				}
				else{
					img_control_runrefresh.setImageResource(R.drawable.tab_cruise_n);
				}
			}
		});

		voiceMute =  (ImageButton)findViewById(R.id.voiceMute);
		voiceMute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if( isPlayMp4)
					mCameraManagerment.userIPCMuteControl(mDevUID,true); //mp4关闭底层声音
				if(LiveViewGLviewActivity.this.mIsListening ){
					try {
						if(!isPlayMp4)
							mCameraManagerment.userIPCMuteControl(mDevUID,true);

						LiveViewGLviewActivity.this.mIsListening = false;
						StartAudio();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(voiceMute!=null){
						voiceMute.setImageResource(R.drawable.sound_off);
					}
				}
				else{
					try {
						if(!isPlayMp4)
							mCameraManagerment.userIPCMuteControl(mDevUID,false);
						LiveViewGLviewActivity.this.mIsListening = true;
						StartAudio();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(voiceMute!=null){
						voiceMute.setImageResource(R.drawable.sound_on);
					}
				}
			}
		});
		if(!LiveViewGLviewActivity.this.mIsListening ){//状态设置
			try {

				mCameraManagerment.userIPCMuteControl(mDevUID,true);
				LiveViewGLviewActivity.this.mIsListening = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(voiceMute!=null){
				voiceMute.setImageResource(R.drawable.sound_off);
			}
		}
		else{
			try {

				mCameraManagerment.userIPCMuteControl(mDevUID,false);
				LiveViewGLviewActivity.this.mIsListening = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(voiceMute!=null){
				voiceMute.setImageResource(R.drawable.sound_on);
			}
		}

		if(VRConfig.isVRdevice(mCameraManagerment.getexistCamera(mDevUID).hardware_pkg)){
			if(img_control_runrefresh!=null) img_control_runrefresh.setVisibility(View.VISIBLE);
			if(img_control_vrmode!=null) img_control_vrmode.setVisibility(View.VISIBLE);
			if(img_control_vrvideo!=null) img_control_vrvideo.setVisibility(View.VISIBLE);
			if(voiceMute!=null) voiceMute.setVisibility(View.VISIBLE);
			if(img_record!=null) img_record.setVisibility(View.GONE);

		}else{
			// if(img_control_runrefresh!=null) img_control_runrefresh.setVisibility(View.GONE);
			// if(img_control_vrmode!=null) img_control_vrmode.setVisibility(View.GONE);

			//	 if(img_control_vrvideo!=null)  img_control_vrvideo.setVisibility(View.GONE);
			if(UbiaApplication.BUILD_FOR_WIFICAM){
				if(voiceMute!=null)  voiceMute.setVisibility(View.GONE);
				if(img_record!=null) img_record.setVisibility(View.VISIBLE);//VR模式
			}else{
				if(voiceMute!=null)  voiceMute.setVisibility(View.VISIBLE);
				if(img_record!=null)img_record.setVisibility(View.GONE);
			}
		}

		if(UbiaApplication.BUILD_CHECK_PLAYVOICE){

		}else{

		}
		//checkDoorbellSound();
		totalTime_tv = (TextView) findViewById(R.id.totalTime);
		nowTime_tv = (TextView) findViewById(R.id.nowTime);
		mp4seekbar = (SeekBar)findViewById(R.id.seek_bar);
		mp4seekbar.setMax(255);
		mp4seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				if(isPlayMp4 && p_mp4Read!=null)
				{
					seekMp4time = arg0.getProgress();
					Log.e("","  seekMp4time :"+seekMp4time+"   getMax:"+arg0.getMax());
					p_mp4Read.Seek(seekMp4time  );
				}
				onSeekbar = false;
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

				onSeekbar = true;
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {


			}
		});

		seek_bar_rl = (RelativeLayout) findViewById(R.id.seek_bar_rl);
		seek_bar_rl.setVisibility(View.GONE);
		mp4pause = (ImageView) findViewById(R.id.pause);
		mp4pause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (isPause) {
					mp4pause.setImageResource(R.drawable.playing_pause);
				} else {
					mp4pause.setImageResource(R.drawable.playing_start);
				}
				isPause = !isPause;
				if(!isPlayMp4 && showGridViewBitmap == false && bInitH264==false){
					try {

						new Thread(new Runnable() {
							@Override
							public void run() {
								try {

									process(playingmUriString);
								}
								catch ( Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
					} catch ( Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		if (MainCameraFragment.build_for_factory_tool) {

			RelativeLayout layout_steering_wheel = (RelativeLayout) findViewById(R.id.layout_steering_wheel);
			layout_steering_wheel.setVisibility(View.VISIBLE);
			bt_top = (ImageButton) findViewById(R.id.top_btn);
			bt_bottom = (ImageButton) findViewById(R.id.bottom_btn);
			bt_left = (ImageButton) findViewById(R.id.left_btn);
			bt_right = (ImageButton) findViewById(R.id.right_btn);
			BtnPTZ_Auto = (ImageButton) findViewById(R.id.BtnPTZ_Auto);
			bt_top.setOnTouchListener(this);
			bt_bottom.setOnTouchListener(this);
			bt_left.setOnTouchListener(this);
			bt_right.setOnTouchListener(this);
			BtnPTZ_Auto.setOnTouchListener(this);
			if (!ptzThreadRunning)
				ptz.start();
		}



	}
	boolean garageDoor;
	ImageView mBatteryView ,image_wifi;

	ImageButton ImageViewRec,refresh_device_ib;
	private int timeUnit;

	private void SetUpView() {
		//if (ImageViewRec == null)
		mBatteryView = (ImageView)findViewById(R.id.image_battery);
		ImageViewRec = (ImageButton) this.findViewById(R.id.ImageViewRec);
		image_wifi =(ImageView) this.findViewById(R.id.image_wifi);
		refresh_device_ib = (ImageButton) this.findViewById(R.id.refresh_device_ib);
		if (mIvFrameAnim != null && mIvFrameAnim.isRunning()) {
			mIvFrameAnim.stop();
			mIvFrameAnim = null;
		}
	}

	private void showMessage() {
		// St_SInfo var1 = new St_SInfo();
		// IOTCAPIs.IOTC_Session_Check(this.mCamera.getMSID(), var1);
		//remove by maxwell 160907
//		if (MainCameraFragment.nShowMessageCount >= 10) {
//			this.txtConnectionStatus.setText(" "+this.mConnStatus);
//			TextView var3 = this.txtConnectionMode;
//			int var4;
//			if (this.mCamera != null) {
//				var4 = this.mCamera.getSessionMode();
//			} else {
//				var4 = -1;
//			}
//
//			// var3.setText(this.getSessionMode(var4) + " C: "
//			// + IOTCAPIs.IOTC_Get_Nat_Type() + ", D: " + var1.NatType
//			// + ",R" + this.mCamera.getbResend());
//			this.txtConnectionSlash.setText(" / ");
//			this.txtResolutionSlash.setText(" / ");
//			this.txtShowFPS.setText(this.getText(R.string.txtFPS));
//			this.txtFPSSlash.setText(" / ");
//			this.txtShowBPS.setText(this.getText(R.string.txtBPS));
//			this.txtOnlineNumberSlash.setText(" / ");
//			this.txtShowFrameRatio
//					.setText(this.getText(R.string.txtFrameRatio));
//			this.txtFrameCountSlash.setText(" / ");
//			this.txtQuality.setText(this.getText(R.string.txtRecordSetting));
//			this.txtRecvFrmSlash.setText(" / ");
//			this.txtConnectionMode.setVisibility(View.VISIBLE);
//			this.txtFrameRate.setVisibility(View.VISIBLE);
//			this.txtBitRate.setVisibility(View.VISIBLE);
//
//			this.txtFrameCount.setVisibility(View.VISIBLE);
//			this.txtIncompleteFrameCount.setVisibility(View.VISIBLE);
//			this.txtRecvFrmPreSec.setVisibility(View.VISIBLE);
//			this.txtDispFrmPreSeco.setVisibility(View.VISIBLE);
//		}

	}

	public View makeView() {
		return new TextView(this);
	}

	private String playingmUriString = "";
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		//Log.i("deviceinfo", "onActivityResult:var1 ="+var1 +"   resultCode ="+resultCode);
		if (requestCode == 99) {
//			this.monitor = (GLView) this.findViewById(R.id.monitorLayout);
//			this.monitor.attachCamera(mCamera, 0);
//			int screenWidth  = getWindowManager().getDefaultDisplay().getWidth();
//			int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
//			double surface_height=screenHeight*(0.4);
//
//			android.widget.RelativeLayout.LayoutParams params=new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) surface_height);
//			 params.addRule(RelativeLayout.CENTER_IN_PARENT);
//			monitor.setLayoutParams(params);
		}
		else if(resultCode==-11){
			quit(resultCode);
			this.finish();

		}

		showGridViewBitmap = false;
		if(PHOTOGRID_REQUESTCODE==requestCode && intent !=null){
			if (right_image3 != null) {
				right_image3.setImageResource(R.drawable.mic_disable);
				right_image3.setEnabled(false);
			}
			if (img_mic != null) {
				img_mic.setImageResource(R.drawable.mic_disable);
				img_mic.setEnabled(false);
			}
			StopTalk();
			this.invalidateOptionsMenu();
			showGridViewBitmap = true;

			Bundle bundle=intent.getExtras();
//			ArrayList	list2 = bundle.getParcelableArrayList("list");
//			int position = bundle.getInt("position") ;
			final String mUriString = intent.getStringExtra("uri");
			Log.i("images", "》》》》》》》》》》》》  uri:" + mUriString);
			if (mUriString == null) {
				getHelper().showMessage(R.string.page9_failed_to_view_image);
				return;
			}
			if(mUriString.toUpperCase().contains(".MP4")){
				isPlayMp4 = true;
				seek_bar_rl.setVisibility(View.VISIBLE);
				if (LiveViewGLviewActivity.this.txt_time != null&& txt_time.getVisibility()==View.VISIBLE) {
					txt_time.setVisibility(View.GONE);
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							String[] nameSplit= mUriString.split("_");
							if(mUriString.contains(BuildConfig.FLAVOR) && nameSplit.length>5) {
								String deviceParmStr = mUriString.contains("Cloud")? "2" :nameSplit[3]; //云存储文件名不包含installmode，暂时写死2
								String deviceParm_hardpack = nameSplit[4];
								int installmode = 0 ;
								if(deviceParmStr.length()>1){
									installmode = 0;
								}else{
									try{
										installmode = Integer.parseInt(deviceParmStr);
									}catch (Exception e){
										installmode = VRConfig.CameraPutModelFaceFront;
									}
								}
								int hardware_pkg = 0;
								try{
									hardware_pkg = mUriString.contains("Cloud") ? HARDWAEW_PKG.CM_BELL_VR_9732_5112 : Integer.parseInt(deviceParm_hardpack); //云存储文件名不包含hardware_pkg，暂时写死19
								}catch (Exception e){
									hardware_pkg = 0;
								}


								Log.d("","ImageAspectEnum :"  +"   installmode:"+installmode);
								monitor.attachCamera(mCameraManagerment.getexistCamera(mDevUID), 0,mDevice.installmode,mDevice,mDevice.snapshot,true);
								monitor.setCameraPutModel(installmode);
								monitor.setCameraHardware_pkg(hardware_pkg);
								monitor.setHorizontal(false);
							}
							playingmUriString= mUriString;
							try {
								if(voiceMute != null){

									voiceMute.setImageResource(R.drawable.sound_on);
								}
								mCameraManagerment.userIPCMuteControl(mDevUID,true);
								LiveViewGLviewActivity.this.mIsListening = true;
							} catch (Exception e) {
								e.printStackTrace();
							}

							process(mUriString);
						}
						catch ( Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}else{
				try {
					if(voiceMute != null){
						voiceMute.setImageResource(R.drawable.sound_off);
						if(mViewPager!=null)
							mViewPager.setVisibility(View.GONE);
						LinearLayout points_ll = (LinearLayout)	findViewById(R.id.points_ll);
						if(points_ll!=null){
							points_ll.setVisibility(View.GONE);
						}
					}
					mCameraManagerment.userIPCMuteControl(mDevUID,true);
					LiveViewGLviewActivity.this.mIsListening = false;


					handler.sendEmptyMessage(1109);
					ContentResolver resolver = getContentResolver();
					Uri uri = Uri.parse(mUriString);
					Bitmap bitmap = BitmapFactory.decodeStream(resolver.openInputStream(uri));
					String[] nameSplit= mUriString.split("_");
//					monitor.refreshBitmap(bitmap) ;
					if(mUriString.contains(BuildConfig.FLAVOR) && nameSplit.length>6) {
						String deviceParmStr = nameSplit[4];
						String deviceParm_hardpack = nameSplit[5];
						int deviceParm = 0;
						if(deviceParmStr.length()>1){
							deviceParm = 0;
						}else{
							try{
								deviceParm = Integer.parseInt(deviceParmStr);
							}catch (Exception e){
								deviceParm = VRConfig.CameraPutModelFaceFront;
							}
//						String deviceParm = String.format("_%x%x_",installmode, ImageAspectEnum.getAspectEnum(var23));
						}
						int hardware_pkg = 0;
						try{
							hardware_pkg = Integer.parseInt(deviceParm_hardpack);
						}catch (Exception e){
							hardware_pkg = 0;
						}   finally {

						}
						int installmode = deviceParm ;
						Log.d("","ImageAspectEnum :"+  "   installmode:"+installmode+"  hardware_pkg:"+hardware_pkg);
						if(installmode<0){
							installmode = VRConfig.CameraPutModelFaceFront;
						}
						monitor.attachCamera(mCameraManagerment.getexistCamera(mDevUID), 0,mDevice.installmode,mDevice,mDevice.snapshot,true);
						monitor.setCameraPutModel(installmode);
						monitor.setCameraHardware_pkg(hardware_pkg);
						this.monitor.setHorizontal(false);
						monitor.refreshBitmap(bitmap) ;
					}else if( nameSplit.length<=6){
						monitor.setCameraPutModel( VRConfig.CameraPutModelFaceFront);
						monitor.setCameraHardware_pkg(HARDWAEW_PKG.MF_STD_1145);
						this.monitor.setHorizontal(false);
						monitor.refreshBitmap(createDefaultImage(bitmap)) ;
					}
//					monitor.setCameraPutModel();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


		}else{
			handler.sendEmptyMessage(1112);
		}
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	}
	private Bitmap createDefaultImage(Bitmap dpDrawable )
	{
		if(dpDrawable!=null){
			if(dpDrawable.getWidth()%8!=0){
				Bitmap result = Bitmap.createBitmap((int) (dpDrawable.getHeight()*8), dpDrawable.getHeight()*8, Config.ARGB_8888);
				Canvas mCanvas = new Canvas(result);
				Matrix matrix = new Matrix();
				matrix.postScale(8, 8); // 长和宽放大缩小的比例
				Bitmap resizeBmp = Bitmap.createBitmap(dpDrawable, 0, 0, dpDrawable.getWidth(),  dpDrawable.getHeight(), matrix, true);
				return resizeBmp;
			}else{
				return dpDrawable;
			}
		}

		return dpDrawable;

	}
	private boolean isLandorientation = false;
	public void onConfigurationChanged(Configuration var1) {
		super.onConfigurationChanged(var1);
		Configuration var2 = this.getResources().getConfiguration();
		currenTime=myHorizontalScrollView.getNowDisplayTime();
		timeUnit=myHorizontalScrollView.view.getTimeUnit();
		if (var2.orientation == 2) {
//			if (timeThread.isAlive()) {
//				Log.i("time", "LandscapeLayout:11111111111111");
//			}
//			if (timeThread.isInterrupted()) {
//				Log.i("time", "LandscapeLayout:2222222");
//			}
//			if (timeThread.isDaemon()) {
//				Log.i("time", "LandscapeLayout:33333");
//			}
			mTime.invalidate();
			this.setupViewInLandscapeLayout();

		} else if (var2.orientation == 1) {
//			if (timeThread.isAlive()) {
//				Log.i("time", "PortraitLayou:11111111111111");
//			}
//			if (timeThread.isInterrupted()) {
//				Log.i("time", "PortraitLayou:2222222");
//			}
//			if (timeThread.isDaemon()) {
//				Log.i("time", "PortraitLayou:33333");
//			}
			mTime.invalidate();
			this.setupViewInPortraitLayout();

		}

	}

	private void initSpeakVolume() {

		mCameraManagerment.userIPCGetVolu(mDevUID, this.mDevice.getChannelIndex());
		mCameraManagerment.userIPCGetMicVolu(mDevUID, this.mDevice.getChannelIndex());


		getmic = false;
		getvolume = false;

		// this.mCamera.sendIOCtrl(0,
		// AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETVOLUME_REQ,
		// AVIOCTRLDEFs.SMsgAVIoctrlSetVolumeReq
		// .parseContent(this.mDevice.getChannelIndex(),(byte) 200));
		// this.mCamera.sendIOCtrl(0,
		// AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMICVOLUME_REQ,
		// AVIOCTRLDEFs.SMsgAVIoctrlSetVolumeReq
		// .parseContent(this.mDevice.getChannelIndex(),(byte) 200));

	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		this.setIntent(intent);
		this.mDevUID = intent.getExtras().getString("dev_uid");
		initData();
	}
	private void initData(){



		initHorizontalScrollView();

		Bundle var2 = this.getIntent().getExtras();
		this.mDevUID = var2.getString("dev_uid");

		this.mConnStatus = var2.getString("conn_status");
		//LiveViewActivity.this.mSelectedChannel = 1;
		if( mCameraManagerment!=null )
			mCameraManagerment = CameraManagerment.getInstance();
		Log.v("", "   initData   add  reconnect" +mDevUID);

		this.mDevice  = MainCameraFragment.getexistDevice(mDevUID);
		UbiaApplication.currentDeviceLive = mDevUID;

		Log.e("guo..live", "mDevice=" +mDevice);

		if(this.mDevice==null){

			DeviceInfo deviceInfo =  MainCameraFragment. getLoaclDevice(this.mDevUID);
			this.mDevice = deviceInfo;

			if(mDevice.DBID==0){
				long dbid = new DatabaseManager(this).getDBIDbyUID(mDevUID);
				mDevice.setDBID(dbid);
			}

			mDevice.country = PreferenceUtil.getInstance().getInt(Constants.COUNTRYCODE+mDevice.UID.toUpperCase() ); //加入国家，否则从推送点进来没有国家，获取云存储有问题
			MainCameraFragment.DeviceList.add(deviceInfo);
			MyCamera mCamera=  CameraManagerment.getInstance().getexistCamera(mDevUID);



			if( mCamera==null && deviceInfo!=null)
			{

				MyCamera mMyCamera = new MyCamera(deviceInfo.nickName,deviceInfo.UID, deviceInfo.viewAccount, deviceInfo.viewPassword);
				mMyCamera.registerIOTCListener(this);
				mMyCamera.connect(deviceInfo.UID);
				mMyCamera.start(0, deviceInfo.viewAccount,  deviceInfo.viewPassword);
				mMyCamera.LastAudioMode = 1;
				mMyCamera.hardware_pkg = deviceInfo.hardware_pkg;
				mCamera = mMyCamera;
				mCameraManagerment.AddCameraItem(mMyCamera);
				Log.v("", "  mMyCamera   add  reconnect");
				HARDWAEW_INFO mdd =VRConfig.getInstance().getDeviceType(mCamera.hardware_pkg);
				if(mCamera.hardware_pkg==HARDWAEW_PKG.CM_BELL_VR_5230_2466 || mCamera.hardware_pkg ==  HARDWAEW_PKG.CM_BELL_VR_9732_5112){
					//默认不显示安装方式 ,门铃设备固定不显安装方式
					isdoolbeel = 1;
				}

				if(mdd.type == VRConfig.VRDEVICE && mdd.width==960 && mdd.height==960 ){
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}else{
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				}
			}
			Log.v("", " 22222 mMyCamera   add  reconnect"+ deviceInfo.viewAccount+"    deviceInfo.viewPassword:"+ deviceInfo.viewPassword);

		}else{
			MyCamera mCamera = CameraManagerment.getInstance().getexistCamera(mDevUID);
			HARDWAEW_INFO mdd = VRConfig.getInstance().getDeviceType(mCamera.hardware_pkg);
			mDevice.hardware_pkg = mCamera.hardware_pkg;

			if(mDevice.DBID==0){
				long dbid = new DatabaseManager(this).getDBIDbyUID(mDevUID);
				mDevice.setDBID(dbid);
			}

			if (mDevice.viewPassword.length() < 8 && mDevice.isModifyPassword ){
				showPasswordSimpleDialog();
			}

			if (mCamera.hardware_pkg == HARDWAEW_PKG.CM_BELL_VR_5230_2466 || mCamera.hardware_pkg == HARDWAEW_PKG.CM_BELL_VR_9732_5112) {
				//默认不显示安装方式 ,门铃设备固定不显安装方式
				isdoolbeel = 1;
			}

			if (mdd.type == VRConfig.VRDEVICE && mdd.width == 960 && mdd.height == 960) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
		}
		Log.v("", " 1111 mMyCamera   add  reconnect"+ mDevice.viewAccount+"    deviceInfo.viewPassword:"+ mDevice.viewPassword);

		mCameraManagerment.registerIOTCListener(mDevUID,this);

//		this.mSelectedChannel = this.mDevice.getChannelIndex();

		mCameraManagerment.registerIOTCListener(mDevUID ,this );
		mCameraManagerment.userIPCStart(mDevUID, mDevice.getChannelIndex(),(byte)currentplaySeq );
		mCameraManagerment.StartPPPP(mDevUID,this.mDevice.viewAccount, this.mDevice.viewPassword);
		if(this.mDevice!=null && this.mDevice.device_connect_state == MainCameraFragment.CONNSTATUS_CONNECTED){
			mCameraManagerment.StartClient(mDevUID,this.mDevice.viewAccount, this.mDevice.viewPassword);
		}
		// Log.i("IOTCamera1", "onCreat startShow!");
		// this.mCamera.startShow(this.mSelectedChannel, true);


		final View var8 = LayoutInflater.from(this).inflate(
				R.layout.two_way_audio, (ViewGroup) null);
		((RadioGroup) var8.findViewById(R.id.radioAudio))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup var1, int var2) {
						if (var2 == var8.findViewById(R.id.radioSpeaker)
								.getId()) {

							mCameraManagerment.userIPCstopSpeak(mDevUID);
							mCameraManagerment.userIPCstartListen(mDevUID);
							LiveViewGLviewActivity.this.mIsListening = true;
							LiveViewGLviewActivity.this.mIsSpeaking = false;
						} else if (var2 == var8.findViewById(
								R.id.radioMicrophone).getId() ) {
							mCameraManagerment.userIPCstartSpeak(mDevUID);
							mCameraManagerment.userIPCstopListen(mDevUID);
							LiveViewGLviewActivity.this.mIsListening = false;
							LiveViewGLviewActivity.this.mIsSpeaking = true;
							return;
						}
					}
				});


		if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
			this.setupViewInLandscapeLayout();
		} else {
			this.setupViewInPortraitLayout();
		}


		checkDoorbellSound();//解决，横屏切到竖屏仍会开启声音

		mCameraManagerment.userIPCstartShow(mDevUID);

		mCameraManagerment.userIPCGetAdvanceSetting(mDevUID);//获取云存储状态

		if(mDevice.device_connect_state == CONNSTATUS_WRONG_PASSWORD){
			handler.sendEmptyMessage(CONNSTATUS_WRONG_PASSWORD);
		}

	}
	private android.app.ActionBar mActionBar;
	private boolean isFirstEnter=true;
	/** 帧动画 */
	private AnimationDrawable mIvFrameAnim;
	public void onCreate(Bundle var1) {
		super.onCreate(var1);
		final Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		this.setContentView(R.layout.live_view_glview_portrait);
		Bundle var2 = this.getIntent().getExtras();
		isdoolbeel= var2.getInt ("isdoolbeel");
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("action.newDeviceCallBroadcastReceiver");
		//  registerReceiver(newDeviceCallBroadcastReceiver, intentFilter); //没用？？

		initData();
	}
	private void initHorizontalScrollView() {

		myHorizontalScrollView =(com.view.timeline.MyHorizontalScrollView)this.findViewById(R.id.myHorizontalScrollView);
		currenTime= myHorizontalScrollView.scorllToCurrentTime();
		myHorizontalScrollView .setTimeLinePlayCallBackInterface(this);
		timeUnit=myHorizontalScrollView.view.getTimeUnit();
		myHorizontalScrollView.setRollBackToCurrentTimeCallbackInterface(this);
		myHorizontalScrollView.setEventColor(R.color.color_less_blue_timeline);
	}
	com.view.timeline.MyHorizontalScrollView myHorizontalScrollView ;
	private void getTimeLineBitmap(){
		hasgetBitMapDataCount++;
		Calendar mCalendar = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
		int startTime = (int) (mCalendar.getTimeInMillis()/1000)-3600*24*7;
		int endTime = (int) (mCalendar.getTimeInMillis()/1000);
		mCameraManagerment.userIPCRecordBitmap(mDevUID, startTime, endTime);//1-60:1-60sec, 61-120:1-60min, 121-144:1-24hrs,145-176:1-31days

	/*	startTime = (int) (mCalendar.getTimeInMillis()/1000)-3600*24*1; //获取事件列表
		mCameraManagerment.userIPCRecordEventList(mDevUID, startTime, endTime);*/
	}
	private void initView() {
		mlastVideoWidth = 0;
		back = (ImageView) this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		title = (TextView) this.findViewById(R.id.title);
		this.findViewById(R.id.left_ll).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						LiveViewGLviewActivity.this.quit(-1);
					}
				});
		back.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						LiveViewGLviewActivity.this.quit(-1);
					}
				});
		title_img = (ImageView) findViewById(R.id.title_img);
		title_img.setImageResource(R.drawable.nty_app_icon);
		title_img.setVisibility(View.GONE);
		title_father = (RelativeLayout) this.findViewById(R.id.title_father);
		recode_time_txt = (TextView) this.findViewById(R.id.recode_time_txt);
		right2_tv = (TextView) this.findViewById(R.id.right2_tv);
		right2_tv.setTextColor(getResources().getColor(R.color.color_skin_color));

		right2_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {changeCH();}
		});
		if(right2_tv!=null){
			//right2_tv.setEnabled(false);
			Resources res = getResources();
			showright_tv();
			if (mDevice.getChannelIndex()  == 0) {
				right2_tv.setText(res.getString(R.string.AVIOCTRLDEFs_page8_vga ));
			} else {
				right2_tv.setText(res.getString(R.string.AVIOCTRLDEFs_page8_hd ));
			}
		}
		mProgressBar =  (ProgressBar) this.findViewById(R.id.MyprogressBar);
		mProgressBar.setVisibility(View.VISIBLE);
		mProgressBar.bringToFront();
		this.txtOnlineNumberlive = (TextView) this .findViewById(R.id.txtOnlineNumberlive);
		txt_time =  (TextView) this.findViewById(R.id.txt_time);
		txtLowPower =  (TextView) this.findViewById(R.id.txt_lowpower);
	}

	protected void StopAllSpeak() {
		// TODO Auto-generated method stub
		try {
			mCameraManagerment.userIPCstopListen(mDevUID);
			mCameraManagerment.userIPCstopSpeak(mDevUID);
			LiveViewGLviewActivity var6 = LiveViewGLviewActivity.this;
			LiveViewGLviewActivity.this.mIsSpeaking = false;
			var6.mIsListening = false;
			Log.i("onTouch", "button_say.StopAllSpeak");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void StartAudio() {
		// TODO Auto-generated method stub
		Log.i("onTouch", "button_say.StartAudio");
		try {

			mCameraManagerment.userIPCstartListen(mDevUID);
//			LiveViewGLviewActivity.this.mIsListening = true;//open anytime
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void StopAudio() {
		try {
			Log.i("onTouch", "button_say.StopAudio");
			mCameraManagerment.userIPCstopListen(mDevUID);
//			LiveViewGLviewActivity.this.mIsListening = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void StartTalk() {
		// TODO Auto-generated method stub
		if(recording || isPlaybackData || isPlayMp4){
			return;
		}
		try {
			mCameraManagerment.userIPCstartSpeak(mDevUID);
			LiveViewGLviewActivity.this.mIsSpeaking = true;
			Log.i("onTouch", "button_say.StartTalk");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void StopTalk() {
		try {
			Log.i("onTouch", "button_say.StopTalk");
			mCameraManagerment.userIPCstopSpeak(mDevUID);
			LiveViewGLviewActivity.this.mIsSpeaking = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean recording = false;



	public boolean onKeyDown(int var1, KeyEvent var2) {
		DialogUtil.getInstance().onKeyDown(var1, var2);
		switch (var1) {
			case 4:
				this.quit(-1);
			default:
				return super.onKeyDown(var1, var2);
		}
	}
	public static int dip2px(Context context,float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	private PopupWindow mPopupWindowAdd;
	private View mPopViewAdd;
	private LinearLayout la_ll;

	private boolean isruningRefresh;
	private boolean onActivityRuning;
	public void initAddPopupWindow() {
		mPopViewAdd = LayoutInflater.from(LiveViewGLviewActivity.this).inflate(
				R.layout.live_setting_pop, null);
		mPopupWindowAdd = new PopupWindow(mPopViewAdd,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		//mPopupWindowAdd.setBackgroundDrawable(new ColorDrawable(0));

//		WindowManager wm = (WindowManager) this.getWindowManager();
//
//	    int width = wm.getDefaultDisplay().getWidth();
//	    int height = wm.getDefaultDisplay().getHeight();
//	    if(width <=800 ){
//	    	popwinoffset = 90;
//	    }else{
//	    	popwinoffset = 130;
//	    }
		Context  cntxt = getApplicationContext();
		popwinoffset = dip2px(cntxt, 88/2);

		la_ll = (LinearLayout) mPopViewAdd.findViewById(R.id.la_ll);
		la_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lastsnap();
				Bundle var21 = new Bundle();
				Intent var3 = new Intent();
				var21.putString("dev_uid", LiveViewGLviewActivity.this.mDevice.UID);
				var21.putString("dev_nickName",
						LiveViewGLviewActivity.this.mDevice.nickName);
				var21.putString("view_acc",
						LiveViewGLviewActivity.this.mDevice.viewAccount);
				var21.putString("view_pwd",
						LiveViewGLviewActivity.this.mDevice.viewPassword);
				var21.putInt("camera_channel",
						LiveViewGLviewActivity.this.mDevice.getChannelIndex());

				var3.putExtras(var21);
				var3.setClass(LiveViewGLviewActivity.this, PhotoGridActivity.class);
				startActivity(var3);
				mPopupWindowAdd.dismiss();
			}
		});

		mPopupWindowAdd.setFocusable(true);
		mPopupWindowAdd.setOutsideTouchable(true);
		mPopupWindowAdd.setBackgroundDrawable(new ColorDrawable(0));
		mPopupWindowAdd
				.setOnDismissListener(new PopupWindow.OnDismissListener() {

					@Override
					public void onDismiss() {
						mPopupWindowAdd.dismiss();
					}
				});
	}

	private void savePhoto() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				Log.i("IOTCamera", "savePhoto Image path========>>>>>>>>>>>"  );

				if (mCameraManagerment.userIPCisChannelConnected(mDevUID)) {
					if (isSDCardValid()) {
						// File var20 = new File(Environment
						// .getExternalStorageDirectory()
						// .getAbsolutePath()
						// + "/Snapshot/");
						final File var20 = new File(Environment.getExternalStorageDirectory()
								.getAbsolutePath() + "/DCIM/Camera/");
//				final File var20 = new File(Environment.getExternalStorageDirectory()
//						.getAbsolutePath() + "/UBellSnapshot/");
						Log.i("IOTCamera", "Image path:" + var20.getAbsolutePath());
//				File var21 = new File(var20.getAbsolutePath() + "/"
//						+ this.mDevUID);
						if (!var20.exists()) {
							try {
								var20.mkdir();
							} catch (SecurityException var32) {
								// super.onOptionsItemSelected(var1);
							}
						}

//				if (!var21.exists()) {
//					try {
//						var21.mkdir();
//					} catch (SecurityException var31) {
						// super.onOptionsItemSelected(var1);
//					}
//				}


						Bitmap var23 = mCameraManagerment.userIPCSnapshot(mDevUID);

						if (var23 == null) {
							Log.v("IOTCamera", "this.bitmap is  null------------------");
							// Toast.makeText(this,
							// this.getText(R.string.tips_snapshot_failed), 0)
							// .show();
						}
						String filename =   "/"+BuildConfig.FLAVOR+"_" + getFileNameWithTime(var23,mDevice.installmode,mCameraManagerment.getexistCamera(mDevUID).hardware_pkg);
						String var22 = var20.getAbsoluteFile()+filename  ;
						if (var23 != null && LiveViewGLviewActivity.this.saveImage(var22, var23)) {

//
//					String[] var24 = new String[] { var22.toString() };
//					String[] var25 = new String[] { "image/*" };
//					OnScanCompletedListener var26 = new OnScanCompletedListener() {
//						public void onScanCompleted(String var1, Uri var2) {
//							Log.i("ExternalStorage", "Scanned " + var1 + ":");
//							Log.i("ExternalStorage", "-> uri=" + var2);
							Message var5 =   handler .obtainMessage();
							var5.what = STS_SNAPSHOT_SAVED;
							handler.sendMessage(var5);
//						}
//					};
//					MediaScannerConnection.scanFile(LiveViewGLviewActivity.this, var24, var25, var26);
							syncAlbum(var22,var20);
						} else {
//					  Toast.makeText(this,
//							  	this.getText(R.string.tips_snapshot_failed), 0)
//					 .show();
						}
					} else {
						LiveViewGLviewActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(LiveViewGLviewActivity.this,
										LiveViewGLviewActivity.this.getText(R.string.page8_page30_tips_no_sdcard).toString(), 0)
										.show();
							}
						});

					}
				}
			}
		}).start();
	}


	private void stopRecord(){
		if(recording){
			seccount = 0;
			recordStartseccount = 0;
			mCameraManagerment.userIPCstopRecode(mDevUID);
			String[] var24 = new String[] { ((Camera)mCameraManagerment.getexistCamera(mDevUID)).getRecordFilePath() };
			String[] var25 = new String[] { "video/*" };
			OnScanCompletedListener var26 = new OnScanCompletedListener() {
				public void onScanCompleted(String var1, final Uri var2) {
					Log.i("ExternalStorage", "Scanned " + var1 + ":");
					Log.i("ExternalStorage", "-> uri=" + var2);
					LiveViewGLviewActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.i("ExternalStorage", "》>>>>>> 保存相册成功-> uri=" + var2);
							Toast.makeText(
									LiveViewGLviewActivity.this,
									LiveViewGLviewActivity.this.getText(R.string.save_record) , Toast.LENGTH_LONG).show();

						}
					});

				}
			};
			MediaScannerConnection.scanFile(this, var24, var25, var26);
			if (right_image4 != null) {
				right_image4.setImageResource(R.drawable.record_off);
			}
			if (img_video != null) {
				img_video.setImageResource(R.drawable.record_off);
			}
			recording = false;
			if (ImageViewRec != null)
				ImageViewRec.setVisibility(View.GONE);
			if (recode_time_txt != null) {
				recode_time_txt.setText(AVIOCTRLDEFs.secToTime(0));
				recode_time_txt.setVisibility(View.GONE);
			}
		}
	}
	private void saveVideo() {

		recording = !recording;
		try {
			if (!mCameraManagerment.userIPCisSavingVideo(mDevUID)) {
				seccount = 0;
				recordStartseccount=0;
				if (recode_time_txt != null) {
					recode_time_txt.setText(AVIOCTRLDEFs.secToTime(0));
					recode_time_txt.setVisibility(View.VISIBLE);
				}
				int fps = 15;
				int package_hardware = ((Camera)mCameraManagerment.getexistCamera(mDevUID)).hardware_pkg;
				VRConfig mVRConfig = VRConfig.getInstance();
				HARDWAEW_INFO device_hardware  = mVRConfig.getDeviceType(package_hardware);
				if(device_hardware.resolution==1080){
					fps = 13;
				}
				mCameraManagerment.userIPCstartRecode(mDevUID,fps);
				if (right_image4 != null) {
					right_image4.setImageResource(R.drawable.record_on);
				}
				if (img_video != null) {
					img_video.setImageResource(R.drawable.record_on);
				}
				recording = true;
				if (ImageViewRec != null)
					ImageViewRec.setVisibility(View.VISIBLE);
			} else {
				seccount = 0;
				recordStartseccount = 0;
				mCameraManagerment.userIPCstopRecode(mDevUID);
				String[] var24 = new String[] { ((Camera)mCameraManagerment.getexistCamera(mDevUID)).getRecordFilePath() };
				String[] var25 = new String[] { "video/*" };
				OnScanCompletedListener var26 = new OnScanCompletedListener() {
					public void onScanCompleted(String var1,final Uri var2) {
						Log.i("ExternalStorage", "Scanned " + var1 + ":");
						Log.i("ExternalStorage", "-> uri=" + var2);
						LiveViewGLviewActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								Log.i("ExternalStorage", "》>>>>>> 保存相册成功-> uri=" + var2);
								Toast.makeText(
										LiveViewGLviewActivity.this,
										LiveViewGLviewActivity.this.getText(R.string.save_record) , Toast.LENGTH_LONG).show();

							}
						});
					}
				};
				MediaScannerConnection.scanFile(this, var24, var25, var26);
				if (right_image4 != null) {
					right_image4.setImageResource(R.drawable.record_off);
				}
				if (img_video != null) {
					img_video.setImageResource(R.drawable.record_off);
				}
				recording = false;
				if (ImageViewRec != null)
					ImageViewRec.setVisibility(View.GONE);
				if (recode_time_txt != null) {
					recode_time_txt.setText(AVIOCTRLDEFs.secToTime(0));
					recode_time_txt.setVisibility(View.GONE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.invalidateOptionsMenu();

	}

	public void setRecodestatue(){

		try {
			if (recording) {
				if (recode_time_txt != null) {
					recode_time_txt.setText(AVIOCTRLDEFs.secToTime((int) recordStartseccount/1000));

					recode_time_txt.setVisibility(View.VISIBLE);
				}

				if (right_image4 != null) {
					right_image4.setImageResource(R.drawable.record_on);
				}
				if (img_video != null) {
					img_video.setImageResource(R.drawable.record_on);
				}

				if (ImageViewRec != null)
					ImageViewRec.setVisibility(View.VISIBLE);
			} else {
				if (recode_time_txt != null) {
					recode_time_txt.setText(AVIOCTRLDEFs.secToTime((int) recordStartseccount/1000));
					recode_time_txt.setVisibility(View.GONE);
				}
				if (right_image4 != null) {
					right_image4.setImageResource(R.drawable.record_off);
				}
				if (img_video != null) {
					img_video.setImageResource(R.drawable.record_off);
				}

				if (ImageViewRec != null)
					ImageViewRec.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public boolean onOptionsItemSelected(MenuItem var1) {
		try {
			int var2 = var1.getItemId();
			Log.i("IOTCamera",
					"LiveViewActivity onOptionsItemSelected getItemId:" + var2);
			if (var2 == 100) {

				// File var3 = new
				// File(Environment.getExternalStorageDirectory()
				// .getAbsolutePath() + "/Snapshot/" + this.mDevUID);
				File var3 = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/DCIM/Camera/"  );

				Log.i("IOTCamera", "" + var3.getAbsolutePath());

				String[] var4 = var3.list();
				if (var4 != null && var4.length > 0 && this.monitor != null) {

					(new StringBuilder(String.valueOf(var3.getAbsolutePath())))
							.append("/").append(var4[-1 + var4.length])
							.toString();
					// var3.getAbsolutePath() + "/" + var4[-1 + var4.length];
					Intent var6 = new Intent(this,
							GridViewGalleryActivity.class);
					var6.putExtra("snap", this.mDevUID);
					var6.putExtra("images_path", var3.getAbsolutePath());
					this.startActivity(var6);
				} else {
					Toast.makeText(
							this,
							this.getText(R.string.page10_page8_tips_no_snapshot_found)
									.toString(), 0).show();
				}

				// Fragment newContent = new PhotoManageFragment();
				// Bundle bundle = new Bundle();
				// bundle.putInt("key", 1);
				// newContent.setArguments(bundle);
				// //if (getActivity() instanceof MainActivity)
				// {
				// // MainActivity fca = (MainActivity.) getActivity();
				// Intent mainIntent = null;
				// mainIntent = new Intent(LiveViewActivity.this,
				// MainActivity.class);
				// this.startActivity(mainIntent);
				// this.finish();
				// // fca.switchContent(newContent, "");
				// }
				//

			} else if (var2 == OPT_MENU_ITEM_SNAPSHOT) {
				savePhoto();
			} else if (var2 == OPT_MENU_ITEM_AUDIOCTRL) {
				changestatue_voice();
			}

			else if (var2 == OPT_MENU_ITEM_SETTING) {

				Bundle bundle = new Bundle();
				Intent intent = new Intent();
				bundle.putString("dev_uid", LiveViewGLviewActivity.this.mDevice.UID);
				bundle.putString("dev_nickName",
						LiveViewGLviewActivity.this.mDevice.nickName);
				bundle.putString("view_acc",
						LiveViewGLviewActivity.this.mDevice.viewAccount);
				bundle.putString("view_pwd",
						LiveViewGLviewActivity.this.mDevice.viewPassword);
				bundle.putInt("camera_channel",
						LiveViewGLviewActivity.this.mDevice.getChannelIndex());
				bundle.putInt("battery",battery);
				intent.putExtras(bundle);
				intent.setClass(LiveViewGLviewActivity.this, SettingActivity.class);
				startActivityForResult(intent, -11);//(intent);


			}

			else if (var2 == OPT_MENU_ITEM_DEVICEVIDEO) {
				lastsnap();
				Bundle var21 = new Bundle();
				Intent var3 = new Intent();
				var21.putString("dev_uid", LiveViewGLviewActivity.this.mDevice.UID);
				var21.putString("dev_nickName",
						LiveViewGLviewActivity.this.mDevice.nickName);
				var21.putString("view_acc",
						LiveViewGLviewActivity.this.mDevice.viewAccount);
				var21.putString("view_pwd",
						LiveViewGLviewActivity.this.mDevice.viewPassword);
				var21.putInt("camera_channel",
						LiveViewGLviewActivity.this.mDevice.getChannelIndex());
				var3.putExtras(var21);
				var3.setClass(LiveViewGLviewActivity.this, AVIOCTRLDEFs.class);
				startActivity(var3);

			} else if (var2 == OPT_MENU_ITEM_LOCALVIDEO) {
				lastsnap();
				Bundle var21 = new Bundle();
				Intent var3 = new Intent();
				var21.putString("dev_uid", LiveViewGLviewActivity.this.mDevice.UID);
				var21.putString("dev_nickName",
						LiveViewGLviewActivity.this.mDevice.nickName);
				var21.putString("view_acc",
						LiveViewGLviewActivity.this.mDevice.viewAccount);
				var21.putString("view_pwd",
						LiveViewGLviewActivity.this.mDevice.viewPassword);
				var21.putInt("camera_channel",
						LiveViewGLviewActivity.this.mDevice.getChannelIndex());

				// var3.putExtra("deviceInfo", LiveViewActivity.this.mDevice);
				var3.putExtras(var21);
				var3.setClass(LiveViewGLviewActivity.this, PhotoGridActivity.class);
				startActivity(var3);

				// quit(100);

			} else if (var2 == OPT_MENU_ITEM_RECORD) {
				saveVideo();
			} else if (var2 == android.R.id.home) {
				quit(-1);

			}

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			return super.onOptionsItemSelected(var1);
		}
		return super.onOptionsItemSelected(var1);
	}

	public void changeCH() {

		if(recording){
			getHelper().showMessageLong(getText(R.string.recording)+"");
			return;
		}
		ArrayAdapter ArrayAdaptervar4 = new ArrayAdapter(LiveViewGLviewActivity.this,
				17367043);


		Resources res = getResources();
		if (mDevice.getChannelIndex()  == 0) {
			mDevice.setChannelIndex(1)  ;
			this.right2_tv.setText(res.getString(R.string.AVIOCTRLDEFs_page8_hd));
		} else {
			mDevice.setChannelIndex(0) ;
			this.right2_tv.setText(res.getString(R.string.AVIOCTRLDEFs_page8_vga));
		}
		DatabaseManager mDM = new DatabaseManager( this);
		mDM.updateChannel(mDevice.UID, 		mDevice.getChannelIndex());
		if(mProgressBar!=null)
			mProgressBar.setVisibility(View.VISIBLE);
		mCameraManagerment.userIPCStart(mDevUID, mDevice.getChannelIndex(),(byte)currentplaySeq );//1-60:1-60sec, 61-120:1-60min, 121-144:1-24hrs,145-176:1-31days

	}
	public void setstatue_voice_icon() {

		if (!mIsSpeaking) {
			if (right_image3 != null) {
				right_image3.setImageResource(R.drawable.mic_off);
			}
			if (img_mic != null) {
				img_mic.setImageResource(R.drawable.mic_off);
			}
			this.invalidateOptionsMenu();
		} else {
			if (right_image3 != null) {
				right_image3.setImageResource(R.drawable.mic_on);
			}
			if (img_mic != null) {
				img_mic.setImageResource(R.drawable.mic_on);
			}
			this.invalidateOptionsMenu();
		}


	}
	public void changestatue_voice() {

		if (mIsSpeaking) {
			if (right_image3 != null) {
				right_image3.setImageResource(R.drawable.mic_off);
			}
			if (img_mic != null) {
				img_mic.setImageResource(R.drawable.mic_off);
			}
			StopTalk();
			this.invalidateOptionsMenu();
		} else {
			if (right_image3 != null) {
				right_image3.setImageResource(R.drawable.mic_on);
			}
			if (img_mic != null) {
				img_mic.setImageResource(R.drawable.mic_on);
			}

			StartTalk();
			this.invalidateOptionsMenu();
		}

	}

	public void lastsnap() {
		if (mCameraManagerment.userIPCisChannelConnected(mDevUID)&& !showGridViewBitmap) {
			if (isSDCardValid()) {
				File var20 = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/LastSnapshot/");
				Log.i("IOTCamera", "鎶撳浘锛�" + var20.getAbsolutePath());
				File var21 = new File(var20.getAbsolutePath() + "/"
						+ this.mDevUID);
				if (!var20.exists()) {
					try {
						var20.mkdir();
					} catch (SecurityException var32) {
						// super.onOptionsItemSelected(var1);
					}
				}

				if (!var21.exists()) {
					try {
						var21.mkdir();
					} catch (SecurityException var31) {
						// super.onOptionsItemSelected(var1);
					}
				}

				final String var22 = var21.getAbsoluteFile() + "/" + this.mDevUID
						+ ".jpg";// getFileNameWithTime();
				final Bitmap var23;
				var23 =   mCameraManagerment.userIPCSnapshot(mDevUID);
				mDevice.snapshot = var23;
				if (var23 == null) {

					return;
				}
				if (var23 != null) {
					saveImage(var22, var23);

				}

			} else {

			}
		}

	}

	protected void onPause() {
		super.onPause();
		mCameraManagerment.userIPCStopAlStream(mDevUID);
		isBackgroundRunning = true;
		onActivityRuning = false;
		isruningRefresh = false;
		timeRunning = false;
		isPlayMp4 = false;
		ptzThreadRunning = false;
//			返回直播，关闭声音
		mCameraManagerment.userIPCMuteControl(mDevUID,true);
		LiveViewGLviewActivity.this.mIsListening = false;
		UbiaApplication.currentDeviceLive = "";
		stopRecord();
		//isdoolbeel = 0;
//		 saveVideo();
	}

	protected void onResume() {
		super.onResume();
//		initData();
		isruningRefresh = false;
		onActivityRuning = true;
		mlastVideoWidth = 0;
		UbiaApplication.currentDeviceLive = mDevUID;

		if(  isBackgroundRunning)
		{
			mCameraManagerment.Init();
			mCameraManagerment.StartPPPP(mDevUID, mDevice.viewAccount, mDevice.viewPassword);
		}
		if(!showGridViewBitmap ){//mP4前后台切换
//    		直播前后
			if( !UbiaApplication.BUILD_CHECK_PLAYVOICE)////check playVoice; detail default close
			{
				try {

//    				返回直播，关闭声音
					mCameraManagerment.userIPCMuteControl(mDevUID,true);
					LiveViewGLviewActivity.this.mIsListening = false;
					StartAudio();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(voiceMute != null){
					voiceMute.setImageResource(R.drawable.sound_off);
				}
			}
			else{
				try {

					mCameraManagerment.userIPCMuteControl(mDevUID,false);
					LiveViewGLviewActivity.this.mIsListening = true;
					StartAudio();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(voiceMute != null){
					voiceMute.setImageResource(R.drawable.sound_on);
				}
			}
			checkDoorbellSound();

		}else{
//    		本地播放前后
			StopAudio();
			if(isPlayMp4){
				try {

					mCameraManagerment.userIPCMuteControl(mDevUID,false);
					LiveViewGLviewActivity.this.mIsListening = true;

				} catch (Exception e) {
					e.printStackTrace();
				}
				if(voiceMute != null){
					voiceMute.setImageResource(R.drawable.sound_on);
				}
			}else{
				try {

					mCameraManagerment.userIPCMuteControl(mDevUID,true);
					LiveViewGLviewActivity.this.mIsListening = false;

				} catch (Exception e) {
					e.printStackTrace();
				}
				if(voiceMute != null){
					voiceMute.setImageResource(R.drawable.sound_off);
				}
			}


		}

		isBackgroundRunning = false;
		//MainCameraFragment.nShowMessageCount = 0;
		try {

			if (  (!showGridViewBitmap)) {
				// initSpeakVolume();
				monitor.restartPlay();
				mCameraManagerment.userIPCstartShow(mDevUID);
				if (this.mIsListening) {
					mCameraManagerment.userIPCstartListen (mDevUID);
				}

				if (this.mIsSpeaking) {
					mCameraManagerment.userIPCstartSpeak (mDevUID);
				}

			}

			Resources res = getResources();
			if (mDevice.getChannelIndex()  == 0) {
				right2_tv.setText(res.getString(R.string.AVIOCTRLDEFs_page8_vga ));
			} else {
				right2_tv.setText(res.getString(R.string.AVIOCTRLDEFs_page8_hd ));
			}
			if(mProgressBar!=null)
				mProgressBar.setVisibility(View.VISIBLE);
			if( showGridViewBitmap) {
				mProgressBar.setVisibility(View.GONE);
			}
			title.setText(mDevice.nickName);
			if(myHorizontalScrollView!=null)
				myHorizontalScrollView.scorllToCurrentTime();
		} catch (Exception e) {
			e.printStackTrace();
		}


		GetTimelineBitMapCallback_Manager.getInstance().setmCallback(new GetTimelineBitMapbackInterface() {

			@Override
			public void SetPushNoteStatecallback(boolean state) {
				// TODO Auto-generated method stub

			}

			@Override
			public void GetPushNoteStatecallback(NoteInfoData mNoteInfoData,
												 boolean state) {
				// TODO Auto-generated method stub
				if(myHorizontalScrollView!=null){
					myHorizontalScrollView.setNoteInfoData(mNoteInfoData);
					LiveViewGLviewActivity.this.mNoteInfoData =  mNoteInfoData;
					myHorizontalScrollView.scorllToTheTime(currenTime);
					Log.v("","事件bitmap返回！！");
					hasgetBitMapData = true;
				}
			}
		});

		DeviceStateCallbackInterface_Manager.getInstance().setmCallback(new DeviceStateCallbackInterface() {

			@Override
			public void DeviceStateCallbackInterface(String did, int type, int param) {
				// TODO Auto-generated method stub
			}

			@Override
			public void DeviceStateCallbackLiveInterface(final String did, final int type,
														 final int param) {
				// TODO Auto-generated method stub
				Log.d("test", LiveViewGLviewActivity.this.toString()+ "did==" + did + "  msgType=" + type +"   msgParam ="+param);
				final DeviceInfo var7 = MainCameraFragment.getexistDevice(did);
				if (var7 == null || !did.equals(mDevUID) || isBackgroundRunning)
				{
					return;
				}
				var7.device_connect_state = param;
				handler.sendEmptyMessage(param);
				switch (param) {
					case MainCameraFragment.CONNSTATUS_CONNECTING:
					{
						Resources res = getResources();
						String text = res
								.getString(R.string.page26_page8_connstus_connecting);
						var7.Status = text;
						var7.online = false;
						var7.offline = false;
						var7.lineing = true;
					}
					break;
					case MainCameraFragment.CONNSTATUS_CONNECTED:
					{
						Resources res = getResources();
						String text = res.getString(R.string.fragment_liveviewactivity_publiccameraactivity_setupadddeviceactivity_state_connected);
						var7.Status = text;
						var7.online = true;
						var7.offline = false;
						var7.lineing = false;
						if (mIsSpeaking) {
							mCameraManagerment.userIPCstartSpeak (mDevUID);
						}
						mCameraManagerment.StartClient(  did, var7.viewAccount, var7.viewPassword ) ;
					}
					break;
					case MainCameraFragment.CONNSTATUS_DISCONNECTED:
						if (var7 != null) {
							Resources res = getResources();
							String text = res
									.getString(R.string.fragment_liveviewactivity_mainactivity_state_disconnected);
							var7.Status = text;
							var7.online = false;
							var7.offline = true;
							var7.lineing = false;
							mCameraManagerment.StopPPPP(did);
						}

						break;
					case MainCameraFragment.CONNSTATUS_UNKOWN_DEVICE:
						if (var7 != null) {
							Resources res = getResources();
							String text = res
									.getString(R.string.page8_connstus_unknown_device);
							var7.Status = text;
							var7.online = false;
							var7.offline = true;
							var7.lineing = false;
							mCameraManagerment.StopPPPP(did);
						}
						break;
					case CONNSTATUS_WRONG_PASSWORD:
						if (var7 != null) {
							Resources res = getResources();
							String text = res
									.getString(R.string.page8_connstus_wrong_password);
							var7.Status = text;
							//	getHelper().showMessage(text);
							var7.offline = true;
							var7.lineing = false;
							var7.online = false;
						}

						break;
					case MainCameraFragment.CONNSTATUS_RECONNECTION:
						if (var7 != null) {
							Resources res = getResources();
							String text = res
									.getString(R.string.fragment_liveviewactivity_mainactivity_state_disconnected);
							var7.Status = text;
							var7.online = false;
							var7.offline = true;
							var7.lineing = false;
							new Thread(new  Runnable() {

								@Override
								public void run() {
									mCameraManagerment.StopPPPP(did);
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									mCameraManagerment.StartPPPP(  did, var7.viewAccount, var7.viewPassword ) ;
								}
							}).start();


						}
						break;
					case MainCameraFragment.CONNSTATUS_CONNECTION_FAILED:
						if (var7 != null) {
							Resources res = getResources();
							String text = res
									.getString(R.string.page8_connstus_connection_failed);
							var7.Status = text;
							Log.i("main", " text =" + text);
							var7.offline = true;
							var7.lineing = false;
							var7.online = false;
							mCameraManagerment.StopPPPP(did);
						}
						break;
				}

				if (var7 == null||mDevUID==null|| !var7.UID.equalsIgnoreCase(mDevUID))
					return;
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if(  MainCameraFragment.CONNSTATUS_CONNECTED!=var7.device_connect_state && MainCameraFragment.CONNSTATUS_STARTDEVICECLIENT!=var7.device_connect_state) {
							if (mIvFrameAnim != null) {
								if (mIvFrameAnim.isRunning())
									mIvFrameAnim.stop();
								mBatteryView.setVisibility(View.GONE);
							} else {
								mBatteryView.setVisibility(View.GONE);
							}
							if (refresh_device_ib != null) {
								refresh_device_ib.setVisibility(View.VISIBLE);
							}
							if(mProgressBar!=null){
								mProgressBar.setVisibility(View.GONE);
							}
						}else{
							if (refresh_device_ib != null) {
								refresh_device_ib.setVisibility(View.GONE);
							}
							if(mProgressBar!=null){
								mProgressBar.setVisibility(View.GONE);
							}
						}
						if( MainCameraFragment.CONNSTATUS_CONNECTING==var7.device_connect_state ){
							if (refresh_device_ib != null) {
								refresh_device_ib.setVisibility(View.GONE);
							}
							if(mProgressBar!=null){
								mProgressBar.setVisibility(View.VISIBLE);
							}
						}
						switch (var7.device_connect_state) {
							case MainCameraFragment.CONNSTATUS_CONNECTING:
								if (  !mCameraManagerment.userIPCisChannelConnected(mDevUID)) {
									LiveViewGLviewActivity.this.mConnStatus = LiveViewGLviewActivity.this
											.getText(R.string.page26_page8_connstus_connecting)
											.toString();

								}
								break;
							case MainCameraFragment.CONNSTATUS_CONNECTED:
								if (mCameraManagerment.userIPCisChannelConnected(mDevUID)) {
									LiveViewGLviewActivity.this.mConnStatus = LiveViewGLviewActivity.this
											.getText(R.string.page8_connstus_connected) .toString();


								}
								break;

							case MainCameraFragment.CONNSTATUS_DISCONNECTED:
								LiveViewGLviewActivity.this.mConnStatus = LiveViewGLviewActivity.this
										.getText(R.string.page8_connstus_disconnect).toString();

								break;
							case MainCameraFragment.CONNSTATUS_UNKOWN_DEVICE:
								LiveViewGLviewActivity.this.mConnStatus = LiveViewGLviewActivity.this
										.getText(R.string.page8_connstus_unknown_device)
										.toString();

								LiveViewGLviewActivity.this.invalidateOptionsMenu();
								break;
							case CONNSTATUS_WRONG_PASSWORD:
								LiveViewGLviewActivity.this.mConnStatus = LiveViewGLviewActivity.this
										.getText(R.string.page8_connstus_wrong_password)
										.toString();

								break;
							case MainCameraFragment.CONNSTATUS_RECONNECTION:
								if(mProgressBar!=null)
								{
									mProgressBar.setVisibility(View.VISIBLE);
									mProgressBar.bringToFront();
								}
								else{
									mProgressBar =  (ProgressBar) LiveViewGLviewActivity.this.findViewById(R.id.MyprogressBar);
									mProgressBar.setVisibility(View.VISIBLE);
									mProgressBar.bringToFront();
								}

								break;
							case MainCameraFragment.CONNSTATUS_CONNECTION_FAILED:
								LiveViewGLviewActivity.this.mConnStatus = LiveViewGLviewActivity.this
										.getText(R.string.page8_connstus_connection_failed)
										.toString();


								getHelper().showMessageLong(LiveViewGLviewActivity.this.mConnStatus);
								break;
							case	MainCameraFragment.CONNSTATUS_STARTDEVICECLIENT:
							{

								if(null !=mCameraManagerment   && mDevice!=null)
									mCameraManagerment.userIPCStart(mDevUID, mDevice.getChannelIndex(),(byte)currentplaySeq );
								if (  (!showGridViewBitmap)) {
									monitor.restartPlay();
									mCameraManagerment.userIPCstartShow(mDevUID);
									if (LiveViewGLviewActivity.this.mIsListening) {
										mCameraManagerment.userIPCstartListen (mDevUID);
									}
									if (LiveViewGLviewActivity.this.mIsSpeaking) {
										mCameraManagerment.userIPCstartSpeak (mDevUID);
									}
								}
								break;
							}
						}
					}
				});
			}


		});

		DoorStateCallbackInterface_Manager.getInstance().setmCallback(
				new DoorCallBackInterface() {

					@Override
					public void DoorStatecallback(int DoorState) {
						// TODO Auto-generated method stub
						garageDoor = DoorState > 0 ? true : false;
						runOnUiThread(new Runnable() {
							public void run() {
								if(img_control_dot!=null)
									img_control_dot.setSelected(garageDoor);
							}
						});

					}
				});
		TimeLineTouchCallbackInterface_Manager.getInstance().setmCallback(
				new TimeLineTouchCallBackInterface() {
					@Override
					public void TimeLineTouchStatecallback(int mTouchstate) {


						sameTimeCount=0;
						currentplaySeq++;
						mCameraManagerment. setcurrentplaySeq(mDevUID,currentplaySeq);

						Log.e("","时间轴操作》》》》》》》》》》》》》currentplaySeq++="+currentplaySeq);
					}
				});
		LiveViewTimeStateCallbackInterface_Manager.getInstance().setmCallback(new LiveViewTimeCallBackInterface() {
			@Override
			public void TimeUTCStatecallback(final long UTCtime) {
				if(txt_time!=null){
					runOnUiThread(new   Runnable() {
						public void run() {
							if(UTCtime<1000000000){//2001/9/9 9:46:40北京时间
								return;
							}
							String text = STimeDay.getLocalTime(UTCtime*1000);
							if(txt_time!=null)
							{
								txt_time.setVisibility(View.VISIBLE);
								txt_time.setText(text);
							}
							if(image_wifi!=null){




								if(mCameraManagerment.getWifiSignal(mDevUID)!=4){
									image_wifi.setBackgroundResource(WiFiViewRec[mCameraManagerment.getWifiSignal(mDevUID)]);
								}


							}

						}
					});
				}
			}

			@Override
			public void saveTimeMsSeccallback(long saveTime) {
				// TODO Auto-generated method stub
				recordStartseccount +=   saveTime;

				seccount++;
				if(recording ){
					Log.e("Thread","==TimeUTCSecStatecallback=== getLocalTime(deviceTime):"+recordStartseccount );
				}
			}
		});
		new Thread() {
			public void run() {
				while(onActivityRuning){
					while (isruningRefresh) {
						if(monitor!=null)
							monitor.refreshPointF();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		if(isruningRefresh){
			if(img_control_runrefresh!=null)
				img_control_runrefresh.setImageResource(R.drawable.tab_cruise_pre);
		}
		else{
			if(img_control_runrefresh!=null)
				img_control_runrefresh.setImageResource(R.drawable.tab_cruise_n);
		}
		new Thread() {
			public void run() {
				int recordtimeCount = 0;
				while (onActivityRuning) {
					if (recording) {
						if (mCameraManagerment.userIPCisSavingVideo(mDevUID)) {
							Message var6 = new Message();
							recordtimeCount++;
							Bundle var5 = new Bundle();
							var5.putInt("recordtimeCount", recordtimeCount);
							var6.what = (int) recordStartseccount;

							var6.setData(var5);
							handlerrec.sendMessage(var6);
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			};
		}.start();
		if(!showGridViewBitmap)
			handler.sendEmptyMessage(1110);//显示直播的东西

		ZigbeeInfoCallbackInterface_Manager.getInstance().setmCallback(new ZigbeeInfoCallBackInterface() {

			@Override
			public void ZigbeeInfocallback(ZigbeeInfo mZigbeeInfo) {
				// TODO Auto-generated method stub
				recZigbee = true;
				LiveViewGLviewActivity.this.mZigbeeInfo = mZigbeeInfo;
				runOnUiThread(new   Runnable() {
					public void run() {
						showQrcodeDialo();
					}
				});

			}
		});

		if(monitor!=null && mDevice!=null && !showGridViewBitmap  )
		{
			this.monitor.attachCamera(mCameraManagerment.getexistCamera(mDevUID), 0,mDevice.installmode,mDevice,mDevice.snapshot,true);
			monitor.setCameraPutModel(mDevice.installmode);
		}
		hasgetBitMapData = false; //reget bitmap
		mNoteInfoData = null;
		hasgetBitMapDataCount= 0;
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if((!hasgetBitMapData) && myHorizontalScrollView!=null && (hasgetBitMapDataCount<hasgetBitMapDataMaxRetryCount)){
					if(mNoteInfoData!=null)
					{
						myHorizontalScrollView.setNoteInfoData(mNoteInfoData);
						myHorizontalScrollView.scorllToTheTime(currenTime);
					}
					else{
						getTimeLineBitmap();
						mHandler.postDelayed(this, 5000);
					}
				}else{
					if(hasgetBitMapData)
						mHandler.removeCallbacks(this);
				}
				if(hasgetBitMapDataMaxRetryCount<hasgetBitMapDataCount){
					mHandler.removeCallbacks(this);
				}
			}
		}, 3000);

		if (getSharedPreferences("isCloudSave",Context.MODE_PRIVATE).getBoolean(mDevice.UID, false)){
			TextView	img_control_vrmode_tv =   (TextView) findViewById(R.id.img_control_vrmode_tv);
			if(img_control_vrmode_tv!=null)
				img_control_vrmode_tv.setText(getString(R.string.cloud_save_tip));
		}else{
			TextView img_control_vrmode_tv =   (TextView) findViewById(R.id.img_control_vrmode_tv);
			if(img_control_vrmode_tv!=null)
				img_control_vrmode_tv.setText(getString(R.string.switch_tip));
		}

	}

	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(monitor!=null)
			monitor.deattachCamera() ;

		mCameraManagerment.ClearBuf(mDevUID);

		//unregisterReceiver(newDeviceCallBroadcastReceiver);
		onActivityRuning= false;
		isruningRefresh= false;
		ptzThreadRunning = false;
		UbiaApplication.currentDeviceLive = "";
		System.gc();
	}
	protected void onStop() {
		super.onStop();
		mCameraManagerment.	ClearBuf(mDevUID);
	}

	NoteInfoData mNoteInfoData;
	private boolean hasgetBitMapData;
	private int  hasgetBitMapDataCount = 0;
	private final int  hasgetBitMapDataMaxRetryCount = 3;
	public void receiveChannelInfo(Camera var1, int var2, int var3) {
		if ( var1.getmDevUID().equals(mDevUID)  ) {
			Bundle var4 = new Bundle();
			var4.putInt("avChannel", var2);
			Message var5 = this.handler.obtainMessage();
			var5.what = var3;
			var5.setData(var4);
			this.handler.sendMessage(var5);
		}

	}

	public void receiveFrameData(Camera var1, int var2, Bitmap var3) {
		if (var3!=null && var1.getmDevUID().equals(mDevUID)  && (var3.getWidth() != this.mVideoWidth || var3.getHeight() != this.mVideoHeight)) {
			mHandler.sendEmptyMessage(2);
			this.mVideoWidth = var3.getWidth();
			this.mVideoHeight = var3.getHeight();
		}

	}

	int sameTimeCount = 0;
	boolean isPlaybackData= false;
	public void receiveFrameInfo(Camera var1, int var2, long var3, int var5,
								 int var6, final AVFrame avFrame, int var8) {
		if (var1.getmDevUID().equals(mDevUID)   ) {
			this.mVideoFPS = var5;
			this.mVideoBPS = var3;
			this.mOnlineNm = var6&0xf;
			this.isPlaybackData =( (var6&0xf0)>>4)==8?true:false;


			//Log.e("", " 表示充电   battery:"+ (0x100 & avFrame.getTempture())+"  var7:"+ ( avFrame.getTempture()&0xfffffff));

			if(( avFrame.getVarbit() & 0x80)==0x80){
				battery =  avFrame.getTempture() &0x7f ;
				//Log.e("", "battery:"+battery);
			}else{
				this.temperature =  avFrame.getTempture();
			}
			final boolean isshow  = this.isPlaybackData ;
			if (isPlaybackData) {
				{
					sameTimeCount++;
					Log.d("","myHorizontalScrollView.onTouching="+myHorizontalScrollView.onTouching);
					//连续的当前时间下，认为视频在播放。

					this.avFrameTimeStamp = var8;

					final int mAvFrameTimeStamp=   this.avFrameTimeStamp - myHorizontalScrollView.getToadyStartTime();//currentSeconds-curDiffTime;
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if(mAvFrameTimeStamp<0)return;
							Log.d("","myHorizontalScrollView.onTouching="+myHorizontalScrollView.onTouching +"myHorizontalScrollView.ishandleEvent=" +myHorizontalScrollView.ishandleEvent+"  (  TimeZone.getDefault().getOffset(System.currentTimeMillis())/1000)%(24*3600)="+(  TimeZone.getDefault().getOffset(System.currentTimeMillis())/1000)%(24*3600));
							if(sameTimeCount>15 && !myHorizontalScrollView.onTouching && !myHorizontalScrollView.ishandleEvent ){
								sameTimeCount = 0;
								myHorizontalScrollView.scorllToTheTime(mAvFrameTimeStamp);
							}
							findViewById(R.id.right2_tv).setVisibility(View.GONE);
							right2_tv.setVisibility(View.GONE);

						}
					});

				}
			} else {
				sameTimeCount++;
				if(sameTimeCount>15 && !myHorizontalScrollView.onTouching && !isPlaybackData){//连续的当前时间下，认为视频在播放。
					sameTimeCount = 0;
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							myHorizontalScrollView.scorllToCurrentTimeAndNoRecall();
							showright_tv();
							setPower(battery,(0x100 &  avFrame.getTempture())==0x100);
						}
					});
				}
			}
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(rockbacktoLive_photo!=null)
					{
						if(!isLandorientation){
							if(isshow )
							{
								if(rockbacktoLive_photo.getVisibility()==View.GONE)
									rockbacktoLive_photo.setVisibility(View.VISIBLE);

							}else {
								if( rockbacktoLive_photo.getVisibility()==View.VISIBLE)
									rockbacktoLive_photo.setVisibility(View.GONE);
							}
						}else{
							if(isshow &&control_bottom_new!=null && control_bottom_new.getVisibility() ==View.VISIBLE )
							{
								if (rockbacktoLive_photo.getVisibility() == View.GONE) {
									rockbacktoLive_photo .setVisibility(View.VISIBLE);
								}
							}
							else{
								rockbacktoLive_photo .setVisibility(View.GONE);
							}
						}

					}


				}
			});


			Bundle var9 = new Bundle();
			var9.putInt("avChannel", var2);
			Message var10 = this.handler.obtainMessage();
			var10.what = 99;
			var10.setData(var9);
			this.handler.sendMessage(var10);
		}

	}
	int charge = 0;
	private void setPower(int battery,boolean ischarge) {
		if(mBatteryView!=null) {

			//	Log.e("setPower","battery="+battery+",pkg="+mDevice.hardware_pkg);



			if((mDevice.hardware_pkg <HARDWAEW_PKG.CM_BELL_VR_9732_5112 && mDevice.hardware_pkg >= 0 && battery == 0)
					||mDevice.hardware_pkg==HARDWAEW_PKG.SREE_VR_LINUX_PIR_LED){ //普通摄像机不显示电池
				mBatteryView.setVisibility(View.GONE);
				return;
			}else{
				if(mBatteryView.getVisibility()==View.GONE){
					mBatteryView.setVisibility(View.VISIBLE);
				}
			}

			if(ischarge){
				if(battery<100){
					mBatteryView.setBackgroundResource(R.drawable.bater_wave);
					if(mIvFrameAnim!=null && !mIvFrameAnim.isRunning()){
						mBatteryView.setBackgroundResource(R.drawable.bater_wave);
						mIvFrameAnim.start();
					} else {
						if(mIvFrameAnim==null){
							mBatteryView.setBackgroundResource(R.drawable.bater_wave);
							mIvFrameAnim = (AnimationDrawable) mBatteryView.getBackground(); // 获取动画内容
							mIvFrameAnim.start();
						}
					}
				}else{
					if (mIvFrameAnim != null && mIvFrameAnim.isRunning()) {
						mIvFrameAnim.stop();
						mIvFrameAnim = null;
					}
					mBatteryView.setBackgroundResource(R.drawable.charge04);
				}

			}else{
				if(battery<=10){
					if(txtLowPower.getVisibility()==View.GONE){
						txtLowPower.setVisibility(View.VISIBLE);
					}
				}else{
					if(txtLowPower.getVisibility()==View.VISIBLE){
						txtLowPower.setVisibility(View.GONE);
					}
				}
				if(battery<6){
					mBatteryView.setBackgroundResource(R.drawable.bater_charge_wave);
					if(mIvFrameAnim!=null && !mIvFrameAnim.isRunning()){
						mBatteryView.setBackgroundResource(R.drawable.bater_charge_wave);
						mIvFrameAnim.start();
					} else {
						if(mIvFrameAnim==null){
							mBatteryView.setBackgroundResource(R.drawable.bater_charge_wave);
							mIvFrameAnim = (AnimationDrawable) mBatteryView.getBackground(); // 获取动画内容
							mIvFrameAnim.start();
						}
					}
				}else{
					if (mIvFrameAnim != null && mIvFrameAnim.isRunning()) {
						mIvFrameAnim.stop();
						mIvFrameAnim = null;
					}
					mBatteryView.setBackgroundResource(BatteryViewRec[battery/25]);
				}
			}
		}
	}

	public void receiveIOCtrlData(Camera var1, int var2, int var3, byte[] var4) {
		if (var1.getmDevUID().equals(mDevUID)  ) {

			Bundle var5 = new Bundle();
			var5.putInt("avChannel", var2);
			var5.putByteArray("data", var4);
			Message var6 = new Message();
			var6.what = var3;
			var6.setData(var5);
			this.handler.sendMessage(var6);
		}

	}

	public void receiveSessionInfo(Camera var1, int var2) {
		if (var1.getmDevUID().equals(mDevUID)  ) {
			Bundle var3 = new Bundle();
			Message var4 = this.handler.obtainMessage();
			var4.what = var2;
			var4.setData(var3);
			this.handler.sendMessage(var4);
		}

	}
//
//	public class TimeThread extends Thread {
//		@Override
//		public void run() {
//			do {
//				try {
//					Thread.sleep(1000);
//					Message msg = new Message();
//					msg.what = msgKey1;
//					Log.i("time", ">>>>>>>>>>");
//					mHandler.sendMessage(msg);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			} while (timeRunning);
//		}
//	}

	private void showright_tv(){
		if(right2_tv!=null)
			right2_tv.setVisibility(View.VISIBLE);
		if(mViewPager!=null)
			mViewPager.setVisibility(View.VISIBLE);

	}
	@Override
	public void receiveCameraCtl(Camera var1, int var2, int var3, byte[] var4) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
						   float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.i("IOTCamera", "222222	onLongPress");
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
							float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.i("IOTCamera", "222222	onShowPress222");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub

		Log.i("IOTCamera", "onSingleTapUp.....................................");

		return false;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.i("IOTCamera", "222222	onDown = false;222");
		return false;
	}
	boolean showRedREC = true;
	private Handler handlerrec = new Handler() {
		public void handleMessage(Message var1) {
			// Bundle var13 = var1.getData();

			try {
				int recordtimeCount = var1.getData().getInt("recordtimeCount");
				Log.v("deviceinfo", "what =" + var1.what+" recordtimeCountL:"+recordtimeCount);
				if (ImageViewRec == null)
					return;
				recode_time_txt.setText(""+AVIOCTRLDEFs.secToTime(var1.what/1000));
				if(recordtimeCount % 5 == 0){
					showRedREC = !showRedREC;
					if (showRedREC)
					{
						recode_time_txt.setTextColor(getResources().getColor(R.color.red));
						ImageViewRec .setBackgroundResource(R.drawable.record_rec_on);
					} else {
						ImageViewRec .setBackgroundResource(R.drawable.record_rec_off);
						recode_time_txt.setTextColor(getResources().getColor(R.color.white));
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};
	int currentplaySeq=0;
	@Override
	public void TimeLinePlayStatecallback(final int playBackTimeUTC) {
		Log.v("",currentplaySeq+"  currentplaySeq  滑动-------尝试回放-------TimeLinePlayStatecallback  playBackTimeUTC:"+playBackTimeUTC+"   时间："+ STimeDay.changeToStringLocalTime(((long)playBackTimeUTC)*1000) );

		if(mNoteInfoData==null ){
			return;
		}
//		if(recording){
//			getHelper().showMessageLong(getText(R.string.recording)+"");
//			return;  //if rockback live ,stop record ,else then continue record
//		}
		{
			if (right_image3 != null) {
				right_image3.setImageResource(R.drawable.mic_disable);
				right_image3.setEnabled(false);
			}
			if (img_mic != null) {
				img_mic.setImageResource(R.drawable.mic_disable);
				img_mic.setEnabled(false);
			}
			StopTalk();
			this.invalidateOptionsMenu();
		}
		Log.e("","时间轴操作》》》》》》》》》》》》》mNoteInfoData.dataBitMapValue== "+mNoteInfoData.dataBitMapValue);
		if(  mNoteInfoData.dataBitMapValue==0){
			return;
		}
		mCameraManagerment.userIPCRecordPalyStart(mDevUID,AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START ,playBackTimeUTC,(byte)currentplaySeq ) ;
//			this.mCamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_START,
//					AVIOCTRLDEFs.UBIA_IO_AVStream.playbackparseContent(AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START,playBackTimeUTC,(byte)currentplaySeq ));//1-60:1-60sec, 61-120:1-60min, 121-144:1-24hrs,145-176:1-31days
		sameTimeCount=0;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(rockbacktoLive_photo.getVisibility()==View.GONE){

					try {
						mCameraManagerment.userIPCMuteControl(mDevUID,false);
						LiveViewGLviewActivity.this.mIsListening = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(voiceMute != null){
						voiceMute.setImageResource(R.drawable.sound_on);
					}
				}
				if(rockbacktoLive_photo!=null    )
				{
					rockbacktoLive_photo.setVisibility(View.VISIBLE);

				}

				if (mProgressBar != null) {
					mProgressBar.setVisibility(View.VISIBLE);
				}


			}
		});
	}

	long mBackPressTime;
	@Override
	public void RollBackToCurrentTimecallback(final int mTimeUTC) {
		// TODO Auto-generated method stub

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(rockbacktoLive_photo!=null    )
				{

					rockbacktoLive_photo.setVisibility(View.GONE);
				}


				Log.v("","滑动--------尝试直播------RollBackToCurrentTimecallback  mTimeUTC:"+mTimeUTC+"   时间："+ STimeDay.changeToStringLocalTime(((long)mTimeUTC)*1000));
				currentplaySeq++;

				mCameraManagerment.setcurrentplaySeq(mDevUID,currentplaySeq);
				mCameraManagerment.userIPCStart(mDevUID, mDevice.getChannelIndex(),(byte)currentplaySeq );
			}
		});

	}

	private void goPhotoGridActivity(){
		Log.i("liveview","popview height:"+mPopViewAdd.getHeight());
		lastsnap();
		Bundle var21 = new Bundle();
		Intent var3 = new Intent();
		var21.putString("dev_uid", LiveViewGLviewActivity.this.mDevice.UID);
		var21.putString("dev_nickName",
				LiveViewGLviewActivity.this.mDevice.nickName);
		var21.putString("view_acc",
				LiveViewGLviewActivity.this.mDevice.viewAccount);
		var21.putString("view_pwd",
				LiveViewGLviewActivity.this.mDevice.viewPassword);
		var21.putInt("camera_channel",
				LiveViewGLviewActivity.this.mDevice.getChannelIndex());
		var3.putExtras(var21);
		var3.setClass(LiveViewGLviewActivity.this, PhotoGridActivity.class);
		startActivityForResult(var3, PHOTOGRID_REQUESTCODE) ;
		mPopupWindowAdd.dismiss();
//		if(mMediaExtractor!=null)
//		mMediaExtractor.release();
		isPlayMp4= false;
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
	Mp4Reader  p_mp4Read ;

	protected boolean process(String Uil) throws IOException {

		AudioTrack mAudioTrack =null;
		Log.e("", "read frame Uil :"+Uil );
		long 	TimeStamp[]=new long[1];
		long lastTimeStamp = 0;
		long 	TimeDuration[]=new long[1];
		long 	audioTimeStamp[]=new long[1];

		boolean 	iRet;
		int	ReadLen[] = new int [1];
//		Mp4Reader  p_mp4Read = new Mp4Reader ();
		p_mp4Read = new Mp4Reader ();
		int ret = 	p_mp4Read.Open(Uil);
		Log.e("","p_mp4Read  :"+ret);
		byte []fVideoBuffer	=   new byte[1024 * 256];
		SurfaceDecoder SDecoder =null;
		seekMp4time = 0;
		int sameCount = 0;
		onSeekbar = false;
		if (!bInitH264) {

			if(SDecoder!=null){
				SDecoder.release();
			}

			SDecoder = new SurfaceDecoder();
			if(SDecoder != null){
				SDecoder.SoftDecoderPrePare( );//初始化软解
				Log.e("TESTDECODE","LiveviewGLW create SurfaceDecoder for playback");
			}
			bInitH264 = true;

		}
		BufferInfo info = new BufferInfo();
		info.presentationTimeUs = 0;
		isPlayMp4 = true;
		isPause = false;
		FdkAACCodec.exitDecoder();
		FdkAACCodec.initDecoder();
		Bitmap mp4bitmap = null;
		monitor.refreshData() ;
		mCameraManagerment.userIPCMuteControl(mDevUID,true); //mp4关闭底层声音
		while(isPlayMp4) {
			if(isPause){
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
				int readAudiolen[] ={1024};
				byte []fAudioBuffer	=   new byte[1024 ];

				long 	audioTimeDuration[]=new long[1];
				iRet = p_mp4Read.GetNextAudioSample(fAudioBuffer,  readAudiolen,  audioTimeStamp,audioTimeDuration );
				//	Log.e("", "read frame error  audioTimeDuration:"+audioTimeDuration[0]+"    +iRet  "+readAudiolen[0] );

				if(readAudiolen[0]>100) {
					byte realData[] = new byte[2048];
					System.arraycopy(fAudioBuffer, 0, realData, 7, readAudiolen[0]);
					addADTStoPacket(realData,readAudiolen[0]);
					int ret2 = FdkAACCodec.decodeFrame(G726OutBuf, 2048,realData, readAudiolen[0]+7) ;
					try {
						if(mAudioTrack==null){
							mAudioTrack = new AudioTrack(3, 16000, 2, 2, 2048, 1);
							mAudioTrack.setStereoVolume(1.0F, 1.0F);
							mAudioTrack.play();
						}


						if(  mAudioTrack!=null && LiveViewGLviewActivity.this.mIsListening )
							mAudioTrack.write(G726OutBuf, 0, 2048);
					}catch(Exception e){
						e.printStackTrace();

					}
				}
			}



			long cutime = System.currentTimeMillis();
//        	if(		TimeStamp[0]< 	audioTimeStamp[0] ) //视频跟着音频走
			iRet = p_mp4Read.GetNextVideoSample(fVideoBuffer,  ReadLen,  TimeStamp ,TimeDuration);
			//  	Log.e("", "read frame error/TimeStamp[%ld]  close file iRet VideoTimeStamp:"+TimeStamp[0]+"    VideoTimeDuration:"+TimeDuration[0]+"     ,ReadLen[%d]  "+ReadLen[0]  +iRet +"   CheckIsSyncSample:"+p_mp4Read.CheckIsSyncSample());

			if(lastTimeStamp!=0 && TimeStamp[0]==lastTimeStamp){
				sameCount++;
			}
			if(TimeStamp[0]!=lastTimeStamp){
				sameCount = 0;
			}
			lastTimeStamp =  TimeStamp[0];
//        	if(ReadLen[0]<4){
//            	try {
//					Thread.sleep(66);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//            	continue;
//            }
			byte[] h264 = new byte[ReadLen[0]];
			System.arraycopy(fVideoBuffer, 0, h264, 0, ReadLen[0]);

			if(ReadLen[0]>0 && h264[4]==0x06 && ReadLen[0]>16)
			{
				long deviceTime = Packet.byteArrayToInt_Little(h264,8);
				LiveViewTimeStateCallbackInterface_Manager.getInstance().TimeUTCStatecallback(deviceTime);
				continue;
			}
			if (  SDecoder != null  ){
				SDecoder.doExtract(h264, ReadLen[0], 1920, 	1080,   0, monitor);

				if(h264[4] == 0x67){
					byte[] ppsBuf = SDecoder.getSPSAndPPS(Uil);
					if(ppsBuf!=null){
						SDecoder.doExtract(ppsBuf,ppsBuf.length, 1920, 	1080,   0, monitor);
					}
				}
			}
			if(p_mp4Read.CheckIsSyncSample())
				continue;




			if(isPlayMp4)
			{
				Bundle var4 = new Bundle();
				//    		Log.v("","mMediaExtractor :"+mMediaExtractor);
//	        	Log.e("", "read frame error/TimeStamp[%ld]  close file iRet:"+TimeStamp[0]+"    TimeDuration:"+TimeDuration[0]+"     ,ReadLen[%d]  "+ReadLen[0]  +iRet +"   CheckIsSyncSample:"+p_mp4Read.CheckIsSyncSample());
				int refreshTime = (int) TimeStamp[0];
				if(refreshTime>(int) TimeDuration[0]){
					refreshTime = (int) TimeDuration[0];
				}
				var4.putInt("SampleTime", refreshTime);
				var4.putInt("duration",(int) TimeDuration[0]);
				Message var5 = this.handler.obtainMessage();
				var5.what = 1111;
				var5.setData(var4);
				handler.sendMessage(var5);
			}
			try {//帧率控制 15
				long lasttime = System.currentTimeMillis();
				int sleeptime = (int) Math.abs(60-(lasttime-cutime));
				if(sleeptime>60){
					sleeptime = 60;
				}
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(TimeStamp[0]/1000 >=TimeDuration[0]/1000 || sameCount>10)//文件读完
			{
				Log.e("", "read frame  文件读完 :"+ReadLen[0]+"    TimeDuration:"+TimeDuration[0] );
				break;
			}
//    		if(isPlayMp4)
//    		mMediaExtractor.advance();
		}//文件结束
//    	if(monitor!=null)
//			monitor.restartPlay();
		{
			if(mAudioTrack!=null)
			{
				mAudioTrack.stop();
				mAudioTrack =null;
			}
			FdkAACCodec.exitDecoder();
			Log.e("FdkAACCodec","LiveView FdkAACCodec exitDecoder");
			p_mp4Read.Close();
			p_mp4Read =null;

			H264Decoder. release();
			if(SDecoder!=null)
				SDecoder.release();
			SurfaceDecoder.delloc();
			bInitH264 = false;
//        handler.sendEmptyMessage(1112);

			showGridViewBitmap = false;

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
				var4.putInt("duration",(int) TimeDuration[0]);
				Message var5 = this.handler.obtainMessage();
				var5.what = 1118;
				var5.setData(var4);
				handler.sendMessage(var5);

			}
			handler.sendEmptyMessage(1114);


		}
		Log.e("", "read frame error  return true" );

		return true;
	}

	public String convertTime(int i) {

		i /= 1000;
		int minute = i / 60;
		int hour = minute / 60;
		int second = i % 60;
		minute %= 60;
		return String.format("%02d:%02d",  minute, second);

	}
	void addADTStoPacket(byte[] packet, int payloadLen) {
		int profile = 2; //AAC LC
		int freqIdx = 8; //0=96K, 1=88.2K, 2=64K  3=48K, 4=44.1KHz
		int chanCfg = 1; //CPE
		int packetLen = payloadLen+ 7;
		packet[0] = (byte)0xFF;
		packet[1] = (byte)0xF1; //0xF9
		packet[2] = (byte)(((profile-1)<<6) + (freqIdx<<2) +(chanCfg>>>2));
		packet[3] = (byte)(((chanCfg&3)<<6) + (packetLen>>>11));
		packet[4] = (byte)((packetLen&0x7FF) >>> 3);
		packet[5] = (byte)(((packetLen&7)<<5) + 0x1F);
		packet[6] = (byte)0xFC;
		//	Log.e("","  packetLen:"+String.format("%d",packetLen&0xff)+"  packet[0]:"+String.format("%X",packet[0]&0xff)+"  packet[1]:"+String.format("%X",packet[1]&0xff)+"  packet[2]:"+String.format("%X",packet[2]&0xff)+"  packet[3]:"+String.format("%X",packet[3]&0xff)+"  packet[4]:"+String.format("%X",packet[4]&0xff)+"    packet[5]:"+  String.format("%X", packet[5]&0xff)+"   packet[6]:"+String.format("%X",packet[6]&0xff));
	}



	private void showXYSettingsDialo() {
		final AlertDialog mAlertDialog = (new Builder(this)).create();
		View mView = mAlertDialog.getLayoutInflater().inflate(
				R.layout.item_xy_setting_choose, (ViewGroup) null);
		mAlertDialog.setView(mView);
		final TextView voice_title = (TextView) mView
				.findViewById(R.id.setting_title_tv);
		final SeekBar xSeekBar = (SeekBar) mView
				.findViewById(R.id.seekBarx);
		xSeekBar.setMax(255);
		xSeekBar.setProgress(spvalue);

		final SeekBar seekBary = (SeekBar) mView
				.findViewById(R.id.seekBary);

		final TextView xvalue=(TextView)mView.findViewById(R.id.xvalue);
		final TextView yvalue=(TextView)mView.findViewById(R.id.yvalue);

		seekBary.setMax(255);
		seekBary.setProgress(micvalue);
		final TextView btnOK = (TextView) mView
				.findViewById(R.id.voice_change_ok);


		btnOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				//mCameraManagerment.userIPCLensOffset(mDevUID, xSeekBar.getProgress(),seekBary.getProgress() , 0);
				mAlertDialog.dismiss();
			}
		});

		xSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {


			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
				// mCamera.sendIOCtrl(0,
				// AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETVOLUME_REQ,
				// AVIOCTRLDEFs.SMsgAVIoctrlSetVolumeReq
				// .parseContent( mDevice.channelIndex,(byte) progress));
				xvalue.setText("x:"+progress);

			}
		});

		seekBary.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {


			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {

				yvalue.setText("y:"+progress);
			}

		});
		mAlertDialog.show();
	}



	private int QR_WIDTH = 120, QR_HEIGHT = 120;

	public void createQRImage(String url,ImageView qr_code_iv  ) {
		try {
			Bitmap bitmap;
			if (url == null || "".equals(url) || url.length() < 1) {
				qr_code_iv.setVisibility(View.GONE);
				return;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);

			int[] rec = bitMatrix.getEnclosingRectangle();
			int resWidth = rec[2] + 1;
			int resHeight = rec[3] + 1;
			BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
			resMatrix.clear();
			for (int i = 0; i < resWidth; i++) {
				for (int j = 0; j < resHeight; j++) {
					if (bitMatrix.get(i + rec[0], j + rec[1])) {
						resMatrix.set(i, j);
					}
				}
			}

			int width = resMatrix.getWidth();
			int height = resMatrix.getHeight();
			// BufferedImage image = new BufferedImage(width,
			// height,BufferedImage.TYPE_INT_ARGB);
			// for (int x = 0; x < width; x++) {
			// for (int y = 0; y < height; y++) {
			// image.setRGB(x, y, resMatrix.get(x, y) == true ?
			// Color.BLACK.getRGB():Color.WHITE.getRGB());
			// }
			// }
			//
			int[] pixels = new int[width * height];
			for (int y = 0; y < width; y++) {
				for (int x = 0; x < height; x++) {
					if (resMatrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}
			bitmap = Bitmap
					.createBitmap(width, height, Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			qr_code_iv.setImageBitmap(bitmap);
			qr_code_iv.setVisibility(View.VISIBLE);
			pixels = null;
			System.gc();
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}
	private AlertDialog mSettingShowQrcodeDialog;

	private void showQrcodeDialo() {
		mSettingShowQrcodeDialog = (new Builder(this)).create();
		View mView = mSettingShowQrcodeDialog.getLayoutInflater().inflate(
				R.layout.item_ipc_setting_qrcode, (ViewGroup) null);
		mSettingShowQrcodeDialog.setView(mView);
		final ImageView fq_code_iv = (ImageView) mView
				.findViewById(R.id.fq_code_iv);
		createQRImage(mZigbeeInfo.macaddr, fq_code_iv);
		mSettingShowQrcodeDialog.show();

		final  TextView qrinfo = (TextView) mView
				.findViewById(R.id.qrinfo);
		qrinfo.setText(" "+mZigbeeInfo.info);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		Log.v("onTouch", "MotionEvent  ACTION_DOWN");
		final MyCamera mCamera = CameraManagerment.getInstance()
				.getexistCamera(mDevUID);
		bt_top.setPressed(false);
		bt_bottom.setPressed(false);
		bt_left.setPressed(false);
		bt_right.setPressed(false);
		BtnPTZ_Auto.setPressed(false);
		((ImageButton) arg0).setPressed(true);
		switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				ptzId = arg0.getId();
				switch (ptzId) {

					case R.id.top_btn:
						mCameraManagerment.userIPCPTZUp(mCamera.getmDevUID(), 0);

						break;
					case R.id.bottom_btn:
						mCameraManagerment.userIPCPTZDown(mCamera.getmDevUID(), 0);

						break;
					case R.id.left_btn:
						mCameraManagerment.userIPCPTZLeft(mCamera.getmDevUID(), 0);

						break;
					case R.id.right_btn:

						mCameraManagerment.userIPCPTZRight(mCamera.getmDevUID(), 0);

						break;
					case R.id.BtnPTZ_Auto:

						Log.e("", "BtnPTZ_Auto=====>>>>什么都不发，停止转动");

						break;

				}

				break;
		}
		return true;
	}

	int ptzId = 0;
	boolean ptzThreadRunning = false;
	Thread ptz = new Thread(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			final MyCamera mCamera = CameraManagerment.getInstance()
					.getexistCamera(mDevUID);
			ptzThreadRunning = true;
			//Log.e("", "ptzId   ptzId:" + ptzId + "   ptzThreadRunning:"+ ptzThreadRunning);
			while (ptzThreadRunning) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("", "ptzId   ptzId:" + ptzId + "   ptzThreadRunning:" + ptzThreadRunning);
				switch (ptzId) {

					case R.id.top_btn:
						mCameraManagerment.userIPCPTZUp(mCamera.getmDevUID(), 0);

						break;
					case R.id.bottom_btn:
						mCameraManagerment.userIPCPTZDown(mCamera.getmDevUID(), 0);

						break;
					case R.id.left_btn:
						mCameraManagerment.userIPCPTZLeft(mCamera.getmDevUID(), 0);

						break;
					case R.id.right_btn:

						mCameraManagerment.userIPCPTZRight(mCamera.getmDevUID(), 0);

						break;

				}

			}
			ptzThreadRunning = false;
			Log.e("", "ptzId   ptzId:" + ptzId + "   ptzThreadRunning:" + ptzThreadRunning);
		}
	});

	private void rockBackToLive(){

		if(mProgressBar!=null)
		{
			mProgressBar.setVisibility(View.VISIBLE);
			mProgressBar.bringToFront();
		}
		if(isLandorientation){
			if(rockbacktoLive_photo!=null)
				rockbacktoLive_photo.setVisibility(View.GONE);
			if(System.currentTimeMillis()-clickBackToLivetime>1000){//大于一秒以上的点击才认为有效
				currentplaySeq++;
				mCameraManagerment.setcurrentplaySeq(mDevUID,currentplaySeq);
				if(txt_time!=null)
					txt_time.setText("");
				mCameraManagerment.userIPCStart(mDevUID, mDevice.getChannelIndex(),(byte)currentplaySeq );
				mCameraManagerment.userIPCstartAllStream( mDevUID,  true,false);
				if(myHorizontalScrollView!=null){
					myHorizontalScrollView.scorllToCurrentTimeAndNoRecall();
				}
			}
			clickBackToLivetime = System.currentTimeMillis();

			MyCamera mCamera=  CameraManagerment.getInstance().getexistCamera(mDevUID);
			HARDWAEW_INFO mdd =VRConfig.getInstance().getDeviceType(mCamera.hardware_pkg);
			monitor.setCameraHardware_pkg(mCamera.hardware_pkg);
			if(mdd.type == VRConfig.VRDEVICE && mdd.width==960 && mdd.height==960 ){
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}else{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}

			if( UbiaApplication.BUILD_CHECK_PLAYVOICE)////check playVoice; detail default close
			{
				try {

//				返回直播，关闭声音
					mCameraManagerment.userIPCMuteControl(mDevUID,true);
					LiveViewGLviewActivity.this.mIsListening = false;
					StartAudio();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(voiceMute!=null){
					voiceMute.setImageResource(R.drawable.sound_off);
				}
			}
			else{
				try {

					mCameraManagerment.userIPCMuteControl(mDevUID,false);
					LiveViewGLviewActivity.this.mIsListening = true;
					StartAudio();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(voiceMute!=null){
					voiceMute.setImageResource(R.drawable.sound_on);
				}
			}

		}else{


			if(isPlayMp4)
			{
				handler.postDelayed(new Runnable() {
					public void run() {
						{
							// initSpeakVolume();
							monitor.restartPlay();
							mCameraManagerment.userIPCstartShow(mDevUID);
							if (mIsListening) {
								mCameraManagerment.userIPCstartListen(mDevUID);
							}

							if (mIsSpeaking) {
								mCameraManagerment.userIPCstartSpeak(mDevUID);
							}
							monitor.attachCamera(mCameraManagerment.getexistCamera(mDevUID), 0,mDevice.installmode,mDevice,mDevice.snapshot,true);
							monitor.setCameraPutModel(mDevice.installmode);
							MyCamera mCamera = mCameraManagerment .getexistCamera(mDevUID);
							monitor.setCameraHardware_pkg(mCamera.hardware_pkg);
							checkDoorbellSound();
						}
					}
				}, 500);
			} else{
				monitor.restartPlay();
				mCameraManagerment.userIPCstartShow(mDevUID);
				if ( mIsListening) {
					mCameraManagerment.userIPCstartListen(mDevUID);
				}

				if ( mIsSpeaking) {
					mCameraManagerment.userIPCstartSpeak(mDevUID);
				}
				checkDoorbellSound();


			}
			isPlayMp4 = false;
			handler.sendEmptyMessage(1112);
			if(rockbacktoLive_photo!=null)
				rockbacktoLive_photo.setVisibility(View.GONE);
			if (System.currentTimeMillis() - clickBackToLivetime > 1000) {// 大于一秒以上的点击才认为有效
				Log.d("", "     clickBackToLivetime = System.currentTimeMillis(); ="+clickBackToLivetime +"     " +System.currentTimeMillis() );

				currentplaySeq++;
				mCameraManagerment.setcurrentplaySeq( mDevUID,currentplaySeq);
				mCameraManagerment.userIPCStart(mDevUID, 	mDevice.getChannelIndex(), 	(byte) currentplaySeq);
				if(myHorizontalScrollView!=null){
					myHorizontalScrollView
							.scorllToCurrentTimeAndNoRecall();
				}
			}



			if( !UbiaApplication.BUILD_CHECK_PLAYVOICE)////check playVoice; detail default close
			{
				try {
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// FdkAACCodec
							// 解码器会被释放，延后处理，保证MP4线程先退出后再初始化FdkAACCodec
							// 返回直播，关闭声音
							mCameraManagerment .userIPCMuteControl(mDevUID, true);
							LiveViewGLviewActivity.this.mIsListening = false;
							StartAudio();
						}
					}, 500);

				} catch (Exception e) {
					e.printStackTrace();
				}
				if(voiceMute!=null){
					voiceMute.setImageResource(R.drawable.sound_off);
				}
			}else{
				try {

					mCameraManagerment.userIPCMuteControl(mDevUID,false);
					LiveViewGLviewActivity.this.mIsListening = true;
					StartAudio();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(voiceMute!=null){
					voiceMute.setImageResource(R.drawable.sound_on);
				}
			}


			clickBackToLivetime = System.currentTimeMillis();

			final MyCamera mCamera=  CameraManagerment.getInstance().getexistCamera(mDevUID);

			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					monitor.restartPlay();
					monitor.attachCamera(mCameraManagerment.getexistCamera(mDevUID), 0,mDevice.installmode,mDevice,mDevice.snapshot,true);
					monitor.setCameraHardware_pkg(mCamera.hardware_pkg);
					monitor.setCameraPutModel(mDevice.installmode);
					if(UbiaApplication.SHOWLASTSNAPSHOT)
						monitor.refreshBitmap(mDevice.snapshot) ;
				}
			}, 500);

			HARDWAEW_INFO mdd =VRConfig.getInstance().getDeviceType(mCamera.hardware_pkg);
			if(mdd.type == VRConfig.VRDEVICE && mdd.width==960 && mdd.height==960 ){
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}else{
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}


		}
		if (right_image3 != null) {
			right_image3.setImageResource(R.drawable.mic_off);
			right_image3.setEnabled(true);
		}
		if (img_mic != null) {
			img_mic.setImageResource(R.drawable.mic_off);
			img_mic.setEnabled(true);
		}
		showGridViewBitmap = false;
	}


	private void checkDoorbellSound(){
		Log.d("","checkDoorbellSound  isdoolbeel :"+isdoolbeel);
		if(isdoolbeel>0){
			try {

				mCameraManagerment.userIPCMuteControl(mDevUID,false);
				LiveViewGLviewActivity.this.mIsListening = true;
				StartAudio();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(voiceMute != null){
				voiceMute.setImageResource(R.drawable.sound_on);
			}
			if(!LiveViewGLviewActivity.this.mIsSpeaking){
				if (right_image3 != null) {
					right_image3.setImageResource(R.drawable.mic_off);
				}
				if (img_mic != null) {
					img_mic.setImageResource(R.drawable.mic_off);
				}

				StopTalk();
			}
		}
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	@Override
	public void onPageSelected(int position) {
		for (int i=0; i<points.length; i++){
			if (position == i) {
				indicationPoint[i].setImageResource(R.drawable.yuan_blue);
			} else {
				indicationPoint[i].setImageResource(R.drawable.yuan2);
			}
		}
	}
	boolean isShowCallDialog = false;
	private BroadcastReceiver newDeviceCallBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, final Intent intent) {


			String action = intent.getAction();
			if (action.equals("action.newDeviceCallBroadcastReceiver"))
			{

				final String alarmMessageuid = (String) intent.getExtras().get( "alarmMessageuid");
				if (alarmMessageuid.equals(mDevUID))
					return;
				if(!isShowCallDialog){
					DialogUtil.getInstance().showDelDialog(LiveViewGLviewActivity.this, "", getString(R.string.newdevicecall), new Dialogcallback(){

						@Override
						public void cancel() {
							isShowCallDialog = false;
						}

						@Override
						public void commit(String str) {
							isShowCallDialog = false;
						}

						@Override
						public void commit() {
							isPlayMp4 = false;
							if (mProgressBar != null)
								mProgressBar.setVisibility(View.VISIBLE);
							mCameraManagerment.userIPCStopAlStream(mDevUID);
							StopAllSpeak();
							Bundle var8 = new Bundle();
							// var8.putString("dev_uuid", var1.UUID);
							DeviceInfo mde = MainCameraFragment
									.getexistDevice(alarmMessageuid);
							var8.putString("dev_uid", alarmMessageuid);
							var8.putString("dev_nickName", mde.nickName);
							var8.putInt("camera_channel", 0);
							var8.putString("view_acc", mde.viewAccount);
							var8.putString("view_pwd", mde.viewPassword);

							intent.putExtras(var8);
							LiveViewGLviewActivity.this.setIntent(intent);
							mDevUID = alarmMessageuid;
							initData();
							mCameraManagerment.userIPCstartShow(mDevUID);
							isShowCallDialog = false;
						}

					});
					isShowCallDialog = true;
				}


				Log.i(" ", "newDeviceCallBroadcastReceiver 重新加载 mDevUID:"
						+ mDevUID);
			}
		}
	};
	private void setUpMonitor(boolean isHorizontal){
		Log.i(" ", "setUpMonitor 重新加载 isHorizontal:" + isHorizontal);
		if(monitor!=null)
			monitor.deattachCamera() ;
		monitor = null;
		this.monitor = (GLView) this.findViewById(R.id.monitorLayout);
		this.monitor.attachCamera(mCameraManagerment.getexistCamera(mDevUID), 0,mDevice.installmode,mDevice,mDevice.snapshot,true);
		monitor.setCameraPutModel(mDevice.installmode);
		MyCamera mCamera = mCameraManagerment .getexistCamera(mDevUID);
		monitor.setCameraHardware_pkg(mCamera.hardware_pkg);
		this.monitor.invalidate();
		if(UbiaApplication.SHOWLASTSNAPSHOT)
			this.monitor.refreshBitmap(mDevice.snapshot);
		this.monitor.setHorizontal(isHorizontal);
		this.monitor.setMhandle(mHandler);
		this.monitor.refreshData();
	}


	private void showPasswordSimpleDialog() {
		DialogUtil.getInstance().showEditTipDialog(LiveViewGLviewActivity.this, getString(R.string.password_sim_tip), new Dialogcallback() {
			@Override
			public void cancel() {
				mDevice.isModifyPassword = false;
			}

			@Override
			public void commit(String str) {

				if (str.length() < 8) {

					DialogUtil.getInstance().showTextTipDialog(LiveViewGLviewActivity.this, getString(R.string.password_short_tip),getString(R.string.close),null);
				}else if(str.contains(":")){
					DialogUtil.getInstance().showTextTipDialog(LiveViewGLviewActivity.this, getString(R.string.password_illegal),getString(R.string.close),null);
				}else {
					if (mDevice != null) {

						isModifyPassword = true;
						mDevice.viewPassword = str;

						mCameraManagerment.userIPCSetPassWord(mDevice.UID,
								"admin", mDevice.viewPassword, mDevice.nickName);


					}


				}
			}

			@Override
			public void commit() {

			}
		});
	}
}
