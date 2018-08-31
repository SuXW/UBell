package com.ubia.IOTC;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import cn.ubia.UBell.R;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.manager.CameraManagerment;

/**
 * MediaCodec 
 ***/
 

@SuppressLint({ "NewApi" })
public class Monitor extends SurfaceView implements Callback,
		IRegisterIOTCListener, OnTouchListener, OnGestureListener {

	private static final float DEFAULT_MAX_ZOOM_SCALE = 2.0F;
	private static final int DRAG = 1;
	private static final int FLING_MIN_DISTANCE = 100;
	private static final int FLING_MIN_VELOCITY = 0;
	private static final int NONE = 0;
	private static final int PTZ_DELAY = 1500;
	private static final int PTZ_SPEED = 8;
	private static final int ZOOM = 2;
	private int mAVChannel = -1;
	private Camera mCamera;
	private int mCurVideoHeight = 0;
	private int mCurVideoWidth = 0;
	private float mCurrentMaxScale = 2.0F;
	private float mCurrentScale = 1.0F;
	public boolean mEnableDither = false;
	private GestureDetector mGestureDetector;
	private Bitmap mLastFrame;
	private Handler mhandle;
	private boolean isHorizontal;

	public Bitmap getmLastFrame() {
		return mLastFrame;
	}

	public void setmLastFrame(Bitmap mLastFrame) {
		this.mLastFrame = mLastFrame;
	}

	private Bitmap myLastFrame;

	public Bitmap getmyLastFrame() {
		return myLastFrame;
	}

	public void setmyLastFrame(Bitmap mLastFrame) {
		this.myLastFrame = mLastFrame;
	}
	
	public void setHandle(Handler mhandle){
		this.mhandle = mhandle;
	}
	
	public void setIsHorizontal(boolean isHorizontal){
		this.isHorizontal = isHorizontal;
		hasdraw = false;
		this.handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(hasdraw) 
				 {
						removeCallbacks(this);
						setDefaultShow(mLastFrame,false);
		                Log.i("IOTCamera", (new StringBuilder("TTTTTT  Change canvas size (")).append(mRectCanvas.right - mRectCanvas.left).append(", ").append(mRectCanvas.bottom - mRectCanvas.top).append(")").toString());

				 }
				else
				{
					postDelayed(this, 500);
				}
			}
		}, 500);
		
	}
	private int reshowTryCount = 0;
	private Lock mLastFrameLock = new ReentrantLock();
	private long mLastZoomTime;
	private PointF mMidPoint = new PointF();
	private PointF mMidPointForCanvas = new PointF();
	private float mOrigDist = 0.0F;
	private Paint mPaint = new Paint();
	private int mPinchedMode = 0;
	private Rect mRectCanvas = new Rect();
	private Rect mRectMonitor1 = new Rect();
	private PointF mStartPoint = new PointF();
	private SurfaceHolder mSurHolder = null;
	private ThreadRender mThreadRender = null;
	private int vBottom;
	private int vLeft;
	private int vRight;
	private int vTop;
	private Bitmap mLastFrame1;
	private boolean isplaying = false;
	private CameraManagerment mCameraManagerment=CameraManagerment.getInstance();
	public boolean isIsplaying() {
		return isplaying;
	}

	public void setIsplaying(boolean isplaying) {
		this.isplaying = isplaying;
	}

	public Bitmap getmLastFrame1() {
		return mLastFrame1;
	}

	public void setmLastFrame1(Bitmap mLastFrame1) {
		this.mLastFrame1 = mLastFrame1;
	}

	private MediaFormat mediaFormat = null;
	private MediaCodec mediaCodec;

	private Bitmap mViewBitmap = null;
	public boolean alerdy = false;
	// ColorConvertUtil converter = null;
 

	public Monitor(Context var1, AttributeSet var2) {
		super(var1, var2);
		mEnableDither = false;
		mPinchedMode = 0;
		mStartPoint = new PointF();
		mMidPoint = new PointF();
		mMidPointForCanvas = new PointF();
		mOrigDist = 0.0F;
		mCurrentScale = 1.0F;
		mCurrentMaxScale = 2.0F;
		mSurHolder = null;
		mRectCanvas = new Rect();
		mRectMonitor1 = new Rect();
		mPaint = new Paint();
		mLastFrameLock = new ReentrantLock();
		mAVChannel = -1;
		mCurVideoWidth = 0;
		mCurVideoHeight = 0;
		mThreadRender = null;
		mSurHolder = getHolder();
		mSurHolder.addCallback(this);
		mGestureDetector = new GestureDetector(this);
		setOnTouchListener(this);
		setLongClickable(true);
		reshowTryCount =0;
		// converter = new ColorConvertUtil();
	 
		surfaceChanged = false;
	}

	// $FF: synthetic method
	static Paint access$0(Monitor var0) {
		return var0.mPaint;
	}

	// $FF: synthetic method
	static Bitmap access$1(Monitor var0) {
		return var0.mLastFrame;
	}

	// $FF: synthetic method
	static SurfaceHolder access$2(Monitor var0) {
		return var0.mSurHolder;
	}

	// $FF: synthetic method
	static Rect access$3(Monitor var0) {
		return var0.mRectCanvas;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			byte[] src = null;
			switch (msg.what) {
			case 1:
				// Resources res = getResources();
				// String text = res.getString(R.string.videoplay_exception);
				// showToast(text);
				// Log.v("receiveIOCtrlData", "handler receiveCameraRestart   "
				// );

				break;
			}
		}

	};

	private void parseMidPoint(PointF var1, float var2, float var3, float var4,
			float var5) {
		var1.set((var2 + var4) / 2.0F, (var3 + var5) / 2.0F);
	}

	@SuppressLint({ "FloatMath" })
	private float spacing(MotionEvent var1) {
		float var2 = var1.getX(0) - var1.getX(1);
		float var3 = var1.getY(0) - var1.getY(1);
		return (float) Math.sqrt((double) (var2 * var2 + var3 * var3));
	}

	public void setrectCanvas(Bitmap var3) {
		if (var3 != null) {
			if (var3.getWidth() > 0
					&& var3.getHeight() > 0
					&& (var3.getWidth() != this.mCurVideoWidth || var3
							.getHeight() != this.mCurVideoHeight)) {
				this.mCurVideoWidth = var3.getWidth();
				this.mCurVideoHeight = var3.getHeight();
				Log.i("IOTCamera", "Monitor1 mCurVideoWidth:" + mCurVideoWidth
						+ ",mCurVideoHeight" + mCurVideoHeight);
				this.mRectCanvas.set(0, 0, this.mRectMonitor1.right,
						this.mRectMonitor1.bottom);

				// if (this.mRectMonitor1.bottom - this.mRectMonitor1.top <
				// this.mRectMonitor1.right
				// - this.mRectMonitor1.left) {
				// Log.i("IOTCameraptz", "Landscape layout");
				// double var9 = (double) this.mCurVideoWidth
				// / (double) this.mCurVideoHeight;
				// this.mRectCanvas.right = (int) (var9 * (double)
				// this.mRectMonitor1.bottom);
				// this.mRectCanvas
				// .offset((this.mRectMonitor1.right - this.mRectCanvas.right) /
				// 2,
				// 0);
				// } else {
				// Log.i("IOTCameraptz", "Portrait layout");
				// double var5 = (double) this.mCurVideoWidth
				// / (double) this.mCurVideoHeight;
				// this.mRectCanvas.bottom = (int) ((double)
				// this.mRectMonitor1.right / var5);
				// this.mRectCanvas
				// .offset(0,
				// (this.mRectMonitor1.bottom - this.mRectCanvas.bottom) / 2);
				// }
				if (Screen == 2) {
					Log.i("IOTCameraptz", "Landscape layout Screen--" + Screen);
					double var9 = (double) this.mCurVideoWidth
							/ (double) this.mCurVideoHeight;
					this.mRectCanvas.right = (int) (var9 * (double) this.mRectMonitor1.bottom) - 100;
					this.mRectCanvas
							.offset((this.mRectMonitor1.right - this.mRectCanvas.right) / 2 + 50,
									0 + 50);
				} else {
					Log.i("IOTCameraptz", "Portrait layout  Screen--" + Screen);
					double var5 = (double) this.mCurVideoWidth
							/ (double) this.mCurVideoHeight;
					this.mRectCanvas.bottom = (int) ((double) this.mRectMonitor1.right / var5);
					this.mRectCanvas
							.offset(0,
									(this.mRectMonitor1.bottom - this.mRectCanvas.bottom) / 2);
				}

				this.vLeft = this.mRectCanvas.left;
				this.vTop = this.mRectCanvas.top;
				this.vRight = this.mRectCanvas.right;
				this.vBottom = this.mRectCanvas.bottom;
				this.mCurrentScale = 1.0F;
				this.parseMidPoint(this.mMidPoint, (float) this.vLeft,
						(float) this.vTop, (float) this.vRight,
						(float) this.vBottom);
				this.parseMidPoint(this.mMidPointForCanvas, (float) this.vLeft,
						(float) this.vTop, (float) this.vRight,
						(float) this.vBottom);
				Log.i("IOTCamera", "Change canvas size ("
						+ "this.mRectCanvas.right:" + this.mRectCanvas.right
						+ ",this.mRectCanvas.left:" + this.mRectCanvas.left
						+ (this.mRectCanvas.right - this.mRectCanvas.left)
						+ ", " + "this.mRectCanvas.bottom:"
						+ this.mRectCanvas.bottom + ",this.mRectCanvas.top:"
						+ this.mRectCanvas.top + ","
						+ (this.mRectCanvas.bottom - this.mRectCanvas.top)
						+ ")");
			}
		}
	}

	private int Screen = 1;

	public void attachCamera(Camera var1, int var2, int i) {
		this.mLastFrame1 = MainCameraFragment.default_snap; //getLoacalBitmap(var1.getmDevUID());
		reshowTryCount = 0;
		Screen = i;
		setrectCanvas(mLastFrame1);
		this.mCamera = var1;
		this.mCamera.registerIOTCListener(this);
		this.mAVChannel = var2;
 
			if (this.mThreadRender == null) {
				this.mThreadRender = new ThreadRender(
						(ThreadRender) null);
				this.mThreadRender.start();
			}
		 
		// 08-30 18:07:46.863: I/IOTCameraptz(21379): Portrait layout
			hasdraw = false;
		 
	}

	public void attachCamera(Camera var1, int var2) {
		
		this.mLastFrame1 = MainCameraFragment.default_snap; //getLoacalBitmap(var1.getmDevUID());
		reshowTryCount= 0;
		setrectCanvas(mLastFrame1);
		this.mCamera = var1;
		this.mCamera.registerIOTCListener(this);
		this.mAVChannel = var2;
		
 
			if (this.mThreadRender == null) {
				this.mThreadRender = new ThreadRender(
						(ThreadRender) null);
				this.mThreadRender.start();
			 
		} 
			hasdraw = false; 

	}

	public void deattachCamera() {
		reshowTryCount = 0;
		this.mAVChannel = -1;
		if (this.mCamera != null) {
			this.mCamera.unregisterIOTCListener(this);
			this.mCamera.attachedMonitor = null;
			 
		}

		if (this.mThreadRender != null) {
			this.mThreadRender.stopThread();

			try {
				 this.mThreadRender.interrupt();
				 this.mThreadRender.join();
			} catch (InterruptedException var2) {
				var2.printStackTrace();
			}

			this.mThreadRender = null;
		}
 

	}

	private long firstClick;
	private long lastClick; // 计算点击的次数
	private int count;
	private int mheight;
	private int mwidth;

	public boolean onDown(MotionEvent var1) {
		// 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
		if (firstClick != 0 && System.currentTimeMillis() - firstClick > 300) {
			count = 0;
		}
		count++;
		if (count == 1) {
			switch (var1.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (isHorizontal && mhandle !=null) {
					mhandle.sendEmptyMessage(98);
					mhandle.removeMessages(99);
				}
				break;
			}
			firstClick = System.currentTimeMillis();
		} else if (count == 2) {
			lastClick = System.currentTimeMillis();
			// 两次点击小于300ms 也就是连续点击
			if (lastClick - firstClick < 300) {// 判断是否是执行了双击事件
				Log.v("IOTCameraptz", "实现双机事件");
				 
	                mRectCanvas.set(0, 0, mRectMonitor1.right, mRectMonitor1.bottom);
	               // if(mRectMonitor1.bottom - mRectMonitor1.top < mRectMonitor1.right - mRectMonitor1.left)
	                if(isHorizontal)
	                {
	                    //Log.i("IOTCamera", "》》》》Landscape layout");
	                    double ratio = (double)mCurVideoWidth / (double)mCurVideoHeight;
	                    mRectCanvas.right = (int)((double)mRectMonitor1.bottom * ratio);
	                    mRectCanvas.offset((mRectMonitor1.right - mRectCanvas.right) / 2, 0);
	                } else
	                {
	                    //Log.i("IOTCamera", "》》》》》》Portrait layout");
	                    double ratio = (double)mCurVideoWidth / (double)mCurVideoHeight;
	                    mRectCanvas.bottom = (int)((double)mRectMonitor1.right / ratio);
	                    mRectCanvas.offset(0, (mRectMonitor1.bottom - mRectCanvas.bottom) / 2);
	                }
	                vLeft = mRectCanvas.left;
	                vTop = mRectCanvas.top;
	                vRight = mRectCanvas.right;
	                vBottom = mRectCanvas.bottom;
	                mCurrentScale = 1.0F;
	                parseMidPoint(mMidPoint, vLeft, vTop, vRight, vBottom);
	                parseMidPoint(mMidPointForCanvas, vLeft, vTop, vRight, vBottom);
	                Log.i("IOTCamera", (new StringBuilder("Change canvas size (")).append(mRectCanvas.right - mRectCanvas.left).append(", ").append(mRectCanvas.bottom - mRectCanvas.top).append(")").toString());
	            }
			 
		}

		return false;
	}

	public boolean onFling(MotionEvent var1, MotionEvent var2, float var3,
			float var4) {
	
		if (this.mRectCanvas.left == this.vLeft
				&& this.mRectCanvas.top == this.vTop
				&& this.mRectCanvas.right == this.vRight
				&& this.mRectCanvas.bottom == this.vBottom) {
			System.out.println("velocityX: " + Math.abs(var3) + ", velocityY: "
					+ Math.abs(var4));
			if (var1.getX() - var2.getX() > 100.0F && Math.abs(var3) > 0.0F) {
	
				if (this.mCamera != null && this.mAVChannel >= 0) {
					
					mCameraManagerment.userIPCPTZLeft(mCamera.getmDevUID(), mAVChannel);
					
					
					
					
				}
			} else if (var2.getX() - var1.getX() > 100.0F
					&& Math.abs(var3) > 0.0F) {
				if (this.mCamera != null && this.mAVChannel >= 0) {
					mCameraManagerment.userIPCPTZRight(mCamera.getmDevUID(), mAVChannel);
					
				}
				
				
		
			} else if (var1.getY() - var2.getY() > 100.0F
					&& Math.abs(var4) > 0.0F) {
				if (this.mCamera != null && this.mAVChannel >= 0) {
					mCameraManagerment.userIPCPTZUp(mCamera.getmDevUID(), mAVChannel);
					
				}
			} else if (var2.getY() - var1.getY() > 100.0F
					&& Math.abs(var4) > 0.0F && this.mCamera != null
					&& this.mAVChannel >= 0) {
				mCameraManagerment.userIPCPTZDown(mCamera.getmDevUID(), mAVChannel);
				
			}

			(new Handler()).postDelayed(new Runnable() {
				public void run() {
					if (Monitor.this.mCamera != null
							&& Monitor.this.mAVChannel >= 0) {
						mCameraManagerment.userIPCPTZStop(Monitor.this.mCamera.getmDevUID(), Monitor.this.mAVChannel);
					
						Log.v("onFling","MotionEvent  sendIOCtrl" );
					}

				}
			}, 1500L);
			Log.v("onFling","MotionEvent  finish" );
			return false;
		} else {
			Log.v("onFling","MotionEvent  finish" );
			return false;
		}
		
		
	}

 
	
	public void onLongPress(MotionEvent var1) {
		

	}

	public boolean onScroll(MotionEvent var1, MotionEvent var2, float var3,
			float var4) {
		return true;
	}

	public void onShowPress(MotionEvent var1) {
	}

	public boolean onSingleTapUp(MotionEvent var1) {
		return false;
	}

	private Toast mToast;

	public void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	public void cancelToast() {
		if (mToast != null) {
			mToast.cancel();
		}
	}

	public void onBackPressed() {
		cancelToast();

	}

	@SuppressLint({ "NewApi" })
	public boolean onTouch(View var1, MotionEvent var2) {
		this.mGestureDetector.onTouchEvent(var2);
		switch (255 & var2.getAction()) {

		case 0:
			if (this.mRectCanvas.left != this.vLeft
					|| this.mRectCanvas.top != this.vTop
					|| this.mRectCanvas.right != this.vRight
					|| this.mRectCanvas.bottom != this.vBottom) {
				this.mPinchedMode = 1;
				this.mStartPoint.set(var2.getX(), var2.getY());
			}
			break;
		case MotionEvent.ACTION_UP:

		case 6:
			if (this.mCurrentScale == 1.0F) {
				this.mPinchedMode = 0;
				
		
				
			} else {
		
			 
					Resources res = getResources();

					String text = res
							.getString(R.string.Monitor_doubleclick_quitfullscreen);
					showToast(text);
				 
			}
			break;
		case 2:
			if (this.mPinchedMode == 1) {
				if (System.currentTimeMillis() - this.mLastZoomTime < 33L) {
					return true;
				}

				PointF var16 = new PointF();
				var16.set(var2.getX(), var2.getY());
				int var17 = (int) var16.x - (int) this.mStartPoint.x;
				int var18 = (int) var16.y - (int) this.mStartPoint.y;
				this.mStartPoint = var16;
				Rect var19 = new Rect();
				var19.set(this.mRectCanvas);
				var19.offset(var17, var18);
				int var20 = var19.right - var19.left;
				int var21 = var19.bottom - var19.top;
				if (this.mRectMonitor1.bottom - this.mRectMonitor1.top > this.mRectMonitor1.right
						- this.mRectMonitor1.left) {
					if (var19.left > this.mRectMonitor1.left) {
						var19.left = this.mRectMonitor1.left;
						var19.right = var20 + var19.left;
					}

					if (var19.top > this.mRectMonitor1.top) {
						var19.top = this.mRectCanvas.top;
						var19.bottom = var21 + var19.top;
					}

					if (var19.right < this.mRectMonitor1.right) {
						var19.right = this.mRectMonitor1.right;
						var19.left = var19.right - var20;
					}

					if (var19.bottom < this.mRectMonitor1.bottom) {
						var19.bottom = this.mRectCanvas.bottom;
						var19.top = var19.bottom - var21;
					}
				} else {
					if (var19.left > this.mRectMonitor1.left) {
						var19.left = this.mRectCanvas.left;
						var19.right = var20 + var19.left;
					}

					if (var19.top > this.mRectMonitor1.top) {
						var19.top = this.mRectMonitor1.top;
						var19.bottom = var21 + var19.top;
					}

					if (var19.right < this.mRectMonitor1.right) {
						var19.right = this.mRectCanvas.right;
						var19.left = var19.right - var20;
					}

					if (var19.bottom < this.mRectMonitor1.bottom) {
						var19.bottom = this.mRectMonitor1.bottom;
						var19.top = var19.bottom - var21;
					}
				}
				  if(var19.top>0){
					  var19.top = var19.top/2;
					  var19.bottom = var19.bottom-var19.top;
	                }
				System.out.println("offset (" + var17 + ", " + var18
						+ "), after offset rect = (" + var19.left + ", "
						+ var19.top + ", " + var19.right + ", " + var19.bottom
						+ ")");
				this.mRectCanvas.set(var19);
			} else if (this.mPinchedMode == 2) {
				if (System.currentTimeMillis() - this.mLastZoomTime < 33L) {
					return true;
				}

				if (var2.getPointerCount() == 1) {
					return true;
				}

				float var4 = this.spacing(var2);
				float var5 = var4 / this.mOrigDist;
				this.mCurrentScale = var5 * this.mCurrentScale;
				this.mOrigDist = var4;
				if (this.mCurrentScale > this.mCurrentMaxScale) {
					this.mCurrentScale = this.mCurrentMaxScale;
					return true;
				}

				if (this.mCurrentScale < 1.0F) {
					this.mCurrentScale = 1.0F;
				}

				System.out.println("newDist(" + var4 + ") / origDist("
						+ this.mOrigDist + ") = zoom scale("
						+ this.mCurrentScale + ")");
				int var6 = 3 * (this.vRight - this.vLeft);
				int var7 = 3 * (this.vBottom - this.vTop);
				int var8 = (int) ((float) (this.vRight - this.vLeft) * this.mCurrentScale);
				int var9 = (int) ((float) (this.vBottom - this.vTop) * this.mCurrentScale);
				int var10 = this.vRight - this.vLeft;
				int var11 = this.vBottom - this.vTop;
				int var12 = (int) ((float) (this.mRectMonitor1.width() / 2) - var5
						* (float) (this.mRectMonitor1.width() / 2 - this.mRectCanvas.left));
				int var13 = (int) ((float) (this.mRectMonitor1.height() / 2) - var5
						* (float) (this.mRectMonitor1.height() / 2 - this.mRectCanvas.top));
				int var14 = var12 + var8;
				int var15 = var13 + var9;
				if (var8 > var10 && var9 > var11) {
					if (var8 >= var6 || var9 >= var7) {
						var12 = this.mRectCanvas.left;
						var13 = this.mRectCanvas.top;
						var14 = var12 + var6;
						var15 = var13 + var7;
					}
				} else {
					var12 = this.vLeft;
					var13 = this.vTop;
					var14 = this.vRight;
					var15 = this.vBottom;
				}

				this.mRectCanvas.set(var12, var13, var14, var15);
				System.out.println("zoom -> l: " + var12 + ", t: " + var13
						+ ", r: " + var14 + ", b: " + var15 + ",  width: "
						+ var8 + ", height: " + var9);
				this.mLastZoomTime = System.currentTimeMillis();
			}
		case 3:
		case 4:
		default:
			break;
		case 5:
			float var22 = this.spacing(var2);
			if (var22 > 10.0F) {
				this.mPinchedMode = 2;
				this.mOrigDist = var22;
				System.out.println("Action_Pointer_Down -> origDist("
						+ this.mOrigDist + ")");
			}
		}

		return true;
	}

	public void receiveChannelInfo(Camera var1, int var2, int var3) {
	}

	public void receiveFrameData(Camera var1, int avChannel, final Bitmap bmp) {
	
		
		
		 if(mAVChannel == avChannel)
	        {
	            mLastFrame = bmp;
	            hasdraw = true;
	            if(bmp.getWidth() > 0 && bmp.getHeight() > 0 && (bmp.getWidth() != mCurVideoWidth || bmp.getHeight() != mCurVideoHeight))
	            {
	                mCurVideoWidth = bmp.getWidth();
	                mCurVideoHeight = bmp.getHeight();
	                mRectCanvas.set(0, 0, mRectMonitor1.right, mRectMonitor1.bottom);
	               // if(mRectMonitor1.bottom - mRectMonitor1.top < mRectMonitor1.right - mRectMonitor1.left)
	                if(isHorizontal)
	                {
	                    Log.i("IOTCamera", "》》》》Landscape layout");
	                    double ratio = (double)mCurVideoWidth / (double)mCurVideoHeight;
	                    mRectCanvas.right = (int)((double)mRectMonitor1.bottom * ratio);
	                    mRectCanvas.offset((mRectMonitor1.right - mRectCanvas.right) / 2, 0);
	                } else
	                {
	                    //Log.i("IOTCamera", "》》》》》》Portrait layout");
	                    double ratio = (double)mCurVideoWidth / (double)mCurVideoHeight;
	                    mRectCanvas.bottom = (int)((double)mRectMonitor1.right / ratio);
	                    mRectCanvas.offset(0, (mRectMonitor1.bottom - mRectCanvas.bottom) / 2);
	                }
	                vLeft = mRectCanvas.left;
	                vTop = mRectCanvas.top;
	                vRight = mRectCanvas.right;
	                vBottom = mRectCanvas.bottom;
	                mCurrentScale = 1.0F;
	                parseMidPoint(mMidPoint, vLeft, vTop, vRight, vBottom);
	                parseMidPoint(mMidPointForCanvas, vLeft, vTop, vRight, vBottom);
	                //Log.i("IOTCamera", (new StringBuilder("Change canvas size (")).append(mRectCanvas.right - mRectCanvas.left).append(", ").append(mRectCanvas.bottom - mRectCanvas.top).append(")").toString());
	            }
	        }
	}

	public void receiveFrameInfo(Camera var1, int var2, long var3, int var5,
			int var6, AVFrame avFrame , int var8) {
	}

	public void receiveIOCtrlData(Camera var1, int var2, int var3, byte[] var4) {
		String sout = new String(var4);
		//Log.v("receiveIOCtrlData", "receiveIOCtrlData  = " + sout);
	}

	public void receiveCameraCtl(Camera var1, int var2, int var3, byte[] var4) {

		//
		// Resources res = getResources();
		// String text = res.getString(R.string.videoplay_exception);
		// showToast(text);
		// String sout=new String(var4);
		// Log.v("receiveIOCtrlData", "receiveCameraRestart  = "+sout);

		Message msg = new Message();
		msg.what = 1;
		this.handler.sendMessage(msg);
	}

	public void receiveSessionInfo(Camera var1, int var2) {
	}

	public void setMaxZoom(float var1) {
		this.mCurrentMaxScale = var1;
	}

	private boolean surfaceChanged = false;

	public void surfaceChanged(SurfaceHolder param1, int param2, int width,
			int height) {
		mheight = height;
		mwidth = width;
		this.mRectMonitor1.set(0, 0, width, height);
		this.mRectCanvas.set(0, 0, width, height);
		Log.i("IOTCameraptz", "height , width...h:" + height + ",w:" + width);
		Log.i("IOTCameraptz", "mCurVideoWidth:" + mCurVideoWidth
				+ ",mCurVideoHeight:" + mCurVideoHeight);
		Log.i("IOTCameraptz", "mRectMonitor1.bottom:" + mRectMonitor1.bottom
				+ ",mRectMonitor1.top:" + mRectMonitor1.top);
		Log.i("IOTCameraptz", "mRectMonitor1.right:" + mRectMonitor1.right
				+ ",mRectMonitor1.left:" + mRectMonitor1.left);
		if (mCurVideoWidth != 0 && mCurVideoHeight != 0) {
			// if (mRectMonitor1.bottom - mRectMonitor1.top >=
			// mRectMonitor1.right
			// - mRectMonitor1.left) {
			// Log.i("IOTCameraptz", "h:" + height + ",w:" + width);
			// Log.i("IOTCameraptz", "surfaceChanged>>Landscape layout");
			// double d1 = (double) mCurVideoWidth / (double) mCurVideoHeight;
			// mRectCanvas.right = (int) (d1 * (double) mRectMonitor1.bottom);
			// mRectCanvas.offset(
			// (mRectMonitor1.right - mRectCanvas.right) / 2, 0);
			// } else {
			// Log.i("IOTCameraptz", "surfaceChanged>>>Portrait layout");
			// double d = (double) mCurVideoWidth / (double) mCurVideoHeight;
			// mRectCanvas.bottom = (int) ((double) mRectMonitor1.right / d);
			// mRectCanvas.offset(0,
			// (mRectMonitor1.bottom - mRectCanvas.bottom) / 2);
			// }
			// Portrait layout
			if (height >= width) {
				Log.i("IOTCameraptz", ">>>>>>>>>>>>>>>>>>>>height > width...h:"
						+ height + ",w:" + width);
				this.mRectCanvas.bottom = (width * 9 / 16);
				this.mRectCanvas.offset(0,
						(height - this.mRectCanvas.bottom) / 2);
			} else {
				mRectCanvas.bottom = (width * height) / width;
				mRectCanvas.offset(0, (height - mRectCanvas.bottom) / 2);
				Log.i("IOTCameraptz", ">>>>>>>>>>>>>>>>>>>>height < width...h:"
						+ height + ",w:" + width);
			}

		} else {
			// if (height > width) {
			// Log.i("IOTCameraptz", ">>>>>>>>>>>>>>>>>>>>height > width...h:"
			// + height + ",w:" + width);
			// this.mRectCanvas.bottom = (width * 3 / 4);
			// this.mRectCanvas.offset(0,
			// (height - this.mRectCanvas.bottom) / 2);
			// } else {
			// mRectCanvas.bottom = (width * 3) / 4;
			// mRectCanvas.offset(0, (height - mRectCanvas.bottom) / 2);
			// Log.i("IOTCameraptz", ">>>>>>>>>>>>>>>>>>>>height < width...h:"
			// + height + ",w:" + width);
			// }
			if (mRectMonitor1.bottom - mRectMonitor1.top >= mRectMonitor1.right
					- mRectMonitor1.left) {
				Log.i("IOTCameraptz", "h:" + height + ",w:" + width);
				Log.i("IOTCameraptz", "surfaceChanged>>Landscape layout");
				double d1 = (double) mCurVideoWidth / (double) mCurVideoHeight;
				mRectCanvas.right = (int) (d1 * (double) mRectMonitor1.bottom);
				mRectCanvas.offset(
						(mRectMonitor1.right - mRectCanvas.right) / 2, 0);
			} else {
				Log.i("IOTCameraptz", "surfaceChanged>>>Portrait layout");
				double d = (double) mCurVideoWidth / (double) mCurVideoHeight;
				mRectCanvas.bottom = (int) ((double) mRectMonitor1.right / d);
				mRectCanvas.offset(0,
						(mRectMonitor1.bottom - mRectCanvas.bottom) / 2);
			}
			// if (height < width) {
			// this.mRectCanvas.right = (paramInt3 * 4 / 3);
			// this.mRectCanvas.offset(
			// (paramInt2 - this.mRectCanvas.right) / 2, 0);
			// this.vLeft = this.mRectCanvas.left;
			// this.vTop = this.mRectCanvas.top;
			// this.vRight = this.mRectCanvas.right;
			// this.vBottom = this.mRectCanvas.bottom;
			// this.mCurrentScale = 1.0F;
			// parseMidPoint(this.mMidPoint, this.vLeft, this.vTop,
			// this.vRight, this.vBottom);
			// parseMidPoint(this.mMidPointForCanvas, this.vLeft,
			// this.vTop, this.vRight, this.vBottom);
			// return;

			// } else {
			// Log.i("aaa", "paramInt3 > paramInt2........................");
			// // this.mRectCanvas.right = (paramInt3 * 4 / 3);
			// // this.mRectCanvas.offset(
			// // (paramInt2 - this.mRectCanvas.right) / 2, 0);
			// this.mRectCanvas.bottom = (width * 9 / 16);
			// this.mRectCanvas.offset(0,
			// (height - this.mRectCanvas.bottom) / 2);
			// }
			// if (height >= width) {
			// Log.i("IOTCameraptz", ">>>>>>>>>>>>>>>>>>>>height > width...h:"
			// + height + ",w:" + width);
			// this.mRectCanvas.bottom = (width * 4 / 3);
			// this.mRectCanvas.offset(0,
			// (height - this.mRectCanvas.bottom) / 2);
			// }

		}
		// vLeft:400,vTop:0,vRight:400,vBottom:954

		vLeft = mRectCanvas.left;
		vTop = mRectCanvas.top;
		vRight = mRectCanvas.right;
		vBottom = mRectCanvas.bottom;
		Log.i("IOTCameraptz", "vLeft:" + vLeft + ",vTop:" + vTop + ",vRight:"
				+ vRight + ",vBottom:" + vBottom);
		mCurrentScale = 1.0F;
		parseMidPoint(mMidPoint, vLeft, vTop, vRight, vBottom);
		parseMidPoint(mMidPointForCanvas, vLeft, vTop, vRight, vBottom);
 
	}

	// ipcam_ch
	public void surfaceCreated(SurfaceHolder var1) {
		surfaceChanged = false;
	}

	public void surfaceDestroyed(SurfaceHolder var1) {
		surfaceChanged = false;
	}

	public static Bitmap getLoacalBitmap(String uid) {
	//	Resources var1 = UbiaApplication.getInstance().getResources();
		//return BitmapFactory.decodeResource(var1, R.drawable.camera_thumbnail);
		String url = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/LastSnapshot/"
				+ uid
				+ "/"
				+ uid
				+ ".jpg";
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}


		// File f = new File(Environment.getExternalStorageDirectory(),
		// "Download/video_encoded.264");
		// // touch (f);
		// try {
		// outputStream = new BufferedOutputStream(new FileOutputStream(f));
		// Log.i("AvcEncoder", "outputStream initialized");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 125000);
		// mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
		// mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
		// mediaCodec.configure(mediaFormat, surface, null,
		// MediaCodec.CONFIGURE_FLAG_ENCODE);

		// mediaCodec = MediaCodec.createDecoderByType("video/avc");
		// MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc",
		// 1280, 720);
		// mediaCodec.configure(mediaFormat, surface, null, 0);
		// mediaCodec.start();


	private void getColorFormat(MediaFormat format) {
		int colorFormat = format.getInteger(MediaFormat.KEY_COLOR_FORMAT);

		int QOMX_COLOR_FormatYUV420PackedSemiPlanar64x32Tile2m8ka = 0x7FA30C03;

		String formatString = "";
		if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format12bitRGB444) {
			formatString = "COLOR_Format12bitRGB444";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format16bitARGB1555) {
			formatString = "COLOR_Format16bitARGB1555";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format16bitARGB4444) {
			formatString = "COLOR_Format16bitARGB4444";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format16bitBGR565) {
			formatString = "COLOR_Format16bitBGR565";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format16bitRGB565) {
			formatString = "COLOR_Format16bitRGB565";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format18bitARGB1665) {
			formatString = "COLOR_Format18bitARGB1665";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format18BitBGR666) {
			formatString = "COLOR_Format18BitBGR666";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format18bitRGB666) {
			formatString = "COLOR_Format18bitRGB666";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format19bitARGB1666) {
			formatString = "COLOR_Format19bitARGB1666";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format24BitABGR6666) {
			formatString = "COLOR_Format24BitABGR6666";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format24bitARGB1887) {
			formatString = "COLOR_Format24bitARGB1887";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format24BitARGB6666) {
			formatString = "COLOR_Format24BitARGB6666";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format24bitBGR888) {
			formatString = "COLOR_Format24bitBGR888";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format24bitRGB888) {
			formatString = "COLOR_Format24bitRGB888";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format25bitARGB1888) {
			formatString = "COLOR_Format25bitARGB1888";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format32bitARGB8888) {
			formatString = "COLOR_Format32bitARGB8888";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format32bitBGRA8888) {
			formatString = "COLOR_Format32bitBGRA8888";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_Format8bitRGB332) {
			formatString = "COLOR_Format8bitRGB332";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatCbYCrY) {
			formatString = "COLOR_FormatCbYCrY";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatCrYCbY) {
			formatString = "COLOR_FormatCrYCbY";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatL16) {
			formatString = "COLOR_FormatL16";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatL2) {
			formatString = "COLOR_FormatL2";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatL24) {
			formatString = "COLOR_FormatL24";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatL32) {
			formatString = "COLOR_FormatL32";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatL4) {
			formatString = "COLOR_FormatL4";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatL8) {
			formatString = "COLOR_FormatL8";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatMonochrome) {
			formatString = "COLOR_FormatMonochrome";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatRawBayer10bit) {
			formatString = "COLOR_FormatRawBayer10bit";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatRawBayer8bit) {
			formatString = "COLOR_FormatRawBayer8bit";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatRawBayer8bitcompressed) {
			formatString = "COLOR_FormatRawBayer8bitcompressed";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYCbYCr) {
			formatString = "COLOR_FormatYCbYCr";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYCrYCb) {
			formatString = "COLOR_FormatYCrYCb";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411PackedPlanar) {
			formatString = "COLOR_FormatYUV411PackedPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411Planar) {
			formatString = "COLOR_FormatYUV411Planar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar) {
			formatString = "COLOR_FormatYUV420PackedPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar) {
			formatString = "COLOR_FormatYUV420PackedSemiPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422PackedPlanar) {
			formatString = "COLOR_FormatYUV422PackedPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422PackedSemiPlanar) {
			formatString = "COLOR_FormatYUV422PackedSemiPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422Planar) {
			formatString = "COLOR_FormatYUV422Planar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422PackedSemiPlanar) {
			formatString = "COLOR_FormatYUV422PackedSemiPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422Planar) {
			formatString = "COLOR_FormatYUV422Planar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV422SemiPlanar) {
			formatString = "COLOR_FormatYUV422SemiPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV444Interleaved) {
			formatString = "COLOR_FormatYUV444Interleaved";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar) {
			formatString = "COLOR_QCOM_FormatYUV420SemiPlanar";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar) {
			formatString = "COLOR_TI_FormatYUV420PackedSemiPlanar";
		} else if (colorFormat == QOMX_COLOR_FormatYUV420PackedSemiPlanar64x32Tile2m8ka) {
			formatString = "QOMX_COLOR_FormatYUV420PackedSemiPlanar64x32Tile2m8ka";
		} else if (colorFormat == MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar) {
			formatString = "COLOR_FormatYUV420Planar";
		}
		// QOMX_COLOR_FormatYUV420PackedSemiPlanar64x32Tile2m8ka
		//Log.i("testdecode", formatString);
	}

	public void onFrame(byte[] buf, int offset, int length, int flag) {

		if (!surfaceChanged) {
			Log.i("Monitor", ">>>>>>>>########surfaceChanged = false");
			return;
		}
		// Log.i("Monitor", ">>>>>>>>########[onFrame] [len:"+ length+"]");
		if (mediaCodec == null) {
			Log.i("Monitor", ">>>>>>>>########[mediaCodec is null]");
			return;

		}
		long startMs = System.currentTimeMillis();
		ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
		ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
		int inputBufferIndex = mediaCodec.dequeueInputBuffer(0);
		if (inputBufferIndex >= 0) {
		//	Log.i("Monitor", ">>>>>>>>########inputBufferIndex >= 0");

			ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
			inputBuffer.clear();
			// inputBuffer.put(buf, offset, length);
			inputBuffer.put(buf);
			// mediaCodec.
			mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, 0, 0);// mCount
																			// *1000000
			// FRAME_RATE
			// mCount++;
		}

		// BufferInfo bufferInfo = new BufferInfo();
		// Log.i("testdecode", bufferInfo.size+ " bytes written1111111");
		// int outIndex = decoder.dequeueOutputBuffer(info, 10000);
		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
		int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);

		if (outputBufferIndex >= 0) {
			isplaying = true;
//			Log.i("Monitor",
//					">>>>>>>>########outputBufferIndex >= 0 outputBufferIndex ="
//							+ outputBufferIndex);

			// // int res = outputBufferIndex;
			// // ByteBuffer buf = mCodecOutputBuffers[ res ];
			// ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
			// byte[] chunk = new byte[bufferInfo.size];// bufferInfo.size
			// Log.i("Monitor",
			// ">>>>>>>>######## bufferInfo.size ="+bufferInfo.size);
			// if (outputBuffer != null) {
			//
			// Log.i("Monitor", ">>>>>>>>outputBuffer != null");
			// outputBuffer.get(chunk); // Read the buffer all at once
			// outputBuffer.clear(); // ** MUST DO!!! OTHERWISE THE NEXT TIME
			// // YOU
			// // GET THIS SAME BUFFER BAD THINGS WILL
			// // HAPPEN
			// if (chunk.length > 0) {
			// MediaFormat oformat = mediaCodec.getOutputFormat();
			//
			// getColorFormat(oformat);
			// //
			// int width = oformat.getInteger(MediaFormat.KEY_WIDTH);
			// int height = oformat.getInteger(MediaFormat.KEY_HEIGHT);
			// int colorFormat = oformat
			// .getInteger(MediaFormat.KEY_COLOR_FORMAT);
			// Log.v("testdecode", "chunk:" + String.valueOf(chunk.length) +
			// "colorformat:"+colorFormat);
			//
			// //mViewBitmap
			// //this.mLastFrame = converter.convertBufferAsRGB565(chunk,
			// // colorFormat, 1280, 720);
			// this.mLastFrame = myconverter.conertYUVtoRGB24
			// (chunk,width,height);
			// if(this.mLastFrame==null)
			// Log.v("IOTCamera"," myconverter.conertYUVtoRGB24 is null ");
			// else
			// {
			// myLastFrame = this.mLastFrame;
			// Log.v("IOTCamera","myLastFrame =	this.mLastFrame; ");
			// }
			// alerdy = true;
			// }
			// }
			// else
			// {
			// Log.v("Monitor","outputBuffer====== null");
			// }
			mediaCodec.releaseOutputBuffer(outputBufferIndex, true);

		} else {
			isplaying = false;
			switch (outputBufferIndex) {
			case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
				//Log.d("Monitor", "INFO_OUTPUT_BUFFERS_CHANGED");
				outputBuffers = mediaCodec.getOutputBuffers();
				break;
			case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
				//Log.d("Monitor", "New format " + mediaCodec.getOutputFormat());
				break;
			case MediaCodec.INFO_TRY_AGAIN_LATER:
				//Log.d("Monitor", "dequeueOutputBuffer timed out! " + bufferInfo);
				break;
			default:
				ByteBuffer buffer = outputBuffers[outputBufferIndex];
				Log.v("Monitor",
						"We can't use this buffer but render it due to the API limit, "
								+ buffer);
				while (bufferInfo.presentationTimeUs / 1000 > System
						.currentTimeMillis() - startMs) {
					try {
						Thread.sleep(10);

					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
				mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
				break;
			}
			if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
				Log.d("Monitor", "OutputBuffer BUFFER_FLAG_END_OF_STREAM");

			}
//			Log.i("Monitor",
//					">>>>>>>>########outputBufferIndex <<<< 0  outputBufferIndex ="
//							+ outputBufferIndex);
		}
	}

	private class ThreadRender extends Thread {

		private boolean mIsRunningThread;
	

		private ThreadRender() {
			this.mIsRunningThread = false;
	
		}

		// $FF: synthetic method
		ThreadRender(ThreadRender var2) {
			this();
		}

		public void run() {

			Log.i("Monitor1", "ThreadRender>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			this.mIsRunningThread = true;
			Canvas localCanvas = null;
			int setdefalutcount = 0;
			while (mIsRunningThread && !Thread.interrupted()) {
				try {
					if (!mPaint.isDither() && mEnableDither) {
						Log.i("IOTCamera",
								"==== Enable Dithering ==== !!!This will decrease FPS.");
						mPaint.setDither(mEnableDither);
					}
					if (!this.mIsRunningThread) {
						System.out.println("===ThreadRender exit===");
						return;
					}
					localCanvas = Monitor.this.mSurHolder.lockCanvas();

					if (localCanvas != null) {
						localCanvas.drawColor(0xff000000);
						if (mLastFrame != null && !mLastFrame.isRecycled()) {
				 

							localCanvas.drawBitmap(Monitor.this.mLastFrame,
									null, Monitor.this.mRectCanvas, mPaint);
							myLastFrame = Monitor.this.mLastFrame;

						// localCanvas.restore();
					} else {
						if (mLastFrame1 != null) {
							if(setdefalutcount++%15==0 && reshowTryCount<3)
							setDefaultShow(mLastFrame1, true);
							localCanvas.drawBitmap(Monitor.this.mLastFrame1,
									null, Monitor.this.mRectCanvas, mPaint);
						}
					}

					{
						mSurHolder.unlockCanvasAndPost(localCanvas);
						localCanvas = null;
					}
					}
				} catch (Exception ex) {

				}
				try {
					sleep(33);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
	
			}
		}

		public void stopThread() {
			this.mIsRunningThread = false;
 
		}
	}
	boolean  hasdraw = false;
	public boolean setDefaultShow(Bitmap bmp,boolean froceDraw){

		 if( (bmp  !=null   && hasdraw )|| froceDraw )
		 {	
			  
	       //Log.i("IOTCamera", (new StringBuilder("XXXXXXX Change canvas size (")).append(mRectCanvas.right - mRectCanvas.left).append(", ").append(mRectCanvas.bottom - mRectCanvas.top).append(")").toString() );

		   mCurVideoWidth = bmp.getWidth();
           mCurVideoHeight = bmp.getHeight();
           mRectCanvas.set(0, 0, mRectMonitor1.right, mRectMonitor1.bottom);
          // if(mRectMonitor1.bottom - mRectMonitor1.top < mRectMonitor1.right - mRectMonitor1.left)
           if(isHorizontal)
           {
               Log.i("IOTCamera", "》》》》Landscape layout");
               double ratio = (double)mCurVideoWidth / (double)mCurVideoHeight;
               mRectCanvas.right = (int)((double)mRectMonitor1.bottom * ratio);
               mRectCanvas.offset((mRectMonitor1.right - mRectCanvas.right) / 2, 0);
           } else
           {
               //Log.i("IOTCamera", "》》》》》》Portrait layout");
               double ratio = (double)mCurVideoWidth / (double)mCurVideoHeight;
               mRectCanvas.bottom = (int)((double)mRectMonitor1.right / ratio);
               mRectCanvas.offset(0, (mRectMonitor1.bottom - mRectCanvas.bottom) / 2);
           }
           vLeft = mRectCanvas.left;
           vTop = mRectCanvas.top;
           vRight = mRectCanvas.right;
           vBottom = mRectCanvas.bottom;
           mCurrentScale = 1.0F;
           parseMidPoint(mMidPoint, vLeft, vTop, vRight, vBottom);
           parseMidPoint(mMidPointForCanvas, vLeft, vTop, vRight, vBottom);
           //Log.i("IOTCamera", (new StringBuilder("Change canvas size (")).append(mRectCanvas.right - mRectCanvas.left).append(", ").append(mRectCanvas.bottom - mRectCanvas.top).append(")").toString());
		
           return true;
       }
		  return false;
	}
	
}
