package com.apppubs.d20;

import android.content.Context;

import com.apppubs.d20.bean.App;
import com.apppubs.d20.bean.AppConfig;
import com.apppubs.d20.bean.Settings;
import com.apppubs.d20.bean.User;
import com.apppubs.d20.bean.UserInfo;
import com.apppubs.d20.myfile.FileCacheManager;
import com.apppubs.d20.myfile.FileCacheManagerImpl;
import com.apppubs.d20.net.WMHHttpClient;
import com.apppubs.d20.net.WMHHttpClientDefaultImpl;
import com.apppubs.d20.net.WMHHttpErrorCode;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.Utils;

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
		mApp = (App) MportalApplication.readObj(mContext, APP_FILE_NAME);
		if (mApp == null) {
			mApp = new App();
		}
		mCurrentUser = (UserInfo) MportalApplication.readObj(mContext, USER_FILE_NAME);
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
		LogM.log(AppContext.class,"获取app:"+mApp.toString());
		return mApp;
	}

	public void setApp(App mApp) {

		this.mApp = mApp;
		serializeApp();
	}

	public synchronized void serializeApp(){
		MportalApplication.writeObj(mContext, mApp, APP_FILE_NAME);
		LogM.log(AppContext.class,"保存app:"+mApp.toString());
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

	public synchronized void setCurrentUser(UserInfo user) {
		mCurrentUser = user;
		MportalApplication.writeObj(mContext, user, USER_FILE_NAME);
	}

	public void clearCurrentUser() {
		this.setCurrentUser(new UserInfo());
	}

	public Settings getSettings() {
		if (mSettings == null) {
			synchronized (AppContext.class) {
				if (mSettings == null) {
					mSettings = (Settings) MportalApplication.readObj(mContext, SYSTEM_SETTING_FILE_NAME);
				}

				if (mSettings == null) {
					String baseUrl = Utils.getMetaValue(mContext, "BASE_URL");
					String appCode = Utils.getMetaValue(mContext, "APPCODE");
					mSettings = new Settings(baseUrl, appCode);
				}
			}
		}
		return mSettings;
	}

	public void setSettings(Settings mSettings) {
		this.mSettings = mSettings;
		MportalApplication.writeObj(mContext, mSettings, SYSTEM_SETTING_FILE_NAME);
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
	 *
	 * @param url
	 * @return
	 */
	public String convertUrl(String url) {
		if (url.contains(PLACEHOLDER_USERNAME)) {
			url = url.replace(PLACEHOLDER_USERNAME, getCurrentUser().getUsername());

		}
		if (url.contains(PLACEHOLDER_USERID)) {
			url = url.replace(PLACEHOLDER_USERID, getCurrentUser().getUserId());

		}
		if (url.contains(PLACEHOLDER_APPID)) {
			url = url.replace(PLACEHOLDER_APPID, getApp().getCode());
		}
		if (url.contains(PLACEHOLDER_COPER_CODE)) {
			url = url.replace(PLACEHOLDER_COPER_CODE, getCurrentUser().getOrgCode());
		}
		return url;
	}

	public FileCacheManager getCacheManager(){

		return FileCacheManagerImpl.getInstance(mContext);
	}

	public WMHHttpClient getHttpClient(){
		if (mHttpClient==null){
			mHttpClient = new WMHHttpClientDefaultImpl();
		}
		return mHttpClient;
	}
}
