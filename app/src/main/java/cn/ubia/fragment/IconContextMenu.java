package cn.ubia.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;
import java.util.ArrayList;

public class IconContextMenu implements OnCancelListener, OnDismissListener {

   private static final int Build_VERSION_CODES_ICE_CREAM_SANDWICH = 14;
   private static final int LIST_PREFERED_HEIGHT = 85;
   private IconContextMenuOnClickListener clickHandler = null;
   private IconMenuAdapter menuAdapter = null;
   private Activity parentActivity = null;


   public IconContextMenu(Activity var1, int var2) {
      this.parentActivity = var1;
      this.menuAdapter = new IconMenuAdapter(this.parentActivity);
   }

   private void cleanup() {}

   public void addItem(Resources var1, int var2, int var3, int var4) {
      this.menuAdapter.addItem(new IconContextMenuItem(var1, var2, var3, var4));
   }

   public void addItem(Resources var1, CharSequence var2, int var3, int var4) {
      this.menuAdapter.addItem(new IconContextMenuItem(var1, var2, var3, var4));
   }

   public void clearItems() {
      this.menuAdapter.clearItems();
   }

   public Dialog createMenu(String var1) {
      Builder var2 = new Builder(this.parentActivity);
      var2.setTitle(var1);
      var2.setAdapter(this.menuAdapter, new OnClickListener() {
         public void onClick(DialogInterface var1, int var2) {
            IconContextMenuItem var3 = (IconContextMenuItem)IconContextMenu.this.menuAdapter.getItem(var2);
            if(IconContextMenu.this.clickHandler != null) {
               IconContextMenu.this.clickHandler.onClick(var3.actionTag);
            }

         }
      });
      var2.setInverseBackgroundForced(true);
      AlertDialog var6 = var2.create();
      if(VERSION.SDK_INT >= 14) {
         var6.getContext().setTheme(16973939);
      }

      var6.setOnCancelListener(this);
      var6.setOnDismissListener(this);
      return var6;
   }

   public void onCancel(DialogInterface var1) {
      this.cleanup();
   }

   public void onDismiss(DialogInterface var1) {}

   public void setOnClickListener(IconContextMenuOnClickListener var1) {
      this.clickHandler = var1;
   }

   protected class IconMenuAdapter extends BaseAdapter {

      private Context context = null;
      private ArrayList mItems = new ArrayList();


      public IconMenuAdapter(Context var2) {
         this.context = var2;
      }

      private float toPixel(Resources var1, int var2) {
         return TypedValue.applyDimension(1, (float)var2, var1.getDisplayMetrics());
      }

      public void addItem(IconContextMenuItem var1) {
         this.mItems.add(var1);
      }

      public void clearItems() {
         this.mItems.clear();
      }

      public int getCount() {
         return this.mItems.size();
      }

      public Object getItem(int var1) {
         return this.mItems.get(var1);
      }

      public long getItemId(int var1) {
         return (long)((IconContextMenuItem)this.getItem(var1)).actionTag;
      }

      public View getView(int var1, View var2, ViewGroup var3) {
         IconContextMenuItem var4 = (IconContextMenuItem)this.getItem(var1);
         Resources var5 = IconContextMenu.this.parentActivity.getResources();
         if(var2 == null) {
            TextView var6 = new TextView(this.context);
            var6.setLayoutParams(new LayoutParams(-1, -2));
            var6.setPadding((int)this.toPixel(var5, 15), 0, (int)this.toPixel(var5, 15), 0);
            var6.setGravity(16);
            Theme var7 = this.context.getTheme();
            TypedValue var8 = new TypedValue();
            if(var7.resolveAttribute(16842819, var8, true)) {
               var6.setTextAppearance(this.context, var8.resourceId);
            }

            var6.setMinHeight(85);
            var6.setCompoundDrawablePadding((int)this.toPixel(var5, 20));
            var2 = var6;
         }

         TextView var9 = (TextView)var2;
         var9.setTextColor(0xff000000);
         var9.setTag(var4);
         var9.setText(var4.text);
         var9.setCompoundDrawablesWithIntrinsicBounds(var4.image, (Drawable)null, (Drawable)null, (Drawable)null);
         return var9;
      }
   }

   protected class IconContextMenuItem {

      public final int actionTag;
      public final Drawable image;
      public final CharSequence text;


      public IconContextMenuItem(Resources var2, int var3, int var4, int var5) {
         this.text = var2.getString(var3);
         if(var4 != -1) {
            this.image = var2.getDrawable(var4);
         } else {
            this.image = null;
         }

         this.actionTag = var5;
      }

      public IconContextMenuItem(Resources var2, CharSequence var3, int var4, int var5) {
         this.text = var3;
         if(var4 != -1) {
            this.image = var2.getDrawable(var4);
         } else {
            this.image = null;
         }

         this.actionTag = var5;
      }
   }

   public interface IconContextMenuOnClickListener {

      void onClick(int var1);
   }
}
