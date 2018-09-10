package cn.ubia.adddevice;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ubia.UbiaApplication;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.base.Constants;
import cn.ubia.bean.MyCamera;
import cn.ubia.db.DatabaseManager;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.util.PreferenceUtil;
import cn.ubia.util.StringUtils;
import cn.ubia.widget.MyProgressBar;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.my.IOTC.UBICAPIs;
import com.tutk.IOTC.st_LanSearchInfo2;
import com.ubia.http.HttpClient;
import com.zbar.lib.CaptureActivity;

public class AddDeviceActivity extends BaseActivity {
	// IOTCAPI
	
	private static final String ZXING_DOWNLOAD_URL = "http://push.iotcplatform.com/release/BarcodeScanner.apk";
	private final int REQUEST_CODE_GETUID_BY_SCAN_BARCODE = 0;
	private Button btnCancel;
	private MyProgressBar mProgressBar;
	private ImageView back;
	private TextView title;
	private final int SEARCH=999;
	private final int LAN_SEARCH=9999;
	private int pkg;
	
	private OnClickListener btnCancelOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			Intent var2 = new Intent();
			AddDeviceActivity.this.setResult(0, var2);
			AddDeviceActivity.this.finish();
		}
	};
	st_LanSearchInfo2[] var6=null;
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.what == SEARCH) {
				String toast =  new String(AddDeviceActivity.this.getString(R.string.page35_p8_toast_lansearch));
				progressdlg.setMessage(toast);
				progressdlg.setCancelable(false);
				progressdlg.show();
				new Thread() {
					public void run() {
						var6 = UBICAPIs.IOTC_Lan_Search(new int[1],
								3000);// Camera.SearchLAN();
						
						handler.sendEmptyMessage(LAN_SEARCH);
					};
				}.start();

			} else if (msg.what == LAN_SEARCH) {

				final AlertDialog var2 = (new Builder(new ContextThemeWrapper(
						AddDeviceActivity.this, R.style.HoloAlertDialog)))
						.create();
				var2.setTitle(AddDeviceActivity.this
						.getText(R.string.page35_dialog_LanSearch));
				// var2.setIcon(17301573);
				View var3 = var2.getLayoutInflater().inflate(
						R.layout.search_device, (ViewGroup) null);
				var2.setView(var3);
				ListView var4 = (ListView) var3
						.findViewById(R.id.lstSearchResult);
				Button var5 = (Button) var3.findViewById(R.id.btnRefresh);
				AddDeviceActivity.this.list.clear();
				progressdlg.dismiss();

				
				if (var6 != null && var6.length > 0) {
					int var8 = var6.length;

					for (int var9 = 0; var9 < var8; ++var9) {
						st_LanSearchInfo2 var10 = var6[var9];
						SearchResult result = AddDeviceActivity.this.new SearchResult(
								(new String(var10.UID)).trim(), (new String(
										var10.IP)).trim(), var10.port);
						pkg = var10.DeviceName[128];
						result.pkg =pkg;
						AddDeviceActivity.this.list.add(result);
						Log.e("","===>>>>IOTC_Lan_Search2:MAC="+ String.format("%02x:%02x:%02x:%02x:%02x:%02x",
								(var10.DeviceName[0]&0xFF),(var10.DeviceName[1]&0xFF),(var10.DeviceName[2]&0xFF),(var10.DeviceName[0]&0xFF)
								,(var10.DeviceName[3]&0xFF),(var10.DeviceName[5]&0xFF)));
					
						Iterator cameraList = CameraManagerment.getInstance().CameraList
								.iterator();
						Log.i("mycamera",
								"camera:" + new String(var10.UID).trim());
						boolean var7;
						while (true) {
							boolean isadded = cameraList.hasNext();
							var7 = false;
							if (!isadded) {
								result.ADDFLAG = "";
								break;
							}

							if (new String(var10.UID).trim().equalsIgnoreCase(
									((MyCamera) cameraList.next()).getUID())) {
								var7 = true;
								result.ADDFLAG = AddDeviceActivity.this.getString(R.string.page35_p10_cell_status); //"已添加"; 
								break;
							}
						}

					}
				} else {
					Log.i("adddevice", "IOTC_Lan_Search is null");
				}

				final SearchResultListAdapter var7 = AddDeviceActivity.this.new SearchResultListAdapter(
						var2.getLayoutInflater());
				var4.setAdapter(var7);
				var4.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView var1, View var2x,
							int var3, long var4) {
						SearchResult var6 = AddDeviceActivity.this.list
								.get(var3);
						//AddDeviceActivity.this.edtUID.setText(var6.UID);
						// AddDeviceActivity.this.edtSecurityCode.setText("admin");
						var2.dismiss();
						Intent intent = new Intent();
						intent.setClass(AddDeviceActivity.this,
								LoginAddDeviceActivity.class);
						// Log.i("fast", "................." + editUID.getText());
						intent.putExtra("selectUID", var6.UID);
						intent.putExtra("pkg", var6.pkg);
						//startActivity(intent);
						startActivityForResult(intent, 9999);
					}
				});
			//	var5.setTextColor(R.color.black);
				var5.setOnClickListener(new OnClickListener() {
					public void onClick(View var1) {
						String toast =  new String(AddDeviceActivity.this.getString(R.string.page35_p8_toast_lansearch));
						progressdlg.setMessage(toast);
						progressdlg.setCancelable(false);
						progressdlg.show();
						new Thread() {
							public void run() {
								var6 = UBICAPIs.IOTC_Lan_Search(new int[1],
										3000);// Camera.SearchLAN();
								
								handler.sendEmptyMessage(LAN_SEARCH);
							};
						}.start();
						var2.dismiss();					
					}
				});
				//var5.setTextColor(R.color.white);
				var2.show();
			}
			if(msg.what == 888){
				
			}
		};
	};
	private ProgressDialog progressdlg = null;
	private Button btnNext;
	private OnClickListener btnOKOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			String var2 = AddDeviceActivity.this.edtUID.getText().toString();// AddDeviceActivity.this.edtName.getText().toString();
			String var3 = AddDeviceActivity.this.edtUID.getText().toString()
					.trim();
			String var4 = "admin";// AddDeviceActivity.this.edtSecurityCode.getText().toString().trim();
			if (var2.length() == 0) {
				getHelper().showMessage(R.string.page7_tips_camera_name);

			} else if (var3.length() == 0) {

				getHelper().showMessage(R.string.page7_tips_dev_uid);
			} else if (var3.length() != 20) {
				getHelper().showMessage(R.string.page7_tips_dev_uid_character);

			} else if (var4.length() == 0) {
				getHelper().showMessage(R.string.page7_tips_dev_security_code);

			} else {


				Iterator var5 = CameraManagerment.getInstance().CameraList.iterator();
				Log.i("mycamera",
						"camera:" + CameraManagerment.getInstance().CameraList.size());
				boolean var7;
				while (true) {
					boolean var6 = var5.hasNext();
					var7 = false;
					if (!var6) {
						break;
					}

					if (var3.equalsIgnoreCase(((MyCamera) var5.next()).getUID())) {
						var7 = true;
						break;
					}
				}

				if (var7) {
					getHelper()
							.showMessage(R.string.page6_tips_add_camera_duplicated);

				} else {

					Intent intent = new Intent();
					intent.setClass(AddDeviceActivity.this,
							LoginAddDeviceActivity.class);
					// Log.i("fast", "................." + editUID.getText());
					intent.putExtra("selectUID", var3);
					//startActivity(intent);
					startActivityForResult(intent, LAN_SEARCH);

					// addDeviceToServer(var3, var2, "", var4);
				}
			}
		}
	};
	private ImageView btnScan;
	private OnClickListener btnScanClickListener = new OnClickListener() {
		public void onClick(View var1) {

			progressdlg.setMessage(getResources()
					.getString(R.string.mainfragment_adddeviceactivity_u_tips_scan_qrcode));
			progressdlg.setCancelable(false);
			progressdlg.show();
			Intent intent = new Intent(getApplicationContext(),
					CaptureActivity.class);
			startActivityForResult(intent, 0);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);// ���붯��
		}
	};
	private ImageView btnSearch;
	private OnClickListener btnSearchOnClickListener = new OnClickListener() {
		public void onClick(View var1) {

			handler.sendEmptyMessage(SEARCH);
			//handler.sendEmptyMessage(9999);

		}
	};
	private EditText edtName;
	private EditText edtSecurityCode;
	private EditText edtUID;
	private IntentFilter filter;
	private List<SearchResult> list = new ArrayList<SearchResult>();
	private ResultStateReceiver resultStateReceiver;

	// private ThreadTPNS thread;

	private boolean isSDCardValid() {
		return Environment.getExternalStorageState().equals("mounted");
	}

	private void startDownload(String var1) throws Exception {
		if (URLUtil.isNetworkUrl(var1)) {
			InputStream var2 = ((HttpURLConnection) (new URL(var1))
					.openConnection()).getInputStream();
			if (var2 != null) {
				BufferedInputStream var3 = new BufferedInputStream(var2);
				File var4 = File.createTempFile("BarcodeScanner", ".apk",
						Environment.getExternalStorageDirectory());
				FileOutputStream var5 = new FileOutputStream(var4);
				byte[] var6 = new byte[1024];

				while (true) {
					int var7 = var3.read(var6);
					if (var7 <= 0) {
						try {
							var5.flush();
							var5.close();
						} catch (Exception var9) {
							System.out.println("error: " + var9.getMessage());
						}

						this.startInstall(var4);
						return;
					}

					var5.write(var6, 0, var7);
				}
			}
		}

	}

	private void startInstall(File var1) {
		Intent var2 = new Intent();
		var2.addFlags(268435456);
		var2.setAction("android.intent.action.VIEW");
		var2.setDataAndType(Uri.fromFile(var1),
				"application/vnd.android.package-archive");
		this.startActivity(var2);
	}

	public void onActivityResult(int var1, int var2, Intent var3) {
		if (var1 == 0 && var2 == -1) {
			String var4 = var3.getStringExtra("SCAN_RESULT");
			String[] str =null;
			if (var4 == null) {
				Bundle var8 = var3.getExtras();
				if (var8 != null) {
					var4 = var8.getString("result");
					str  = var4.split(":");
				}
				
			}
			
			if (var4 != null) { 
				var4 =str[0];
				this.edtUID.setText(var4);
				Intent intent = new Intent();
				intent.setClass(AddDeviceActivity.this,
						LoginAddDeviceActivity.class);
				// Log.i("fast", "................." + editUID.getText());
				intent.putExtra("selectUID", var4);
				if(str!=null && str.length==3)
				{
					intent.putExtra("user", str[1]);
					intent.putExtra("devpwd", str[2]);
				}
				if(str!=null && str.length==4)
				{
					intent.putExtra("user", str[1]);
					intent.putExtra("devpwd", str[2]);
					intent.putExtra("installmode", str[3]);
				}
				if(str!=null && str.length==5)
				{
					intent.putExtra("user", str[1]);
					intent.putExtra("devpwd", str[2]);
					intent.putExtra("installmode", str[3]);
				}
				if(str!=null && str.length==6)
				{
					intent.putExtra("user", str[1]);
					intent.putExtra("devpwd", str[2]);
					intent.putExtra("installmode", str[3]);
					intent.putExtra("myCountryCode", str[5]);
					PreferenceUtil.getInstance().putInt(Constants.COUNTRYCODE+str[0].toUpperCase() , StringUtils.getCurrentLocaltionISOCountryCodeNumber(str[5]));
				} 
				startActivityForResult(intent, LAN_SEARCH); 
				return;
			}
		}
		if (var1 == LAN_SEARCH)
		{
			setResult(SEARCH, null);
			finish();
		}
		

	}

	protected void onCreate(Bundle var1) {
		super.onCreate(var1);
		this.setTitle(this.getText(R.string.page35_dialog_AddCamera));
		this.setContentView(R.layout.setup_add_device);
		getActionBar().hide();
		Intent intent = this.getIntent();
		String stringValue = intent.getStringExtra("selectUID");
		String stringpwd = intent.getStringExtra("selectPWD");

		mProgressBar = new MyProgressBar(this);
		this.filter = new IntentFilter();
		this.filter.addAction(AddDeviceActivity.class.getName());
		this.resultStateReceiver = new ResultStateReceiver(
				(ResultStateReceiver) null);
		LocalBroadcastManager.getInstance(this).registerReceiver(resultStateReceiver,
				filter);
		this.edtUID = (EditText) this.findViewById(R.id.edtUID);
		// this.edtSecurityCode = (EditText) this
		// .findViewById(R.id.edtSecurityCode);

		if (stringValue != null) {
			this.edtSecurityCode.setText(stringpwd);
			this.edtUID.setText(stringValue);

		}

		// this.edtName = (EditText) this.findViewById(R.id.edtNickName);
		this.btnScan = (ImageView) this.findViewById(R.id.btnScan);
		this.btnScan.setOnClickListener(this.btnScanClickListener);
		this.findViewById(R.id.btnScan_ll).setOnClickListener(btnScanClickListener);
		
		this.btnSearch = (ImageView) this.findViewById(R.id.btnSearch);
		this.btnSearch.setOnClickListener(this.btnSearchOnClickListener);
		this.findViewById(R.id.btnSearch_ll).setOnClickListener(this.btnSearchOnClickListener);
		this.btnNext = (Button) this.findViewById(R.id.btnNext);
		this.btnNext.setOnClickListener(this.btnOKOnClickListener);
		// this.btnCancel = (Button) this.findViewById(R.id.btnCancel);
		// this.btnCancel.setOnClickListener(this.btnCancelOnClickListener);

		progressdlg = new ProgressDialog(this);
		progressdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressdlg.setMessage(getString(R.string.page35_camera_add));
		back = (ImageView) this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		title.setText(getString(R.string.page14_page35_p3_connect_camera_btn));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddDeviceActivity.this.finish();
			}
		});
		title = (TextView) this.findViewById(R.id.title);
		title.setText(getString(R.string.page14_page35_p3_connect_camera_btn));
		this.findViewById(R.id.left_ll).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AddDeviceActivity.this.finish();
			}
		});
		this.getWindow().setSoftInputMode(3);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (progressdlg != null && progressdlg.isShowing()) {
			progressdlg.cancel();

		}
		super.onResume();
		mProgressBar.dismiss();
		
		MyCamera.init();
	}

	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(this.resultStateReceiver);
	}

	private class ResultStateReceiver extends BroadcastReceiver {

		private ResultStateReceiver() {
		}

		// $FF: synthetic method
		ResultStateReceiver(ResultStateReceiver var2) {
			this();
		}

		public void onReceive(Context var1, Intent var2) {
		}
	}

	private class SearchResult {

		public String IP;
		public String UID;
		public String ADDFLAG;
		public int pkg;
		public SearchResult(String var2, String var3, int var4) {
			this.UID = var2;
			this.IP = var3;
		}
	}

	public final class ViewHolder {

		public TextView ip;
		public TextView uid;
		public TextView addflag;

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
	private void addDeviceToServer(final String devicUid,
			final String deviceName, final String deviceLocation,
			final String viewPwd,final int installmode) {

		String token = getHelper().getConfig(Constants.TOKEN);
		String tokenSecret = getHelper().getConfig(Constants.TOKEN_SECRET);
		String account = getHelper().getConfig(Constants.USER_NAME);
		String password = getHelper().getConfig(Constants.USER_PASSWORD);
		Log.i("mycamera", "password:" + password + ",viewPwd:" + viewPwd
				+ ",devicUid:" + devicUid);
		if (!UbiaApplication.ISWEB) {
			mProgressBar.dismiss();
			Log.d("mycamera", "addDeviceToServer: is LAN");
			long var8 = (new DatabaseManager(AddDeviceActivity.this))
					.addDevice(deviceName, devicUid, "", "", "admin", viewPwd,
							3, 1,installmode,pkg);
			Toast.makeText(
					AddDeviceActivity.this,
					AddDeviceActivity.this.getText(R.string.page6_tips_add_camera_ok)
							.toString(), 0).show();
			Bundle var10 = new Bundle();
			var10.putLong("db_id", var8);
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
			AddDeviceActivity.this.setResult(-1, var11);
			AddDeviceActivity.this.finish();
			
			HttpClient.operateDeviceVoip(devicUid, 1,mJsonHttpResponseHandler);
			/*
			 * String viewAccount = "admin"; int channel = 0; boolean isPrivate
			 * = true; boolean isPublic = false; boolean isShare = false;
			 * boolean isAlarm = false;
			 * 
			 * long dbId = new DatabaseManager(AddDeviceActivity.this)
			 * .addDevice(deviceName, devicUid, viewAccount, viewPwd,
			 * deviceLocation, 3, channel, isPrivate, isPublic, isShare,
			 * isAlarm); DeviceInfo deviceInfo = new DeviceInfo(dbId, null,
			 * deviceName, devicUid, deviceLocation, viewAccount, viewPwd, "",
			 * 3, channel, (Bitmap) null, isPrivate, isPublic, isShare, isAlarm,
			 * true, null, null); Intent intent = new Intent();
			 * intent.putExtra("deviceInfo", deviceInfo); setResult(RESULT_OK,
			 * intent); finish();
			 * getHelper().showMessage(R.string.tips_add_camera_ok);
			 */

		} else {
			Log.d("mycamera", "addDeviceToServer: is WIFI");
			HttpClient httpClient = new HttpClient(token, tokenSecret);
			if (httpClient == null)
				return;
			httpClient.addDevice(account, viewPwd, devicUid, deviceName,
					deviceLocation, new JsonHttpResponseHandler() {// password
						// changed
						// viewPwd

						// httpClient.removeDevice(account, viewPwd, devicUid,
						// new AsyncHttpResponseHandler() {// password

						@Override
						public void onStart() {
							super.onStart();
							mProgressBar.show();
						}

						@Override
						public void onSuccess(int statusCode,
								JSONObject response) {
							mProgressBar.dismiss();
							Log.d("url",
									"addDeviceToServer:" + response.toString());
							String result = response.toString();
							if (result != null) {
								boolean state = response.optBoolean("state");
								if (state) {
									// String viewAccount = "admin";
									// int channel = 0;
									// boolean isPrivate = true;
									// boolean isPublic = false;
									// boolean isShare = false;
									// boolean isAlarm = false;
									//
									// long dbId = new DatabaseManager(
									// AddDeviceActivity.this)
									// .addMyDevice(deviceName, devicUid,
									// viewAccount, viewPwd,
									// deviceLocation, 3, channel,
									// isPrivate, isPublic,
									// isShare, isAlarm);
									// DeviceInfo deviceInfo = new DeviceInfo(
									// dbId, null, deviceName, devicUid,
									// deviceLocation, viewAccount,
									// viewPwd, "", 3, channel,
									// (Bitmap) null, isPrivate, isPublic,
									// isShare, isAlarm, true, null, null);
									long var8 = (new DatabaseManager(
											AddDeviceActivity.this)).addDevice(
											deviceName, devicUid, "", "",
											"admin", viewPwd, 3, 0,installmode,pkg);
									Toast.makeText(
											AddDeviceActivity.this,
											AddDeviceActivity.this
													.getText(
															R.string.page6_tips_add_camera_ok)
													.toString(), 0).show();
									Bundle var10 = new Bundle();
									var10.putLong("db_id", var8);
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
									

									
									AddDeviceActivity.this.setResult(-1, var11);
									AddDeviceActivity.this.finish();
									// AddDeviceActivity.this.finish();
									// Intent intent = new Intent();
									// intent.putExtra("deviceInfo",
									// deviceInfo);
									// setResult(RESULT_OK, intent);
									// finish();
									// getHelper().showMessage(
									// R.string.tips_add_camera_ok);
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
							mProgressBar.dismiss();
							getHelper().showMessage(
									R.string.page6_failed_to_add_device);
							error.printStackTrace();
						}

					});
		}
	}

	private class SearchResultListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public SearchResultListAdapter(LayoutInflater var2) {
			this.mInflater = var2;
		}

		public int getCount() {
			return AddDeviceActivity.this.list.size();
		}

		public Object getItem(int var1) {
			return AddDeviceActivity.this.list.get(var1);
		}

		public long getItemId(int var1) {
			return (long) var1;
		}

		public View getView(int var1, View var2, ViewGroup var3) {
			SearchResult var4 = (SearchResult) this
					.getItem(var1);
			ViewHolder var5;
			if (var2 == null) {
				var2 = this.mInflater.inflate(R.layout.search_device_result,
						(ViewGroup) null);
				var5 = new ViewHolder();
				var5.uid = (TextView) var2.findViewById(R.id.uid);
				var5.ip = (TextView) var2.findViewById(R.id.ip);
				var5.addflag = (TextView) var2.findViewById(R.id.addflag);
				var2.setTag(var5);
			} else {
				var5 = (ViewHolder) var2.getTag();
			}

			var5.uid.setText(var4.UID);
			var5.ip.setText(var4.IP);
			var5.addflag.setText(var4.ADDFLAG);
			return var2;
		}
	}

}
