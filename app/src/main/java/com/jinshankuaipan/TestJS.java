// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   TestJS.java

package com.jinshankuaipan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

// Referenced classes of package com.jinshankuaipan:
//			HMACSHA1, Base64Utility

public class TestJS
{

	private static final String NONCE_SAMPLE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final String ENCODE = "UTF-8";

	public TestJS()
	{
	}

	private static String generateNonce()
	{
		return generateNonce(10);
	}

	private static String generateNonce(int length)
	{
		Random random = new Random(System.currentTimeMillis());
		if (length < 10)
			length = 10;
		int MAX_LEN = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++)
			buf.append("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(random.nextInt(MAX_LEN)));

		return buf.toString();
	}

	private static String urlEncode(String str)
	{
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static void getmetadate()
	{
		String oauth_nonce = generateNonce();
		String oauth_timestamp = Long.toString(System.currentTimeMillis() / 1000L);
		String oauth_version = "1.0";
		String oauth_consumer_key = "xcRiubcG7pcuUopU";
		String consumer_secret = "V3dFfAEyjCZTgJny";
		String oauth_token = "03e073e43fa8a8bb2e210173";
		String oauth_token_secret = "0df5d18b279d44dbbf03835749fb4bed";
		String oauth_signature_method = "HMAC-SHA1";
		String baseurl = "http://openapi.kuaipan.cn/1/metadata/app_folder";
		String reqparam = String.format("oauth_consumer_key=%s&oauth_nonce=%s&oauth_signature_method=%s&oauth_timestamp=%s&oauth_token=%s&oauth_version=%s", new Object[] {
			oauth_consumer_key, oauth_nonce, oauth_signature_method, oauth_timestamp, oauth_token, oauth_version
		});
		String hmax_txt = String.format("GET&%s&%s", new Object[] {
			urlEncode(baseurl), urlEncode(reqparam)
		});
		System.out.println((new StringBuilder("hmax:")).append(hmax_txt).toString());
		String secret = (new StringBuilder(String.valueOf(consumer_secret))).append("&").append(oauth_token_secret).toString();
		String signature = urlEncode(Base64Utility.encode(HMACSHA1.getHmacSHA1(hmax_txt, secret)));
		System.out.println((new StringBuilder("signature:")).append(signature).toString());
		String reqUrl = String.format("%s?oauth_signature=%s&%s", new Object[] {
			baseurl, signature, reqparam
		});
		System.out.println((new StringBuilder("req_metadate_Url:")).append(reqUrl).toString());
	}

	public static void download()
	{
		String oauth_nonce = generateNonce();
		String oauth_timestamp = Long.toString(System.currentTimeMillis() / 1000L);
		String oauth_version = "1.0";
		String oauth_consumer_key = "xcRiubcG7pcuUopU";
		String consumer_secret = "V3dFfAEyjCZTgJny";
		String oauth_token = "03e073e43fa8a8bb2e210173";
		String oauth_token_secret = "0df5d18b279d44dbbf03835749fb4bed";
		String oauth_signature_method = "HMAC-SHA1";
		String baseurl = "http://api-content.dfs.kuaipan.cn/1/fileops/download_file";
		String path = "test.mp4";
		String root = "app_folder";
		String reqparam = String.format("oauth_consumer_key=%s&oauth_nonce=%s&oauth_signature_method=%s&oauth_timestamp=%s&oauth_token=%s&oauth_version=%s&path=%s&root=%s", new Object[] {
			oauth_consumer_key, oauth_nonce, oauth_signature_method, oauth_timestamp, oauth_token, oauth_version, path, root
		});
		String hmax_txt = String.format("GET&%s&%s", new Object[] {
			urlEncode(baseurl), urlEncode(reqparam)
		});
		System.out.println((new StringBuilder("hmax:")).append(hmax_txt).toString());
		String secret = (new StringBuilder(String.valueOf(consumer_secret))).append("&").append(oauth_token_secret).toString();
		String signature = urlEncode(Base64Utility.encode(HMACSHA1.getHmacSHA1(hmax_txt, secret)));
		System.out.println((new StringBuilder("signature:")).append(signature).toString());
		String reqUrl = String.format("%s?oauth_signature=%s&%s", new Object[] {
			baseurl, signature, reqparam
		});
		System.out.println((new StringBuilder("req_dowload_file_Url:")).append(reqUrl).toString());
	}

	public static void main(String args[])
	{
		getmetadate();
	}
}
