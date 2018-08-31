// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   hmac_sha1.java

package com.ubia.http;

import android.util.Log;
import com.ubia.util.sha1;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

// Referenced classes of package com.ubia.http:
//			Base64

public class hmac_sha1
{

	public hmac_sha1()
	{
	}

	public static byte[] getHmacSHA1(String data, String key)
	{
		byte ipadArray[] = new byte[64];
		byte opadArray[] = new byte[64];
		byte keyArray[] = new byte[64];
		int ex = key.length();
		sha1 sha1 = new sha1();
		if (key.length() > 64)
		{
			byte temp[] = sha1.getDigestOfBytes(key.getBytes());
			ex = temp.length;
			for (int i = 0; i < ex; i++)
				keyArray[i] = temp[i];

		} else
		{
			byte temp[] = key.getBytes();
			for (int i = 0; i < temp.length; i++)
				keyArray[i] = temp[i];

		}
		for (int i = ex; i < 64; i++)
			keyArray[i] = 0;

		for (int j = 0; j < 64; j++)
		{
			ipadArray[j] = (byte)(keyArray[j] ^ 0x36);
			opadArray[j] = (byte)(keyArray[j] ^ 0x5c);
		}

		byte tempResult[] = sha1.getDigestOfBytes(join(ipadArray, data.getBytes()));
		return sha1.getDigestOfBytes(join(opadArray, tempResult));
	}

	private static byte[] join(byte b1[], byte b2[])
	{
		int length = b1.length + b2.length;
		byte newer[] = new byte[length];
		for (int i = 0; i < b1.length; i++)
			newer[i] = b1[i];

		for (int i = 0; i < b2.length; i++)
			newer[i + b1.length] = b2[i];

		return newer;
	}

	public static void standard(String data, String key)
	{
		byte byteHMAC[] = null;
		try
		{
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
			mac.init(spec);
			byteHMAC = mac.doFinal(data.getBytes());
		}
		catch (InvalidKeyException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException nosuchalgorithmexception) { }
	}

	public static String hmac_sha1(String key, String datas)
	{
		String reString = "";
		try
		{
			byte data[] = key.getBytes("UTF-8");
			javax.crypto.SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(secretKey);
			byte text[] = datas.getBytes("UTF-8");
			byte text1[] = mac.doFinal(text);
			reString = new String(Base64.encode(text1));
		}
		catch (Exception exception) { }
		return reString;
	}

	public static String hash_hmac(String value, String key)
	{
		StringBuilder sb;
		String type = "HmacSHA1";
		Mac mac=null;
		try {
			mac = Mac.getInstance(type);
			SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
			mac.init(secret);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte digest[] = mac.doFinal(value.getBytes());
		sb = new StringBuilder(digest.length * 2);
		byte abyte0[];
		int j = (abyte0 = digest).length;
		for (int i = 0; i < j; i++)
		{
			byte b = abyte0[i];
			String s = Integer.toHexString(b & 0xff);
			if (s.length() == 1)
				sb.append('0');
			sb.append(s);
		}

		return sb.toString();
	
		//Log.v("TAG", (new StringBuilder("Exception [")).append(e.getMessage()).append("]").toString(), e);
		//return null;
	}
}
