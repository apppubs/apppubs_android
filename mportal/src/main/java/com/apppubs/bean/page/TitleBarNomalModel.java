package com.apppubs.bean.page;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siger on 2018/4/17.
 */
public class TitleBarNomalModel extends TitleBarModel {

    private String leftImgUrl;
    private String leftAction;

    public TitleBarNomalModel(Context context, String json) {
        super(context, json);
        JSONObject jo = null;
        try {
            jo = new JSONObject(json);
            leftAction = jo.getString("leftbtnurl");
            leftImgUrl = jo.getString("leftbtnimgurl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getLeftImgUrl() {
        return leftImgUrl;
    }

    public void setLeftImgUrl(String leftImgUrl) {
        this.leftImgUrl = leftImgUrl;
    }


    public String getLeftAction() {
        return leftAction;
    }

    public void setLeftAction(String leftAction) {
        this.leftAction = leftAction;
    }
}
