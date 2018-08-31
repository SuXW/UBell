package com.ubia.IOTC;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import cn.ubia.UbiaApplication;
import cn.ubia.UBell.R;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.util.StringUtils;

public class AVIOCTRLDEFs {

	public static final int UBIA_IO_SET_LENS_OFFSET_REQ = 0xF3;
	public static final int UBIA_IO_SET_LENS_OFFSET_RESP = 0xF4;
	public static final int IOTYPE_UBIA_SET_UID_REQ = 0xF1;
	public static final int IOTYPE_UBIA_SET_UID_RESP = 0xF2;
	public static final int UBIA_IO_IPCAM_LISTEVENT_REQ = 0x100,
							UBIA_IO_IPCAM_LISTEVENT_RESP = 0x101,
							UBIA_IO_IPCAM_RECORD_BITMAP_REQ = 0x102,
							UBIA_IO_IPCAM_RECORD_BITMAP_RSP = 0x103;
	public static final int IOTYPE_USER_IPCAM_SETCOVER_REQ = 0x0382;
	public static final int IOTYPE_USER_IPCAM_SETCOVER_RESP = 0x0383;
	public static final int IOTYPE_USER_IPCAM_GETCOVER_REQ = 0x0384;
	public static final int IOTYPE_USER_IPCAM_GETCOVER_RESP = 0x0385;

	public static final int IOTYPE_USER_IPCAM_SETVOLUME_REQ = 0x0386;
	public static final int IOTYPE_USER_IPCAM_SETVOLUME_RESP = 0x0387;
	public static final int IOTYPE_USER_IPCAM_GETVOLUME_REQ = 0x0388;
	public static final int IOTYPE_USER_IPCAM_GETVOLUME_RESP = 0x0389;

	/* œ¬√ÊÀƒ∏ˆΩ·ππÃÂ∫Õœ‡”¶µƒIOTYPE_USER_IPCAM_XXXVOLUME_XXX ÕÍ»´œ‡Õ¨ */
	public static final int IOTYPE_USER_IPCAM_SETMICVOLUME_REQ = 0x038A;
	public static final int IOTYPE_USER_IPCAM_SETMICVOLUME_RESP = 0x038B;
	public static final int IOTYPE_USER_IPCAM_GETMICVOLUME_REQ = 0x038C;
	public static final int IOTYPE_USER_IPCAM_GETMICVOLUME_RESP = 0x038D;

	public static final int IOTYPE_USER_IPCAM_SETMOTIONDETECT_DETAIL_REQ = 0x038e;
	public static final int IOTYPE_USER_IPCAM_SETMOTIONDETECT_DETAIL_RESP = 0x038f;
	public static final int IOTYPE_USER_IPCAM_GETMOTIONDETECT_DETAIL_REQ = 0x0390;
	public static final int IOTYPE_USER_IPCAM_GETMOTIONDETECT_DETAIL_RESP = 0x0391;

	public static final int IOTYPE_USER_IPCAM_GETSTREAMCTRL_DETAIL_REQ = 0x0392;
	public static final int IOTYPE_USER_IPCAM_GETSTREAMCTRL_DETAIL_RESP = 0x0393;
	// IOTYPE_USER_IPCAM_GET_FLOWINFO_REQ = 0x0390;
	// IOTYPE_USER_IPCAM_GET_FLOWINFO_RESP = 0x0391;
	// IOTYPE_USER_IPCAM_CURRENT_FLOWINFO = 0x0392;
	public static final int IOTYPE_USER_IPCAM_I_FRAME_REQ = 0x0394;
	public static final int IOTYPE_USER_IPCAM_I_FRAME_RESP = 0x0395;

	public static final int IOTYPE_USER_IPCAM_SETSHARE_REQ = 0x0396; /*
																	 * …Ë÷√ø™∆Ù∫Õ
																	 * »
																	 * °œ˚…„œÒÕ∑
																	 * π≤œÌ
																	 */
	public static final int IOTYPE_USER_IPCAM_SETSHARE_RESP = 0x0397;
	public static final int IOTYPE_USER_IPCAM_GETSHARE_REQ = 0x0398; /*
																	 * ªÒ»°…„œÒÕ∑
																	 * π≤œÌ ◊¥Ã¨
																	 */
	public static final int IOTYPE_USER_IPCAM_GETSHARE_RESP = 0x0399;

	public static final int IOTYPE_USER_IPCAM_GET_ALARMMODE_REQ = 0x0404; // Get
																			// Alarm
																			// Mode
																			// Request
	public static final int IOTYPE_USER_IPCAM_GET_ALARMMODE_RESP = 0x0405; // Get
																			// Alarm
																			// Mode
																			// Response
	public static final int IOTYPE_USER_IPCAM_SET_ALARMMODE_REQ = 0x0406; // Set
																			// Alarm
																			// Mode
																			// req
	public static final int IOTYPE_USER_IPCAM_SET_ALARMMODE_RESP = 0x0407; // Set
																			// Alarm
																			// Mode
																			// resp
	public static final int IOTYPE_USER_IPCAM_AUDIO_REPORT = 0x1101; // audio
																		// report

	public static final int IOTYPE_USER_IPCAM_IR_SWITCH_CMD = 0x1200; // IR
																		// switch
																		// command;
																		// add
																		// by
																		// zlq
	public static final int IOTYPE_USER_IPCAM_UPDATE_CHECK_REQ = 0x1210;
	public static final int IOTYPE_USER_IPCAM_UPDATE_CHECK_RSP = 0x1211;
	public static final int IOTYPE_USER_IPCAM_UPDATE_REQ = 0x1212;
	public static final int IOTYPE_USER_IPCAM_UPDATE_RSP = 0x1213;
	public static final int IOTYPE_USER_IPCAM_UPDATE_STATUS = 0x1214;

	public static final int IOTYPE_USER_IPCAM_UPDATE_CHECK_PHONE_REQ = 0x1215; // RECV
																				// APP
																				// REQ
																				// //ethanluo
	public static final int IOTYPE_USER_IPCAM_UPDATE_CHECK_PHONE_RSP = 0x1216; // send
																				// board
																				// version
																				// info
																				// //ethanluo
	public static final int IOTYPE_USER_IPCAM_UPDATE_PHONE_REQ = 0x1217; // recv
																			// APP
																			// update
																			// req
																			// //ethanluo
	public static final int IOTYPE_USER_IPCAM_UPDATE_PHONE_RSP = 0x1218; // //ethanluo

	public static final int IOTYPE_USER_IPCAM_SYSTEM_REBOOT_REQ = 0x1219; // RECV
																			// APP
																			// reboot
																			// REQ//ethanluo
	public static final int IOTYPE_USER_IPCAM_SYSTEM_REBOOT_RSP = 0x1220; // //ethanluo

	public static final int IOTYPE_USER_IPCAM_SETMOTIONADDR_REQ = 0X1221; // ethanluo
	public static final int IOTYPE_USER_IPCAM_SETMOTIONADDR_RSP = 0X1222; // ethanluo

	public static final int IOTYPE_USER_IPCAM_SETAUDIOSENSITIVITY_REQ = 0X1231; // maxwell
	public static final int IOTYPE_USER_IPCAM_SETAUDIOSENSITIVITY_RSP = 0X1232; // maxwell

	public static final int IOTYPE_USER_IPCAM_GETAUDIOSENSITIVITY_REQ = 0X1233; // maxwell
	public static final int IOTYPE_USER_IPCAM_GETAUDIOSENSITIVITY_RSP = 0X1234; // maxwell

	public static final int IOTYPE_USER_IPCAM_SETTEMPSENSITIVITY_REQ = 0X1235; // maxwell
	public static final int IOTYPE_USER_IPCAM_SETTEMPSENSITIVITY_RSP = 0X1236; // maxwell

	public static final int IOTYPE_USER_IPCAM_GETTEMPSENSITIVITY_REQ = 0X1237; // maxwell
	public static final int IOTYPE_USER_IPCAM_GETTEMPSENSITIVITY_RSP = 0X1238; // maxwell

	public static final int IOTYPE_USER_EVENT_DOWNLOAD_REQ = 0X130C; // maxwell
	public static final int IOTYPE_USER_EVENT_DOWNLOAD_RSP = 0X130D; // maxwell

	public static final int IOTYPE_USER_IPCAM_SETPIR_REQ = 0X1241; // pir
	public static final int IOTYPE_USER_IPCAM_SETPIR_RESP = 0X1242;
	public static final int IOTYPE_USER_IPCAM_GETPIR_REQ = 0X1243;
	public static final int IOTYPE_USER_IPCAM_GETPIR_RESP = 0X1244;
	public static final int IOTYPE_USER_IPCAM_FIRMWARE_UPDATE_CHECK_REQ = 0x1215, // maxwell
			IOTYPE_USER_IPCAM_FIRMWARE_UPDATE_CHECK_RSP = 0x1216, // maxwell
			IOTYPE_USER_IPCAM_FIRMWARE_UPDATE_REQ = 0x1217, // maxwell
			IOTYPE_USER_IPCAM_FIRMWARE_UPDATE_RSP = 0x1218; // maxwell
	public static final int IOTYPE_USER_IPCAM_SETIRMODE_REQ = 0X1251; // ir
	public static final int IOTYPE_USER_IPCAM_SETIRMODE_RESP = 0X1252;
	public static final int IOTYPE_USER_IPCAM_GETIRMODE_REQ = 0X1253;
	public static final int IOTYPE_USER_IPCAM_GETIRMODE_RESP = 0X1254;

	public static final int IOTYPE_USER_IPCAM_GET_OSD_REQ = 0x3B2;
	public static final int IOTYPE_USER_IPCAM_GET_OSD_RESP = 0x3B3;
	public static final int IOTYPE_USER_IPCAM_SET_OSD_REQ = 0x3B4;
	public static final int IOTYPE_USER_IPCAM_SET_OSD_RESP = 0x3B5;
	public static final int
	 	 UBIA_IO_EXT_ZIGBEEINFO_REQ           = 0x3002,//UBIA_IO_EXT_ZigbeeInfoReq
	 UBIA_IO_EXT_ZIGBEEINFO_RSP           = 0x3003;//UBIA_IO_EXT_ZigbeeInfoRsp
	 
	
	public static final int UBIA_IO_SET_ACPOWER_FREQ_REQ                = 0x0010,
    UBIA_IO_SET_ACPOWER_FREQ_RESP               = 0x0011,
    UBIA_IO_SET_SCENE_MODE_REQ               	= 0x0012,
    UBIA_IO_SET_SCENE_MODE_RESP                	= 0x0013,
    UBIA_IO_SET_PIR_REQ                			= 0X0014, 
    UBIA_IO_SET_PIR_RESP                		= 0X0015,
	UBIA_IO_SET_ELOCK_REQ                       = 0X0018,// 开锁 0， 关锁 1
	UBIA_IO_SET_ELOCK_RESP                      = 0X0019,
	UBIA_IO_SET_ACTIVETIME_REQ				 	 = 0X001E,//休眠
	UBIA_IO_SET_ACTIVETIME_RESP			 = 0X001F,
	UBIA_IO_SET_LED_REQ					     = 0X001C,
	UBIA_IO_SET_LED_RESP 					 = 0X001D;

	public static final int UBIA_IO_SET_CLOUD_REQ = 0X001A,
	UBIA_IO_SET_CLOUD_RESP                		= 0X001B;
	public static final int AVIOCTRL_AUTO_PAN_LIMIT = 28;
	public static final int AVIOCTRL_AUTO_PAN_SPEED = 27;
	public static final int AVIOCTRL_AUTO_PAN_START = 29;
	public static final int AVIOCTRL_CLEAR_AUX = 34;
	public static final int AVIOCTRL_ENVIRONMENT_INDOOR_50HZ = 0;
	public static final int AVIOCTRL_ENVIRONMENT_INDOOR_60HZ = 1;
	public static final int AVIOCTRL_ENVIRONMENT_NIGHT = 3;
	public static final int AVIOCTRL_ENVIRONMENT_OUTDOOR = 2;
	public static final int AVIOCTRL_EVENT_ALL = 0;
	public static final int AVIOCTRL_EVENT_EXPT_REBOOT = 16;
	public static final int AVIOCTRL_EVENT_IOALARM = 3;
	public static final int AVIOCTRL_EVENT_IOALARMPASS = 6;
	public static final int AVIOCTRL_EVENT_MOTIONDECT = 1;
	public static final int AVIOCTRL_EVENT_MOTIONPASS = 4;
	public static final int AVIOCTRL_EVENT_SDFAULT = 17;
	public static final int AVIOCTRL_EVENT_VIDEOLOST = 2;
	public static final int AVIOCTRL_EVENT_VIDEORESUME = 5;
	public static final int AVIOCTRL_LENS_APERTURE_CLOSE = 22;
	public static final int AVIOCTRL_LENS_APERTURE_OPEN = 21;
	public static final int AVIOCTRL_LENS_FOCAL_FAR = 26;
	public static final int AVIOCTRL_LENS_FOCAL_NEAR = 25;
	public static final int AVIOCTRL_LENS_ZOOM_IN = 23;
	public static final int AVIOCTRL_LENS_ZOOM_OUT = 24;
	public static final int AVIOCTRL_MOTOR_RESET_POSITION = 35;
	public static final int AVIOCTRL_PATTERN_RUN = 32;
	public static final int AVIOCTRL_PATTERN_START = 30;
	public static final int AVIOCTRL_PATTERN_STOP = 31;
	public static final int AVIOCTRL_PTZ_AUTO = 9;
	public static final int AVIOCTRL_PTZ_CLEAR_POINT = 11;
	public static final int AVIOCTRL_PTZ_DOWN = 2;
	public static final int AVIOCTRL_PTZ_FLIP = 19;
	public static final int AVIOCTRL_PTZ_GOTO_POINT = 12;
	public static final int AVIOCTRL_PTZ_LEFT = 3;
	public static final int AVIOCTRL_PTZ_LEFT_DOWN = 5;
	public static final int AVIOCTRL_PTZ_LEFT_UP = 4;
	public static final int AVIOCTRL_PTZ_MENU_ENTER = 18;
	public static final int AVIOCTRL_PTZ_MENU_EXIT = 17;
	public static final int AVIOCTRL_PTZ_MENU_OPEN = 16;
	public static final int AVIOCTRL_PTZ_MODE_RUN = 15;
	public static final int AVIOCTRL_PTZ_RIGHT = 6;
	public static final int AVIOCTRL_PTZ_RIGHT_DOWN = 8;
	public static final int AVIOCTRL_PTZ_RIGHT_UP = 7;
	public static final int AVIOCTRL_PTZ_SET_MODE_START = 13;
	public static final int AVIOCTRL_PTZ_SET_MODE_STOP = 14;
	public static final int AVIOCTRL_PTZ_SET_POINT = 10;
	public static final int AVIOCTRL_PTZ_START = 20;
	public static final int AVIOCTRL_PTZ_STOP = 0;
	public static final int AVIOCTRL_PTZ_UP = 1;
	public static final int AVIOCTRL_PTZ_START_CRUISE = 38;
	public static final int AVIOCTRL_PTZ_STOP_CRUISE = 39;
	public static final int AVIOCTRL_QUALITY_HIGH = 2;
	public static final int AVIOCTRL_QUALITY_LOW = 4;
	public static final int AVIOCTRL_QUALITY_MAX = 1;
	public static final int AVIOCTRL_QUALITY_MIDDLE = 3;
	public static final int AVIOCTRL_QUALITY_MIN = 5;
	public static final int AVIOCTRL_QUALITY_UNKNOWN = 0;
	public static final int AVIOCTRL_RECORD_PLAY_BACKWARD = 5;
	public static final int AVIOCTRL_RECORD_PLAY_END = 7;
	public static final int AVIOCTRL_RECORD_PLAY_RESUME = 8;
	public static final int AVIOCTRL_RECORD_PLAY_FORWARD = 4;
	public static final int AVIOCTRL_RECORD_PLAY_PAUSE = 0;
	public static final int AVIOCTRL_RECORD_PLAY_SEEKTIME = 6;
	public static final int AVIOCTRL_RECORD_PLAY_START = 16;
	public static final int AVIOCTRL_RECORD_PLAY_STEPBACKWARD = 3;
	public static final int AVIOCTRL_RECORD_PLAY_STEPFORWARD = 2;
	public static final int AVIOCTRL_RECORD_PLAY_STOP = 1;
	public static final int AVIOCTRL_SET_AUX = 33;
	public static final int AVIOCTRL_VIDEOMODE_FLIP = 1;
	public static final int AVIOCTRL_VIDEOMODE_FLIP_MIRROR = 3;
	public static final int AVIOCTRL_VIDEOMODE_MIRROR = 2;
	public static final int AVIOCTRL_VIDEOMODE_NORMAL = 0;
	public static final int AVIOTC_RECORDTYPE_ALAM = 2;
	public static final int AVIOTC_RECORDTYPE_FULLTIME = 1;
	public static final int AVIOTC_RECORDTYPE_MANUAL = 3;
	public static final int AVIOTC_RECORDTYPE_OFF = 0;
	public static final int AVIOTC_WIFIAPENC_INVALID = 0;
	public static final int AVIOTC_WIFIAPENC_NONE = 1;
	public static final int AVIOTC_WIFIAPENC_WEP = 2;
	public static final int AVIOTC_WIFIAPENC_WPA2_AES = 6;
	public static final int AVIOTC_WIFIAPENC_WPA2_PSK_AES = 10;
	public static final int AVIOTC_WIFIAPENC_WPA2_PSK_TKIP = 9;
	public static final int AVIOTC_WIFIAPENC_WPA2_TKIP = 5;
	public static final int AVIOTC_WIFIAPENC_WPA_AES = 4;
	public static final int AVIOTC_WIFIAPENC_WPA_PSK_AES = 8;
	public static final int AVIOTC_WIFIAPENC_WPA_PSK_TKIP = 7;
	public static final int AVIOTC_WIFIAPENC_WPA_TKIP = 3;
	public static final int AVIOTC_WIFIAPMODE_ADHOC = 0;
	public static final int AVIOTC_WIFIAPMODE_MANAGED = 1;
	public static final int IOTYPE_USER_IPCAM_AUDIOSTART = 768;
	public static final int IOTYPE_USER_IPCAM_AUDIOSTOP = 769;
	public static final int IOTYPE_USER_IPCAM_CURRENT_FLOWINFO = 914;
	public static final int IOTYPE_USER_IPCAM_DEVINFO_REQ = 0x330;
	public static final int IOTYPE_USER_IPCAM_DEVINFO_RESP = 817;
	public static final int IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_REQ = 896;
	public static final int IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_RESP = 897;
	public static final int IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ = 0x32a;
	public static final int IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_RESP = 811;
	public static final int IOTYPE_USER_IPCAM_GETMOTIONDETECT_REQ = 806;
	public static final int IOTYPE_USER_IPCAM_GETMOTIONDETECT_RESP = 807;
	public static final int IOTYPE_USER_IPCAM_GETRCD_DURATION_REQ = 790;
	public static final int IOTYPE_USER_IPCAM_GETRCD_DURATION_RESP = 791;
	public static final int IOTYPE_USER_IPCAM_GETRECORD_REQ = 786;
	public static final int IOTYPE_USER_IPCAM_GETRECORD_RESP = 787;
	public static final int IOTYPE_USER_IPCAM_GETSTREAMCTRL_REQ = 802;
	public static final int IOTYPE_USER_IPCAM_GETSTREAMCTRL_RESP = 803;
	public static final int IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ = 0x328;
	public static final int IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_RESP = 809;
	public static final int IOTYPE_USER_IPCAM_GETWIFI_REQ = 836;
	public static final int IOTYPE_USER_IPCAM_GETWIFI_RESP = 837;
	public static final int IOTYPE_USER_IPCAM_GETWIFI_RESP_2 = 839;
	public static final int IOTYPE_USER_IPCAM_GET_ENVIRONMENT_REQ = 866;
	public static final int IOTYPE_USER_IPCAM_GET_ENVIRONMENT_RESP = 867;
	public static final int IOTYPE_USER_IPCAM_GET_EVENTCONFIG_REQ = 1024;
	public static final int IOTYPE_USER_IPCAM_GET_EVENTCONFIG_RESP = 1025;
	public static final int IOTYPE_USER_IPCAM_GET_FLOWINFO_REQ = 912;
	public static final int IOTYPE_USER_IPCAM_GET_FLOWINFO_RESP = 913;
	public static final int IOTYPE_USER_IPCAM_GET_SAVE_DROPBOX_REQ = 1280;
	public static final int IOTYPE_USER_IPCAM_GET_SAVE_DROPBOX_RESP = 1281;
	public static final int IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ = 0x3a0;
	public static final int IOTYPE_USER_IPCAM_GET_TIMEZONE_RESP = 929;
	public static final int IOTYPE_USER_IPCAM_GET_VIDEOMODE_REQ = 882;
	public static final int IOTYPE_USER_IPCAM_GET_VIDEOMODE_RESP = 883;
	public static final int IOTYPE_USER_IPCAM_LISTEVENT_REQ = 792;
	public static final int IOTYPE_USER_IPCAM_LISTEVENT_RESP = 793;
	public static final int IOTYPE_USER_IPCAM_LISTWIFIAP_REQ = 832;
	public static final int IOTYPE_USER_IPCAM_LISTWIFIAP_RESP = 833;
	public static final int IOTYPE_USER_IPCAM_PTZ_COMMAND = 4097;
	public static final int IOTYPE_USER_IPCAM_RECEIVE_FIRST_IFRAME = 4098;
	public static final int IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL = 794;
	public static final int IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL_RESP = 795;
	public static final int IOTYPE_USER_IPCAM_SETMOTIONDETECT_REQ = 804;
	public static final int IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP = 805;
	public static final int IOTYPE_USER_IPCAM_SETPASSWORD_REQ = 818;
	public static final int IOTYPE_USER_IPCAM_SETPASSWORD_RESP = 819;
	
	public static final int  IOTYPE_USER_IPCAM_SET_DEVNAME_REQ			= 0x0334;
	public static final int  IOTYPE_USER_IPCAM_SET_DEVNAME_RESP			= 0x0335;
	public static final int IOTYPE_USER_IPCAM_SETRCD_DURATION_REQ = 788;
	public static final int IOTYPE_USER_IPCAM_SETRCD_DURATION_RESP = 789;
	public static final int IOTYPE_USER_IPCAM_SETRECORD_REQ = 784;
	public static final int IOTYPE_USER_IPCAM_SETRECORD_RESP = 785;
	public static final int IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ = 800;
	public static final int IOTYPE_USER_IPCAM_SETSTREAMCTRL_RESP = 801;
	public static final int IOTYPE_USER_IPCAM_SETWIFI_REQ = 834;
	public static final int IOTYPE_USER_IPCAM_SETWIFI_REQ_2 = 838;
	public static final int IOTYPE_USER_IPCAM_SETWIFI_RESP = 835;
	public static final int IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ = 864;
	public static final int IOTYPE_USER_IPCAM_SET_ENVIRONMENT_RESP = 865;
	public static final int IOTYPE_USER_IPCAM_SET_EVENTCONFIG_REQ = 1026;
	public static final int IOTYPE_USER_IPCAM_SET_EVENTCONFIG_RESP = 1027;
	public static final int IOTYPE_USER_IPCAM_SET_SAVE_DROPBOX_REQ = 1282;
	public static final int IOTYPE_USER_IPCAM_SET_SAVE_DROPBOX_RESP = 1283;
	public static final int IOTYPE_USER_IPCAM_SET_TIMEZONE_REQ = 944;
	public static final int IOTYPE_USER_IPCAM_SET_TIMEZONE_RESP = 945;
	public static final int IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ = 880;
	public static final int IOTYPE_USER_IPCAM_SET_VIDEOMODE_RESP = 881;
	public static final int IOTYPE_USER_IPCAM_SPEAKERSTART = 848;
	public static final int IOTYPE_USER_IPCAM_SPEAKERSTOP = 849;
	public static final int IOTYPE_USER_IPCAM_START = 0x1ff;
	public static final int IOTYPE_USER_IPCAM_STOP = 767;
	/* 20141217 add define for file transfer */

	public static final int IOTYPE_USER_FILE_LIST_REQ = 0X1300, // maxwell
			IOTYPE_USER_FILE_LIST_RSP = 0X1301, // maxwell

			IOTYPE_USER_FILE_DOWNLOAD_REQ = 0X1302, // maxwell
			IOTYPE_USER_FILE_DOWNLOAD_RSP = 0X1303, // maxwell

			IOTYPE_USER_FILE_UPLOAD_REQ = 0X1304, // maxwell
			IOTYPE_USER_FILE_UPLOAD_RSP = 0X1305, // maxwell

			IOTYPE_USER_FILE_TRANS_PKT_ACK = 0X1306, // maxwell
			IOTYPE_USER_FILE_TRANS_PKT_NAK = 0X1307, // maxwell

			IOTYPE_USER_FILE_TRANS_PKT_START = 0X1308, // maxwell
			IOTYPE_USER_FILE_TRANS_PKT_STOP = 0X1309, // maxwell
			/* 20141217 add define for file transfer */
			IOTYPE_USER_FILE_DELETE_REQ = 0X130A, // maxwell
			IOTYPE_USER_FILE_DELETE_RSP = 0X130B, // maxwell
			IOTYPE_USER_FILE_LIST_REQ1 = 0X1310; // maxwell

	public static final int IOTYPE_USER_IPCAM_EDIT_PTZ_RUN_STATUS_REQ = 0X210D,
			IOTYPE_USER_IPCAM_EDIT_PTZ_RUN_STATUS_RSP = 0X210E,
			IOTYPE_USER_IPCAM_PTZ_INFO_EVENT_REPORT = 0x210F; // Device Event
																// Report Msg

	public static final int IOTYPE_USER_IPCAM_GET_SENSOR_LIST_REQ = 0X2100;
	public static final int IOTYPE_USER_IPCAM_GET_SENSOR_LIST_RSP = 0X2101;
	public static final int IOTYPE_USER_IPCAM_EDIT_SENSOR_LIST_REQ = 0X2102;
	public static final int IOTYPE_USER_IPCAM_EDIT_SENSOR_LIST_RSP = 0X2103;

	public static final int IOTYPE_USER_IPCAM_EVENT_REPORT = 0x1FFF; // Device
																		// Event
																		// Report
																		// Msg
	// public static final int IOTYPE_USER_IPCAM_GET_ALARM_AREA_LIST_REQ =
	// 0X2104;
	// public static final int IOTYPE_USER_IPCAM_GET_ALARM_AREA_LIST_RSP =
	// 0X2105;
	public static final int IOTYPE_USER_IPCAM_EDIT_ALARM_AREA_LIST_REQ = 0X2106;
	public static final int IOTYPE_USER_IPCAM_EDIT_ALARM_AREA_LIST_RSP = 0X2107;

	// public static final int IOTYPE_USER_IPCAM_GET_ALARM_STATUS_REQ = 0X2108;
	// public static final int IOTYPE_USER_IPCAM_GET_ALARM_STATUS_RSP = 0X2109;
	public static final int IOTYPE_USER_IPCAM_EDIT_ALARM_STATUS_REQ = 0X210A;
	public static final int IOTYPE_USER_IPCAM_EDIT_ALARM_STATUS_RSP = 0X210B;
	public static final int IOTYPE_SENSOR_ALARM_P2P_PUSH_EVENT_ENUM = 0X210C;
	public static final int IOTYPE_USER_IPCAM_PTZ_PRESET_STATUS_REQ = 0x2110,
			IOTYPE_USER_IPCAM_PTZ_PRESET_STATUS_RSP = 0x2111;

	public static final int IOTYPE_USER_IPCAM_GET_ADVANCESETTINGS_REQ = 0x3C0,
			IOTYPE_USER_IPCAM_GET_ADVANCESETTINGS_RESP = 0x3C1;
	public static final int UBIA_IO_EXT_GARAGEDOOR_REQ = 0x3000,
			UBIA_IO_EXT_GARAGEDOOR_RSP = 0x3001;


	public static final int IOTYPE_USER_IPCAM_CAPTURE_PICTURE_REQ  = 0x2122; //拍照
	public static final int IOTYPE_USER_IPCAM_CAPTURE_PICTURE_RSP  = 0x2123;

	public static class SMsgAVIoctrlGetFlowInfoReq {

		public int channel;
		public int collect_interval;

	}

	public static final int MAX_FILE_NAME_SIZE = 32;
	public static final int MAX_PATH_NAME_SIZE = 128;

	// public static class SMsgEventIoctrlDownloadReq
	// {
	// byte command; //see ENUM_FILE_TRANS_CMD
	// byte segments;
	// short pktSize;
	// byte type;
	// byte resv[] = new byte[3];
	// int time;
	// byte fileSegment[] = new byte[12]; // one or more FileSegmentInfo append
	// // with
	// /**
	// * byte command; //see ENUM_FILE_TRANS_CMD<p>
	// byte segments;<p>
	// short pktSize;<p>
	// byte type;<p>
	// byte resv[] = new byte[3];<p>
	// int time;<p>
	// byte fileSegment[]<p>
	// */
	// public static byte[] parseContent(byte command, byte segments,
	// byte pktSize[], byte type , int time, byte fileSegment[]) {
	// byte[] var2 = new byte[24];
	// var2[0] = command;
	// var2[1] = segments;
	// System.arraycopy(pktSize, 0, var2, 2, 2);
	// System.arraycopy(Packet.intToByteArray_Little(time), 0, var2, 8, 4);
	// System.arraycopy(fileSegment, 0, var2, 12, 12);
	// return var2;
	// }
	// };
	public static class SMsgFileIoctrlDownloadReq {

		byte command; // see ENUM_FILE_TRANS_CMD
		byte segments; // 请求同时下载/上传的线程数（分块传输数）
		byte pktSize[] = new byte[2]; // 请求每个发送数据包的数据大小，最后一个包可能小于此值
		byte resv[] = new byte[3];
		byte filenamesize;
		byte filename[] = new byte[MAX_FILE_NAME_SIZE];
		byte fileSegment[] = new byte[12]; // one or more FileSegmentInfo append
											// with

		public static byte[] parseContent(byte command, byte segments,
				byte pktSize[], byte resv[], byte filenamesize,
				byte mfilename[], byte fileSegment[]) {
			byte[] var2 = new byte[52];
			var2[0] = command;
			var2[1] = segments;
			System.arraycopy(pktSize, 0, var2, 2, 2);
			// System.arraycopy(resv, 0, var2, 4, 2);
			var2[7] = filenamesize;
			if (mfilename.length < 32)
				System.arraycopy(mfilename, 0, var2, 8, mfilename.length);
			else
				System.arraycopy(mfilename, 0, var2, 8, 32);
			System.arraycopy(fileSegment, 0, var2, 40, 12);
			return var2;
		}
	};

	// 用在正常连续数据确认，以bitmap描述
	public static class SMsgFileTransPktAck {
		byte channel;
		byte segmentindex;
		byte resv[] = new byte[2];

		int BeginPktSeq;
		short PktNum; // 后面带Pktnum个整数，以bitmap描述（1为收到，0为未收到）
		short resv2;
		int bitmap;

		public static byte[] parseContent(byte channel, byte segmentindex,
				byte resv[], int BeginPktSeq, short PktNum, short resv2,
				int bitmap) {
			byte[] var7 = new byte[16];
			var7[0] = channel;
			var7[1] = segmentindex;
			System.arraycopy(Packet.intToByteArray_Little(BeginPktSeq), 0,
					var7, 4, 4);
			System.arraycopy(Packet.shortToByteArray_Little(PktNum), 0, var7,
					8, 2);
			return var7;
		}
	}; // 设备在通道上接收

	// 用在稀疏丢包的时候，单个描述需要重传的packet
	public static class SMsgFileTransPktNak {
		byte channel;
		byte segmentindex;
		byte resv[] = new byte[4];
		short PktNum; // 后面带Pktnum个整数，描述需要传输的PktSeq
		int resendindex;

		public static byte[] parseContent(byte channel, byte segmentindex,
				byte resv[], short PktNum, int resendindex) {
			byte[] var7 = new byte[12];
			var7[0] = channel;
			var7[1] = segmentindex;
			System.arraycopy(Packet.shortToByteArray_Little(PktNum), 0, var7,
					6, 2);
			System.arraycopy(Packet.intToByteArray_Little(resendindex), 0,
					var7, 8, 4);
			return var7;
		}

		public static byte[] parseContent(byte channel, int resendindex) {
			byte[] var7 = new byte[12];
			var7[0] = channel;
			var7[1] = 0;
			System.arraycopy(Packet.shortToByteArray_Little((short) 1), 0,
					var7, 6, 2);
			System.arraycopy(Packet.intToByteArray_Little(resendindex), 0,
					var7, 8, 4);
			return var7;
		}
	}; // 设备在通道上接收

	public static class SMsgFileIoctrlListFileReq {
		int channel; // Camera Index
		byte resv[] = new byte[3];
		byte pathnamesize;
		byte pathname[] = new byte[MAX_FILE_NAME_SIZE];
		int startTime; // search begin time,UTC second
		int endtime; // search end update time,UTC second
		byte mSMsgFileIoctrlListFileReq[] = new byte[48];

		public byte[] setSMsgFileIoctrlListFileReq(int channel,
				byte pathnamesize, byte pathname[], int startTime, int endtime) {
			System.arraycopy(Packet.intToByteArray_Little(channel), 0,
					mSMsgFileIoctrlListFileReq, 0, 4);
			mSMsgFileIoctrlListFileReq[8] = pathnamesize;
			System.arraycopy(pathname, 0, mSMsgFileIoctrlListFileReq, 8,
					MAX_FILE_NAME_SIZE);
			System.arraycopy(Packet.intToByteArray_Little(startTime), 0,
					mSMsgFileIoctrlListFileReq, 40, 4);
			System.arraycopy(Packet.intToByteArray_Little(endtime), 0,
					mSMsgFileIoctrlListFileReq, 44, 4);
			return mSMsgFileIoctrlListFileReq;
		}

		public static byte[] setSMsgFileIoctrlListFileReq(int channel) {
			byte mSMsgFileIoctrlListFileReq[] = new byte[48];
			System.arraycopy(Packet.intToByteArray_Little(channel), 0,
					mSMsgFileIoctrlListFileReq, 0, 4);
			return mSMsgFileIoctrlListFileReq;

		}
	};

	public static class SMsgFileIoctrlListFileResp {
		int channel; // Camera Index
		int total; // Total file amount in this directory
		int index; // package index, 0,1,2...;
		// because avSendIOCtrl() send package up to 1024 bytes one time, you
		// may want split search results to serveral package to send.
		byte endflag; // end flag; endFlag = 1 means this package is the last
						// one.
		byte count; // how much fileinfo in this package,MAX number: 1024(IOCtrl
					// packet size) / 80(bytes) = 12
		byte resv[] = new byte[2];

		public void setsMsgFileIoctrlListFileResp(
				byte[] sMsgFileIoctrlListFileResp) {
			// SFileInfo = sFileInfo;

			channel = Packet.byteArrayToInt_Little(sMsgFileIoctrlListFileResp,
					0);
			total = Packet.byteArrayToInt_Little(sMsgFileIoctrlListFileResp, 4);
			index = Packet.byteArrayToInt_Little(sMsgFileIoctrlListFileResp, 8);
			endflag = sMsgFileIoctrlListFileResp[12];
			count = sMsgFileIoctrlListFileResp[13];

		}

		public SMsgFileIoctrlListFileResp(byte[] sMsgFileIoctrlListFileResp) {
			// SFileInfo = sFileInfo;

			channel = Packet.byteArrayToInt_Little(sMsgFileIoctrlListFileResp,
					0);
			total = Packet.byteArrayToInt_Little(sMsgFileIoctrlListFileResp, 4);
			index = Packet.byteArrayToInt_Little(sMsgFileIoctrlListFileResp, 8);
			endflag = sMsgFileIoctrlListFileResp[12];
			count = sMsgFileIoctrlListFileResp[13];

		}

		public int getChannel() {
			return channel;
		}

		public void setChannel(int channel) {
			this.channel = channel;
		}

		public int getTotal() {
			return total;
		}

		public void setTotal(int total) {
			this.total = total;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public byte getEndflag() {
			return endflag;
		}

		public void setEndflag(byte endflag) {
			this.endflag = endflag;
		}

		public byte getCount() {
			return count;
		}

		public void setCount(byte count) {
			this.count = count;
		}

		public byte[] getResv() {
			return resv;
		}

		public void setResv(byte[] resv) {
			this.resv = resv;
		}

	};

	public static class SFileInfo implements Serializable {
		int filesize;
		byte flag; // 0:file, 1:Directory
		byte resv[] = new byte[2];
		byte filenamesize;
		byte filename[] = new byte[MAX_FILE_NAME_SIZE];
		int createtime; // file create time,UTC second
		int updatetime; // file last update time,UTC second
		byte SFileInfo[] = new byte[48];

		public int getFilesize() {
			return filesize;
		}

		public void setFilesize(int filesize) {
			this.filesize = filesize;
		}

		public byte getFlag() {
			return flag;
		}

		public void setFlag(byte flag) {
			this.flag = flag;
		}

		public byte[] getResv() {
			return resv;
		}

		public void setResv(byte[] resv) {
			this.resv = resv;
		}

		public byte getFilenamesize() {
			return filenamesize;
		}

		public void setFilenamesize(byte filenamesize) {
			this.filenamesize = filenamesize;
		}

		public byte[] getFilename() {
			return filename;
		}

		public void setFilename(byte[] filename) {
			this.filename = filename;
		}

		public int getCreatetime() {
			return createtime;
		}

		public void setCreatetime(int createtime) {
			this.createtime = createtime;
		}

		public int getUpdatetime() {
			return updatetime;
		}

		public void setUpdatetime(int updatetime) {
			this.updatetime = updatetime;
		}

		public byte[] getSFileInfo() {
			return SFileInfo;
		}

		public void setSFileInfo(byte[] sFileInfo) {
			// SFileInfo = sFileInfo;
			System.arraycopy(sFileInfo, 0, SFileInfo, 0, 48);
			filesize = Packet.byteArrayToInt_Little(sFileInfo, 0);
			flag = sFileInfo[4]; // 0:file, 1:Directory

			resv[0] = sFileInfo[5];
			resv[1] = sFileInfo[6];
			filenamesize = sFileInfo[7];
			System.arraycopy(sFileInfo, 8, filename, 0, filenamesize);
			createtime = Packet.byteArrayToInt_Little(sFileInfo, 40); // file
																		// create
																		// time,UTC
																		// second
			updatetime = Packet.byteArrayToInt_Little(sFileInfo, 44);
			// file last update time,UTC second
		}

		public SFileInfo(byte[] sFileInfo) {
			// SFileInfo = sFileInfo;
			System.arraycopy(sFileInfo, 0, SFileInfo, 0, 48);
			filesize = Packet.byteArrayToInt_Little(sFileInfo, 0);
			flag = sFileInfo[4]; // 0:file, 1:Directory

			resv[0] = sFileInfo[5];
			resv[1] = sFileInfo[6];
			filenamesize = sFileInfo[7];
			System.arraycopy(sFileInfo, 8, filename, 0, 32);
			createtime = Packet.byteArrayToInt_Little(sFileInfo, 40); // file
			// create
			// time,UTC
			// second
			updatetime = Packet.byteArrayToInt_Little(sFileInfo, 44);
			// file last update time,UTC second
		}

		public SFileInfo() {
		}
	};

	public static class SMsgFileIoctrlDownloadResp {
		byte command; // see ENUM_FILE_TRANS_CMD
		byte segments; // 允许同时下载/上传的线程数（分块传输数）
		short pktSize; // 请求每个发送数据包的数据大小，最后一个包可能小于此值
		byte status; // 0: success, 1:file not exist
		short resv;
		byte filenamesize;
		byte filename[] = new byte[MAX_FILE_NAME_SIZE];
		int filesize;
		byte md5sum[] = new byte[32 * 4]; // 32byte md5 checksum of totle file
		FileSegmentInfo fileSegment; // one or more
										// FileSegmentInfo
										// append with

		public SMsgFileIoctrlDownloadResp(byte initdata[]) {
			command = initdata[0];
			segments = initdata[1];
			pktSize = Packet.byteArrayToShort_Little(initdata, 2);
			status = initdata[4];
			resv = Packet.byteArrayToShort_Little(initdata, 5);
			filenamesize = initdata[7];

			System.arraycopy(initdata, 8, filename, 0, MAX_FILE_NAME_SIZE);
			filesize = Packet.byteArrayToInt_Little(initdata, 40);
			System.arraycopy(initdata, 44, md5sum, 0, 32 * 4);
			byte fileSegmentdata[] = new byte[12];
			System.arraycopy(initdata, 32 * 4 + 44, fileSegmentdata, 0, 12);
			fileSegment = new FileSegmentInfo(fileSegmentdata);

		}

		public byte getCommand() {
			return command;
		}

		public void setCommand(byte command) {
			this.command = command;
		}

		public byte getSegments() {
			return segments;
		}

		public void setSegments(byte segments) {
			this.segments = segments;
		}

		public short getPktSize() {
			return pktSize;
		}

		public void setPktSize(short pktSize) {
			this.pktSize = pktSize;
		}

		public byte getStatus() {
			return status;
		}

		public void setStatus(byte status) {
			this.status = status;
		}

		public short getResv() {
			return resv;
		}

		public void setResv(short resv) {
			this.resv = resv;
		}

		public byte getFilenamesize() {

			return filenamesize;
		}

		public void setFilenamesize(byte filenamesize) {
			this.filenamesize = filenamesize;
		}

		public byte[] getFilename() {
			byte filenamedata[] = new byte[filenamesize];
			System.arraycopy(filename, 0, filenamedata, 0, filenamesize);
			return filenamedata;
		}

		public String getsFileNameS() {
			// TODO Auto-generated method stub
			byte filenamedata[] = new byte[filenamesize];
			System.arraycopy(filename, 0, filenamedata, 0, filenamesize);
			String name = new String(filenamedata);
			name = name.replaceAll("\u0000", "");
			return name;
		}

		public void setFilename(byte[] filename) {
			this.filename = filename;
		}

		public int getFilesize() {
			return filesize;
		}

		public String getFilesizeS() {
			return filesize / 1204 + "kB";
		}

		public void setFilesize(int filesize) {
			this.filesize = filesize;
		}

		public byte[] getMd5sum() {
			return md5sum;
		}

		public void setMd5sum(byte[] md5sum) {
			this.md5sum = md5sum;
		}

		public FileSegmentInfo getFileSegment() {
			return fileSegment;
		}

		public void setFileSegment(FileSegmentInfo fileSegment) {
			this.fileSegment = fileSegment;
		}

	}

	public static class FileSegmentInfo {
		byte status; // 0x00：success，0x01:fail, 0x02... errcode
		byte ubicChannel; // UBIC 传输通道
		byte segmentindex; // 请求分段编号
		byte resv;
		int segmentoffset;
		int segmentsize;
		byte fileSegment[] = new byte[12];

		public FileSegmentInfo(byte[] initdata) {

			status = initdata[0];
			ubicChannel = initdata[1];
			segmentindex = initdata[2];
			resv = initdata[3];
			segmentoffset = Packet.byteArrayToInt_Little(initdata, 4);
			segmentsize = Packet.byteArrayToInt_Little(initdata, 8);
			System.arraycopy(initdata, 0, fileSegment, 0, 12);

		}

		public FileSegmentInfo() {
		}

		public byte getStatus() {
			return status;
		}

		public void setStatus(byte status) {
			this.status = status;
			fileSegment[0] = status;
		}

		public byte getUbicChannel() {
			return ubicChannel;
		}

		public void setUbicChannel(byte ubicChannel) {
			this.ubicChannel = ubicChannel;
			fileSegment[1] = ubicChannel;
		}

		public byte getSegmentindex() {
			return segmentindex;
		}

		public void setSegmentindex(byte segmentindex) {
			this.segmentindex = segmentindex;
			fileSegment[2] = segmentindex;
		}

		public byte getResv() {
			return resv;
		}

		public void setResv(byte resv) {
			this.resv = resv;
			fileSegment[3] = resv;
		}

		public int getSegmentoffset() {
			return segmentoffset;
		}

		public void setSegmentoffset(int segmentoffset) {
			this.segmentoffset = segmentoffset;
			System.arraycopy(Packet.intToByteArray_Little(segmentoffset), 0,
					fileSegment, 4, 4);

		}

		public int getSegmentsize() {
			return segmentsize;
		}

		public void setSegmentsize(int segmentsize) {
			this.segmentsize = segmentsize;
			System.arraycopy(Packet.intToByteArray_Little(segmentsize), 0,
					fileSegment, 8, 4);
		}

		public byte[] getFileSegment() {
			return fileSegment;
		}

		public void setFileSegment(byte[] fileSegment) {
			// this.fileSegment = fileSegment;
			System.arraycopy(fileSegment, 0, this.fileSegment, 0, 12);
			status = fileSegment[0];
			ubicChannel = fileSegment[1];
			segmentindex = fileSegment[2];
			resv = fileSegment[3];
			segmentoffset = Packet.byteArrayToInt_Little(fileSegment, 4);
			segmentsize = Packet.byteArrayToInt_Little(fileSegment, 8);
		}

	};

	// typedef struct
	// {
	// unsigned char control; // PTZ control command, refer to ENUM_PTZCMD
	// unsigned char speed; // PTZ control speed
	// unsigned char point; // no use in APP so far
	// unsigned char limit; // no use in APP so far
	// unsigned char aux; // no use in APP so far
	// unsigned char channel; // camera index
	// unsigned char reserve[2];
	// } SMsgAVIoctrlPtzCmd;

	// AVIOCTRL PTZ Command Value
	public static class ENUM_PTZCMD {
		public static final int AVIOCTRL_PTZ_STOP = 0;
		public static final int AVIOCTRL_PTZ_UP = 1;
		public static final int AVIOCTRL_PTZ_DOWN = 2;
		public static final int AVIOCTRL_PTZ_LEFT = 3;
		public static final int AVIOCTRL_PTZ_RESET = 0xf1;
		public static final int AVIOCTRL_PTZ_LEFT_UP = 4;
		public static final int AVIOCTRL_PTZ_LEFT_DOWN = 5;
		public static final int AVIOCTRL_PTZ_RIGHT = 6;
		public static final int AVIOCTRL_PTZ_RIGHT_UP = 7;
		public static final int AVIOCTRL_PTZ_RIGHT_DOWN = 8;
		public static final int AVIOCTRL_PTZ_AUTO = 9;
		public static final int AVIOCTRL_PTZ_SET_POINT = 10;
		public static final int AVIOCTRL_PTZ_CLEAR_POINT = 11;
		public static final int AVIOCTRL_PTZ_GOTO_POINT = 12;

		public static final int AVIOCTRL_PTZ_SET_MODE_START = 13;
		public static final int AVIOCTRL_PTZ_SET_MODE_STOP = 14;
		public static final int AVIOCTRL_PTZ_MODE_RUN = 15;

		public static final int AVIOCTRL_PTZ_MENU_OPEN = 16;
		public static final int AVIOCTRL_PTZ_MENU_EXIT = 17;
		public static final int AVIOCTRL_PTZ_MENU_ENTER = 18;

		public static final int AVIOCTRL_PTZ_FLIP = 19;
		public static final int AVIOCTRL_PTZ_START = 20;

		public static final int AVIOCTRL_LENS_APERTURE_OPEN = 21;
		public static final int AVIOCTRL_LENS_APERTURE_CLOSE = 22;

		public static final int AVIOCTRL_LENS_ZOOM_IN = 23;
		public static final int AVIOCTRL_LENS_ZOOM_OUT = 24;

		public static final int AVIOCTRL_LENS_FOCAL_NEAR = 25;
		public static final int AVIOCTRL_LENS_FOCAL_FAR = 26;

		public static final int AVIOCTRL_AUTO_PAN_SPEED = 27;

		public static final int AVIOCTRL_AUTO_PAN_LIMIT = 28;
		public static final int AVIOCTRL_AUTO_PAN_START = 29;

		public static final int AVIOCTRL_PATTERN_START = 30;
		public static final int AVIOCTRL_PATTERN_STOP = 31;
		public static final int AVIOCTRL_PATTERN_RUN = 32;

		public static final int AVIOCTRL_SET_AUX = 33;
		public static final int AVIOCTRL_CLEAR_AUX = 34;
		public static final int AVIOCTRL_MOTOR_RESET_POSITION = 35;
		public static final int AVIOCTRL_PTZ_AUTO_UP_DOWN = 36;
		public static final int AVIOCTRL_PTZ_AUTO_R_L = 37;
		public static final int AVIOCTRL_PTZ_START_CRUISE = 38;
		public static final int AVIOCTRL_PTZ_STOP_CRUISE = 39;
	};

	public static class ENUM_FILE_TRANS_CMD {
		public static final int FILE_TRANS_CMD_NULL = 0;
		public static final int FILE_TRANS_CMD_START = 1;
		public static final int FILE_TRANS_CMD_FINISH = 2;
		public static final int FILE_TRANS_CMD_PAUSE = 3;
		public static final int FILE_TRANS_CMD_RESUME = 4;
		public static final int FILE_TRANS_CMD_STOP = 5; // cancel
		public static final int FILE_TRANS_CMD_START_SEND = 6;
	};

	public static class PtzInfoEventStruct {
		int MaxLevelPos;
		int MaxVerPos;
		int CurLevelPos;
		int CurVerPos;

		public int getMaxLevelPos() {
			return MaxLevelPos;
		}

		public void setMaxLevelPos(int maxLevelPos) {
			MaxLevelPos = maxLevelPos;
		}

		public int getMaxVerPos() {
			return MaxVerPos;
		}

		public void setMaxVerPos(int maxVerPos) {
			MaxVerPos = maxVerPos;
		}

		public int getCurLevelPos() {
			return CurLevelPos;
		}

		public void setCurLevelPos(int curLevelPos) {
			CurLevelPos = curLevelPos;
		}

		public int getCurVerPos() {
			return CurVerPos;
		}

		public void setCurVerPos(int curVerPos) {
			CurVerPos = curVerPos;
		}

	};

	public static class SMsgAVIoCtrlPtzInfoEventStruct {
		STimeDay stTime;
		int time; // UTC Time
		int channel; // Camera Index
		int event; // event type, refer to SENSOR_ALARM_P2P_PUSH_EVENT_ENUM
		PtzInfoEventStruct PtzInfoEvent;

	};

	public static class SMsgAVIoCtrlSensorAlarmEvent {
		final static int ALARM_SENSOR_NAME_MAX_LEN = 32;

		STimeDay stTime;
		int time; // UTC Time
		int channel; // Camera Index
		int event; // event type, refer to SENSOR_ALARM_P2P_PUSH_EVENT_ENUM
		byte gSensorData[] = new byte[ALARM_SENSOR_NAME_MAX_LEN + 8];
		int AlarmAreaID;
		byte AlarmAreaName[] = new byte[ALARM_SENSOR_NAME_MAX_LEN];
		private byte[] mBuf = new byte[8];
		int saveIndex;

		public SMsgAVIoCtrlSensorAlarmEvent(byte PushData[]) {
			System.arraycopy(PushData, 0, this.mBuf, 0, 8);
			stTime = new STimeDay(mBuf);
			time = Packet.byteArrayToInt_Little(PushData, 8);
			channel = Packet.byteArrayToInt_Little(PushData, 12);
			event = Packet.byteArrayToInt_Little(PushData, 16);

			System.arraycopy(PushData, 20, gSensorData, 0,
					ALARM_SENSOR_NAME_MAX_LEN + 8);
			AlarmAreaID = Packet.byteArrayToInt_Little(PushData,
					ALARM_SENSOR_NAME_MAX_LEN + 8 + 20);
			System.arraycopy(PushData, ALARM_SENSOR_NAME_MAX_LEN + 8 + 20 + 4,
					AlarmAreaName, 0, ALARM_SENSOR_NAME_MAX_LEN);
			saveIndex = Packet.byteArrayToInt_Little(PushData,
					ALARM_SENSOR_NAME_MAX_LEN + ALARM_SENSOR_NAME_MAX_LEN + 8
							+ 20 + 4);
		}

		public int getSaveIndex() {
			return saveIndex;
		}

		public void setSaveIndex(int saveIndex) {
			this.saveIndex = saveIndex;
		}

		public STimeDay getStTime() {
			return stTime;
		}

		public void setStTime(STimeDay stTime) {
			this.stTime = stTime;
		}

		public int getTime() {
			return time;
		}

		public void setTime(int time) {
			this.time = time;
		}

		public int getChannel() {
			return channel;
		}

		public void setChannel(int channel) {
			this.channel = channel;
		}

		public int getEvent() {
			return event;
		}

		public void setEvent(int event) {
			this.event = event;
		}

		public byte[] getgSensorData() {
			return gSensorData;
		}

		public void setgSensorData(byte[] gSensorData) {
			this.gSensorData = gSensorData;
		}

		public int getAlarmAreaID() {
			return AlarmAreaID;
		}

		public void setAlarmAreaID(int alarmAreaID) {
			AlarmAreaID = alarmAreaID;
		}

		public byte[] getAlarmAreaName() {
			return AlarmAreaName;
		}

		public void setAlarmAreaName(byte[] alarmAreaName) {
			AlarmAreaName = alarmAreaName;
		}

		public byte[] getmBuf() {
			return mBuf;
		}

		public void setmBuf(byte[] mBuf) {
			this.mBuf = mBuf;
		}

		public static int getAlarmSensorNameMaxLen() {
			return ALARM_SENSOR_NAME_MAX_LEN;
		}

	}

	public static class USER_EDIT_PARAMS_CMD {
		public static final int USER_EDIT_PARAMS_CMD_DEL_ONE = 0;// DEL
																	// ONE/////////////////////////////////////////////////////////////////////////////////
		public static final int USER_EDIT_PARAMS_CMD_DEL_ALL = 1;// DEL ALL
																	// ///////////////////
																	// Type ENUM
																	// Define
																	// ////////////////////////////////////////////
		// ///////////////////////////////////////////////////////////////////////////////
		public static final int USER_EDIT_PARAMS_CMD_GET_ALL = 2;// GET ALL
																	// typedef
																	// enum
		public static final int USER_EDIT_PARAMS_CMD_GET_ONE = 3;// GET ONE {
		// AVIOCTRL_OK = 0x00,
		public static final int USER_EDIT_PARAMS_CMD_SET_ALL = 4;// GET ALL
																	// AVIOCTRL_ERR
																	// = -0x01,
		public static final int USER_EDIT_PARAMS_CMD_SET_ONE = 5;// GET ONE
																	// AVIOCTRL_ERR_PASSWORD
		public static final int USER_EDIT_PARAMS_CMD_ADD_ONE = 6;// GET ONE // =
																	// AVIOCTRL_ERR
																	// - 0x01,
	};

	public static class SENSOR_ALARM_CMD {
		public static final int SENSOR_ALARM_CMD_ENABLE_MATCH_CODE = 0;
		public static final int SENSOR_ALARM_CMD_DISABLE_MATCH_CODE = 1;

		public static final int SENSOR_ALARM_CMD_ENABLE_ALL_ALARM = 2;
		public static final int SENSOR_ALARM_CMD_DISABLE_ALL_ALARM = 3;
		public static final int SENSOR_ALARM_CMD_ENABLE_AREA_ALARM = 4;

		public static final int SENSOR_ALARM_CMD_GET_DEVICE_STATUS = 5;
	};

	public static class SENSOR_ALARM_P2P_PUSH_EVENT_ENUM {
		public static final int SENSOR_ALARM_P2P_PUSH_EVENT_DISABLE_ALL_ALARM = 0;
		public static final int SENSOR_ALARM_P2P_PUSH_EVENT_ENABLE_ALL_ALARM = 1;
		public static final int SENSOR_ALARM_P2P_PUSH_EVENT_AREA_MODE_ALARM = 2;
		public static final int SENSOR_ALARM_P2P_PUSH_EVENT_DISABLE_MATCH_CODE = 3;
		public static final int SENSOR_ALARM_P2P_PUSH_EVENT_ENABLE_MATCH_CODE = 4;

		public static final int SENSOR_ALARM_P2P_PUSH_EVENT_TRIGER_ONE_SENSOR_ALARM = 5;
		public static final int SENSOR_ALARM_P2P_PUSH_EVENT_TRIGER_AREA_SENSOR_ALARM = 6;
		public static final int SENSOR_ALARM_P2P_PUSH_EVENT_MATCH_CODE_INFO = 7;
		public static final int SENSOR_ALARM_P2P_PUSH_EVENT_MATCH_HASCODED = 8;
		public static final int SENSOR_ALARM_P2P_PUSH_EVENT_TRIGER_ONE_SOS_ALARM = 9;
	};

	public class SMsgAVIoctrlListEventResp {

		int channel;
		byte count;
		byte endflag;
		byte index;
		byte reserved;
		int total;
		SAvEvent stEvent;

	}

	public static class SMsgAVIoctrlGetSupportStreamReq {

		public static int getContentSize() {
			return 4;
		}

		public static byte[] parseContent() {
			return new byte[4];
		}
	}

	public class SMsgAVIoctrlGetWifiResp {

		byte enctype;
		byte mode;
		byte[] password = new byte[32];
		byte signal;
		byte[] ssid = new byte[32];
		byte status;

	}

	public static class SMsgAVIoctrlGetVideoModeReq {

		int channel;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int var0) {
			byte[] var1 = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var1, 0, 4);
			return var1;
		}
	}

	public class SMsgAVIoctrlDeviceInfoResp {

		int channel;
		int free;
		byte[] model = new byte[16];
		byte[] reserved = new byte[8];
		int total;
		byte[] vendor = new byte[16];
		int version;

	}

	public class SMsgAVIoctrlGetRecordResp {

		int channel;
		int recordType;

	}

	public class SMsgAVIoctrlSetRcdDurationResp {

		byte[] reserved = new byte[3];
		byte result;

	}

	public class SMsgAVIoctrlEventConfig {

		long channel;
		byte ftp;
		byte localIO;
		byte mail;
		byte p2pPushMsg;

	}

	public static class SMsgAVIoctrlSetRecordReq {

		int channel;
		int recordType;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int var0, int var1) {
			byte[] var2 = new byte[12];
			byte[] var3 = Packet.intToByteArray_Little(var0);
			byte[] var4 = Packet.intToByteArray_Little(var1);
			System.arraycopy(var3, 0, var2, 0, 4);
			System.arraycopy(var4, 0, var2, 4, 4);
			return var2;
		}
	}

	public static class SMsgAVIoctrlTimeZone {

		public int cbSize;
		public int nGMTDiff;
		public int nIsSupportTimeZone;
		public byte[] szTimeZoneString = new byte[256];

		public static byte[] parseContent() {
			return new byte[268];
		}

		public static byte[] parseContent(int var0, int var1, int var2,
				byte[] var3) {
			byte[] var4 = new byte[268];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var4, 0, 4);
			System.arraycopy(Packet.intToByteArray_Little(var1), 0, var4, 4, 4);
			System.arraycopy(Packet.intToByteArray_Little(var2), 0, var4, 8, 4);
			System.arraycopy(var3, 0, var4, 12, var3.length);
			return var4;
		}
	}

	public class SMsgAVIoctrlListWifiApResp {

		int number;
		SWifiAp stWifiAp;

	}

	public static class STimeDay {

		public byte day;
		public byte hour;
		private byte[] mBuf = new byte[8];
		public byte minute;
		public byte month;
		public byte second;
		public byte wday;
		public short year;

		public STimeDay(byte[] var1) {
			System.arraycopy(var1, 0, this.mBuf, 0, 8);
			this.year = Packet.byteArrayToShort_Little(var1, 0);
			this.month = var1[2];
			this.day = var1[3];
			this.wday = var1[4];
			this.hour = var1[5];
			this.minute = var1[6];
			this.second = var1[7];
		}

		public int getmillis() {

			Log.v("getmillis", "时间转化后的毫秒 秒数为：" + this.getTimeInMillis() / 1000);
			return (int) (this.getTimeInMillis());
		}

		public int getsecond() {

			Log.v("test", "时间转化后的秒数为：" + this.getTimeInMillis() / 1000);
			return (int) (this.getTimeInMillis() / 1000);
		}

		public long getTime(int seconds) {
			Calendar var1 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			var1.set(this.year, -1 + this.month, this.day, this.hour,
					this.minute, this.second);
			Log.v("SAvEvent", "this.getMonth =" + var1.getTime().getMonth()
					+ "this.getHours =" + var1.getTime().getHours()
					+ "this.getMinutes =" + var1.getTime().getMinutes()
					+ "this.second =" + var1.getTime().getSeconds());
			// Log.v("SAvEvent", "时间转化qian的秒数为：" + var1.getTimeInMillis() /
			// 1000);
			// Log.v("SAvEvent", "时间转化qian的hao秒数为：" + var1.getTimeInMillis());
			// {
			var1.setTimeInMillis(var1.getTimeInMillis() + seconds * 1000);
			var1.setTimeZone(TimeZone.getTimeZone("gmt"));
			this.minute = (byte) var1.getTime().getMinutes();
			this.second = (byte) var1.getTime().getSeconds();
			this.mBuf[2] = this.month;
			this.mBuf[3] = this.day;// = var1[3];
			this.mBuf[4] = this.wday;// = var1[4];
			this.mBuf[5] = this.hour;// = var1[5];
			this.mBuf[6] = this.minute;// = var1[6];
			this.mBuf[7] = this.second;// = var1[7];
			Calendar var2 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			var2.set(this.year, -1 + this.month, this.day, this.hour,
					this.minute, this.second);
			// Log.v("SAvEvent", "this.getMonth =" +
			// var2.getTime().getMonth()
			// + "this.getHours =" + var2.getTime().getHours()
			// + "this.getMinutes =" + var2.getTime().getMinutes()
			// + "this.second =" + var2.getTime().getSeconds());
			Log.v("SAvEvent", "时间转化后的秒数为：" + var2.getTimeInMillis() / 1000);
			// }
			return var2.getTimeInMillis() / 1000;
		}

		public byte[] addtime(int seconds) {
			Calendar var1 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			var1.set(this.year, -1 + this.month, this.day, this.hour,
					this.minute, this.second);
			Log.v("SAvEvent", "this.getMonth =" + var1.getTime().getMonth()
					+ "this.getHours =" + var1.getTime().getHours()
					+ "this.getMinutes =" + var1.getTime().getMinutes()
					+ "this.second =" + var1.getTime().getSeconds());
			// Log.v("SAvEvent", "时间转化qian的秒数为：" + var1.getTimeInMillis() /
			// 1000);
			// Log.v("SAvEvent", "时间转化qian的hao秒数为：" + var1.getTimeInMillis());
			{
				var1.setTimeInMillis(var1.getTimeInMillis() + seconds * 1000);
				var1.setTimeZone(TimeZone.getTimeZone("gmt"));
				this.minute = (byte) var1.getTime().getMinutes();
				this.second = (byte) var1.getTime().getSeconds();
				this.mBuf[2] = this.month;
				this.mBuf[3] = this.day;// = var1[3];
				this.mBuf[4] = this.wday;// = var1[4];
				this.mBuf[5] = this.hour;// = var1[5];
				this.mBuf[6] = this.minute;// = var1[6];
				this.mBuf[7] = this.second;// = var1[7];
				Calendar var2 = Calendar.getInstance(TimeZone
						.getTimeZone("gmt"));
				var2.set(this.year, -1 + this.month, this.day, this.hour,
						this.minute, this.second);
				// Log.v("SAvEvent", "this.getMonth =" +
				// var2.getTime().getMonth()
				// + "this.getHours =" + var2.getTime().getHours()
				// + "this.getMinutes =" + var2.getTime().getMinutes()
				// + "this.second =" + var2.getTime().getSeconds());
				// seektime=var2.getTimeInMillis() / 1000;
				Log.v("SAvEvent", "时间转化后的秒数为：" + var2.getTimeInMillis() / 1000);
			}
			return mBuf;
		}

		public static String getLocalTime(long timems) {
			Calendar var1 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			var1.setTimeInMillis(timems);
			SimpleDateFormat var2 ;
			if(MainCameraFragment.isChinaSetting()){
				  var2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			}else{
				  var2 = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
			}
			var2.setTimeZone(TimeZone.getDefault());
			return var2.format(var1.getTime());
		}

		public static byte[] parseContent(int var0, int var1, int var2,
				int var3, int var4, int var5, int var6) {
			byte[] var7 = new byte[8];
			System.arraycopy(Packet.shortToByteArray_Little((short) var0), 0,
					var7, 0, 2);
			var7[2] = (byte) var1;
			var7[3] = (byte) var2;
			var7[4] = (byte) var3;
			var7[5] = (byte) var4;
			var7[6] = (byte) var5;
			var7[7] = (byte) var6;
			return var7;
		}

		public String getLocalTime() {
			Calendar var1 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			var1.setTimeInMillis(this.getTimeInMillis());
			// SimpleDateFormat var2 = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat var2 = new SimpleDateFormat(
					"MM-dd-yyyy hh:mm:ss a");
			var2.setTimeZone(TimeZone.getDefault());
			return var2.format(var1.getTime());
		}

		public String getLocalTimeDownLoadFormat() {
			Calendar var1 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			var1.setTimeInMillis(this.getTimeInMillis());
			// SimpleDateFormat var2 = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat var2 = new SimpleDateFormat("yyyyMMddHHmmss");
			var2.setTimeZone(TimeZone.getTimeZone("gmt"));
			return var2.format(var1.getTime());
		}

		public String getLocalTimeDownLoadShowFormat() {
			Calendar var1 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			var1.setTimeInMillis(this.getTimeInMillis());
			// SimpleDateFormat var2 = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat var2 = new SimpleDateFormat("yyyyMMddHHmmss");
			var2.setTimeZone(TimeZone.getDefault());
			return var2.format(var1.getTime());
		}

		public int getLocalTimeDownLoadShowFormatInSecond() {
			Calendar var1 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			var1.setTimeInMillis(this.getTimeInMillis());
			var1.setTimeZone(TimeZone.getTimeZone("gmt"));
			return (int) (var1.getTimeInMillis() / 1000);
		}

		public long getTimeInMillis() {
			Calendar var1 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			var1.set(this.year, -1 + this.month, this.day, this.hour,
					this.minute, this.second);
			return var1.getTimeInMillis();
		}

		public static String changeToStringLocalTime(long timems) {
			Calendar var1 = Calendar.getInstance(TimeZone.getDefault());
			var1.setTimeInMillis(timems);
			SimpleDateFormat var2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			var2.setTimeZone(TimeZone.getDefault());
			return var2.format(var1.getTime());
		}

		public byte[] toByteArray() {
			return this.mBuf;
		}
	}

	public static class SMsgAVIoctrlSetVideoModeReq {

		int channel;
		byte mode;
		byte[] reserved = new byte[3];

		public static byte[] parseContent(int var0, byte var1) {
			byte[] var2 = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var2, 0, 4);
			var2[4] = var1;
			return var2;
		}
	}

	public static class SMsgAVIoctrlGetRecordReq {

		int channel;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int var0) {
			byte[] var1 = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var1, 0, 4);
			return var1;
		}
	}

	public static class SMsgAVIoctrlGetStreamCtrlReq {

		int channel;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int var0) {
			byte[] var1 = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var1, 0, 4);
			return var1;
		}
	}

	public class SMsgAVIoctrlPlayRecordResp {

		int channel;
		byte[] reserved = new byte[4];
		int result;

	}

	public class SMsgAVIoctrlSetStreamCtrlResp {

		byte[] reserved = new byte[4];
		int result;

	}

	public class SMsgAVIoctrlSetWifiResp {

		byte[] reserved = new byte[3];
		byte result;

	}

	public static class SMsgAVIoctrlSetEnvironmentReq {

		int channel;
		byte mode;
		byte[] reserved = new byte[3];

		public static byte[] parseContent(int var0, byte var1) {
			byte[] var2 = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var2, 0, 4);
			var2[4] = var1;
			return var2;
		}
	}

	public class SMsgAVIoctrlGetEnvironmentResp {

		int channel;
		byte mode;
		byte[] reserved = new byte[3];

	}

	public class SMsgAVIoctrlEvent {

		int channel;
		int event;
		byte[] reserved = new byte[4];
		STimeDay stTime;

	}

	public class SMsgAVIoctrlGetAudioOutFormatResp {

		public int channel;
		public int format;

	}

	public static class SMsgAVIoctrlSetDropbox {

		public int nLinked;
		byte[] reserved = new byte[4];
		byte[] szAccessToken = new byte[32];
		byte[] szAccessTokenSecret = new byte[32];
		byte[] szAppKey = new byte[32];
		byte[] szLinkUDID = new byte[64];
		byte[] szSecret = new byte[32];

		public static byte[] parseContent(int var0, String var1, String var2,
				String var3, String var4, String var5, String var6) {
			byte[] var7 = new byte[198];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var7, 0, 2);
			byte[] var8 = var1.getBytes();
			System.arraycopy(var8, 0, var7, 2, var8.length);
			byte[] var9 = var2.getBytes();
			System.arraycopy(var9, 0, var7, 66, var9.length);
			byte[] var10 = var3.getBytes();
			System.arraycopy(var10, 0, var7, 98, var10.length);
			byte[] var11 = var4.getBytes();
			System.arraycopy(var11, 0, var7, 130, var11.length);
			byte[] var12 = var5.getBytes();
			System.arraycopy(var12, 0, var7, 162, var12.length);
			byte[] var13 = var6.getBytes();
			System.arraycopy(var13, 0, var7, 194, var13.length);
			return var7;
		}
	}

	public static class SMsgAVIoctrlListEventReq {

		int channel;
		byte[] endutctime = new byte[8];
		byte event;
		byte[] reversed = new byte[2];
		byte[] startutctime = new byte[8];
		byte status;

		public static byte[] parseConent(int var0, long var1, long var3,
				byte var5, byte var6) {
			Calendar var7 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			Calendar var8 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			var7.setTimeInMillis(var1);
			var8.setTimeInMillis(var3);
			System.out.println("search from " + var7.get(1) + "/" + var7.get(2)
					+ "/" + var7.get(5) + " " + var7.get(11) + ":"
					+ var7.get(12) + ":" + var7.get(13));
			System.out.println("       to   " + var8.get(1) + "/" + var8.get(2)
					+ "/" + var8.get(5) + " " + var8.get(11) + ":"
					+ var8.get(12) + ":" + var8.get(13));
			byte[] var9 = new byte[24];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var9, 0, 4);
			System.arraycopy(
					STimeDay.parseContent(var7.get(1),
							1 + var7.get(2), var7.get(5), var7.get(7),
							var7.get(11), var7.get(12), 0), 0, var9, 4, 8);
			System.arraycopy(
					STimeDay.parseContent(var8.get(1),
							1 + var8.get(2), var8.get(5), var8.get(7),
							var8.get(11), var8.get(12), 0), 0, var9, 12, 8);
			var9[20] = var5;
			var9[21] = var6;
			return var9;
		}
	}

	public class SMsgAVIoctrlGetVideoModeResp {

		int channel;
		byte mode;
		byte[] reserved = new byte[3];

	}

	public static class SMsgAVIoctrlReceiveFirstIFrame {

		int channel;
		int recordType;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int var0, int var1) {
			byte[] var2 = new byte[12];
			byte[] var3 = Packet.intToByteArray_Little(var0);
			byte[] var4 = Packet.intToByteArray_Little(var1);
			System.arraycopy(var3, 0, var2, 0, 4);
			System.arraycopy(var4, 0, var2, 4, 4);
			return var2;
		}
	}

	public class SMsgAVIoctrlGetMotionDetectResp {

		int channel;
		int sensitivity;

	}

	public static class SMsgAVIoctrlFormatExtStorageReq {

		byte[] reserved = new byte[4];
		int storage;

		public static byte[] parseContent(int var0) {
			byte[] var1 = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var1, 0, 4);
			return var1;
		}
	}

	public class SMsgAVIoctrlGetRcdDurationReq {

		int channel;
		byte[] reserved = new byte[4];

	}

	public static class SAvEvent {

		byte event;
		byte[] reserved = new byte[2];
		byte status;
		byte[] utctime = new byte[8];

		public static int getTotalSize() {
			return 12;
		}
	}

	public static class SMsgAVIoctrlGetAudioOutFormatReq {

		public static byte[] parseContent() {
			return new byte[8];
		}
	}

	public static class SMsgAVIoctrlGetFlowInfoResp {

		public int channel;
		public int collect_interval;

		public static byte[] parseContent(int var0, int var1) {
			byte[] var2 = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var2, 0, 4);
			System.arraycopy(Packet.intToByteArray_Little(var1), 0, var2, 4, 4);
			return var2;
		}
	}

	public static class SMsgAVIoctrlSetWifiReq {

		byte enctype;
		byte mode;
		byte[] password = new byte[32];
		byte[] reserved = new byte[10];
		byte[] ssid = new byte[32];

		public static byte[] parseContent(byte[] var0, byte[] var1, byte var2,
				byte var3) {
			byte[] var4 = new byte[76];
			System.arraycopy(var0, 0, var4, 0, var0.length);
			if (var1 != null)
				System.arraycopy(var1, 0, var4, 32, var1.length);
			else
				for (int i = 0; i < 32; i++)
					var4[i] = '\0';
			var4[64] = var2;
			var4[65] = var3;
			return var4;
		}
	}

	public class SMsgAVIoctrlSetVideoModeResp {

		int channel;
		byte[] reserved = new byte[3];
		byte result;

	}

	public static class SMsgAVIoctrlSetMotionDetectReq {

		int channel;
		int sensitivity;

		public static byte[] parseContent(int var0, int var1) {
			byte[] var2 = new byte[8];
			byte[] var3 = Packet.intToByteArray_Little(var0);
			byte[] var4 = Packet.intToByteArray_Little(var1);
			System.arraycopy(var3, 0, var2, 0, 4);
			System.arraycopy(var4, 0, var2, 4, 4);
			return var2;
		}
	}

	public class SMsgAVIoctrlGetSupportStreamResp {

		public SStreamDef[] mStreamDef;
		public long number;

	}

	public class SMsgAVIoctrlGetRcdDurationResp {

		int channel;
		int durasecond;
		int presecond;

	}

	public static class SMsgAVIoctrlDeviceInfoReq {

		static byte[] reserved = new byte[4];

		public static byte[] parseContent() {
			return reserved;
		}
	}

	public static class SStreamDef {

		public int channel;
		public int index;

		public SStreamDef(byte[] var1) {
			this.index = Packet.byteArrayToShort_Little(var1, 0);
			this.channel = Packet.byteArrayToShort_Little(var1, 2);
		}

		public String toString() throws NotFoundException {
			String ch = "VGA";
			try {

				if (this.index == 0) {
					// Resources res = getResources();
					String text = UbiaApplication.getInstance().getString(
							R.string.AVIOCTRLDEFs_page8_vga);
					ch = text;// "VGA";
				} else if (this.index == 1) {
					String text = UbiaApplication.getInstance().getString(
							R.string.AVIOCTRLDEFs_page8_hd);
					ch = text;
				}
				// return "CH" + String.valueOf(1 + this.index);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ch;
		}
	}

	public class SMsgAVIoctrlFormatExtStorageResp {

		byte[] reserved = new byte[3];
		byte result;
		int storage;

	}

	public class SMsgAVIoctrlGetStreamCtrlResp {

		int channel;
		byte quality;
		byte[] reserved = new byte[3];

	}

	public static class SMsgAVIoctrlCurrentFlowInfo {

		public int channel;
		public int elapse_time_ms;
		public int lost_incomplete_frame_count;
		public int total_actual_frame_size;
		public int total_expected_frame_size;
		public int total_frame_count;

		public static byte[] parseContent(int var0, int var1, int var2,
				int var3, int var4, int var5) {
			byte[] var6 = new byte[32];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var6, 0, 4);
			System.arraycopy(Packet.intToByteArray_Little(var1), 0, var6, 4, 4);
			System.arraycopy(Packet.intToByteArray_Little(var2), 0, var6, 8, 4);
			System.arraycopy(Packet.intToByteArray_Little(var3), 0, var6, 12, 4);
			System.arraycopy(Packet.intToByteArray_Little(var4), 0, var6, 16, 4);
			System.arraycopy(Packet.intToByteArray_Little(var5), 0, var6, 20, 4);
			return var6;
		}
	}

	public static class SMsgAVIoctrlListWifiApReq {

		static byte[] reserved = new byte[4];

		public static byte[] parseContent() {
			return reserved;
		}
	}

	public static class SMsgAVIoctrlGetWifiReq {

		static byte[] reserved = new byte[4];

		public static byte[] parseContent() {
			return reserved;
		}
	}

	public class SMsgAVIoctrlSetMotionDetectResp {

		byte[] reserved = new byte[3];
		byte result;

	}

	public class SMsgAVIoctrlSetEnvironmentResp {

		int channel;
		byte[] reserved = new byte[3];
		byte result;

	}

	public static class SMsgAVIoctrlGetMotionDetectReq {

		int channel;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int var0) {
			byte[] var1 = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var1, 0, 4);
			return var1;
		}
	}

	public static class SMsgAVIoctrlGetMICVolumeReq {

		int channel;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int var0) {
			byte[] var1 = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var1, 0, 4);
			return var1;
		}
	}

	public static class SMsgAVIoctrlGetSPVolumeReq {

		int channel;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int var0) {
			byte[] var1 = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var1, 0, 4);
			return var1;
		}
	}

	public static class SMsgAVIoctrlSetVolumeReq {

		int channel;
		byte volume;
		byte[] rsv = new byte[3];
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int var0, byte var1) {
			byte[] var2 = new byte[12];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var2, 0, 4);
			var2[4] = var1;
			return var2;
		}
	}

	public static class SMsgAVIoctrlSetPasswdReq {

		byte[] newPasswd = new byte[32];
		byte[] oldPasswd = new byte[32];

		public static byte[] parseContent(String var0, String var1,String deviceName) {
			byte[] var2 = var0.getBytes();
			byte[] var3 = var1.getBytes();
			byte[] var4 = new byte[64+128+4];
			System.arraycopy(var2, 0, var4, 0, var2.length);
			System.arraycopy(var3, 0, var4, 32, var3.length);
			System.arraycopy(Packet.shortToByteArray_Little((short) 0xFFEE) , 0, var4, 64,2);
			Log.e("","UbiaApplication.myCountryCode:"+UbiaApplication.myCountryCode);
			var4[66] = (byte)StringUtils.getCurrentLocaltionISOCountryCodeNumber(UbiaApplication.myCountryCode); 
			String nickName = new String( Base64.encode(deviceName.getBytes(),0));
			var4[67] =  (byte) nickName.getBytes().length;
			nickName =  nickName.replace("+", "-"); 
			nickName =  nickName.replace("/", "_");
			nickName =  nickName.replace("=", ",");
	 
			System.arraycopy(nickName.getBytes(), 0, var4, 68, var4[67] );
			return var4;
		}
		public static byte[] parseContentwithCountry(String var0, String var1,String deviceName,String myCountryCode) {
			byte[] var2 = var0.getBytes();
			byte[] var3 = var1.getBytes();
			byte[] var4 = new byte[64+128+4];
			System.arraycopy(var2, 0, var4, 0, var2.length);
			System.arraycopy(var3, 0, var4, 32, var3.length);
			System.arraycopy(Packet.shortToByteArray_Little((short) 0xFFEE) , 0, var4, 64,2);
			Log.e(""," parseContentwithCountry myCountryCode:"+ myCountryCode);
			var4[66] = (byte)StringUtils.getCurrentLocaltionISOCountryCodeNumber( myCountryCode);
			String nickName = new String( Base64.encode(deviceName.getBytes(),0));
			var4[67] =  (byte) nickName.getBytes().length;
			nickName =  nickName.replace("+", "-");
			nickName =  nickName.replace("/", "_");
			nickName =  nickName.replace("=", ",");
			System.arraycopy(nickName.getBytes(), 0, var4, 68, var4[67] );
			return var4;
		}
		public static byte[] parseContentNoFFEE(String var0, String var1,String deviceName) {
			byte[] var2 = var0.getBytes();
			byte[] var3 = var1.getBytes();
			byte[] var4 = new byte[64+128+4];
			System.arraycopy(var2, 0, var4, 0, var2.length);
			System.arraycopy(var3, 0, var4, 32, var3.length);
			System.arraycopy(Packet.shortToByteArray_Little((short) 0) , 0, var4, 64,2);
			var4[66] = (byte)StringUtils.getCurrentLocaltionISOCountryCodeNumber(UbiaApplication.myCountryCode);
			String nickName = new String( Base64.encode(deviceName.getBytes(),0));
			var4[67] =  (byte) nickName.getBytes().length;
			nickName =  nickName.replace("+", "-");
			nickName =  nickName.replace("/", "_");
			nickName =  nickName.replace("=", ",");

			System.arraycopy(nickName.getBytes(), 0, var4, 68, var4[67] );
			return var4;
		}
		public static byte[] parseContentDeviceName( String deviceName) {
			 
			byte[] var4 = new byte[ 128+4];
		 
			String nickName = new String( Base64.encode(deviceName.getBytes(),0));
			nickName =  nickName.replace("+", "-"); 
			nickName =  nickName.replace("/", "_");
			nickName =  nickName.replace("=", ",");
			int length=  (byte) nickName.getBytes().length;
			System.arraycopy(Packet.intToByteArray_Little(length), 0, var4, 0, 4);
			System.arraycopy(nickName.getBytes(), 0, var4, 4, length );
			return var4;
		}
	}

	public class SMsgAVIoctrlSetRcdDurationReq {

		int channel;
		int durasecond;
		int presecond;

	}

	public class SMsgAVIoctrlSetPasswdResp {

		byte[] reserved = new byte[3];
		byte result;

	}

	public static class SMsgAVIoctrlAVStream {

		int channel = 0;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int var0) {
			byte[] var1 = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var1, 0, 4);
			return var1;
		}
	}

	public static class SFrameInfo {

		byte cam_index;
		short codec_id;
		byte flags;
		byte onlineNum;
		byte[] reserved = new byte[3];
		int reserved2;
		int timestamp;

		public static byte[] parseContent(short var0, byte var1, byte var2,
				byte var3, int var4) {
			byte[] var5 = new byte[16];
			System.arraycopy(Packet.shortToByteArray_Little(var0), 0, var5, 0,
					2);
			var5[2] = var1;
			var5[3] = var2;
			var5[4] = var3;
			System.arraycopy(Packet.intToByteArray_Little(var4), 0, var5, 12, 4);
			return var5;
		}
	}

	public static class SMsgAVIoctrlGetEnvironmentReq {

		int channel;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int var0) {
			byte[] var1 = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var1, 0, 4);
			return var1;
		}
	}

	/**
	 * // typedef struct // { // unsigned char control; // PTZ control command,
	 * refer to ENUM_PTZCMD // unsigned char speed; // PTZ control speed //
	 * unsigned char point; // no use in APP so far // unsigned char limit; //
	 * no use in APP so far // unsigned char aux; // no use in APP so far //
	 * unsigned char channel; // camera index // unsigned char reserve[2]; // }
	 * SMsgAVIoctrlPtzCmd;
	 * */
	public static class SMsgAVIoctrlPtzCmd {

		byte control;
		byte speed;
		byte point;
		byte limit;
		byte aux;
		byte channel;
		byte[] reserved = new byte[2];

		/**
		 * byte control;<br>
		 * byte speed;<br>
		 * byte point;<br>
		 * byte limit;<br>
		 * byte aux;<br>
		 * byte channel; <br>
		 * byte[] reserved = new byte[2];<br>
		 */
		public static byte[] parseContent(byte var0, byte var1, byte var2,
				byte var3, byte var4, byte var5) {
			byte[] var6 = new byte[] { var0, var1, var2, var3, var4, var5,
					(byte) 0, (byte) 0 };
			return var6;
		}
	}

	public static class SWifiAp {

		public byte enctype;
		public byte mode;
		public byte signal;
		public byte[] ssid = new byte[32];
		public byte status;

		public SWifiAp(byte[] var1) {
			System.arraycopy(var1, 1, this.ssid, 0, var1.length);
			this.mode = var1[32];
			this.enctype = var1[33];
			this.signal = var1[34];
			this.status = var1[35];
		}

		public SWifiAp(byte[] var1, byte var2, byte var3, byte var4, byte var5) {
			System.arraycopy(var1, 0, this.ssid, 0, var1.length);
			this.mode = var2;
			this.enctype = var3;
			this.signal = var4;
			this.status = var5;
		}

		public static int getTotalSize() {
			return 36;
		}
	}

	/**
	 * typedef struct _SENSOR_ALARM_TUTK_SEND_INFO_STRUCT{<br>
	 * int Cmd;<br>
	 * INT32U DataSize;<br>
	 * INT32U CurIndex;<br>
	 * INT32U AllCounts;<br>
	 * INT32U SaveIndex;<br>
	 * }SENSOR_ALARM_TUTK_SEND_INFO_STRUCT,
	 * *pSENSOR_ALARM_TUTK_SEND_INFO_STRUCT;<br>
	 */
	public static class SMsgAVIoctrlDataHead {
		int Cmd;
		int DataSize;
		int CurIndex;
		int AllCounts;
		int SaveIndex;
		int EngFlag;

		public SMsgAVIoctrlDataHead(byte[] var1) {
			// if(var1.length==20)
			{
				Cmd = Packet.byteArrayToInt_Little(var1, 0);

				DataSize = Packet.byteArrayToInt_Little(var1, 4);
				CurIndex = Packet.byteArrayToInt_Little(var1, 8);
				AllCounts = Packet.byteArrayToInt_Little(var1, 12);
				SaveIndex = Packet.byteArrayToInt_Little(var1, 16);
				EngFlag = Packet.byteArrayToInt_Little(var1, 20);
			}

		}

		public static byte[] parseSMsgAVIoctrlSensorDataHead(int Cmd,
				int DataSize, int CurIndex, int AllCounts, int SaveIndex,
				int EngFlag) {
			byte[] DataHead = new byte[24];
			System.arraycopy(Packet.intToByteArray_Little(Cmd), 0, DataHead, 0,
					4);
			System.arraycopy(Packet.intToByteArray_Little(DataSize), 0,
					DataHead, 4, 4);
			System.arraycopy(Packet.intToByteArray_Little(CurIndex), 0,
					DataHead, 8, 4);
			System.arraycopy(Packet.intToByteArray_Little(AllCounts), 0,
					DataHead, 12, 4);
			System.arraycopy(Packet.intToByteArray_Little(SaveIndex), 0,
					DataHead, 16, 4);
			System.arraycopy(Packet.intToByteArray_Little(EngFlag), 0,
					DataHead, 16, 4);

			return DataHead;

		}

		public static byte[] parseSMsgAVIoctrlSensorDataHead_Data(int Cmd,
				int DataSize, int CurIndex, int AllCounts, int SaveIndex,
				int EngFlag, byte data[]) {
			byte[] DataHead = new byte[24 + DataSize];
			System.arraycopy(Packet.intToByteArray_Little(Cmd), 0, DataHead, 0,
					4);
			System.arraycopy(Packet.intToByteArray_Little(DataSize), 0,
					DataHead, 4, 4);
			System.arraycopy(Packet.intToByteArray_Little(CurIndex), 0,
					DataHead, 8, 4);
			System.arraycopy(Packet.intToByteArray_Little(AllCounts), 0,
					DataHead, 12, 4);
			System.arraycopy(Packet.intToByteArray_Little(SaveIndex), 0,
					DataHead, 16, 4);
			System.arraycopy(Packet.intToByteArray_Little(EngFlag), 0,
					DataHead, 20, 4);
			System.arraycopy(data, 0, DataHead, 24, data.length);
			return DataHead;

		}

		public int getCmd() {
			return Cmd;
		}

		public void setCmd(int cmd) {
			Cmd = cmd;
		}

		public int getDataSize() {
			return DataSize;
		}

		public void setDataSize(int dataSize) {
			DataSize = dataSize;
		}

		public int getCurIndex() {
			return CurIndex;
		}

		public void setCurIndex(int curIndex) {
			CurIndex = curIndex;
		}

		public int getAllCounts() {
			return AllCounts;
		}

		public void setAllCounts(int allCounts) {
			AllCounts = allCounts;
		}

		public int getSaveIndex() {
			return SaveIndex;
		}

		public void setSaveIndex(int saveIndex) {
			SaveIndex = saveIndex;
		}

		public int getEngFlag() {
			return EngFlag;
		}

		public void setEngFlag(int engFlag) {
			EngFlag = engFlag;
		}

	}

	/**
	 * 
	 byte[] SensorInfo = new byte[4]; <br>
	 * byte Valid;<br>
	 * byte LinkPreset; <br>
	 * byte [] Reserver= new byte[2];<br>
	 * byte Name[ ] = new byte[ALARM_SENSOR_NAME_MAX_LEN];<br>
	 * */
	public static class SMsgAVIoctrlSensorDataInfoStruct {

		final static int ALARM_SENSOR_NAME_MAX_LEN = 32;
		byte[] SensorInfo = new byte[4];
		byte Valid;
		byte LinkPreset;
		byte[] Reserver = new byte[2];
		byte Name[] = new byte[ALARM_SENSOR_NAME_MAX_LEN];
		byte gSensorData[] = new byte[ALARM_SENSOR_NAME_MAX_LEN + 8];
		int SaveIndex;

		/**
		 * 
		 byte[] SensorInfo = new byte[4]; <br>
		 * byte Valid;<br>
		 * byte LinkPreset; <br>
		 * byte [] Reserver= new byte[2];<br>
		 * byte Name[ ] = new byte[ALARM_SENSOR_NAME_MAX_LEN];<br>
		 * */

		public SMsgAVIoctrlSensorDataInfoStruct(byte[] var1, byte var2,
				byte var3, byte var4[], byte[] var5) {
			System.arraycopy(var1, 0, this.SensorInfo, 0, var1.length);
			this.Valid = var2;
			this.LinkPreset = var3;
			System.arraycopy(var4, 0, this.Reserver, 0, 2);
			System.arraycopy(var5, 0, this.Name, 0, var5.length);

		}

		public SMsgAVIoctrlSensorDataInfoStruct(byte[] var1) {
			System.arraycopy(var1, 0, this.SensorInfo, 0, 4);
			this.Valid = var1[4];
			this.LinkPreset = var1[5];
			System.arraycopy(var1, 6, this.Reserver, 0, 2);
			System.arraycopy(var1, 8, this.Name, 0, ALARM_SENSOR_NAME_MAX_LEN);
			System.arraycopy(var1, 0, this.gSensorData, 0,
					ALARM_SENSOR_NAME_MAX_LEN + 8);

		}

		public byte[] getSMsgAVIoctrlSensorDataInfoStruct() {
			return gSensorData;
		}

		/**
		 * 
		 byte[] SensorInfo = new byte[4]; <br>
		 * byte Valid; sit4<br>
		 * byte LinkPreset; sit5<br>
		 * byte [] Reserver= new byte[2]; sit6<br>
		 * byte Name[ ] = new byte[ALARM_SENSOR_NAME_MAX_LEN]; sit8<br>
		 * */
		public static byte[] parseContentSensorData(byte Valid,
				byte LinkPreset, byte Name[]) {
			byte[] SensorData = new byte[ALARM_SENSOR_NAME_MAX_LEN + 8];

			SensorData[4] = Valid;
			SensorData[5] = LinkPreset;
			System.arraycopy(Name, 0, SensorData, 8, Name.length);
			return SensorData;

		}

		public static int parseContentSensorDataSize() {

			return ALARM_SENSOR_NAME_MAX_LEN + 8;

		}

		public static byte[] parseContentSensorData(byte[] var1, byte var2,
				byte var3, byte var4[], byte[] var5) {
			byte[] SensorData = new byte[ALARM_SENSOR_NAME_MAX_LEN + 8];
			System.arraycopy(var1, 0, SensorData, 0, var1.length);
			SensorData[4] = var2;
			SensorData[5] = var3;
			System.arraycopy(var4, 0, SensorData, 6, 2);
			System.arraycopy(var5, 0, SensorData, 8, var5.length);
			return SensorData;

		}

		public int getSaveIndex() {
			return SaveIndex;
		}

		public void setSaveIndex(int i) {
			SaveIndex = i;
		}

		public static int getSMsgAVIoctrlSensorDataInfoStructSize() {
			return ALARM_SENSOR_NAME_MAX_LEN + 8;
		}

		public byte getLinkPreset() {
			return LinkPreset;
		}

		public void setLinkPreset(byte linkPreset) {
			LinkPreset = linkPreset;
			this.gSensorData[5] = linkPreset;
			// System.arraycopy(linkPreset, 0, this.gSensorData, 5, 1);
		}

		public byte[] getSensorInfo() {
			return SensorInfo;
		}

		public void setSensorInfo(byte[] sensorInfo) {
			SensorInfo = sensorInfo;
		}

		public byte getValid() {
			return Valid;
		}

		public void setValid(byte valid) {
			Valid = valid;
			this.gSensorData[4] = valid;
			// System.arraycopy(valid, 0, this.gSensorData, 4, 1);
		}

		public byte[] getReserver() {
			return Reserver;
		}

		public void setReserver(byte[] reserver) {
			Reserver = reserver;
			System.arraycopy(reserver, 0, this.gSensorData, 6, 2);
		}

		public byte[] getName() {
			return Name;
		}

		public void setName(byte[] name) {
			Name = name;
			byte cpName[] = new byte[ALARM_SENSOR_NAME_MAX_LEN];
			if (name.length < 32)
				System.arraycopy(name, 0, cpName, 0, name.length);
			else
				System.arraycopy(name, 0, cpName, 0, ALARM_SENSOR_NAME_MAX_LEN);
			System.arraycopy(cpName, 0, this.gSensorData, 8,
					ALARM_SENSOR_NAME_MAX_LEN);

		}

		public int getALARM_SENSOR_NAME_MAX_LEN() {
			return ALARM_SENSOR_NAME_MAX_LEN;
		}
	}

	public static class SMsgAVIoctrlAlarmZoneInfoStruct implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		final static int ALARM_SENSOR_NAME_MAX_LEN = 32;
		final static int ALARM_SENSOR_MAX_NUM = 64;
		byte LinkDeviceId[] = new byte[ALARM_SENSOR_MAX_NUM]; // =0-
																// ALARM_SENSOR_MAX_NUM
		byte ZoneAlarmEnabe;// 1=triger alarm
		byte reserver[] = new byte[3];
		byte Name[] = new byte[ALARM_SENSOR_NAME_MAX_LEN];
		byte AlarmData[] = new byte[ALARM_SENSOR_MAX_NUM
				+ ALARM_SENSOR_NAME_MAX_LEN + 4];
		int SaveIndex;
		final static int thissize = ALARM_SENSOR_MAX_NUM
				+ ALARM_SENSOR_NAME_MAX_LEN + 4;

		/**
		 * 
		 int LinkDeviceId[]= new int[ALARM_SENSOR_MAX_NUM]; //=0-
		 * ALARM_SENSOR_MAX_NUM<br>
		 * byte ZoneAlarmEnabe;//1=triger alarm<br>
		 * byte reserver[ ]= new byte[3];<br>
		 * byte Name [ ]= new byte[ALARM_SENSOR_NAME_MAX_LEN];<br>
		 * */
		public static int AlarmZoneInfoStructSize() {
			return thissize;
		}

		public SMsgAVIoctrlAlarmZoneInfoStruct(byte[] var1) {
			System.arraycopy(var1, 0, this.LinkDeviceId, 0,
					ALARM_SENSOR_MAX_NUM);
			this.ZoneAlarmEnabe = var1[ALARM_SENSOR_MAX_NUM];
			System.arraycopy(var1, ALARM_SENSOR_MAX_NUM + 1, this.reserver, 0,
					3);
			System.arraycopy(var1, ALARM_SENSOR_MAX_NUM + 4, this.Name, 0,
					ALARM_SENSOR_NAME_MAX_LEN);
			System.arraycopy(var1, 0, AlarmData, 0, thissize);

		}

		public static byte[] parseContentAlarmZoneInfoStruct(
				byte ZoneAlarmEnabe, byte[] Name) {
			byte[] ZoneAlarmEnabeData = new byte[thissize];
			ZoneAlarmEnabeData[ALARM_SENSOR_MAX_NUM] = ZoneAlarmEnabe;
			// System.arraycopy(Name, 0, ZoneAlarmEnabeData,
			// ALARM_SENSOR_MAX_NUM+4, Name.length);
			if (Name.length < ALARM_SENSOR_NAME_MAX_LEN)
				System.arraycopy(Name, 0, ZoneAlarmEnabeData,
						ALARM_SENSOR_MAX_NUM + 4, Name.length);
			else
				System.arraycopy(Name, 0, ZoneAlarmEnabeData,
						ALARM_SENSOR_MAX_NUM + 4, ALARM_SENSOR_NAME_MAX_LEN);
			return ZoneAlarmEnabeData;
		}

		public int getSaveIndex() {
			return SaveIndex;
		}

		public void setSaveIndex(int saveIndex) {
			SaveIndex = saveIndex;
		}

		public byte[] getLinkDeviceId() {
			return LinkDeviceId;
		}

		public void setLinkDeviceId(byte linkDeviceId) {
			for (int i = 0; i < ALARM_SENSOR_MAX_NUM; i++) {
				if (LinkDeviceId[i] == 0) {
					LinkDeviceId[i] = linkDeviceId;
					break;
				}
			}
			System.arraycopy(LinkDeviceId, 0, this.AlarmData, 0,
					ALARM_SENSOR_MAX_NUM);
		}

		public void ClearLinkDeviceId() {
			byte newLinkDeviceId[] = new byte[ALARM_SENSOR_MAX_NUM];
			System.arraycopy(newLinkDeviceId, 0, this.LinkDeviceId, 0,
					ALARM_SENSOR_MAX_NUM);
			System.arraycopy(newLinkDeviceId, 0, this.AlarmData, 0,
					ALARM_SENSOR_MAX_NUM);
		}

		public void delLinkDeviceId(int linkDeviceId) {

			LinkDeviceId[linkDeviceId] = 0;

			System.arraycopy(LinkDeviceId, 0, this.AlarmData, 0,
					ALARM_SENSOR_MAX_NUM);
		}

		public byte getZoneAlarmEnabe() {
			return ZoneAlarmEnabe;
		}

		public void setZoneAlarmEnabe(byte zoneAlarmEnabe) {
			ZoneAlarmEnabe = zoneAlarmEnabe;
			this.AlarmData[ALARM_SENSOR_MAX_NUM] = zoneAlarmEnabe;

		}

		public byte[] getName() {
			return Name;
		}

		public void setName(byte[] name) {
			Name = name;

			if (Name.length < ALARM_SENSOR_NAME_MAX_LEN)
				System.arraycopy(Name, 0, AlarmData, ALARM_SENSOR_MAX_NUM + 4,
						Name.length);
			else
				System.arraycopy(Name, 0, AlarmData, ALARM_SENSOR_MAX_NUM + 4,
						ALARM_SENSOR_NAME_MAX_LEN);
		}

		public byte[] getAlarmData() {
			return AlarmData;
		}

		public void setAlarmData(byte[] alarmData) {
			AlarmData = alarmData;
		}

		public int getAlarmSensorNameMaxLen() {
			return ALARM_SENSOR_NAME_MAX_LEN;
		}

		public int getAlarmSensorMaxNum() {
			return ALARM_SENSOR_MAX_NUM;
		}

	}

	public static class SMsg_SENSOR_ALARM_TUTK_EDIT_INFO_STRUCT {

		int Cmd;
		int DataSize;
		int EditIndex;
		int AllCounts;

		/** int mCmd,int mDataSize,int mEditIndex,int mAllCounts */
		public static byte[] parseContent(int mCmd, int mDataSize,
				int mEditIndex, int mAllCounts, byte[] data) {
			byte[] var2 = new byte[16 + mDataSize];
			System.arraycopy(Packet.intToByteArray_Little(mCmd), 0, var2, 0, 4);
			System.arraycopy(Packet.intToByteArray_Little(mDataSize), 0, var2,
					4, 4);
			System.arraycopy(Packet.intToByteArray_Little(mEditIndex), 0, var2,
					8, 4);
			System.arraycopy(Packet.intToByteArray_Little(mAllCounts), 0, var2,
					12, 4);
			if (mDataSize != 0)
				System.arraycopy(data, 0, var2, 16, data.length);
			return var2;
		}

	}

	public static class SMsgAVIoctrlSetStreamCtrlReq {

		int channel;
		byte quality;
		byte[] reserved = new byte[3];

		public static byte[] parseContent(int var0, byte var1) {
			byte[] var2 = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var2, 0, 4);
			var2[4] = var1;
			return var2;
		}
	}

	public static class SMsgIoctrlGSetPIRCtrlReq {

		int channel;
		byte pirstatus;
		byte[] rsv = new byte[3];
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int var0, byte var1) {
			byte[] var2 = new byte[12];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var2, 0, 4);
			var2[4] = var1;
			return var2;
		}
	}

	public static class SMsgIoctrlGSetIRModeCtrlReq {

		int channel;
		byte pirstatus;
		byte[] rsv = new byte[3];
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int var0, byte var1) {
			byte[] var2 = new byte[12];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var2, 0, 4);
			var2[4] = var1;
			return var2;
		}
	}

	public static class SMsgAVIoctrlPlayRecord {

		int Param;
		int channel;
		int command;
		byte[] reserved = new byte[4];
		byte[] stTimeDay = new byte[8];

		public static byte[] parseContent(int var0, int var1, int var2,
				long var3) {
			byte[] var5 = new byte[24];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var5, 0, 4);
			System.arraycopy(Packet.intToByteArray_Little(var1), 0, var5, 4, 4);
			System.arraycopy(Packet.intToByteArray_Little(var2), 0, var5, 8, 4);
			Calendar var6 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
			var6.setTimeInMillis(var3);
			var6.add(5, -1);
			var6.add(5, 1);
			System.arraycopy(STimeDay.parseContent(var6.get(1),
					var6.get(2), var6.get(5), var6.get(7), var6.get(11),
					var6.get(12), var6.get(13)), 0, var5, 12, 8);
			return var5;
		}

		public static byte[] parseContent(int var0, int var1, int var2,
				byte[] var3, int var5) {
			byte[] var4 = new byte[24];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var4, 0, 4);
			System.arraycopy(Packet.intToByteArray_Little(var1), 0, var4, 4, 4);
			System.arraycopy(Packet.intToByteArray_Little(var2), 0, var4, 8, 4);
			System.arraycopy(var3, 0, var4, 12, 8);
			System.arraycopy(Packet.intToByteArray_Little(var5), 0, var4, 20, 4);
			return var4;
		}

		public static byte[] parseContent(int var0, int var1, int var2,
				byte[] var3) {
			byte[] var4 = new byte[24];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var4, 0, 4);
			System.arraycopy(Packet.intToByteArray_Little(var1), 0, var4, 4, 4);
			System.arraycopy(Packet.intToByteArray_Little(var2), 0, var4, 8, 4);
			System.arraycopy(var3, 0, var4, 12, 8);
			return var4;
		}
	}

	public class SMsgAVIoctrlSetRecordResp {

		byte[] reserved = new byte[3];
		byte result;

	}

	public static class SMsgAVIoctrlGetDropbox {

		public short nLinked;
		public short nSupportDropbox;
		public String szLinkUDID;

		public SMsgAVIoctrlGetDropbox(byte[] var1) {
			this.nSupportDropbox = Packet.byteArrayToShort_Little(var1, 0);
			this.nLinked = Packet.byteArrayToShort_Little(var1, 2);
			this.szLinkUDID = (new String(var1, 4, -4 + var1.length)).replace(
					" ", "");
		}
	}

	public static class SMsgAVIoctrlGetOsdReq {

		int channel;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int var0) {
			byte[] var1 = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(var0), 0, var1, 0, 4);
			return var1;
		}
	}

	public static class OSDItem {
		public static final int MAX_OSD_CONTENT_SIZE = 128;
		char index; // index of osd item, 0: default for date and time;
					// 1:location; 2-255:custom define
		char flag; // 0: hide, 1: show
		char bytes; // valid bytes in content buf
		char fontsize;

		short xpoint;
		short ypoint;
		int frontargb;
		int backgroundargb;
		char content[] = new char[MAX_OSD_CONTENT_SIZE];

		// byte mdata []=new byte [128+4*4+8];

		public static byte[] setData(int channel, char osddata) {
			byte data[] = new byte[128 + 4 * 4 + 8];
			System.arraycopy(Packet.intToByteArray_Little(channel), 0, data, 0,
					4);
			System.arraycopy(Packet.intToByteArray_Little(1), 0, data, 4, 4);
			data[9] = (byte) osddata;
			return data;
		}

	};

	public static class SMsgEventIoctrlDownloadReq {
		public byte command; // see ENUM_FILE_TRANS_CMD
		public byte segments;
		public short pktSize;
		public byte type;
		public byte resv[] = new byte[3];
		public int time;
		public FileSegmentInfo fileSegment = new FileSegmentInfo(); // one or
																	// more
																	// FileSegmentInfo
																	// append
																	// with
		public byte data[] = new byte[12 + 12];

		public byte[] getData() {
			data[0] = command;
			data[1] = segments;
			System.arraycopy(Packet.shortToByteArray_Little(pktSize), 0, data,
					2, 2);
			data[4] = type;
			System.arraycopy(Packet.intToByteArray_Little(time), 0, data, 8, 4);
			System.arraycopy(fileSegment.getFileSegment(), 0, data, 12, 12);
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}

	};

	public static class SMsgAVIoctrlFirmwareUpdateReq {
		int version;
		byte file_type; // 1:home, 2: rootfs, 3:kernel
		byte file_url_len;
		byte file_md5_len; // =32
		byte resv;
		byte md5sum[] = new byte[32];
		byte file_url[] = new byte[128];
		byte data[] = new byte[172];
		int file_size;

		public byte[] getData() {

			System.arraycopy(Packet.intToByteArray_Little(version), 0, data, 0,
					4);
			data[4] = file_type;
			data[5] = file_url_len;
			data[6] = file_url_len;
			data[7] = resv;
			System.arraycopy(Packet.intToByteArray_Little(file_size), 0, data,
					8, 4);
			System.arraycopy(md5sum, 0, data, 12, file_md5_len);
			System.arraycopy(file_url, 0, data, 44, file_url_len);
			return data;
		}

		public void getData(byte[] data) {
			this.data = data;
		}

		public int getVersion() {
			return version;
		}

		public void setVersion(int version) {
			this.version = version;
		}

		public byte getFile_type() {
			return file_type;
		}

		public void setFile_type(byte file_type) {
			this.file_type = file_type;
		}

		public int getFile_size() {
			return this.file_size;
		}

		public void setFile_size(int file_size) {
			this.file_size = file_size;
		}

		public byte getFile_url_len() {
			return file_url_len;
		}

		public void setFile_url_len(byte file_url_len) {
			this.file_url_len = file_url_len;
		}

		public byte getFile_md5_len() {
			return file_md5_len;
		}

		public void setFile_md5_len(byte file_md5_len) {
			this.file_md5_len = file_md5_len;
		}

		public byte getResv() {
			return resv;
		}

		public void setResv(byte resv) {
			this.resv = resv;
		}

		public byte[] getMd5sum() {
			return md5sum;
		}

		public void setMd5sum(byte[] md5sum) {
			this.md5sum = md5sum;
		}

		public byte[] getFile_url() {
			return file_url;
		}

		public void setFile_url(byte[] file_url) {
			this.file_url = file_url;
		}
	};

	public static class SMsgAVIoctrlFirmwareUpdateRsp {
		int result; // 0:ok, start to update: 10 fileversion same,not to update
					// 100:fileversion is older than current one,not to update
		int progress; // 0-100
	};

	public static class SMsgAVIoctrlFirmwareUpdateCheckReq {
		int version;
		byte file_type; // 1:home, 2: rootfs, 3:kernel
		byte resv[] = new byte[3];
	};

	public static class SMsgAVIoctrlFirmwareUpdateCheckRsp {
		int result; // 0:ok, start to update: 10 fileversion same,not to update
					// 100:fileversion is older than current one,not to update
		int progress; // 0-100 progress
	};

	public static class SMsgIoctrlSetUIDReq {

		public static byte[] parseContent(int uidsize, byte[] uid) {
			byte[] var2 = new byte[28];
			System.arraycopy(Packet.intToByteArray_Little(uidsize), 0, var2, 0,
					4);
			if (uidsize < 24) {
				System.arraycopy(uid, 0, var2, 4, uidsize);
			} else {
				System.arraycopy(uid, 0, var2, 4, 24);
			}
			return var2;
		}
	}

	// typedef struct
	// {
	// unsigned int channel; // Camera Index
	// unsigned char IOProtoVer;
	// unsigned char event;
	// unsigned char unit; //1-60:1-60sec, 61-120:1-60min,
	// 121-144:1-24hrs,145-176:1-31days
	// unsigned char index;
	// unsigned int starttime;
	// unsigned int endtime;
	// unsigned short validbits; // up to 1004*8 bits
	// unsigned short bytes; // up to 1004 bytes append to this msg
	// } UBIA_IO_RecordBitmapReq, UBIA_IO_RecordBitmapRsp;

	// public static class UBIA_IO_AVStream
	// {
	// unsigned int channel; // Camera Index
	// unsigned char IOProtoVer;
	// unsigned char playrecord; /* Live stream or record stream: 0: live, 1:
	// record */
	// unsigned char streamindex; /* 0: substream, 1: mainstream, 2: */
	// unsigned char withAudio; /* 0: non audio, 1: just listen, 2: dual way
	// talk */
	// unsigned int stTimeDay; // stTimeDay; second since 1970
	// unsigned int command; // play record command. refer to ENUM_PLAYCONTROL
	// unsigned int Param; // command param, that the user defined
	// unsigned char reserved[4];
	// } ;


	public static class UBIA_IO_RecordEventListReqRsp {

		public static byte[] parseContent(int startDate, int endDate) {
			byte[] var2 = new byte[5 * 4];
			var2[6] = (byte) 60;
			System.arraycopy(Packet.intToByteArray_Little(startDate), 0, var2,
					8, 4);
			System.arraycopy(Packet.intToByteArray_Little(endDate), 0, var2,
					12, 4);
			return var2;
		}

	};

	public static class UBIA_IO_RecordBitmapReqRsp {
		int channel; // Camera Index
		char IOProtoVer;
		char event;
		char unit; // default 1 second per bit
		char index;
		int starttime;
		int endtime;
		short validbits; // up to 1004*8 bits
		short bytes; // up to 1004 bytes append to this msg

		public static byte[] parseContent(int startDate, int endDate) {
			byte[] var2 = new byte[5 * 4];
			var2[6] = (byte) 60;
			System.arraycopy(Packet.intToByteArray_Little(startDate), 0, var2,
					8, 4);
			System.arraycopy(Packet.intToByteArray_Little(endDate), 0, var2,
					12, 4);
			return var2;
		}

	};

	public static class UBIA_IO_AVStream {
		int channel; // Camera Index
		char IOProtoVer;
		char playrecord; /* Live stream or record stream: 0: live, 1: record */
		char streamindex; /* 0: substream, 1: mainstream, 2: */
		char withAudio; /* 0: non audio, 1: just listen, 2: dual way talk */
		int stTimeDay; // stTimeDay; second since 1970
		int command; // play record command. refer to ENUM_PLAYCONTROL
		int Param; // command param, that the user defined
		char reqseq; // app端维护，每次重新seek位置时自增1， 回放video
						// frameinfo里的nGMTDiff字段即为此值，以便清空buf
		char reserved[] = new char[3];

		public static byte[] playbackparseContent(int command, int playtimeutc,
				byte reqseq) {
			byte[] var2 = new byte[6 * 4];
			var2[5] = (byte) 1;
			System.arraycopy(Packet.intToByteArray_Little(command), 0, var2,
					12, 4);
			System.arraycopy(Packet.intToByteArray_Little(playtimeutc), 0,
					var2, 8, 4);
			System.arraycopy(Packet.intToByteArray_Little(1), 0, var2, 16, 4);

			var2[20] = reqseq;

			return var2;
		}

		public static byte[] startLiveView(int streamindex, byte reqseq) {
			byte[] var2 = new byte[6 * 4];
			var2[5] = (byte) 0;
			var2[6] = (byte) streamindex;
			Log.v("", "startLiveView------->" + streamindex + "    reqseq:"
					+ reqseq);
			System.arraycopy(Packet.intToByteArray_Little(0), 0, var2, 12, 4);
			System.arraycopy(Packet.intToByteArray_Little(0), 0, var2, 8, 4);
			System.arraycopy(Packet.intToByteArray_Little(0), 0, var2, 16, 4);
			var2[20] = reqseq;
			return var2;
		}
	};

	// UBIA_IO_RecordBitmapReq, UBIA_IO_RecordBitmapRsp;
	// typedef struct
	// {
	// unsigned int channel; // Camera Index
	// unsigned char IOProtoVer;
	// unsigned char event; // event type, refer to ENUM_EVENTTYPE
	// unsigned char status; // 0x00: Recording file exists, Event unreaded
	// // 0x01: Recording file exists, Event readed
	// // 0x02: No Recording file in the event
	// unsigned char maxsegment; //0:will reply all event, or maxsegment packets
	// unsigned int stStartTime;
	// unsigned int stEndTime;
	// }UBIA_IO_ListEventReq;
	//
	// typedef struct
	// {
	// unsigned int stTime; //seconds since 1970-1-1 0:0:0
	// unsigned short length; //seconds in record file
	// unsigned char event;
	// unsigned char status; // 0x00: Recording file exists, Event unreaded
	// // 0x01: Recording file exists, Event readed
	// // 0x02: No Recording file in the event
	// }UBIA_SAvEvent;
	//
	// typedef struct
	// {
	// unsigned int channel; // Camera Index
	// unsigned char IOProtoVer;
	// unsigned char index; // package index, 0,1,2...;
	// // because avSendIOCtrl() send package up to 1024 bytes one time, you may
	// want split search results to serveral package to send.
	// unsigned char endflag; // end flag; endFlag = 1 means this package is the
	// last one.
	// unsigned char count; // how much events in this package
	// unsigned short total; // Total event amount in this search session
	// unsigned char reserved[2];
	// //UBIA_SAvEvent stEvent[1]; // The first memory address of the events in
	// this package
	// }UBIA_IO_ListEventResp;
	//

	public static class UBIA_IO_EXT_GarageDoorReq {
		int channel; // Camera Index
		char IOProtoVer;
		char seq; // action seq, 如果前面是开，后面关的命令seq＋1，前面是关，后面是开命令seq＋1
		char operation; // 0: close door, 1: open door;
		char index; // door index, default is 0
		int resv;

		public static byte[] parseContent(int operation) {
			byte[] var2 = new byte[3 * 4];

			var2[6] = (byte) operation;

			return var2;
		}
	};

	public static class UBIA_IO_EXT_GarageDoorRsp {
		int channel; // Camera Index
		char IOProtoVer;
		char seq; // action seq
		char operation; // 0: close door, 1: open door;
		char index; // door index, default is 0
		int status; // 0:succ, -1:fail
	};

	public static class CameraPutModel {
		public static final int CameraPutModelFaceDown = 0;
		public static final int CameraPutModelFaceUp = 1; //
		public static final int CameraPutModelFaceFront = 2;//
	};

	public static class ImageAspectEnum {
		public static final int ImageAspect011 = 0;
		public static final int ImageAspect169 = 1; //
		public static final int ImageAspect043 = 2;//

		public static int getAspectEnum(Bitmap mBitmap) {
			float mWidth = mBitmap.getWidth();
			float mHeight = mBitmap.getHeight();
			float ratio = 1;
			if (mHeight != 0) {
				ratio = mWidth / mHeight;
			}

			if (mHeight != 0 && (ratio - 1) < 0.01 && (ratio - 1) > 0) {
				return ImageAspect011;
			}
			if (mHeight != 0 && (ratio - 16.0 / 9.0) < 0.01
					&& (ratio - 16.0 / 9.0) > 0) {
				return ImageAspect169;
			}
			if (mHeight != 0 && (ratio - 4.0 / 3.0) < 0.01
					&& (ratio - 4.0 / 3.0) > 0) {
				return ImageAspect043;
			}

			return 0;

		}
	};
	
	
	
	 
	public static class UBIA_IO_LensOffset {
	 
//			typedef struct
//			{
	    int xoffset;    //x
	    int yoffset;    //y
	    int zoffset;    //z
				int crop_width;
				int crop_height;

			public static byte[] parseContent(int x,int y,int z,int width,int height) {
			byte[] sendData = new byte[5 * 4];
				System.arraycopy(Packet.intToByteArray_Little(x), 0, sendData, 0, 4);
				System.arraycopy(Packet.intToByteArray_Little(y), 0, sendData, 4, 4);
				System.arraycopy(Packet.intToByteArray_Little(z), 0, sendData, 8, 4);
				System.arraycopy(Packet.intToByteArray_Little(width), 0, sendData, 12, 4);
				System.arraycopy(Packet.intToByteArray_Little(height), 0, sendData, 16, 4);
			return sendData;
		}
	};
	public static class SMsguser {
		public static byte[] getData(String name, String pwd) {
			byte[] cUser = new byte[64];
			byte[] var8 = name.getBytes();
			byte[] var9 = pwd.getBytes();
			if (var8.length < 32)
				System.arraycopy(var8, 0, cUser, 0, var8.length);
			else
				System.arraycopy(var8, 0, cUser, 0, 32);
			if (var9.length < 32)
				System.arraycopy(var9, 0, cUser, 32, var9.length);
			else
				System.arraycopy(var9, 0, cUser, 32, 32);
			return cUser;
		}
	}
	
	
	public static String secToTime(int time) {
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(minute) + ":"
				+ unitFormat(second);
			}
		}
		return timeStr;
	}
	public static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = "" + i;
		return retStr;
	}

	
	
	public static class UBIA_IO_EXT_ZigbeeInfoReq {
		int channel; // Camera Index
		char IOProtoVer;
		char seq;
		char operation; // =0
		char index; // =0
		int resv;

		public static byte[] parseContent(int operation) {
			byte[] var2 = new byte[3 * 4];
			var2[6] = (byte) operation;
			return var2;
		}
		
//		typedef struct
//		{
//		    unsigned int channel; // Camera Index
//		    unsigned char IOProtoVer;
//		    unsigned char seq;    //
//		    unsigned char operation; //=0 
//		    unsigned char index;     //=0
//		    int infoSize;      //-1: fail, >0: zigbee info size
//		    char macaddr[8];
//		    char info[128];
//		} UBIA_IO_EXT_ZigbeeInfoRsp;
	};
	public static class UBIA_IO_ACPowerFreq_SetReq {
		int channel; // Camera Index
		char IOProtoVer;
		char seq;
		char operation; // =0
		char index; // =0
		int resv;

		public static byte[] parseContent(int operation) {
			byte[] var2 = new byte[3 * 4];
			var2[6] = (byte) operation;
			return var2;
		}
 
	};
	public static class UBIA_IO_LED_SetReq {


		public static byte[] parseContent(int operation) {
			byte[] var2 = new byte[3 * 4];
			var2[6] = (byte) operation;
			return var2;
		}

	};

	public static class UBIA_IO_ACTIVETIME_SetReq {


		public static byte[] parseContent(int operation) {
			byte[] var2 = new byte[3 * 4];
			var2[6] = (byte) operation;
			return var2;
		}

	};

	public static class UBIA_IO_PIR_SetReq {
		int channel; // Camera Index
		char IOProtoVer;
		char seq;
		char operation; // =0
		char index; // =0
		int resv;

		public static byte[] parseContent(int operation) {
			byte[] var2 = new byte[3 * 4];
			var2[6] = (byte) operation;
			return var2;
		}
 
	};
	public static class UBIA_IO_SceneMode_SetReq {
		int channel; // Camera Index
		char IOProtoVer;
		char seq;
		char operation; // =0
		char index; // =0
		int resv;

		public static byte[] parseContent(int operation) {
			byte[] var2 = new byte[3 * 4];
			var2[6] = (byte) operation;
			return var2;
		}
 
	};
	public static class UBIA_IO_ELOCK_SetReq {
		int channel; // Camera Index
		char IOProtoVer;
		char seq;
		char operation; // =0
		char index; // =0
		int resv;

		public static byte[] parseContent(int operation) {
			byte[] var2 = new byte[3 * 4];
			var2[6] = (byte) operation;
			return var2;
		}
	};
	public static class UBIA_IO_Cloud_SetReq
	{
		  int channel; 	// Camera Index 0:default
		  char IOProtoVer;
		  char seq;    	//action seq
		  char provider; //0:close, 1:aliyun 2:amazon
		  char nEnableDST;// device is support TimeZone or not, 1: Supported, 0: Unsupported.
		char nGMTDiff;				// the difference between GMT in hours
		char timezonestrlen;
		char rsv[ ] = new char[18];
		char szTimeZoneString[]= new char[256];  	// the timezone description string in multi-bytes char format
		public static byte[] parseContent(int provider,int nEnableDST,int nGMTDiff) {
			byte[] var2 = new byte[8+20+256];
			var2[6] = (byte) provider;
			var2[7] = (byte) nEnableDST;
			var2[8] = (byte) nGMTDiff;
			return var2;
		}
	} ;
	public static class UBIA_IO_CAPTURE_PICTURE_SetReq
	{
		public static byte[] parseContent(int pictureCount,int timeInterval) {
			byte[] var2 = new byte[24];
			var2[6] = (byte) pictureCount;
			var2[7] = (byte) timeInterval;

			return var2;
		}
	}

};
