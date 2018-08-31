package com.mp4;

public class Mp4AvcCAtom {
    public class ProfileLevel {
        byte mProfile;
        byte mLevel;
    }
    Mp4Atom fAvcC;
    
    Mp4AvcCAtom( Mp4Atom avcC )
    {
        fAvcC = avcC;
    }

    /** ���� H.264 Profile �ͼ��������ֵ. */
    void GetProfileLevel ()
    {
        if (null != fAvcC) {
            ProfileLevel profile = new ProfileLevel(); 
            profile.mProfile = (byte)fAvcC.GetIntProperty("AVCProfileIndication");
            profile.mLevel   = (byte)fAvcC.GetIntProperty("AVCLevelIndication");
        }
    }

    /** ���� H.264 Profile �ͼ��������ֵ. */
    void SetProfileLevel (byte profile, byte level)
    {
        if (null != fAvcC) {
            fAvcC.SetIntProperty("AVCProfileIndication", profile);
            fAvcC.SetIntProperty("AVCLevelIndication", level);
        }
    }

    void SetProfileCompatibility( byte compatibility )
    {
        if (null != fAvcC) {
            fAvcC.SetIntProperty("profile_compatibility", compatibility);
        }
    }

    byte GetProfileCompatibility()
    {
        if (null != fAvcC) {
            return (byte)fAvcC.GetIntProperty("profile_compatibility");
        }
        return 0;
    }

    /** ���س���. */
    int GetLengthSize ()
    {
        if (null == fAvcC) {
            return 0;
        }

        return (int)fAvcC.GetIntProperty("lengthSizeMinusOne") & 0x03;
    }

    /** ���һ�� H.264 ���в�����. */
    boolean AddSequenceParameters(byte[] sequenceSets, int offset, int length)
    {
        if (fAvcC == null) {
            return false;
        }

        Mp4Property count = fAvcC.FindProperty("numOfSequenceParameterSets");
        Mp4SizeTableProperty table = GetSizeTable("sequenceEntries");
        if (count == null || table == null) {
            return false;
        }

        table.AddEntry(sequenceSets, offset, length);
        count.SetIntValue(table.GetCount());
        return true;
    }

    /** ���һ�� H.264 ͼ�������. */
    boolean AddPictureParameters(byte[] pictureSets, int offset, int length)
    {
        if (fAvcC == null) {
            return false;
        }

        Mp4Property count = fAvcC.FindProperty("numOfPictureParameterSets");
        Mp4SizeTableProperty table = GetSizeTable("pictureEntries");
        if (count == null || table == null) {
            return false;
        }

        table.AddEntry(pictureSets, offset, length);
        count.SetIntValue(table.GetCount());
        return true;
    }

    /** ���� H.264 ͼ�����������Ŀ. */
    int GetPictureSetCount()
    {
        if (null != fAvcC) {
            return (int)fAvcC.GetIntProperty("numOfPictureParameterSets");
        }
        return 0;
    }

    /** ���� H.264 ���в���������Ŀ. */
    int GeSequenceSetCount()
    {
        if (null != fAvcC) {
            return (int)fAvcC.GetIntProperty("numOfSequenceParameterSets");
        }
        return 0;
    }

    /** ����ָ�������Ƶ� SizeTable ����. */
    Mp4SizeTableProperty GetSizeTable(String name)
    {
        if (null != fAvcC) {
            return (Mp4SizeTableProperty)fAvcC.FindProperty(name);
        }
        return null;
    }

    /** 
     * ����ָ���������� H.264 ͼ������������ݺͳ���. 
     * @param length [out] ����ָ���Ĳ������ĳ���.
     * @return ����ָ��ָ���Ĳ��������ݵ�ָ��, ����������򷵻� null.
     */
    byte[] GetPictureParameters(int index)
    {   
        Mp4SizeTableProperty table = GetSizeTable("pictureEntries");
        if (null != table) {
            return table.GetEntry(index);
        }   
        return null;
    }

    /** 
     * ����ָ���������� H.264 ���в����������ݺͳ���. 
     * @param length [out] ����ָ���Ĳ������ĳ���.
     * @return ����ָ��ָ���Ĳ��������ݵ�ָ��, ����������򷵻� null.
     */
    byte[] GetSequenceParameters(int index)
    {
        Mp4SizeTableProperty table = GetSizeTable("sequenceEntries");
        if (null != table) {
            return table.GetEntry(index);
        }   
        return null;
    }
}
