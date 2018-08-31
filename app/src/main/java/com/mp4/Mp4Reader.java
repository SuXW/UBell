package com.mp4;

import java.util.Vector;

import android.util.Log;

import com.mp4.*;
import com.ubia.IOTC.Packet;

public class Mp4Reader {
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
    int[] fVideoBufferSize = new int[1]; // /< ��Ƶ�������Ĵ�С
    int fVideoSampleSize; // /< ��Ƶ��������Ч���ݴ�С
    boolean   []fIsSyncSample= new boolean[1]; // /< �Ƿ���ͬ�� sample
    int fSyncSampleCount; // /< д��Ĺؼ�֡����Ŀ
    boolean   fHasSPS;
    boolean   fHasPPS;

    long fLastVideoTimestamp; // /< ǰһ֡��ʱ���
    long fLastAudioTimestamp; // /< ǰһ֡��ʱ���
    long fStartTimestamp; // /< ��ʼ¼���ʱ���

    int fVideoTrackIndex; // /< ��Ƶ track ������ֵ.
    char[] fFileName; // /< �ļ�������
    boolean   fIsAACAudio;
    int fSampleRate;
    long fSampleDuration;

    Mp4Atom fRootAtom; // /< �� ATOM
    Mp4File fMp4File; // /< Mp4 �ļ�
    Vector<Mp4Track> fTracks; // /< Track �б�
     
    int	fVideoOffset;		///< œ¬“ª¥Œ“™∑µªÿ∏¯”¶”√≥Ã–Úµƒ ”∆µ ˝æ›µƒø™ ºŒª÷√ 
    int	fMaxBufferSize;		///<  ”∆µ ˝æ›ª∫¥Ê«¯µƒ◊Ó¥Û≥§∂»
    long	[]fTimestamp = new long[1];			///< ◊Ó∫Û∂¡»°µƒ ”∆µµƒ ±º‰¥¡
	int	fNaluIndex;			///< œ¬“ª¥Œ“™∑µªÿ∏¯”¶”√≥Ã–Úµƒ ”∆µ NALU µƒÀ˜“˝‘⁄µ±«∞÷° ˝ 
	int	fVideoSampleId;		///< œ¬“ª¥Œ“™∂¡»°µƒ ”∆µ÷°µƒ ID.
	long	fPosition;			///< µ±«∞≤•∑≈µƒŒª÷√
	int	fAudioSampleId;		///< œ¬“ª¥Œ“™∂¡»°µƒ“Ù∆µ÷°µƒ ID. 
	int		fAudioTrackIndex; 
	long	[]fTimesDuration = new long[1];	
	long	 lTimesDurationAll ;	
public Mp4Reader() {
	fFileName			= null;

	fVideoSampleId		= 1;
	fAudioSampleId		= 1;

	fVideoTrackIndex	= -1;
	fAudioTrackIndex	= -1;

	fTimestamp[0]			= 0;
	fNaluIndex			= 0;
	fPosition			= 0;
	fIsSyncSample[0]		= false;


	fVideoBuffer		= null;
	fMaxBufferSize		= 0;
	fVideoBufferSize[0]	= 0;
	fVideoOffset		= 0;
	
	  fTracks = new Vector<Mp4Track>();
}
private void DestoryMp4Reader()
{
	if (fVideoBuffer != null) {
//		Mp4Free(fVideoBuffer);
		fVideoBuffer = null;
		fMaxBufferSize = 0;
	}

 
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
	        
	  
}

/** 
 * ¥Úø™≤¢∂¡»°÷∏∂®µƒ MP4 Œƒº˛. 
 * @param name “™¥Úø™µƒŒƒº˛µƒ√˚≥∆.
 */
public int   Open( String name )
{
	if (fMp4File != null) {
		return Mp4ErrorCode.MP4_ERR_FAILED;
	}

	fMp4File = new Mp4File();
	if (fMp4File.Open(name, "rb") != Mp4ErrorCode.MP4_S_OK) {
		fMp4File = null;
		return Mp4ErrorCode.MP4_ERR_FAILED;
	}

	// ¥¥Ω®∏˘Ω⁄µ„≤¢∂¡»°À˘”–µƒ◊”Ω⁄µ„
	fRootAtom = new Mp4Atom("root");
	fRootAtom.SetSize(fMp4File.getfFileSize());
	if (fRootAtom.Read(fMp4File) != Mp4ErrorCode.MP4_S_OK) {
		fMp4File.Close();
		fMp4File = null;
		lTimesDurationAll = 0;
		fRootAtom.Clear();
		fRootAtom = null;

		return Mp4ErrorCode.MP4_ERR_FAILED;
	}

	fVideoSampleId		= 1;
	fAudioSampleId		= 1;

	fVideoTrackIndex	= -1;
	fAudioTrackIndex	= -1;

	fVideoBufferSize[0]	= 0;
	fVideoOffset		= 0;

	fTimestamp[0]			= 0;
	fNaluIndex			= 0;
	fIsSyncSample[0]			= false;
	fPosition			= 0;

	// ¥¥Ω®œ‡πÿµƒ track.
	GenerateTracks();
	return Mp4ErrorCode.MP4_S_OK;
}

/** πÿ±’’‚∏ˆŒƒº˛, ≤¢ Õ∑≈À˘”–µƒ◊ ‘¥. */
public void  Close()
{
	  for (int i = 0; i < fTracks.size(); i++) {
          Mp4Track track = fTracks.get(i);
          if (null != track) {
              track.Clear();
          }
      }
	fTracks.clear();

	if (fMp4File != null) {
		fMp4File.Close();
		fMp4File = null;
	}

	if (fRootAtom != null) {
		fRootAtom.Clear();
		fRootAtom = null;
	}

	if (fVideoBuffer != null) {
//		Mp4Free(fVideoBuffer);
		fVideoBuffer = null;
		fMaxBufferSize = 0;
		fVideoBufferSize[0] = 0;
	}
	DestoryMp4Reader();
}

/** …˙≥…œ‡”¶µƒ track. */
int  GenerateTracks()
{
	if (fRootAtom == null) {
		return 0;
	}

	Mp4Atom  moov = fRootAtom.FindAtom("moov");
	if (moov == null) {
		// √ª”–’“µΩ moov Ω⁄µ„
		return 0;
	}

	int count = moov.GetChildAtomCount();
	for (int i = 0; i < count; i++) {
		Mp4Atom  atom = moov.GetChildAtom(i);
		if (atom == null) {
			break;
		}

		// “ª∏ˆ trak atom Ω⁄µ„¥˙±Ì“ª∏ˆ track 
		if (Mp4Common.ATOMID("trak") == Mp4Common.ATOMID(atom.GetType()) ) {
			Mp4Track track = new Mp4Track();
			track.SetTrackAtom(atom);

			if (Mp4Common.ATOMID(track.GetType()) == Mp4Common.ATOMID("vide")) {
				fVideoTrackIndex = fTracks.size();  // º«¬º ”∆µ track µƒÀ˜“˝±„”⁄≤È’“

			} else if (Mp4Common.ATOMID(track.GetType()) == Mp4Common.ATOMID("soun")) {
				fAudioTrackIndex = fTracks.size();
			}

			fTracks.add(track);
		}
	}

	return fTracks.size();
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
/** ∑µªÿ ”∆µµƒøÌ∂». */
int GetWidth()
{
	Mp4Track track = GetTrack("vide");
	return track != null ? track.GetWidth() : 0;
}

/** ∑µªÿ ”∆µµƒ∏ﬂ∂». */
int  GetHeight()
{
	Mp4Track track = GetTrack("vide");
	return track != null ? track.GetHeight() : 0;	
}

/** ∑µªÿ÷∏∂®µƒ track µƒ÷°¬ . */
int  GetFrameRate( boolean  isVideo /*= TRUE*/ )
{
	return isVideo ? 25 : 50;	
}

/** ∑µªÿ÷∏∂®µƒ track µƒ¬Î¬ . */
int  GetBitrate( boolean  isVideo /*= TRUE*/ )
{
	Mp4Track track = GetTrack(isVideo ? "vide" : "soun");
	return track != null ? track.GetAvgBitrate() : 0;		
}

/** ∑µªÿ÷∏∂®µƒ track µƒ≥§∂». */
int  GetDuration()
{
	if (fRootAtom != null) {
		return (int )fRootAtom.GetIntProperty("moov.mvhd.duration");
	}
	return 0;
}

int  GetTimeScale()
{
	if (fRootAtom != null) {
		return (int )fRootAtom.GetIntProperty("moov.mvhd.timeScale");
	}
	return 0;
}

/** ∑µªÿ÷∏∂®µƒ track µƒ÷° ˝. */
int  GetSampleCount( boolean  isVideo /*= TRUE*/ )
{
	Mp4Track track = GetTrack(isVideo ? "vide" : "soun");
	return track != null ? track.GetNumberOfSamples() : 0;
}

void SetPosition( long position )
{
	Mp4Track track = GetTrack(fVideoTrackIndex);
	Mp4Track trackaudio = GetTrack(fAudioTrackIndex); 
	long audioposition = position;
	if (track != null) {
		int  timeScale = track.GetTimeScale();
		if (timeScale != 1000) {
			position = position * timeScale / 1000;
		}

		fVideoSampleId = track.GetSampleIdFromTime(position, true);
		fAudioSampleId = trackaudio.GetSampleIdFromTime(position, true);
		// »°µ√µ±«∞ ”∆µ÷°µƒ ±º‰¥¡
		if (fVideoSampleId > 0) {
			long startTime[] =new long [1];
			long duration[] = new long [1] ;
			track.GetSampleTimes(fVideoSampleId,  startTime,  duration);
		//	Log.e("","seek fVideoSampleId:"+fVideoSampleId+"   startTime:"+startTime[0] +"  duration:"+duration[0]);
			if (timeScale != 0 && timeScale != 1000) {
				fPosition = startTime[0] * 1000 / timeScale;  // º∆À„µ±«∞≤•Œª÷√, µ•ŒªŒ™ MS.
			} else {
				fPosition = startTime[0];
			}
		}
	}

	

	// ÷ÿŒª∂®Œª“Ù∆µ TRACK µƒŒª÷√
	trackaudio = GetTrack(fAudioTrackIndex);
//	Log.e("","seek fAudioSampleId:"+fAudioSampleId+"   fVideoSampleId:"+fVideoSampleId +"  fPosition:"+fPosition);
	if (track != null) {
		int  timeScale = trackaudio.GetTimeScale();
	 
		if (timeScale != 1000) {
			audioposition = audioposition * timeScale / 1000;
		}
		
		fAudioSampleId =  trackaudio.GetSampleIdFromTime(audioposition, true);
	//	Log.e("","seek fAudioSampleId:"+fAudioSampleId+"   fVideoSampleId:"+fVideoSampleId);
	}
} 
public void Seek( long time )
{
	SetPosition(time);
}
public boolean  GetNextVideoSample( byte[]  buf, int  length[], long timestamp[] , long[] duration )
{


 	if (fVideoBufferSize[0] <= 0){
		Mp4Track track = GetTrack(fVideoTrackIndex);
		if (track == null) {
			return false;
		}

		if (fVideoBuffer == null) {
			fVideoBuffer	=   new byte[1024 * 256]; 
			fMaxBufferSize	= 1024 * 256;
		}

		fVideoBufferSize[0] = fMaxBufferSize;
		fVideoOffset	= 0;
		fNaluIndex		= 0;
//		long duration[]	= new long[1];

		track.ReadSample(fMp4File, fVideoSampleId, fVideoBuffer,  fVideoBufferSize,  fTimestamp, fTimesDuration,  fIsSyncSample);

//		lTimesDurationAll +=fTimesDuration[0] ;
		//Log.e("", "read frame  file size iRet fVideoSampleId: "+fVideoSampleId+"  fVideoOffset:"+fVideoOffset+"  fTimestamp:"+fTimestamp[0]+"   fTimesDuration:"+fTimesDuration[0] +"   lTimesDurationAll:"+lTimesDurationAll);

		if (fVideoSampleId == 1) {
			fIsSyncSample [0]	= true;
		}
		timestamp[0] = fTimestamp[0] ;
		int  timeScale = track.GetTimeScale();
		fTimesDuration[0] = track.GetDuration();
		if (timeScale != 0 && timeScale != 1000) {
			fPosition = fTimestamp[0] * 1000 / timeScale;  // º∆À„µ±«∞≤•Œª÷√, µ•ŒªŒ™ MS.
		} else {
			fPosition = fTimestamp[0];
		}

		fVideoSampleId++;
	}

	// »Áπ˚ª∫¥Ê«¯√ª”– ˝æ›
	if (fVideoBufferSize[0] <= 0 || fVideoOffset >= fVideoBufferSize[0]) {
		return false;
	}

	boolean  flags = true;
	int  size = 0;


	if (fIsSyncSample[0]) {
		//Log.e("guo..", "read frame  file   fIsSyncSample: "+fIsSyncSample );
		Mp4Track track = GetTrack(fVideoTrackIndex);
		if (track == null) {
			return false;
		}


	    Mp4AvcCAtom avcc = new Mp4AvcCAtom(track.GetAvcCAtom());
//		Mp4AvcCAtom avcc(track.GetAvcCAtom());
		int picSets = avcc.GetPictureSetCount();

		if (fNaluIndex == 0) {
			if (avcc.GeSequenceSetCount() > 0) {
				  byte[] set = avcc.GetSequenceParameters(0 );
				  length[0] = (short)set.length;
				if (set!=null && length[0] > 0) {
//					memcpy(buf + 4, set, length);
					System.arraycopy(set , 0,buf , 4, length[0]);

					size = length[0] + 4;
					flags = false;
				}
			}
		} else if (fNaluIndex < picSets + 1) {

			  byte[] set = avcc.GetPictureParameters(fNaluIndex - 1 );
			  length[0] = (short)set.length;
			if (set!=null && length[0] > 0) {
//				memcpy(buf + 4, set, length);
				System.arraycopy(set,0, buf,   4, length[0]);
				size = length[0] + 4;
				flags = false;
			}
		}



	}

	// ∏¥÷∆ sample ƒ⁄»› ˝æ›
	if (flags) {
        byte[] p = new byte[4];
        if (null != fVideoBuffer )
            System.arraycopy(fVideoBuffer, fVideoOffset, p, 0, 4);
		 
		 
		size = Packet.byteArrayToInt_Big(p);// p[0] << 24 | p[1] << 16 | p[2] << 8 | p[3];
		if (size >= 300000) {
			return false;
		}
		size += 4;
 	//	Log.e("", "read frame  file size iRet: "+size+"  fVideoOffset:"+fVideoOffset+"  fTimestamp:"+fTimestamp[0] );
		System.arraycopy(fVideoBuffer,fVideoOffset, buf, 0, size);
		fVideoOffset += size;
//		memcpy(buf, fVideoBuffer, size);
	}


//	System.arraycopy(fVideoBuffer,0, buf,0, fVideoBufferSize[0] );
	// H.264 Õ¨≤ΩÕ∑
		buf[0] = 0x00;
		buf[1] = 0x00;
		buf[2] = 0x00;
		buf[3] = 0x01;
//	fVideoOffset += fVideoBufferSize[0];
	fNaluIndex++;

	if (length!=null) {
		 length[0] =  size ;
	}
//
	if (timestamp!=null) {
		 timestamp[0] = fTimestamp[0];
	}
	if(duration!=null){
		duration[0]=fTimesDuration[0];
	}
	// »Áπ˚ª∫¥Ê«¯µƒ ˝æ›∂º∂¡ÕÍ¡À, ‘Ú÷ÿ÷√œ‡πÿµƒ±‰¡ø

	Log.e("guo..Mp4Reader","fVideoOffset:"+fVideoOffset+".fVideoBufferSize:"+fVideoBufferSize[0]);

	if (fVideoOffset >= fVideoBufferSize[0]) {
		fVideoBufferSize[0] = 0;
		fVideoOffset = 0;
	}

	return true;
}
/** ∑µªÿœ¬“ª∏ˆ“Ù∆µ sample µƒƒ⁄»›. */
public boolean  GetNextAudioSample( byte[] buf, int []  length, long[]  timestamp ,long duration[] )
{
	Mp4Track track = GetTrack(fAudioTrackIndex);
	if (track == null) {
		if (length  !=null) {
			 length[0] = 0;
		}
		return false;
	}

 
	boolean readBoolean[]={false};
	track.ReadSample(fMp4File, fAudioSampleId, buf, length, timestamp,  duration, readBoolean );
	
	if (buf != null) {
		fAudioSampleId++;
	}
	//Log.e("","fAudioSampleId:"+fAudioSampleId);
	return true;
}
public boolean CheckIsSyncSample()
{
	return fIsSyncSample[0];
}

}
