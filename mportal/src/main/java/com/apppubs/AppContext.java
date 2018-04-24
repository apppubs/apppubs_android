package com.apppubs;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.apppubs.bean.App;
import com.apppubs.bean.AppConfig;
import com.apppubs.bean.Settings;
import com.apppubs.bean.UserInfo;
import com.apppubs.constant.URLs;
import com.apppubs.bean.CompelMessageDialogActivity;
import com.apppubs.ui.home.CompelReadMessageModel;
import com.apppubs.model.myfile.FileCacheManager;
import com.apppubs.model.myfile.FileCacheManagerImpl;
import com.apppubs.net.WMHHttpClient;
import com.apppubs.net.WMHHttpClientDefaultImpl;
import com.apppubs.net.WMHHttpErrorCode;
import com.apppubs.net.WMHRequestListener;
import com.apppubs.util.JSONResult;
import com.apppubs.util.JSONUtils;
import com.apppubs.util.LogM;
import com.apppubs.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
					mSettings = new Settings();
					resetBaseUrlAndAppCode();
				}
			}
		}
		return mSettings;
	}

	public void resetBaseUrlAndAppCode(){
		String baseUrl = Utils.getMetaValue(mContext, "BASE_URL");
		String appCode = Utils.getMetaValue(mContext, "APPCODE");
		mSettings.setBaseURL(baseUrl);
		mSettings.setAppCode(appCode);
		setSettings(mSettings);
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
	public static final String PLACEHOLDER_PASSWORD = "$password";

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
		if (url.contains(PLACEHOLDER_PASSWORD)){
			url = url.replace(PLACEHOLDER_PASSWORD,getCurrentUser().getPassword());
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

	public void showCompelMessageIfHave(){

		getHttpClient().GET(URLs.URL_COMPEL_READ_LIST,new String[]{URLs.baseURL,URLs.appCode,getCurrentUser().getUsername()}, new WMHRequestListener(){

			@Override
			public void onDone(JSONResult jsonResult, @Nullable WMHHttpErrorCode errorCode) {
				if (errorCode==null){
					if (jsonResult.code ==1){
						String infoListJson = "";
						try {
							JSONObject jo = new JSONObject(jsonResult.result);
							infoListJson = jo.getString("infolist");
							Log.v("HomeBaseActivity","infolist"+infoListJson);
							List<CompelReadMessageModel> l = JSONUtils.parseListFromJson(infoListJson,CompelReadMessageModel.class);
							if (l.isEmpty()){
								return;
							}
							ArrayList<CompelReadMessageModel> list = new ArrayList<CompelReadMessageModel>(l);
							Intent i = new Intent(mContext,CompelMessageDialogActivity.class);
							i.putExtra(CompelMessageDialogActivity.EXTRA_DATAS,list);
							mContext.startActivity(i);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}else{
//						Log.e("HomeBaseActivity",jsonResult.msg);
					}
				}else{
					Log.e("HomeBaseActivity",errorCode.getMessage());
				}
			}
		});
	}
}
