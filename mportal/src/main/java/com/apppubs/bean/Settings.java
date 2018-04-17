package com.apppubs.bean;

import com.apppubs.constant.URLs;

import java.io.Serializable;

public class Settings implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int THEME_BLUE = 0;
	public static final int THEME_INDIGO = 1;
	public static final int THEME_RED = 2;
	public static final int THEME_BROWN = 3;
	public static final int THEME_CUSTOM = 4;
	
	public static final int TEXTSIZE_BIG = 0;
	public static final int TEXTSIZE_MEDIUM = 1;
	public static final int TEXTSIZE_SMALL = 2;
	
	private int TextSize = 1;
	private boolean isNeedPushNotification = true;//是显示推送通知
	private boolean isAllowDowPicUse2G = true;
	private float cacheSize = 0f;
	
	private boolean isFristSplash=true;//是否时启动时打开的启动界面
	private boolean isAllowAutoLogin;//是否自动登录
	
	private int theme;//app颜色主题
	private String customThemeColor;//用户自定义
	private boolean isDevMode;
	private String baseURL = "http://123.56.46.218/";
//	private String appCode = "U1433518913341";
	private String appCode = "U1433417616429";//国华
//	private String baseURL = "http://124.205.71.106:8080/";
//	private String appCode = "D63";

	public Settings(){}
	public Settings(String baseUrl,String appCode){
		this.baseURL = baseUrl;
		this.appCode = appCode;
	}
	public int getTextSize() {
		return TextSize;
	}
	/**
	 * 设置大小
	 * @param textSize 
	 * 	TEXTSIZE_BIG 大字体
	 * 	TEXTSIZE_MEDIUM 中等字体
	 * 	TEXTSIZE_SMALL 小字体
	 */
	
	public void setTextSize(int textSize) {
		TextSize = textSize;
	}
	public boolean isAllowAutoLogin() {
		return isAllowAutoLogin;
	}
	public void setIsAllowAutoLogin(boolean allow) {
		this.isAllowAutoLogin = allow;
	}
	
	public boolean isFristSplash() {
		return isFristSplash;
	}
	
	public  void setFristSplash(boolean isFristSplash) {
		this.isFristSplash = isFristSplash;
	}
	public boolean isNeedPushNotification() {
		return isNeedPushNotification;
	}
	public void setNeedPush(boolean isNeedPush) {
		this.isNeedPushNotification = isNeedPush;
	}
	public boolean isAllowDowPicUse2G() {
		return isAllowDowPicUse2G;
	}
	public void setAllowDowPicUse2G(boolean isAllowDowPicUse2G) {
		this.isAllowDowPicUse2G = isAllowDowPicUse2G;
	}
	public float getCacheSize() {
		return cacheSize;
	}
	public void setCacheSize(float cacheSize) {
		this.cacheSize = cacheSize;
	}
	public int getTheme() {
		return theme;
	}
	public void setTheme(int theme) {
		this.theme = theme;
	}
	
	public String getCustomThemeColor() {
		return customThemeColor;
	}
	public void setCustomThemeColor(String customThemeColor) {
		this.customThemeColor = customThemeColor;
	}
	public String getBaseURL() {
		return baseURL;
	}
	public void setBaseURL(String baseURL) {
		URLs.baseURL = baseURL;
		this.baseURL = baseURL;
	}
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		URLs.appCode = appCode;
		this.appCode = appCode;
	}
	public boolean isDevMode() {
		return isDevMode;
	}
	public void setDevMode(boolean isDevMode) {
		this.isDevMode = isDevMode;
	}
	
	
	
}
