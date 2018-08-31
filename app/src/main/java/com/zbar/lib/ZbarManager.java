package com.zbar.lib;

/**
 * @File @ZbarManager.java
 * @Description:
 * @Copyright:WISE. All Rights Reserved
 * @Company: WISE
 * @author LinYongKun
 * @version 1.1
 * @date @下午2:50:42
 */
public class ZbarManager {

	static {
		System.loadLibrary("zbar");
	}

	public native String decode(byte[] data, int width, int height,
			boolean isCrop, int x, int y, int cwidth, int cheight);
}
