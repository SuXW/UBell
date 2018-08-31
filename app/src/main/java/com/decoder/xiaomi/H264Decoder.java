package com.decoder.xiaomi;

import android.graphics.Bitmap;

public class H264Decoder 
{
  static
  {
    System.loadLibrary("h264decoder");
  }

  public static native boolean decode(byte[] paramArrayOfByte, int paramInt, long paramLong);

  public static native int getHeight();

  public static native int getWidth();

  public static native void init();

  public static native void release();

  public static native int toBitmap(Bitmap paramBitmap);
}

/* Location:           D:\nwork\app\apk\dex2jar-0.0.9.13\classes_dex2jar.jar
 * Qualified Name:     com.decoder.xiaomi.H264Decoder
 * JD-Core Version:    0.6.2
 */