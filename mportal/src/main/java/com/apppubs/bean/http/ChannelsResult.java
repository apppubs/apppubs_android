package com.apppubs.bean.http;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

public class ChannelsResult implements IJsonResult {

    private List<Item> items;

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public class Item {

        private String channelCode;
        private String channelName;
        private int displayOrder;
        private int showType;
        private int picNum;
        private String picURL;
        private Date lastUpdateTime;
        private String linkURL;

        public void setChannelCode(String channelCode) {
            this.channelCode = channelCode;
        }

        public String getChannelCode() {
            return channelCode;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setDisplayOrder(int displayOrder) {
            this.displayOrder = displayOrder;
        }

        public int getDisplayOrder() {
            return displayOrder;
        }

        public void setShowType(int showType) {
            this.showType = showType;
        }

        public int getShowType() {
            return showType;
        }

        public void setPicNum(int picNum) {
            this.picNum = picNum;
        }

        public int getPicNum() {
            return picNum;
        }

        public void setPicURL(String picURL) {
            this.picURL = picURL;
        }

        public String getPicURL() {
            return picURL;
        }

        public void setLastUpdateTime(Date lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }

        public Date getLastUpdateTime() {
            return lastUpdateTime;
        }

        public void setLinkURL(String linkURL) {
            this.linkURL = linkURL;
        }

        public String getLinkURL() {
            return linkURL;
        }

    }
}
