package com.my.IOTC;

import android.util.Log;

import com.tutk.IOTC.st_LanSearchInfo;
import com.tutk.IOTC.st_LanSearchInfo2;
import com.ubia.IOTC.St_SInfo;
import com.ubia.IOTC.St_VPGInfo;

public class UBICAPIs {
	public int p4plibver = 0;
	public static final int API_ER_ANDROID_NULL = -10000;
	public static final int IOTC_ER_AES_CERTIFY_FAIL = -29;
	public static final int IOTC_ER_ALREADY_INITIALIZED = -3;
	public static final int IOTC_ER_CAN_NOT_FIND_DEVICE = -19;
	public static final int IOTC_ER_CH_NOT_ON = -26;
	public static final int IOTC_ER_CLIENT_NOT_SECURE_MODE = -34;
	public static final int IOTC_ER_CLIENT_SECURE_MODE = -35;
	public static final int IOTC_ER_CONNECT_IS_CALLING = -20;
	public static final int IOTC_ER_DEVICE_MULTI_LOGIN = -45;
	public static final int IOTC_ER_DEVICE_NOT_LISTENING = -24;
	public static final int IOTC_ER_DEVICE_NOT_SECURE_MODE = -36;
	public static final int IOTC_ER_DEVICE_SECURE_MODE = -37;
	public static final int IOTC_ER_EXCEED_MAX_SESSION = -18;
	public static final int IOTC_ER_EXIT_LISTEN = -39;
	public static final int IOTC_ER_FAIL_CONNECT_SEARCH = -27;
	public static final int IOTC_ER_FAIL_CREATE_MUTEX = -4;
	public static final int IOTC_ER_FAIL_CREATE_SOCKET = -6;
	public static final int IOTC_ER_FAIL_CREATE_THREAD = -5;
	public static final int IOTC_ER_FAIL_GET_LOCAL_IP = -16;
	public static final int IOTC_ER_FAIL_RESOLVE_HOSTNAME = -2;
	public static final int IOTC_ER_FAIL_SETUP_RELAY = -42;
	public static final int IOTC_ER_FAIL_SOCKET_BIND = -8;
	public static final int IOTC_ER_FAIL_SOCKET_OPT = -7;
	public static final int IOTC_ER_INVALID_ARG = -46;
	public static final int IOTC_ER_INVALID_MODE = -38;
	public static final int IOTC_ER_INVALID_SID = -14;
	public static final int IOTC_ER_LISTEN_ALREADY_CALLED = -17;
	public static final int IOTC_ER_LOGIN_ALREADY_CALLED = -11;
	public static final int IOTC_ER_MASTER_TOO_FEW = -28;
	public static final int IOTC_ER_NETWORK_UNREACHABLE = -41;
	public static final int IOTC_ER_NOT_INITIALIZED = -12;
	public static final int IOTC_ER_NOT_SUPPORT_RELAY = -43;
	public static final int IOTC_ER_NO_PERMISSION = -40;
	public static final int IOTC_ER_NO_SERVER_LIST = -44;
	public static final int IOTC_ER_NoERROR = 0;
	public static final int IOTC_ER_REMOTE_TIMEOUT_DISCONNECT = -23;
	public static final int IOTC_ER_SERVER_NOT_RESPONSE = -1;
	public static final int IOTC_ER_SESSION_CLOSE_BY_REMOTE = -22;
	public static final int IOTC_ER_SESSION_NO_FREE_CHANNEL = -31;
	public static final int IOTC_ER_TCP_CONNECT_TO_SERVER_FAILED = -33;
	public static final int IOTC_ER_TCP_TRAVEL_FAILED = -32;
	public static final int IOTC_ER_TIMEOUT = -13;
	public static final int IOTC_ER_UNKNOWN_DEVICE = -15;
	public static final int IOTC_ER_UNLICENSE = -10;

	public static final int P2PLIB_VER_NULL = 0;
	public static final int P2PLIB_VER_UBIA_V1 = 1;
	public static final int P2PLIB_VER_UBIA_V2 = 2;
	public static final int P2PLIB_VER_UBIA_V3 = 3;
	public static final int P2PLIB_VER_UBIA_V4 = 4;
    	public static final int P2PLIB_VER_TUTK = 255;
	public int getP4plibver() {
		return p4plibver;
	}

	public void setP4plibver(int p4plibver) {
		this.p4plibver = p4plibver;
	}

	public int IOTC_Connect_ByUID_Parallel(String var0, int var1) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Connect_ByUID_Parallel(var0,var1);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
		return com.ubia.IOTC.IOTCAPIs.UBIC_Connect_ByUID_Parallel(var0, var1);
		//} else {
		//	return -1;
		//}
	}
	public int IOTC_Connect_ByUID_Parallel2(String uid, int sid, int isBell) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Connect_ByUID_Parallel(var0,var1);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
		return com.ubia.IOTC.IOTCAPIs.UBIC_Connect_ByUID_Parallel2(uid, sid,isBell);
		//} else {
		//	return -1;
		//}
	}
	public void IOTC_Connect_Stop() {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
	
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			com.ubia.IOTC.IOTCAPIs.UBIC_Connect_Stop();
		//} else {

		//}
	}

	public int IOTC_Connect_Stop_BySID(int var0) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Connect_Stop_BySID(var0);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			return com.ubia.IOTC.IOTCAPIs.UBIC_Connect_Stop_BySID(var0);
		//} else {
		//	return -1;
		//}
	}

	public static int IOTC_DeInitialize() {
		// if (p4plibver == 1) {
		// return com.tutk.IOTC.IOTCAPIs.IOTC_DeInitialize();
		// } else if (p4plibver == 2) {
		// return com.ubia.IOTC.IOTCAPIs.UBIC_DeInitialize();
		// } else {
		// return -1;
		// }

		return com.ubia.IOTC.IOTCAPIs.UBIC_DeInitialize();
	}

	public int IOTC_Device_Login(String var0, String var1, String var2) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Device_Login(var0, var1, var2);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			return com.ubia.IOTC.IOTCAPIs.UBIC_Device_Login(var0, var1, var2);
		//} else {
		//	return -1;
		//}
	}

	public int IOTC_Get_Login_Info(long[] var0) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Get_Login_Info(var0);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			return com.ubia.IOTC.IOTCAPIs.UBIC_Get_Login_Info(var0);
		//} else {
		//	return -1;
		//}
	}

	public int IOTC_Get_Nat_Type() {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Get_Nat_Type();
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			return com.ubia.IOTC.IOTCAPIs.UBIC_Get_Nat_Type();
		//} else {
		//	return -1;
		//}
	}

	public int IOTC_Get_SessionID() {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Get_SessionID();
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			return com.ubia.IOTC.IOTCAPIs.UBIC_Get_SessionID();
		//} else {
		//	return -1;
		//}
	}

	public void IOTC_Get_Version(long[] var0) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	com.tutk.IOTC.IOTCAPIs.IOTC_Get_Version(var0);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			com.ubia.IOTC.IOTCAPIs.UBIC_Get_Version(var0);
		//} else {
			// return -1;
		//}
	}

	public int IOTC_Initialize(int var0, String var1, String var2, String var3,
			String var4) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Initialize(var0, var1, var2,
		//			var3, var4);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
		return com.ubia.IOTC.IOTCAPIs.UBIC_Initialize(var0, var1, var2, var3,
				var4);
		//} else {
			//return -1;
		//}
	}

	public static int IOTC_Initialize2(int var0) {

		return 0;

	}

	public static int UBIC_Initialize2(int var0) {

		return com.ubia.IOTC.IOTCAPIs.UBIC_Initialize2(var0);

	}

	public int IOTC_IsDeviceSecureMode(int var0) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_IsDeviceSecureMode(var0);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			return com.ubia.IOTC.IOTCAPIs.UBIC_IsDeviceSecureMode(var0);
		//} else {
		//	return -1;
		//}
	}

	public static st_LanSearchInfo2[] IOTC_Lan_Search(int[] var0, int var1) {
		// if (p4plibver == 1) {
		// return com.tutk.IOTC.IOTCAPIs.IOTC_Lan_Search(var0, var1);
		// } else if (p4plibver == 2) {
		return com.ubia.IOTC.IOTCAPIs.UBIC_Lan_Search2(var0, var1);
		// } else {
		// return null;
		// }
	}

	public int IOTC_Listen(long var0) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Listen(var0);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			return com.ubia.IOTC.IOTCAPIs.UBIC_Listen(var0);
		//} else {
		//	return -1;
		//}
	}

	public int IOTC_Listen2(long var0, String var2, int var3) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Listen2(var0, var2, var3);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			return com.ubia.IOTC.IOTCAPIs.UBIC_Listen2(var0, var2, var3);
		//} else {
		//	return -1;
		//}
	}

	public int IOTC_Session_Channel_OFF(int var0, int var1) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Session_Channel_OFF(var0, var1);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			return com.ubia.IOTC.IOTCAPIs.UBIC_Session_Channel_OFF(var0, var1);
		//} else {
		//	return -1;
		//}
	}

	public int IOTC_Session_Channel_ON(int var0, int var1) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Session_Channel_ON(var0, var1);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			return com.ubia.IOTC.IOTCAPIs.UBIC_Session_Channel_ON(var0, var1);
		//} else {
		//	return -1;
		//}
	}

	public int IOTC_Session_Check(int var0, St_SInfo var1) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Session_Check(var0, var1);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			return com.ubia.IOTC.IOTCAPIs.UBIC_Session_Check(var0, var1);
		//} else {
		//	return -1;
		//}
	}

	public void IOTC_Session_Close(int var0) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	com.tutk.IOTC.IOTCAPIs.IOTC_Session_Close(var0);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			com.ubia.IOTC.IOTCAPIs.UBIC_Session_Close(var0);
		//} else {
			// return -1;
		//}
	}

	public int IOTC_Session_Get_Free_Channel(int var0) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Session_Get_Free_Channel(var0);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			return com.ubia.IOTC.IOTCAPIs.UBIC_Session_Get_Free_Channel(var0);
		//} else {
		//	return -1;
		//}
	}

	public int IOTC_Session_Read(int var0, byte[] var1, int var2, int var3,
			int var4) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Session_Read(var0, var1, var2,
		//			var3, var4);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
		return com.ubia.IOTC.IOTCAPIs.UBIC_Session_Read(var0, var1, var2, var3,
				var4);
		//} else {
		//	return -1;
		//}
	}

	public int IOTC_Session_Write(int var0, byte[] var1, int var2, int var3) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	return com.tutk.IOTC.IOTCAPIs.IOTC_Session_Get_Free_Channel(var0);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			return com.ubia.IOTC.IOTCAPIs.UBIC_Session_Get_Free_Channel(var0);
		//} else {
		//	return -1;
		//}
	}

	public void IOTC_Set_Max_Session_Number(long var0) {
		//if (p4plibver == P2PLIB_VER_UBIA_V1) {
		//	com.tutk.IOTC.IOTCAPIs.IOTC_Set_Max_Session_Number(var0);
		//} else if (p4plibver == P2PLIB_VER_UBIA_V2) {
			com.ubia.IOTC.IOTCAPIs.UBIC_Set_Max_Session_Number(var0);
		//} else {

		//}
	}
	public static int UBIC_GetVPGByUid(String Uid, St_VPGInfo s_vpgInfo) {
		return com.ubia.IOTC.IOTCAPIs.UBIC_GetVPGByUid(Uid,s_vpgInfo);
	}
	public static int UBIC_GetDevLibVer(String Uid) {
		return P2PLIB_VER_UBIA_V2;
		//return com.ubia.IOTC.IOTCAPIs.UBIC_GetDevLibVer(Uid);
	}
	
	public static int UBIC_Session_Check_ByCallBackFn(int SID, Object obj){
		return com.ubia.IOTC.IOTCAPIs.UBIC_Session_Check_ByCallBackFn(SID,obj);
	}
	public void loginInfoCB(long nLonInfo) {
		System.out.println("[parent] LoginInfo Callback, nLogInfo=" + nLonInfo);
	};
	public void sessionStatusCB(int nSID, int nErrCode) {
		Log.e("sessionStatusCB", "sessionStatusCB--->>>>>>>>sid:" + nSID
				+ "   nErrCode:" + nErrCode);
		System.out
				.println("[parent] sessionStatusCB  SessionStatus Callback, nSID="
						+ nSID + ", nErrCode=" + nErrCode);
	};
}
