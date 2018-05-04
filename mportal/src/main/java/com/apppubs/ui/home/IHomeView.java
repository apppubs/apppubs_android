package com.apppubs.ui.home;

import android.support.v4.app.Fragment;

import com.apppubs.bean.TMenuItem;
import com.apppubs.ui.ICommonView;

import java.util.List;

public interface IHomeView extends ICommonView {
    void changeContent(Fragment frg);
    void setMenus(List<TMenuItem> menus);
}
