package com.apppubs.d20.webapp;

import com.apppubs.d20.webapp.model.DeptVO;
import com.apppubs.d20.webapp.model.SearchVO;
import com.apppubs.d20.webapp.model.UserPickerVO;
import com.apppubs.d20.webapp.model.UserVO;

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
