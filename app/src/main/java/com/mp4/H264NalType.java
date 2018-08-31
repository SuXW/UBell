package com.mp4;

public class H264NalType {
    public static final int H264_NAL_TYPE_NON_IDR_SLICE     = 1;
    public static final int H264_NAL_TYPE_DP_A_SLICE        = 2;
    public static final int H264_NAL_TYPE_DP_B_SLICE        = 3;
    public static final int H264_NAL_TYPE_DP_C_SLICE        = 0x4;
    public static final int H264_NAL_TYPE_IDR_SLICE         = 0x5;
    public static final int H264_NAL_TYPE_SEI               = 0x6;  // 
    public static final int H264_NAL_TYPE_SEQ_PARAM         = 0x7;  // ���в�����
    public static final int H264_NAL_TYPE_PIC_PARAM         = 0x8;  // ͼ�������
    public static final int H264_NAL_TYPE_ACCESS_UNIT       = 0x9;
    public static final int H264_NAL_TYPE_END_OF_SEQ        = 0xa;
    public static final int H264_NAL_TYPE_END_OF_STREAM     = 0xb;
    public static final int H264_NAL_TYPE_FILLER_DATA       = 0xc;
    public static final int H264_NAL_TYPE_SEQ_EXTENSION     = 0xd;
}
