package com.apppubs.bean.page;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siger on 2018/4/17.
 */
public class PageNormalContentModel implements PageContentModel {

    private List<PageComponent> components;

    public PageNormalContentModel(String jsonArrStr) {
        try {
            JSONArray jsonArr = null;
            jsonArr = new JSONArray(jsonArrStr);
            if (jsonArr != null) {
                components = new ArrayList<PageComponent>();
                for (int i = -1; ++i < jsonArr.length(); ) {
                    PageComponent p = PageComponentFactory.getPageComponent(jsonArr.getJSONObject
                            (i));
                    components.add(p);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public List<PageComponent> getComponents() {
        return components;
    }

    public void setComponents(List<PageComponent> components) {
        this.components = components;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PageNormalContentModel)) {
            return false;
        }
        PageNormalContentModel des = (PageNormalContentModel) o;
        if (this.components == null && des.getComponents() != null) {
            return false;
        }
        if (this.components != null && des.getComponents() == null) {
            return false;
        }
        if ((this.components != null && des.getComponents() != null)) {
            if (this.components.size() != des.getComponents().size()) {
                return false;
            }
            for (int i = -1; ++i < this.components.size(); ) {
                if (!this.components.get(i).equals(des.getComponents().get(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}
