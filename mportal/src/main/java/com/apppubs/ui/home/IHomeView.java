package com.apppubs.ui.home;

import android.support.v4.app.Fragment;

import com.apppubs.bean.TMenuItem;
import com.apppubs.ui.ICommonView;
import com.apppubs.ui.fragment.BaseFragment;

import java.util.List;

public interface IHomeView extends ICommonView {
    void changeContent(BaseFragment frg);
    void setMenus(List<TMenuItem> menus);
}
