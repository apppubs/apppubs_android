package com.apppubs.ui.page;

import com.apppubs.bean.page.PageContentModel;
import com.apppubs.bean.page.TitleBarModel;
import com.apppubs.ui.ICommonView;

/**
 * Created by zhangwen on 2017/9/27.
 */

public interface IPageView extends ICommonView{

	void showTitleBar(TitleBarModel model);

	void setTitleBarAddress(String text);

	void showContentView(PageContentModel model);

	String getPageId();

}
