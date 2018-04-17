package com.apppubs.ui.page;

import com.apppubs.bean.page.PageContentModel;
import com.apppubs.bean.page.TitleBarModel;

/**
 * Created by zhangwen on 2017/9/27.
 */

public interface IPageView {

	void showTitleBar(TitleBarModel model);

	void setTitleBarAddress(String text);

	void showContentView(PageContentModel model);

	void showLoadingView();

	void hideLoadingView();

	void showErrorView();

	String getPageId();

}
