package com.mp4;

import java.util.Arrays;

public class H264SliceHeader {
    public boolean is_slice;              ///< ��ǰ��Ԫ�Ƿ�����������
    public byte nal_unit_type;         ///< ��ǰ��Ԫ������
    public int slice_type;            ///< ��ǰ����������
    public byte field_pic_flag;
    public byte bottom_field_flag;
    public int frame_num;             ///< ֡����
    public int idr_pic_id;
    public int pic_order_cnt_lsb;
    public int  delta_pic_order_cnt_bottom;
    public int[]  delta_pic_order_cnt = new int[2];
    
    void clear() {
        is_slice = false;
        nal_unit_type = 0;
        slice_type = 0;
        field_pic_flag = 0;
        bottom_field_flag = 0;
        frame_num = 0;
        idr_pic_id = 0;
        pic_order_cnt_lsb = 0;
        delta_pic_order_cnt_bottom = 0;
        Arrays.fill(delta_pic_order_cnt, 0);
    }
}
