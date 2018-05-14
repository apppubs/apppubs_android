package com.apppubs.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.apppubs.bean.TMenuItem;
import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.SystemBiz;
import com.apppubs.ui.FragmentFactory;
import com.apppubs.ui.fragment.BaseFragment;
import com.apppubs.ui.home.IHomeView;
import com.apppubs.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HomePresenter<T extends IHomeView> extends AbsPresenter<T> {

    private Map<String, BaseFragment> mFragmentsMap;

    public HomePresenter(Context context, T view) {
        super(context, view);

        mFragmentsMap = new HashMap<>();
    }

    public void onMenuSelected(String uri) {
        changeContent(uri);
    }

    private void changeContent(String uri) {

        if (TextUtils.isEmpty(uri)) {
            return;
        }
        if (mFragmentsMap.keySet().contains(uri)) {
            mView.changeContent(mFragmentsMap.get(uri));
        } else {
            BaseFragment frg = FragmentFactory.getFragment(uri);
            mFragmentsMap.put(uri, frg);
            mView.changeContent(frg);
        }
    }

    private void showMenus() {
        List<TMenuItem> menus = SystemBiz.getInstance(mContext).getLocalPrimaryMenus();
        mView.setMenus(menus);
    }

    private void loadMenus() {
        // 如果菜单更新了则全部初始化
        SystemBiz.getInstance(mContext).initMenus(new IAPCallback<Boolean>() {
            @Override
            public void onDone(Boolean obj) {
                if (obj) {
                    showMenus();
                }
            }

            @Override
            public void onException(APError error) {
                mView.onError(error);
            }
        });
    }

    public void onViewCreated() {
        List<TMenuItem> menus = SystemBiz.getInstance(mContext).getLocalPrimaryMenus();
        if (!Utils.isEmpty(menus)) {
            showMenus();
        }
        loadMenus();
    }

}
