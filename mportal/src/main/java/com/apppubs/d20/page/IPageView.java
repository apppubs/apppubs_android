package com.apppubs.d20.page;

/**
 * Created by zhangwen on 2017/9/27.
 */

public interface IPageView {

	void showTitleBar(TitleBarModel model);

	void showContentView(PageContentModel model);

	void showErrorView();

	String getPageId();
}
