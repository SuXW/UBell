// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HMACSHA1.java

package com.jinshankuaipan;

import com.ubia.http.SHA1;

public class HMACSHA1
{

	public HMACSHA1()
	{
	}

	public static byte[] getHmacSHA1(String data, String key)
	{
		byte ipadArray[] = new byte[64];
		byte opadArray[] = new byte[64];
		byte keyArray[] = new byte[64];
		int ex = key.length();
		SHA1 sha1 = new SHA1();
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
}
