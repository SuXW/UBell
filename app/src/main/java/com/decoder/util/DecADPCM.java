package com.decoder.util;


public class DecADPCM {

   static {
      try {
         System.loadLibrary("ADPCMAndroid");
      } catch (UnsatisfiedLinkError var1) {
         System.out.println("loadLibrary(ADPCMAndroid)," + var1.getMessage());
      }
   }

   public static native int Decode(byte[] var0, int var1, byte[] var2);

   public static native int ResetDecoder();
}
