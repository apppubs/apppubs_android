package com.apppubs.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.apppubs.AppContext;
import com.apppubs.bean.TDepartment;
import com.apppubs.bean.UserInfo;
import com.apppubs.bean.http.AdbookInfoResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.d20.BuildConfig;
import com.apppubs.model.AdbookBiz;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.cache.CacheListener;
import com.apppubs.model.cache.FileCacheErrorCode;
import com.apppubs.model.cache.FileCacheManager;
import com.apppubs.model.message.UserPickerHelper;
import com.apppubs.ui.home.HomeBaseActivity;
import com.apppubs.ui.message.IAdbookView;
import com.apppubs.ui.widget.AlertDialog;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.util.LogM;
import com.apppubs.util.Utils;

import java.io.File;
import java.util.List;

import io.rong.imkit.RongIM;

public class AdbookPresenter extends AbsPresenter<IAdbookView> {
    private AdbookBiz mBiz;
    private AdbookInfoResult mAdbookInfo;
    private boolean isLoading;

    public AdbookPresenter(Context context, IAdbookView view) {
        super(context, view);
        mBiz = AdbookBiz.getInstance(context);
    }

    public void onVisible() {
        LogM.log(this.getClass(), "onVisiable");
        loadAdbookInfo();
    }

    public void onCreateView() {
        loadAdbookInfo();
        mAdbookInfo = mBiz.getCachedAdbookInfo();
    }

    public void onOrgFragmentCreate() {
        loadRootDepartments();
    }

    public void onDeptSelected(String deptId) {
        if (mBiz.isLeaf(deptId)) {
            loadUsers(deptId);
        } else {
            loadDepts(deptId);
        }
    }

    private void loadAdbookInfo() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        mView.showLoading();
        mBiz.fetchAdbookInfo(new IAPCallback<AdbookInfoResult>() {
            @Override
            public void onDone(AdbookInfoResult obj) {
                mView.hideLoading();
                LogM.log(this.getClass(), "获取AdbookInfoResult成功");
                if (mAdbookInfo == null) {
                    mView.showSyncDialog();
                } else if (!Utils.compare(mAdbookInfo.getUpdateTime(), obj.getUpdateTime())) {
                    mView.showHaveNewVersion(obj.getUpdateTime());
                } else {
                    //已经是最新
                }
                mAdbookInfo = obj;
                isLoading = false;
            }

            @Override
            public void onException(APError error) {
                LogM.log(this.getClass(), "获取AdbookInfoResult失败");
                mView.hideLoading();
                mView.onError(error);
                isLoading = false;
            }
        });
    }


    public void onUpdateConfirmed() {
        if (!isLoading) {
            startDownload();
            Toast.makeText(mContext, "后台同步已开始，请在通知栏查看进度！", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, "正在后台同步，请在通知栏查看进度！", Toast.LENGTH_LONG).show();
        }
    }

    private void startDownload() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        mView.showSyncLoading();
        FileCacheManager manager = AppContext.getInstance(mContext).getCacheManager();
        manager.cacheFile(mAdbookInfo.getDownloadURL(), null, new CacheListener() {
            @Override
            public void onException(FileCacheErrorCode errorCode) {
                mView.hideSyncLoading();
                mView.onError(new APError(APErrorCode.GENERAL_ERROR, "下载XML错误！"));
                isLoading = false;
            }

            @Override
            public void onDone(String localPath) {
                LogM.log(this.getClass(), "下载完成" + localPath);
                mView.setSyncLoadText("解析中...");
                mView.showParsingLoading();
                mBiz.parseXML(new File(localPath), new IAPCallback() {
                    @Override
                    public void onDone(Object obj) {
                        mView.hideParsingLoading();
                        mView.setSyncLoadText("同步完成");
                        Toast.makeText(mContext, "同步完成", Toast.LENGTH_LONG).show();
                        mBiz.cacheAdbookInfo(mAdbookInfo);
                        mView.hideSyncLoading();
                        loadRootDepartments();
                        isLoading = false;
                    }

                    @Override
                    public void onException(APError error) {
                        mView.onError(error);
                        mView.hideParsingLoading();
                        isLoading = false;
                    }
                });
            }

            @Override
            public void onProgress(float progress, long totalBytesExpectedToRead) {
                LogM.log(this.getClass(), "下载进度：" + progress);
                mView.setSyncProgress(progress);
            }
        });
    }

    private void loadRootDepartments() {
        if (mAdbookInfo == null) {
            return;
        }
        TDepartment rootDept = mBiz.getDepartmentById(mAdbookInfo.getRootDeptId());
        if (rootDept == null){

        }else{
            mView.clearBreadcrumb(rootDept);
            List<TDepartment> departments = mBiz.listSubDepartments(mAdbookInfo.getRootDeptId());
            mView.showDepts(departments);
        }
    }

    private void loadDepts(String superId) {
        if (TextUtils.isEmpty(superId)) {
            return;
        }
        List<TDepartment> departments = mBiz.listSubDepartments(superId);
        mView.showDepts(departments);
    }

    private void loadUsers(String deptId) {
        mView.showUsers(mBiz.listUser(deptId));
    }

    public void onCreateDiscussClicked(String deptId){
        TDepartment department = mBiz.getDepartmentById(deptId);
        String deptName = department != null ? department.getName() : "组织";
        final List<String> userIdList = mBiz.getUserIdsOfCertainDepartment(deptId, mAdbookInfo.needReadPermission());
        if (userIdList == null || userIdList.size() < 1) {
            String message = "此部门无可会话人员";
            AlertDialog dialog = new AlertDialog(mContext, null, "无法创建讨论组！", message, "确定");
            dialog.show();
            return;
        }

        final UserInfo currentUser = AppContext.getInstance(mContext).getCurrentUser();
        int countOfUser = userIdList.contains(currentUser.getUserId()) ? userIdList.size() : userIdList.size() + 1;
        if (countOfUser > UserPickerHelper.MAX_SELECTED_USER_NUM) {
            String message = String.format("讨论组最大人数为%d,当前部门可会话人数%d", UserPickerHelper.MAX_SELECTED_USER_NUM,
                    countOfUser);
            AlertDialog dialog = new AlertDialog(mContext, null, "无法创建讨论组！", message, "确定");
            dialog.show();
            return;
        }
        String message = String.format("讨论组包含\"%s\"下所有可会话人员和自己共（%d）人", deptName, countOfUser);
        ConfirmDialog dialog = new ConfirmDialog(mContext, new ConfirmDialog.ConfirmListener() {
            @Override
            public void onOkClick() {
//                ((HomeBaseActivity) mHostActivity).selectMessageFragment();
//                String title = currentUser.getTrueName() + "+" + mUserBussiness.getDepartmentById(mSuperId).getName();
//                RongIM.getInstance().createDiscussion(title, userIdList, null);
            }

            @Override
            public void onCancelClick() {

            }
        }, "创建讨论组？", message, "取消", "创建");
        dialog.show();
    }
}
