package com.mp4;

import java.util.Vector;
import com.mp4.Mp4ArrayProperty;

public class Mp4TableProperty extends Mp4Property {
    protected Vector<Mp4Property> fProperties;

    Mp4TableProperty(String name) {
        super(Mp4PropertyType.TableProperty, 0, name);
        
        fProperties = new Vector<Mp4Property>();
    }

    Mp4ArrayProperty GetColumn(int index) {
        if (index < 0 || index >= fProperties.size()) {
            return null;
        }

        return (Mp4ArrayProperty) fProperties.get(index);
    }

    void AddColumn(String name) {
        fProperties.add(new Mp4ArrayProperty(name));
    }

    @Override
	int Read(Mp4File file) {
        if (file == null) {
            return Mp4ErrorCode.MP4_ERR_FAILED;
        }

        int cols = GetColCount();
        int rows = fExpectSize;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Mp4ArrayProperty array = GetColumn(col);
                if (array == null) {
                    return Mp4ErrorCode.MP4_ERR_FAILED;
                }

                array.AddValue((int) file.ReadInt(4));
            }
        }
        return Mp4ErrorCode.MP4_S_OK;
    }

    @Override
	int Write(Mp4File file) {
        if (file == null) {
            return Mp4ErrorCode.MP4_ERR_FAILED;
        }

        int cols = GetColCount();
        int rows = GetRowCount();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Mp4ArrayProperty array = GetColumn(col);
                if (array == null) {
                    return Mp4ErrorCode.MP4_ERR_FAILED;
                }

                file.WriteInt(array.GetValue(row), 4);
            }
        }

        return Mp4ErrorCode.MP4_S_OK;
    }

    /** ���ر�������. */
    int GetColCount() {
        return fProperties.size();
    }

    int GetRowCount() {
        Mp4ArrayProperty array = GetColumn(0);
        if (null == array)
            return 0;

        return array.GetCount();
    }
}
