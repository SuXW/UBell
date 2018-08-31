package cn.ubia.util;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AntsUtil {
	public static byte[] int2bytes(int num) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[3 - i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}

	public byte[] int2bytes2(int channel) {
		byte[] b = new byte[32];
		for (int i = 0; i < 32; i++) {
			b[i] = (byte) (channel >>> (24 - i * 8));
		}
		return b;
	}

	static public int bytes2int(byte[] b) {
		// 0xff�Ķ����Ʊ�ʾΪ: 1111 1111
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 4; i++) {
			res <<= 8;// ����8λ���ʲô��˼?
			temp = b[3 - i] & mask;
			res |= temp;
		}
		return res;
	}

	static public short bytes2short(byte[] b) {
		short mask = 0xff;
		short temp = 0;
		short res = 0;
		for (int i = 0; i < 2; i++) {
			res <<= 8;
			temp = (short) (b[1 - i] & mask);
			res |= temp;
		}
		return res;
	}

	static public byte[] short2bytes(short data) {
		byte bytes[] = new byte[2];

		bytes[1] = (byte) ((data & 0xff00) >> 8);
		bytes[0] = (byte) (data & 0x00ff);
		return bytes;
	}

	public static String getVedioFileNameWithTime() {
		Calendar localCalendar = Calendar.getInstance();
		int i = localCalendar.get(1);
		int j = 1 + localCalendar.get(2);
		int k = localCalendar.get(5);
		int m = localCalendar.get(11);
		int n = localCalendar.get(12);
		int i1 = localCalendar.get(13);
		localCalendar.get(14);
		StringBuffer localStringBuffer = new StringBuffer();
		localStringBuffer.append("VEDIO_");
		localStringBuffer.append(i);
		if (j < 10)
			localStringBuffer.append('0');
		localStringBuffer.append(j);
		if (k < 10)
			localStringBuffer.append('0');
		localStringBuffer.append(k);
		localStringBuffer.append('_');
		if (m < 10)
			localStringBuffer.append('0');
		localStringBuffer.append(m);
		if (n < 10)
			localStringBuffer.append('0');
		localStringBuffer.append(n);
		if (i1 < 10)
			localStringBuffer.append('0');
		localStringBuffer.append(i1);
		localStringBuffer.append(".mp4");
		return localStringBuffer.toString();
	}

	public static boolean hasBind(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		String flag = sp.getString("bind_flag", "");
		if ("ok".equalsIgnoreCase(flag)) {
			return true;
		}
		return false;
	}

}
