package com.apppubs.bean.http;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by siger on 2018/4/20.
 */

public class AppInfoResult implements IJsonResult{

    private String appId;
    private String name;
    private int loginFlag;
    private String webLoginURL;
    private int userRegFlag;
    private int layoutSchema;
    private int defaultTheme;
    private String themeColor;
    private Date menuUpdateTime;
    private String startupPicURL;
    private String loginPicURL;
    private String androidVersion;
    private String iosVersion;
    private int chatFlag;
    private List<ConfigItem> configs;

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLoginFlag(int loginFlag) {
        this.loginFlag = loginFlag;
    }

    public int getLoginFlag() {
        return loginFlag;
    }

    public void setWebLoginURL(String webLoginURL) {
        this.webLoginURL = webLoginURL;
    }

    public String getWebLoginURL() {
        return webLoginURL;
    }

    public void setUserRegFlag(int userRegFlag) {
        this.userRegFlag = userRegFlag;
    }

    public int getUserRegFlag() {
        return userRegFlag;
    }

    public void setLayoutSchema(int layoutSchema) {
        this.layoutSchema = layoutSchema;
    }

    public int getLayoutSchema() {
        return layoutSchema;
    }

    public void setDefaultTheme(int defaultTheme) {
        this.defaultTheme = defaultTheme;
    }

    public int getDefaultTheme() {
        return defaultTheme;
    }

    public void setThemeColor(String themeColor) {
        this.themeColor = themeColor;
    }

    public String getThemeColor() {
        return themeColor;
    }

    public void setMenuUpdateTime(Date menuUpdateTime) {
        this.menuUpdateTime = menuUpdateTime;
    }

    public Date getMenuUpdateTime() {
        return menuUpdateTime;
    }

    public void setStartupPicURL(String startupPicURL) {
        this.startupPicURL = startupPicURL;
    }

    public String getStartupPicURL() {
        return startupPicURL;
    }

    public void setLoginPicURL(String loginPicURL) {
        this.loginPicURL = loginPicURL;
    }

    public String getLoginPicURL() {
        return loginPicURL;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setIosVersion(String iosVersion) {
        this.iosVersion = iosVersion;
    }

    public String getIosVersion() {
        return iosVersion;
    }

    public void setChatFlag(int chatFlag) {
        this.chatFlag = chatFlag;
    }

    public int getChatFlag() {
        return chatFlag;
    }

    public List<ConfigItem> getConfigs() {
        return configs;
    }

    public void setConfigs(List<ConfigItem> configs) {
        this.configs = configs;
    }

    public class ConfigItem{
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
