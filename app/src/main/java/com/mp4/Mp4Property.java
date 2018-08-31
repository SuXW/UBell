package com.mp4;

import java.util.Arrays;

public class Mp4Property {
    protected int fType; // /< ���Ե�����
    protected String fName; // /< ��������
    protected int fSize; // /< ֵ�ĳ���.
    protected int fExpectSize; // /< �����Ĵ�С�򳤶�

    protected long fIntValue; // /< ����ֵ
    protected float fFloatValue; // /< ������ֵ
    protected byte[] fBytesValue; // /< �ַ���/�ֽ�/��������ֵ
    protected String fStringValue; // /< �ַ���/�ֽ�/��������ֵ

    public Mp4Property(int type, int size, String name) {
        fType = type;
        fSize = size;
        fIntValue = 0;
        fFloatValue = 0;
        fBytesValue = null;
        fStringValue = null;
        fExpectSize = 0;

        if (name == null)
            fName = null;
        else
            fName = new String(name);

        if (type == Mp4PropertyType.BytesProperty) {
            if (size > 0) {
                fBytesValue = new byte[size + 1];
                Arrays.fill(fBytesValue, (byte) 0);
            }
        }
    }

    /** ����������Ե�����. */
    int GetType() {
        return fType;
    }

    String GetName() {
        return fName;
    }

    /** �������������ռ�õĿռ�, ��λΪ�ֽ�. ���� 0 ��ʾ��ȷ��. */
    int GetSize() {
        return fSize;
    }

    byte[] GetBytes() {
        if (fType == Mp4PropertyType.StringProperty) {
            if (null == fStringValue)
                fStringValue = new String("                ");
            
            return fStringValue.getBytes();
        }

        return fBytesValue;
    }

    void SetStringValue(String value) {
        if (fType != Mp4PropertyType.StringProperty || null == value) {
            return;
        }

        fStringValue = value;
    }

    String GetStringValue() {
        return fStringValue;
    }
    
    int GetStringValue( byte[] buf, int bufLen )
    {
        if (buf == null || bufLen <= 1) {
            return 0;
        }
        
        Arrays.fill(buf, (byte)0);
        if (null != fStringValue) {
            fStringValue.getBytes(0, bufLen-1, buf, 0);
            return fSize;
        }

        return 0;
    }

    void SetBytesValue(byte[] bytes, int count) {
        if (bytes == null || count <= 0) {
            return;
        }

        if (count > fSize) {
            return;
        }

        if (fBytesValue != null) {
            Arrays.fill(fBytesValue, (byte)0);
            System.arraycopy(bytes, 0, fBytesValue, 0, count);
        }
    }

    long GetIntValue() {
        if (fType == Mp4PropertyType.FloatProperty) {
            return (long) fFloatValue;
        }
        return fIntValue;
    }

    void SetIntValue(long value) {
        if (fType == Mp4PropertyType.FloatProperty) {
            fFloatValue = value;
        }
        fIntValue = value;
    }

    float GetFloatValue() {
        if (fType == Mp4PropertyType.IntegerProperty) {
            return fIntValue;
        }
        return fFloatValue;
    }

    void SetFloatValue(float value) {
        if (fType == Mp4PropertyType.IntegerProperty) {
            fIntValue = (long) value;
        }
        fFloatValue = value;
    }

    int Read(Mp4File file) {
        if (file == null) {
            return Mp4ErrorCode.MP4_ERR_FAILED;
        }

        switch (fType) {
        case Mp4PropertyType.IntegerProperty:
            SetIntValue(file.ReadInt(fSize));
            break;

        case Mp4PropertyType.BitsProperty:
            SetIntValue(file.ReadBits(fSize));
            break;

        case Mp4PropertyType.FloatProperty:
            if (fSize == 4) {
                // 32
                short iPart = (short) file.ReadInt(2);
                short fPart = (short) file.ReadInt(2);
                float value = iPart + (((float) fPart) / 0x10000);
                SetFloatValue(value);

            } else if (fSize == 2) {
                // 16
                byte iPart = (byte) file.ReadInt(1);
                byte fPart = (byte) file.ReadInt(1);
                float value = iPart + (((float) fPart) / 0x100);
                SetFloatValue(value);

            } else {
                return Mp4ErrorCode.MP4_ERR_FAILED;
            }
            break;

        case Mp4PropertyType.StringProperty: {
            int size = fSize;
            if (size == 0) {
                size = fExpectSize;
            }

            if (size <= 0) {
                return Mp4ErrorCode.MP4_ERR_FAILED;
            }

            byte[] data = new byte[size + 1];

            file.ReadBytes(data, size);
            data[size] = '\0';
            SetStringValue(new String(data));

            if (fSize == 0) {
                fSize = size;
            }
        }
            break;

        case Mp4PropertyType.BytesProperty: {
            int size = fSize;
            if (size <= 0) {
                return Mp4ErrorCode.MP4_ERR_FAILED;
            }
            byte[] data = new byte[size + 1];

            file.ReadBytes(data, size);
        }
            break;
        case Mp4PropertyType.TableProperty:
        case Mp4PropertyType.DescriptorProperty:
        case Mp4PropertyType.IntegerArrayProperty:
        case Mp4PropertyType.SizeTableProperty:
            break;
        }

        return Mp4ErrorCode.MP4_S_OK;
    }

    int Write(Mp4File file) {
        if (file == null) {
            return Mp4ErrorCode.MP4_ERR_FAILED;
        }
        float value = (float) 0.0;

        switch (fType) {
        case Mp4PropertyType.IntegerProperty:
            file.WriteInt(GetIntValue(), fSize);
            break;

        case Mp4PropertyType.BitsProperty:
            file.WriteBits(GetIntValue(), fSize);
            break;

        case Mp4PropertyType.FloatProperty:
            value = GetFloatValue();
            if (fSize == 4) {
                short iPart = (short) value;
                short fPart = (short) ((value - iPart) * 0x10000);
                file.WriteInt(iPart, 2);
                file.WriteInt(fPart, 2);
            } else if (fSize == 2) {
                byte iPart = (byte) value;
                byte fPart = (byte) ((value - iPart) * 0x100);
                file.WriteInt(iPart, 1);
                file.WriteInt(fPart, 1);
            }
            break;

        case Mp4PropertyType.StringProperty: {
            int size = fSize;
            byte[] bytes = GetBytes();
            file.WriteBytes(bytes, size);
        }

            break;
        case Mp4PropertyType.BytesProperty:
            file.WriteBytes(GetBytes(), fSize);
            break;
        case Mp4PropertyType.TableProperty:
        case Mp4PropertyType.DescriptorProperty:
        case Mp4PropertyType.IntegerArrayProperty:
        case Mp4PropertyType.SizeTableProperty:
            break;
        }

        return Mp4ErrorCode.MP4_S_OK;
    }

    void SetExpectSize(int count) {
        fExpectSize = count;
    }

}
