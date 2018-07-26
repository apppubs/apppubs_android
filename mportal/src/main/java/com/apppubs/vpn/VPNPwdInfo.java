package com.apppubs.vpn;

import java.io.Serializable;

public class VPNPwdInfo implements Serializable{
    private String vpnId;
    private String vpnURL;
    private String username;
    private String pwd;

    public String getVpnId() {
        return vpnId;
    }

    public void setVpnId(String vpnId) {
        this.vpnId = vpnId;
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
