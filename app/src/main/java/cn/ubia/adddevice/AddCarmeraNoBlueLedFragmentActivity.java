package cn.ubia.adddevice;
 

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;

public class AddCarmeraNoBlueLedFragmentActivity extends BaseActivity implements
		OnClickListener {
	private ImageView back;
	private TextView title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.fragment_playbackactivity_menu_title);
		setContentView(R.layout.setup_camera_config_incorret);
		getActionBar().hide();
//		findViewById(R.id.btnSetup).setOnClickListener(this);
//		findViewById(R.id.btnConnect).setOnClickListener(this);
		back = (ImageView) this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setOnClickListener(this);
		back.setVisibility(View.VISIBLE);
		this.findViewById(R.id.left_ll).setOnClickListener(this);
		title = (TextView) this.findViewById(R.id.title);
		title.setText(getString(R.string.resetdoorbell));
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.left_ll:
				this.finish();
				break;
			case R.id.back:
				this.finish();
				break;
			case R.id.rlfast_search:
			case R.id.btnSetup:
	 
	
			 intent = new Intent();
			intent.setClass(this , AddCarmeraFragmentYesOrNoActivity.class);
			intent.putExtra("selectUID", "");
		 
			startActivityForResult(intent, 556);
			break;

	 
			 
		case R.id.rllan_serach:
			intent = new Intent();
			intent.setClass(this, AddDeviceActivity.class);
			this.startActivityForResult(intent, 0);
			break;
		default:
			break;
		}
	}
	
	public void onActivityResult(int var1, int var2, Intent var3) {
		super.onActivityResult(var1, var2, var3);
		Log.i("wifi", "AddCameraFragmentActivity onActivityResult:" + var1 + "," + var2);
	 
			switch (var2) {
			case -1:
//				Builder SettingCameraBuilder = new Builder(
//						AddCarmeraFragmentActivity.this);
//				SettingCameraBuilder.setMessage(getText(R.string.edit));
//				SettingCameraBuilder.setPositiveButton(
//						getApplicationContext().getText(R.string.edit),
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface var1,
//									int var2) {
//								var1.dismiss(); 
//								AddCarmeraFragmentActivity.this.finish();
//								AddCarmeraFragmentActivity.this.setResult(-22, null);
//							 
//
//							}
//						});
//
//				SettingCameraBuilder
//						.setNegativeButton(
//								getApplicationContext().getText(
//										R.string.btnCancel),
//								new DialogInterface.OnClickListener() {
//									public void onClick(
//											DialogInterface var1, int var2) {
//										var1.dismiss();
//										AddCarmeraFragmentActivity.this.finish();
//									}
//								});
//				SettingCameraBuilder.create().show();
				AddCarmeraNoBlueLedFragmentActivity.this.finish();

				return;
		 
	 
			default:
				return;
			}
	 

	}
}
