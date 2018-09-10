package cn.ubia.customprovider;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.DeleteMultipleObjectRequest;
import com.alibaba.sdk.android.oss.model.DeleteMultipleObjectResult;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.DeleteObjectResult;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.ubia.UBell.BuildConfig;
import cn.ubia.UBell.R;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.FileInfo;
import cn.ubia.widget.CloudVideoAdapter;

/**
 * Created by Steven.lin on 2018/4/19.
 */

public class OssService {
    public static final int GETVIDEOLISTSUCCESS = 0x00001;
    public static final int GETVIDEOLISTERROR = 0x00002;
    public static final int DOWNLOADVIDEOLISTSUCCESS = 0x00003;
    public static final int DOWNLOADVIDEOLISTERROR = 0x00004;
    public static final int GETIMGSUCCESS = 0x00005;
    public static final int GETIMGERROR = 0x00006;
    public static final int DOWNLOADING = 0x00007;
    public static final int DELETESUCCESS = 0x00008;
    public static final int DELETEFAIL = 0x00009;
    public int maxKeysLimit = 1000;

    public OSS mOss;
    private String mBucket;
    private Handler aliHandler,downloadHandler;
    List<FileInfo> cloudFileList = new ArrayList<>();

    public OssService(OSS oss, String bucket) {
        this.mOss = oss;
        this.mBucket = bucket;
    }

    public void setBucketName(String bucket) {
        this.mBucket = bucket;
    }

    public void initOss(OSS _oss) {
        this.mOss = _oss;
    }

    public void setHandler(Handler aliHandler) {
        this.aliHandler = aliHandler;
    }

    public void setDownloadHandler(Handler downloadHandler) {
        this.downloadHandler = downloadHandler;
    }

    public void asyncListObjectsWithBucketName(final String uid, final String type, final String date) {
        ListObjectsRequest listObjects = new ListObjectsRequest(mBucket);
        listObjects.setDelimiter("");
        listObjects.setPrefix(uid + "/" + type + "/" + date);
        listObjects.setMaxKeys(maxKeysLimit);
        cloudFileList.clear();

        OSSAsyncTask task = mOss.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
            @Override
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {

                for (int i = 0; i < result.getObjectSummaries().size(); i++) {
                    FileInfo fi = new FileInfo();
                    fi.setFileName(result.getObjectSummaries().get(i).getKey().substring(result.getObjectSummaries().get(i).getKey().lastIndexOf("/") + 1, result.getObjectSummaries().get(i).getKey().length()));
                    String fileName = fi.getFileName().substring(0, fi.getFileName().lastIndexOf("."));
                    String[] dates = fileName.split("_");
                    StringBuilder yearSb = new StringBuilder(dates[0]);
                    yearSb.insert(4, "/").insert(7, "/");
                    StringBuilder timeSb = new StringBuilder(dates[1]);
                    timeSb.insert(2, ":").insert(5,":");
                    if(dates.length>3)
                    fi.setFileTimeLength(Integer.parseInt(dates[3]));
                    fi.setRecordTime(new Date(yearSb.toString() + " " + timeSb.toString()));
                    fi.setDisplayName((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(fi.getRecordTime()));
                    //fi.setDisplayName((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date(result.getObjectSummaries().get(i).getLastModified().toString())));
                    fi.setFileCloudPath(result.getObjectSummaries().get(i).getKey());
                    fi.setFileImgCloudPath(fi.getFileCloudPath().replace(type, "jpg").replace(".mp4", ".jpg"));
                    fi.setFileType(type);
                    fi.setFileSize(result.getObjectSummaries().get(i).getSize());
                    fi.setFileTriggerType(("" + fi.getFileName().charAt(fi.getFileName().lastIndexOf(".") - 1)).toUpperCase());

                    cloudFileList.add(fi);
                }
                Collections.sort(cloudFileList, new Comparator<FileInfo>() {
                    @Override
                    public int compare(FileInfo o1, FileInfo o2) {
                        String str1= o1.getDisplayName();
                        String str2= o2.getDisplayName();
                        if (str1.compareToIgnoreCase(str2)>0){
                            return -1;
                        }
                        return 1;
                    }
                });
                List<FileInfo> fileList = new ArrayList<>();
                int count = cloudFileList.size() >20 ? 19 : cloudFileList.size()-1; //取最新的20条显示
                 for(int i = 0;i<=count;i ++){
                    fileList.add(cloudFileList.get(i));
                    asyncGetImg(cloudFileList.get(i));
                }
                Message msg = new Message();
                msg.what = GETVIDEOLISTSUCCESS;
                msg.obj = fileList;
                if (aliHandler != null)
                    aliHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (clientExcepion != null) {
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    OSSLog.logError("ErrorCode", serviceException.getErrorCode());
                    OSSLog.logError("RequestId", serviceException.getRequestId());
                    OSSLog.logError("HostId", serviceException.getHostId());
                    OSSLog.logError("RawMessage", serviceException.getRawMessage());
                }
                if (aliHandler != null) {
                    aliHandler.sendEmptyMessage(GETVIDEOLISTERROR);
                }
            }
        });
    }

    public synchronized void asyncGetImg(final FileInfo fi) {
        if ((fi == null)) {
            Log.w("asyncGetImg", "file--> Null");
            return;
        }
        GetObjectRequest get = new GetObjectRequest(mBucket, fi.getFileImgCloudPath());
        get.setCRC64(OSSRequest.CRC64Config.YES);
        get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
            @Override
            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {

            }
        });
        OSSAsyncTask task = mOss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                InputStream inputStream = result.getObjectContent();
                try {
                    byte[] data;
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    outStream.close();
                    data = outStream.toByteArray();
                    fi.setThumbnailImg(BitmapFactory.decodeByteArray(data, 0, data.length));
                    inputStream.close();
                    if (aliHandler != null) {
                        Message msg = new Message();
                        msg.obj = fi;
                        msg.what = GETIMGSUCCESS;
                        aliHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (aliHandler != null) {
                        aliHandler.sendEmptyMessage(GETIMGERROR);
                    }
                }

            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (clientExcepion != null) {
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    Log.d("ErrorCode", serviceException.getErrorCode());
                    Log.d("RequestId", serviceException.getRequestId());
                    Log.d("HostId", serviceException.getHostId());
                    Log.d("RawMessage", serviceException.getRawMessage());
                }
                if (aliHandler != null) {
                    aliHandler.sendEmptyMessage(GETIMGERROR);
                }
            }
        });

    }


    public synchronized void deleteVideo(final String  fileName){
        DeleteObjectRequest delete = new DeleteObjectRequest(mBucket,fileName);

        mOss.asyncDeleteObject(delete, new OSSCompletedCallback<DeleteObjectRequest, DeleteObjectResult>() {
            @Override
            public void onSuccess(DeleteObjectRequest request, DeleteObjectResult result) {
                Log.d("asyncCopyAndDelObject", "delete success!");
                Message msg = new Message();
                msg.what = DELETESUCCESS;
                aliHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(DeleteObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }

                Message msg = new Message();
                msg.what = DELETEFAIL;
                aliHandler.sendMessage(msg);
            }

        });
    }

    public synchronized void dowloadVideo(final FileInfo fi, final TextView downloadTv,final ImageView downloadImg) {

        GetObjectRequest get = new GetObjectRequest(mBucket, fi.getFileCloudPath());
        get.setCRC64(OSSRequest.CRC64Config.YES);
        get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
            @Override
            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {

                Message msg = new Message();

                View[] views = new View[2];
                views[0] = downloadTv;
                views[1] = downloadImg;

                msg.obj = views;


                msg.what = DOWNLOADING;
                downloadHandler.sendMessage(msg);
            }
        });
        mOss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                if (Environment.getExternalStorageState().equals("mounted")) {
                    File  file = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/DCIM/Camera/");

                  if (!file.exists()) {
                        try {
                            file.mkdirs();
                        } catch (SecurityException var32) {
                            var32.printStackTrace();
                        }
                    }
                    InputStream inputStream = result.getObjectContent();
                    try {
                        byte[] buffer = new byte[2048];
                        FileOutputStream fos = new FileOutputStream(file.getPath() + "/"+ BuildConfig.FLAVOR+"_Cloud_"+fi.getFileName());
                        Log.d("Oss","下载到 ."+file.getPath() + "/UBell_Cloud_" + fi.getFileName());
                        int len;
                        while ((len = inputStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.flush();
                        inputStream.close();
                        fos.close();
                        if (downloadTv != null) {
                            //下载成功
                            View[] views = new View[2];
                            views[0] = downloadTv;
                            views[1] = downloadImg;
                            Log.d("Oss","下载成功.");
                            Message msg = new Message();
                            msg.obj = views;

                            Bundle bundle = new Bundle();
                            bundle.putString("fileName",fi.getFileName());
                            msg.setData(bundle);

                            msg.what = DOWNLOADVIDEOLISTSUCCESS;
                            downloadHandler.sendMessage(msg);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (fi != null) {
                            Log.d("Oss","下载失败.");
                            View[] views = new View[2];
                            views[0] = downloadTv;
                            views[1] = downloadImg;
                            Message msg = new Message();
                            msg.obj = views;
                            msg.what = DOWNLOADVIDEOLISTERROR;
                            downloadHandler.sendMessage(msg);
                        }
                    }
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (clientExcepion != null) {
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    Log.d("ErrorCode", serviceException.getErrorCode());
                    Log.d("RequestId", serviceException.getRequestId());
                    Log.d("HostId", serviceException.getHostId());
                    Log.d("RawMessage", serviceException.getRawMessage());
                }
                if (downloadTv != null) {
                    //下载失败
                    Log.d("Oss","下载失败.");
                    Message msg = new Message();
                    View[] views = new View[2];
                    views[0] = downloadTv;
                    views[1] = downloadImg;
                    msg.obj = views;
                    msg.what = DOWNLOADVIDEOLISTERROR;
                    downloadHandler.sendMessage(msg);
                }
            }
        });
    }

    public synchronized void deleteAllVideo(final String uid){
        ListObjectsRequest listObjects = new ListObjectsRequest(mBucket);
        listObjects.setPrefix(uid);
        listObjects.setMaxKeys(maxKeysLimit);
        // 设置成功、失败回调，发送异步罗列请求
        mOss.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
            @Override
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                Log.d("AyncListObjects", "Success!");
                List<String> keyList = new ArrayList<>();
                for (int i = 0; i < result.getObjectSummaries().size(); i++) {
                    Log.d("AyncListObjects", "object: " + result.getObjectSummaries().get(i).getKey() + " "
                            + result.getObjectSummaries().get(i).getETag() + " "
                            + result.getObjectSummaries().get(i).getLastModified());

                    keyList.add(result.getObjectSummaries().get(i).getKey());


                }

                if(keyList.size()>0){
                    multipleDeleteVideo(keyList,uid);
                }

            }

            @Override
            public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });

    }


    public synchronized void deleteDayVideo(){
        List<String>  keyList =  new ArrayList<>();
        for(FileInfo fileInfo:cloudFileList){
            keyList.add(fileInfo.getFileImgCloudPath());
            keyList.add(fileInfo.getFileCloudPath());
        }
        multipleDeleteVideo(keyList,"");
    }

    public synchronized void multipleDeleteVideo(List<String> keyList,final String uid){


            DeleteMultipleObjectRequest deleteMultipleObjectRequest = new DeleteMultipleObjectRequest(mBucket,keyList,false);

            mOss.asyncDeleteMultipleObject(deleteMultipleObjectRequest, new OSSCompletedCallback<DeleteMultipleObjectRequest, DeleteMultipleObjectResult>(){
                @Override
                public void onSuccess(DeleteMultipleObjectRequest request, DeleteMultipleObjectResult result) {

                    List<String> deletedObjects = result.getDeletedObjects();
                    for (String object : deletedObjects) {
                        Log.d("multipleDeleteVideo", "delete:"+object);
                    }
                    if(deletedObjects.size()<maxKeysLimit){
                    Message msg = new Message();
                    msg.what = DELETESUCCESS;
                    aliHandler.sendMessage(msg);
                    }else{
                        deleteAllVideo(uid);
                    }
                }
                @Override
                public void onFailure(DeleteMultipleObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                    // 请求异常
                    if (clientExcepion != null) {
                        // 本地异常如网络异常等
                        clientExcepion.printStackTrace();
                    }
                    if (serviceException != null) {
                        // 服务异常
                        Log.e("ErrorCode", serviceException.getErrorCode());
                        Log.e("RequestId", serviceException.getRequestId());
                        Log.e("HostId", serviceException.getHostId());
                        Log.e("RawMessage", serviceException.getRawMessage());
                    }

                    Message msg = new Message();
                    msg.what = DELETEFAIL;
                    aliHandler.sendMessage(msg);
                }
            });






    }

    public synchronized void asyncGetVideo(final FileInfo fi, final DeviceInfo mDev) {
        final long get_start = System.currentTimeMillis();
        if ((fi == null)) {
            Log.w("AsyncGetImage", "file--> Null");
            return;
        }
        GetObjectRequest get = new GetObjectRequest(mBucket, fi.getFileCloudPath());
        get.setCRC64(OSSRequest.CRC64Config.YES);
        get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
            @Override
            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
                fi.setDownLoadState(2);
            }
        });
        OSSAsyncTask task = mOss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                if (Environment.getExternalStorageState().equals("mounted")) {

                    File  file = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/ubia/cloudVideo/" + mDev.UID + "/");

                  if (!file.exists()) {
                        try {
                            file.mkdirs();
                        } catch (SecurityException var32) {
                            if (aliHandler != null) {
                                aliHandler.sendEmptyMessage(DOWNLOADVIDEOLISTERROR);
                            }
                        }
                    }
                    InputStream inputStream = result.getObjectContent();
                    try {
                        byte[] buffer = new byte[2048];
                        FileOutputStream fos = new FileOutputStream(file.getPath() + "/" + fi.getFileName());
                        int len;
                        while ((len = inputStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.flush();
                        inputStream.close();
                        fos.close();
                        if (aliHandler != null) {
                            Message msg = new Message();
                            msg.what = DOWNLOADVIDEOLISTSUCCESS;
                            msg.obj = fi;
                            aliHandler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (aliHandler != null) {
                            fi.setDownLoadState(0);
                            aliHandler.sendEmptyMessage(DOWNLOADVIDEOLISTERROR);
                        }
                    }
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (clientExcepion != null) {
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    Log.d("ErrorCode", serviceException.getErrorCode());
                    Log.d("RequestId", serviceException.getRequestId());
                    Log.d("HostId", serviceException.getHostId());
                    Log.d("RawMessage", serviceException.getRawMessage());
                }
                if (aliHandler != null) {
                    fi.setDownLoadState(0);
                    aliHandler.sendEmptyMessage(DOWNLOADVIDEOLISTERROR);
                }
            }
        });

    }
}
