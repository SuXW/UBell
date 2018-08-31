package com.decoder.util;


public class DecSpeex {

   static {
      try {
         System.loadLibrary("SpeexAndroid");
      } catch (UnsatisfiedLinkError var1) {
         System.out.println("loadLibrary(SpeexAndroid)," + var1.getMessage());
      }
   }

   public static native int Decode(byte[] var0, int var1, short[] var2);

   public static native int InitDecoder(int var0);

   public static native int UninitDecoder();
}
