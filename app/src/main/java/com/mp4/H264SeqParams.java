package com.mp4;

import java.util.Arrays;

public class H264SeqParams {
    byte profile;
    byte level;
    int chroma_format_idc;
    byte residual_colour_transform_flag;
    int bit_depth_luma_minus8;
    int bit_depth_chroma_minus8;
    byte qpprime_y_zero_transform_bypass_flag;
    byte seq_scaling_matrix_present_flag;
    int log2_max_frame_num_minus4;
    int log2_max_pic_order_cnt_lsb_minus4;
    int pic_order_cnt_type;
    byte pic_order_present_flag;
    byte delta_pic_order_always_zero_flag;
    int  offset_for_non_ref_pic;
    int  offset_for_top_to_bottom_field;
    int pic_order_cnt_cycle_length;
    short[] offset_for_ref_frame = new short[256];
    int pic_width;             ///< ��Ƶ�Ŀ��
    int pic_height;            ///< ��Ƶ�ĸ߶�
    byte frame_mbs_only_flag;   ///< �Ƿ�ֻ������֡
    
    void clear() {
        profile = 0;
        level = 0;
        chroma_format_idc = 0;
        residual_colour_transform_flag = 0;
        bit_depth_luma_minus8 = 0;
        bit_depth_chroma_minus8 = 0;
        qpprime_y_zero_transform_bypass_flag = 0;
        seq_scaling_matrix_present_flag = 0;
        log2_max_frame_num_minus4 = 0;
        log2_max_pic_order_cnt_lsb_minus4 = 0;
        pic_order_cnt_type = 0;
        pic_order_present_flag = 0; 
        delta_pic_order_always_zero_flag = 0;
        offset_for_non_ref_pic = 0;
        offset_for_top_to_bottom_field = 0;
        pic_order_cnt_cycle_length = 0;
        Arrays.fill(offset_for_ref_frame, (short)0);
        pic_width = 0;
        pic_height = 0;
        frame_mbs_only_flag = 0;
    }
}
