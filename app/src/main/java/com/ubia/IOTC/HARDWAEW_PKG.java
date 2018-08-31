package com.ubia.IOTC;

/**
 * @author 作者  : WISE
 * @version 创建时间：2018/1/17  下午7:55
 * @description 类描述：
 * @project 项目： Doorbell-1.0.27-180117
 * @天天都是星期一
 */


public class HARDWAEW_PKG {

    public static final int DEFAULTANGLE =180;
    public static final int
            MF_VR_1135_1446 = 0, //default SC1135 960x960 配1146全景镜头 190度
            MF_VR_1145_1866 = 1, //SC1145 720P 配1866全景镜头 150度
            MF_VR_2135_1720 = 2, //SC2135 1080P 配1720全景镜头 186度
            MF_VR_2135_2466 = 3, //SC2135 1080P 配2466全景镜头 160度
            MF_STD_1145 = 4,     // G2.0 普通16:9设备
            MF_STD_1135 = 5,     // G2.0 普通 4:3设备
            MF_STD_2135 = 6,     // G2.0 普通16:9设备
            MF_STD_SWING_1145 = 7,   // G2.0 无太网口摇头16:9设备
            MF_STD_SWING_1135 = 8,   // G2.0 无太网口摇头 4:3设备
            MF_STD_SWING_2135 = 9,   // G2.0 无太网口摇头16:9设备
            SY_VR_9732_1866 = 10,// OV9732 720P 16:9
            SY_STD_9732 = 11,     // 普通16:9设备 720卡片机
            MF_SWING_1145 = 12,   // G2.0 以太网口摇头16:9设备
            MF_SWING_1135 = 13,   // G2.0 以太网口摇头 4:3设备
            MF_SWING_2135 = 14,   // G2.0 以太网口摇头16:9设备
            LM_VR_2135_1720 = 15,  // LM 1080P 配1720全景镜头 186度
            DH_VR_5230_1720 = 16,  // 大河 1080P 配1720全景镜头 186度
            SY_STD_5230 = 17,
            SY_SWING_5230 = 18,
            CM_BELL_VR_9732_5112=19,  // UBell OV9732 720P ?1866???? 150?
            CM_BELL_VR_5230_2466=20,   // UBell PS5230 1080P ?2466???? 160?
            SY_STD_SWING_5230 = 21,
            reserve = 22,  //291
            SREE_VR_LINUX_PIR_LED = 23, // 三锐庭院灯,PIR + 白光LED,APP控制开关灯
            BELL_LOCK = 24,
            CM_BELL_720P_9732_KY4G = 25, //720P 4G
            CM_BELL_1080P_5230_KY4G = 26; //1080P 4G
    public static final String deviceTypeString[] = {
            "MF_VR_1135_1446", //default SC1135 960x960 配1146全景镜头 190度
            "MF_VR_1145_1866", //SC1145 720P 配1866全景镜头 150度
            "MF_VR_2135_1720", //SC2135 1080P 配1720全景镜头 186度
            "MF_VR_2135_2466", //SC2135 1080P 配2466全景镜头 160度
            "MF_STD_1145",     // G2.0 普通16:9设备
            "MF_STD_1135",     // G2.0 普通 4:3设备
            "MF_STD_2135",     // G2.0 普通16:9设备
            "MF_STD_SWING_1145",   // G2.0 无太网口摇头16:9设备
            "MF_STD_SWING_1135",   // G2.0 无太网口摇头 4:3设备
            "MF_STD_SWING_2135",   // G2.0 无太网口摇头16:9设备
            "SY_VR_9732_1866",// OV9732 720P 16:9
            "SY_STD_9732",     // 普通16:9设备 720卡片机
            "MF_SWING_1145",   // G2.0 以太网口摇头16:9设备
            "MF_SWING_1135",   // G2.0 以太网口摇头 4:3设备
            "MF_SWING_2135",   // G2.0 以太网口摇头16:9设备
            "LM_VR_2135_1720",
            "DH_VR_5230_1720",  // 大河 1080P 配1720全景镜头 186度
            "SY_STD_5230",
            "SY_SWING_5230",
            "CM_BELL_VR_9732_5112",  // UBell OV9732 720P ?1866???? 150?
            "CM_BELL_VR_5230_2466",   // UBell PS5230 1080P ?2466???? 160?
            "SY_STD_SWING_5230",
            "reserve",   //291
            "SREE_VR_LINUX_PIR_LED",  // 三锐庭院灯,PIR + 白光LED,APP控制开关灯
            "BELL_LOCK",
            "CM_BELL_720P_9732_KY4G", //720P 4G
            "CM_BELL_1080P_5230_KY4G" //1080P 4G
    }; // LM 1080P 配1720全景镜头 186度


}