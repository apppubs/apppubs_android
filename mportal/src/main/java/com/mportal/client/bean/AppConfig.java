package com.mportal.client.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhangwen on 2017/1/5.
 */

public class AppConfig {

    public static final String APP_CONFIG_PARAM_ADBOOK_ROOT_ID = "adbook_rootid";
    public static final String APP_CONFIG_PARAM_ADBOOK_VERSION = "adbookversion";
    public static final String APP_CONFIG_PARAM_ORG_SHOW_COUNT_FLAG = "adbook_org_count_flag";
    public static final String APP_CONFIG_PARAM_USER_ICON_FLAGS = "adbook_user_icon_flags";
    public static final String APP_CONFIG_PARAM_USER_CENTER_PWD_FLAGS = "user_account_pwd_flags";
    public static final String APP_CONFIG_PARAM_PDF_EDITABLE = "pdf_editable";
    public static final String APP_CONFIG_PARAM_REG_URL = "reg_url";
    public static final String APP_CONFIG_PARAM_FORGET_PWD_URL = "forget_password_url";

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
}
