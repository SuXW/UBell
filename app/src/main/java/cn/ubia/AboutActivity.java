package cn.ubia;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
public class AboutActivity extends BaseActivity {
	boolean ishardDecode;
	private TextView title;
	private ImageView back;
	private ImageView 	hardDecodeCheck;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.about);
		// TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
		TextView var5 = (TextView) findViewById(R.id.txtVersion);
		TextView var6 = (TextView) findViewById(R.id.txtIOTCAPIs);
		TextView var7 = (TextView) findViewById(R.id.txtAVAPIs);
		this.getActionBar().hide();
		title = (TextView) this.findViewById(R.id.title);
		title.setText(this.getText( R.string.about));
		setTitle(R.string.about);
		// var5.setText(var3);
		var6.setText(this.getIOTCAPis());
		var7.setText(this.getAVAPis());
		var5.setText(getVersionName());
		initView();
	}

	private void initView() {
		back = (ImageView) this.findViewById(R.id.back);
		hardDecodeCheck= (ImageView) this.findViewById(R.id.hardDecodeCheck);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						//AboutActivity.this.quit(-1);
						AboutActivity.this.finish();
						System.gc();
					}
				});
		back.setVisibility(View.VISIBLE);
		title = (TextView) this.findViewById(R.id.title);
		this.findViewById(R.id.left_ll).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						//AboutActivity.this.quit(-1);
						AboutActivity.this.finish();
						System.gc();
					}
				});
		ishardDecode  =   false ;
	 	if(ishardDecode){
	 		hardDecodeCheck.setImageResource(R.drawable.checkbox_on);
	 	}else{
	 		hardDecodeCheck.setImageResource(R.drawable.checkbox_off);
	 	}
			
				// TODO Auto-generated method stub
 
		
		//title_img = (ImageView) findViewById(R.id.title_img);
		//title_img.setImageResource(R.drawable.nty_app_icon);
	}
	private String getVersionName() {
		//获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		//getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		String version = "";
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "v" + version;
	}

	private String getAVAPis() {
		byte[] var1 = new byte[4];
		int var2 = com.ubia.IOTC.AVAPIs.UBIC_avGetAVApiVer();
		StringBuffer var3 = new StringBuffer();
		var1[3] = (byte) var2;
		var1[2] = (byte) (var2 >>> 8);
		var1[1] = (byte) (var2 >>> 16);
		var1[0] = (byte) (var2 >>> 24);
		var3.append(255 & var1[0]);
		var3.append('.');
		var3.append(255 & var1[1]);
		var3.append('.');
		var3.append(255 & var1[2]);
		var3.append('.');
		var3.append(255 & var1[3]);
		return var3.toString();
	}

	private String getIOTCAPis() {
		byte[] var1 = new byte[4];
		long[] var2 = new long[1];
		com.ubia.IOTC.IOTCAPIs.UBIC_Get_Version(var2);
		int var3 = (int) var2[0];
		StringBuffer var4 = new StringBuffer();
		var1[3] = (byte) var3;
		var1[2] = (byte) (var3 >>> 8);
		var1[1] = (byte) (var3 >>> 16);
		var1[0] = (byte) (var3 >>> 24);
		var4.append(255 & var1[0]);
		var4.append('.');
		var4.append(255 & var1[1]);
		var4.append('.');
		var4.append(255 & var1[2]);
		var4.append('.');
		var4.append(255 & var1[3]);
		return var4.toString();
	}
}
