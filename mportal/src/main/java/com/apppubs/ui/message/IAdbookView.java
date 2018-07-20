package com.apppubs.ui.message;

import com.apppubs.bean.TDepartment;
import com.apppubs.bean.TUser;
import com.apppubs.ui.ICommonView;

import java.util.List;

public interface IAdbookView extends ICommonView{
    void showSyncDialog();
    void showSyncLoading();
    void setSyncProgress(Float progress);
    void setSyncLoadText(String text);
    void hideSyncLoading();
    void showHaveNewVersion(String updateTime);
    void showDepts(List<TDepartment> depts);
    void showUsers(List<TUser> users);
    void clearBreadcrumb(TDepartment department);
    void showParsingLoading();
    void hideParsingLoading();
}
