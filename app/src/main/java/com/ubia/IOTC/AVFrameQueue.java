package com.ubia.IOTC;

import java.util.LinkedList;

import android.util.Log;

class AVFrameQueue {
	public volatile LinkedList<AVFrame> listData = new LinkedList<AVFrame>();
	private volatile int mSize = 0;

	// public void addLast(AVFrame avFrame)
	// {
	// while (true)
	// {
	// int i;
	//
	// AVFrame localAVFrame;
	// try
	// {
	// if (this.mSize > 1500)
	// {
	// i = 1;
	// if (!this.listData.isEmpty());
	// }
	// else
	// {
	// this.listData.addLast(avFrame);
	// this.mSize = (1 + this.mSize);
	// return;
	// }
	// localAVFrame = (AVFrame)this.listData.get(0);
	// if (i != 0)
	// {
	// if (localAVFrame.isIFrame())
	// {
	// System.out.println("drop I frame");
	// this.listData.removeFirst();
	// this.mSize = (-1 + this.mSize);
	// // break label149;
	// }
	// System.out.println("drop p frame");
	// continue;
	// }
	// }
	// finally
	// {
	// }
	// if (!localAVFrame.isIFrame())
	// {
	// System.out.println("drop p frame");
	// this.listData.removeFirst();
	// this.mSize = (-1 + this.mSize);
	// i = 0;
	// }
	// }
	// }

	public synchronized void addLast(AVFrame avFrame) {

		boolean isFull = false;
		if (this.mSize > 1500) {
			isFull = true;
		}
		while (isFull) {
			AVFrame localAVFrame = (AVFrame) this.listData.get(0);
			if (localAVFrame.isIFrame()) {
				System.out.println("drop I frame");
				this.listData.removeFirst();
				this.mSize = (-1 + this.mSize);
			} else {
				System.out.println("drop p frame");
				this.listData.removeFirst();
				this.mSize = (-1 + this.mSize);
				isFull = false;
			}
			if (this.mSize == 0) {
				break;
			}
		}
		this.listData.addLast(avFrame);
		this.mSize = (1 + this.mSize);

	}

	public int getCount() {
		try {
			int i = this.mSize;
			return i;
		} finally {
			// localObject = finally;
			// throw localObject;
		}
	}

	public boolean isFirstIFrame() {
		try {
			boolean bool1 = false;
			if ((this.listData != null) && (!this.listData.isEmpty())) {
				boolean bool2 = ((AVFrame) this.listData.get(0)).isIFrame();
				if (bool2) {
					bool1 = true;
					return bool1;
				}
			}

		} finally {
		}
		return false;
	}

	public void removeAll() {
		try {
			if (!this.listData.isEmpty())
				this.listData.clear();
			this.mSize = 0;
			return;
		} finally {
		}
	}
//poll
	public synchronized AVFrame removeHead() {
		AVFrame localAVFrame;
		try {
			int i = this.mSize;

			if (i == 0)
				localAVFrame = null;
			else {
				if (true) {
					localAVFrame = (AVFrame) this.listData.removeFirst();
					this.mSize = (-1 + this.mSize);
				}
			}
		}catch(Exception e){
			return null;
		} finally {
		}
		return localAVFrame;
	}
}
