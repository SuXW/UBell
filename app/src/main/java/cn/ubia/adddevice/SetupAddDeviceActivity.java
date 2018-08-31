package cn.ubia.adddevice;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ubia.UbiaApplication;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.base.Constants;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.MyCamera;
import cn.ubia.db.DatabaseManager;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.interfaceManager.IOTCCMDStateCallbackInterface_Manager;
import cn.ubia.interfaceManager.IOTCResultCallBackInterface;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.util.PreferenceUtil;
import cn.ubia.util.StringUtils;
import cn.ubia.util.UbiaUtil;
import cn.ubia.util.WifiAdmin;
import cn.ubia.widget.DialogUtil;
import cn.ubia.widget.DialogUtil.DialogChooseItemStringcallback;
import cn.ubia.widget.EditTextDrawable;
import cn.ubia.widget.MyProgressBar;
import cn.ubia.widget.EditTextDrawable.DrawableListener;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ubia.IOTC.AVIOCTRLDEFs;
import com.ubia.IOTC.HARDWAEW_PKG;
import com.ubia.http.HttpClient;
import com.ubia.vr.VRConfig;

@SuppressLint("ShowToast")
public class SetupAddDeviceActivity extends BaseActivity implements OnLayoutChangeListener {

	private static final String ZXING_DOWNLOAD_URL = "http://push.iotcplatform.com/release/BarcodeScanner.apk";
	private final int REQUEST_CODE_GETUID_BY_SCAN_BARCODE = 0;
	private Button btnCancel;
	// private UbiaNetConfig sendIP;
	MyProgressBar myProgressBar;
	private Button btnScan;
	private EditText edtName;
	private EditText edtSecurityCode;
	private EditText edtUID;
	private IntentFilter filter;
	private ListView devListView = null;
	private List list = new ArrayList();
	private Button btnSearch;
	private TextView selectUID;
	private EditText edtSSID;
	private EditText edtKEY;
	private Button nextButton;
	private String selectUidStr;
	private WifiAdmin wifiAdmin;
	private String mUserSelectedWifiSsid;
	private EditText devlogin;
	private EditTextDrawable devpwd2;
	private EditTextDrawable devpwd;
	private EditText devname;
	private EditText devpos;
	private Button btnok;
	private ImageView back,down_name,local_name;
	private ImageView face_horizon_iv,face_down_iv,face_up_iv;
	private TextView devLocal;
	int faceDirection = -1;
	private CameraManagerment mCameraManagerment=CameraManagerment.getInstance();
	private int flag = 1;
	int pkg;
	private OnClickListener btnCancelOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			if (myProgressBar != null)
				myProgressBar.dismiss();
			if (flag == 0) {
				flag = 1;
				// txtstate.setText("stop...");
				Log.i("send", "stop>>>>>>>>>>>>>>>>>>>");

				// sendIP.stopApp();
			}
		}
	};

	private OnClickListener  btnOkOnClickListener= new OnClickListener() {
		public void onClick(View var1) {


			if(devpwd.getText().toString().contains(":")){
				Toast.makeText(SetupAddDeviceActivity.this,R.string.password_illegal,Toast.LENGTH_SHORT).show();
				return;
			}

			if (countryCodeIndex == -1) {
				//请选择一个区域
				Toast.makeText(SetupAddDeviceActivity.this,R.string.select_server,Toast.LENGTH_SHORT).show();
				return;
			}

			selectServer(countryCodeIndex);
		}
	};

	int countryCodeIndex = -1;
	private OnClickListener  btnLocalOnClickListener= new OnClickListener() {
		public void onClick(View var1) {
			if(UbiaApplication.myCountryCode==null||UbiaApplication.myCountryCode.equals("--")){
				int choseItem = -1;

				if(UbiaUtil.getLocalVersionName(SetupAddDeviceActivity.this).contains("us")){ //根据版本名称判断是否默认美国
					choseItem = 0; //默认美国
				}


				String[] items = getResources().getStringArray(R.array.controy_array);
				DialogUtil.getInstance().showChoseControyPiviewDialo(SetupAddDeviceActivity.this, items, countryCodeIndex, "" + getString(R.string.location_right_chose), new DialogUtil.DialogChooseItemcallback() {


					@Override
					public void chooseItem(int chooseItem) {

						countryCodeIndex = chooseItem;
						String[] controyname_array = getResources().getStringArray(R.array.controy_array);
						devLocal.setText(controyname_array[chooseItem]);

					}

				},findViewById(R.id.devlocal_lv));
				return;
			}
			if (isexist) {
				getHelper().showMessage(R.string.page6_tips_add_camera_duplicated);
				return;
			}
			if (findViewById(R.id.putmode_ll).getVisibility() == View.VISIBLE)
				if (faceDirection == -1) {
					getHelper().showMessage(R.string.page6_tips_add_camera_facedirection);
					return;
				}

			{
				UbiaApplication.ISWEB = false;

				if(devname.getText().toString().equals("")){
					getHelper().showMessage(R.string.page12_p11_name_new_hit);
					return;
				}
				boolean ret[] =  UbiaUtil.validatepassword(devpwd.getText().toString().trim());

				String toastshow = 	getHelper().getString(R.string.page25_p11_password_toast);
				if (!ret[0]) {
					toastshow += getHelper().getString(R.string.p11_confirmpassword_toast1);
					//getHelper().showMessage(R.string.p11_password_toast);
					//return;
				}
				if (!ret[1]) {
					toastshow += getHelper().getString(R.string.p11_confirmpassword_toast2);
					//getHelper().showMessage(R.string.p11_password_toast);
					//return;
				}
				if (!ret[2]) {
					toastshow += getHelper().getString(R.string.p11_confirmpassword_toast3);
					//getHelper().showMessage(R.string.p11_password_toast);
					//return;
				}
				if (!ret[3]) {
					toastshow += getHelper().getString(R.string.p11_confirmpassword_toast4);
					//getHelper().showMessage(R.string.p11_password_toast);
				}
				if (!toastshow.equals(getHelper().getString(R.string.page25_p11_password_toast))) {
					getHelper().showMessageLong(toastshow);
					return;
				}
				if (!devpwd.getText().toString().equals(devpwd2.getText().toString())) {
					getHelper().showMessage(R.string.page12_p11_confirmpassword_toast);
					return;
				}
				selectUidStr = selectUidStr.toUpperCase();
				addDeviceToServer(selectUidStr, devname.getText().toString(),
						devpos.getText().toString(), devpwd.getText()
								.toString());
				HttpClient.operateDeviceVoip(selectUidStr, 1, mJsonHttpResponseHandler);
			}
		}
	};
	private void selectServer(int chooseItem){

		final String[] controycode_array = getResources().getStringArray(R.array.controycode_array);

		Log.i("IOTCamera", "设置  controycode_array   myCountryCode= " + (controycode_array[chooseItem]));
		UbiaApplication.myCountryCode = controycode_array[chooseItem];
		int number = StringUtils.getCurrentLocaltionISOCountryCodeNumber(UbiaApplication.myCountryCode);
		UbiaApplication.myCountryCode = "--"; //选择后为重新赋值.下次重新选择
		Log.i("IOTCamera", "设置  controycode_array   myCountryCode getCurrentLocaltionISOCountryCodeNumber= " + number);
		PreferenceUtil.getInstance().putInt(Constants.COUNTRYCODE + selectUidStr.toUpperCase(), number);
		if (isexist) {
			getHelper().showMessage(R.string.page6_tips_add_camera_duplicated);
			return;
		}
		if (findViewById(R.id.putmode_ll).getVisibility() == View.VISIBLE)
			if (faceDirection == -1) {
				getHelper().showMessage(R.string.page6_tips_add_camera_facedirection);
				return;
			}


		{
			UbiaApplication.ISWEB = false;

			if (devname.getText().toString().equals("")) {
				getHelper().showMessage(R.string.page12_p11_name_new_hit);
				return;
			}
			boolean ret[] = UbiaUtil.validatepassword(devpwd.getText().toString().trim());

			String toastshow = getHelper().getString(R.string.page25_p11_password_toast);
			if (!ret[0]) {
				toastshow += getHelper().getString(R.string.p11_confirmpassword_toast1);
				//getHelper().showMessage(R.string.p11_password_toast);
				//return;
			}
			if (!ret[1]) {
				toastshow += getHelper().getString(R.string.p11_confirmpassword_toast2);
				//getHelper().showMessage(R.string.p11_password_toast);
				//return;
			}
			if (!ret[2]) {
				toastshow += getHelper().getString(R.string.p11_confirmpassword_toast3);
				//getHelper().showMessage(R.string.p11_password_toast);
				//return;
			}
			if (!ret[3]) {
				toastshow += getHelper().getString(R.string.p11_confirmpassword_toast4);
				//getHelper().showMessage(R.string.p11_password_toast);
				//return;
			}
			if (!toastshow.equals(getHelper().getString(R.string.page25_p11_password_toast))) {
				getHelper().showMessageLong(toastshow);
				return;
			}

			if (!devpwd.getText().toString().equals(devpwd2.getText().toString())) {
				getHelper().showMessage(R.string.page12_p11_confirmpassword_toast);
				return;
			}


			selectUidStr = selectUidStr.toUpperCase();
			addDeviceToServer(selectUidStr, devname.getText().toString(),
					devpos.getText().toString(), devpwd.getText()
							.toString());
			HttpClient.operateDeviceVoip(selectUidStr, 1,mJsonHttpResponseHandler);
		}
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

	private Object waitPasswordChanged  = new Object();
	private boolean connected = false;
	private boolean haschangePassword = false;
	private void addDeviceToServer(final String devicUid,
								   final String deviceName, final String deviceLocation,
								   final String viewPwd) {
		myProgressBar.dismiss();
		Log.d("mycamera", "addDeviceToServer: is LAN");
		final int MAXCOUNT = 4;
		showDialog();
		final long var8 = (new DatabaseManager(SetupAddDeviceActivity.this))
				.addDevice(deviceName, devicUid, "", "", "admin", "admin",
						3, DatabaseManager.DEFAULT_STREAM_INDEX,faceDirection,pkg);

		new Thread(new Runnable() {
			public void run() {
				MyCamera mMyCamera =mCameraManagerment.getexistCamera(selectUidStr);
				int trycount = 0;
				/************************* Conectting*********************************/
				while(!mMyCamera.isConected && trycount<MAXCOUNT) {//尝试连接3次
					mMyCamera.connect(selectUidStr);
					mMyCamera.start(0, "admin", "admin");
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(mMyCamera.isConected) {
						break;
					}else{
						trycount++;
					}
					mMyCamera.stop(0);

					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (trycount >= MAXCOUNT) {// device offline ,password change failed
					haschangePassword = false;
				} else {
					trycount = 0;
					while (mMyCamera.isConected && trycount < MAXCOUNT || haschangePassword) {// 尝试修改3次
						mCameraManagerment.userIPCSetPassWord(devicUid,
								"admin", viewPwd, deviceName);
						Log.e("mycamera", "userIPCSetPassWord:     pwd:"+ viewPwd);
						synchronized (waitPasswordChanged) {
							try {
								waitPasswordChanged.wait(2500);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if(haschangePassword){
							break;//wait for  succeed
						}
						trycount++;
					}
				}
				if(trycount>=4 || !mMyCamera.isConected ){//device offline ,password change failed
					haschangePassword = false;
				}

				String pwd = viewPwd;
				if(haschangePassword){
					new DatabaseManager(UbiaApplication.getInstance().getApplicationContext()).updateDeviceViewPwdByUID( devicUid,pwd );
				}else{
					pwd = "admin";//password change failed
				}
				/**************************** addDeviceToServer ******************************/
				DeviceInfo mDeviceInfo = new DeviceInfo(0L, selectUidStr, deviceName, selectUidStr, "admin", pwd, "", 0, DatabaseManager.DEFAULT_STREAM_INDEX, null);
				Log.e("mycamera", "addDeviceToServer: UID:"+mDeviceInfo.UID+"    pwd:"+ pwd);
				Resources res = getResources();
				String text = res.getString(R.string.fragment_liveviewactivity_publiccameraactivity_setupadddeviceactivity_state_connected);
				mDeviceInfo.Status = text;
				mDeviceInfo.online = true;
				mDeviceInfo.offline = false;
				mDeviceInfo.lineing = false;
				mDeviceInfo.connect_count = 0;
				mDeviceInfo.isPublic = false;
				mDeviceInfo.installmode = faceDirection;
				mDeviceInfo.hardware_pkg = pkg;
				mDeviceInfo.device_connect_state = MainCameraFragment.CONNSTATUS_STARTDEVICECLIENT;
				MainCameraFragment.DeviceList.add(mDeviceInfo);

				final Intent var11 = new Intent();
				Bundle var10 = new Bundle();
				var10.putLong("db_id", var8);
				var10.putString("dev_nickname", deviceName);
				var10.putString("dev_uid", devicUid);
				var10.putString("dev_name", "");
				var10.putString("dev_pwd", "");
				var10.putString("view_acc", "admin");
				var10.putString("view_pwd", pwd);
				var10.putInt("video_quality", 0);
				var10.putInt("pkg", pkg);
				var10.putInt("camera_channel",DatabaseManager.DEFAULT_STREAM_INDEX); //0
				var11.putExtras(var10);

				runOnUiThread(new   Runnable() {
					public void run() {
						SetupAddDeviceActivity.this.setResult(9999, var11);
						SetupAddDeviceActivity.this.finish();
						if(progressDialog.isShowing())
							progressDialog.dismiss();
						Toast.makeText(
								SetupAddDeviceActivity.this,
								SetupAddDeviceActivity.this.getText(
										R.string.page6_tips_add_camera_ok).toString(), Toast.LENGTH_SHORT).show();
					}
				});


			}
		}).start();


	}

	private boolean isSDCardValid() {
		return Environment.getExternalStorageState().equals("mounted");
	}

	private void startDownload(String var1) throws Exception {
	}

	private void startInstall(File var1) {
	}

	public void onActivityResult(int var1, int var2, Intent var3) {
	}
	boolean isexist = false;
	private ScrollView mScrollView;
	View activityRootView;
	int screenHeight,keyHeight;
	protected void onCreate(Bundle var1) {
		super.onCreate(var1);
		Resources res = getResources();

		String text = res.getString(R.string.page12_p11_title);
		this.setTitle(text);
		this.setContentView(R.layout.setup_add_newdevice);
		mScrollView = (ScrollView) findViewById(R.id.scroll);
		activityRootView = findViewById(R.id.activityRoot);
		//获取屏幕高度
		screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
		//阀值设置为屏幕高度的1/3
		keyHeight = screenHeight/3;
		//添加layout大小发生改变监听器
		activityRootView.addOnLayoutChangeListener(this);

		showpsdOn = getResources().getDrawable(
				R.drawable.add_icon_seen_press);
		showpsdOff = getResources().getDrawable(
				R.drawable.add_icon_seen);
		showpsdOn.setBounds(0, 0, showpsdOn.getIntrinsicWidth(),
				showpsdOn.getIntrinsicHeight());
		showpsdOff.setBounds(0, 0, showpsdOff.getIntrinsicWidth(),
				showpsdOff.getIntrinsicHeight());
		getActionBar().hide();
		Bundle var2 = this.getIntent().getExtras();
		this.selectUidStr = var2.getString("selectUID");
		this.pkg = var2.getInt("pkg");
		this.selectUidStr = selectUidStr.toUpperCase();
		this.devlogin = (EditText) this.findViewById(R.id.devlogin);
		this.devpwd = (EditTextDrawable) this.findViewById(R.id.devpwd);
		this.devLocal = (TextView) this.findViewById(R.id.devlocal);
		this.devpwd2 = (EditTextDrawable) this.findViewById(R.id.devpwd2);
		this.devpwd.setDrawableListener(drawableListener);
		this.devpwd2.setDrawableListener(drawableListener2);
		this.devname = (EditText) this.findViewById(R.id.devname);
		if(MainCameraFragment.build_for_factory_tool){
			this.devpwd.setText("admin");
			this.devpwd2.setText("admin");
		}
		this.devpos = (EditText) this.findViewById(R.id.devpos);
		this.btnok = (Button) this.findViewById(R.id.btnok);
		back = (ImageView) this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		down_name= (ImageView) this.findViewById(R.id.down_name);
		down_name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(devname,InputMethodManager.SHOW_FORCED);
				imm.hideSoftInputFromWindow(devname.getWindowToken(), 0); //强制隐藏键盘
				DialogUtil.getInstance().showChoseNameDialo(SetupAddDeviceActivity.this, new DialogChooseItemStringcallback() {

					@Override
					public void chooseItemString(String chooseItem) {
						devname.setText(""+chooseItem);

					}
				});
			}
		});
		local_name = (ImageView) this.findViewById(R.id.down_local);
		local_name.setOnClickListener(this.btnLocalOnClickListener);
		((TextView) this.findViewById(R.id.title)).setText(""+text);
		btnok.setOnClickListener(this.btnOkOnClickListener);
		this.findViewById(R.id.left_ll).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						SetupAddDeviceActivity.this.finish();
					}
				});

		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		devpos.requestFocus();
		this.getWindow().setSoftInputMode(3);
		myProgressBar = new MyProgressBar(this);

		IOTCCMDStateCallbackInterface_Manager.getInstance().setmCallback(new IOTCResultCallBackInterface() {
			@Override
			public void IOTCCMDStatecallback(int IOTCCMD, int result) {
				if(IOTCCMD==  AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPASSWORD_RESP){
					if(result>=0){
						haschangePassword = true;
						synchronized (waitPasswordChanged) {
							try {
								waitPasswordChanged.notify();
							}catch( Exception e) {
								e.printStackTrace();
							}
						}
					}else{
						haschangePassword = false;
					}
				}

			}
		});

		Iterator var5 = mCameraManagerment.CameraList.iterator();
		Log.i("mycamera", "camera:" +mCameraManagerment.CameraList.size());

		while (true) {
			boolean var6 = var5.hasNext();
			isexist = false;
			if (!var6) {
				break;
			}

			if (selectUidStr.equalsIgnoreCase(((MyCamera) var5.next())
					.getUID())) {
				isexist = true;
				break;
			}
		}

		if (isexist) {
			getHelper().showMessage(R.string.page6_tips_add_camera_duplicated);
			return;
		}

		MyCamera var21 = new MyCamera( "" , selectUidStr.toUpperCase().trim(), "admin", "admin");
		mCameraManagerment.AddCameraItem(var21);
		//var21.connect(selectUidStr);
		//var21.start(0, "admin", "admin");

		if (UbiaApplication.DEFAULT_NO_PUTMODE){
			//默认不显示安装方式
			LinearLayout putmode_ll  = (LinearLayout) this.findViewById(R.id.putmode_ll);
			putmode_ll.setVisibility(View.GONE);
			this.findViewById(R.id.putmode_title_tv).setVisibility(View.GONE);
			faceDirection = VRConfig.CameraPutModelFaceFront;
		}


		if(pkg==HARDWAEW_PKG.CM_BELL_VR_5230_2466 || pkg ==  HARDWAEW_PKG.CM_BELL_VR_9732_5112){
			//默认不显示安装方式 ,门铃设备固定不显安装方式
			LinearLayout putmode_ll  = (LinearLayout) this.findViewById(R.id.putmode_ll);
			putmode_ll.setVisibility(View.GONE);
			this.findViewById(R.id.putmode_title_tv).setVisibility(View.GONE);
			faceDirection = VRConfig.CameraPutModelFaceFront;
		}

		if (UbiaApplication.BUILD_CHECK_PWD){

			this.findViewById(R.id.pwd_tip).setVisibility(View.VISIBLE);
		}else{
			this.findViewById(R.id.pwd_tip).setVisibility(View.GONE);
		}

		face_horizon_iv = (ImageView) this.findViewById(R.id.face_horizon_iv);
		face_down_iv = (ImageView) this.findViewById(R.id.face_down_iv);
		face_up_iv = (ImageView) this.findViewById(R.id.face_up_iv);

		this.findViewById(R.id.face_down_ll).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						faceDirection = VRConfig.CameraPutModelFaceDown;
						switchFaceDirection();
					}
				});
		this.findViewById(R.id.face_up_ll).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						faceDirection = VRConfig.CameraPutModelFaceUp;
						switchFaceDirection();
					}
				});

		this.findViewById(R.id.face_horizon_ll).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						faceDirection = VRConfig.CameraPutModelFaceFront;
						switchFaceDirection();
					}
				});
		switchFaceDirection() ;

	}
	protected void switchFaceDirection() {
		//0:horizon
		// 1: down
		if(VRConfig.CameraPutModelFaceDown==faceDirection){

			face_down_iv.setImageResource(R.drawable.facedown_on);
			face_horizon_iv.setImageResource(R.drawable.facefront);
			face_up_iv.setImageResource(R.drawable.faceup );
		}else if(VRConfig.CameraPutModelFaceFront==faceDirection){

			face_down_iv.setImageResource(R.drawable.facedown );
			face_up_iv.setImageResource(R.drawable.faceup );
			face_horizon_iv.setImageResource(R.drawable.facefront_on);
		}else if(VRConfig.CameraPutModelFaceUp==faceDirection){

			face_up_iv.setImageResource(R.drawable.faceup_on );
			face_down_iv.setImageResource(R.drawable.facedown );
			face_horizon_iv.setImageResource(R.drawable.facefront);
		}

	}
	protected void onDestroy() {
		super.onDestroy();
		// if (sendIP != null) {
		// sendIP.stopApp();
		// }
		if (myProgressBar != null)
			myProgressBar.dismiss();
		// this.unregisterReceiver(this.resultStateReceiver);
	}

	private class SearchResult {

		public String IP;
		public String UID;

		public SearchResult(String var2, String var3, int var4) {
			this.UID = var2;
			this.IP = var3;
		}
	}

	public final class ViewHolder {

		public TextView ip;
		public TextView uid;

	}


	private ProgressDialog progressDialog;
	public void showDialog(){
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(this.getText(
				R.string.page26_page8_connstus_connecting).toString());
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}
	private class SearchResultListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public SearchResultListAdapter(LayoutInflater var2) {
			this.mInflater = var2;
		}

		public int getCount() {
			return SetupAddDeviceActivity.this.list.size();
		}

		public Object getItem(int var1) {
			return SetupAddDeviceActivity.this.list.get(var1);
		}

		public long getItemId(int var1) {
			return (long) var1;
		}

		public View getView(int var1, View var2, ViewGroup var3) {
			SearchResult var4 = (SearchResult) this
					.getItem(var1);
			ViewHolder var5;
			if (var2 == null) {
				// var2 = this.mInflater.inflate(R.layout.search_device_result,
				// (ViewGroup) null);
				// var5 = new AddDeviceActivity.ViewHolder();
				// var5.uid = (TextView) var2.findViewById(R.id.uid);
				// var5.ip = (TextView) var2.findViewById(R.id.ip);
				// var2.setTag(var5);
			} else {
				var5 = (ViewHolder) var2.getTag();
			}

			// var5.uid.setText(var4.UID);
			// var5.ip.setText(var4.IP);
			return var2;
		}
	}
	private Handler mHandler = new Handler();
	@Override
	public void onLayoutChange(View v, int left, int top, int right,
							   int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
		//现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
		if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > keyHeight)){

			//将ScrollView滚动到底

			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					//将ScrollView滚动到底
					mScrollView.scrollTo(0, screenHeight/3);
				}
			}, 100);
			Log.d("", "监听到软键盘弹起..." );

		}else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > keyHeight)){

			Log.d("", "监听到软件盘关闭..." );

		}
	}


	private Drawable showpsdOn;
	private Drawable showpsdOff;
	private boolean flag_showpsd = false;
	private boolean flag_showpsd2 = false;
	DrawableListener drawableListener = new DrawableListener() {

		@Override
		public void onRight() {
			flag_showpsd = !flag_showpsd;
			toggleShowpsd();
		}
		@Override
		public void onLeft() {
		}
	};
	DrawableListener drawableListener2 = new DrawableListener() {

		@Override
		public void onRight() {
			flag_showpsd2 = !flag_showpsd2;
			toggleShowpsd2();
		}
		@Override
		public void onLeft() {
		}
	};

	private void toggleShowpsd() {
		if (flag_showpsd) {
			devpwd.setCompoundDrawables(devpwd.getCompoundDrawables()[0],
					devpwd.getCompoundDrawables()[1], showpsdOn,
					devpwd.getCompoundDrawables()[3]);
			devpwd.setTransformationMethod(HideReturnsTransformationMethod
					.getInstance());
		} else {
			devpwd.setCompoundDrawables(devpwd.getCompoundDrawables()[0],
					devpwd.getCompoundDrawables()[1], showpsdOff,
					devpwd.getCompoundDrawables()[3]);
			devpwd.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		}
		devpwd.setSelection(devpwd.getText().length());
	}
	private void toggleShowpsd2() {
		if (flag_showpsd2) {
			devpwd2.setCompoundDrawables(devpwd2.getCompoundDrawables()[0],
					devpwd2.getCompoundDrawables()[1], showpsdOn,
					devpwd2.getCompoundDrawables()[3]);
			devpwd2.setTransformationMethod(HideReturnsTransformationMethod
					.getInstance());
		} else {
			devpwd2.setCompoundDrawables(devpwd2.getCompoundDrawables()[0],
					devpwd2.getCompoundDrawables()[1], showpsdOff,
					devpwd2.getCompoundDrawables()[3]);
			devpwd2.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		}
		devpwd2.setSelection(devpwd2.getText().length());
	}
}
