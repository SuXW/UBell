package com.keeplife;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.util.Log;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.ubia.MainActivity;
import cn.ubia.UbiaApplication;
import cn.ubia.util.UbiaUtil;

import com.xiaomi.mipush.sdk.MiPushClient;

import static cn.ubia.UbiaApplication.APP_ID;
import static cn.ubia.UbiaApplication.APP_KEY;
import static cn.ubia.UbiaApplication.context;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobWakeUpService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        try {
         //   Log.d("JobSchedulerService","start~~"+ System.currentTimeMillis());
            //重启service
          if (UbiaUtil.shouldInitPush(this) ) {
                MiPushClient.registerPush(this, APP_ID, APP_KEY);
                JPushInterface.init(this);
           }

                //创建一个新的JobScheduler任务
            scheduleRefresh();
           // jobFinished(params, false);
                //   Log.d("JobSchedulerService","7.0 handleMessage task end~~"+ System.currentTimeMillis());
            return false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
    //    Log.d("JobSchedulerService","onStopJob...........~");
        return false;
    }

    private void scheduleRefresh() throws Exception {
        JobScheduler mJobScheduler = (JobScheduler)getApplicationContext()
                .getSystemService(JOB_SCHEDULER_SERVICE);

        JobInfo.Builder mJobBuilder =
                new JobInfo.Builder(0,
                        new ComponentName(getPackageName(),
                                JobWakeUpService.class.getName()));


        mJobBuilder.setMinimumLatency(10 * 1000).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);


        if (mJobScheduler != null && mJobScheduler.schedule(mJobBuilder.build())
                <= JobScheduler.RESULT_FAILURE) {
            //Scheduled Failed/LOG or run fail safe measures
            Log.d("JobSchedulerService", "7.0 Unable to schedule the service FAILURE!");
        }else{
       //     Log.d("JobSchedulerService", "7.0 schedule the service SUCCESS!");
        }
    }

}
