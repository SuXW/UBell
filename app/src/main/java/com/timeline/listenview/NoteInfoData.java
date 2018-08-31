package com.timeline.listenview;

import android.util.Log;

import com.ubia.IOTC.Packet;

public class NoteInfoData {

	public NoteInfoData(byte[] data) {
		this.channel = Packet.byteArrayToInt_Little(data, 0);
		this.starttime = Packet.byteArrayToInt_Little(data, 8);
		this.endtime = Packet.byteArrayToInt_Little(data, 12);
		this.validbits  = Packet.byteArrayToShort_Little(data, 16);
		this.bytes  = Packet.byteArrayToShort_Little(data, 18); 
		this.unit = (byte) data[6];
		System.arraycopy(data, 20, dataBitMap, 0, bytes);
		for(int i =0;i<bytes;i++){
			dataBitMapValue = (byte) (dataBitMap[i]|dataBitMapValue);
		}



		Log.d("NoteInfoData","NoteInfoData ToString:     this.starttime :"+this.starttime
				+"   this.endtime= "+this.endtime
				+"   this.validbits= "+this.validbits
				+"   this.bytes= "+this.bytes
				+"     dataBitMapValue:"+dataBitMapValue);
		
	}
	public int channel; // Camera Index
	public byte IOProtoVer;
	public byte event;
	public byte unit; // 1-60:1-60sec, 61-120:1-60min,
						// 121-144:1-24hrs,145-176:1-31days
	public byte index;
	public int starttime;
	public int endtime;
	public short validbits; // up to 1004*8 bits
	public short bytes; // up to 1004 bytes append to this msg
	public byte dataBitMap[] = new byte[1280];
	public byte dataBitMapValue = 0;

}
