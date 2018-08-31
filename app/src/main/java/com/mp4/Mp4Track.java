package com.mp4;

import android.util.FloatMath;
import android.util.Log;

public class Mp4Track {
    public class Mp4Sample {
        byte[] mData = null;
        int numBytes = 0;
        long timestamp = 0;
        long duration = 0;
        boolean isSyncSample = false;
    }
    public long  fSampleDuration;
    int fChunkCount; // /< Chunk ������
    int fWriteSampleId; // /< ��һ��Ҫд��� sample �� ID.
    int fFixedSampleSize; // /< �̶��� sample ��ʱ�䳤��
    byte[] fChunkBuffer; // /< ���� chunk ������
    int fChunkBufferSize; // /< ��ǰ���� chunk �������Ĵ�С
    int fChunkSamples; // /< ��ǰ���� chunk ���������ܹ� sample ����Ŀ
    long fChunkDuration; // /< ��ǰ Chunk ��ʱ�䳤��

    // controls for chunking
    int fSampleCountPerChunk; // /< ÿһ������� sample ����Ŀ
    long fDurationPerChunk; // /< ÿһ�������ʱ�䳤��
    int fBytesPerChunk; // /< ÿһ�� sample �Ĵ�С.

    Mp4Atom fTrackAtom; // /< �� ATOM

    Mp4Property fTrackType; // /< ��� Track ������.
    Mp4Property fTrackId; // /< ��� Track �� ID.
    Mp4Property fTimeScale; // /< Time scale
    Mp4Property fTrackDuration; // /< Track Duration
    Mp4Property fMediaDuration; // /< Media Duration

    Mp4ArrayProperty fSamplesSize; // /< Sample ��С��
    Mp4ArrayProperty fChunksOffset; // /< Chunk �ļ�ƫ��λ�ñ�
    Mp4ArrayProperty fSyncSamples; // /< ͬ�� Sample ID ��

    // stts
    Mp4ArrayProperty fSampleCount; // /< stts ����ͬʱ��� sample ������
    Mp4ArrayProperty fSampleDelta; // /< stts sample ��ʱ�䳤��

    // stsc
    Mp4ArrayProperty fFirstChunk; // /< ��һ�� chunk �� ID
    Mp4ArrayProperty fSamplesPerChunk; // /< ��Щ chunk ������ sample ��
    Mp4ArrayProperty fFirstSample; // /< ��һ�� sample �� ID.
    Mp4ArrayProperty fSampleDescIndex; // /<

    Mp4Atom GetTrackAtom() {
        return fTrackAtom;
    }

    void SetSampleCountPerChunk(int count) {
        fSampleCountPerChunk = count;
    }

    public Mp4Track() {
        Reset();
    }

    /** ���³�ʼ�����صĳ�Ա����. */
    void Reset() {
        fChunkCount = 0;
        fTimeScale = null;
        fMediaDuration = null;

        fBytesPerChunk = 0;
        fChunkBufferSize = 0;
        fChunkDuration = 0;
        fChunkSamples = 0;
        fDurationPerChunk = 0;
        fChunkBuffer = null;
        fSamplesPerChunk = null;
        fWriteSampleId = 1;

        fFixedSampleSize = 0;
    }

    void Clear() {
        if (fTrackAtom != null) {
            fTrackAtom.Clear();
        }

        Reset();

        fTrackAtom = null;
        fTrackId = null;
        fTrackDuration = null;
        fMediaDuration = null;
        fTimeScale = null;

        fTrackType = null;
        fSamplesSize = null;
        fSyncSamples = null;
        fFirstChunk = null;
        fSamplesPerChunk = null;

        fSampleDescIndex = null;
        fSampleCount = null;
        fSampleDelta = null;
        fChunksOffset = null;
        fFirstSample = null;
    }

    /** �󶨵�ָ���� Trak ATOM. */
    void SetTrackAtom(Mp4Atom track) {
        fTrackAtom = track;
        Init();
    }

    void Init() {
        Mp4Atom track = fTrackAtom;
        if (track == null) {
            return;
        }

        fTrackId = track.FindProperty("tkhd.trackId");
        fTrackDuration = track.FindProperty("tkhd.duration");

        fMediaDuration = track.FindProperty("mdia.mdhd.duration");
        fTimeScale = track.FindProperty("mdia.mdhd.timeScale");
        fTrackType = track.FindProperty("mdia.hdlr.handlerType");

        fSamplesSize = track.GetTableProperty("mdia.minf.stbl.stsz.entries",
                "sampleSize");
        fSyncSamples = track.GetTableProperty("mdia.minf.stbl.stss.entries",
                "sampleNumber");
        fChunksOffset = track.GetTableProperty("mdia.minf.stbl.stco.entries",
                "chunkOffset");

        fSampleCount = track.GetTableProperty("mdia.minf.stbl.stts.entries",
                "sampleCount");
        fSampleDelta = track.GetTableProperty("mdia.minf.stbl.stts.entries",
                "sampleDelta");

        fFirstChunk = track.GetTableProperty("mdia.minf.stbl.stsc.entries",
                "firstChunk");
        fSamplesPerChunk = track.GetTableProperty(
                "mdia.minf.stbl.stsc.entries", "samplesPerChunk");
        fSampleDescIndex = track.GetTableProperty(
                "mdia.minf.stbl.stsc.entries", "sampleDescriptionIndex");

        fDurationPerChunk = GetTimeScale();
        fSampleCountPerChunk = 25;

        if (fChunksOffset != null) {
            fChunkCount = fChunksOffset.GetCount();
        }

        // ����һ�� firstSample �Ա��ڲ�ѯ
        int sampleId = 1;
        if (fFirstChunk != null && fSamplesPerChunk != null) {
            fFirstSample = new Mp4ArrayProperty("firstSample");
            int count = fFirstChunk.GetCount();
            for (int i = 0; i < count; i++) {
                fFirstSample.AddValue(sampleId);

                if (i < count - 1) {
                    sampleId += (fFirstChunk.GetValue(i + 1) - fFirstChunk
                            .GetValue(i)) * fSamplesPerChunk.GetValue(i);
                }
            }
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    // һ�������

    /** ������� track �� ID. ���� 0 ��ʾ��Ч�� ID, ��Чֵ�� 1 ��. */
    int GetTrackId() {
        return (fTrackId != null) ? (int) fTrackId.GetIntValue() : 0;
    }

    /** ������� track �� ID ֵ, ��Чֵ�� 1 ��. */
    void SetTrackId(int id) {
        if (fTrackId != null) {
            fTrackId.SetIntValue(id);
        }
    }

    /** ������� track ������. */
    String GetType() {
        if (fTrackType != null) {
            return fTrackType.GetStringValue();
        }

        return null;
    }

    /** ������� track ������. */
    void SetType(String type) {
        if (fTrackType != null && type != null) {
            fTrackType.SetStringValue(type);
        }
    }

    /** ���� Sample ������. */
    int GetNumberOfSamples() {
        if (fSamplesSize != null) {
            return fSamplesSize.GetCount();
        }
        return 0;
    }

    /** ���� Chunk �ܹ�������. */
    int GetNumberOfChunks() {
        return fChunkCount;
    }

    /** ������Ƶ�Ŀ��. */
    int GetWidth() {
        if (fTrackAtom != null) {
            return (int) fTrackAtom.GetFloatProperty("tkhd.width");
        }
        return 0;
    }

    /** ������Ƶ�ĸ߶�. */
    int GetHeight() {
        if (fTrackAtom != null) {
            return (int) fTrackAtom.GetFloatProperty("tkhd.height");
        }
        return 0;
    }

    /** ���� AvcC atom ��ָ��. */
    Mp4Atom GetAvcCAtom() {
        if (fTrackAtom != null) {
            return fTrackAtom.FindAtom("mdia.minf.stbl.stsd.avc1.avcC");
        }
        return null;
    }

    /** ������� track ��ƽ������. */
    int GetAvgBitrate() {
        long duration = GetDuration();
        if (duration == 0) {
            return 0;
        }

        double calc = GetTotalOfSampleSizes();
        // this is a bit better - we use the whole duration
        calc *= 8.0;
        calc *= GetTimeScale();
        calc /= duration;
        // we might want to think about rounding to the next 100 or 1000
        return  (int)Math.ceil((float) calc);
    }

    /** ������� track ���������. */
    int GetMaxBitrate() {
        return (int) (GetAvgBitrate() * 1.2f);
    }

    // //////////////////////////////////////////////////////////////////////////////
    // Duration and TimeScale

    /** �����ܹ���ʱ�䳤��. */
    long GetDuration() {
        if (fMediaDuration != null) {
            return fMediaDuration.GetIntValue();
        }
        return 0;
    }

    /** �����ܹ���ʱ�䳤��. */
    void SetDuration(long duration) {
        if (fMediaDuration != null) {
            fMediaDuration.SetIntValue(duration);
        }
    }

    /** ����ʱ�����ֵ. */
    int GetTimeScale() {
        if (fTimeScale != null) {
            return (int) fTimeScale.GetIntValue();
        }
        return 0;
    }

    /** ����ʱ�����ֵ. */
    void SetTimeScale(int timeScale) {
        if (null != fTimeScale) {
            fTimeScale.SetIntValue(timeScale);
        }
    }

    /** ����ʱ��. */
    void UpdateDurations(long duration) {
        if (fMediaDuration != null) {
            fMediaDuration.SetIntValue(fMediaDuration.GetIntValue() + duration);
        }

        if (fTrackDuration != null) {
            int timeScale = GetTimeScale();
            long movieDuration = duration;
            if (timeScale > 0 && timeScale != 1000) {
                movieDuration = movieDuration * 1000 / timeScale;
            }
            fTrackDuration.SetIntValue(fTrackDuration.GetIntValue()
                    + movieDuration);
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    // Read Sample ����
    /** д��ָ���� sample. */
    int ReadSample(Mp4File file, int sampleId,byte[] data,  int numBytes[], long[]timestamp, long[] duration ,
            boolean[] isSyncSample) {
    	if (sampleId == 0 || file == null || numBytes==null) {
    		 
    		return Mp4ErrorCode.MP4_ERR_FAILED;
    	}
    	
    	int ret = 0;
    	int sampleSize = GetSampleSize(sampleId);			// ≥§∂»
    	duration[0]= GetDuration(); 
//    	Log.e(""," duration[0]:"+duration[0]);
    	if (sampleSize <= 0) {
    		numBytes[0] = 0;
    		return ret;
    	}
    	
    	if (data!=null) {
    		long fileOffset = GetSampleFileOffset(sampleId);	// ∆´“∆Œª÷√
    		int copySize = (int) Math.min(sampleSize,  numBytes[0]);

    		file.SetPosition(fileOffset);
    		ret = file.ReadBytes(data, copySize);
    	}
    	numBytes[0] = sampleSize; 
    	long[] duration2 = new long[1];
    	duration2[0] = 0;
		if (timestamp!=null || duration!=null) {
    		GetSampleTimes(sampleId, timestamp , duration  );
    	}
    	
    	if (isSyncSample!=null) {
    		 isSyncSample[0] = IsSyncSample(sampleId);
    	}
    	
    	return ret;
    }
    /** ��ȡָ���� sample ������. */
    int ReadSample(Mp4File file, int sampleId, Mp4Sample sample) {
        if (sampleId == 0 || file == null) {
            sample.numBytes = 0;
            return Mp4ErrorCode.MP4_ERR_FAILED;
        }

        int ret = 0;
        int sampleSize = GetSampleSize(sampleId); // ����
        if (sampleSize <= 0) {
            sample.numBytes = 0;
            return ret;
        }

        if (0 != sample.numBytes) {
            long fileOffset = GetSampleFileOffset(sampleId); // ƫ��λ��
            int copySize = Math.min(sampleSize, sample.numBytes);

            sample.mData = new byte[copySize];
            file.SetPosition(fileOffset);
            ret = file.ReadBytes(sample.mData, copySize);
            sample.numBytes = copySize;
        } else {
            sample.numBytes = sampleSize;
        }

        GetSampleTimes(sampleId, sample);

        sample.isSyncSample = IsSyncSample(sampleId);

        return ret;
    }

    // //////////////////////////////////////////////////////////////////////////////
    // Write Sample ����

    /** д��ָ���� sample. */
    int WriteSample(Mp4File file, byte[] data, int offset, int numBytes, long duration,
            boolean isSyncSample) {
        if (file == null || data == null || numBytes <= 0) {
            return Mp4ErrorCode.MP4_ERR_FAILED; // ��Ч�Ĳ���
        }

        int ret = Mp4ErrorCode.MP4_S_OK;

        // append sample bytes to chunk buffer
        int oldBufferSize = fChunkBufferSize;
        fChunkBufferSize += numBytes;
        byte[] newBuffer = new byte[fChunkBufferSize];
        if (null != fChunkBuffer && 0 != oldBufferSize)
            System.arraycopy(fChunkBuffer, 0, newBuffer, 0, oldBufferSize);
        fChunkBuffer = newBuffer;
        
        System.arraycopy(data, offset, fChunkBuffer, oldBufferSize, numBytes);
        fChunkSamples++;
        fChunkDuration += duration;
        fSampleDuration =duration;
        // ��һ�� sample ��ʱ�䳤��ͨ��Ϊ 0, ��һ�� sample ��ʱ�����ǰһ�� sample
        // ����������ʱ��, ����������һ�� sample �����ʱ��, ����д���, Ҫ��һ��
        // sample ��ʱ��, �� FinishWrite
     
        if (fWriteSampleId > 1) {
            UpdateSampleTimes(fSampleDuration);
        }

        UpdateSampleSizes(fWriteSampleId, numBytes);
        UpdateSyncSamples(fWriteSampleId, isSyncSample);

        if (IsChunkFull(fWriteSampleId)) {
            ret = FlushChunkBuffer(file);
        }

        if (ret == Mp4ErrorCode.MP4_S_OK) {
            UpdateDurations(fSampleDuration);
        }

        fWriteSampleId++;
        return ret;
    }

    /** �� Chunk �������е�����д���ļ���. */
    int FlushChunkBuffer(Mp4File file) {
        if (file == null || fChunkBufferSize == 0) {
            return Mp4ErrorCode.MP4_ERR_FAILED;
        }

        int ret = Mp4ErrorCode.MP4_S_OK;
        // write chunk buffer
        long chunkOffset = file.GetPosition();
        int result = file.WriteBytes(fChunkBuffer, fChunkBufferSize);
        if (result == fChunkBufferSize) {
            UpdateSampleToChunk(fWriteSampleId, fChunkCount + 1, fChunkSamples);
            UpdateChunkOffsets(chunkOffset);
        } else {
            ret = Mp4ErrorCode.MP4_ERR_WRITE;
        }

        // clean up chunk buffer
        fChunkBuffer = null;
        fChunkBufferSize = 0;
        fChunkSamples = 0;
        fChunkDuration = 0;

        return ret;
    }

    /** ���д����. */
    int FinishWrite(Mp4File file) {
        if (fTrackAtom == null) {
            return Mp4ErrorCode.MP4_ERR_FAILED;
        }

        // ���������˵�һ֡, ������Ҫ����һ֡��ʱ��, ��ʱ�� 0.
        if (fWriteSampleId > 1) {
            UpdateSampleTimes(0);
        }

        FlushChunkBuffer(file);

        if (fSamplesSize != null && fFirstChunk != null
                && fChunksOffset != null && fSampleCount != null) {
            if (fSamplesSize.GetCount() == 0) {
                fTrackAtom.SetIntProperty("mdia.minf.stbl.stsz.sampleSize",
                        fFixedSampleSize);
            } else {
                fTrackAtom.SetIntProperty("mdia.minf.stbl.stsz.entryCount",
                        fSamplesSize.GetCount());
            }

            fTrackAtom.SetIntProperty("mdia.minf.stbl.stsc.entryCount",
                    fFirstChunk.GetCount());
            fTrackAtom.SetIntProperty("mdia.minf.stbl.stco.entryCount",
                    fChunksOffset.GetCount());
            fTrackAtom.SetIntProperty("mdia.minf.stbl.stts.entryCount",
                    fSampleCount.GetCount());
        } else {
            return Mp4ErrorCode.MP4_ERR_FAILED;
        }

        if (fSyncSamples != null) {
            fTrackAtom.SetIntProperty("mdia.minf.stbl.stss.entryCount",
                    fSyncSamples.GetCount());
        }

        int timeScale = GetTimeScale();
        if (null != fTrackDuration && timeScale > 0) {
            fTrackDuration.SetIntValue(GetDuration() * 1000 / timeScale);
        }

        Mp4Atom mp4a = fTrackAtom.FindAtom("mdia.minf.stbl.stsd.mp4a");
        if (mp4a != null) {
            mp4a.SetIntProperty("esds.bufferSize", GetMaxSampleSize());
            mp4a.SetIntProperty("esds.avgBitrate", GetAvgBitrate());
            mp4a.SetIntProperty("esds.maxBitrate", GetMaxBitrate());
        }

        return Mp4ErrorCode.MP4_S_OK;
    }

    // //////////////////////////////////////////////////////////////////////////////
    // Sync Time Sample ����

    /** ����ָ���� sampleId ֮�����һ���ؼ�֡. */
    int GetNextSyncSample(int sampleId) {
        if (fSyncSamples == null) {
            // ��������� stss ��ÿһ֡���ǹؼ�֡
            return sampleId;
        }

        int numStss = fSyncSamples.GetCount();
        for (int stssIndex = 0; stssIndex < numStss; stssIndex++) {
            int syncSampleId = fSyncSamples.GetValue(stssIndex);
            if (sampleId > syncSampleId) {
                continue;
            }
            return syncSampleId;
        }

        // LATER check stsh for alternate sample
        return 0;
    }

    /** ָ��ָ���� Sample �Ƿ���ͬ�� Sample. */
    boolean IsSyncSample(int sampleId) {
        if (fSyncSamples == null) {
            // ��������� stss ��ÿһ֡���ǹؼ�֡
            return true;
        }

        int count = fSyncSamples.GetCount();
        for (int stssIndex = 0; stssIndex < count; stssIndex++) {
            int syncSampleId = fSyncSamples.GetValue(stssIndex);
            if (sampleId == syncSampleId) {
                // ������ ID ������������м���ʾ����һ���ؼ�֡
                return true;
            }

            if (sampleId < syncSampleId) {
                break;
            }
        }

        return false;
    }

    /** ���� Sync Sample ��. */
    void UpdateSyncSamples(int sampleId, boolean isSyncSample) {
        if (isSyncSample) {
            // if stss atom exists, add entry
            if (fSyncSamples != null) {
                fSyncSamples.AddValue(sampleId);
            } // else nothing to do (yet)
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    // ƫ��λ�ò���

    /** ����ָ���� sample ���ļ��е�ƫ��ֵ. */
    long GetSampleFileOffset(int sampleId) {
        if (fFirstChunk == null || fFirstSample == null
                || fSamplesPerChunk == null || fChunksOffset == null) {
            return 0;
        }

        int stscIndex = GetSampleStscIndex(sampleId);

        // firstChunk is the chunk index of the first chunk with
        // samplesPerChunk samples in the chunk. There may be multiples -
        // ie: several chunks with the same number of samples per chunk.
        int firstChunk = fFirstChunk.GetValue(stscIndex);
        int firstSample = fFirstSample.GetValue(stscIndex);
        int samplesPerChunk = fSamplesPerChunk.GetValue(stscIndex);
        if (samplesPerChunk == 0) {
            return 0;
        }

        // chunkId tells which is the absolute chunk number that this sample
        // is stored in.
        int chunkId = firstChunk + ((sampleId - firstSample) / samplesPerChunk);

        // chunkOffset is the file offset (absolute) for the start of the chunk
        long chunkOffset = fChunksOffset.GetValue(chunkId - 1);
        int firstSampleInChunk = sampleId
                - ((sampleId - firstSample) % samplesPerChunk);

        // need cumulative samples sizes from firstSample to sampleId - 1
        int sampleOffset = 0;
        for (int i = firstSampleInChunk; i < sampleId; i++) {
            sampleOffset += GetSampleSize(i);
        }

        return chunkOffset + sampleOffset;
    }

    /** ����ָ�� ID �� sample �� stsc ���е�����ֵ. */
    int GetSampleStscIndex(int sampleId) {
        if (fFirstChunk == null || fFirstSample == null) {
            return 0xFFFFFFFF;
        }

        int stscIndex = 0;
        int numStscs = fFirstChunk.GetCount();
        if (numStscs == 0) {
            return 0xFFFFFFFF;
        }

        for (stscIndex = 0; stscIndex < numStscs; stscIndex++) {
            if (sampleId < fFirstSample.GetValue(stscIndex)) {
                stscIndex -= 1;
                break;
            }
        }

        if (stscIndex == numStscs) {
            stscIndex -= 1;
        }

        return stscIndex;
    }

    /** ���� SampleToChunk ��. */
    void UpdateSampleToChunk(int sampleId, int chunkId, int samplesPerChunk) {
        if (fFirstChunk == null || fSamplesPerChunk == null
                || fSampleDescIndex == null || fFirstSample == null) {
            return;
        }

        int numStsc = fFirstChunk.GetCount();
        // if samplesPerChunk == samplesPerChunk of last entry
        if (0 != numStsc
                && (samplesPerChunk == fSamplesPerChunk.GetValue(numStsc - 1))) {
            return;
        }

        // add stsc entry
        fFirstChunk.AddValue(chunkId);
        fSamplesPerChunk.AddValue(samplesPerChunk);
        fSampleDescIndex.AddValue(1);
        fFirstSample.AddValue(sampleId - samplesPerChunk + 1);
    }

    /** ���� chunk ƫ��λ�ñ�. */
    void UpdateChunkOffsets(long chunkOffset) {
        // TODO: 64 λƫ��
        if (fChunksOffset != null) {
            fChunksOffset.AddValue((int) chunkOffset);
        }

        fChunkCount++;
    }

    // //////////////////////////////////////////////////////////////////////////////
    // Sample Time ����

    /** ����ָ����ʱ���Ӧ�� sample �� ID. */
    int GetSampleIdFromTime(long when, boolean wantSyncSample) {
        if (fSampleCount == null || fSampleDelta == null) {
            return 0;
        }

        int numStts = fSampleCount.GetCount();
        int sid = 1;
        long elapsed = 0;

        for (int sttsIndex = 0; sttsIndex < numStts; sttsIndex++) {
            int sampleCount = fSampleCount.GetValue(sttsIndex);
            int sampleDelta = fSampleDelta.GetValue(sttsIndex);

            long d = when - elapsed;
       // 	Log.e("","seek when - elapsed =d:"+d +"  sampleCount:"+sampleCount+"  sampleDelta:"+sampleDelta);
            if (d <= sampleCount * sampleDelta) {
                int sampleId = sid;
                if (0 != sampleDelta) {
                    sampleId += (int) (d / sampleDelta);
                }

                if (wantSyncSample) {
                    int syncSampleId = GetNextSyncSample(sampleId);
                    return (syncSampleId > 0) ? syncSampleId : sampleId;
                }
                return sampleId;
            }

            sid += sampleCount;
            elapsed += sampleCount * sampleDelta;
        }

        return 0; // satisfy MS compiler
    }
    void  GetSampleTimes( int sampleId, long[] pStartTime, long pDuration[] )
    {
    	if (fSampleCount == null || fSampleDelta == null) {
    		return;
    	}

    	int numStts = fSampleCount.GetCount();
    	int sid = 1;
    	long elapsed = 0;
    	
//    	for (int sttsIndex = 0; sttsIndex < numStts; sttsIndex++) {
//    		int sampleCount = fSampleCount.GetValue(sttsIndex);
//    		int sampleDelta = fSampleDelta.GetValue(sttsIndex);
//    		
//    		if (sampleId <= sid + sampleCount - 1) {
//    			if (pStartTime) {
//    				*pStartTime = (sampleId - sid);
//    				*pStartTime *= sampleDelta;
//    				*pStartTime += elapsed;
//    			}
//    			if (pDuration) {
//    				*pDuration = sampleDelta;
//    			}
//    			return;
//    		}
//    		sid += sampleCount;
//    		elapsed += sampleCount * sampleDelta;
//    	}
    	
    	
        for (int sttsIndex = 0; sttsIndex < numStts; sttsIndex++) {
            int sampleCount = fSampleCount.GetValue(sttsIndex);
            int sampleDelta = fSampleDelta.GetValue(sttsIndex);

            if (sampleId <= (sid + sampleCount - 1)) {
                long timeStamp = (sampleId - sid);
                timeStamp *= sampleDelta;
                timeStamp += elapsed;
//                sample.timestamp = timeStamp;
//
//                sample.duration = sampleDelta;
                pStartTime[0]  =  timeStamp;
     //        	Log.e("","seek when - elapsed = :"+elapsed +"  sampleCount:"+sampleCount+"  sampleDelta:"+sampleDelta+"  timeStamp:"+timeStamp);
                pDuration[0] = sampleDelta;
                return;
            }
            sid += sampleCount;
            elapsed += sampleCount * sampleDelta;
        }
    }

    /** ����ָ���� sample ��ʱ����Ϣ. */
    void GetSampleTimes(int sampleId, Mp4Sample sample) {
        if (fSampleCount == null || fSampleDelta == null) {
            return;
        }

        int numStts = fSampleCount.GetCount();
        int sid = 1;
        long elapsed = 0;

        for (int sttsIndex = 0; sttsIndex < numStts; sttsIndex++) {
            int sampleCount = fSampleCount.GetValue(sttsIndex);
            int sampleDelta = fSampleDelta.GetValue(sttsIndex);

            if (sampleId <= (sid + sampleCount - 1)) {
                long timeStamp = (sampleId - sid);
                timeStamp *= sampleDelta;
                timeStamp += elapsed;
                sample.timestamp = timeStamp;

                sample.duration = sampleDelta;
                return;
            }
            sid += sampleCount;
            elapsed += sampleCount * sampleDelta;
        }
    }

    /** ���� sample ��ʱ���. */
    void UpdateSampleTimes(long duration) {
        if (fSampleCount == null || fSampleDelta == null) {
            return;
        }

        int numStts = fSampleCount.GetCount();
//        Log.e("WriteSample","   track. fSampleDelta.GetValue(numStts - 1) ="+ fSampleDelta.GetValue(numStts - 1)+"    fSampleCount.GetValue(0):"+ fSampleCount.GetValue(0)+"   fChunkSamples:"+fChunkSamples+"   duration:"+duration  +"   numStts:"+numStts);

        // if duration == duration of last entry
        if (numStts > 0 && duration == fSampleDelta.GetValue(numStts - 1)) //mp4录像没有时间差
        {//&& duration == fSampleDelta.GetValue(numStts - 1)
            // increment last entry sampleCount
            int index = numStts - 1;
            fSampleCount.SetValue(index, fSampleCount.GetValue(index) + 1);

        } else {//时间差值
            // add stts entry, sampleCount = 1, sampleDuration = duration
            fSampleCount.AddValue(1);
            fSampleDelta.AddValue((int) duration);
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    // Sample Size ����

    void SetFixedSampleDuration(long duration) {
        if (null != fSamplesSize && fSamplesSize.GetCount() > 1) {
            return;
        }
        fFixedSampleSize = (int) duration;
    }

    /** ����ָ���� ID �� sample �Ĵ�С. */
    int GetSampleSize(int sampleId) {
        int fixedSampleSize = fFixedSampleSize;
        if (fixedSampleSize > 0) {
            return fixedSampleSize;
        }

        if (fSamplesSize != null) {
            return fSamplesSize.GetValue(sampleId - 1);
        }
        return 0;
    }

    /** �������С sample �Ĵ�С. */
    int GetMaxSampleSize() {
        int fixedSampleSize = fFixedSampleSize;
        if (fixedSampleSize != 0) {
            return fixedSampleSize;
        }

        if (fSamplesSize == null) {
            return 0;
        }

        int maxSampleSize = 0;
        int numSamples = fSamplesSize.GetCount();
        for (int sid = 1; sid <= numSamples; sid++) {
            int sampleSize = fSamplesSize.GetValue(sid - 1);
            if (sampleSize > maxSampleSize) {
                maxSampleSize = sampleSize;
            }
        }
        return maxSampleSize;
    }

    /** �������е� sample �ܹ��Ĵ�С. */
    long GetTotalOfSampleSizes() {
        long retval = 0;
        int fixedSampleSize = fFixedSampleSize;

        // if fixed sample size, just need to multiply by number of samples
        if (fixedSampleSize != 0) {
            retval = fixedSampleSize * GetNumberOfSamples();
            return retval;
        }

        if (fSamplesSize == null) {
            return 0;
        }

        // else non-fixed sample size, sum them
        long totalSampleSizes = 0;
        int numSamples = fSamplesSize.GetCount();
        for (int sid = 1; sid <= numSamples; sid++) {
            int sampleSize = fSamplesSize.GetValue(sid - 1);
            totalSampleSizes += sampleSize;
        }
        return totalSampleSizes;
    }

    /** ���һ�� Sample ��С��. */
    void UpdateSampleSizes(int sampleId, int numBytes) {
        fSamplesSize.AddValue(numBytes);
    }

    /** ָ����ǰ chunk �Ƿ��Ѿ�����. */
    boolean IsChunkFull(int sampleId) {
        if (fSamplesPerChunk != null) {
            return fChunkSamples >= fSampleCountPerChunk;
        }

        return fChunkDuration >= fDurationPerChunk;
    }

    long GetChunkTime(int chunkId) {
        // int stscIndex = GetChunkStscIndex(chunkId);
        // int firstChunkId = fFirstChunk.GetValue(stscIndex);
        // int firstSample =

        return 0;
    }

}
