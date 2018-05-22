package com.apppubs;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;

import com.apppubs.bean.App;
import com.apppubs.bean.AppConfig;
import com.apppubs.bean.Settings;
import com.apppubs.bean.UserInfo;
import com.apppubs.bean.http.AppInfoResult;
import com.apppubs.constant.Constants;
import com.apppubs.d20.R;
import com.apppubs.model.myfile.FileCacheManager;
import com.apppubs.model.myfile.FileCacheManagerImpl;
import com.apppubs.net.WMHHttpClient;
import com.apppubs.net.WMHHttpClientDefaultImpl;
import com.apppubs.util.FileUtils;
import com.apppubs.util.LogM;
import com.apppubs.util.Utils;
import com.bumptech.glide.load.Encoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by zhangwen on 2017/2/22.
 */

public class AppContext {

    private static final String APP_FILE_NAME = "app.cfg";
    private static final String USER_FILE_NAME = "user.cfg";
    private static final String SYSTEM_SETTING_FILE_NAME = "settings.cfg";


    private static AppContext sAppContext;
    private Context mContext;

    private App mApp;

    private UserInfo mCurrentUser;

    private Settings mSettings;

    private WMHHttpClient mHttpClient;

    private AppContext(Context context) {
        mContext = context;
        mApp = (App) FileUtils.readObj(mContext, APP_FILE_NAME);
        if (mApp == null) {
            mApp = new App();
        }
        mCurrentUser = (UserInfo) FileUtils.readObj(mContext, USER_FILE_NAME);
        if (mCurrentUser == null) {
            mCurrentUser = new UserInfo();
        }
    }

    public static AppContext getInstance(Context context) {
        if (sAppContext == null) {
            synchronized (AppContext.class) {
                if (sAppContext == null) {
                    sAppContext = new AppContext(context);
                }
            }
        }
        return sAppContext;
    }

    public App getApp() {
        return mApp;
    }

    public void setApp(App mApp) {
        this.mApp = mApp;
        serializeApp();
    }

    public synchronized void serializeApp() {
        FileUtils.writeObj(mContext, mApp, APP_FILE_NAME);
        LogM.log(AppContext.class, "保存app:" + mApp.toString());
    }

    public void increaseInitTimes() {
        mApp.setInitTimes(mApp.getInitTimes() + 1);
        this.serializeApp();
    }

    public void updateWithAppInfo(AppInfoResult info) {
        mApp.setCode(info.getAppId());
        mApp.setName(info.getName());
        mApp.setLoginFlag(info.getLoginFlag());
        mApp.setWebLoginUrl(info.getWebLoginURL());
        mApp.setAllowRegister(info.getUserRegFlag());
        mApp.setLayoutScheme(info.getLayoutSchema());
        mApp.setDefaultTheme(info.getDefaultTheme());
        mApp.setCustomThemeColor(info.getThemeColor());
        mApp.setMenuUpdateTime(info.getMenuUpdateTime());
        mApp.setStartUpPic(info.getStartupPicURL());
        mApp.setLoginPicUrl(info.getLoginPicURL());
        mApp.setUpdateType(info.getUpdateType());
        mApp.setWebAppCode(info.getCMSId());
        if (mApp.getInitTimes() == 0) {
            mApp.setLayoutLocalScheme(info.getLayoutSchema());
            mSettings.setTheme(info.getDefaultTheme());
        }
        mApp.getAppConfig().update(info.getConfigs());
        this.serializeApp();
    }

    public AppConfig getAppConfig() {

        return getApp().getAppConfig();
    }

    public synchronized void setAppConfig(AppConfig appConfig) {

        mApp.setAppConfig(appConfig);
        serializeApp();
    }

    public UserInfo getCurrentUser() {
        return mCurrentUser;
    }

    public boolean haveLogined() {
        return mCurrentUser != null && !TextUtils.isEmpty(mCurrentUser.getUsername());
    }

    public synchronized void setCurrentUser(UserInfo user) {
        mCurrentUser = user;
        FileUtils.writeObj(mContext, user, USER_FILE_NAME);
    }

    public void clearCurrentUser() {
        this.setCurrentUser(new UserInfo());
    }

    public Settings getSettings() {
        if (mSettings == null) {
            synchronized (AppContext.class) {
                if (mSettings == null) {
                    mSettings = (Settings) FileUtils.readObj(mContext,
                            SYSTEM_SETTING_FILE_NAME);
                }

                if (mSettings == null) {
                    mSettings = new Settings();
                    resetBaseUrlAndAppCode();
                }
            }
        }
        return mSettings;
    }

    public void resetBaseUrlAndAppCode() {
        String baseUrl = Utils.getMetaValue(mContext, "BASE_URL");
        String appCode = Utils.getMetaValue(mContext, "APPID");
        mSettings.setBaseURL(baseUrl);
        mSettings.setAppCode(appCode);
        setSettings(mSettings);
    }

    public String getLocalBaseURL() {
        return mSettings.getBaseURL();
    }

    public String getLocalAppId() {
        return mSettings.getAppCode();
    }

    public void setSettings(Settings mSettings) {
        this.mSettings = mSettings;
        FileUtils.writeObj(mContext, mSettings, SYSTEM_SETTING_FILE_NAME);
    }

    /**
     * 转换服务器传来的url
     *
     * @param url 转化之前url
     * @return 转换之后的url
     */
    public String convertUrl(String url) {
        try {
            url = URLEncoder.encode(url,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String myURL = getLocalBaseURL() + Constants.API_ENTRY + "?apiName=" + Constants.API_NAME_HTTP +
                "&redirectURL=" + url + "&username=" +
                getCurrentUser().getUsername() + "&token=" + getCurrentUser().getToken();
        return myURL;
    }

    public FileCacheManager getCacheManager() {

        return FileCacheManagerImpl.getInstance(mContext);
    }

    public WMHHttpClient getHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new WMHHttpClientDefaultImpl();
        }
        return mHttpClient;
    }

    public String getVersionString() {
        PackageInfo pInfo = null;
        try {
            pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo != null) {
            return "V" + pInfo.versionName + " (" + Utils.getVersionCode(mContext) + ")";
        } else {
            return null;
        }
    }

    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;

        try {
            PackageManager pm = mContext.getPackageManager();
            pi = pm.getPackageInfo(mContext.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    public String getVersionName() {
        PackageInfo pi;
        if ((pi = getPackageInfo()) != null) {
            return pi.versionName;
        }
        return null;
    }

    public int getVersionCode() {
        PackageInfo pi;
        if ((pi = getPackageInfo()) != null) {
            return pi.versionCode;
        }
        return 0;
    }

    public int getThemeColor() {
        int themeColor;
        int theme = getSettings().getTheme();
        // app配色
        if (theme < 4) {
            TypedArray array = mContext.getTheme().obtainStyledAttributes(new int[]{R.attr
                    .appDefaultColor});
            themeColor = array.getColor(0, 0x000000);
            array.recycle();
        } else {
            themeColor = Color.parseColor(getApp().getCustomThemeColor());
        }
        return themeColor;
    }

}
