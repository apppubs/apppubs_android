package com.apppubs.ui.home;

import com.apppubs.bean.TMenuItem;
import com.apppubs.constant.APError;

import java.util.List;

public interface IHomeBottomMenuView {

    void setMenus(List<TMenuItem> menus);
    void showLoading();
    void hideLoading();
    void showError(APError error);
}
