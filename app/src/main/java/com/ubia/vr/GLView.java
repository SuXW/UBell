 package com.ubia.vr;

  

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.interfaceManager.DeviceStateCallbackInterface_Manager;
import cn.ubia.manager.CameraManagerment;

import com.ubia.IOTC.AVFrame;
import com.ubia.IOTC.Camera;
import com.ubia.IOTC.HARDWAEW_PKG;
import com.ubia.IOTC.IRegisterIOTCListener;
 
public class GLView extends GLSurfaceView implements IRegisterIOTCListener,OnGestureListener {

	public interface GLViewTouch{
		public void ubiaGLViewDoubleClick();
		public void ubiaGLViewClick();
	}
	private GLViewTouch myGLViewTouch;
    private final String TAG = "VideoPlayGLView"; 
    private GLRenderer mHardDecoderRenderer ;
    private ScaleGestureDetector mScaleDetector;
	private Camera mCamera;
	private CameraManagerment mCameraManagerment=CameraManagerment.getInstance();
	DeviceInfo mDeviceInfo;
    private int width;
    private int height;

    private float mTouchDownX;
    private float mTouchDownY;
    
    private float mMoveDownX = 0;
    private float mMoveDownY = 0;
    
    private int mTouchType;
    private final int DRAG = 1;
    private final int SCALE = 2; 
    private final int DOUBLECLICK = 3; 
	private Bitmap lastBitmap;
	private GestureDetector detector;  
    public void setRenderer(GLRenderer renderer) { 
    	mHardDecoderRenderer = renderer;
    	super.setRenderer(renderer);
    
    }
    public GLRenderer getRenderer() {
		 return mHardDecoderRenderer; 
	}
    
    
    @Override
    protected void onAttachedToWindow() {
        Utils.LOGD("surface onAttachedToWindow()");
        super.onAttachedToWindow();
        // setRenderMode() only takes effectd after SurfaceView attached to window!
        // note that on this mode, surface will not render util GLSurfaceView.requestRender() is
        // called, it's good and efficient -v-
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        Utils.LOGD("surface setRenderMode RENDERMODE_WHEN_DIRTY");
    }
    public GLView(Context context) {
        super(context);
      
		this.mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		detector = new GestureDetector(context, this);
		this.setEGLContextClientVersion(2);
		mHardDecoderRenderer	=  new  GLRenderer (context, this);
		this.setRenderer(mHardDecoderRenderer);
        
    }
    public GLView(Context context, AttributeSet attrs) {
    	super(context, attrs); 
    	 
			this.mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
			detector = new GestureDetector(context, this);
			this.setEGLContextClientVersion(2);
			mHardDecoderRenderer =  new GLRenderer (context, this);
			this.setRenderer(mHardDecoderRenderer);
    	}
    
	public void attachCamera(Camera var1, int var2,int installmode, DeviceInfo mDeviceInfo, Bitmap lastBitmap,Boolean showBitmap) {
		
		if(showBitmap)
		this.lastBitmap = lastBitmap;  
		this.mCamera = var1;
		this.mCamera.registerIOTCListener(this); 
 
	     var1.attachedMonitor = this;
	    if( mHardDecoderRenderer!=null)
	    {
	    	mHardDecoderRenderer.setHardware_pkg(var1.hardware_pkg);
	    	this.mHardDecoderRenderer.setCameraPutModel(installmode);
	    	 
	    } 
	    if(mDeviceInfo == null){
		    Log.e("","attachCamera error input mDeviceInfo ==null");
	    }
	    
		this.mDeviceInfo = mDeviceInfo;
	    
		if(this.mDeviceInfo ==null){
	    	Log.e("","attachCamera error this.mDeviceInfo ==null");
	    }
	}
	public void setCameraPutModel(int model){
		 if( mHardDecoderRenderer!=null)
		{
			 if(model>VRConfig.CameraPutModelFaceFront || model< 0){
				 model=VRConfig.CameraPutModelFaceFront;
			 }
			 this.mHardDecoderRenderer.setCameraPutModel(model);
		 }
//	     if(mSoftDecoderRenderer!=null){
//		       mSoftDecoderRenderer.setCameraPutModel(model);
//		    }
	}
	
	public void initSurfacemode(){

	 if( mHardDecoderRenderer!=null)
			this.mHardDecoderRenderer.initSurfacemode( );
	 
	}
	public void setCameraHardware_pkg(int hardware_pkg){

		 if(hardware_pkg>HARDWAEW_PKG.deviceTypeString.length || hardware_pkg< 0){
			 hardware_pkg=HARDWAEW_PKG.MF_STD_1145;
		 }
		 if( mHardDecoderRenderer!=null)
			 	mHardDecoderRenderer.setHardware_pkg(hardware_pkg);
	}

	public Bitmap getLastBitmap() {
		return lastBitmap;
	}
	public void setLastBitmap(Bitmap lastBitmap) {
		this.lastBitmap = lastBitmap;
	}
	public void deattachCamera() {
	 
		if (this.mCamera != null) {
			this.mCamera.unregisterIOTCListener(this);
          if(lastBitmap != null)
          {
        	  lastBitmap.recycle();
        	  lastBitmap = null;
          } 
		}
		  myGLViewTouch = null; 
		  if(mHardDecoderRenderer!=null)
		  mHardDecoderRenderer.release();
		  mHardDecoderRenderer  = null;
		  mScaleDetector = null;
		  System.gc();
	}

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
       	 if( mHardDecoderRenderer!=null)
       	 {
       		float mTouchDownX = detector.getFocusX();
       		float mTouchDownY= detector.getFocusY();
       		int width = GLView.this.getWidth();
	 	 
	 		float scaledefalut = mHardDecoderRenderer.getScaleDefalut();
	 		float scalelast  = mHardDecoderRenderer.getScale();
	 		float translationX = mHardDecoderRenderer.gettranslationX();
	 		float translationY= mHardDecoderRenderer.gettranslationY();
	 		float hasChange = width/2* scalelast ;
	 		float hasChangeScale = width/2.0f/scaledefalut;//每次移动一个translation代表的距离
	 		PointF  center = new PointF();
	 	    {
	 	            center.x =width/2+   (translationX*hasChangeScale);//圆心的偏移量
		 	    	center.y =width/2+   (translationY*hasChangeScale);
	 	    }  
	 	 
	 	   mHardDecoderRenderer.scaleByFloat(detector.getScaleFactor()); 
	 		float scalenow  = mHardDecoderRenderer.getScale();
	 		float scaleNew = (scalenow-1.0f);
	 		 PointF newCenter = new PointF();
		 	     {
		 	    	   newCenter.x =((float)center.x-mTouchDownX )/hasChange  ;//新圆心的偏移量
		 	    	   newCenter.y =((float)center.y-mTouchDownY)/hasChange  ;
		 	     } 
	 		if(!VRConfig.isVRdevice(mHardDecoderRenderer.getHardware_pkg()))
	 			mHardDecoderRenderer.moveTranslation( newCenter.x *scaleNew, -newCenter.y *scaleNew   ,true);//放大后图像大小-放大后图的触摸点 *(1-mHardDecoderRenderer.getstartMoveScale_Y())
	 		 
	 		requestRender();
			Log.v("IOTCameraptz", "实现双机事件  mTouchDownX："+mTouchDownX+"  mTouchDownY: "+mTouchDownY+"  center.x: "+center.x+"  center.y: "+center.y+"   scale:"+ scalenow+"   hasChange):"+ hasChange +"    newCenter.x:"+  newCenter.x+
					"   newCenter.y:"+  newCenter.y+"    distX:"+newCenter.x *scaleNew+"   distY:"+newCenter.y  *scaleNew *(1-mHardDecoderRenderer.getstartMoveScale_Y())) ;
       	 }
         
            return true;
        }
    }
    boolean touchMove = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

    	if(mScaleDetector == null || detector == null){
    		return false;
		}

        mScaleDetector.onTouchEvent(event);
        detector.onTouchEvent(event);  
   
        final float touchX = event.getX();
        final float touchY = event.getY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            	touchMove = true;
                mTouchDownX = touchX;
                mTouchDownY = touchY;
                mMoveDownX = touchX;
                mMoveDownY = touchY;
                mTouchType = DRAG; 
                if(myGLViewTouch!=null){
		 			myGLViewTouch.ubiaGLViewClick ();
		 		}
                break;
            case MotionEvent.ACTION_UP:{
            	if(mDeviceInfo!=null && !mDeviceInfo.online)
            		DeviceStateCallbackInterface_Manager.getInstance().DeviceStateCallbackInterface(mCamera.getmDevUID(), 0, MainCameraFragment.CONNSTATUS_RECONNECTION);
            		myhanHandler.sendMessageDelayed(myhanHandler.obtainMessage(1010, this),  50);
            		if (firstClick != 0 && System.currentTimeMillis() - firstClick > 300) {
            			count = 0;
            		}
            		count++;
            		if (count == 1) {
            			firstClick = System.currentTimeMillis();

           				 if (isHorizontal && mhandle !=null&& mTouchType!=SCALE&& mTouchType!=DOUBLECLICK && Math.abs( touchX - mTouchDownX)<50 && Math.abs( touchY - mTouchDownY)<50) {//原地双击
           					myhanHandler.sendMessageDelayed(myhanHandler.obtainMessage(1015, this),  300); 
           				 }
            	
            		} else if (count == 2) {
            			lastClick = System.currentTimeMillis();
            			// 两次点击小于300ms 也就是连续点击
            			if (lastClick - firstClick < 300) {// 判断是否是执行了双击事件
							changeSurfaceMode();
            				 /*if( this.mHardDecoderRenderer!=null){

            					 		int width = this.getWidth();
            					 		float scaledefalut = mHardDecoderRenderer.getScaleDefalut();
            					 		float scalelast  = mHardDecoderRenderer.getScale();
            					 		float translationX = mHardDecoderRenderer.gettranslationX();
            					 		float translationY= mHardDecoderRenderer.gettranslationY();
            					 		float startMoveScale_Y=mHardDecoderRenderer. getstartMoveScale_Y();
            					 		float hasChange = width/2* scalelast ;
            					 		float hasChangeScale = width/2.0f/scaledefalut;//每次移动一个translation代表的距离
            					 		
            					 	 
            					 	    mHardDecoderRenderer.changeSurfaceMode(); 
            					 		float scalenow  = mHardDecoderRenderer.getScale();
            					 		float scaleNew = (scalenow-1.0f);
            					 		PointF  center = new PointF();
            					 	    {
            					 	        center.x =width/2+   (translationX*hasChangeScale);//圆心的偏移量
               					 	    	center.y =width/2 +   (translationY*hasChangeScale);
            					 	    }  
            					 		 PointF newCenter = new PointF();
              					 	     {
              					 	    	   newCenter.x =((float)center.x-mTouchDownX )/hasChange  ;//新圆心的偏移量
              					 	    	   newCenter.y =((float)center.y-mTouchDownY)/hasChange  ;
              					 	     } 
            					 		if(!VRConfig.isVRdevice(mHardDecoderRenderer.getHardware_pkg()))
            					 			mHardDecoderRenderer.moveTranslation( newCenter.x *scaleNew, -newCenter.y *scaleNew /startMoveScale_Y  ,true);//放大后图像大小-放大后图的触摸点 *(1-mHardDecoderRenderer.getstartMoveScale_Y())
            					 		this.requestRender();
            					 		if(myGLViewTouch!=null){
            					 			myGLViewTouch.ubiaGLViewDoubleClick();
            					 		}
                        				Log.v("IOTCameraptz", "实现双机事件  mTouchDownX："+mTouchDownX+"  mTouchDownY: "+mTouchDownY+"  center.x: "+center.x+"  center.y: "+center.y+"   scale:"+ scalenow+"  width:"+ width +"    newCenter.x:"+  newCenter.x+
                        						"   newCenter.y:"+  newCenter.y+"    distX:"+newCenter.x *scaleNew+"   distY:"+newCenter.y  *scaleNew ) ;
							 }*/
            			     mTouchType = DOUBLECLICK;
            	         } 
            		}
            		
            		
        		 
            }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            	touchMove = true;
                mTouchType = SCALE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
            	myhanHandler.sendMessageDelayed(myhanHandler.obtainMessage(1010, this),  50);
                break;
            case MotionEvent.ACTION_MOVE:
            	touchMove = true;
                if (mTouchType == DRAG) {
                
                    float mx = touchX - mMoveDownX;
                    float my = touchY - mMoveDownY;
                    mMoveDownX = touchX;
                    mMoveDownY = touchY;
                    width = this.getWidth();
                    height = this.getHeight();
               	 if( mHardDecoderRenderer!=null)
                    mHardDecoderRenderer.transByPointF(new PointF(mx / width * 4, my / height * 4));
               
//               	 if( mSoftDecoderRenderer!=null)
//               		mSoftDecoderRenderer. transByPointF(new PointF(mx / width * 4, my / height * 4));
//               	 
                    float dx = touchX - mTouchDownX;
                    float dy = touchY - mTouchDownY;
                    Log.d("Angle", "dx:" +dx + "dy:"+ dy);
                    
                    dy *= 0.005;
                    dx *= 0.005;
//                  //maxwell 170110 for just rotate one axis once
                    {
                        if (Math.abs(dx) > Math.abs(dy)) dy = 0;
                        else dx = 0;
                    }
                    
                    //this.mRotateX  += (dy *85 / 100);
                   
                    //this.mRotateY -= (dx *85 / 100);
               	 if( mHardDecoderRenderer!=null)
                    mHardDecoderRenderer.touchesMoved(dx, dy,false);
//            	 if( mSoftDecoderRenderer!=null)
//            		 mSoftDecoderRenderer.touchesMoved(dx, dy);
               	
                    this.requestRender();
                }

                break;
                default :
                {
                	myhanHandler.sendMessageDelayed(myhanHandler.obtainMessage(1010, this), 50);
                	break;
                }
        }

        return true;
    }
	@Override
	public void receiveChannelInfo(Camera var1, int var2, int var3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void receiveFrameData(Camera var1, int var2, Bitmap bitmap) {
		// TODO Auto-generated method stub
	 
		this.lastBitmap = bitmap;//onDrawBitmap(bitmap);// bitmap;
		if(mHardDecoderRenderer!=null&& this.lastBitmap!=null)
			this.mHardDecoderRenderer.setBitmap(this.lastBitmap );
	
	}
	@Override
	public void receiveFrameInfo(Camera var1, int var2, long var3, int var5,
								 int var6, AVFrame avFrame , int var8) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void receiveIOCtrlData(Camera var1, int var2, int var3, byte[] var4) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void receiveSessionInfo(Camera var1, int var2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void receiveCameraCtl(Camera var1, int var2, int var3, byte[] var4) {
		// TODO Auto-generated method stub
		
	}
	public void changeSurfaceMode() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() { 
				 if( mHardDecoderRenderer!=null)
				     mHardDecoderRenderer.changeSurfaceMode();
//				 if( mSoftDecoderRenderer!=null)
//					 mSoftDecoderRenderer. changeSurfaceMode();
				 requestRender();
			}
		}).start();
	}
	
	public void refreshData(  ) {
    	if(mHardDecoderRenderer!=null){
			mHardDecoderRenderer. refreshData();
		}

	
	}
	public void refreshBitmap(  final Bitmap bitmap) {
		// TODO Auto-generated method stub
		   Utils.LOGD("解码更新   refreshBitmap  w:"+width );
		  if( bitmap!=null)
		  {
			  this.lastBitmap = bitmap;   
			  if(this.mHardDecoderRenderer!=null )
			  {
				  mHardDecoderRenderer.refreshData();
				  new Thread(new Runnable() {
					
					@Override
					public void run() {
						 Utils.LOGD("解码更新  11111 getYUVByBitmap  w:"+width );
//						  byte[] yuvBitmap = getYUVByBitmap(bitmap); 
						  mHardDecoderRenderer.update(bitmap);
						   Utils.LOGD("解码更新   getYUVByBitmap  w:"+width );
//    					  mHardDecoderRenderer.update(bitmap.getWidth(), bitmap.getHeight(),yuvBitmap,21);  
						 
					}
				}).start();
			 
			  } 
		  } 
	
	}
	public void refreshPointF( ) {
		// TODO Auto-generated method stub
		if(!touchMove ){
		     if( this.mHardDecoderRenderer!=null && this.mHardDecoderRenderer.canRequestRender() )
		     {	
		    	 if (System.currentTimeMillis() -  this.mHardDecoderRenderer.getRefreshTime()>80)
		    	 {
		    		 this.mHardDecoderRenderer.refreshPointF( 0,0,true); 
		    		 requestRender();
		    	 }
		    	 else{
		    		 this.mHardDecoderRenderer.refreshPointF( 0,0,true);  
		    	 }
		     	
		     }
		}
	}
 
	private double lastDirectionDistan = 0;
	private boolean isMaxSpeedX = false;
	private int touchEventId = -9983761;
	private boolean isRightDirection = false; 
	@Override 
    public boolean onFling(MotionEvent var1, MotionEvent var2, float velocityX,   
    		float velocityY)  {
		if (!VRConfig.isVRdevice(mHardDecoderRenderer.getHardware_pkg())&& !mHardDecoderRenderer.hasScale()) {//not vr not scale
			 
			if (var1.getX() - var2.getX() > 100.0F && Math.abs(velocityX) > 0.0F) {
	
				if (this.mCamera != null ) {
					
					mCameraManagerment.userIPCPTZLeft(mCamera.getmDevUID(), 0);
					 
				}
			} else if (var2.getX() - var1.getX() > 100.0F
					&& Math.abs(velocityX) > 0.0F) {
				if (this.mCamera != null ) {
					mCameraManagerment.userIPCPTZRight(mCamera.getmDevUID(), 0);
					
				}
				
				
		
			} else if (var1.getY() - var2.getY() > 100.0F
					&& Math.abs(velocityY) > 0.0F) {
				if (this.mCamera != null ) {
					mCameraManagerment.userIPCPTZUp(mCamera.getmDevUID(), 0);
					
				}
			} else if (var2.getY() - var1.getY() > 100.0F
					&& Math.abs(velocityY) > 0.0F && this.mCamera != null ) {
				mCameraManagerment.userIPCPTZDown(mCamera.getmDevUID(), 0);
				
			}

			(new Handler()).postDelayed(new Runnable() {
				public void run() {
					if ( mCamera != null) {
						mCameraManagerment.userIPCPTZStop( mCamera.getmDevUID(), 0); 
						Log.v("onFling","MotionEvent  sendIOCtrl" );
					}

				}
			}, 1500L);
			Log.v("onFling","MotionEvent  finish" );
			return false;
		} 
		 Log.d("","onFling:velocityY:"+velocityY+"    velocityX:"+velocityX);
		 myhanHandler.sendMessageDelayed(myhanHandler.obtainMessage(touchEventId,this),0);
		 if(Math.abs(velocityX)>Math.abs(velocityY)){
			 isMaxSpeedX = true;//刷新x
			 lastDirectionDistan =(int) Math.abs(velocityX);
			 if(velocityX>0)
				 isRightDirection = true;
			 else
				 isRightDirection = false;
		 }
		 else{
			 lastDirectionDistan =(int) Math.abs(velocityY);
			 isMaxSpeedX = false;//refresh y
			 if(velocityY>0)
				 isRightDirection = true;
			 else
				 isRightDirection = false;
		 }
		return false;
	}
	@Override
	public void onLongPress(MotionEvent arg0) {
	}
	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}
	@Override
	public void onShowPress(MotionEvent arg0) {
	}
	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}
	
	private long firstClick;
	private long lastClick; // 计算点击的次数
	private int count;
	private Handler mhandle;
	private boolean isHorizontal;
 

	Handler myhanHandler =new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1015){
				 if (isHorizontal && mhandle !=null&& mTouchType!=SCALE&& mTouchType!=DOUBLECLICK   ) {//原地双击
   					mhandle.sendEmptyMessage(98);
   					mhandle.removeMessages(99);
   			 } 
			}
			if(mHardDecoderRenderer!=null){
				if( !VRConfig.getInstance().isVRdevice(mHardDecoderRenderer.getHardware_pkg()))
					return  ;
			}else{
				return;
			}

			if(msg.what==touchEventId) {
				if(lastDirectionDistan >100) {
						touchMove = true;
						Log.d("","myhanHandler:"+  "    lastX:"+lastDirectionDistan);
						myhanHandler.sendMessageDelayed(myhanHandler.obtainMessage(touchEventId, this), 50);
						lastDirectionDistan =   (0.8*lastDirectionDistan) ; //���ٺ���
						if(isMaxSpeedX)
						{
							if(isRightDirection){
								 if( mHardDecoderRenderer!=null)
								mHardDecoderRenderer.refreshPointF(  (float) (lastDirectionDistan*0.02) ,1,false); 
//								 if( mSoftDecoderRenderer!=null)
//									 mSoftDecoderRenderer. refreshPointF( (float) (lastDirectionDistan*0.02) ,1,false); 
							}else{
								 if( mHardDecoderRenderer!=null)
								mHardDecoderRenderer.refreshPointF( 0-(float) (lastDirectionDistan*0.02) ,1,false); 
//								 if( mSoftDecoderRenderer!=null)
//									 mSoftDecoderRenderer. refreshPointF( 0-(float) (lastDirectionDistan*0.02) ,1,false); 
							}
						}
						else
						{
							if(isRightDirection){
								 if( mHardDecoderRenderer!=null)
								mHardDecoderRenderer.refreshPointF( 1, (float) (lastDirectionDistan*0.02) ,false); 
//								 if( mSoftDecoderRenderer!=null)
//									 mSoftDecoderRenderer. refreshPointF( 1, (float) (lastDirectionDistan*0.02) ,false); 
							}else{
								 if( mHardDecoderRenderer!=null)
								mHardDecoderRenderer.refreshPointF( 1,0- (float) (lastDirectionDistan*0.02) ,false); 
//								 if( mSoftDecoderRenderer!=null)
//									 mSoftDecoderRenderer. refreshPointF( 1, 0-(float) (lastDirectionDistan*0.02) ,false); 
							}
						}
						 requestRender();
				}else { 
					touchMove = false;//拖拽减速过程算手势一部分
					 myhanHandler.removeMessages(touchEventId);
					 
				}
			}
			if(msg.what==1010){
				touchMove = false;
			}
		
		}
	};
	   public Bitmap onDrawBitmap(Bitmap dpDrawable ){
		   if(dpDrawable.getWidth()!=dpDrawable.getHeight()){
			   Bitmap result = Bitmap.createBitmap(dpDrawable.getWidth(), dpDrawable.getWidth(), Config.ARGB_8888);
	        	Canvas mCanvas = new Canvas(result);
	        	mCanvas.drawBitmap(dpDrawable, 0, (dpDrawable.getWidth()-dpDrawable.getHeight())/2, null); 
	        	return result;
		   }else{
			   return dpDrawable;
		   }
	    }
	   
	  public Bitmap getBitmap(){

	   	if(mHardDecoderRenderer!=null){
			lastBitmap=  mHardDecoderRenderer.getBitmap();
			return lastBitmap;
		}

		return null;
	  }
  
	public Handler getMhandle() {
		return mhandle;
	}
	public void setMhandle(Handler mhandle) {
		this.mhandle = mhandle;
	}
	public boolean isHorizontal() {
		return isHorizontal;
	}
	public void setHorizontal(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
		if(mHardDecoderRenderer!=null)
			mHardDecoderRenderer.reSetScale(isHorizontal);
	}
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}  
	  

	  public GLViewTouch getMyGLViewTouch() {
			return myGLViewTouch;
		}
		public void setMyGLViewTouch(GLViewTouch myGLViewTouch) {
			this.myGLViewTouch = myGLViewTouch;
		}
		public void restartPlay() {
			if(mHardDecoderRenderer!=null)
				mHardDecoderRenderer.refreshData();
		}
	  
}
