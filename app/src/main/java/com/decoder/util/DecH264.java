package com.decoder.util;


public class DecH264 {

   static {
      try {
         System.loadLibrary("H264Android");
      } catch (UnsatisfiedLinkError var1) {
         System.out.println("loadLibrary(H264Android)," + var1.getMessage());
      }
   }

   public static native int DecoderNal(byte[] var0, int var1, int[] var2, byte[] var3, boolean var4);

   public static native int InitDecoder();

   public static native int UninitDecoder();
}
