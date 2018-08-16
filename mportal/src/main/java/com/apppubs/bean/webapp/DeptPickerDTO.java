package com.apppubs.bean.webapp;

import java.io.Serializable;

public class DeptPickerDTO implements Serializable{

    public static final int SELECT_MODE_MULTI = 1;
    public static final int SELECT_MODE_SINGLE = 0;

    private int selectMode;
    private String deptsURL;
    private String searchURL;
    private String rootDeptId;

    public int getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }

    public String getDeptsURL() {
        return deptsURL;
    }

    public void setDeptsURL(String deptsURL) {
        this.deptsURL = deptsURL;
    }

    public String getSearchURL() {
        return searchURL;
    }

    public void setSearchURL(String searchURL) {
        this.searchURL = searchURL;
    }

    public String getRootDeptId() {
        return rootDeptId;
    }

    public void setRootDeptId(String rootDeptId) {
        this.rootDeptId = rootDeptId;
    }
}
