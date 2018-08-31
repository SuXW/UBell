// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HttpClient.java

package com.ubia.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Random;

import org.apache.http.HttpEntity;

import android.content.Context;
import android.util.Log;
import cn.ubia.UbiaApplication;

import com.jinshankuaipan.Base64Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HttpClient
{

	public static String getBaseUrl() {
		return BASE_URL;
	}
	public static void setBaseUrl(String country) {
		if(country.toUpperCase().equals("CN")){
			BASE_URL = "http://cnportal.ubianet.com";//
		}else{
			BASE_URL = "http://usportal.ubianet.com";
		}
	}
	private static   String BASE_URL = "http://usportal.ubianet.com"; //"cnportal.ubianet.com";
	private static final String JS_BASE_URL = "http://openapi.kuaipan.cn/1/metadata/app_folder";
	private static final String HASH_METHOD = "HmacSHA1";
	private static final String NONCE_SAMPLE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final String ENCODE = "UTF-8";
	private static AsyncHttpClient client;
	private String token;
	private String tokenSecret;
	String oauth_secret;
	String oauth_token;
	String oauth_consumer_key;
	String oauth_consumer_secret;
	String maccount;
	String mvalidCode;
	String mpassword;
	String mauthid;

	public HttpClient()
	{
	}

	public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
	{
		client.get(url, params, responseHandler);
	}

	public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler)
	{
		client.post(url, params, responseHandler);
	}

	public void post(Context context, String url, HttpEntity entity, String contentType, AsyncHttpResponseHandler responseHandler)
	{
		client.post(context, url, entity, contentType, responseHandler);
	}

	public void setToken(String token, String tokenSecret)
	{
		this.token = token;
		this.tokenSecret = tokenSecret;
	}

	public HttpClient(String token, String tokenSecret)
	{
		this.token = token;
		this.tokenSecret = tokenSecret;
	}

	public HttpClient(String oauth_secret, String oauth_token, String oauth_consumer_key, String oauth_consumer_secret)
	{
		this.oauth_secret = oauth_secret;
		this.oauth_token = oauth_token;
		this.oauth_consumer_key = oauth_consumer_key;
		this.oauth_consumer_secret = oauth_consumer_secret;
	}

	public String get_replace_str(String str)
	{
		if (str != null)
		{
			str = str.replace("+", "-");
			str = str.replace("/", "_");
			str = str.replace("=", ",");
		}
		return str;
	}

	public String getCharAndNumr(int length)
	{
		String val = "";
		Random random = new Random();
		for (int i = 0; i < length; i++)
		{
			String charOrNum = random.nextInt(2) % 2 != 0 ? "num" : "char";
			if ("char".equalsIgnoreCase(charOrNum))
			{
				int choice = random.nextInt(2) % 2 != 0 ? 97 : 65;
				val = (new StringBuilder(String.valueOf(val))).append((char)(choice + random.nextInt(26))).toString();
			} else
			if ("num".equalsIgnoreCase(charOrNum))
				val = (new StringBuilder(String.valueOf(val))).append(String.valueOf(random.nextInt(10))).toString();
		}

		return val;
	}

	private void addCommonParams(LinkedHashMap map)
	{
		map.put("Time", get_replace_str(new String(Base64.encode(DateUtil.formatHttpParamStyle((new Date()).getTime()).getBytes()))));
		map.put("Nonce", get_replace_str(new String(Base64.encode(getCharAndNumr(8).getBytes()))));
		map.put("Seq", get_replace_str(new String(Base64.encode("4678".getBytes()))));
	}

	private String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}

	private String getHmac(LinkedHashMap map, String tokenStr)
	{
		String params = "";
		for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();)
		{
			String key = (String)iterator.next();
			params = (new StringBuilder(String.valueOf(params))).append("&").append(key).append("=").append((String)map.get(key)).toString();
		}

		if (params.length() > 0)
			params = params.substring(1);
		return Encryption.EnResult(params, tokenStr);
	}

	private String getmaptostr(LinkedHashMap map)
	{
		String params = "";
		for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();)
		{
			String key = (String)iterator.next();
			params = (new StringBuilder(String.valueOf(params))).append("&").append(key).append("=").append((String)map.get(key)).toString();
		}

		if (params.length() > 0)
			params = params.substring(1);
		return params;
	}

	public void get(RequestParams params, AsyncHttpResponseHandler responseHandler)
	{
		LinkedHashMap map = new LinkedHashMap();
		addCommonParams(map);
		String hmac = getHmac(map, (new StringBuilder(String.valueOf(token))).append("&").append(tokenSecret).toString());
		params.add("HMAC", hmac);
		if (UbiaApplication.DEBUG.booleanValue())
		{
			String url = AsyncHttpClient.getUrlWithQueryString(true, "http://www.miaohome.net", params);
			Log.d("url", url);
		}
		client.get(getAbsoluteUrl("/interface.php"), params, responseHandler);
	}

	public void requestTemporaryToken(String account, AsyncHttpResponseHandler responseHandler)
	{
		int Function = 49;
		RequestParams params = new RequestParams();
		params.add("Webaccount", account);
		params.add("Function", Integer.toString(Function));
		params.add("Command", "1");
		params.add("Time", DateUtil.formatHttpParamStyle((new Date()).getTime()));
		params.add("Nonce", "7aT8z8hJ");
		params.add("Seq", "4");
		params.add("Hmac", "0");
		client.get(getAbsoluteUrl("/interface.php"), params, responseHandler);
	}

	public void renewToken(String account, AsyncHttpResponseHandler responseHandler)
	{
		int Function = 48;
		RequestParams params = new RequestParams();
		params.add("Account", account);
		params.add("Function", Integer.toString(Function));
		params.add("Command", "3");
		params.add("Time", DateUtil.formatHttpParamStyle((new Date()).getTime()));
		params.add("Nonce", "7aT8z8hJ");
		params.add("Seq", "4");
		params.add("Hmac", "0");
		client.get(getAbsoluteUrl("/interface"), params, responseHandler);
		showUrl(params);
	}

	public void getVerificationURL(AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();
		int Function = 67;
		params.add("Function", Integer.toString(Function));
		params.add("Command", "1");
		params.add("Time", DateUtil.formatHttpParamStyle((new Date()).getTime()));
		params.add("Nonce", "7aT8z8hJ");
		params.add("Seq", "4");
		params.add("Hmac", "0");
		params.add("Account", "0");
		params.add("Token", "0");
		client.get(getAbsoluteUrl("/interface.php"), params, responseHandler);
		showUrl(params);
	}

	public void regsiterUser(String account, String password, String validCode, String authid, AsyncHttpResponseHandler responseHandler)
	{
		maccount = account;
		mpassword = password;
		mvalidCode = validCode;
		mauthid = authid;
		String time = get_replace_str(new String(Base64.encode(DateUtil
				.formatHttpParamStyle(new Date().getTime()).getBytes())));
		String seq = get_replace_str(new String(
				Base64.encode("4678".getBytes())));
		String noncestr = get_replace_str(new String(
				Base64.encode(getCharAndNumr(8).getBytes())));
		String accountstr = get_replace_str(new String(Base64.encode(maccount
				.getBytes())));

		String passwordstr = get_replace_str(new String(Base64.encode(hmac_sha1
				.getHmacSHA1(mpassword, ""))));
		Log.i("url", "token before:" + token);
		String tokenstr = get_replace_str(new String(Base64.encode(token
				.getBytes())));
		String validCodestr = get_replace_str(new String(
				Base64.encode(mvalidCode.getBytes())));
		String authidstr = (get_replace_str(new String(Base64.encode(mauthid
				.getBytes()))));
		// Log.i("url", "token after:" + token);
		String summary = String
				.format("Time=%s&Nonce=%s&Seq=%s&Webaccount=%s&Password=%s&TemporaryToken=%s&verification_code=%s&authid=%s",
						time, noncestr, seq, accountstr, passwordstr, tokenstr,
						validCodestr, authidstr);
		String HMAC = get_replace_str(Encryption.EnResult1(summary, token + "&"
				+ tokenSecret));
		String strUrl = String
				.format("/interface.php?Function=%s&Command=%s&Hmac=%s&Time=%s&Nonce=%s&Seq=%s&Webaccount=%s&Password=%s&TemporaryToken=%s&verification_code=%s&authid=%s",
						49, 2, HMAC, time, noncestr, seq, accountstr,
						passwordstr, tokenstr, validCodestr, authidstr);
		client.get(getAbsoluteUrl(strUrl), responseHandler);
	}

	public void updateLoginPwd(String account, String password, AsyncHttpResponseHandler responseHandler)
	{
		maccount = account;
		mpassword = password;
		String time = get_replace_str(new String(Base64.encode(DateUtil.formatHttpParamStyle((new Date()).getTime()).getBytes())));
		String seq = get_replace_str(new String(Base64.encode("4678".getBytes())));
		String noncestr = get_replace_str(new String(Base64.encode(getCharAndNumr(8).getBytes())));
		String accountstr = get_replace_str(new String(Base64.encode(maccount.getBytes())));
		String passwordstr = get_replace_str(new String(Base64.encode(hmac_sha1.getHmacSHA1(mpassword, ""))));
		Log.i("url", (new StringBuilder("token before:")).append(token).toString());
		String tokenstr = get_replace_str(new String(Base64.encode(token.getBytes())));
		String summary = String.format("Time=%s&Nonce=%s&Seq=%s&Webaccount=%s&Token=%s&Password=%s", new Object[] {
				time, noncestr, seq, accountstr, tokenstr, passwordstr
		});
		String HMAC = get_replace_str(Encryption.EnResult1(summary, tokenSecret));
		String strUrl = String.format("/interface.php?Function=%s&Command=%s&Hmac=%s&Time=%s&Nonce=%s&Seq=%s&Webaccount=%s&Token=%s&Password=%s", new Object[] {
				Integer.valueOf(49), Integer.valueOf(3), HMAC, time, noncestr, seq, accountstr, tokenstr, passwordstr
		});
		Log.i("change", (new StringBuilder("changepwdurl:")).append(getAbsoluteUrl(strUrl)).toString());
		client.get(getAbsoluteUrl(strUrl), responseHandler);
	}

	public void loginUser(String account, String password, AsyncHttpResponseHandler responseHandler)
	{
		String time = get_replace_str(new String(Base64.encode(DateUtil.formatHttpParamStyle((new Date()).getTime()).getBytes())));
		String seq = get_replace_str(new String(Base64.encode("4678".getBytes())));
		String noncestr = get_replace_str(new String(Base64.encode(getCharAndNumr(8).getBytes())));
		String accountstr = get_replace_str(new String(Base64.encode(account.getBytes())));
		String passwordstr = get_replace_str(new String(Base64.encode(hmac_sha1.getHmacSHA1(password, ""))));
		String hmac_text = String.format("Time=%s&Nonce=%s&Seq=%s&Account=%s", new Object[] {
				time, noncestr, seq, accountstr
		});
		String HMAC = get_replace_str(Encryption.EnResult1(hmac_text, passwordstr));
		String strUrl = String.format("/interface.php?Function=%s&Command=%s&Hmac=%s&Time=%s&Nonce=%s&Seq=%s&Account=%s", new Object[] {
				Integer.valueOf(48), Integer.valueOf(1), HMAC, time, noncestr, seq, accountstr
		});
		client.get(getAbsoluteUrl(strUrl), responseHandler);
	}

	public void voipServiceDeviceOperate(String devicUid, int operation, AsyncHttpResponseHandler responseHandler) {

		String time = get_replace_str(new String(Base64.encode(DateUtil.formatHttpParamStyle((new Date()).getTime()).getBytes())));
		String seq = get_replace_str(new String(Base64.encode("4678".getBytes())));
		String noncestr = get_replace_str(new String(Base64.encode(getCharAndNumr(8).getBytes())));

		String tokenstr = get_replace_str(new String(Base64.encode(token.getBytes())));

		String uidJsonstr = String.format("{\"UID_List\":[{\"UID\":\"%s\"}],\"UidCount\":1}",devicUid);
		String uidListstr = get_replace_str(new String(Base64.encode(uidJsonstr.getBytes())));
		String devicetoken =  String.format("%s",UbiaApplication.getDeviceToken());
		String devicetokenstr = get_replace_str(new String(Base64.encode(devicetoken.getBytes())));

		//Log.e("voipServiceDeviceOperate", "devicetoken:"+devicetoken +"UIDLIST:"+uidJsonstr);
		String devicetypestr = get_replace_str(new String(Base64.encode("2".getBytes())));

		String hmac_text = String.format("Time=%s&Nonce=%s&Seq=%s&Token=%s&Devicetoken=%s&Devicetype=%s&UIDList=%s", new Object[] {
				time, noncestr, seq, tokenstr, devicetokenstr,devicetypestr,uidListstr});

		String HMAC = get_replace_str(Encryption.EnResult1(hmac_text, tokenSecret));

		String strUrl = String.format("/interface.php?Function=72&Command=%d&Hmac=%s&Time=%s&Nonce=%s&Seq=%s&Token=%s&Devicetoken=%s&Devicetype=%s&UIDList=%s", new Object[] {
				operation, HMAC, time, noncestr, seq,tokenstr, devicetokenstr,devicetypestr,uidListstr});
		//Log.e("url", (new StringBuilder("voipServiceDeviceOperate:")).append(getAbsoluteUrl(strUrl)).toString());
		client.get(getAbsoluteUrl(strUrl), responseHandler);
	}

	public static void operateDeviceVoip(String devicUid, int operation ,JsonHttpResponseHandler mJsonHttpResponseHandler){
		HttpClient httpClient = new HttpClient("ubianet123456", "ubianet123456");
		httpClient.voipServiceDeviceOperate(devicUid,operation,mJsonHttpResponseHandler);

	}


	public void addDevice(String account, String password, String devicUid, String deviceName, String deviceLocation, AsyncHttpResponseHandler responseHandler)
	{
		if ("".equals(deviceLocation) || deviceLocation == null)
			deviceLocation = "default";
		String time = get_replace_str(new String(Base64.encode(DateUtil.formatHttpParamStyle((new Date()).getTime()).getBytes())));
		String seq = get_replace_str(new String(Base64.encode("4678".getBytes())));
		String noncestr = get_replace_str(new String(Base64.encode(getCharAndNumr(8).getBytes())));
		String accountstr = get_replace_str(new String(Base64.encode(account.getBytes())));
		String tokenstr = get_replace_str(new String(Base64.encode(token.getBytes())));
		String uidstr = get_replace_str(new String(Base64.encode(devicUid.getBytes())));
		String namestr = get_replace_str(new String(Base64.encode(deviceName.getBytes())));
		String deviceLocationstr = get_replace_str(new String(Base64.encode(deviceLocation.getBytes())));
		String passwordstr = get_replace_str(new String(Base64.encode(password.getBytes())));
		String LoginID = get_replace_str(new String(Base64.encode("admin".getBytes())));
		String hmac_text = String.format("Time=%s&Nonce=%s&Seq=%s&Webaccount=%s&Password=%s&Token=%s&UID=%s&Name=%s&Location=%s&LoginID=%s", new Object[] {
				time, noncestr, seq, accountstr, passwordstr, tokenstr, uidstr, namestr, deviceLocationstr, LoginID
		});
		String HMAC = get_replace_str(Encryption.EnResult1(hmac_text, tokenSecret));
		String strUrl = String.format("/interface.php?Function=%s&Command=%s&Hmac=%s&Time=%s&Nonce=%s&Seq=%s&Webaccount=%s&Password=%s&Token=%s&UID=%s&Name=%s&Location=%s&LoginID=%s", new Object[] {
				Integer.valueOf(51), Integer.valueOf(1), HMAC, time, noncestr, seq, accountstr, passwordstr, tokenstr, uidstr,
				namestr, deviceLocationstr, LoginID});
		Log.i("url", (new StringBuilder("addDevice:")).append(getAbsoluteUrl(strUrl)).toString());
		client.get(getAbsoluteUrl(strUrl), responseHandler);
	}

	public void updateDevice(String account, String password, String devicUid, String deviceName, String deviceLocation, AsyncHttpResponseHandler responseHandler)
	{
		if ("".equals(deviceLocation) || deviceLocation == null)
			deviceLocation = "default";
		String time = get_replace_str(new String(Base64.encode(DateUtil.formatHttpParamStyle((new Date()).getTime()).getBytes())));
		String seq = get_replace_str(new String(Base64.encode("4678".getBytes())));
		String noncestr = get_replace_str(new String(Base64.encode(getCharAndNumr(8).getBytes())));
		String accountstr = get_replace_str(new String(Base64.encode(account.getBytes())));
		String tokenstr = get_replace_str(new String(Base64.encode(token.getBytes())));
		String uidstr = get_replace_str(new String(Base64.encode(devicUid.getBytes())));
		String namestr = get_replace_str(new String(Base64.encode(deviceName.getBytes())));
		String deviceLocationstr = get_replace_str(new String(Base64.encode(deviceLocation.getBytes())));
		String passwordstr = get_replace_str(new String(Base64.encode(password.getBytes())));
		String LoginID = get_replace_str(new String(Base64.encode("admin".getBytes())));
		String hmac_text = String.format("Time=%s&Nonce=%s&Seq=%s&Webaccount=%s&Password=%s&Token=%s&UID=%s&Name=%s&Location=%s&LoginID=%s", new Object[] {
				time, noncestr, seq, accountstr, passwordstr, tokenstr, uidstr, namestr, deviceLocationstr, LoginID
		});
		String HMAC = get_replace_str(Encryption.EnResult1(hmac_text, tokenSecret));
		String strUrl = String.format("/interface.php?Function=%s&Command=%s&Hmac=%s&Time=%s&Nonce=%s&Seq=%s&Webaccount=%s&Password=%s&Token=%s&UID=%s&Name=%s&Location=%s&LoginID=%s", new Object[] {
				Integer.valueOf(51), Integer.valueOf(2), HMAC, time, noncestr, seq, accountstr, passwordstr, tokenstr, uidstr,
				namestr, deviceLocationstr, LoginID
		});
		Log.i("url", (new StringBuilder("updateDevice:")).append(getAbsoluteUrl(strUrl)).toString());
		client.get(getAbsoluteUrl(strUrl), responseHandler);
	}

	public void removeDevice(String account, String nickname, String password, String devicUid, AsyncHttpResponseHandler responseHandler)
	{
		String time = get_replace_str(new String(Base64.encode(DateUtil.formatHttpParamStyle((new Date()).getTime()).getBytes())));
		String seq = get_replace_str(new String(Base64.encode("4678".getBytes())));
		String noncestr = get_replace_str(new String(Base64.encode(getCharAndNumr(8).getBytes())));
		String accountstr = get_replace_str(new String(Base64.encode(account.getBytes())));
		String passwordstr = get_replace_str(new String(Base64.encode(password.getBytes())));
		String tokenstr = get_replace_str(new String(Base64.encode(token.getBytes())));
		String uidstr = get_replace_str(new String(Base64.encode(devicUid.getBytes())));
		String namestr = get_replace_str(new String(Base64.encode(nickname.getBytes())));
		String deviceLocationstr = get_replace_str(new String(Base64.encode("hello".getBytes())));
		String LoginID = get_replace_str(new String(Base64.encode("admin".getBytes())));
		String hmac_text = String.format("Time=%s&Nonce=%s&Seq=%s&Webaccount=%s&Password=%s&Token=%s&UID=%s&Name=%s&Location=%s&LoginID=%s", new Object[] {
				time, noncestr, seq, accountstr, passwordstr, tokenstr, uidstr, namestr, deviceLocationstr, LoginID
		});
		String HMAC = get_replace_str(Encryption.EnResult1(hmac_text, tokenSecret));
		String strUrl = String.format("/interface.php?Function=%s&Command=%s&Hmac=%s&Time=%s&Nonce=%s&Seq=%s&Webaccount=%s&Password=%s&Token=%s&UID=%s&Name=%s&Location=%s&LoginID=%s", new Object[] {
				Integer.valueOf(51), Integer.valueOf(3), HMAC, time, noncestr, seq, accountstr, passwordstr, tokenstr, uidstr,
				namestr, deviceLocationstr, LoginID
		});
		Log.i("deldevice", (new StringBuilder("removeDevice:")).append(getAbsoluteUrl(strUrl)).toString());
		client.get(getAbsoluteUrl(strUrl), responseHandler);
	}

	public void getMyDeviceList(String account, AsyncHttpResponseHandler responseHandler)
	{
		String time = get_replace_str(new String(Base64.encode(DateUtil.formatHttpParamStyle((new Date()).getTime()).getBytes())));
		String seq = get_replace_str(new String(Base64.encode("4678".getBytes())));
		String noncestr = get_replace_str(new String(Base64.encode(getCharAndNumr(8).getBytes())));
		String accountstr = get_replace_str(new String(Base64.encode(account.getBytes())));
		String tokenstr = get_replace_str(new String(Base64.encode(token.getBytes())));
		String LoginID = get_replace_str(new String(Base64.encode("admin".getBytes())));
		String hmac_text = String.format("Time=%s&Nonce=%s&Seq=%s&Account=%s&Token=%s&LoginID=%s", new Object[] {
				time, noncestr, seq, accountstr, tokenstr, LoginID
		});
		String HMAC = get_replace_str(Encryption.EnResult1(hmac_text, tokenSecret));
		String strUrl = String.format("/interface.php?Function=%s&Command=%s&Hmac=%s&Time=%s&Nonce=%s&Seq=%s&Account=%s&Token=%s&LoginID=%s", new Object[] {
				Integer.valueOf(64), Integer.valueOf(1), HMAC, time, noncestr, seq, accountstr, tokenstr, LoginID
		});
		Log.i("url", (new StringBuilder("getMyDeviceList:")).append(getAbsoluteUrl(strUrl)).toString());
		client.get(getAbsoluteUrl(strUrl), responseHandler);
	}

	public void query_account_cloudinfo(String account, AsyncHttpResponseHandler responseHandler)
	{
		String time = get_replace_str(new String(Base64.encode(DateUtil.formatHttpParamStyle((new Date()).getTime()).getBytes())));
		String seq = get_replace_str(new String(Base64.encode("4678".getBytes())));
		String noncestr = get_replace_str(new String(Base64.encode(getCharAndNumr(8).getBytes())));
		String accountstr = get_replace_str(new String(Base64.encode(account.getBytes())));
		String tokenstr = get_replace_str(new String(Base64.encode(token.getBytes())));
		String hmac_text = String.format("Time=%s&Nonce=%s&Seq=%s&Webaccount=%s&Token=%s", new Object[] {
				time, noncestr, seq, accountstr, tokenstr
		});
		String HMAC = get_replace_str(Encryption.EnResult1(hmac_text, tokenSecret));
		String strUrl = String.format("/interface.php?Function=%s&Command=%s&Hmac=%s&Time=%s&Nonce=%s&Seq=%s&Webaccount=%s&Token=%s", new Object[] {
				Integer.valueOf(69), Integer.valueOf(1), HMAC, time, noncestr, seq, accountstr, tokenstr
		});
		Log.i("url", (new StringBuilder("query_account_cloudinfo:")).append(getAbsoluteUrl(strUrl)).toString());
		client.get(getAbsoluteUrl(strUrl), responseHandler);
	}

	private static String generateNonce()
	{
		return generateNonce(8);
	}

	private static String generateNonce(int length)
	{
		Random random = new Random(System.currentTimeMillis());
		if (length < 8)
			length = 8;
		int MAX_LEN = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++)
			buf.append("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(random.nextInt(MAX_LEN)));

		return buf.toString();
	}

	public String getdownloadUrl(String filename)
	{
		String oauth_nonce = generateNonce();
		String oauth_timestamp = Long.toString(System.currentTimeMillis() / 1000L);
		String oauth_version = "1.0";
		String oauth_signature_method = "HMAC-SHA1";
		String baseurl = "http://api-content.dfs.kuaipan.cn/1/fileops/download_file";
		String reqparam = String.format("oauth_consumer_key=%s&oauth_nonce=%s&oauth_signature_method=%s&oauth_timestamp=%s&oauth_token=%s&oauth_version=%s&path=%s&root=%s", new Object[] {
				oauth_consumer_key, oauth_nonce, oauth_signature_method, oauth_timestamp, oauth_token, oauth_version, filename, "app_folder"
		});
		String hmax_txt = String.format("GET&%s&%s", new Object[] {
				urlEncode(baseurl), urlEncode(reqparam)
		});
		System.out.println((new StringBuilder("hmax:")).append(hmax_txt).toString());
		String secret = (new StringBuilder(String.valueOf(oauth_consumer_secret))).append("&").append(oauth_secret).toString();
		String signature = urlEncode(Base64Utility.encode(HMACSHA1.getHmacSHA1(hmax_txt, secret)));
		System.out.println((new StringBuilder("signature:")).append(signature).toString());
		String reqUrl = String.format("%s?oauth_signature=%s&%s", new Object[] {
				baseurl, signature, reqparam
		});
		Log.i("url", (new StringBuilder("oauth_nonce:")).append(oauth_nonce).toString());
		Log.i("url", (new StringBuilder("oauth_secret:")).append(oauth_secret).toString());
		Log.i("url", (new StringBuilder("getdownloadUrl:")).append(reqUrl).toString());
		return reqUrl;
	}

	public void getmetadata(AsyncHttpResponseHandler responseHandler)
	{
		String oauth_nonce = generateNonce();
		String oauth_timestamp = Long.toString(System.currentTimeMillis() / 1000L);
		String oauth_version = "1.0";
		String oauth_signature_method = "HMAC-SHA1";
		String baseurl = "http://openapi.kuaipan.cn/1/metadata/app_folder";
		String reqparam = String.format("oauth_consumer_key=%s&oauth_nonce=%s&oauth_signature_method=%s&oauth_timestamp=%s&oauth_token=%s&oauth_version=%s", new Object[] {
				oauth_consumer_key, oauth_nonce, oauth_signature_method, oauth_timestamp, oauth_token, oauth_version
		});
		String hmax_txt = String.format("GET&%s&%s", new Object[] {
				urlEncode(baseurl), urlEncode(reqparam)
		});
		System.out.println((new StringBuilder("hmax:")).append(hmax_txt).toString());
		String secret = (new StringBuilder(String.valueOf(oauth_consumer_secret))).append("&").append(oauth_secret).toString();
		String signature = urlEncode(Base64Utility.encode(HMACSHA1.getHmacSHA1(hmax_txt, secret)));
		System.out.println((new StringBuilder("signature:")).append(signature).toString());
		String reqUrl = String.format("%s?oauth_signature=%s&%s", new Object[] {
				baseurl, signature, reqparam
		});
		Log.i("url", (new StringBuilder("getmetadata:")).append(reqUrl).toString());
		client.get(reqUrl, responseHandler);
		System.out.println((new StringBuilder("req_metadate_Url:")).append(reqUrl).toString());
	}

	private static String urlEncode(String str)
	{
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		UnsupportedEncodingException e;
//		e;
		return null;
	}

	public void getPublicDeviceList(AsyncHttpResponseHandler responseHandler)
	{
		String time = get_replace_str(new String(Base64.encode(DateUtil.formatHttpParamStyle((new Date()).getTime()).getBytes())));
		String seq = get_replace_str(new String(Base64.encode("4678".getBytes())));
		String noncestr = get_replace_str(new String(Base64.encode(getCharAndNumr(8).getBytes())));
		String strUrl = String.format("/interface.php?Function=%s&Command=%s&Hmac=%s&Time=%s&Nonce=%s&Seq=%s&Account=%s&Token=%s", new Object[] {
				Integer.valueOf(64), Integer.valueOf(3), "0", time, noncestr, seq, "0", "0"
		});
		Log.i("url", (new StringBuilder("getPublicDeviceList:")).append(getAbsoluteUrl(strUrl)).toString());
		client.get(getAbsoluteUrl(strUrl), responseHandler);
	}

	public void getSharedDeviceList(String account, String password, AsyncHttpResponseHandler responseHandler)
	{
		LinkedHashMap map = new LinkedHashMap();
		addCommonParams(map);
		map.put("Webaccount", account);
		map.put("Token", token);
		map.put("Password", password);
		String hmac = getHmac(map, (new StringBuilder(String.valueOf(token))).append("&").append(tokenSecret).toString());
		RequestParams params = new RequestParams(map);
		params.add("Hmac", hmac);
		int Function = 66;
		params.add("Function", Integer.toString(Function));
		params.add("Command", "3");
		client.get(getAbsoluteUrl("/interface"), params, responseHandler);
		showUrl(params);
	}

	public void shareToDeviceToFriend(String account, String password, String uid, String shareUser, String shareMessage, AsyncHttpResponseHandler responseHandler)
	{
		LinkedHashMap map = new LinkedHashMap();
		addCommonParams(map);
		map.put("Webaccount", account);
		map.put("Token", token);
		map.put("Password", password);
		map.put("UID", uid);
		map.put("Sharewebaccount", shareUser);
		map.put("Message", shareMessage);
		String hmac = getHmac(map, (new StringBuilder(String.valueOf(token))).append("&").append(tokenSecret).toString());
		RequestParams params = new RequestParams(map);
		params.add("Hmac", hmac);
		int Function = 66;
		params.add("Function", Integer.toString(Function));
		params.add("Command", "1");
		client.get(getAbsoluteUrl("/interface"), params, responseHandler);
		showUrl(params);
	}

	public void addPublicDevice(String account, String password, String uid, AsyncHttpResponseHandler responseHandler)
	{
		String time = get_replace_str(new String(Base64.encode(DateUtil.formatHttpParamStyle((new Date()).getTime()).getBytes())));
		String seq = get_replace_str(new String(Base64.encode("4678".getBytes())));
		String noncestr = get_replace_str(new String(Base64.encode(getCharAndNumr(8).getBytes())));
		String accountstr = get_replace_str(new String(Base64.encode(account.getBytes())));
		String tokenstr = get_replace_str(new String(Base64.encode(token.getBytes())));
		String uidstr = get_replace_str(new String(Base64.encode(uid.getBytes())));
		String passwordstr = get_replace_str(new String(Base64.encode(password.getBytes())));
		String hmac_text = String.format("Time=%s&Nonce=%s&Seq=%s&Webaccount=%s&Token=%s&Password=%s&UID=%s", new Object[] {
				time, noncestr, seq, accountstr, tokenstr, passwordstr, uidstr
		});
		String HMAC = get_replace_str(Encryption.EnResult1(hmac_text, tokenSecret));
		String strUrl = String.format("/interface.php?Function=%s&Command=%s&Hmac=%s&Time=%s&Nonce=%s&Seq=%s&Webaccount=%s&Token=%s&Password=%s&UID=%s", new Object[] {
				Integer.valueOf(65), Integer.valueOf(1), HMAC, time, noncestr, seq, accountstr, tokenstr, passwordstr, uidstr
		});
		Log.i("public", (new StringBuilder("addPublicDevice:")).append(getAbsoluteUrl(strUrl)).toString());
		client.get(getAbsoluteUrl(strUrl), responseHandler);
	}

	public void removePublicDevice(String account, String password, String uid, AsyncHttpResponseHandler responseHandler)
	{
		LinkedHashMap map = new LinkedHashMap();
		addCommonParams(map);
		map.put("Webaccount", account);
		map.put("Token", token);
		map.put("Password", password);
		map.put("UID", uid);
		String hmac = getHmac(map, (new StringBuilder(String.valueOf(token))).append("&").append(tokenSecret).toString());
		RequestParams params = new RequestParams(map);
		params.add("Hmac", hmac);
		int Function = 65;
		params.add("Function", Integer.toString(Function));
		params.add("Command", "2");
		client.get(getAbsoluteUrl("/interface"), params, responseHandler);
		showUrl(params);
	}

	public void removeShareDevice(String account, String password, String ownerAccount, String uid, AsyncHttpResponseHandler responseHandler)
	{
		LinkedHashMap map = new LinkedHashMap();
		addCommonParams(map);
		map.put("Webaccount", account);
		map.put("Token", token);
		map.put("Password", password);
		map.put("Ownerwebaccount", ownerAccount);
		map.put("Owneruid", uid);
		String hmac = getHmac(map, (new StringBuilder(String.valueOf(token))).append("&").append(tokenSecret).toString());
		RequestParams params = new RequestParams(map);
		params.add("Hmac", hmac);
		int Function = 66;
		params.add("Function", Integer.toString(Function));
		params.add("Command", "5");
		client.get(getAbsoluteUrl("/interface"), params, responseHandler);
		showUrl(params);
	}

	public void getShareUserFromDevice(String account, String password, String uid, AsyncHttpResponseHandler responseHandler)
	{
		LinkedHashMap map = new LinkedHashMap();
		addCommonParams(map);
		map.put("Webaccount", account);
		map.put("Token", token);
		map.put("Password", password);
		map.put("UID", uid);
		String hmac = getHmac(map, (new StringBuilder(String.valueOf(token))).append("&").append(tokenSecret).toString());
		RequestParams params = new RequestParams(map);
		params.add("Hmac", hmac);
		int Function = 66;
		params.add("Function", Integer.toString(Function));
		params.add("Command", "6");
		client.get(getAbsoluteUrl("/interface"), params, responseHandler);
		showUrl(params);
	}

	public void removeShareDeviceByOwner(String account, String password, String shareUser, String uid, AsyncHttpResponseHandler responseHandler)
	{
		LinkedHashMap map = new LinkedHashMap();
		addCommonParams(map);
		map.put("Webaccount", account);
		map.put("Token", token);
		map.put("Password", password);
		map.put("Sharewebaccount", shareUser);
		map.put("UID", uid);
		String hmac = getHmac(map, (new StringBuilder(String.valueOf(token))).append("&").append(tokenSecret).toString());
		RequestParams params = new RequestParams(map);
		params.add("Hmac", hmac);
		int Function = 66;
		params.add("Function", Integer.toString(Function));
		params.add("Command", "2");
		client.get(getAbsoluteUrl("/interface"), params, responseHandler);
		showUrl(params);
	}

	private void showUrl(RequestParams params)
	{
		if (UbiaApplication.DEBUG.booleanValue())
		{
			String url = AsyncHttpClient.getUrlWithQueryString(true, getAbsoluteUrl("/interface.php"), params);
			Log.d("url","121456456456465"+ url);
		}
	}

	public void getYoukuToken(AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();
		params.add("command", "1");
		client.get(getAbsoluteUrl("/retoken/YouKuServlet"), params, responseHandler);
		Log.d("url", AsyncHttpClient.getUrlWithQueryString(true, getAbsoluteUrl("/retoken/YouKuServlet"), params));
	}


	static
	{
		client = new AsyncHttpClient();
		client.setTimeout(30000);
	}


}
