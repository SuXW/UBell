package cn.ubia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;


import java.util.ArrayList;
import java.util.List;

import cn.ubia.UBell.R;
import cn.ubia.db.DatabaseManager;

public class PhotoThumbnailsActivity extends Activity {

   private OnItemClickListener gridOnItemClickListener = new OnItemClickListener() {
      public void onItemClick(AdapterView var1, View var2, int var3, long var4) {
         String var6 = (String)PhotoThumbnailsActivity.this.mPhotoSet.get(var3);
         System.gc();
         Intent var7 = new Intent(PhotoThumbnailsActivity.this.getApplicationContext(), PhotoViewerActivity.class);
         var7.putExtra("filename", var6);
         PhotoThumbnailsActivity.this.startActivity(var7);
      }
   };
   private GridView imageGrid;
   private String mDevUID;
   private List mPhotoSet = new ArrayList();


   private void quit() {
      this.setResult(-1, new Intent());
      this.finish();
   }

   public void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.mDevUID = this.getIntent().getExtras().getString("dev_uid");
      SQLiteDatabase var2 = (new DatabaseManager(this)).getReadableDatabase();
      Cursor var3 = var2.query("snapshot", new String[]{"_id", "dev_uid", "file_path", "time"}, "dev_uid = \'" + this.mDevUID + "\'", (String[])null, (String)null, (String)null, "_id");

      while(var3.moveToNext()) {
         String var4 = var3.getString(2);
         this.mPhotoSet.add(var4);
      }

      var3.close();
      var2.close();
      if(this.mPhotoSet.size() == 0) {
         this.setContentView(R.layout.no_photo);
      } else {
         this.setContentView(R.layout.photo_thumbnails);
         this.imageGrid = (GridView)this.findViewById(R.id.PhoneImageGrid);
         this.imageGrid.setAdapter(new ImageAdapter(this.getApplicationContext()));
         this.imageGrid.setOnItemClickListener(this.gridOnItemClickListener);
      }
   }

   public boolean onKeyDown(int var1, KeyEvent var2) {
      switch(var1) {
      case 4:
         this.quit();
      default:
         return super.onKeyDown(var1, var2);
      }
   }

   private class ImageAdapter extends BaseAdapter {

      private Context mContext;


      public ImageAdapter(Context var2) {
         this.mContext = var2;
      }

      public int getCount() {
         return PhotoThumbnailsActivity.this.mPhotoSet.size();
      }

      public Object getItem(int var1) {
         return Integer.valueOf(var1);
      }

      public long getItemId(int var1) {
         return (long)var1;
      }

      public View getView(int var1, View var2, ViewGroup var3) {
         System.gc();
         ImageView var4 = new ImageView(this.mContext.getApplicationContext());
         Options var5 = new Options();
         var5.inSampleSize = 2;
         if(var2 == null) {
            try {
               var4.setImageBitmap(BitmapFactory.decodeFile((String)PhotoThumbnailsActivity.this.mPhotoSet.get(var1), var5));
               var4.setScaleType(ScaleType.CENTER_CROP);
               var4.setLayoutParams(new LayoutParams(120, 90));
               return var4;
            } catch (Exception var7) {
               return var4;
            }
         } else {
            return (ImageView)var2;
         }
      }
   }
}
