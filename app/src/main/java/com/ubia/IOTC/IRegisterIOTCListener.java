package com.ubia.IOTC;

import android.graphics.Bitmap;

import com.ubia.IOTC.Camera;

public interface IRegisterIOTCListener {

   void receiveChannelInfo(Camera var1, int var2, int var3);

   void receiveFrameData(Camera var1, int var2, Bitmap var3);

   void receiveFrameInfo(Camera var1, int var2, long var3, int var5, int var6, AVFrame avFrame, int var8);

   void receiveIOCtrlData(Camera var1, int var2, int var3, byte[] var4);

   void receiveSessionInfo(Camera var1, int var2);
   
   void receiveCameraCtl(Camera var1, int var2, int var3, byte[] var4);
}
