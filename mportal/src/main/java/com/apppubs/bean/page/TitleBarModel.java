package com.apppubs.bean.page;

import android.content.Context;

import com.apppubs.AppContext;
import com.apppubs.util.StringUtils;
import com.apppubs.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siger on 2018/4/17.
 */
public class TitleBarModel {

    public static final String TYPE_NORMAL = "0";
    public static final String TYPE_ADDRESS = "1";

    private String mJson;
    private String type;
    private String title;
    private int bgColor;
    private String titleImgUrl;
    private String rightImgUrl;
    private String rightAction;
    private int underlineColor;

    public TitleBarModel(Context context, String json) {
        mJson = json;
        try {
            JSONObject jo = new JSONObject(json);
            type = jo.getString("titletype");
            title = jo.getString("title").replaceAll("\\$truename", AppContext.getInstance
                    (context).getCurrentUser().getTrueName());
            bgColor = Utils.parseColor(jo.getString("bgcolor"));
            titleImgUrl = jo.getString("titleimgurl");
            rightImgUrl = jo.getString("rightbtnimgurl");
            rightAction = jo.getString("rightbtnurl");
            int underColor = Utils.parseColor(jo.getString("underlinecolor"));
            if (underColor > -1) {
                underlineColor = underColor;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static TitleBarModel buildTitleBarModel(Context context, String json) {
        JSONObject jo = null;
        try {
            jo = new JSONObject(json);
            String type = jo.getString("titletype");
            if (TYPE_NORMAL.equals(type)) {
                return new TitleBarNomalModel(context, json);
            } else {
                return new TitleBarAddressModel(context, json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public String getTitleImgUrl() {
        return titleImgUrl;
    }

    public void setTitleImgUrl(String titleImgUrl) {
        this.titleImgUrl = titleImgUrl;
    }

    public String getRightImgUrl() {
        return rightImgUrl;
    }

    public void setRightImgUrl(String rightImgUrl) {
        this.rightImgUrl = rightImgUrl;
    }

    public String getRightAction() {
        return rightAction;
    }

    public void setRightAction(String rightAction) {
        this.rightAction = rightAction;
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
    }

    @Override
    public String toString() {
        return mJson;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TitleBarModel)) {
            return false;
        }
        TitleBarModel des = (TitleBarModel) o;
        if (!StringUtils.equals(mJson, des.toString())) {
            return false;
        }
        return true;
    }
}
