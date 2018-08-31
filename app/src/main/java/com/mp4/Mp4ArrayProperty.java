package com.mp4;

import java.util.Vector;

public class Mp4ArrayProperty extends Mp4Property {
    protected Vector<Integer> fProperties;     ///< ��������
    
    Mp4ArrayProperty(String name) {
        super(Mp4PropertyType.IntegerArrayProperty, 0, name);
        
        fProperties = new Vector<Integer>();
    }

    int GetValue( int index )
    {
        if (index < 0 || index >= fProperties.size()) {
            return 0;
        }
        
        return fProperties.get(index);  
    }

    void SetValue( int index, int value )
    {
        if (index < 0 || index >= fProperties.size()) {
            return;
        }
        
        fProperties.set(index, value);
    }

    void AddValue( int value )
    {
        fProperties.add(value);
    }
    
    /** ��������ĳ���. */
    int GetCount() { return fProperties.size(); }

    /** �����������. */
    void Clear() { fProperties.clear(); }
}
