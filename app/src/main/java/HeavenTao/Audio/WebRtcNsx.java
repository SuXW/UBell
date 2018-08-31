package HeavenTao.Audio;

public class WebRtcNsx //WebRtc定点版噪音抑制器类
{
    private Long pclWebRtcNsxInst; //WebRtc定点版噪音抑制器的内存指针

    int pszi32AnalysisFilterState1[];//分频滤波器状态数据1，存放噪音抑制过程中的临时数据
    int pszi32AnalysisFilterState2[];//分频滤波器状态数据2，存放噪音抑制过程中的临时数据
    int pszi32SynthesisFilterState1[];//合频滤波器状态数据1，存放噪音抑制过程中的临时数据
    int pszi32SynthesisFilterState2[];//合频滤波器状态数据2，存放噪音抑制过程中的临时数据

    static
    {
        System.loadLibrary( "Func" ); //加载libFunc.so
        System.loadLibrary( "WebRtcNs" ); //加载libWebRtcNs.so
    }

    //构造函数
    public WebRtcNsx()
    {
        pclWebRtcNsxInst = new Long(0);
        pszi32AnalysisFilterState1 = new int[6];
        pszi32AnalysisFilterState2 = new int[6];
        pszi32SynthesisFilterState1 = new int[6];
        pszi32SynthesisFilterState2 = new int[6];
    }

    //析构函数
    public void finalize()
    {
        Destory(); //销毁WebRtc定点版噪音抑制器

        pclWebRtcNsxInst = null;
        pszi32AnalysisFilterState1 = null;
        pszi32AnalysisFilterState2 = null;
        pszi32SynthesisFilterState1 = null;
        pszi32SynthesisFilterState2 = null;
    }

    //初始化WebRtc定点版噪音抑制器
    public int Init( int i32SamplingRate, int i32PolicyMode )
    {
        if( pclWebRtcNsxInst.longValue() == 0) //如果WebRtc定点版噪音抑制器还没有初始化
        {
            return WebRtcNsxInit( pclWebRtcNsxInst, i32SamplingRate, i32PolicyMode );
        }
        else //如果WebRtc定点版噪音抑制器已经初始化
        {
            return 0;
        }
    }

    //获取WebRtc定点版噪音抑制器的内存指针
    public Long GetWebRtcNsx()
    {
        return pclWebRtcNsxInst;
    }

    //对一帧音频输入数据进行WebRtc定点版噪音抑制
    public int Process( int i32SamplingRate, short pszi16AudioDataFrame[], int i32AudioDataFrameSize )
    {
        return WebRtcNsxProcess( pclWebRtcNsxInst, i32SamplingRate, pszi16AudioDataFrame, i32AudioDataFrameSize, pszi32AnalysisFilterState1, pszi32AnalysisFilterState2, pszi32SynthesisFilterState1, pszi32SynthesisFilterState2 );
    }

    //销毁WebRtc定点版噪音抑制器
    public void Destory()
    {
        WebRtcNsxDestory( pclWebRtcNsxInst );
    }

    private native int WebRtcNsxInit( Long pclWebRtcNsxInst, int i32SamplingRate, int i32PolicyMode );
    private native int WebRtcNsxProcess( Long pclWebRtcNsxInst, int i32SamplingRate, short pszi16AudioDataFrame[], int i32AudioDataFrameSize, int pszi32AnalysisFilterState1[], int pszi32AnalysisFilterState2[], int pszi32SynthesisFilterState1[], int pszi32SynthesisFilterState2[] );
    private native void WebRtcNsxDestory( Long pclWebRtcNsxInst );
}
