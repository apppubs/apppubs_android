package com.apppubs.d20.presenter;

import android.content.Context;

import com.apppubs.d20.AppManager;
import com.apppubs.d20.model.SystemBussiness;
import com.apppubs.d20.model.VersionInfo;
import com.apppubs.d20.view.IVersionView;

/**
 * Created by siger on 2018/3/13.
 */

public class VersionPresenter extends AbsPresenter<IVersionView> {

    public VersionPresenter(Context context, IVersionView view) {
        super(context, view);
    }

    public void checkUpdate() {
        SystemBussiness.getInstance(mContext).checkUpdate(mContext, new SystemBussiness
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
