package cn.ubia.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;

import java.io.File;

import cn.ubia.UBell.BuildConfig;
import cn.ubia.UBell.R;
import cn.ubia.UbiaApplication;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.customprovider.OssService;

/**
 * Activity辅助类，一些常用的方法 继承BaseActivity或BaseFragment后，可以通过getHelper()获取
 * 
 * @author hello
 * 
 */

public class ActivityHelper {

	private Context context;
	private SharedPreferences sharedPreferences;

	public ActivityHelper(Context context) {
		if (context != null) {
			this.context = context;
			this.sharedPreferences = context.getSharedPreferences("profile",
					Context.MODE_PRIVATE);
		}
	}

	/**
	 * save config value to sharepreference
	 * 
	 * @param key
	 * @param value
	 */
	public void saveConfig(String key, String value) {
		Editor shareDate = sharedPreferences.edit();
		shareDate.putString(key, value);
		shareDate.commit();
	}

	/**
	 * get config value from sharepreference
	 * 
	 * @param key
	 * @return
	 */
	public String getConfig(String key) {
		return sharedPreferences.getString(key, null);
	}

	public boolean getBooleanConfig(String key) {
		return sharedPreferences.getBoolean(key, false);
	}

	public void removeConfig(String key) {
		sharedPreferences.edit().remove(key).commit();
	}

	public void showAlert(CharSequence title, CharSequence message,
			CharSequence positiveButton) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
		localBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		localBuilder.setTitle(title);
		localBuilder.setMessage(message);
		localBuilder.setPositiveButton(positiveButton,
				new OnClickListener() {
					public void onClick(
							DialogInterface paramAnonymousDialogInterface,
							int paramAnonymousInt) {
					}
				}).show();
	}

	public String getString(int id) {
		if (context == null)
			return null;
		return context.getString(id);
	}

	public void showMessage(int id) {
		Toast.makeText(context, getString(id), Toast.LENGTH_SHORT).show();
	}

	public void showMessageLong(int id) {
		Toast.makeText(context, getString(id), Toast.LENGTH_LONG).show();
	}
	
	public void showMessage(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public void showMessageLong(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	
	public boolean isSDCardValid() {
		return Environment.getExternalStorageState().equals("mounted");
	}

	// 显示dialog
	public Dialog showDialog(final String title, final String message,
			final ShowDialogCallback callback) {
		return showDialog(title, message, true, callback);
	}

	public Dialog showDialog(final String title, final String message,
			boolean hasCancle, final ShowDialogCallback callback) {
		ShowDialogParam param = new ShowDialogParam();
		param.title = title;
		param.message = message;
		param.callback = callback;
		param.hasCancle = hasCancle;
		param.negativeButtonName = context.getString(R.string.cancel);
		param.positiveButtonName = context.getString(R.string.ok);
		return showDialog(param);
	}

	public Dialog showDialog(String title, String message,
			String positiveButtonName, String negativeButtonName,
			ShowDialogCallback callback) {
		ShowDialogParam param = new ShowDialogParam();
		param.title = title;
		param.message = message;
		param.callback = callback;
		param.positiveButtonName = positiveButtonName;
		param.negativeButtonName = negativeButtonName;
		return showDialog(param);
	}

	// 创建列表形式的对话框
	public Dialog createItemSelectDialog(String title, String[] items,
			final ShowItemSelectDialogCallback callback) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setItems(items, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.callback(which);
			}
		});
		return builder.create();
	}

	// 显示列表形式的对话框
	public Dialog showItemSelectDialog(String title, String[] items,
			ShowItemSelectDialogCallback showItemSelectDialogCallback) {
		Dialog dialog = createItemSelectDialog(title, items,
				showItemSelectDialogCallback);
		dialog.show();
		return dialog;
	}

	public static class ShowDialogParam {
		public String title;
		public String message;
		public String positiveButtonName;
		public String negativeButtonName;
		public boolean hasCancle = true;
		public boolean cancelable = true;
		public View view;
		public ShowDialogCallback callback;
		public int style = -1;
	}

	public Dialog showDialog(final ShowDialogParam showDialogParam) {
		AlertDialog.Builder builder = null;
		if (showDialogParam.style != -1) {
			builder = new AlertDialog.Builder((new ContextThemeWrapper(context,
					showDialogParam.style)));
		} else {
			builder = new AlertDialog.Builder(context);
		}
		if (showDialogParam.title != null) {
			builder.setTitle(showDialogParam.title);
		}
		if (showDialogParam.view == null && showDialogParam.message != null) {
			builder.setMessage(showDialogParam.message);
		}
		builder.setPositiveButton(showDialogParam.positiveButtonName,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						showDialogParam.callback.callback(true);
					}
				});

		if (showDialogParam.hasCancle) {
			builder.setNegativeButton(showDialogParam.negativeButtonName,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							showDialogParam.callback.callback(false);
						}
					});
		}
		if (showDialogParam.view != null) {
			builder.setView(showDialogParam.view);
		}
		builder.setCancelable(showDialogParam.cancelable);
		return builder.show();
	}

	public static final int NETWORK_TYPE_NONE = -0x1;
	public static final int NETWORK_TYPE_WIFI = 0x1;
	public static final int NETWOKR_TYPE_MOBILE = 0x2;

	public int getCurrentNetType() {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI); // wifi
		NetworkInfo gprs = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); // gprs
		if (wifi != null && wifi.getState() == State.CONNECTED) {
			Log.d("", "Current net type:  WIFI.");
			return NETWORK_TYPE_WIFI;
		} else if (gprs != null && gprs.getState() == State.CONNECTED) {
			Log.d("", "Current net type:  MOBILE.");
			return NETWOKR_TYPE_MOBILE;
		}
		Log.e("", "Current net type:  NONE.");
		return NETWORK_TYPE_NONE;
	}



	public double getVer(String firmwareVersion){

		try {
			return Double.valueOf(firmwareVersion.substring(
					firmwareVersion.indexOf(".", firmwareVersion.indexOf(".") + 1) + 1,
					firmwareVersion.length())); //按照x.0.1.x的格式解析出版本号1.x
		}catch (Exception e){
			return 0;
		}
	}
	public boolean isConnectedToMyAp() {
		Log.i("first", "getWifiInfo...................");
		if (getCurrentNetType() != NETWORK_TYPE_WIFI)
			return false;
		WifiManager mWifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = mWifi.getConnectionInfo();
		String curssid = wifiInfo.getSSID();
		Log.i("first", "getWifiInfo..................." + curssid);
		if (!curssid.equalsIgnoreCase("MyAP")) {
			if (curssid.equalsIgnoreCase("\"MyAP\"")) {
				return true;
			}
			if (curssid.indexOf("MyAP") != -1) {
				return true;
			}
			return false;
		}
		return true;
	}

	// 打开WIFI设置
	public Intent getWifiSettingIntent() {
		Intent intent = null;
		if (Build.VERSION.SDK_INT > 10) {
			// 3.0以上打开设置界面
			intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
		} else {
			intent = new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS);
		}
		return intent;
	}

	public   void syncAlbum(Context context,String name) {

		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/DCIM/Camera/");

		String filePath = file.getPath() + "/"+ BuildConfig.FLAVOR+"_Cloud_"+name;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			final Uri contentUri = Uri.parse("file://" +filePath);
			scanIntent.setData(contentUri);
			context.sendBroadcast(scanIntent);
		} else {
			//4.4开始不允许发送"Intent.ACTION_MEDIA_MOUNTED"广播, 否则会出现: Permission Denial: not allowed to send broadcast android.intent.action.MEDIA_MOUNTED from pid=15410, uid=10135
			final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + filePath));
			context.sendBroadcast(intent);
		}


	}

	private String[] thumbColumns = new String[] {
			MediaStore.Video.Thumbnails.DATA,
			MediaStore.Video.Thumbnails.VIDEO_ID };

	private static final String FILE_NAME = "/pic.jpg";
	public static String TEST_IMAGE;


	OssService ossService;
	public OssService initOSS(String endpoint, String bucket) {

		OSSPlainTextAKSKCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(BuildConfig.OSSAK, BuildConfig.OSSSK);
		ClientConfiguration conf = new ClientConfiguration();
		conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
		conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
		conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
		conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
		OSS oss = new OSSClient(context, endpoint, credentialProvider, conf);

		OSSLog.enableLog();

		if(ossService==null){
			ossService =  new OssService(oss, bucket);
		}

		return ossService;
	}
}
