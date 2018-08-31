package cn.ubia;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.MyCamera;
import cn.ubia.db.DatabaseManager;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.util.UbiaUtil;

import com.ubia.IOTC.AVFrame;
import com.ubia.IOTC.Camera;
import com.ubia.IOTC.IRegisterIOTCListener;
//import com.tutk.P4Pbo.R;
//import com.tutk.P2PCam264.AdvancedSettingActivity;
//import com.tutk.P2PCam264.DatabaseManager;
//import com.tutk.P2PCam264.DeviceInfo;
//import com.tutk.P2PCam264.MainActivity;
//import com.tutk.P2PCam264.MyCamera;


public class EditDeviceActivity extends BaseActivity implements
		IRegisterIOTCListener {

	// private static final int REQUEST_CODE_ADVANCED;

	private Button btnAdvanced;
	private CameraManagerment mCameraManagerment=CameraManagerment.getInstance();
	private OnClickListener btnAdvancedOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			Bundle var2 = new Bundle();
			var2.putString("dev_uuid", EditDeviceActivity.this.mDevice.UUID);
			var2.putString("dev_uid", EditDeviceActivity.this.mDevice.UID);
			Intent var3 = new Intent();
			var3.setClass(EditDeviceActivity.this,
					AdvancedSettingActivity.class);
			var3.putExtras(var2);
			EditDeviceActivity.this.startActivityForResult(var3, 0);
		}
	};
	private Button btnCancel;
	private OnClickListener btnCancelOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			EditDeviceActivity.this.quit(false);
		}
	};
	private Button btnOK;
	private OnClickListener btnOKOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			 
			EditDeviceActivity.this.quit(true);
		}
	};
	private EditText edtNickName;
	private EditText edtSecurityCode;
	private EditText edtUID;
	private MyCamera mCamera = null;
	private DeviceInfo mDevice = null;
	private boolean mIsModifyAdvancedSettingAndNeedReconnect = false;
    private final int SET_PASSWPRD_RESULT_CODE=-11;
	private void quit(boolean var1) {
		if (!var1 && !this.mIsModifyAdvancedSettingAndNeedReconnect) {
			if (this.mCamera != null) {
				this.mCamera.unregisterIOTCListener(this);
			}

			this.setResult(0, new Intent());
			this.finish();
		} else {
			 
			if (!var1 && this.mIsModifyAdvancedSettingAndNeedReconnect) {
				if (this.mCamera != null) {
					this.mCamera.setPassword(this.mDevice.viewPassword);
//					this.mCamera.stop(0);
//					this.mCamera.disconnect();
//					this.mCamera.connect(this.mDevice.UID);
//					this.mCamera.start(0, this.mDevice.viewAccount,
//							this.mDevice.viewPassword);
//					this.mCamera.sendIOCtrl(0, 816,
//							AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq
//									.parseContent());
//					this.mCamera.sendIOCtrl(0, 808,
//							AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq
//									.parseContent());
//					this.mCamera.sendIOCtrl(0, 810,
//							AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
//									.parseContent());
//					this.mCamera.sendIOCtrl(0, 928,
//							AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
				}

				this.setResult(SET_PASSWPRD_RESULT_CODE, new Intent());
				this.finish();
				return;
			}

			if (var1 && this.mIsModifyAdvancedSettingAndNeedReconnect) {
				String var7 = this.edtNickName.getText().toString();
				String var8 = this.edtUID.getText().toString();
				String var9 = this.mDevice.viewAccount;
				String var10 = this.edtSecurityCode.getText().toString();
				boolean ret[] =  UbiaUtil.validatepassword(var10.trim());
  			    ret[0]= true;
		        ret[1]= true;
		        ret[2]= true;
		        ret[3]= true;
				String toastshow = 	getHelper().getString(R.string.page25_p11_password_toast);
				if(!ret[0])
				{
					toastshow += getHelper().getString(R.string.p11_confirmpassword_toast1);
					//getHelper().showMessage(R.string.p11_password_toast);
					//return;
				}
				if(!ret[1])
				{
					toastshow += getHelper().getString(R.string.p11_confirmpassword_toast2);
					//getHelper().showMessage(R.string.p11_password_toast);
					//return;
				}
				if(!ret[2])
				{
					toastshow += getHelper().getString(R.string.p11_confirmpassword_toast3);
					//getHelper().showMessage(R.string.p11_password_toast);
					//return;
				}
				if(!ret[3])
				{
					toastshow += getHelper().getString(R.string.p11_confirmpassword_toast4);
					//getHelper().showMessage(R.string.p11_password_toast);
					//return;
				}
				if(!toastshow.equals(getHelper().getString(R.string.page25_p11_password_toast)))
				{
					 getHelper().showMessageLong(toastshow);
					 return;
				}
				if (var7.length() == 0) {
//					MainActivity.showAlert(this,
//							this.getText(R.string.tips_warning),
//							this.getText(R.string.tips_camera_name),
//							this.getText(R.string.ok));
					getHelper().showMessage(R.string.page7_tips_camera_name);
					return;
				}

				if (var8.length() == 0) {
//					MainActivity.showAlert(this,
//							this.getText(R.string.tips_warning),
//							this.getText(R.string.tips_dev_uid),
//							this.getText(R.string.ok));
					getHelper().showMessage(R.string.page7_tips_dev_uid);
					return;
				}

				if (var8.length() != 20) {
//					MainActivity.showAlert(this,
//							this.getText(R.string.tips_warning),
//							this.getText(R.string.tips_dev_uid_character),
//							this.getText(R.string.ok));
					getHelper().showMessage(R.string.page7_tips_dev_uid_character);
					return;
				}

				if (var10.length() <= 0) {
//					MainActivity.showAlert(this,
//							this.getText(R.string.tips_warning),
//							this.getText(R.string.tips_dev_security_code),
//							this.getText(R.string.ok));
					getHelper().showMessage(R.string.page7_tips_dev_security_code);
					return;
				}

				if (!var7.equalsIgnoreCase(this.mDevice.nickName)
						|| !var8.equalsIgnoreCase(this.mDevice.UID)
						|| !var9.equalsIgnoreCase(this.mDevice.viewAccount)
						|| !var10.equalsIgnoreCase(this.mDevice.viewPassword)) {
					this.mDevice.nickName = var7;
					this.mDevice.UID = var8;
					this.mDevice.viewAccount = var9;
					this.mDevice.viewPassword = var10;
					(new DatabaseManager(this)).updateDeviceInfoByDBID(
							this.mDevice.DBID, this.mDevice.UID, var7, "", "",
							var9, var10, this.mDevice.EventNotification,
							this.mDevice.getChannelIndex(),this.mDevice.isPublic);
					 
					mCameraManagerment.userIPCSetDeviceName(this.mDevice.UID, this.mDevice.nickName );
//					   IOTYPE_USER_IPCAM_SET_DEVNAME_REQ			= 0x0334,
				}

				if (this.mCamera != null) {
					this.mCamera.setPassword(var10);
//					this.mCamera.unregisterIOTCListener(this);
//					this.mCamera.stop(0);
//					this.mCamera.disconnect();
//					this.mCamera.connect(var8);
//					this.mCamera.start(0, var9, var10);
//					this.mCamera.sendIOCtrl(0, 816,
//							AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq
//									.parseContent());
//					this.mCamera.sendIOCtrl(0, 808,
//							AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq
//									.parseContent());
//					this.mCamera.sendIOCtrl(0, 810,
//							AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
//									.parseContent());
//					this.mCamera.sendIOCtrl(0, 928,
//							AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
				}

//				Toast.makeText(this, this.getText(R.string.tips_edit_camera_ok).toString(), 0)
//						.show();
				this.setResult(SET_PASSWPRD_RESULT_CODE, new Intent());
				this.finish();
				return;
			}

			if (var1 && !this.mIsModifyAdvancedSettingAndNeedReconnect) {
				String var2 = this.edtNickName.getText().toString();
				String var3 = this.edtUID.getText().toString();
				String var4 = this.mDevice.viewAccount;
				String var5 = this.edtSecurityCode.getText().toString();
				boolean ret[] =  UbiaUtil.validatepassword(var5.trim());
				    ret[0]= true;
			        ret[1]= true;
			        ret[2]= true;
			        ret[3]= true;
				String toastshow = 	getHelper().getString(R.string.page25_p11_password_toast);
				if(!ret[0])
				{
					toastshow += getHelper().getString(R.string.p11_confirmpassword_toast1);
					//getHelper().showMessage(R.string.p11_password_toast);
					//return;
				}
				if(!ret[1])
				{
					toastshow += getHelper().getString(R.string.p11_confirmpassword_toast2);
					//getHelper().showMessage(R.string.p11_password_toast);
					//return;
				}
				if(!ret[2])
				{
					toastshow += getHelper().getString(R.string.p11_confirmpassword_toast3);
					//getHelper().showMessage(R.string.p11_password_toast);
					//return;
				}
				if(!ret[3])
				{
					toastshow += getHelper().getString(R.string.p11_confirmpassword_toast4);
					//getHelper().showMessage(R.string.p11_password_toast);
					//return;
				}
				if(!toastshow.equals(getHelper().getString(R.string.page25_p11_password_toast)))
				{
					 getHelper().showMessageLong(toastshow);
					 return;
				}
				if (var2.length() == 0) {
//					MainActivity.showAlert(this, this.getText(R.string.tips_warning),
//							this.getText(R.string.tips_camera_name), this.getText(R.string.ok));
					getHelper().showMessage(R.string.page7_tips_camera_name);
					return;
				}

				if (var3.length() == 0) {
//					MainActivity.showAlert(this, this.getText(R.string.tips_warning),
//							this.getText(R.string.tips_dev_uid), this.getText(R.string.ok));
					getHelper().showMessage(R.string.page7_tips_dev_uid);
					return;
				}

				if (var3.length() != 20) {
//					MainActivity.showAlert(this, this.getText(R.string.tips_warning),
//							this.getText(R.string.tips_dev_uid_character), this.getText(R.string.ok));
					getHelper().showMessage(R.string.page7_tips_dev_uid_character);
					return;
				}

				if (var5.length() <= 0) {
//					MainActivity.showAlert(this, this.getText(R.string.tips_warning),
//							this.getText(R.string.tips_dev_security_code), this.getText(R.string.ok));
					getHelper().showMessage(R.string.page7_tips_dev_security_code);
					return;
				}

				if (this.mCamera != null
						&& (!var3.equalsIgnoreCase(this.mDevice.UID)
								|| !var4.equalsIgnoreCase(this.mDevice.viewAccount) || !var5
									.equalsIgnoreCase(this.mDevice.viewPassword))) {
					this.mCamera.setPassword(var5);
					this.mCamera.unregisterIOTCListener(this);
					this.mCamera.stop(0);
					this.mCamera.disconnect();
					this.mCamera.connect(var3);
 					this.mCamera.start(0, var4, var5);
 					mCameraManagerment.userIPCDeviceInfo(mCamera.getmUID());
					
 					mCameraManagerment.userIPCGetSupportStream(mCamera.getmUID());
					
 					mCameraManagerment.userIPCGetAudioOutFormat(mCamera.getmUID());
					
//					this.mCamera.sendIOCtrl(0, 928,
//							AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
				}

				if (!var2.equalsIgnoreCase(this.mDevice.nickName)
						|| !var3.equalsIgnoreCase(this.mDevice.UID)
						|| !var4.equalsIgnoreCase(this.mDevice.viewAccount)
						|| !var5.equalsIgnoreCase(this.mDevice.viewPassword)) {
					if (!var5.equalsIgnoreCase(this.mDevice.viewPassword)) {
						this.mDevice.ChangePassword = true;
					}

					this.mDevice.nickName = var2;
					this.mDevice.UID = var3;
					this.mDevice.viewAccount = var4;
					this.mDevice.viewPassword = var5;
					(new DatabaseManager(this)).updateDeviceInfoByDBID(
							this.mDevice.DBID, this.mDevice.UID, var2, "", "",
							var4, var5, this.mDevice.EventNotification,
							this.mDevice.getChannelIndex(),this.mDevice.isPublic);
					mCameraManagerment.userIPCSetDeviceName(this.mDevice.UID, this.mDevice.nickName );
				}

				this.setResult(SET_PASSWPRD_RESULT_CODE, new Intent());
				this.finish();
				return;
			}
		}

	} 
	protected void onActivityResult(int var1, int var2, Intent var3) {
		super.onActivityResult(var1, var2, var3);
		if (var1 == 0) {
			Bundle var4 = var3.getExtras();
			switch (var2) {
			case -1:
				this.mIsModifyAdvancedSettingAndNeedReconnect = var4
						.getBoolean("need_reconnect");
				boolean var5 = var4.getBoolean("change_password");
				String var6 = var4.getString("new_password");
				if (var5) {
					this.edtSecurityCode.setText(var6);
		
				}
			default:
				if (this.mCamera != null) {
					this.btnAdvanced.setEnabled(this.mCamera
							.isChannelConnected(0));
				}
			}
		}

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuItem item = menu.add(0, MENU_SAVE, 0, "");
		// item.setIcon(R.drawable.ic_menu_save);
		// item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}
	private ImageView back;
	private TextView title;
	private ImageView title_img;
	protected void onCreate(Bundle var1) {
		super.onCreate(var1);
		this.setTitle(this.getText(R.string.page25_Selectcamera));
		this.setContentView(R.layout.edit_device);
		getActionBar().hide();
		Bundle var2 = this.getIntent().getExtras();
		String var3 = var2.getString("dev_uuid");
		String mDevUID = var2.getString("dev_uid");


		this.mCamera =  CameraManagerment.getInstance().getexistCamera(mDevUID);

			//if (var3.equalsIgnoreCase(var8.getUUID())
		if(mCamera!=null)
				this.mCamera.registerIOTCListener(this);
			//		&& var4.equalsIgnoreCase(var8.getUID())) {
		this.mDevice  = MainCameraFragment.getexistDevice(mDevUID);

		this.edtUID = (EditText) this.findViewById(R.id.edtUID);
		this.edtSecurityCode = (EditText) this.findViewById(R.id.edtSecurityCode);
		this.edtNickName = (EditText) this.findViewById(R.id.edtNickName);
		this.btnOK = (Button) this.findViewById(R.id.btnOK);
		this.btnCancel = (Button) this.findViewById(R.id.btnCancel);
		this.btnAdvanced = (Button) this.findViewById(R.id.btnAdvanced);
		Button var10 = this.btnAdvanced;
		boolean var11;
		if (this.mCamera != null && this.mCamera.isChannelConnected(0)) {
			var11 = true;
		} else {
			var11 = false;
		}
		
		back = (ImageView) this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		title = (TextView) this.findViewById(R.id.title);
		title.setText(getString(R.string.page25_camera_settings));
		this.findViewById(R.id.left_ll).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						EditDeviceActivity.this.finish();
					}
				});
		title_img = (ImageView) findViewById(R.id.title_img);
		title_img.setImageResource(R.drawable.setting_off);
		var10.setEnabled(var11);
		this.edtUID.setText(mDevUID);
		this.edtUID.setEnabled(false);
		
		if(this.mDevice != null){
			this.edtSecurityCode.setText(this.mDevice.viewPassword);
			this.edtNickName.setText(this.mDevice.nickName);
		}
		
		this.btnOK.setOnClickListener(this.btnOKOnClickListener);
		this.btnCancel.setOnClickListener(this.btnCancelOnClickListener);
		this.btnAdvanced.setOnClickListener(this.btnAdvancedOnClickListener);
		this.getWindow().setSoftInputMode(3);
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	public boolean onKeyDown(int var1, KeyEvent var2) {
		switch (var1) {
		case 4:
			this.quit(false);
		default:
			return super.onKeyDown(var1, var2);
		}
	}

	protected void onStart() {
		super.onStart();
	}

	protected void onStop() {
		super.onStop();
	}

	public void receiveChannelInfo(Camera var1, int var2, int var3) {
		if (this.mCamera == var1 && var3 == 2) {
			this.btnAdvanced.setEnabled(true);
		}

	}

	public void receiveFrameData(Camera var1, int var2, Bitmap var3) {
	}

	public void receiveFrameInfo(Camera var1, int var2, long var3, int var5,
								 int var6, AVFrame avFrame , int var8) {
	}

	public void receiveIOCtrlData(Camera var1, int var2, int var3, byte[] var4) {
	}

	public void receiveSessionInfo(Camera var1, int var2) {
	}
}
