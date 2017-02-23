package com.mportal.client;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mportal.client.activity.StartUpActivity;
import com.mportal.client.bean.App;

/**
 * Created by zhangwen on 2017/2/24.
 */

public class AppManager {

    private Context mContext;

    private static AppManager sAppManager;

    private AppContext mAppContext;

    private AppManager(Context context){
        mContext = context;
        mAppContext = AppContext.getInstance(context);
    }

    public static AppManager getInstant(Context context){
        if (sAppManager==null){
            synchronized (AppManager.class){
                if (sAppManager==null){
                    sAppManager = new AppManager(context);
                }
            }
        }
        return sAppManager;
    }


    public void restart() {

        Intent mStartActivity = new Intent(mContext, StartUpActivity.class);
        mStartActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId, mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis()+20 , mPendingIntent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void switchLayout() {
        // 杀死该应用进程
        App app = mAppContext.getApp();
        app.setLayoutLocalScheme(app.getLayoutLocalScheme() ^ 1);
        mAppContext.setApp(app);
        this.restart();
    }



}
