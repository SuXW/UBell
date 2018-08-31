package com.decoder.util;


public class DecG726 {

   public static final int API_ER_ANDROID_NULL = -10000;
   public static final byte FORMAT_ALAW = 1;
   public static final byte FORMAT_LINEAR = 2;
   public static final byte FORMAT_ULAW = 0;
   public static final int G726_16 = 0;
   public static final int G726_24 = 1;
   public static final int G726_32 = 2;
   public static final int G726_40 = 3;


   static {
      try {
         System.loadLibrary("G726Android");
      } catch (UnsatisfiedLinkError var1) {
         System.out.println("loadLibrary(G726Android)," + var1.getMessage());
      }
   }

   public static native int g726_dec_state_create(byte var0, byte var1);

   public static native void g726_dec_state_destroy();

   public static native int g726_decode(byte[] var0, long var1, byte[] var3, long[] var4);
}
