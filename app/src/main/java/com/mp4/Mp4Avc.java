package com.mp4;

import com.mp4.Bitstream.BitstreamException;

public class Mp4Avc {
    public static final byte[] exp_golomb_bits = {
        8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 
        3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 
        2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
        2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
        1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 
    };
    
    public static final byte[] ff_ue_golomb_len = { 1, 3, 3, 5, 5, 5, 5, 7, 7,
            7, 7, 7, 7, 7, 7, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9,
            11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11,
            11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 13, 13,
            13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
            13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
            13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
            13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 17, };

    
    public static int h264_ue(Bitstream bs) throws BitstreamException
    {
        int read = 0;
        int bits = 0;
        boolean done = false;

        // we want to read 8 bits at a time - if we don't have 8 bits, 
        // read what's left, and shift.  The exp_golomb_bits calc remains the
        // same.
        while (done == false) {
            int bits_left = bs.bits_remain();
            if (bits_left < 8) {
                read = bs.PeekBits(bits_left) << (8 - bits_left);
                done = true;
            } else {
                read = bs.PeekBits(8);
                if (read == 0) {
                    bs.GetBits(8);
                    bits += 8;
                } else {
                    done = true;
                }
            }
        }

        byte coded = exp_golomb_bits[read];
        bs.GetBits(coded);
        bits += coded;
        
        //  printf("ue - bits %d\n", bits);
        return bs.GetBits(bits + 1) - 1;
    }
    
    public static int h264_se (Bitstream bs) throws BitstreamException 
    {
        int ret;
        ret = h264_ue(bs);
        if ((ret & 0x1) == 0) {
            ret >>= 1;
            int temp = 0 - ret;
            return temp;
        } 
        return (ret + 1) >> 1;
    }

    public static void h264_set_us(Bitstream bs, int i) throws Exception
    {
        if(i<256) {
            bs.putBits(ff_ue_golomb_len[i], i+1);
        }
        
        // TODO
    }
    
    public static void scaling_list (int sizeOfScalingList, Bitstream bs) throws BitstreamException
    {
        int lastScale = 8, nextScale = 8;
        int j;
        
        for (j = 0; j < sizeOfScalingList; j++) {
            if (nextScale != 0) {
                int deltaScale = h264_se(bs);
                nextScale = (lastScale + deltaScale + 256) % 256;
            }
            if (nextScale == 0) {
                lastScale = lastScale;
            } else {
                lastScale = nextScale;
            }
        }
    }
    
    static int h264_read_seq_info (byte[] buffer, int offset, int buflen, H264SeqParams dec)
    {
        Bitstream bs = new Bitstream();   
        int header = (buffer[offset+2] == 0x01) ? 4 : 5;
        bs.init(buffer, offset+header, (buflen - header) * 8);
        
        try {
            dec.profile = (byte) bs.GetBits(8);
            bs.GetBits(1 + 1 + 1 + 1 + 4);
            dec.level = (byte) bs.GetBits(8);
            
            h264_ue(bs); // seq_parameter_set_id
            if (dec.profile == 100 || dec.profile == 110 ||
                dec.profile == 122 || dec.profile == 144) {
                dec.chroma_format_idc = h264_ue(bs);
                if (dec.chroma_format_idc == 3) {
                    dec.residual_colour_transform_flag = (byte)bs.GetBits(1);
                }
                dec.bit_depth_luma_minus8 = h264_ue(bs);
                dec.bit_depth_chroma_minus8 = h264_ue(bs);
                dec.qpprime_y_zero_transform_bypass_flag = (byte)bs.GetBits(1);
                dec.seq_scaling_matrix_present_flag = (byte)bs.GetBits(1);
                if (0 != dec.seq_scaling_matrix_present_flag) {
                    for (int ix = 0; ix < 8; ix++) {
                        if (0 != bs.GetBits(1)) {
                            scaling_list(ix < 6 ? 16 : 64, bs);
                        }
                    }
                }
            }

            dec.log2_max_frame_num_minus4 = h264_ue(bs);
            dec.pic_order_cnt_type = h264_ue(bs);
            if (dec.pic_order_cnt_type == 0) {
                dec.log2_max_pic_order_cnt_lsb_minus4 = h264_ue(bs);
                
            } else if (dec.pic_order_cnt_type == 1) {
                dec.delta_pic_order_always_zero_flag = (byte)bs.GetBits(1);
                dec.offset_for_non_ref_pic = h264_se(bs); // offset_for_non_ref_pic
                dec.offset_for_top_to_bottom_field = h264_se(bs); // offset_for_top_to_bottom_field
                dec.pic_order_cnt_cycle_length = h264_ue(bs); // poc_cycle_length
                for (int ix = 0; ix < dec.pic_order_cnt_cycle_length; ix++) {
                    dec.offset_for_ref_frame[Math.min(ix,255)] = (short) h264_se(bs); // offset for ref fram -
                }
            }

            h264_ue(bs); // num_ref_frames
            bs.GetBits(1); // gaps_in_frame_num_value_allowed_flag
            int PicWidthInMbs = h264_ue(bs) + 1;
            dec.pic_width = PicWidthInMbs * 16;
            int PicHeightInMapUnits = h264_ue(bs) + 1;
            
            dec.frame_mbs_only_flag = (byte) bs.GetBits(1);
            dec.pic_height = (2 - dec.frame_mbs_only_flag) * PicHeightInMapUnits * 16;
            
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }
    
    public static int h264_read_slice_info (byte[] buffer, int offset, int buflen, H264SeqParams seq, H264SliceHeader dec)
    {   
        Bitstream bs = new Bitstream();
        int header = (buffer[offset+2] == 1) ? 4 : 5;
        bs.init(buffer, offset+header, Math.min(buflen - header, 512) * 8);
        try {
            dec.field_pic_flag         = 0;
            dec.bottom_field_flag      = 0;
            dec.delta_pic_order_cnt[0] = 0;
            dec.delta_pic_order_cnt[1] = 0;
            
            h264_ue(bs);                   // first_mb_in_slice    ue(v)
            dec.slice_type = h264_ue(bs); // slice type           ue(v)
            h264_ue(bs);                       // pic_parameter_set_id ue(v)
            dec.frame_num = bs.GetBits(seq.log2_max_frame_num_minus4 + 4); // frame_num u(v)
            
            if (0 == seq.frame_mbs_only_flag) {
                dec.field_pic_flag = (byte) bs.GetBits(1);        // field_pic_flag    u(1)
                if (0 != dec.field_pic_flag) {
                    dec.bottom_field_flag = (byte) bs.GetBits(1); // bottom_field_flag u(1)
                }
            }
            
            if (dec.nal_unit_type == H264NalType.H264_NAL_TYPE_IDR_SLICE) {
                dec.idr_pic_id = h264_ue(bs);     // idr_pic_id   ue(v)
            }
            
            switch (seq.pic_order_cnt_type) {
            case 0:
                // pic_order_cnt_lsb    u(v)
                dec.pic_order_cnt_lsb = bs.GetBits(seq.log2_max_pic_order_cnt_lsb_minus4 + 4);
                if (0 != seq.pic_order_present_flag && 0 == dec.field_pic_flag) {
                    dec.delta_pic_order_cnt_bottom = h264_se(bs); // se(v)
                }
                break;
            case 1:
                if (0 == seq.delta_pic_order_always_zero_flag) {
                    dec.delta_pic_order_cnt[0] = h264_se(bs);
                }
                
                if (0 != seq.pic_order_present_flag && 0 == dec.field_pic_flag) {
                    dec.delta_pic_order_cnt[1] = h264_se(bs);
                }
                break;
            }
            
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }
}
