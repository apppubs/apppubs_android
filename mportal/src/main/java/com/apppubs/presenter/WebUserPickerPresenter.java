package com.apppubs.presenter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.apppubs.AppContext;
import com.apppubs.ui.activity.MainHandler;
import com.apppubs.net.WMHHttpErrorCode;
import com.apppubs.net.WMHRequestListener;
import com.apppubs.ui.webapp.IWebUserPickerView;
import com.apppubs.ui.webapp.WebUserPickerActivity;
import com.apppubs.bean.webapp.SearchVO;
import com.apppubs.bean.webapp.UserPickerVO;
import com.apppubs.util.JSONResult;
import com.apppubs.util.JSONUtils;
import com.apppubs.bean.webapp.DeptVO;
import com.apppubs.bean.webapp.UserVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwen on 2018/1/8.
 */

public class WebUserPickerPresenter {

	private Context mContext;
	private IWebUserPickerView mView;
	private boolean isLoading;
	private List<UserVO> mSelectedUserList;
	private List<UserVO> mUserList;
	private List<SearchVO> mSearchList;

	public WebUserPickerPresenter(Context context, IWebUserPickerView view) {
		mContext = context;
		mView = view;
		mSelectedUserList = new ArrayList<UserVO>();
	}

	public void onCreate() {
		UserPickerVO vo = mView.getUserPickerVO();
		loadDepts(vo.getRootDeptId());
	}

	public void onDeptItemClick(DeptVO deptVO) {
		if (deptVO.isLeaf()) {
			loadUsers(deptVO.getId());
		} else {
			loadDepts(deptVO.getId());
		}
	}

	public void onUserItemClick(UserVO userVO) {
		UserPickerVO vo = mView.getUserPickerVO();
		if (vo.getmSelectMode() == UserPickerVO.SELECT_MODE_MULTI) {
			if (!isUserSelected(userVO.getId())) {
				mSelectedUserList.add(userVO);
				mView.addSelectedBarUser(userVO);
				refreshUserAndSearchLv();
			} else {
				removeSelectedUser(userVO.getId());
				mView.removeSelectedBarUser(userVO.getId());
				refreshUserAndSearchLv();
			}
		} else {
			mSelectedUserList.add(userVO);
			done();
		}
	}

	public void onSearchUserItemClick(SearchVO searchVO) {
		UserPickerVO vo = mView.getUserPickerVO();
		UserVO userVO = searchVO.toUserVO();
		if (vo.getmSelectMode() == UserPickerVO.SELECT_MODE_MULTI) {
			if (!isUserSelected(searchVO.getId())) {
				mSelectedUserList.add(userVO);
				refreshUserAndSearchLv();
				mView.addSelectedBarUser(userVO);
			} else {
				removeSelectedUser(userVO.getId());
				mView.removeSelectedBarUser(userVO.getId());
				refreshUserAndSearchLv();
			}
		} else {
			mSelectedUserList.add(userVO);
			done();
		}
	}

	public void onBreadcrumbClicked(String deptId) {
		UserPickerVO vo = mView.getUserPickerVO();
		loadDepts(vo.getDeptsURL(), deptId, false);
	}

	public void onRemoveSelecedtUser(String userId) {
		removeSelectedUser(userId);
		mView.removeSelectedBarUser(userId);
		mView.refreshUserList(resolveUsersVOListWithSelectedFlag(mUserList));
	}

	public void onDoneBtnClicked() {
		done();
	}

	public void onQueryTextChange(String searchText) {
		searchUsers(searchText);
	}

	private void loadDepts(String deptId) {
		UserPickerVO vo = mView.getUserPickerVO();
		loadDepts(vo.getDeptsURL(), deptId);
	}

	private void loadDepts(String detpsURL, String deptId) {
		loadDepts(detpsURL, deptId, true);
	}

	private void loadDepts(String detpsURL, String deptId, final boolean needBreadbrumb) {
		JSONObject jo = new JSONObject();
		try {
			jo.put("deptId", deptId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (isLoading) {
			return;
		}
		isLoading = true;
		mView.showLoading();
		AppContext.getInstance(mContext).getHttpClient().POST(detpsURL, jo.toString(), new WMHRequestListener() {
			@Override
			public void onDone(JSONResult jsonResult, @Nullable WMHHttpErrorCode errorCode) {
				isLoading = false;
				hideLoading();

				if (errorCode != null) {
					MainHandler.getInstance().post(new Runnable() {
						@Override
						public void run() {
							mView.showError("加载错误");
						}
					});
					return;
				}
				JSONObject jo = jsonResult.getResultJSONObject();
				try {
					final String deptName = jo.getString("deptName");
					final String deptId = jo.getString("deptId");
					String itemsStr = jo.getString("items");
					final List<DeptVO> items = JSONUtils.parseListFromJson(itemsStr, DeptVO.class);

					MainHandler.getInstance().post(new Runnable() {
						@Override
						public void run() {
							mView.setDepts(items);
							if (needBreadbrumb) {
								mView.pushBreadcrumb(deptName, deptId);
							}
						}
					});

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void loadUsers(String deptId) {
		JSONObject jo = new JSONObject();
		try {
			jo.put("deptId", deptId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		isLoading = true;
		mView.showLoading();
		UserPickerVO vo = mView.getUserPickerVO();
		AppContext.getInstance(mContext).getHttpClient().POST(vo.getUsersURL(), jo.toString(), new WMHRequestListener() {
			@Override
			public void onDone(JSONResult jsonResult, @Nullable WMHHttpErrorCode errorCode) {
				isLoading = false;
				hideLoading();
				if (errorCode != null) {
					MainHandler.getInstance().post(new Runnable() {
						@Override
						public void run() {
							mView.showError("加载错误");
						}
					});
					return;
				}
				JSONObject jo = jsonResult.getResultJSONObject();
				try {
					final String deptName = jo.getString("deptName");
					final String deptId = jo.getString("deptId");
					String itemsStr = jo.getString("items");
					mUserList = resolveUsersVOListWithSelectedFlag(JSONUtils.parseListFromJson(itemsStr, UserVO.class));

					MainHandler.getInstance().post(new Runnable() {
						@Override
						public void run() {
							mView.setUsers(mUserList);
							mView.pushBreadcrumb(deptName, deptId);
						}
					});

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void searchUsers(String text) {
		JSONObject jo = new JSONObject();
		try {
			jo.put("text", text);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		isLoading = true;
		mView.showLoading();
		UserPickerVO vo = mView.getUserPickerVO();
		AppContext.getInstance(mContext).getHttpClient().POST(vo.getSearchURL(), jo.toString(), new WMHRequestListener() {
			@Override
			public void onDone(JSONResult jsonResult, @Nullable WMHHttpErrorCode errorCode) {
				isLoading = false;
				hideLoading();
				if (errorCode != null) {
					MainHandler.getInstance().post(new Runnable() {
						@Override
						public void run() {
							mView.showError("加载错误");
						}
					});
					return;
				}
				JSONObject jo = jsonResult.getResultJSONObject();
				try {
					String itemsStr = jo.getString("items");
					mSearchList = resolveSearchUsersVOListWithSelectedFlag(JSONUtils.parseListFromJson(itemsStr, SearchVO.class));

					MainHandler.getInstance().post(new Runnable() {
						@Override
						public void run() {
							mView.setSearchUsers(mSearchList);
						}
					});

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void hideLoading() {
		MainHandler.getInstance().post(new Runnable() {

			@Override
			public void run() {
				mView.hideLoading();
			}
		});
	}

	private List<String> getSelectedUserId() {
		List<String> userIds = new ArrayList<String>();
		for (UserVO uv : mSelectedUserList) {
			userIds.add(uv.getId());
		}
		return userIds;
	}

	private List<UserVO> resolveUsersVOListWithSelectedFlag(List<UserVO> userList) {
		if (userList != null) {
			List<String> userIds = getSelectedUserId();
			for (UserVO uv : userList) {
				uv.setSelected(userIds.contains(uv.getId()));
			}
		}
		return userList;
	}

	private List<SearchVO> resolveSearchUsersVOListWithSelectedFlag(List<SearchVO> userList) {
		if (userList != null) {
			List<String> userIds = getSelectedUserId();
			for (SearchVO uv : userList) {
				uv.setSelected(userIds.contains(uv.getId()));
			}
		}
		return userList;
	}

	private boolean isUserSelected(String id) {
		List<String> selectedUserIds = getSelectedUserId();
		return selectedUserIds.contains(id);
	}

	private void removeSelectedUser(String userId) {
		UserVO userWaitingRemove = null;
		for (UserVO uv : mSelectedUserList) {
			if (uv.getId().equals(userId)) {
				userWaitingRemove = uv;
			}
		}
		mSelectedUserList.remove(userWaitingRemove);
	}

	private void done() {
		WebUserPickerActivity.UserPickerListener listener = mView.getListener();
		if (listener != null) {
			listener.onPickDone(mSelectedUserList);
		}
		mView.finishActivity();
	}

	private void refreshUserAndSearchLv() {
		mView.refreshUserList(resolveUsersVOListWithSelectedFlag(mUserList));
		mView.resreshSearchUserList(resolveSearchUsersVOListWithSelectedFlag(mSearchList));
	}

}
