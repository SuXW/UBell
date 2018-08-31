package cn.ubia.adddevice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ubia.vr.GLRenderer;
import com.ubia.vr.VRConfig;
import cn.ubia.UBell.BuildConfig;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.base.Constants;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.MyCamera;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.util.FileUtils;
import cn.ubia.util.PreferenceUtil;
import cn.ubia.util.StringUtils;


public class QrCodeShareInfoActivity extends BaseActivity implements
		OnClickListener {

	private MyCamera mCamera;
	private DeviceInfo mDevice;
	/** 后退或者菜单 */
	private ImageView left_iv;
	/*** activity标题 */
	private TextView title;
	private ImageView qr_code_iv;
	RelativeLayout camera_saveqr_rl;
	Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_deviceinfo);
		getActionBar().hide();
		left_iv = (ImageView) this.findViewById(R.id.back);
		left_iv.setBackgroundResource(R.drawable.ic_action_left);
		title = (TextView) this.findViewById(R.id.title);
		left_iv.setVisibility(View.VISIBLE);
		title.setText(getResources().getString(R.string.page10_page30_share_device_qr));
		qr_code_iv = (ImageView) this.findViewById(R.id.fq_code_iv);
		left_iv.setOnClickListener(this);
		this.findViewById(R.id.camera_saveqr_rl).setOnClickListener(this);
		this.findViewById(R.id.left_ll).setOnClickListener(this);
		Bundle var2 = this.getIntent().getExtras(); 
		String mDevUID = var2.getString("dev_uid");
		this.mCamera =  CameraManagerment.getInstance().getexistCamera(mDevUID);

		if(mCamera!=null)
			this.mCamera.registerIOTCListener(this);

		this.mDevice  = MainCameraFragment.getexistDevice(mDevUID);


		if (this.mDevice != null) {
			mDevice.country = PreferenceUtil.getInstance().getInt(Constants.COUNTRYCODE+ mDevUID.toUpperCase() );
			if(mDevice.installmode<0){
				mDevice.installmode = 0;
			}
			if(mDevice.hardware_pkg<0){
				mDevice.hardware_pkg = 0;
			}
			createQRImage(mDevice.UID + ":" + mDevice.viewAccount + ":"
					+ mDevice.viewPassword+ ":"+mDevice.installmode+":"+mDevice.hardware_pkg+":"+ StringUtils.getCurrentLocaltionISOCountryCodeString(mDevice.country));
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private int QR_WIDTH = 630, QR_HEIGHT = 630; //10px给边框

	public void createQRImage(String url) {
		try {
			if (url == null || "".equals(url) || url.length() < 1) {
				qr_code_iv.setVisibility(View.GONE);
				return;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);

		/*	int[] rec = bitMatrix.getEnclosingRectangle();
			int resWidth = rec[2] + 1;
			int resHeight = rec[3] + 1;
			BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
	a		resMatrix.clear();
			for (int i = 0; i < resWidth; i++) {
				for (int j = 0; j < resHeight; j++) {
					if (bitMatrix.get(i + rec[0], j + rec[1])) {
						resMatrix.set(i, j);
					}
				}
			}

			int width = resMatrix.getWidth();
			int height = resMatrix.getHeight()*/;
			// BufferedImage image = new BufferedImage(width,
			// height,BufferedImage.TYPE_INT_ARGB);
			// for (int x = 0; x < width; x++) {
			// for (int y = 0; y < height; y++) {
			// image.setRGB(x, y, resMatrix.get(x, y) == true ?
			// Color.BLACK.getRGB():Color.WHITE.getRGB());
			// }
			// }
			//
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			for (int y = 0; y < QR_WIDTH; y++) {
				for (int x = 0; x < QR_HEIGHT; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			bitmap = Bitmap .createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			bitmap = bg2WhiteBitmap(bitmap);
			qr_code_iv.setImageBitmap(bitmap);
			qr_code_iv.setVisibility(View.VISIBLE);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * 设置bitmap四周白边
	 *
	 *
	 * @return
	 */
	public  Bitmap bg2WhiteBitmap(Bitmap bitmap)
	{
		int size = bitmap.getWidth() < bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight();
		int num = 10;
		int sizebig = size + num;
		// 背图
		Bitmap newBitmap = Bitmap.createBitmap(sizebig, sizebig, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		// 生成白色的
		paint.setColor(Color.WHITE);
		canvas.drawBitmap(bitmap, num / 2, num / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
		// 画正方形的
		canvas.drawRect(0, 0, sizebig, sizebig, paint);
		return newBitmap;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case  R.id.back:
			this.finish();
			break;
		case R.id.left_ll:
			this.finish();
			break;
		case R.id.camera_saveqr_rl:
			// 修改名称
			savePhoto();
			break;

		default:
			break;
		}
	}

	private boolean saveImage(String fileName, Bitmap mBitmap) {
		Log.i("IOTCamera", "fileName:" + fileName);
		if (mBitmap != null) {
			Log.i("IOTCamera", "mBitmap!=null");
		}
		if (mBitmap.isRecycled()) {
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
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 99, fOut);
			try {
				fOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private void savePhoto() {
		if (this.mDevice != null) {
			if (isSDCardValid()) {
				File var20 = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/DCIM/Camera/");
				Log.i("IOTCamera", "Image path:" + var20.getAbsolutePath());
//				File var21 = new File(var20.getAbsolutePath() + "/"
//						+ this.mDevice.UID);
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

				String var22 = var20.getAbsoluteFile() + "/"+ BuildConfig.FLAVOR+"_"
						+ getFileNameWithTime();
				Bitmap var23 = bitmap;

				if (var23 == null) {
					Log.v("IOTCamera", "this.bitmap is  null------------------");
					// Toast.makeText(this,
					// this.getText(R.string.tips_snapshot_failed), 0)
					// .show();
				}

				if (var23 != null && this.saveImage(var22, var23)) {
					String[] var24 = new String[] { var22.toString() };
					String[] var25 = new String[] { "image/*" };


					OnScanCompletedListener var26 = new OnScanCompletedListener() {
						public void onScanCompleted(String var1, Uri var2) {
							Log.i("ExternalStorage", "Scanned " + var1 + ":");
							// var1
							// + ":");
							Log.i("ExternalStorage", "-> uri=" + var2);
							Message var5 =   handler
									.obtainMessage();
							var5.what = STS_SNAPSHOT_SAVED;
							  handler.sendMessage(var5);
						}
					};

					MediaScannerConnection.scanFile(this, var24, var25, var26);
					// var25,var26);

				} else {
					// Toast.makeText(this,
					// this.getText(R.string.tips_snapshot_failed), 0)
					// .show();
				}
			} else {
				Toast.makeText(this,
						this.getText(R.string.page8_page30_tips_no_sdcard).toString(), 0)
						.show();
			}
		}
	}

	private static boolean isSDCardValid() {
		return Environment.getExternalStorageState().equals("mounted");
	}

	private static String getFileNameWithTime() {
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
		var8.append(".jpg");
		return var8.toString();
	}

	private static final int STS_SNAPSHOT_SAVED = 198;
	private Handler handler = new Handler() {

		@SuppressLint("NewApi")
		public void handleMessage(Message var1) {
			try {

				switch (var1.what) {

				case STS_SNAPSHOT_SAVED:
					Toast.makeText(
							QrCodeShareInfoActivity.this,
							QrCodeShareInfoActivity.this
									.getText(R.string.page30_share_device_qr_hadsaved),
							0).show();
					break;

				}

				super.handleMessage(var1);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};
}
