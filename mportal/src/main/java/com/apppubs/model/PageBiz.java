package com.apppubs.model;

import android.content.Context;

import com.apppubs.bean.page.PageContentModel;
import com.apppubs.bean.page.PageModel;
import com.apppubs.bean.page.PageNormalContentModel;
import com.apppubs.constant.APError;
import com.apppubs.constant.Constants;
import com.apppubs.ui.activity.MainHandler;
import com.apppubs.util.LogM;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangwen on 2017/9/27.
 */

public class PageBiz extends BaseBiz implements IPageBiz {

    private Context mContext;

    public PageBiz(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void loadPage(String pageId, final IAPCallback<PageModel> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("pageId", pageId);
        asyncPOST(Constants.API_NAME_PAGE, params, true, new IRQStringListener() {

            @Override
            public void onResponse(String result, final APError error) {
                if (error == null) {
                    LogM.log(this.getClass(), "请求page json：" + result);
                    final PageModel model = new PageModel(mContext, result);

                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(model);
                        }
                    });
                } else {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onException(error);
                        }
                    });
                }
            }
        });
    }

    private PageNormalContentModel getPageNormalContentIfExit(PageModel model) {
        PageContentModel content = model.getContent();
        if (content instanceof PageNormalContentModel) {
            return (PageNormalContentModel) content;
        }
        return null;
    }

}
