package com.apppubs.vpn;

public class VPNInfoWithPwd{
    private String vpnId;
    private String vpnName;
    private String vpnURL;
    private String username;
    private String pwd;

    public String getVpnId() {
        return vpnId;
    }

    public void setVpnId(String vpnId) {
        this.vpnId = vpnId;
    }

    public String getVpnName() {
        return vpnName;
    }

    public void setVpnName(String vpnName) {
        this.vpnName = vpnName;
    }

    public String getVpnURL() {
        return vpnURL;
    }

    public void setVpnURL(String vpnURL) {
        this.vpnURL = vpnURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}