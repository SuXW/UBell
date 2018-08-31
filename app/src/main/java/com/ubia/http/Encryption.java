// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Encryption.java

package com.ubia.http;

import android.util.Log;

// Referenced classes of package com.ubia.http:
//			hmac_sha1, Base64

public class Encryption
{

	public Encryption()
	{
	}

	public static String EnResult(String data, String hmac_sha1_key)
	{
		Log.e("", (new StringBuilder("summary:")).append(data).toString());
		Log.e("", (new StringBuilder("key:")).append(hmac_sha1_key).toString());
		String base64Data = hmac_sha1.hmac_sha1(hmac_sha1_key, data);
		Log.e("", (new StringBuilder("base64Data:")).append(new String(base64Data)).toString());
		return base64Data;
	}

	public static String EnResult1(String data, String hmac_sha1_key)
	{
		Log.e("url", (new StringBuilder("99999999hmac_text:")).append(data).toString());
		String base64Data = new String(Base64.encode(hmac_sha1.getHmacSHA1(data, hmac_sha1_key)));
		Log.e("url", (new StringBuilder("88888888base64Data:")).append(new String(base64Data)).toString());
		return base64Data;
	}

	public static String EnResult2(String data, String hmac_sha1_key)
	{

		String result =hmac_sha1.hash_hmac(data, hmac_sha1_key);

		return result;
	}
}
