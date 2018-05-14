package com.apppubs.bean.http;

import java.util.List;

public class ChannelResult implements IJsonResult {
    private String channelCode;

    private String channelName;

    private String picURL;

    private String lastUpdateTime;

    private String linkURL;

    private List<Headers> headers;

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getChannelCode() {
        return this.channelCode;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public String getPicURL() {
        return this.picURL;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public String getLinkURL() {
        return this.linkURL;
    }

    public void setHeaders(List<Headers> headers) {
        this.headers = headers;
    }

    public List<Headers> getHeaders() {
        return this.headers;
    }

    public class Headers {
        private String infoId;

        private String URL;

        private String topic;

        private String picURL;

        private int sortId;

        private int indexFlag;

        private int openFlag;

        private int comment;

        public void setInfoId(String infoId) {
            this.infoId = infoId;
        }

        public String getInfoId() {
            return this.infoId;
        }

        public void setURL(String URL) {
            this.URL = URL;
        }

        public String getURL() {
            return this.URL;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getTopic() {
            return this.topic;
        }

        public void setPicURL(String picURL) {
            this.picURL = picURL;
        }

        public String getPicURL() {
            return this.picURL;
        }

        public void setSortId(int sortId) {
            this.sortId = sortId;
        }

        public int getSortId() {
            return this.sortId;
        }

        public void setIndexFlag(int indexFlag) {
            this.indexFlag = indexFlag;
        }

        public int getIndexFlag() {
            return this.indexFlag;
        }

        public void setOpenFlag(int openFlag) {
            this.openFlag = openFlag;
        }

        public int getOpenFlag() {
            return this.openFlag;
        }

        public void setComment(int comment) {
            this.comment = comment;
        }

        public int getComment() {
            return this.comment;
        }

    }
}
