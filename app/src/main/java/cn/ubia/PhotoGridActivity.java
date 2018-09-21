package cn.ubia;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.ubia.UBell.BuildConfig;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.widget.MyProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class PhotoGridActivity extends BaseActivity implements
		OnItemClickListener, OnItemLongClickListener {

	private static final int MENU_DELETE = 0;
	private ViewGroup mRootView;
	private List<ImageInfo> mImageInfos = new ArrayList<ImageInfo>();
	ArrayList<String> mList = new ArrayList<String>();
	private List<Integer> deletemImageInfos = new ArrayList<Integer>();
	private List<String> deletemImageUri = new ArrayList<String>();
	private Map<String, DeviceInfo> deviceMap = new HashMap<String, DeviceInfo>();
	private ImageAdapter mAdapter;
	private GridView mGridView;
	private DeviceInfo deviceInfo;
	private int ShowDeviceIndex;
	private LinearLayout layoutDel;
	private Button btnDel;
	private Button selectall;
	private Button unselectall;
	ArrayList<HashMap<String, Object>> imagelist = new ArrayList<HashMap<String, Object>>();
	private ImageView back;
	private TextView title;
	private ImageView title_img;
	private boolean showSystemAlbum = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		deviceInfo = MainCameraFragment.getexistDevice(this.getIntent()
				.getStringExtra("dev_uid"));
		// (DeviceInfo) this .getIntent()
		// .getSerializableExtra("deviceInfo");

		// ShowDeviceIndex= this .getIntent().getInt("key");
		this.setContentView(R.layout.grid);

		mGridView = (GridView) this.findViewById(R.id.grid);
		mAdapter = new ImageAdapter(PhotoGridActivity.this, mImageInfos);
		mGridView.setAdapter(mAdapter);

		mGridView.setOnItemClickListener(this);
		mGridView.setOnItemLongClickListener(this);
		layoutDel = (LinearLayout) this.findViewById(R.id.del_bottom_layout);
		layoutDel.setVisibility(View.GONE);
		back = (ImageView) this.findViewById(R.id.back);
		back.setBackgroundResource(R.drawable.ic_action_left);
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						PhotoGridActivity.this.finish();
					}
				});
		title = (TextView) this.findViewById(R.id.title);
		title.setText(getString(R.string.PhotoGridActivity_local_local));
		title_img = (ImageView) findViewById(R.id.title_img);
		title_img.setVisibility(View.GONE);
		title_img.setImageResource(R.drawable.record_tap_off);
		this.findViewById(R.id.left_ll).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						PhotoGridActivity.this.finish();
					}
				});

		btnDel = (Button) this.findViewById(R.id.delete);
		btnDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (deletemImageInfos == null)
					return;
				int sized = deletemImageInfos.size();
				if (sized <= 0)
					return;
				// Log.d("tag", "onItemClick btnDel=" );
				for (int i = 0; i < sized; i++) {
					int delpos = deletemImageInfos.get(i);
					String delpath = mImageInfos.get(delpos).path;
					File file = new File(delpath);
					syncAlbum(delpath,file);
					file.delete();
					deletemImageUri.add(delpath);
				
				}
				;
				int deletesize = deletemImageUri.size();
				for (int i = 0; i < deletesize; i++) {

					removeImgaeinfos(deletemImageUri.get(i));

				} ;
				deletemImageUri.clear();
				deletemImageInfos.clear();
				mAdapter.notifyDataSetChanged();
				File dir = null;
				 dir = new File(Environment.getExternalStorageDirectory()
							.getAbsolutePath() + "/DCIM/Camera/");
				String[] var24 = new String[] { dir.getAbsolutePath() };
				String[] var25 = new String[] { "image/*" };
				OnScanCompletedListener var26 = new OnScanCompletedListener() {
					public void onScanCompleted(String var1, Uri var2) {
					}
				};
				MediaScannerConnection.scanFile(PhotoGridActivity.this, var24, var25, var26);
				
			}
		});
		// registerForContextMenu(mGridView);
		unselectall = (Button) this.findViewById(R.id.selectreverse);
		unselectall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				deletemImageInfos.clear();

				mAdapter.notifyDataSetChanged();
			}
		});

		selectall = (Button) this.findViewById(R.id.selectall);
		selectall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				deletemImageInfos.clear();
				for (int i = 0; i < mImageInfos.size(); i++) {
					deletemImageInfos.add(i);
				}
				mAdapter.notifyDataSetChanged();
			}

		});

		if (!getHelper().isSDCardValid()) {
			getHelper().showMessage(R.string.PhotoGridActivity_sdcard_not_mounted);
			return;
		}
		if (!mDataLoaded) {
			LoadDataTask task = new LoadDataTask();
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR) ;;
			mDataLoaded = true;
		}
		TextView	right2_tv   = (TextView) this.findViewById(R.id.right2_tv);
		right2_tv.setTextColor(Color.BLUE);
		right2_tv.setVisibility(View.VISIBLE); 
		right2_tv.setText(R.string.PhotoGridActivity_local_switch);
		right2_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				LoadDataTask task = new LoadDataTask();
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR) ;
				
			}
		});
	}

//	private AsyncTask<Void, Void, Void> loadDataTask = new AsyncTask<Void, Void, Void>()
	 public  class LoadDataTask  extends AsyncTask<Void, Void, Void>{
		MyProgressBar mProgressBar = null;

		protected void onPreExecute() {
			mProgressBar = new MyProgressBar(PhotoGridActivity.this, mRootView);
			mProgressBar.show();
			Log.v("PhotoGridFragment", "PhotoGridFragment ShowDeviceIndex = "
					+ 4);
		};

		@Override
		protected Void doInBackground(Void... params) {
			Log.v("PhotoGridFragment", "PhotoGridFragment ShowDeviceIndex = "
					+ 0);
			if(!showSystemAlbum)
				getAllImages();
			else
				getAllSystemImages() ;
			Log.v("PhotoGridFragment", "PhotoGridFragment ShowDeviceIndex = "
					+ 1);
			sortImages();
			Log.v("PhotoGridFragment", "PhotoGridFragment ShowDeviceIndex = "
					+ 2);
			for (ImageInfo imageinfo : mImageInfos) {
				mList.add(imageinfo.uri);
			}
			Log.v("PhotoGridFragment", "PhotoGridFragment ShowDeviceIndex = "
					+ 3);
			return null;
		}

		protected void onPostExecute(Void result) {
			mProgressBar.dismiss();
			mAdapter.notifyDataSetChanged();
			mDataLoaded = false;
			if(title!=null)
				{
				if(!showSystemAlbum)	
					title.setText(getString(R.string.PhotoGridActivity_local_system));
				else
					title.setText(getString(R.string.PhotoGridActivity_local_local));
				}
			Log.v("PhotoGridFragment", "PhotoGridFragment ShowDeviceIndex = "
					+ 5);
		}
 
	};

	private boolean mDataLoaded = false;
	private boolean isEditing = false;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, MENU_DELETE, 0, getString(R.string.photogridactivity_menu_delete));
		menu.setHeaderTitle(R.string.mycamearfragment_photogridactivity_title_operation);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int pos = menuInfo.position;
		ImageInfo imageInfo = mImageInfos.get(pos);
		switch (item.getItemId()) {

		case MENU_DELETE:
			File file = new File(imageInfo.path);
			file.delete();
			mImageInfos.remove(pos);
			mAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mDataLoaded = false;
		unregisterForContextMenu(mGridView);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mDataLoaded = false;
		unregisterForContextMenu(mGridView);
	}
	// 按时间倒序排序
	protected void sortImages() {
		// 排序会出现异常，“Comparison method violates its general contract!”
		// System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Collections.sort(mImageInfos, new ImageInfoComparator());
		// Collections.reverse(mImageInfos, new ImageInfoComparator());
	}

	// 获取所有照片
	// private void getAllImages1() {
	// mImageInfos.clear();
	// for (DeviceInfo deviceInfo : AntsApplication.myDeviceList) {
	// deviceMap.put(deviceInfo.UID, deviceInfo);
	// }
	// if (getHelper().isSDCardValid()) {
	// File dir = new File(Environment.getExternalStorageDirectory()
	// .getAbsolutePath() + Constants.PHOTO_PATH);
	// if (!dir.exists()) {
	// return;
	// }
	// if (deviceInfo != null) {
	// getDeviceImages(dir, deviceInfo.UID, deviceInfo);
	// } else {
	// String[] list = dir.list();
	// for (String deviceUid : list) {
	// DeviceInfo deviceInfo = deviceMap.get(deviceUid);
	// getDeviceImages(dir, deviceUid, deviceInfo);
	// }
	// }
	//
	// }
	// }
	// 获取所有照片
	private void getAllImages() {
		mImageInfos.clear();
		showSystemAlbum = true;
		Log.v("PhotoGridFragment", "PhotoGridFragment ShowDeviceIndex = "
				+ ShowDeviceIndex);
		File dir = null;
//		if (ShowDeviceIndex < 0) 
		{
			for (DeviceInfo deviceInfo : MainCameraFragment.DeviceList) {
				Log.i("IOTCamera", "getAllImages:" + deviceInfo.UID);
				deviceMap.put(deviceInfo.UID, deviceInfo);
			}
			dir = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/DCIM/Camera/");
		}
//		else { //vr获取所有设备的相册
//
//			//DeviceInfo deviceInfo = MainCameraFragment.DeviceList
//			//		.get(ShowDeviceIndex);
//			deviceInfo = MainCameraFragment.getexistDevice(this.getIntent()
//					.getStringExtra("dev_uid"));
//			deviceMap.put(deviceInfo.UID, deviceInfo);
//			dir = new File(Environment.getExternalStorageDirectory()
//					.getAbsolutePath() + "/DCIM/Camera/UBell/" + deviceInfo.UID + "/");
//			if (getHelper().isSDCardValid()) {
//				Log.i("IOTCamera", "getAllImages:00000000 deviceInfo.UID ="
//						+ deviceInfo.UID);
//				// File var5 = new File(Environment
//				// .getExternalStorageDirectory()
//				// .getAbsolutePath()
//				// + "/snapshot/" + selectedDevice.UID);
//
//				Log.i("IOTCamera",
//						"getAllImages:11111>>>" + dir.getAbsolutePath());
//				if (!dir.exists()) {
//					Log.i("IOTCamera",
//							"getAllImages:222>>>" + dir.getAbsolutePath());
//					return;
//				}
//				{
//					getDeviceImages(dir, "", deviceInfo);
//				}
//
//			}
//			return;
//		}
		if (getHelper().isSDCardValid()) {
			// File var5 = new File(Environment
			// .getExternalStorageDirectory()
			// .getAbsolutePath()
			// + "/snapshot/" + selectedDevice.UID);

			if (!dir.exists()) {
				Log.i("IOTCamera",
						"getAllImages:222>>>" + dir.getAbsolutePath());
				return;
			}
//			if (deviceInfo != null) { //获取所有设备的相册
//				Log.i("IOTCamera", "getAllImages:11111 deviceInfo = null");
//				getDeviceImages(dir, deviceInfo.UID, deviceInfo);
//			} else 
				String[] list = dir.list();
				if (list == null) {
					Log.v("getDeviceImages", "getDeviceImages = null");
					return;
				}
				for (String file : list) { 
					ImageInfo fileInfo = new ImageInfo();
					fileInfo.name = file;
					fileInfo.path = dir.getAbsolutePath()+"/"+file;
					fileInfo.uri = "file://" + fileInfo.path;
					 File infoFile = new File(fileInfo.path );
					fileInfo.lastModifed = infoFile.lastModified();
					
					fileInfo.device = null;
					Log.v("getDeviceImages", "－－－－－getDeviceImages fileInfo.path =" + fileInfo.path+"   fileInfo.lastModifed:"+infoFile.lastModified());
					if(fileInfo.name.contains(""+ BuildConfig.FLAVOR+"_"))
					mImageInfos.add(fileInfo);
			}

		}
	}
	// 获取所有照片
		private void getAllSystemImages() {
			mImageInfos.clear();
			showSystemAlbum = false;
			Log.v("PhotoGridFragment", "PhotoGridFragment ShowDeviceIndex = "
					+ ShowDeviceIndex);
			File dir = null;
			 dir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/DCIM/Camera/");
			 
			if (getHelper().isSDCardValid()) { 
				Log.i("IOTCamera", "getAllImages:11111>>>" + dir.getAbsolutePath());
				if (!dir.exists()) {
					Log.i("IOTCamera",
							"getAllImages:222>>>" + dir.getAbsolutePath());
					return;
				}
				{
					String[] list = dir.list();
					for (String filestring : list) { 
						Log.i("IOTCamera", "getAllImages:11111>>>" + dir.getAbsolutePath()+"   filestring :"+filestring);
						getSystemImages(dir, filestring );
					}
				}

			}
		}

	private void getDeviceImages(File dir, String deviceUid,
			DeviceInfo deviceInfo) {
		File deviceDir = new File(dir.getAbsolutePath() + "/" + deviceUid);
		if (!deviceDir.exists()) {
			return;
		}
		File[] list = deviceDir.listFiles();
		if (list == null) {
			Log.v("getDeviceImages", "getDeviceImages = null");
			return;
		}

		for (File file : list) {
			ImageInfo fileInfo = new ImageInfo();
			fileInfo.name = file.getName();
			fileInfo.path = file.getAbsolutePath();
			fileInfo.uri = "file://" + fileInfo.path;
			fileInfo.lastModifed = file.lastModified();
			fileInfo.device = deviceInfo;
			Log.v("getDeviceImages", "getDeviceImages fileInfo.path =" + fileInfo.path+"   file.getName():"+file.getName()+"   fileInfo.lastModifed:"+fileInfo.lastModifed);

			mImageInfos.add(fileInfo);
			// mList.add(fileInfo.uri);
		}
		 
	}
	private void getSystemImages(File dir, String filename ) {
		File deviceDir = new File(dir.getAbsolutePath() + "/" + filename); 
		if (!deviceDir.exists()) {
			Log.v("getDeviceImages", "getDeviceImages !deviceDir.exists()"+deviceDir.getPath());
			return;
		}
		File[] list = deviceDir.listFiles();
		if (list == null) {
			Log.v("getDeviceImages", "getDeviceImages = null");
			ImageInfo fileInfo = new ImageInfo();
			fileInfo.name = deviceDir.getName();
			fileInfo.path = deviceDir.getAbsolutePath();
			fileInfo.uri = "file://" + fileInfo.path;
			 File infoFile = new File(fileInfo.path );
			fileInfo.lastModifed = infoFile.lastModified();
			fileInfo.device = null;
			Log.v("getDeviceImages", "getDeviceImages fileInfo.path =" + fileInfo.path+"   file.getName():"+deviceDir.getName()+"   fileInfo.lastModifed:"+fileInfo.lastModifed);
			mImageInfos.add(fileInfo);
			return;
		}

		for (File file : list) {
			if(file.getPath().toLowerCase().contains(".thumbnails"))
			continue;
			if (file.isDirectory()) {
				String[] listdir = file.list();
				for (String filestring : listdir) { 
					getSystemImages(file, filestring);//递归子文件夹
				}
				continue;
			}
			ImageInfo fileInfo = new ImageInfo();
			fileInfo.name = file.getName();
			fileInfo.path = file.getAbsolutePath();
			fileInfo.uri = "file://" + fileInfo.path;
			 File infoFile = new File(fileInfo.path );
			 fileInfo.lastModifed = infoFile.lastModified();
			fileInfo.device = null;
			Log.v("getDeviceImages", "getDeviceImages fileInfo.path =" + fileInfo.path);
			mImageInfos.add(fileInfo);
			// mList.add(fileInfo.uri);
		}
		 
	}

	private class ImageInfoComparator implements Comparator<ImageInfo> {
		@Override
		public int compare(ImageInfo lhs, ImageInfo rhs) {
			if (rhs == null) {
				return -1;
			}

			if (lhs == null) {
				return 1;
			}

			if ((rhs.lastModifed - lhs.lastModifed) < 0)
				return  -1;
			else if ((rhs.lastModifed - lhs.lastModifed) > 0)
				return  1;
			else
				return 0;
		}
	}

	private class ImageInfo {
		public String uri;
		public String name;
		public String path;
		public long lastModifed;
		public DeviceInfo device;

	}

	private class ImageAdapter extends BaseAdapter {

		private Context context;
		// private List<ImageInfo> imageInfos;
		protected ImageLoader imageLoader = ImageLoader.getInstance();
		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
		private DisplayImageOptions options;

		public ImageAdapter(Context context, List<ImageInfo> imageInfos) {
			this.context = context;
			// this.imageInfos = imageInfos;
			this.options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.camera_thumbnail)
					.showImageForEmptyUri(R.drawable.camera_thumbnail)
					.showImageOnFail(R.drawable.camera_thumbnail)
					.cacheInMemory().build();
		}

		class ViewHolder {

			public ImageView imageView;
			public ImageView selectView;
			public ImageView PhotoPlayButton;
		}

		@Override
		public int getCount() {
			Log.v("getDeviceImages",
					"getDeviceImages getCount .  mImageInfos.size()= "
							+ mImageInfos.size());
			return mImageInfos.size();
		}

		@Override
		public Object getItem(int position) {
			Log.v("getDeviceImages", "getDeviceImages getItem. imageInfo.uri. ");
			return mImageInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageInfo imageInfo = mImageInfos.get(position);
			Log.v("getDeviceImages",
					"getDeviceImages imageInfo.uri. imageInfo.uri. ="
							+ imageInfo.uri);
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.photo_grid_item, null);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.cuepointListItemThumbnail);
				holder.selectView = (ImageView) convertView
						.findViewById(R.id.del_hook);
				holder.PhotoPlayButton = (ImageView) convertView
						.findViewById(R.id.PhotoPlayButton);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (deletemImageInfos.contains(position))
				holder.selectView.setVisibility(View.VISIBLE);
			else
				holder.selectView.setVisibility(View.GONE);

			Log.v("getDeviceImages",
					"getDeviceImages imageInfo.uri. imageInfo.uri. ="
							+ imageInfo.uri);
			if (imageInfo.uri.toUpperCase().contains(".MP4")) {
				holder.PhotoPlayButton.setVisibility(View.VISIBLE);
				LoadImageAsyncTask task = new LoadImageAsyncTask();

				task.setImageView(holder.imageView); // iv为获取的ImageView对象实例
				task.execute(imageInfo.path); // 执行任务,参数与
												// doInBackground(String...
												// params) 方法参数一致
				//

			} else {
				holder.PhotoPlayButton.setVisibility(View.GONE);
				imageLoader = ImageLoader.getInstance();
				imageLoader.displayImage(imageInfo.uri, holder.imageView,
						options, animateFirstListener);
			}

			return convertView;
		}

	}

	/**
	 * 异步加载图片
	 * 
	 */
	public class LoadImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {
		private ImageView imageView;

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (null != imageView) {
				imageView.setImageBitmap(bitmap);
			}
		}

		// 设置图片视图实例
		public void setImageView(ImageView image) {
			this.imageView = image;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = getVideoThumbnail(params[0], 384, (int)(384*(9.0f/16.0f)),
					MediaStore.Images.Thumbnails.MINI_KIND);// ApacheUtility.GetBitmapByUrl(params[0]);
																// // 调用前面
																// ApacheUtility
																// 类的方法

			return bitmap;
		}

	}

	/**
	 * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	 * 
	 * @param videoPath
	 *            视频的路径
	 * @param width
	 *            指定输出视频缩略图的宽度
	 * @param height
	 *            指定输出视频缩略图的高度度
	 * @param kind
	 *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
	 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return 指定大小的视频缩略图
	 */
	public Bitmap getVideoThumbnail(String videoPath, int width, int height,
			int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		Log.v("videoPath", "videoPath =" + videoPath);
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		if (bitmap == null) {
			Log.e("videoPath", "videoPath = bitmap==null");
			Resources res = PhotoGridActivity.this.getResources();
			bitmap = BitmapFactory.decodeResource(res, R.drawable.usnap_bg);
		}
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
				} else {
					imageView.setImageBitmap(loadedImage);
				}
				displayedImages.add(imageUri);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

		ImageInfo imageInfo = mImageInfos.get(position);
		if (!isEditing) {
			//if (!imageInfo.uri.contains("_Cloud_")){
				Intent intent = new Intent(PhotoGridActivity.this,
						PhotoViewActivity.class);
				Log.i("images", "uri:" + imageInfo.path);
				if (imageInfo.uri.toUpperCase().contains(".MP4")) {
					intent.putExtra("uri", imageInfo.path);
					Bundle bundle = new Bundle();

					intent.putExtras(bundle);
					this.setResult(LiveViewGLviewActivity.PHOTOGRID_REQUESTCODE, intent);
					this.finish();
				}else {
					if(getResources().getConfiguration().orientation==1){
						Bundle bundle = new Bundle();
						intent.putExtra("uri", imageInfo.uri);
						intent.putExtras(bundle);
						this.setResult(LiveViewGLviewActivity.PHOTOGRID_REQUESTCODE, intent);
						this.finish();
					}else {
						this.startActivity(cn.ubia.util.AndroidFileUtil
								.openFile(imageInfo.uri.substring(7,
										imageInfo.uri.length())));
					}
				}



		/*	}else{
				String[] dates = imageInfo.name.split("_");
				StringBuilder yearSb = new StringBuilder(dates[2]);
				yearSb.insert(4, "/").insert(7, "/");
				StringBuilder timeSb = new StringBuilder(dates[3]);
				timeSb.insert(2, ":").insert(5,":");

				Intent mp4PlayIntent = new Intent(this, Mp4PlayActivity.class);
				mp4PlayIntent.putExtra("dev_uid", deviceInfo.UID);
				mp4PlayIntent.putExtra("path",imageInfo.path);
				mp4PlayIntent.putExtra("fileDate", new Date(yearSb.toString() + " " + timeSb.toString()));
				startActivity(mp4PlayIntent);
			}*/
//			else {
//				if (imageInfo.uri.toUpperCase().contains(".MP4")) {
//					Intent intent = new Intent(PhotoGridActivity.this,
//							PlayVideoActivity.class);
//					Log.i("images", "uri:" + imageInfo.uri);
//					intent.putExtra("uri", imageInfo.uri);
//					Bundle bundle = new Bundle();
//					bundle.putInt("position", position);
//					bundle.putString("item", imageInfo.path);
//					bundle.putString("nickName", deviceInfo.nickName);
//
//					bundle.putParcelableArrayList("list", (ArrayList) mList);
//					intent.putExtras(bundle);
//					startActivity(intent);
//				} else {
//					this.startActivity(cn.ubia.util.AndroidFileUtil
//							.openFile(imageInfo.uri.substring(7,
//									imageInfo.uri.length())));
//				}
//				Log.v("click", "PhotoGridFragment.isEditing =" + imageInfo.uri);
//			}
		} else {
			if (!deletemImageInfos.contains(position)) {

				Log.d("tag", "onItemClick VISIBLE position =" + position);
				deletemImageInfos.add(position);
				mAdapter.notifyDataSetChanged();
			} else {

				Log.d("tag", "onItemClick GONE position =" + position);

				removeposition(position);
				mAdapter.notifyDataSetChanged();
			}

		}
	}

	// private boolean isEditing = false;
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		if (!deletemImageInfos.contains(position)) {
			layoutDel.setVisibility(View.VISIBLE);
			isEditing = true;
			// Log.d("tag", "onItemLongClick VISIBLE position ="+position);
			// deletemImageInfos.add( position );
			// mAdapter.notifyDataSetChanged();
		} else {

			// Log.d("tag", "onItemLongClick GONE position ="+position);
			//
			// removeposition( position );
			// mAdapter.notifyDataSetChanged();
		}

		return false;
	}

	private void removeposition(Integer position) {
		int minfosize = deletemImageInfos.size();
		for (int i = 0; i < minfosize; i++) {
			if (position == deletemImageInfos.get(i)) {

				deletemImageInfos.remove(i);
				if (deletemImageInfos.size() == 0) {
					layoutDel.setVisibility(View.GONE);
					isEditing = false;
				}
				return;
			}
		}
	}

	private void removeImgaeinfos(String Uri) {
		int minfosize = mImageInfos.size();
		for (int i = 0; i < minfosize; i++) {
			if (Uri == mImageInfos.get(i).path) {
				mImageInfos.remove(i);

				return;
			}
		}
	}
	

	private   void syncAlbum(String imageUrl,File path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            final Uri contentUri = Uri.fromFile(path);
            scanIntent.setData(contentUri);
			LocalBroadcastManager.getInstance(this).sendBroadcast(scanIntent);
        } else {
            //4.4开始不允许发送"Intent.ACTION_MEDIA_MOUNTED"广播, 否则会出现: Permission Denial: not allowed to send broadcast android.intent.action.MEDIA_MOUNTED from pid=15410, uid=10135
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    } 
}
