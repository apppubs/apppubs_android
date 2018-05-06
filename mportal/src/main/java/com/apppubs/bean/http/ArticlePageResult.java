package com.apppubs.bean.http;

import java.util.Date;
import java.util.List;

public class ArticlePageResult implements IJsonResult{
    private int totalNum;
    private List<Item> items;
    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }
    public int getTotalNum() {
        return totalNum;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
    public List<Item> getItems() {
        return items;
    }

    public class Item {

        private String infoId;
        private String URL;
        private String topic;
        private Date pubTime;
        private String summary;
        private String picURL;
        private int comment;
        private String contentType;
        private String channelCode;
        private String tag;
        public void setInfoId(String infoId) {
            this.infoId = infoId;
        }
        public String getInfoId() {
            return infoId;
        }

        public void setURL(String URL) {
            this.URL = URL;
        }
        public String getURL() {
            return URL;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }
        public String getTopic() {
            return topic;
        }

        public void setPubTime(Date pubTime) {
            this.pubTime = pubTime;
        }
        public Date getPubTime() {
            return pubTime;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
        public String getSummary() {
            return summary;
        }

        public void setPicURL(String picURL) {
            this.picURL = picURL;
        }
        public String getPicURL() {
            return picURL;
        }

        public void setComment(int comment) {
            this.comment = comment;
        }
        public int getComment() {
            return comment;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
        public String getContentType() {
            return contentType;
        }

        public void setChannelCode(String channelCode) {
            this.channelCode = channelCode;
        }
        public String getChannelCode() {
            return channelCode;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }

    }
}
