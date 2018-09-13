package com.ubia.IOTC;


public class Packet {

   public static final int byteArrayToInt_Big(byte[] var0) {
      int var2;
      if(var0.length == 1) {
         var2 = 255 & var0[0];
      } else {
         if(var0.length == 2) {
            return (255 & var0[0]) << 8 | 255 & var0[1];
         }

         int var1 = var0.length;
         var2 = 0;
         if(var1 >= 4) {
            return (255 & var0[0]) << 24 | (255 & var0[1]) << 16 | (255 & var0[2]) << 8 | 255 & var0[3];
         }
      }

      return var2;
   }
   public static final int byteArrayToInt_Little(byte[] var0) {
      int var2;
      if(var0.length == 1) {
         var2 = 255 & var0[0];
      } else {
         if(var0.length == 2) {
            return 255 & var0[0] | (255 & var0[1]) << 8;
         }

         int var1 = var0.length;
         var2 = 0;
         if(var1 == 4) {
            return 255 & var0[0] | (255 & var0[1]) << 8 | (255 & var0[2]) << 16 | (255 & var0[3]) << 24;
         }
      }

      return var2;
   }

   public static final int byteArrayToInt_Little(byte[] var0, int var1) {
      return 255 & var0[var1] | (255 & var0[var1 + 1]) << 8 | (255 & var0[var1 + 2]) << 16 | (255 & var0[var1 + 3]) << 24;
   }

   public static final long byteArrayToLong_Little(byte[] var0, int var1) {
      return (long)(255 & var0[var1] | (255 & var0[var1 + 1]) << 8 | (255 & var0[var1 + 2]) << 16 | (255 & var0[var1 + 3]) << 24 | (255 & var0[var1 + 1]) << 32 | (255 & var0[var1 + 1]) << 40 | (255 & var0[var1 + 1]) << 48 | (255 & var0[var1 + 1]) << 56);
   }

   public static final short byteArrayToShort_Little(byte[] var0, int var1) {
      return (short)(255 & var0[var1] | (255 & var0[var1 + 1]) << 8);


   }

   public static final byte[] intToByteArray_Big(int var0) {
      byte[] var1 = new byte[]{(byte)(var0 >>> 24), (byte)(var0 >>> 16), (byte)(var0 >>> 8), (byte)var0};
      return var1;
   }

   public static final byte[] intToByteArray_Little(int var0) {
      byte[] var1 = new byte[]{(byte)var0, (byte)(var0 >>> 8), (byte)(var0 >>> 16), (byte)(var0 >>> 24)};
      return var1;
   }

   public static final byte[] longToByteArray_Little(long var0) {
      byte[] var2 = new byte[]{(byte)((int)var0), (byte)((int)(var0 >>> 8)), (byte)((int)(var0 >>> 16)), (byte)((int)(var0 >>> 24)), (byte)((int)(var0 >>> 32)), (byte)((int)(var0 >>> 40)), (byte)((int)(var0 >>> 48)), (byte)((int)(var0 >>> 56))};
      return var2;
   }

   public static final byte[] shortToByteArray_Big(short var0) {
      byte[] var1 = new byte[]{(byte)(var0 >>> 8), (byte)var0};
      return var1;
   }

   public static final byte[] shortToByteArray_Little(short var0) {
      byte[] var1 = new byte[]{(byte)var0, (byte)(var0 >>> 8)};
      return var1;
   }

   public static final short byteArrayToShort(byte[] bytes,int offset ){

      return (short)(((bytes[offset]<<8) + bytes[offset+1]));


   }
}
