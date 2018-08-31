package com.mp4;

import java.util.Vector;

public class Mp4DescriptorProperty extends Mp4Property {
    protected Vector<Mp4Descriptor> fDescriptors; // /< �� Descriptor �б�

    Mp4DescriptorProperty(String name) {
        super(Mp4PropertyType.DescriptorProperty, 0, name);
        
        fDescriptors = new Vector<Mp4Descriptor>();
    }

    @Override
	int Read(Mp4File file) {
        if (file == null) {
            return Mp4ErrorCode.MP4_ERR_FAILED;
        }

        byte type = (byte) file.ReadInt(1);
        int size = file.ReadMpegLength();
        long start = file.GetPosition();

        Mp4Descriptor desc = new Mp4Descriptor(type);
        fDescriptors.add(desc);
        desc.SetSize((byte) size);

        desc.Read(file);

        if ((start + size) != file.GetPosition()) {
            file.SetPosition(start + size);
        }

        return Mp4ErrorCode.MP4_S_OK;
    }

    @Override
	int Write(Mp4File file) {
        if (file == null) {
            return Mp4ErrorCode.MP4_ERR_FAILED;
        }

        int count = fDescriptors.size();
        for (int i = 0; i < count; i++) {
            Mp4Descriptor descriptor = fDescriptors.get(i);
            if (descriptor == null) {
                break;
            }

            descriptor.Write(file);
        }

        return Mp4ErrorCode.MP4_S_OK;
    }

    Mp4Descriptor GetDescriptor(int index) {
        if (index < 0 || index >= fDescriptors.size()) {
            return null;
        }

        return fDescriptors.get(index);
    }

    void AddDescriptor(Mp4Descriptor descriptor) {
        if (null != descriptor) {
            fDescriptors.add(descriptor);
        }
    }

    Mp4Property GetProperty(String name) {
        if (fDescriptors.size() > 0) {
            return fDescriptors.get(0).GetProperty(name);
        }
        return null;
    }
}
