package com.apppubs.bean.http;

public class LoginResult implements IJsonResult {

    private String userId;
    private String token;
    private String rongToken;
    private String username;
    private String CNName;
    private String email;
    private String mobile;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setRongToken(String rongToken) {
        this.rongToken = rongToken;
    }

    public String getRongToken() {
        return rongToken;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setCNName(String CNName) {
        this.CNName = CNName;
    }

    public String getCNName() {
        return CNName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

}
