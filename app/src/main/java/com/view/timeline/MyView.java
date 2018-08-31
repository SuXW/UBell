package com.view.timeline;
 
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.provider.CalendarContract.Colors;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.timeline.listenview.NoteInfoData;

public class MyView extends View {
	Context context;
	int mScreenWitdh,mScreenHeigh;
	int mScreenHigh;
	int currentMinute;
	int currentTime;
	int currentYear;
	int currentMonth;
	int currentDay;
	int currentHour;
	int currentSecond;
	int mWidth;
	int mHeight;
	float nodeWidth = 120;
	NoteInfoData mNoteInfoData;
	float nodeSmallWidth = 120;
	
	float frameScreenWidth = 0;
	private float mDensity;
	private     int PIX_EVENT_MAGTOP = 10;
	private     int ITEM_MAX_HEIGHT  ;
	private     int ITEM_MIN_HEIGHT = 20;
	private     int ITEM_MAX_HEIGHT_STARTY  ;
	private     int ITEM_MIN_HEIGHT_STARTY = 20;
	 private static final int  SHOWTIME_DIVIDER =1;
	 private static final int TEXT_SIZE = 12;
	 private static final int SHOW_HIDEHOUR_TIME=6; 
	 int timeUnit=0;
	 private static final float SHOW_PIX_TIME=60.0f;//每个bit代表的时间秒数 
	 public static int getShowHidehourTime() {
		return SHOW_HIDEHOUR_TIME;
	}


	private static final int SHOW_HOUR_TIME = 24+SHOW_HIDEHOUR_TIME;//前后增加SHOW_HIDEHOUR_TIME/2个小时显示
	 private static final int SCREEN_SHOW_DIVIDER_NUM = 4; 
	 private     int SCREEN_SHOW_HOURNODE_DIVIDER_NUM=1 ; //每6小时节点数
	 public int getSCREEN_SHOW_HOURNODE_DIVIDER_NUM() {
		return SCREEN_SHOW_HOURNODE_DIVIDER_NUM;
	}

	 private static final int TIMEUNIT1HOUR = 1; 
	 private static final int TIMEUNIT12MIN = 0; 
	 private static final int TIMEUNIT6HOUR = 2; 
	
	public int getTimeUnit() {
		return timeUnit;
	}
	public void setTimeUnit(int timeUnit) {
		this.timeUnit = timeUnit;
	}
	public MyView(Context context , int timeUnit) {
		super(context);
		this.context = context;
		   this.timeUnit =timeUnit;
		this.mHeight =300;
		if(timeUnit==TIMEUNIT12MIN){
			SCREEN_SHOW_HOURNODE_DIVIDER_NUM =36;
		}else if(timeUnit==TIMEUNIT1HOUR){
			SCREEN_SHOW_HOURNODE_DIVIDER_NUM =6;
		}else{
			SCREEN_SHOW_HOURNODE_DIVIDER_NUM=1;
		}
		// TODO Auto-generated constructor stub
		
	}
	public float getNodeWidthHide() {
		return nodeWidth;
	}

 

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// 获得屏幕宽度
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWitdh = wm.getDefaultDisplay().getWidth();//.widthPixels;
		mScreenHeigh =wm.getDefaultDisplay().getHeight();//outMetrics.heightPixels;
		nodeWidth= ((float)mScreenWitdh)/(float)SCREEN_SHOW_DIVIDER_NUM;//大节点间宽度
		nodeSmallWidth = nodeWidth/5.0f; 
		frameScreenWidth = wm.getDefaultDisplay().getWidth();
		if(timeUnit==TIMEUNIT12MIN){
			SCREEN_SHOW_HOURNODE_DIVIDER_NUM =36;//6小时36块
		}else if(timeUnit==TIMEUNIT1HOUR){
			SCREEN_SHOW_HOURNODE_DIVIDER_NUM =6;//6小时6块
		}else{
			SCREEN_SHOW_HOURNODE_DIVIDER_NUM=1;//6小时一块
		}
		if(timeUnit==TIMEUNIT12MIN|| timeUnit==TIMEUNIT1HOUR)
		setMeasuredDimension((int) (((float)(((7*24)+6)/6)/(float)SCREEN_SHOW_DIVIDER_NUM )*SCREEN_SHOW_HOURNODE_DIVIDER_NUM* mScreenWitdh), heightMeasureSpec);
		else
			setMeasuredDimension((int) (((float)(((7*24)+24)/6)/(float)SCREEN_SHOW_DIVIDER_NUM )*SCREEN_SHOW_HOURNODE_DIVIDER_NUM* mScreenWitdh), heightMeasureSpec);
	}
 

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {

		mDensity = getContext().getResources().getDisplayMetrics().density;
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawScaleLine(canvas); 
	}

	private void drawScaleLine(Canvas canvas) {
		// TODO Auto-generated method stub
		mWidth = getWidth();
 		mHeight = getHeight();
		ITEM_MAX_HEIGHT = mHeight*5/7;
		ITEM_MIN_HEIGHT = mHeight*2/7;
		ITEM_MIN_HEIGHT_STARTY = mHeight/3;
		ITEM_MAX_HEIGHT_STARTY = mHeight/6;
		
	  
		
		Paint linePaint = new Paint();
		linePaint.setStrokeWidth(2);
		linePaint.setColor(Color.WHITE);
		float sizecount = mWidth / nodeWidth;
		TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(TEXT_SIZE * mDensity);
		textPaint.setColor(Color.WHITE);
		
		TextPaint datePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		datePaint.setTextSize(TEXT_SIZE * mDensity);
		datePaint.setColor(Color.rgb(255, 125, 0));
		
		
		int  TodayStartTimeInSec =getTodayStartTime();
		for (int i = 0; i < sizecount; i++) {
			canvas.drawLine(nodeWidth * i, getPaddingTop() + ITEM_MAX_HEIGHT_STARTY-5*mDensity, nodeWidth * i,
					mHeight- getPaddingBottom()- ITEM_MAX_HEIGHT_STARTY-5*mDensity, linePaint);//长线
			
			if(timeUnit==TIMEUNIT12MIN){//10min
				//SCREEN_SHOW_HOURNODE_DIVIDER_NUM =6; 6格子为1小时
				
				if(mNoteInfoData!=null)
				{
					 int second =  ((24-SHOW_HIDEHOUR_TIME/2+i/6)%24)*3600+(60*10*i)%(60*10*6);
					 if(second%(3600*24)==0){
						 String date =getLocalTimeMMdd(TodayStartTimeInSec+  ((24-SHOW_HIDEHOUR_TIME/2+i/6) )*3600-24*3600)  ;
						 Log.d("","second ="+date);  
						 canvas.drawText(date  , nodeWidth * i-nodeSmallWidth/2, mHeight- getPaddingBottom()- ITEM_MAX_HEIGHT_STARTY+5*mDensity, datePaint);//时间字符串

					 }else
					 canvas.drawText(cal( second)  , nodeWidth * i-nodeSmallWidth/2, mHeight- getPaddingBottom()- ITEM_MAX_HEIGHT_STARTY+5*mDensity, textPaint);//时间字符串

				}
			    else
				 canvas.drawText(cal( ((24-SHOW_HIDEHOUR_TIME/2+i/6)%24)*3600+(60*10*i)%(60*10*6))  , nodeWidth * i-nodeSmallWidth/2, mHeight- getPaddingBottom()- ITEM_MAX_HEIGHT_STARTY+5*mDensity, textPaint);//时间字符串

			}else if(timeUnit==TIMEUNIT1HOUR){//1hour
				if(mNoteInfoData!=null)
				{
					 int second =  ((24-SHOW_HIDEHOUR_TIME/2+i)%24)*3600 ;
					 if(second%(3600*24)==0){
						 String date =getLocalTimeMMdd(TodayStartTimeInSec+ ((24-SHOW_HIDEHOUR_TIME/2+i) )*3600 -24*3600);
						 Log.d("","second ="+date);
						 canvas.drawText(date, nodeWidth * i-nodeSmallWidth/2, mHeight- getPaddingBottom()- ITEM_MAX_HEIGHT_STARTY+5*mDensity, datePaint);//时间字符串

					 }else
						 canvas.drawText(cal( second), nodeWidth * i-nodeSmallWidth/2, mHeight- getPaddingBottom()- ITEM_MAX_HEIGHT_STARTY+5*mDensity, textPaint);//时间字符串

				}
			    else
				 canvas.drawText(cal( ((24-SHOW_HIDEHOUR_TIME/2+i)%24)*3600 ), nodeWidth * i-nodeSmallWidth/2, mHeight- getPaddingBottom()- ITEM_MAX_HEIGHT_STARTY+5*mDensity, textPaint);//时间字符串
			}else{//6hour
				if(mNoteInfoData!=null)
				{
					 int second = ((24-24/2+i*6)%24)*3600  ;
					 if(second%(3600*24)==0){
						 String date =getLocalTimeMMdd(TodayStartTimeInSec+((24-24/2+i*6) )*3600-24*3600) ;
						 Log.d("","second ="+date); 
						 canvas.drawText(date, nodeWidth * i-nodeSmallWidth/2, mHeight- getPaddingBottom()- ITEM_MAX_HEIGHT_STARTY+5*mDensity, datePaint);//时间字符串

					 }else
						 canvas.drawText(cal( second), nodeWidth * i-nodeSmallWidth/2, mHeight- getPaddingBottom()- ITEM_MAX_HEIGHT_STARTY+5*mDensity, textPaint);//时间字符串

				}
			    else
				 canvas.drawText(cal( ((24-24/2+i*6)%24)*3600 ), nodeWidth * i-nodeSmallWidth/2, mHeight- getPaddingBottom()- ITEM_MAX_HEIGHT_STARTY+5*mDensity, textPaint);//时间字符串
			}
		}
		
		linePaint.setStrokeWidth(1);
		float sizecountsmall = mWidth / nodeSmallWidth;
		for (int i = 0; i < sizecountsmall; i++) {
			canvas.drawLine(nodeSmallWidth * i, getPaddingTop()+ITEM_MIN_HEIGHT_STARTY-5*mDensity, nodeSmallWidth * i,
					mHeight- getPaddingBottom()-ITEM_MIN_HEIGHT_STARTY-5*mDensity, linePaint);//短线
		}
		
		Paint eventpaint = new Paint();
		eventpaint.setStrokeWidth(2);
		if(eventcolor==0){
			eventpaint.setColor(Color.argb(0xa0,255,125,0));
			Log.e("timeline", "do not  set event paint color");
		}else{
			eventpaint.setColor(context.getResources().getColor(eventcolor));
 		}
		
		if(mNoteInfoData!=null) {
			int mStartTime = mNoteInfoData.starttime;
			int mCurrentTodayStartTimeInSec =getTodayStartTime();
			int mNoTodaySec = mCurrentTodayStartTimeInSec-mStartTime;
			 
			int mNoTodaybit = 0;
			int starti = mNoTodaybit/8;
			int startbit = mNoTodaybit%8;
			if(mNoTodaySec>0){
				
					mNoTodaybit = (int) (mNoTodaySec/(60)) ;//60秒一个bit
				
				  starti = mNoTodaybit/8;//总的字节数
				  startbit = mNoTodaybit%8;
			}
//			Log.d("","starti ="+starti+
//					"     startbit ="+startbit+"     mNoTodaybit ="+mNoTodaybit+"     starti ="+starti+"    mNoTodaySec="+mNoTodaySec+"     mCurrentTodayStartTimeInSec="+mCurrentTodayStartTimeInSec+"     mStartTime="+mStartTime);
			boolean isfirst = true;
			float bitWidth ;
			
			if(timeUnit==TIMEUNIT12MIN)
			{
			
 			 bitWidth = ((float)mScreenWitdh)/(40.0f*60.0f/60.0f);//每60秒的宽度
 
			}
			else if(timeUnit==TIMEUNIT1HOUR)
			 {
 			  bitWidth = ((float)mScreenWitdh)/(4.0f*3600.0f/60.0f);//每60秒的宽度
 
			 }else{
				 bitWidth = ((float)mScreenWitdh)/(24.0f*3600.0f/60.0f)*8.0f;// 八分钟宽度
			 }
			
			
			eventpaint.setStrokeWidth(bitWidth);
			for (int i = starti ; i < mNoteInfoData.bytes; i++) {
		if(timeUnit!=TIMEUNIT6HOUR){
			int bit =0;
			if(isfirst)
			{
				isfirst = false;
				bit = startbit;
			}
			
			for(;bit<8;bit ++){
				if(((mNoteInfoData.dataBitMap[i]>>(7-bit))&1)==1)
				{ 
					if(timeUnit==TIMEUNIT12MIN)
						{
						float pxdraw = (float) (getNodeWidthHide()*SHOW_HIDEHOUR_TIME/2*6)+  ((i-starti)*8.0f+(bit+1))*bitWidth -(startbit)*bitWidth ;//数据长度：(bit+1) //startbit没有用的bit数：startbit
						 
						canvas.drawLine(pxdraw  ,PIX_EVENT_MAGTOP*mDensity, pxdraw ,
								mHeight- getPaddingBottom()- ITEM_MAX_HEIGHT_STARTY-PIX_EVENT_MAGTOP*mDensity, eventpaint);//事件线 
						
						}
						else if(timeUnit==TIMEUNIT1HOUR)
						 {
							 
							float pxdraw = (float) (getNodeWidthHide()*((float)(SHOW_HIDEHOUR_TIME)/2))+  ((i-starti)*8.0f+(bit+1))*bitWidth -(startbit)*bitWidth ;
							 
							canvas.drawLine(pxdraw ,PIX_EVENT_MAGTOP*mDensity,pxdraw ,
									mHeight- getPaddingBottom()- ITEM_MAX_HEIGHT_STARTY-PIX_EVENT_MAGTOP*mDensity, eventpaint); 
							 
						 }
								
							
				}
			}
		}else{
			
				if(mNoteInfoData.dataBitMap[i]!=0)
					{
						float pxdraw = (float) getNodeWidthHide()*2 + ((i-starti))*bitWidth ;
						Log.d("","pxdraw=======>"+pxdraw+"----->>"+bitWidth);
						canvas.drawLine((float)  pxdraw ,PIX_EVENT_MAGTOP*mDensity, (float) pxdraw ,
						mHeight- getPaddingBottom()- ITEM_MAX_HEIGHT_STARTY-PIX_EVENT_MAGTOP*mDensity, eventpaint);//事件线/ 
					}
		}
		}
		}
	
		
	}
	
//	private void printfTime(int computeHorizontalScrollOffset){
//		int minScrollpix,maxScrollpix;
//		  if(this.isTenMin()){
//			  minScrollpix=(int) ((int) (this.getShowHidehourTime()/2-1)* this.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM() *(this.getNodeWidthHide())+ 4*(this.getNodeWidthHide()));
//			  maxScrollpix = (int) (this.getWidth()-this.getNodeWidthHide()*2-this.getShowHidehourTime()/2 *this.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM() * this.getNodeWidthHide());//最左侧，位置大于2个节点+3个小时
// 			 
//		  }else{
//			  minScrollpix=(int) this.getNodeWidthHide();  //屏幕左侧只有一个节点
//			  maxScrollpix = (int) (this.getWidth()-this.getNodeWidthHide()*5 );//最左侧，位置大于2个节点+3个小时
//		 
//		  }
//		int minTime = (int) ((computeHorizontalScrollOffset - minScrollpix)%  this.getNodeWidthHide()*this.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM() )  ;
//		int hourTime = (int) ((computeHorizontalScrollOffset - minScrollpix)/ ( this.getNodeWidthHide()*this.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM()  ));
//		float perMinTime = (float) minTime / ((float)  this.getNodeWidthHide()*this.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM() )  * 60;
//		float perSecTime = (float) minTime / ((float)  this.getNodeWidthHide() *this.getSCREEN_SHOW_HOURNODE_DIVIDER_NUM() )  * 3600
//				% 60;
//		Log.e("", "===========Bitmap  Record Time:=========hourTime=" + hourTime + " :" + (int)perMinTime
//				+ ":" + (int)perSecTime);
//		 
//	}
	
	public int getTodayStartTime(){
		int mCurrentTimeInSec= (int)  (System.currentTimeMillis()/1000);//+  TimeZone.getDefault().getOffset(System.currentTimeMillis()))%(24*3600)
		int mCurrentTodayInSec =( mCurrentTimeInSec%(3600*24)+(TimeZone.getDefault().getOffset(System.currentTimeMillis())/1000)%(24*3600))%(3600*24);//当天的秒数。
		int mCurrentTodayStartTimeInSec = mCurrentTimeInSec-mCurrentTodayInSec-6*24*3600;
//		Log.d("","mCurrentTodayStartTimeInSec ="+mCurrentTodayStartTimeInSec+
//				"     mCurrentTimeInSec ="+mCurrentTimeInSec+
//				"     mCurrentTodayInSec ="+mCurrentTodayInSec);
		
		return mCurrentTodayStartTimeInSec;//当天开始的秒数。
	}
	
	private void drawEventData(NoteInfoData mNoteInfoData){
		
	}

 
	public static String cal(int second) {
		//second =second*SHOWTIME_DIVIDER;
        int h = 0;  
        int d = 0;  
        int s = 0;  
        int temp = second % 3600;  
        if (second >= 3600) {  
            h = second / 3600 % 24;  
            if (temp != 0) {  
                if (temp > 60) {  
                    d = temp / 60;  
                    if (temp % 60 != 0) {  
                        s = temp % 60;  
                    }  
                } else {  
                    s = temp;  
                }  
            }  
        } else {  
            d = second / 60;  
            if (second % 60 != 0) {  
                s = second % 60;  
            }  
        }  
        String sh,sd,ss;
        if(h<10)
        	sh = "0"+h;
        else
        	sh = h+"";
        if(d<10)
        	sd = "0"+d;
        else
        	sd = d+"";
        if(s<10)
        	ss = "0"+s;
        else
        	ss = s+"";
        
           return sh + ":" + sd   ;  
      
    }
	public void setNoteInfoData(NoteInfoData mNoteInfoData) {
		// TODO Auto-generated method stub
		this.mNoteInfoData = mNoteInfoData;
		this.postInvalidate();
	}  
	
	
	public boolean hasExistEvent(int utcTime){
	if(this.mNoteInfoData==null )
			return false;
	int playTimeDiff = 	utcTime-this.mNoteInfoData.starttime;
	int playBitLoaction = (int) (playTimeDiff/SHOW_PIX_TIME);
	Log.v("","滑动到播放时间 ：playBitLoaction="+playBitLoaction+"  playTimeDiff="+playTimeDiff+"    this.mNoteInfoData.starttime="+this.mNoteInfoData.starttime+"   utcTime="+utcTime+ "   时："+playTimeDiff/3600+ "   分："+playTimeDiff/60%60+  "秒："+playTimeDiff%60  );
	int starti = playBitLoaction/8 ;
	boolean isfirst = true; 
	int  startbit = 7-playBitLoaction%8;
	int    startbitdiff1 = startbit-1; 
	int  startbitadd1 = startbit+1;
	if(startbitdiff1<0)startbitdiff1=0;
	if(startbitadd1>7)startbitadd1=7;//误差前后20秒
	if(starti>mNoteInfoData.bytes|| starti<0) return false;
//	if(startbit+1>mNoteInfoData.validbits || startbit<0) return false;
	 Log.v("","滑动到播放时间 ：playBitLoaction="+playBitLoaction+"  mNoteInfoData.dataBitMap[starti]="+mNoteInfoData.dataBitMap[starti] +"   starti="+starti +"  startbit="+startbit);

     if(((mNoteInfoData.dataBitMap[starti]>>startbit)&1)==1) // ||((mNoteInfoData.dataBitMap[starti]>>startbitdiff1)&1)==1 || ((mNoteInfoData.dataBitMap[starti]>>startbitadd1)&1)==1 )
	 { 
    	 Log.v("","--------滑动，---存在事件");
			return true; 		
						
	 }
     Log.v("","------滑动，不存在事件");
	 return false;
 
	
	}
	
	public int findFirstExistEvent(int utcTime){
	if(this.mNoteInfoData==null )
	 return 0;
	int playTimeDiff = utcTime-this.mNoteInfoData.starttime;
	int playBitLoaction = (int) (playTimeDiff/SHOW_PIX_TIME);
	Log.v("","滑动到播放时间 ：playBitLoaction="+playBitLoaction+"  playTimeDiff="+playTimeDiff+"    this.mNoteInfoData.starttime="+this.mNoteInfoData.starttime+"   utcTime="+utcTime+ "   时："+playTimeDiff/3600+ "   分："+playTimeDiff/60%60+  "秒："+playTimeDiff%60  );
	int starti = playBitLoaction/8 ;//总的字节
	boolean isfirst = true; 
	int  startbit =  playBitLoaction%8;
   
	if(starti>mNoteInfoData.bytes|| starti<0) return 0;
	for (int i = starti ; i < mNoteInfoData.bytes; i++) {
		 
		int bit =0;
		if(isfirst)
		{
			isfirst = false;
			bit = startbit;
		}
		for(;bit<8;bit ++){
			if(((mNoteInfoData.dataBitMap[i]>>(7-bit))&1)==1)
			{ 
				if(i == mNoteInfoData.bytes-1 && bit==7)
					return -1;
				int firstcanPlayTime = (int) (this.mNoteInfoData.starttime+ ((float)i)*SHOW_PIX_TIME*8.0f+((float)bit)*SHOW_PIX_TIME)-this.mNoteInfoData.starttime%60;
 				return  firstcanPlayTime;
						
			}
		}
		 
	 }
	return -1;	
}
 
	public static String getLocalTime(long timems) {
		Calendar var1 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
		var1.setTimeInMillis(timems*1000);
		SimpleDateFormat var2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		var2.setTimeZone(TimeZone.getDefault());
		return var2.format(var1.getTime());
	}
	public static String getLocalTimeMMdd(long timems) {
		Calendar var1 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
		var1.setTimeInMillis(timems*1000);
		SimpleDateFormat var2 = new SimpleDateFormat("MM/dd");
		var2.setTimeZone(TimeZone.getDefault());
		return var2.format(var1.getTime());
	}
	int eventcolor = 0;
	public void setEventColor(int colorResIndex) {
		// TODO Auto-generated method stub
		eventcolor= colorResIndex ;
	}
}
