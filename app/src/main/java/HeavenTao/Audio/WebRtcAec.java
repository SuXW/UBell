package HeavenTao.Audio;

public class WebRtcAec//WebRtc声学回音消除器类
{
    private Long pclWebRtcAecInst;//WebRtc声学回音消除器的内存指针

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so
        System.loadLibrary( "WebRtcAec" ); //加载libWebRtcAec.so
    }

    //构造函数
    public WebRtcAec()
    {
        pclWebRtcAecInst = new Long(0);
    }

    //析构函数
    public void finalize()
    {
        Destory(); //销毁WebRtcAec声学回音消除器

        pclWebRtcAecInst = null;
    }

    //初始化WebRtc声学回音消除器
    public int Init( int iSamplingRate, int iNlpMode )
    {
        if( pclWebRtcAecInst.longValue() == 0 )//如果WebRtc声学回音消除器还没有初始化
        {
            return WebRtcAecInit( pclWebRtcAecInst, iSamplingRate, iNlpMode );
        }
        else//如果WebRtc声学回音消除器已经初始化
        {
            return 0;
        }
    }

    //获取WebRtc声学回音消除器的内存指针
    public Long GetWebRtcAecInst()
    {
        return pclWebRtcAecInst;
    }

    //对一帧音频输入数据进行WebRtc声学回音消除
    public int Echo( short AudioIn[], short AudioOut[], short AudioResult[] )
    {
        return WebRtcAecEcho( pclWebRtcAecInst, AudioIn, AudioOut, AudioResult, AudioIn.length );
    }

    //销毁WebRtcAec声学回音消除器
    public void Destory()
    {
        WebRtcAecDestory( pclWebRtcAecInst );
    }

    public native int WebRtcAecInit( Long pclWebRtcAecInst, int iSamplingRate, int iNlpMode );
    public native int WebRtcAecEcho( Long pclWebRtcAecInst, short AudioIn[], short AudioOut[], short AudioResult[], int iFrameSize );
    public native void WebRtcAecDestory( Long pclWebRtcAecInst );
}