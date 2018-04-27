package com.apppubs.bean.page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by siger on 2018/4/17.
 */
public class PageNavContentModel implements PageContentModel {

    private JSONObject navBar;
    private JSONArray navItems;
    private List<PageNormalContentModel> items;

    public PageNavContentModel(String json) {
        try {
            navBar = new JSONObject(json).getJSONObject("navBar");
            navItems = navBar.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getNavItems() {
        return navItems;
    }

    public void setNavItems(JSONArray navItems) {
        this.navItems = navItems;
    }

    public List<PageNormalContentModel> getItems() {
        return items;
    }

    public void setItems(List<PageNormalContentModel> items) {
        this.items = items;
    }

    public JSONObject getNavBar() {
        return navBar;
    }

    public void setNavBar(JSONObject navBar) {
        this.navBar = navBar;
    }

    @Override
    public boolean equals(Object o) {
        return true;
    }
}
