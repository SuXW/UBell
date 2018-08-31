package com.mp4;

import java.util.Vector;

public class Mp4SizeTableProperty extends Mp4Property {
    protected Vector<byte[]> fBytesArray;
    
    Mp4SizeTableProperty(String name)
    {
        super(Mp4PropertyType.SizeTableProperty, 0, name);
        
        fBytesArray = new Vector<byte[]>();
    }
    
    int GetCount() { return fBytesArray.size(); }

    void AddEntry( byte[] bytes, int offset, int length )
    {
        if (bytes == null || length <= 0) {
            return;
        }

        byte[] buf = new byte[length];
        System.arraycopy(bytes, offset, buf, 0, length);

        fBytesArray.add(buf);
    }

    byte[] GetEntry( int index )
    {
        if (index < 0 || index >= fBytesArray.size()) {
            return null;
        }

        return fBytesArray.get(index);
    }

    @Override
	int Read( Mp4File file )
    {
        if (file == null) {
            return Mp4ErrorCode.MP4_ERR_FAILED;
        }
        
        int count = fExpectSize;
        for (int i = 0; i < count; i++) {
            short length = (short)file.ReadInt(2);
            byte[] bytes = new byte[length + 1];
            file.ReadBytes(bytes, length);                 
            AddEntry(bytes, 0, length);
        }

        return Mp4ErrorCode.MP4_S_OK;
    }

    @Override
	int Write( Mp4File file )
    {
        if (file == null) {
            return Mp4ErrorCode.MP4_ERR_FAILED;
        }

        int count = GetCount();
        for (int i = 0; i < count; i++) {
            byte[] buf = GetEntry(i);
            short length = (short)buf.length;
            file.WriteInt(length, 2);
            file.WriteBytes(buf, length);
        }

        return Mp4ErrorCode.MP4_S_OK;
    }
}
