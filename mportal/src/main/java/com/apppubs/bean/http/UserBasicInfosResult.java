package com.apppubs.bean.http;

import java.io.Serializable;
import java.util.List;

public class UserBasicInfosResult implements IJsonResult,Serializable {

    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item implements Serializable{
        private String userId;
        private String truename;
        private String avatarURL;
        private long appVersionCode;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getTruename() {
            return truename;
        }

        public void setTruename(String truename) {
            this.truename = truename;
        }

        public String getAvatarURL() {
            return avatarURL;
        }

        public void setAvatarURL(String avatarURL) {
            this.avatarURL = avatarURL;
        }

        public long getAppVersionCode() {
            return appVersionCode;
        }

        public void setAppVersionCode(long appVersionCode) {
            this.appVersionCode = appVersionCode;
        }
    }
}
