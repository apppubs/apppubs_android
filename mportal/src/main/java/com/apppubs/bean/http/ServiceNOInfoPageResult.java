package com.apppubs.bean.http;

import java.util.Date;
import java.util.List;

public class ServiceNOInfoPageResult implements IJsonResult{

    private int totalNum;
    private List<Items> items;
    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }
    public int getTotalNum() {
        return totalNum;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }
    public List<Items> getItems() {
        return items;
    }

    public class Items {

        private String articleId;
        private String title;
        private String picURL;
        private String summary;
        private Date createTime;
        private int servicenoType;
        private String linkURL;
        private int flag;
        public void setArticleId(String articleId) {
            this.articleId = articleId;
        }
        public String getArticleId() {
            return articleId;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

        public void setPicURL(String picURL) {
            this.picURL = picURL;
        }
        public String getPicURL() {
            return picURL;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
        public String getSummary() {
            return summary;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }
        public Date getCreateTime() {
            return createTime;
        }

        public void setServicenoType(int servicenoType) {
            this.servicenoType = servicenoType;
        }
        public int getServicenoType() {
            return servicenoType;
        }

        public void setLinkURL(String linkURL) {
            this.linkURL = linkURL;
        }
        public String getLinkURL() {
            return linkURL;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }
        public int getFlag() {
            return flag;
        }

    }
}
