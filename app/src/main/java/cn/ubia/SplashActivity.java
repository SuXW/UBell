package cn.ubia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

import com.zbar.lib.CaptureActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;

public class SplashActivity extends BaseActivity {
	/** Called when the activity is first created. */

	final private int REQUEST_CODE_ASK_PERMISSIONS_CAM = 123;//Ȩ��������
	final private int REQUEST_CODE_ASK_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 124;//Ȩ��������
	final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 125;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);


		if (Build.VERSION.SDK_INT >=23){
			try {
				requestPermissions();
			} catch (Exception e) {
				e.printStackTrace();

			}

		}else{
			gotoMain();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:

				Map<String, Integer> perms = new HashMap<>();
				// Initial
				perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);

				for (int i = 0; i < permissions.length; i++) {
					perms.put(permissions[i], grantResults[i]);
				}
				if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
						&& perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
						&& perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
						) {
					// All Permissions Granted
					gotoMain();
				} else {
					finish();
					// Permission Denied
					/*Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
							.show();*/
				}

				break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	private void requestPermissions() {


		final List<String> permissionsList = new ArrayList<String>();
		addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		addPermission(permissionsList, Manifest.permission.CAMERA);
		addPermission(permissionsList, Manifest.permission.RECORD_AUDIO);

		if (permissionsList.size() > 0) {
				requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
					REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
		}else{
			gotoMain();
		}

	}



	private void addPermission(List<String> permissionsList, String permission) {
		if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
			permissionsList.add(permission);

		}

	}



	private void gotoMain(){
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent mainIntent = null;

				UbiaApplication.ISWEB = false;
				mainIntent = new Intent(SplashActivity.this,
						MainActivity.class);//LoginActivity.class);//	WelcomeActivity.class);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

				SplashActivity.this.startActivity(mainIntent);
				SplashActivity.this.finish();
			}
		}, 1000);
	}
}