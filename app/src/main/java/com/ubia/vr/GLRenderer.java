package com.ubia.vr;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import cn.ubia.UbiaApplication;
import cn.ubia.UBell.R;
import cn.ubia.base.Constants;
import cn.ubia.util.PreferenceUtil;
import cn.ubia.util.StringUtils;

import com.ubia.IOTC.HARDWAEW_INFO;
import com.ubia.IOTC.HARDWAEW_PKG;
import com.ubia.IOTC.Packet;
import com.ubia.util.DateUtil;
import com.ubia.util.SourceUtil;
import com.ubia.vr.SphereMath.Point3F;
//import com.opengl.obj;


public class GLRenderer implements  Renderer {
    public static final String TAG = GLRenderer.class.getSimpleName();
    public static final float MAX_SCALE = 1.5f;
    public static final float MIN_SCALE = 1.0f;
    public static final float MAX_OVERTURE = 100.0f;
    public static final float MIN_OVERTURE = 70.0f;
 
	public  static final float     Rectangle_Scale = 1.0f;
	public     float startMoveScale_Y = 1.0f;
	public     float startMoveScale_X = 1.0f;
	public     float startScale_Ratio =9.0f/16.0f;
	public int clickCount = 1;
    
    private GLProgram proghard = new GLProgram(0);
  
    private int width;
    private int height;
    private int a_texCoord_h;
    private int a_position_h;
    private int u_texture_h;
    private int u_MVPMatrix_h;

    private int vertexBufferID[] = new int[1];
    private int textureBufferID[] = new int[1];
    private int vertexIndicesBufferID[] = new int[1];
	
    private int hardware_pkg;
    // program id
    private int _hardprogram; 
    // texture id
    private int _textureI;
    private int _textureII;
    private int _textureIII;
    // texture index in gles
    private int _tIindex;
    private int _tIIindex;
    private int _tIIIindex;
    private ISimplePlayer mParentAct;
    private GLSurfaceView mTargetSurface; 
    private ByteBuffer y;
    private ByteBuffer u;
    private ByteBuffer v;
	byte[] imageBuffer;
  
    // handles
    private int _positionHandle = -1, _coordHandle = -1;
    private int _yhandle = -1, _uhandle = -1, _vhandle = -1;
    private int _ytid = -1, _utid = -1, _vtid = -1;
    // vertices buffer

    // video width and height
    private int _video_width = -1;
    private int _video_height = -1; 
    ByteBuffer indicesBuffer;
    FloatBuffer vextbuffer;
    FloatBuffer textbuffer;
    int numVertices,numTexcoord,numIndice;
    
    Context context;
    private float cam_scale = 1.0f;
    private Point3F cam_eye = new Point3F(0f, 1.0f, 0f);
    private Point3F cam_head = new Point3F(0f, 0f, 1.0f);
    private int vCount;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int GEO_COORDS_PER_VERTEX = 3;
    private static final int TEX_COORDS_PER_VERTEX = 2;
    private static final int VERTEX_PER_SQUARE = 6;
    private Bitmap bitmap;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    float fingerRotationX = 0,fingerRotationY = 0,fingerRotationZ = 0;
    float translationX = 0, translationY = 0;
    float overture = 100.0f;   //default
    
    float maxFinger = 720;
    float minFinger = 0;
    int surfaceMode;    
    
    float bitmapRatio = 1.0f;
	private boolean hasCreatedBitmap = false;//是否刷新bitmap
	int cameraPutModel=-1; 
    
    private boolean shallUpdateGLES = true;
 
 
 
	public int getSurfaceMode() {
		return surfaceMode;
	}
	public void setSurfaceMode(int surfaceMode) {
		this.surfaceMode = surfaceMode;
	}
	public void validateTranslateXY(){
		if (cam_scale <= 1.0){
			this.translationX = 0;
			this.translationY = 0;
			cam_scale = 1.0f;
		}else{
			float xrange = (cam_scale - 1.0f);
			float yrange = (cam_scale - startMoveScale_Y) * startScale_Ratio;
			
			if(this.translationX < -xrange)
				this.translationX = -xrange;
			if(this.translationX > xrange)
				this.translationX = xrange;
			
			 if (cam_scale >startMoveScale_Y ) {
				 yrange = (cam_scale -startMoveScale_Y )*(startScale_Ratio);
             } else {
            	yrange = 0;
             } 
            
			if(this.translationY <  -yrange)
				this.translationY = -yrange;
			if(this.translationY > yrange) 
				this.translationY = yrange;
		}
	}
	public void moveTranslation (float distX , float distY,boolean force){
     	if( !VRConfig.getInstance().isVRdevice(hardware_pkg) && cam_scale==Rectangle_Scale && !force )
    		return  ;
        
     			translationX  = distX ; 
     			translationY  = distY ;  
            	validateTranslateXY();
             
                Log.w("TRotateXY"," [X="+ fingerRotationX + " Y=" + fingerRotationY + " Z="+ fingerRotationZ +"]  distX:"+distX+"   distY:"+distY+"  cam_scale:"+cam_scale );
    }
	
    public void touchesMoved(float distX , float distY,boolean force){
     	if( !VRConfig.getInstance().isVRdevice(hardware_pkg) && cam_scale==Rectangle_Scale && !force )
    		return  ;
          overture = 85.0f;
        
        //Log.e("Rotate","distX"+distX +" distY:"+distY);
        if(distX>0)
        	touchMax =false;
        else
        	touchMax = true;
        if (cameraPutModel == VRConfig.CameraPutModelFaceFront) {
            if (surfaceMode == VRConfig.surfaceHemiSpherePano) {
            	
                if(distX<0)
                	touchMax =false;
                else
                	touchMax = true;
                
                fingerRotationZ -= distX *  overture / 100;
                Log.d("TRotateXY"," [X="+ fingerRotationX + " Y=" + fingerRotationY + " Z="+ fingerRotationZ +"]  distX:"+distX+"   distY:"+distY);
                if (fingerRotationZ <= minFinger) {
                    fingerRotationZ = minFinger;
                }
                if (fingerRotationZ >= maxFinger) {
                    fingerRotationZ = maxFinger;
                }
            }else{
              
                float space = 1.5f;
                if (cam_scale >startMoveScale_X) {
                    space = cam_scale - startMoveScale_X;
                }else{
                	space = 0;
                }
                    
                translationX += distX *  overture / 100.0f * 0.04f;  
                if (this.translationX <= minFinger * space) {
                	this.translationX = minFinger * space;
                }
                    
                if (this.translationX >= maxFinger * space) {
                	this.translationX = maxFinger *space;
                }
                float hasChangeX = 0;
                if(space!=0)
                  hasChangeX = translationX/( overture / 100.0f * 0.04f);//已经移动的距离
                Log.w("TRotateXY"," [X="+ fingerRotationX + " Y=" + fingerRotationY + " Z="+ fingerRotationZ +"]  distX:"+distX+"   distY:"+distY+"  cam_scale:"+cam_scale+"  space:"+space);
                Log.w("TRotateXY"," [X= translation="+ translationX + " translationY=" + translationY + " Z="+ fingerRotationZ +"]  hasChangeX:"+hasChangeX+"   hasChangeY:"  +"   fovy:"+fovy);
 
                
                if (cam_scale >startMoveScale_Y ) {
                	 space = (cam_scale -startMoveScale_Y )*(startScale_Ratio);
                } else{
                     space = 0;
                    } 
                
                translationY -= distY *  overture / 100.0f * 0.04f; //0.02
                if (this.translationY <= minFinger * space) {
                	this.translationY = minFinger * space;
                }
                    
                if (this.translationY >= maxFinger * space) {
                	this.translationY = maxFinger *space;
                }
                float hasChangeY = 0;
                if(space!=0)
                 hasChangeY = translationY/( overture / 100.0f * 0.04f);
                
                Log.d("TRotateXY"," [X="+ fingerRotationX + " Y=" + fingerRotationY + " Z="+ fingerRotationZ +"]  distX:"+distX+"   distY:"+distY+"  cam_scale:"+cam_scale+"  space:"+space);
                Log.d("TRotateXY"," [X= translation="+ translationX + " translationY=" + translationY + " Z="+ fingerRotationZ +"]  hasChangeX:"+hasChangeX+"   hasChangeY:"+hasChangeY+"   fovy:"+fovy);
 
            }
        }else if (cameraPutModel == VRConfig.CameraPutModelFaceDown){
            if (surfaceMode == VRConfig.surfaceHemiSpherePano) {
                fingerRotationX += distY *  overture / 100;
                fingerRotationY += distX *  overture / 100;
                if (fingerRotationX <= minFinger) {
                    fingerRotationX = minFinger;
                }
                if (fingerRotationX >= maxFinger) {
                    fingerRotationX = maxFinger;
                }
//              myhanHandler.sendMessageDelayed(myhanHandler.obtainMessage(touchEventId,this),0);
//	       		 {
//	       			 lastDirectionDistan =(int) Math.abs(2000);
//	       			 isMaxSpeedX = false;
//	       			 isRightDirection = false; 
//	       		 }
            }else{
                fingerRotationX += distY *  overture / 100;
                fingerRotationY -= distX *  overture / 100;
              
                if (fingerRotationX <= minFinger) {
                    fingerRotationX = minFinger;
                }
                if (fingerRotationX >= maxFinger) {
                    fingerRotationX = maxFinger;
                }
                Log.e("Rotate","X:"+fingerRotationX +" scale:"+cam_scale);
            }
        }
    }
    public void transByPointF(PointF p) {
        Point3F x_axis = SphereMath.cross(cam_head, cam_eye).normalize();
        Point3F y_axis = cam_head;
        cam_eye = SphereMath.add(
                        cam_eye,
                        SphereMath.add(
                                SphereMath.mul(x_axis, p.x),
                                SphereMath.mul(y_axis, p.y)));
        if (cam_eye.y < 0.1f)
            cam_eye.y = 0.1f;
        
        cam_eye = cam_eye.normalize();
        double theta = Math.acos(cam_eye.z);
        double phi = Math.acos(cam_eye.x / Math.sin(theta));
        cam_head = new Point3F(
                (float) (Math.sin(theta - Math.PI / 2) * Math.cos(phi)),
                (float) (Math.sin(theta - Math.PI / 2) * Math.sin(phi)),
                (float) (Math.cos(theta - Math.PI / 2))).normalize();
    }
    public float lastScale= 0f;
    public void scaleByFloat(float scaleFactor) {
    	lastScale = this.cam_scale;
       this.cam_scale *= scaleFactor;
        overture /= scaleFactor;

        //cam_scale = cam_scale + ((scaleFactor - 1)/10);
        if (this.overture > MAX_OVERTURE) {
	        this.overture = MAX_OVERTURE;
	    }
	      
	    if (this.overture < MIN_OVERTURE) {
	        this.overture = MIN_OVERTURE;
	    }
      
      if(cameraPutModel == VRConfig.CameraPutModelFaceDown && surfaceMode == VRConfig.surfaceHemiSphereFull){
            if (this.cam_scale > MAX_SCALE/3) {
            	this.cam_scale = MAX_SCALE/3;
            }
            if (this.cam_scale < MIN_SCALE/2) {
            	this.cam_scale = MIN_SCALE/2;
            }
        }else if(cameraPutModel == VRConfig.CameraPutModelFaceFront && surfaceMode == VRConfig.surfaceHemiSphereFull){
        	if(!VRConfig.isVRdevice(hardware_pkg)){
        	    if (this.cam_scale > 4 ) {
                	this.cam_scale = 4;
                }
                if (this.cam_scale < 1) {
                	this.cam_scale = 1;
                } 
        	}
        	else{
        		float miniScale =  getDefalutMinScale();
        	    if (this.cam_scale > 4*MAX_SCALE) {
                	this.cam_scale = 4*MAX_SCALE;
                }
                if (this.cam_scale <miniScale) {
                	this.cam_scale =miniScale;
                } 
        	}
        
            
        }else{
        	float miniScale =  getDefalutMinScale();
            if (this.cam_scale > MAX_SCALE) {
            	this.cam_scale = MAX_SCALE;
            }
            if (this.cam_scale <  miniScale) {
            	this.cam_scale =  miniScale;
            }
        }
      	
        Log.e("scale","factor="+ scaleFactor + " scale:"+this.cam_scale + " overture:"+overture);
    }
    public GLRenderer(Context context, GLSurfaceView view ) {
       
    	this.context = context;  
        mTargetSurface = view; 
	
    }
    private void setGLRenderer(Context context, GLSurfaceView view ) {
        
    	this.context = context;  
        mTargetSurface = view; 
	
    }
    int windowW,windowH;
 
    
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        bitmapRatio = ((float)this.bitmap.getWidth())/((float)this.bitmap.getHeight());
        if(bitmapRatio==16f/9f && !hasCreatedBitmap){
        	hasCreatedBitmap = true;
        	surfaceMode = VRConfig.surfaceHemiSphereFull;
        	setCameraPutModel(VRConfig.CameraPutModelFaceFront);
        	Log.e("","setBitmap surfaceMode changemode ");
        }
    }
    private void resetHardHorizontalMode(int w, int h){
        
        bitmapRatio = ((float)w)/((float)h);
    	Log.e("","setBitmap surfaceMode changemode ");
        if(bitmapRatio==16f/9f && !hasCreatedBitmap){
        	hasCreatedBitmap = true;
        	surfaceMode = VRConfig.surfaceHemiSphereFull;
        	setCameraPutModel(VRConfig.CameraPutModelFaceFront);
        	Log.e("","setBitmap surfaceMode changemode ");
        }
    }
    public void resize(int w, int h) {
        this.width = w;
        this.height = h;
    }
    
 
    
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated");
        Utils.LOGD("GLFrameRenderer :: onSurfaceCreated");
    
    }
    
    /**
     * prepared for later use
     */
    public void setup(int position) {

            _textureI = GLES20.GL_TEXTURE0;
            _textureII = GLES20.GL_TEXTURE1;
            _textureIII = GLES20.GL_TEXTURE2;
            _tIindex = 0;
            _tIIindex = 1;
            _tIIIindex = 2;

    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged"); 
 
        useHardProgram();
        GLES20.glViewport(0, 0, width, height); 
    }
    
    public void getDefaultAngle()
    {
     	if( !VRConfig.getInstance().isVRdevice( hardware_pkg))
     	{
        	this.translationX = 0.0f;
        	this.translationY = 0.0f;
            this.fingerRotationX = 180.0f;
            this.fingerRotationY = 180.0f;
            this.fingerRotationZ = 0.0f;
            maxFinger = 1.0f;   //330,210
            minFinger = -1.0f; 
            cam_scale = Rectangle_Scale  ;
     	}else{
        cam_scale = 1.0f;
    	VRConfig mVRConfig = VRConfig.getInstance();
    	HARDWAEW_INFO device_hardware  = mVRConfig.getDeviceType(hardware_pkg);
    	switch(surfaceMode){
            case VRConfig.surfaceHemiSpherePano:
                switch (cameraPutModel) {
                    case VRConfig.CameraPutModelFaceDown:
                        this.fingerRotationX = 221;//231;//270.0f;
                        this.fingerRotationY = 0.0f;
                        this.fingerRotationZ = 0;
                        maxFinger = 308.0f; //2.40f;
                        minFinger = 221;//230.0f; //0.75f;
                        break;
                    case VRConfig.CameraPutModelFaceUp:
                        this.fingerRotationX = 221;//231//270.0f;
                        this.fingerRotationY = 0.0f;
                        this.fingerRotationZ = 0;
                        maxFinger = 308.0f; //2.40f;
                        minFinger = 221.0f; //230 //0.75f;
                        break;
                    case VRConfig.CameraPutModelFaceFront:
                        this.fingerRotationX = 90.0f; //90
                        this.fingerRotationY = 180.0f;//0
                        this.fingerRotationZ = 180.0f;
                        maxFinger = device_hardware.maxFinger;  //
                        minFinger =device_hardware.minFinger;
                       
                        break;
                    default:
                        break;
                }
                break;
            case VRConfig.surfaceHemiSphereFull:
                switch (cameraPutModel) {
                    case VRConfig.CameraPutModelFaceDown:
                        this.fingerRotationX = 45; //330.0f;
                        this.fingerRotationY = 0.0f;
                        this.fingerRotationZ = 0.0f;
                        maxFinger = 60;//360.0f;//1.0;
                        minFinger = -45;//210.0f;//-1.0;
                        cam_scale = 0.4f;
                        break;
                    case VRConfig.CameraPutModelFaceUp:
                        this.fingerRotationX = 45; //330.0f;
                        this.fingerRotationY = 0.0f;
                        this.fingerRotationZ = 0.0f;
                        maxFinger = 60;//360.0f;//1.0;
                        minFinger = -45;//210.0f;//-1.0;
                        cam_scale = 0.4f;
                        break;
                    case VRConfig.CameraPutModelFaceFront:
                    	this.translationX = 0.0f;
                    	this.translationY = 0.0f;
                        this.fingerRotationX = 180.0f;
                        this.fingerRotationY = 180.0f;
                        this.fingerRotationZ = 0.0f;
                        maxFinger = 1.0f;   //330,210
                        minFinger = -1.0f;
                        cam_scale = 2.4f;//2.92f;
                        break;
                    default:
                        break;
                }
                break;
            case VRConfig.surfaceCylinderFull:
                switch (cameraPutModel) {
                    case VRConfig.CameraPutModelFaceDown:
                    	this.fingerRotationX = 225;
                    	this.fingerRotationY = -180;
                    	this.fingerRotationZ = 0;
                        maxFinger = 270.0f; //2.40;
                        minFinger = 185.0f;
                        break;
                    case VRConfig.CameraPutModelFaceUp:
                        this.fingerRotationX = 45; //330.0f;
                        this.fingerRotationY = 0.0f;
                        this.fingerRotationZ = 0.0f;
                        maxFinger = 60;//360.0f;//1.0;
                        minFinger = -45;//210.0f;//-1.0;
                        cam_scale = 0.4f;
                        break;
                    case VRConfig.CameraPutModelFaceFront:
                        break;
                    default:
                        break;
                }
                break;
        }
     	}
    }
    private void genObjModel(){
    	int numIndices;
    	
    	VRConfig mVRConfig = VRConfig.getInstance();
    	HARDWAEW_INFO device_hardware  = mVRConfig.getDeviceType(hardware_pkg);
    	
//    	width = device_hardware.width;
//    	height = device_hardware.height;
//    	if(width == 0) width = 960;
//    	if(height == 0) height = 960;
    	float aperture = device_hardware.angle; 
    	
     	Log.e("ubiaGLES", "genObjModel===>  mVideoWidth:"+width+"   mVideoHeight:"+height);
     	if (!VRConfig.isVRdevice(hardware_pkg)) {
     		
     		surfaceMode = VRConfig.surfaceHemiSphereFull;
     		numIndices =mVRConfig.Rectangle(200,   width,   height ,VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
     	}else{
		if(surfaceMode == VRConfig.surfaceCylinderFull){
			 numIndices = mVRConfig.Cylinder(200, 1.5f,480,0,0,aperture,VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
		}else{
	    	 if(cameraPutModel == VRConfig.CameraPutModelFaceFront && surfaceMode == VRConfig.surfaceHemiSpherePano){
	    		
	    			 numIndices = mVRConfig.AspectSphere(200, width, height,0,0,aperture,VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
	    	 }else if(cameraPutModel == VRConfig.CameraPutModelFaceFront && surfaceMode == VRConfig.surfaceHemiSphereFull){
	    		 if(VRConfig.isBELL(hardware_pkg))
	    	     	 numIndices =mVRConfig.Rectangle(200,   width,   height ,VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
	    		 else
	    			 numIndices = mVRConfig.AspectCircle(200, width, height,VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
	    	 }else if(cameraPutModel == VRConfig.CameraPutModelFaceDown && surfaceMode == VRConfig.surfaceHemiSphereFull)
	    	 {
	    		 numIndices = mVRConfig.HemiSphere(200, 480,0,0,aperture,-90.0f,VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
	    	 }else{
	    		 numIndices = mVRConfig.HemiSphere(200, width/2,0,0,aperture,90.0f,VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
	    	 }
    	 }
     	}
    	//int numIndices = mVRConfig.HemiSphere(200, 1.0f, VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
    	//int numIndices = mVRConfig.AspectSphere(200, 1.0f,9.0f/16, VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
    	//int numIndices = mVRConfig.AspectCircle(64, 1.0f, 9.0f/16,VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
    	vCount = numIndices;//Packet.byteArrayToInt_Little(VRConfig.sizebuffer.array(), 12) ;
    	if(textbuffer!=null)
		{
			textbuffer.clear();
			textbuffer= null;
		}
		if(vextbuffer!=null)
		{
			vextbuffer.clear();
			vextbuffer= null;
		}
		if(indicesBuffer!=null)
		{
			indicesBuffer.clear();
			indicesBuffer= null;
		}
		try {
            int sizebufferoffset = VRConfig.sizebuffer.arrayOffset();
            numVertices = Packet.byteArrayToInt_Little(VRConfig.sizebuffer.array(), 0 + sizebufferoffset);
            numTexcoord = Packet.byteArrayToInt_Little(VRConfig.sizebuffer.array(), 4 + sizebufferoffset);
            numIndice = Packet.byteArrayToInt_Little(VRConfig.sizebuffer.array(), 8 + sizebufferoffset);

            Log.e("genObjModel", "vCount:" + vCount + " numVertices:" + numVertices + " numTexcoord:" + numTexcoord + " numIndice:" + numIndice);
            indicesBuffer = ByteBuffer.allocateDirect(numIndice * 2).order(ByteOrder.nativeOrder());
            byte indiceslist[] = new byte[numIndice * 2];
            VRConfig.indicebuffer.position(0);
            VRConfig.indicebuffer.get(indiceslist, 0, numIndice);
            indicesBuffer.put(indiceslist).position(0);

            vextbuffer = ByteBuffer.allocateDirect(numVertices * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            float tmplist[] = new float[numVertices];
            VRConfig.vertexbuffer.position(0);
            VRConfig.vertexbuffer.get(tmplist, 0, numVertices);
            vextbuffer.put(tmplist).position(0);
            Log.d("vertexbuff", "v0:" + tmplist[0] + " v1: " + tmplist[1]);
            textbuffer = ByteBuffer.allocateDirect(numTexcoord * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            float textlist[] = new float[numTexcoord];
            VRConfig.texturebuffer.position(0);
            VRConfig.texturebuffer.get(textlist, 0, numTexcoord);
            textbuffer.put(textlist).position(0);
            Log.d("textbuffer", "v0:" + textlist[0] + " v1:" + textlist[1]);
            tmplist = null;
            textlist = null;
            indiceslist = null;
            System.gc();
        }catch (Exception e){
    	    e.printStackTrace();
        }
    }
    float fovy = 100;
    private void ubiaGLESDraw(){
  
        float aspect = 1.0f;//ratio;

        
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setIdentityM(mProjectionMatrix, 0);
        switch(surfaceMode){
        	case VRConfig.surfaceHemiSpherePano:
            	if(HARDWAEW_PKG.MF_VR_1145_1866 == hardware_pkg){
            	     Matrix.perspectiveM(mProjectionMatrix, 0, 83.0f, aspect, 0.1f, 400f);
            	}else if(HARDWAEW_PKG.CM_BELL_VR_9732_5112 == hardware_pkg){
            		 Matrix.perspectiveM(mProjectionMatrix, 0, 65.0f, aspect, 0.1f, 400f);
            	}else{
            		Matrix.perspectiveM(mProjectionMatrix, 0, this.overture, aspect, 0.1f, 400f);
            	}
                Matrix.scaleM(mModelMatrix, 0, cam_scale, cam_scale, cam_scale);
                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationX, 1.0f, 0.0f, 0);
                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationY, 0.0f, 1.0f, 0);
                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationZ, 0.0f, 0.0f, 1.0f);
                break;
        	case VRConfig.surfaceHemiSphereFull:
        		if(cameraPutModel == VRConfig.CameraPutModelFaceFront){
        			if(!VRConfig.isVRdevice(hardware_pkg))
            			Matrix.perspectiveM(mProjectionMatrix, 0, fovy, 1.0f, 0.1f, 400f); 
        			else
        				Matrix.perspectiveM(mProjectionMatrix, 0, 100, 1.0f, 0.1f, 400f);
        			Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 2.0f, 0f, 0.0f, 0f, 0f, 1.0f, 1.0f);
        			Matrix.translateM(mProjectionMatrix, 0, this.translationX, 0f, 0);
        			Matrix.translateM(mProjectionMatrix, 0, 0,this.translationY, 0);
            		Matrix.scaleM(mModelMatrix, 0, cam_scale, cam_scale, cam_scale);
        		}else{
            		Matrix.setLookAtM(mViewMatrix, 0, 0, 2.0f, 0.0f, 0f, 0.0f, 0f, 0f, 1.0f, 1.0f);
            		//调用此方法计算产生透视投影矩阵
               		//Matrix.translateM(mModelMatrix, 0, 0f, 0f, 0.5f);            		

            		//Matrix.frustumM(mProjectionMatrix, 0, -1, 1, 1, -1, 2f, 5);
                    Matrix.perspectiveM(mProjectionMatrix, 0, 12, aspect, 0.1f, 400f);
            		Matrix.scaleM(mModelMatrix, 0, cam_scale*0.4f, cam_scale*0.4f, cam_scale*0.4f);
        		}

    
                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationX, 1.0f, 0.0f, 0);
                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationY, 0.0f, 1.0f, 0);
                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationZ, 0.0f, 0.0f, 1.0f);
        		break;
        	case VRConfig.surfaceCylinderFull:
        		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        		Matrix.setIdentityM(mModelMatrix, 0);

        		//float ratio = (float) width / height;
        		//Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, cam_scale, 25);
        		
        		Matrix.perspectiveM(mProjectionMatrix, 0, 45, aspect, 0.1f, 400f);
        		Matrix.scaleM(mModelMatrix, 0, cam_scale, cam_scale, cam_scale);
                
        		Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationX, 1.0f, 0.0f, 0);
                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationY, 0.0f, 1.0f, 0);
                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationZ, 0.0f, 0.0f, 1.0f);
                if(cameraPutModel == VRConfig.CameraPutModelFaceDown){
                	Matrix.setLookAtM(mViewMatrix, 0,
	   	                 0, 0, 3.0f,
	   	                 0, 0, 0,
	   	                 0, 0.5f, -1.0f);
                }else if(cameraPutModel == VRConfig.CameraPutModelFaceUp){
                	Matrix.setLookAtM(mViewMatrix, 0,
		   	                 0, 0, 3.0f,
		   	                 0, 0, 0,
		   	                 0, 0.5f, 1.0f);
                	
                }
                
        		break;
        	default:
        		break;
        }

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(u_MVPMatrix_h, 1, false, mMVPMatrix, 0);
        //Log.e("","--------drawEEEEYUVFrame-------------");
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, vCount, GLES20.GL_UNSIGNED_SHORT, 0);
        //Log.e("","--------drawDDDDYUVFrame-------------");
    }
    long   refreshTime = 0L;
 
    public long getRefreshTime() {
    	return refreshTime;
    }
 
	@Override
    public void onDrawFrame(GL10 gl) {
		refreshTime = System.currentTimeMillis();
        if (y != null && u!=null && v!=null) {
	        synchronized (y) {
	        	if(width == mScreenWidth && _video_width == width&& mVideoWidth == width && y!=null && u!=null && updateYUV){
	        		//Log.i("parseYUV","_video_width:"+_video_width + " mScreenWidth:"+mScreenWidth + "  width:"+width+"  shouldDrawFrame:"+shouldDrawFrame+"   y:"+y.limit()+"   "+y.arrayOffset()+"   position:"+y.position());

	        	   shouldDrawFrame = true;
	        	
	        	}else{
 	        		return;
	        	}
	        	//Utils.LOGD("解码更新   开始刷新UI图像  w:"+width );
	            	if(shallUpdateGLES){
	            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
	            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//	            		setupHardBuffers();
	            		genObjModel();
	    	    		shallUpdateGLES = false;
	    	        	GLES20.glFinish();
	    	    		 
		    	        GLES20.glDisableVertexAttribArray(_positionHandle);
		    	        GLES20.glDisableVertexAttribArray(_coordHandle);

		    	    	GLES20.glDeleteBuffers(1, vertexBufferID, 0);
		    	    	GLES20.glDeleteBuffers(1, textureBufferID, 0);
		    	        GLES20.glDeleteBuffers(1, vertexIndicesBufferID,0);
	                // reset position, have to be done
	                y.position(0);
	                u.position(0);
	                v.position(0);
//	                Utils.LOGD("onDrawFrame  mVideoWidth:"+width+"  mVideoHeight:"+height);
	                GLES20.glEnable(GLES20.GL_DEPTH_TEST);
	            
	    	    	
	                GLES20.glGenBuffers(1, vertexBufferID, 0);
	    	    	GLES20.glGenBuffers(1, textureBufferID, 0);
	    	        GLES20.glGenBuffers(1, vertexIndicesBufferID,0);
	    	    	}
	                buildHardTextures(y, u, v, width, height);  
	    	        
	    	        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vertexIndicesBufferID[0]);
	    	        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, vCount*2, indicesBuffer, GLES20.GL_DYNAMIC_DRAW);
	    	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferID[0]);
	    	        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, numVertices*4, vextbuffer, GLES20.GL_DYNAMIC_DRAW);
	    	        GLES20.glEnableVertexAttribArray(_positionHandle);
	    	        GLES20.glVertexAttribPointer(_positionHandle, GEO_COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 12, 0);
	    	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureBufferID[0]);
	    	        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, numTexcoord * 4, textbuffer, GLES20.GL_DYNAMIC_DRAW);
	    	        GLES20.glEnableVertexAttribArray(_coordHandle);
	    	        GLES20.glVertexAttribPointer(_coordHandle, TEX_COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 8,0);

	    	        // bind textures
	    	        GLES20.glActiveTexture(_textureI);
	    	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _ytid);
	    	        GLES20.glUniform1i(_yhandle, _tIindex);

	    	        GLES20.glActiveTexture(_textureII);
	    	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _utid);
	    	        GLES20.glUniform1i(_uhandle, _tIIindex);

	    	        GLES20.glActiveTexture(_textureIII);
	    	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _vtid);
	    	        GLES20.glUniform1i(_vhandle, _tIIIindex);
                    try {
	    	        ubiaGLESDraw();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
	    	 



	            }
	        }
    	 
    }
    
    public static void checkGlError(String op) {
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR) {
        }
    }
 
    
 	public void changeSurfaceMode() { 
		Log.d("SurfaceMode","Change from " + surfaceMode + " PutMode: " + cameraPutModel);
		if(VRConfig.getInstance().isVRdevice(hardware_pkg)){
		switch(cameraPutModel){

     	case VRConfig.CameraPutModelFaceDown:
		        switch(surfaceMode){
			        case VRConfig.surfaceHemiSpherePano:
			        	surfaceMode = VRConfig.surfaceHemiSphereFull;
			        	break;
			        case VRConfig.surfaceHemiSphereFull:
			        	surfaceMode = VRConfig.surfaceCylinderFull;
			        	break;
			        case VRConfig.surfaceCylinderFull:
			        	surfaceMode = VRConfig.surfaceHemiSpherePano;
			        	break;
		        }
		        break;
    	case VRConfig.CameraPutModelFaceUp:
     	case VRConfig.CameraPutModelFaceFront:
		        switch(surfaceMode){
			        case VRConfig.surfaceHemiSpherePano:
			        	surfaceMode = VRConfig.surfaceHemiSphereFull;
			        	break;
			        case VRConfig.surfaceHemiSphereFull:
			        	surfaceMode = VRConfig.surfaceHemiSpherePano;
			        	break;
			        default:
			        	break;
		        }
		        break;
		  default:
		    	 break;
		}
		Log.d("SurfaceMode","Change to " + surfaceMode);
		getDefaultAngle();		
		}
		else{
		 	surfaceMode = VRConfig.surfaceHemiSphereFull;
		 	switch (clickCount) {
			case 1:
				clickCount = 2;
				break;
			case 2:
				clickCount = 4;
				break;
			case 4:
				clickCount = 1;
				break;
			default:
				clickCount = 1;
				break;
			}
		 	  cam_scale = Rectangle_Scale* clickCount  ; 
		 	 
		}

		shallUpdateGLES = true;

	}
    public int getCameraPutModel() {
		return cameraPutModel;
	}
	public void setCameraPutModel(int cameraPutModel) {
		HARDWAEW_INFO device = VRConfig.getInstance().getDeviceType(
				hardware_pkg);
		if (device != null) {
			if (device.height == device.width) {
				this.cameraPutModel = cameraPutModel;
			} else {
				this.cameraPutModel = VRConfig.CameraPutModelFaceFront;
			}
		} else {
			this.cameraPutModel = cameraPutModel;
		}
		initSurfacemode();
		getDefaultAngle();
		shallUpdateGLES = true;
	}
	public void refreshPointF(float lastX, float lastY,boolean auto) {
		if(auto)//自动刷新
		{
			if (!(cameraPutModel == VRConfig.CameraPutModelFaceFront && surfaceMode == VRConfig.surfaceHemiSphereFull))
				if (VRConfig.isVRdevice(hardware_pkg))
					autoMoved(0.4f, 0);
		}else//手动滑动
		{
			if(lastX==1)
			{
				touchesMoved(0, 0.4f*lastY,false) ;
			}
			else if(lastY==1)
					{
						if((cameraPutModel==VRConfig.CameraPutModelFaceFront && surfaceMode==VRConfig.surfaceHemiSphereFull))
							touchesMoved( 0.1f*lastX,0,false) ;//move speed
						else
							touchesMoved( 0.4f*lastX,0,false) ;
					}
	}
	}
	boolean touchMax = false;
	public void autoMoved(float distX , float distY){
	    	Log.d("ARotateXY"," [X="+ fingerRotationX + " Y="+fingerRotationY);
	        float overture = 85.0f;
	        if(touchMax)
	        	distX = 0-Math.abs(distX);//向x轴负方向
	        else
	        	distX = Math.abs(distX);
	        
	        if (cameraPutModel == VRConfig.CameraPutModelFaceFront) {
	            if (surfaceMode == VRConfig.surfaceHemiSpherePano) {
	                fingerRotationZ += distX *  overture / 100;
	                if (fingerRotationZ <= minFinger) {
	                    fingerRotationZ = minFinger;
	                    touchMax = false;
	                    }
	                if (fingerRotationZ >= maxFinger) {
	                    fingerRotationZ = maxFinger;
	                    touchMax = true;
	                }
	            }else{
	            	//don't rotate as front
//	                fingerRotationX += distY *  overture / 100;
//	                fingerRotationZ -= distX *  overture / 100;
//	                if (fingerRotationX <= 210f) {
//	                    fingerRotationX = 210f;
//	                    touchMax = false;
//	                    }
//	                if (fingerRotationX >= 330f) {
//	                    fingerRotationX = 330;
//	                    touchMax = true;
//	                }
//	                if (fingerRotationZ <= -60) {
//	                    fingerRotationZ = -60;
//	                    touchMax = false;
//	                }
//	                if (fingerRotationZ >= 60) {
//	                    fingerRotationZ = 60;
//	                    touchMax = true;
//	                }
	            }
	        }else if (cameraPutModel == VRConfig.CameraPutModelFaceDown){
	            if (surfaceMode == VRConfig.surfaceHemiSpherePano) {
	                //fingerRotationX += distY *  overture / 100;
	                fingerRotationY += distX *  overture / 100;
	                if (fingerRotationX <= minFinger) {
	                    fingerRotationX = minFinger;
	                    touchMax = false;
	                }
	                if (fingerRotationX >= maxFinger) {
	                    fingerRotationX = maxFinger;
	                    touchMax = true;
	                }
	                if(fingerRotationX>minFinger){
	                	fingerRotationX-=(fingerRotationX-minFinger)*0.2;
	                }
	                else  if(fingerRotationX<=minFinger){
	                	fingerRotationX = minFinger;
	                }
	      
	            }else{
	                fingerRotationX += distY *  overture / 100;
	                fingerRotationY -= distX *  overture / 100;
	                if (fingerRotationX <= minFinger) {
	                    fingerRotationX = minFinger;
	                    touchMax = false;
	                }
	                if (fingerRotationX >= maxFinger) {
	                    fingerRotationX = maxFinger;
	                    touchMax = true;
	                }
	            }
	        }
	}
    public void printMatrix(String str,float[] mtrix){
    	int i =0;
    	Log.d("Matrix","=============="+str+"=============");
    	Log.d("Matrix", " "+mtrix[i+0]+" "+mtrix[i+1]+" "+mtrix[i+2]+" "+mtrix[i+3]);
    	i = 4;
    	Log.d("Matrix", " "+mtrix[i+0]+" "+mtrix[i+1]+" "+mtrix[i+2]+" "+mtrix[i+3]);
    	i = 8;
    	Log.d("Matrix", " "+mtrix[i+0]+" "+mtrix[i+1]+" "+mtrix[i+2]+" "+mtrix[i+3]);
    	i = 12;
    	Log.d("Matrix", " "+mtrix[i+0]+" "+mtrix[i+1]+" "+mtrix[i+2]+" "+mtrix[i+3]);
    }

    private int mScreenWidth, mScreenHeight;
    private int mVideoWidth, mVideoHeight;

    /**
     * this method will be called from native code, it happens when the video is about to play or
     * the video size changes.
     */
    public void update(int w, int h, byte[] chunk,int colorFormat) {
//        Utils.LOGD("解码更新    INIT E  w:"+w+"   h:"+h);
        if(!hasCreatedBitmap){
        	resetHardHorizontalMode(w,h);
        }
        
        if (w > 0 && h > 0) {
            // 调整比例
            if (mScreenWidth > 0 && mScreenHeight > 0) {
                float f1 = 1f * mScreenHeight / mScreenWidth;
                float f2 = 1f * h / w;
                if (f1 == f2) {
                    createBuffers(GLProgram.squareVertices);
                } else if (f1 < f2) {
                    float widScale = f1 / f2;
                     createBuffers(new float[] { -widScale, -1.0f, widScale, -1.0f, -widScale, 1.0f, widScale,
                            1.0f, });
                } else {
                    float heightScale = f2 / f1;
                    createBuffers(new float[] { -1.0f, -heightScale, 1.0f, -heightScale, -1.0f, heightScale, 1.0f,
                            heightScale, });
                }
            }
            // 初始化容器
            if (w != mVideoWidth ||  h != mVideoHeight ||_video_width!=w ||VRConfig.width!=w || mScreenWidth!=w ) {
            	int[] yuvStrides = { w, w / 2, w / 2};
            	 
            	VRConfig.width = w;
            	VRConfig.height = h;
            	mScreenWidth = w;
            	mScreenHeight = h;
                this.mVideoWidth = w;
                this.mVideoHeight = h;
                _video_height = h;
                _video_width = w;
                width = w;
                height = h;
                int yarraySize = w * h;
                int uvarraySize = yarraySize / 4;
                synchronized (this) { 
                    y = ByteBuffer.allocateDirect(yarraySize).order(ByteOrder.nativeOrder()) ;
                    u = ByteBuffer.allocateDirect(uvarraySize).order(ByteOrder.nativeOrder()) ;
                    v = ByteBuffer.allocateDirect(uvarraySize).order(ByteOrder.nativeOrder()) ;
                }
                shallUpdateGLES= true;
            }
            VRConfig mVRConfig = VRConfig.getInstance(); 
        	this.imageBuffer = chunk;
            if(colorFormat==21 || colorFormat==19)
            {
            	synchronized (this) {
            		//Log.i("parseYUV","colorformat:"+colorFormat + " w:"+w + "  h:"+h);
             		int valueRet = mVRConfig.ParseYUV(w,h,colorFormat,chunk,y,u,v); 
                  if(width == mScreenWidth && _video_width == width&& mVideoWidth == width && valueRet>=0){
            			updateYUV = true;
                        mTargetSurface.requestRender();
                        
          	      };
          	      if(valueRet<0){
          	    	   Activity a = (Activity)context;  
            		   a.runOnUiThread(new Runnable() {
    					
    					@Override
    					public void run() {
    						// TODO Auto-generated method stub
    	       	    	  	Toast.makeText(UbiaApplication.getInstance().getApplicationContext(), SourceUtil.getPictureNosupportStringSource(UbiaApplication.getInstance().getApplicationContext())+"", Toast.LENGTH_LONG).show();

    					}
    				}); 
          	      }
            	}
            }
         
      
           
        }
        //chunk =null;
        if (mParentAct != null) {
        	mParentAct.onPlayStart();
        }
//        Utils.LOGD("INIT X");
    }
    
    /**
     * this method will be called from native code, it happens when the video is about to play or
     * the video size changes.
     */
    public void update(  Bitmap bitmap) {
        if (bitmap == null) {  
	          return  ;  
	      }  
	        width = bitmap.getWidth();  
	        height = bitmap.getHeight();   
	      int size = width * height;   
	      int pixels[] = new int[size];  
	    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);   
    	int w = bitmap.getWidth();
    	int h = bitmap.getHeight();
        Log.d("guo,,GLRenderer","mScreenWidth:"+mScreenWidth+",,mScreenHeight:"+mScreenHeight);
        if (w > 0 && h > 0) {
            // 调整比例
            if (mScreenWidth > 0 && mScreenHeight > 0) {
                float f1 = 1f * mScreenHeight / mScreenWidth;
                float f2 = 1f * h / w;
                if (f1 == f2) {
                    createBuffers(GLProgram.squareVertices);
                } else if (f1 < f2) {
                    float widScale = f1 / f2;
                     createBuffers(new float[] { -widScale, -1.0f, widScale, -1.0f, -widScale, 1.0f, widScale,
                            1.0f, });
                } else {
                    float heightScale = f2 / f1;
                    createBuffers(new float[] { -1.0f, -heightScale, 1.0f, -heightScale, -1.0f, heightScale, 1.0f,
                            heightScale, });
                }
            }
            // 初始化容器
            if (w != mVideoWidth ||  h != mVideoHeight ||_video_width!=w ||VRConfig.width!=w || mScreenWidth!=w ) {
            	int[] yuvStrides = { w, w / 2, w / 2};
             
            	VRConfig.width = w;
            	VRConfig.height = h;
            	mScreenWidth = w;
            	mScreenHeight = h;
                this.mVideoWidth = w;
                this.mVideoHeight = h;
                _video_height = h;
                _video_width = w;
                width = w;
                height = h;
                int yarraySize = w * h;
                int uvarraySize = yarraySize / 4;
                synchronized (this) {
 
                    y = ByteBuffer.allocateDirect(yarraySize).order(ByteOrder.nativeOrder()) ;
                    u = ByteBuffer.allocateDirect(uvarraySize).order(ByteOrder.nativeOrder()) ;
                    v = ByteBuffer.allocateDirect(uvarraySize).order(ByteOrder.nativeOrder()) ;
                }
                shallUpdateGLES= true;
            }
            VRConfig mVRConfig = VRConfig.getInstance(); 
        
            synchronized (this) {
             	int value = 	mVRConfig.ARGB2YUV(w,h,pixels,y,u,v);
             	Log.d("guo,,","解码更新   width: "+width+",,_video_width:"+_video_width+",,mVideoWidth:"+mVideoWidth+"..");
            		
               if(width == mScreenWidth && _video_width == width&& mVideoWidth == width && value>=0){
                   Log.d("guo..GLRenderer","解码更新.....");
            	   	updateYUV = true;
                    mTargetSurface.requestRender();
        	    };
        	   if(value<0){
        		   Activity a = (Activity)context;  
        		   a.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
                        Toast.makeText(UbiaApplication.getInstance().getApplicationContext(), SourceUtil.getPictureNosupportStringSource(UbiaApplication.getInstance().getApplicationContext())+"", Toast.LENGTH_LONG).show();

					}
				}); 
     
           		}
            }
            
    
           
        }

        if (mParentAct != null) {
        	mParentAct.onPlayStart();
        }
//        Utils.LOGD("INIT X");
    }
    
  public boolean   canRequestRender(){
	  return shouldDrawFrame;
  }
    /**
     * this method will be called from native code, it's used for passing yuv data to me.
     */
    public void update(byte[] ydata, byte[] udata, byte[] vdata) {
        synchronized (this) {
            y.clear();
            u.clear();
            v.clear();
            y.put(ydata, 0, ydata.length);
            u.put(udata, 0, udata.length);
            v.put(vdata, 0, vdata.length);
            updateYUV = false;
        }
 
    }

    /**
     * this method will be called from native code, it's used for passing play state to activity.
     */
    public void updateState(int state) {
        Utils.LOGD("updateState E = " + state);
        if (mParentAct != null) {
            mParentAct.onReceiveState(state);
        }
        Utils.LOGD("updateState X");
    }
 
 

	public Bitmap getBitmap() { 
		if(this.imageBuffer==null || mVideoWidth==0 || mVideoHeight==0)
			return null;
		  VRConfig mVRConfig = VRConfig.getInstance();
		  int [] rgba = new int[mVideoWidth * mVideoHeight];
		  int yarraySize = mVideoWidth * mVideoHeight;
          int uvarraySize = yarraySize / 4;
          synchronized (this) {
        	  if(y==null){
        		  return null;
        		 // y = ByteBuffer.allocateDirect(yarraySize).order(ByteOrder.nativeOrder()) ;
        	  }
        	  if(u==null){
        		  return null;
//                  u = ByteBuffer.allocateDirect(uvarraySize).order(ByteOrder.nativeOrder()) ;
        	  }

        	  if(v==null){
        		  return null;
//        		     v = ByteBuffer.allocateDirect(uvarraySize).order(ByteOrder.nativeOrder()) ;
        	  }
         
          }
		synchronized (this) {
			mVRConfig.YUV2ARGB(mVideoWidth, mVideoHeight, rgba, y, u, v);
		}
		
	    Bitmap bmp = Bitmap.createBitmap(mVideoWidth, mVideoHeight, Bitmap.Config.ARGB_8888);
	    bmp.setPixels(rgba, 0 , mVideoWidth, 0, 0, mVideoWidth, mVideoHeight);
	    rgba = null;
	    imageBuffer = null;
	    System.gc();
	    return bmp;
	    
		//return rawByteArray2RGBABitmap2(this.imag;eBuffer,mVideoWidth,mVideoHeight);
	}
	public Bitmap rawByteArray2RGBABitmap2(byte[] data, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];
            for (int i = 0; i < height; i++)
                for (int j = 0; j < width; j++) {
                    int y = (0xff & ((int) data[i * width + j]));
                    int u = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 0]));
                    int v = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 1]));
                    y = y < 16 ? 16 : y;
                  
          
                    
                    int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                    int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                    int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));
                    r = r < 0 ? 0 : (r > 255 ? 255 : r);
                    g = g < 0 ? 0 : (g > 255 ? 255 : g);
                    b = b < 0 ? 0 : (b > 255 ? 255 : b);
                    rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
                }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.setPixels(rgba, 0 , width, 0, 0, width, height);
	    rgba = null;
	    System.gc();
        return bmp;
    }

	   public void setupHardBuffers() {
	    	int numIndices;
	    	VRConfig mVRConfig = VRConfig.getInstance();
	    	width = VRConfig.width;
	    	height = VRConfig.height;
	    	if(width == 0) width = 960;
	    	if(height == 0) height = 960;
	    	Log.e("ubiaGLES", "setupHardBuffers===>  mVideoWidth:"+width+"   mVideoHeight:"+height);
			if(surfaceMode == VRConfig.surfaceCylinderFull){
				 numIndices = mVRConfig.Cylinder(200, 1.5f,480,0,0,180,VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
			}else{
		    	 if(cameraPutModel == VRConfig.CameraPutModelFaceFront && surfaceMode == VRConfig.surfaceHemiSpherePano){
		    		 numIndices = mVRConfig.AspectSphere(200, width, height,0,0,180,VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
		    	 }else if(cameraPutModel == VRConfig.CameraPutModelFaceFront && surfaceMode == VRConfig.surfaceHemiSphereFull){
		    		 numIndices = mVRConfig.AspectCircle(200, width, height,VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
		    	 }else if(cameraPutModel == VRConfig.CameraPutModelFaceDown && surfaceMode == VRConfig.surfaceHemiSphereFull)
		    	 {
		    		 numIndices = mVRConfig.HemiSphere(200, 480,0,0,180,-90.0f,VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
		    	 }else{
		    		 numIndices = mVRConfig.HemiSphere(200, width/2,0,0,180,90.0f,VRConfig.vertexbuffer, VRConfig.texturebuffer, VRConfig.indicebuffer, VRConfig.sizebuffer);
		    	 }
	    	 }
	    	vCount = numIndices;//Packet.byteArrayToInt_Little(VRConfig.sizebuffer.array(), 12) ;
	    	Log.d("",">>>>>>>>"+StringUtils.getHex(VRConfig.indicebuffer.array(),32));
	     	Log.d("","??>>>>>>>>>"+StringUtils.getHex(VRConfig.sizebuffer.array(),20));
	     	int sizebufferoffset = VRConfig.sizebuffer.arrayOffset();
	    	numVertices = Packet.byteArrayToInt_Little(VRConfig.sizebuffer.array(), 0+sizebufferoffset);
	        numTexcoord = Packet.byteArrayToInt_Little(VRConfig.sizebuffer.array(), 4+sizebufferoffset);
	        numIndice = Packet.byteArrayToInt_Little(VRConfig.sizebuffer.array(), 8+sizebufferoffset);
	        Log.e("setupBuffers","vCount:"+vCount+" numVertices:"+numVertices +" numTexcoord:"+numTexcoord+" numIndice:"+numIndice);
	        indicesBuffer = ByteBuffer.allocateDirect(numIndice * 2).order(ByteOrder.nativeOrder()) ;
	    	byte indiceslist[] = new byte[numIndice*2];
			VRConfig.indicebuffer.position(0);
			VRConfig.indicebuffer.get(indiceslist, 0, numIndice);
			indicesBuffer.put(indiceslist).position(0);
			vextbuffer = ByteBuffer.allocateDirect(numVertices * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
			float tmplist[] = new float[numVertices];
			VRConfig.vertexbuffer.position(0);
			VRConfig.vertexbuffer.get(tmplist, 0, numVertices);
			vextbuffer.put(tmplist).position(0);
	    	Log.d("vertexbuff","v0:"+tmplist[0]+" v1: "+ tmplist[1]);
	    	textbuffer = ByteBuffer.allocateDirect(numTexcoord * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
			float textlist[] = new float[numTexcoord];
			VRConfig.texturebuffer.position(0);
			VRConfig.texturebuffer.get(textlist, 0, numTexcoord);
			textbuffer.put(textlist).position(0);
	    	Log.d("textbuffer","v0:"+textlist[0]+" v1:"+ textlist[1]);
	    	shallUpdateGLES = false;
	    	getDefaultAngle();
	        GLES20.glDisable(GLES20.GL_CULL_FACE);                          //�رձ������
    }
	    /**
	     * build a set of textures, one for R, one for G, and one for B.
	     */
	    public void buildHardTextures(Buffer y, Buffer u, Buffer v, int width, int height) {
	        boolean videoSizeChanged = (width != _video_width || height != _video_height);
	        //Log.e("ubiaGLES", "buildTextures===>");
	  
	        if (videoSizeChanged) {
	            _video_width = width;
	            _video_height = height;
	            //Utils.LOGD("buildTextures videoSizeChanged: w=" + _video_width + " h=" + _video_height);
	        }
	    
	        // building texture for Y data
	        if (_ytid < 0 || videoSizeChanged) {
//	            if (_ytid >= 0) {
//	                //Utils.LOGD("glDeleteTextures Y");
//	                GLES20.glDeleteTextures(1, new int[] { _ytid }, 0);
//	                checkGlError("glDeleteTextures");
//	            }
	            if(_ytid < 0){
		            // GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
		            int[] textures = new int[1];
		            GLES20.glGenTextures(1, textures, 0);
		            checkGlError("glGenTextures y");
		            _ytid = textures[0];
		            Utils.LOGD("glGenTextures Y = " + _ytid);
	            }
	        }
	        if(_ytid >=0){
		        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _ytid);
		        checkGlError("glBindTexture y");
		        
		        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, _video_width, _video_height, 0,GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, y);
		        checkGlError("glTexImage2D y");
		        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	        }
	        // building texture for U data
	        if (_utid < 0 || videoSizeChanged) {
//	            if (_utid >= 0) {
//	                Utils.LOGD("glDeleteTextures U");
//	                GLES20.glDeleteTextures(1, new int[] { _utid }, 0);
//	                checkGlError("glDeleteTextures");
//	            }
	        	if(_utid < 0){
		            int[] textures = new int[1];
		            GLES20.glGenTextures(1, textures, 0);
		            checkGlError("glGenTextures u");
		            _utid = textures[0];
		            Utils.LOGD("glGenTextures U = " + _utid);
	        	}
	        }
	        if(_utid >= 0){
		        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _utid);
		        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, _video_width / 2, _video_height / 2, 0,
		                GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, u);
		        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	        }
	        // building texture for V data
	        if (_vtid < 0 || videoSizeChanged) {
//	            if (_vtid >= 0) {
//	                Utils.LOGD("glDeleteTextures V");
//	                GLES20.glDeleteTextures(1, new int[] { _vtid }, 0);
//	                checkGlError("glDeleteTextures");
//	            }
	        	if(_vtid < 0){
	            int[] textures = new int[1];
	            GLES20.glGenTextures(1, textures, 0);
	            checkGlError("glGenTextures v");
	            _vtid = textures[0];
	            Utils.LOGD("glGenTextures V = " + _vtid);
	        	}
	        }
	        
	        if(_vtid>=0){
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _vtid);
	        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, _video_width / 2, _video_height / 2, 0,
	                GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, v);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
	        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	        }
       //     Utils.LOGD("buildHardTextures  end"  );
	    }

	    /**
	     * render the frame
	     * the YUV data will be converted to RGB by shader.
	     */
	    public void drawHardYUVFrame() {
	       
	        if(shallUpdateGLES){
//	        	setupHardBuffers();
	        	genObjModel();
	        	shallUpdateGLES = false;
	        }
	        
	        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            
	    	GLES20.glGenBuffers(1, vertexBufferID, 0);
	    	GLES20.glGenBuffers(1, textureBufferID, 0);
	        GLES20.glGenBuffers(1, vertexIndicesBufferID,0);
	        
	        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vertexIndicesBufferID[0]);
	        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, vCount*2, indicesBuffer, GLES20.GL_DYNAMIC_DRAW);
	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferID[0]);
	        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, numVertices*4, vextbuffer, GLES20.GL_DYNAMIC_DRAW);
	        GLES20.glEnableVertexAttribArray(_positionHandle);
	        GLES20.glVertexAttribPointer(_positionHandle, GEO_COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 12, 0);
	        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureBufferID[0]);
	        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, numTexcoord * 4, textbuffer, GLES20.GL_DYNAMIC_DRAW);
	        GLES20.glEnableVertexAttribArray(_coordHandle);
	        GLES20.glVertexAttribPointer(_coordHandle, TEX_COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 8,0);

	        // bind textures
	        GLES20.glActiveTexture(_textureI);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _ytid);
	        GLES20.glUniform1i(_yhandle, _tIindex);

	        GLES20.glActiveTexture(_textureII);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _utid);
	        GLES20.glUniform1i(_uhandle, _tIIindex);

	        GLES20.glActiveTexture(_textureIII);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _vtid);
	        GLES20.glUniform1i(_vhandle, _tIIIindex);
	        
	        float aspect = 1.0f;//ratio;
	        float fovy = 100;

	        Matrix.setIdentityM(mModelMatrix, 0);
	        Matrix.setIdentityM(mViewMatrix, 0);
	        Matrix.setIdentityM(mProjectionMatrix, 0);
	        switch(surfaceMode){
	        	case VRConfig.surfaceHemiSpherePano:
	                Matrix.perspectiveM(mProjectionMatrix, 0, this.overture, aspect, 0.1f, 400f);
	                Matrix.scaleM(mModelMatrix, 0, cam_scale, cam_scale, cam_scale);
	                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationX, 1.0f, 0.0f, 0);
	                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationY, 0.0f, 1.0f, 0);
	                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationZ, 0.0f, 0.0f, 1.0f);
	                break;
	        	case VRConfig.surfaceHemiSphereFull:
	        		if(cameraPutModel == VRConfig.CameraPutModelFaceFront){
	        			Matrix.perspectiveM(mProjectionMatrix, 0, fovy, 1.0f, 0.1f, 400f);
	        			Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 2.0f, 0f, 0.0f, 0f, 0f, 1.0f, 1.0f);
	        			Matrix.translateM(mProjectionMatrix, 0, this.translationX, 0f, 0);
	        		}else{
	            		Matrix.setLookAtM(mViewMatrix, 0, 0, 2.0f, 0.0f, 0f, 0.0f, 0f, 0f, 1.0f, 1.0f);
	                    Matrix.perspectiveM(mProjectionMatrix, 0, 30, aspect, 0.1f, 400f);
	        		}
	        		Matrix.scaleM(mModelMatrix, 0, cam_scale, cam_scale, cam_scale);
	                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationX, 1.0f, 0.0f, 0);
	                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationY, 0.0f, 1.0f, 0);
	                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationZ, 0.0f, 0.0f, 1.0f);
	        		break;
	        	case VRConfig.surfaceCylinderFull:
	        		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
	        		Matrix.setIdentityM(mModelMatrix, 0);
	        		Matrix.perspectiveM(mProjectionMatrix, 0, 45, aspect, 0.1f, 400f);
	        		Matrix.scaleM(mModelMatrix, 0, cam_scale, cam_scale, cam_scale);
	        		Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationX, 1.0f, 0.0f, 0);
	                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationY, 0.0f, 1.0f, 0);
	                Matrix.rotateM(mModelMatrix, 0, (float)fingerRotationZ, 0.0f, 0.0f, 1.0f);
	                if(cameraPutModel == VRConfig.CameraPutModelFaceDown){
	                	Matrix.setLookAtM(mViewMatrix, 0,
		   	                 0, 0, 3.0f,
		   	                 0, 0, 0,
		   	                 0, 0.5f, -1.0f);
	                }else if(cameraPutModel == VRConfig.CameraPutModelFaceUp){
	                	Matrix.setLookAtM(mViewMatrix, 0,
			   	                 0, 0, 3.0f,
			   	                 0, 0, 0,
			   	                 0, 0.5f, 1.0f);
	                }
	        		break;
	        	default:
	        		break;
	        }
	        
	        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
	        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
	        GLES20.glUniformMatrix4fv(u_MVPMatrix_h, 1, false, mMVPMatrix, 0);
	        GLES20.glDrawElements(GLES20.GL_TRIANGLES, vCount, GLES20.GL_UNSIGNED_SHORT, 0);
	       	GLES20.glFinish();

	        GLES20.glDisableVertexAttribArray(_positionHandle);
	        GLES20.glDisableVertexAttribArray(_coordHandle);

	    	GLES20.glDeleteBuffers(1, vertexBufferID, 0);
	    	GLES20.glDeleteBuffers(1, textureBufferID, 0);
	        GLES20.glDeleteBuffers(1, vertexIndicesBufferID,0);
	        
	        Log.e("","--------drawHardYUVFrame-------------");
	    }
	    /**
	     * these two buffers are used for holding vertices, screen vertices and texture vertices.
	     */
	    void createBuffers(float[] vert) {

	        if(shallUpdateGLES){
//	        	setupHardBuffers();
	        	genObjModel();
	        	shallUpdateGLES = false;
	        }
	    }
	 

		private void useHardProgram(){
		 
            if (!proghard.isProgramBuilt()) {
	        		proghard.buildProgram();
	        		   // Select the program.
	                GLES20.glUseProgram(proghard.getProgram());
	                 checkGlError("glUseProgram");
	        		  _hardprogram = proghard.  getProgram();
	        	   u_MVPMatrix_h = GLES20.glGetUniformLocation(_hardprogram, "u_MVPMatrix");
	               /*
	                * get handle for "vPosition" and "a_texCoord"
	                */
	               _positionHandle = GLES20.glGetAttribLocation(_hardprogram, "a_position");
	               Utils.LOGD("_positionHandle = " + _positionHandle);
	               checkGlError("glGetAttribLocation a_position");
	               if (_positionHandle == -1) {
	                   throw new RuntimeException("Could not get attribute location for a_position");
	               }
	               _coordHandle = GLES20.glGetAttribLocation(_hardprogram, "a_texCoord");
	               Utils.LOGD("_coordHandle = " + _coordHandle);
	               checkGlError("glGetAttribLocation a_texCoord");
	               if (_coordHandle == -1) {
	                   throw new RuntimeException("Could not get attribute location for a_texCoord");
	               }

	               /*
	                * get uniform location for y/u/v, we pass data through these uniforms
	                */
	               _yhandle = GLES20.glGetUniformLocation(_hardprogram, "tex_y");
	               Utils.LOGD("_yhandle = " + _yhandle);
	               checkGlError("glGetUniformLocation tex_y");
	               if (_yhandle == -1) {
	                   throw new RuntimeException("Could not get uniform location for tex_y");
	               }
	               _uhandle = GLES20.glGetUniformLocation(_hardprogram, "tex_u");
	               Utils.LOGD("_uhandle = " + _uhandle);
	               checkGlError("glGetUniformLocation tex_u");
	               if (_uhandle == -1) {
	                   throw new RuntimeException("Could not get uniform location for tex_u");
	               }
	               _vhandle = GLES20.glGetUniformLocation(_hardprogram, "tex_v");
	               Utils.LOGD("_vhandle = " + _vhandle);
	               checkGlError("glGetUniformLocation tex_v");
	               if (_vhandle == -1) {
	                   throw new RuntimeException("Could not get uniform location for tex_v");
	               } 
	               setup(0);
	               Utils.LOGD("GLFrameRenderer :: buildProgram done");
	        }
	    	  
	    	 GLES20.glUseProgram(_hardprogram);
	     	 checkGlError("glUsehardProgram");
		}
		public int getHardware_pkg() {
			return hardware_pkg;
		}
		public void setHardware_pkg(int hardware_pkg) {
				this.hardware_pkg = hardware_pkg; 
				HARDWAEW_INFO device_hardware =	VRConfig.getInstance().getDeviceType(hardware_pkg);
				VRConfig.width  = device_hardware.width;
				VRConfig.height = device_hardware.height;
		        getDefaultAngle();
		        shallUpdateGLES = true;
		}
		public boolean hasScale(){
			if(cam_scale==Rectangle_Scale){
				return false;
			}else{
				return true;
			}
		}
		
		public void reSetScale(boolean isHorizontal){
			HARDWAEW_INFO device_hardware =	VRConfig.getInstance().getDeviceType(hardware_pkg);
			if(device_hardware.type!=VRConfig.VRDEVICE){
				if(isHorizontal){
					if(device_hardware.resolution==720){
						startMoveScale_X = 1.0f;
		        		fovy =53;
		        	    cam_scale = Rectangle_Scale* clickCount  ;
		        	    startMoveScale_Y = 1.0f;
		        	}else if(device_hardware.resolution==1280){
		        		startMoveScale_X =(float)device_hardware.width/(float)device_hardware.height;
		        		fovy =66;
		        	    cam_scale = Rectangle_Scale* clickCount  ;
		        	    startMoveScale_Y = 1.0f;
		        	}else if(device_hardware.resolution==960){
		        		startMoveScale_X = (float)device_hardware.width/(float)device_hardware.height;
		        		fovy =66;
		        	    cam_scale = Rectangle_Scale* clickCount  ;
		        	    startMoveScale_Y = 1.0f;
		        	}else if(device_hardware.resolution==1080){
		        		startMoveScale_X = 1.0f;
		        		fovy =53;
		        	    cam_scale = Rectangle_Scale* clickCount  ;
		        	    startMoveScale_Y = 1.0f;
		        	    
		        	}
				}else{
					startMoveScale_X = 1.0f; 
	        		fovy =53;
	        	    cam_scale = Rectangle_Scale* clickCount  ;
	        	    startMoveScale_Y = (float)device_hardware.width/(float)device_hardware.height;//放大倍数为正方形边框大小时比例
 				}
			}
	    		lastScale = this.cam_scale;
    	    	startScale_Ratio = (float)device_hardware.height/(float)device_hardware.width ;// 图像比例
    	    	shallUpdateGLES = true;
		}
		
	private float scaleDefalut;
	public float	getScaleDefalut(){
			return  1.0f/( 85.0f / 100.0f * 0.04f);//已经移动的距离;  
		}
	public float	getScale (){
		return cam_scale; //scaleDefalut = 1.0f/( overture / 100.0f * 0.04f);
	}
	public float	gettranslationX(){
		return translationX/( 85.0f / 100.0f * 0.04f);  //scaleDefalut = 1.0f/( overture / 100.0f * 0.04f);
	}
	public float	gettranslationY(){
		return translationY/( 85.0f / 100.0f * 0.04f);  //scaleDefalut = 1.0f/( overture / 100.0f * 0.04f);
	}
	public float	getstartMoveScale_Y(){
		return startMoveScale_Y;  //scaleDefalut = 1.0f/( overture / 100.0f * 0.04f);
	}

	public void initSurfacemode() {
		 surfaceMode = VRConfig.surfaceHemiSpherePano;
		if (!VRConfig.isVRdevice(hardware_pkg)) {
	     	 surfaceMode = VRConfig.surfaceHemiSphereFull;
	     }
		if(this.cameraPutModel==VRConfig.CameraPutModelFaceFront){
			 surfaceMode = VRConfig.surfaceHemiSphereFull;
		}
		
	}
	
	private float getDefalutMinScale(){
     	if( !VRConfig.getInstance().isVRdevice( hardware_pkg))
     	{
        	 
            return  Rectangle_Scale;
     	}else{
       
    	VRConfig mVRConfig = VRConfig.getInstance();
    	HARDWAEW_INFO device_hardware  = mVRConfig.getDeviceType(hardware_pkg);
    	switch(surfaceMode){
            case VRConfig.surfaceHemiSpherePano:
                switch (cameraPutModel) {
                    case VRConfig.CameraPutModelFaceDown:
                    	 return 1.0f; 
                    case VRConfig.CameraPutModelFaceUp:
                    	 return 1.0f; 
                    case VRConfig.CameraPutModelFaceFront:
                    	 return 1.0f; 
                    default:
                        break;
                }
                break;
            case VRConfig.surfaceHemiSphereFull:
                switch (cameraPutModel) {
                    case VRConfig.CameraPutModelFaceDown:
                    	 return 0.4f;
                       
                    case VRConfig.CameraPutModelFaceUp:
                    	 return 0.4f;
                         
                    case VRConfig.CameraPutModelFaceFront:
                    	 return 2.4f;//2.92f;
                       
                    default:
                        break;
                }
                break;
            case VRConfig.surfaceCylinderFull:
                switch (cameraPutModel) {
                    case VRConfig.CameraPutModelFaceDown:
                    	 return 1.0f;
                 
                    case VRConfig.CameraPutModelFaceUp:
                    	return  0.4f;
                      
                    case VRConfig.CameraPutModelFaceFront:
                    	 return 1.0f;
                    default:
                        break;
                }
                break;
        }
     	}
		return 1.0f;
    }
	Boolean shouldDrawFrame = false;
	Boolean updateYUV = false;
	public void refreshData() {
		mScreenHeight = -1;
		mScreenWidth = -1;
		width = -1;
		height = -1;
		_video_width = -1;
		shallUpdateGLES = true;
		shouldDrawFrame = false;
		updateYUV = false;
		if(y!=null)
	      {
			y.clear(); 
	      }
		if(u!=null)
		{
			u.clear(); 
		}
		if(v!=null)
		{
			v.clear(); 
		}
		System.gc();
	}
	public void release() {
		mScreenHeight = -1;
		mScreenWidth = -1;
		width = -1;
		height = -1;
		_video_width = -1;
		shallUpdateGLES = true;
		shouldDrawFrame = false;
		updateYUV = false;
		if(y!=null)
	      {
			y.clear(); 
			y= null;
	      }
		if(u!=null)
		{
			u.clear();
			u= null;
		}
		if(v!=null)
		{
			v.clear();
			v= null;
		}
		  
		if(textbuffer!=null)
		{
			textbuffer.clear();
			textbuffer= null;
		}
		if(vextbuffer!=null)
		{
			vextbuffer.clear();
			vextbuffer= null;
		}
		if(indicesBuffer!=null)
		{
			indicesBuffer.clear();
			indicesBuffer= null;
		} 
		System.gc();
	}
}
