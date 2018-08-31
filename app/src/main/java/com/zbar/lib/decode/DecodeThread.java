package com.zbar.lib.decode;

import java.util.concurrent.CountDownLatch;

import com.zbar.lib.CaptureActivity;

import android.os.Handler;
import android.os.Looper;

/**
 * @File @DecodeThread.java
 * @Description:
 * @Copyright:WISE. All Rights Reserved
 * @Company: WISE
 * @author LinYongKun
 * @version 1.1
 * @date @下午2:50:06
 */
final class DecodeThread extends Thread {

	CaptureActivity activity;
	private Handler handler;
	private final CountDownLatch handlerInitLatch;

	DecodeThread(CaptureActivity activity) {
		this.activity = activity;
		handlerInitLatch = new CountDownLatch(1);
	}

	Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new DecodeHandler(activity);
		handlerInitLatch.countDown();
		Looper.loop();
	}

}
