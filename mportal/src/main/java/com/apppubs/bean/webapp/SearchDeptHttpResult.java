package com.apppubs.bean.webapp;

/**
 * Created by zhangwen on 2018/1/9.
 */

public class SearchDeptHttpResult {

    private String id;
    private String name;
    private boolean isSelected;
    private boolean isPreSelected;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isPreSelected() {
        return isPreSelected;
    }

    public void setPreSelected(boolean preSelected) {
        isPreSelected = preSelected;
    }
}
