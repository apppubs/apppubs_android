package com.apppubs.ui.webapp;

import com.apppubs.bean.webapp.SearchVO;
import com.apppubs.bean.webapp.UserPickerVO;
import com.apppubs.bean.webapp.DeptVO;
import com.apppubs.bean.webapp.UserVO;

import java.util.List;

/**
 * Created by zhangwen on 2018/1/8.
 */

public interface IWebUserPickerView {

	void showLoading();

	void hideLoading();

	void showError(String error);

	void setDepts(List<DeptVO> depts);

	void setUsers(List<UserVO> users);

	void setSearchUsers(List<SearchVO> searchUsers);

	void refreshUserList(List<UserVO> users);

	void resreshSearchUserList(List<SearchVO> searchUsers);

	void pushBreadcrumb(String name, String tag);

	void addSelectedBarUser(UserVO vo);

	void removeSelectedBarUser(String userId);

	UserPickerVO getUserPickerVO();

	WebUserPickerActivity.UserPickerListener getListener();

	void finishActivity();
}
