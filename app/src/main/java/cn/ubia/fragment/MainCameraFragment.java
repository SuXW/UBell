package cn.ubia.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import cn.ubia.adddevice.SetupAddDeviceActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import cn.ubia.AboutActivity;
import cn.ubia.LiveViewGLviewActivity;
import cn.ubia.MainActivity;
import cn.ubia.SettingActivity;
import cn.ubia.UbiaApplication;
import cn.ubia.UBell.R;
import cn.ubia.adddevice.AddCarmeraFragmentActivity;
import cn.ubia.adddevice.AddDeviceActivity;
import cn.ubia.adddevice.WIfiAddDeviceActivity;
import cn.ubia.base.Constants;
import cn.ubia.bean.AlarmMessage;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.DeviceSearchResult;
import cn.ubia.bean.MyCamera;
import cn.ubia.db.DatabaseManager;
import cn.ubia.interfaceManager.DeviceStateCallbackInterface;
import cn.ubia.interfaceManager.DeviceStateCallbackInterface_Manager;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.manager.NotificationTagManager;
import cn.ubia.util.BaseTools;
import cn.ubia.util.PreferenceUtil;
import cn.ubia.widget.DialogUtil;
import cn.ubia.widget.DialogUtil.DialogChooseItemStringcallback;
import cn.ubia.widget.DialogUtil.Dialogcallback;
import cn.ubia.widget.MyProgressBar;

import com.baidu.push.AlarmMessageActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.ubia.IOTC.AVFrame;
import com.ubia.IOTC.AVIOCTRLDEFs;
import com.ubia.IOTC.Camera;
import com.ubia.IOTC.IRegisterIOTCListener;
import com.ubia.IOTC.IRegisterUBIAListener;
import com.ubia.IOTC.Packet;
import com.ubia.IOTC.WiFiDirectConfig;
import com.ubia.http.HttpClient;
import com.ubia.util.MobileInfoUtils;
import com.ubia.vr.VRConfig;
import com.zbar.lib.CaptureActivity;

public class MainCameraFragment extends  Fragment implements
		IRegisterIOTCListener, IRegisterUBIAListener {
	
	public static final int CONNSTATUS_NULL=0;
	public static final int CONNSTATUS_CONNECTING=0x1;
	public static final int CONNSTATUS_CONNECTED=0x2,
			CONNSTATUS_DISCONNECTED=0x3;
	public static final int CONNSTATUS_CONNECTION_FAILED=0x8;
	public static final int CONNSTATUS_RECONNECTION=0x6;
	public static final int CONNSTATUS_WRONG_PASSWORD=0x5;
	public static final int CONNSTATUS_UNKOWN_DEVICE=0x4;
	public static final int CONNSTATUS_STARTDEVICECLIENT=0x9;
	
	private static final int Build_VERSION_CODES_ICE_CREAM_SANDWICH = 14;
	public static final int CAMERA_MAX_LIMITS = 4;
	private static final int CTX_MENU_ITEM_EDIT_CAM = 2;
	private static final int CTX_MENU_ITEM_RECONNECT = 1;
	private static final int CTX_MENU_ITEM_REMOVE_CAM = 5;
	private static final int CTX_MENU_ITEM_VIEW_EVENT = 3;
	private static final int CTX_MENU_ITEM_VIEW_snapshot = 4;
	public static final boolean build_for_factory_tool = false;
	public static List<DeviceInfo> DeviceList = Collections
			.synchronizedList(new ArrayList<DeviceInfo>());
	// public static List<MyCamera> CameraList = new ArrayList<MyCamera>();
	// public static List CameraList = new ArrayList();
	// public static List DeviceList = Collections
	// .synchronizedList(new ArrayList());
	MediaPlayer alarmAudio;
	private static final int OPT_MENU_ITEM_TWO_SEARCH = 2;
	private static final int OPT_MENU_ITEM_ADD_CAM = 1;
	private static final int OPT_MENU_ITEM_FIRST_SEARCH = 3;
	private static final int OPT_MENU_ITEM_FAST_SEARCH = 4;
	
	private static final int CAMERA_SCAN_ADD_REQUEST = 201;
	private static final int LIVE_VIEW_REQUEST = 202;
	private static final int CAMERA_SCAN_REQUEST = 203;
	
	private static final int REQUEST_CODE_CAMERA_ADD = 0;
	private static final int REQUEST_CODE_CAMERA_EDIT = 2;
	private static final int REQUEST_CODE_CAMERA_HISTORY = 3;
	private static final int REQUEST_CODE_CAMERA_VIEW = 1;
	
	private static final int MENU_ADD = 1;
	//public static int nShowMessageCount = 0;
	public static String token = null;
	private final int CONTEXT_MENU_ID = 1;
	private DeviceListAdapter adapter;
	private View addDeviceView;
	private IntentFilter filter;
	private HttpClient mHttpClient;
	private String mAccount;
	private MyProgressBar mProgressBar;
	private String mPassword;
	public static boolean mCameraLoaded = false;
	public static MainCameraFragment mainCameraFragment;
	public static MainCameraFragment pubCameraFragment;
	public static int g_isby = 0;
	public boolean message_check = true;
	public boolean bfcg_istrue = false;
	private ImageView g_imgbt =null;
	public static Bitmap default_snap;
	int screenWidth;
	float density;
	private CameraManagerment mCameraManagerment=CameraManagerment.getInstance();
	public static MainCameraFragment getInstance() {
		if (mainCameraFragment == null) {
			Log.i("IOTCamera", "====mainCameraFragment is null====");
			mainCameraFragment = new MainCameraFragment();
		}
		Log.i("IOTCamera", "====into  mycamear====");
		g_isby = 0;
		return mainCameraFragment;
	}

	public void setCamearIsBY(int isby) {
		g_isby = isby;
	}

			// Bundle var13 = var1.getData();
					// loadMyCameraListFromWebServer();
//					var21.start(0, var18, var19);
//					mCameraManagerment.userIPCDeviceInfo(var17);
//					mCameraManagerment.userIPCGetSupportStream(var17);
//					mCameraManagerment.userIPCGetAudioOutFormat(var17);
//					mCameraManagerment.userIPCGetTimeZone(var17);
					
					
					//CameraList.add(var21);


				// TODO: handle exception

	public void setAdapternotifyDataSetChanged() {

		adapter.notifyDataSetChanged();

	}

	private Handler handler = new Handler() {

		public void handleMessage(Message var1) {

			try {
				Bundle var2 = var1.getData();
				String var3 = var2.getString("requestDevice");
				byte[] var4 = var2.getByteArray("data");
				int var5 = 0;
				// Log.i("main", "go into  handler ");
				DeviceInfo var7 = null;
				while (true) {
					int var6 = MainCameraFragment.DeviceList.size();
					var7 = null;
					if (var5 >= var6) {
						break;
					}

					if (((DeviceInfo) MainCameraFragment.DeviceList.get(var5)).UID
							.equalsIgnoreCase(var3)) {
						var7 = (DeviceInfo) MainCameraFragment.DeviceList
								.get(var5);
				 
						break;
					}

					++var5;
					// Log.i("main", "++var5");
				}

				int var8 = 0;

				MyCamera var10=null;
				while (true) {
					int var9 = mCameraManagerment .CameraList.size();
					var10 = null;
					if (var8 >= var9) {
						break;
					}

					if (((MyCamera) mCameraManagerment.CameraList.get(var8))
							.getUUID().equalsIgnoreCase(var3)) {
						var10 = (MyCamera) mCameraManagerment.CameraList
								.get(var8);
						// Log.i("main", "var8.what:" + var8);
						break;
					}

					++var8;
					// Log.i("main", "++var8 =" + var8);
				}
				if(var7==null || var10==null) return;
				// Log.i("main", "var1.what:" + var1.what);
				switch (var1.what) {
//				case CONNSTATUS_CONNECTING:
//					if (var10 != null
//							&& (!var10.isSessionConnected() || !var10
//									.isChannelConnected(0)) && var7 != null) {
//						Resources res = getResources();
//						String text = res
//								.getString(R.string.connstus_connecting);
//						var7.Status = text; 
//						var7.online = false;
//						var7.offline = false;
//						var7.lineing = true;
//						// Log.i("main", "var1.what:" + var1.what
//						// + "      var7.Status" + var7.Status);
//					}
//					break;
//				case CONNSTATUS_CONNECTED:
//					if (var10 != null && var10.isSessionConnected()
//							&& var10.isChannelConnected(0) && var7 != null) {
//						Resources res = getResources();
//						String text = res.getString(R.string.state_connected);
//						var7.Status = text; 
//						var7.online = true;
//						var7.offline = false;
//						var7.lineing = false;
//						if (var7.ChangePassword) {
//							var7.ChangePassword = false;
//							// (new ThreadTPNS(MainActivity.this, var7.UID, 0))
//							// .start();
//							(new DatabaseManager(
//									MainCameraFragment.this.getActivity()))
//									.delete_remove_list(var7.UID);
//						}
//					}
//					break;
//				case CONNSTATUS_DISCONNECTED:
//					if (var7 != null) {
//						Resources res = getResources();
//						String text = res
//								.getString(R.string.state_disconnected);
//						var7.Status = text;
//						// getHelper().showMessage(text);
//						// Log.i("main", " text =" + text);
//						// MainCameraFragment.this.getText(
//						// R.string.connstus_disconnect).toString();
//						var7.online = false;
//						var7.offline = true;
//						var7.lineing = false;
//						if (var7.connect_count < 5) {
//							MainCameraFragment.this.checkWiFi(var10, var7);
//							++var7.connect_count;
//						}
//					}
//
//					if (var10 != null) {
//						var10.disconnect();
//					}
//					break;
//				case CONNSTATUS_UNKOWN_DEVICE:
//					if (var7 != null) {
//						// <string name="connstus_unknown_device">未知设备</string>
//						// <string
//						// name="connstus_connection_failed">连接失败</string>
//						// <string name="connstus_wrong_password">错误密码</string>
//						Resources res = getResources();
//						String text = res
//								.getString(R.string.connstus_unknown_device);
//						var7.Status = text;
//						// getHelper().showMessage(text);
//						// MainCameraFragment.this.getText(
//						// R.string.connstus_unknown_device).toString();
//						var7.online = false;
//						var7.offline = true;
//						var7.lineing = false;
//					}
//					break;
//				case CONNSTATUS_WRONG_PASSWORD:
//					if (var7 != null) {
//						Resources res = getResources();
//						String text = res
//								.getString(R.string.connstus_wrong_password);
//						var7.Status = text;
//						getHelper().showMessage(text);
//						Log.i("main", " text =" + text);
//
//						// var7.Status = "错误密码";
//						// MainCameraFragment.this.getText(
//						// R.string.connstus_wrong_password).toString();
//						var7.offline = true;
//						var7.lineing = false;
//						var7.online = false;
//						// (new ThreadTPNS(MainActivity.this,
//						// var7.UID)).start();
//					}
//
//					if (var10 != null) {
//						var10.disconnect();
//					}
//					break;
//				case CONNSTATUS_RECONNECTION:
//					if (var7 != null) {
//						Resources res = getResources();
//						String text = res
//								.getString(R.string.state_disconnected);
//						var7.Status = text;
//						// getHelper().showMessage(text);
//						Log.i("main", " text =" + text);
//						// MainCameraFragment.this.getText(
//						// R.string.connstus_disconnect).toString();
//						var7.online = false;
//						var7.offline = true;
//						var7.lineing = false;
//						MainCameraFragment.this.checkWiFi(var10, var7);
//					}
//					break;
//				case CONNSTATUS_CONNECTION_FAILED:
//					if (var7 != null) {
//						Resources res = getResources();
//						String text = res
//								.getString(R.string.connstus_connection_failed);
//						var7.Status = text;
//						// getHelper().showMessage(text);
//						Log.i("main", " text =" + text);
//						var7.offline = true;
//						var7.lineing = false;
//						// MainCameraFragment.this.getText(
//						// R.string.connstus_connection_failed).toString();
//						var7.online = false;
//						// MainCameraFragment.this.checkWiFi(var10, var7);
//					}
//					break;
				case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_RESP:
					if (Packet.byteArrayToInt_Little(var4, 40) == -1
							&& var10 != null
							&& var10.getSDCardFormatSupported(0)
							&& var7 != null && var7.ShowTipsForFormatSDCard) {
						MainCameraFragment.this.showSDCardFormatDialog(var10,
								var7);
					}
					break;
				case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_RESP:
//					if (var4[4] == 0) {
//						Toast.makeText(
//								MainCameraFragment.this.getActivity(),
//								MainCameraFragment.this.getText(
//										R.string.tips_format_sdcard_success)
//										.toString(), 0).show();
//					} else {
//						Toast.makeText(
//								MainCameraFragment.this.getActivity(),
//								MainCameraFragment.this.getText(
//										R.string.tips_format_sdcard_failed)
//										.toString(), 0).show();
//					}
					break;

				case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP:
//					if (var4[0] == 0) {
//						if (var4[4] == 0) {
//							
//							bfcg_istrue = true;
//							if(g_imgbt!=null)
//								g_imgbt.setImageResource(R.drawable.start_alarm);
//					
//						} else {
//				
//							bfcg_istrue = false;
//							if(g_imgbt!=null)
//							g_imgbt.setImageResource(R.drawable.stop_alarm);
//						}
//						
//				
//					}
//					
//			 
//					adapter.notifyDataSetChanged();
					break;
					
				case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETMOTIONDETECT_RESP:
				 
//					if(bfcg_istrue){
//						
//					if (var4[4] == 0) {
//						alarmAudio = MediaPlayer.create(MainCameraFragment.this.getActivity(), R.raw.cfcg);
//						// alarmAudio.setVolume(1, 1);
//						alarmAudio.setLooping(false);
//						alarmAudio.start();
//						var10.setbIsIOAlarm(false);
//					} else {
//						
//						alarmAudio = MediaPlayer.create(MainCameraFragment.this.getActivity(), R.raw.bfcg);
//						// alarmAudio.setVolume(1, 1);
//						alarmAudio.setLooping(false);
//						alarmAudio.start();
//						var10.setbIsIOAlarm(true);
//					}
//					handler.postDelayed(new Runnable() {
//	                    public void run() {
//	                    	if (null != alarmAudio) {
//	    						alarmAudio.release();
//	    						alarmAudio = null;
//	    					}
//	    					adapter.notifyDataSetChanged();
//	                    }
//	                }, 1500);
//					}
					break;
				case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_EVENT_REPORT:
					byte[] var11 = new byte[8];
					System.arraycopy(var4, 0, var11, 0, 8);
					AVIOCTRLDEFs.STimeDay var12 = new AVIOCTRLDEFs.STimeDay(
							var11);
					int var13 = Packet.byteArrayToInt_Little(var4, 12);
					int var14 = Packet.byteArrayToInt_Little(var4, 16);
					if (var14 != 4 && var14 != 6) {
						MainCameraFragment.this.showNotification(var7, var13,
								var14, System.currentTimeMillis());
					}
				}

				if (var7 != null && var10 != null) {
					var7.Mode = var10.getSessionMode();
				}

				MainCameraFragment.this.adapter.notifyDataSetChanged();
				super.handleMessage(var1);
			} catch (Exception e) {
				e.printStackTrace();

			}
		}

	};
	private IconContextMenu iconContextMenu = null;
	private ListView listView;
	private LinearLayout empty_layout,add_device_row_ll;
	private OnItemClickListener listViewOnItemClickListener = new OnItemClickListener() {// 视频列表
		public void onItemClick(AdapterView var1, View var2, int sel, long var4) {
			try {
				
//				AlarmMessage alarmMessage = new AlarmMessage("", "RL6CP4FRATYEOPYBB4BQ", "");
//		    	Intent activityIntent = new Intent(MainCameraFragment.this.getActivity(), PhoneMessageActivity.class)
//		    			.putExtra("alarmMessage", alarmMessage);
//		    	activityIntent.setClass(MainCameraFragment.this.getActivity(),
//		    			PhoneMessageActivity.class);
//		    	activityIntent.putExtra("alarmMessage", alarmMessage); 
//		        startActivity(activityIntent); 
//		        
//		  
//		    	if(1==1)return;
				nowtime = System.currentTimeMillis();
				if (nowtime - lasttime > 1500) {
					lasttime = nowtime;
					if(sel==MainCameraFragment.DeviceList.size()){
						Intent intentadd = new Intent(MainCameraFragment.this.getActivity(),
								AddCarmeraFragmentActivity.class);
						startActivityForResult(intentadd, 1);
						return;
					}
				
					selectedDevice = (DeviceInfo) MainCameraFragment.DeviceList .get(sel);
					selectedCamera = (MyCamera) mCameraManagerment.CameraList .get(sel);
					final int var3 = sel;
					
					if(selectedDevice.device_connect_state == CONNSTATUS_WRONG_PASSWORD ){
							Log.d("","   密码错误");
							DialogUtil.getInstance().showpwdErrorDialo(getActivity(),new DialogChooseItemStringcallback(){

								@Override
								public void chooseItemString(String newPassword) {
								 
									DeviceInfo mDevice =  selectedDevice;
									String var17; 
									 var17 = newPassword;
							 
									 mDevice.viewPassword = var17;
									// //////////////////////////////////////////////////////////////////////////
									 (new DatabaseManager(
											 getActivity())).updateDeviceInfoByDBID(
									mDevice.DBID, mDevice.UID, mDevice.nickName,
									 "", "", "admin", mDevice.viewPassword,
									 mDevice.EventNotification, mDevice.getChannelIndex(),
									 mDevice.isPublic);
										selectedCamera.disconnect();
										try {
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										selectedCamera
												.connect(selectedDevice.UID);

										selectedCamera.start(0,
												selectedDevice.viewAccount,
												selectedDevice.viewPassword);
										DialogUtil.getInstance().hidePopupWindow( );
										
								}
								
						},null)	;
							
						return;
					}
					Resources res = getResources();
					final String text = res.getString(R.string.fragment_liveviewactivity_publiccameraactivity_setupadddeviceactivity_state_connected);
					if (!text.equals(DeviceList.get(sel).Status)) {
						Toast.makeText(getActivity(), R.string.mainfragment_refreshableview_refreshing,

						Toast.LENGTH_SHORT).show();
					}
					if (var3 < MainCameraFragment.DeviceList.size()) {
						{

							(new Thread(new Runnable() {
								public void run() {

									if(DeviceList.get(var3).device_connect_state == CONNSTATUS_CONNECTED ){
										selectedCamera.start(0,
												selectedDevice.viewAccount,
												selectedDevice.viewPassword);
									} 
									if ( (DeviceList.get(var3).device_connect_state!=CONNSTATUS_CONNECTED ) &&  (DeviceList.get(var3).device_connect_state!=CONNSTATUS_STARTDEVICECLIENT )) {
									
										selectedCamera.disconnect();
										try {
											Thread.sleep(1000);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										selectedCamera.connect(selectedDevice.UID);

										selectedCamera.start(0,
												selectedDevice.viewAccount,
												selectedDevice.viewPassword);
										
										mCameraManagerment.userIPCDeviceInfo(selectedDevice.UID);
//										mCameraManagerment.userIPCGetSupportStream(selectedDevice.UID);
//										mCameraManagerment.userIPCGetAudioOutFormat(selectedDevice.UID);
//										mCameraManagerment.userIPCGetTimeZone(selectedDevice.UID);
										

									} 
									else {
										Bundle var6 = new Bundle();
										var6.putString(
												"dev_uid",
												((DeviceInfo) MainCameraFragment.DeviceList
														.get(var3)).UID);
										var6.putString(
												"dev_uuid",
												((DeviceInfo) MainCameraFragment.DeviceList
														.get(var3)).UUID);
										var6.putString(
												"dev_nickName",
												((DeviceInfo) MainCameraFragment.DeviceList
														.get(var3)).nickName);
										var6.putString(
												"conn_status",
												((DeviceInfo) MainCameraFragment.DeviceList
														.get(var3)).Status);
										var6.putString(
												"view_acc",
												((DeviceInfo) MainCameraFragment.DeviceList
														.get(var3)).viewAccount);
										var6.putString(
												"view_pwd",
												((DeviceInfo) MainCameraFragment.DeviceList
														.get(var3)).viewPassword);
										var6.putInt(
												"camera_channel",
												((DeviceInfo) MainCameraFragment.DeviceList
														.get(var3)).getChannelIndex());
										if( MainCameraFragment.DeviceList.get(var3).DBID==0){
											long dbid = new DatabaseManager(getActivity()).getDBIDbyUID(( MainCameraFragment.DeviceList
													.get(var3)).UID);
											MainCameraFragment.DeviceList.get(var3).setDBID(dbid);
										}
										Intent var7 = new Intent();
										var7.putExtras(var6);
										var7.setClass(MainCameraFragment.this .getActivity(),
												LiveViewGLviewActivity.class);
										MainCameraFragment.this .startActivityForResult(var7, 1);
									}
								}
							})).start();

						}

					}
				}

			} catch (Exception e) {
				Log.v("IOTCamera",
						"IOTCamera listViewOnItemClickListener Exception ");
				e.printStackTrace();
			}
		}
	};
	private OnItemLongClickListener listViewOnItemLongClickListener = new OnItemLongClickListener() {
		public boolean onItemLongClick(AdapterView var1, View var2, int var3,
				long var4) {
			 
			if (var3 < MainCameraFragment.DeviceList.size()
					&& var3 < mCameraManagerment.CameraList.size() && build_for_factory_tool) {
				MainCameraFragment.this.selectedCamera = (MyCamera) mCameraManagerment.CameraList
						.get(var3);
				MainCameraFragment.this.selectedDevice = (DeviceInfo) MainCameraFragment.DeviceList
						.get(var3);
				selectdeviceIndex = var3;
				initContextMenu();
				MainCameraFragment.this.iconContextMenu.createMenu("").show();
				MainCameraFragment.this.iconContextMenu.createMenu("")
						.setCanceledOnTouchOutside(true);
				return true;
			} else {
				return false;
			}
		}
	};
	// private MainCameraFragment.ResultStateReceiver resultStateReceiver;
	private MyCamera selectedCamera = null;
	private int selectdeviceIndex = -1;
	private DeviceInfo selectedDevice = null;
	private ViewGroup mRootView;

	// private ThreadTPNS thread;

	private void checkWiFi(final Camera var1, final DeviceInfo var2) {
		(new Thread(new Runnable() {
			public void run() {
				int var1x = 0;
				// adapter.notifyDataSetChanged();
				while (true) {
					label15: {
						try {
							Thread.sleep(1000L);
						} catch (Exception var3) {
							var3.printStackTrace();
							break label15;
						}
						if (var1x == 0) {
							try {
								Thread.sleep(1000L);
							} catch (Exception var3) {
								var3.printStackTrace();

							}
							var2.Status = "连接中...";
						}
						++var1x;
					}
					// SMsgAVIoctrlPtzCmd
//					if (var1x == 30) {
//						var1.disconnect();
//						var1.connect(var2.UID);
//						var1.start(0, var2.viewAccount, var2.viewPassword);
						
//						mCameraManagerment.userIPCDeviceInfo(var2.UID);
//						mCameraManagerment.userIPCGetSupportStream(var2.UID);
//						mCameraManagerment.userIPCGetAudioOutFormat(var2.UID);
//						mCameraManagerment.userIPCGetTimeZone(var2.UID);
					
//					}
				}
			}
		})).start();
	}

	public static void check_mapping_list(Context var0) {
		SQLiteDatabase var1 = (new DatabaseManager(var0)).getReadableDatabase();
		Cursor var2 = var1.query("device", new String[] { "dev_uid" },
				(String) null, (String[]) null, (String) null, (String) null,
				(String) null);
		if (var2 != null) {
			var2.moveToFirst();

			for (int var4 = 0; var4 < var2.getCount(); ++var4) {
				// (new ThreadTPNS(var0, var2.getString(0), 0)).start();
				var2.moveToNext();
			}

			var2.close();
		}

		var1.close();
		check_remove_list(var0);
	}

	public static void check_remove_list(Context var0) {
		SQLiteDatabase var1 = (new DatabaseManager(var0)).getReadableDatabase();
		Cursor var2 = var1.query("remove_list", new String[] { "uid" },
				(String) null, (String[]) null, (String) null, (String) null,
				(String) null);
		if (var2 != null) {
			var2.moveToFirst();

			for (int var4 = 0; var4 < var2.getCount(); ++var4) {
				// (new ThreadTPNS(var0, var2.getString(0))).start();
				var2.moveToNext();
			}

			var2.close();
		}

		var1.close();
	}

	public static final String getEventType(Context var0, int var1, boolean var2) {
		switch (var1) {
		case 0:
			if (var2) {
				return var0.getText(R.string.page17_evttype_all).toString();
			}
			return var0.getText(R.string.page17_evttype_fulltime_recording).toString();
		case 1:
			return var0.getText(R.string.page17_evttype_motion_detection).toString();
		case 2:
			return var0.getText(R.string.page17_evttype_fulltime_recording).toString();
		case 3:
			return var0.getText(R.string.page17_evttype_io_alarm).toString();
		case 4:
			return var0.getText(R.string.page17_evttype_motion_pass).toString();
		case 5:
			return var0.getText(R.string.page17_evttype_video_resume).toString();
		case 6:
			return var0.getText(R.string.page17_evttype_io_alarm_pass).toString();
		case 7:
			return var0.getText(R.string.page17_evttype_video_lost).toString();
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		default:
			return "";
		case 16:
			return var0.getText(R.string.page17_evttype_expt_reboot).toString();
		case 17:
			return var0.getText(R.string.page17_evttype_sd_fault).toString();
		}
	}

	public boolean v = false;

	private void loadMyCameraListFromWebServer() {
 
	}

	// 解析设备信息
	protected List<DeviceInfo> parseMyUidList(JSONArray jsonList) {
		List<DeviceInfo> infos = new ArrayList<DeviceInfo>();
		try {
			for (int i = 0; i < jsonList.length(); i++) {
				JSONObject jo = jsonList.getJSONObject(i);
				String uid = jo.optString("UID");
				boolean isPublic = jo.optBoolean("public");
				Log.i("url", "web>>>>uid:" + uid + ",public" + isPublic);
				boolean flag = false;
				for (int j = 0; j < DeviceList.size(); j++) {
					DeviceInfo localcamera = DeviceList.get(j);
					Log.i("url", "local>>>>uid:" + localcamera.UID);
					if (uid.equals(localcamera.UID)) {
						flag = true;
						if (isPublic) {
							DeviceList.get(j).tvCameraStateText = "--已分享";
						} else {
							DeviceList.get(j).tvCameraStateText = "";
						}
						break;
					}

				}
				if (flag) {
					continue;
				}
				Log.i("url", "增加web的设备");
				String name = jo.optString("Name");
				String acc = jo.optString("LoginID");
				String pwd = jo.optString("Password");
				String location = jo.optString("Location");
				// PreferenceUtil.getInstance().putString(
				// Constants.VIEWPASSWORD,pwd) ;
				boolean isPrivate = jo.optBoolean("private");

				boolean isShare = jo.optBoolean("share");
				boolean online = jo.optBoolean("online");
				Log.i("mycamera", uid);
				DeviceInfo info = new DeviceInfo(uid, name, location,
						isPrivate, isPublic, isShare, online);
				info.isMy = true;
				info.viewAccount = acc;
				info.viewPassword = pwd;
				MyCamera var15 = new MyCamera(name, uid, acc, pwd);
				//info.UUID = var15.getUUID();
				info.snapshot = getlastsnap(info);
				infos.add(info);
				DeviceList.add(info);
				var15.registerIOTCListener(this);
				var15.connect(uid);
				var15.start(0, acc, pwd);
				mCameraManagerment.userIPCDeviceInfo(uid);
				mCameraManagerment.userIPCGetSupportStream(uid);
				mCameraManagerment.userIPCGetAudioOutFormat(uid);
				mCameraManagerment.userIPCGetTimeZone(uid);

				var15.LastAudioMode = 1;
				mCameraManagerment.CameraList.add(var15);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return infos;
	}

	 
	// AlarmMessageActivity
	private void initCameraList() {
		// DeviceList.clear();
		// CameraList.clear();
		Log.i("first", "===================================initCameraList");
		DatabaseManager var1 = new DatabaseManager(this.getActivity());
		SQLiteDatabase var2 = var1.getReadableDatabase();
		Cursor var3 = var2.query("device", new String[] { "_id",
				"dev_nickName", "dev_uid", "dev_name", "dev_pwd", "view_acc",
				"view_pwd", "event_notification", "camera_channel", "snapshot",
				"ask_format_sdcard", "camera_public" , "installmode","hardware_pkg"}, (String) null,
				(String[]) null, (String) null, (String) null, "_id LIMIT 50");
		var3.getCount();

		Set<String> JiGuangTags = new LinkedHashSet<>();

		while (var3.moveToNext()) {
			long var4 = var3.getLong(0);
			String var6 = var3.getString(1);
			final String var7 = var3.getString(2);
			final String var8 = var3.getString(5);
			final String var9 = var3.getString(6);
			int var10 = var3.getInt(7);
			int var11 = var3.getInt(8);
			byte[] var12 = var3.getBlob(9);
			int var13 = var3.getInt(10);
			int ispublic = var3.getInt(11); 
			int installmode = var3.getInt(12);
			int hardware_pkg = var3.getInt(13);

			JiGuangTags.add(var7); //uuid
			
			DeviceInfo var16 = new DeviceInfo(var4, var7, var6,
					var7, var8, var9, "", var10, var11, null);
			// Log.i("IOTCamera", "DeviceInfo.size()>>>>>>>>>>>" + var16);
			var16.setDBID(var4);
			var16.installmode = installmode;
			Bitmap var14 = getlastsnap(var16);
			var16.hardware_pkg = hardware_pkg;
			var16.snapshot = var14;
			var16.connect_count = 0;
			var16.country = PreferenceUtil.getInstance().getInt(Constants.COUNTRYCODE+ var16.UID.toUpperCase() );
			Log.i("IOTCamera", "设置 \tvar16.country  controycode_array   myCountryCode getCurrentLocaltionISOCountryCodeNumber= "+ 	var16.country);
			Log.i("IOTCamera", "var16..............未检测在线.device_connect_state:" + var16.device_connect_state+"  installmode:"+installmode+"  hardware_pkg:"+hardware_pkg);
			if (!hasexistDevice(var16)) {
				if (ispublic == 1)
					var16.isPublic = true;
				else
					var16.isPublic = false;
				Log.i("IOTCamera", "var6..............." + var6 + ",ispublic:" + var16.isPublic);
				boolean var17;
				if (var13 == 1) {
					var17 = true;
				} else {
					var17 = false;
				}

				var16.ShowTipsForFormatSDCard = var17;
				DeviceList.add(var16);
				Log.i("guo..IOTCamera", "DeviceList.add.."+var16.UID+"...........检测不存在，加入设备列表..device_connect_state:" + var16.device_connect_state);
 
				MyCamera mMyCamera = new MyCamera(var6, var7, var8, var9);
				mMyCamera.hardware_pkg = hardware_pkg;
//				mMyCamera.setInstallmode(installmode);
				Log.e("",this.getClass().getSimpleName()+" mDeviceInfo.installmode:"+ installmode+"   hardware_pkg:"+hardware_pkg);
			if (!hasexistCamera(mMyCamera)) {
				mMyCamera.registerIOTCListener(this);
				mMyCamera.connect(var7);
 				mMyCamera.start(0, var8, var9); 
				mMyCamera.LastAudioMode = 1;
				mCameraManagerment.AddCameraItem(mMyCamera);
					Log.v("guo..IOTCamera", "initCameraList mMyCamera   add  reconnect UID:"+var6 +"   var9:"+var9);
				 
			}else{
					Log.v("guo..IOTCamera", "222initCameraList mMyCamera   add  reconnect UID:"+var6 +"   var9:"+var9);
				mMyCamera.registerIOTCListener(this);
			    mMyCamera.connect(var7);
					Log.v("guo..IOTCamera", "initCameraList mMyCamera   add  connect");
			}
			
			}  
			try {
				Thread.sleep(20L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		var3.close();
		var2.close();
		String var23;
		if (mCameraManagerment.CameraList.size() == 0) {
			String var24 = this.getText(R.string.page26_ntfAppRunning).toString();
			Object[] var25 = new Object[] { this.getText(R.string.app_name)
					.toString() };
			var23 = String.format(var24, var25);
		} else {
			String var21 = this.getText(R.string.page26_ntfCameraRunning).toString();
			Object[] var22 = new Object[] { Integer.valueOf(mCameraManagerment.CameraList.size()) };
			// var23 = String.format(var21, var22);
			var23 = var21 + "(" + mCameraManagerment.CameraList.size() + ")";
		}
		NotificationTagManager.getInstance().listTags();
		NotificationTagManager.getInstance().addJiguanTag(JiGuangTags);
		for (DeviceInfo md : DeviceList) {
		    NotificationTagManager.getInstance().addTag(md.UID );
		    NotificationTagManager.getInstance().addxiaomiTag(md.UID );
		}
		adapter.notifyDataSetChanged();
		boolean show = false;
		if(show)//不想显示状态栏提示就关闭，想就打开
		// }
		this.showButtonNotify(var23);
		
		//this.startOnGoingNotification(var23);
//		if(CameraList.size()>8 && maddMenu!=null)
//			maddMenu.setVisible(false);
//		else
//		{
//			maddMenu.setVisible(false);
//		}
		verifyCameraLimit();
		isFirstinit = false;
		
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(DeviceList.size()==0){
					empty_layout.setVisibility(View.VISIBLE);
					listView.setVisibility(View.GONE);
			
				}else{
					empty_layout.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
				}	
				if(adapter!=null)
					adapter.notifyDataSetChanged();
			}
		});
	}

	private boolean hasexistCamera(MyCamera checkCamera) {

		for (MyCamera myCamera : mCameraManagerment.CameraList) { 
			if (myCamera.getUID().equals(checkCamera.getUID()))
				return true;
		}
		return false;
	}

	private static MyCamera getexistCamera(MyCamera checkCamera) {

		for (MyCamera myCamera : CameraManagerment.getInstance().CameraList) { 
			if (myCamera.getUID().equals(checkCamera.getUID()))
				return myCamera;
		}
		return null;
	}

	public static MyCamera getexistCameraBySid(int sid) {
		for (MyCamera myCamera : CameraManagerment.getInstance().CameraList) { 
			if (myCamera.getMSID()==sid)
				return myCamera;
		}
		return null;
	}
	private boolean hasexistDevice(DeviceInfo checkDevice) {

		for (DeviceInfo mydev : DeviceList) { 
			Log.v("","hasexistDevice  checkDevice: "+checkDevice.UID+"   checkDevice:"+checkDevice.device_connect_state
					+"   mydev.device_connect_state:"+mydev.device_connect_state);
			if (mydev.UID.equals(checkDevice.UID))
				return true;
		}
		return false;
	}

	private static DeviceInfo getexistDevice(DeviceInfo checkDevice) {

		for (DeviceInfo mydev : DeviceList) {
			// Log.v("hasexistDevice", "mydev.UID = " + mydev.UID
			// + "checkDevice.UID" + checkDevice.UID);
			if (mydev.UID.equals(checkDevice.UID))
				return mydev;
		}
		return null;
	}

	public static DeviceInfo getexistDevice(String checkUID) {

		for (DeviceInfo mydev : DeviceList) {
			// Log.v("hasexistDevice", "mydev.UID = " + mydev.UID
			// + "checkDevice.UID" + checkDevice.UID);
			if (mydev.UID.equals(checkUID))
				return mydev;
		}
		return null;
	}

	// 刷新菜单

	private void initContextMenu() {
		Resources var1 = this.getResources();
		if (this.iconContextMenu == null) {
			this.iconContextMenu = new IconContextMenu(this.getActivity(), 1);
		}

		this.iconContextMenu.clearItems();
		this.iconContextMenu.addItem(var1, this.getText(R.string.page17_ctxReconnect),
				R.drawable.ic_reconnect_camera, 1);
		this.iconContextMenu.addItem(var1,
				this.getText(R.string.page17_ctxEditCamera),
				R.drawable.ic_edit_camera, 2);
		
//		if(uidPrefix.equals("FFFFFFFFFFFFFFFFFF")){
//			this.iconContextMenu.addItem(var1,
//					this.getText(R.string.ctxChangeUID),
//					R.drawable.ic_edit_camera, 7);
//		}
//		if (this.selectedCamera.getEventListSupported(0)) {
//			this.iconContextMenu.addItem(var1,
//					this.getText(R.string.ctxViewEvent),
//					R.drawable.ic_view_event, 3);
//		}
//
//		this.iconContextMenu.addItem(var1,
//				this.getText(R.string.ctxViewSnapshot), R.drawable.ic_album, 4);
		this.iconContextMenu.addItem(var1,
				this.getText(R.string.ctxRemoveCamera),
				R.drawable.ic_delete_camera, 5);
		// this.iconContextMenu.addItem(var1,
		// this.getText(R.string.ctxPublicCamera),
		// R.drawable.ic_btn_actionmenu_share_default, 6);
		this.iconContextMenu
				.setOnClickListener(new IconContextMenu.IconContextMenuOnClickListener() {
					public void onClick(int var1) {
						Bundle var2 = new Bundle();
						Intent var3 = new Intent();
						switch (var1) {
						case 1:// ��������

							new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									selectedCamera.disconnect();
									selectedCamera.connect(selectedDevice.UID);
		
								}
							}).start();

							Toast.makeText(getActivity(), R.string.mainfragment_refreshableview_refreshing,
									Toast.LENGTH_SHORT).show();
							return;
						case 2:// �޸������
							if(selectedDevice.device_connect_state == CONNSTATUS_CONNECTED ){
								selectedCamera.start(0,
										selectedDevice.viewAccount,
										selectedDevice.viewPassword);
							}
							var2.putLong("db_id", selectedDevice.DBID);
							//var2.putString("dev_uuid", selectedDevice.UUID);
							var2.putString("dev_uid", selectedDevice.UID);
							var2.putString("view_acc",
									selectedDevice.viewAccount);
							var2.putString("view_pwd",
									selectedDevice.viewPassword);
							var2.putString("dev_nickName",
									selectedDevice.nickName);
							var2.putInt("camera_channel",
									selectedDevice.getChannelIndex());
							var2.putInt("camera_public",
									selectedDevice.isPublic ? 1 : 0);
							var3.putExtras(var2);
							var3.setClass(getActivity(),
									SettingActivity.class);
							startActivityForResult(var3, 2);
							return;
						case 3:// �鿴�¼�
//							selectedDevice.n_gcm_count = 0;
//							//var2.putString("dev_uuid", selectedDevice.UUID);
//							var2.putString("dev_uid", selectedDevice.UID);
//							var2.putString("dev_nickName",
//									selectedDevice.nickName);
//							var2.putString("view_acc",
//									selectedDevice.viewAccount);
//							var2.putString("view_pwd",
//									selectedDevice.viewPassword);
//							var2.putInt("camera_channel",
//									selectedDevice.getChannelIndex());
//							var3.putExtras(var2);
//							var3.setClass(getActivity(),
//									EventListActivity.class);
//							startActivity(var3);
							return;
						case 4:// �鿴����
								// File var5 = new File(Environment
								// .getExternalStorageDirectory()
								// .getAbsolutePath()
								// + "/snapshot/" + selectedDevice.UID);
								// // Log.i("IOTCamera", "图浏览：" +
								// // var5.getAbsolutePath()
								// // + ",size" + var5.list().length);
								// String[] var6 = var5.list();
								// if (var6 != null && var6.length > 0) {
								// // var5.getAbsolutePath()+"/" + var6[-1 +
								// // var6.length];
								// Intent var9 = new Intent(getActivity(),
								// GridViewGalleryActivity.class);
								// var9.putExtra("snap", selectedDevice.UID);
								// var9.putExtra("images_path",
								// var5.getAbsolutePath());
								// getActivity().startActivity(var9);

							if (getActivity() == null)
								return;
//							Fragment newContent = new PhotoManageFragment();
//							Bundle bundle = new Bundle();
//							bundle.putInt("key", selectdeviceIndex);
//							newContent.setArguments(bundle);
//							if (getActivity() instanceof MainActivity) {
//								MainActivity fca = (MainActivity) getActivity();
//								fca.switchContent(newContent, "");
////								fca.getmFrag().setmSelectedMenuId(3);
//							}

							// String var7 = getActivity().getText(
							// R.string.tips_no_snapshot_found).toString();
							// Toast.makeText(getActivity(), var7, 0).show();
							return;
						case 5:// ɾ�������
							(new Builder(getActivity()))
									.setIcon(17301543)
									.setTitle(
											getActivity().getText(
													R.string.page5_tips_warning))
									.setMessage(
											getActivity()
													.getText(
															R.string.page26_tips_remove_camera_confirm))
									.setPositiveButton(
											getActivity().getText(R.string.ok),
											new OnClickListener() {
												public void onClick(
														DialogInterface var1,
														int var2) {
													doDeleteMyDevice();
								
												}
											})
									.setNegativeButton(
											getActivity().getText(
													R.string.cancel),
											new OnClickListener() {
												public void onClick(
														DialogInterface var1,
														int var2) {
												}
											}).show();
							return;
						case 6:
							(new Builder(getActivity()))
									.setIcon(17301543)
									.setTitle(
											getActivity().getText(
													R.string.page5_tips_warning))
									.setMessage(
											getActivity()
													.getText(
															R.string.page26_tips_share_camera_confirm))
									.setPositiveButton(
											getActivity().getText(R.string.ok),
											new OnClickListener() {
												public void onClick(
														DialogInterface var1,
														int var2) {

												 
																		// add
													//
													// String var13;
													// if (CameraList.size() ==
													// 0) {
													// String var14 =
													// getActivity()
													// .getText(
													// R.string.ntfAppRunning)
													// .toString();
													// Object[] var15 = new
													// Object[] { getActivity()
													// .getText(
													// R.string.app_name)
													// .toString() };
													// var13 = String.format(
													// var14, var15);
													// } else {
													// String var11 =
													// getActivity()
													// .getText(
													// R.string.ntfCameraRunning)
													// .toString();
													// Object[] var12 = new
													// Object[] { Integer
													// .valueOf(CameraList
													// .size()) };
													// var13 = String.format(
													// var11, var12);
													// }

													  verifyCameraLimit();
													// startOnGoingNotification(var13);

													// showAlert(
													// getActivity(),
													// getActivity()
													// .getText(
													// R.string.tips_warning),
													// getActivity()
													// .getText(
													// R.string.tips_remove_camera_ok),
													// getActivity()
													// .getText(
													// R.string.ok));
												}
											})
									.setNegativeButton(
											getActivity().getText(
													R.string.cancel),
											new OnClickListener() {
												public void onClick(
														DialogInterface var1,
														int var2) {
												}
											}).show();
							return;
						case 7:
						{
//							progressdlg.setMessage(getResources()
//									.getString(R.string.u_tips_scan_qrcode));
//							progressdlg.setCancelable(false);
//							progressdlg.show();
							
							Intent intent = new Intent(getActivity(),
									CaptureActivity.class);
							startActivityForResult(intent, CAMERA_SCAN_REQUEST);
							//overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
						}
						return;
						default:
						}
					}
				});
	}
	JsonHttpResponseHandler mJsonHttpResponseHandler = new   JsonHttpResponseHandler(){
		@Override
		public void onStart() {
			super.onStart();
			//myProgressBar.show();
		}

		@Override
		public void onSuccess(int statusCode,
				JSONObject response) {
			//myProgressBar.dismiss();
			Log.e("url", "voipServiceDeviceOperate:" + response.toString());
			String result = response.toString();
			if (result != null) {
				boolean state = response.optBoolean("state");
				if (state) {

				} else {
					try {
						if (response.getInt("reason") == 207) { 

						} else { 
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void onFailure(int statusCode, Throwable error,
				String content) { 
			error.printStackTrace();
		}

	};
	private void doDeleteMyDevice() {
		// String password = getHelper().getConfig(Constants.USER_PASSWORD);
		String password = selectedCamera.getPassword();
		String UID = selectedCamera.getUID();
		String nickname = selectedCamera.getName();
		if (!UbiaApplication.ISWEB) {
			if(mProgressBar!=null)
			mProgressBar.dismiss();
			DeviceInfo selectedDevice = this.DeviceList.get(selectdeviceIndex);
			MyCamera selectedCamera = (MyCamera) this.mCameraManagerment.CameraList
					.get(selectdeviceIndex);
			selectedCamera.stop(0);
			selectedCamera.disconnect();
			selectedCamera.unregisterIOTCListener(MainCameraFragment.this);
			this.mCameraManagerment.CameraList.remove(selectedCamera);
			DatabaseManager var5 = new DatabaseManager(getActivity());
			SQLiteDatabase var6 = var5.getReadableDatabase();
			Cursor var7 = var6.query("snapshot", new String[] { "_id",
					"dev_uid", "file_path", "time" }, "dev_uid = \'"
					+ selectedDevice.UID + "\'", (String[]) null,
					(String) null, (String) null, "_id LIMIT 4");

			while (var7.moveToNext()) {
				File var8 = new File(var7.getString(2));
				if (var8.exists()) {
					var8.delete();
				}
			}
		 
		 HttpClient.operateDeviceVoip(selectedDevice.UID, 2,mJsonHttpResponseHandler);
		 
		 NotificationTagManager.getInstance().removeTag(selectedDevice.UID);
			var7.close();
			var6.close();
			var5.removeSnapshotByUID(selectedDevice.UID);
			var5.removeDeviceByUID(selectedDevice.UID);
			this.DeviceList.remove(selectedDevice);
			adapter.notifyDataSetChanged();
		}  
		verifyCameraLimit() ;
		
	}

 
	private void updateDevice(DeviceInfo deviceInfo) {
		// new
		// DatabaseManager(this.getActivity()).updateDeviceInfoByDBID(selectedDevice.DBID,
		// selectedDevice.UID, selectedDevice.nickName, selectedDevice.location,
		// selectedDevice.viewAccount, mDevice.viewPassword,
		// mDevice.getChannelIndex(), mDevice.isPublic, mDevice.isPrivate,
		// mDevice.isShare, mDevice.isAlarm);
		(new DatabaseManager(getActivity())).updateDeviceInfoByDBID(
				this.selectedDevice.DBID, this.selectedDevice.UID,
				selectedDevice.nickName, "", "", selectedDevice.viewAccount,
				selectedDevice.viewPassword,
				this.selectedDevice.EventNotification,
				this.selectedDevice.getChannelIndex(), this.selectedDevice.isPublic);
		// AntsApplication.bus.post(deviceInfo);
	}

 
	private boolean isNetworkAvailable() {
		((ConnectivityManager) this.getActivity().getSystemService(
				"connectivity")).getActiveNetworkInfo();
		return true;
	}

	private void quit() {
		Log.i("maincamera", "MainCameraFragmen222222t>>>>>>>>>>>>>quit");
		this.stopOnGoingNotification();

		new Thread() {
			public void run() {
				Iterator var1 = mCameraManagerment.CameraList.iterator();

				while (var1.hasNext()) {
					MyCamera var2 = (MyCamera) var1.next();
					// NotificationTagManager.getInstance().removeTag(var2.getmDevUID()) ;
					if (var2.isSessionConnected()) {
						var2.disconnect();
						var2.unregisterIOTCListener(MainCameraFragment.this);
					}
				}

				System.out.println("kill process");
//				 NotificationTagManager.getInstance().unbind();
				MyCamera.uninit();
			}
		}.start();
//		 NotificationTagManager.getInstance().unbind();
		Log.i("maincamera", "MainCameraFragment>>>>>>>>>>>>kill process");
		// Process.killProcess(Process.myPid());
		  this.getActivity().finish();
		System.exit(0);
	}

	private View setupView() {
		setHasOptionsMenu(true);
		View v = View.inflate(getActivity(), R.layout.camera_list, null);
		mRootView = (ViewGroup) v;

		//listView = (ListView) v.findViewById(R.id.list); 
		// this.addDeviceView = this.getActivity().getLayoutInflater()
		// .inflate(R.layout.add_device_row, (ViewGroup) null);
		// v.findViewById(R.id.btnAddDevice).setOnClickListener(this);
		empty_layout = (LinearLayout) v.findViewById(R.id.empty_layout);
		
		add_device_row_ll =  (LinearLayout)v.findViewById(R.id.add_device_row_ll);
		add_device_row_ll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intentadd = new Intent(MainCameraFragment.this.getActivity(),
						AddCarmeraFragmentActivity.class);
				startActivityForResult(intentadd, 1);
			}
		});
		v.findViewById(R.id.nodevicetip) .setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intentadd = new Intent(MainCameraFragment.this.getActivity(),
						AddCarmeraFragmentActivity.class);
				startActivityForResult(intentadd, 1);
			}
		});
		// this.getActivity().getWindow().setFlags(128, 128);
		// this.getActivity().setContentView(R.layout.main);
		 this.listView = (ListView)v.findViewById(
		 R.id.list);
		 this.addDeviceView = this.getActivity().getLayoutInflater()
		 .inflate(R.layout.add_device_row, (ViewGroup) null);
		// this.adapter = new MainCameraFragment.DeviceListAdapter(
		// this.getActivity());
		// this.initCameraList();
		 this.listView.addFooterView(this.addDeviceView);
		// this.listView.setAdapter(this.adapter);
		 this.listView.setOnItemClickListener(this.listViewOnItemClickListener); 
		// this.listView
		// .setOnItemLongClickListener(this.listViewOnItemLongClickListener);
	 	this.verifyCameraLimit();
		 registerForContextMenu(listView);
		 return v;
	}

	public static void showAlert(Context var0, CharSequence var1,
			CharSequence var2, CharSequence var3) {
		Builder var4 = new Builder(var0);
		var4.setIcon(17301543);
		var4.setTitle(var1);
		var4.setMessage(var2);
		var4.setPositiveButton(var3, new OnClickListener() {
			public void onClick(DialogInterface var1, int var2) {
			}
		}).show();
	}

	private void showNotification(DeviceInfo var1, int var2, int var3, long var4) {
		try {

//			if (PreferenceUtil.getInstance().getBoolean(
//					Constants.IS_MESSAGE_UNCHECKED))
//				return;
			if(var1==null) {
				Log.e("error",
						"=====================================showNotification  var1==null  return;");
				return;
			}else{
				Log.i("deviceinfo",
						"=====================================showNotification  var1.UID="+var1.UID+"   var1.nickName="+var1.nickName);
			}
			NotificationManager var7 = (NotificationManager) this.getActivity()
					.getSystemService("notification");
			Log.i("first",
					"=====================================showNotification");
			Bundle var8 = new Bundle();
			//var8.putString("dev_uuid", var1.UUID);
			var8.putString("dev_uid", var1.UID);
			var8.putString("dev_nickName", var1.nickName);
			var8.putInt("camera_channel", var2);
			var8.putString("view_acc", var1.viewAccount);
			var8.putString("view_pwd", var1.viewPassword);
			Intent var9 = new Intent(this.getActivity(),
					LiveViewGLviewActivity.class);
			var9.setFlags(335544320);
			var9.putExtras(var8);
			PendingIntent mPendingIntent = PendingIntent.getActivity(this.getActivity(),
					0, var9, 134217728);
			Calendar var13 = Calendar.getInstance();
			var13.setTimeZone(TimeZone.getDefault());
			var13.setTimeInMillis(var4);
			var13.add(2, 0);
			String var14 = this.getText(R.string.page26_ntfIncomingEvent).toString();
			Object[] var15 = new Object[] { var1.nickName };
		/*	Notification var16 = new Notification(R.drawable.nty_alert,
					String.format(var14, var15), var13.getTimeInMillis());*/
			Notification var16 = new Notification(
					 R.drawable.nty_alert, getString(R.string.page26_page34_MyPushMessageReceiver_alarm_alert_from) +" "+ var1.nickName, (System.currentTimeMillis()));
			var16.flags |= Notification.FLAG_AUTO_CANCEL;
			
			// var16.flags |= 32;
			/**
			 *  	 public static final int DEFAULT_SOUND = 1; 
  					public static final int DEFAULT_VIBRATE = 2; 
  					public static final int DEFAULT_LIGHTS = 4;
  */
			boolean Pushmute= PreferenceUtil.getInstance().getBoolean(Constants.IS_PUSHMUTE_CHECKED) ;
			boolean Pushvirable= PreferenceUtil.getInstance().getBoolean(Constants.IS_VIBRATE_CHECKED) ;
			
			Log.v("deviceinfo", "Pushmute ="+Pushmute+"  Pushvirable = "+Pushvirable);
			
			
		   if (!Pushmute && Pushvirable) {
				var16.defaults = Notification.DEFAULT_VIBRATE |Notification.DEFAULT_SOUND ;
			} else if ( Pushmute && Pushvirable) {
				var16.defaults = Notification.DEFAULT_VIBRATE  ;
			}else if (! Pushmute && !Pushvirable) {
				var16.defaults =Notification.DEFAULT_SOUND  ;
			} else if ( Pushmute && !Pushvirable) {
				var16.defaults = 0;
			} 


			String var17 = this.getText(R.string.page26_ntfIncomingEvent).toString();
			Object[] var18 = new Object[] { var1.nickName };
			String var19 = String.format(var17, var18);
			String var20 = this.getText(R.string.page26_ntfLastEventIs).toString();
			/*Object[] var21 = new Object[] { getEventType(this.getActivity(),
					var3, false) };*/
			Object[] var21 = new Object[] {getString(R.string.page26_page34_MyPushMessageReceiver_alarm_alert_from) +" "+ var1.nickName  };
			/*var16.setLatestEventInfo(this.getActivity(), var19,
					String.format(var20, var21), mPendingIntent);*/
//			var16.setLatestEventInfo(UbiaApplication.getInstance()
//					.getApplicationContext(), getEventType(this.getActivity(),
//							var3, false),  getString(R.string.page26_page34_MyPushMessageReceiver_alarm_alert_from) + " " +var1.nickName, mPendingIntent);
			var7.notify(0, var16);

			if(!getRunningActivityName(LiveViewGLviewActivity.class.getName()))
			{
			AlarmMessage alarmMessage = new AlarmMessage("new message",
					var1.UID, var1.nickName);
			Intent activityIntent = new Intent(this.getActivity(),
					AlarmMessageActivity.class).putExtra("alarmMessage",
					alarmMessage);
			activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.getActivity().startActivity(activityIntent);
			}
	 

		} catch (Exception var22) {
			var22.printStackTrace();

		}
	}
	public static  boolean getRunningActivityName(String name) {
		ActivityManager activityManager = (ActivityManager) UbiaApplication
				.getInstance().getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
				.getClassName();
		if (runningActivity.contains(name)) {
			return true;
		}
		Log.v("deviceinfo", "getRunningActivityName name runningActivity =" + runningActivity +"    name="+name);

		return false;
	}
   
	public static  boolean getAllRunningActivityName(String name) {
		ActivityManager activityManager = (ActivityManager) UbiaApplication
				.getInstance().getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
	    
	        boolean flag = false;       
	    List<RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(10);  //获取从栈顶开始往下查找的10个activity  
        for (RunningTaskInfo taskInfo : taskInfoList) { 
			String eqName = taskInfo.baseActivity .getClassName();
			Log.v("deviceinfo", "getAllRunningActivityName name eqName =" + eqName +"    name="+name);
			if(eqName.contains(name)) {
            flag = true;    
            break;  //跳出循环，优化效率 
			}
        }  
 


		return flag;
	}
   
	private void showSDCardFormatDialog(final Camera var1, final DeviceInfo var2) {
		final AlertDialog var3 = (new Builder(this.getActivity())).create();
		var3.setTitle(R.string.page26_dialog_FormatSDCard);
		var3.setIcon(17301573);
		View var4 = var3.getLayoutInflater().inflate(R.layout.format_sdcard,
				(ViewGroup) null);
		var3.setView(var4);
		final CheckBox var5 = (CheckBox) var4
				.findViewById(R.id.chbShowTipsFormatSDCard);
		Button var6 = (Button) var4.findViewById(R.id.btnFormatSDCard);
		Button var7 = (Button) var4.findViewById(R.id.btnClose);
		var6.setOnClickListener(new View.OnClickListener() {
			public void onClick(View var1x) {
				
				mCameraManagerment.userIPCFormatExistStorage(var1.getmDevUID());
				
				var2.ShowTipsForFormatSDCard = var5.isChecked();
				(new DatabaseManager(MainCameraFragment.this.getActivity()))
						.updateDeviceAskFormatSDCardByUID(var2.UID,
								var2.ShowTipsForFormatSDCard);
				var3.dismiss();
			}
		});
		var7.setOnClickListener(new View.OnClickListener() {
			public void onClick(View var1) {
				var2.ShowTipsForFormatSDCard = var5.isChecked();
				(new DatabaseManager(MainCameraFragment.this.getActivity()))
						.updateDeviceAskFormatSDCardByUID(var2.UID,
								var2.ShowTipsForFormatSDCard);
				var3.dismiss();
			}
		});
	}



 
	  protected void requestPermission(int requestCode) {
		   // TODO Auto-generated method stub
		   // 6.0以上系统才可以判断权限

		   if (VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
		      // 进入设置系统应用权限界面
		      Intent intent = new Intent(Settings.ACTION_SETTINGS);
		      startActivity(intent);
		      return;
		   } else if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 运行系统在5.x环境使用
		      // 进入设置系统应用权限界面
		      Intent intent = new Intent(Settings.ACTION_SETTINGS);
		      startActivity(intent);
		      return;
		   }
		   return;
		}
	public void stopOnGoingNotification() {
		NotificationManager var1 = (NotificationManager) this.getActivity()
				.getSystemService("notification");
		var1.cancel(0);
		var1.cancel(1);
	}

	private void verifyCameraLimit() {
		Log.i("wifi", "verifyCameraLimit");
		if (mCameraManagerment.CameraList.size() < 8) {
			if (this.listView.getFooterViewsCount() == 0) {
				  this.listView.addFooterView(this.addDeviceView);
				this.adapter.notifyDataSetChanged();
			}
		} else if (this.listView.getFooterViewsCount() > 0) {
			 this.listView.removeFooterView(this.addDeviceView);
			this.adapter.notifyDataSetChanged();
		}
 
	}

	public void onActivityResult(int var1, int var2, Intent var3) {
		super.onActivityResult(var1, var2, var3);
		Log.i("wifi", " MainCameraFragment onActivityResult:" + var1 + ","
				+ var2);
		verifyCameraLimit();
		if (var1 == 0) {
			switch (var2) {
			case -1:
				Bundle var13 = var3.getExtras();
				long var14 = var13.getLong("db_id");
				String var16 = var13.getString("dev_nickname");
				String var17 = var13.getString("dev_uid");
				String var18 = var13.getString("view_acc");
				String var19 = var13.getString("view_pwd");
				int var20 = var13.getInt("camera_channel");
				Log.i("IOTCamera", " 添加设备: dev_nickName:" + var16 + ",dev_uid:"
						+ var17 + ",view_acc:" + var18 + ",view_pwd:" + var19
						+ ",camera_channel:" + var20);
				MyCamera var21 = new MyCamera(var16, var17, var18, var19);
				DeviceInfo var22 = new DeviceInfo(var14, "",
						var16, var17, var18, var19, "", 3, var20, (Bitmap) null);
				DeviceList.add(var22);
				var21.registerIOTCListener(this);
				var21.connect(var17);
				var21.start(0, var18, var19);
				mCameraManagerment.userIPCDeviceInfo(var17);
				mCameraManagerment.userIPCGetSupportStream(var17);
				mCameraManagerment.userIPCGetAudioOutFormat(var17);
				mCameraManagerment.userIPCGetTimeZone(var17);
				var21.LastAudioMode = 1;
				mCameraManagerment.CameraList.add(var21);
				this.adapter.notifyDataSetChanged();
				String var28;
				if (mCameraManagerment.CameraList.size() == 0) {
					String var29 = this.getText(R.string.page26_ntfAppRunning)
							.toString();
					Object[] var30 = new Object[] { this.getText(
							R.string.app_name).toString() };
					var28 = String.format(var29, var30);
				} else {
					String var26 = this.getText(R.string.page26_ntfCameraRunning) .toString();
					Object[] var27 = new Object[] { Integer.valueOf(mCameraManagerment.CameraList .size()) };
					var28 = String.format(var26, var27);
				}

				this.verifyCameraLimit();

				return;
			}
		} else if (var1 == 1) {
			switch (var2) {
			case 100:	
				
				 
//				Fragment newContent = new PhotoManageFragment();
//				Bundle bundle = new Bundle();
//				bundle.putInt("key", selectdeviceIndex);
//				newContent.setArguments(bundle);
//				if (getActivity() instanceof MainActivity) {
//					MainActivity fca = (MainActivity) getActivity();
//					fca.switchContent(newContent, "");
////					fca.getmFrag().setmSelectedMenuId(3);
//			 
//				}
			break;
			case -1:
				Bundle var4 = var3.getExtras();
				String var5 = var4.getString("dev_uuid");
				String var6 = var4.getString("dev_uid");
				byte[] var7 = var4.getByteArray("snapshot");
				String viewPassword = var4.getString("view_password");
				int var10 = 0;

				while (true) {
					int var11 = DeviceList.size();
					if (var10 >= var11) {
						return;
					}
					DeviceInfo devinfo = (DeviceInfo) DeviceList.get(var10);
					if (var6.equalsIgnoreCase(devinfo.UID)) {
						Log.i("images", "uid:" + devinfo.UID); 
						devinfo.snapshot = getlastsnap(devinfo);
						devinfo.viewPassword = viewPassword;
						this.adapter.notifyDataSetChanged();
						return;
					}

					++var10;
				}
			default:
				Log.i("wifi", " MainCameraFragment 成功添加设备  onActivityResult " );
				return;
			}
		} else if (var1 == 2) {
			switch (var2) {
			case -1:
				this.adapter.notifyDataSetChanged();
				return;
			default:
				return;
			}
		} else if (var1 == 556) {

		} else if (var1 == CAMERA_SCAN_REQUEST
				&& var2 == Activity.RESULT_OK) {
			String code = var3.getStringExtra("result");
			DeviceSearchResult result = new DeviceSearchResult();
			result.UID = code;
			
			Log.i("ubiacamera", "select Camera :" +MainCameraFragment.this.selectedDevice.UID + "Change to:" + result.UID);

			mCameraManagerment.userUbiaSetUID(MainCameraFragment.this.selectedCamera.getmUID(), result.UID.getBytes());
			

			
//			Intent intent = new Intent(getActivity(), LoginAddDeviceActivity.class);
//			intent.putExtra("selectUID", result.UID);
//			startActivityForResult(intent, CAMERA_SCAN_ADD_REQUEST);
		}else if(var1 == CAMERA_SCAN_ADD_REQUEST){
			Log.i("ubiacamera", "===========================add finish, REBOOT Camera");
			(new Builder(getActivity()))
			.setIcon(17301543)
			.setTitle(
					getActivity().getText(
							R.string.page26_tips_info))
			.setMessage(
					getActivity()
							.getText(
									R.string.page26_tips_change_uid_note))
			.setPositiveButton(
					getActivity().getText(R.string.ok),
					new OnClickListener() {
						public void onClick(
								DialogInterface var1,
								int var2) {
							var1.dismiss();
							//quit();
						}
					}).show();
//			.setNegativeButton(
//					getActivity().getText(
//							R.string.cancel),
//					new OnClickListener() {
//						public void onClick(
//								DialogInterface var1,
//								int var2) {
//						}
//					}).show();
		}

	}


	public Bitmap getlastsnap(DeviceInfo item) {
		Bitmap lastbitmap = null;
		if (isSDCardValid()) {
			File var20 = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/LastSnapshot/"
					+ item.UID
					+ "/"
					+ item.UID + ".jpg");
			// File var21 = new File(var20.getAbsolutePath() + "/"
			// + var4.UID+"/"+var4.UID+".jpg");
			if (var20.exists()) {
				Log.i("images", "snapshot is not null");
				Log.i("images", "snapshot:" + var20.getAbsoluteFile());
				String url = var20.getAbsoluteFile() + "";
				lastbitmap = getLoacalBitmap(url);
			}
		}
		return lastbitmap;
	}

	public void onConfigurationChanged(Configuration var1) {
		super.onConfigurationChanged(var1);
		Configuration var2 = this.getResources().getConfiguration();
		if (var2.orientation == 2) {
			System.out.println("ORIENTATION_LANDSCAPE");
		} else if (var2.orientation == 1) {
			System.out.println("ORIENTATION_PORTRAIT");
			return;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	 
		// mHttpClient = new HttpClient(token, tokenSecret);
		Resources res = getResources();
		default_snap = BitmapFactory.decodeResource(res, R.drawable.usnap_bg);
		initButtonReceiver();
		MyCamera.init();
	}
	private static boolean isgotoAutoManagerFirst = false;
	private static boolean isgotoAutoManager = false;
	private static boolean isgotoNotifyManager = false;
	private static boolean isgotoNotifyManagerFirst = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	 
		Log.i("first", "===================================onCreateView");
		isFirstinit = true;
		View view = null;
		
		if (this.isNetworkAvailable()) {
			view = this.setupView();
		} else {
			this.getActivity().getWindow().setFlags(128, 128);
			view = View.inflate(getActivity(), R.layout.camera_list, null);
			// this.getActivity().setContentView(R.layout.no_network_connection);
			((Button) this.getActivity().findViewById(R.id.btnRetry))
					.setOnClickListener(new View.OnClickListener() {
						public void onClick(View var1) {
							if (MainCameraFragment.this.isNetworkAvailable()) {
								MainCameraFragment.this.setupView();
							}

						}
					});
	
		}
		//init screen
		WindowManager wm = (WindowManager) getActivity()
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		  screenWidth =wm.getDefaultDisplay().getWidth();// outMetrics.widthPixels;
		  density =  getActivity().getResources().getDisplayMetrics().density;
		  
		  
		initView(view);
		return view;
	}
	
	private RelativeLayout title_father;
	/*** 无摄像头提示展示 */
	private LinearLayout ll_no_connect_tips;
	/*** 有摄像头显示 */
	private RelativeLayout rl_list;
	/** 后退或者菜单 */
	private ImageView back;
	/*** activity标题 */
	private TextView title;
	/*** 刷新按钮 */
	private ImageView refIcon;
	/*** 标题栏右图标 */
	private ImageView rightIco;
	/*** 是否检查摄像机事件提示状态 */
	private void initView(final View view) { 
		title_father = (RelativeLayout) view.findViewById(R.id.title_father); 
		back = (ImageView) view.findViewById(R.id.back);
		title = (TextView) view.findViewById(R.id.title);
		refIcon = (ImageView) view.findViewById(R.id.right_image2);
		refIcon.setImageResource(R.drawable.refresh);
		refIcon.setVisibility(View.GONE);
		refIcon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub
				Log.d("test", "zhaogenghuai:onMenuItemClick");
				Toast.makeText(getActivity(), R.string.mainfragment_refreshableview_refreshing,
						Toast.LENGTH_SHORT).show();
				nowtime = System.currentTimeMillis();
				if (nowtime - lasttime > 1500) {
					lasttime = nowtime;
					if (!isOver) {
						isOver = true;
						new Thread() {
							public void run() { 
								for (int i = 0; i < mCameraManagerment.CameraList.size(); i++) {
									mCameraManagerment.CameraList.get(i).disconnect();

									mCameraManagerment.CameraList.get(i).connect(
											DeviceList.get(i).UID);

									mCameraManagerment.CameraList
											.get(i)
											.start(0,
													DeviceList.get(i).viewAccount,
													DeviceList.get(i).viewPassword);
								 
//									mCameraManagerment.userIPCDeviceInfo(mCameraManagerment.CameraList.get(i).getmUID());
//									mCameraManagerment.userIPCGetSupportStream(mCameraManagerment.CameraList.get(i).getmUID());
//									mCameraManagerment.userIPCGetAudioOutFormat(mCameraManagerment.CameraList.get(i).getmUID());
//									mCameraManagerment.userIPCGetTimeZone(mCameraManagerment.CameraList.get(i).getmUID());
									try {
										Thread.sleep(200);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch
										// block
										e.printStackTrace();
									}
								}

								isOver = false;
							};
						}.start();
					}
				}
				 
			
			}
 
		});
		rightIco = (ImageView) view.findViewById(R.id.right_image);
		title.setText(getResources().getString(R.string.page26_txhometitle));
		// title.setText(getResources().getString(R.string.add_device));
		//back.setImageResource(R.drawable.title_bar_guide_top_menu_press);版本
		back.setVisibility(View.GONE);
		rightIco.setImageResource(R.drawable.about_n);
		rightIco.setVisibility(View.VISIBLE);
		view.findViewById(R.id.left_ll).setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}

	 
		});
		rightIco.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainCameraFragment.this.getActivity(), AboutActivity.class);
				startActivity(intent);
			}

	 
		});
		empty_layout = (LinearLayout)view.findViewById(R.id.empty_layout);
	
	}
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i("wifi", "MainCameraFragment>>>>>>>>>>>>>onActivityCreated"); 

		this.adapter = new DeviceListAdapter(
				this.getActivity());
		this.listView.setAdapter(this.adapter);
		this.listView.setOnItemClickListener(this.listViewOnItemClickListener);
		this.listView
				.setOnItemLongClickListener(this.listViewOnItemLongClickListener);
		this.verifyCameraLimit();

		Bundle var7 = this.getActivity().getIntent().getExtras();
		if (var7 != null) {
			String var9 = var7.getString("dev_uid");

			for (int var10 = 0; var10 < DeviceList.size(); ++var10) {
				if (((DeviceInfo) DeviceList.get(var10)).UID.equals(var9)) {
					DeviceInfo var11 = (DeviceInfo) DeviceList.get(var10);
					++var11.n_gcm_count;
					this.adapter.notifyDataSetChanged();
				}
			}
		}
		//this.adapter.notifyDataSetChanged();
		this.filter = new IntentFilter();
		this.filter.addAction(MainCameraFragment.class.getName());
		// this.resultStateReceiver = new
		// MainCameraFragment.ResultStateReceiver(
		// (MainCameraFragment.ResultStateReceiver) null);
		// this.getActivity().registerReceiver(this.resultStateReceiver,
		// this.filter);
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(bReceiver);
		DatabaseManager.n_mainActivity_Status = 1;
		WiFiDirectConfig.registerUBICListener(this);
	}

	private boolean isOver = false;
	private long lasttime = 0;
	private long nowtime = 0;
	//MenuItem maddMenu; 
	public void onCreateOptionsMenu(Menu menu,
			 MenuInflater inflater) {
		menu.clear();
		SubMenu localSubMenu1 = menu.addSubMenu("");
		// localSubMenu.setIcon(R.drawable.add_normal);
		MenuItem localMenuItem1 = localSubMenu1.getItem();
		localMenuItem1.setTitle(R.string.refresh);
		localMenuItem1
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						Log.d("test", "zhaogenghuai:onMenuItemClick");
						Toast.makeText(getActivity(), R.string.mainfragment_refreshableview_refreshing,
								Toast.LENGTH_SHORT).show();
						nowtime = System.currentTimeMillis();
						if (nowtime - lasttime > 1500) {
							lasttime = nowtime;
							if (!isOver) {
								isOver = true;
								new Thread() {
									public void run() {
										Log.d("test",
												"zhaogenghuai:onMenuItemClick go into CameraList.get(i).disconnect()");
										for (int i = 0; i < mCameraManagerment.CameraList.size(); i++) {
											mCameraManagerment.CameraList.get(i).disconnect();

											mCameraManagerment.CameraList.get(i).connect(
													DeviceList.get(i).UID);

											mCameraManagerment.CameraList
													.get(i)
													.start(0,
															DeviceList.get(i).viewAccount,
															DeviceList.get(i).viewPassword);

											mCameraManagerment.userIPCDeviceInfo(mCameraManagerment.CameraList.get(i).getmUID());
											mCameraManagerment.userIPCGetSupportStream(mCameraManagerment.CameraList.get(i).getmUID());
											mCameraManagerment.userIPCGetAudioOutFormat(mCameraManagerment.CameraList.get(i).getmUID());
											mCameraManagerment.userIPCGetTimeZone(mCameraManagerment.CameraList.get(i).getmUID());
											try {
												Thread.sleep(200);
											} catch (InterruptedException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}

										isOver = false;
									};
								}.start();
							}
						}
						return false;
					}

				});
		localMenuItem1.setIcon(R.drawable.refresh);

		localMenuItem1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
 
		
		SubMenu localSubMenu3 = menu.addSubMenu("");
	
		 
		MenuItem localMenuItem3 = localSubMenu3.getItem();
		localMenuItem3.setIcon(R.drawable.info_blue);
		localMenuItem3.setTitle(R.string.about);

		localMenuItem3.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		localMenuItem3
		.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(MainCameraFragment.this.getActivity(), AboutActivity.class);
				startActivity(intent);
				return true;
				
			}
		}
		);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
		//super.toggleSlidingMenu();
			return true;
		case OPT_MENU_ITEM_ADD_CAM:
			if (UbiaApplication.CameraList.size() < 14) {
				Intent var5 = new Intent();
				var5.setClass(this.getActivity(), AddDeviceActivity.class);
				this.startActivityForResult(var5, 0);
			}
			break;
		case OPT_MENU_ITEM_TWO_SEARCH:
			Intent var5 = new Intent();
			var5.setClass(this.getActivity(), AddDeviceActivity.class);
			this.startActivityForResult(var5, 0);
			break;
		case OPT_MENU_ITEM_FIRST_SEARCH:

		 
			break;
		case OPT_MENU_ITEM_FAST_SEARCH:

			Intent intent = new Intent();
			intent.setClass(this.getActivity(), WIfiAddDeviceActivity.class);
			intent.putExtra("selectUID", "");
			// startActivity(intent);
			startActivityForResult(intent, 556);

			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onDestroy() {
		Log.i("IOTCamera", "MainCameraFragment>>>>>>>>>>>>>onDestroy");
		isOver = false;
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
		// this.getActivity().unregisterReceiver(this.resultStateReceiver);
		DatabaseManager.n_mainActivity_Status = 0;
		 this.quit();
	}

	public boolean onKeyDown(int var1, KeyEvent var2) {
		DialogUtil.getInstance().onKeyDown(var1, var2);
		if (var1 == 4) {
			Builder var3 = new Builder(this.getActivity());
			var3.setMessage(this.getText(R.string.page18_dialog_Exit));
			var3.setPositiveButton(this.getText(R.string.page18_btnExit),
					new OnClickListener() {
						public void onClick(DialogInterface var1, int var2) {
							var1.dismiss();
							quit();
						}
					});
			var3.setNeutralButton(this.getText(R.string.page26_MainActivity_btnRunInBackground),
					new OnClickListener() {
						public void onClick(DialogInterface var1, int var2) {
							var1.dismiss();
							MainCameraFragment.this.getActivity()
									.runOnUiThread(new Runnable() {
										public void run() {
											MainCameraFragment.this
													.getActivity()
													.moveTaskToBack(true);
										}
									});
							if (mCameraManagerment.CameraList.size() > 0) {
								MainCameraFragment.this.getActivity()
										.moveTaskToBack(true);
							}

						}
					});
			var3.setNegativeButton(this.getText(R.string.btnCancel),
					new OnClickListener() {
						public void onClick(DialogInterface var1, int var2) {
							var1.dismiss();

						}
					});
			var3.create().show();
			return false;
		} else {
			return super.getActivity().onKeyDown(var1, var2);
		}
	}
	private boolean isFirstinit = false;
	public void onResume() {
		super.onResume();
		Log.i("first", "===================================onResume");
		UbiaApplication.ISCLIENTB = false;
		Boolean hasisgotoNotifyManager =  PreferenceUtil.getInstance().getBoolean(Constants.NOTIFYMANAGER, false);
		if(!hasisgotoNotifyManager && !isgotoNotifyManagerFirst){
    	DialogUtil.getInstance().showDelDialog(getActivity(),
				"" + getString(R.string.notifytiptitle),
				"" + getString(R.string.commitopennotify),
				new Dialogcallback() {

					@Override
					public void commit() {
							isgotoNotifyManager = true;
							isgotoNotifyManagerFirst = true;
							PreferenceUtil.getInstance().putBoolean(Constants.NOTIFYMANAGER, true);
							MobileInfoUtils.jumpPowerInterface(getActivity());
					}

					@Override
					public void commit(String str) { 
						isgotoNotifyManagerFirst = true;
							PreferenceUtil.getInstance().putBoolean(Constants.NOTIFYMANAGER, true);
					}

					@Override
					public void cancel() {
						isgotoNotifyManagerFirst = true;
							PreferenceUtil.getInstance().putBoolean(Constants.NOTIFYMANAGER, true);
				 
					}
				});
		}else{
			
			Boolean hasSetjumpauto =  PreferenceUtil.getInstance().getBoolean(Constants.AUTOMANAGER, false);
			if (!hasSetjumpauto && !isgotoAutoManagerFirst) {
				DialogUtil.getInstance().showDelDialog(getActivity(),
						"" + getString(R.string.automanager),
						"" + getString(R.string.automanagercontent),
						new Dialogcallback() {

							@Override
							public void commit() {
								isgotoAutoManager = true;
								isgotoAutoManagerFirst = true;
								PreferenceUtil.getInstance().putBoolean(Constants.AUTOMANAGER, true);
								MobileInfoUtils.jumpStartInterface(getActivity());
							}

							@Override
							public void commit(String str) {
								PreferenceUtil.getInstance().putBoolean(Constants.AUTOMANAGER, true);
								isgotoAutoManagerFirst = true;
							}

							@Override
							public void cancel() {
								PreferenceUtil.getInstance().putBoolean(Constants.AUTOMANAGER, true);
								isgotoAutoManagerFirst = true;
							}
						});
			}
	         
		}
		
	/*	if(isgotoNotifyManager){
			DialogUtil.getInstance().showDelDialog(getActivity(), ""+getString(R.string.notifytiptitle ), ""+getString(R.string.commitnotifymanagercontent), new Dialogcallback() {
					
				@Override
				public void commit() {
					isgotoNotifyManager = false;
					PreferenceUtil.getInstance().putBoolean(Constants.NOTIFYMANAGER, true);
					 

					
				@Override
				public void commit(String str) {
					isgotoNotifyManager = false;
					PreferenceUtil.getInstance().putBoolean(Constants.NOTIFYMANAGER, true);
					
				@Override
				public void cancel() {
					isgotoNotifyManager = false;
					PreferenceUtil.getInstance().putBoolean(Constants.NOTIFYMANAGER, false);
				}
			});

		}*/
	
		
	/*	if(isgotoAutoManager){
			DialogUtil.getInstance().showDelDialog(getActivity(), ""+getString(R.string.automanagertiptitle ), ""+getString(R.string.commitautomanagercontent), new Dialogcallback() {
					
				@Override
				public void commit() {
					isgotoAutoManager = false;
					PreferenceUtil.getInstance().putBoolean(Constants.AUTOMANAGER, true);
					 

					
				@Override
				public void commit(String str) {
					isgotoAutoManager = false;
					PreferenceUtil.getInstance().putBoolean(Constants.AUTOMANAGER, true);
					
				@Override
				public void cancel() {
					isgotoAutoManager = false;
					PreferenceUtil.getInstance().putBoolean(Constants.AUTOMANAGER, false);
				}
			});

		}*/
		DeviceStateCallbackInterface_Manager.getInstance().setmCallback(new DeviceStateCallbackInterface() {
			
			@Override
			public void DeviceStateCallbackInterface(String did, int type, int param) {
				// TODO Auto-generated method stub
				
				Log.d("test", "设备状况返回：did==" + did + "  msgType=" + type +"   msgParam ="+param);
				final DeviceInfo var7 = getexistDevice(did);
				if (var7 == null)
					return;
				var7.device_connect_state = param;
				switch (param) {
				case CONNSTATUS_NULL:
					break;
				case CONNSTATUS_CONNECTING:
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
				case CONNSTATUS_STARTDEVICECLIENT:{
					  {
							Resources res = getResources();
							String text = res.getString(R.string.fragment_liveviewactivity_publiccameraactivity_setupadddeviceactivity_state_connected);
							var7.Status = text; 
							var7.online = true;
							var7.offline = false;
							var7.lineing = false; 
							var7.connect_count = 0; 
						}
						break;
				}
				case CONNSTATUS_CONNECTED:
					  {
						Resources res = getResources();
						String text = res.getString(R.string.fragment_liveviewactivity_publiccameraactivity_setupadddeviceactivity_state_connected);
						var7.Status = text; 
						var7.online = true;
						var7.offline = false;
						var7.lineing = false; 
						var7.connect_count = 0;
						 mCameraManagerment.StartClient(  did, var7.viewAccount, var7.viewPassword ) ;	
					}
					break;
				case CONNSTATUS_DISCONNECTED:
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
				case CONNSTATUS_UNKOWN_DEVICE:
					if (var7 != null) { 
						Resources res = getResources();
						String text = res
								.getString(R.string.page17_connstus_unknown_device);
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
								.getString(R.string.page26_connstus_wrong_password);
						var7.Status = text;
					
						var7.offline = true;
						var7.lineing = false;
						var7.online = false;  
					

					}
					break;
				case CONNSTATUS_RECONNECTION:
					if (var7 != null) {
						Resources res = getResources();
						String text = res
								.getString(R.string.fragment_liveviewactivity_mainactivity_state_disconnected);
						var7.Status = text; 
						var7.online = false;
						var7.offline = true;
						var7.lineing = false; 
						
						mCameraManagerment.StopPPPP(did);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					  mCameraManagerment.StartPPPP(  did, var7.viewAccount, var7.viewPassword ) ;
						
						
					}
					break;
				case CONNSTATUS_CONNECTION_FAILED:
					if (var7 != null) {
						Resources res = getResources();
						String text = res
								.getString(R.string.page26_page8_MyCameraFragment_connstus_connection_failed);
						var7.Status = text; 
						Log.i("main", " text =" + text);
						var7.offline = true;
						var7.lineing = false; 
						var7.online = false; 
						 mCameraManagerment.StopPPPP(did);
					}
					break;
				}
				 getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						adapter.notifyDataSetChanged();
						if(var7.device_connect_state==CONNSTATUS_WRONG_PASSWORD){
							Resources res = getResources();
							String text = res
									.getString(R.string.page26_connstus_wrong_password);
//							getHelper().showMessage(text); 
						}
					}
				});
			}

			@Override
			public void DeviceStateCallbackLiveInterface(String did, int type,
					int param) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					initCameraList();

				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}, 100);
		if(adapter!=null)
		adapter.notifyDataSetChanged();
	}

	public void onStart() {
		Log.i("first", "===================================onStart");
		DatabaseManager.n_mainActivity_Status = 1;
		super.onStart();
	}

	public void onStop() {
		Log.i("first", "===================================onStop");
		DatabaseManager.n_mainActivity_Status = 0;
		super.onStop();
	}

	public void receiveChannelInfo(Camera var1, int var2, int var3) {
		Log.i("wifi", "receiveChannelInfo............................" + var3);
		Bundle var4 = new Bundle();
		//var4.putString("requestDevice", ((MyCamera) var1).getUUID());
		var4.putInt("sessionChannel", var2);
		Message var5 = this.handler.obtainMessage();
		var5.what = var3;
		var5.setData(var4);
		this.handler.sendMessage(var5);
	}

	public void receiveFrameData(Camera var1, int var2, Bitmap var3) {
	}

	public void receiveFrameInfo(Camera var1, int var2, long var3, int var5,
								 int var6, AVFrame avFrame , int var8) {
	}

	public void receiveIOCtrlData(Camera var1, int var2, int var3, byte[] var4) {
		Log.i("wifi", "receiveIOCtrlData............................" + var3);
		Bundle var5 = new Bundle();
		//var5.putString("requestDevice", ((MyCamera) var1).getUUID());
		var5.putInt("sessionChannel", var2);
		var5.putByteArray("data", var4);
		Message var6 = this.handler.obtainMessage();
		var6.what = var3;
		var6.setData(var5);
		this.handler.sendMessage(var6);
	}

	public void receiveSessionInfo(Camera var1, int var2) {
		Log.i("wifi", "receiveSessionInfo............................" + var2);
		Bundle var3 = new Bundle();
		//var3.putString("requestDevice", ((MyCamera) var1).getUUID());
		Message var4 = this.handler.obtainMessage();
		var4.what = var2;
		var4.setData(var3);
		this.handler.sendMessage(var4);
	}

	private static boolean isSDCardValid() {
		return Environment.getExternalStorageState().equals("mounted");
	}

	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private class DeviceListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		// private MyProgressBar mProgressBar1;

		public DeviceListAdapter(Context var2) {
			this.mInflater = LayoutInflater.from(var2);
		}

		public int getCount() {
			return MainCameraFragment.DeviceList.size();
		}

		public Object getItem(int var1) {
			return MainCameraFragment.DeviceList.get(var1);
		}

		public long getItemId(int var1) {
			return (long) var1;
		}

		public View getView(int var1, View var2, ViewGroup var3) {
			final DeviceInfo var4 = (DeviceInfo) MainCameraFragment.DeviceList
					.get(var1);

			Log.v("mAdapter", "mAdapter.getDeviceInfo" + var1);
			// final MyCamera var5 = (MyCamera) mCameraManagerment.CameraList
			// .get(var1);

			if (var4 != null /* && var5 != null */) {

				ViewHolder var6;
				// if (var2 == null)
				{
					var6 = new ViewHolder();

//					if (PreferenceUtil.getInstance().getBoolean(
//					Constants.IS_THUMBNAIL_CHECKED)) 
					if(build_for_factory_tool)
					{
						var2 = this.mInflater.inflate(
								R.layout.camera_thumbnail_list_item,
								(ViewGroup) null);
					
						var6.DevUID = (TextView) var2
								.findViewById(R.id.myplayTextUID);
						var6.DevUID.setText("ID: " + var4.UID);

				

					} else {
						var2 = this.mInflater.inflate(
								R.layout.camera_list_item, (ViewGroup) null);
						
					}
					
					if(build_for_factory_tool){
						var6.mUIDEditingBt =  (ImageButton) var2 .findViewById(R.id.UIDEditingbt);
						var6.mUIDEditingBt.setTag(var1);
						var6.mUIDEditingBt .setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								ImageButton imgbt = (ImageButton) arg0;
								Integer curPosition = (Integer) imgbt
										.getTag();
								MainCameraFragment.this.selectedCamera = (MyCamera) mCameraManagerment.CameraList
										.get(curPosition);
								MainCameraFragment.this.selectedDevice = (DeviceInfo) MainCameraFragment.DeviceList
										.get(curPosition);
								selectdeviceIndex = curPosition;
								Intent intent = new Intent(getActivity(),
										CaptureActivity.class);
								startActivityForResult(intent, CAMERA_SCAN_REQUEST);
								Log.v("UIDEditingbt",
										"UIDEditingbt Clicked");
							}
						});
					}
					var6.mSettingBt = (ImageButton) var2
							.findViewById(R.id.Settingbt);
					var6.mSettingBt.setTag(var1);
					
					var6.mSettingBt .setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							ImageButton imgbt = (ImageButton) arg0;
							Integer curPosition = (Integer) imgbt .getTag();
							MainCameraFragment.this.selectedCamera = (MyCamera) mCameraManagerment.CameraList .get(curPosition);
							MainCameraFragment.this.selectedDevice = (DeviceInfo) MainCameraFragment.DeviceList .get(curPosition);
							if(selectedDevice.device_connect_state == CONNSTATUS_CONNECTED ){
								selectedCamera.start(0,
										selectedDevice.viewAccount,
										selectedDevice.viewPassword);
							}
							Bundle var2 = new Bundle();
							Intent var3 = new Intent();
							var2.putLong("db_id", selectedDevice.DBID);
							//var2.putString("dev_uuid", selectedDevice.UUID);
							var2.putString("dev_uid", selectedDevice.UID);
							var2.putString("view_acc",
									selectedDevice.viewAccount);
							var2.putString("view_pwd",
									selectedDevice.viewPassword);
							var2.putString("dev_nickName",
									selectedDevice.nickName);
							var2.putInt("camera_channel",
									selectedDevice.getChannelIndex());
							var2.putInt("camera_public",
									selectedDevice.isPublic ? 1 : 0);
							var2.putInt("fromMain",1);
							var3.putExtras(var2);
							var3.setClass(getActivity(),
									SettingActivity.class);
							startActivityForResult(var3, 2);
							

						}
					});

					var6.cameraListItemPrimary = (TextView) var2
							.findViewById(R.id.cameraListItemPrimary);
					var6.ivCameraState = (TextView) var2
							.findViewById(R.id.ivCameraState);
					var6.cameraListItemThumbnail = (ImageView) var2
							.findViewById(R.id.cameraListItemThumbnail);
					var6.myplayButton = (ImageView) var2
							.findViewById(R.id.myplayButton);
					var6.onlinestatus = (TextView) var2
							.findViewById(R.id.myplayTextView);
					var6.mProgressBar = (ProgressBar) var2
							.findViewById(R.id.progress_bar);

					var6.tvCameraStateText = (TextView) var2
							.findViewById(R.id.tvCameraStateText);

					var2.setTag(var6);
				}
				// else {
				// var6 = (MainCameraFragment.ViewHolder) var2.getTag();
				// }
		//		var6.alarmButton.setVisibility(View.GONE);
				if (var6 != null) {
					if(var4.snapshot!=null && var4.snapshot.isRecycled()){
						var4.snapshot = getlastsnap(var4); 
					}
						
						Bitmap bm = getlastsnap(var4);
						if(bm != null){
						MyCamera mMyCamera = CameraManagerment.getInstance().getexistCamera(var4.UID);
						if(mMyCamera!= null && VRConfig.isVRdevice(mMyCamera.hardware_pkg))
						{
							//var6.cameraListItemThumbnail.setImageBitmap( createCircleImage(var4.snapshot ));
							var6.cameraListItemThumbnail.setImageBitmap( bm );
						} else{
							var6.cameraListItemThumbnail.setImageBitmap( bm  );
						}
						if(!build_for_factory_tool && bm.getWidth()==bm.getHeight()){
						 
							ViewGroup.LayoutParams lp = var6.cameraListItemThumbnail.getLayoutParams();
							lp.width = (int) (screenWidth-14*density);
							lp.height = (int) (screenWidth-14*density);
							var6.cameraListItemThumbnail.setLayoutParams(lp);
						}else{
							var6.cameraListItemThumbnail.setScaleType(ScaleType.FIT_CENTER);
							
						}
						
					
					} else {
						var6.cameraListItemThumbnail .setImageResource(R.drawable.camera_thumbnail);
						// var6.cameraListItemThumbnail
						// .setImageResource(R.drawable.camera_thumbnail);

					}
					var6.cameraListItemPrimary.setText(var4.nickName);

					//if (MainCameraFragment.nShowMessageCount == 0)
						var6.ivCameraState.setText(var4.Status);
						String uidPrefix="";
						if(var4!=null && var4.UID.length()>18)
						{
							uidPrefix = var4.UID.substring(0,18);
						}
						else{
							  uidPrefix=var4.UID;
						}
						Log.d("deviceinfo", "  var4.device_connect_state= "+ var4.device_connect_state);
							//var6.alarmButton.setVisibility(View.VISIBLE);
							
							//var6.alarmButton.setVisibility(View.GONE);
						//	var6.alarmButton.setVisibility(View.GONE);
//					else {
//						// var6.ivCameraState.setText(var4.Status + " "
//						// + var5.gettempAvIndex());
//					}
					if (!"".equals(var4.tvCameraStateText)) {
						var6.tvCameraStateText.setVisibility(8);
						var6.tvCameraStateText.setTextColor(0xff000099);
						var6.tvCameraStateText.setText(var4.tvCameraStateText);
					} else {
						var6.tvCameraStateText.setVisibility(8);
					}
					if(var4.offline && !var4.ChangePassword  &&var4.device_connect_state !=CONNSTATUS_WRONG_PASSWORD)
					{

						if (var4.connect_count < 2) {
							++var4.connect_count;
							ReconnectOne(var4);
						}
					} 
					if(var4.device_connect_state ==CONNSTATUS_NULL || var4.device_connect_state== CONNSTATUS_CONNECTING ){
						var6.myplayButton.setVisibility(View.GONE);
					}
					if(var4.device_connect_state ==CONNSTATUS_WRONG_PASSWORD){
						var6.tvCameraStateText.setText(var4.Status);
						var6.mProgressBar.setVisibility(View.GONE);
						var6.tvCameraStateText.setVisibility(View.VISIBLE);
					}else{
						var6.tvCameraStateText.setText(var4.Status);
						var6.tvCameraStateText.setVisibility(View.GONE);
					}
					if (var4.device_connect_state== CONNSTATUS_CONNECTED || var4.device_connect_state== CONNSTATUS_STARTDEVICECLIENT) {
						if(build_for_factory_tool && uidPrefix.equals("FFFFFFFFFFFFFFFFFF")){
							var6.mUIDEditingBt.setVisibility(View.VISIBLE);
				}
						var6.mProgressBar.setVisibility(View.GONE);
						var6.myplayButton.setVisibility(View.VISIBLE);
						var6.onlinestatus.setVisibility(View.VISIBLE);
						var6.myplayButton .setImageResource(R.drawable.play_white);
						var6.onlinestatus.setText(R.string.page26_camer_online);
						var6.onlinestatus.setVisibility(View.GONE);
						var6.ivCameraState.setTextColor(0xff000099);
						Log.d("deviceinfo", "  var4.device_connect_state=  设备在线 " );
					} else if (var4.device_connect_state!= CONNSTATUS_CONNECTING 
							&& var4.device_connect_state!= CONNSTATUS_WRONG_PASSWORD
							&& var4.device_connect_state!= CONNSTATUS_NULL
							&& var4.device_connect_state!= CONNSTATUS_CONNECTED 
							&& var4.device_connect_state!= CONNSTATUS_STARTDEVICECLIENT) { 
						if(build_for_factory_tool && uidPrefix.equals("FFFFFFFFFFFFFFFFFF")){
							var6.mUIDEditingBt.setVisibility(View.GONE);
						}
						var6.mProgressBar.setVisibility(View.GONE);
						var6.myplayButton.setVisibility(View.VISIBLE);
						var6.myplayButton .setImageResource(R.drawable.reconnect_bt);// play_grey);
						var6.onlinestatus.setVisibility(View.VISIBLE);
						var6.onlinestatus.setText(R.string. page26_camer_offline);
						var6.onlinestatus.setVisibility(View.GONE);
						var6.ivCameraState.setTextColor(0xff666666);
						Log.d("deviceinfo", "  var4.device_connect_state=  设备离线 " );
					} else if (var4.device_connect_state== CONNSTATUS_CONNECTING ) {
						var6.myplayButton.setVisibility(View.GONE);
						var6.onlinestatus.setVisibility(View.GONE);
						var6.mProgressBar.setVisibility(View.VISIBLE);
						Log.d("deviceinfo", "  var4.device_connect_state=  设备连线中 " );
					}
				}
				return var2;
			} else {
				return null;
			}
		}
	}
	protected void ReconnectOne(final DeviceInfo mDeviceInfo) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				 CameraManagerment.getInstance().StopPPPP(mDeviceInfo.UID); 
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				 CameraManagerment.getInstance().StartPPPP( 
							mDeviceInfo.UID, mDeviceInfo.viewAccount, mDeviceInfo.viewPassword );
			}
		}).start();
	}

	public String getLastDeviceSnapPath(String UID) {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ Constants.DEVICE_LAST_SNAPSHOT_PATH + UID + ".jpg";
	}

	public final class ViewHolder {

		public TextView GCM_Prompt;
		public FrameLayout eventLayout;
		public ImageView img;
		public TextView info;
		public TextView status;
		public TextView title;

		public TextView onlinestatus;
		public TextView DevUID;

		public TextView cameraListItemPrimary;
		public ImageView cameraListItemThumbnail, myplayButton;
		public TextView ivCameraState;
		public TextView tvCameraStateText;
		public ProgressBar mProgressBar;
		public ImageButton mSettingBt;
		public ImageView alarmButton;
		public ImageButton mUIDEditingBt;
	}

	@Override
	public void CallbackNetconfigStatus(int Success, String uid,int pkg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void CallWifiConfigToAddDevice(int success, Bundle date) {
		// TODO Auto-generated method stub
		Log.i("wifi", "CallWifiConfigToAddDevice.......................main");

	}

	@Override
	public void receiveCameraCtl(Camera var1, int var2, int var3, byte[] var4) {
		// TODO Auto-generated method stub

	}

	public final static String ACTION_BUTTON = "com.notifications.intent.action.ButtonClick";
	public final static String INTENT_BUTTONID_TAG = "ButtonId"; 
	public final static int BUTTON_SWITCH_ID = 1;
	public final static int BUTTON_OPEN_ID = 2;

	public void showButtonNotify(String var1){
	
		
		
		PreferenceUtil.getInstance().putBoolean(Constants.IS_SWITCHOFF_CLICK, false); 
		NotificationManager mNotificationManager  = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.getActivity().getApplicationContext());
		RemoteViews mRemoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.view_custom_button);
		mRemoteViews.setImageViewResource(R.id.custom_icon,R.drawable.nty_app);
		mRemoteViews.setTextViewText(R.id.tv_custom_title,this.getText(R.string.app_name));
		mRemoteViews.setTextViewText(R.id.tv_custom_content, var1);
		if(BaseTools.getSystemVersion() <= 9){
			mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.GONE);
		}else{
			mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
		}
		//Intent var3 = new Intent(this.getActivity(), MainActivity.class);
		//var3.setFlags(536870912);
		Intent buttonIntent = new Intent(ACTION_BUTTON);
	 	buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_SWITCH_ID); 
		PendingIntent intent_prev = PendingIntent.getBroadcast(this.getActivity(), 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_switch, intent_prev);
		mBuilder.setContent(mRemoteViews)
				.setContentIntent(  getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
				.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setTicker("")
				.setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				.setOngoing(true)
				.setSmallIcon(R.drawable.nty_app);
		Notification notify = mBuilder.build();
		notify.flags = Notification.FLAG_ONGOING_EVENT; 
		mNotificationManager.notify(1, notify);
	}
	public PendingIntent getDefalutIntent(int flags){
		Intent var3 = new Intent(UbiaApplication.getInstance().getApplicationContext(),MainActivity.class);
		var3.setFlags(536870912);
		PendingIntent pendingIntent= PendingIntent.getActivity(this.getActivity(), 1, var3, flags);
		return pendingIntent;
	}
	public class ButtonBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(ACTION_BUTTON)){ 
				int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
				switch (buttonId) {
				case BUTTON_SWITCH_ID:
					Log.d("main" , "**************BUTTON_SWITCH_ID********************");
					PreferenceUtil.getInstance().putBoolean(Constants.IS_SWITCHOFF_CLICK, true); 
					quit();
					break;
				case BUTTON_OPEN_ID:
					Log.d("main" , "**************BUTTON_OPEN_ID********************");
					break;
			
				default:
					break;
				}
			}
		}
	}
	public ButtonBroadcastReceiver bReceiver;
	public void initButtonReceiver(){
		bReceiver = new ButtonBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_BUTTON);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(bReceiver,
				new IntentFilter(intentFilter));
	}
	
	
    public static DeviceInfo getLoaclDevice(String UID){
    	DatabaseManager var1 = new DatabaseManager(UbiaApplication
				.getInstance().getApplicationContext());
		SQLiteDatabase var2 = var1.getReadableDatabase();
		Cursor var3 = var2.query("device", new String[] { "_id",
				"dev_nickName", "dev_uid", "dev_name", "dev_pwd", "view_acc",
				"view_pwd", "event_notification", "camera_channel", "snapshot",
				"ask_format_sdcard", "camera_public" , "installmode","hardware_pkg"}, (String) null,
				(String[]) null, (String) null, (String) null, "_id LIMIT 50");
		var3.getCount();
		while (var3.moveToNext()) {
			long var4 = var3.getLong(0);
			String var6 = var3.getString(1);
			final String var7 = var3.getString(2);
			final String var8 = var3.getString(5);
			final String var9 = var3.getString(6);
			int installmode = var3.getInt(12);
			int hardware_pkg = var3.getInt(13);
			int var10 = var3.getInt(7);
			int var11 = var3.getInt(8);
			byte[] var12 = var3.getBlob(9);
			int var13 = var3.getInt(10);
			int ispublic = var3.getInt(11);   
			DeviceInfo var16 = new DeviceInfo(var4, var7, var6,
					var7, var8, var9, "", var10, var11, null);
			var16.installmode = installmode;
			var16.hardware_pkg = hardware_pkg;
			if(var16.UID.equalsIgnoreCase(UID))
				return var16;
		}
		return null;
    }
    private Bitmap createCircleImage(Bitmap source )  
    {  
    	int min =  source.getHeight();
        final Paint paint = new Paint();  
        paint.setColor(Color.WHITE); 
        paint.setAntiAlias(true);  
        Bitmap target = Bitmap.createBitmap(source.getWidth(), min, Config.ARGB_8888);   
        Canvas canvas = new Canvas(target);   
        canvas.drawCircle(source.getWidth() / 2, min / 2,source.getWidth() / 2, paint);    
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));   
        canvas.drawBitmap(source, 0, 0, paint); 
        return target;  
    }  
    public static boolean isChinaSetting() {
        String language = getLanguageEnv();
        if (language != null
                && (language.trim().equals("zh-CN") || language.trim().equals(
                "zh-TW")))
            return true;
        else
            return false;
    }
    private static String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh-CN";
            } else if ("tw".equals(country)) {
                language = "zh-TW";
            }
        } else if ("pt".equals(language)) {
            if ("br".equals(country)) {
                language = "pt-BR";
            } else if ("pt".equals(country)) {
                language = "pt-PT";
            }
        }
        return language;
    }
}
