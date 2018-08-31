package com.decoder.util;


public class DecMp3 {

   static {
      try {
         System.loadLibrary("Mp3Android");
      } catch (UnsatisfiedLinkError var1) {
         System.out.println("loadLibrary(Mp3Android)," + var1.getMessage());
      }
   }

   public static native int Decode(byte[] var0, int var1, byte[] var2);

   public static native int InitDecoder(int var0, int var1);

   public static native int UninitDecoder();
}
