package com.apppubs.model;

import com.apppubs.bean.page.PageModel;

/**
 * Created by zhangwen on 2017/9/27.
 */

public interface IPageBiz {

	void loadPage(String pageId, APResultCallback<PageModel> callback);
}
