package com.hikvh.media.amr;
 
public class AmrDecoder {

    public static native void init();

    public static native void exit();

    public static native int decode(byte[] in, short[] out);

 
	static {
		try {
			System.loadLibrary("amr-codec");
		} catch (UnsatisfiedLinkError var1) {
			System.out.println("amr-codec," + var1.getMessage());
		}
	}
}
