package cn.ubia;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.ubia.IOTC.AVIOCTRLDEFs;
import com.ubia.IOTC.Camera;
import com.ubia.IOTC.Packet;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


import cn.ubia.UBell.R;
import cn.ubia.base.BaseActivity;
import cn.ubia.bean.DeviceInfo;
import cn.ubia.bean.FileInfo;
import cn.ubia.bean.MyCamera;
import cn.ubia.customprovider.OssService;
import cn.ubia.fragment.MainCameraFragment;
import cn.ubia.manager.CameraManagerment;
import cn.ubia.util.StringUtils;
import cn.ubia.widget.CloudVideoAdapter;
import cn.ubia.widget.DialogUtil;
import java.util.Collections;
import cn.ubia.UBell.BuildConfig;
import cn.ubia.widget.MyProgressBar;

/**
 * Created by Steven.lin on 2018/4/19.
 */
public class CloudSaveVideoListActivity extends BaseActivity implements View.OnClickListener, DialogUtil.DialogChooseCloudSaveDateCallback, AdapterView.OnItemClickListener {

    private ImageView back, right_image,right_image2;

    private String mDevUID;
    private DeviceInfo mDevice = null;
    private OssService ossService;
    private int mYear;
    private int mMonth;
    private int mDay;
    private List<FileInfo> cloudFileList = new ArrayList<>();
    private List<FileInfo> locatFileList = new ArrayList<>();
    private CloudVideoAdapter mCloudVideoAdapter;
    private String date;
    private MyCamera mCamera;
    private TextView video_tip_txt;
    private ListView video_list;
    private ProgressBar mProgressBar;

    private boolean isGetLocatViedoSuccess = false;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.cloud_save_video_list);
        Bundle var2 = this.getIntent().getExtras();
        this.mDevUID = var2.getString("dev_uid");
        this.mDevice = MainCameraFragment.getexistDevice(mDevUID);
        this.mCamera =  CameraManagerment.getInstance().getexistCamera(mDevUID);

        if(mCamera!=null) this.mCamera.registerIOTCListener(this);

        initView();

        if(mDevice.firmwareVersion == null){
            CameraManagerment.getInstance().userIPCGetAdvanceSetting(mDevUID);
        }else {
            selectCloud();
        }


    }

   /* public OssService initOSS(String endpoint, String bucket) {

        OSSPlainTextAKSKCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(BuildConfig.OSSAK, BuildConfig.OSSSK);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider, conf);

        OSSLog.enableLog();
        return new OssService(oss, bucket);
    }*/

    private void initView() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.delete_file_wait));


        back = (ImageView) findViewById(R.id.back);
        back.setBackgroundResource(R.drawable.ic_action_left);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        right_image = (ImageView) findViewById(R.id.right_image);
        right_image.setImageDrawable(getResources().getDrawable(R.drawable.live_btn_dat_screen));
        right_image.setVisibility(View.VISIBLE);
        right_image.setOnClickListener(this);
        right_image2= (ImageView) findViewById(R.id.right_image2);
        right_image2.setImageDrawable(getResources().getDrawable(R.drawable.file_delete));
        right_image2.setVisibility(View.VISIBLE);
        right_image2.setOnClickListener(this);
        ((TextView) findViewById(R.id.title)).setText(getString(R.string.cloud_save_tip));
        video_tip_txt = (TextView) findViewById(R.id.video_tip_txt);
        video_list = (ListView) findViewById(R.id.video_list);


        mProgressBar = (ProgressBar) this.findViewById(R.id.MyprogressBar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.right_image:
                showChooseDateDialog();
                break;
            case R.id.right_image2:
                showDeleteDialog();
                break;
        }
    }


    private void showDeleteDialog(){
        DialogUtil.getInstance().showDeleteCloudDialog(this, new DialogUtil.DialogChooseItemcallback() {
            @Override
            public void chooseItem(int chooseItem) {
                switch (chooseItem){
                    case 0:
                      Log.e("guo...","删除所有文件");
                        ossService.deleteAllVideo(mDevUID);
                        progressDialog.show();
                    break;
                    case 1:
                        ossService.deleteDayVideo();
                        progressDialog.show();

                    break;
                }
            }
        });
    }

    private void selectCloud(){

        Log.e("oss","firmwareVersion="+mDevice.firmwareVersion);

        if (getHelper().getVer(mDevice.firmwareVersion) <= 1.20) {

            ossService = getHelper().initOSS(UbiaApplication.endpoint, BuildConfig.OSSBUCKET);

        } else {

            Log.e("oss","country="+StringUtils.getCurrentLocaltionISOCountryCodeString(mDevice.country));
            if (StringUtils.getCurrentLocaltionISOCountryCodeString(mDevice.country).equals("CN")) {
                ossService = getHelper().initOSS(UbiaApplication.endpoint, BuildConfig.OSSBUCKET);
            } else {
                ossService = getHelper().initOSS(UbiaApplication.endpoint_us, BuildConfig.OSSBUCKET + "-us");
            }
        }


        ossService.setHandler(aliHandler);
        mCloudVideoAdapter = new CloudVideoAdapter(this,ossService);
        video_list.setAdapter(mCloudVideoAdapter);
        video_list.setOnItemClickListener(this);
        getAllLocatVideos(false, null);

        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

        String monthStr;
        if (mMonth < 9)
            monthStr = "0" + (mMonth + 1);
        else
            monthStr = "" + (mMonth + 1);

        String dayStr;
        if (mDay < 10)
            dayStr = "0" + mDay;
        else
            dayStr = "" + mDay;
        chooseDate(mYear + "" + monthStr + "" + dayStr);

        Log.e("guo.date.",mYear + "" + monthStr + "" + dayStr);

    }

    private void showChooseDateDialog() {
        //DialogUtil.getInstance().showChoseCloudSaveDateDialo(this, this);
        DatePickerDialog dpDialog = new DatePickerDialog(this,
                R.style.MyDatePickerDialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mYear = year;
                        mMonth = month;
                        String monthStr;
                        if (mMonth < 9)
                            monthStr = "0" + (mMonth + 1);
                        else
                            monthStr = "" + (mMonth + 1);
                        mDay = dayOfMonth;
                        String dayStr;
                        if (mDay < 10)
                            dayStr = "0" + mDay;
                        else
                            dayStr = "" + mDay;
                        chooseDate(mYear + "" + monthStr + "" + dayStr);
                    }
                },
                mYear, mMonth, mDay);
        if (Build.VERSION.SDK_INT >= 11) {
            DatePicker dpicker = dpDialog.getDatePicker();
            dpicker.setMaxDate(new Date().getTime());
        }
        dpDialog.show();
    }

    @Override
    public void chooseDate(String date) {
        this.date = date;
        cloudFileList.clear();
        ossService.asyncListObjectsWithBucketName(mDevUID, "video", date);
     //   ossService.deleteAllVideo(mDevUID);
    }



    private Handler aliHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            switch (msg.what) {
                case OssService.GETVIDEOLISTERROR:
                    video_tip_txt.setText(getString(R.string.get_cloud_video_error_tip));
                    video_tip_txt.setVisibility(View.VISIBLE);
                    video_list.setVisibility(View.GONE);
                    break;
                case OssService.GETVIDEOLISTSUCCESS:

                    List<FileInfo> tempList = (List<FileInfo>) msg.obj;
                    if (tempList != null && tempList.size() > 0) {
                        video_tip_txt.setVisibility(View.GONE);
                        video_list.setVisibility(View.VISIBLE);
                        cloudFileList.clear();
                        cloudFileList.addAll(tempList);

                        sendEmptyMessage(1001);
                    } else {
                        video_tip_txt.setText(getString(R.string.get_cloud_video_tip2));
                        video_tip_txt.setVisibility(View.VISIBLE);
                        video_list.setVisibility(View.GONE);
                    }
                    break;
                case OssService.DOWNLOADVIDEOLISTERROR:
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(CloudSaveVideoListActivity.this, getString(R.string.get_cloud_video_error_tip), Toast.LENGTH_SHORT).show();
                    break;
                case OssService.DOWNLOADVIDEOLISTSUCCESS:

                     getAllLocatVideos(true, (FileInfo) msg.obj);
                    break;
                case OssService.GETIMGSUCCESS:
                    initVideoImg((FileInfo) msg.obj);
                    break;
                case OssService.DELETESUCCESS:
                    chooseDate(CloudSaveVideoListActivity.this.date);
                    break;
                case OssService.DELETEFAIL:
                    //删除失败
                    break;
                case 1001:
                    assemblyData((FileInfo) msg.obj);
                    break;
            }
        }
    };

    private void initVideoImg(FileInfo tFi){
        for (FileInfo cFile : cloudFileList) {
            if (cFile.getFileName().equals(tFi.getFileName())) {
                if (tFi.getThumbnailImg() != null)
                    cFile.setThumbnailImg(tFi.getThumbnailImg());
                break;
            }
        }
      //  Collections.reverse(cloudFileList);
        sortList();
        mCloudVideoAdapter.setData(cloudFileList);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FileInfo cFileInfo = cloudFileList.get(i);
        if (cFileInfo.getDownLoadState() == 0) {
            ossService.asyncGetVideo(cFileInfo, mDevice);
            mProgressBar.setVisibility(View.VISIBLE);
            Toast.makeText(CloudSaveVideoListActivity.this, getString(R.string.page24_loading), Toast.LENGTH_SHORT).show();
        } else if (cFileInfo.getDownLoadState() == 1) {
            Intent mp4PlayIntent = new Intent(this, Mp4PlayActivity.class);
            mp4PlayIntent.putExtra("dev_uid", mDevUID);
            mp4PlayIntent.putExtra("path", cFileInfo.getFileLocatPath());
            mp4PlayIntent.putExtra("fileDate", cFileInfo.getRecordTime());
            startActivity(mp4PlayIntent);
        }
    }

    private void getAllLocatVideos(final boolean isChangeAdapter, final FileInfo dlFile) {
        locatFileList.clear();
        isGetLocatViedoSuccess = false;
        new Thread() {
            @Override
            public void run() {
                File deviceDir = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/ubia/cloudVideo/" + mDevUID + "/");
                if (deviceDir.exists()) {
                    File[] list = deviceDir.listFiles();
                    if (list != null) {
                        for (File file : list) {
                            if (file.isDirectory())
                                continue;
                            if (!file.isFile())
                                continue;
                            if (!file.getName().toUpperCase().contains(".MP4"))
                                continue;
                            FileInfo fInfo = new FileInfo();
                            fInfo.setFileName(file.getName());
                            fInfo.setFileLocatPath(file.getAbsolutePath());
                            fInfo.setFileLocatUri("file://" + file.getAbsolutePath());
                            locatFileList.add(fInfo);
                        }
                    }
                }
                isGetLocatViedoSuccess = true;
                if (isChangeAdapter) {
                    Message msg = new Message();
                    msg.what = 1001;
                    msg.obj = dlFile;
                    aliHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    private void assemblyData(FileInfo dlFile) {
        if (isGetLocatViedoSuccess) {
            for (FileInfo cFile : cloudFileList) {
                for (FileInfo lFile : locatFileList) {
                    if (cFile.getFileName().equals(lFile.getFileName())) {
                        try {
                            cFile.setDownLoadState(1);
                            cFile.setFileLocatPath(lFile.getFileLocatPath());
                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                            mmr.setDataSource(cFile.getFileLocatPath());
                            cFile.setFileTimeLength((Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000));
                            cFile.setThumbnailImg(mmr.getFrameAtTime(-1));
                        }catch (Exception e){
                            new File(cFile.getFileLocatPath()).delete(); //解析损坏的文件时报错，删除损坏文件
                        }
                        break;
                    }
                }
                if (dlFile != null && cFile.getFileName().equals(dlFile.getFileName())) {
                    Intent mp4PlayIntent = new Intent(this, Mp4PlayActivity.class);
                    mp4PlayIntent.putExtra("dev_uid", mDevUID);
                    mp4PlayIntent.putExtra("path", cFile.getFileLocatPath());
                    mp4PlayIntent.putExtra("fileDate", cFile.getRecordTime());
                    startActivity(mp4PlayIntent);
                    mProgressBar.setVisibility(View.GONE);
                }
            }
         //   Collections.reverse(cloudFileList);


            sortList();
            mCloudVideoAdapter.setData(cloudFileList);
        }
    }

    private void sortList(){
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
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            byte[] var2 = msg.getData().getByteArray("data");

            switch (msg.what) {
                case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_ADVANCESETTINGS_RESP:
                    char adv_valid = (char) var2[48] ;
                    if(adv_valid==0x7E){
                        int ver = Packet.byteArrayToInt_Little(var2, 32);
                        mDevice.firmwareVersion = getVersion(ver);
                        selectCloud();
                    }
                    break;

            }
        }
    };

    private String getVersion(int var1) {
        byte[] var2 = new byte[4];
        StringBuffer var3 = new StringBuffer();
        var2[3] = (byte) var1;
        var2[2] = (byte) (var1 >>> 8);
        var2[1] = (byte) (var1 >>> 16);
        var2[0] = (byte) (var1 >>> 24);
        var3.append(255 & var2[0]);
        var3.append('.');
        var3.append(255 & var2[1]);
        var3.append('.');
        var3.append(255 & var2[2]);
        var3.append('.');
        var3.append(255 & var2[3]);
        mDevice.firmwareVersionPrefix = (255 & var2[0]);
        return var3.toString();
    }



    public void receiveIOCtrlData(Camera var1, int var2, int var3, byte[] var4) {
        Log.d("oss", "receiveIOCtrlData.............." + var3);
        if (this.mCamera == var1) {
            Bundle var5 = new Bundle();
            var5.putInt("sessionChannel", var2);
            var5.putByteArray("data", var4);
            Message var6 = new Message();
            var6.what = var3;
            var6.setData(var5);
            this.handler.sendMessage(var6);
        }

    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.mCamera != null) {
            this.mCamera.unregisterIOTCListener(this);
        }
    }


}



