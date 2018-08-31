package com.ubia.vr;


import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import cn.ubia.base.Constants;
import cn.ubia.util.PreferenceUtil;
import cn.ubia.util.StringUtils;

import com.decoder.util.H264Decoder;

/**
 * Created by guoheng on 2016/9/1.
 */
public class SurfaceDecoder {

	private	H264Decoder player;
	public byte[] yuvbuf;
	public int SoftDecoderPrePare()
	{
		try {
			player = new H264Decoder(0);
			if(yuvbuf == null){
				yuvbuf = new byte[1920 * 1080 * 3 / 2];
			}
		}catch ( Exception e)
		{
			e.printStackTrace();
			return -1;
		}
		return 0;

	}

	public  byte[] ppsBuf;
	public  byte[]  getSPSAndPPS(String fileName) throws IOException {

		if(ppsBuf!=null){
			return ppsBuf;
		}else {

			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);

			int fileLength = (int) file.length();
			byte[] fileData = new byte[fileLength];
			fis.read(fileData);

			// 'a'=0x61, 'v'=0x76, 'c'=0x63, 'C'=0x43
			byte[] avcC = new byte[]{0x61, 0x76, 0x63, 0x43};

			// avcC的起始位置
			int avcRecord = 0;
			for (int ix = 0; ix < fileLength; ++ix) {
				if (fileData[ix] == avcC[0] && fileData[ix + 1] == avcC[1]
						&& fileData[ix + 2] == avcC[2]
						&& fileData[ix + 3] == avcC[3]) {
					// 找到avcC，则记录avcRecord起始位置，然后退出循环。
					avcRecord = ix + 4;
					break;
				}
			}
			if (0 == avcRecord) {
				System.out.println("没有找到avcC，请检查文件格式是否正确");
				return null;
			}

			// 加6的目的是为了跳过
			// (1)8字节的 configurationVersion
			// (2)8字节的 AVCProfileIndication
			// (3)8字节的 profile_compatibility
			// (4)8 字节的 AVCLevelIndication
			// (5)6 bit 的 reserved
			// (6)2 bit 的 lengthSizeMinusOne
			// (7)3 bit 的 reserved
			// (8)5 bit 的numOfSequenceParameterSets
			// 共6个字节，然后到达sequenceParameterSetLength的位置
			int spsStartPos = avcRecord + 6;
			byte[] spsbt = new byte[]{fileData[spsStartPos],
					fileData[spsStartPos + 1]};
			int spsLength = bytes2Int(spsbt);
			byte[] SPS = new byte[spsLength];
			// 跳过2个字节的 sequenceParameterSetLength
			spsStartPos += 2;
			// 底下部分为获取PPS
			// spsStartPos + spsLength 可以跳到pps位置
			// 再加1的目的是跳过1字节的 numOfPictureParameterSets
			int ppsStartPos = spsStartPos + spsLength + 1;
			byte[] ppsbt = new byte[]{fileData[ppsStartPos],
					fileData[ppsStartPos + 1]};
			int ppsLength = bytes2Int(ppsbt);
			byte[] PPS = new byte[ppsLength+4];
			ppsStartPos += 2;
			System.arraycopy(fileData, ppsStartPos, PPS, 4, ppsLength);

			ppsBuf = PPS;
			ppsBuf[0] = 0x00;
			ppsBuf[1] = 0x00;
			ppsBuf[2] = 0x00;
			ppsBuf[3] = 0x01;

			return ppsBuf;
		}
	}

	private static int bytes2Int(byte[] bt) {
		int ret = bt[0];
		ret <<= 8;
		ret |= bt[1];
		return ret;
	}


	public Bitmap doExtract(byte[] h264byte, int length, int width2, int height2,
							int IorBFram,GLView glSurfaceView ) throws IOException  {

		if (h264byte != null) {
			int a = player.consumeNalUnitsFromDirectBuffer(h264byte, length, 0L);
			if (a > 0) {
				//VRConfig.width = H264Decoder.getWidth();
				//VRConfig.height = H264Decoder.getHeight();
				//byte[] yuvbuf = new byte[VRConfig.width * VRConfig.height * 3 / 2];
				if(yuvbuf != null){
					long b = (int) player.getYUVData( yuvbuf, yuvbuf.length);
					int width = player.getWidth();
					int height = player.getHeight();
					if(b == 0 && width> 0 && height > 0){
						glSurfaceView.getRenderer().update(width,height, yuvbuf, 19);
					}else{
						Log.e("TESTDECODE","doExtract getYUVData b="+b+" width="+width +" height="+height);
					}
				}else{
					yuvbuf = new byte[1920 * 1080 * 3 / 2];
					Log.e("TESTDECODE","doExtract yuvbuf is null recreated");
				}
				//yuvbuf = null;
			}
			h264byte = null;
		}
//    		boolean inputDone = false;
//    		{ 
//    			// Feed more data to the decoder.

//    						 decoder.releaseOutputBuffer(decoderStatus, doRender);
		return null;
	}


	public void release() {
		if(player!=null )
		{
			player.nativeDestroy();
			player= null;
		}
		yuvbuf = null;
		System.gc();
	}
	public static void delloc(){

	}
}
