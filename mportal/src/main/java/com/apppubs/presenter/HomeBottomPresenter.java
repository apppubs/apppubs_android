package com.apppubs.presenter;

import android.content.Context;

import com.apppubs.bean.TMenuItem;
import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.SystemBiz;
import com.apppubs.ui.home.IHomeBottomMenuView;
import com.apppubs.util.Utils;

import java.util.List;

public class HomeBottomPresenter extends HomePresenter<IHomeBottomMenuView> {


    public HomeBottomPresenter(Context context, IHomeBottomMenuView view) {
        super(context, view);
    }
}
