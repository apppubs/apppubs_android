package com.apppubs.bean.http;

import java.util.List;

public class MenusResult implements IJsonResult{
    private List<MenuItem> items;

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }

    public class MenuItem {

        private String id;
        private String superId;
        private String name;
        private String appURL;
        private String iconPic;
        private int sortId;
        private int appType;
        private String channelTypeId;
        private int channelLayout;
        private int openType;
        private int webViewStyle;
        private int channelConfigFlag;
        private int loginPowerFlag;
        private String webViewMenus;
        private String badgeURL;
        private boolean isAllowCustomIp;
        private boolean needTitleBar;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setSuperId(String superId) {
            this.superId = superId;
        }

        public String getSuperId() {
            return superId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setAppURL(String appURL) {
            this.appURL = appURL;
        }

        public String getAppURL() {
            return appURL;
        }

        public void setIconPic(String iconPic) {
            this.iconPic = iconPic;
        }

        public String getIconPic() {
            return iconPic;
        }

        public void setSortId(int sortId) {
            this.sortId = sortId;
        }

        public int getSortId() {
            return sortId;
        }

        public void setAppType(int appType) {
            this.appType = appType;
        }

        public int getAppType() {
            return appType;
        }

        public void setChannelTypeId(String channelTypeId) {
            this.channelTypeId = channelTypeId;
        }

        public String getChannelTypeId() {
            return channelTypeId;
        }

        public void setChannelLayout(int channelLayout) {
            this.channelLayout = channelLayout;
        }

        public int getChannelLayout() {
            return channelLayout;
        }

        public void setOpenType(int openType) {
            this.openType = openType;
        }

        public int getOpenType() {
            return openType;
        }

        public void setWebViewStyle(int webViewStyle) {
            this.webViewStyle = webViewStyle;
        }

        public int getWebViewStyle() {
            return webViewStyle;
        }

        public void setChannelConfigFlag(int channelConfigFlag) {
            this.channelConfigFlag = channelConfigFlag;
        }

        public int getChannelConfigFlag() {
            return channelConfigFlag;
        }

        public void setLoginPowerFlag(int loginPowerFlag) {
            this.loginPowerFlag = loginPowerFlag;
        }

        public int getLoginPowerFlag() {
            return loginPowerFlag;
        }

        public void setWebViewMenus(String webViewMenus) {
            this.webViewMenus = webViewMenus;
        }

        public String getWebViewMenus() {
            return webViewMenus;
        }

        public void setBadgeURL(String badgeURL) {
            this.badgeURL = badgeURL;
        }

        public String getBadgeURL() {
            return badgeURL;
        }

        public void setIsAllowCustomIp(boolean isAllowCustomIp) {
            this.isAllowCustomIp = isAllowCustomIp;
        }

        public boolean getIsAllowCustomIp() {
            return isAllowCustomIp;
        }

        public void setNeedTitleBar(boolean needTitleBar) {
            this.needTitleBar = needTitleBar;
        }

        public boolean getNeedTitleBar() {
            return needTitleBar;
        }
    }

}

