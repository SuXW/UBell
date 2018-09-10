package cn.ubia.adddevice;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.ubia.SettingActivity;
import cn.ubia.UbiaApplication;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.base.Constants;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.MyCamera;
import cn.ubia.db.DatabaseManager;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.manager.NotificationTagManager;
import cn.ubia.util.PreferenceUtil;
import cn.ubia.util.StringUtils;
import cn.ubia.util.WifiAdmin;
import cn.ubia.widget.DialogUtil;
import cn.ubia.widget.MyProgressBar;
import cn.ubia.widget.EditTextDrawable.DrawableListener;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ubia.IOTC.HARDWAEW_PKG;
import com.ubia.http.HttpClient;
import com.ubia.vr.VRConfig;

@SuppressLint("ShowToast")
public class LoginAddDeviceActivity extends BaseActivity {

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
	private String country;
	private WifiAdmin wifiAdmin;
	private String mUserSelectedWifiSsid;
	private EditText devlogin;
	private cn.ubia.widget.EditTextDrawable devpwd;
	private EditText devname;
	private EditText devpos;
	private Button btnok;
	private ImageView back;
	private ImageView face_horizon_iv,face_down_iv,face_up_iv;
	private LinearLayout localLv;
	private TextView locaTv;
	private int flag = 1;
	private int pkg;
	int faceDirection = -1;
	int countryCodeIndex = -1;
	private OnClickListener btnCancelOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			if (myProgressBar != null)
				myProgressBar.dismiss();
			if (flag == 0) {
				flag = 1;
				// txtstate.setText("stop...");
				Log.i("send", "stop>>>>>>>>>>>>>>>>>>>");
				btnOK.setTextColor(Color.BLACK);
				// sendIP.stopApp();
			}
		}
	};

	private Button btnOK;
	private OnClickListener btnOKOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			Iterator var5 = CameraManagerment.getInstance().CameraList.iterator();
			Log.i("mycamera", "camera:" + CameraManagerment.getInstance().CameraList.size());
			boolean isExist;
			while (true) {
				boolean var6 = var5.hasNext();
				isExist = false;
				if (!var6) {
					break;
				}

				if (selectUidStr.equalsIgnoreCase(((MyCamera) var5.next())
						.getUID())) {
					isExist = true;
					break;
				}
			}

			if (isExist) {
//				getHelper().showMessage(R.string.page6_tips_add_camera_duplicated);
		 
				(new Builder(LoginAddDeviceActivity.this))
				.setIcon(17301543)
				.setTitle(
						 getText(
								R.string.page5_tips_warning))
				.setMessage(
						 getText(
										R.string.page6_tips_add_camera_duplicated_replace))
				.setPositiveButton(
						 getText(R.string.ok),
						new DialogInterface. OnClickListener() {
							public void onClick(
									DialogInterface var1,
									int var2) {
								UbiaApplication.ISWEB = false;
								selectUidStr = selectUidStr.toUpperCase();
								if( findViewById(R.id.putmode_ll).getVisibility()==View.VISIBLE)
								if (faceDirection==-1 ) {
									getHelper().showMessage(R.string.page6_tips_add_camera_facedirection);
									return;
								}  
								if (devpwd.getText().toString().trim().equals("")) {
									getHelper().showMessage(R.string.page7_tips_dev_security_code);
									return;
								}  
								deleteDeviceFromDB(selectUidStr);
								addDeviceToServer(selectUidStr, devname.getText().toString(),
										devpos.getText().toString(), devpwd.getText()
												.toString(),faceDirection);
							}
						})
				.setNegativeButton(
					getText(
								R.string.cancel),
						new DialogInterface. OnClickListener() {
							public void onClick(
									DialogInterface var1,
									int var2) {
						
							}
 
						}).show();
			} else {
				UbiaApplication.ISWEB = false;
				selectUidStr = selectUidStr.toUpperCase();
				if( findViewById(R.id.putmode_ll).getVisibility() == View.VISIBLE)
				if (faceDirection==-1) {
					getHelper().showMessage(R.string.page6_tips_add_camera_facedirection);
					return;
				}  
				if (devpwd.getText().toString().trim().equals("")) {
					getHelper().showMessage(R.string.page7_tips_dev_security_code);
					return;
				}

				if(country == null || country.equalsIgnoreCase("unknow")){
					getHelper().showMessage(R.string.select_server);
					return;
				}

				addDeviceToServer(selectUidStr, devname.getText().toString(),
						devpos.getText().toString(), devpwd.getText()
								.toString(),faceDirection);
			}

		}
	};
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
	private void addDeviceToServer(final String devicUid,
			final String deviceName, final String deviceLocation,
			final String viewPwd,final int installmode) {

		String token = getHelper().getConfig(Constants.TOKEN);
		String tokenSecret = getHelper().getConfig(Constants.TOKEN_SECRET);
		String account = getHelper().getConfig(Constants.USER_NAME);
		String password = getHelper().getConfig(Constants.USER_PASSWORD);
		Log.i("guo...ADD", "password:" + password + ",viewPwd:" + viewPwd
				+ ",devicUid:" + devicUid+",PKG:" + pkg);
		if (!UbiaApplication.ISWEB) {
			myProgressBar.dismiss();
			Log.d("mycamera", "addDeviceToServer: is LAN");
			long var8 = (new DatabaseManager(LoginAddDeviceActivity.this))
					.addDevice(deviceName, devicUid, "", "", "admin", viewPwd,
							3, DatabaseManager.DEFAULT_STREAM_INDEX,installmode,pkg);
			Toast.makeText(
					LoginAddDeviceActivity.this,
					LoginAddDeviceActivity.this.getText(
							R.string.page6_tips_add_camera_ok).toString(), 0).show();
			Bundle var10 = new Bundle();
			var10.putLong("db_id", var8);
			var10.putString("dev_nickname", deviceName);
			var10.putString("dev_uid", devicUid);
			var10.putString("dev_name", "");
			var10.putString("dev_pwd", "");
			var10.putString("view_acc", "admin");
			var10.putString("view_pwd", viewPwd);
			var10.putInt("video_quality", 0);
			var10.putInt("camera_channel",DatabaseManager.DEFAULT_STREAM_INDEX); //0
			Intent var11 = new Intent();
			var11.putExtras(var10);
			HttpClient.operateDeviceVoip(devicUid, 1,mJsonHttpResponseHandler);



			int number = StringUtils.getCurrentLocaltionISOCountryCodeNumber(country);
			PreferenceUtil.getInstance().putInt(Constants.COUNTRYCODE + selectUidStr.toUpperCase(), number);

		/*	CameraManagerment.getInstance().userIPCSetPassWordwithCountry(devicUid,
					viewPwd,viewPwd,deviceName,StringUtils.getCurrentLocaltionISOCountryCodeString(number));*/

			LoginAddDeviceActivity.this.setResult(9999, var11);
			LoginAddDeviceActivity.this.finish();


		} else {
			Log.e("mycamera", "addDeviceToServer: is WIFI");
			HttpClient httpClient = new HttpClient(token, tokenSecret);

			httpClient.addDevice(account, viewPwd, devicUid, deviceName,
					deviceLocation, new JsonHttpResponseHandler() {// password


						@Override
						public void onStart() {
							super.onStart();
							myProgressBar.show();
						}

						@Override
						public void onSuccess(int statusCode,
								JSONObject response) {
							myProgressBar.dismiss();
							Log.d("url",
									"addDeviceToServer:" + response.toString());
							String result = response.toString();
							if (result != null) {
								boolean state = response.optBoolean("state");
								if (state) {

									Bundle var10 = new Bundle();

									var10.putString("dev_nickname", deviceName);
									var10.putString("dev_uid", devicUid);
									var10.putString("dev_name", "");
									var10.putString("dev_pwd", "");
									var10.putString("view_acc", "admin");
									var10.putString("view_pwd", viewPwd);
									var10.putInt("video_quality", 0);
									var10.putInt("camera_channel", 1); //0
									Intent var11 = new Intent();
									var11.putExtras(var10);

									var11.setClass(LoginAddDeviceActivity.this,
											MainCameraFragment.class);

									startActivity(var11);
								} else {
									try {
										if (response.getInt("reason") == 207) {
											getHelper()
													.showMessage(
															R.string.page6_camera_added_by_other_user);

										} else {
											getHelper()
													.showMessage(
															R.string.page6_failed_to_add_device);
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
							myProgressBar.dismiss();
							getHelper().showMessage(
									R.string.page6_failed_to_add_device);
							error.printStackTrace();
						}

					});
		}
	}
	private void deleteDeviceFromDB(String checkUID){
  
		MyCamera selectedCamera = (MyCamera)  CameraManagerment.getInstance().getexistCamera(checkUID) ;
		selectedCamera.stop(0);
		selectedCamera.disconnect(); 
		CameraManagerment.getInstance().CameraList.remove(selectedCamera);
		DatabaseManager var5 = new DatabaseManager(this);
		SQLiteDatabase var6 = var5.getReadableDatabase();
		Cursor var7 = var6.query("snapshot", new String[] { "_id",
				"dev_uid", "file_path", "time" }, "dev_uid = \'"
				+checkUID + "\'", (String[]) null,
				(String) null, (String) null, "_id LIMIT 4");

		while (var7.moveToNext()) {
			File var8 = new File(var7.getString(2));
			if (var8.exists()) {
				var8.delete();
			}
		}

	 NotificationTagManager.getInstance().removeTag(checkUID);
		var7.close();
		var6.close();
		var5.removeSnapshotByUID(checkUID);
		var5.removeDeviceByUID(checkUID);
		DeviceInfo selectedDevice = MainCameraFragment.getexistDevice(checkUID);
		if(selectedDevice!=null )
		MainCameraFragment.DeviceList.remove(selectedDevice); 
	
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

	protected void onCreate(Bundle var1) {
		super.onCreate(var1);
		Resources res = getResources();

		String text = res.getString(R.string.page23_p11_title);
		this.setTitle(text);
		this.setContentView(R.layout.admin_add_device);
		getActionBar().hide();
		Bundle bundle = this.getIntent().getExtras();

		this.selectUidStr = bundle.getString("selectUID");
		this.country = bundle.getString("country");
		String pkgString = bundle.getString("pkg");
		String devpwd =  bundle.getString("devpwd");
		String installmode =  bundle.getString("installmode");

		try {
			if (pkgString == null || pkgString.equals("")) {
				this.pkg = 19;
			} else {
				this.pkg = Integer.parseInt(pkgString);
			}
		}catch (Exception e){
			this.pkg = 19;
		}

		try {
			if(installmode!=null&& !installmode.equals("") ){
				faceDirection = Integer.parseInt(installmode);
			}
		}catch (Exception e){
			faceDirection = -1;
		}

		this.selectUidStr = selectUidStr.toUpperCase();


		if(country== null || country.equalsIgnoreCase("unknow")){
			locaTv =  (TextView) findViewById(R.id.devlocal_tv);
			localLv = (LinearLayout) findViewById(R.id.local_ll);
			localLv.setVisibility(View.VISIBLE);

			localLv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final String[] controycode_array = getResources().getStringArray(R.array.controycode_array);
					final String[] items = getResources().getStringArray(R.array.controy_array);
					DialogUtil.getInstance().showChoseControyPiviewDialo(LoginAddDeviceActivity.this, items, countryCodeIndex, getString(R.string.location_right_chose), new DialogUtil.DialogChooseItemcallback() {

					@Override
						public void chooseItem(int chooseItem) {
							String[] controyname_array = getResources().getStringArray(R.array.controy_array);
							locaTv.setText(controyname_array[chooseItem]);
							country = controycode_array[chooseItem];

						}

					},findViewById(R.id.devlocal_tv));
				}
			});
		}



		this.devlogin = (EditText) this.findViewById(R.id.devlogin);
		this.devpwd = (cn.ubia.widget.EditTextDrawable) this.findViewById(R.id.devpwd);
		this.devpwd.setDrawableListener(drawableListener);

		showpsdOn = getResources().getDrawable(
				R.drawable.add_icon_seen_press);
		showpsdOff = getResources().getDrawable(
				R.drawable.add_icon_seen);
		showpsdOn.setBounds(0, 0, showpsdOn.getIntrinsicWidth(),
				showpsdOn.getIntrinsicHeight());
		showpsdOff.setBounds(0, 0, showpsdOff.getIntrinsicWidth(),
				showpsdOff.getIntrinsicHeight());
		if(devpwd!=null && devpwd.equals("")){
			this.devpwd.setText(""); //用户自己输入;
		}else if(devpwd!=null){
			 this.devpwd.setText(devpwd);
		}
		if(MainCameraFragment.build_for_factory_tool){
			this.devpwd.setText("admin");
		}
		this.devname = (EditText) this.findViewById(R.id.devname);
		this.devpos = (EditText) this.findViewById(R.id.devpos);
		this.btnok = (Button) this.findViewById(R.id.btnok);
		back = (ImageView) this.findViewById(R.id.back);
		TextView	title  =  (TextView) this.findViewById(R.id.title); 
		title.setText(text);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						LoginAddDeviceActivity.this.finish();
					}
				});
		btnok.setOnClickListener(this.btnOKOnClickListener);
		
		this.findViewById(R.id.left_ll).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						LoginAddDeviceActivity.this.finish();
					}
				});

		devpos.requestFocus();
		this.getWindow().setSoftInputMode(3);
		myProgressBar = new MyProgressBar(this);
 		if (UbiaApplication.DEFAULT_NO_PUTMODE){
 			 
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
		face_horizon_iv = (ImageView) this.findViewById(R.id.face_horizon_iv);
		face_down_iv = (ImageView) this.findViewById(R.id.face_down_iv);
		face_up_iv = (ImageView) this.findViewById(R.id.face_up_iv);
		this.findViewById(R.id.face_down_ll).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						 
						faceDirection = VRConfig.CameraPutModelFaceDown;
						LoginAddDeviceActivity.this.switchFaceDirection();
					}
				});
		this.findViewById(R.id.face_up_ll).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						 
						faceDirection = VRConfig.CameraPutModelFaceUp;
						LoginAddDeviceActivity.this.switchFaceDirection();
					}
				});
		this.findViewById(R.id.face_horizon_ll).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
					 
						faceDirection = VRConfig.CameraPutModelFaceFront;
						LoginAddDeviceActivity.this.switchFaceDirection();
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

	private class SearchResultListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public SearchResultListAdapter(LayoutInflater var2) {
			this.mInflater = var2;
		}

		public int getCount() {
			return LoginAddDeviceActivity.this.list.size();
		}

		public Object getItem(int var1) {
			return LoginAddDeviceActivity.this.list.get(var1);
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
	
	
	private Drawable showpsdOn;
	private Drawable showpsdOff;
	private boolean flag_showpsd = false; 
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
}
