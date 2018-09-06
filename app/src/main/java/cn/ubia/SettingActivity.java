package cn.ubia;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.ubia.UBell.R;
import cn.ubia.adddevice.QrCodeShareInfoActivity;
import cn.ubia.base.BaseActivity;
import cn.ubia.base.Constants;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.MyCamera;
import cn.ubia.db.DatabaseManager;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.interfaceManager.DeviceStateCallbackInterface;
import cn.ubia.interfaceManager.DeviceStateCallbackInterface_Manager;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.manager.NotificationTagManager;
import cn.ubia.util.PreferenceUtil;
import cn.ubia.util.SharedPreferencesUtil;
import cn.ubia.util.ShowDialogCallback;
import cn.ubia.util.StringUtils;
import cn.ubia.util.UbiaUtil;
import cn.ubia.widget.DialogUtil;
import cn.ubia.widget.DialogUtil.DialogChooseItemcallback;
import cn.ubia.widget.DialogUtil.Dialogcallback;
import cn.ubia.widget.MyProgressBar;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ubia.IOTC.AVFrame;
import com.ubia.IOTC.AVIOCTRLDEFs;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlFirmwareUpdateReq;
import com.ubia.IOTC.Camera;
import com.ubia.IOTC.HARDWAEW_PKG;
import com.ubia.IOTC.IRegisterIOTCListener;
import com.ubia.IOTC.Packet;
import com.ubia.http.HttpClient;

import static android.view.View.GONE;
import static cn.ubia.fragment.MainCameraFragment.CONNSTATUS_RECONNECTION;
import static cn.ubia.fragment.MainCameraFragment.mainCameraFragment;
import static com.ubia.IOTC.AVIOCTRLDEFs.UBIA_IO_CAPTURE_PICTURE_SetReq.parseContent;

public class SettingActivity extends BaseActivity implements
		IRegisterIOTCListener, OnClickListener {
	//自动获取时区
	private int Customtimezone;
	private boolean isAutoSelect = true;
	private static final int CHECK_STATUS = 1;
	private static boolean isModifyPassword = false;
	private static boolean isModifyWiFi = false;
	private static List m_wifiList = new ArrayList();
	private static String newPassword;
	private SMsgAVIoctrlFirmwareUpdateReq UpdateReq ;
	public int micvalue = 0;
	public int spvalue = 0;
	public int versionloacl = 0;
	public boolean pirvalue = false;
	public boolean irvalue = false;
	public int  led = 1;


	private boolean httpoperate = false;
	private ToggleButton toggleBtnDST;
	private boolean isFromUser = false;//修复在没修改密码的情况下也会提示密码修改成功的问题

	private CameraManagerment mCameraManagerment=CameraManagerment.getInstance();
	private OnClickListener btnCancelOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			SettingActivity.this.quit(false);
		}
	};
	private Button btnFormatSDCard;

	private RelativeLayout setting_sd_rl;
	private RelativeLayout setting_env_rl;
	private RelativeLayout setting_ir_rl;
	private RelativeLayout setting_dt_rl;
	private RelativeLayout setting_led_rl;
	private RelativeLayout setting_md_rl;
	private RelativeLayout setting_alarm_rl;

	private RelativeLayout setting_record_rl;
	private RelativeLayout setting_power_rl,setting_timezone_rl,setting_dst_rl,setting_asytimezone_rl;

	private RelativeLayout setting_namepwd_rl;
	private RelativeLayout setting_shareqr_rl;
	private RelativeLayout setting_checkversion_rl;
	private RelativeLayout setting_offline_qr_rl;
	private RelativeLayout setting_offline_editName_rl;
	private RelativeLayout 	setting_call_rl;
	private RelativeLayout 	 setting_flip_rl;
	private TextView setting_sd_tv;
	private TextView setting_env_tv;
	private TextView setting_md_tv;
	private TextView setting_alarm_tv;
	private TextView setting_ir_tv;
	private TextView setting_dormancy_tv;
	private TextView setting_led_tv;
	private TextView setting_record_tv;
	private TextView setting_power_tv;
	private TextView setting_timezone_tv;
	private TextView setting_flip_tv;
	private TextView setting_devicename_tv;
	private TextView setting_deviceid_tv;
	private TextView setting_devicemode_tv;
	private TextView setting_deviceversion_tv;
	private TextView setting_deviceproducts_tv;
	private TextView setting_dst_tv;
	private int providerCloud = -1;
	private boolean isSetPasswordWithCountry = false;
	private RelativeLayout findRlview(int id){
		findViewById( id).setOnClickListener(this);
		return (RelativeLayout) findViewById( id);
	}
	private TextView findTvview(int id){
		return (TextView) findViewById( id);
	}

	private void initview() {
		setting_sd_rl = findRlview(R.id.setting_sd_rl);
		setting_env_rl = findRlview(R.id.setting_env_rl);
		setting_md_rl= findRlview(R.id.setting_md_rl);
		setting_alarm_rl = findRlview(R.id.setting_alarm_rl);
		setting_ir_rl = findRlview(R.id.setting_ir_rl);
		setting_dt_rl = findRlview(R.id.setting_dt_rl);
		setting_led_rl= findRlview(R.id.setting_led_rl);
		setting_record_rl = findRlview(R.id.setting_record_rl);
		setting_power_rl = findRlview(R.id.setting_power_rl);
		setting_asytimezone_rl= findRlview(R.id.setting_asytimezone_rl);
		setting_timezone_rl= findRlview(R.id.setting_timezone_rl);
		setting_dst_rl =   findRlview(R.id.setting_dst_rl);
		setting_namepwd_rl = findRlview(R.id.setting_namepwd_rl);
		setting_shareqr_rl = findRlview(R.id.setting_shareqr_rl);
		setting_checkversion_rl = findRlview(R.id.setting_checkversion_rl);
		setting_offline_qr_rl = findRlview(R.id.setting_offline_qr_rl);
		setting_offline_editName_rl = findRlview(R.id.setting_offline_editName_rl);
		setting_flip_rl= findRlview(R.id.setting_flip_rl);
		setting_call_rl = findRlview(R.id.setting_call_rl);
		setting_sd_tv = findTvview(R.id.setting_sd_tv);
		setting_env_tv = findTvview(R.id.setting_env_tv);
		setting_md_tv = findTvview(R.id.setting_md_tv);
		setting_alarm_tv = findTvview(R.id.setting_alarm_tv);
		setting_ir_tv = findTvview(R.id.setting_ir_tv);
		setting_dormancy_tv = findTvview(R.id.setting_dt_tv);
		setting_led_tv= findTvview(R.id.setting_led_tv);
		setting_record_tv = findTvview(R.id.setting_record_tv);
		setting_power_tv = findTvview(R.id.setting_power_tv);
		setting_timezone_tv = findTvview(R.id.setting_timezone_tv);
		setting_flip_tv= findTvview(R.id.setting_flip_tv);
		setting_devicename_tv = findTvview(R.id.setting_devicename_tv);
		setting_deviceid_tv = findTvview(R.id.setting_deviceid_tv);
		setting_devicemode_tv = findTvview(R.id.setting_devicemode_tv);
		setting_deviceversion_tv = findTvview(R.id.setting_deviceversion_tv);
		setting_deviceproducts_tv   = findTvview(R.id.setting_deviceproducts_tv);
		setting_dst_tv   = findTvview(R.id.setting_dst_tv);
		findViewById(R.id.btnRemoveCamera).setOnClickListener(this);//btnRemoveCamera
		if(fromMain==1){
			findViewById(R.id.deviceoffline_scrollview).	setVisibility(View.VISIBLE);
			findViewById(R.id.deviceonline_scrollview).	setVisibility(GONE);
		}else{
			findViewById(R.id.deviceoffline_scrollview).	setVisibility(GONE);
			findViewById(R.id.deviceonline_scrollview).	setVisibility(View.VISIBLE);
		}

		if(mDevice.hardware_pkg == HARDWAEW_PKG.SREE_VR_LINUX_PIR_LED){
			setting_md_rl.setVisibility(View.VISIBLE);
			setting_alarm_rl.setVisibility(View.VISIBLE);
			setting_env_rl.setVisibility(View.GONE);
			setting_power_rl.setVisibility(View.GONE);
			setting_record_rl.setVisibility(View.VISIBLE);
		}


	}




	private OnClickListener btnFormatSDCardListener = new OnClickListener() {
		public void onClick(View var1) {
			(new Builder(SettingActivity.this))
					.setIcon(17301543)
					.setTitle(
							SettingActivity.this
									.getText(R.string.page5_tips_warning))
					.setMessage(
							SettingActivity.this
									.getText(R.string.page10_tips_format_sdcard_confirm))
					.setPositiveButton(
							SettingActivity.this.getText(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface var1,
													int var2) {
									SettingActivity.this.mCamera
											.sendIOCtrl(
													0,
													896,
													AVIOCTRLDEFs.SMsgAVIoctrlFormatExtStorageReq
															.parseContent(0));
								}
							})
					.setNegativeButton(
							SettingActivity.this
									.getText(R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface var1,
													int var2) {
								}
							}).show();
		}
	};


	private boolean changeStatus = false;
	protected int TimeZone;
	protected int enableDST;//夏令时
	protected int TimeZoneEnable;
	private boolean isConnectSuccess = false;
	private boolean isDownloadComplete = false;
	private Handler handler = new Handler() {
		public void handleMessage(Message var1) {
			byte[] var2 = var1.getData().getByteArray("data");
			Log.e("SettingsActivity","var1.what="+var1.what+",var2="+var2);
			int var28;
			switch (var1.what) {

				case  AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FIRMWARE_UPDATE_CHECK_RSP:
					mProgressBar.dismiss();
					if(var2.length<8){
						Toast.makeText(SettingActivity.this,SettingActivity.this.getText(
								R.string.page10_failed_to_update_device)
								.toString(), Toast.LENGTH_LONG).show();
						return;
					}else{

						if(!isDownloadComplete) {  //进度到了100后，仍然会继续收到进度，判断是否下载完成

							String tmpstring = SettingActivity.this.getText(R.string.page10_u_firmware_updating).toString();
							progressDialog.setMessage(tmpstring + Packet.byteArrayToInt_Little(var2, 4) + "%");

							Log.v("lastversion", "AVIOCTRLDEFs Updating   " + Packet.byteArrayToInt_Little(var2, 4));

							if (Packet.byteArrayToInt_Little(var2, 4) >= 100) { //下载完成

								if (progressDialog.isShowing()) {
									//	progressDialog.dismiss();



									final CameraManagerment mCameraManagerment = CameraManagerment.getInstance();


									DeviceStateCallbackInterface_Manager.getInstance().setmCallback(new DeviceStateCallbackInterface() {

										@Override
										public void DeviceStateCallbackInterface(String did, int type, int param) {
											if (did.equals(mDevice.UID)) {
												if (param == mainCameraFragment.CONNSTATUS_CONNECTED) {

													waitUpdateHandler.sendEmptyMessage(1);//升级成功提示
													Log.e("SettingsActivry...guo", "连接成功、、、、、、");
													initDeviceInfo();//重新获取参数
													isConnectSuccess = true;

												}
												if (param == mainCameraFragment.CONNSTATUS_CONNECTION_FAILED) {
													if (!isConnectSuccess) {
														Log.e("SettingsActivry...guo", "连接失败，重连、、、、、、");
														mCameraManagerment.StopPPPP(did);
														mCameraManagerment.StartPPPP(
																mDevice.UID, mDevice.viewAccount, mDevice.viewPassword); //收到失败后，再次连接
													}

												}

											}
										}

										@Override
										public void DeviceStateCallbackLiveInterface(String did, int type, int param) {

										}
									});

									/*	Toast.makeText(
												SettingActivity.this,SettingActivity.this.getText(
														R.string.page10_u_firmware_update_toast)
														.toString(), Toast.LENGTH_LONG).show();*/


									//升级进度
									waitUpdateHandler.sendEmptyMessageDelayed(0, 1000);

									mCameraManagerment.StartPPPP(mDevice.UID, mDevice.viewAccount, mDevice.viewPassword);
									isDownloadComplete = true;


								}
							}
						}
					}

					break;
				case  AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FIRMWARE_UPDATE_RSP:
					mProgressBar.dismiss();
					if (var2.length < 4) {
						Toast.makeText(
								SettingActivity.this,
								SettingActivity.this.getText(
										R.string.page10_failed_to_update_device)
										.toString(), Toast.LENGTH_LONG).show();
						return;
					} else {
						showDialog();
						//Toast.makeText(
						//		AdvancedSettingActivity.this,"Upgrating ...", Toast.LENGTH_LONG).show();
						Log.v("lastversion", "AVIOCTRLDEFs IOTYPE_USER_IPCAM_FIRMWARE_UPDATE_RSP=   " + Packet.byteArrayToInt_Little(var2, 0));
					}
					break;
				case 34:
					Log.v("lastversion","versionz="+versionz);
					Log.v("lastversion","versionloacl="+versionloacl);

					if(  versionz > versionloacl)
					{
						Log.v("lastversion","versionz >versionloacl"  );

					}
					else
					{
						Log.v("lastversion","versionz < versionloacl"  );
					}
					break;

				case 821:
					if (var2[0] == 0) {
						Toast.makeText(
								SettingActivity.this,
								SettingActivity.this.getText(
										R.string.page10_success_to_update_device_name)
										.toString(), 0).show();
						setting_devicename_tv.setText(""+mDevice.nickName);
					}
					break;
				case 805:
					var28 = Packet.byteArrayToInt_Little(var2, 0);
					Log.i("deviceinfo", "805..............="+var28);
					break;

				case	AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_OSD_RESP:
					var28 = Packet.byteArrayToInt_Little(var2, 4);


					break;
				case	AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETPIR_RESP:
					// mProgressBar.dismiss();
					var28 = Packet.byteArrayToInt_Little(var2, 4);

					pirvalue =true;
					Log.i("deviceinfo", "IOTYPE_USER_IPCAM_GETPIR_RESP..............="+var28);

				case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_OSD_RESP:
					// mProgressBar.dismiss();
					var28 = var2[9];

					if(var28<=1&&var28>=0)
					{
						osdvalue =true;
					}
					Log.i("deviceinfo",
							"IOTYPE_USER_IPCAM_GET_OSD_RESP.........ahahah.....=" + var28);

					break;

				case 785:
					var28 = Packet.byteArrayToInt_Little(var2, 0);
					Log.i("deviceinfo", " 录像设置模式..............=" + var28);
					break;

				case	AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPIR_RESP:
					// mProgressBar.dismiss();
					var28 = Packet.byteArrayToInt_Little(var2, 4);
					pirvalue =true;
					Log.i("deviceinfo", "IOTYPE_USER_IPCAM_GETPIR_RESP..............="+var28);

					break;

				case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETVOLUME_RESP:
					mProgressBar.dismiss();
					var28 = Packet.byteArrayToInt_Little(var2, 4);
					Log.i("deviceinfo", "IOTYPE_USER_IPCAM_GETVOLUME_RESP..............="+var28);
					if(var28>255||var28<0)break;
					spvalue = var28;


					break;

				case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETMICVOLUME_RESP:
					mProgressBar.dismiss();
					var28 = Packet.byteArrayToInt_Little(var2, 4);
					Log.i("deviceinfo", "IOTYPE_USER_IPCAM_GETVOLUME_RESP..............="+var28);
					if(var28>255||var28<0)break;
					micvalue = var28;


					break;

				case 787:

					break;
				case 803:

					break;
				case 807:

					break;


				case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_ADVANCESETTINGS_RESP:
				{
					mProgressBar.dismiss();
					Log.i("deviceinfo", "got IOTYPE_USER_IPCAM_GET_ADVANCESETTINGS_RESP 961..............");
					versionloacl = Packet.byteArrayToInt_Little(var2, 32);
					startUrlCheck(mDevice.UID) ;
					char adv_valid = (char) var2[48] ;
					if(adv_valid==0x7E){
						int videoquality = (int) var2[49];
						int videoflips= (int) var2[50];
						int envmode= (int) var2[51];
						int motionsensitivity= (int) var2[52];
						int alarmmode= (int) var2[53];
						int recordmode= (int) var2[54];
						int audioCodec= (int) var2[55];
						pirsetting= (int) var2[56];
						acPowerFreq= (int) var2[75];
						country = (int) (var2[76]&0xff);
						providerCloud= (int) (var2[77]&0xff);
						led = (int) (var2[79]&0xff);
						dormancytime =  (int) (var2[80]&0xff);

						int localCountry = 	PreferenceUtil.getInstance().getInt(Constants.COUNTRYCODE+mDevice.UID.toUpperCase() );

						Log.e("SettingActivity..guo","led="+led+",dormancytime="+dormancytime);


						if(country!=localCountry){

							//PreferenceUtil.getInstance().putInt(Constants.MESSAGETYPE_CHECK+SettingActivity.this.mCamera.getmUID(), 0);

							if (SettingActivity.this.mCamera != null) {
								if(country>=0  && localCountry>0){
									if (SettingActivity.this.mCamera != null) {
										mCameraManagerment.userIPCSetPassWordwithCountry(SettingActivity.this.mCamera.getmDevUID(),
												mDevice.viewPassword, mDevice.viewPassword,mDevice.nickName,StringUtils.getCurrentLocaltionISOCountryCodeString(localCountry));
									}

								}
							}

						}else{
							PreferenceUtil.getInstance().putInt(Constants.COUNTRYCODE+mDevice.UID.toUpperCase() ,country);
							mDevice.country = country;
						}

						Log.v("deviceinfo","    videoquality=  "+videoquality+"     videoflips= "+videoflips+"     envmode= "+envmode+"      motionsensitivity="+motionsensitivity+
								"      alarmmode="+alarmmode+"     recordmode= "+recordmode+"      audioCodec="+audioCodec
								+"      pirsetting:"+pirsetting +"    acPowerFreq:"+acPowerFreq+"   country:"+country
								+"");


						if (videoquality >= 0 && videoquality <= 5) {
							SettingActivity.this.mVideoQuality = videoquality;
						}

						Log.i("deviceinfo", "录像模式........00......=" + recordmode);

						Log.i("deviceinfo", "移动侦测录像获取模式........11......=" + motionsensitivity);
						if (motionsensitivity == 0) {
							SettingActivity.this.mMotionDetection = 0;
							setting_md_tv.setText(R.string.page10_u_off);
						}else{
							SettingActivity.this.mMotionDetection = 1;
							setting_md_tv.setText(R.string.page10_u_on);
						}
						if (alarmmode == 0) {
							mAlarmMode = 0;
							setting_alarm_tv.setText(R.string.page10_u_off);
						}else{
							mAlarmMode = 1;
							setting_alarm_tv.setText(R.string.page10_u_on);
						}



						if (videoflips >= 0 && videoflips <= 3) {
							SettingActivity.this.mVideoFlip = videoflips;
						}
						recordvalue = true;

						byte[] deviceinfovar21 = new byte[16];
						byte[] deviceinfovar22 = new byte[16];
						System.arraycopy(var2, 0, deviceinfovar21, 0, 16);
						System.arraycopy(var2, 16, deviceinfovar22, 0, 16);
						String deviceinfovar23 = SettingActivity.getString(deviceinfovar21);
						String deviceinfovar24 = SettingActivity.getString(deviceinfovar22);
						int deviceinfovar25 = Packet.byteArrayToInt_Little(var2, 32);
						int deviceinfovar26 = Packet.byteArrayToInt_Little(var2, 44);
						SettingActivity.this.mTotalSize = Packet.byteArrayToInt_Little(var2, 40);
						SettingActivity.this.mfree = Packet.byteArrayToInt_Little(var2, 44);
						Log.i("deviceinfo", "got IOTYPE_USER_IPCAM_GET_ADVANCESETTINGS_RESP 961.............+String.valueOf(deviceinfovar26)."+String.valueOf(deviceinfovar26)+"AdvancedSettingActivity.this.mTotalSize="+SettingActivity.this.mTotalSize);

						int data = Packet.byteArrayToInt_Little(var2, 25*4);

						TimeZone = (int)var2[69];
						Log.v("test","TimeZone = "+TimeZone);

						//取值（夏令时）
						enableDST=var2[73];

						int osdenable = (int)var2[70];
						if(osdenable<=1 && osdenable>=0)
						{
							osdvalue =true;
						}

						if (pirsetting >=0  && pirsetting <=5  ) {
							SettingActivity.this.pirsetting = pirsetting;

						}



						SharedPreferences.Editor editor = SettingActivity.this.getSharedPreferences("isCloudSave", Context.MODE_PRIVATE).edit();
						editor.putBoolean(mCamera.getmDevUID(), providerCloud==1);
						editor.commit();
						Boolean isCloudSave = (providerCloud==1);
						if(isCloudSave){
							setting_sd_tv.setText(""+getString(R.string.cloud_save_tip));
						}else if(mTotalSize / 1024 > 128 || mTotalSize < 0){
							setting_sd_tv.setText(R.string.setting_sdcord_noformat);
						}else{
							float f1 = (float)mTotalSize/(float)1024;
							setting_sd_tv.setText(String.format("%.2f", f1)+" GB");
						}
						if (envmode >=0  && envmode <=5) {
							if(envmode==5){
								envmode=4;
							}
							SettingActivity.this.mEnvMode = envmode;
							String[] items = getResources().getStringArray(R.array.environment_mode);
							setting_env_tv.setText(""+items[envmode]);
						}
						if (pirsetting >=0  && pirsetting <=3  ) {

							String[] items = getResources().getStringArray(R.array.pirmode_quality);
							setting_ir_tv.setText(""+items[pirsetting]);
							if(mDevice.hardware_pkg == HARDWAEW_PKG.SREE_VR_LINUX_PIR_LED){
								items =  getResources().getStringArray(R.array.motion_detection);
								setting_ir_tv.setText(""+items[pirsetting]);
							}

						}

						if(dormancytime>0){
							String[] items = getResources().getStringArray(R.array.dormancytime);

							switch (dormancytime){
								case 15:
									setting_dormancy_tv.setText(""+items[0]);
									break;
								case 30:
									setting_dormancy_tv.setText(""+items[1]);
									break;
								case 60:
									setting_dormancy_tv.setText(""+items[2]);
									break;
								case 255:
									setting_dormancy_tv.setText(""+items[3]);

									break;
								default:
									setting_dormancy_tv.setText("");
									break;
							}
						}

						if(led >= 0 &&led < 2){

							led = led == 0 ? 1:0;

							String[] items = getResources().getStringArray(R.array.motion_detection);
							setting_led_tv.setText(""+items[led]);
						}

						if ((TimeZone+12 )>=0  && (TimeZone+12) < 25  ) {
							String[] items = getResources().getStringArray(R.array.time_zone_name);
							setting_timezone_tv.setText(""+items[TimeZone+12]);
						}
						if (recordmode >= 0 && recordmode <= 2) {
							SettingActivity.this.mRecordType = recordmode;
							String[] items = getResources().getStringArray(R.array.recording_mode);
							setting_record_tv.setText(""+items[mRecordType]);
						}
						{
							String[] items = getResources().getStringArray(R.array.recording_mode);
							setting_dst_tv.setText(""+items[enableDST]);
						}
						if (acPowerFreq >=0  && acPowerFreq <=1   ) {

							String[] items = getResources().getStringArray(R.array.powerfreq);
							setting_power_tv.setText(""+items[acPowerFreq]);
						}
						if (mVideoFlip >=0  && mVideoFlip <=3   ) {

							String[] items = getResources().getStringArray(R.array.video_flip);
							setting_flip_tv.setText(""+items[mVideoFlip]);
						}

						setting_devicename_tv.setText(""+mDevice.nickName);
						setting_deviceid_tv.setText(""+mDevice.UID);
						setting_devicemode_tv.setText(""+deviceinfovar23);
						mDevice.firmwareVersion = getVersion(deviceinfovar25);

						if(mDevice.hardware_pkg == HARDWAEW_PKG.SREE_VR_LINUX_PIR_LED){
							setting_flip_rl.setVisibility(GONE);
						}else{
							if(mDevice.firmwareVersionPrefix>=200 || mDevice.firmwareVersionPrefix<=60){
								setting_flip_rl.setVisibility(View.VISIBLE);
							}else{
								setting_flip_rl.setVisibility(GONE);
							}
						}



						if(getHelper().getVer(mDevice.firmwareVersion)<3.0){
							setting_led_rl.setVisibility(GONE);
							setting_dt_rl.setVisibility(GONE);
						}

						setting_deviceversion_tv.setText(mDevice.firmwareVersion);
						setting_deviceproducts_tv.setText(""+deviceinfovar24);
					}
				}
				break;
				case 819:
					if (var2[0] == 0) {

						Log.e("SettingActivity...guo","密码修改成功");

						isSetPasswordWithCountry = true;

						if(isFromUser){
							Toast.makeText(
									SettingActivity.this,
									SettingActivity.this.getText(
											R.string.page10_tips_modify_security_code_ok)
											.toString(), 0).show();
						}


					}
					break;

				case 835:
					Handler var18 = SettingActivity.this.handler;
					Runnable var19 = new Runnable() {
						public void run() {
							mCameraManagerment.userIPCListWifiAP(SettingActivity.this.mCamera.getmUID());

						}
					};
					var18.postDelayed(var19, 30000L);
					break;

				case 897:
					if (var2[4] == 0) {
						Log.v("formatresp", "status:"+ var2[4] + " progress:"+ var2[5]);
						if(var2[5]<100){
							SettingActivity.this.mTotalSize = 0;

							Toast.makeText(
									SettingActivity.this,
									SettingActivity.this.getText(
											R.string.page10_tips_format_sdcard_ongoing)
											.toString(), 0).show();
						}else if(var2[5]==100){
							SettingActivity.this.mTotalSize = Packet
									.byteArrayToInt_Little(var2, 8);
							int freesize = Packet.byteArrayToInt_Little(var2, 12);

							Log.v("formatresp", "status:"+ var2[4] + " progress:"+ var2[5]);
							Toast.makeText(
									SettingActivity.this,
									SettingActivity.this.getText(
											R.string.page10_tips_format_sdcard_success)
											.toString(), 0).show();

							PopupWindow getPopupWindow = DialogUtil.getInstance().getPopupWindow(); //格式化完成，更新剩余容量显示
							TextView freeTv	 = (TextView)getPopupWindow.getContentView().findViewById(R.id.setting_devicefree_tv);
							if(freeTv!=null){
								float f1 = (float)freesize/(float)1024;
								freeTv.setText(String.format("%.2f", f1)+" GB");
							}

						}
					} else {
						Toast.makeText(
								SettingActivity.this,
								SettingActivity.this.getText(
										R.string.page10_tips_format_sdcard_failed)
										.toString(), Toast.LENGTH_SHORT).show();
					}
					break;
				case 929:
					TimeZoneEnable = Packet.byteArrayToInt_Little(var2,  4);
					enableDST= Packet.byteArrayToInt_Little(var2, 4);

					TimeZone = Packet.byteArrayToInt_Little(var2, 2*4);

					Log.v("test","929  TimeZone = "+TimeZone+"   TimeZoneEnable ="+TimeZoneEnable);

					break;
				case   AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_ALARMMODE_RESP:
					if(var2[4]!=0){
						Toast.makeText(
								SettingActivity.this,
								SettingActivity.this.getText(
										R.string.page10_failed_to_update_device)
										.toString(), 0).show();
					}
					break;
				case 945:
					break;

			}

			super.handleMessage(var1);
		}
	};
	private MyCamera mCamera;
	private DeviceInfo mDevice;
	private int mEnvMode = -1;
	private int mMotionDetection = -1;
	private int mAlarmMode = -1;
	private int mPostition = -1;
	private int mRecordType = -1;
	private int mTotalSize = -1,mfree = -1;

	private int mVideoFlip = -1;
	private int pirsetting = -1;
	private int dormancytime = -1;
	private int acPowerFreq = -1;
	private int country = -1;
	private int mVideoQuality = -1;
	protected ThreadCheck m_threadCheck = null;
	private int mtotalMinute = 0;

	boolean stopCheck = true;
	private byte[] szTimeZoneString = null;
	private long t1 = 0L;
	private String[] timeZoneList = {"-12","-11","-10","-9","-8","-7","-6","-5","-4","-3","-2","-1","GMT","1","2","3","4","5","6","7","8","9","10","11","12"};
	private String[] timeZoneNameList = null;

	private boolean motiondetectionvalue = false;
	private boolean recordvalue= false;

	// $FF: synthetic method
	static String access$11() {
		return newPassword;
	}

	// $FF: synthetic method
	static boolean access$13() {
		return isModifyPassword;
	}

	// $FF: synthetic method
	static boolean access$3() {
		return isModifyWiFi;
	}

	private void checkWiFi() {
		if (this.m_threadCheck == null) {
			this.m_threadCheck = new ThreadCheck();
			this.m_threadCheck.start();
		}

	}

	private void getCSVdata(String s, int i) {
		InputStream inputstream = null;
		BufferedReader bufferedreader = null;

		try {
			inputstream = getResources().getAssets().open(s);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
		String s1 = null;
		String as[];
		boolean bReadNext = true;

		while (bReadNext) {
			try {
				s1 = bufferedreader.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (s1 == null)
				try {
					inputstream.close();
					bufferedreader.close();
					return;
				} catch (IOException ioexception4) {
					return;
				}
			as = s1.split(",");
			Log.i("IOTCamera", "as:" + as);
			if (as[2].equals("--")) {
				try {
					inputstream.close();
					bufferedreader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				timeZoneList[i] = (new StringBuilder(String.valueOf(as[1])))
						.append("\n").append(as[2]).toString();
				timeZoneNameList[i] = as[1];
				i++;
			}
		}

	}

	private int getConut(String s, int i) {
		InputStream inputstream = null;
		BufferedReader bufferedreader;
		try {
			inputstream = getResources().getAssets().open(s);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
		String s1;
		boolean bReadNext = true;

		while (bReadNext) {
			try {
				s1 = bufferedreader.readLine();
			} catch (IOException ioexception2) {
				try {
					inputstream.close();
					bufferedreader.close();
				} catch (IOException ioexception3) {
					return i;
				}
				return i;
			}
			if (s1 == null) {

				try {
					inputstream.close();
					bufferedreader.close();
				} catch (IOException ioexception4) {
					return i;
				}
				return i;
			}
			boolean flag = s1.split(",")[2].equals("--");
			if (!flag) {
				i++;
			} else {
				bReadNext = false;
				try {
					inputstream.close();
					bufferedreader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	private static String getString(byte[] var0) {
		StringBuilder var1 = new StringBuilder();

		for (int var2 = 0; var2 < var0.length && var0[var2] != 0; ++var2) {
			var1.append((char) var0[var2]);
		}

		return var1.toString();
	}

	private void getTimeZoneCSV() {
		// String[] var1 = new String[] { "africa.csv", "america.csv",
		// "asia.csv",
		// "europe.csv", "oceania.csv" };
		String[] var1 = new String[] { "asia.csv" };
		int[] var2 = new int[6];

		for (int var3 = 1; var3 <= var1.length; ++var3) {
			var2[var3] = this.getConut(var1[var3 - 1], var2[var3 - 1]);
		}

		this.timeZoneList = new String[var2[1]];
		this.timeZoneNameList = new String[var2[1]];

		for (int var4 = 0; var4 < var1.length; ++var4) {
			this.getCSVdata(var1[var4], var2[var4]);
		}

		Arrays.sort(this.timeZoneList);
		Arrays.sort(this.timeZoneNameList);
	}

	private void getTimeZonetDate(String var1) {
		String[] var2 = var1.split("\\n");
		this.szTimeZoneString = var2[0].getBytes();
		String var3 = var2[1].substring(4);
		if (var3.indexOf("+") != -1) {
			int var8 = 1 + var3.indexOf("+");
			int var9 = var3.indexOf(":");
			int var10 = Integer.parseInt(var3.substring(var8, var9));
			this.mtotalMinute = Integer.parseInt(var3.substring(var9 + 1))
					+ var10 * 60;
		} else {
			int var4 = 1 + var3.indexOf("-");
			int var5 = var3.indexOf(":");
			int var6 = Integer.parseInt(var3.substring(var4, var5));
			int var7 = Integer.parseInt(var3.substring(var5 + 1));
			this.mtotalMinute = var6 * -60 - var7;
		}
	}

	private String getVersion(int var1) {
		byte[] var2 = new byte[4];
		StringBuffer var3 = new StringBuffer();
		var2[3] = (byte) var1;
		var2[2] = (byte) (var1 >>> 8);
		var2[1] = (byte) (var1 >>> 16);
		var2[0] = (byte) (var1 >>> 24);
		var3.append(255 & var2[0]);
		var3.append('.');
		var3.append(255 & var2[1]);
		var3.append('.');
		var3.append(255 & var2[2]);
		var3.append('.');
		var3.append(255 & var2[3]);
		mDevice.firmwareVersionPrefix = (255 & var2[0]);
		return var3.toString();
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
			if(result.contains(""+mCamera.getUID())){
				httpoperate = true;
			}else{
				httpoperate = false;
			}
			if (result != null) {
				boolean state = response.optBoolean("state");
				if (!state) {
					httpoperate = false;
				}
			}
			runOnUiThread(new   Runnable() {
				public void run() {
					sethttpoperate_iv();
				}
			});
		}

		@Override
		public void onFailure(int statusCode, Throwable error,
							  String content) {
			error.printStackTrace();
		}

	};
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initDeviceInfo() ;

		HttpClient.operateDeviceVoip(this.mCamera.getmUID(), 3, mJsonHttpResponseHandler);
	}
	private void initDeviceInfo() {

		if (this.mCamera != null) {

			mCameraManagerment.userIPCGetAdvanceSetting(this.mCamera.getmUID());

		}

	}



	private void quit(boolean var1) {

		if (isModifyPassword) {
			DeviceInfo var16 = this.mDevice;
			String var17;
			if (isModifyPassword) {
				var17 = newPassword;
			} else {
				var17 = this.mDevice.viewPassword;
			}
			var16.viewPassword = var17;
			// //////////////////////////////////////////////////////////////////////////



			(new DatabaseManager(this)).updateDeviceInfoByDBID(
					this.mDevice.DBID, this.mDevice.UID, this.mDevice.nickName,
					"", "", "admin", this.mDevice.viewPassword,
					this.mDevice.EventNotification, this.mDevice.getChannelIndex(),
					this.mDevice.isPublic);

		}

		if (this.mCamera != null) {
			this.mCamera.unregisterIOTCListener(this);
		}


		this.finish();
	}

	public void onConfigurationChanged(Configuration var1) {
		super.onConfigurationChanged(var1);
		Configuration var2 = this.getResources().getConfiguration();
		if (var2.orientation != 2) {
			int var10000 = var2.orientation;
		}

	}

	MyProgressBar mProgressBar;
	private LinearLayout pnlosdSeting;
	private boolean osdvalue = false;
	private ImageView back,httpoperate_iv;
	private TextView title,setting_call_tv;
	private ImageView title_img;
	int fromMain ;
	protected void onCreate(Bundle var1) {
		super.onCreate(var1);
		this.setTitle(this.getText(R.string.page10_dialog_AdvancedSetting));
		this.getWindow().setFlags(128, 128);
		this.setContentView(R.layout.activity_device_setting);
		getActionBar().hide();
		((TextView)findViewById(R.id.title)).setText(""+getString(R.string.menu_setting));
		Bundle var2 = this.getIntent().getExtras();
		String var3 = var2.getString("dev_uuid");
		String mDevUID = var2.getString("dev_uid");
		fromMain = var2.getInt("fromMain");
		this.mCamera =  CameraManagerment.getInstance().getexistCamera(mDevUID);
		if(mCamera!=null) this.mCamera.registerIOTCListener(this);

		this.mDevice  = MainCameraFragment.getexistDevice(mDevUID);
		if(mDevice != null) {
			HttpClient.setBaseUrl(StringUtils.getCurrentLocaltionISOCountryCodeString( this.mDevice.country));
		}else{
			this.mDevice  = MainCameraFragment.getLoaclDevice(mDevUID);//mDevice null，直接从DB获取
		}

		mProgressBar = new MyProgressBar(SettingActivity.this , null);
		if(this.mDevice.viewPassword != null){
			newPassword = this.mDevice.viewPassword;
		}else{
			newPassword = "";
		}
		initview();
		back = (ImageView) this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);

		httpoperate_iv= (ImageView) this.findViewById(R.id.httpoperate_iv);
		title = (TextView) this.findViewById(R.id.title);
		title.setText(getString(R.string.page25_page10_txtAdvancedSetting));

		title_img = (ImageView) findViewById(R.id.title_img);
		title_img.setImageResource(R.drawable.setting_off);
		title_img.setVisibility(GONE);
		back.setOnClickListener(this);
		this.findViewById(R.id.left_ll).setOnClickListener(this);
		setting_call_tv= (TextView) this.findViewById(R.id.setting_call_tv);
		sethttpoperate_iv();

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
		this.stopCheck = true;
		super.onStop();
	}

	public void receiveChannelInfo(Camera var1, int var2, int var3) {
	}

	public void receiveFrameData(Camera var1, int var2, Bitmap var3) {
	}

	public void receiveFrameInfo(Camera var1, int var2, long var3, int var5,
								 int var6, AVFrame avFrame , int var8) {
	}

	public void receiveIOCtrlData(Camera var1, int var2, int var3, byte[] var4) {
		Log.i("deviceinfo", "receiveIOCtrlData.............." + var3);
		if (this.mCamera == var1) {
			Bundle var5 = new Bundle();
			var5.putInt("sessionChannel", var2);
			var5.putByteArray("data", var4);
			Message var6 = new Message();
			var6.what = var3;
			var6.setData(var5);
			this.handler.sendMessage(var6);
		}

	}

	public void receiveSessionInfo(Camera var1, int var2) {
	}

	class ThreadCheck extends Thread {

		public ThreadCheck() {
			SettingActivity.this.stopCheck = false;
		}

		public void run() {
			do {
				long var1 = System.currentTimeMillis();
				if (var1 - SettingActivity.this.t1 > 50000L
						&& !SettingActivity.this.changeStatus) {
					SettingActivity.this.handler.postDelayed(
							new Runnable() {
								public void run() {
									mCameraManagerment.userIPCListWifiAP(SettingActivity.this.mCamera.getmUID());

								}
							}, 100L);
				}

				if (var1 - SettingActivity.this.t1 > 60000L) {
					if (!SettingActivity.this.changeStatus) {
						Message var3 = new Message();
						var3.what = 1;
						SettingActivity.this.handler.sendMessage(var3);
						SettingActivity.this.changeStatus = false;
						SettingActivity.this.stopCheck = true;
					}

					SettingActivity.this.t1 = System.currentTimeMillis();
				}
			} while (!SettingActivity.this.stopCheck);

		}
	}



	private void startUrlCheck(final String UID) {
		new Thread() {
			public void run() {
				DefaultHttpClient client = new DefaultHttpClient();
				StringBuilder builder = new StringBuilder();

				String uid64 = (new String( Base64.encode(UID.getBytes(),0)));
				uid64 =	uid64.replace('+','-');
				uid64 = uid64.replace('/','_');
				uid64 = uid64.replace('=',',');

				String asB64 = uid64.substring(0, uid64.length()-1);

				int pidnum = (versionloacl & 0xFF000000) >> 24;
				if(pidnum < 0) pidnum +=256;
				String strpid =  String.valueOf(pidnum);

				byte[] pid = strpid.getBytes();
				String apidB64 = (new String( Base64.encode(pid,0)));

				apidB64 = apidB64.replace('+','-');
				apidB64 = apidB64.replace('/','_');
				apidB64 = apidB64.replace('=',',');
				String pid64 = apidB64.substring(0, apidB64.length()-1);

				//HttpGet myget = new HttpGet(
				//		"http://camera.kkmove.com/interface.php?Function=19&Command=1&Hmac=0&Time=20150504T235516&Nonce=nN1uwy5Y&Seq=12345678&UID="+asB64+",&WebAccount=0&Token=0");

				HttpGet myget = new HttpGet( HttpClient.getBaseUrl()+"/interface.php?Function=19&Command=2&Hmac=0&Time=20150504T235516&Nonce=nN1uwy5Y&Seq=12345678&UID="+asB64+"&Kpid="+ pid64 +"&WebAccount=0&Token=0");
				// HttpGet myget = new HttpGet("http://www.crazyit.org");
				try {
					HttpResponse response = client.execute(myget);
					HttpEntity entity = response.getEntity();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(entity.getContent()));
					for (String s = reader.readLine(); s != null; s = reader
							.readLine()) {
						builder.append(s);
					}
					Log.v("lastversion","builder.toString() ="+ builder.toString());
					JSONObject jsonObject = new JSONObject(builder.toString());
					ver = jsonObject.getString("lastversion");
					String file_url = jsonObject.getString("file_url");
					file_desc = jsonObject.getString("desc");
					Log.v("lastversion","lastversion="+ver);
					String version[]=ver.split("\\.");
					versionz = (Integer.parseInt(version[0])<<24)|(Integer.parseInt(version[1])<<16)|(Integer.parseInt(version[2])<<8)|(Integer.parseInt(version[3])<<0);
					Log.v("lastversion","versionz="+versionz);
					Log.v("lastversion","file_url="+file_url);
					Bundle var5 = new Bundle();
					var5.putInt("lastversion", versionz);
					UpdateReq = new  SMsgAVIoctrlFirmwareUpdateReq();
					UpdateReq.setVersion(versionz);
					UpdateReq.setFile_type((byte) jsonObject.getInt("file_type"));
					UpdateReq.setFile_size((int) jsonObject.getInt("file_size"));

					UpdateReq.setFile_md5_len((byte) jsonObject.getString("md5sum").length());
					UpdateReq.setMd5sum( jsonObject.getString("md5sum").getBytes());
					UpdateReq.setFile_url(jsonObject.getString("file_url").getBytes());
					UpdateReq.setFile_url_len((byte) jsonObject.getString("file_url").length());

					Message var6 = new Message();
					var6.what = 34;
					var6.setData(var5);
					handler.sendMessage(var6);
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		}.start();
	}
	String file_desc ;
	int versionz;
	String ver;
	private ProgressDialog progressDialog;

	public void showDialog() {
		Log.e("SettingActivity...guo","showDialog");
		progressDialog = new ProgressDialog(SettingActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		String tmpstring = SettingActivity.this.getText(
				R.string.page10_u_firmware_updating).toString();

		progressDialog.setMessage(tmpstring + "0%");
		progressDialog.setCanceledOnTouchOutside(false);
		if(!isFinishing()){
			progressDialog.show();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
			{
				this.finish();
			}
			break;
			case R.id.left_ll:
			{
				this.finish();
			}
			break;

			case R.id.btnRemoveCamera:
			{
				DialogUtil.getInstance().showDelDialog(this,getString( R.string.page26_ctxRemoveCamera)+"", getString( R.string.page26_tips_remove_camera_confirm)+"",
						new Dialogcallback(){

							@Override
							public void cancel() {
							}

							@Override
							public void commit(String str) {
							}

							@Override
							public void commit() {
								doDeleteMyDevice() ;
								finish();
							}

						});
				break;
			}
			case R.id.setting_call_rl:{

				final String[] items = getResources().getStringArray(R.array.messagetype);
				int currentmessageType =   PreferenceUtil.getInstance().getInt(Constants.MESSAGETYPE_CHECK+SettingActivity.this.mCamera.getmUID(), UbiaApplication.DefaultReceiverType);
				DialogUtil.getInstance().showChosePiviewDialo(this, items, currentmessageType, getString(R.string.page10_call_alert),  new DialogChooseItemcallback(){


					@Override
					public void chooseItem(int chooseItem) {
						byte newvalue = (byte)chooseItem;
						PreferenceUtil.getInstance().putInt(Constants.MESSAGETYPE_CHECK+SettingActivity.this.mCamera.getmUID(), chooseItem);
						setting_call_tv.setText(""+items[newvalue]);
						if(newvalue!=0){
							httpoperate = true;
							HttpClient.operateDeviceVoip(SettingActivity.this.mCamera.getmUID(), 1, mJsonHttpResponseHandler);//开
						}else{
							httpoperate = false;
							HttpClient.operateDeviceVoip(SettingActivity.this.mCamera.getmUID(), 2, mJsonHttpResponseHandler);//关
						}
						sethttpoperate_iv();
						int localCountry = 	PreferenceUtil.getInstance().getInt(Constants.COUNTRYCODE+mDevice.UID.toUpperCase() );
						if(country>=0 && country!=localCountry && localCountry>0){
							if (SettingActivity.this.mCamera != null) {

								mCameraManagerment.userIPCSetPassWordwithCountry(SettingActivity.this.mCamera.getmDevUID(),
										mDevice.viewPassword, mDevice.viewPassword,mDevice.nickName,StringUtils.getCurrentLocaltionISOCountryCodeString(localCountry));
							}

						}
					}

				});

				HttpClient.operateDeviceVoip(this.mCamera.getmUID(), 3, mJsonHttpResponseHandler);//查
//			呼叫提醒功能：
			}
			break;
			case  R.id. setting_sd_rl:{
				DialogUtil.getInstance().showformatSDDialo(this, mCamera, getString(R.string.page10_btnSDCard), mTotalSize, mfree, mDevice.firmwareVersionPrefix,enableDST, new Dialogcallback() {
					@Override
					public void cancel() {
						initDeviceInfo() ;
					}
					@Override
					public void commit(String str) {
					}
					@Override
					public void commit() {
					}
				});

				break;}
			case  R.id. setting_env_rl:{
				String[] items = getResources().getStringArray(R.array.environment_mode);

				DialogUtil.getInstance().showChosePiviewDialo(this, items, mEnvMode, getString(R.string.page10_txtEnvironment),  new DialogChooseItemcallback(){


					@Override
					public void chooseItem(int chooseItem) {
						byte newvalue = (byte)chooseItem;
						mCameraManagerment.userIPCsetSceneMode(mCamera.getmUID(), newvalue);
//					mCameraManagerment.userIPCSetEnv(mCamera.getmUID(),  mDevice.getChannelIndex(), newvalue);
						initDeviceInfo() ;
					}

				});
				break;}
			case  R.id. setting_flip_rl:{
				String[] items = getResources().getStringArray(R.array.video_flip);
				DialogUtil.getInstance().showChosePiviewDialo(this, items, mVideoFlip, getString(R.string.page10_txtVideoFlip),  new DialogChooseItemcallback(){
					@Override
					public void chooseItem(int chooseItem) {
						byte newvalue = (byte)chooseItem;
						mCameraManagerment.userIPCSetVideoMode(mCamera.getmUID(),0, newvalue);
						initDeviceInfo() ;
					}
				});
				break;
			}

			case  R.id. setting_led_rl:{
				String[] items = getResources().getStringArray(R.array.motion_detection);
				DialogUtil.getInstance().showChosePiviewDialo(this, items, led, ""+ "LED",  new DialogChooseItemcallback(){


					@Override
					public void chooseItem(int chooseItem) {
						// TODO Auto-generated method stub
						int value  =  chooseItem == 0 ? 1:0;
						mCameraManagerment.userIPCsetLed(	mDevice.UID,value);
						initDeviceInfo() ;
					}

				});
				break;
			}


			case  R.id. setting_dt_rl:{
				String[] items = getResources().getStringArray(R.array.dormancytime);

				int value = 0;
				switch (dormancytime){
					case 15:
						value = 0;
						break;
					case 30:
						value = 1;
						break;
					case 60:
						value = 2;
						break;
					case 0xff:
						value = 3;
						break;
				}

				DialogUtil.getInstance().showChoseDormancyTimeDialo(this, items, value, ""+ getString(R.string.setting_dormancytime),  new DialogChooseItemcallback(){


					@Override
					public void chooseItem(int chooseItem) {
						// TODO Auto-generated method stub

						int value = 0;

						switch (chooseItem){
							case 0:
								value = 15;
								break;
							case 1:
								value = 30;
								break;
							case 2:
								value = 60;
								break;
							case 3:
								value = 0xff;
								mCameraManagerment.userIPCsetPir(mDevice.UID,0); //不休眠时，PIR关闭
								break;
						}
						//Log.i("IOTCamera", "设置休眠时间 = "+value);

						mCameraManagerment.userIPCsetActiveTime(	mDevice.UID,value);
						initDeviceInfo() ;
					}

				});
				break;}

			case  R.id. setting_alarm_rl:{
				String[] items = getResources().getStringArray(R.array.motion_detection);

				DialogUtil.getInstance().showChosePiviewDialo(this, items, mAlarmMode, ""+ getString(R.string.page10_alarm_alert),  new DialogChooseItemcallback(){


					@Override
					public void chooseItem(int chooseItem) {
						// TODO Auto-generated method stub
						Log.i("IOTCamera", "alarmMode = "+( chooseItem));
						byte data[] = new byte[8];
						data[4]= (byte) chooseItem;
						mCameraManagerment.userIPCSetAlermMode(mCamera.getmUID(), data);
						initDeviceInfo() ;
					}

				});
				break;
			}
			case  R.id. setting_md_rl:{
				String[] items = getResources().getStringArray(R.array.motion_detection);

				DialogUtil.getInstance().showChosePiviewDialo(this, items, mMotionDetection, ""+ getString(R.string.page10_txtMotionDetectionSetting),  new DialogChooseItemcallback(){


					@Override
					public void chooseItem(int chooseItem) {
						// TODO Auto-generated method stub
						Log.i("IOTCamera", "设置MotionDetection = "+( chooseItem));
						mCameraManagerment.userIPCSetMotionDetect(mCamera.getmUID(),0,chooseItem);
						initDeviceInfo() ;
					}

				});
				break;
			}


			case  R.id. setting_ir_rl:{
				String[] items = getResources().getStringArray(R.array.pirmode_quality);

				if(mDevice.hardware_pkg == HARDWAEW_PKG.SREE_VR_LINUX_PIR_LED){
					items = getResources().getStringArray(R.array.motion_detection);
				}

				DialogUtil.getInstance().showChosePiviewDialo(this, items, pirsetting, ""+ getString(R.string.page10_alarm_pirmode),  new DialogChooseItemcallback(){


					@Override
					public void chooseItem(int chooseItem) {
						// TODO Auto-generated method stub
						Log.i("IOTCamera", "设置spinPIRSetStatus = "+( chooseItem));
						mCameraManagerment.userIPCsetPir(		mDevice.UID,chooseItem);
						initDeviceInfo() ;
					}

				});
				break;}

			case  R.id. setting_record_rl:{
				String[] items = getResources().getStringArray(R.array.recording_mode);

				DialogUtil.getInstance().showChosePiviewDialo(this, items, mRecordType, "" +getString(R.string.page10_txtRecordSetting),  new DialogChooseItemcallback(){


					@Override
					public void chooseItem(int chooseItem) {
						// TODO Auto-generated method stub
						if (mCamera != null && recordvalue) {
							mCameraManagerment.userIPCSetRecord(mCamera.getmUID(), mDevice.getChannelIndex(), chooseItem);

							Log.i("deviceinfo", "setOnItemSelectedListener...设置录像模式.......=" + chooseItem );
							initDeviceInfo() ;
						}
					}

				});
				break;}
			case  R.id. setting_dst_rl:{
				String[] items = getResources().getStringArray(R.array.recording_mode);
				DialogUtil.getInstance().showChosePiviewDialo(this, items, enableDST , "" +getString(R.string.page10_dialog_ManagCustomtimezone),  new DialogChooseItemcallback(){
					@Override
					public void chooseItem(int chooseItem) {
						if (mCamera != null  ) {
							mCameraManagerment.userIPCSetTimeZone(mCamera.getmUID(), 268, chooseItem, TimeZone);
							Log.e("deviceinfo", "userIPCSetTimeZone...设置DST时区.......=" + chooseItem );
							initDeviceInfo() ;
						}
					}
				});
				break;}
			case  R.id. setting_asytimezone_rl:{
				if (mCamera != null  ) {
					if(mCamera!=null){

						TimeZone tz =java.util.TimeZone.getDefault();
						int offset = tz.getOffset(System.currentTimeMillis())/(3600*1000);
						mCameraManagerment.userIPCSetTimeZone(mCamera.getmUID(), 268,
								enableDST,
								offset);
					}
					initDeviceInfo() ;
				}
				break;}
			case  R.id. setting_power_rl:{
				String[] items = getResources().getStringArray(R.array.powerfreq);
				if(acPowerFreq != -1){
					setting_power_tv.setText(""+items[acPowerFreq]);
				}

				DialogUtil.getInstance().showChosePiviewDialo(this, items, acPowerFreq, ""+ getString(R.string.page10_power),  new DialogChooseItemcallback(){


					@Override
					public void chooseItem(int chooseItem) {
						// TODO Auto-generated method stub
						Log.i("IOTCamera", "设置userIPCsetACPPower = "+( chooseItem));

						mCameraManagerment.userIPCsetACPPower(mCamera.getmUID(), chooseItem);
						initDeviceInfo();
					}

				});
				break;
			}
			case  R.id. setting_timezone_rl:{
				String[] items = getResources().getStringArray(R.array.time_zone_name);
				setting_timezone_tv.setText(""+items[TimeZone + 12]);
				DialogUtil.getInstance().showChosePiviewDialo(this, items, TimeZone, ""+ getString(R.string.page10_txtTimeZoneSetting),  new DialogChooseItemcallback(){
					@Override
					public void chooseItem(int chooseItem) {
						Log.i("IOTCamera", "setting_timezone_tv = "+( chooseItem));
						{
							mCameraManagerment.userIPCSetTimeZone(mCamera.getmUID(), 268, enableDST,  chooseItem - 12);
							try {
								Thread.sleep(150);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						initDeviceInfo() ;
					}
				});
				break;}
			case  R.id. setting_offline_editName_rl:{

				DialogUtil.getInstance().showNamePWdDialo(this,mCamera, ""+ getString(R.string.page10_editName),new Dialogcallback(){

					@Override
					public void cancel() {
						// TODO Auto-generated method stub

					}

					@Override
					public void commit() {
						// TODO Auto-generated method stub

					}

					@Override
					public void commit(String str) {
						mDevice.nickName = str;

						(new DatabaseManager(SettingActivity.this)).updateDeviceInfoByDBID(
								mDevice.DBID, mDevice.UID, mDevice.nickName,
								"", "", "admin", mDevice.viewPassword,
								mDevice.EventNotification, mDevice.getChannelIndex(),
								mDevice.isPublic);
						mCameraManagerment.userIPCSetDeviceName( mDevice.UID,  mDevice.nickName );
					}

				},null);
		   /*new Dialogcallback(){

				@Override
				public void cancel() {
					// TODO Auto-generated method stub

				}

				@Override
				public void commit() {
					// TODO Auto-generated method stub

				}

				@Override
				public void commit(String str) {
					  if (SettingActivity.this.mCamera != null) {


							newPassword = str;
							DeviceInfo var16 =  mDevice;
							String var17;
							isModifyPassword = true;
							 var17 = newPassword;

							var16.viewPassword = var17;
							// //////////////////////////////////////////////////////////////////////////
							 (new DatabaseManager(SettingActivity.this)).updateDeviceInfoByDBID(
							mDevice.DBID, mDevice.UID, mDevice.nickName,
							 "", "", "admin", mDevice.viewPassword,
							 mDevice.EventNotification, mDevice.getChannelIndex(),
							 mDevice.isPublic);

					 }


				}

			});
			*/
				break;
			}
			case  R.id. setting_namepwd_rl:{

				DialogUtil.getInstance().showNamePWdDialo(this,mCamera, ""+ getString(R.string.page10_dialog_defaultSetting),new Dialogcallback(){

					@Override
					public void cancel() {
						// TODO Auto-generated method stub

					}

					@Override
					public void commit() {
						// TODO Auto-generated method stub

					}

					@Override
					public void commit(String str) {
						mDevice.nickName = str;
						Log.e("guo,,","updateDeviceInfoByDBID....."+mDevice.DBID+","+mDevice.UID);
						new DatabaseManager(SettingActivity.this).updateDeviceInfoByDBID(
								mDevice.DBID, mDevice.UID, mDevice.nickName,
								"", "", "admin", mDevice.viewPassword,
								mDevice.EventNotification, mDevice.getChannelIndex(),
								mDevice.isPublic);

						Toast.makeText(
								SettingActivity.this,
								SettingActivity.this.getText(
										R.string.page10_success_to_update_device_name)
										.toString(), Toast.LENGTH_SHORT).show();
						setting_devicename_tv.setText(""+mDevice.nickName);

						//	mCameraManagerment.userIPCSetDeviceName( mDevice.UID,  mDevice.nickName ); //保存到本地，不需要调用服务器接口
					}

				},new Dialogcallback(){

					@Override
					public void cancel() {
						// TODO Auto-generated method stub

					}

					@Override
					public void commit() {
						// TODO Auto-generated method stub

					}

					@Override
					public void commit(String str) {
						if (SettingActivity.this.mCamera != null) {

							boolean ret[] =  UbiaUtil.validatepassword(str);

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





							newPassword = str;
							DeviceInfo var16 =  mDevice;
							String var17;
							isModifyPassword = true;
							var17 = newPassword;
							isFromUser = true;
							mCameraManagerment.userIPCSetSettingPassWord(SettingActivity.this.mCamera.getmDevUID(),
									var16.viewPassword, str,mDevice.nickName);
							var16.viewPassword = var17;
							// //////////////////////////////////////////////////////////////////////////
							(new DatabaseManager(SettingActivity.this)).updateDeviceInfoByDBID(
									mDevice.DBID, mDevice.UID, mDevice.nickName,
									"", "", "admin", mDevice.viewPassword,
									mDevice.EventNotification, mDevice.getChannelIndex(),
									mDevice.isPublic);

						}


					}

				});

				break;}
			case R.id.setting_offline_qr_rl:
			case  R.id. setting_shareqr_rl:{
				Bundle var2 = new Bundle();
				var2.putString("dev_uuid",  mDevice.UUID);
				var2.putString("dev_uid",  mDevice.UID);
				Intent var3 = new Intent();
				var3.setClass(SettingActivity.this, QrCodeShareInfoActivity.class);
				var3.putExtras(var2);
				SettingActivity.this.startActivityForResult(var3, 0);
				break;}
			case  R.id. setting_checkversion_rl:{

				// TODO Auto-generated method stub
				if(mCamera!=null){

					if(ver == null || ver.equals("") || (  versionz <= versionloacl) || getHelper().getVer(mDevice.firmwareVersion) <= 1.18 ){
						Toast.makeText(
								SettingActivity.this,
								SettingActivity.this
										.getText(
												R.string.page10_u_firmware_update_lastversion)
										.toString(), 0).show();
					}else{
						String msgStr = getString(R.string.page10_u_firmware_update_newversion) + ver + " \n" + file_desc;
						getHelper().showDialog(getString(R.string.page10_u_firmware_update_prompt), msgStr,
								new ShowDialogCallback() {

									@Override
									public void callback(boolean sure) {
										if (sure) {
											mProgressBar.show();
											mCamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FIRMWARE_UPDATE_REQ,
													UpdateReq.getData());
										} else {
											mProgressBar.dismiss();
										}

									}
								});
					}


				}


				break;
			}


			default:
				break;
		}

	}

	int waitCount = 0;

	Handler waitUpdateHandler = new Handler(){
		public void handleMessage(Message msg) {

			switch (msg.what){
				case 0:
					if(waitCount == 100 || isConnectSuccess){
						waitCount = 0;
						progressDialog.dismiss();

					}else {

						String tmpstring = SettingActivity.this.getText(
								R.string.page10_u_firmware_update_toast).toString();

						progressDialog.setMessage(waitCount + "%..." + tmpstring);
						waitCount++;
						waitUpdateHandler.sendEmptyMessageDelayed(0, 3000);
					}
					break;
				case 1:
					Toast.makeText(
							SettingActivity.this,SettingActivity.this.getText(
									R.string.firmware_update_success)
									.toString(), Toast.LENGTH_LONG).show();
					progressDialog.dismiss();
					break;
			}


		}
	};


	private void doDeleteMyDevice() {
		// String password = getHelper().getConfig(Constants.USER_PASSWORD);

		if (!UbiaApplication.ISWEB) {
			if(mProgressBar!=null)
				mProgressBar.dismiss();
			PreferenceUtil.getInstance().remove(Constants.COUNTRYCODE+mDevice.UID.toUpperCase() );
			DeviceInfo selectedDevice = MainCameraFragment.getexistDevice(mDevice.UID);
			MyCamera selectedCamera = (MyCamera) this.mCameraManagerment.deleteExistCamera(mDevice.UID);
			selectedCamera.stop(0);
			selectedCamera.disconnect();
			selectedCamera.unregisterIOTCListener( this);
			this.mCameraManagerment.CameraList.remove(selectedCamera);
			DatabaseManager var5 = new DatabaseManager(this);
			SQLiteDatabase var6 = var5.getReadableDatabase();
			Cursor var7 = var6.query("snapshot", new String[] { "_id",
							"dev_uid", "file_path", "time" }, "dev_uid = \'"
							+ selectedDevice.UID + "\'", (String[]) null,
					(String) null, (String) null, "_id LIMIT 4");

			while (var7.moveToNext()) {
				File var8 = new File(var7.getString(2));
				if (var8.exists()) {
					var8.delete();
				}
			}

			HttpClient.operateDeviceVoip(selectedDevice.UID, 2,mJsonHttpResponseHandler);

			NotificationTagManager.getInstance().removeTag(selectedDevice.UID);
			var7.close();
			var6.close();
			var5.removeSnapshotByUID(selectedDevice.UID);
			var5.removeDeviceByUID(selectedDevice.UID);
			MainCameraFragment.DeviceList.remove(selectedDevice);
			selectedCamera = null;
			selectedDevice = null;
		}
	}

	public void sethttpoperate_iv(){

		if(mCamera!=null){
			final String[] items = getResources().getStringArray(R.array.messagetype);
			int currentmessageType =   PreferenceUtil.getInstance().getInt(Constants.MESSAGETYPE_CHECK+SettingActivity.this.mCamera.getmUID(),  UbiaApplication.DefaultReceiverType);

			if(currentmessageType<items.length &&currentmessageType>=0)
				setting_call_tv.setText(""+items[currentmessageType]);
		}


	}
}
 