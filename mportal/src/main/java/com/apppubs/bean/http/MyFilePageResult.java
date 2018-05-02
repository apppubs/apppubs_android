package com.apppubs.bean.http;

import java.util.Date;
import java.util.List;

public class MyFilePageResult implements IJsonResult{
    private int totalNum;
    private List<MyFilePageItem> items;

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setItems(List<MyFilePageItem> items) {
        this.items = items;
    }

    public List<MyFilePageItem> getItems() {
        return items;
    }

    public class MyFilePageItem {
        private String id;
        private Date createTime;
        private String name;
        private String URL;
        private long size;
        private String fileType;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setURL(String URL) {
            this.URL = URL;
        }

        public String getURL() {
            return URL;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getFileType() {
            return fileType;
        }
    }
}
