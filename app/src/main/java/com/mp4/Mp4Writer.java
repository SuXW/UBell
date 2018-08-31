package com.mp4;

import java.util.Vector;

import android.util.Log;

public class Mp4Writer {
    public class NalType {
        public static final int NAL_TYPE_NON_IDR_SLICE = 0x1;
        public static final int NAL_TYPE_DP_A_SLICE = 0x2;
        public static final int NAL_TYPE_DP_B_SLICE = 0x3;
        public static final int NAL_TYPE_DP_C_SLICE = 0x4;
        public static final int NAL_TYPE_IDR_SLICE = 0x5;
        public static final int NAL_TYPE_SEI = 0x6;
        public static final int NAL_TYPE_SEQ_PARAM = 0x7;
        public static final int NAL_TYPE_PIC_PARAM = 0x8;
        public static final int NAL_TYPE_ACCESS_UNIT = 0x9;
        public static final int NAL_TYPE_END_OF_SEQ = 0xa;
        public static final int NAL_TYPE_END_OF_STREAM = 0xb;
        public static final int NAL_TYPE_FILLER_DATA = 0xc;
        public static final int NAL_TYPE_SEQ_EXTENSION = 0xd;
    };

    public class SampleFlags {
        public static final int SAMPLE_SYNC_POINT = 0x01; // /< ����һ��ͬ����
        public static final int SAMPLE_START = 0x10; // /< ������� sample �ĵ�һ�����ݰ�
        public static final int SAMPLE_END = 0x20; // /< ������� sample �����һ�����ݰ�
        public static final int SAMPLE_FRAGMENT = 0x80; // /< ������ݰ���һ����Ƭ
    };

    byte[] fVideoBuffer; // /< ��Ƶ������
    int fVideoBufferSize; // /< ��Ƶ�������Ĵ�С
    int fVideoSampleSize; // /< ��Ƶ��������Ч���ݴ�С
    boolean fIsSyncSample; // /< �Ƿ���ͬ�� sample
    int fSyncSampleCount; // /< д��Ĺؼ�֡����Ŀ
    boolean fHasSPS;
    boolean fHasPPS;

    long fLastVideoTimestamp; // /< ǰһ֡��ʱ���
    long fLastAudioTimestamp; // /< ǰһ֡��ʱ���
    long fStartTimestamp; // /< ��ʼ¼���ʱ���

    int fVideoTrackIndex; // /< ��Ƶ track ������ֵ.
    char[] fFileName; // /< �ļ�������
    boolean fIsAACAudio;
    int fSampleRate;
    long fSampleDuration;

    Mp4Atom fRootAtom; // /< �� ATOM
    Mp4File fMp4File; // /< Mp4 �ļ�
    Vector<Mp4Track> fTracks; // /< Track �б�

    public Mp4Writer() {
        fTracks = new Vector<Mp4Track>();
        fVideoBuffer = null;
        fVideoSampleSize = 0;
        fVideoBufferSize = 0;
        fIsSyncSample = true;
        fSyncSampleCount = 0;

        fLastVideoTimestamp = 0;
        fLastAudioTimestamp = 0;
        fStartTimestamp = 0;
        fIsAACAudio = false;
        fSampleRate = 0;
        
        fHasPPS = false;
        fHasSPS = false;
    }

    /**
     * ����ָ�����Ƶ� MP4 �ļ�.
     * 
     * @return Mp4ErrorCode.MP4_ERR_ALREADY_OPEN �ļ��Ѿ����� MP4_ERR_OPEN ���ļ�ʧ��
     */
    public int Create(String name) {
        if (fMp4File != null) {
            return Mp4ErrorCode.MP4_ERR_ALREADY_OPEN; // �ļ��Ѿ�����
        }

        int ret = 0;
        fMp4File = new Mp4File();
        if ((ret = fMp4File.Open(name, "wb")) != Mp4ErrorCode.MP4_S_OK) {
            fMp4File = null;
            return ret;
        }

        fIsSyncSample = true;
        fSyncSampleCount = 0;
        fVideoSampleSize = 0;
        fVideoSampleSize = 0;
        fVideoBufferSize = 0;
        fLastVideoTimestamp = 0;
        fIsAACAudio = false;
        fSampleRate = 0;

        // ������ ATOM �ڵ��Լ���Ҫ���ӽڵ�
        fRootAtom = new Mp4Atom("root");
        fRootAtom.Init((byte) 0);

        long now = Mp4Common.Mp4GetTimestamp();
        fRootAtom.SetIntProperty("moov.mvhd.creationTime", now);
        fRootAtom.SetIntProperty("moov.mvhd.modificationTime", now);

        return Mp4ErrorCode.MP4_S_OK;
    }

    /**
     * ���һ��ָ�������͵� Track.
     * 
     * @param type
     *            Ҫ��ӵ� Track ������, �� "vide", "soun".
     * @param timeScale
     *            timeScale, Ĭ��Ϊ 1000.
     * @return ������ӵ� track �����ָ��.
     */
    Mp4Track AddTrack(String type, int timeScale /* = 1000 */) {
        if (fRootAtom == null) {
            return null;
        }

        int trackId = fTracks.size() + 1;
        fRootAtom.SetIntProperty("moov.mvhd.nextTrackId", trackId + 1);

        // ���� trak �ڵ�
        Mp4Atom atom = fRootAtom.AddChildAtom("moov.trak");
        if (null != atom) {
            long now = Mp4Common.Mp4GetTimestamp();

            atom.SetIntProperty("tkhd.creationTime", now);
            atom.SetIntProperty("tkhd.modificationTime", now);
            atom.SetIntProperty("tkhd.trackId", trackId);
            atom.SetIntProperty("mdia.mdhd.creationTime", now);
            atom.SetIntProperty("mdia.mdhd.modificationTime", now);
            atom.SetStringProperty("mdia.hdlr.handlerType", type);
        }

        // ���� track ����
        Mp4Track track = new Mp4Track();
        track.SetTrackAtom(atom);
        track.SetTimeScale(timeScale);
        fTracks.add(track);

        fSampleRate = timeScale;
        return track;
    }

    /** ���һ�� AMR ��Ƶ Track. */
    int AddAmrTrack(int timeScale, long duration, byte framesPerSample,
            boolean isAmrWB) {
        Mp4Track track = AddTrack("soun", timeScale);

        Mp4Atom atom = track.GetTrackAtom();
        if (null != atom) {
            atom.SetFloatProperty("tkhd.volume", 1.0);
            Mp4Atom smhd = atom.AddChildAtom("mdia.minf.smhd", 0);

            atom.SetIntProperty("mdia.minf.stbl.stsd.entryCount", 1);
            String name = isAmrWB ? "mdia.minf.stbl.stsd.sawb"
                    : "mdia.minf.stbl.stsd.samr";
            Mp4Atom amr = atom.AddChildAtom(name);
            if (null != amr) {
                amr.SetIntProperty("timeScale", timeScale);
                amr.SetIntProperty("damr.modeSet", 0);
                amr.SetIntProperty("damr.modeChangePeriod", 0);
                amr.SetIntProperty("damr.framesPerSample", framesPerSample);
            }
        }

        fSampleRate = timeScale;
        track.SetSampleCountPerChunk(47);
        track.Init();
        return track.GetTrackId();
    }

    /** ���һ����Ƶ�켣. */
    int AddAudioTrack( int timeScale, int sampleRate, int duration, byte type) {
//        Mp4Track track = AddTrack("soun", timeScale);
//        Mp4Atom atom = track.GetTrackAtom();
//
//        if (null != atom) {
//            atom.SetFloatProperty("tkhd.volume", 1.0);
//
//            Mp4Atom smhd = atom.AddChildAtom("mdia.minf.smhd", 0);
//            atom.SetIntProperty("mdia.minf.stbl.stsd.entryCount", 1);
//            Mp4Atom mp4a = atom.AddChildAtom("mdia.minf.stbl.stsd.mp4a");
//            if (null != mp4a) {
//                mp4a.SetIntProperty("sampleRate", 1000);
////                mp4a.SetIntProperty("timeScale", timeScale);
//                mp4a.SetIntProperty("channels", 1);
//            }
//        }
//        fIsAACAudio = true;
////        track.SetSampleCountPerChunk(47);
//        track.Init();
//        track.SetSampleCountPerChunk(43);	
    	Mp4Track track = AddTrack("soun", timeScale);	
    	Mp4Atom atom = track.GetTrackAtom();
        track.fSampleDuration =  64;
    	if (null != atom) {
    		atom.SetFloatProperty("tkhd.volume", 1.0);

    		Mp4Atom smhd = atom.AddChildAtom("mdia.minf.smhd", 0);
    		atom.SetIntProperty("mdia.minf.stbl.stsd.entryCount", 1);
    		Mp4Atom mp4a = atom.AddChildAtom("mdia.minf.stbl.stsd.mp4a");
    		if (null !=mp4a) {
    			//mp4a->SetIntProperty("sampleRate", timeScale);
    			//mp4a->SetIntProperty("channels", 2);
    			mp4a.SetIntProperty("timeScale", timeScale);
    			mp4a.SetIntProperty("sampleRate", sampleRate);
    			mp4a.SetIntProperty("channels", 1);
    		}
    	}
//    	fAacSampleRate = timeScale;
    	fIsAACAudio = true;
    	//track.SetSampleCountPerChunk(47);	 //for 48KHz
    	track.Init();
    	track.SetSampleCountPerChunk(43);	
    	
        return track.GetTrackId();
    }

    /** ���һ����Ƶ�켣. */
    public int AddVideoTrack(int timeScale, long duration, short width, short height,
            byte type) {
        fSampleDuration = duration;
        Mp4Track track = AddTrack("vide", timeScale);
        Mp4Atom atom = track.GetTrackAtom();
        track.fSampleDuration =  fSampleDuration;
        if (null != atom) {
            atom.SetFloatProperty("tkhd.width", width);
            atom.SetFloatProperty("tkhd.height", height);

            Mp4Atom vmhd = atom.AddChildAtom("mdia.minf.vmhd", 0);
            Mp4Atom stss = atom.AddChildAtom("mdia.minf.stbl.stss");

            atom.SetIntProperty("mdia.minf.stbl.stsd.entryCount", 1);
            Mp4Atom avc1 = atom.AddChildAtom("mdia.minf.stbl.stsd.avc1");

            if (null != avc1) {
                avc1.SetIntProperty("width", width);
                avc1.SetIntProperty("height", height);
            }
        }
        
        fVideoTrackIndex = fTracks.size() - 1;
        track.SetSampleCountPerChunk(25);
        track.Init();
        return track.GetTrackId();
    }

    /** �ر�����ļ�, ���ͷ����е���Դ. */
    public void Close() {
        for (int i = 0; i < fTracks.size(); i++) {
            Mp4Track track = fTracks.get(i);
            if (null != track) {
                track.Clear();
            }
        }

        fTracks.clear();

        if (null != fMp4File) {
            fMp4File.Close();
            fMp4File = null;
        }

        if (null != fRootAtom) {
            fRootAtom.Clear();
            fRootAtom = null;
        }
        
        fVideoBufferSize = 0;
        fVideoBuffer = null;
        fVideoSampleSize = 0;
        fVideoBufferSize = 0;
        fIsSyncSample = true;
        fSyncSampleCount = 0;

        fLastVideoTimestamp = 0;
        fLastAudioTimestamp = 0;
        fStartTimestamp = 0;
        fIsAACAudio = false;
        fSampleRate = 0;
        fSampleDuration = 0;
        
        fHasPPS = false;
        fHasSPS = false;
    }

    /**
     * ׼����ʼд MP4 �ļ�. ����������Ƚ� mdat ֮ǰ�� ATOM �ڵ�� mdat �ڵ�� ͷ����д���ļ�, �������žͿ��Կ�ʼд
     * sample ������, �� mdat ֮��Ľڵ� Ҫ�� mdat д��֮��, �Ż��ڵ��� FinishWrite ʱ��д���ļ���.
     * 
     * @return ����ɹ���� Mp4ErrorCode.MP4_S_OK(0), �����һ����ʾ������ĸ���.
     *         MP4_ERR_NOT_OPEN ����ļ���û�д�. Mp4ErrorCode.MP4_ERR_NULL_ATOM �����յ�
     *         ATOM �ڵ�.
     */
    public int BeginWrite() {
        if (null == fMp4File || null == fRootAtom) {
            return Mp4ErrorCode.MP4_ERR_NOT_OPEN;
        }

        int ret = 0;

        // д���ļ�ͷ, ��׼����ʼдý������
        for (int i = 0; i < fRootAtom.GetChildAtomCount(); i++) {
            Mp4Atom atom = fRootAtom.GetChildAtom(i);
            if (atom == null) {
                return Mp4ErrorCode.MP4_ERR_NULL_ATOM;
            }

            if (Mp4Common.ATOMID("mdat") == Mp4Common.ATOMID(atom.GetType())) {
                return atom.BeginWrite(fMp4File);

            } else {
                if ((ret = atom.Write(fMp4File)) != Mp4ErrorCode.MP4_S_OK) {
                    return ret;
                }
            }
        }

        return Mp4ErrorCode.MP4_S_OK;
    }

    /** ���д��������β����. */
    public int FinishWrite() {
        if (null == fMp4File || null == fRootAtom) {
            return Mp4ErrorCode.MP4_ERR_FAILED;
        }

        int i = 0;

        long duration = 0;

        // д�����е� track ��û��д���ļ��Ļ�����������
        for (i = 0; i < fTracks.size(); i++) {
            Mp4Track track = fTracks.get(i);
            if (null != track) {
                track.FinishWrite(fMp4File);
            }

            int timeScale = track.GetTimeScale();
            Log.e("","timeScale last duration:"+timeScale);
            if (timeScale > 0) {
                long ret = track.GetDuration() * 1000 / timeScale;
                if (ret > duration) {
                    duration = ret;
                }
            }
        }
        Log.e("","last duration:"+duration);
        fRootAtom.SetIntProperty("moov.mvhd.duration", duration);

        // ��� mdat atom ��֮��Ľڵ��д����
        boolean flags = false;
        for (i = 0; i < fRootAtom.GetChildAtomCount(); i++) {
            Mp4Atom atom = fRootAtom.GetChildAtom(i);
            if (atom == null) {
                return Mp4ErrorCode.MP4_ERR_FAILED;
            }

            if (Mp4Common.ATOMID("mdat") == Mp4Common.ATOMID(atom.GetType())) {
                atom.FinishWrite(fMp4File);
                flags = true;
            } else {
                if (flags) {
                    atom.Write(fMp4File);
                }
            }
        }

        return Mp4ErrorCode.MP4_S_OK;
    }

    /** ����ָ���������� Track. */
    Mp4Track GetTrack(int index) {
        if (index < 0 || index >= fTracks.size()) {
            return null;
        }

        return fTracks.get(index);
    }

    /** ����ָ�������͵� Track. */
    Mp4Track GetTrack(String type) {
        for (int i = 0; i < fTracks.size(); i++) {
            Mp4Track track = fTracks.get(i);
            if (null != track && null != track.GetType()
                    && type.equalsIgnoreCase(track.GetType())) {
                return track;
            }
        }

        return null;
    }

    /** ָ���Ƿ�����ļ�. */
    public boolean IsOpen() {
        return (fMp4File != null) && (fRootAtom != null);
    }

    /** ���˵����к�ͼ��������� Nalu ��. */
    int FilterParamSets(Mp4Track track, byte[] sample, int offset, int sampleSize) {
        byte naluType = H264HeaderParser.GetNaluType(sample, offset);
        int seqSize = 0;

        byte naluByte = H264HeaderParser.GetNaluRefIdc(sample, offset);
        if (naluByte > 3)
	      return -1;	

        switch (naluType) {
        case NalType.NAL_TYPE_SEQ_PARAM: {
            seqSize = sampleSize;
            // ֻ������һ���ؼ�֡
            if (!fHasSPS) {

                // ȡ����Ƶ��ʵ�ʿ�Ⱥ͸߶�
                H264HeaderParser parser = new H264HeaderParser();
                if (parser.ParseHeader(sample, offset, sampleSize) >= 0) {
                    Mp4Atom trackAtom = track.GetTrackAtom();
                    Mp4Atom avc1 = trackAtom
                            .FindAtom("mdia.minf.stbl.stsd.avc1");
                    if (null != avc1) {
                        avc1.SetIntProperty("width",
                                parser.fSeqParams.pic_width);
                        avc1.SetIntProperty("height",
                                parser.fSeqParams.pic_height);
                    }
                }

                // ȡ�����в�����
                byte headerSize = (byte) ((sample[offset+2] == 1) ? 3 : 4);
                seqSize = sampleSize - headerSize;
                Mp4AvcCAtom avc = new Mp4AvcCAtom(track.GetAvcCAtom());
                avc.AddSequenceParameters(sample, offset+headerSize, seqSize);
                if (sampleSize > 3) {
                    avc.SetProfileLevel(sample[offset+headerSize+1], sample[offset+headerSize+3]);
                    avc.SetProfileCompatibility(sample[offset+headerSize+2]);
                }
                
                fHasSPS = true;
            }
            return seqSize;
        }
        case NalType.NAL_TYPE_PIC_PARAM: {
            seqSize = sampleSize;
            // ֻ������һ���ؼ�֡
            if (!fHasPPS) {

                // ȡ��ͼ�������
                byte headerSize = (byte) ((sample[offset+2] == 1) ? 3 : 4);
                seqSize = sampleSize - headerSize;
                Mp4AvcCAtom avc = new Mp4AvcCAtom(track.GetAvcCAtom());
                avc.AddPictureParameters(sample, offset+headerSize, seqSize);
                
                fHasPPS = true;
            }
            return seqSize;
        }
        }

        return 0;
    }

    /** д��Ƶ Sample. */
    public int WriteVideoSample(byte[] sample, int offset, int sampleSize, long timeduration,
            boolean isSyncSample, boolean isEnd, int timeScale) {
        Mp4Track track = GetTrack(fVideoTrackIndex);
        if (track == null) {
            return 0; // ��������� track
        }

        if (sampleSize > 0) {
            if (isSyncSample) {
                fIsSyncSample = true;
            }

            // ��ȡ����/ͼ��������Ȳ���
            int size = FilterParamSets(track, sample, offset, sampleSize);
            if (size < 0) {
                return size;
            }

            // ���� H.264 ͬ����, һ��Ϊ "00 00 01" �� "00 00 00 01"
            byte headerSize = (byte) ((sample[offset+2] == 1) ? 3 : 4);
            int seqSize = sampleSize;
            // sample += headerSize;
            seqSize = sampleSize - headerSize;
            int SIZE_HEADER_LEN = 4;

            // ��鲢�����㹻�Ļ������ռ�
            if (sampleSize + SIZE_HEADER_LEN + fVideoSampleSize > fVideoBufferSize) {
                int oldBufferSize = fVideoBufferSize;
                fVideoBufferSize += (seqSize + SIZE_HEADER_LEN);
                byte[] newBuffer = new byte[fVideoBufferSize];
                if (null != fVideoBuffer && 0 != oldBufferSize)
                    System.arraycopy(fVideoBuffer, 0, newBuffer, 0, oldBufferSize);
                fVideoBuffer = newBuffer;
            }

            // �� sample �Ŀ�ʼ��� sample ����ͷ.
            fVideoBuffer[fVideoSampleSize] = (byte) ((seqSize >> 24) & 0xff);
            fVideoBuffer[fVideoSampleSize + 1] = (byte) ((seqSize >> 16) & 0xff);
            fVideoBuffer[fVideoSampleSize + 2] = (byte) ((seqSize >> 8) & 0xff);
            fVideoBuffer[fVideoSampleSize + 3] = (byte) (seqSize & 0xff);

            // ���� sample ����
            // memcpy(fVideoBuffer + fVideoSampleSize + SIZE_HEADER_LEN, sample,
            // sampleSize);
            System.arraycopy(sample, offset+headerSize, fVideoBuffer, fVideoSampleSize
                    + SIZE_HEADER_LEN, seqSize);
            fVideoSampleSize += (seqSize + SIZE_HEADER_LEN);
        }
//      	Log.e("","beginPosition:"+  "   timeStamp:"+timeduration+"  isIFrame:"+timeScale);
        // д���ļ�
        if (isEnd || fVideoSampleSize >= 1024 * 1024) {
      
            FlushVideoBuffer(track, timeduration, timeScale);
        }

        return sampleSize;
    }

    /** �����ѻ������е�������Ϊһ�� sample д���ļ���. */
    boolean FlushVideoBuffer(Mp4Track track, long timeduration, int timeScale) {
        if (fVideoSampleSize <= 0) {
            return false;
        }

//        long duration = 0;
//        if (fLastVideoTimestamp != 0) {
//            duration = timestamp - fLastVideoTimestamp;
//        }
//        fLastVideoTimestamp = timestamp;
//
//        // ����ʱ���
//        if (timeScale == 0) {
//            timeScale = 1000;
//        }
//        int trackTimeScale = track.GetTimeScale();
//        if (trackTimeScale != timeScale) {
//            duration = (duration * trackTimeScale) / timeScale;
//        }
       // fSampleDuration = timeduration;
        long duration = fSampleDuration;

//        Log.e("", "FlushVideoBuffer  duration："+duration+"   timestamp:"+timeduration +"   fSampleDuration:"+fSampleDuration);

        int ret = track.WriteSample(fMp4File, fVideoBuffer, 0,
                fVideoSampleSize, timeduration, fIsSyncSample);
        if (ret != Mp4ErrorCode.MP4_S_OK) {
            return false;
        }

        if (fIsSyncSample) {
            fSyncSampleCount++;
        }

        fVideoSampleSize = 0;
        fIsSyncSample = false;

        return true;
    }

    /** д��Ƶ Sample. */
    int WriteAudioSampe(byte[] sample, int size, long duration, int timeScale) {
        if (sample == null || size == 0 || fSampleRate == 0) {
            return 0;
        }

        int index = (fVideoTrackIndex == 0) ? 1 : 0;
        Mp4Track track = GetTrack(index);
        if (track == null) {
            return 	-1;
        }

//        long duration = 0;
//        if (fLastAudioTimestamp != 0) {
//            duration = timestamp - fLastAudioTimestamp;
//        }
//        fLastAudioTimestamp = timestamp;
//
//        // ����ʱ���
//        if (timeScale == 0) {
//            timeScale = 1000;
//        }
//        if (timeScale != fSampleRate) {
//            duration = (duration * fSampleRate) / timeScale;
//        }
//
        int offset = 0;
//        if (fIsAACAudio) 
        {
            // ȥ�� AAC ADTS ͷ (7���ֽ�)
            byte data = sample[offset];
            if ((data == (byte) 0xFF) && size > 7) {
                offset += 7;
                size -= 7;
            }
        }

      int value =   track.WriteSample(fMp4File, sample, offset, size, duration, true);
      //  Log.d("WriteSample","   track.WriteSample ="+value+"   index:"+index+"   duration:"+duration+"   size:"+size +"   offset:"+offset);
        return 0;
    }
}
