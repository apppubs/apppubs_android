package com.apppubs.ui.message.activity;

import com.apppubs.bean.TDepartment;
import com.apppubs.bean.TUser;

import java.util.List;

public interface IUserPickerView {
    public void setUsers(List<TUser> list);
    public void setDepartments(List<TDepartment> departments);
    public void onBreadcrumbClicked(String deptId);
    public void pushBreadCrumb(String deptId, String deptName);
}
