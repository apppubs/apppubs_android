package com.mportal.client;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mportal.client.activity.StartUpActivity;
import com.mportal.client.bean.App;
import com.mportal.client.bean.Settings;
import com.mportal.client.bean.User;
import com.mportal.client.constant.URLs;
import com.mportal.client.util.LogM;
import com.mportal.client.widget.ConfirmDialog;

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

    public void showChangeDialog(final Context context, final String  ip, final String code) {
        new ConfirmDialog(context, new ConfirmDialog.ConfirmListener() {

            @Override
            public void onOkClick() {
                final Settings s = mAppContext.getSettings();

                LogM.log(this.getClass(), ip + ":" + code);
                String verifyURL = ip+ URLs.URL_APP_BASIC_INFO+"&appcode="+code;
                StringRequest request = new StringRequest(verifyURL, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String str) {
                        if(!TextUtils.isEmpty(str)&&!str.equals("null")){
                            s.setBaseURL(ip);
                            s.setAppCode(code);
                            AppContext.getInstance(context).setSettings(s);
                            mAppContext.clearCurrentUser();
                            App app = AppContext.getInstance(context).getApp();
                            app.setStartupTimes(0);
                            AppContext.getInstance(context).setApp(app);
                            AppManager.getInstant(context).restart();

                        }else{

                            Toast.makeText(mContext, "ip地址错误或者客户号不存在",Toast.LENGTH_LONG).show();

                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Toast.makeText(mContext, "ip地址错误或者客户号不存在",Toast.LENGTH_LONG).show();
                    }

                });
                Volley.newRequestQueue(mContext).add(request);

            }

            @Override
            public void onCancelClick() {
            }
        }, "确定修改？修改会抹除当前数据然后自动启动", "取消", "确定").show();
    }



}
