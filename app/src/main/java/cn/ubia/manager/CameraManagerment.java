package cn.ubia.manager;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import cn.ubia.bean.MyCamera;
import cn.ubia.fragment.MainCameraFragment;

import com.ubia.IOTC.AVIOCTRLDEFs;
import com.ubia.IOTC.Camera;
import com.ubia.IOTC.IRegisterIOTCListener;
import com.ubia.IOTC.LoadLibConfig;
import com.ubia.IOTC.Packet;
import com.ubia.IOTC.AVIOCTRLDEFs.ENUM_FILE_TRANS_CMD;
import com.ubia.IOTC.AVIOCTRLDEFs.ENUM_PTZCMD;
import com.ubia.IOTC.AVIOCTRLDEFs.OSDItem;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlAVStream;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlFormatExtStorageReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlGetMICVolumeReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlGetSPVolumeReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlListEventReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlListWifiApReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlSetEnvironmentReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlSetMotionDetectReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlSetPasswdReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlSetRecordReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlSetVideoModeReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlSetVolumeReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlSetWifiReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlTimeZone;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgFileIoctrlDownloadReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgIoctrlGSetPIRCtrlReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsgIoctrlSetUIDReq;
import com.ubia.IOTC.AVIOCTRLDEFs.SMsguser;
import com.ubia.IOTC.AVIOCTRLDEFs.UBIA_IO_AVStream;
import com.ubia.IOTC.AVIOCTRLDEFs.UBIA_IO_EXT_GarageDoorReq;
import com.ubia.IOTC.AVIOCTRLDEFs.UBIA_IO_LensOffset;
import com.ubia.IOTC.AVIOCTRLDEFs.UBIA_IO_RecordBitmapReqRsp;

public class CameraManagerment {
	public static List<MyCamera> CameraList = new ArrayList<MyCamera>() {
	};
	private static CameraManagerment manager = null;
	private int moveleftBit = 8;

	private CameraManagerment() {
	};

	public synchronized static CameraManagerment getInstance() {
		if (null == manager) {
			synchronized (CameraManagerment.class) {
				manager = new CameraManagerment(); 
				Init();
			}
		}
		return manager;
	}

	public static void Init() {
		LoadLibConfig ldlib  =  LoadLibConfig.getInstance();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try { 
			
					MyCamera.init();
				} catch (Exception e) {

				}
			}
		}).start(); 
	};
	public void Free() {
		//// NativeCaller.Free();
		for (MyCamera myCamera : CameraList) {
			  Log.e("main", "StopPPPP   =" +  myCamera.getmDevUID() );
			myCamera.disconnect();
		}
		MyCamera.uninit();
	};
	public int StartClient(  String did, String user, String pwd ) {
		  {
			Camera mycamera = getexistCamera(did);
			  Log.v("main", "22222 StartClient.add      StartPPPP   =" +  did+"  user:"+user+"  pwd:"+pwd);
			if (mycamera != null) {  
				mycamera.start(0, user, pwd); 
			} else {
				MyCamera addcamera = new MyCamera("", did, user, pwd);
				addcamera.connect(did);
				addcamera.start(0, user, pwd); 
				CameraList.add(addcamera); 
			}
		}
		return 0;
	};
	public int StartPPPP(  String did, String user, String pwd ) {
		  {

			Camera mycamera = getexistCamera(did);
			  Log.v("main", "111111CameraList.add      StartPPPP   =" +  did+"  user:"+user+"  pwd:"+pwd);
			if (mycamera != null) {
 				mycamera.connect(did);
				mycamera.start(0, user, pwd); 
			} else {
				MyCamera addcamera = new MyCamera("", did, user, pwd);
				addcamera.connect(did);
				addcamera.start(0, user, pwd);
				 
				CameraList.add(addcamera);
				
			}

		}

		return 0;
	};
	public int AddCameraItem(MyCamera myCamera) {
		
		if (myCamera != null && getexistCamera(myCamera.getmDevUID())==null) 
		{
			
			CameraList.add(myCamera);
			Log.v("main", "CameraList.add      AddCameraItem=" + myCamera.getmDevUID());
		}
		return 0;

	}
	public int StopPPPP(String did) {
	 	try{
			Camera mycamera = getexistCamera(did);
			if (mycamera != null )
				{
					mycamera.ClearBuf(0);
 					mycamera.disconnect();
				}
		}catch (Exception e){

		}

		return 0;
	};
	
	public int ClearBuf(String did) {
		 {
				Camera mycamera = getexistCamera(did);
				if (mycamera != null )
					{
						mycamera.ClearBuf(0); 
					}

			}

			return 0;
		};

	public MyCamera getexistCamera(String UID) {
		for (MyCamera myCamera : CameraList) {
			if (myCamera.getUID().trim().equalsIgnoreCase(UID.toUpperCase().trim()))
				return myCamera;
		}
		return null;
	}
	public MyCamera deleteExistCamera(String UID) {
		for (MyCamera myCamera : CameraList) {
			if (myCamera.getUID().trim().equalsIgnoreCase(UID.toUpperCase().trim())){
				CameraList.remove(myCamera);
				return myCamera;
			}
				
		}
		return null;
	}
	public void userIPCDeviceInfo(String uid){
		
			Camera mycamera = getexistCamera(uid);
	        if(mycamera!=null){
	        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ, 
	        			SMsgAVIoctrlDeviceInfoReq.parseContent());
	        }
	}
	
	public void userIPCGetAdvanceSetting(String uid){
		
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_ADVANCESETTINGS_REQ, 
        			Packet.intToByteArray_Little(0));
        }
        
   
    }
	
	 public void userIPCGetVolu(String uid,int deviceInfoChannelIndex){
		 Camera mycamera = getexistCamera(uid);
	        if(mycamera!=null){
	        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETVOLUME_REQ, 
	        			SMsgAVIoctrlGetSPVolumeReq
						.parseContent(deviceInfoChannelIndex));
	        }
     }
	 
	 public void userIPCGetMicVolu(String uid,int deviceInfoChannelIndex){
		 Camera mycamera = getexistCamera(uid);
	        if(mycamera!=null){
	        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETMICVOLUME_REQ, 
	        			SMsgAVIoctrlGetMICVolumeReq
						.parseContent(deviceInfoChannelIndex));
	        }
     }
	 
	 public void userIPCSetAlermMode(String uid,byte[] data){
		 Camera mycamera = getexistCamera(uid);
	        if(mycamera!=null){
	        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_ALARMMODE_REQ, 
	        			data);
	        }
	 }
	 
	 public void userIPCSetStreamCrl(String uid,int channelIndex,int var){
		 Camera mycamera = getexistCamera(uid);
	        if(mycamera!=null){
	        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ, 
	        			SMsgAVIoctrlSetStreamCtrlReq
						.parseContent(channelIndex,
								(byte) var));
	        }
	 }
	 
	 public void userIPCSetVideoMode(String uid,int channelIndex,int var){
		 Camera mycamera = getexistCamera(uid);
	        if(mycamera!=null){
	        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ, 
	        			SMsgAVIoctrlSetVideoModeReq
						.parseContent(channelIndex,
								(byte) var));
	        }
	 }
	 
	 public void userIPCSetEnv(String uid,int channelIndex,int var){
		 Camera mycamera = getexistCamera(uid);
	        if(mycamera!=null){
	        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ, 
	        			SMsgAVIoctrlSetEnvironmentReq
						.parseContent(channelIndex,
								(byte) var));
	        }
	 }
	 
	 public void userIPCSetVolu(String uid,int channelIndex,int seekBar){
		 Camera mycamera = getexistCamera(uid);
	        if(mycamera!=null){
	        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETVOLUME_REQ, 
	        			SMsgAVIoctrlSetVolumeReq
						.parseContent(channelIndex,
								(byte) seekBar));
	        }
	 }
	 
	 public void userGetIPCVolu(String uid,int channelIndex){
		 Camera mycamera = getexistCamera(uid);
	        if(mycamera!=null){
	        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETVOLUME_REQ, 
	        			SMsgAVIoctrlGetSPVolumeReq
						.parseContent(channelIndex));
	        }
	 }
	 
	 public void userIPCSetMicVolu(String uid,int channelIndex,int seekBar){
		 Camera mycamera = getexistCamera(uid);
	        if(mycamera!=null){
	        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMICVOLUME_REQ, 
	        			SMsgAVIoctrlSetVolumeReq
						.parseContent( channelIndex,(byte)seekBar));
	        }
	 }
	 
	public void userIPCSetPassWord(String uid,String password1,String str2,String nickName){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
     
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPASSWORD_REQ, 
        			SMsgAVIoctrlSetPasswdReq
					.parseContent(password1,
							str2,nickName));
        }
	}
	public void userIPCSetPassWordwithCountry(String uid,String password1,String str2,String nickName,String myCountryCode){
		Camera mycamera = getexistCamera(uid);
		if(mycamera!=null){
			mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPASSWORD_REQ,
					SMsgAVIoctrlSetPasswdReq
							.parseContentwithCountry(password1,
									str2,nickName,myCountryCode));
		}
	}
	public void userIPCSetSettingPassWord(String uid,String password1,String str2,String nickName){
		Camera mycamera = getexistCamera(uid);
		if(mycamera!=null){
	 
			mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPASSWORD_REQ,
					SMsgAVIoctrlSetPasswdReq
							.parseContentNoFFEE(password1,
									str2,nickName));
		}
	}
	public void userIPCSetDeviceName(String uid ,String nickName){
		Camera mycamera = getexistCamera(uid);
       if(mycamera!=null){
       
       	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_DEVNAME_REQ, 
       			SMsgAVIoctrlSetPasswdReq
					.parseContentDeviceName( nickName));
       }
	}
	public void userIPCListWifiAP(String uid){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTWIFIAP_REQ, 
        			SMsgAVIoctrlListWifiApReq
					.parseContent());
        }
	}
	
	public void userIPCSetMotionDetect(String uid,int var1,int var2){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_REQ, 
        			SMsgAVIoctrlSetMotionDetectReq.parseContent(var1,var2*25));
        }
	}
	
	public void userIPCSetRecord(String uid,int channelIndex,int var2){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETRECORD_REQ, 
        			SMsgAVIoctrlSetRecordReq.parseContent(
        					channelIndex, var2 ));
        }
	}
	
	public void userIPCSetTimeZone(String uid,int var1,int var2,int var3){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
			Log.d(""," userIPCSetTimeZone enableDST"+var2  +"   var3:"+var3);
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_TIMEZONE_REQ, 
        			SMsgAVIoctrlTimeZone.parseContent(var1,
        					var2,//	this.mCamera.getIsSupportTimeZone()
        					var3, "".getBytes()));
        }
	}
	
	public void userIPCSetPir(String uid,int channelIndex,int var0){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPIR_REQ, 
        			SMsgIoctrlGSetPIRCtrlReq
					.parseContent( channelIndex,(byte) var0));
        }
	}
	
	public void userIPCSetOSD(String uid,int channelIndex,int var0){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_OSD_REQ, 
        			OSDItem.setData (channelIndex,
							  (char) var0));
        }
	}
	
	public void userIPCSetWifi(String uid,byte[] var0,byte[] var1,byte var2,byte var3){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_REQ, 
        			SMsgAVIoctrlSetWifiReq.parseContent(
        					var0,
        					var1,
        					var2,
        					var3));
        }
	}
	
	public void userIPCGetSupportStream(String uid){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ, 
        			SMsgAVIoctrlGetSupportStreamReq
					.parseContent());
        }
	}
	
	public void userIPCGetAudioOutFormat(String uid){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ, 
        			SMsgAVIoctrlGetAudioOutFormatReq
					.parseContent());
        }
	}
	
	public void userIPCFileDownLoad(String uid,String Downloadfilename,byte[] var0){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_FILE_DOWNLOAD_REQ, 
        			SMsgFileIoctrlDownloadReq
					.parseContent(
							(byte) ENUM_FILE_TRANS_CMD.FILE_TRANS_CMD_START,
							(byte) 1,
							Packet.shortToByteArray_Little((short) 1280),
							Packet.shortToByteArray_Little((short) 0),
							(byte) Downloadfilename
									.getBytes().length,
							Downloadfilename.getBytes(),
							var0));
        }
	}
	
	public void userIPCFileDownLoadStop(String uid,String Downloadfilename,byte[] var0){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_FILE_DOWNLOAD_REQ, 
        			SMsgFileIoctrlDownloadReq
					.parseContent(
							(byte) ENUM_FILE_TRANS_CMD.FILE_TRANS_CMD_STOP,
							(byte) 1,
							Packet.shortToByteArray_Little((short) 1280),
							Packet.shortToByteArray_Little((short) 0),
							(byte) Downloadfilename
									.getBytes().length,
							Downloadfilename.getBytes(),
							var0));
        }
	}
	
	public void userIPCEventDownLoad(String uid,byte[] var0){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_EVENT_DOWNLOAD_REQ, 
        			var0);
        }
	}
	
	public void userIPCStart(String uid,int channelIndex,byte seq){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
//			mycamera.sendIOCtrl( 0, 0x213c,AVIOCTRLDEFs.SMsguser.getData("admin",MainCameraFragment.getexistDevice(uid).viewPassword));
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_START, 
        			UBIA_IO_AVStream.startLiveView(channelIndex,(byte) seq));
        	//mycamera.sendIOCtrl(0, 0x200,
			//		SMsgAVIoctrlAVStream.parseContent(channelIndex));
        }
	}
	
	public void userIPCGetTimeZone(String uid){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ, 
        			SMsgAVIoctrlTimeZone.parseContent());
        }
	}

	public void userIPCRecordEventList(String uid,int startTime,int endTime){
		Camera mycamera = getexistCamera(uid);
		if(mycamera!=null){
			mycamera.sendIOCtrl(0, AVIOCTRLDEFs.UBIA_IO_IPCAM_LISTEVENT_REQ,
					AVIOCTRLDEFs.UBIA_IO_RecordEventListReqRsp.parseContent(startTime,endTime));
		}
	}

	public void userIPCRecordBitmap(String uid,int startTime,int endTime){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.UBIA_IO_IPCAM_RECORD_BITMAP_REQ, 
        			UBIA_IO_RecordBitmapReqRsp.parseContent(startTime,endTime));
        }
	}
	
	public void userIPCRecordPalyStart(String uid,int cmd,int playBackTimeUTC,int seqsec){
		Camera mycamera = getexistCamera(uid);
        if(mycamera!=null){
        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_START, 
        			UBIA_IO_AVStream.playbackparseContent(cmd,playBackTimeUTC,  (byte) seqsec));
        	mycamera.setStartisIFrame ( false);
        }
	}
	
//	public void userIPCPause(String uid,int seqsec){
//		Camera mycamera = getexistCamera(uid);
//		 if(mycamera!=null){
//	        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_START, 
//	        			AVIOCTRLDEFs.UBIA_IO_AVStream.parseContent(AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_PAUSE));
//	        }
//	}
	
	public void userIPCRecordPlayControl(String uid,int channelIndex,byte[]var1){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
	        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL, 
	        			SMsgAVIoctrlPlayRecord
						.parseContent(
								channelIndex,
								AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START,
								0,
								var1));
	        }
	}
	
	public void userIPCRecordPlayStop(String uid,int channelIndex,byte[]var0){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
	        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL, 
	        			SMsgAVIoctrlPlayRecord
						.parseContent(
								channelIndex,
								AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_STOP,
								0,
								var0));
	        }
		
	}
	
	public void userIPCRecordPlaySeekTime(String uid,int channelIndex,byte[]var0){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
	        	mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL, 
	        			SMsgAVIoctrlPlayRecord
						.parseContent(
								channelIndex,
								AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_SEEKTIME,
								0,
								var0));
	        }
		
	}
	
	public void userIPCPlayPause(String uid,int channelIndex,byte[]var0){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,
						SMsgAVIoctrlPlayRecord.parseContent(
								channelIndex, AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_PAUSE, 0,
								var0));
		 }
	}
	
	public void userIPCFormatExistStorage(String uid){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_REQ,
						SMsgAVIoctrlFormatExtStorageReq
								.parseContent(0));
		 }
	}
	
	public void userUbiaSetUID(String uid,byte[]var0){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(0,
						AVIOCTRLDEFs.IOTYPE_UBIA_SET_UID_REQ,
						SMsgIoctrlSetUIDReq
								.parseContent(20,var0));

		 }
	}
	
	public void userIPCSatrt(String uid,int channelIndex){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(
					 channelIndex,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_START,
						Packet.intToByteArray_Little(0));

		 }
	}
	
	public void userIPCStop(String uid,int channelIndex){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(
					 channelIndex,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_STOP,
						Packet.intToByteArray_Little(0));


		 }
	}
	
	public void userIPCListEvent(String uid,int var0,long var1,long var2,int var3){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTEVENT_REQ,
						SMsgAVIoctrlListEventReq.parseConent(
								var0, var1, var2, (byte) var3,
								(byte) 0));


		 }
	}
	
	public void userIPCPTZReset(String uid,int var0){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(var0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						SMsgAVIoctrlPtzCmd.parseContent(
								(byte) ENUM_PTZCMD.AVIOCTRL_PTZ_RESET, (byte) 8, (byte) 0, (byte) 0,
								(byte) 0, (byte) 0));
		 }
	}
	
	public void userIPCPTZCruise(String uid,int var0, int cmd){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 byte ptzCmd = ENUM_PTZCMD.AVIOCTRL_PTZ_START_CRUISE;
			 if(cmd == 1)  ptzCmd = ENUM_PTZCMD.AVIOCTRL_PTZ_STOP_CRUISE;
			 mycamera.sendIOCtrl(var0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						SMsgAVIoctrlPtzCmd.parseContent(
								ptzCmd, (byte) 1, (byte) 0, (byte) 0,
								(byte) 0, (byte) 0)); //16
		 }
	}
	public void userIPCPTZLeft(String uid,int var0){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(var0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						SMsgAVIoctrlPtzCmd.parseContent(
								(byte) ENUM_PTZCMD.AVIOCTRL_PTZ_LEFT, (byte) 8, (byte) 0, (byte) 0,
								(byte) 0, (byte) 0));

		 }
	}
	
	public void userIPCPTZRight(String uid,int var0){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(var0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						SMsgAVIoctrlPtzCmd.parseContent(
								(byte)ENUM_PTZCMD.AVIOCTRL_PTZ_RIGHT, (byte) 8, (byte) 0, (byte) 0,
								(byte) 0, (byte) 0));

		 }
	}
	
	public void userIPCPTZUp(String uid,int var0){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(var0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						SMsgAVIoctrlPtzCmd.parseContent(
								(byte) ENUM_PTZCMD.AVIOCTRL_PTZ_UP, (byte) 8, (byte) 0, (byte) 0,
								(byte) 0, (byte) 0));

		 }
	}
	
	public void userIPCPTZDown(String uid,int var0){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(var0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						SMsgAVIoctrlPtzCmd.parseContent((byte)ENUM_PTZCMD.AVIOCTRL_PTZ_DOWN,
								(byte) 8, (byte) 0, (byte) 0, (byte) 0,
								(byte) 0));

		 }
	}
	
	public void userIPCPTZStop(String uid,int var0){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(
					 var0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						SMsgAVIoctrlPtzCmd.parseContent(
								(byte)ENUM_PTZCMD.AVIOCTRL_PTZ_STOP, (byte) 8, (byte) 0, (byte) 0,
								(byte) 0, (byte) 0));

		 }
	}
	
	public void userIPCControlDoor(String uid,int channel,boolean  doorstate){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(
					 channel, AVIOCTRLDEFs.UBIA_IO_EXT_GARAGEDOOR_REQ,
						UBIA_IO_EXT_GarageDoorReq.parseContent( doorstate?1:0 ));

		 }
	 
	}

	public void userIPCStopAllPPP(String uid)
	{
	  Camera mycamera = getexistCamera(uid);
	  if (mycamera != null) {
	    mycamera.stopSpeaking(0);
	    mycamera.stopListening(0);
	    mycamera.stopShow(0);
	    mycamera.stop(0);
	    mycamera.disconnect();
	  }
	}

	public void userIPCStopAlStream(String uid)
	{
	  Camera mycamera = getexistCamera(uid);
	  if (mycamera != null) {
	    mycamera.stopSpeaking(0);
	    mycamera.stopListening(0);
	    mycamera.stopShow(0);
	  }
	}

 
 

	public void userIPCstartAllStream(String uid, boolean mIsListening, boolean mIsSpeaking)
	{
	  Camera mycamera = getexistCamera(uid);
	  if (mycamera != null) {
		 mycamera.sendIOCtrl(0, 0x200,
					SMsgAVIoctrlAVStream.parseContent(0));
	    mycamera.startShow(0, true);
	    if(mIsSpeaking)
	    mycamera.startSpeaking(0);
	    if(mIsListening)
	    mycamera.startListening(0);
	  }
	}

	public void userIPCstartShow(String uid) {
	  Camera mycamera = getexistCamera(uid);
	  if (mycamera != null)
	    mycamera.startShow(0, true);
	}

	public void userIPCstartSpeak(String uid)
	{
	  Camera mycamera = getexistCamera(uid);
	  if (mycamera != null)
	    mycamera.startSpeaking(0);
	}
	

	public void userIPCMuteControl(String uid, boolean mute)
	{
	  Camera mycamera = getexistCamera(uid);
	  if (mycamera != null){
		  mycamera.mutePlayout = mute;
	  }
	}

	public void userIPCstartListen(String uid)
	{
	  Camera mycamera = getexistCamera(uid);
	  if (mycamera != null)
	    mycamera.startListening(0);
	}

	
	public void userIPCstopSpeak(String uid)
	{
	  Camera mycamera = getexistCamera(uid);
	  if (mycamera != null)
	    mycamera.stopSpeaking(0);
	}

	public void userIPCstopListen(String uid)
	{
	  Camera mycamera = getexistCamera(uid);
	  if (mycamera != null)
	    mycamera.stopListening(0);
	}

	public void unregisterIOTCListener(String uid, IRegisterIOTCListener listen)
	{
	  Camera mycamera = getexistCamera(uid);
	  if (mycamera != null)
	    mycamera.unregisterIOTCListener(listen);
	}

	public void registerIOTCListener(String uid, IRegisterIOTCListener listen)
	{
	  Camera mycamera = getexistCamera(uid);
	  if (mycamera != null)
	    mycamera.registerIOTCListener(listen);
	}

	 
 

	public void setcurrentplaySeq(String uid, int currentplaySeq)
	{
	  Camera mycamera = getexistCamera(uid);
	  if (mycamera != null)
	    mycamera.setcurrentplaySeq(currentplaySeq);
	}

	public void setCameraLastAudioMode(String uid, int i)
	{
	  MyCamera mycamera = getexistCamera(uid);
	  if (mycamera != null)
	    mycamera.LastAudioMode = 0;
	}

	public boolean userIPCisSavingVideo(String uid)
	{
	  MyCamera mycamera = getexistCamera(uid);
	  if (mycamera != null) {
	    return mycamera.getRecodeHelper().isSavingVideo();
	  }

	  return false;
	}

	public boolean userIPCisChannelConnected(String uid) {
	  MyCamera mycamera = getexistCamera(uid);
	  if (mycamera != null) {
	    return mycamera.isChannelConnected(0);
	  }

	  return false;
	}

	public Bitmap userIPCSnapshot(String uid) {
	  MyCamera mycamera = getexistCamera(uid);
	  if (mycamera != null && mycamera.attachedMonitor!=null) {
	    return mycamera.attachedMonitor.getBitmap();
	  }

	  return null;
	}

	public void userIPCstartRecode(String uid,int fps) {
	  MyCamera mycamera = getexistCamera(uid);
	  if (mycamera != null)
	    mycamera.startRecode(0, true,fps);
	}

	public void userIPCstopRecode(String uid)
	{
	  MyCamera mycamera = getexistCamera(uid);
	  if (mycamera != null)
	    mycamera.stopRecode(0, true);
	} 

	public void userIPCLensOffset(String uid,int x,int y,int z,int w,int h){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(
					 0, AVIOCTRLDEFs.UBIA_IO_SET_LENS_OFFSET_REQ,
						UBIA_IO_LensOffset.parseContent( x,y,z,w,h));

		 }
	}
	public void userIPCGetZigbee(String uid ){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(
					 0, AVIOCTRLDEFs.UBIA_IO_EXT_ZIGBEEINFO_REQ,
						AVIOCTRLDEFs.UBIA_IO_EXT_ZigbeeInfoReq.parseContent(0   ));

		 }
	 
	}
	public void userIPCsetACPPower(String uid,int operation ){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(
					 0, AVIOCTRLDEFs.UBIA_IO_SET_ACPOWER_FREQ_REQ,
						AVIOCTRLDEFs.UBIA_IO_ACPowerFreq_SetReq.parseContent(operation  ));

		 }
	 
	}
	public void userIPCsetSceneMode(String uid,int operation  ){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(
					 0, AVIOCTRLDEFs.UBIA_IO_SET_SCENE_MODE_REQ,
						AVIOCTRLDEFs.UBIA_IO_SceneMode_SetReq.parseContent(  operation    ));

		 }
	 
	}	
	public void userIPCsetPir(String uid ,int operation ){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(
					 0, AVIOCTRLDEFs.UBIA_IO_SET_PIR_REQ,
						AVIOCTRLDEFs.UBIA_IO_PIR_SetReq.parseContent(  operation    ));

		 }
	 
	}

	public void userIPCsetLed(String uid ,int operation ){
		Camera mycamera = getexistCamera(uid);
		if(mycamera!=null){
			mycamera.sendIOCtrl(
					0, AVIOCTRLDEFs.UBIA_IO_SET_LED_REQ,
					AVIOCTRLDEFs.UBIA_IO_LED_SetReq.parseContent(  operation    ));

		}

	}

	public void userIPCsetActiveTime(String uid ,int operation ){
		Camera mycamera = getexistCamera(uid);
		if(mycamera!=null){
			mycamera.sendIOCtrl(
					0, AVIOCTRLDEFs.UBIA_IO_SET_ACTIVETIME_REQ,
					AVIOCTRLDEFs.UBIA_IO_ACTIVETIME_SetReq.parseContent(operation));

		}

	}


	public void userIPCsetLock(String uid ,int operation ){
		Camera mycamera = getexistCamera(uid);
		 if(mycamera!=null){
			 mycamera.sendIOCtrl(
					 0, AVIOCTRLDEFs.UBIA_IO_SET_ELOCK_REQ,
						AVIOCTRLDEFs.UBIA_IO_ELOCK_SetReq.parseContent(  operation    ));
		 }
	}
	public int getWifiSignal(String uid )
	{
		Camera mycamera = getexistCamera(uid);
		if (mycamera != null){
			return mycamera.deviceWifiSignal;
		}else{
			return 4;
		}
	}
	public void userIPC_Cloud_SetReq(String uid ,int provider,int nEnableDST,int nGMTDiff ){
		Camera mycamera = getexistCamera(uid);
		if(mycamera!=null){
			mycamera.sendIOCtrl(
					0, AVIOCTRLDEFs.UBIA_IO_SET_CLOUD_REQ,
					AVIOCTRLDEFs.UBIA_IO_Cloud_SetReq.parseContent(    provider,  nEnableDST,  nGMTDiff  ));
		}
	}

	public void userIPC_CAPTURE_PICTURE_SetReq(String uid ,int pictureCount,int timeInterval ){
		Camera mycamera = getexistCamera(uid);
		if(mycamera!=null){
			mycamera.sendIOCtrl(
					0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_CAPTURE_PICTURE_REQ,
					AVIOCTRLDEFs.UBIA_IO_CAPTURE_PICTURE_SetReq.parseContent( pictureCount,  timeInterval));
		}
	}
}
