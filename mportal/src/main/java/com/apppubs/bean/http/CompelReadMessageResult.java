package com.apppubs.bean.http;

import java.util.List;

public class CompelReadMessageResult implements IJsonResult {

    private List<Item> items;
    public void setItems(List<Item> items) {
        this.items = items;
    }
    public List<Item> getItems() {
        return items;
    }

    public class Item {

        private String content;
        private String serviceArticleId;
        public void setContent(String content) {
            this.content = content;
        }
        public String getContent() {
            return content;
        }

        public void setServiceArticleId(String serviceArticleId) {
            this.serviceArticleId = serviceArticleId;
        }
        public String getServiceArticleId() {
            return serviceArticleId;
        }

    }
}
