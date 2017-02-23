package com.mportal.client.bean;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.mportal.client.MportalApplication;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class App implements Serializable{
	
	private static final long serialVersionUID = 1L;


	public static final int STYLE_SLIDE_MENU = 0;
	public static final int LAYOUT_BOTTOM_MENU = 1;
	
	public static final int LOGIN_INAPP = 0;
	public static final int LOGIN_ONSTART_USE_USERNAME_PASSWORD = 1;
	public static final int LOGIN_ONSTART_USE_PHONE_NUMBER = 2;
	public static final int LOGIN_ONSTART_USE_USERNAME = 3;
	public static final int LOGIN_ONSTART_USE_USERNAME_PASSWORD_ORGCODE = 4;
	public static final int LOGIN_ONSTART_WEB = 5;//使用web方式登录
	
	public static final int ALLOW_CHAT_TRUE = 1;
	public static final int ALLOW_CHAT_FALSE = 0;
	
	public static final int ALLOW_MODIFY_USER_INFO_TRUE = 1;
	public static final int ALLOW_MODIFY_USER_INFO_FALSE = 0;
	
	public static final int GUIDE_FLAG_NEED = 0;
	public static final int GUIDE_FLAG_NEEDLESS = 1;
	
	public static final int NEED_FORCE_UPDATE_ADDRESSBOOK_YES = 1;
	public static final int NEED_FORCE_UPDATE_ADDRESSBOOK_NO = 0;
	
	public static final int PUSH_VENDOR_TYPE_BAIDU = 1;
	public static final int PUSH_VENDOR_TYPE_JPUSH = 2;

	
	public static final int NEED = 1;
	public static final int DO_NOT_NEED = 0;
	
//	private String id;
	private String code;
	private String name;
	private String content;
	
	/**
	 * 启动次数,可以用来判断是否第一次启动，也可以用来记录使用频率，还可以用作当启动次数达到一定数量时做一些操作
	 */
	private int initTimes;
	@SerializedName("loginflag")
	private int loginFlag;//0==系统内登录 1==打开登录
	@SerializedName("webappcode")
	private String webAppCode;
	@SerializedName("loginpicurl")
	private String loginPicUrl;
	@SerializedName("startpicurl")
	private String startUpPic;//启动图
	/**
	 * 应用的样式主界面样式，可以是左右滑动菜单样式也可以是底部菜单样式等
	 */
	@SerializedName("layoutscheme")
	private int layoutScheme;
	
	private int layoutLocalScheme;
	/**
	 * 滑动菜单布局模式下的滑动菜单背景
	 */
	@SerializedName("bgpicurl")
	private String bgPicURL;
	
	/**
	 * 菜单更新时间(服务器)
	 */
	@SerializedName("menuupdatetime")
	private Date menuUpdateTime;
	/**
	 * 菜单上次更新时间(本地)
	 */
	private Date menuLocalUpdateTime;
	
	/**
	 * 频道更新时间
	 */
	@SerializedName("channelupdatetime")
	private Date channelUpdateTime;
	/**
	 * 频道上次更新时间
	 */
	private Date channelLocalUpdateTime;
	
	@SerializedName("baidupushapikey")
	private String baiduPushApiKey;
	
	private String baiduPushUserId;
	
	
	/**
	 * 是否允许注册
	 */
	@SerializedName("userregflag")
	private int allowRegister;
	
	@SerializedName("defauttheme")
	private int defaultTheme;
	
	@SerializedName("customthemecolor")
	private String customThemeColor;//系统定义默认颜色
	
	/**
	 * 服务器端菜单布局上次更新时间
	 */
	@SerializedName("menugroupupdatetime")
	private Date menuGroupUpdateTime;
	
	/**
	 * 本地的菜单上次更新时间
	 */
	private Date menuGroupLocalUpdateTime;
	
	private boolean isDownloadApp;//下载App
	
	@SerializedName("chatflag")
	private int allowChat;//是否允许聊天
	
	@SerializedName("mduserinfoflag")
	private int allModifyUserInfo;//是否允许客户端修改个人信息
	
	
	/**
	 * 通讯录相关
	 */
	@SerializedName("adbookupdateflag")
	private int needForceUploadAddressbook;//是否需要强制更新addressbook 0：否 1：是
	@SerializedName("adbookversion")
	private int addressbookVersion;//通讯录版本，默认0  
	private int addressbookLocalVersion;//通讯录本地版本，
	private String addressbookUserUrl;//通讯录用户url
	private String addressbookDeptUserUrl;//关联表url
	private String addressbookDetpUrl;//部门url
	private int addressbookNeedDecryption;//是否需要加密
	private int addressbookNeedPermission;//是否需要权限限制显示通讯录
	
	private String documentReaderPageUrl;//文档阅读器地址
	/**
	 * 程序最新版本
	 */
	@SerializedName("appandroidversion")
	private int latestVersion;
	private int preWorkingVersion;//上一个工作的版本
	@SerializedName("service_id")
	private String defaultServiceNoId;//默认的服务号id,通过此id来进行读取推送列表
	
	@SerializedName("weathershowflag")
	private int weatherDisplayFlag;
	
	@SerializedName("pushvendortype")
	private int pushVendorType;
	
	private String jpushRegistrationID;
	private String orgCode;
	@SerializedName("webloginurl")
	private String webLoginUrl;

	private String paddingUrlOnHomeActivityStartUp;

	public boolean isDownload() {
		return isDownloadApp;
	}

	public void setDownload(boolean isDownload) {
		this.isDownloadApp = isDownload;
	}
	
	public String getWebAppCode() {
		return webAppCode;
	}
	public void setWebAppCode(String webAppCode) {
		this.webAppCode = webAppCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getLoginFlag() {
		return loginFlag;
	}
	public void setLoginFlag(int loginFlag) {
		this.loginFlag = loginFlag;
	}
	public int getInitTimes() {
		return initTimes;
	}
	public void setStartupTimes(int startupTimes) {
		this.initTimes = startupTimes;
	}
//	@Override
//	public String getId() {
//		
//		return id;
//	}
//	@Override
//	public void setId(String id) {
//		this.id = id;
//	}
	public String getLoginPicUrl() {
		return loginPicUrl;
	}
	public void setLoginPicUrl(String loginPicUrl) {
		this.loginPicUrl = loginPicUrl;
	}
	public int getLayoutScheme() {
		return layoutScheme;
	}
	public void setLayoutScheme(int layoutScheme) {
		this.layoutScheme = layoutScheme;
	}
	
	public int getLayoutLocalScheme() {
		return layoutLocalScheme;
	}

	public void setLayoutLocalScheme(int layoutLocalScheme) {
		this.layoutLocalScheme = layoutLocalScheme;
	}

	public Date getMenuUpdateTime() {
		return menuUpdateTime;
	}
	public void setMenuUpdateTime(Date menuUpdateTime) {
		this.menuUpdateTime = menuUpdateTime;
	}
	public Date getMenuLocalUpdateTime() {
		return menuLocalUpdateTime;
	}
	public void setMenuLocalUpdateTime(Date menuLastUpdateTime) {
		this.menuLocalUpdateTime = menuLastUpdateTime;
	}
	public Date getChannelUpdateTime() {
		return channelUpdateTime;
	}
	public void setChannelupdatetime(Date channelupdatetime) {
		this.channelUpdateTime = channelupdatetime;
	}
	public Date getChannelLocalUpdateTime() {
		return channelLocalUpdateTime;
	}
	public void setChannelLocalUpdateTime(Date channelLastUpdateTime) {
		this.channelLocalUpdateTime = channelLastUpdateTime;
	}
	public String getStartUpPic() {
		return startUpPic;
	}
	public void setStartUpPic(String startUpPic) {
		this.startUpPic = startUpPic;
	}
	
	public String getBgPicURL() {
		return bgPicURL;
	}
	public void setBgPicURL(String bgPicURL) {
		this.bgPicURL = bgPicURL;
	}
	
	public String getBaiduPushApiKey() {
		return "gXTQ44ISYRpdRorvo84c9N7j";
//		return baiduPushApiKey;
	}
	public void setBaiduPushApiKey(String baiduPushApiKey) {
		this.baiduPushApiKey = baiduPushApiKey;
	}
	public int getAllowRegister() {
		return allowRegister;
	}
	public void setAllowRegister(int allowRegister) {
		this.allowRegister = allowRegister;
	}
	public int getDefaultTheme() {
		return defaultTheme;
	}
	public void setDefaultTheme(int defaultTheme) {
		this.defaultTheme = defaultTheme;
	}
	
	public String getCustomThemeColor() {
		return customThemeColor;
	}

	public void setCustomThemeColor(String customThemeColor) {
		this.customThemeColor = customThemeColor;
	}

	public int getLatestVersion() {
		return latestVersion;
	}
	public void setLatestVersion(int latestVersion) {
		this.latestVersion = latestVersion;
	}

	public Date getMenuGroupUpdateTime() {
		return menuGroupUpdateTime;
	}

	public void setMenuGroupUpdateTime(Date menuGroupUpdateTime) {
		this.menuGroupUpdateTime = menuGroupUpdateTime;
	}

	public Date getMenuGroupLocalUpdateTime() {
		return menuGroupLocalUpdateTime;
	}

	public void setMenuGroupLocalUpdateTime(Date menuGroupLocalUpdateTime) {
		this.menuGroupLocalUpdateTime = menuGroupLocalUpdateTime;
	}

	public String getBaiduPushUserId() {
		return baiduPushUserId;
	}

	public void setBaiduPushUserId(String baiduPushUserId) {
		this.baiduPushUserId = baiduPushUserId;
	}

	public int getAllowChat() {
		return allowChat;
	}

	public void setAllowChat(int allowChat) {
		this.allowChat = allowChat;
	}

	public int getAllModifyUserInfo() {
		return allModifyUserInfo;
	}

	public void setAllModifyUserInfo(int allModifyUserInfo) {
		this.allModifyUserInfo = allModifyUserInfo;
	}


	public int getNeedForceUploadAddressbook() {
		return needForceUploadAddressbook;
	}

	public void setNeedForceUploadAddressbook(int needForceUploadAddressbook) {
		this.needForceUploadAddressbook = needForceUploadAddressbook;
	}

	public int getAddressbookVersion() {
		return addressbookVersion;
	}

	public void setAddressbookVersion(int addressbookVersion) {
		this.addressbookVersion = addressbookVersion;
	}

	public int getAddressbookLocalVersion() {
		return addressbookLocalVersion;
	}

	public void setAddressbookLocalVersion(int addressbookLocalVersion) {
		this.addressbookLocalVersion = addressbookLocalVersion;
	}

	public String getAddressbookUserUrl() {
		return addressbookUserUrl;
	}

	public void setAddressbookUserUrl(String addressbookUserUrl) {
		this.addressbookUserUrl = addressbookUserUrl;
	}

	public String getAddressbookDeptUserUrl() {
		return addressbookDeptUserUrl;
	}

	public void setAddressbookDeptUserUrl(String addressbookDeptUserUrl) {
		this.addressbookDeptUserUrl = addressbookDeptUserUrl;
	}

	public String getAddressbookDetpUrl() {
		return addressbookDetpUrl;
	}

	public void setAddressbookDetpUrl(String addressbookDetpUrl) {
		this.addressbookDetpUrl = addressbookDetpUrl;
	}

	public int getAddressbookNeedDecryption() {
		return addressbookNeedDecryption;
	}

	public void setAddressbookNeedDecryption(int addressbookNeedDecryption) {
		this.addressbookNeedDecryption = addressbookNeedDecryption;
	}

	public int getAddressbookNeedPermission() {
		return addressbookNeedPermission;
	}

	public void setAddressbookNeedPermission(int addressbookNeedPermission) {
		this.addressbookNeedPermission = addressbookNeedPermission;
	}

	public int getPreWorkingVersion() {
		return preWorkingVersion;
	}

	public void setPreWorkingVersion(int preWorkingVersion) {
		this.preWorkingVersion = preWorkingVersion;
	}

	public String getDefaultServiceNoId() {
		return defaultServiceNoId;
	}

	public void setDefaultServiceNoId(String defaultServiceNoId) {
		this.defaultServiceNoId = defaultServiceNoId;
	}

	public int getWeatherDisplayFlag() {
		return weatherDisplayFlag;
	}

	public void setWeatherDisplayFlag(int weatherDisplayFlag) {
		this.weatherDisplayFlag = weatherDisplayFlag;
	}

	public int getPushVendorType() {
		return pushVendorType;
	}

	public void setPushVendorType(int pushVendorType) {
		this.pushVendorType = pushVendorType;
	}

	public String getJpushRegistrationID() {
		return jpushRegistrationID;
	}

	public void setJpushRegistrationID(String jpushRegistrationID) {
		this.jpushRegistrationID = jpushRegistrationID;
	}
	
	
	public String getDocumentReaderPageUrl() {
		return documentReaderPageUrl;
	}

	public void setDocumentReaderPageUrl(String documentReaderPageUrl) {
		this.documentReaderPageUrl = documentReaderPageUrl;
	}

	public String getPushToken(){
		if(pushVendorType==PUSH_VENDOR_TYPE_BAIDU){
			return baiduPushUserId;
		}else{
			return jpushRegistrationID;
		}
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getWebLoginUrl() {
		return webLoginUrl;
	}

	public void setWebLoginUrl(String webLoginUrl) {
		this.webLoginUrl = webLoginUrl;
	}

	public String getPaddingUrlOnHomeActivityStartUp() {
		return paddingUrlOnHomeActivityStartUp;
	}

	public void setPaddingUrlOnHomeActivityStartUp(String paddingUrlOnHomeActivityStartUp) {
		this.paddingUrlOnHomeActivityStartUp = paddingUrlOnHomeActivityStartUp;
	}

}
