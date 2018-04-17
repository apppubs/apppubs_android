package com.apppubs.bean.page;

import com.apppubs.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siger on 2018/4/17.
 */
public class PageComponent {
    private String jsonStr;
    private String code;
    private JSONObject jo;

    public PageComponent(String json) {
        jsonStr = json;
        try {
            jo = new JSONObject(json);
            code = jo.getString("comtype");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getJson() {
        return jsonStr;
    }

    public void setJson(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JSONObject getJSONObject() {
        return jo;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PageComponent)) {
            return false;
        }
        PageComponent des = (PageComponent) o;
        if (!Utils.compare(jsonStr, des.getJson())) {
            return false;
        }
        return true;
    }
}
