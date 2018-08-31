/**
 * 
 */
/**
 * @author maxwell
 *
 */
package com.ubia.IOTC;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class FdkAACCodec {
	public static FdkAACCodec g_fdkAACCodec = null;

	public static FdkAACCodec getInstance() {
		if (g_fdkAACCodec == null) {
			g_fdkAACCodec = new FdkAACCodec();
		}
		return g_fdkAACCodec;
	}

	public native static int   initDecoder();
	public native static void  exitDecoder();
	
	public native static int decodeFrame(byte[] outputArr, int outputSize, byte[] inputArr, int inputSize);
	
	public native static int initEncoder();
	public native static void exitEncoder();
	
	public native static int encodeFrame(byte[] outputArr, int outputSize, byte[] inputArr, int inputSize);
				


}
