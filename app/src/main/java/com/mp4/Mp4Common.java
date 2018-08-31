package com.mp4;

import java.util.Date;

import com.ubia.IOTC.Packet;

public class Mp4Common {

	public static int ATOMID(byte[] s) {
		if (s.length < 4) {
			return 0;
		}
		
		return Packet.byteArrayToInt_Big(s);//(s[0] << 24) | (s[1] << 16) | (s[2] << 8) | s[3];
	}
	
	public static int ATOMID(String str) {
		if (null == str || str.length() < 4) {
			return 0;
		}
		
		byte[] s = str.getBytes();
		return ATOMID(s);
	}
	
	public static long Mp4GetTimestamp() {
	    long ret;
	    Date date = new Date();
	    ret = date.getTime();
	    ret += 2082844800;
	    return ret; // MP4 start date is 1/1/1904
	    // 208284480 is (((1970 - 1904) * 365) + 17) * 24 * 60 * 60
	}
}
