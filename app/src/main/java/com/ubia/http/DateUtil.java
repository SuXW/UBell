// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   DateUtil.java

package com.ubia.http;

import java.text.*;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil
{

	public DateUtil()
	{
	}

	public static String formatToNormalStyle(long time)
	{
		Date date = new Date(time);
		String pattern = "yyyy-MM-dd HH:mm:ss";
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}

	public static String formatHttpParamStyle(long time)
	{
		Date date = new Date(time);
		String pattern = "yyyyMMdd'T'HHmmss";
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}

	public static String formatNormalTimeStyle(long time)
	{
		Date date = new Date(time);
		String pattern = "HH:mm:ss";
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}

	public static String formatToEventDateStyle(long time)
	{
		Date date = new Date(time);
		String pattern = "yyyy-MM-dd";
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}

	public static String formatToEventTimeStyle(long time)
	{
		Date date = new Date(time);
		String pattern = "HH:mm";
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}

	public static long parseToUTCTime(String dateStr)
	{
		long time = 0L;
		String pattern = "yyyyMMdd'T'HHmmss";
		DateFormat df = new SimpleDateFormat(pattern);
		df.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		try
		{
			time = df.parse(dateStr).getTime();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return time;
	}
}
