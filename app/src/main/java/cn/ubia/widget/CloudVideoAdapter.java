package cn.ubia.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.ubia.CloudSaveVideoListActivity;
import cn.ubia.UBell.BuildConfig;
import cn.ubia.UBell.R;
import cn.ubia.bean.FileInfo;
import cn.ubia.customprovider.OssService;

/**
 * Created by Steven.lin on 2018/4/20.
 */

public class CloudVideoAdapter extends BaseAdapter {
    private Context mContext;
    private List<FileInfo> cloudFileList = new ArrayList<>();
    private OssService ossService;

    public CloudVideoAdapter(Context mContext, OssService ossService) {
        this.ossService = ossService;
        this.mContext = mContext;
        this.ossService.setDownloadHandler(handler);
    }

    public void setData(List<FileInfo> cloudFileList) {



        this.cloudFileList.clear();
        this.cloudFileList.addAll(cloudFileList);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cloudFileList.size();
    }

    @Override
    public Object getItem(int i) {
        return cloudFileList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = View.inflate(mContext,
                    R.layout.item_cloud_video_file, null);
            mViewHolder.thumbnail_img = (ImageView) convertView.findViewById(R.id.thumbnail_img);
            mViewHolder.fileName_txt = (TextView) convertView.findViewById(R.id.fileName_txt);
            mViewHolder.time_txt = (TextView) convertView.findViewById(R.id.time_txt);
            mViewHolder.download_txt = (TextView) convertView.findViewById(R.id.download_txt);
            mViewHolder.delete_img = (ImageView) convertView.findViewById(R.id.delete_img);
            mViewHolder.download_img = (ImageView) convertView.findViewById(R.id.download_img);
            convertView.setTag(mViewHolder);
        } else
             mViewHolder = (ViewHolder) convertView.getTag();
             final FileInfo mFileInfo = cloudFileList.get(position);

        if(isExist(mFileInfo.getFileName())){
            mViewHolder.download_txt.setTextColor(mContext.getResources().getColor(R.color.color_info));
            mViewHolder.download_txt.setBackgroundResource(R.color.white);
            mViewHolder.download_txt.setText(mContext.getString(R.string.download_success));
            mViewHolder.download_img.setOnClickListener(null);
            mViewHolder.download_img.setVisibility(View.GONE);
            mViewHolder.download_txt.setVisibility(View.VISIBLE);
        }else{
            mViewHolder.download_txt.setTextColor(mContext.getResources().getColor(R.color.white));
            mViewHolder.download_txt.setBackgroundResource(R.color.color_skin_color);
            mViewHolder.download_txt.setText(mContext.getString(R.string.download));
            mViewHolder.download_img.setOnClickListener(new DownloadOnClickListener(mFileInfo, mViewHolder.download_txt));
            mViewHolder.download_img.setVisibility(View.VISIBLE);
            mViewHolder.download_txt.setVisibility(View.GONE);
        }
        mViewHolder.delete_img.setOnClickListener(new DeleteOnClickListener(mFileInfo));

        mViewHolder.fileName_txt.setText(mFileInfo.getDisplayName());
        String typeStr = "unknown";
        if (mFileInfo.getFileTriggerType().equals("P")) {
            typeStr = mContext.getString(R.string.pri_tip);
        } else if (mFileInfo.getFileTriggerType().equals("R")) {
            typeStr = mContext.getString(R.string.bell_tip);
        } else if (mFileInfo.getFileTriggerType().equals("U")) {
            typeStr = mContext.getString(R.string.put_all_tip);
        }
        if (mFileInfo.getThumbnailImg() != null && mFileInfo.getDownLoadState() != 1) {
            mViewHolder.thumbnail_img.setImageBitmap(mFileInfo.getThumbnailImg());
            float mSize = (mFileInfo.getFileSize() / 1048576.0f);
            mViewHolder.time_txt.setText(typeStr + " " + mFileInfo.getFileTimeLength() + ""+mContext.getString(R.string.seconds_tip));
        } else if (mFileInfo.getDownLoadState() == 0) {
            mViewHolder.thumbnail_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.home_bg01));
            float mSize = (mFileInfo.getFileSize() / 1048576.0f);
            mViewHolder.time_txt.setText(typeStr + " " +mFileInfo.getFileTimeLength()  +mContext.getString(R.string.seconds_tip));
        } else if (mFileInfo.getDownLoadState() == 1) {
            if (mFileInfo.getThumbnailImg() != null)
                mViewHolder.thumbnail_img.setImageBitmap(mFileInfo.getThumbnailImg());
            mViewHolder.time_txt.setText(typeStr + " " + mFileInfo.getFileTimeLength() + mContext.getString(R.string.seconds_tip));
        }



        return convertView;
    }


    private void delete(final FileInfo mFileInfo){

        DialogUtil.getInstance().showDelDialog(mContext,mContext.getString( R.string.page26_ctxRemoveCamera)+"", mContext.getString( R.string.delete_file_tip)+"",
                new DialogUtil.Dialogcallback(){

                    @Override
                    public void cancel() {
                    }

                    @Override
                    public void commit(String str) {
                    }

                    @Override
                    public void commit() {
                        ossService.deleteVideo(mFileInfo.getFileCloudPath());
                    }

                });


    }

    private void download(FileInfo mFileInfo,TextView v,ImageView downloadIv){

        Log.d("oss","download.。。.");

        ossService.dowloadVideo(mFileInfo, v, downloadIv);
    }

    class DeleteOnClickListener implements View.OnClickListener{

        FileInfo mFileInfo = null;
        public DeleteOnClickListener(FileInfo mFileInfo){
            this.mFileInfo = mFileInfo;
        }

        public void onClick(View v){
            delete(mFileInfo);
        }
    }

    class DownloadOnClickListener implements View.OnClickListener{
         FileInfo mFileInfo = null;

         private TextView downloadTv;

         public DownloadOnClickListener(FileInfo mFileInfo,TextView downloadIv){
            this.mFileInfo = mFileInfo;
            this.downloadTv = downloadIv;
         }

        public void onClick(View v){
             ImageView iv = (ImageView)v;
             download(mFileInfo,downloadTv,iv);
        }
    }

    private boolean isExist(String fileName){

        File[] files = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/DCIM/Camera/").listFiles();

         for(int i = 0; i < files.length; i ++){
                int nameIndex = files[i].getName().indexOf("Cloud_");
                if(nameIndex!=-1) {

                    if (fileName.equals(files[i].getName().substring(
                            //nameIndex,
                            files[i].getName().indexOf("_",files[i].getName().indexOf("_")+1 )+1,
                            files[i].getName().length()))) {
                        return true;
                    }
                }
         }
         return false;
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            View[] views = (View[]) msg.obj;

            TextView tv = (TextView) views[0];
            ImageView iv = (ImageView) views[1];

            iv.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            switch (msg.what) {
                case OssService.DOWNLOADVIDEOLISTSUCCESS:

                   tv.setTextColor(mContext.getResources().getColor(R.color.color_info));
                   tv.setBackgroundResource(R.color.white);
                   tv.setOnClickListener(null);
                   tv.setText(mContext.getString(R.string.download_success));

                   String fileName = msg.getData().getString("fileName");
                   CloudSaveVideoListActivity activity = (CloudSaveVideoListActivity)mContext;
                   activity.getHelper().syncAlbum(mContext,fileName);
                   // syncAlbum(mContext,fileName);

                    break;
                case OssService.DOWNLOADVIDEOLISTERROR:
                    tv.setTextColor(mContext.getResources().getColor(R.color.color_info));
                    tv.setBackgroundResource(R.color.white);
                    tv.setText(mContext.getString(R.string.download_fail));
                    break;
                case OssService.DOWNLOADING:
                    tv.setTextColor(mContext.getResources().getColor(R.color.color_info));
                    tv.setBackgroundResource(R.color.white);
                    tv.setText(mContext.getString(R.string.download_ing));
                    break;

            }

        }

    };


    class ViewHolder {
        ImageView thumbnail_img;
        ImageView download_img;
        TextView fileName_txt;
        TextView time_txt;
        TextView download_txt;
        TextView delete_txt;
        ImageView delete_img;
    }
}
