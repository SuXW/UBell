package cn.ubia;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.widget.MyProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * 查看单张照片
 * 
 * @author hello
 * 
 */	
public class PhotoViewActivity extends BaseActivity {

	private static final int MENU_SHARE = 0;
	private static final int MENU_DELETE = 1;
	//private PhotoView mPhotoView;
	private MyProgressBar mProgressBar;
	private String mUriString;
	private static final String FILE_SCHEME = "file://";
	private static final int QQ = 7;
	PhotoView mPhotoView;
	PhotoViewAttacher mAttacher;
	private int position;
	ArrayList list2 ;//= bundle.getParcelableArrayList("list");
	private long firstClick;
	private long lastClick; // 计算点击的次数
	private int count;
	private boolean hasscale = false;
	final float f[] = {1.0F,0.0F,0.0F,0.0F,-1.0F,120.0F,0.0F,0.0F,1.0F};  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_view);
		setTitle(R.string.page9_text_photo_view);
		getActionBar().hide();
		Bundle bundle=this.getIntent().getExtras();
		list2 = bundle.getParcelableArrayList("list");
		position = bundle.getInt("position") ;
		mUriString = (String)list2.get( position );//getIntent().getStringExtra("uri");
		if (mUriString == null) {
			getHelper().showMessage(R.string.page9_failed_to_view_image);
			return;
		}
		mPhotoView= (PhotoView) findViewById(R.id.iv_photo); //mPhotoView 
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.camera_thumbnail)
				.showImageForEmptyUri(R.drawable.camera_thumbnail)
				.showImageOnFail(R.drawable.camera_thumbnail).cacheInMemory()
				.build();
		ImageLoader imageLoader = ImageLoader.getInstance();
		mProgressBar = new MyProgressBar(this);
		imageLoader.displayImage(mUriString, mPhotoView, options,
				new ImageLoadingListener() {
					@Override
					public void onLoadingCancelled(String arg0, View arg1) {

					}
					//手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
					float x1 = 0;
					float x2 = 0;
					float y1 = 0;
					float y2 = 0;
			 

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap bitmap) {
						mProgressBar.hide();
						
					    mAttacher = new PhotoViewAttacher(mPhotoView);
					     mAttacher.setOnPhotoTapListener(new PhotoTapListener());
				        mPhotoView.setOnTouchListener(new OnTouchListener() {
							
				        	   /** 记录是拖拉照片模式还是放大缩小照片模式 */
				            private int mode = 0;// 初始状态
				            /** 拖拉照片模式 */
				            private static final int MODE_DRAG = 1;
				            /** 放大缩小照片模式 */
				            private static final int MODE_ZOOM = 2;
				            private static final int MODE_DOUBLECLICK =3;
				            /** 用于记录拖拉图片移动的坐标位置 */
				   
				            /** 用于记录图片要进行拖拉时候的坐标位置 */
				           private Matrix currentMatrix = new Matrix();
				 
				            /** 用于记录开始时候的坐标位置 */
				            private PointF startPoint = new PointF();
				     
				            /** 两个手指的开始距离 */
				            private float startDis;
				            /** 两个手指的中间点 */
				            private PointF midPoint;
				       
				           // private   Matrix defaultMatrix = new Matrix();
				            private Matrix defaultMatrix = mPhotoView.getDisplayMatrix();
				            private Matrix matrix =  new Matrix();
							public boolean onTouch(View arg0, MotionEvent event) {
								//继承了Activity的onTouchEvent方法，直接监听点击事件
						 
							 /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
				            switch (event.getAction() & MotionEvent.ACTION_MASK) {
				                // 手指压下屏幕
				                case MotionEvent.ACTION_DOWN:
				                	
				                	Log.v("mode","ACTION_DOWN mode ="+ mode +"defaultMatrix =currentMatrix  ="+ (defaultMatrix ==currentMatrix)  );
				                	if (firstClick != 0 && System.currentTimeMillis() - firstClick > 300) {
				            			count = 0;
				            		}
				            		count++;
				            		if (count == 1) {
				            			firstClick = System.currentTimeMillis();
				            		} else if (count == 2) {
				            			lastClick = System.currentTimeMillis();
				            			// 两次点击小于300ms 也就是连续点击
				            			if (lastClick - firstClick < 300) {// 判断是否是执行了双击事件
				            				Log.v("IOTCameraptz", "实现双机事件");
  
				            				 mode = MODE_DOUBLECLICK;
				            				 matrix.set(defaultMatrix);
				            				 hasscale = false;
				            				  break;
				            			}
				            		}

				            	 if(!hasscale)
				            	 {
				            		 
				            		 matrix.set(defaultMatrix);
				            	 }
				                    	mode = MODE_DRAG;
				                    // 记录ImageView当前的移动位置
				                
				                    	currentMatrix.set(mPhotoView.getImageMatrix());
				                        startPoint.set(event.getX(), event.getY());
				                 
				             
				                    break;
				                // 手指在屏幕上移动，改事件会被不断触发
				                case MotionEvent.ACTION_MOVE:
				                	Log.v("mode","ACTION_MOVE mode ="+ mode +"defaultMatrix =currentMatrix  ="+ (defaultMatrix ==currentMatrix)  );
				                    // 拖拉图片
				                	if(hasscale){
				                    if (mode == MODE_DRAG ) {
				                        float dx = event.getX() - startPoint.x;
				                        // 得到x轴的移动距离
				                        float dy = event.getY() - startPoint.y; 
				                        // 得到x轴的移动距离
				                        // 在没有移动之前的位置上进行移动
				                        matrix.set(currentMatrix);
				                        // 得到缩放倍数
				                        matrix.postTranslate(dx, dy);
				                        hasscale = true;
				                    }
//				                    放大缩小图片
				                    else if (mode == MODE_ZOOM) { 
				                		float endDis = distance(event);
										// 结束距离
										// if (endDis > 10f)
										{
											// 两个手指并拢在一起的时候素大于10
											float scale = endDis / startDis;
											// 得到缩放倍数

											//Log.v("mode", " ACTION_POINTER_DOWN getValues2 =" + f[0] +"scale ="+scale);
											if(f[0] <10 && f[0] >0.2  ){
												matrix.set(currentMatrix);
												matrix.postScale(scale, scale, midPoint.x,
													midPoint.y);
											//	Log.v("mode", "ACTION_MOVE scale ="+ scale);
												matrix.getValues(f);
												hasscale = true;
												break;
											}
											
											if(f[0] >=10  ){
								
											
												if(scale<1){
													matrix.set(currentMatrix);
													matrix.postScale(scale, scale, midPoint.x,
														midPoint.y);
													//Log.v("mode", "ACTION_MOVE scale ="+ scale);
													matrix.getValues(f);
												}
											}
											
											if(f[0] <=0.2  ){
									
											 
												if(scale>1){
													matrix.set(currentMatrix);
													matrix.postScale(scale, scale, midPoint.x,
														midPoint.y);
													//Log.v("mode", "ACTION_MOVE scale ="+ scale);
													matrix.getValues(f);
												}
											}
										}
										hasscale = true;
				                    }
				                	}
      
				                    break;
				                // 手指离开屏幕
				                case MotionEvent.ACTION_UP:
				                	Log.v("mode","ACTION_UP mode ="+ mode +"defaultMatrix =currentMatrix  ="+ (defaultMatrix ==currentMatrix)  );
				                    // 当触点离开屏幕，但是屏幕上还有触点(手指)
				                // currentMatrix.set(mPhotoView.getImageMatrix());.
				                
				                	if(mode == MODE_DOUBLECLICK)
				                		matrix.set(defaultMatrix);

				                    else if(mode ==  MODE_ZOOM ||mode == MODE_DRAG)
				                    	 currentMatrix.set(mPhotoView.getImageMatrix());
					                  
				                   if(!hasscale)
				                    {
				                        float dx = event.getX() - startPoint.x;
				                         if(dx<-100)
				                         {
				                        		//Toast.makeText( PhotoViewActivity.this, "下一张", Toast.LENGTH_SHORT).show();
												 position++ ;
												 if(position>list2.size()-1)
													 position =0;
												list2.get( position);
												ImageLoader.getInstance().displayImage((String)list2.get( position ),mPhotoView);
												matrix.set(defaultMatrix);
												hasscale = false;
				                         }
				                         else  if(dx>100){
				                        	 
				                        	 position-- ;
											 if(position < 0)
												 position =list2.size()-1;
												ImageLoader.getInstance().displayImage((String)list2.get( position ),mPhotoView);
												matrix.set(defaultMatrix);
												hasscale = false;
											//Toast.makeText( PhotoViewActivity.this, "上一张", Toast.LENGTH_SHORT).show();
				                         }
				                    	
				                    }
				                	mode = 0;
				                case MotionEvent.ACTION_POINTER_UP:
				                    mode = 0;
				                    break;
//				                当屏幕上已经有触点(手指)，再有一个触点压下屏幕
				                case MotionEvent.ACTION_POINTER_DOWN:
				                	Log.v("mode"," ACTION_POINTER_DOWN mode ="+ mode +"defaultMatrix =currentMatrix  ="+ (defaultMatrix ==currentMatrix)  );
				                    mode = MODE_ZOOM;
				                    /** 计算两个手指间的距离 */
				                    startDis = distance(event);
				                    /** 计算两个手指间的中间点 */
				                    //if (startDis > 10f) 
				                    { // 两个手指并拢在一起的时候像素大于10
				                        midPoint = mid(event);
				                        //记录当前ImageView的缩放倍数
				                        currentMatrix.set(mPhotoView.getImageMatrix());
				                        hasscale = true;
				                    }
				                    break;
				            }
				             mPhotoView.setImageMatrix(matrix);
				            return true;
				        }

						       /** 计算两个手指间的距离 */
					        private float distance (MotionEvent event){
					            float dx = event.getX(1) - event.getX(0);
					            float dy = event.getY(1) - event.getY(0);
					            /** 使用勾股定理返回两点之间的距离 */
					            return (float) Math.sqrt(dx * dx + dy * dy);
					        }
					 
					        /** 计算两个手指间的中间点 */
					        private PointF mid (MotionEvent event){
					            float midX = (event.getX(1) + event.getX(0)) / 2.0f;
					            float midY = (event.getY(1) + event.getY(0)) / 2.0f;
					            return new PointF(midX, midY);
					        }

						});
				       
				 
				 
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						mProgressBar.hide();
						getHelper().showMessage(R.string.page9_failed_to_view_image);
					}

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						mProgressBar.show();

					}
				});
		initView();
		getActionBar().hide();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuItem item = menu.add(1, MENU_SHARE, 0, R.string.text_share);
//		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//		item.setIcon(android.R.drawable.ic_menu_share);
//		return super.onCreateOptionsMenu(menu);
//	}
	private ImageView back;
	private TextView title;
	private ImageView title_img;
	private void initView() {
		back = (ImageView) this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		title = (TextView) this.findViewById(R.id.title);
		this.findViewById(R.id.left_ll).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		title_img = (ImageView) findViewById(R.id.title_img);
		title_img.setImageResource(R.drawable.record_client);
		title.setText(getText(R.string.PhotoGridActivity_local_video)+"");
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SHARE:
		
			break;
		case MENU_DELETE:
			String path = mUriString.substring(FILE_SCHEME.length());
			Log.d("path", path);
			File file = new File(path);
			file.delete();
			finish();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// ShareSDK.stopSDK(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/** Returns a share intent */
	private Intent getDefaultShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("image/*");
		Uri uri = Uri.parse(mUriString);
		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		return shareIntent;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
		    // Need to call clean-up
	        mAttacher.cleanup();
		} catch (Exception e) {
		}
	}

	

	



	
	
 
    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";

    private TextView mCurrMatrixTv; 
    private Toast mCurrentToast;

    private Matrix mCurrentDisplayMatrix = null;
 
	  public class PhotoTapListener implements OnPhotoTapListener {

	        @Override
	        public void onPhotoTap(View view, float x, float y) {
	            float xPercentage = x * 100f;
	            float yPercentage = y * 100f;

	            showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view == null ? 0 : view.getId()));
	        }
	    }

	    private void showToast(CharSequence text) {
	        if (null != mCurrentToast) {
	            mCurrentToast.cancel();
	        }

	        mCurrentToast = Toast.makeText(PhotoViewActivity.this, text, Toast.LENGTH_SHORT);
	        mCurrentToast.show();
	    }
 
    
    

}
