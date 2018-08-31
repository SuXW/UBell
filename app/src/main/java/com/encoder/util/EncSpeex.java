package com.encoder.util;


public class EncSpeex
{
  static
  {
    try
    {
      System.loadLibrary("SpeexAndroid");
      //return;
    }
    catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
    {
      System.out.println("loadLibrary(SpeexAndroid)," + localUnsatisfiedLinkError.getMessage());
    }
  }

  public static native int Encode(short[] paramArrayOfShort, int paramInt, byte[] paramArrayOfByte);

  public static native int InitEncoder(int paramInt);

  public static native int UninitEncoder();
}

/* Location:           D:\nwork\app\apk\dex2jar-0.0.9.13\classes_dex2jar.jar
 * Qualified Name:     com.encoder.util.EncSpeex
 * JD-Core Version:    0.6.2
 */