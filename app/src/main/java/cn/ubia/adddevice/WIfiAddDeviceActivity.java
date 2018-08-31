package cn.ubia.adddevice;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.ubia.LiveViewGLviewActivity;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.bean.MyCamera;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.util.PreferenceUtil;
import cn.ubia.util.StringUtils;
import cn.ubia.util.WifiAdmin;
import cn.ubia.widget.DialogUtil;
import cn.ubia.widget.EditTextDrawable;
import cn.ubia.widget.EditTextDrawable.DrawableListener;


@SuppressLint("ShowToast")
public class WIfiAddDeviceActivity extends BaseActivity {

	private static final String ZXING_DOWNLOAD_URL = "http://push.iotcplatform.com/release/BarcodeScanner.apk";
	private final int REQUEST_CODE_GETUID_BY_SCAN_BARCODE = 0;
	private Button btnCancel;
	// private UbiaNetConfig sendIP;
	//ProgressDialog myProgressBar;
	private int flag = 1;
	private EditText edtName;
	private EditText edtSecurityCode;
	private EditText edtUID;
	private IntentFilter filter;
	private ListView devListView = null;
	private List list = new ArrayList();
	private Button btnSearch;
	private TextView selectUID;
	private EditText edtSSID;
	private EditTextDrawable edtKEY;
	private Button nextButton, btnPreButton;
	private String selectUidStr;
	private WifiAdmin wifiAdmin;
	private String mUserSelectedWifiSsid;
	private PreferenceUtil mPreferenceUtil;
	private Handler handler = new Handler() {
		public void handleMessage(Message var1) {
			switch (var1.what) {
			case 1:
				//myProgressBar.dismiss();

				Iterator var5 = CameraManagerment.getInstance().CameraList.iterator();
				Log.i("mycamera",
						"camera:" +CameraManagerment.getInstance().CameraList.size());
				boolean var7;
				while (true) {
					boolean var6 = var5.hasNext();
					var7 = false;
					if (!var6) {
						break;
					}

					if (selectUidStr.equalsIgnoreCase(((MyCamera) var5.next())
							.getUID())) {
						var7 = true;
						break;
					}
				}

				if (var7) {
					Toast.makeText(WIfiAddDeviceActivity.this, "网络配置成功", 0)
							.show();
					WIfiAddDeviceActivity.this.finish();
				} else {
					WIfiAddDeviceActivity.this.finish();
					Intent intent = new Intent();
					intent.setClass(WIfiAddDeviceActivity.this,
							SetupAddDeviceActivity.class);
					// Log.i("fast", "................." + editUID.getText());
					intent.putExtra("selectUID", selectUidStr);
					startActivity(intent);
				}

				// if (Success == 1) {
				// Toast.makeText(WIfiAddDeviceActivity.this, "网络配置成功",
				// 0).show();
				// } else {
				// Toast.makeText(WIfiAddDeviceActivity.this, "网络配置超时",
				// 0).show();
				// }
				break;
			case 0:
				//myProgressBar.dismiss();
				Toast.makeText(WIfiAddDeviceActivity.this, "网络配置超时", 0).show();
				break;
			}
		}
	};

	private OnClickListener btnCancelOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
//			if (myProgressBar != null)
//				myProgressBar.dismiss();
			if (flag == 0) {
				flag = 1;

				// txtstate.setText("stop...");
				Log.i("send", "stop>>>>>>>>>>>>>>>>>>>");
				btnOK.setTextColor(Color.BLACK);
				// sendIP.stopApp();
			}
			// Intent var2 = new Intent();
			// AddDeviceActivity.this.setResult(0, var2);
			// AddDeviceActivity.this.finish();
		}
	};

	private Button btnOK;
	private OnClickListener btnOKOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			switch (var1.getId()) {
			case R.id.btnPre:
				WIfiAddDeviceActivity.this.finish();
				break;
			case R.id.btnNext:
				/*if(!isWifi(WIfiAddDeviceActivity.this)){

					Toast.makeText(WIfiAddDeviceActivity.this, getString(R.string.page10_tips_wifi_connect_failed), 0)
							.show();
					return;
				}*/
				String key = edtKEY.getText().toString();
				String ssidStr = edtSSID.getText().toString();

				if(!ssidStr.equals("")){
					if(key.length() >= 8 || key.length() == 5 || key.equals("")){
						if((key.length() + ssidStr.length()) > 50){
							getHelper().showMessage(R.string.page13_tips_dev_ssid_key);
						}else {
							Log.i("wifitest", "ssidStr:" + ssidStr);
							Intent intent = new Intent();
							intent.setClass(WIfiAddDeviceActivity.this, SearchCarmeraFragmentActivity.class);
							intent.putExtra("ssidStr", ssidStr);
							intent.putExtra("key", key);
							mPreferenceUtil.putString(ssidStr, key);
							// startActivity(intent);
							startActivityForResult(intent, 556);
						}
					}else{
						DialogUtil.getInstance().showTextTipDialog(
								WIfiAddDeviceActivity.this, getString(R.string.password_wifi_tip),getString(R.string.finish),null);
					}

				}

				break;
			}

		}
	};
	private Button btnScan;
	private OnClickListener btnScanClickListener = new OnClickListener() {
		public void onClick(View var1) {
			Intent var2 = new Intent("com.google.zxing.client.android.SCAN");
			if (WIfiAddDeviceActivity.this.getPackageManager()
					.queryIntentActivities(var2, 65536).size() == 0) {
				(new Builder(WIfiAddDeviceActivity.this))
						.setIcon(17301543)
						.setTitle(
								WIfiAddDeviceActivity.this
										.getText(R.string.page5_tips_warning))
						.setMessage(
								WIfiAddDeviceActivity.this
										.getText(R.string.page10_page13_tips_no_barcode_scanner))
						.setPositiveButton(
								WIfiAddDeviceActivity.this.getText(R.string.ok),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface var1,
											int var2) {
										(new Thread(new Runnable() {
											public void run() {
												if (WIfiAddDeviceActivity.this
														.isSDCardValid()) {
													try {
														WIfiAddDeviceActivity.this
																.startDownload("http://push.iotcplatform.com/release/BarcodeScanner.apk");
													} catch (Exception var2) {
														System.out.println(var2
																.getMessage());
													}
												} else {
													Toast.makeText(
															WIfiAddDeviceActivity.this,
															"û��sd��", 0)
															.show();
												}
											}
										})).start();
									}
								})
						.setNegativeButton(
								WIfiAddDeviceActivity.this
										.getText(R.string.cancel),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface var1,
											int var2) {
									}
								}).show();
			} else {
				var2.putExtra("SCAN_MODE", "QR_CODE_MODE");
				WIfiAddDeviceActivity.this.startActivityForResult(var2, 0);
			}
		}
	};
	private ProgressDialog progressdlg;

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
		Log.i("wifi","WIfiAddDeviceActivity:"+var1+"var2:"+var2);
		if (var1 == 0 && var2 == -1) {
			String var4 = var3.getStringExtra("SCAN_RESULT");
			if (var4 == null) {
				Bundle var8 = var3.getExtras();
				if (var8 != null) {
					var4 = var8.getString("result");
				}
			}

			if (var4 != null) {
				if (var4.length() > 20) {
					String var6 = "";

					for (int var7 = 0; var7 < var4.length(); ++var7) {
						if (var4.substring(var7, var7 + 1).matches(
								"[A-Z0-9]{1}")) {
							var6 = var6 + var4.substring(var7, var7 + 1);
						}
					}

					var4 = var6;
				}

				this.edtUID.setText(var4);
				this.edtName.requestFocus();
				return;
			}
		}
		if (var1 == 9999) {
			setResult(999, null);
			finish();
		}
		if (var1 == 556) {
			setResult(999, null);
			finish();
		}

	}
 	public   boolean isWifi(Context context) {
	 ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo();
	if (networkINfo != null&& networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
		return true;
	}
	 return false;
	}
	private ImageView back;
	private TextView title;
	protected void onCreate(Bundle var1) {
		super.onCreate(var1);
		mPreferenceUtil = PreferenceUtil.getInstance();
		mPreferenceUtil.init(this);
		this.setTitle(R.string.page13_title_add_device);
		// this.setContentView(R.layout.wifi_fast_add_device);
		this.setContentView(R.layout.setup_camera_config_wifi);
		getActionBar().hide();

		title = (TextView) this.findViewById(R.id.title);
		title.setText(getString(R.string.page13_co_camera_to_wf));
		this.findViewById(R.id.left_ll).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				WIfiAddDeviceActivity.this.finish();
			}
		});
		back = (ImageView) findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				WIfiAddDeviceActivity.this.finish();
			}
		});


		Bundle var2 = this.getIntent().getExtras();
		// this.selectUidStr = var2.getString("selectUID");
		// this.selectUID = (TextView) this.findViewById(R.id.selectUID);
		// selectUID.setText(selectUidStr);
		showpsdOn = getResources().getDrawable(
				R.drawable.add_icon_seen_press);
		showpsdOff = getResources().getDrawable(
				R.drawable.add_icon_seen);
		showpsdOn.setBounds(0, 0, showpsdOn.getIntrinsicWidth(),
				showpsdOn.getIntrinsicHeight());
		showpsdOff.setBounds(0, 0, showpsdOff.getIntrinsicWidth(),
				showpsdOff.getIntrinsicHeight());
		this.edtSSID = (EditText) this.findViewById(R.id.edtSSID);
		this.edtKEY = (EditTextDrawable) this.findViewById(R.id.edtKEY);
		edtKEY.requestFocus();
		this.edtKEY.setDrawableListener(drawableListener);
		// this.edtName = (EditText) this.findViewById(R.id.edtNickName);
		this.btnPreButton = (Button) this.findViewById(R.id.btnPre);
		this.btnPreButton.setOnClickListener(this.btnOKOnClickListener);
		this.nextButton = (Button) this.findViewById(R.id.btnNext);
		this.nextButton.setOnClickListener(this.btnOKOnClickListener);
		// btnOKOnClickListener

		wifiAdmin = new WifiAdmin(this);
		mUserSelectedWifiSsid = wifiAdmin.getSSID();
		wifiAdmin.getConfiguration();
		if (mUserSelectedWifiSsid != null
				&& mUserSelectedWifiSsid.startsWith("\"")
				&& mUserSelectedWifiSsid.length() > 2) {
			mUserSelectedWifiSsid = mUserSelectedWifiSsid.substring(1,
					mUserSelectedWifiSsid.length() - 1);
		}
		edtSSID.setText(mUserSelectedWifiSsid);
		if(isWifiApEnabled()){
			edtSSID.setText("");
			edtSSID.setHint(R.string.page13_p5_wifi_placeholder);
		}else if(mUserSelectedWifiSsid.equals("<unknown ssid>")||mUserSelectedWifiSsid.equals("NULL")){
			edtSSID.setText("");
			edtSSID.setHint(R.string.page13_p5_wifi_placeholder);
			DialogUtil.getInstance().showTextTipDialog(this,getString(R.string.add_getssid_fail),getString(R.string.ok),null);
		}



		edtKEY.requestFocus();
	
		this.getWindow().setSoftInputMode(3);


		String pwd = mPreferenceUtil.getString(mUserSelectedWifiSsid);
		if (!StringUtils.isEmpty(pwd)) {
			edtKEY.setText(pwd);
		}  
		toggleShowpsd();
		// myProgressBar = new MyProgressBar(this);
	}

	protected void onDestroy() {

		super.onDestroy();
		// if (sendIP != null) {
		// sendIP.stopApp();
		// }
//		Log.i("wifi", "WiFiDirectConfig.unregisterUBICListener(this).......");
//		if (myProgressBar != null)
//			myProgressBar.dismiss();
//		WiFiDirectConfig.StopConfig();
//		WiFiDirectConfig.unregisterUBICListener(this);
		// this.unregisterReceiver(this.resultStateReceiver);
	}

	/**
	 * 热点开关是否打开
	 * @return
	 */
	public boolean isWifiApEnabled() {
		try {
			WifiManager mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

			Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
			method.setAccessible(true);
			return (Boolean) method.invoke(mWifiManager);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
			return WIfiAddDeviceActivity.this.list.size();
		}

		public Object getItem(int var1) {
			return WIfiAddDeviceActivity.this.list.get(var1);
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
	private void toggleShowpsd() {
		if (flag_showpsd) {
			edtKEY.setCompoundDrawables(edtKEY.getCompoundDrawables()[0],
					edtKEY.getCompoundDrawables()[1], showpsdOn,
					edtKEY.getCompoundDrawables()[3]);
			edtKEY.setTransformationMethod(HideReturnsTransformationMethod
					.getInstance());
		} else {
			edtKEY.setCompoundDrawables(edtKEY.getCompoundDrawables()[0],
					edtKEY.getCompoundDrawables()[1], showpsdOff,
					edtKEY.getCompoundDrawables()[3]);
			edtKEY.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
		}
		edtKEY.setSelection(edtKEY.getText().length());
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
}
