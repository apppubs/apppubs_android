package com.apppubs.presenter;

import android.content.Context;

import com.apppubs.bean.TMenuItem;
import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.SystemBiz;
import com.apppubs.ui.ICommonListView;
import com.apppubs.ui.activity.ViewCourier;
import com.apppubs.ui.home.IHomeBottomMenuView;
import com.apppubs.ui.home.IHomeSlideMenuView;
import com.apppubs.util.Utils;

import java.util.List;

public class HomeSlidePresenter extends HomePresenter<IHomeSlideMenuView> {
    public HomeSlidePresenter(Context context, IHomeSlideMenuView view) {
        super(context, view);
    }

    @Override
    public void onMenuSelected(String uri) {
        if (uri.startsWith("apppubs://page")) {
            super.onMenuSelected(uri);
            mView.hideMenu();
        }else{
            ViewCourier.getInstance(mContext).openWindow(uri);
        }
    }
}
