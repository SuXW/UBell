package com.mp4;

import java.util.Arrays;

import com.mp4.Bitstream.BitstreamException;

public class Mp4Util {
    private static final int NAL_SPS = 0x07;
    private static final int EXTENDED_SAR = 255;
    private static final byte[] SPSDef = {0x0,0x0,0x0,0x1,0x67,0x42,(byte) 0xe0,0x14,(byte) 0xdb,0x5,0x7};
    
    public static class ModifySPSParam {
        public byte[] mNewSPS = null;
        public int mSPSStart = -1;
        public int mSPSSize = -1;
        
        public ModifySPSParam() {
            
        }
    }
    
    private static class SPSInfo {
        public boolean mHasVui = false;
        public boolean mHasTime = false;
        public int mRemainPos = 0;
        public int mRemainSize = 0;
        
        public SPSInfo() {
            
        }
    }
    
    public static int searchNalHeader(byte[] buffer, int offset, int size) {
        int i = offset;
        int end = offset + size;
        for (; i + 4 < end; i++) {
            if (buffer[i] == 0 && buffer[i + 1] == 0 && buffer[i + 2] == 0
                    && buffer[i + 3] == 1)
                break;
        }

        if (i + 4< end)
            return i;

        return -1;
    }

    public static boolean modifySPS(byte[] frameData, int offset, int size, ModifySPSParam param) {
        if (null == frameData || offset < 0 || size <= 0 || offset + size < 0
                || offset >= frameData.length || size > frameData.length
                || offset + size > frameData.length)
            return false;

        int end = offset + size;
        int spsStart = -1;
        int spsSize = -1;
        int nalStart = offset;
        int nalSize = size;

        while (nalSize > 0) {
            int nalPos = searchNalHeader(frameData, nalStart, nalSize);
            if (spsStart < 0) {
                if (nalPos < 0 || nalPos + 4 > end)
                    break;

                int nalType = frameData[nalPos + 4] & 0x1F;
                if (nalType == NAL_SPS) {
                    spsStart = nalPos;
                }
            } else {
                if (nalPos < 0 || nalPos + 4 > end)
                    break;

                spsSize = nalPos - spsStart;
                break;
            }

            nalSize = nalSize - (nalPos + 4 - nalStart);
            nalStart = nalPos + 4;
        }
        
        if (spsStart >=0 && spsSize < 0)
            return false;

        byte[] newSPS = null;
        try {
            newSPS = genSPS(frameData, spsStart, spsSize);
        } catch (Exception e) {
            // TODO: handle exception
        }
        
//        String tmpString = "";
//        for (int i = 0; i < newSPS.length; i++) {
//            tmpString = tmpString + Integer.toHexString(newSPS[i]) + " ";
//        }
                
        if (null == newSPS)
            return false;
        
        param.mNewSPS = newSPS;
        param.mSPSStart = spsStart;
        param.mSPSSize = spsSize;
        
        return true;
    }
    
    public static int get_bits(Bitstream bs, int bitCount) throws BitstreamException {
        int ret = bs.GetBits(bitCount);
        return ret;
    }
    
    public static int get_bits1(Bitstream bs) throws BitstreamException {
        int ret = bs.GetBits(1);
        return ret;
    }
    
    public static void getSPSInfo(Bitstream bs, int totalBits, SPSInfo info) throws Exception {
        if (null == info)
            return;
        
        info.mHasVui = false;
        info.mHasTime = false;
        info.mRemainPos = 0;
        info.mRemainSize = 0;
        
        // skip 00 00 00 01 67
        bs.GetBits(32);
        bs.GetBits(8);
        
        int profile_idc = 0;
        int level_idc = 0;
        int constraint_set_flags = 0;
        int sps_id = 0;
        
        profile_idc = get_bits(bs, 8);
        constraint_set_flags |= get_bits1(bs) << 0;   //constraint_set0_flag
        constraint_set_flags |= get_bits1(bs) << 1;   //constraint_set1_flag
        constraint_set_flags |= get_bits1(bs) << 2;   //constraint_set2_flag
        constraint_set_flags |= get_bits1(bs) << 3;   //constraint_set3_flag
        get_bits(bs, 4); // reserved
        level_idc= get_bits(bs, 8);
        sps_id= Mp4Avc.h264_ue(bs);
        
        int chroma_format_idc= 0;
        int bit_depth_luma = 0;
        int bit_depth_chroma = 0;
        if(profile_idc >= 100){ //high profile
            int residual_color_transform_flag = 0;
            int transform_bypass = 0;
            int seq_scaling_matrix_present_flag = 0;
            
            chroma_format_idc = Mp4Avc.h264_ue(bs);
            if(chroma_format_idc == 3)
                residual_color_transform_flag = get_bits1(bs);
            bit_depth_luma   = Mp4Avc.h264_ue(bs) + 8;
            bit_depth_chroma = Mp4Avc.h264_ue(bs) + 8;

            transform_bypass = get_bits1(bs);
            seq_scaling_matrix_present_flag = (byte)bs.GetBits(1);
            if (0 != seq_scaling_matrix_present_flag) {
                for (int ix = 0; ix < 8; ix++) {
                    if (0 != bs.GetBits(1)) {
                        Mp4Avc.scaling_list(ix < 6 ? 16 : 64, bs);
                    }
                }
            }
        }else{
            chroma_format_idc= 1;
            bit_depth_luma   = 8;
            bit_depth_chroma = 8;
        }

        int log2_max_frame_num= Mp4Avc.h264_ue(bs) + 4;
        int poc_type= Mp4Avc.h264_ue(bs);

        if(poc_type == 0){ //FIXME #define
            int log2_max_poc_lsb= Mp4Avc.h264_ue(bs) + 4;
        } else if(poc_type == 1){//FIXME #define
            int delta_pic_order_always_zero_flag= get_bits1(bs);
            int offset_for_non_ref_pic= Mp4Avc.h264_se(bs);
            int offset_for_top_to_bottom_field= Mp4Avc.h264_se(bs);
            int poc_cycle_length                = Mp4Avc.h264_ue(bs);

            int offset_for_ref_frame = 0;
            for(int i=0; i<poc_cycle_length; i++)
                offset_for_ref_frame = Mp4Avc.h264_se(bs);
        }

        int ref_frame_count= Mp4Avc.h264_ue(bs);

        int gaps_in_frame_num_allowed_flag= get_bits1(bs);
        int mb_width = Mp4Avc.h264_ue(bs) + 1;
        int mb_height= Mp4Avc.h264_ue(bs) + 1;

        int frame_mbs_only_flag= get_bits1(bs);
        int mb_aff = 0;
        if(0 == frame_mbs_only_flag)
            mb_aff= get_bits1(bs);
        else
            mb_aff= 0;

        int direct_8x8_inference_flag= get_bits1(bs);

        int crop= get_bits1(bs);
        int crop_left  = 0;
        int crop_right = 0;
        int crop_top   = 0;
        int crop_bottom= 0;
        if(0 != crop){
            int crop_vertical_limit   = ((chroma_format_idc & 2) > 0)? 16 : 8;
            int crop_horizontal_limit = chroma_format_idc == 3 ? 16 : 8;
            crop_left  = Mp4Avc.h264_ue(bs);
            crop_right = Mp4Avc.h264_ue(bs);
            crop_top   = Mp4Avc.h264_ue(bs);
            crop_bottom= Mp4Avc.h264_ue(bs);
        }else{
            crop_left  =
            crop_right =
            crop_top   =
            crop_bottom= 0;
        }
        
        info.mRemainPos = totalBits - bs.bits_remain();
        info.mRemainSize = bs.bits_remain();
        int vui_parameters_present_flag= get_bits1(bs);
        if( 0 != vui_parameters_present_flag ) {
            // ================ VuiStart ================
            info.mHasVui = true;
            
            int aspect_ratio_info_present_flag;
            int aspect_ratio_idc;

            aspect_ratio_info_present_flag= get_bits1(bs);

            int sar_num = 0;
            int sar_den = 0;
            if( 0 != aspect_ratio_info_present_flag ) {
                aspect_ratio_idc= get_bits(bs, 8);
                if( aspect_ratio_idc == EXTENDED_SAR ) {
                    sar_num= get_bits(bs, 16);
                    sar_den= get_bits(bs, 16);
                } else {
                    sar_num = sar_den = 0;
                }
            }else{
                sar_num=
                sar_den= 0;
            }
//                    s->avctx->aspect_ratio= sar_width*s->width / (float)(s->height*sar_height);

            if(0 != get_bits1(bs)){      /* overscan_info_present_flag */
                get_bits1(bs);      /* overscan_appropriate_flag */
            }

            int video_signal_type_present_flag = get_bits1(bs);
            if(video_signal_type_present_flag > 0){
                get_bits(bs, 3);    /* video_format */
                int full_range = get_bits1(bs); /* video_full_range_flag */

                int colour_description_present_flag = get_bits1(bs);
                if(colour_description_present_flag > 0){
                    int color_primaries = get_bits(bs, 8); /* colour_primaries */
                    int color_trc       = get_bits(bs, 8); /* transfer_characteristics */
                    int colorspace      = get_bits(bs, 8); /* matrix_coefficients */
                }
            }

            if(0 != get_bits1(bs)){      /* chroma_location_info_present_flag */
                int chroma_sample_location = Mp4Avc.h264_ue(bs)+1;  /* chroma_sample_location_type_top_field */
                Mp4Avc.h264_ue(bs);  /* chroma_sample_location_type_bottom_field */
            }

            info.mRemainPos = totalBits - bs.bits_remain();
            info.mRemainSize = bs.bits_remain();
            int timing_info_present_flag = get_bits1(bs);
            if(0 != timing_info_present_flag) {
                info.mHasTime = true;
                
                int num_units_in_tick = get_bits(bs, 32);
                int time_scale = get_bits(bs, 32);

                int fixed_frame_rate_flag = get_bits1(bs);
            }

            // TODO
        }
    }
    
    public static byte[] genSPS(byte[] frameData, int spsStart, int spsSize) throws Exception {
        if (null == frameData || (spsStart >= 0 && spsSize <= 0))
            return null;

        byte[] newSPS = null;
        int newSPSBits = 0;

        if (spsStart < 0 && spsSize <= 0) {
            //header
            newSPSBits = SPSDef.length*8+4;
            //vui
            newSPSBits = newSPSBits + 1 + 1 + 1 + 1;
            //time
            newSPSBits = newSPSBits + 1 + 32 + 32 + 1;
            
            
            newSPS = new byte[newSPSBits/8+1];
            Arrays.fill(newSPS, (byte) 0);
            
            System.arraycopy(SPSDef, 0, newSPS, 0, SPSDef.length);
            
            Bitstream output = new Bitstream();
            output.init(newSPS, SPSDef.length, newSPSBits-SPSDef.length*8);
            
            output.putBits(4, 0x0c);
            
            // vui
            output.putBits(1, 1);
            output.putBits(4, 0);
            // time
            output.putBits(1, 1);
            output.putBits(32, 0x101);
            output.putBits(32, 0x1000);
            output.putBits(1, 1);

            // TODO
        } else {
            boolean hasTime = false;
            boolean hasVui = false;
            int remainPos = 0;
            //int remainSize = 0;
            int remainBits = 0;
            int remainValue = 0;
            
            int totalBits = spsSize*8;
            Bitstream bs = new Bitstream();
            bs.init(frameData, spsStart, totalBits);
            
            SPSInfo info = new SPSInfo();
            getSPSInfo(bs, totalBits, info);
            
            hasTime = info.mHasTime;
            hasVui = info.mHasVui;
            remainPos = info.mRemainPos;
            //remainSize = info.mRemainSize;
            
            if (hasTime)
                return null;
            
            newSPSBits = remainPos;
            if (!hasVui) {
                newSPSBits = newSPSBits + 1 + 1 + 1 + 1;
            }
            newSPSBits = newSPSBits + 1 + 32 + 32 + 1;
            
            //newSPSBits = newSPSBits + remainSize;
            
            newSPS = new byte[newSPSBits/8+1];
            Arrays.fill(newSPS, (byte) 0);
            
            int copyLen = remainPos/8;
            remainBits = remainPos - copyLen*8;
            remainValue = frameData[spsStart+copyLen];
            remainValue = remainValue>>(8-remainBits);
            System.arraycopy(frameData, spsStart, newSPS, 0, copyLen);
            
            int writeOffset = remainPos/8;
            Bitstream output = new Bitstream();
            output.init(newSPS, writeOffset, newSPS.length - writeOffset);

            output.putBits(remainBits, remainValue);
            if (!hasVui) {
                output.putBits(1, 1);
                output.putBits(4, 0);
            }
            
            output.putBits(1, 1);
            output.putBits(32, 0x101);
            output.putBits(32, 0x1000);
            output.putBits(1, 1);
            
            // TODO
//            while (true) {
//                int remainLen = bs.bits_remain();
//                if (remainLen <= 0)
//                    break;
//                
//                int bitCount = (remainLen > 32) ? 32 : remainLen;
//                int val = bs.GetBits(bitCount);
//                //output.putBits(bitCount, val);
//                output.putBits(bitCount, 0);
//            }
        }

        return newSPS;
    }
}
