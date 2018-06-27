package com.apppubs.ui.message;

import com.apppubs.bean.TDepartment;
import com.apppubs.bean.TUser;
import com.apppubs.ui.ICommonView;

import java.util.List;

public interface IAdbookView extends ICommonView{
    void showUpdateDialog();
    void showHaveNewVersion(String updateTime);
    void showDepts(List<TDepartment> depts);
    void showUsers(List<TUser> users);
}
