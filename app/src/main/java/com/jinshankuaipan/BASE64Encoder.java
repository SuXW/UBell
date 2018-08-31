// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   BASE64Encoder.java

package com.jinshankuaipan;


public class BASE64Encoder
{

	private static final char last2byte = (char)Integer.parseInt("00000011", 2);
	private static final char last4byte = (char)Integer.parseInt("00001111", 2);
	private static final char last6byte = (char)Integer.parseInt("00111111", 2);
	private static final char lead6byte = (char)Integer.parseInt("11111100", 2);
	private static final char lead4byte = (char)Integer.parseInt("11110000", 2);
	private static final char lead2byte = (char)Integer.parseInt("11000000", 2);
	private static final char encodeTable[] = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
		'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 
		'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 
		'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 
		'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', 
		'8', '9', '+', '/'
	};

	public BASE64Encoder()
	{
	}

	public String encode(byte from[])
	{
		StringBuffer to = new StringBuffer((int)((double)from.length * 1.3400000000000001D) + 3);
		int num = 0;
		char currentByte = '\0';
		for (int i = 0; i < from.length; i++)
			for (num %= 8; num < 8; num += 6)
			{
				switch (num)
				{
				case 1: // '\001'
				case 3: // '\003'
				case 5: // '\005'
				default:
					break;

				case 0: // '\0'
					currentByte = (char)(from[i] & lead6byte);
					currentByte >>>= '\002';
					break;

				case 2: // '\002'
					currentByte = (char)(from[i] & last6byte);
					break;

				case 4: // '\004'
					currentByte = (char)(from[i] & last4byte);
					currentByte <<= '\002';
					if (i + 1 < from.length)
						currentByte |= (from[i + 1] & lead2byte) >>> 6;
					break;

				case 6: // '\006'
					currentByte = (char)(from[i] & last2byte);
					currentByte <<= '\004';
					if (i + 1 < from.length)
						currentByte |= (from[i + 1] & lead4byte) >>> 4;
					break;
				}
				to.append(encodeTable[currentByte]);
			}


		if (to.length() % 4 != 0)
		{
			for (int i = 4 - to.length() % 4; i > 0; i--)
				to.append("=");

		}
		return to.toString();
	}

}
