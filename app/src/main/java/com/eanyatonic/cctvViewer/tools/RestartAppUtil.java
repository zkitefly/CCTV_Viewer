package com.eanyatonic.cctvViewer.tools;

import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * 多种方式重启应用自身
 */

public class RestartAppUtil
{

    /**
     * 使用 AlarmManager 来帮助重启
     *
     * @param context
     * @param cls
     */
    public static void restartByAlarm(Context context, Class<?> cls)
    {
        Intent mStartActivity = new Intent(context, cls);
        int mPendingIntentId = 123456;
        PendingIntent pIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 500, pIntent);

        System.exit(0);
    }

    /**
     * 使用 killProcess 杀死自身，系统会恢复应用
     *
     * @param context
     * @param cls
     */
    public static void restartByKillProcess(Context context, Class<?> cls)
    {
        Intent intent = new Intent(context, cls);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 通过清栈触发应用重启。但不会重启 application ，与应用相关的静态变量也会更重启前一样。
     *
     * @param context
     */
    public static void restartByClearTop(Context context)
    {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    /**
     * 利用系统重启api触发应用重启
     *
     * @param context
     */
    public static void restartBySystemApi(Context context)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        manager.restartPackage(context.getPackageName());
    }

    /**
     * 通过 Intent.makeRestartActivityTask 来触发应用重启，跟 restartByClearTop 类似。
     * 但不会重启 application ，与应用相关的静态变量也会更重启前一样。
     *
     * @param context
     */
    public static void restartByCompatApi(Context context, Class<?> cls)
    {
        Intent intent = new Intent(context, cls);
        Intent restartIntent = Intent.makeRestartActivityTask(intent.getComponent());
        context.startActivity(restartIntent);
        System.exit(0);
    }

    /**
     * 5.1 版本以后可以借助 JobScheduler 来重启应用
     *
     * @param context
     */
    public static void restartByJobScheduler(Context context, Class<?> cls)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            int delayTimeMin = 1000;
            int delayTimeMax = 2000;

            MyJobSchedulerService.setMainIntent(new Intent(context, cls));

            JobInfo.Builder jobInfoBuild = new JobInfo.Builder(0, new ComponentName(context, MyJobSchedulerService.class));
            jobInfoBuild.setMinimumLatency(delayTimeMin);
            jobInfoBuild.setOverrideDeadline(delayTimeMax);
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfoBuild.build());

            System.exit(0);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    static class MyJobSchedulerService extends JobService
    {
        private static Intent mIntent;

        public static void setMainIntent(Intent intent)
        {
            mIntent = intent;
        }

        @Override
        public boolean onStartJob(JobParameters params)
        {
            startActivity(mIntent);
            jobFinished(params, false);
            return false;
        }

        @Override
        public boolean onStopJob(JobParameters params)
        {
            return false;
        }
    }

}

