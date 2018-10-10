package com.zbar.lib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.zbar.lib.bitmap.RGBLuminanceSource;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.util.StringUtils;

/**
 * @File Name: CaptureActivity.java
 * @Description: 扫描二维码
 * @Copyright:Fluolife. All Rights Reserved
 * @Company: 深圳市萤光生活科技有限公司
 * @author LinYongKun
 * @version 1.1
 * @date 2015-5-15 上午10:28:36
 */
public class CaptureActivity extends BaseActivity implements OnClickListener,
		Callback {
	/** 声音大小 */
	private static final float BEEP_VOLUME = 0.50f;

	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;

	private boolean vibrate;
	/** 后退或者菜单 */
	private ImageView back;
	/*** activity标题 */
	private TextView title,rightTxt,btnFlashScan;
	/*** 标题栏右图标 */
	private ImageView rightIco;
	private int ifOpenLight = 0; // 判断是否开启闪光灯
	ProgressDialog mProgress;
	String photo_path;
	Bitmap scanBitmap;
	/*** 进入类型(1：NVR，0:IPC) */
	private boolean hasAdd;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private RelativeLayout captureContainter;
	private RelativeLayout captureCropLayout;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.twodimensioncode_camera);

		hasAdd = getIntent().getBooleanExtra("hasAdd", false);
		CameraManager.init(getApplication(),getResources().getConfiguration().orientation);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		initView();
	}

	/**
	 * @Method Name: initView
	 * @Description: 初始化控件
	 */
	private void initView() {
		back = (ImageView) this.findViewById(R.id.back);
		title = (TextView) this.findViewById(R.id.title);
		btnFlashScan = (TextView) this.findViewById(R.id.btn_cancel_scan);
		btnFlashScan.setOnClickListener(this);
		rightTxt = (TextView) this.findViewById(R.id.right_tv);
		rightTxt.setText(getResources().getString(R.string.captureactivity_xianc));
		rightTxt.setVisibility(View.VISIBLE);
		rightTxt.setOnClickListener(this);
		back.setImageResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		this.findViewById(R.id.left_ll).setOnClickListener(this);
		back.setOnClickListener(this);

		captureContainter = (RelativeLayout) findViewById(R.id.captureContainter);
		captureCropLayout = (RelativeLayout) findViewById(R.id.captureCropLayout);
		ImageView captureScanLine = (ImageView) findViewById(R.id.captureScanLine);
		TranslateAnimation mAnimation = new TranslateAnimation(
				TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE,
				0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		captureScanLine.setAnimation(mAnimation);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capturePreview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * Handler scan result
	 */
	public void handleDecode(String resultString) {
		inactivityTimer.onActivity();

		// FIXME
		if (resultString.equals("")) {
			Toast.makeText(CaptureActivity.this, "Scan failed!",
					Toast.LENGTH_SHORT).show();
			CaptureActivity.this.finish();
		} else {
			String scanResult = dealData(resultString);
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("result", resultString);
			resultIntent.putExtras(bundle);
			CaptureActivity.this.setResult(RESULT_OK, resultIntent);
//			CaptureActivity.this.finish();
		}
 		CaptureActivity.this.finish();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;
			int x = captureCropLayout.getLeft() * width / captureContainter.getWidth();
			int y = captureCropLayout.getTop() * height / captureContainter.getHeight();
			int cropWidth = captureCropLayout.getWidth() * width / captureContainter.getWidth();
			int cropHeight = captureCropLayout.getHeight() * height / captureContainter.getHeight();
			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);

		} catch (IOException ioe) {
			ioe.printStackTrace();
			return;
		} catch (RuntimeException e) {

			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(CaptureActivity.this,
							getString(R.string.scann_code_tip), Toast.LENGTH_LONG)
							.show();
				}
			});
			e.printStackTrace();
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(CaptureActivity.this);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public Handler getHandler() {
		return handler;
	}


	private static final long VIBRATE_DURATION = 200L;



	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {

			case R.id.left_ll:
				this.finish();
			break;
			case R.id.back:
				this.finish();
				break;


			case R.id.right_tv:
			{
				pickPictureFromAblum();

			}
				break;
			case R.id.btn_cancel_scan:
				setFlash();
				break;
		}
	}

	/**
	 * @Method Name: pickPictureFromAblum
	 * @Description: 获取带二维码的相片进行扫描
	 * @param v
	 */
	public void pickPictureFromAblum() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				try {
					try {
						Log.d("sss", "photo_path =" + photo_path);
						Uri selectedImage = data.getData();
						String[] filePathColumn = { MediaStore.Images.Media.DATA };

						Cursor cursor = getContentResolver()
								.query(selectedImage, filePathColumn, null,
										null, null);
						cursor.moveToFirst();

						int columnIndex = cursor
								.getColumnIndex(filePathColumn[0]);
						photo_path = cursor.getString(columnIndex);
						cursor.close();
						Log.d("sss", "photo_path =" + photo_path);
					} catch (Exception e) {
						e.printStackTrace();
					}


					new Thread(new Runnable() {
						@Override
						public void run() {
							Result result = scanningImage(photo_path);
							if (result != null) {
								Message m = mHandler.obtainMessage();
								m.what = 1;
								m.obj = result.getText();
								mHandler.sendMessage(m);
							} else {
								Message m = mHandler.obtainMessage();
								m.what = 2;
								m.obj = "Scan failed!";
								mHandler.sendMessage(m);
							}
						}
					}).start();
				} catch (Exception e) {
					Message m = mHandler.obtainMessage();
					m.what = 2;
					m.obj = "Scan failed!";
					mHandler.sendMessage(m);
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@SuppressLint("HandlerLeak")
	final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (mProgress != null) {
					mProgress.dismiss();
				}
				String resultString = msg.obj.toString();
				if (resultString.equals("")) {
					Toast.makeText(CaptureActivity.this,
							getString(R.string.captureactivity_a1_camera_alarm_photo),
							Toast.LENGTH_SHORT).show();
				} else {
//					Intent resultIntent = new Intent();
//					Bundle bundle = new Bundle();
//					bundle.putString("result", resultString);
//					resultIntent.putExtras(bundle);
//					CaptureActivity.this.setResult(RESULT_OK, resultIntent);

					String scanResult = dealData(resultString);
					Intent resultIntent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("result", resultString);
					resultIntent.putExtras(bundle);
					CaptureActivity.this.setResult(RESULT_OK, resultIntent);
					CaptureActivity.this.finish();
				}


				break;
			case 2:
				if (mProgress != null) {
					mProgress.dismiss();
				}
				Toast.makeText(CaptureActivity.this,
						getString(R.string.captureactivity_a1_camera_alarm_photo), Toast.LENGTH_LONG)
						.show();

				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	private String dealData(String str) {
		String scanResult = str;

		if (scanResult != null) {
			if (scanResult.length() > 20) {
				String var6 = "";

				for (int var7 = 0; var7 < scanResult.length(); ++var7) {
					if (scanResult.substring(var7, var7 + 1).matches(
							"[A-Z0-9]{1}")) {
						var6 = var6 + scanResult.substring(var7, var7 + 1);
					}
				}

				scanResult = var6;
			}

			return StringUtils.replaceStr(scanResult, "-", "");
		}
		return null;
	}

	/**
	 * 扫描二维码图片的方法
	 * 
	 * 目前识别度不高，有待改进
	 * 
	 * @param path
	 * @return
	 */
	public Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}

		Map<DecodeHintType, Object> hints = new HashMap<>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
		//复杂模式，开启PURE_BARCODE模式
		hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false;

		int sampleSize = (int) (options.outWidth / (float) 200);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);

		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {
			return reader.decode(bitmap1, hints);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Method Name: setFlash
	 * @Description: 设置闪光灯
	 */
	private void setFlash() {
		ifOpenLight++;
		switch (ifOpenLight % 2) {
		case 0:
			/*
			 * cancelScanButton
			 * .setImageResource(R.drawable.guide_connect_step02_scran_light_close
			 * );
			 */
			CameraManager.get().offLight();
			break;
		case 1:
			/*
			 * cancelScanButton
			 * .setImageResource(R.drawable.guide_connect_step02_scran_light_open
			 * );
			 */
			CameraManager.get().openLight();
			break;
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}

}