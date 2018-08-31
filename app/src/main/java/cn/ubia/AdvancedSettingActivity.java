package cn.ubia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.ubia.UBell.R;
import cn.ubia.adddevice.QrCodeShareInfoActivity;
import cn.ubia.base.BaseActivity;
import cn.ubia.base.Constants;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.MyCamera;
import cn.ubia.bean.ZigbeeInfo;
import cn.ubia.db.DatabaseManager;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.interfaceManager.ZigbeeInfoCallBackInterface;
import cn.ubia.interfaceManager.ZigbeeInfoCallbackInterface_Manager;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.util.ShowDialogCallback;
import cn.ubia.util.UbiaUtil;
import cn.ubia.widget.MyProgressBar;

import com.ubia.IOTC.AVFrame;
import com.ubia.IOTC.AVIOCTRLDEFs;
import com.ubia.IOTC.Camera;
import com.ubia.IOTC.HARDWAEW_PKG;
import com.ubia.IOTC.IRegisterIOTCListener;
import com.ubia.IOTC.Packet;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlFirmwareUpdateReq;
import com.ubia.http.HttpClient;
import com.ubia.vr.VRConfig;

public class AdvancedSettingActivity extends BaseActivity implements
		IRegisterIOTCListener {
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
	private Button btnCancel;
	private Button 	btnUpdateSetting;
	private Button 	btnQrSetting;
	private Button 	btnAutoSyncTimeZone;
	private ToggleButton toggleBtnDST;
	private CameraManagerment mCameraManagerment=CameraManagerment.getInstance();
	private OnClickListener btnCancelOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			AdvancedSettingActivity.this.quit(false);
		}
	};
	private Button btnFormatSDCard;
	private OnClickListener btnFormatSDCardListener = new OnClickListener() {
		public void onClick(View var1) {
			(new Builder(AdvancedSettingActivity.this))
					.setIcon(R.drawable.icon)
					.setTitle(
							AdvancedSettingActivity.this
									.getText(R.string.page5_tips_warning))
					.setMessage(
							AdvancedSettingActivity.this
									.getText(R.string.page10_tips_format_sdcard_confirm))
					.setPositiveButton(
							AdvancedSettingActivity.this.getText(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface var1,
										int var2) {
									AdvancedSettingActivity.this.mCamera
											.sendIOCtrl(
													0,
													896,
													AVIOCTRLDEFs.SMsgAVIoctrlFormatExtStorageReq
															.parseContent(0));
								}
							})
					.setNegativeButton(
							AdvancedSettingActivity.this
									.getText(R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface var1,
										int var2) {
								}
							}).show();
		}
	};
	private Button btnManageWiFiNetworks;
	private OnClickListener btnManageWiFiNetworksOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			final AlertDialog var2 = (new Builder(new ContextThemeWrapper(
					AdvancedSettingActivity.this, R.style.HoloAlertDialog)))
					.create();
			var2.setTitle(AdvancedSettingActivity.this
					.getText(R.string.page10_dialog_ManagWiFiNetworks));
			var2.setIcon(R.drawable.icon);
			View var3 = var2.getLayoutInflater().inflate(
					R.layout.manage_device_wifi, (ViewGroup) null);
			var2.setView(var3);
			final Spinner var4 = (Spinner) var3.findViewById(R.id.spinWiFiSSID);
			final TextView var5 = (TextView) var3
					.findViewById(R.id.txtWiFiSignal);
			final TextView var6 = (TextView) var3
					.findViewById(R.id.txtWiFiSecurity);
			final EditText var7 = (EditText) var3
					.findViewById(R.id.edtWiFiPassword);
			CheckBox var8 = (CheckBox) var3
					.findViewById(R.id.chbShowHiddenPassword);
			Button var9 = (Button) var3.findViewById(R.id.btnOK);
			Button var10 = (Button) var3.findViewById(R.id.btnCancel);
			String[] var11 = new String[AdvancedSettingActivity.m_wifiList
					.size()];

			for (int var12 = 0; var12 < AdvancedSettingActivity.m_wifiList
					.size(); ++var12) {
				var11[var12] = AdvancedSettingActivity
						.getString(((AVIOCTRLDEFs.SWifiAp) AdvancedSettingActivity.m_wifiList
								.get(var12)).ssid);
			}

			if (AdvancedSettingActivity.m_wifiList.size() == 0) {
				var4.setEnabled(false);
				var9.setEnabled(false);
				var8.setEnabled(false);
				var7.setEnabled(false);
			}

			ArrayAdapter var13 = new ArrayAdapter(AdvancedSettingActivity.this,
					17367048, var11);
			var13.setDropDownViewResource(17367049);
			var4.setAdapter(var13);
			OnItemSelectedListener var14 = new OnItemSelectedListener() {
				public void onItemSelected(AdapterView var1, View var2,
						int var3, long var4) {
					AVIOCTRLDEFs.SWifiAp var6x = (AVIOCTRLDEFs.SWifiAp) AdvancedSettingActivity.m_wifiList
							.get(var3);
					String var7;
					if (var6x.enctype == 0) {
						var7 = "Invalid";
					} else if (var6x.enctype == 1) {
						var7 = "None";
					} else if (var6x.enctype == 2) {
						var7 = "WEP";
					} else if (var6x.enctype == 6) {
						var7 = "WPA2 AES";
					} else if (var6x.enctype == 5) {
						var7 = "WPA2 TKIP";
					} else if (var6x.enctype == 4) {
						var7 = "WPA AES";
					} else if (var6x.enctype == 3) {
						var7 = "WPA TKIP";
					} else if (var6x.enctype == 7) {
						var7 = "WPA PSK TKIP";
					} else if (var6x.enctype == 8) {
						var7 = "WPA PSK AES";
					} else if (var6x.enctype == 9) {
						var7 = "WPA2 PSK TKIP";
					} else if (var6x.enctype == 10) {
						var7 = "WPA2 PSK AES";
					} else {
						var7 = "Unknown";
					}

					var6.setText(var7);
					var5.setText(var6x.signal + " %");
				}

				public void onNothingSelected(AdapterView var1) {
				}
			};
			var4.setOnItemSelectedListener(var14);
			float var15 = AdvancedSettingActivity.this.getResources()
					.getDisplayMetrics().density;
			var8.setPadding(var8.getPaddingLeft() + (int) (5.0F * var15),
					var8.getPaddingTop(), var8.getPaddingRight(),
					var8.getPaddingBottom());
			OnCheckedChangeListener var16 = new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton var1, boolean var2) {
					if (!var2) {
						var7.setTransformationMethod(PasswordTransformationMethod
								.getInstance());
					} else {
						var7.setTransformationMethod(HideReturnsTransformationMethod
								.getInstance());
					}
				}
			};
			var8.setOnCheckedChangeListener(var16);
			OnClickListener var17 = new OnClickListener() {
				public void onClick(View var1) {
					String var2x = var7.getText().toString();
					AVIOCTRLDEFs.SWifiAp var3 = (AVIOCTRLDEFs.SWifiAp) AdvancedSettingActivity.m_wifiList
							.get(var4.getSelectedItemPosition());
					if (AdvancedSettingActivity.this.mCamera != null
							&& var3 != null) {
						mCameraManagerment.userIPCSetWifi(AdvancedSettingActivity.this.mCamera.getmUID(),
								var3.ssid, var2x.getBytes(), var3.mode, var3.enctype);
						
						AdvancedSettingActivity.isModifyWiFi = true;
						AdvancedSettingActivity.this.txtWiFiSSID
								.setText(AdvancedSettingActivity
										.getString(var3.ssid));
						AdvancedSettingActivity.this.txtWiFiSSID.setTypeface(
								(Typeface) null, 1);
						AdvancedSettingActivity.this.txtWiFiStatus
								.setText(AdvancedSettingActivity.this
										.getText(R.string.page10_tips_wifi_remote_device_connecting));
						AdvancedSettingActivity.this.t1 = System
								.currentTimeMillis();
						AdvancedSettingActivity.this.checkWiFi();
					}

					var2.dismiss();
				}
			};
			var9.setOnClickListener(var17);
			OnClickListener var18 = new OnClickListener() {
				public void onClick(View var1) {
					var2.dismiss();
				}
			};
			var10.setOnClickListener(var18);
			var2.show();
		}
	};
	
	
	private Button btnModifyVolume;
	private OnClickListener  btnModifyVolumeOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			
			if ( mCamera != null ) {
				 initSpeakVolume(); 
				 pnlVolumeSetting.setVisibility(0);
			} else {
				 pnlVolumeSetting.setVisibility(8);
				 return;
			}
			
			
			final AlertDialog var2 = (new Builder(new ContextThemeWrapper(
					AdvancedSettingActivity.this, R.style.HoloAlertDialog)))
					.create();
			var2.setTitle(R.string.page10_txtVolumeSetting);
			var2.setIcon(17301573);
			View var3 = var2.getLayoutInflater().inflate(
					R.layout.manage_device_volume, (ViewGroup) null);
			var2.setView(var3);
			final SeekBar var4 = (SeekBar) var3
					.findViewById(R.id.seekBarspeak);
			var4.setMax(255);
			var4.setProgress(spvalue);
			
			final SeekBar var5 = (SeekBar) var3
					.findViewById(R.id.seekBarmic);
			var5.setMax(255);
   
			var5.setProgress(micvalue);
			
			final Button btnOK = (Button)var3.findViewById(R.id.btnOK);
			btnOK.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					var2.dismiss();
				}
			});
			
			var4.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					mCameraManagerment.userIPCSetVolu(mCamera.getmUID(), mDevice.getChannelIndex(), seekBar.getProgress());
					
					mCameraManagerment.userGetIPCVolu(mCamera.getmUID(), mDevice.getChannelIndex());
					 mProgressBar.show();
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
//					 mCamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETVOLUME_REQ,
// 							AVIOCTRLDEFs.SMsgAVIoctrlSetVolumeReq
// 									.parseContent( mDevice.getChannelIndex(),(byte) progress));
					
				}
			});
	        
	        
	        
			var5.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

					Log.i("deviceinfo", "onStopTrackingTouch..............="+seekBar.getProgress());
					mCameraManagerment.userIPCSetMicVolu(mCamera.getmUID(), mDevice.getChannelIndex(),  seekBar.getProgress());
					
					mCameraManagerment.userIPCGetMicVolu(mCamera.getmUID(), mDevice.getChannelIndex());
					
					 
					 mProgressBar.show();
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

					Log.i("deviceinfo", "onStartTrackingTouch..............="+seekBar.getProgress());
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {

//					 mCamera.sendIOCtrl(0,  AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMICVOLUME_REQ,
//					AVIOCTRLDEFs.SMsgAVIoctrlSetVolumeReq
//							.parseContent( mDevice.getChannelIndex(),(byte) progress));
					Log.i("deviceinfo", "onProgressChanged.........progress....seekBar.getProgress().="+seekBar.getProgress()+"    ,progress="+progress);
				}
				
				
			});
	        
	    	var2.show();
		}
	};
	 
	private Button btnModifySecurityCode;
	private OnClickListener btnModifySecurityCodeOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			final AlertDialog var2 = (new Builder(new ContextThemeWrapper(
					AdvancedSettingActivity.this, R.style.HoloAlertDialog)))
					.create();
			var2.setTitle(R.string.page10_dialog_ModifySecurityCode);
			var2.setIcon(17301573);
			View var3 = var2.getLayoutInflater().inflate(
					R.layout.modify_security_code, (ViewGroup) null);
			var2.setView(var3);
			final EditText var4 = (EditText) var3
					.findViewById(R.id.edtOldPassword);
			var4.setText(""+ AdvancedSettingActivity.this.mDevice.viewPassword.toString().trim());
			final EditText var5 = (EditText) var3
					.findViewById(R.id.edtNewPassword);
			final EditText var6 = (EditText) var3
					.findViewById(R.id.edtConfirmPassword);
			Button var7 = (Button) var3.findViewById(R.id.btnOK);
			Button var8 = (Button) var3.findViewById(R.id.btnCancel);
			var7.setOnClickListener(new OnClickListener() {
				public void onClick(View var1) {
					String var2x = AdvancedSettingActivity.this.mDevice.viewPassword.toString().trim();
					String var3 = var5.getText().toString().trim();
					String var4x = var6.getText().toString().trim();
					if (var2x.length() != 0 && var3.length() != 0
							&& var4x.length() != 0) {
						if (!var2x
								.equals(AdvancedSettingActivity.this.mDevice.viewPassword)) {
							Toast.makeText(
									AdvancedSettingActivity.this,
									AdvancedSettingActivity.this
											.getText(
													R.string.page10_tips_old_password_is_wrong)
											.toString(), 0).show();
						} else if (!var3.equals(var4x)) {
							Toast.makeText(
									AdvancedSettingActivity.this,
									AdvancedSettingActivity.this
											.getText(
													R.string.page10_tips_new_passwords_do_not_match)
											.toString(), 0).show();
 						} 
							else {
								boolean ret[] =  UbiaUtil.validatepassword(var3.trim());
								
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
								
							 
								if (AdvancedSettingActivity.this.mCamera != null) {
//									
									mCameraManagerment.userIPCSetPassWord(AdvancedSettingActivity.this.mCamera.getmDevUID(),
											var2x, var3,mDevice.nickName);
//									
								}
	//
								AdvancedSettingActivity.newPassword = var3;
								AdvancedSettingActivity.isModifyPassword = true;
								var2.dismiss();
							}
					} else {
						Toast.makeText(
								AdvancedSettingActivity.this,
								AdvancedSettingActivity.this.getText(
										R.string.page10_tips_all_field_can_not_empty)
										.toString(), 0).show();
					}
				}
			});
			var8.setOnClickListener(new OnClickListener() {
				public void onClick(View var1) {
					var2.dismiss();
				}
			});
			var2.show();
		}
	};
	private Button btnOK;
	private OnClickListener btnOKOnClickListener = new OnClickListener() {
		public void onClick(View var1) {
			AdvancedSettingActivity.this.quit(true);
		}
	};
	
	private Runnable myrunable = new Runnable() {

		@Override
		public void run() {
			if(progressDialog.isShowing())
				progressDialog.dismiss();
			Toast.makeText(
					AdvancedSettingActivity.this,	AdvancedSettingActivity.this.getText(
							R.string.page10_success_to_update_device)
							.toString(), Toast.LENGTH_LONG).show();
		}
	};

	private boolean changeStatus = false;
	protected int TimeZone;
	protected int enableDST;//夏令时
	protected int TimeZoneEnable;
	private Handler handler = new Handler() {
		public void handleMessage(Message var1) {
			byte[] var2 = var1.getData().getByteArray("data");

			int var28;
			switch (var1.what) {
			case  AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FIRMWARE_UPDATE_CHECK_RSP:
				  mProgressBar.dismiss();
					if(var2.length<8)
					{	
						Toast.makeText(
								AdvancedSettingActivity.this,
								AdvancedSettingActivity.this.getText(
										R.string.page10_failed_to_update_device)
										.toString(), Toast.LENGTH_LONG).show();
						return;
					}else
					{
						String tmpstring = AdvancedSettingActivity.this.getText(R.string.page10_u_firmware_updating).toString();
						progressDialog.setMessage(tmpstring+Packet.byteArrayToInt_Little(var2, 4) +"%");
						//Toast.makeText(
						//		AdvancedSettingActivity.this,"Upgrating..."+Packet.byteArrayToInt_Little(var2, 4) +"%", Toast.LENGTH_LONG).show();
						Log.v("lastversion","AVIOCTRLDEFs Updating   "+Packet.byteArrayToInt_Little(var2, 4) );
						if(Packet.byteArrayToInt_Little(var2, 4)>=100){
							//this.postDelayed(myrunable, 3000);
							if(progressDialog.isShowing()){
								progressDialog.dismiss();
								Toast.makeText(
										AdvancedSettingActivity.this,AdvancedSettingActivity.this.getText(
												R.string.page10_u_firmware_update_toast)
												.toString(), Toast.LENGTH_LONG).show();
							}
						}
							
					}
				
				break;
			case  AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FIRMWARE_UPDATE_RSP:
				  mProgressBar.dismiss();
				if(var2.length<4)
				{	
					Toast.makeText(
							AdvancedSettingActivity.this,
							AdvancedSettingActivity.this.getText(
									R.string.page10_failed_to_update_device)
									.toString(), Toast.LENGTH_LONG).show();
					return;
				}
				else
				{
					showDialog();
					//Toast.makeText(
					//		AdvancedSettingActivity.this,"Upgrating ...", Toast.LENGTH_LONG).show();
					Log.v("lastversion","AVIOCTRLDEFs IOTYPE_USER_IPCAM_FIRMWARE_UPDATE_RSP=   "+Packet.byteArrayToInt_Little(var2, 0) );
				}
				break;	
			case 34:
				Log.v("lastversion","versionz="+versionz);
				Log.v("lastversion","versionloacl="+versionloacl); 
				
				if(  versionz > versionloacl)
					{
						Log.v("lastversion","versionz >versionloacl"  );
						panelUpdateSetting.setVisibility(View.VISIBLE);
					
					}
				else
				{
					Log.v("lastversion","versionz < versionloacl"  );
					panelUpdateSetting.setVisibility(View.VISIBLE);
				}
				break;

			case 1:
				AdvancedSettingActivity.this.txtWiFiSSID.setText(AdvancedSettingActivity.this.getText(R.string.advancesettingactivity_none));
				AdvancedSettingActivity.this.txtWiFiSSID.setTypeface((Typeface) null, 1);
				AdvancedSettingActivity.this.txtWiFiStatus.setText(AdvancedSettingActivity.this.getText(R.string.page10_tips_wifi_remote_device_timeout));
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
				    spinPIRSetStatus.setEnabled(true);
				    pirvalue =true;
					Log.i("deviceinfo", "IOTYPE_USER_IPCAM_GETPIR_RESP..............="+var28);
				
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_OSD_RESP:
				// mProgressBar.dismiss();
				var28 = var2[9];
				spinosdSetStatus.setEnabled(true);
				 
				if(var28<=1&&var28>=0)
				{
					spinosdSetStatus.setSelection(var28);
					spinosdSetStatus.setEnabled(true);
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
				    spinPIRSetStatus.setEnabled(true);
				    pirvalue =true;
				    spinPIRSetStatus.setSelection(var28);
					Log.i("deviceinfo", "IOTYPE_USER_IPCAM_GETPIR_RESP..............="+var28);
				
				break;	
				
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETVOLUME_RESP:
				 mProgressBar.dismiss();
				  var28 = Packet.byteArrayToInt_Little(var2, 4);
				  btnModifyVolume.setEnabled(true);
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
				  btnModifyVolume.setEnabled(true);
				 
 
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
						panelUpdateSetting.setVisibility(View.VISIBLE);
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
							   Log.v("deviceinfo","    videoquality=  "+videoquality+"     videoflips= "+videoflips+"     envmode= "+envmode+"      motionsensitivity="+motionsensitivity+
									   "      alarmmode="+alarmmode+"     recordmode= "+recordmode+"      audioCodec="+audioCodec+"      pirsetting:"+pirsetting +"    acPowerFreq:"+acPowerFreq);

							  
								if (videoquality >= 0 && videoquality <= 5) {
									AdvancedSettingActivity.this.spinVideoQuality.setSelection(
											videoquality - 1, true);
									AdvancedSettingActivity.this.spinVideoQuality
											.setEnabled(true);
									AdvancedSettingActivity.this.mVideoQuality = videoquality;
								}
								if(alarmmode==0)
								{
									 spinnotifyMode.setSelection(0);//关闭声音
								}
								else{
									 spinnotifyMode.setSelection(1);
								}
								Log.i("deviceinfo", "录像模式........00......=" + recordmode);
								if (recordmode >= 0 && recordmode <= 2) {
									AdvancedSettingActivity.this.spinRecordingMode.setSelection(recordmode);
									AdvancedSettingActivity.this.spinRecordingMode.setEnabled(true);
									AdvancedSettingActivity.this.mRecordType = recordmode;							
								}
								Log.i("deviceinfo", "移动侦测录像获取模式........11......=" + motionsensitivity);
								if (motionsensitivity == 0) {
									AdvancedSettingActivity.this.spinMotionDetection.setSelection(0);
									AdvancedSettingActivity.this.mMotionDetection = 0;
								}else{
									AdvancedSettingActivity.this.spinMotionDetection.setSelection(1);
									AdvancedSettingActivity.this.mMotionDetection = 1;
								}
								AdvancedSettingActivity.this.spinMotionDetection.setEnabled(true);

								if (envmode >=0  && envmode <=5 && spinEnvironmentMode!=null ) {
									AdvancedSettingActivity.this.spinEnvironmentMode
											.setSelection(envmode);
									AdvancedSettingActivity.this.spinEnvironmentMode
											.setEnabled(true);
									AdvancedSettingActivity.this.mEnvMode = envmode;
								}
								if (videoflips >= 0 && videoflips <= 3) {
									AdvancedSettingActivity.this.spinVideoFlip.setSelection(
											videoflips );
									AdvancedSettingActivity.this.spinVideoFlip.setEnabled(true);
									AdvancedSettingActivity.this.mVideoFlip = videoflips;
								}
								recordvalue = true;
				    	}
				    	byte[] deviceinfovar21 = new byte[16];
						byte[] deviceinfovar22 = new byte[16];
						System.arraycopy(var2, 0, deviceinfovar21, 0, 16);
						System.arraycopy(var2, 16, deviceinfovar22, 0, 16);
						String deviceinfovar23 = AdvancedSettingActivity.getString(deviceinfovar21);
						String deviceinfovar24 = AdvancedSettingActivity.getString(deviceinfovar22);
						int deviceinfovar25 = Packet.byteArrayToInt_Little(var2, 32);
						int deviceinfovar26 = Packet.byteArrayToInt_Little(var2, 44);
						AdvancedSettingActivity.this.mTotalSize = Packet.byteArrayToInt_Little(var2, 40);
						AdvancedSettingActivity.this.txtDeviceModel.setText(deviceinfovar23);
						AdvancedSettingActivity.this.txtDeviceVersion.setText(AdvancedSettingActivity.this.getVersion(deviceinfovar25));
						AdvancedSettingActivity.this.txtVenderName.setText(deviceinfovar24);
						AdvancedSettingActivity.this.txtStorageFreeSize.setText(String.valueOf(deviceinfovar26) + " MB");

				    	Log.i("deviceinfo", "got IOTYPE_USER_IPCAM_GET_ADVANCESETTINGS_RESP 961.............+String.valueOf(deviceinfovar26)."+String.valueOf(deviceinfovar26)+"AdvancedSettingActivity.this.mTotalSize="+AdvancedSettingActivity.this.mTotalSize);
						AdvancedSettingActivity.this.txtDeviceModel.setTypeface((Typeface) null, 0);
						AdvancedSettingActivity.this.txtDeviceVersion.setTypeface((Typeface) null, 0);
						AdvancedSettingActivity.this.txtVenderName.setTypeface((Typeface) null, 0);
						AdvancedSettingActivity.this.txtStorageTotalSize.setTypeface((Typeface) null, 0);
						AdvancedSettingActivity.this.txtStorageFreeSize.setTypeface((Typeface) null, 0);
						
						if(AdvancedSettingActivity.this.mTotalSize == -1){
							AdvancedSettingActivity.this.txtStorageTotalSize.setText(R.string.page10_u_sd_not_formatted_txt);
						}else{
							AdvancedSettingActivity.this.txtStorageTotalSize.setText(String
								.valueOf(AdvancedSettingActivity.this.mTotalSize)
								+ " MB");
						}

						if (AdvancedSettingActivity.this.mTotalSize != 0
								&& AdvancedSettingActivity.this.mCamera
										.getSDCardFormatSupported(0)) {
							AdvancedSettingActivity.this.pnlFormatSDCard
									.setVisibility(0);
						} else {
							AdvancedSettingActivity.this.pnlFormatSDCard
									.setVisibility(8);
						}
					int data = Packet.byteArrayToInt_Little(var2, 25*4);
					
					TimeZone = (int)var2[69];
					Log.v("test","TimeZone = "+TimeZone);
										 
					spinTimeZone.setEnabled(true);
				 	spinTimeZone.setSelection(TimeZone+12);
					//取值（夏令时）
				 	enableDST=var2[73];
				 	if(enableDST != 0){
				 		toggleBtnDST.setChecked(true);
				 	}
					spinosdSetStatus.setEnabled(true);
					int osdenable = (int)var2[70]; 
					if(osdenable<=1 && osdenable>=0)
					{
						spinosdSetStatus.setSelection(osdenable);
						spinosdSetStatus.setEnabled(true);
						osdvalue =true;
					}

					if (pirsetting >=0  && pirsetting <=5 && null!=power_spiner ) {
						AdvancedSettingActivity.this.pirsetting = pirsetting;
						AdvancedSettingActivity.this.
						spinPIRSetStatus.setSelection(pirsetting);
						AdvancedSettingActivity.this.spinPIRSetStatus .setEnabled(true);
			
					}
					if (acPowerFreq >=0  && acPowerFreq <=1 && power_spiner!=null ) {
						AdvancedSettingActivity.this.acPowerFreq = acPowerFreq;
						AdvancedSettingActivity.this.
						power_spiner.setSelection(acPowerFreq);
						AdvancedSettingActivity.this.power_spiner .setEnabled(true);
					
					}
				    }
				    break;
			case 819:
				if (var2[0] == 0) {
					Toast.makeText(
							AdvancedSettingActivity.this,
							AdvancedSettingActivity.this.getText(
									R.string.page10_tips_modify_security_code_ok)
									.toString(), 0).show();
				}
				break;
			case 833:
//				int var29 = Packet.byteArrayToInt_Little(var2, 0);
//				int var30 = AVIOCTRLDEFs.SWifiAp.getTotalSize();
//				AdvancedSettingActivity.m_wifiList.clear();
//				AdvancedSettingActivity.this.txtWiFiSSID
//						.setText(AdvancedSettingActivity.this
//								.getText(R.string.none));
//				AdvancedSettingActivity.this.txtWiFiSSID.setTypeface(
//						(Typeface) null, 1);
//				AdvancedSettingActivity.this.txtWiFiStatus
//						.setText(AdvancedSettingActivity.this
//								.getText(R.string.tips_wifi_remote_device_timeout));
//				if (var29 > 0 && var2.length >= 40) {
//					for (int var31 = 0; var31 < var29; ++var31) {
//						byte[] var32 = new byte[32];
//						System.arraycopy(var2, 4 + var31 * var30, var32, 0, 32);
//						byte var33 = var2[32 + 4 + var31 * var30];
//						byte var34 = var2[33 + 4 + var31 * var30];
//						byte var35 = var2[34 + 4 + var31 * var30];
//						byte var36 = var2[35 + 4 + var31 * var30];
//						AdvancedSettingActivity.m_wifiList
//								.add(new AVIOCTRLDEFs.SWifiAp(var32, var33,
//										var34, var35, var36));
//						if (var36 == 1) {
//							AdvancedSettingActivity.this.txtWiFiSSID
//									.setText(AdvancedSettingActivity
//											.getString(var32));
//							AdvancedSettingActivity.this.txtWiFiSSID
//									.setTypeface((Typeface) null, 1);
//							AdvancedSettingActivity.this.txtWiFiStatus
//									.setText(AdvancedSettingActivity.this
//											.getText(R.string.tips_wifi_connected));
//							AdvancedSettingActivity.this.changeStatus = true;
//							AdvancedSettingActivity.this.stopCheck = true;
//							AdvancedSettingActivity.this.m_threadCheck = null;
//						} else if (var36 == 2) {
//							AdvancedSettingActivity.this.txtWiFiSSID
//									.setText(AdvancedSettingActivity
//											.getString(var32));
//							AdvancedSettingActivity.this.txtWiFiSSID
//									.setTypeface((Typeface) null, 1);
//							AdvancedSettingActivity.this.txtWiFiStatus
//									.setText(AdvancedSettingActivity.this
//											.getText(R.string.tips_wifi_wrongpassword));
//							AdvancedSettingActivity.this.changeStatus = true;
//							AdvancedSettingActivity.this.stopCheck = true;
//							AdvancedSettingActivity.this.m_threadCheck = null;
//						} else if (var36 == 3) {
//							AdvancedSettingActivity.this.txtWiFiSSID
//									.setText(AdvancedSettingActivity
//											.getString(var32));
//							AdvancedSettingActivity.this.txtWiFiSSID
//									.setTypeface((Typeface) null, 1);
//							AdvancedSettingActivity.this.txtWiFiStatus
//									.setText(AdvancedSettingActivity.this
//											.getText(R.string.tips_wifi_weak_signal));
//							AdvancedSettingActivity.this.changeStatus = true;
//							AdvancedSettingActivity.this.stopCheck = true;
//							AdvancedSettingActivity.this.m_threadCheck = null;
//						} else if (var36 == 4) {
//							AdvancedSettingActivity.this.txtWiFiSSID
//									.setText(AdvancedSettingActivity
//											.getString(var32));
//							AdvancedSettingActivity.this.txtWiFiSSID
//									.setTypeface((Typeface) null, 1);
//							AdvancedSettingActivity.this.txtWiFiStatus
//									.setText(AdvancedSettingActivity.this
//											.getText(R.string.tips_wifi_ready));
//							AdvancedSettingActivity.this.changeStatus = true;
//							AdvancedSettingActivity.this.stopCheck = true;
//							AdvancedSettingActivity.this.m_threadCheck = null;
//						}
//					}
//				}
//
//				AdvancedSettingActivity.this.btnManageWiFiNetworks
//						.setEnabled(true);
				break;
			case 835:
				Handler var18 = AdvancedSettingActivity.this.handler;
				Runnable var19 = new Runnable() {
					public void run() {
						mCameraManagerment.userIPCListWifiAP(AdvancedSettingActivity.this.mCamera.getmUID());
						
					}
				};
				var18.postDelayed(var19, 30000L);
				break;
			case 837:
//				AVIOCTRLDEFs.SWifiAp.getTotalSize();
//				byte[] var16 = new byte[32];
//				System.arraycopy(var2, 0, var16, 0, 32);
//				byte var17 = var2[67];
//				if (var17 == 0) {
//					AdvancedSettingActivity.this.txtWiFiSSID
//							.setText(AdvancedSettingActivity.getString(var16));
//					AdvancedSettingActivity.this.txtWiFiSSID.setTypeface(
//							(Typeface) null, 1);
//					AdvancedSettingActivity.this.txtWiFiStatus
//							.setText(AdvancedSettingActivity.this
//									.getText(R.string.tips_wifi_remote_device_timeout));
//					AdvancedSettingActivity.this.changeStatus = true;
//				} else if (var17 == 1) {
//					AdvancedSettingActivity.this.txtWiFiSSID
//							.setText(AdvancedSettingActivity.getString(var16));
//					AdvancedSettingActivity.this.txtWiFiSSID.setTypeface(
//							(Typeface) null, 1);
//					AdvancedSettingActivity.this.txtWiFiStatus
//							.setText(AdvancedSettingActivity.this
//									.getText(R.string.tips_wifi_connected));
//					AdvancedSettingActivity.this.changeStatus = true;
//					AdvancedSettingActivity.this.stopCheck = true;
//					AdvancedSettingActivity.this.m_threadCheck = null;
//				} else if (var17 == 2) {
//					AdvancedSettingActivity.this.txtWiFiSSID
//							.setText(AdvancedSettingActivity.getString(var16));
//					AdvancedSettingActivity.this.txtWiFiSSID.setTypeface(
//							(Typeface) null, 1);
//					AdvancedSettingActivity.this.txtWiFiStatus
//							.setText(AdvancedSettingActivity.this
//									.getText(R.string.tips_wifi_wrongpassword));
//					AdvancedSettingActivity.this.changeStatus = true;
//					AdvancedSettingActivity.this.stopCheck = true;
//					AdvancedSettingActivity.this.m_threadCheck = null;
//				} else if (var17 == 3) {
//					AdvancedSettingActivity.this.txtWiFiSSID
//							.setText(AdvancedSettingActivity.getString(var16));
//					AdvancedSettingActivity.this.txtWiFiSSID.setTypeface(
//							(Typeface) null, 1);
//					AdvancedSettingActivity.this.txtWiFiStatus
//							.setText(AdvancedSettingActivity.this
//									.getText(R.string.tips_wifi_weak_signal));
//					AdvancedSettingActivity.this.changeStatus = true;
//					AdvancedSettingActivity.this.stopCheck = true;
//					AdvancedSettingActivity.this.m_threadCheck = null;
//				} else if (var17 == 4) {
//					AdvancedSettingActivity.this.txtWiFiSSID
//							.setText(AdvancedSettingActivity.getString(var16));
//					AdvancedSettingActivity.this.txtWiFiSSID.setTypeface(
//							(Typeface) null, 1);
//					AdvancedSettingActivity.this.txtWiFiStatus
//							.setText(AdvancedSettingActivity.this
//									.getText(R.string.tips_wifi_ready));
//					AdvancedSettingActivity.this.changeStatus = true;
//					AdvancedSettingActivity.this.stopCheck = true;
//					AdvancedSettingActivity.this.m_threadCheck = null;
//				}
//
//				AdvancedSettingActivity.this.btnManageWiFiNetworks
//						.setEnabled(true);
				break;
			case 839:
//				AVIOCTRLDEFs.SWifiAp.getTotalSize();
//				byte[] var13 = new byte[32];
//				System.arraycopy(var2, 0, var13, 0, 32);
//				byte var14 = var2[99];
//				if (var14 == 0) {
//					AdvancedSettingActivity.this.txtWiFiSSID
//							.setText(AdvancedSettingActivity.getString(var13));
//					AdvancedSettingActivity.this.txtWiFiSSID.setTypeface(
//							(Typeface) null, 1);
//					AdvancedSettingActivity.this.txtWiFiStatus
//							.setText(AdvancedSettingActivity.this
//									.getText(R.string.tips_wifi_remote_device_timeout));
//					AdvancedSettingActivity.this.changeStatus = true;
//				} else if (var14 == 1) {
//					AdvancedSettingActivity.this.txtWiFiSSID
//							.setText(AdvancedSettingActivity.getString(var13));
//					AdvancedSettingActivity.this.txtWiFiSSID.setTypeface(
//							(Typeface) null, 1);
//					AdvancedSettingActivity.this.txtWiFiStatus
//							.setText(AdvancedSettingActivity.this
//									.getText(R.string.tips_wifi_connected));
//					AdvancedSettingActivity.this.changeStatus = true;
//					AdvancedSettingActivity.this.stopCheck = true;
//					AdvancedSettingActivity.this.m_threadCheck = null;
//				} else if (var14 == 2) {
//					AdvancedSettingActivity.this.txtWiFiSSID
//							.setText(AdvancedSettingActivity.getString(var13));
//					AdvancedSettingActivity.this.txtWiFiSSID.setTypeface(
//							(Typeface) null, 1);
//					AdvancedSettingActivity.this.txtWiFiStatus
//							.setText(AdvancedSettingActivity.this
//									.getText(R.string.tips_wifi_wrongpassword));
//					AdvancedSettingActivity.this.changeStatus = true;
//					AdvancedSettingActivity.this.stopCheck = true;
//					AdvancedSettingActivity.this.m_threadCheck = null;
//				} else if (var14 == 3) {
//					AdvancedSettingActivity.this.txtWiFiSSID
//							.setText(AdvancedSettingActivity.getString(var13));
//					AdvancedSettingActivity.this.txtWiFiSSID.setTypeface(
//							(Typeface) null, 1);
//					AdvancedSettingActivity.this.txtWiFiStatus
//							.setText(AdvancedSettingActivity.this
//									.getText(R.string.tips_wifi_weak_signal));
//					AdvancedSettingActivity.this.changeStatus = true;
//					AdvancedSettingActivity.this.stopCheck = true;
//					AdvancedSettingActivity.this.m_threadCheck = null;
//				} else if (var14 == 4) {
//					AdvancedSettingActivity.this.txtWiFiSSID
//							.setText(AdvancedSettingActivity.getString(var13));
//					AdvancedSettingActivity.this.txtWiFiSSID.setTypeface(
//							(Typeface) null, 1);
//					AdvancedSettingActivity.this.txtWiFiStatus
//							.setText(AdvancedSettingActivity.this
//									.getText(R.string.tips_wifi_ready));
//					AdvancedSettingActivity.this.changeStatus = true;
//					AdvancedSettingActivity.this.stopCheck = true;
//					AdvancedSettingActivity.this.m_threadCheck = null;
//				}
//
//				AdvancedSettingActivity.this.btnManageWiFiNetworks
//						.setEnabled(true);
				break;
			case 867:
//				byte var38 = var2[4];
//				if (var38 >= 0 && var38 <= 3) {
//					AdvancedSettingActivity.this.spinEnvironmentMode
//							.setSelection(var38, true);
//					AdvancedSettingActivity.this.spinEnvironmentMode
//							.setEnabled(true);
//					AdvancedSettingActivity.this.mEnvMode = var38;
//				}
				break;
			case 883:
//				byte var39 = var2[4];
//				if (var39 >= 0 && var39 <= 3) {
//					AdvancedSettingActivity.this.spinVideoFlip.setSelection(
//							var39, true);
//					AdvancedSettingActivity.this.spinVideoFlip.setEnabled(true);
//					AdvancedSettingActivity.this.mVideoFlip = var39;
//				}
				break;
			case 897:
				if (var2[4] == 0) {
                    Log.v("formatresp", "status:"+ var2[4] + " progress:"+ var2[5]);
					if(var2[5]<100){
						AdvancedSettingActivity.this.mTotalSize = 0;
						AdvancedSettingActivity.this.txtStorageTotalSize.setText(String
								.valueOf(AdvancedSettingActivity.this.mTotalSize)
								+ " MB");
						AdvancedSettingActivity.this.txtStorageFreeSize.setText(String
								.valueOf(AdvancedSettingActivity.this.mTotalSize)
								+ " MB");
						
						Toast.makeText(
								AdvancedSettingActivity.this,
								AdvancedSettingActivity.this.getText(
										R.string.page10_tips_format_sdcard_ongoing)
										.toString(), 0).show();
					}else if(var2[5]==100){
						AdvancedSettingActivity.this.mTotalSize = Packet
								.byteArrayToInt_Little(var2, 8);
						int freesize = Packet.byteArrayToInt_Little(var2, 12);
						
						AdvancedSettingActivity.this.txtStorageTotalSize.setText(String
								.valueOf(AdvancedSettingActivity.this.mTotalSize)
								+ " MB");
						AdvancedSettingActivity.this.txtStorageFreeSize.setText(String
								.valueOf(freesize) + " MB");
						  Log.v("formatresp", "status:"+ var2[4] + " progress:"+ var2[5]);
						Toast.makeText(
							AdvancedSettingActivity.this,
							AdvancedSettingActivity.this.getText(
									R.string.page10_tips_format_sdcard_success)
									.toString(), 0).show();
						
					}
				} else {
					Toast.makeText(
							AdvancedSettingActivity.this,
							AdvancedSettingActivity.this.getText(
									R.string.page10_tips_format_sdcard_failed)
									.toString(), 0).show();
				}
				break;
			case 929:
				 TimeZoneEnable = Packet.byteArrayToInt_Little(var2,  4);
				  enableDST= Packet.byteArrayToInt_Little(var2, 4);
		
				  TimeZone = Packet.byteArrayToInt_Little(var2, 2*4);
		
				 	spinTimeZone.setEnabled(true);
				 	 spinTimeZone.setSelection(TimeZone+12);
			
				 Log.v("test","929  TimeZone = "+TimeZone+"   TimeZoneEnable ="+TimeZoneEnable);

				break;
			case   AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_ALARMMODE_RESP:
				if(var2[4]!=0){
					Toast.makeText(
							AdvancedSettingActivity.this,
							AdvancedSettingActivity.this.getText(
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
	private int mPostition = -1;
	private int mRecordType = -1;
	private int mTotalSize = -1;
	private int mVideoFlip = -1;
	private int pirsetting = -1;
	private int acPowerFreq = -1;
	
	private int mVideoQuality = -1;
	protected ThreadCheck m_threadCheck = null;
	private int mtotalMinute = 0;
	private LinearLayout pnlDeviceInfo;
	private LinearLayout pnlEnvMode,panelpowermode;
	private LinearLayout pnlEventSeting;
	private LinearLayout pnlVolumeSetting;
	private LinearLayout pnlPIRSeting;
	private LinearLayout pnlFormatSDCard;
	private LinearLayout pnlRecordSetting;
	private LinearLayout pnlTimeZone;
	private LinearLayout pnlVideoFlip;
	private LinearLayout pnlVideoQuality;
	private LinearLayout pnlWiFiSetting;
	private LinearLayout panelUpdateSetting;
	private LinearLayout panelnotifySetting;
	private LinearLayout panelPutMode_switch;
	private Spinner spinnotifyMode;
	private Spinner putmode_spiner,power_spiner;
	private Spinner spinEnvironmentMode;
	private Spinner spinEventNotification;
//	private Spinner spinSpeakVolumeSetting;
//	private Spinner spinMICVolumeSetting;
	private Spinner	spinPIRSetStatus;
	private Spinner spinMotionDetection;
	private Spinner spinRecordingMode;
	private Spinner spinTimeZone;
	private Spinner spinVideoFlip;
	private Spinner spinVideoQuality;
	boolean stopCheck = true;
	private byte[] szTimeZoneString = null;
	private long t1 = 0L;
	private String[] timeZoneList = {"-12","-11","-10","-9","-8","-7","-6","-5","-4","-3","-2","-1","GMT","1","2","3","4","5","6","7","8","9","10","11","12"};
	private String[] timeZoneNameList = null;
	private TextView txtDeviceModel;
	private TextView txtDeviceVersion;
	private TextView txtStorageFreeSize;
	private TextView txtStorageTotalSize;
	private TextView txtVenderName;
	private TextView txtWiFiSSID;
	private TextView txtWiFiStatus;
	private Spinner spinosdSetStatus;
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
		return var3.toString();
	}

	private void initDeviceInfo() {
		this.txtDeviceModel.setText(this.getText(R.string.page10_tips_wifi_retrieving));
		this.txtDeviceVersion.setText(this.getText(R.string.page10_tips_wifi_retrieving));
		this.txtVenderName.setText(this.getText(R.string.page10_tips_wifi_retrieving));
		this.txtStorageTotalSize.setText(this.getText(R.string.page10_tips_wifi_retrieving));
		this.txtStorageFreeSize.setText(this.getText(R.string.page10_tips_wifi_retrieving));
		this.txtDeviceModel.setTypeface((Typeface) null, 3);
		this.txtDeviceVersion.setTypeface((Typeface) null, 3);
		this.txtVenderName.setTypeface((Typeface) null, 3);
		this.txtStorageTotalSize.setTypeface((Typeface) null, 3);
		this.txtStorageFreeSize.setTypeface((Typeface) null, 3);
		if (this.mCamera != null) {
//			Log.i("deviceinfo", "request IOTYPE_USER_IPCAM_DEVINFO_REQ......");
//			mCameraManagerment.userIPCDeviceInfo(this.mCamera.getmUID());

//			try {
//				Thread.sleep(150);
//			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			Log.i("deviceinfo", "request IOTYPE_USER_IPCAM_GET_ADVANCESETTINGS_REQ......");
			mCameraManagerment.userIPCGetAdvanceSetting(this.mCamera.getmUID());

		}

	}

	private void initEventNotification() {
		ArrayAdapter var1 = ArrayAdapter.createFromResource(this,
				R.array.event_notification, 17367048);
		var1.setDropDownViewResource(17367049);
		this.spinEventNotification.setAdapter(var1); 
	}
	private void initSpeakVolume() { 
		mProgressBar.show();
		mCameraManagerment.userIPCGetVolu(this.mCamera.getmUID(), this.mDevice.getChannelIndex());
		
		mCameraManagerment.userIPCGetMicVolu(this.mCamera.getmUID(), this.mDevice.getChannelIndex());
	
		 
	}
	
	
	 
	private void initMotionDetection() {
		ArrayAdapter var1 = ArrayAdapter.createFromResource(this,R.array.motion_detection, 17367048);
		var1.setDropDownViewResource(17367049);
	   
		this.spinMotionDetection.setAdapter(var1);
		this.spinMotionDetection.setEnabled(false);
		this.spinMotionDetection.setSelection(0, false);
		this.spinMotionDetection.setOnItemSelectedListener( new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
			
				if (mCamera != null ) {
					mCameraManagerment.userIPCSetMotionDetect(mCamera.getmUID(),0,arg2);
					
					Log.i("deviceinfo","setOnItemSelectedListener...设置移动侦测级别.......="
									+ arg2*25);
				}else{
					getHelper().showMessage(R.string.page10_fail_to_connect_camera);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			  

			}
		
		});
//		if (this.mCamera != null) {
//			motiondetectionvalue = false;
//			this.mCamera.sendIOCtrl(0, 806,
//					AVIOCTRLDEFs.SMsgAVIoctrlGetMotionDetectReq
//							.parseContent(this.mDevice.getChannelIndex()));
//		}

	}

	private void initRecordingMode() {
		ArrayAdapter var1 = ArrayAdapter.createFromResource(this,
				R.array.recording_mode, 17367048);
		var1.setDropDownViewResource(17367049);
		this.spinRecordingMode.setAdapter(var1);
		this.spinRecordingMode.setEnabled(false);
		this.spinRecordingMode.setSelection(0, false);
		recordvalue  = false;
		this.spinRecordingMode.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (mCamera != null && recordvalue) {
					mCameraManagerment.userIPCSetRecord(mCamera.getmUID(), mDevice.getChannelIndex(), arg2);
					
					Log.i("deviceinfo",
							"setOnItemSelectedListener...设置录像模式.......="
									+ arg2 );
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		}  );
 

	}

	private void initPIRSetStatusMode() {
		ArrayAdapter var1 = ArrayAdapter.createFromResource(this,
				R.array.pirmode_quality, 17367048);
		var1.setDropDownViewResource(17367049);
		this.spinPIRSetStatus.setAdapter(var1);
	 	 this.spinPIRSetStatus.setEnabled(true);
	
		spinPIRSetStatus.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Log.i("IOTCamera", "设置spinPIRSetStatus = "+( arg2));
				mCameraManagerment.userIPCsetPir(		mDevice.UID,arg2);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
		private void initosdSetStatusMode() {
		ArrayAdapter var1 = ArrayAdapter.createFromResource(this,
				R.array.osd_mode,  android.R.layout.simple_spinner_item);
		var1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
		this.spinosdSetStatus.setAdapter(var1);
		//this.spinosdSetStatus.setEnabled(false);
		
		if (this.mCamera != null) {
//			this.mCamera.sendIOCtrl(0,
//					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_OSD_REQ,
//					AVIOCTRLDEFs.SMsgAVIoctrlGetOsdReq.parseContent(
//							this.mDevice.getChannelIndex() ));
		}

	}
		
		private void initputmode_mode() {
			
			
			this.putmode_spiner = (Spinner) this
					.findViewById(R.id.putmode_spiner);
		
			this.panelPutMode_switch = (LinearLayout) this
					.findViewById(R.id.panelPutMode_switch);
			this.panelPutMode_switch.setVisibility(View.VISIBLE);
			this.panelPutMode_switch.setEnabled(true);
			
			if (UbiaApplication.DEFAULT_NO_PUTMODE){
				this.panelPutMode_switch.setVisibility(View.GONE);
				this.panelPutMode_switch.setEnabled(false);
			}else{
				this.panelPutMode_switch.setVisibility(View.VISIBLE);
				this.panelPutMode_switch.setEnabled(true);
			}
			ArrayAdapter var1 = ArrayAdapter.createFromResource(this,
					R.array.cameraputmodel,  android.R.layout.simple_spinner_item);
			var1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
			this.putmode_spiner.setAdapter(var1);
			putmode_spiner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Log.i("IOTCamera", "设置putmode_spiner = "+( arg2));
					if(arg2==0){
						mDevice.installmode = VRConfig.CameraPutModelFaceDown; 
					}else	if(arg2==1){
						mDevice.installmode  = VRConfig.CameraPutModelFaceUp;  
					}else	if(arg2==2){
						mDevice.installmode  = VRConfig.CameraPutModelFaceFront; 
					}
					(new DatabaseManager(AdvancedSettingActivity.this)).updateDeviceInstallmodeByUID(
							mDevice.UID,mDevice.installmode );
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
		if (mDevice.installmode == VRConfig.CameraPutModelFaceDown)
			putmode_spiner.setSelection(0);
		else if (mDevice.installmode == VRConfig.CameraPutModelFaceUp)
			putmode_spiner.setSelection(1);
		else if (mDevice.installmode == VRConfig.CameraPutModelFaceFront)
			putmode_spiner.setSelection(2);
		}
		
		
		private void initpanelpowermode() {
			this.power_spiner = (Spinner) this
					.findViewById(R.id.power_spiner);
		
			this.panelpowermode = (LinearLayout) this
					.findViewById(R.id.panelpowermode);
			this.panelpowermode.setVisibility(View.VISIBLE);
			this.panelpowermode.setEnabled(true);
			ArrayAdapter var1 = ArrayAdapter.createFromResource(this,
					R.array.powerfreq,  android.R.layout.simple_spinner_item);
			var1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
			this.power_spiner.setAdapter(var1);
			power_spiner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Log.i("IOTCamera", "设置userIPCsetACPPower = "+( arg2));
				 
					mCameraManagerment.userIPCsetACPPower(mCamera.getmUID(), arg2);
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
		}
		private void initnotify_mode() {
			this.spinnotifyMode = (Spinner) this
					.findViewById(R.id.notify_spiner); 
			this.panelnotifySetting = (LinearLayout) this
					.findViewById(R.id.panelnotify_switch);
			this.panelnotifySetting.setVisibility(View.VISIBLE);
			this.panelnotifySetting.setEnabled(true);
			ArrayAdapter var1 = ArrayAdapter.createFromResource(this,
					R.array.notify_mode,  android.R.layout.simple_spinner_item);
			var1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
			this.spinnotifyMode.setAdapter(var1);
			spinnotifyMode.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					Log.i("IOTCamera", "设置swhChangePushMute = "+( arg2));
					byte data[] = new byte[8];
					data[4]= (byte) arg2;
					mCameraManagerment.userIPCSetAlermMode(mCamera.getmUID(), data);
				}
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
		}
		
		
	private void initTimeZone() {
 
		ArrayAdapter var2 = ArrayAdapter.createFromResource(this,
				R.array.time_zone_name,  android.R.layout.simple_spinner_item);
		var2.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
		
		//ArrayAdapter var2 = new ArrayAdapter(this, 17367048, this.timeZoneList);
		//var2.setDropDownViewResource(R.layout.myspinner);
		this.spinTimeZone.setAdapter(var2);
		this.spinTimeZone.setSelection(0,true);
		this.spinTimeZone.setEnabled(false);
		this.spinTimeZone
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView var1, View var2,
							int var3, long var4) {
						if(isAutoSelect){
							isAutoSelect=false;
						}
						else{
							Toast.makeText(
									AdvancedSettingActivity.this,
									AdvancedSettingActivity.this
											.getText(
													R.string.addvancesettingactivity_Time_adjust)
											.toString(), 0).show();
						}
						AdvancedSettingActivity.this.mPostition = var3;
					}
					public void onNothingSelected(AdapterView var1) {
						for (int var2 = 0; var2 < AdvancedSettingActivity.this.timeZoneNameList.length; ++var2) {
							try {
								String	nameSwitch[] = getResources().getStringArray(R.array.time_zone_name);
								if ((new String(
										AdvancedSettingActivity.this.mCamera.getTimeZoneString(), 0,AdvancedSettingActivity.this.mCamera
												.getTimeZoneString().length,
										"utf-8"))
										.indexOf(nameSwitch[var2]) != -1) {
									AdvancedSettingActivity.this.mPostition = var2;
								}
							} catch (UnsupportedEncodingException var4) {
								var4.printStackTrace();
							}
						}
					
					}
					
				});
	}

	private void initVideoSetting() {
		ArrayAdapter var1 = ArrayAdapter.createFromResource(this,
				R.array.video_quality, 17367048);
		var1.setDropDownViewResource(17367049);
		this.spinVideoQuality.setAdapter(var1);
		this.spinVideoQuality.setSelection(2);
		this.spinVideoQuality.setEnabled(false);
		ArrayAdapter var2 = ArrayAdapter.createFromResource(this,
				R.array.video_flip, 17367048);
		var2.setDropDownViewResource(17367049);
		this.spinVideoFlip.setAdapter(var2);
		this.spinVideoFlip.setSelection(0);
		this.spinVideoFlip.setEnabled(false);
		ArrayAdapter var3 = ArrayAdapter.createFromResource(this,
				R.array.environment_mode, 17367048);
		var3.setDropDownViewResource(17367049);
		this.spinEnvironmentMode.setAdapter(var3);
		this.spinEnvironmentMode.setSelection(0);
		this.spinEnvironmentMode.setEnabled(false);
	 
		spinEnvironmentMode.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Log.i("IOTCamera", "设置userIPCsetSceneMode = "+( arg2));
			 
				mCameraManagerment.userIPCsetSceneMode(mCamera.getmUID(), arg2);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		if (this.mCamera != null) {

		//	try {
				//this.mCamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
				//		AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());

//				this.mCamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSTREAMCTRL_REQ,
//						AVIOCTRLDEFs.SMsgAVIoctrlGetStreamCtrlReq
//								.parseContent(this.mDevice.getChannelIndex()));
//				Thread.sleep(150);
//				this.mCamera.sendIOCtrl(0, 882,
//						AVIOCTRLDEFs.SMsgAVIoctrlGetVideoModeReq
//								.parseContent(this.mDevice.getChannelIndex()));
//				Thread.sleep(150);
//				this.mCamera.sendIOCtrl(0, 866,
//						AVIOCTRLDEFs.SMsgAVIoctrlGetEnvironmentReq
//								.parseContent(this.mDevice.getChannelIndex()));
//				Thread.sleep(150);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}

	}

	private void initWiFiSSID() {
		this.txtWiFiSSID.setText(this.getText(R.string.page10_tips_wifi_retrieving));
		this.txtWiFiSSID.setTypeface((Typeface) null, 3);
		this.btnManageWiFiNetworks.setEnabled(false);
		if (this.mCamera != null) {
//			this.mCamera.sendIOCtrl(0, 832,
//					AVIOCTRLDEFs.SMsgAVIoctrlListWifiApReq.parseContent());
		}

	}

	private void quit(boolean var1) {
		int var2 = 1 + this.spinVideoQuality.getSelectedItemPosition();
		int var3 = this.spinVideoFlip.getSelectedItemPosition();
		int var4 = this.spinEnvironmentMode.getSelectedItemPosition();
		int var5 = this.spinRecordingMode.getSelectedItemPosition();
		int var6 = this.spinEventNotification.getSelectedItemPosition();
		int var7 = this.spinMotionDetection.getSelectedItemPosition();
		int var8 = this.spinTimeZone.getSelectedItemPosition();
		String token = getHelper().getConfig(Constants.TOKEN);
		String tokenSecret = getHelper().getConfig(Constants.TOKEN_SECRET);
		String account = getHelper().getConfig(Constants.USER_NAME);
		String password = getHelper().getConfig(Constants.USER_PASSWORD);
		if (var1) {
			this.mDevice.EventNotification = var6;
			(new DatabaseManager(this)).updateDeviceInfoByDBID(
					this.mDevice.DBID, this.mDevice.UID, this.mDevice.nickName,
					"", "", "admin", this.mDevice.viewPassword,
					this.mDevice.EventNotification, this.mDevice.getChannelIndex(),
					this.mDevice.isPublic);
			if (this.mCamera != null) {
				if (this.mVideoQuality != -1 && this.mVideoQuality != var2) {
					mCameraManagerment.userIPCSetStreamCrl(mCamera.getmUID(), this.mDevice.getChannelIndex(), var2);

				}

				if (this.mVideoFlip != -1 && this.mVideoFlip != var3) {
					mCameraManagerment.userIPCSetVideoMode(mCamera.getmUID(), this.mDevice.getChannelIndex(), var3);

				}

				if (this.mEnvMode != -1 && this.mEnvMode != var4) {
					byte newvalue = (byte)var4;
					
					mCameraManagerment.userIPCSetEnv(mCamera.getmUID(), this.mDevice.getChannelIndex(), newvalue);
					
					try {
						Thread.sleep(150);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (this.mPostition != -1) {
					// this.getTimeZonetDate(this.timeZoneList[this.mPostition]);
					
					mCameraManagerment.userIPCSetTimeZone(mCamera.getmUID(), 268, this.enableDST, this.mPostition - 12);
					

					Log.i("deviceinfo", "this.mPostition" + this.mPostition);
					try {
						Thread.sleep(150);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (this.mMotionDetection != -1 && this.mMotionDetection != var7) {
					byte var21;
					if (this.spinMotionDetection.getSelectedItemPosition() == 0) {
						var21 = 0;
					}else{
						var21 = 75;
					}
//					} else if (this.spinMotionDetection
//							.getSelectedItemPosition() == 1) {
//						var21 = 25;
//					} else if (this.spinMotionDetection
//							.getSelectedItemPosition() == 2) {
//						var21 = 50;
//					} else if (this.spinMotionDetection
//							.getSelectedItemPosition() == 3) {
//						var21 = 75;
//					} else {						
//						int var20 = this.spinMotionDetection.getSelectedItemPosition();
//						var21 = 100;
//						if (var20 == 4) {
//							var21 = 100;
//						}
//					}

	/*			this.mCamera.sendIOCtrl(0, 804,
							AVIOCTRLDEFs.SMsgAVIoctrlSetMotionDetectReq
									.parseContent(this.mDevice.getChannelIndex(),
										var21));*/
				}

				if (this.mRecordType != -1 && this.mRecordType != var5) {
					byte var19;
					if (this.spinRecordingMode.getSelectedItemPosition() == 0) {
						var19 = 0;
					} else if (this.spinRecordingMode.getSelectedItemPosition() == 1) {
						var19 = 1;
					} else if (this.spinRecordingMode.getSelectedItemPosition() == 2) {
						var19 = 2;
					} else {
						int var18 = this.spinRecordingMode
								.getSelectedItemPosition();
						var19 = 0;
						if (var18 == 3) {
							var19 = 3;
						}
					}
					try {
						Thread.sleep(150);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					this.mCamera.sendIOCtrl(0, 784,
//							AVIOCTRLDEFs.SMsgAVIoctrlSetRecordReq.parseContent(
//									this.mDevice.getChannelIndex(), var19));
				}
			}
		}

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

			//HttpClient httpClient = new HttpClient(token, tokenSecret);
			// Log.i("url", "password:" + this.mDevice.viewPassword);
			// Log.i("url", "newpassword:" + var16.viewPassword);
//			httpClient.updateDevice(account, this.mDevice.viewPassword,
//					this.mDevice.UID, this.mDevice.nickName,
//					this.mDevice.location, new JsonHttpResponseHandler() {// password
//
//						@Override
//						public void onStart() {
//							super.onStart();
//							// mProgressBar.show();
//						}
//
//						@Override
//						public void onSuccess(int statusCode,
//								JSONObject response) {
//							// mProgressBar.dismiss();
//							Log.d("url",
//									"updateDeviceToServer:"
//											+ response.toString());
//							String result = response.toString();
//							if (result != null) {
//								boolean state = response.optBoolean("state");
//								if (state) {
//									// String viewAccount = "admin";
//									// int channel = 0;
//									// boolean isPrivate = true;
//									// boolean isPublic = false;
//									// boolean isShare = false;
//									// boolean isAlarm = false;
//									//
//									// long dbId = new DatabaseManager(
//									// AddDeviceActivity.this)
//									// .addMyDevice(deviceName, devicUid,
//									// viewAccount, viewPwd,
//									// deviceLocation, 3, channel,
//									// isPrivate, isPublic,
//									// isShare, isAlarm);
//									// DeviceInfo deviceInfo = new DeviceInfo(
//									// dbId, null, deviceName, devicUid,
//									// deviceLocation, viewAccount,
//									// viewPwd, "", 3, channel,
//									// (Bitmap) null, isPrivate, isPublic,
//									// isShare, isAlarm, true, null, null);
//									// long var8 = (new
//									// DatabaseManager(AddDeviceActivity.this))
//									// .addDevice(deviceName, devicUid, "", "",
//									// "admin", viewPwd, 3, 0);
//									// Toast.makeText(
//									// AddDeviceActivity.this,
//									// AddDeviceActivity.this.getText(R.string.tips_add_camera_ok)
//									// .toString(), 0).show();
//									// Bundle var10 = new Bundle();
//									// var10.putLong("db_id", var8);
//									// var10.putString("dev_nickname",
//									// deviceName);
//									// var10.putString("dev_uid", devicUid);
//									// var10.putString("dev_name", "");
//									// var10.putString("dev_pwd", "");
//									// var10.putString("view_acc", "admin");
//									// var10.putString("view_pwd", viewPwd);
//									// var10.putInt("video_quality", 0);
//									// var10.putInt("camera_channel", 0);
//									// Intent var11 = new Intent();
//									// var11.putExtras(var10);
//									// AddDeviceActivity.this.setResult(-1,
//									// var11);
//									// AddDeviceActivity.this.finish();
//									// AddDeviceActivity.this.finish();
//									// Intent intent = new Intent();
//									// intent.putExtra("deviceInfo",
//									// deviceInfo);
//									// setResult(RESULT_OK, intent);
//									// finish();
//									// getHelper().showMessage(
//									// R.string.tips_add_camera_ok);
//								} else {
//									// getHelper().showMessage(
//									// R.string.failed_to_add_device);
//								}
//							}
//						}
//
//						@Override
//						public void onFailure(int statusCode, Throwable error,
//								String content) {
//							// mProgressBar.dismiss();
//							// getHelper().showMessage(
//							// R.string.failed_to_add_device);
//							error.printStackTrace();
//						}
//
//					});

			// (new DatabaseManager(this)).updateDeviceInfoByDBID(
			// this.mDevice.DBID, this.mDevice.UID, this.mDevice.nickName,
			// "", "", "admin", this.mDevice.viewPassword,
			// this.mDevice.EventNotification,
			// this.mDevice.getChannelIndex(),this.mDevice.isPublic);

		}

		if (this.mCamera != null) {
			this.mCamera.unregisterIOTCListener(this);
		}

		boolean var9;
		if (var1
				&& (this.mVideoQuality != -1 && var2 != this.mVideoQuality
						|| this.mVideoFlip != -1 && var3 != this.mVideoFlip
						|| this.mEnvMode != -1 && var4 != this.mEnvMode
						|| this.mRecordType != -1 && var5 != this.mRecordType
						|| this.mMotionDetection != -1
						&& var7 != this.mMotionDetection || this.mPostition != -1
						&& var8 != this.mPostition)) {
			var9 = true;
		} else {
			var9 = false;
		}

		boolean var10;
		if (!var9 && !isModifyPassword && !isModifyWiFi) {
			var10 = false;
		} else {
			var10 = true;
		}

		Intent var11 = new Intent();
		Bundle var12 = new Bundle();
		var12.putBoolean("need_reconnect", var10);
		var12.putBoolean("change_password", isModifyPassword);
		var12.putString("new_password", newPassword);
		Log.v("deviceinfo","need_reconnect = "+var10+"isModifyPassword = "+isModifyPassword+"newPassword = "+newPassword	);
		var11.putExtras(var12);
		byte var14;
		if (!var1 && !isModifyPassword && !isModifyWiFi) {
			var14 = 0;
		} else {
			var14 = -1;
		}

		this.setResult(var14, var11);
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
	private ImageView back;
	private TextView title;
	private ImageView title_img;

	protected void onCreate(Bundle var1) {
		super.onCreate(var1);
		this.setTitle(this.getText(R.string.page10_dialog_AdvancedSetting));
		this.getWindow().setFlags(128, 128);
		this.setContentView(R.layout.advanced_settings);
		getActionBar().hide();
		Bundle var2 = this.getIntent().getExtras();
		String var3 = var2.getString("dev_uuid");
		String mDevUID = var2.getString("dev_uid");
		this.mCamera =  CameraManagerment.getInstance().getexistCamera(mDevUID);

		if(mCamera!=null)
				this.mCamera.registerIOTCListener(this);

		this.mDevice  = MainCameraFragment.getexistDevice(mDevUID);

		mProgressBar = new MyProgressBar(AdvancedSettingActivity.this , null);
		if(this.mDevice.viewPassword != null){
		newPassword = this.mDevice.viewPassword;
		}else{
			newPassword = "";
		}
		this.pnlVideoQuality = (LinearLayout) this
				.findViewById(R.id.panelVideoQualitySetting);
		this.pnlVideoFlip = (LinearLayout) this
				.findViewById(R.id.panelVideoFlip);
		this.pnlEnvMode = (LinearLayout) this
				.findViewById(R.id.panelEnvironmentMode);
		panelpowermode = (LinearLayout) this
				.findViewById(R.id.panelpowermode);
		this.pnlWiFiSetting = (LinearLayout) this
				.findViewById(R.id.panelWiFiSetting);
		
		this.panelUpdateSetting= (LinearLayout) this
				.findViewById(R.id.panelUpdateSetting);
		panelUpdateSetting.setVisibility(View.VISIBLE);
		back = (ImageView) this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		title = (TextView) this.findViewById(R.id.title);
		title.setText(getString(R.string.page25_page10_txtAdvancedSetting));
		this.findViewById(R.id.left_ll).setOnClickListener(btnOKOnClickListener);
		title_img = (ImageView) findViewById(R.id.title_img);
		title_img.setImageResource(R.drawable.setting_off);
		

		//自动获取时区
		TimeZone timeZone1;
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
		Customtimezone = zoneOffset / 60 / 60 / 1000;
		toggleBtnDST=(ToggleButton) findViewById(R.id.Switchbutton1);
		toggleBtnDST.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){					
					 //spinTimeZone.setSelection(mPostition- 11);
					 // spinTimeZone.setSelection(TimeZone+11);
					 enableDST = 1;
				}else{
					 enableDST = 0;
				}
			}
		});
		
		btnAutoSyncTimeZone = (Button)this.findViewById(R.id.btnAutoSyncTimeZone);
		
		btnAutoSyncTimeZone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mCamera!=null){
					Date d1 = new Date(2009, 0, 1); 
					Date d2 = new Date(2009, 6, 1); 
					if (d1.getTimezoneOffset() != d2.getTimezoneOffset()) 
					{ 
						enableDST = 1;
					} 
					else 
					{ 
						enableDST = 0;
					} 
				 	if(enableDST != 0){
				 		toggleBtnDST.setChecked(true);
				 	}else{
				 		toggleBtnDST.setChecked(false);
				 	}
					spinTimeZone.setSelection(Customtimezone+12); 
					mCameraManagerment.userIPCSetTimeZone(mCamera.getmUID(), 268, 
							AdvancedSettingActivity.this.enableDST,
							AdvancedSettingActivity.this.mPostition - 12);
					
				}
			}
		}
		);
		
		btnUpdateSetting = (Button)this.findViewById(R.id.btnUpdateSetting);
		btnUpdateSetting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mCamera!=null){
						if(ver == null || ver.equals("") || (  versionz <= versionloacl)){
							Toast.makeText(
									AdvancedSettingActivity.this,
									AdvancedSettingActivity.this
											.getText(
													R.string.page10_u_firmware_update_lastversion)
											.toString(), 0).show();
						}else{
							String msgStr = getString(R.string.page10_u_firmware_update_newversion) + ver +" \n"+  file_desc;
							getHelper().showDialog(getString(R.string.page10_u_firmware_update_prompt),msgStr,
									new ShowDialogCallback() {

										@Override
										public void callback(boolean sure) {
											if (sure) {
	 											mProgressBar.show();
												mCamera.sendIOCtrl(0,AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FIRMWARE_UPDATE_REQ,
														UpdateReq.getData() );
											}
											else
											{
												mProgressBar.dismiss();
											}

										}
									});
						}
	
					 
				}
			}
		}
		);
		btnQrSetting = (Button)this.findViewById(R.id.btnQrSetting);
		btnQrSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) { 
				Bundle var2 = new Bundle();
				var2.putString("dev_uuid",  mDevice.UUID);
				var2.putString("dev_uid",  mDevice.UID);
				Intent var3 = new Intent();
				var3.setClass(AdvancedSettingActivity.this,
						QrCodeShareInfoActivity.class);
				var3.putExtras(var2);
				AdvancedSettingActivity.this.startActivityForResult(var3, 0);
			}
		}
		);
		
		this.pnlEventSeting = (LinearLayout) this.findViewById(R.id.panelEventSetting);
		this.pnlVolumeSetting = (LinearLayout) this.findViewById(R.id.panelVolumeSetting);
		this.pnlPIRSeting= (LinearLayout) this.findViewById(R.id.panelPIRSetting);
		this.pnlosdSeting = (LinearLayout) this.findViewById(R.id.panelosdSetting);
		this.pnlRecordSetting = (LinearLayout) this.findViewById(R.id.panelRecordSetting);
		this.pnlTimeZone = (LinearLayout) this.findViewById(R.id.panelTimeZone);
		this.pnlFormatSDCard = (LinearLayout) this.findViewById(R.id.panelFormatSDCard);
		this.pnlDeviceInfo = (LinearLayout) this.findViewById(R.id.panelDeviceInfo);
		this.btnModifySecurityCode = (Button) this.findViewById(R.id.btnModifySecurityCode);
		this.btnModifyVolume = (Button) this.findViewById(R.id.btnModifyVolumeSetting);
		this.btnManageWiFiNetworks = (Button) this.findViewById(R.id.btnManageWiFiNetworks);
		this.btnFormatSDCard = (Button) this.findViewById(R.id.btnFormatSDCard);
		this.btnOK = (Button) this.findViewById(R.id.btnOK);
		this.btnCancel = (Button) this.findViewById(R.id.btnCancel);
		
		this.spinVideoQuality = (Spinner) this.findViewById(R.id.spinVideoQuality);
		this.spinVideoFlip = (Spinner) this.findViewById(R.id.spinVideoFlip);	
		this.spinEnvironmentMode = (Spinner) this.findViewById(R.id.spinEnvironment);
		this.spinMotionDetection = (Spinner) this.findViewById(R.id.spinMotionDetection);
		this.spinEventNotification = (Spinner) this.findViewById(R.id.spinEventNotification); 
		this.spinPIRSetStatus = (Spinner) this.findViewById(R.id.spinPIRSetStatus); 
		this.spinosdSetStatus = (Spinner) this.findViewById(R.id.spinosdSetStatus);
		pirvalue =false;
//		this.spinPIRSetStatus.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				// TODO Auto-generated method stub
//
//				Log.i("deviceinfo", "setOnItemSelectedListener.......OnItemSelectedListener.......="+arg2);
//				if ( mCamera != null && pirvalue) {
//					mCameraManagerment.userIPCSetPir(mCamera.getmUID(), mDevice.getChannelIndex(), arg2);
//					
//				}
//				
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		osdvalue  = false;
		this.spinosdSetStatus
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub

						Log.i("deviceinfo",
								"osdvalue.......OnItemSelectedListener.......="
										+ arg2);
						if (mCamera != null && osdvalue) {
							mCameraManagerment.userIPCSetOSD(mCamera.getmUID(), mDevice.getChannelIndex(), arg2);
							
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
		this.spinTimeZone = (Spinner) this.findViewById(R.id.spinTimeZone);
		this.spinRecordingMode = (Spinner) this.findViewById(R.id.spinRecordingMode);
		this.txtWiFiSSID = (TextView) this.findViewById(R.id.txtWiFiSSID);
		this.txtWiFiStatus = (TextView) this.findViewById(R.id.txtWiFiStatus);
		this.txtDeviceModel = (TextView) this.findViewById(R.id.txtDeviceModel);
		this.txtDeviceVersion = (TextView) this.findViewById(R.id.txtDeviceVersion);
		this.txtVenderName = (TextView) this.findViewById(R.id.txtVenderName);
		this.txtStorageTotalSize = (TextView) this.findViewById(R.id.txtStorageTotalSize);
		this.txtStorageFreeSize = (TextView) this.findViewById(R.id.txtStorageFreeSize);
		this.btnModifySecurityCode.setOnClickListener(this.btnModifySecurityCodeOnClickListener);
		
		this.btnModifyVolume
		.setOnClickListener(this.btnModifyVolumeOnClickListener);
		this.btnManageWiFiNetworks.setOnClickListener(this.btnManageWiFiNetworksOnClickListener);
		this.btnFormatSDCard.setOnClickListener(this.btnFormatSDCardListener);
		this.btnOK.setOnClickListener(this.btnOKOnClickListener);
		this.btnCancel.setOnClickListener(this.btnCancelOnClickListener);
		LinearLayout var10 = this.pnlVideoFlip;
		byte var11;
		
		if (this.mCamera != null && this.mCamera.getVideoFlipSupported(0)) {
			var11 = 0;
		} else {
			var11 = 8;
		}

		var10.setVisibility(var11);
		LinearLayout var12 = this.pnlEnvMode;
		byte var13;
		if (this.mCamera != null && this.mCamera.getEnvironmentModeSupported(0)) {
			var13 = 0;
		} else {
			var13 = 8;
		}

		var12.setVisibility(var13);
		this.initVideoSetting();
		this.initDeviceInfo();
		if (this.mCamera != null && this.mCamera.getWiFiSettingSupported(0)) {
			this.initWiFiSSID();
			this.pnlWiFiSetting.setVisibility(View.GONE);
		} else {
			this.pnlWiFiSetting.setVisibility(View.GONE);
		}

		if (this.mCamera != null && this.mCamera.getEventSettingSupported(0)) {
			this.initMotionDetection();
			this.initEventNotification();
			this.pnlEventSeting.setVisibility(0);
		} else {
			this.pnlEventSeting.setVisibility(View.GONE);
		}


		if (this.mCamera != null  ) {
			pnlosdSeting.setEnabled(true);
			this.initosdSetStatusMode();
			this.pnlosdSeting.setVisibility(0);
		} else {
			this.pnlosdSeting.setVisibility(View.GONE);
		}
		
		if (this.mCamera != null ) {
			btnModifyVolume.setEnabled(true);
			this.initSpeakVolume(); 
			this.pnlVolumeSetting.setVisibility(8);//0
		} 
		
		if (this.mCamera != null && this.mCamera.getRecordSettingSupported(0)) {
			this.initRecordingMode();
			this.pnlRecordSetting.setVisibility(0);
		} else {
			this.pnlRecordSetting.setVisibility(8);
		}

		if (this.mCamera != null && this.mCamera.getTimeZone(0)) {
			this.initTimeZone();
			this.pnlTimeZone.setVisibility(0); //8
		}
		if(VRConfig.isVRdevice(this.mCamera.hardware_pkg)){
			this.pnlTimeZone.setVisibility(View.GONE);
		}
		if (this.mTotalSize >= 0 && this.mCamera != null
				&& this.mCamera.getSDCardFormatSupported(0)) {
			this.pnlFormatSDCard.setVisibility(0);
		} else {
			this.pnlFormatSDCard.setVisibility(8);
		}

		if (this.mCamera != null
				&& this.mCamera.getVideoQualitySettingSupport(0)) {
			this.pnlVideoQuality.setVisibility(0);
		} else {
			this.pnlVideoQuality.setVisibility(8);
		}

		if (this.mCamera != null && this.mCamera.getDeviceInfoSupport(0)) {
			this.pnlDeviceInfo.setVisibility(0);
		} else {
			this.pnlDeviceInfo.setVisibility(8);
		}
		this.initnotify_mode();
		
		if(this.mCamera.hardware_pkg== HARDWAEW_PKG.CM_BELL_VR_9732_5112
|| this.mCamera.hardware_pkg== HARDWAEW_PKG.CM_BELL_VR_5230_2466){
			pnlPIRSeting.setVisibility(View.VISIBLE);
			pnlEnvMode.setVisibility(View.VISIBLE);
			panelnotifySetting.setVisibility(View.GONE);
			pnlEventSeting.setVisibility(View.GONE);
			pnlosdSeting.setVisibility(View.GONE);
			pnlVideoFlip.setVisibility(View.GONE);
			initpanelpowermode();
			initPIRSetStatusMode();
			
			this.panelPutMode_switch = (LinearLayout) this //door bell no putmode
					.findViewById(R.id.panelPutMode_switch); 
			panelPutMode_switch.setVisibility(View.GONE);
		}else{
			initputmode_mode();
			pnlPIRSeting.setVisibility(View.GONE);
			pnlEnvMode.setVisibility(View.GONE);
			panelpowermode.setVisibility(View.GONE);
		}
	
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
			AdvancedSettingActivity.this.stopCheck = false;
		}

		public void run() {
			do {
				long var1 = System.currentTimeMillis();
				if (var1 - AdvancedSettingActivity.this.t1 > 50000L
						&& !AdvancedSettingActivity.this.changeStatus) {
					AdvancedSettingActivity.this.handler.postDelayed(
							new Runnable() {
								public void run() {
									mCameraManagerment.userIPCListWifiAP(AdvancedSettingActivity.this.mCamera.getmUID());
									
								}
							}, 100L);
				}

				if (var1 - AdvancedSettingActivity.this.t1 > 60000L) {
					if (!AdvancedSettingActivity.this.changeStatus) {
						Message var3 = new Message();
						var3.what = 1;
						AdvancedSettingActivity.this.handler.sendMessage(var3);
						AdvancedSettingActivity.this.changeStatus = false;
						AdvancedSettingActivity.this.stopCheck = true;
					}

					AdvancedSettingActivity.this.t1 = System.currentTimeMillis();
				}
			} while (!AdvancedSettingActivity.this.stopCheck);

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
				String strpid =  String.valueOf(pidnum);
				
				byte[] pid = strpid.getBytes();
				String apidB64 = (new String( Base64.encode(pid,0)));

				apidB64 = apidB64.replace('+','-');
				apidB64 = apidB64.replace('/','_');
				apidB64 = apidB64.replace('=',',');
				String pid64 = apidB64.substring(0, apidB64.length()-1);
				
				//HttpGet myget = new HttpGet(
				//		"http://camera.kkmove.com/interface.php?Function=19&Command=1&Hmac=0&Time=20150504T235516&Nonce=nN1uwy5Y&Seq=12345678&UID="+asB64+",&WebAccount=0&Token=0");
				Log.v("lastversion","HttpClient.getBaseUrl() ="+ HttpClient.getBaseUrl());
				HttpGet myget = new HttpGet(
						HttpClient.getBaseUrl()+"/interface.php?Function=19&Command=2&Hmac=0&Time=20150504T235516&Nonce=nN1uwy5Y&Seq=12345678&UID="+asB64+"&Kpid="+ pid64 +"&WebAccount=0&Token=0");
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
public void showDialog(){
	progressDialog = new ProgressDialog( this);
	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	
	String tmpstring = AdvancedSettingActivity.this.getText(R.string.page10_u_firmware_updating).toString();

	progressDialog.setMessage(tmpstring +"0%");
	progressDialog.setCanceledOnTouchOutside(false);
	progressDialog.show();
}
}
/*
 * if(0x06 == recvBuf[4]){ //Get SEI Frame //int size = recvBuf[6]; int
 * deviceTime;
 * 
 * memcpy(&deviceTime, &recvBuf[8], sizeof(deviceTime));
 * 
 * if (secondSince1970 != deviceTime) { secondSince1970 = deviceTime;
 * 
 * [[NSNotificationCenter defaultCenter] postNotificationName:
 * @"ubiaDeviceTimeUpdateNotification" object:nil];
 * 
 * }
 * 
 * ubia2014-06-17 01:47:39 ｝
 */