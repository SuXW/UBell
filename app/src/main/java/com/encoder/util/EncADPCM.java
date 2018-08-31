package com.encoder.util;


public class EncADPCM
{
  static
  {
    try
    {
      System.loadLibrary("ADPCMAndroid");
      //return;
    }
    catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
    {
      System.out.println("loadLibrary(ADPCMAndroid)," + localUnsatisfiedLinkError.getMessage());
    }
  }

  public static native int Encode(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2);

  public static native int ResetEncoder();
}

/* Location:           D:\nwork\app\apk\dex2jar-0.0.9.13\classes_dex2jar.jar
 * Qualified Name:     com.encoder.util.EncADPCM
 * JD-Core Version:    0.6.2
 */