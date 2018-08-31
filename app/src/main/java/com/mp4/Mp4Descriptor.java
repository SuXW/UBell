package com.mp4;

import java.util.Arrays;
import java.util.Vector;

public class Mp4Descriptor {
    protected byte fSize; // /< ��� Descriptor �����ݵĳ���
    protected byte[] fBuffer = null; // /< ��� Descriptor �����ݻ�����

    protected int fType; // /< ��� Descriptor ������
    protected Vector<Mp4Property> fProperties; // /< ��� Descriptor �������б�
    protected Vector<Mp4Descriptor> fDescriptors; // /< �� Descriptor �б�.
    public static final int MP4_MPEG4_AUDIO_TYPE = 0x40;
    public static final int MP4_AUDIOSTREAMTYPE = 0x15;

    Mp4Descriptor(int type) {
        fProperties = new Vector<Mp4Property>();
        fDescriptors = new Vector<Mp4Descriptor>();
        fBuffer = new byte[256];
        fType = type;
        fSize = 0;

        if (fType == Mp4DescriptorType.Mp4ESDescrTag) {
            fProperties.add(new Mp4Property(Mp4PropertyType.IntegerProperty, 1,
                    "objectTypeId"));
            fProperties.add(new Mp4Property(Mp4PropertyType.IntegerProperty, 1,
                    "streamType"));
            fProperties.add(new Mp4Property(Mp4PropertyType.IntegerProperty, 3,
                    "bufferSize"));
            fProperties.add(new Mp4Property(Mp4PropertyType.IntegerProperty, 4,
                    "maxBitrate"));
            fProperties.add(new Mp4Property(Mp4PropertyType.IntegerProperty, 4,
                    "avgBitrate"));
        }
    }

    byte GetSize() {
        return fSize;
    }

    void SetSize(byte size) {
        fSize = size;
    }

    int GetType() {
        return fType;
    }

    int WriteMpegLength(int length, int countIn) {
        int count = countIn;
        fBuffer[count++] = (byte) 0x80; // size
        fBuffer[count++] = (byte) 0x80; // size
        fBuffer[count++] = (byte) 0x80; // size
        fBuffer[count++] = (byte) length; // size
        return count;
    }

    int WriteInt(int value, int size, int countIn) {
        int count = countIn;
        for (int i = size - 1; i >= 0; i--) {
            fBuffer[count++] = (byte) ((value >> (i * 8)) & 0xFF);
        }
        return count;
    }

    int Write(Mp4File file) {
        int count = 0;
        Arrays.fill(fBuffer, (byte) 0);

        if (fType == Mp4DescriptorType.Mp4ESDescrTag) {
            fBuffer[count++] = (byte) Mp4DescriptorType.Mp4ESDescrTag;
            count = WriteMpegLength(0x22, count);

            // ESID
            fBuffer[count++] = (byte) 0x00;
            fBuffer[count++] = (byte) 0x00;

            // flags
            fBuffer[count++] = (byte) 0x00;

            fBuffer[count++] = (byte) Mp4DescriptorType.Mp4DecConfigDescrTag;
            count = WriteMpegLength(0x14, count);
            count = WriteInt(MP4_MPEG4_AUDIO_TYPE, 1, count);
            count = WriteInt(MP4_AUDIOSTREAMTYPE, 1, count);

            // buffer size DB
            count = WriteInt(GetPropertyValue("bufferSize"), 3, count);
            count = WriteInt(GetPropertyValue("maxBitrate"), 4, count);
            count = WriteInt(GetPropertyValue("avgBitrate"), 4, count);

            fBuffer[count++] = Mp4DescriptorType.Mp4DecSpecificDescrTag;
            count = WriteMpegLength(0x02, count);

//            fBuffer[count++] = (byte) 0x11;
//            fBuffer[count++] = (byte) 0x90;
            fBuffer[count++] = (byte) 0x14;
            fBuffer[count++] = (byte) 0x08;
            fBuffer[count++] = Mp4DescriptorType.Mp4SLConfigDescrTag;
            count = WriteMpegLength(0x01, count);

            fBuffer[count++] = (byte) 0x02;

            fSize = (byte)(count - 2);
            file.WriteBytes(fBuffer, count);

        } else if (fType == Mp4DescriptorType.Mp4FileIODescrTag) {
            fBuffer[count++] = Mp4DescriptorType.Mp4FileIODescrTag;
            count = WriteMpegLength(0x07, count);

            fBuffer[count++] = (byte) 0x00;
            fBuffer[count++] = (byte) 0x4F;
            fBuffer[count++] = (byte) 0xFF;
            fBuffer[count++] = (byte) 0xFF;

            fBuffer[count++] = (byte) 0x0F;
            fBuffer[count++] = (byte) 0x7F;
            fBuffer[count++] = (byte) 0xFF;

            fSize = (byte)(count - 2);
            file.WriteBytes(fBuffer, count);
        }
        return Mp4ErrorCode.MP4_S_OK;
    }

    int Read(Mp4File file) {
        return Mp4ErrorCode.MP4_S_OK;
    }

    Mp4Property GetProperty(String name) {
        if (name == null || 0 == name.length()) {
            return null;
        }

        int count = fProperties.size();
        for (int i = 0; i < count; i++) {
            Mp4Property property = fProperties.get(i);
            if (null != property && null != property.GetName()
                    && name.equalsIgnoreCase(property.GetName()))
                return property;

        }

        return null;
    }

    int GetPropertyValue(String name) {
        Mp4Property property = GetProperty(name);
        if (null != property) {
            return (int) property.GetIntValue();
        }
        return 0;
    }
}
