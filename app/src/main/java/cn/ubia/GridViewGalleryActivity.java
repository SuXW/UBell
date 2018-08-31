package cn.ubia;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;

public class GridViewGalleryActivity extends BaseActivity {
//查看缩略图
	private static final int DEFAULT_LIST_SIZE = 20;
	final List IMAGE_FILES = new ArrayList(20);
	private GridView gridview;
	private ImageAdapter imageAdapter;
	private String imagesPath;
	private String mFileName;
	ArrayList<String> mList = new ArrayList<String>();  
	public void onCreate(Bundle var1) {
		super.onCreate(var1);
		System.gc();
		this.imagesPath = this.getIntent().getExtras().getString("images_path");
		this.setContentView(R.layout.gridviewgalleryactivity);
		this.setImagesPath(this.imagesPath);
		this.removeCorruptImage();
		this.imageAdapter = new ImageAdapter(this);
		this.gridview = (GridView) this.findViewById(R.id.gridview);
		this.gridview.setAdapter(this.imageAdapter);
		this.gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView var1, View var2, int var3,
					long var4) {
				Intent var6 = new Intent(GridViewGalleryActivity.this,
						PhotoViewerActivity.class);
				Bundle bundle=new Bundle(); 
		        bundle.putParcelableArrayList("list", (ArrayList) mList);
		      	bundle.putInt("position", var3);
		      	bundle.putString("filename", (String) GridViewGalleryActivity.this.IMAGE_FILES
		      											.get(var3));
		        var6.putExtras(bundle);
		  
//				var6.putExtra("filename",
//						(String) GridViewGalleryActivity.this.IMAGE_FILES
//								.get(var3));
				GridViewGalleryActivity.this.startActivity(var6);
			}
		});
		this.gridview.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView var1, View var2,
					final int var3, long var4) {
				OnClickListener var6 = new OnClickListener() {
					public void onClick(DialogInterface var1, int var2) {
						switch (var2) {
						case -1:
							GridViewGalleryActivity.this
									.runOnUiThread(new Runnable() {
										public void run() {
											GridViewGalleryActivity.this.imageAdapter
													.deleteImageAtPosition(var3);
										}
									});
							return;
						default:
						}
					}
				};
				(new Builder(GridViewGalleryActivity.this))
						.setMessage(
								GridViewGalleryActivity.this
										.getResources()
										.getString(
												R.string.page22_dlgAreYouSureToDeleteThisSnapshot))
						.setPositiveButton(
								GridViewGalleryActivity.this.getResources()
										.getString(
												R.string.page22_dlgDeleteSnapshotYes),
								var6)
						.setNegativeButton(
								GridViewGalleryActivity.this
										.getResources()
										.getString(R.string.page22_dlgDeleteSnapshotNo),
								var6).show();
				return false;
			}
		});
	}

	public final void removeCorruptImage() {
		Iterator var1 = this.IMAGE_FILES.iterator();

		while (var1.hasNext()) {
			if (BitmapFactory.decodeFile((String) var1.next()) == null) {
				var1.remove();
			}
		}

	}

	public final void setImagesPath(String path) {
		this.IMAGE_FILES.clear();
		String[] files = new File(path).list();
		for (String fileName : files) {
			this.IMAGE_FILES.add(path + "/" + fileName);
			mList.add(path + "/" + fileName);
		}
		Collections.reverse(this.IMAGE_FILES);
		Collections.reverse(mList);
		// $FF: Couldn't be decompiled
	}

	public class ImageAdapter extends BaseAdapter {

		private Context mContext;

		public ImageAdapter(Context var2) {
			this.mContext = var2;
		}

		public final boolean deleteImageAtPosition(int var1) {
			boolean var2 = (new File(
					(String) GridViewGalleryActivity.this.IMAGE_FILES.get(var1)))
					.delete();
			GridViewGalleryActivity.this.IMAGE_FILES.remove(var1);
			this.notifyDataSetChanged();
			return var2;
		}

		public int getCount() {
			return GridViewGalleryActivity.this.IMAGE_FILES.size();
		}

		public Object getItem(int var1) {
			return null;
		}

		public long getItemId(int var1) {
			return 0L;
		}

		public View getView(int var1, View var2, ViewGroup var3) {
			ImageView var4;
			if (var2 == null) {
				var4 = new ImageView(this.mContext);
				var4.setLayoutParams(new LayoutParams(-1, 160));
				var4.setScaleType(ScaleType.CENTER_INSIDE);
				var4.setPadding(8, 8, 8, 8);
			} else {
				var4 = (ImageView) var2;
			}

			Options var5 = new Options();
			var5.inSampleSize = 4;
			Bitmap var6 = BitmapFactory
					.decodeFile(
							(String) GridViewGalleryActivity.this.IMAGE_FILES
									.get(var1), var5);
			if (var6 == null) {
				for (int var7 = -1 + this.getCount(); var7 >= 0; --var7) {
					var6 = BitmapFactory.decodeFile(
							(String) GridViewGalleryActivity.this.IMAGE_FILES
									.get(var7), var5);
					if (var6 != null) {
						break;
					}
				}
			}

			var4.setImageBitmap(var6);
			return var4;
		}
	}
}
