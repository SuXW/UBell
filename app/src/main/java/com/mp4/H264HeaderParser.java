package com.mp4;

public class H264HeaderParser {
    public class SliceType {
        byte mType = 0;
    }
    
    public H264SliceHeader fSliceHeader = new H264SliceHeader();   ///< ��ǰ������ͷ��Ϣ
    public H264SeqParams   fSeqParams = new H264SeqParams();     ///< ��ǰ���в�����
    
    public H264HeaderParser()
    {
        Clear();
    }

    public static byte GetNaluType (byte[] buffer, int offset)
    {
        int typeOffset = (buffer[offset+2] == 1) ? 3 : 4;
        return (byte)(buffer[offset+typeOffset] & 0x1f);
    }
    
    public static byte GetNaluRefIdc(byte[] buffer, int offset) {
    	int typeOffset = (buffer[offset+2] == 1) ? 3 : 4;
        return (byte)((buffer[offset+typeOffset]>>5) & 0x07);
	}

    int GetSliceType (byte[] buffer, int buflen, SliceType slice_type, boolean noheader)
    {
        int header = 0;
        if (noheader) {
            header = 1;
        } else {
            if (buffer[2] == 1) {
                header = 4;
            } else {
                header = 5;
            }
        }

        Bitstream bs = new Bitstream();
        bs.init(buffer, header, (buflen - header) * 8);
        try {
            Mp4Avc.h264_ue(bs);               // first_mb_in_slice
            slice_type.mType = (byte)Mp4Avc.h264_ue(bs); // slice type
            Mp4Avc.h264_ue(bs);               // pic_parameter_set_id ue(v)
            return bs.GetBits(0 + 4);   // frame_num u(v)
        } catch (Exception e) {
            return -1;
        }
    }

    public int ParseHeader( byte[] sample, int offset, int length )
    {
        int ret = 0;
        byte type = GetNaluType(sample, offset);
        boolean isSlice = false;
        switch (type) {
        case H264NalType.H264_NAL_TYPE_ACCESS_UNIT:
        case H264NalType.H264_NAL_TYPE_END_OF_SEQ:
        case H264NalType.H264_NAL_TYPE_END_OF_STREAM:
            ret = 1;
            break;
        case H264NalType.H264_NAL_TYPE_SEQ_PARAM:
            if (Mp4Avc.h264_read_seq_info(sample, offset, length, fSeqParams) < 0) {
                return -1;
            }
            break;
        case H264NalType.H264_NAL_TYPE_NON_IDR_SLICE:
        case H264NalType.H264_NAL_TYPE_DP_A_SLICE:
        case H264NalType.H264_NAL_TYPE_DP_B_SLICE:
        case H264NalType.H264_NAL_TYPE_DP_C_SLICE:
        case H264NalType.H264_NAL_TYPE_IDR_SLICE:
            isSlice = true;
            break;
        }

        fSliceHeader.nal_unit_type = type;
        fSliceHeader.is_slice = isSlice;

        if (isSlice) {
            if (Mp4Avc.h264_read_slice_info(sample, offset, length, fSeqParams, fSliceHeader) < 0) {
                return -1;
            }
        }

        return 0;
    }

    public void Clear()
    {
        fSliceHeader.clear();
        fSeqParams.clear();
    }
}
