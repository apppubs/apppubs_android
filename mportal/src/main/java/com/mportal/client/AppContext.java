package com.mportal.client;

import android.content.Context;

import com.mportal.client.bean.App;
import com.mportal.client.bean.AppConfig;
import com.mportal.client.bean.Settings;
import com.mportal.client.bean.User;
import com.mportal.client.util.LogM;
import com.mportal.client.util.Utils;

/**
 * Created by zhangwen on 2017/2/22.
 */

public class AppContext {

    private static final String APP_CONFIG_FILE_NAME = "app_config.cfg";
    private static final String APP_FILE_NAME = "app.cfg";
    private static final String USER_FILE_NAME = "user.cfg";
    private static final String SYSTEM_SETTING_FILE_NAME = "settings.cfg";


    private static AppContext sAppContext;
    private Context mContext;

    private App mApp;
    private AppConfig mAppConfig;

    private User mCurrentUser;

    private Settings mSettings;

    private AppContext(Context context){
        mContext = context;
    }

    public static AppContext getInstance(Context context){
        if (sAppContext ==null){
            synchronized (AppContext.class){
                if (sAppContext ==null){
                    sAppContext = new AppContext(context);
                }
            }
        }
        return sAppContext;
    }

    public App getApp() {
        if (mApp ==null){
            synchronized (App.class){
                if (mApp ==null){
                    mApp = (App) MportalApplication.readObj(mContext,APP_FILE_NAME);
                    if (mApp ==null){
                        mApp = new App();
                    }
                }
            }
        }
        return mApp;
    }

    public void setApp(App mApp) {
        MportalApplication.writeObj(mContext,mApp,APP_FILE_NAME);
        this.mApp = mApp;
    }

    public AppConfig getAppConfig() {

        if (mAppConfig ==null){
            synchronized (AppConfig.class){
                if (mAppConfig ==null){
                    mAppConfig = (AppConfig) MportalApplication.readObj(mContext,APP_CONFIG_FILE_NAME);
                    if (mAppConfig ==null){
                        mAppConfig = new AppConfig();
                    }
                }
            }
        }
        return mAppConfig;
    }

    public void setAppConfig(AppConfig mAppConfig) {
        MportalApplication.writeObj(mContext,mAppConfig,APP_CONFIG_FILE_NAME);
        LogM.log(this.getClass(),"save2File");
        this.mAppConfig = mAppConfig;
    }

    public User getCurrentUser(){
        if (mCurrentUser==null){
            synchronized (AppContext.class){
                if (mCurrentUser==null){
                    mCurrentUser = (User) MportalApplication.readObj(mContext,USER_FILE_NAME);
                }
                if (mCurrentUser==null){
                    mCurrentUser = new User();
                }
            }
        }
        return mCurrentUser;
    }

    public void setCurrentUser(User user){
        mCurrentUser = user;
        MportalApplication.writeObj(mContext,user,USER_FILE_NAME);
    }

    public void clearCurrentUser(){
        this.setCurrentUser(new User());
    }

    public Settings getSettings() {
        if (mSettings==null){
            synchronized (AppContext.class){
                if (mSettings==null){
                    mSettings = (Settings) MportalApplication.readObj(mContext,SYSTEM_SETTING_FILE_NAME);
                }

                if (mSettings==null){
                    String baseUrl = Utils.getMetaValue(mContext, "BASE_URL");
                    String appCode = Utils.getMetaValue(mContext, "APPCODE");
                    mSettings = new Settings(baseUrl,appCode);
                }
            }
        }
        return mSettings;
    }

    public void setSettings(Settings mSettings) {
        this.mSettings = mSettings;
        MportalApplication.writeObj(mContext,mSettings,SYSTEM_SETTING_FILE_NAME);
    }

    /**
     * 各种url中的占位符
     */
    public static final String PLACEHOLDER_USERNAME = "$username";//用户名
    public static final String PLACEHOLDER_USERID = "$userid";//用户ID；
    public static final String PLACEHOLDER_APPID = "$appid";//appid==appcode
    public static final String PLACEHOLDER_COPER_CODE = "$corpcode";



    /**
     * 转换服务器传来的url
     * @param url
     * @return
     */
    public String convertUrl(String url){
        if(url.contains(PLACEHOLDER_USERNAME)){
            url = url.replace(PLACEHOLDER_USERNAME, getCurrentUser().getUsername());

        }
        if(url.contains(PLACEHOLDER_USERID)){
            url = url.replace(PLACEHOLDER_USERID, getCurrentUser().getUserId());

        }
        if(url.contains(PLACEHOLDER_APPID)){
            url = url.replace(PLACEHOLDER_APPID, getApp().getCode());
        }
        if(url.contains(PLACEHOLDER_COPER_CODE)){
            url = url.replace(PLACEHOLDER_COPER_CODE,  getCurrentUser().getOrgCode());
        }
        return url;
    }
}
