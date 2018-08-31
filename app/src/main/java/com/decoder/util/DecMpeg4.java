package com.decoder.util;


public class DecMpeg4 {

   static {
      try {
         System.loadLibrary("FFmpeg");
      } catch (UnsatisfiedLinkError var1) {
         System.out.println("loadLibrary(FFmpeg)," + var1.getMessage());
      }
   }

   public static native int Decode(byte[] var0, int var1, byte[] var2, int[] var3, int[] var4, int[] var5);

   public static native int InitDecoder(int var0, int var1);

   public static native int UninitDecoder();
}
