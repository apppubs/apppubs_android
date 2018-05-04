package com.apppubs.presenter;

import android.content.Context;

import com.apppubs.bean.TMenuItem;
import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.SystemBiz;
import com.apppubs.ui.home.IHomeBottomMenuView;
import com.apppubs.util.Utils;

import java.util.List;

public class HomePresenter extends AbsPresenter<IHomeBottomMenuView> {
    public HomePresenter(Context context, IHomeBottomMenuView view) {
        super(context, view);
    }

    private void showMenus(){
        List<TMenuItem> menus = SystemBiz.getInstance(mContext).getLocalPrimaryMenus();
        mView.setMenus(menus);
    }

    private void loadMenus(){
        // 如果菜单更新了则全部初始化
        SystemBiz.getInstance(mContext).initMenus(new IAPCallback<Boolean>() {
            @Override
            public void onDone(Boolean obj) {
                if (obj){
                    showMenus();
                }
            }

            @Override
            public void onException(APError error) {
                mView.showError(error);
            }
        });
    }

    public void onViewCreated(){
        List<TMenuItem> menus = SystemBiz.getInstance(mContext).getLocalPrimaryMenus();
        if (!Utils.isEmpty(menus)){
            showMenus();
        }
        loadMenus();
    }
}
