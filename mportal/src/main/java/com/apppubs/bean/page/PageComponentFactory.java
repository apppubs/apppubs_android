package com.apppubs.bean.page;

import com.apppubs.constant.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by siger on 2018/4/17.
 */
class PageComponentFactory {
    public static PageComponent getPageComponent(JSONObject component) {
        String componentCode = null;
        try {
            componentCode = component.getString("comtype");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (component == null || componentCode == null) {
            return null;
        }
        if (Constants.PAGE_COMPONENT_DEFAULT_USER_INFO.equals(componentCode)) {
            return new DefaultUserinfoComponent(component.toString());
        } else {
            return new PageComponent(component.toString());
        }
    }
}
