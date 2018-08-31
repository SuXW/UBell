package com.view.timeline;

import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.ubia.interfaceManager.TimeLineTouchCallbackInterface_Manager;

import com.timeline.listenview.NoteInfoData;
import com.timeline.listenview.RollBackToCurrentTimeCallbackInterface;
import com.timeline.listenview.TimeLinePlayCallBackInterface;

public class MyHorizontalScrollView extends HorizontalScrollView implements
		OnClickListener {

	RollBackToCurrentTimeCallbackInterface mRollBackToCurrentTimeCallbackInterface;

	public RollBackToCurrentTimeCallbackInterface getmRollBackToCurrentTimeCallbackInterface() {
		return mRollBackToCurrentTimeCallbackInterface;
	}

	public void setRollBackToCurrentTimeCallbackInterface(
			RollBackToCurrentTimeCallbackInterface mRollBackToCurrentTimeCallbackInterface) {
		this.mRollBackToCurrentTimeCallbackInterface = mRollBackToCurrentTimeCallbackInterface;
	}

	public interface CurrentImageChangeListener {
		void onCurrentImgChanged(int position, View viewIndicator);
	}

	public interface OnItemClickListener {
		void onClick(View view, int pos);
	}
	private int mTodayDisplayCurrentTimeInSec;
	private CurrentImageChangeListener mListener;
	
	private OnItemClickListener mOnClickListener;

	private static final String TAG = "MyHorizontalScrollView";
	private int mTodayBeforeDay=0;
	/**
	 * HorizontalListView中的LinearLayout
	 */
	private LinearLayout mContainer;
	private Context context;
	private int minScrollpix;//0点线到的最左边距离
	private int maxScrollpix;//24点线到的最右边距离
	private int mScrollX;
	private boolean ischangeDisplay = false;
	private boolean scorllToCurrentTime = false;
	public int timeUnit=0;//0:10分钟，1:1小时，2:6小时；
	/**
	 * 屏幕的宽度
	 */
	private int mScreenWitdh;
	public   MyView view;
	double nLenStart = 0;
	public boolean ishandleEvent = false;
	public boolean ishandleDoubleEvent = false;
	int sameCount = 0;

	Handler myhanHandler =new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			 
			if(msg.what==touchEventId) {
				if(lastX ==MyHorizontalScrollView.this.getScrollX()) {
					if(sameCount==0){//连续两次相同
						sameCount++;
						myhanHandler.sendMessageDelayed(myhanHandler.obtainMessage(touchEventId,MyHorizontalScrollView.this), 50);
						lastX = MyHorizontalScrollView.this.getScrollX ();
						ishandleEvent = true;
					}else{
						sameCount=0;
					if(!onTouching)
					 handleStop(lastX);
					 ishandleEvent = false;
					 myhanHandler.removeMessages(touchEventId);
					}
				}else {
 					sameCount =0;
					myhanHandler.sendMessageDelayed(myhanHandler.obtainMessage(touchEventId,MyHorizontalScrollView.this), 100);
					lastX = MyHorizontalScrollView.this.getScrollX ();
					ishandleEvent = true;
				}
			}
		}
	};
	
	//这里写真正的事件
	private void handleStop(int lastX) {

		 int playUtcTime = view.getTodayStartTime()+(int) getNowDisplayTime(); //-TimeZone.getDefault().getOffset(System.currentTimeMillis())/1000 ;
		long nowTime = System.currentTimeMillis();
	  
		handUpNowTime =nowTime;
		if(nowTime-tryLivePlayTime>1500)//大于尝试直播时间的手势才算回放手势
			//if(handUpNowTime-handUpLastTime>5000)//连续滑动期间，算最后一次滑动有效松手2秒后
			  { 
				handUpLastTime= nowTime;
				int playtime =playUtcTime;// view.findFirstExistEvent(playUtcTime);
				
//				if(view.hasExistEvent(playUtcTime))
				Log.d("", "滑动回放时间：playtime="+playtime);
				if(playtime>0)
					{
						if(mTimeLinePlayCallBackInterface!=null)
					mTimeLinePlayCallBackInterface.TimeLinePlayStatecallback(playtime);
					}
				else if(playtime==-1){
					if(this.mRollBackToCurrentTimeCallbackInterface !=null)
					 	this.mRollBackToCurrentTimeCallbackInterface.RollBackToCurrentTimecallback((int) (System.currentTimeMillis()/1000)); 
				}
			  }

	
		} ;
	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获得屏幕宽度
		this.context = context;
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWitdh = outMetrics.widthPixels;
		 view = new MyView(context,1);
		this.addView(view); 
		
	 
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		mWidth = getWidth();
		mHeight = getHeight();
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// drawScaleLine(canvas);
		// drawWheel(canvas);
		//drawMiddleLine(canvas);
	}

	int mWidth;
	int mHeight;
	private TimeLinePlayCallBackInterface mTimeLinePlayCallBackInterface;
	private NoteInfoData mNoteInfoData;

		// TOOD 常量太多，暂时放这，最终会放在类的开始，放远了怕很快忘记



 
                     
	/**
	 * 滑动时的回调
	 */
	public void notifyCurrentmScrollXChanged(int mScrollX) {
		// 先清除所有的背景色，点击时会设置为蓝色
		  if(view.timeUnit==0){
			  minScrollpix=(int) ((int) (view.getShowHidehourTime()/2-1) *(view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6)+ 4*(view.getNodeWidthHide()));
			  maxScrollpix = (int) (view.getWidth()-view.getNodeWidthHide()*2-view.getShowHidehourTime()/2 * view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6);//最左侧，位置大于2个节点+3个小时
 			  if(mScrollX<minScrollpix)
					this.smoothScrollTo(minScrollpix, 0);
				
				if(mScrollX>maxScrollpix)
					this.smoothScrollTo(maxScrollpix, 0);
		  }else if(view.timeUnit==1){
			  minScrollpix=(int) view.getNodeWidthHide();  //屏幕左侧只有一个节点
//			  int currentTime=scorllToCurrentTime();
//			  int canScrollDistance=(int) ((int) (currentTime/3600)*((view.getNodeWidthHide())*(view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM())))+minScrollpix;
			  maxScrollpix = (int) ((view.getWidth()-view.getNodeWidthHide()*5));//最左侧，位置大于2个节点+3个小时
			  if(mScrollX<view.getNodeWidthHide())
					this.smoothScrollTo(minScrollpix, 0);
				
				if(mScrollX>maxScrollpix)
					this.smoothScrollTo(maxScrollpix, 0);
		  }else{
			 
			  minScrollpix=0;
			  maxScrollpix=(int) (view.getWidth()-view.getNodeWidthHide()*2);
			  if(mScrollX<minScrollpix)
					this.smoothScrollTo(minScrollpix, 0);
				
				if(mScrollX>maxScrollpix)
					this.smoothScrollTo(maxScrollpix, 0);
		  }
		 
	
	}
	
	long handUpNowTime = 0;
	long handUpLastTime = 0;
	float lastClickpt=0 ;
	float lastmovex = 0;
	public boolean onTouching =false;
	Runnable mytouchrunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			onTouching = false;
		}
	};
	
	private int lastX = 0;
	private int touchEventId = -9983761;
	private int doubletouchEventId = -9983762;
	
	
	public static final int HandlerReleaseTime = 50;//ms
	public static final int HandlerStopCheckTime = 50;//ms
	int clickCount=0;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {	
//				
		int nCnt = event.getPointerCount();
		//scorllToCurrentTime();
		int n = event.getAction();
		onTouching = true;
		myhanHandler.removeCallbacks(mytouchrunnable);
		 if( (event.getAction()  == MotionEvent.ACTION_DOWN && 1 == nCnt)   && ishandleDoubleEvent ==false){
			 ishandleEvent = true;
			 lastmovex = event.getX();
			 myhanHandler.removeMessages(touchEventId);
			
		 }
		 if( (event.getAction()  == MotionEvent.ACTION_UP && 1 == nCnt )  && ishandleDoubleEvent ==false){
			 
			onTouching =false;
			 //myhanHandler.postDelayed(mytouchrunnable, HandlerReleaseTime); 
				// myhanHandler.postDelayed(mytouchrunnableCheckStop, HandlerStopCheckTime); 
			 myhanHandler.sendMessageDelayed(myhanHandler.obtainMessage(touchEventId,this),0);
//			int playUtcTime = view.getTodayStartTime()+(int) getNowDisplayTime(); //-TimeZone.getDefault().getOffset(System.currentTimeMillis())/1000 ;
//			long nowTime = System.currentTimeMillis();
//			float nowClickpt = event.getX();
//			
//			 myhanHandler.postDelayed(mytouchrunnable, 2000); 
//			handUpNowTime =nowTime;
//			if(nowTime-tryLivePlayTime>1500)//大于尝试直播时间的手势才算回放手势
//				//if(handUpNowTime-handUpLastTime>5000)//连续滑动期间，算最后一次滑动有效松手2秒后
//				  { 
//					handUpLastTime= nowTime;
//					int playtime = view.findFirstExistEvent(playUtcTime);
//				
////					if(view.hasExistEvent(playUtcTime))
//					if(playtime!=0)
//						mTimeLinePlayCallBackInterface.TimeLinePlayStatecallback(playtime);
//					else if(playtime==-1){
//						if(this.mRollBackToCurrentTimeCallbackInterface !=null)
			 TimeLineTouchCallbackInterface_Manager.getInstance().TimeLineTouchStatecallback(event.getAction());
//					}
//				  }

		}
		
		if( (event.getAction()&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN && 2 == nCnt)//<span style="color:#ff0000;">2表示两个手指</span>
		{
			ishandleDoubleEvent = true;
			for(int i=0; i< nCnt; i++)
			{
				float x = event.getX(i);
				float y = event.getY(i);
				
				Point pt = new Point((int)x, (int)y);
	
			}
			
			int xlen = Math.abs((int)event.getX(0) - (int)event.getX(1));
			int ylen = Math.abs((int)event.getY(0) - (int)event.getY(1));
			
			nLenStart = Math.sqrt((double)xlen*xlen + (double)ylen * ylen);
			
			
		}else if( (event.getAction()&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP  && 2 == nCnt)
		{
			ishandleDoubleEvent = true;
			onTouching = false;
			ishandleEvent = false;
			for(int i=0; i< nCnt; i++)
			{
				float x = event.getX(i);
				float y = event.getY(i);
				
				Point pt = new Point((int)x, (int)y);

			}
			
			int xlen = Math.abs((int)event.getX(0) - (int)event.getX(1));
			int ylen = Math.abs((int)event.getY(0) - (int)event.getY(1));
			
			double nLenEnd = Math.sqrt((double)xlen*xlen + (double)ylen * ylen);
			if(nLenEnd > nLenStart)//通过两个手指开始距离和结束距离，来判断放大缩小
			{
				if(view.timeUnit==2)
				{mTodayDisplayCurrentTimeInSec = getNowDisplayTime();
					Toast.makeText(  context, "放大", 3000).show();
					this.removeAllViews();
					MyView view = new MyView(context,1);//10分钟
					this.addView(view); 
					view.setNoteInfoData(this.view.mNoteInfoData);
					ischangeDisplay = true;
					this.view = view;

					  scorllToTheTime(mTodayDisplayCurrentTimeInSec);
				}
				
				else if(view.timeUnit==1)
				{
					mTodayDisplayCurrentTimeInSec = getNowDisplayTime();
					Toast.makeText(  context, "放大", 3000).show();
					this.removeAllViews();
					MyView view = new MyView(context,0);//10分钟
					this.addView(view); 
					view.setNoteInfoData(this.view.mNoteInfoData);
					ischangeDisplay = true;
					this.view = view;
					 scorllToTheTime(mTodayDisplayCurrentTimeInSec);
				}
				
			}
			else
			{
				
				if( view.timeUnit==0)
					{
					mTodayDisplayCurrentTimeInSec = getNowDisplayTime();
					Toast.makeText(  context, "缩小", 3000).show();
					MyView view = new MyView(context,1);
					this.removeAllViews();
					this.addView(view); 
					view.setNoteInfoData(this.view.mNoteInfoData);
					ischangeDisplay= true;
					this.view = view;
					 scorllToTheTime(mTodayDisplayCurrentTimeInSec);
					}
				
				else if( view.timeUnit==1)
				{
				mTodayDisplayCurrentTimeInSec = getNowDisplayTime();
				Toast.makeText(  context, "缩小", 3000).show();
				MyView view = new MyView(context,2);
				this.removeAllViews();
				this.addView(view); 
				view.setNoteInfoData(this.view.mNoteInfoData);
				ischangeDisplay= true;
				this.view = view;
				 scorllToTheTime(mTodayDisplayCurrentTimeInSec);
				}
			}
			myhanHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					ishandleDoubleEvent = false;
					onTouching =false; 
				}
			}, 100);
		}
		
//		
//		
//	
		if(nCnt>1)return true;
		return super.onTouchEvent(event);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}
	long mBackPressTime;
	long tryLivePlayTime;
	public  int scorllToCurrentTime(){
	
			int mCurrentTimeInSec= (int) ((System.currentTimeMillis()/1000+  TimeZone.getDefault().getOffset(System.currentTimeMillis())/1000)%(24*3600));

		  
			
			int  	 scorllPx =    (int) (((float)mCurrentTimeInSec/(3600.0)+(6*24))*(view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6)  +minScrollpix) ;
				if(!ishandleEvent)   
			  	 this.smoothScrollTo(scorllPx, 0);
			   	long nowTime = System.currentTimeMillis();
			
				
				if(  nowTime - mBackPressTime< 200){//去除抖动
					mBackPressTime = nowTime;
					return mCurrentTimeInSec+(6*24*3600);
				}
				mBackPressTime = nowTime; 
				{ 
					tryLivePlayTime = nowTime;
				 	if(this.mRollBackToCurrentTimeCallbackInterface !=null)
					 	this.mRollBackToCurrentTimeCallbackInterface.RollBackToCurrentTimecallback((int) (System.currentTimeMillis()/1000)); 
					return  mCurrentTimeInSec+(6*24*3600);
				}
 
	}
	public  void scorllToCurrentTimeAndNoRecall(){
		if(ishandleEvent)   return;
		int mCurrentTimeInSec= (int) ((System.currentTimeMillis()/1000+  TimeZone.getDefault().getOffset(System.currentTimeMillis())/1000)%(24*3600));

	  
		
		int  	 scorllPx =    (int) (((float)mCurrentTimeInSec/(3600.0)+(6*24))*(view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6)  +minScrollpix) ;
	    this.smoothScrollTo(scorllPx, 0);
	 
		 	long nowTime = System.currentTimeMillis();
//		 	Log.e(TAG, scorllPx
//					+ " =scorllPx() scorllPx="+scorllPx +"     nowTime:"+nowTime+"   mCurrentTimeInSec:  "+mCurrentTimeInSec);
//			
//			if(  nowTime - mBackPressTime< 200){//去除抖动
//				mBackPressTime = nowTime;
//				return mCurrentTimeInSec+(6*24*3600);
//			}
//			mBackPressTime = nowTime; 
//			{ 
//				tryLivePlayTime = nowTime;
//			 	if(this.mRollBackToCurrentTimeCallbackInterface !=null)
//				 	this.mRollBackToCurrentTimeCallbackInterface.RollBackToCurrentTimecallback((int) (System.currentTimeMillis()/1000)); 
//				return  mCurrentTimeInSec+(6*24*3600);
//			}
// 
	}
	/**本地时间当天的秒*/
	public  void scorllToTheTime(int mTimeInSec){
		if(ishandleEvent){
			return;
		}
		mTimeInSec = mTimeInSec;//当天的秒数
		//int mCurrentTimeInSec= (int) ((mTimeInSec+  TimeZone.getDefault().getOffset(System.currentTimeMillis()))%(24*3600));
		  if(view.timeUnit==0){
			  minScrollpix=(int) ((int) (view.getShowHidehourTime()/2-1) *(view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6)+ 4*(view.getNodeWidthHide()));
			  maxScrollpix = (int) (view.getWidth()-view.getNodeWidthHide()*2-view.getShowHidehourTime()/2  * view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6);//最左侧，位置大于2个节点+3个小时
 			 
		  }else if(view.timeUnit==1){
			  minScrollpix=(int) view.getNodeWidthHide();  //屏幕左侧只有一个节点
			  maxScrollpix = (int) (view.getWidth()-view.getNodeWidthHide()*5 );//最左侧，位置大于2个节点+3个小时
		 
		  }else{
			  minScrollpix=0;
			  maxScrollpix= (int) (view.getWidth()-view.getNodeWidthHide()*2 );
		  }
		
	
		  int	 scorllPx =    (int) (((float)mTimeInSec/(3600.0f))*(view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6)  +minScrollpix) ;
	
		  	this.smoothScrollTo(scorllPx, 0);

}

	@Override
	protected int computeHorizontalScrollOffset() {
		int computeHorizontalScrollOffset=	 super.computeHorizontalScrollOffset();
	
		  notifyCurrentmScrollXChanged(computeHorizontalScrollOffset);
		  
		
			 	int minTime = (int) ((computeHorizontalScrollOffset - minScrollpix)%( view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6));//0点左边有3小时
				int hourTime = (int) ((computeHorizontalScrollOffset - minScrollpix)/(  view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6 ));
				float perMinTime = (float) minTime / ((float)  view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6  ) * 60;
				float perSecTime = (float) minTime /( (float)  view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6 ) * 3600% 60;
					
			
			  
//				if(isOutofCurrentTimeInSec((int) (hourTime*3600+perMinTime*60+perSecTime))){
//					 scorllToCurrentTime();
//				}else{
					
//				}
 		
		
		  
		  if(ischangeDisplay){
				scorllToTheTime(mTodayDisplayCurrentTimeInSec);
				ischangeDisplay =false;
			}
		  
		 
		  return computeHorizontalScrollOffset;
	}

	/**当前滑动块所在时间*/
	public int getNowDisplayTime(){
		int computeHorizontalScrollOffset=	 super.computeHorizontalScrollOffset();
		  
				int minTime = (int) ((computeHorizontalScrollOffset - minScrollpix)%( (view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6) ));//0点左边有3小时
				int hourTime = (int) ((computeHorizontalScrollOffset - minScrollpix)/( ( view.getNodeWidthHide() *view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6)  ));
				float perMinTime = (float) minTime /( ((float)  view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6  )) * 60;
				float perSecTime = (float) minTime /(( (float)  view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()/6 ) ) * 3600
						% 60;
 				Log.e(TAG, "hourTime=" + hourTime + " perMinTime =" + perMinTime
 						+ "  perSecTime " + perSecTime);
			 
				return (int) (hourTime*3600+perMinTime*60+perSecTime);
		 
		 
		
	}
	
	/**当前滑动块所在时间*/
//	private int getNowDisplayTimeInUTC(){
//		int computeHorizontalScrollOffset=	 super.computeHorizontalScrollOffset();
//		  
//		  if(view.isTenMin()){
//				Log.e(TAG, computeHorizontalScrollOffset
//						+ " =computeHorizontalScrollOffset()");
//				int minTime = (int) ((computeHorizontalScrollOffset - minScrollpix)%( view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM() ));//0点左边有3小时
//				int hourTime = (int) ((computeHorizontalScrollOffset - minScrollpix)/(  view.getNodeWidthHide() *view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()  ));
//				float perMinTime = (float) minTime / ((float)  view.getNodeWidthHide() *view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()  ) * 60;
//				float perSecTime = (float) minTime /( (float)  view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()   ) * 3600
//						% 60;
//				Log.e(TAG, "hourTime=" + hourTime + " perMinTime =" + perMinTime
//						+ "  perSecTime " + perSecTime);
//			 
//				return (int) (hourTime*3600+perMinTime*60+perSecTime);
//		  }
//		  else {
//			Log.e(TAG, computeHorizontalScrollOffset
//					+ " =computeHorizontalScrollOffset()");
//			int minTime = (int) ((computeHorizontalScrollOffset - minScrollpix)%  view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM() )  ;
//			int hourTime = (int) ((computeHorizontalScrollOffset - minScrollpix)/ ( view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()  ));
//			float perMinTime = (float) minTime / ((float)  view.getNodeWidthHide()*view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM() )  * 60;
//			float perSecTime = (float) minTime / ((float)  view.getNodeWidthHide() *view.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM() )  * 3600
//					% 60;
//			Log.e(TAG, "hourTime=" + hourTime + " perMinTime =" + perMinTime
//					+ "  perSecTime " + perSecTime);
//			 
//			return (int) (hourTime*3600+perMinTime*60+perSecTime);
//		}
//		
//	}
	
	
	private boolean isOutofCurrentTimeInSec(int sec){
 		 
		 
		 int mCurrentTimeInSec = (int) (((System.currentTimeMillis()/1000)%(24*3600))+  TimeZone.getDefault().getOffset(System.currentTimeMillis())%(24*3600));
//		 int mCurrentTimeInSec = ((int) ((System.currentTimeMillis()/1000+  TimeZone.getDefault().getOffset(System.currentTimeMillis()))%(24*3600)))%(24*3600); 
		 if(sec > (mCurrentTimeInSec+60*5+6*24*3600)){
			
			 return true;
		 }
		 return false;
	}
 
	@Override
	public void fling(int velocityX) {
		// TODO Auto-generated method stub
		super.fling(velocityX);
	}

	public void setNoteInfoData(NoteInfoData mNoteInfoData) {
		// TODO Auto-generated method stub
		this.mNoteInfoData = mNoteInfoData;
		view.setNoteInfoData(  mNoteInfoData);
	}

	public void setTimeLinePlayCallBackInterface(
			TimeLinePlayCallBackInterface mTimeLinePlayCallBackInterface) {
		// TODO Auto-generated method stub
		this.mTimeLinePlayCallBackInterface = mTimeLinePlayCallBackInterface;
	}

	public int getToadyStartTime(){
		if(view!=null){
			return view.getTodayStartTime();
		}
		else{
			return -1;
		}
	}
	
	public void setEventColor(int ColorResIndex){
		if(view!=null){
			  view.setEventColor(ColorResIndex);
		}
	}
}
