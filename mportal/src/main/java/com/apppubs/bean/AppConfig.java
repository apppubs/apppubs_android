package com.apppubs.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhangwen on 2017/1/5.
 */

public class AppConfig implements Serializable {

    @SerializedName("adbook_rootid")
    private String adbookRootId;
    @SerializedName("adbookversion")
    private int adbookVersion;
    @SerializedName("adbook_org_count_flag")
    private String adbookOrgCountFlag;
    @SerializedName("adbook_user_icon_flags")
    private String adbookUserIconFlags;
    @SerializedName("user_account_pwd_flags")
    private String adbookAccountPWDFlags;
    @SerializedName("pdf_editable")
    private String pdfEditableFlag;
    @SerializedName("reg_url")
    private String regURL;
    @SerializedName("forget_password_url")
    private String forgetPasswordUrl;
    @SerializedName("android_version")
    private String latestVersion;
    @SerializedName("android_version_describe")
    private String latestVersionDescribe;
    @SerializedName("android_min_app_code_version")
    private int minSupportedVersionCode;
    @SerializedName("android_download_url")
    private String updateUrl;
    @SerializedName("android_version_alert")
    private Integer needVersionAlertFlag;
    @SerializedName("chat_flag")
    private String chatFlag;
    @SerializedName("adbookauth")
    private Integer adbookAuthFlag;
    @SerializedName("chat_auth")
    private Integer chatAuthFlag;
    @SerializedName("about_properties")
    private String aboutProperties;

    public AppConfig() {
        adbookRootId = "0";
        adbookVersion = 0;
        adbookOrgCountFlag = "1";
        adbookUserIconFlags = "";
        adbookAccountPWDFlags = "";
        pdfEditableFlag = "0";
        regURL = "";
        forgetPasswordUrl = "";
        chatFlag = "0";
        adbookAuthFlag = 0;
        chatAuthFlag = 0;
    }

    public String getAdbookRootId() {
        return adbookRootId;
    }

    public void setAdbookRootId(String adbookRootId) {
        this.adbookRootId = adbookRootId;
    }

    public int getAdbookVersion() {
        return adbookVersion;
    }

    public void setAdbookVersion(int adbookVersion) {
        this.adbookVersion = adbookVersion;
    }

    public String getAdbookOrgCountFlag() {
        return adbookOrgCountFlag;
    }

    public void setAdbookOrgCountFlag(String adbookOrgCountFlag) {
        this.adbookOrgCountFlag = adbookOrgCountFlag;
    }

    public String getAdbookUserIconFlags() {
        return adbookUserIconFlags;
    }

    public void setAdbookUserIconFlags(String adbookUserIconFlags) {
        this.adbookUserIconFlags = adbookUserIconFlags;
    }

    public String getAdbookAccountPWDFlags() {
        return adbookAccountPWDFlags;
    }

    public void setAdbookAccountPWDFlags(String adbookAccountPWDFlags) {
        this.adbookAccountPWDFlags = adbookAccountPWDFlags;
    }

    public String getPdfEditableFlag() {
        return pdfEditableFlag;
    }

    public void setPdfEditableFlag(String pdfEditableFlag) {
        this.pdfEditableFlag = pdfEditableFlag;
    }

    public String getRegURL() {
        return regURL;
    }

    public void setRegURL(String regURL) {
        this.regURL = regURL;
    }

    public String getForgetPasswordUrl() {
        return forgetPasswordUrl;
    }

    public void setForgetPasswordUrl(String forgetPasswordUrl) {
        this.forgetPasswordUrl = forgetPasswordUrl;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getLatestVersionDescribe() {
        return latestVersionDescribe;
    }

    public void setLatestVersionDescribe(String latestVersionDescribe) {
        this.latestVersionDescribe = latestVersionDescribe;
    }

    public int getMinSupportedVersionCode() {
        return minSupportedVersionCode;
    }

    public void setMinSupportedVersionCode(int minSupportedVersionCode) {
        this.minSupportedVersionCode = minSupportedVersionCode;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public Integer getNeedVersionAlertFlag() {
        return needVersionAlertFlag;
    }

    public void setNeedVersionAlertFlag(Integer needVersionAlertFlag) {
        this.needVersionAlertFlag = needVersionAlertFlag;
    }

    public String getChatFlag() {
        return chatFlag;
    }

    public void setChatFlag(String chatFlag) {
        this.chatFlag = chatFlag;
    }

    public Integer getAdbookAuthFlag() {
        return adbookAuthFlag;
    }

    public void setAdbookAuthFlag(Integer adbookAuthFlag) {
        this.adbookAuthFlag = adbookAuthFlag;
    }

    public Integer getChatAuthFlag() {
        return chatAuthFlag;
    }

    public void setChatAuthFlag(Integer chatAuthFlag) {

        this.chatAuthFlag = chatAuthFlag;
    }

    public String getAboutProperties() {
        return aboutProperties;
    }

    public void setAboutProperties(String aboutProperties) {
        this.aboutProperties = aboutProperties;
    }
}
