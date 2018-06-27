package com.apppubs.presenter;

import android.content.Context;
import android.widget.Toast;

import com.apppubs.AppContext;
import com.apppubs.bean.http.AdbookInfoResult;
import com.apppubs.constant.APError;
import com.apppubs.model.AdbookBiz;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.cache.CacheListener;
import com.apppubs.model.cache.FileCacheErrorCode;
import com.apppubs.model.cache.FileCacheManager;
import com.apppubs.ui.message.IAdbookView;
import com.apppubs.util.LogM;
import com.apppubs.util.Utils;

import java.io.File;

public class AdbookPresenter extends AbsPresenter<IAdbookView> {
    private AdbookBiz mBiz;
    private AdbookInfoResult mAdbookInfo;
    private boolean isLoadingInfo;
    private boolean isDownloadXml;

    public AdbookPresenter(Context context, IAdbookView view) {
        super(context, view);
        mBiz = new AdbookBiz(context);
    }

    public void onVisible() {
        LogM.log(this.getClass(), "onVisiable");
        loadAdbookInfo();
    }

    public void onCreateView() {
        loadAdbookInfo();
    }

    private void loadAdbookInfo() {
        mView.showLoading();
        mBiz.fetchAdbookInfo(new IAPCallback<AdbookInfoResult>() {
            @Override
            public void onDone(AdbookInfoResult obj) {
                mAdbookInfo = obj;
                mView.hideLoading();
                LogM.log(this.getClass(), "获取AdbookInfoResult成功");
                AdbookInfoResult adbookInfo = mBiz.getCachedAdbookInfo();
                if (adbookInfo == null) {
                    mView.showUpdateDialog();
                } else if (!Utils.compare(adbookInfo.getUpdateTime(), obj.getUpdateTime())) {
                    mView.showHaveNewVersion(obj.getUpdateTime());
                } else {
                    //已经是最新
                }

            }

            @Override
            public void onException(APError error) {
                LogM.log(this.getClass(), "获取AdbookInfoResult失败");
                mView.hideLoading();
                mView.onError(error);
            }
        });
    }


    public void onUpdateConfirmed() {
        startDownload();
    }

    private void startDownload() {
        FileCacheManager manager = AppContext.getInstance(mContext).getCacheManager();
        manager.cacheFile(mAdbookInfo.getDownloadURL(), null, new CacheListener() {
            @Override
            public void onException(FileCacheErrorCode errorCode) {

            }

            @Override
            public void onDone(String localPath) {
                LogM.log(this.getClass(), "下载完成"+localPath);
                mBiz.parseXML(new File(localPath), new IAPCallback() {
                    @Override
                    public void onDone(Object obj) {
//                        mView.showDepts();
                        Toast.makeText(mContext,"加载完成",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onException(APError error) {

                    }
                });
            }

            @Override
            public void onProgress(float progress, long totalBytesExpectedToRead) {
                LogM.log(this.getClass(),"下载进度："+progress);
            }
        });
    }





}
