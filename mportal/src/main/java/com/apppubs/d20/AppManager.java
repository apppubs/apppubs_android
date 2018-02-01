package com.apppubs.d20;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apppubs.d20.constant.Constants;
import com.apppubs.d20.start.StartUpActivity;
import com.apppubs.d20.bean.App;
import com.apppubs.d20.bean.Settings;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.SharedPreferenceUtils;
import com.apppubs.d20.util.Utils;
import com.apppubs.d20.widget.ConfirmDialog;

/**
 * Created by zhangwen on 2017/2/24.
 */

public class AppManager {

    private Context mContext;

    private static AppManager sAppManager;

    private AppContext mAppContext;

    private AppManager(Context context) {
        mContext = context;
        mAppContext = AppContext.getInstance(context);
    }

    public static AppManager getInstant(Context context) {
        if (sAppManager == null) {
            synchronized (AppManager.class) {
                if (sAppManager == null) {
                    sAppManager = new AppManager(context);
                }
            }
        }
        return sAppManager;
    }


    public void restart() {
        Intent intent = new Intent(mContext, StartUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
    }

    public void switchLayout() {
        // 杀死该应用进程
        App app = mAppContext.getApp();
        app.setLayoutLocalScheme(app.getLayoutLocalScheme() ^ 1);
        mAppContext.setApp(app);
        mAppContext.serializeApp();
        this.restart();
    }

    public void showChangeDialog(final Context context, final String ip, final String code) {
        new ConfirmDialog(context, new ConfirmDialog.ConfirmListener() {

            @Override
            public void onOkClick() {
                final Settings s = mAppContext.getSettings();

                LogM.log(this.getClass(), ip + ":" + code);
                String verifyURL = ip + URLs.URL_APP_BASIC_INFO + "&appcode=" + code;
                StringRequest request = new StringRequest(verifyURL, new Response
                        .Listener<String>() {

                    @Override
                    public void onResponse(String str) {
                        if (!TextUtils.isEmpty(str) && !str.equals("null")) {
                            s.setBaseURL(ip);
                            s.setAppCode(code);
                            AppContext.getInstance(context).setSettings(s);
                            mAppContext.clearCurrentUser();
                            App app = AppContext.getInstance(context).getApp();
                            app.init();
                            AppContext.getInstance(context).setApp(app);
                            AppContext.getInstance(context).serializeApp();
                            AppManager.getInstant(context).restart();

                        } else {

                            Toast.makeText(mContext, "ip地址错误或者客户号不存在", Toast.LENGTH_LONG).show();

                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Toast.makeText(mContext, "ip地址错误或者客户号不存在", Toast.LENGTH_LONG).show();
                    }

                });
                Volley.newRequestQueue(mContext).add(request);

            }

            @Override
            public void onCancelClick() {
            }
        }, "确定修改？修改会抹除当前数据然后自动启动", "取消", "确定").show();
    }


    public void destory() {
        App app = mAppContext.getApp();
        app.setPreWorkingVersion(Utils.getVersionCode(mContext));
        mAppContext.serializeApp();
    }

    public void downloadApp(String url) {
        final DownloadManager dManager = (DownloadManager) mContext.getSystemService(Context
                .DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置下载路径和文件名
        request.setDestinationInExternalPublicDir("download", "sdic.apk");
        request.setDescription(mAppContext.getApp().getName());
        request.setNotificationVisibility(DownloadManager.Request
                .VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
        // 获取此次下载的ID
        final long refernece = dManager.enqueue(request);
        SharedPreferenceUtils.getInstance(mContext).putLong(Constants
                .DEFAULT_SHARED_PREFERENCE_NAME, Constants
                .SHAREDPREFRERENCE_KEY_DOWNLOAD_REFERENCE, refernece);
    }

    public void saveCurrentAddress(String addressName, String addressCode) {
        SharedPreferenceUtils utils = SharedPreferenceUtils.getInstance(mContext);
        SharedPreferenceUtils.getInstance(mContext).putString(
                Constants.DEFAULT_SHARED_PREFERENCE_NAME,
                Constants.SHAREDPREFRERENCE_KEY_ADDRESS_CODE,
                addressCode);
        utils.putString(Constants.DEFAULT_SHARED_PREFERENCE_NAME,
                Constants.SHAREDPREFRERENCE_KEY_ADDRESS_NAME,
                addressName);
    }

    public String getCurrentAddressName() {
        SharedPreferenceUtils utils = SharedPreferenceUtils.getInstance(mContext);
        return utils.getString(Constants.DEFAULT_SHARED_PREFERENCE_NAME, Constants
                .SHAREDPREFRERENCE_KEY_ADDRESS_NAME, "");
    }

    public String getCurrentAddressCode() {
        SharedPreferenceUtils utils = SharedPreferenceUtils.getInstance(mContext);
        return utils.getString(Constants.DEFAULT_SHARED_PREFERENCE_NAME, Constants
                .SHAREDPREFRERENCE_KEY_ADDRESS_CODE, "");
    }
}
