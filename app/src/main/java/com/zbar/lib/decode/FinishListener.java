package com.zbar.lib.decode;

import android.app.Activity;
import android.content.DialogInterface;

/**
 * @File @FinishListener.java
 * @Description:
 * @Copyright:WISE. All Rights Reserved
 * @Company: WISE
 * @author LinYongKun
 * @version 1.1
 * @date @下午2:50:24
 */
public final class FinishListener implements DialogInterface.OnClickListener,
		DialogInterface.OnCancelListener, Runnable {

	private final Activity activityToFinish;

	public FinishListener(Activity activityToFinish) {
		this.activityToFinish = activityToFinish;
	}

	public void onCancel(DialogInterface dialogInterface) {
		run();
	}

	public void onClick(DialogInterface dialogInterface, int i) {
		run();
	}

	public void run() {
		activityToFinish.finish();
	}

}
