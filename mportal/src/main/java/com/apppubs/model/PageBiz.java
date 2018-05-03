package com.apppubs.model;

import android.content.Context;

import com.apppubs.AppContext;
import com.apppubs.bean.UserInfo;
import com.apppubs.bean.page.PageContentModel;
import com.apppubs.bean.page.PageModel;
import com.apppubs.bean.page.PageNormalContentModel;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.constant.URLs;
import com.apppubs.net.WMHHttpClient;
import com.apppubs.ui.activity.MainHandler;
import com.apppubs.util.LogM;

import java.util.HashMap;

/**
 * Created by zhangwen on 2017/9/27.
 */

public class PageBiz extends BaseBiz implements IPageBiz  {

    private Context mContext;
    private WMHHttpClient mHttpClient;

    public PageBiz(Context context) {
        super(context);
        mContext = context;
        mHttpClient = AppContext.getInstance(mContext).getHttpClient();
    }

    @Override
    public void loadPage(String pageId, final IAPCallback<PageModel> callback) {
        asyncPOST(getUrl(pageId), new HashMap<String, String>(), new IRQStringListener() {

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

    private String getUrl(String pageId) {
        UserInfo ui = AppContext.getInstance(mContext).getCurrentUser();
        String url = null;
        if (ui != null) {
            url = String.format(URLs.URL_PAGE, URLs.baseURL, URLs.appCode, pageId, ui.getUserId());
        } else {
            url = String.format(URLs.URL_PAGE, URLs.baseURL, URLs.appCode, pageId, "");
        }
        url = "http://result.eolinker.com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=page";
        return url;
    }


    private PageNormalContentModel getPageNormalContentIfExit(PageModel model) {
        PageContentModel content = model.getContent();
        if (content instanceof PageNormalContentModel) {
            return (PageNormalContentModel) content;
        }
        return null;
    }

}
