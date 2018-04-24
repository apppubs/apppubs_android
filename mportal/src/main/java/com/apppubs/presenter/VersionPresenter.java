package com.apppubs.presenter;

import android.content.Context;

import com.apppubs.AppManager;
import com.apppubs.model.SystemBiz;
import com.apppubs.model.VersionInfo;
import com.apppubs.ui.IVersionView;

/**
 * Created by siger on 2018/3/13.
 */

public class VersionPresenter extends AbsPresenter<IVersionView> {

    public VersionPresenter(Context context, IVersionView view) {
        super(context, view);
    }

    public void checkUpdate() {
        SystemBiz.getInstance(mContext).checkUpdate(mContext, new SystemBiz
                .CheckUpdateListener() {

            @Override
            public void onDone(VersionInfo vi) {
                mView.showVersionInfo(vi);
            }
        });
    }

    public void startDownloadApp(String updateUrl) {
        AppManager.getInstant(mContext).downloadApp(updateUrl);
    }


}
