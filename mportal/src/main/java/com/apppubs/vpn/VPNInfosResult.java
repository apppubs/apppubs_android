package com.apppubs.vpn;

import com.apppubs.bean.http.IJsonResult;

import java.util.List;

public class VPNInfosResult implements IJsonResult {
    private List<VPNInfoItem> items;
    private String readmeURL;

    public List<VPNInfoItem> getItems() {
        return items;
    }

    public void setItems(List<VPNInfoItem> items) {
        this.items = items;
    }

    public String getReadmeURL() {
        return readmeURL;
    }

    public void setReadmeURL(String readmeURL) {
        this.readmeURL = readmeURL;
    }

    public class VPNInfoItem {
        private String vpnURL;
        private String vpnId;
        private String name;

        public String getVpnURL() {
            return vpnURL;
        }

        public void setVpnURL(String vpnURL) {
            this.vpnURL = vpnURL;
        }

        public String getVpnId() {
            return vpnId;
        }

        public void setVpnId(String vpnId) {
            this.vpnId = vpnId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
