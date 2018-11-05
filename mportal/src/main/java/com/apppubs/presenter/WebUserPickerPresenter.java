package com.apppubs.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.apppubs.bean.webapp.DeptHttpModel;
import com.apppubs.bean.webapp.DeptModel;
import com.apppubs.bean.webapp.DeptPickerDTO;
import com.apppubs.bean.webapp.DeptPickerResultItem;
import com.apppubs.bean.webapp.SearchDeptHttpResult;
import com.apppubs.bean.webapp.SearchHttpResult;
import com.apppubs.bean.webapp.UserModel;
import com.apppubs.bean.webapp.UserPickerDTO;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.net.APHttpClient;
import com.apppubs.net.IHttpClient;
import com.apppubs.net.IRequestListener;
import com.apppubs.ui.activity.MainHandler;
import com.apppubs.ui.webapp.IWebUserPickerView;
import com.apppubs.ui.webapp.WebUserPickerActivity;
import com.apppubs.util.JSONUtils;
import com.apppubs.util.LogM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangwen on 2018/1/8.
 */

public class WebUserPickerPresenter {

    private Context mContext;
    private IWebUserPickerView mView;
    private boolean isLoading;
    private String mCurDeptId;
    private List<UserModel> mSelectedUserList;
    private List<DeptModel> mSelectedDeptList;
    private List<UserModel> mUserList;
    private List<DeptModel> mDeptList;
    private List<SearchHttpResult> mSearchList;
    private List<SearchDeptHttpResult> mSearchDeptList;
    private boolean isDeptSelection;

    public WebUserPickerPresenter(Context context, IWebUserPickerView view) {
        mContext = context;
        mView = view;
        mSelectedUserList = new ArrayList<UserModel>();
        mSelectedDeptList = new ArrayList<DeptModel>();
    }

    public void onCreate() {
        isDeptSelection = mView.isDeptSelection();
        if (isDeptSelection) {
            DeptPickerDTO dto = mView.getDeptPickerDTO();
            loadDepts(dto.getDeptsURL(), dto.getRootDeptId());
        } else {
            UserPickerDTO vo = mView.getUserPickerVO();
            if (TextUtils.isEmpty(vo.getDeptsURL())) {
                loadUsers(vo.getRootDeptId());
            } else {
                loadDepts(vo.getDeptsURL(), vo.getRootDeptId());
            }
        }
    }

    public void onDeptItemClick(DeptModel deptModel) {
        if (isDeptSelection) {
            if (deptModel.isLeaf()) {
                LogM.log(this.getClass(), "部门选择到叶子结点，暂时什么都不做！");
                mView.showMessage("没有子部门！");
            } else {
                DeptPickerDTO dto = mView.getDeptPickerDTO();
                loadDepts(dto.getDeptsURL(), deptModel.getId());
            }
        } else {
            if (deptModel.isLeaf()) {
                loadUsers(deptModel.getId());
            } else {
                UserPickerDTO dto = mView.getUserPickerVO();
                loadDepts(dto.getDeptsURL(), deptModel.getId());
            }
        }
    }

    public void onUserItemClick(UserModel userModel) {
        if (userModel.isPreSelected()) {
            return;
        }
        UserPickerDTO vo = mView.getUserPickerVO();
        if (vo.getmSelectMode() == UserPickerDTO.SELECT_MODE_MULTI) {
            if (!isUserSelected(userModel.getId())) {
                mSelectedUserList.add(userModel);
                mView.addSelectedBarUser(userModel);
                refreshUserAndSearchLv();
            } else {
                removeSelectedUser(userModel.getId());
                mView.removeSelectedBarUser(userModel.getId());
                refreshUserAndSearchLv();
            }
        } else {
            mSelectedUserList.add(userModel);
            done();
        }
    }

    public void onSearchDeptItemClick(SearchDeptHttpResult searchDeptHttpResult) {
        if (searchDeptHttpResult.isPreSelected()) {
            return;
        }
        DeptPickerDTO dto = mView.getDeptPickerDTO();
        DeptModel deptModel = new DeptModel();
        deptModel.setName(searchDeptHttpResult.getName());
        deptModel.setId(searchDeptHttpResult.getId());
        if (dto.getSelectMode() == DeptPickerDTO.SELECT_MODE_MULTI) {
            if (!isDeptSelected(searchDeptHttpResult.getId())) {
                mSelectedDeptList.add(deptModel);
                mView.addSelectedBarDept(deptModel);
            } else {
                removeSelectedDept(deptModel.getId());
                mView.removeSelectedBarDept(deptModel.getId());
            }
            refreshDeptList();
            mView.hideSearchLv();
        } else {
            mSelectedDeptList.add(deptModel);
            selectDeptDone();
        }
    }

    public void onSearchUserItemClick(SearchHttpResult searchHttpResult) {
        if (searchHttpResult.isPreSelected()) {
            return;
        }
        UserPickerDTO vo = mView.getUserPickerVO();
        UserModel userModel = searchHttpResult.toUserVO();
        if (vo.getmSelectMode() == UserPickerDTO.SELECT_MODE_MULTI) {
            if (!isUserSelected(searchHttpResult.getId())) {
                mSelectedUserList.add(userModel);
                refreshUserAndSearchLv();
                mView.addSelectedBarUser(userModel);
            } else {
                removeSelectedUser(userModel.getId());
                mView.removeSelectedBarUser(userModel.getId());
                refreshUserAndSearchLv();
            }
            mView.hideSearchLv();
        } else {
            mSelectedUserList.add(userModel);
            done();
        }
    }

    public void onBreadcrumbClicked(String deptId) {
        if (mCurDeptId.equals(deptId)) {
            return;
        }
        if (isDeptSelection) {
            DeptPickerDTO dto = mView.getDeptPickerDTO();
            loadDepts(dto.getDeptsURL(), deptId, false);
        } else {
            UserPickerDTO vo = mView.getUserPickerVO();
            loadDepts(vo.getDeptsURL(), deptId, false);
        }
    }

    public void onRemoveSelecedtUser(String userId) {
        removeSelectedUser(userId);
        mView.refreshUserList(resolveUsersVOList(mUserList));
    }

    public void onDoneBtnClicked() {
        if (isDeptSelection) {
            selectDeptDone();
        } else {
            done();
        }
    }

    public void onQueryTextChange(String searchText) {
        if (isDeptSelection) {
            searchDept(searchText, 1, 20);
        } else {
            searchUsers(searchText, 1, 20);
        }
    }

    public void onRightBtnClicked(String curText) {
        if("全选".equals(curText)){
            for (UserModel user : mUserList){
                if (!user.isSelected()&&!user.isPreSelected()){
                    addSelectedUser(user);
                    mView.addSelectedBarUser(user);
                }
            }
            refreshUserAndSearchLv();
        }else{
            for (UserModel user : mUserList){
                removeSelectedUser(user.getId());
                mView.removeSelectedBarUser(user.getId());
            }
            refreshUserAndSearchLv();
        }
    }

    private void loadDepts(String detpsURL, String deptId) {
        loadDepts(detpsURL, deptId, true);
    }

    private void loadDepts(String detpsURL, String deptId, final boolean needBreadbrumb) {
        if (isLoading) {
            return;
        }
        isLoading = true;
        mView.showLoading();
        mCurDeptId = deptId;
        Map<String, String> params = new HashMap();
        params.put("deptId", deptId);
        IHttpClient httpClient = new APHttpClient();
        httpClient.asyncPOST(detpsURL, params, new IRequestListener() {
            @Override
            public void onResponse(String json, APError e) {
                isLoading = false;
                hideLoading();
                if (e == null) {
                    JSONObject jsonObj = JSONObject.parseObject(json);
                    Integer code = jsonObj.getInteger("code");
                    if (code == APErrorCode.SUCCESS.getCode()) {
                        JSONObject result = jsonObj.getJSONObject("result");
                        final String deptName = result.getString("deptName");
                        String itemsStr = result.getString("items");
                        List<DeptModel> models = getDeptModels(itemsStr);
                        mDeptList = models;
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                mView.setDepts(models);
                                if (needBreadbrumb) {
                                    mView.pushBreadcrumb(deptName, deptId);
                                }
                            }
                        });
                    } else {
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                mView.onError(new APError(code, jsonObj.getString("msg")));
                            }
                        });
                    }
                } else {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            mView.onError(e);
                        }
                    });
                }
            }

            @NonNull
            private List<DeptModel> getDeptModels(String itemsStr) {
                List<DeptHttpModel> items = JSONUtils.parseListFromJson(itemsStr, DeptHttpModel.class);
                List<DeptModel> models = new ArrayList<>();

                for (DeptHttpModel httpModel : items) {
                    DeptModel model = DeptModel.createFrom(httpModel);
                    if (isDeptSelected(model.getId())) {
                        model.setSelected(true);
                    }
                    models.add(model);
                }

                DeptPickerDTO dto = mView.getDeptPickerDTO();
                if (dto != null && !TextUtils.isEmpty(dto.getPreIds())) {
                    List<String> arr = Arrays.asList(dto.getPreIds().split(","));
                    for (DeptModel model : models) {
                        model.setPreselected(arr.contains(model.getId()));
                    }
                }
                return models;
            }
        });
    }

    private void loadUsers(String deptId) {
        isLoading = true;
        mView.showLoading();
        mCurDeptId = deptId;
        UserPickerDTO vo = mView.getUserPickerVO();
        IHttpClient httpClient = new APHttpClient();
        Map<String, String> map = new HashMap<>();
        map.put("deptId", deptId);
        httpClient.asyncPOST(vo.getUsersURL(), map, new IRequestListener() {
            @Override
            public void onResponse(String json, APError e) {
                isLoading = false;
                hideLoading();
                if (e == null) {
                    JSONObject jsonObj = JSONObject.parseObject(json);
                    Integer code = jsonObj.getInteger("code");
                    if (APErrorCode.SUCCESS.getCode() == code) {
                        JSONObject result = jsonObj.getJSONObject("result");
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                final String deptName = result.getString("deptName");
                                String itemsStr = result.getString("items");
                                mUserList = resolveUsersVOList(JSONUtils.parseListFromJson(itemsStr,
                                        UserModel.class));
                                MainHandler.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mView.setUsers(mUserList);
                                        mView.pushBreadcrumb(deptName, deptId);
                                    }
                                });
                            }
                        });
                    } else {
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                mView.onError(new APError(code, jsonObj.getString("msg")));
                            }
                        });
                    }
                } else {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            mView.onError(e);
                        }
                    });
                }
            }
        });
    }

    private void searchUsers(String text, int pageNum, int pageSize) {
        mView.showLoading();
        Map<String, String> map = new HashMap<>();
        map.put("text", text);
        map.put("pageNum", pageNum + "");
        map.put("pageSize", pageSize + "");
        isLoading = true;
        UserPickerDTO vo = mView.getUserPickerVO();
        IHttpClient httpClient = new APHttpClient();
        httpClient.asyncPOST(vo.getSearchURL(), map, new IRequestListener() {
            @Override
            public void onResponse(String json, APError e) {
                isLoading = true;
                hideLoading();
                if (e == null) {
                    JSONObject jsonObj = JSONObject.parseObject(json);
                    Integer code = jsonObj.getInteger("code");
                    if (APErrorCode.SUCCESS.getCode() == code) {
                        JSONObject result = jsonObj.getJSONObject("result");
                        String itemsStr = result.getString("items");
                        mSearchList = resolveSearchUsersVOList(JSONUtils.parseListFromJson(itemsStr,
                                SearchHttpResult.class));

                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                mView.setSearchUsers(mSearchList);
                            }
                        });
                    } else {
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                mView.onError(new APError(code, jsonObj.getString("msg")));
                            }
                        });
                    }
                } else {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            mView.onError(e);
                        }
                    });
                }
            }
        });
    }

    private void searchDept(String text, int pageNum, int pageSize) {
        mView.showLoading();
        Map<String, String> map = new HashMap<>();
        map.put("text", text);
        map.put("pageNum", pageNum + "");
        map.put("pageSize", pageSize + "");
        isLoading = true;
        DeptPickerDTO dto = mView.getDeptPickerDTO();
        IHttpClient httpClient = new APHttpClient();
        httpClient.asyncPOST(dto.getSearchURL(), map, new IRequestListener() {
            @Override
            public void onResponse(String json, APError e) {
                isLoading = true;
                hideLoading();
                if (e == null) {
                    JSONObject jsonObj = JSONObject.parseObject(json);
                    Integer code = jsonObj.getInteger("code");
                    if (APErrorCode.SUCCESS.getCode() == code) {
                        JSONObject result = jsonObj.getJSONObject("result");
                        String itemsStr = result.getString("items");
                        mSearchDeptList = getSearchDepts(itemsStr);
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                mView.setSearchDepts(mSearchDeptList);
                            }
                        });
                    } else {
                        MainHandler.getInstance().post(new Runnable() {
                            @Override
                            public void run() {
                                mView.onError(new APError(code, jsonObj.getString("msg")));
                            }
                        });
                    }
                } else {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            mView.onError(e);
                        }
                    });
                }
            }

            private List<SearchDeptHttpResult> getSearchDepts(String itemsStr) {
                List<SearchDeptHttpResult> list = JSONUtils.parseListFromJson(itemsStr,
                        SearchDeptHttpResult.class);

                for (SearchDeptHttpResult item : list) {
                    if (isDeptSelected(item.getId())) {
                        item.setSelected(true);
                    }
                }

                DeptPickerDTO dto = mView.getDeptPickerDTO();
                String preIds = dto.getPreIds();
                if (!TextUtils.isEmpty(preIds)) {
                    List<String> idsList = Arrays.asList(preIds.split(","));
                    for (SearchDeptHttpResult item : list) {
                        item.setPreSelected(idsList.contains(item.getId()));
                    }
                }

                return list;
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
        for (UserModel uv : mSelectedUserList) {
            userIds.add(uv.getId());
        }
        return userIds;
    }

    private List<UserModel> resolveUsersVOList(List<UserModel> userList) {
        if (userList != null) {
            List<String> userIds = getSelectedUserId();
            for (UserModel uv : userList) {
                uv.setSelected(userIds.contains(uv.getId()));
            }
            UserPickerDTO vo = mView.getUserPickerVO();
            String preIds = vo.getPreIds();
            if (!TextUtils.isEmpty(preIds)) {
                String[] preIdsArr = preIds.split(",");
                List<String> idsList = Arrays.asList(preIdsArr);
                for (UserModel um : userList) {
                    um.setPreSelected(idsList.contains(um.getId()));
                }
            }
        }
        return userList;
    }

    private List<SearchHttpResult> resolveSearchUsersVOList(List<SearchHttpResult> userList) {
        if (userList != null) {
            List<String> userIds = getSelectedUserId();
            for (SearchHttpResult uv : userList) {
                uv.setSelected(userIds.contains(uv.getId()));
            }
            UserPickerDTO vo = mView.getUserPickerVO();
            String preIds = vo.getPreIds();
            if (!TextUtils.isEmpty(preIds)) {
                String[] preIdsArr = preIds.split(",");
                List<String> idsList = Arrays.asList(preIdsArr);
                for (SearchHttpResult um : userList) {
                    um.setPreSelected(idsList.contains(um.getId()));
                }
            }
        }
        return userList;
    }

    private boolean isUserSelected(String id) {
        List<String> selectedUserIds = getSelectedUserId();
        return selectedUserIds.contains(id);
    }

    private void removeSelectedUser(String userId) {
        UserModel userWaitingRemove = null;
        for (UserModel uv : mSelectedUserList) {
            if (uv.getId().equals(userId)) {
                userWaitingRemove = uv;
            }
        }
        mSelectedUserList.remove(userWaitingRemove);
    }

    private void addSelectedUser(UserModel user){
        mSelectedUserList.add(user);
    }

    private void done() {
        WebUserPickerActivity.UserPickerListener listener = mView.getListener();
        if (listener != null) {
            listener.onPickDone(mSelectedUserList);
        }
        mView.finishActivity();
    }

    private void refreshUserAndSearchLv() {
        mView.refreshUserList(resolveUsersVOList(mUserList));
        mView.resreshSearchUserList(resolveSearchUsersVOList(mSearchList));
    }

    public void onDeptCheckBtnClicked(DeptModel bean) {
        DeptPickerDTO dto = mView.getDeptPickerDTO();
        if (dto.getSelectMode() == DeptPickerDTO.SELECT_MODE_MULTI) {
            if (!isDeptSelected(bean.getId())) {
                mSelectedDeptList.add(bean);
                mView.addSelectedBarDept(bean);
            } else {
                removeSelectedDept(bean.getId());
                mView.removeSelectedBarDept(bean.getId());
            }
        } else {
            mSelectedDeptList.add(bean);
            selectDeptDone();
        }
        refreshDeptList();
    }

    private void selectDeptDone() {
        WebUserPickerActivity.DeptPickerListener listener = mView.getDeptPickerListener();
        if (listener != null) {
            List<DeptPickerResultItem> result = new ArrayList<>();
            for (DeptModel model : mSelectedDeptList) {
                DeptPickerResultItem item = new DeptPickerResultItem();
                item.setId(model.getId());
                item.setName(model.getName());
                result.add(item);
            }
            listener.onDeptPickerDone(result);
        }
        mView.finishActivity();
    }

    private boolean isDeptSelected(String deptId) {
        for (DeptModel model : mSelectedDeptList) {
            if (deptId.equals(model.getId())) {
                return true;
            }
        }
        return false;
    }

    private void removeSelectedDept(String deptId) {
        DeptModel paddingDeleteModel = null;
        for (DeptModel model : mSelectedDeptList) {
            if (model.getId().equals(deptId)) {
                paddingDeleteModel = model;
                break;
            }
        }
        mSelectedDeptList.remove(paddingDeleteModel);
    }

    private void refreshDeptList() {
        for (DeptModel model : mDeptList) {
            model.setSelected(isDeptSelected(model.getId()));
        }
        mView.setDepts(mDeptList);
    }

    public void onRemoveDeptClicked(String deptId) {
        removeSelectedDept(deptId);
        refreshDeptList();
    }

}
