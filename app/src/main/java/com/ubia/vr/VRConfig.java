
package com.ubia.vr;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.util.Log;

import com.ubia.IOTC.HARDWAEW_INFO;
import com.ubia.IOTC.HARDWAEW_PKG;
public class VRConfig {
	private  static final int GELS_BUFFER_SIZE = 1024*1024;//512*1024;
	
	public  static final int STDDEVICE =2;
	public  static final int SWINGDEVICE = 1;//512*1024;
	public  static final int VRDEVICE = 3;//512*1024;
	public  static FloatBuffer vertexbuffer;
	public  static FloatBuffer texturebuffer;
	public  static ByteBuffer indicebuffer;
	public  static ByteBuffer sizebuffer;
	public static VRConfig vrConfig = null;
	public static int width;
	public static int height;
	
    public static final int surfaceHemiSpherePano = 0;
    public static final int surfaceHemiSphereFull = 1;
    public static final int surfaceCylinderFull = 2;
    private static final int surfaceSemicircle = 3;
    private static final int surfaceSquare = 4;
    private static final int surfaceTwoSquare = 5;
    public static final int CameraPutModelFaceDown = 0;
    public static final int CameraPutModelFaceUp = 1;
    public static final int CameraPutModelFaceFront = 2;
	public static final int UBIA_PIC_1280_720 = 0, //1145 9732 720P
	UBIA_PIC_640_360 = 1,
	UBIA_PIC_640_480 = 2, 
	UBIA_PIC_1280_960 = 3,  //1135 960P
	UBIA_PIC_960_960 = 4,   
	UBIA_PIC_480_480 = 5,  
	UBIA_PIC_1920_1080 = 6; //2135  1080P
	

	
	public HARDWAEW_INFO getDeviceType(int checkType){
		Log.e("","getDeviceType HARDWAEW_INFO  checkType:"+checkType);
		HARDWAEW_INFO device = new  HARDWAEW_INFO();
		if(checkType > HARDWAEW_PKG.deviceTypeString.length || checkType < 0){
			return device;
		}
	
		device.type = STDDEVICE;
		if(HARDWAEW_PKG.deviceTypeString[checkType].contains("VR")){
			device.type = VRDEVICE; 
		} else if(HARDWAEW_PKG.deviceTypeString[checkType].contains("SWING")){
			device.type = SWINGDEVICE; 
		}
		
		switch(checkType){
		case HARDWAEW_PKG.MF_VR_1135_1446: // default SC1135 960x960 配1146全景镜头
											// 190度
		{
			device.angle = 190;
			device.maxFinger = 218f;  //
			device.minFinger = 142.0f;
			setRolution(960, device);
			break;
		}
		case HARDWAEW_PKG.MF_VR_1145_1866: // SC1145 720P 配1866全景镜头 150度
		{
			device.angle = 150;
			setRolution(720, device);
			device.maxFinger = 210f;  //
			device.minFinger = 150.0f;
			break;
		}
		case HARDWAEW_PKG.MF_VR_2135_1720: // SC2135 1080P 配1720全景镜头 186度
		{
			device.angle = 186;
			device.maxFinger = 218f;  //
			device.minFinger = 142.0f;
			setRolution(1080, device);
			break;
		}
		case HARDWAEW_PKG.MF_VR_2135_2466: // SC2135 1080P 配2466全景镜头 160度
		{
			device.angle = 160;
			device.maxFinger = 218f;  //
			device.minFinger = 142.0f;
			setRolution(1080, device);
			break;
		}
		case HARDWAEW_PKG.MF_STD_1145: // G2.0 普通16:9设备
		{
			device.angle = HARDWAEW_PKG.DEFAULTANGLE;
			setRolution(720, device);
			break;
		}
		case HARDWAEW_PKG.MF_STD_1135: // G2.0 普通 4:3设备
		{
			device.angle =HARDWAEW_PKG.DEFAULTANGLE;
			setRolution(1280, device);
			break;
		}
		case HARDWAEW_PKG.MF_STD_2135: // G2.0 普通16:9设备
		{
			device.angle = HARDWAEW_PKG.DEFAULTANGLE;
			setRolution(1080, device);
			break;
		}
		case HARDWAEW_PKG.MF_STD_SWING_1145: // G2.0 无太网口摇头16:9设备
		{
			device.angle = HARDWAEW_PKG.DEFAULTANGLE;
			setRolution(720, device);
			break;
		}
		case HARDWAEW_PKG.MF_STD_SWING_1135: // G2.0 无太网口摇头 4:3设备
		{
			device.angle = HARDWAEW_PKG.DEFAULTANGLE;
			setRolution(1280, device);
			break;
		}
		case HARDWAEW_PKG.MF_STD_SWING_2135: // G2.0 无太网口摇头16:9设备
		{
			device.angle =HARDWAEW_PKG.DEFAULTANGLE;
			setRolution(1080, device);
			break;
		}
		case HARDWAEW_PKG.SY_VR_9732_1866: // OV9732 720P 16:9
		{
			device.angle =HARDWAEW_PKG.DEFAULTANGLE;
			setRolution(720, device);
			break;
		}
		case HARDWAEW_PKG.SY_STD_9732: // 普通16:9设备 720卡片机
		{
			device.angle = HARDWAEW_PKG.DEFAULTANGLE;
			setRolution(720, device);
			break;
		}
		case HARDWAEW_PKG.MF_SWING_1145: // G2.0 以太网口摇头16:9设备
		{
			device.angle = HARDWAEW_PKG.DEFAULTANGLE;
			setRolution(720, device);
			break;
		}
		case HARDWAEW_PKG.MF_SWING_1135: // G2.0 以太网口摇头 4:3设备
		{
			device.angle =HARDWAEW_PKG.DEFAULTANGLE;
			setRolution(1280, device);
			break;
		}
		case HARDWAEW_PKG.MF_SWING_2135: // G2.0 以太网口摇头16:9设备
		{
			device.angle = HARDWAEW_PKG.DEFAULTANGLE;
			setRolution(1080, device);
			break;
		}
		case HARDWAEW_PKG.LM_VR_2135_1720: // LM 1080P 配1720全景镜头 186度
		{
			device.angle = 186;
			setRolution(1080, device);
			device.maxFinger = 218f;  //
			device.minFinger = 142.0f;
			break;
		}
		case HARDWAEW_PKG.DH_VR_5230_1720: // LM 1080P 配1720全景镜头 186度
		{
			device.angle = 186;
			setRolution(1080, device);
			device.maxFinger = 218f;  //
			device.minFinger = 142.0f;
			break;
		}
		case HARDWAEW_PKG.SY_STD_5230: // LM 1080P 配1720全景镜头 186度
		{
			device.angle = 186;
			setRolution(1080, device);
			device.maxFinger = 218f;  //
			device.minFinger = 142.0f;
			break;
		}
		case HARDWAEW_PKG.SY_SWING_5230: // LM 1080P 配1720全景镜头 186度
		{
			device.angle = 186;
			setRolution(1080, device);
			device.maxFinger = 218f;  //
			device.minFinger = 142.0f;
			break;
		}
		case HARDWAEW_PKG.CM_BELL_VR_9732_5112: // 普通16:9设备 720卡片机
			   device.angle = 120;
			   setRolution(720, device);
			   device.maxFinger = 210f;  //
			   device.minFinger = 150.0f;
			   break;
			 case HARDWAEW_PKG.CM_BELL_VR_5230_2466: // SC2135 1080P 配2466全景镜头 160度
				{
					device.angle = 160;
					device.maxFinger = 218f;  //
					device.minFinger = 142.0f;
					setRolution(1080, device);
					break;
				}
		}
		
		return device;
	}
	public void setRolution(int resolution,HARDWAEW_INFO device){
		switch(resolution){
		case 720:{
			device.height = 720;
			device.width = 1280;
			device.resolution =720;
			break;
		}
		case 960:{
			device.height = 960;
			device.width = 960;
			device.resolution =960;
			break;
		}
		case 1080:{
			device.height = 1080;
			device.width = 1920;
			device.resolution = 1080;
			break;
		}
		case 1280:{
			device.height = 960;
			device.width = 1280;
			device.resolution = 1280;
			break;
		}
		}
		
	}
	
	
 
	public VRConfig() {
		vertexbuffer = ByteBuffer.allocateDirect(GELS_BUFFER_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
		texturebuffer = ByteBuffer.allocateDirect(GELS_BUFFER_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer(); 
		indicebuffer  =  ByteBuffer.allocateDirect(GELS_BUFFER_SIZE);
		sizebuffer  =  ByteBuffer.allocateDirect(20);

	}
	
	public static void reSetBuffer(){
		vertexbuffer = ByteBuffer.allocateDirect(GELS_BUFFER_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
		texturebuffer = ByteBuffer.allocateDirect(GELS_BUFFER_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer(); 
		indicebuffer  =  ByteBuffer.allocateDirect(GELS_BUFFER_SIZE);
		sizebuffer  =  ByteBuffer.allocateDirect(20);
	}
	public static VRConfig getInstance() {
		if (vrConfig == null) {
			vrConfig = new VRConfig();
		}
		return vrConfig;
	}
	
//	public native int HemiSphere(int numSlices, int width,float rotate, Object vertexbuffer,Object texturebuffer,Object indicebuffer,Object sizebuffer);
	//picture width and height
//	public native int AspectSphere(int numSlices, int width, int height,Object vertexbuffer,Object texturebuffer,Object indicebuffer,Object sizebuffer);
	public native int HemiSphere(int numSlices,float fishradius,int jxoffset, int yoffset, float aperture,float rotate, Object vertexbuffer,Object texturebuffer,Object indicebuffer,Object sizebuffer);
	public native int AspectSphere(int numSlices, int width, int height, int xoffset, int yoffset, float aperture,Object vertexbuffer,Object texturebuffer,Object indicebuffer,Object sizebuffer);
	public native int AspectCircle(int numSlices, int width, int height,Object vertexbuffer,Object texturebuffer,Object indicebuffer,Object sizebuffer); 
	public native int Cylinder(int numSlices, float height,float fishradius, int xoffset, int yoffset, float aperture, Object vertexbuffer,Object texturebuffer,Object indicebuffer,Object sizebuffer);
	public native int Rectangle(int numSlices, int width, int height,Object vertexbuffer,Object texturebuffer,Object indicebuffer,Object sizebuffer); 
//	public native static int HemiSphere(int numSlices, float radius, Object vertexbuffer, Object texturebuffer,Object indicebuffer );

	public native int ParseYUV(int width, int height, int colorformat, byte[] yuv,Object ybuffer,Object ubuffer,Object vbuffer);
	public native int YUV2BmpARGB(int width, int height, int colorformat, byte[] yuv,int[]rgba);
	public native int YUV2ARGB(int width, int height, int[] argb,Object ybuffer,Object ubuffer,Object vbuffer);
	public native int ARGB2YUV(int width, int height, int[] argb,Object ybuffer,Object ubuffer,Object vbuffer);

	public static boolean isVRdevice(int hardware_pkg ){

		if (hardware_pkg > HARDWAEW_PKG.deviceTypeString.length || hardware_pkg < 0) {
			return false;
		}
		if (HARDWAEW_PKG.deviceTypeString[hardware_pkg].contains("VR")) {
			return true;
		}

		return false;
	}
	public static boolean isBELL(int hardware_pkg ){
		try {
			if(hardware_pkg > HARDWAEW_PKG.deviceTypeString.length || hardware_pkg < 0){
				return false;
			}
			if(HARDWAEW_PKG.deviceTypeString[hardware_pkg].contains("BELL")){
				return true;
			}
		}catch (IndexOutOfBoundsException e){
			return false;
		}
		return false;
	}

}
