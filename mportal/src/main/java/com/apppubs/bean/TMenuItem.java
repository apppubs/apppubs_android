package com.apppubs.bean;

import com.apppubs.bean.http.MenusResult;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Table;

/**
 * 菜单
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 
 * 2015年3月5日 by zhangwen 修改
 * 2015-05-10 zhangwen 增加 webAppMenus
 * 
 */
@Table(name = "menu_item")
public class TMenuItem extends SugarRecord {

	public static final int MENU_LOCATION_PRIMARY = 0;// 菜单位置，主菜单
	public static final int MENU_LOCATION_SECONDARY = 1;// 菜单位置，次菜单
	public static final int MENU_LOCATION_SUB = 2;// 菜单位置，子菜单

	public static final int OPEN_TYPE_HOME = 1;// 主界面打开
	public static final int OPEN_TYPE_NEW = 0;// 新建窗口

	public static final int CHANNEL_LAYOUT_SLIDE = 0;// 资讯频道布局横向滑动
	public static final int CHANNEL_LAYOUT_ZAKER = 1;// 资讯频道展示grid

	public static final int SUBMENU_LAYOUT_GRID = 0;// 子菜单布局
	public static final int SUBMENU_LAYOUT_LIST = 2;// list形式的列表
	
	public static final int PROTECTED_FLAG_TRUE = 1;//此单受保护
	public static final int PROTECTED_FLAG_FALSE = 0;//菜单不受保护
	
	public static final int TITLEBAR_SHOW_FLAG_TRUE = 1;
	public static final int TITLEBAR_SHOW_FLAG_FALSE = 0;
	
	public static final int WEB_APP_MENU_REFRESH = 0;
	public static final int WEB_APP_MENU_OPEN_WITH_BROWSER = 1;
	public static final int WEB_APP_MENU_SHARE = 2;
	
	public static final int YES = 1;
	
	public static final String MENU_URL_NEWSPAPER = "app:{$newspaper}";
	public static final String MENU_URL_NEWS = "app:{$news}";
	public static final String MENU_URL_BAOLIAO = "app:{$baol}";
	public static final String MENU_URL_WEIBO = "app:{$weibo}";
	public static final String MENU_URL_HISTORY_MESSAGE ="app:{$history_message}";
	public static final String MENU_URL_MESSAGE ="app:{$message}";
	public static final String MENU_URL_PIC ="app:{$pic}";
	public static final String MENU_URL_VIDEO ="app:{$mov}";
	public static final String MENU_URL_FAVORITE ="app:{$favorite}";
	public static final String MENU_URL_MENU ="app:{$menu}";
	public static final String MENU_URL_ADDRESSBOOK ="app:{$addressbook}";
	public static final String MENU_URL_SETTING ="app:{$setting}";
	public static final String MENU_URL_EMAIL ="app:{$email}";
	public static final String MENU_URL_USER_ACCOUNT ="app:{$user_account}";
	public static final String MENU_URL_LOGOUT ="app:{$logout}";
	public static final String MENU_URL_MY_FILE = "app:{$my_file}";
	
	
	
	@SerializedName("id")
	private String id;
	@SerializedName("name")
	private String name;
	@SerializedName("iconpic")
	private String iconpic;// 图标
	@SerializedName("sortid")
	private int sortId;// 排序数
	@SerializedName("apptype")
	private int location;// 菜单摆放位置0==左边，1==右边
	private int openType;// 0：默认打开，1，新窗口打开
	@SerializedName("appurl")
	private String url;
	@SerializedName("channeltypeid")
	private String channelTypeId;// 如果是新闻才有的频道类型id,如果是滚动图就是组图id，如果通讯录的话就是通讯录首节点的superid
	private String isEnable;// 是否启用 0,1
	@SerializedName("webviewstyle")
	private int menuBarType;// 工具条类型 0==全工具条，1==只有返回，2==空
	@SerializedName("channellayout")
	private int channelLayout;// 0==默认布局（资讯为左右滑动布局），1==布局1(资讯为zaker主页布局) 2==布局2
	@SerializedName("webviewmenus")
	private String webAppMenus;//webapp的菜单，用『，』隔开，0.刷新，1.从浏览器打开，2.分享 "0,2"
	@SerializedName("titlebarshowflag")
	private int titleBarShowFlag;//titlebar是否现实
	/**
	 * 是否允许配置
	 */
	@SerializedName("channelconfigflag")
	private int allowConfigFlag;

	/**
	 * 是否需要权限 1需要0不需要
	 */
	@SerializedName("loginpowerflag")
	private int protectedFlag;

	@SerializedName("superid")
	private String superId;
	@SerializedName("badgeurl")
	private String badgeURL;
	
	private transient int badgeNum;//徽章数字
	
	@SerializedName("isallowcustomip")
	private int isAllowCustomIp;//是否允许配置协议和ip
	
	public TMenuItem() {

	}

	public TMenuItem(String name, String iconpic, String url, int sortid, int location, int openType, int menuBartype) {
		this.name = name;
		this.iconpic = iconpic;
		this.sortId = sortid;
		this.url = url;
		this.location = location;
		this.openType = openType;
		this.menuBarType = menuBartype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconpic() {
		return iconpic;
	}

	public void setIconpic(String iconpic) {
		this.iconpic = iconpic;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortid) {
		this.sortId = sortid;
	}

	public String getChannelTypeId() {
		return channelTypeId;
	}

	public void setChannelTypeId(String channelTypeId) {
		this.channelTypeId = channelTypeId;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public int getOpenType() {
		return openType;
	}

	public void setOpenType(int openType) {
		this.openType = openType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(String isEnable) {
		this.isEnable = isEnable;
	}

	public int getMenuBarType() {
		return menuBarType;
	}

	public void setMenuBarType(int menuBarType) {
		this.menuBarType = menuBarType;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public int getChannelLayout() {
		return channelLayout;
	}

	public void setChannelLayout(int channelLayout) {
		this.channelLayout = channelLayout;
	}

	public int getAllowConfigFlag() {
		return allowConfigFlag;
	}

	public void setAllowConfigFlag(int allowConfigFlag) {
		this.allowConfigFlag = allowConfigFlag;
	}

	@Override
	public String toString() {
		return "TMenuItem [id=" + id + ", name=" + name + ", iconpic=" + iconpic + ", sortid=" + sortId + ", location=" + location
				+ ", openType=" + openType + ", url=" + url + ", channelTypeId=" + channelTypeId + ", isEnable=" + isEnable
				+ ", menuBarType=" + menuBarType + ", channelLayout=" + channelLayout + ", allowConfigFlag=" + allowConfigFlag
				+ "]";
	}


	public int getProtectedFlag() {
		return protectedFlag;
	}

	public void setProtectedFlag(int protectedFlag) {
		this.protectedFlag = protectedFlag;
	}

	public String getSuperId() {
		return superId;
	}

	public void setSuperId(String superId) {
		this.superId = superId;
	}

	public String getWebAppMenus() {
		return webAppMenus;
	}

	public void setWebAppMenus(String webAppMenus) {
		this.webAppMenus = webAppMenus;
	}

	public String getBadgeURL() {
		return badgeURL;
	}

	public void setBadgeURL(String badgeURL) {
		this.badgeURL = badgeURL;
	}
	
	
	public int getBadgeNum() {
		return badgeNum;
	}

	public void setBadgeNum(int badgeNum) {
		this.badgeNum = badgeNum;
	}

	public int isAllowCustomIp() {
		return isAllowCustomIp;
	}

	public void setAllowCustomIp(int isAllowCustomIp) {
		this.isAllowCustomIp = isAllowCustomIp;
	}

	public int getTitleBarShowFlag() {
		return titleBarShowFlag;
	}

	public void setTitleBarShowFlag(int titleBarShowFlag) {
		this.titleBarShowFlag = titleBarShowFlag;
	}

	public static TMenuItem createFrom(MenusResult.MenuItem menuItem){
		TMenuItem item = new TMenuItem();
		item.setId(menuItem.getId());
		item.setSuperId(menuItem.getSuperId());
		item.setUrl(menuItem.getAppURL());
		item.setIconpic(menuItem.getIconPic());
		item.setSortId(menuItem.getSortId());
		item.setLocation(menuItem.getAppType());
		item.setChannelTypeId(menuItem.getChannelTypeId());
		item.setChannelLayout(menuItem.getChannelLayout());
		item.setOpenType(menuItem.getOpenType());
		item.setMenuBarType(menuItem.getWebViewStyle());
		item.setAllowConfigFlag(menuItem.getChannelConfigFlag());
		item.setProtectedFlag(menuItem.getLoginPowerFlag());
		item.setBadgeURL(menuItem.getBadgeURL());
		item.setWebAppMenus(menuItem.getWebViewMenus());
		item.setAllowCustomIp(menuItem.getIsAllowCustomIp()?1:0);
		item.setTitleBarShowFlag(menuItem.getNeedTitleBar()?1:0);
		return item;
	}
	
}
