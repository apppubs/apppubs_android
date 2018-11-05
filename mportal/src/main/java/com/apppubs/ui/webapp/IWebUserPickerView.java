package com.apppubs.ui.webapp;

import com.apppubs.bean.webapp.DeptModel;
import com.apppubs.bean.webapp.DeptPickerDTO;
import com.apppubs.bean.webapp.SearchDeptHttpResult;
import com.apppubs.bean.webapp.SearchHttpResult;
import com.apppubs.bean.webapp.UserModel;
import com.apppubs.bean.webapp.UserPickerDTO;
import com.apppubs.ui.ICommonView;

import java.util.List;

/**
 * Created by zhangwen on 2018/1/8.
 */

public interface IWebUserPickerView extends ICommonView {

    void showLoading();

    void hideLoading();

    void showError(String error);

    void setDepts(List<DeptModel> depts);

    void setUsers(List<UserModel> users);

    void setSearchDepts(List<SearchDeptHttpResult> mSearchDeptList);

    void setSearchUsers(List<SearchHttpResult> searchUsers);

    void refreshUserList(List<UserModel> users);

    void resreshSearchUserList(List<SearchHttpResult> searchUsers);

    void hideSearchLv();

    void pushBreadcrumb(String name, String tag);


    void addSelectedBarDept(DeptModel model);

    void removeSelectedBarDept(String id);

    void addSelectedBarUser(UserModel vo);

    void removeSelectedBarUser(String userId);

    UserPickerDTO getUserPickerVO();

    DeptPickerDTO getDeptPickerDTO();

    boolean isDeptSelection();

    WebUserPickerActivity.UserPickerListener getListener();

    WebUserPickerActivity.DeptPickerListener getDeptPickerListener();

    void finishActivity();

    boolean isUserListShow();

    void showRightBtnText(boolean isShow, boolean enable, String text);
}
