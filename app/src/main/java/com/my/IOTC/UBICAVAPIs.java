package com.my.IOTC;

public class UBICAVAPIs {

	public int p4plibver = 0;
	public static final int API_ER_ANDROID_NULL = -10000;
	public static final int AV_ER_BUFPARA_MAXSIZE_INSUFF = -20001;
	public static final int AV_ER_CLIENT_EXIT = -20018;
	public static final int AV_ER_CLIENT_NOT_SUPPORT = -20020;
	public static final int AV_ER_CLIENT_NO_AVLOGIN = -20008;
	public static final int AV_ER_DATA_NOREADY = -20012;
	public static final int AV_ER_EXCEED_MAX_ALARM = -20005;
	public static final int AV_ER_EXCEED_MAX_CHANNEL = -20002;
	public static final int AV_ER_EXCEED_MAX_SIZE = -20006;
	public static final int AV_ER_FAIL_CREATE_THREAD = -20004;
	public static final int AV_ER_INCOMPLETE_FRAME = -20013;
	public static final int AV_ER_INVALID_ARG = -20000;
	public static final int AV_ER_INVALID_SID = -20010;
	public static final int AV_ER_LOSED_THIS_FRAME = -20014;
	public static final int AV_ER_MEM_INSUFF = -20003;
	public static final int AV_ER_NOT_INITIALIZED = -20019;
	public static final int AV_ER_NoERROR = 0;
	public static final int AV_ER_REMOTE_TIMEOUT_DISCONNECT = -20016;
	public static final int AV_ER_SENDIOCTRL_ALREADY_CALLED = -20021;
	public static final int AV_ER_SENDIOCTRL_EXIT = -20022;
	public static final int AV_ER_SERVER_EXIT = -20017;
	public static final int AV_ER_SERV_NO_RESPONSE = -20007;
	public static final int AV_ER_SESSION_CLOSE_BY_REMOTE = -20015;
	public static final int AV_ER_TIMEOUT = -20011;
	public static final int AV_ER_WRONG_VIEWACCorPWD = -20009;
	public static final int IOTYPE_INNER_SND_DATA_DELAY = 255;
	public static final int TIME_DELAY_DELTA = 1;
	public static final int TIME_DELAY_INITIAL = 0;
	public static final int TIME_DELAY_MAX = 500;
	public static final int TIME_DELAY_MIN = 4;
	public static final int TIME_SPAN_LOSED = 1000;
	public int getP4plibver() {
		return p4plibver;
	}

	public void setP4plibver(int p4plibver) {
		this.p4plibver = p4plibver;
	}

	public int avCheckAudioBuf(int var0) {
		//if (p4plibver == 1) {
		//	return com.tutk.IOTC.AVAPIs.avCheckAudioBuf(var0);
		//} else if (p4plibver == 2) {
			return com.ubia.IOTC.AVAPIs.UBIC_avCheckAudioBuf(var0);
		//} else {
		//	return -1;
		//}
	}

	public int avClientCleanAudioBuf(int var0) {
//		if (p4plibver == 1) {
//			return com.tutk.IOTC.AVAPIs.avCheckAudioBuf(var0);
//		} else if (p4plibver == 2) {
			return com.ubia.IOTC.AVAPIs.UBIC_avCheckAudioBuf(var0);
//		} else {
//			return -1;
//		}
	}

	public int avClientCleanBuf(int var0) {
//		if (p4plibver == 1) {
//			return com.tutk.IOTC.AVAPIs.avCheckAudioBuf(var0);
//		} else if (p4plibver == 2) {
		com.ubia.IOTC.AVAPIs.UBIC_avClientCleanBuf(var0);
			return com.ubia.IOTC.AVAPIs.UBIC_avCheckAudioBuf(var0);
//		} else {
//			return -1;
//		}
	}

	public int avClientCleanVideoBuf(int var0) {
//		if (p4plibver == 1) {
//			return com.tutk.IOTC.AVAPIs.avCheckAudioBuf(var0);
//		} else if (p4plibver == 2) {
		com.ubia.IOTC.AVAPIs.UBIC_avClientCleanVideoBuf(var0);
			return com.ubia.IOTC.AVAPIs.UBIC_avCheckAudioBuf(var0);
//		} else {
//			return -1;
//		}
	}

	public void avClientExit(int var0, int var1)

	{
//		if (p4plibver == 1) {
//			com.tutk.IOTC.AVAPIs.avClientExit(var0, var1);
//		} else if (p4plibver == 2) {
			com.ubia.IOTC.AVAPIs.UBIC_avClientExit(var0, var1);
//		} else {
//			// return -1;
//		}
	}

	public void avClientSetMaxBufSize(int var0) {
//		if (p4plibver == 1) {
//			com.tutk.IOTC.AVAPIs.avClientSetMaxBufSize(var0);
//		} else if (p4plibver == 2) {
			com.ubia.IOTC.AVAPIs.UBIC_avClientSetMaxBufSize(var0);
//		} else {
//			// return -1;
//		}
	}

	public int avClientStart(int var0, String var1, String var2, long var3,
			long[] var5, int var6) {
//		if (p4plibver == 1) {
//			return com.tutk.IOTC.AVAPIs.avClientStart(var0, var1, var2, var3,
//					var5, var6);
//		} else if (p4plibver == 2) {
			return com.ubia.IOTC.AVAPIs.UBIC_avClientStart(var0, var1, var2,
					var3, var5, var6);
//		} else {
//			return -1;
//		}
	}

	public int avClientStart2(int var0, String var1, String var2, long var3,
			long[] var5, int var6, int[] var7) {
//		if (p4plibver == 1) {
//			return com.tutk.IOTC.AVAPIs.avClientStart2(var0, var1, var2, var3,
//					var5, var6, var7);
//		} else if (p4plibver == 2) {
			return com.ubia.IOTC.AVAPIs.UBIC_avClientStart2(var0, var1, var2,
					var3, var5, var6, var7);
//		} else {
//			return -1;
//		}
	}

	public void avClientStop(int var0) {
//		if (p4plibver == 1) {
//			com.tutk.IOTC.AVAPIs.avClientStop(var0);
//		} else if (p4plibver == 2) {
		com.ubia.IOTC.AVAPIs.UBIC_avClientStop(var0);
//		} else {
//			// return -1;
//		}
	}

	public static int avDeInitialize() {
		// if (p4plibver == 1) {
		// return com.tutk.IOTC.AVAPIs.avDeInitialize();
		// } else if (p4plibver == 2) {
		// return com.ubia.IOTC.AVAPIs.UBIC_avDeInitialize();
		// } else {
		// return -1;
		// }

		return com.ubia.IOTC.AVAPIs.UBIC_avDeInitialize();
	}

	public int avGetAVApiVer() {
//		if (p4plibver == 1) {
//			return com.tutk.IOTC.AVAPIs.avGetAVApiVer();
//		} else if (p4plibver == 2) {
			return com.ubia.IOTC.AVAPIs.UBIC_avGetAVApiVer();
//		} else {
//			return -1;
//		}
	}

	public static int avInitialize(int var0) {

		// if (p4plibver == 1) {
		// return com.tutk.IOTC.AVAPIs.avInitialize(var0);
		// } else if (p4plibver == 2) {
		// return com.ubia.IOTC.AVAPIs.UBIC_avInitialize(var0);
		// } else {
		// return -1;
		// }

		return com.ubia.IOTC.AVAPIs.UBIC_avInitialize(var0);
		
		// St_VPGInfo s_vpgInfo=null;
		// com.ubia.IOTC.IOTCAPIs.UBIC_GetVPGByUid("xxxxx",s_vpgInfo);
		// if(s_vpgInfo.Gid......=newso){
		// g_so=newso;
		// return com.ubia.IOTC.AVAPIs.UBIC_avInitialize(var0);
		// }else
		// {
		// g_so=oldso;
		// return com.tutk.IOTC.AVAPIs.avInitialize(var0);
		// }
	}

	public int avRecvAudioData(int var0, byte[] var1, int var2, byte[] var3,
			int var4, int[] var5) {
//		if (p4plibver == 1) {
//			return com.tutk.IOTC.AVAPIs.avRecvAudioData(var0, var1, var2, var3,
//					var4, var5);
//		} else if (p4plibver == 2) {
			return com.ubia.IOTC.AVAPIs.UBIC_avRecvAudioData(var0, var1, var2,
					var3, var4, var5);
//		} else {
//			return -1;
//		}
	}

	public int avRecvFrameData(int var0, byte[] var1, int var2, byte[] var3,
			int var4, int[] var5) {
//		if (p4plibver == 1) {
//			return com.tutk.IOTC.AVAPIs.avRecvFrameData(var0, var1, var2, var3,
//					var4, var5);
//		} else if (p4plibver == 2) {
			return com.ubia.IOTC.AVAPIs.UBIC_avRecvFrameData(var0, var1, var2,
					var3, var4, var5);
//		} else {
//			return -1;
//		}
	}

	public int avRecvFrameData2(int var0, byte[] var1, int var2, int[] var3,
			int[] var4, byte[] var5, int var6, int[] var7, int[] var8) {
		int returntemp = 0;
//		if (p4plibver == 1) {
//			returntemp =  com.tutk.IOTC.AVAPIs.avRecvFrameData2(var0, var1, var2,
//					var3, var4, var5, var6, var7, var8);
//		} else if (p4plibver == 2) {
			returntemp =  com.ubia.IOTC.AVAPIs.UBIC_avRecvFrameData2(var0, var1, var2,
					var3, var4, var5, var6, var7, var8);
//		} else {
//			returntemp = -1;
//		}
		
		return returntemp;
	}

	public int avRecvIOCtrl(int var0, int[] var1, byte[] var2, int var3,
			int var4) {
//		if (p4plibver == 1) {
//			return com.tutk.IOTC.AVAPIs.avRecvIOCtrl(var0, var1, var2, var3,
//					var4);
//		} else if (p4plibver == 2) {
			return com.ubia.IOTC.AVAPIs.UBIC_avRecvIOCtrl(var0, var1, var2,
					var3, var4);
//		} else {
//			return -1;
//		}
	}

	public int avSendAudioData(int var0, byte[] var1, int var2, byte[] var3,
			int var4) {
//		if (p4plibver == 1) {
//			return com.tutk.IOTC.AVAPIs.avSendAudioData(var0, var1, var2, var3,
//					var4);
//		} else if (p4plibver == 2) {
			return com.ubia.IOTC.AVAPIs.UBIC_avSendAudioData(var0, var1, var2,
					var3, var4);
//		} else {
//			return -1;
//		}
	}

	public int avSendFrameData(int var0, byte[] var1, int var2, byte[] var3,
			int var4) {
//		if (p4plibver == 1) {
//			return com.tutk.IOTC.AVAPIs.avSendFrameData(var0, var1, var2, var3,
//					var4);
//		} else if (p4plibver == 2) {
			return com.ubia.IOTC.AVAPIs.UBIC_avSendFrameData(var0, var1, var2,
					var3, var4);
//		} else {
//			return -1;
//		}
	}

	public int avSendIOCtrl(int var0, int var1, byte[] var2, int var3) {
//		if (p4plibver == 1) {
//			return com.tutk.IOTC.AVAPIs.avSendIOCtrl(var0, var1, var2, var3);
//		} else if (p4plibver == 2) {
			return com.ubia.IOTC.AVAPIs.UBIC_avSendIOCtrl(var0, var1, var2,
					var3);
//		} else {
//			return -1;
//		}
	}

	public int avSendIOCtrlExit(int var0) {
//		if (p4plibver == 1) {
//			return com.tutk.IOTC.AVAPIs.avSendIOCtrlExit(var0);
//		} else if (p4plibver == 2) {
			return com.ubia.IOTC.AVAPIs.UBIC_avSendIOCtrlExit(var0);
//		} else {
//			return -1;
//		}
	}

	public void avServExit(int var0, int var1) {
//		if (p4plibver == 1) {
//			com.tutk.IOTC.AVAPIs.avServExit(var0, var1);
//		} else if (p4plibver == 2) {
			com.ubia.IOTC.AVAPIs.UBIC_avServExit(var0, var1);
//		} else {
//			// return -1;
//		}
	}

	public int avServStart(int var0, byte[] var1, byte[] var2, long var3,
			long var5, int var7) {
//		if (p4plibver == 1) {
//			return com.tutk.IOTC.AVAPIs.avServStart(var0, var1, var2, var3,
//					var5, var7);
//		} else if (p4plibver == 2) {
			return com.ubia.IOTC.AVAPIs.UBIC_avServStart(var0, var1, var2,
					var3, var5, var7);
//		} else {
//			return -1;
//		}
	}

	public void avServStop(int var0) {
//		if (p4plibver == 1) {
//			com.tutk.IOTC.AVAPIs.avServStop(var0);
//		} else if (p4plibver == 2) {
			com.ubia.IOTC.AVAPIs.UBIC_avServStop(var0);
//		} else {
//			// return -1;
//		}
	}
}
