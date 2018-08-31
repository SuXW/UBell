package HeavenTao.Audio;

public class SpeexEncoder//Speex编码器类
{
    private Long clSpeexEncoderState;//Speex编码器的内存指针
    private Long clSpeexBits;//SpeexBits的内存指针

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so
        System.loadLibrary( "Speex" ); //加载libSpeex.so
    }

    //构造函数
    public SpeexEncoder()
    {
        clSpeexEncoderState = new Long(0);
        clSpeexBits = new Long(0);
    }

    //析构函数
    public void finalize()
    {
        Destory();
    }

    //初始化Speex编码器
    public int Init( int iSamplingRate, int iUseCbrOrVbr, int iQuality, int iComplexity, int iPlcExpectedLossRate )
    {
        if( clSpeexEncoderState.longValue() == 0)//如果Speex编码器还没有初始化
        {
            return SpeexEncoderInit( clSpeexEncoderState, clSpeexBits, iSamplingRate, iUseCbrOrVbr, iQuality, iComplexity, iPlcExpectedLossRate );
        }
        else//如果Speex编码器已经初始化
        {
            return 0;
        }
    }

    //获取Speex编码器的内存指针
    public Long GetSpeexEncoderState()
    {
        return clSpeexEncoderState;
    }

    //将一帧PCM格式音频数据编码成Speex格式音频数据
    public int Encode( short clPCMAudioData[], byte clSpeexAudioData[], Integer clSpeexAudioDataSize, Integer clIsNeedTrans )
    {
        return SpeexEncoderEncode( clSpeexEncoderState, clSpeexBits, clPCMAudioData, clSpeexAudioData, clSpeexAudioDataSize, clIsNeedTrans );
    }

    public void Destory()//销毁Speex编码器
    {
        SpeexEncoderDestory( clSpeexEncoderState, clSpeexBits);
    }

    private native int SpeexEncoderInit( Long clSpeexEncoderState, Long clSpeexBits, int iSamplingRate, int iUseCbrOrVbr, int iQuality, int iComplexity, int iPlcExpectedLossRate );
    private native int SpeexEncoderEncode( Long clSpeexEncoderState, Long clSpeexBits, short clPCMAudioData[], byte clSpeexAudioData[], Integer clSpeexAudioDataSize, Integer clIsNeedTrans );
    private native void SpeexEncoderDestory( Long clSpeexEncoderState, Long clSpeexBits );
}