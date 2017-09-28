package com.apppubs.d20.page;

import android.content.Context;

import com.apppubs.d20.model.APResultCallback;

/**
 * Created by zhangwen on 2017/9/27.
 */

public interface IPageBiz {

	void loadPage(String pageId, APResultCallback<PageModel> callback);
}
