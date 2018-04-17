package com.apppubs.bean.page;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siger on 2018/4/17.
 */
public class TitleBarAddressModel extends TitleBarModel {

    private String rootCode;
    private String defaultAddress;
    private String defaultAddressCode;
    private String rightBtnImgURL;
    private String rightBtnAction;

    public TitleBarAddressModel(Context context, String json) {
        super(context, json);
        try {
            JSONObject jo = new JSONObject(json);
            rootCode = jo.getString("rootcode");
            defaultAddress = jo.getString("defaultaddress");
            defaultAddressCode = jo.getString("defaultaddresscode");
            rightBtnImgURL = jo.getString("rightbtnimgurl");
            rightBtnAction = jo.getString("rightbtnurl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getRootCode() {
        return rootCode;
    }

    public void setRootCode(String rootCode) {
        this.rootCode = rootCode;
    }

    public String getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(String defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public String getDefaultAddressCode() {
        return defaultAddressCode;
    }

    public void setDefaultAddressCode(String defaultAddressCode) {
        this.defaultAddressCode = defaultAddressCode;
    }

    public String getRightBtnImgURL() {
        return rightBtnImgURL;
    }

    public void setRightBtnImgURL(String rightBtnImgURL) {
        this.rightBtnImgURL = rightBtnImgURL;
    }

    public String getRightBtnAction() {
        return rightBtnAction;
    }

    public void setRightBtnAction(String rightBtnAction) {
        this.rightBtnAction = rightBtnAction;

    }
}
