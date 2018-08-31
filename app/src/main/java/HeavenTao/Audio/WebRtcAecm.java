package HeavenTao.Audio;

public class WebRtcAecm //WebRtc移动版声学回音消除器类
{
    private Long m_pclWebRtcAecmInst; //WebRtc移动版声学回音消除器的内存指针
    public int m_i32Delay; //WebRtc移动版声学回音消除器的回音延迟时间，单位毫秒

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so
        System.loadLibrary( "WebRtcAecm" ); //加载libWebRtcAecm.so
    }

    //构造函数
    public WebRtcAecm()
    {
        m_pclWebRtcAecmInst = new Long(0);
    }

    //析构函数
    public void finalize()
    {
        m_pclWebRtcAecmInst = null;
    }

    //初始化WebRtc移动版声学回音消除器
    public int Init( int i32SamplingRate, int i32EchoMode, int i32Delay )
    {
        m_i32Delay = i32Delay;
        if( m_pclWebRtcAecmInst.longValue() == 0 )//如果WebRtc移动版声学回音消除器还没有初始化
        {
            return WebRtcAecmInit( m_pclWebRtcAecmInst, i32SamplingRate, i32EchoMode );
        }
        else//如果WebRtc移动版声学回音消除器已经初始化
        {
            return 0;
        }
    }

    //获取WebRtc移动版声学回音消除器的内存指针
    public Long GetWebRtcAecmInst()
    {
        return m_pclWebRtcAecmInst;
    }

    //对一帧音频输入数据进行WebRtc移动版声学回音消除
    public int Echo( short szi16AudioIn[], short szi16AudioOut[], short szi16AudioResult[] )
    {
        return WebRtcAecmEcho( m_pclWebRtcAecmInst, szi16AudioIn, szi16AudioOut, szi16AudioResult, szi16AudioIn.length, m_i32Delay );
    }

    //销毁WebRtc移动版声学回音消除器
    public void Destory()
    {
        WebRtcAecmDestory( m_pclWebRtcAecmInst );
    }

    private native int WebRtcAecmInit( Long pclWebRtcAecmInst, int i32SamplingRate, int i32EchoMode );
    private native int WebRtcAecmEcho( Long pclWebRtcAecmInst, short szi16AudioIn[], short szi16AudioOut[], short szi16AudioResult[], int i32FrameSize, int i32Delay );
    private native void WebRtcAecmDestory( Long pclWebRtcAecmInst );
}