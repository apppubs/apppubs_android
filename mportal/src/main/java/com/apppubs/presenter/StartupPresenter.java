package com.apppubs.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.apppubs.AppContext;
import com.apppubs.AppManager;
import com.apppubs.bean.App;
import com.apppubs.bean.http.CheckVersionResult;
import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.SystemBiz;
import com.apppubs.ui.start.IStartUpView;
import com.apppubs.util.SharedPreferenceUtils;
import com.apppubs.util.Utils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangwen on 2017/9/5.
 */

public class StartupPresenter {

    public static final String SHARED_PREFERENCE_NAME_WELCOME_LOAD_HISTORY = "welcome_load_history";

    private final long SKIP_MILLIS = 5 * 1000;
    private SystemBiz mSystemBiz;
    private IStartUpView mView;
    private Context mContext;
    private boolean isStartCanceled;

    public StartupPresenter(Context context, IStartUpView view) {
        mContext = context;
        mSystemBiz = SystemBiz.getInstance(context);
        mView = view;
    }

    public void onStart() {

        String startUpPic = AppContext.getInstance(mContext).getApp().getStartUpPic();
        if (!TextUtils.isEmpty(startUpPic)) {
            showBgPic(startUpPic);
        }
        if (isNeedWelcome()) {
            mView.showWelcomeFragment();
        } else {
            init();
        }
    }

    public void onWelcomeBack() {
        mView.hideWelcomeFragment();
        init();
    }

    public void onSkipBtnClicked() {
        mView.skip2Home();
    }

    public void onSkipBtnCompleted() {
        mView.skip2Home();
    }

    public void onUpdateCancel() {
        mView.skip2Home();
    }

    public void init() {
        mSystemBiz.initSystem(new IAPCallback<App>() {
            @Override
            public void onDone(App obj) {
                showBgPic(obj.getStartUpPic());
                checkUpdate();
            }

            @Override
            public void onException(APError excepCode) {
                mView.showInitFailDialog();
            }
        });
    }

    private void showBgPic(String url) {
        mView.showBgImage(url);
    }

    private boolean isNeedWelcome() {

        String flag = SharedPreferenceUtils.getInstance(mContext).getString
                (SHARED_PREFERENCE_NAME_WELCOME_LOAD_HISTORY, Utils.getVersionCode(mContext) + "", "");

        //版本升级,并且有欢迎图
        if (TextUtils.isEmpty(flag)) {

            int welcomePicNum = 0;
            try {
                welcomePicNum = mContext.getAssets().list("welcome").length;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (welcomePicNum > 0) {
                return true;
            }
        }
        return false;
    }

    private void checkUpdate() {
        int updateType = AppContext.getInstance(mContext).getApp().getUpdateType();
        if (updateType != 0) {
            mSystemBiz.checkUpdate(new IAPCallback<CheckVersionResult>() {
                @Override
                public void onDone(CheckVersionResult obj) {
                    //需要进行版本检查
                    if (obj.getUpdateType() > 1) {
                        String title = "发现新版本 V" + obj.getVersionName();
                        mView.showUpdateDialog(title, obj.getDescribe(), obj.getDownloadURL(), obj.getUpdateType() > 2);
                        if (obj.getUpdateType() < 2) {
                            afterCheckUpdate();
                        } else {
                            //强制更新，不能进入主界面
                        }
                    } else {
                        afterCheckUpdate();
                    }
                }

                @Override
                public void onException(APError error) {
                    afterCheckUpdate();
                }
            });
        } else {
            afterCheckUpdate();
        }
    }

    private void afterCheckUpdate() {
        boolean enableSkip = Utils.getBooleanMetaValue(mContext, "ENABLE_SPLASH_SKIP");
        if (enableSkip) {
            mView.showSkipBtn(SKIP_MILLIS);
        } else {
            preSkip2Home();
        }
    }

    public void startDownloadApp(String updateUrl) {
//		Intent it = new Intent(mContext, DownloadAppService.class);
//		it.putExtra(DownloadAppService.SERVICRINTENTURL, updateUrl);
//		it.putExtra(DownloadAppService.SERVACESHARENAME, 0);
//		mContext.startService(it);
        AppManager.getInstance(mContext).downloadApp(updateUrl);
    }

    public void preSkip2Home() {
        if (Utils.getBooleanMetaValue(mContext, "NEED_START_UP_VERSION")) {
            String versionStr = AppContext.getInstance(mContext).getVersionString();
            mView.showVersion(versionStr);
        }
        Timer skipTimer = new Timer();
        skipTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isStartCanceled) {
                    return;
                }
                mView.skip2Home();
            }
        }, 2 * 1000);
    }

    public void cancelSkip2Home() {
        isStartCanceled = true;
    }


}
