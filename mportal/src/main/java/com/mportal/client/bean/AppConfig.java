package com.mportal.client.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by zhangwen on 2017/1/5.
 */

public class AppConfig implements Serializable{



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
    @SerializedName("android_updateurl")
    private String updateUrl;

    public AppConfig(){
        adbookRootId = "0";
        adbookVersion = 0;
        adbookOrgCountFlag = "1";
        adbookUserIconFlags = "";
        adbookAccountPWDFlags = "";
        pdfEditableFlag = "0";
        regURL = "";
        forgetPasswordUrl = "";
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
}
